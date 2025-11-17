package cn.hutool.core.io.file;

import cn.hutool.core.thread.lock.LockUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ObjectUtil;
import java.io.File;
import java.io.PrintWriter;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.function.Supplier;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/io/file/FileAppender.class */
public class FileAppender implements Serializable {
    private static final long serialVersionUID = 1;
    private final FileWriter writer;
    private final int capacity;
    private final boolean isNewLineMode;
    private final List<String> list;
    private final Lock lock;

    public FileAppender(File destFile, int capacity, boolean isNewLineMode) {
        this(destFile, CharsetUtil.CHARSET_UTF_8, capacity, isNewLineMode);
    }

    public FileAppender(File destFile, Charset charset, int capacity, boolean isNewLineMode) {
        this(destFile, charset, capacity, isNewLineMode, null);
    }

    public FileAppender(File destFile, Charset charset, int capacity, boolean isNewLineMode, Lock lock) {
        this.capacity = capacity;
        this.list = new ArrayList(capacity);
        this.isNewLineMode = isNewLineMode;
        this.writer = FileWriter.create(destFile, charset);
        this.lock = (Lock) ObjectUtil.defaultIfNull(lock, (Supplier<? extends Lock>) LockUtil::getNoLock);
    }

    public FileAppender append(String line) {
        if (this.list.size() >= this.capacity) {
            flush();
        }
        this.lock.lock();
        try {
            this.list.add(line);
            return this;
        } finally {
            this.lock.unlock();
        }
    }

    public FileAppender flush() {
        this.lock.lock();
        try {
            PrintWriter pw = this.writer.getPrintWriter(true);
            Throwable th = null;
            try {
                for (String str : this.list) {
                    pw.print(str);
                    if (this.isNewLineMode) {
                        pw.println();
                    }
                }
                if (pw != null) {
                    if (0 != 0) {
                        try {
                            pw.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        pw.close();
                    }
                }
                this.list.clear();
                this.lock.unlock();
                return this;
            } finally {
            }
        } catch (Throwable th3) {
            this.lock.unlock();
            throw th3;
        }
    }
}
