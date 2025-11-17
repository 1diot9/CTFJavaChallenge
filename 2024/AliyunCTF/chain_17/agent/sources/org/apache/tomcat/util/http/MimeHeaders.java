package org.apache.tomcat.util.http;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/MimeHeaders.class */
public class MimeHeaders {
    public static final int DEFAULT_HEADER_SIZE = 8;
    private static final StringManager sm = StringManager.getManager("org.apache.tomcat.util.http");
    private int count;
    private MimeHeaderField[] headers = new MimeHeaderField[8];
    private int limit = -1;

    public void setLimit(int limit) {
        this.limit = limit;
        if (limit > 0 && this.headers.length > limit && this.count < limit) {
            MimeHeaderField[] tmp = new MimeHeaderField[limit];
            System.arraycopy(this.headers, 0, tmp, 0, this.count);
            this.headers = tmp;
        }
    }

    public void recycle() {
        for (int i = 0; i < this.count; i++) {
            this.headers[i].recycle();
        }
        this.count = 0;
    }

    @Deprecated
    public void clear() {
        recycle();
    }

    public String toString() {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.println("=== MimeHeaders ===");
        Enumeration<String> e = names();
        while (e.hasMoreElements()) {
            String n = e.nextElement();
            Enumeration<String> ev = values(n);
            while (ev.hasMoreElements()) {
                pw.print(n);
                pw.print(" = ");
                pw.println(ev.nextElement());
            }
        }
        return sw.toString();
    }

    public void duplicate(MimeHeaders source) throws IOException {
        for (int i = 0; i < source.size(); i++) {
            MimeHeaderField mhf = createHeader();
            mhf.getName().duplicate(source.getName(i));
            mhf.getValue().duplicate(source.getValue(i));
        }
    }

    public int size() {
        return this.count;
    }

    public MessageBytes getName(int n) {
        if (n < 0 || n >= this.count) {
            return null;
        }
        return this.headers[n].getName();
    }

    public MessageBytes getValue(int n) {
        if (n < 0 || n >= this.count) {
            return null;
        }
        return this.headers[n].getValue();
    }

    public int findHeader(String name, int starting) {
        for (int i = starting; i < this.count; i++) {
            if (this.headers[i].getName().equalsIgnoreCase(name)) {
                return i;
            }
        }
        return -1;
    }

    public Enumeration<String> names() {
        return new NamesEnumerator(this);
    }

    public Enumeration<String> values(String name) {
        return new ValuesEnumerator(this, name);
    }

    private MimeHeaderField createHeader() {
        if (this.limit > -1 && this.count >= this.limit) {
            throw new IllegalStateException(sm.getString("headers.maxCountFail", Integer.valueOf(this.limit)));
        }
        int len = this.headers.length;
        if (this.count >= len) {
            int newLength = this.count * 2;
            if (this.limit > 0 && newLength > this.limit) {
                newLength = this.limit;
            }
            MimeHeaderField[] tmp = new MimeHeaderField[newLength];
            System.arraycopy(this.headers, 0, tmp, 0, len);
            this.headers = tmp;
        }
        MimeHeaderField mimeHeaderField = this.headers[this.count];
        MimeHeaderField mh = mimeHeaderField;
        if (mimeHeaderField == null) {
            MimeHeaderField[] mimeHeaderFieldArr = this.headers;
            int i = this.count;
            MimeHeaderField mimeHeaderField2 = new MimeHeaderField();
            mh = mimeHeaderField2;
            mimeHeaderFieldArr[i] = mimeHeaderField2;
        }
        this.count++;
        return mh;
    }

    public MessageBytes addValue(String name) {
        MimeHeaderField mh = createHeader();
        mh.getName().setString(name);
        return mh.getValue();
    }

    public MessageBytes addValue(byte[] b, int startN, int len) {
        MimeHeaderField mhf = createHeader();
        mhf.getName().setBytes(b, startN, len);
        return mhf.getValue();
    }

    public MessageBytes setValue(String name) {
        for (int i = 0; i < this.count; i++) {
            if (this.headers[i].getName().equalsIgnoreCase(name)) {
                int j = i + 1;
                while (j < this.count) {
                    if (this.headers[j].getName().equalsIgnoreCase(name)) {
                        int i2 = j;
                        j--;
                        removeHeader(i2);
                    }
                    j++;
                }
                return this.headers[i].getValue();
            }
        }
        MimeHeaderField mh = createHeader();
        mh.getName().setString(name);
        return mh.getValue();
    }

    public MessageBytes getValue(String name) {
        for (int i = 0; i < this.count; i++) {
            if (this.headers[i].getName().equalsIgnoreCase(name)) {
                return this.headers[i].getValue();
            }
        }
        return null;
    }

    public MessageBytes getUniqueValue(String name) {
        MessageBytes result = null;
        for (int i = 0; i < this.count; i++) {
            if (this.headers[i].getName().equalsIgnoreCase(name)) {
                if (result == null) {
                    result = this.headers[i].getValue();
                } else {
                    throw new IllegalArgumentException();
                }
            }
        }
        return result;
    }

    public String getHeader(String name) {
        MessageBytes mh = getValue(name);
        if (mh != null) {
            return mh.toString();
        }
        return null;
    }

    public void removeHeader(String name) {
        int i = 0;
        while (i < this.count) {
            if (this.headers[i].getName().equalsIgnoreCase(name)) {
                int i2 = i;
                i--;
                removeHeader(i2);
            }
            i++;
        }
    }

    public void removeHeader(int idx) {
        MimeHeaderField mh = this.headers[idx];
        mh.recycle();
        System.arraycopy(this.headers, idx + 1, this.headers, idx, (this.count - idx) - 1);
        this.headers[this.count - 1] = mh;
        this.count--;
    }
}
