package ch.qos.logback.classic.net;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import javax.net.ServerSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* loaded from: server.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/net/SimpleSocketServer.class */
public class SimpleSocketServer extends Thread {
    private final int port;
    private final LoggerContext lc;
    private ServerSocket serverSocket;
    private CountDownLatch latch;
    Logger logger = LoggerFactory.getLogger((Class<?>) SimpleSocketServer.class);
    private boolean closed = false;
    private List<SocketNode> socketNodeList = new ArrayList();

    public static void main(String[] argv) throws Exception {
        doMain(SimpleSocketServer.class, argv);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void doMain(Class<? extends SimpleSocketServer> serverClass, String[] argv) throws Exception {
        int port = -1;
        if (argv.length == 2) {
            port = parsePortNumber(argv[0]);
        } else {
            usage("Wrong number of arguments.");
        }
        String configFile = argv[1];
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        configureLC(lc, configFile);
        SimpleSocketServer sss = new SimpleSocketServer(lc, port);
        sss.start();
    }

    public SimpleSocketServer(LoggerContext lc, int port) {
        this.lc = lc;
        this.port = port;
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        String oldThreadName = Thread.currentThread().getName();
        try {
            try {
                String newThreadName = getServerThreadName();
                Thread.currentThread().setName(newThreadName);
                this.logger.info("Listening on port " + this.port);
                this.serverSocket = getServerSocketFactory().createServerSocket(this.port);
                while (!this.closed) {
                    this.logger.info("Waiting to accept a new client.");
                    signalAlmostReadiness();
                    Socket socket = this.serverSocket.accept();
                    this.logger.info("Connected to client at " + String.valueOf(socket.getInetAddress()));
                    this.logger.info("Starting new socket node.");
                    SocketNode newSocketNode = new SocketNode(this, socket, this.lc);
                    synchronized (this.socketNodeList) {
                        this.socketNodeList.add(newSocketNode);
                    }
                    String clientThreadName = getClientThreadName(socket);
                    new Thread(newSocketNode, clientThreadName).start();
                }
                Thread.currentThread().setName(oldThreadName);
            } catch (Exception e) {
                if (this.closed) {
                    this.logger.info("Exception in run method for a closed server. This is normal.");
                } else {
                    this.logger.error("Unexpected failure in run method", (Throwable) e);
                }
                Thread.currentThread().setName(oldThreadName);
            }
        } catch (Throwable th) {
            Thread.currentThread().setName(oldThreadName);
            throw th;
        }
    }

    protected String getServerThreadName() {
        return String.format("Logback %s (port %d)", getClass().getSimpleName(), Integer.valueOf(this.port));
    }

    protected String getClientThreadName(Socket socket) {
        return String.format("Logback SocketNode (client: %s)", socket.getRemoteSocketAddress());
    }

    protected ServerSocketFactory getServerSocketFactory() {
        return ServerSocketFactory.getDefault();
    }

    void signalAlmostReadiness() {
        if (this.latch != null && this.latch.getCount() != 0) {
            this.latch.countDown();
        }
    }

    void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }

    public CountDownLatch getLatch() {
        return this.latch;
    }

    public boolean isClosed() {
        return this.closed;
    }

    public void close() {
        this.closed = true;
        try {
        } catch (IOException e) {
            this.logger.error("Failed to close serverSocket", (Throwable) e);
        } finally {
            this.serverSocket = null;
        }
        if (this.serverSocket != null) {
            this.serverSocket.close();
        }
        this.logger.info("closing this server");
        synchronized (this.socketNodeList) {
            for (SocketNode sn : this.socketNodeList) {
                sn.close();
            }
        }
        if (this.socketNodeList.size() != 0) {
            this.logger.warn("Was expecting a 0-sized socketNodeList after server shutdown");
        }
    }

    public void socketNodeClosing(SocketNode sn) {
        this.logger.debug("Removing {}", sn);
        synchronized (this.socketNodeList) {
            this.socketNodeList.remove(sn);
        }
    }

    static void usage(String msg) {
        System.err.println(msg);
        System.err.println("Usage: java " + SimpleSocketServer.class.getName() + " port configFile");
        System.exit(1);
    }

    static int parsePortNumber(String portStr) {
        try {
            return Integer.parseInt(portStr);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            usage("Could not interpret port number [" + portStr + "].");
            return -1;
        }
    }

    public static void configureLC(LoggerContext lc, String configFile) throws JoranException {
        JoranConfigurator configurator = new JoranConfigurator();
        lc.reset();
        configurator.setContext(lc);
        configurator.doConfigure(configFile);
    }
}
