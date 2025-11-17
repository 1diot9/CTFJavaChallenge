package org.apache.catalina.valves;

import java.io.BufferedWriter;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.apache.catalina.LifecycleException;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.buf.B2CConverter;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/AccessLogValve.class */
public class AccessLogValve extends AbstractAccessLogValve {
    private static final Log log = LogFactory.getLog((Class<?>) AccessLogValve.class);
    private volatile String dateStamp = "";
    private String directory = "logs";
    protected volatile String prefix = "access_log";
    protected boolean rotatable = true;
    protected boolean renameOnRotate = false;
    private boolean buffered = true;
    protected volatile String suffix = "";
    protected PrintWriter writer = null;
    protected SimpleDateFormat fileDateFormatter = null;
    protected File currentLogFile = null;
    private volatile long rotationLastChecked = 0;
    private boolean checkExists = false;
    protected String fileDateFormat = ".yyyy-MM-dd";
    protected volatile String encoding = null;
    private int maxDays = -1;
    private volatile boolean checkForOldLogs = false;

    public int getMaxDays() {
        return this.maxDays;
    }

    public void setMaxDays(int maxDays) {
        this.maxDays = maxDays;
    }

    public String getDirectory() {
        return this.directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public boolean isCheckExists() {
        return this.checkExists;
    }

    public void setCheckExists(boolean checkExists) {
        this.checkExists = checkExists;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public boolean isRotatable() {
        return this.rotatable;
    }

    public void setRotatable(boolean rotatable) {
        this.rotatable = rotatable;
    }

    public boolean isRenameOnRotate() {
        return this.renameOnRotate;
    }

    public void setRenameOnRotate(boolean renameOnRotate) {
        this.renameOnRotate = renameOnRotate;
    }

    public boolean isBuffered() {
        return this.buffered;
    }

    public void setBuffered(boolean buffered) {
        this.buffered = buffered;
    }

    public String getSuffix() {
        return this.suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getFileDateFormat() {
        return this.fileDateFormat;
    }

    public void setFileDateFormat(String fileDateFormat) {
        String newFormat;
        if (fileDateFormat == null) {
            newFormat = "";
        } else {
            newFormat = fileDateFormat;
        }
        this.fileDateFormat = newFormat;
        synchronized (this) {
            this.fileDateFormatter = new SimpleDateFormat(newFormat, Locale.US);
            this.fileDateFormatter.setTimeZone(TimeZone.getDefault());
        }
    }

    public String getEncoding() {
        return this.encoding;
    }

    public void setEncoding(String encoding) {
        if (encoding != null && encoding.length() > 0) {
            this.encoding = encoding;
        } else {
            this.encoding = null;
        }
    }

    @Override // org.apache.catalina.valves.ValveBase, org.apache.catalina.Valve
    public synchronized void backgroundProcess() {
        String[] oldAccessLogs;
        if (getState().isAvailable() && getEnabled() && this.writer != null && this.buffered) {
            this.writer.flush();
        }
        int maxDays = this.maxDays;
        String prefix = this.prefix;
        String suffix = this.suffix;
        if (this.rotatable && this.checkForOldLogs && maxDays > 0) {
            long deleteIfLastModifiedBefore = System.currentTimeMillis() - ((((maxDays * 24) * 60) * 60) * 1000);
            File dir = getDirectoryFile();
            if (dir.isDirectory() && (oldAccessLogs = dir.list()) != null) {
                for (String oldAccessLog : oldAccessLogs) {
                    boolean match = false;
                    if (prefix != null && prefix.length() > 0) {
                        if (oldAccessLog.startsWith(prefix)) {
                            match = true;
                        }
                    }
                    if (suffix != null && suffix.length() > 0) {
                        if (oldAccessLog.endsWith(suffix)) {
                            match = true;
                        }
                    }
                    if (match) {
                        File file = new File(dir, oldAccessLog);
                        if (file.isFile() && file.lastModified() < deleteIfLastModifiedBefore && !file.delete()) {
                            log.warn(sm.getString("accessLogValve.deleteFail", file.getAbsolutePath()));
                        }
                    }
                }
            }
            this.checkForOldLogs = false;
        }
    }

    public void rotate() {
        if (this.rotatable) {
            long systime = System.currentTimeMillis();
            if (systime - this.rotationLastChecked > 1000) {
                synchronized (this) {
                    if (systime - this.rotationLastChecked > 1000) {
                        this.rotationLastChecked = systime;
                        String tsDate = this.fileDateFormatter.format(new Date(systime));
                        if (!this.dateStamp.equals(tsDate)) {
                            close(true);
                            this.dateStamp = tsDate;
                            open();
                        }
                    }
                }
            }
        }
    }

    public synchronized boolean rotate(String newFileName) {
        if (this.currentLogFile != null) {
            File holder = this.currentLogFile;
            close(false);
            try {
                holder.renameTo(new File(newFileName));
            } catch (Throwable e) {
                ExceptionUtils.handleThrowable(e);
                log.error(sm.getString("accessLogValve.rotateFail"), e);
            }
            this.dateStamp = this.fileDateFormatter.format(new Date(System.currentTimeMillis()));
            open();
            return true;
        }
        return false;
    }

    private File getDirectoryFile() {
        File dir = new File(this.directory);
        if (!dir.isAbsolute()) {
            dir = new File(getContainer().getCatalinaBase(), this.directory);
        }
        return dir;
    }

    private File getLogFile(boolean useDateStamp) {
        File pathname;
        File dir = getDirectoryFile();
        if (!dir.mkdirs() && !dir.isDirectory()) {
            log.error(sm.getString("accessLogValve.openDirFail", dir));
        }
        if (useDateStamp) {
            pathname = new File(dir.getAbsoluteFile(), this.prefix + this.dateStamp + this.suffix);
        } else {
            pathname = new File(dir.getAbsoluteFile(), this.prefix + this.suffix);
        }
        File parent = pathname.getParentFile();
        if (!parent.mkdirs() && !parent.isDirectory()) {
            log.error(sm.getString("accessLogValve.openDirFail", parent));
        }
        return pathname;
    }

    private void restore() {
        File newLogFile = getLogFile(false);
        File rotatedLogFile = getLogFile(true);
        if (rotatedLogFile.exists() && !newLogFile.exists() && !rotatedLogFile.equals(newLogFile)) {
            try {
                if (!rotatedLogFile.renameTo(newLogFile)) {
                    log.error(sm.getString("accessLogValve.renameFail", rotatedLogFile, newLogFile));
                }
            } catch (Throwable e) {
                ExceptionUtils.handleThrowable(e);
                log.error(sm.getString("accessLogValve.renameFail", rotatedLogFile, newLogFile), e);
            }
        }
    }

    private synchronized void close(boolean rename) {
        if (this.writer == null) {
            return;
        }
        this.writer.flush();
        this.writer.close();
        if (rename && this.renameOnRotate) {
            File newLogFile = getLogFile(true);
            if (!newLogFile.exists()) {
                try {
                    if (!this.currentLogFile.renameTo(newLogFile)) {
                        log.error(sm.getString("accessLogValve.renameFail", this.currentLogFile, newLogFile));
                    }
                } catch (Throwable e) {
                    ExceptionUtils.handleThrowable(e);
                    log.error(sm.getString("accessLogValve.renameFail", this.currentLogFile, newLogFile), e);
                }
            } else {
                log.error(sm.getString("accessLogValve.alreadyExists", this.currentLogFile, newLogFile));
            }
        }
        this.writer = null;
        this.dateStamp = "";
        this.currentLogFile = null;
    }

    @Override // org.apache.catalina.valves.AbstractAccessLogValve
    public void log(CharArrayWriter message) {
        rotate();
        if (this.checkExists) {
            synchronized (this) {
                if (this.currentLogFile != null && !this.currentLogFile.exists()) {
                    try {
                        close(false);
                    } catch (Throwable e) {
                        ExceptionUtils.handleThrowable(e);
                        log.info(sm.getString("accessLogValve.closeFail"), e);
                    }
                    this.dateStamp = this.fileDateFormatter.format(new Date(System.currentTimeMillis()));
                    open();
                }
            }
        }
        try {
            message.write(System.lineSeparator());
            synchronized (this) {
                if (this.writer != null) {
                    message.writeTo(this.writer);
                    if (!this.buffered) {
                        this.writer.flush();
                    }
                }
            }
        } catch (IOException ioe) {
            log.warn(sm.getString("accessLogValve.writeFail", message.toString()), ioe);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public synchronized void open() {
        File pathname = getLogFile(this.rotatable && !this.renameOnRotate);
        Charset charset = null;
        if (this.encoding != null) {
            try {
                charset = B2CConverter.getCharset(this.encoding);
            } catch (UnsupportedEncodingException ex) {
                log.error(sm.getString("accessLogValve.unsupportedEncoding", this.encoding), ex);
            }
        }
        if (charset == null) {
            charset = StandardCharsets.UTF_8;
        }
        try {
            this.writer = new PrintWriter((Writer) new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pathname, true), charset), 128000), false);
            this.currentLogFile = pathname;
        } catch (IOException e) {
            this.writer = null;
            this.currentLogFile = null;
            log.error(sm.getString("accessLogValve.openFail", pathname, System.getProperty("user.name")), e);
        }
        this.checkForOldLogs = true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.valves.ValveBase, org.apache.catalina.util.LifecycleBase
    public synchronized void startInternal() throws LifecycleException {
        String format = getFileDateFormat();
        this.fileDateFormatter = new SimpleDateFormat(format, Locale.US);
        this.fileDateFormatter.setTimeZone(TimeZone.getDefault());
        this.dateStamp = this.fileDateFormatter.format(new Date(System.currentTimeMillis()));
        if (this.rotatable && this.renameOnRotate) {
            restore();
        }
        open();
        super.startInternal();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.valves.ValveBase, org.apache.catalina.util.LifecycleBase
    public synchronized void stopInternal() throws LifecycleException {
        super.stopInternal();
        close(false);
    }
}
