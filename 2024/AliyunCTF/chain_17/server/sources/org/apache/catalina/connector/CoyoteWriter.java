package org.apache.catalina.connector;

import java.io.IOException;
import java.io.PrintWriter;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/connector/CoyoteWriter.class */
public class CoyoteWriter extends PrintWriter {
    private static final char[] LINE_SEP = System.lineSeparator().toCharArray();
    protected OutputBuffer ob;
    protected boolean error;

    public CoyoteWriter(OutputBuffer ob) {
        super(ob);
        this.error = false;
        this.ob = ob;
    }

    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void clear() {
        this.ob = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void recycle() {
        this.error = false;
    }

    @Override // java.io.PrintWriter, java.io.Writer, java.io.Flushable
    public void flush() {
        if (this.error) {
            return;
        }
        try {
            this.ob.flush();
        } catch (IOException e) {
            this.error = true;
        }
    }

    @Override // java.io.PrintWriter, java.io.Writer, java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        try {
            this.ob.close();
        } catch (IOException e) {
        }
        this.error = false;
    }

    @Override // java.io.PrintWriter
    public boolean checkError() {
        flush();
        return this.error;
    }

    @Override // java.io.PrintWriter, java.io.Writer
    public void write(int c) {
        if (this.error) {
            return;
        }
        try {
            this.ob.write(c);
        } catch (IOException e) {
            this.error = true;
        }
    }

    @Override // java.io.PrintWriter, java.io.Writer
    public void write(char[] buf, int off, int len) {
        if (this.error) {
            return;
        }
        try {
            this.ob.write(buf, off, len);
        } catch (IOException e) {
            this.error = true;
        }
    }

    @Override // java.io.PrintWriter, java.io.Writer
    public void write(char[] buf) {
        write(buf, 0, buf.length);
    }

    @Override // java.io.PrintWriter, java.io.Writer
    public void write(String s, int off, int len) {
        if (this.error) {
            return;
        }
        try {
            this.ob.write(s, off, len);
        } catch (IOException e) {
            this.error = true;
        }
    }

    @Override // java.io.PrintWriter, java.io.Writer
    public void write(String s) {
        write(s, 0, s.length());
    }

    @Override // java.io.PrintWriter
    public void print(boolean b) {
        if (b) {
            write("true");
        } else {
            write("false");
        }
    }

    @Override // java.io.PrintWriter
    public void print(char c) {
        write(c);
    }

    @Override // java.io.PrintWriter
    public void print(int i) {
        write(String.valueOf(i));
    }

    @Override // java.io.PrintWriter
    public void print(long l) {
        write(String.valueOf(l));
    }

    @Override // java.io.PrintWriter
    public void print(float f) {
        write(String.valueOf(f));
    }

    @Override // java.io.PrintWriter
    public void print(double d) {
        write(String.valueOf(d));
    }

    @Override // java.io.PrintWriter
    public void print(char[] s) {
        write(s);
    }

    @Override // java.io.PrintWriter
    public void print(String s) {
        if (s == null) {
            s = "null";
        }
        write(s);
    }

    @Override // java.io.PrintWriter
    public void print(Object obj) {
        write(String.valueOf(obj));
    }

    @Override // java.io.PrintWriter
    public void println() {
        write(LINE_SEP);
    }

    @Override // java.io.PrintWriter
    public void println(boolean b) {
        print(b);
        println();
    }

    @Override // java.io.PrintWriter
    public void println(char c) {
        print(c);
        println();
    }

    @Override // java.io.PrintWriter
    public void println(int i) {
        print(i);
        println();
    }

    @Override // java.io.PrintWriter
    public void println(long l) {
        print(l);
        println();
    }

    @Override // java.io.PrintWriter
    public void println(float f) {
        print(f);
        println();
    }

    @Override // java.io.PrintWriter
    public void println(double d) {
        print(d);
        println();
    }

    @Override // java.io.PrintWriter
    public void println(char[] c) {
        print(c);
        println();
    }

    @Override // java.io.PrintWriter
    public void println(String s) {
        print(s);
        println();
    }

    @Override // java.io.PrintWriter
    public void println(Object o) {
        print(o);
        println();
    }
}
