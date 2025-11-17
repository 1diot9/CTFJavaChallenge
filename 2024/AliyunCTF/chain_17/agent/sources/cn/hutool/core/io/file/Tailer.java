package cn.hutool.core.io.file;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.LineHandler;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.CharsetUtil;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Stack;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/io/file/Tailer.class */
public class Tailer implements Serializable {
    private static final long serialVersionUID = 1;
    public static final LineHandler CONSOLE_HANDLER = new ConsoleLineHandler();
    private final Charset charset;
    private final LineHandler lineHandler;
    private final int initReadLine;
    private final long period;
    private final RandomAccessFile randomAccessFile;
    private final ScheduledExecutorService executorService;

    public Tailer(File file, LineHandler lineHandler) {
        this(file, lineHandler, 0);
    }

    public Tailer(File file, LineHandler lineHandler, int initReadLine) {
        this(file, CharsetUtil.CHARSET_UTF_8, lineHandler, initReadLine, DateUnit.SECOND.getMillis());
    }

    public Tailer(File file, Charset charset, LineHandler lineHandler) {
        this(file, charset, lineHandler, 0, DateUnit.SECOND.getMillis());
    }

    public Tailer(File file, Charset charset, LineHandler lineHandler, int initReadLine, long period) {
        checkFile(file);
        this.charset = charset;
        this.lineHandler = lineHandler;
        this.period = period;
        this.initReadLine = initReadLine;
        this.randomAccessFile = FileUtil.createRandomAccessFile(file, FileMode.r);
        this.executorService = Executors.newSingleThreadScheduledExecutor();
    }

    public void start() {
        start(false);
    }

    public void start(boolean async) {
        try {
            readTail();
            LineReadWatcher lineReadWatcher = new LineReadWatcher(this.randomAccessFile, this.charset, this.lineHandler);
            ScheduledFuture<?> scheduledFuture = this.executorService.scheduleAtFixedRate(lineReadWatcher, 0L, this.period, TimeUnit.MILLISECONDS);
            if (false == async) {
                try {
                    scheduledFuture.get();
                } catch (InterruptedException e) {
                } catch (ExecutionException e2) {
                    throw new UtilException(e2);
                }
            }
        } catch (IOException e3) {
            throw new IORuntimeException(e3);
        }
    }

    public void stop() {
        try {
            this.executorService.shutdown();
        } finally {
            IoUtil.close((Closeable) this.randomAccessFile);
        }
    }

    private void readTail() throws IOException {
        long len = this.randomAccessFile.length();
        if (this.initReadLine > 0) {
            Stack<String> stack = new Stack<>();
            long start = this.randomAccessFile.getFilePointer();
            long nextEnd = len - serialVersionUID < 0 ? 0L : len - serialVersionUID;
            this.randomAccessFile.seek(nextEnd);
            int currentLine = 0;
            while (true) {
                if (nextEnd <= start || currentLine > this.initReadLine) {
                    break;
                }
                int c = this.randomAccessFile.read();
                if (c == 10 || c == 13) {
                    String line = FileUtil.readLine(this.randomAccessFile, this.charset);
                    if (null != line) {
                        stack.push(line);
                    }
                    currentLine++;
                    nextEnd -= serialVersionUID;
                }
                nextEnd -= serialVersionUID;
                this.randomAccessFile.seek(nextEnd);
                if (nextEnd == 0) {
                    String line2 = FileUtil.readLine(this.randomAccessFile, this.charset);
                    if (null != line2) {
                        stack.push(line2);
                    }
                }
            }
            while (false == stack.isEmpty()) {
                this.lineHandler.handle(stack.pop());
            }
        }
        try {
            this.randomAccessFile.seek(len);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    private static void checkFile(File file) {
        if (false == file.exists()) {
            throw new UtilException("File [{}] not exist !", file.getAbsolutePath());
        }
        if (false == file.isFile()) {
            throw new UtilException("Path [{}] is not a file !", file.getAbsolutePath());
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/io/file/Tailer$ConsoleLineHandler.class */
    public static class ConsoleLineHandler implements LineHandler {
        @Override // cn.hutool.core.io.LineHandler
        public void handle(String line) {
            Console.log(line);
        }
    }
}
