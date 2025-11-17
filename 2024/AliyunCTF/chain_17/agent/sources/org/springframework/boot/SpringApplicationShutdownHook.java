package org.springframework.boot;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.util.Assert;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/SpringApplicationShutdownHook.class */
public class SpringApplicationShutdownHook implements Runnable {
    private static final int SLEEP = 50;
    private static final long TIMEOUT = TimeUnit.MINUTES.toMillis(10);
    private static final Log logger = LogFactory.getLog((Class<?>) SpringApplicationShutdownHook.class);
    private final Handlers handlers = new Handlers();
    private final Set<ConfigurableApplicationContext> contexts = new LinkedHashSet();
    private final Set<ConfigurableApplicationContext> closedContexts = Collections.newSetFromMap(new WeakHashMap());
    private final ApplicationContextClosedListener contextCloseListener = new ApplicationContextClosedListener();
    private final AtomicBoolean shutdownHookAdded = new AtomicBoolean();
    private volatile boolean shutdownHookAdditionEnabled = false;
    private boolean inProgress;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SpringApplicationShutdownHandlers getHandlers() {
        return this.handlers;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void enableShutdownHookAddition() {
        this.shutdownHookAdditionEnabled = true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void registerApplicationContext(ConfigurableApplicationContext context) {
        addRuntimeShutdownHookIfNecessary();
        synchronized (SpringApplicationShutdownHook.class) {
            assertNotInProgress();
            context.addApplicationListener(this.contextCloseListener);
            this.contexts.add(context);
        }
    }

    private void addRuntimeShutdownHookIfNecessary() {
        if (this.shutdownHookAdditionEnabled && this.shutdownHookAdded.compareAndSet(false, true)) {
            addRuntimeShutdownHook();
        }
    }

    void addRuntimeShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this, "SpringApplicationShutdownHook"));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void deregisterFailedApplicationContext(ConfigurableApplicationContext applicationContext) {
        synchronized (SpringApplicationShutdownHook.class) {
            Assert.state(!applicationContext.isActive(), "Cannot unregister active application context");
            this.contexts.remove(applicationContext);
        }
    }

    @Override // java.lang.Runnable
    public void run() {
        Set<ConfigurableApplicationContext> contexts;
        Set<ConfigurableApplicationContext> closedContexts;
        Set<Runnable> actions;
        synchronized (SpringApplicationShutdownHook.class) {
            this.inProgress = true;
            contexts = new LinkedHashSet<>(this.contexts);
            closedContexts = new LinkedHashSet<>(this.closedContexts);
            actions = new LinkedHashSet<>(this.handlers.getActions());
        }
        contexts.forEach(this::closeAndWait);
        closedContexts.forEach(this::closeAndWait);
        actions.forEach((v0) -> {
            v0.run();
        });
    }

    boolean isApplicationContextRegistered(ConfigurableApplicationContext context) {
        boolean contains;
        synchronized (SpringApplicationShutdownHook.class) {
            contains = this.contexts.contains(context);
        }
        return contains;
    }

    void reset() {
        synchronized (SpringApplicationShutdownHook.class) {
            this.contexts.clear();
            this.closedContexts.clear();
            this.handlers.getActions().clear();
            this.inProgress = false;
        }
    }

    private void closeAndWait(ConfigurableApplicationContext context) {
        if (!context.isActive()) {
            return;
        }
        context.close();
        int waited = 0;
        while (context.isActive()) {
            try {
                if (waited > TIMEOUT) {
                    throw new TimeoutException();
                }
                Thread.sleep(50L);
                waited += 50;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warn("Interrupted waiting for application context " + context + " to become inactive");
                return;
            } catch (TimeoutException ex) {
                logger.warn("Timed out waiting for application context " + context + " to become inactive", ex);
                return;
            }
        }
    }

    private void assertNotInProgress() {
        Assert.state(!this.inProgress, "Shutdown in progress");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/SpringApplicationShutdownHook$Handlers.class */
    public final class Handlers implements SpringApplicationShutdownHandlers, Runnable {
        private final Set<Runnable> actions = Collections.newSetFromMap(new IdentityHashMap());

        private Handlers() {
        }

        @Override // org.springframework.boot.SpringApplicationShutdownHandlers
        public void add(Runnable action) {
            Assert.notNull(action, "Action must not be null");
            SpringApplicationShutdownHook.this.addRuntimeShutdownHookIfNecessary();
            synchronized (SpringApplicationShutdownHook.class) {
                SpringApplicationShutdownHook.this.assertNotInProgress();
                this.actions.add(action);
            }
        }

        @Override // org.springframework.boot.SpringApplicationShutdownHandlers
        public void remove(Runnable action) {
            Assert.notNull(action, "Action must not be null");
            synchronized (SpringApplicationShutdownHook.class) {
                SpringApplicationShutdownHook.this.assertNotInProgress();
                this.actions.remove(action);
            }
        }

        Set<Runnable> getActions() {
            return this.actions;
        }

        @Override // java.lang.Runnable
        public void run() {
            SpringApplicationShutdownHook.this.run();
            SpringApplicationShutdownHook.this.reset();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/SpringApplicationShutdownHook$ApplicationContextClosedListener.class */
    public final class ApplicationContextClosedListener implements ApplicationListener<ContextClosedEvent> {
        private ApplicationContextClosedListener() {
        }

        @Override // org.springframework.context.ApplicationListener
        public void onApplicationEvent(ContextClosedEvent event) {
            synchronized (SpringApplicationShutdownHook.class) {
                ApplicationContext applicationContext = event.getApplicationContext();
                SpringApplicationShutdownHook.this.contexts.remove(applicationContext);
                SpringApplicationShutdownHook.this.closedContexts.add((ConfigurableApplicationContext) applicationContext);
            }
        }
    }
}
