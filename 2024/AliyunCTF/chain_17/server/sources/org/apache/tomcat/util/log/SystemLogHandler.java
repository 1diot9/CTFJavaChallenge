package org.apache.tomcat.util.log;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/log/SystemLogHandler.class */
public class SystemLogHandler extends PrintStream {
    private final PrintStream out;
    private static final ThreadLocal<Deque<CaptureLog>> logs = new ThreadLocal<>();
    private static final Queue<CaptureLog> reuse = new ConcurrentLinkedQueue();

    public SystemLogHandler(PrintStream wrapped) {
        super(wrapped);
        this.out = wrapped;
    }

    public static void startCapture() {
        CaptureLog log;
        if (!reuse.isEmpty()) {
            try {
                log = reuse.remove();
            } catch (NoSuchElementException e) {
                log = new CaptureLog();
            }
        } else {
            log = new CaptureLog();
        }
        Deque<CaptureLog> stack = logs.get();
        if (stack == null) {
            stack = new ArrayDeque();
            logs.set(stack);
        }
        stack.addFirst(log);
    }

    public static String stopCapture() {
        CaptureLog log;
        Queue<CaptureLog> stack = logs.get();
        if (stack == null || stack.isEmpty() || (log = stack.remove()) == null) {
            return null;
        }
        String capture = log.getCapture();
        log.reset();
        reuse.add(log);
        return capture;
    }

    protected PrintStream findStream() {
        CaptureLog log;
        PrintStream ps;
        Queue<CaptureLog> stack = logs.get();
        if (stack != null && !stack.isEmpty() && (log = stack.peek()) != null && (ps = log.getStream()) != null) {
            return ps;
        }
        return this.out;
    }

    @Override // java.io.PrintStream, java.io.FilterOutputStream, java.io.OutputStream, java.io.Flushable
    public void flush() {
        findStream().flush();
    }

    @Override // java.io.PrintStream, java.io.FilterOutputStream, java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        findStream().close();
    }

    @Override // java.io.PrintStream
    public boolean checkError() {
        return findStream().checkError();
    }

    @Override // java.io.PrintStream
    protected void setError() {
    }

    @Override // java.io.PrintStream, java.io.FilterOutputStream, java.io.OutputStream
    public void write(int b) {
        findStream().write(b);
    }

    @Override // java.io.PrintStream, java.io.FilterOutputStream, java.io.OutputStream
    public void write(byte[] b) throws IOException {
        findStream().write(b);
    }

    @Override // java.io.PrintStream, java.io.FilterOutputStream, java.io.OutputStream
    public void write(byte[] buf, int off, int len) {
        findStream().write(buf, off, len);
    }

    @Override // java.io.PrintStream
    public void print(boolean b) {
        findStream().print(b);
    }

    @Override // java.io.PrintStream
    public void print(char c) {
        findStream().print(c);
    }

    @Override // java.io.PrintStream
    public void print(int i) {
        findStream().print(i);
    }

    @Override // java.io.PrintStream
    public void print(long l) {
        findStream().print(l);
    }

    @Override // java.io.PrintStream
    public void print(float f) {
        findStream().print(f);
    }

    @Override // java.io.PrintStream
    public void print(double d) {
        findStream().print(d);
    }

    @Override // java.io.PrintStream
    public void print(char[] s) {
        findStream().print(s);
    }

    @Override // java.io.PrintStream
    public void print(String s) {
        findStream().print(s);
    }

    @Override // java.io.PrintStream
    public void print(Object obj) {
        findStream().print(obj);
    }

    @Override // java.io.PrintStream
    public void println() {
        findStream().println();
    }

    @Override // java.io.PrintStream
    public void println(boolean x) {
        findStream().println(x);
    }

    @Override // java.io.PrintStream
    public void println(char x) {
        findStream().println(x);
    }

    @Override // java.io.PrintStream
    public void println(int x) {
        findStream().println(x);
    }

    @Override // java.io.PrintStream
    public void println(long x) {
        findStream().println(x);
    }

    @Override // java.io.PrintStream
    public void println(float x) {
        findStream().println(x);
    }

    @Override // java.io.PrintStream
    public void println(double x) {
        findStream().println(x);
    }

    @Override // java.io.PrintStream
    public void println(char[] x) {
        findStream().println(x);
    }

    @Override // java.io.PrintStream
    public void println(String x) {
        findStream().println(x);
    }

    @Override // java.io.PrintStream
    public void println(Object x) {
        findStream().println(x);
    }
}
