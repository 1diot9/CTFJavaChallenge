package org.h2.store;

import java.io.IOException;
import java.io.OutputStream;
import java.net.BindException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Properties;
import org.h2.Driver;
import org.h2.api.ErrorCode;
import org.h2.engine.Constants;
import org.h2.message.DbException;
import org.h2.message.Trace;
import org.h2.message.TraceSystem;
import org.h2.store.fs.FilePath;
import org.h2.store.fs.FileUtils;
import org.h2.util.MathUtils;
import org.h2.util.NetUtils;
import org.h2.util.SortedProperties;
import org.h2.util.StringUtils;
import org.h2.value.Transfer;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.util.backoff.ExponentialBackOff;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/store/FileLock.class */
public class FileLock implements Runnable {
    private static final String MAGIC = "FileLock";
    private static final String FILE = "file";
    private static final String SOCKET = "socket";
    private static final int RANDOM_BYTES = 16;
    private static final int SLEEP_GAP = 25;
    private static final int TIME_GRANULARITY = 2000;
    private volatile String fileName;
    private volatile ServerSocket serverSocket;
    private volatile boolean locked;
    private final int sleep;
    private final Trace trace;
    private long lastWrite;
    private String method;
    private Properties properties;
    private String uniqueId;
    private Thread watchdog;

    public FileLock(TraceSystem traceSystem, String str, int i) {
        this.trace = traceSystem == null ? null : traceSystem.getTrace(4);
        this.fileName = str;
        this.sleep = i;
    }

    public synchronized void lock(FileLockMethod fileLockMethod) {
        checkServer();
        if (this.locked) {
            throw DbException.getInternalError("already locked");
        }
        switch (fileLockMethod) {
            case FILE:
                lockFile();
                break;
            case SOCKET:
                lockSocket();
                break;
        }
        this.locked = true;
    }

    public synchronized void unlock() {
        if (!this.locked) {
            return;
        }
        this.locked = false;
        try {
            if (this.watchdog != null) {
                this.watchdog.interrupt();
            }
        } catch (Exception e) {
            this.trace.debug(e, "unlock");
        }
        try {
            if (this.fileName != null && load().equals(this.properties)) {
                FileUtils.delete(this.fileName);
            }
            if (this.serverSocket != null) {
                this.serverSocket.close();
            }
        } catch (Exception e2) {
            this.trace.debug(e2, "unlock");
        } finally {
            this.fileName = null;
            this.serverSocket = null;
        }
        try {
            if (this.watchdog != null) {
                this.watchdog.join();
            }
        } catch (Exception e3) {
            this.trace.debug(e3, "unlock");
        } finally {
            this.watchdog = null;
        }
    }

    public void setProperty(String str, String str2) {
        if (str2 == null) {
            this.properties.remove(str);
        } else {
            this.properties.put(str, str2);
        }
    }

    public Properties save() {
        try {
            OutputStream newOutputStream = FileUtils.newOutputStream(this.fileName, false);
            Throwable th = null;
            try {
                this.properties.store(newOutputStream, MAGIC);
                if (newOutputStream != null) {
                    if (0 != 0) {
                        try {
                            newOutputStream.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        newOutputStream.close();
                    }
                }
                this.lastWrite = aggressiveLastModified(this.fileName);
                if (this.trace.isDebugEnabled()) {
                    this.trace.debug("save " + this.properties);
                }
                return this.properties;
            } finally {
            }
        } catch (IOException e) {
            throw getExceptionFatal("Could not save properties " + this.fileName, e);
        }
    }

    private static long aggressiveLastModified(String str) {
        try {
            FileChannel open = FilePath.get(str).open("rws");
            Throwable th = null;
            try {
                open.read(ByteBuffer.wrap(new byte[1]));
                if (open != null) {
                    if (0 != 0) {
                        try {
                            open.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        open.close();
                    }
                }
            } finally {
            }
        } catch (IOException e) {
        }
        return FileUtils.lastModified(str);
    }

    private void checkServer() {
        Properties load = load();
        String property = load.getProperty("server");
        if (property == null) {
            return;
        }
        boolean z = false;
        String property2 = load.getProperty("id");
        try {
            Socket createSocket = NetUtils.createSocket(property, Constants.DEFAULT_TCP_PORT, false);
            Transfer transfer = new Transfer(null, createSocket);
            transfer.init();
            transfer.writeInt(17);
            transfer.writeInt(20);
            transfer.writeString(null);
            transfer.writeString(null);
            transfer.writeString(property2);
            transfer.writeInt(14);
            transfer.flush();
            if (transfer.readInt() == 1) {
                z = true;
            }
            transfer.close();
            createSocket.close();
            if (z) {
                throw DbException.get(ErrorCode.DATABASE_ALREADY_OPEN_1, "Server is running").addSQL(property + "/" + property2);
            }
        } catch (IOException e) {
        }
    }

    public Properties load() {
        IOException iOException = null;
        for (int i = 0; i < 5; i++) {
            try {
                SortedProperties loadProperties = SortedProperties.loadProperties(this.fileName);
                if (this.trace.isDebugEnabled()) {
                    this.trace.debug("load " + loadProperties);
                }
                return loadProperties;
            } catch (IOException e) {
                iOException = e;
            }
        }
        throw getExceptionFatal("Could not load properties " + this.fileName, iOException);
    }

    private void waitUntilOld() {
        for (int i = 0; i < 160; i++) {
            long currentTimeMillis = System.currentTimeMillis() - aggressiveLastModified(this.fileName);
            if (currentTimeMillis < -2000) {
                try {
                    Thread.sleep(2 * this.sleep);
                    return;
                } catch (Exception e) {
                    this.trace.debug(e, "sleep");
                    return;
                }
            }
            if (currentTimeMillis > ExponentialBackOff.DEFAULT_INITIAL_INTERVAL) {
                return;
            }
            try {
                Thread.sleep(25L);
            } catch (Exception e2) {
                this.trace.debug(e2, "sleep");
            }
        }
        throw getExceptionFatal("Lock file recently modified", null);
    }

    private void setUniqueId() {
        this.uniqueId = Long.toHexString(System.currentTimeMillis()) + StringUtils.convertBytesToHex(MathUtils.secureRandomBytes(16));
        this.properties.setProperty("id", this.uniqueId);
    }

    private void lockFile() {
        this.method = "file";
        this.properties = new SortedProperties();
        this.properties.setProperty("method", String.valueOf(this.method));
        setUniqueId();
        FileUtils.createDirectories(FileUtils.getParent(this.fileName));
        if (!FileUtils.createFile(this.fileName)) {
            waitUntilOld();
            String property = load().getProperty("method", "file");
            if (!property.equals("file")) {
                throw getExceptionFatal("Unsupported lock method " + property, null);
            }
            save();
            sleep(2 * this.sleep);
            if (!load().equals(this.properties)) {
                throw getExceptionAlreadyInUse("Locked by another process: " + this.fileName);
            }
            FileUtils.delete(this.fileName);
            if (!FileUtils.createFile(this.fileName)) {
                throw getExceptionFatal("Another process was faster", null);
            }
        }
        save();
        sleep(25);
        if (!load().equals(this.properties)) {
            this.fileName = null;
            throw getExceptionFatal("Concurrent update", null);
        }
        this.locked = true;
        this.watchdog = new Thread(this, "H2 File Lock Watchdog " + this.fileName);
        Driver.setThreadContextClassLoader(this.watchdog);
        this.watchdog.setDaemon(true);
        this.watchdog.setPriority(9);
        this.watchdog.start();
    }

    private void lockSocket() {
        this.method = SOCKET;
        this.properties = new SortedProperties();
        this.properties.setProperty("method", String.valueOf(this.method));
        setUniqueId();
        String localAddress = NetUtils.getLocalAddress();
        FileUtils.createDirectories(FileUtils.getParent(this.fileName));
        if (!FileUtils.createFile(this.fileName)) {
            waitUntilOld();
            long aggressiveLastModified = aggressiveLastModified(this.fileName);
            Properties load = load();
            String property = load.getProperty("method", SOCKET);
            if (property.equals("file")) {
                lockFile();
                return;
            }
            if (!property.equals(SOCKET)) {
                throw getExceptionFatal("Unsupported lock method " + property, null);
            }
            String property2 = load.getProperty("ipAddress", localAddress);
            if (!localAddress.equals(property2)) {
                throw getExceptionAlreadyInUse("Locked by another computer: " + property2);
            }
            String property3 = load.getProperty("port", CustomBooleanEditor.VALUE_0);
            int parseInt = Integer.parseInt(property3);
            try {
                InetAddress byName = InetAddress.getByName(property2);
                for (int i = 0; i < 3; i++) {
                    try {
                        new Socket(byName, parseInt).close();
                        throw getExceptionAlreadyInUse("Locked by another process");
                        break;
                    } catch (BindException e) {
                        throw getExceptionFatal("Bind Exception", null);
                    } catch (ConnectException e2) {
                        this.trace.debug(e2, "socket not connected to port " + property3);
                    } catch (IOException e3) {
                        throw getExceptionFatal("IOException", null);
                    }
                }
                if (aggressiveLastModified != aggressiveLastModified(this.fileName)) {
                    throw getExceptionFatal("Concurrent update", null);
                }
                FileUtils.delete(this.fileName);
                if (!FileUtils.createFile(this.fileName)) {
                    throw getExceptionFatal("Another process was faster", null);
                }
            } catch (UnknownHostException e4) {
                throw getExceptionFatal("Unknown host " + property2, e4);
            }
        }
        try {
            this.serverSocket = NetUtils.createServerSocket(0, false);
            int localPort = this.serverSocket.getLocalPort();
            this.properties.setProperty("ipAddress", localAddress);
            this.properties.setProperty("port", Integer.toString(localPort));
            save();
            this.locked = true;
            this.watchdog = new Thread(this, "H2 File Lock Watchdog (Socket) " + this.fileName);
            this.watchdog.setDaemon(true);
            this.watchdog.start();
        } catch (Exception e5) {
            this.trace.debug(e5, "lock");
            this.serverSocket = null;
            lockFile();
        }
    }

    private static void sleep(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            throw getExceptionFatal("Sleep interrupted", e);
        }
    }

    private static DbException getExceptionFatal(String str, Throwable th) {
        return DbException.get(ErrorCode.ERROR_OPENING_DATABASE_1, th, str);
    }

    private DbException getExceptionAlreadyInUse(String str) {
        DbException dbException = DbException.get(ErrorCode.DATABASE_ALREADY_OPEN_1, str);
        if (this.fileName != null) {
            try {
                Properties load = load();
                String property = load.getProperty("server");
                if (property != null) {
                    dbException = dbException.addSQL(property + "/" + load.getProperty("id"));
                }
            } catch (DbException e) {
            }
        }
        return dbException;
    }

    public static FileLockMethod getFileLockMethod(String str) {
        if (str == null || str.equalsIgnoreCase("FILE")) {
            return FileLockMethod.FILE;
        }
        if (str.equalsIgnoreCase("NO")) {
            return FileLockMethod.NO;
        }
        if (str.equalsIgnoreCase("SOCKET")) {
            return FileLockMethod.SOCKET;
        }
        if (str.equalsIgnoreCase("FS")) {
            return FileLockMethod.FS;
        }
        throw DbException.get(ErrorCode.UNSUPPORTED_LOCK_METHOD_1, str);
    }

    public String getUniqueId() {
        return this.uniqueId;
    }

    @Override // java.lang.Runnable
    public void run() {
        while (this.locked && this.fileName != null) {
            try {
                try {
                    if (!FileUtils.exists(this.fileName) || aggressiveLastModified(this.fileName) != this.lastWrite) {
                        save();
                    }
                    Thread.sleep(this.sleep);
                } catch (InterruptedException | NullPointerException | OutOfMemoryError e) {
                } catch (Exception e2) {
                    this.trace.debug(e2, "watchdog");
                }
            } catch (Exception e3) {
                this.trace.debug(e3, "watchdog");
            }
        }
        while (true) {
            ServerSocket serverSocket = this.serverSocket;
            if (serverSocket == null) {
                break;
            }
            try {
                this.trace.debug("watchdog accept");
                serverSocket.accept().close();
            } catch (Exception e4) {
                this.trace.debug(e4, "watchdog");
            }
        }
        this.trace.debug("watchdog end");
    }
}
