package org.apache.tomcat.util.buf;

import java.io.Serializable;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/buf/AbstractChunk.class */
public abstract class AbstractChunk implements Cloneable, Serializable {
    private static final long serialVersionUID = 1;
    protected static final StringManager sm = StringManager.getManager((Class<?>) AbstractChunk.class);
    public static final int ARRAY_MAX_SIZE = 2147483639;
    protected boolean isSet;
    protected int start;
    protected int end;
    private int hashCode = 0;
    protected boolean hasHashCode = false;
    private int limit = -1;

    protected abstract int getBufferElement(int i);

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getLimit() {
        return this.limit;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getLimitInternal() {
        if (this.limit > 0) {
            return this.limit;
        }
        return ARRAY_MAX_SIZE;
    }

    public int getStart() {
        return this.start;
    }

    public int getEnd() {
        return this.end;
    }

    public void setEnd(int i) {
        this.end = i;
    }

    public int getOffset() {
        return this.start;
    }

    public void setOffset(int off) {
        if (this.end < off) {
            this.end = off;
        }
        this.start = off;
    }

    public int getLength() {
        return this.end - this.start;
    }

    public boolean isNull() {
        return this.end <= 0 && !this.isSet;
    }

    public int indexOf(String src, int srcOff, int srcLen, int myOff) {
        char first = src.charAt(srcOff);
        int srcEnd = srcOff + srcLen;
        for (int i = myOff + this.start; i <= this.end - srcLen; i++) {
            if (getBufferElement(i) == first) {
                int myPos = i + 1;
                int srcPos = srcOff + 1;
                while (srcPos < srcEnd) {
                    int i2 = myPos;
                    myPos++;
                    int i3 = srcPos;
                    srcPos++;
                    if (getBufferElement(i2) != src.charAt(i3)) {
                        break;
                    }
                }
                return i - this.start;
            }
        }
        return -1;
    }

    public void recycle() {
        this.hasHashCode = false;
        this.isSet = false;
        this.start = 0;
        this.end = 0;
    }

    public int hashCode() {
        if (this.hasHashCode) {
            return this.hashCode;
        }
        int code = hash();
        this.hashCode = code;
        this.hasHashCode = true;
        return code;
    }

    public int hash() {
        int code = 0;
        for (int i = this.start; i < this.end; i++) {
            code = (code * 37) + getBufferElement(i);
        }
        return code;
    }
}
