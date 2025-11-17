package ch.qos.logback.core.net.server;

import ch.qos.logback.core.net.server.Client;
import ch.qos.logback.core.spi.ContextAwareBase;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/net/server/ConcurrentServerRunner.class */
public abstract class ConcurrentServerRunner<T extends Client> extends ContextAwareBase implements Runnable, ServerRunner<T> {
    private final Lock clientsLock = new ReentrantLock();
    private final Collection<T> clients = new ArrayList();
    private final ServerListener<T> listener;
    private final Executor executor;
    private boolean running;

    protected abstract boolean configureClient(T t);

    public ConcurrentServerRunner(ServerListener<T> listener, Executor executor) {
        this.listener = listener;
        this.executor = executor;
    }

    @Override // ch.qos.logback.core.net.server.ServerRunner
    public boolean isRunning() {
        return this.running;
    }

    protected void setRunning(boolean running) {
        this.running = running;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // ch.qos.logback.core.net.server.ServerRunner
    public void stop() throws IOException {
        this.listener.close();
        accept(new ClientVisitor<T>() { // from class: ch.qos.logback.core.net.server.ConcurrentServerRunner.1
            @Override // ch.qos.logback.core.net.server.ClientVisitor
            public void visit(T client) {
                client.close();
            }
        });
    }

    @Override // ch.qos.logback.core.net.server.ServerRunner
    public void accept(ClientVisitor<T> visitor) {
        Collection<T> clients = copyClients();
        for (T client : clients) {
            try {
                visitor.visit(client);
            } catch (RuntimeException ex) {
                addError(String.valueOf(client) + ": " + String.valueOf(ex));
            }
        }
    }

    private Collection<T> copyClients() {
        this.clientsLock.lock();
        try {
            Collection<T> copy = new ArrayList<>((Collection<? extends T>) this.clients);
            return copy;
        } finally {
            this.clientsLock.unlock();
        }
    }

    @Override // java.lang.Runnable
    public void run() {
        setRunning(true);
        try {
            addInfo("listening on " + String.valueOf(this.listener));
            while (!Thread.currentThread().isInterrupted()) {
                T client = this.listener.acceptClient();
                if (!configureClient(client)) {
                    addError(String.valueOf(client) + ": connection dropped");
                    client.close();
                } else {
                    try {
                        this.executor.execute(new ClientWrapper(client));
                    } catch (RejectedExecutionException e) {
                        addError(String.valueOf(client) + ": connection dropped");
                        client.close();
                    }
                }
            }
        } catch (InterruptedException e2) {
        } catch (Exception ex) {
            addError("listener: " + String.valueOf(ex));
        }
        setRunning(false);
        addInfo("shutting down");
        this.listener.close();
    }

    private void addClient(T client) {
        this.clientsLock.lock();
        try {
            this.clients.add(client);
        } finally {
            this.clientsLock.unlock();
        }
    }

    private void removeClient(T client) {
        this.clientsLock.lock();
        try {
            this.clients.remove(client);
        } finally {
            this.clientsLock.unlock();
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/net/server/ConcurrentServerRunner$ClientWrapper.class */
    private class ClientWrapper implements Client {
        private final T delegate;

        public ClientWrapper(T client) {
            this.delegate = client;
        }

        @Override // java.lang.Runnable
        public void run() {
            ConcurrentServerRunner.this.addClient(this.delegate);
            try {
                this.delegate.run();
            } finally {
                ConcurrentServerRunner.this.removeClient(this.delegate);
            }
        }

        @Override // ch.qos.logback.core.net.server.Client, java.io.Closeable, java.lang.AutoCloseable
        public void close() {
            this.delegate.close();
        }
    }
}
