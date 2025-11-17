package org.apache.juli;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.security.AccessController;
import java.sql.Timestamp;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.ErrorManager;
import java.util.logging.Filter;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.regex.Pattern;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/juli/FileHandler.class */
public class FileHandler extends Handler {
    public static final int DEFAULT_MAX_DAYS = -1;
    public static final int DEFAULT_BUFFER_SIZE = -1;
    private static final ExecutorService DELETE_FILES_SERVICE = Executors.newSingleThreadExecutor(new ThreadFactory("FileHandlerLogFilesCleaner-"));
    private volatile String date;
    private String directory;
    private String prefix;
    private String suffix;
    private Boolean rotatable;
    private Integer maxDays;
    private volatile PrintWriter writer;
    protected final ReadWriteLock writerLock;
    private Integer bufferSize;
    private Pattern pattern;

    public FileHandler() {
        this(null, null, null);
    }

    public FileHandler(String directory, String prefix, String suffix) {
        this(directory, prefix, suffix, null);
    }

    public FileHandler(String directory, String prefix, String suffix, Integer maxDays) {
        this(directory, prefix, suffix, maxDays, null, null);
    }

    public FileHandler(String directory, String prefix, String suffix, Integer maxDays, Boolean rotatable, Integer bufferSize) {
        this.date = null;
        this.writer = null;
        this.writerLock = new ReentrantReadWriteLock();
        this.directory = directory;
        this.prefix = prefix;
        this.suffix = suffix;
        this.maxDays = maxDays;
        this.rotatable = rotatable;
        this.bufferSize = bufferSize;
        configure();
        clean();
    }

    /* JADX WARN: Finally extract failed */
    @Override // java.util.logging.Handler
    public void publish(LogRecord record) {
        String tsDate;
        if (!isLoggable(record)) {
            return;
        }
        if (this.rotatable.booleanValue()) {
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            tsDate = ts.toString().substring(0, 10);
        } else {
            tsDate = "";
        }
        this.writerLock.readLock().lock();
        try {
            if (!tsDate.equals(this.date)) {
                this.writerLock.readLock().unlock();
                this.writerLock.writeLock().lock();
                try {
                    if (!tsDate.equals(this.date)) {
                        closeWriter();
                        this.date = tsDate;
                        openWriter();
                        clean();
                    }
                    this.writerLock.readLock().lock();
                    this.writerLock.writeLock().unlock();
                } catch (Throwable th) {
                    this.writerLock.readLock().lock();
                    this.writerLock.writeLock().unlock();
                    throw th;
                }
            }
            try {
                String result = getFormatter().format(record);
                try {
                    if (this.writer != null) {
                        this.writer.write(result);
                        if (this.bufferSize.intValue() < 0) {
                            this.writer.flush();
                        }
                    } else {
                        reportError("FileHandler is closed or not yet initialized, unable to log [" + result + "]", null, 1);
                    }
                } catch (Exception e) {
                    reportError(null, e, 1);
                }
                this.writerLock.readLock().unlock();
            } catch (Exception e2) {
                reportError(null, e2, 5);
                this.writerLock.readLock().unlock();
            }
        } catch (Throwable th2) {
            this.writerLock.readLock().unlock();
            throw th2;
        }
    }

    @Override // java.util.logging.Handler
    public void close() {
        closeWriter();
    }

    protected void closeWriter() {
        this.writerLock.writeLock().lock();
        try {
            if (this.writer == null) {
                return;
            }
            this.writer.write(getFormatter().getTail(this));
            this.writer.flush();
            this.writer.close();
            this.writer = null;
            this.date = null;
        } catch (Exception e) {
            reportError(null, e, 3);
        } finally {
            this.writerLock.writeLock().unlock();
        }
    }

    @Override // java.util.logging.Handler
    public void flush() {
        this.writerLock.readLock().lock();
        try {
            if (this.writer == null) {
                return;
            }
            this.writer.flush();
        } catch (Exception e) {
            reportError(null, e, 2);
        } finally {
            this.writerLock.readLock().unlock();
        }
    }

    private void configure() {
        String className = getClass().getName();
        ClassLoader cl = ClassLoaderLogManager.getClassLoader();
        if (this.rotatable == null) {
            this.rotatable = Boolean.valueOf(getProperty(className + ".rotatable", "true"));
        }
        if (this.directory == null) {
            this.directory = getProperty(className + ".directory", "logs");
        }
        if (this.prefix == null) {
            this.prefix = getProperty(className + ".prefix", "juli.");
        }
        if (this.suffix == null) {
            this.suffix = getProperty(className + ".suffix", ".log");
        }
        boolean shouldCheckForRedundantSeparator = (this.rotatable.booleanValue() || this.prefix.isEmpty() || this.suffix.isEmpty()) ? false : true;
        if (shouldCheckForRedundantSeparator && this.prefix.charAt(this.prefix.length() - 1) == this.suffix.charAt(0)) {
            this.suffix = this.suffix.substring(1);
        }
        this.pattern = Pattern.compile("^(" + Pattern.quote(this.prefix) + ")\\d{4}-\\d{1,2}-\\d{1,2}(" + Pattern.quote(this.suffix) + ")$");
        if (this.maxDays == null) {
            String sMaxDays = getProperty(className + ".maxDays", String.valueOf(-1));
            try {
                this.maxDays = Integer.valueOf(sMaxDays);
            } catch (NumberFormatException e) {
                this.maxDays = -1;
            }
        }
        if (this.bufferSize == null) {
            String sBufferSize = getProperty(className + ".bufferSize", String.valueOf(-1));
            try {
                this.bufferSize = Integer.valueOf(sBufferSize);
            } catch (NumberFormatException e2) {
                this.bufferSize = -1;
            }
        }
        String encoding = getProperty(className + ".encoding", null);
        if (encoding != null && encoding.length() > 0) {
            try {
                setEncoding(encoding);
            } catch (UnsupportedEncodingException e3) {
            }
        }
        setLevel(Level.parse(getProperty(className + ".level", Level.ALL)));
        String filterName = getProperty(className + ".filter", null);
        if (filterName != null) {
            try {
                setFilter((Filter) cl.loadClass(filterName).getConstructor(new Class[0]).newInstance(new Object[0]));
            } catch (Exception e4) {
            }
        }
        String formatterName = getProperty(className + ".formatter", null);
        if (formatterName != null) {
            try {
                setFormatter((Formatter) cl.loadClass(formatterName).getConstructor(new Class[0]).newInstance(new Object[0]));
            } catch (Exception e5) {
                setFormatter(new OneLineFormatter());
            }
        } else {
            setFormatter(new OneLineFormatter());
        }
        setErrorManager(new ErrorManager());
    }

    private String getProperty(String name, String defaultValue) {
        String value;
        String value2 = LogManager.getLogManager().getProperty(name);
        if (value2 == null) {
            value = defaultValue;
        } else {
            value = value2.trim();
        }
        return value;
    }

    public void open() {
        openWriter();
    }

    protected void openWriter() {
        File dir = new File(this.directory);
        if (!dir.mkdirs() && !dir.isDirectory()) {
            reportError("Unable to create [" + dir + "]", null, 4);
            this.writer = null;
            return;
        }
        this.writerLock.writeLock().lock();
        FileOutputStream fos = null;
        OutputStream os = null;
        try {
            try {
                File pathname = new File(dir.getAbsoluteFile(), this.prefix + (this.rotatable.booleanValue() ? this.date : "") + this.suffix);
                File parent = pathname.getParentFile();
                if (!parent.mkdirs() && !parent.isDirectory()) {
                    reportError("Unable to create [" + parent + "]", null, 4);
                    this.writer = null;
                    this.writerLock.writeLock().unlock();
                } else {
                    String encoding = getEncoding();
                    FileOutputStream fos2 = new FileOutputStream(pathname, true);
                    OutputStream os2 = this.bufferSize.intValue() > 0 ? new BufferedOutputStream(fos2, this.bufferSize.intValue()) : fos2;
                    this.writer = new PrintWriter((Writer) (encoding != null ? new OutputStreamWriter(os2, encoding) : new OutputStreamWriter(os2)), false);
                    this.writer.write(getFormatter().getHead(this));
                    this.writerLock.writeLock().unlock();
                }
            } catch (Exception e) {
                reportError(null, e, 4);
                this.writer = null;
                if (0 != 0) {
                    try {
                        fos.close();
                    } catch (IOException e2) {
                    }
                }
                if (0 != 0) {
                    try {
                        os.close();
                    } catch (IOException e3) {
                    }
                }
                this.writerLock.writeLock().unlock();
            }
        } catch (Throwable th) {
            this.writerLock.writeLock().unlock();
            throw th;
        }
    }

    private void clean() {
        if (this.maxDays.intValue() <= 0 || Files.notExists(getDirectoryAsPath(), new LinkOption[0])) {
            return;
        }
        DELETE_FILES_SERVICE.submit(() -> {
            try {
                DirectoryStream<Path> files = streamFilesForDelete();
                try {
                    for (Path file : files) {
                        Files.delete(file);
                    }
                    if (files != null) {
                        files.close();
                    }
                } finally {
                }
            } catch (IOException e) {
                reportError("Unable to delete log files older than [" + this.maxDays + "] days", null, 0);
            }
        });
    }

    private DirectoryStream<Path> streamFilesForDelete() throws IOException {
        LocalDate maxDaysOffset = LocalDate.now().minus(this.maxDays.intValue(), (TemporalUnit) ChronoUnit.DAYS);
        return Files.newDirectoryStream(getDirectoryAsPath(), (DirectoryStream.Filter<? super Path>) path -> {
            boolean result = false;
            String date = obtainDateFromPath(path);
            if (date != null) {
                try {
                    LocalDate dateFromFile = LocalDate.from(DateTimeFormatter.ISO_LOCAL_DATE.parse(date));
                    result = dateFromFile.isBefore(maxDaysOffset);
                } catch (DateTimeException e) {
                }
            }
            return result;
        });
    }

    private Path getDirectoryAsPath() {
        return Path.of(this.directory, new String[0]);
    }

    private String obtainDateFromPath(Path path) {
        Path fileName = path.getFileName();
        if (fileName == null) {
            return null;
        }
        String date = fileName.toString();
        if (this.pattern.matcher(date).matches()) {
            String date2 = date.substring(this.prefix.length());
            return date2.substring(0, date2.length() - this.suffix.length());
        }
        return null;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/juli/FileHandler$ThreadFactory.class */
    protected static final class ThreadFactory implements java.util.concurrent.ThreadFactory {
        private final String namePrefix;
        private final boolean isSecurityEnabled;
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);

        public ThreadFactory(String namePrefix) {
            this.namePrefix = namePrefix;
            SecurityManager s = System.getSecurityManager();
            if (s == null) {
                this.isSecurityEnabled = false;
                this.group = Thread.currentThread().getThreadGroup();
            } else {
                this.isSecurityEnabled = true;
                this.group = s.getThreadGroup();
            }
        }

        @Override // java.util.concurrent.ThreadFactory
        public Thread newThread(Runnable r) {
            Thread t = new Thread(this.group, r, this.namePrefix + this.threadNumber.getAndIncrement());
            if (this.isSecurityEnabled) {
                AccessController.doPrivileged(() -> {
                    t.setContextClassLoader(ThreadFactory.class.getClassLoader());
                    return null;
                });
            } else {
                t.setContextClassLoader(ThreadFactory.class.getClassLoader());
            }
            t.setDaemon(true);
            return t;
        }
    }
}
