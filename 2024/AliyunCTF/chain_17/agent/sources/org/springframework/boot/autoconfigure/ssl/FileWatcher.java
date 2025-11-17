package org.springframework.boot.autoconfigure.ssl;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.log.LogMessage;
import org.springframework.util.Assert;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/ssl/FileWatcher.class */
public class FileWatcher implements Closeable {
    private static final Log logger = LogFactory.getLog((Class<?>) FileWatcher.class);
    private final Duration quietPeriod;
    private final Object lock = new Object();
    private WatcherThread thread;

    /* JADX INFO: Access modifiers changed from: package-private */
    public FileWatcher(Duration quietPeriod) {
        Assert.notNull(quietPeriod, "QuietPeriod must not be null");
        this.quietPeriod = quietPeriod;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void watch(Set<Path> paths, Runnable action) {
        Assert.notNull(paths, "Paths must not be null");
        Assert.notNull(action, "Action must not be null");
        if (paths.isEmpty()) {
            return;
        }
        synchronized (this.lock) {
            try {
                if (this.thread == null) {
                    this.thread = new WatcherThread();
                    this.thread.start();
                }
                this.thread.register(new Registration(paths, action));
            } catch (IOException ex) {
                throw new UncheckedIOException("Failed to register paths for watching: " + paths, ex);
            }
        }
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        synchronized (this.lock) {
            if (this.thread != null) {
                this.thread.close();
                this.thread.interrupt();
                try {
                    this.thread.join();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                this.thread = null;
            }
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/ssl/FileWatcher$WatcherThread.class */
    private class WatcherThread extends Thread implements Closeable {
        private final WatchService watchService = FileSystems.getDefault().newWatchService();
        private final Map<WatchKey, List<Registration>> registrations = new ConcurrentHashMap();
        private volatile boolean running = true;

        WatcherThread() throws IOException {
            setName("ssl-bundle-watcher");
            setDaemon(true);
            setUncaughtExceptionHandler(this::onThreadException);
        }

        private void onThreadException(Thread thread, Throwable throwable) {
            FileWatcher.logger.error("Uncaught exception in file watcher thread", throwable);
        }

        void register(Registration registration) throws IOException {
            for (Path path : registration.paths()) {
                if (!Files.isRegularFile(path, new LinkOption[0]) && !Files.isDirectory(path, new LinkOption[0])) {
                    throw new IOException("'%s' is neither a file nor a directory".formatted(path));
                }
                Path directory = Files.isDirectory(path, new LinkOption[0]) ? path : path.getParent();
                WatchKey watchKey = register(directory);
                this.registrations.computeIfAbsent(watchKey, key -> {
                    return new CopyOnWriteArrayList();
                }).add(registration);
            }
        }

        private WatchKey register(Path directory) throws IOException {
            FileWatcher.logger.debug(LogMessage.format("Registering '%s'", directory));
            return directory.register(this.watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            FileWatcher.logger.debug("Watch thread started");
            Set<Runnable> actions = new HashSet<>();
            while (this.running) {
                try {
                    long timeout = FileWatcher.this.quietPeriod.toMillis();
                    WatchKey key = this.watchService.poll(timeout, TimeUnit.MILLISECONDS);
                    if (key == null) {
                        actions.forEach(this::runSafely);
                        actions.clear();
                    } else {
                        accumulate(key, actions);
                        key.reset();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (ClosedWatchServiceException e2) {
                    FileWatcher.logger.debug("File watcher has been closed");
                    this.running = false;
                }
            }
            FileWatcher.logger.debug("Watch thread stopped");
        }

        private void runSafely(Runnable action) {
            try {
                action.run();
            } catch (Throwable ex) {
                FileWatcher.logger.error("Unexpected SSL reload error", ex);
            }
        }

        private void accumulate(WatchKey key, Set<Runnable> actions) {
            List<Registration> registrations = this.registrations.get(key);
            Path directory = (Path) key.watchable();
            for (WatchEvent<?> event : key.pollEvents()) {
                Path file = directory.resolve((Path) event.context());
                for (Registration registration : registrations) {
                    if (registration.manages(file)) {
                        actions.add(registration.action());
                    }
                }
            }
        }

        @Override // java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            this.running = false;
            this.watchService.close();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/ssl/FileWatcher$Registration.class */
    public static final class Registration extends Record {
        private final Set<Path> paths;
        private final Runnable action;

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Registration.class), Registration.class, "paths;action", "FIELD:Lorg/springframework/boot/autoconfigure/ssl/FileWatcher$Registration;->paths:Ljava/util/Set;", "FIELD:Lorg/springframework/boot/autoconfigure/ssl/FileWatcher$Registration;->action:Ljava/lang/Runnable;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Registration.class), Registration.class, "paths;action", "FIELD:Lorg/springframework/boot/autoconfigure/ssl/FileWatcher$Registration;->paths:Ljava/util/Set;", "FIELD:Lorg/springframework/boot/autoconfigure/ssl/FileWatcher$Registration;->action:Ljava/lang/Runnable;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Registration.class, Object.class), Registration.class, "paths;action", "FIELD:Lorg/springframework/boot/autoconfigure/ssl/FileWatcher$Registration;->paths:Ljava/util/Set;", "FIELD:Lorg/springframework/boot/autoconfigure/ssl/FileWatcher$Registration;->action:Ljava/lang/Runnable;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public Set<Path> paths() {
            return this.paths;
        }

        public Runnable action() {
            return this.action;
        }

        Registration(Set<Path> paths, Runnable action) {
            this.paths = (Set) paths.stream().map((v0) -> {
                return v0.toAbsolutePath();
            }).collect(Collectors.toSet());
            this.action = action;
        }

        boolean manages(Path file) {
            Path absolutePath = file.toAbsolutePath();
            return this.paths.contains(absolutePath) || isInDirectories(absolutePath);
        }

        private boolean isInDirectories(Path file) {
            Stream<Path> filter = this.paths.stream().filter(x$0 -> {
                return Files.isDirectory(x$0, new LinkOption[0]);
            });
            Objects.requireNonNull(file);
            return filter.anyMatch(file::startsWith);
        }
    }
}
