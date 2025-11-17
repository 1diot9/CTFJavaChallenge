package com.alibaba.com.caucho.hessian.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/HessianDebugInputStream.class */
public class HessianDebugInputStream extends InputStream {
    private InputStream _is;
    private HessianDebugState _state;

    public HessianDebugInputStream(InputStream is, PrintWriter dbg) {
        this._is = is;
        this._state = new HessianDebugState(dbg == null ? new PrintWriter(System.out) : dbg);
    }

    public HessianDebugInputStream(InputStream is, Logger log, Level level) {
        this(is, new PrintWriter(new LogWriter(log, level)));
    }

    public void startTop2() {
        this._state.startTop2();
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        InputStream is = this._is;
        if (is == null) {
            return -1;
        }
        int ch2 = is.read();
        this._state.next(ch2);
        return ch2;
    }

    @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        InputStream is = this._is;
        this._is = null;
        if (is != null) {
            is.close();
        }
        this._state.println();
    }

    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/HessianDebugInputStream$LogWriter.class */
    static class LogWriter extends Writer {
        private Logger _log;
        private Level _level;
        private StringBuilder _sb = new StringBuilder();

        LogWriter(Logger log, Level level) {
            this._log = log;
            this._level = level;
        }

        public void write(char ch2) {
            if (ch2 == '\n' && this._sb.length() > 0) {
                this._log.log(this._level, this._sb.toString());
                this._sb.setLength(0);
            } else {
                this._sb.append(ch2);
            }
        }

        @Override // java.io.Writer
        public void write(char[] buffer, int offset, int length) {
            for (int i = 0; i < length; i++) {
                char ch2 = buffer[offset + i];
                if (ch2 == '\n' && this._sb.length() > 0) {
                    this._log.log(this._level, this._sb.toString());
                    this._sb.setLength(0);
                } else {
                    this._sb.append(ch2);
                }
            }
        }

        @Override // java.io.Writer, java.io.Flushable
        public void flush() {
        }

        @Override // java.io.Writer, java.io.Closeable, java.lang.AutoCloseable
        public void close() {
        }
    }
}
