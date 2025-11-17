package com.alibaba.com.caucho.hessian.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.tomcat.util.bcel.Const;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/HessianDebugOutputStream.class */
public class HessianDebugOutputStream extends OutputStream {
    private OutputStream _os;
    private HessianDebugState _state;

    public HessianDebugOutputStream(OutputStream os, PrintWriter dbg) {
        this._os = os;
        this._state = new HessianDebugState(dbg);
    }

    public HessianDebugOutputStream(OutputStream os, Logger log, Level level) {
        this(os, new PrintWriter(new LogWriter(log, level)));
    }

    public void startTop2() {
        this._state.startTop2();
    }

    @Override // java.io.OutputStream
    public void write(int ch2) throws IOException {
        int ch3 = ch2 & Const.MAX_ARRAY_DIMENSIONS;
        this._os.write(ch3);
        this._state.next(ch3);
    }

    @Override // java.io.OutputStream, java.io.Flushable
    public void flush() throws IOException {
        this._os.flush();
    }

    @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        OutputStream os = this._os;
        this._os = null;
        if (os != null) {
            os.close();
        }
        this._state.println();
    }

    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/HessianDebugOutputStream$LogWriter.class */
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
