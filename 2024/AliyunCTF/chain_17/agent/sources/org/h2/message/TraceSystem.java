package org.h2.message;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReferenceArray;
import org.h2.api.ErrorCode;
import org.h2.engine.Constants;
import org.h2.jdbc.JdbcException;
import org.h2.store.fs.FileUtils;
import org.h2.util.IOUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/message/TraceSystem.class */
public class TraceSystem implements TraceWriter {
    public static final int PARENT = -1;
    public static final int OFF = 0;
    public static final int ERROR = 1;
    public static final int INFO = 2;
    public static final int DEBUG = 3;
    public static final int ADAPTER = 4;
    public static final int DEFAULT_TRACE_LEVEL_SYSTEM_OUT = 0;
    public static final int DEFAULT_TRACE_LEVEL_FILE = 1;
    private static final int DEFAULT_MAX_FILE_SIZE = 67108864;
    private static final int CHECK_SIZE_EACH_WRITES = 4096;
    private static DateTimeFormatter DATE_TIME_FORMATTER;
    private int levelMax;
    private String fileName;
    private Writer fileWriter;
    private PrintWriter printWriter;
    private boolean closed;
    private boolean writingErrorLogged;
    private int levelSystemOut = 0;
    private int levelFile = 1;
    private int maxFileSize = 67108864;
    private final AtomicReferenceArray<Trace> traces = new AtomicReferenceArray<>(Trace.MODULE_NAMES.length);
    private int checkSize = -1;
    private TraceWriter writer = this;
    private PrintStream sysOut = System.out;

    public TraceSystem(String str) {
        this.fileName = str;
        updateLevel();
    }

    private void updateLevel() {
        this.levelMax = Math.max(this.levelSystemOut, this.levelFile);
    }

    public void setSysOut(PrintStream printStream) {
        this.sysOut = printStream;
    }

    public Trace getTrace(int i) {
        Trace trace = this.traces.get(i);
        if (trace == null) {
            trace = new Trace(this.writer, i);
            if (!this.traces.compareAndSet(i, null, trace)) {
                trace = this.traces.get(i);
            }
        }
        return trace;
    }

    public Trace getTrace(String str) {
        return new Trace(this.writer, str);
    }

    @Override // org.h2.message.TraceWriter
    public boolean isEnabled(int i) {
        if (this.levelMax == 4) {
            return this.writer.isEnabled(i);
        }
        return i <= this.levelMax;
    }

    public void setFileName(String str) {
        this.fileName = str;
    }

    public void setMaxFileSize(int i) {
        this.maxFileSize = i;
    }

    public void setLevelSystemOut(int i) {
        if (i < -1 || i > 3) {
            throw DbException.getInvalidValueException("TRACE_LEVEL_SYSTEM_OUT", Integer.valueOf(i));
        }
        this.levelSystemOut = i;
        updateLevel();
    }

    public void setLevelFile(int i) {
        if (i == 4) {
            try {
                this.writer = (TraceWriter) Class.forName("org.h2.message.TraceWriterAdapter").getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
                String str = this.fileName;
                if (str != null) {
                    if (str.endsWith(Constants.SUFFIX_TRACE_FILE)) {
                        str = str.substring(0, str.length() - Constants.SUFFIX_TRACE_FILE.length());
                    }
                    int max = Math.max(str.lastIndexOf(47), str.lastIndexOf(92));
                    if (max >= 0) {
                        str = str.substring(max + 1);
                    }
                    this.writer.setName(str);
                }
            } catch (Throwable th) {
                write(1, 2, "org.h2.message.TraceWriterAdapter", DbException.get(ErrorCode.CLASS_NOT_FOUND_1, th, "org.h2.message.TraceWriterAdapter"));
                return;
            }
        } else if (i < -1 || i > 3) {
            throw DbException.getInvalidValueException("TRACE_LEVEL_FILE", Integer.valueOf(i));
        }
        this.levelFile = i;
        updateLevel();
    }

    public int getLevelFile() {
        return this.levelFile;
    }

    private static String format(String str, String str2) {
        DateTimeFormatter dateTimeFormatter = DATE_TIME_FORMATTER;
        if (dateTimeFormatter == null) {
            dateTimeFormatter = initTimeFormatter();
        }
        return dateTimeFormatter.format(OffsetDateTime.now()) + ' ' + str + ": " + str2;
    }

    private static DateTimeFormatter initTimeFormatter() {
        DateTimeFormatter formatter = new DateTimeFormatterBuilder().append(DateTimeFormatter.ISO_LOCAL_DATE).appendLiteral(' ').appendValue(ChronoField.HOUR_OF_DAY, 2).appendLiteral(':').appendValue(ChronoField.MINUTE_OF_HOUR, 2).appendLiteral(':').appendValue(ChronoField.SECOND_OF_MINUTE, 2).appendFraction(ChronoField.NANO_OF_SECOND, 6, 6, true).appendOffsetId().toFormatter(Locale.ROOT);
        DATE_TIME_FORMATTER = formatter;
        return formatter;
    }

    @Override // org.h2.message.TraceWriter
    public void write(int i, int i2, String str, Throwable th) {
        write(i, Trace.MODULE_NAMES[i2], str, th);
    }

    @Override // org.h2.message.TraceWriter
    public void write(int i, String str, String str2, Throwable th) {
        boolean z = i <= this.levelSystemOut || i > this.levelMax;
        boolean z2 = this.fileName != null && i <= this.levelFile;
        if (z || z2) {
            String format = format(str, str2);
            if (z) {
                this.sysOut.println(format);
                if (th != null && this.levelSystemOut == 3) {
                    th.printStackTrace(this.sysOut);
                }
            }
            if (z2) {
                writeFile(format, th);
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    private synchronized void writeFile(String str, Throwable th) {
        try {
            this.checkSize = (this.checkSize + 1) % 4096;
            if (this.checkSize == 0) {
                closeWriter();
                if (this.maxFileSize > 0 && FileUtils.size(this.fileName) > this.maxFileSize) {
                    String str2 = this.fileName + ".old";
                    FileUtils.delete(str2);
                    FileUtils.move(this.fileName, str2);
                }
            }
            if (!openWriter()) {
                return;
            }
            this.printWriter.println(str);
            if (th != 0) {
                if (this.levelFile == 1 && (th instanceof JdbcException)) {
                    if (ErrorCode.isCommon(((JdbcException) th).getErrorCode())) {
                        this.printWriter.println(th);
                    } else {
                        th.printStackTrace(this.printWriter);
                    }
                } else {
                    th.printStackTrace(this.printWriter);
                }
            }
            this.printWriter.flush();
            if (this.closed) {
                closeWriter();
            }
        } catch (Exception e) {
            logWritingError(e);
        }
    }

    private void logWritingError(Exception exc) {
        if (this.writingErrorLogged) {
            return;
        }
        this.writingErrorLogged = true;
        DbException dbException = DbException.get(ErrorCode.TRACE_FILE_ERROR_2, exc, this.fileName, exc.toString());
        this.fileName = null;
        this.sysOut.println(dbException);
        dbException.printStackTrace();
    }

    private boolean openWriter() {
        if (this.printWriter == null) {
            try {
                FileUtils.createDirectories(FileUtils.getParent(this.fileName));
                if (FileUtils.exists(this.fileName) && !FileUtils.canWrite(this.fileName)) {
                    return false;
                }
                this.fileWriter = IOUtils.getBufferedWriter(FileUtils.newOutputStream(this.fileName, true));
                this.printWriter = new PrintWriter(this.fileWriter, true);
                return true;
            } catch (Exception e) {
                logWritingError(e);
                return false;
            }
        }
        return true;
    }

    private synchronized void closeWriter() {
        if (this.printWriter != null) {
            this.printWriter.flush();
            this.printWriter.close();
            this.printWriter = null;
        }
        if (this.fileWriter != null) {
            try {
                this.fileWriter.close();
            } catch (IOException e) {
            }
            this.fileWriter = null;
        }
    }

    public void close() {
        closeWriter();
        this.closed = true;
    }

    @Override // org.h2.message.TraceWriter
    public void setName(String str) {
    }
}
