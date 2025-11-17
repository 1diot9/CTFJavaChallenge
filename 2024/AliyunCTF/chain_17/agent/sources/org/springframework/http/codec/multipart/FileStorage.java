package org.springframework.http.codec.multipart;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.function.Supplier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/multipart/FileStorage.class */
public abstract class FileStorage {
    private static final Log logger = LogFactory.getLog((Class<?>) FileStorage.class);

    public abstract Mono<Path> directory();

    protected FileStorage() {
    }

    public static FileStorage fromPath(Path path) throws IOException {
        if (!Files.exists(path, new LinkOption[0])) {
            Files.createDirectory(path, new FileAttribute[0]);
        }
        return new PathFileStorage(path);
    }

    public static FileStorage tempDirectory(Supplier<Scheduler> scheduler) {
        return new TempFileStorage(scheduler);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/multipart/FileStorage$PathFileStorage.class */
    private static final class PathFileStorage extends FileStorage {
        private final Mono<Path> directory;

        public PathFileStorage(Path directory) {
            this.directory = Mono.just(directory);
        }

        @Override // org.springframework.http.codec.multipart.FileStorage
        public Mono<Path> directory() {
            return this.directory;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/multipart/FileStorage$TempFileStorage.class */
    private static final class TempFileStorage extends FileStorage {
        private static final String IDENTIFIER = "spring-multipart-";
        private final Supplier<Scheduler> scheduler;
        private volatile Mono<Path> directory = tempDirectory();

        public TempFileStorage(Supplier<Scheduler> scheduler) {
            this.scheduler = scheduler;
        }

        @Override // org.springframework.http.codec.multipart.FileStorage
        public Mono<Path> directory() {
            return this.directory.flatMap(this::createNewDirectoryIfDeleted).subscribeOn(this.scheduler.get());
        }

        private Mono<Path> createNewDirectoryIfDeleted(Path directory) {
            if (!Files.exists(directory, new LinkOption[0])) {
                Mono<Path> newDirectory = tempDirectory();
                this.directory = newDirectory;
                return newDirectory;
            }
            return Mono.just(directory);
        }

        private static Mono<Path> tempDirectory() {
            return Mono.fromCallable(() -> {
                Path directory = Files.createTempDirectory(IDENTIFIER, new FileAttribute[0]);
                if (FileStorage.logger.isDebugEnabled()) {
                    FileStorage.logger.debug("Created temporary storage directory: " + directory);
                }
                return directory;
            }).cache();
        }
    }
}
