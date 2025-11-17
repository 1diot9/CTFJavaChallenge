package ch.qos.logback.core;

import ch.qos.logback.core.recovery.ResilientFileOutputStream;
import ch.qos.logback.core.util.ContextUtil;
import ch.qos.logback.core.util.FileSize;
import ch.qos.logback.core.util.FileUtil;
import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Map;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/FileAppender.class */
public class FileAppender<E> extends OutputStreamAppender<E> {
    public static final long DEFAULT_BUFFER_SIZE = 8192;
    protected static String COLLISION_WITH_EARLIER_APPENDER_URL = "http://logback.qos.ch/codes.html#earlier_fa_collision";
    protected boolean append = true;
    protected String fileName = null;
    private boolean prudent = false;
    private FileSize bufferSize = new FileSize(DEFAULT_BUFFER_SIZE);

    public void setFile(String file) {
        if (file == null) {
            this.fileName = file;
        } else {
            this.fileName = file.trim();
        }
    }

    public boolean isAppend() {
        return this.append;
    }

    public final String rawFileProperty() {
        return this.fileName;
    }

    public String getFile() {
        return this.fileName;
    }

    @Override // ch.qos.logback.core.OutputStreamAppender, ch.qos.logback.core.UnsynchronizedAppenderBase, ch.qos.logback.core.spi.LifeCycle
    public void start() {
        int errors = 0;
        if (getFile() != null) {
            addInfo("File property is set to [" + this.fileName + "]");
            if (this.prudent && !isAppend()) {
                setAppend(true);
                addWarn("Setting \"Append\" property to true on account of \"Prudent\" mode");
            }
            if (checkForFileCollisionInPreviousFileAppenders()) {
                addError("Collisions detected with FileAppender/RollingAppender instances defined earlier. Aborting.");
                addError("For more information, please visit " + COLLISION_WITH_EARLIER_APPENDER_URL);
                errors = 0 + 1;
            } else {
                try {
                    openFile(getFile());
                } catch (IOException e) {
                    errors = 0 + 1;
                    addError("openFile(" + this.fileName + "," + this.append + ") call failed.", e);
                }
            }
        } else {
            errors = 0 + 1;
            addError("\"File\" property not set for appender named [" + this.name + "].");
        }
        if (errors == 0) {
            super.start();
        }
    }

    @Override // ch.qos.logback.core.OutputStreamAppender, ch.qos.logback.core.UnsynchronizedAppenderBase, ch.qos.logback.core.spi.LifeCycle
    public void stop() {
        if (!isStarted()) {
            return;
        }
        super.stop();
        Map<String, String> map = ContextUtil.getFilenameCollisionMap(this.context);
        if (map == null || getName() == null) {
            return;
        }
        map.remove(getName());
    }

    protected boolean checkForFileCollisionInPreviousFileAppenders() {
        Map<String, String> previousFilesMap;
        boolean collisionsDetected = false;
        if (this.fileName != null && (previousFilesMap = (Map) this.context.getObject(CoreConstants.FA_FILENAME_COLLISION_MAP)) != null) {
            for (Map.Entry<String, String> entry : previousFilesMap.entrySet()) {
                if (this.fileName.equals(entry.getValue())) {
                    addErrorForCollision("File", entry.getValue(), entry.getKey());
                    collisionsDetected = true;
                }
            }
            if (this.name != null) {
                previousFilesMap.put(getName(), this.fileName);
            }
            return collisionsDetected;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void addErrorForCollision(String optionName, String optionValue, String appenderName) {
        addError("'" + optionName + "' option has the same value \"" + optionValue + "\" as that given for appender [" + appenderName + "] defined earlier.");
    }

    public void openFile(String file_name) throws IOException {
        this.streamWriteLock.lock();
        try {
            File file = new File(file_name);
            boolean result = FileUtil.createMissingParentDirectories(file);
            if (!result) {
                addError("Failed to create parent directories for [" + file.getAbsolutePath() + "]");
            }
            ResilientFileOutputStream resilientFos = new ResilientFileOutputStream(file, this.append, this.bufferSize.getSize());
            resilientFos.setContext(this.context);
            setOutputStream(resilientFos);
            this.streamWriteLock.unlock();
        } catch (Throwable th) {
            this.streamWriteLock.unlock();
            throw th;
        }
    }

    public boolean isPrudent() {
        return this.prudent;
    }

    public void setPrudent(boolean prudent) {
        this.prudent = prudent;
    }

    public void setAppend(boolean append) {
        this.append = append;
    }

    public void setBufferSize(FileSize bufferSize) {
        addInfo("Setting bufferSize to [" + bufferSize.toString() + "]");
        this.bufferSize = bufferSize;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.OutputStreamAppender
    public void writeOut(E event) throws IOException {
        if (this.prudent) {
            safeWriteOut(event);
        } else {
            super.writeOut(event);
        }
    }

    private void safeWriteOut(E event) {
        byte[] byteArray = this.encoder.encode(event);
        if (byteArray == null || byteArray.length == 0) {
            return;
        }
        this.streamWriteLock.lock();
        try {
            safeWriteBytes(byteArray);
        } finally {
            this.streamWriteLock.unlock();
        }
    }

    private void safeWriteBytes(byte[] byteArray) {
        ResilientFileOutputStream resilientFOS = (ResilientFileOutputStream) getOutputStream();
        FileChannel fileChannel = resilientFOS.getChannel();
        if (fileChannel == null) {
            return;
        }
        boolean interrupted = Thread.interrupted();
        FileLock fileLock = null;
        try {
            try {
                fileLock = fileChannel.lock();
                long position = fileChannel.position();
                long size = fileChannel.size();
                if (size != position) {
                    fileChannel.position(size);
                }
                writeByteArrayToOutputStreamWithPossibleFlush(byteArray);
                releaseFileLock(fileLock);
                if (interrupted) {
                    Thread.currentThread().interrupt();
                }
            } catch (IOException e) {
                resilientFOS.postIOFailure(e);
                releaseFileLock(fileLock);
                if (interrupted) {
                    Thread.currentThread().interrupt();
                }
            }
        } catch (Throwable th) {
            releaseFileLock(fileLock);
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
            throw th;
        }
    }

    private void releaseFileLock(FileLock fileLock) {
        if (fileLock != null && fileLock.isValid()) {
            try {
                fileLock.release();
            } catch (IOException e) {
                addError("failed to release lock", e);
            }
        }
    }
}
