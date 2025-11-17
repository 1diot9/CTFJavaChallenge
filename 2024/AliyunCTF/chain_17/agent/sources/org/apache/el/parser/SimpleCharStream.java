package org.apache.el.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:org/apache/el/parser/SimpleCharStream.class */
public class SimpleCharStream {
    public static final boolean staticFlag = false;
    int bufsize;
    int available;
    int tokenBegin;
    public int bufpos;
    protected int[] bufline;
    protected int[] bufcolumn;
    protected int column;
    protected int line;
    protected boolean prevCharIsCR;
    protected boolean prevCharIsLF;
    protected Reader inputStream;
    protected char[] buffer;
    protected int maxNextCharInd;
    protected int inBuf;
    protected int tabSize;
    protected boolean trackLineColumn;

    public void setTabSize(int i) {
        this.tabSize = i;
    }

    public int getTabSize() {
        return this.tabSize;
    }

    protected void ExpandBuff(boolean wrapAround) {
        char[] newbuffer = new char[this.bufsize + 2048];
        int[] newbufline = new int[this.bufsize + 2048];
        int[] newbufcolumn = new int[this.bufsize + 2048];
        try {
            if (wrapAround) {
                System.arraycopy(this.buffer, this.tokenBegin, newbuffer, 0, this.bufsize - this.tokenBegin);
                System.arraycopy(this.buffer, 0, newbuffer, this.bufsize - this.tokenBegin, this.bufpos);
                this.buffer = newbuffer;
                System.arraycopy(this.bufline, this.tokenBegin, newbufline, 0, this.bufsize - this.tokenBegin);
                System.arraycopy(this.bufline, 0, newbufline, this.bufsize - this.tokenBegin, this.bufpos);
                this.bufline = newbufline;
                System.arraycopy(this.bufcolumn, this.tokenBegin, newbufcolumn, 0, this.bufsize - this.tokenBegin);
                System.arraycopy(this.bufcolumn, 0, newbufcolumn, this.bufsize - this.tokenBegin, this.bufpos);
                this.bufcolumn = newbufcolumn;
                int i = this.bufpos + (this.bufsize - this.tokenBegin);
                this.bufpos = i;
                this.maxNextCharInd = i;
            } else {
                System.arraycopy(this.buffer, this.tokenBegin, newbuffer, 0, this.bufsize - this.tokenBegin);
                this.buffer = newbuffer;
                System.arraycopy(this.bufline, this.tokenBegin, newbufline, 0, this.bufsize - this.tokenBegin);
                this.bufline = newbufline;
                System.arraycopy(this.bufcolumn, this.tokenBegin, newbufcolumn, 0, this.bufsize - this.tokenBegin);
                this.bufcolumn = newbufcolumn;
                int i2 = this.bufpos - this.tokenBegin;
                this.bufpos = i2;
                this.maxNextCharInd = i2;
            }
            this.bufsize += 2048;
            this.available = this.bufsize;
            this.tokenBegin = 0;
        } catch (Throwable t) {
            throw new Error(t.getMessage());
        }
    }

    protected void FillBuff() throws IOException {
        if (this.maxNextCharInd == this.available) {
            if (this.available == this.bufsize) {
                if (this.tokenBegin > 2048) {
                    this.maxNextCharInd = 0;
                    this.bufpos = 0;
                    this.available = this.tokenBegin;
                } else if (this.tokenBegin < 0) {
                    this.maxNextCharInd = 0;
                    this.bufpos = 0;
                } else {
                    ExpandBuff(false);
                }
            } else if (this.available > this.tokenBegin) {
                this.available = this.bufsize;
            } else if (this.tokenBegin - this.available < 2048) {
                ExpandBuff(true);
            } else {
                this.available = this.tokenBegin;
            }
        }
        try {
            int i = this.inputStream.read(this.buffer, this.maxNextCharInd, this.available - this.maxNextCharInd);
            if (i == -1) {
                this.inputStream.close();
                throw new IOException();
            }
            this.maxNextCharInd += i;
        } catch (IOException e) {
            this.bufpos--;
            backup(0);
            if (this.tokenBegin == -1) {
                this.tokenBegin = this.bufpos;
            }
            throw e;
        }
    }

    public char BeginToken() throws IOException {
        this.tokenBegin = -1;
        char c = readChar();
        this.tokenBegin = this.bufpos;
        return c;
    }

    protected void UpdateLineColumn(char c) {
        this.column++;
        if (this.prevCharIsLF) {
            this.prevCharIsLF = false;
            int i = this.line;
            this.column = 1;
            this.line = i + 1;
        } else if (this.prevCharIsCR) {
            this.prevCharIsCR = false;
            if (c == '\n') {
                this.prevCharIsLF = true;
            } else {
                int i2 = this.line;
                this.column = 1;
                this.line = i2 + 1;
            }
        }
        switch (c) {
            case '\t':
                this.column--;
                this.column += this.tabSize - (this.column % this.tabSize);
                break;
            case '\n':
                this.prevCharIsLF = true;
                break;
            case '\r':
                this.prevCharIsCR = true;
                break;
        }
        this.bufline[this.bufpos] = this.line;
        this.bufcolumn[this.bufpos] = this.column;
    }

    public char readChar() throws IOException {
        if (this.inBuf > 0) {
            this.inBuf--;
            int i = this.bufpos + 1;
            this.bufpos = i;
            if (i == this.bufsize) {
                this.bufpos = 0;
            }
            return this.buffer[this.bufpos];
        }
        int i2 = this.bufpos + 1;
        this.bufpos = i2;
        if (i2 >= this.maxNextCharInd) {
            FillBuff();
        }
        char c = this.buffer[this.bufpos];
        UpdateLineColumn(c);
        return c;
    }

    @Deprecated
    public int getColumn() {
        return this.bufcolumn[this.bufpos];
    }

    @Deprecated
    public int getLine() {
        return this.bufline[this.bufpos];
    }

    public int getEndColumn() {
        return this.bufcolumn[this.bufpos];
    }

    public int getEndLine() {
        return this.bufline[this.bufpos];
    }

    public int getBeginColumn() {
        return this.bufcolumn[this.tokenBegin];
    }

    public int getBeginLine() {
        return this.bufline[this.tokenBegin];
    }

    public void backup(int amount) {
        this.inBuf += amount;
        int i = this.bufpos - amount;
        this.bufpos = i;
        if (i < 0) {
            this.bufpos += this.bufsize;
        }
    }

    public SimpleCharStream(Reader dstream, int startline, int startcolumn, int buffersize) {
        this.bufpos = -1;
        this.column = 0;
        this.line = 1;
        this.prevCharIsCR = false;
        this.prevCharIsLF = false;
        this.maxNextCharInd = 0;
        this.inBuf = 0;
        this.tabSize = 1;
        this.trackLineColumn = true;
        this.inputStream = dstream;
        this.line = startline;
        this.column = startcolumn - 1;
        this.bufsize = buffersize;
        this.available = buffersize;
        this.buffer = new char[buffersize];
        this.bufline = new int[buffersize];
        this.bufcolumn = new int[buffersize];
    }

    public SimpleCharStream(Reader dstream, int startline, int startcolumn) {
        this(dstream, startline, startcolumn, 4096);
    }

    public SimpleCharStream(Reader dstream) {
        this(dstream, 1, 1, 4096);
    }

    public void ReInit(Reader dstream, int startline, int startcolumn, int buffersize) {
        this.inputStream = dstream;
        this.line = startline;
        this.column = startcolumn - 1;
        if (this.buffer == null || buffersize != this.buffer.length) {
            this.bufsize = buffersize;
            this.available = buffersize;
            this.buffer = new char[buffersize];
            this.bufline = new int[buffersize];
            this.bufcolumn = new int[buffersize];
        }
        this.prevCharIsCR = false;
        this.prevCharIsLF = false;
        this.maxNextCharInd = 0;
        this.inBuf = 0;
        this.tokenBegin = 0;
        this.bufpos = -1;
    }

    public void ReInit(Reader dstream, int startline, int startcolumn) {
        ReInit(dstream, startline, startcolumn, 4096);
    }

    public void ReInit(Reader dstream) {
        ReInit(dstream, 1, 1, 4096);
    }

    public SimpleCharStream(InputStream dstream, String encoding, int startline, int startcolumn, int buffersize) throws UnsupportedEncodingException {
        this(encoding == null ? new InputStreamReader(dstream) : new InputStreamReader(dstream, encoding), startline, startcolumn, buffersize);
    }

    public SimpleCharStream(InputStream dstream, int startline, int startcolumn, int buffersize) {
        this(new InputStreamReader(dstream), startline, startcolumn, buffersize);
    }

    public SimpleCharStream(InputStream dstream, String encoding, int startline, int startcolumn) throws UnsupportedEncodingException {
        this(dstream, encoding, startline, startcolumn, 4096);
    }

    public SimpleCharStream(InputStream dstream, int startline, int startcolumn) {
        this(dstream, startline, startcolumn, 4096);
    }

    public SimpleCharStream(InputStream dstream, String encoding) throws UnsupportedEncodingException {
        this(dstream, encoding, 1, 1, 4096);
    }

    public SimpleCharStream(InputStream dstream) {
        this(dstream, 1, 1, 4096);
    }

    public void ReInit(InputStream dstream, String encoding, int startline, int startcolumn, int buffersize) throws UnsupportedEncodingException {
        ReInit(encoding == null ? new InputStreamReader(dstream) : new InputStreamReader(dstream, encoding), startline, startcolumn, buffersize);
    }

    public void ReInit(InputStream dstream, int startline, int startcolumn, int buffersize) {
        ReInit(new InputStreamReader(dstream), startline, startcolumn, buffersize);
    }

    public void ReInit(InputStream dstream, String encoding) throws UnsupportedEncodingException {
        ReInit(dstream, encoding, 1, 1, 4096);
    }

    public void ReInit(InputStream dstream) {
        ReInit(dstream, 1, 1, 4096);
    }

    public void ReInit(InputStream dstream, String encoding, int startline, int startcolumn) throws UnsupportedEncodingException {
        ReInit(dstream, encoding, startline, startcolumn, 4096);
    }

    public void ReInit(InputStream dstream, int startline, int startcolumn) {
        ReInit(dstream, startline, startcolumn, 4096);
    }

    public String GetImage() {
        if (this.bufpos >= this.tokenBegin) {
            return new String(this.buffer, this.tokenBegin, (this.bufpos - this.tokenBegin) + 1);
        }
        return new String(this.buffer, this.tokenBegin, this.bufsize - this.tokenBegin) + new String(this.buffer, 0, this.bufpos + 1);
    }

    public char[] GetSuffix(int len) {
        char[] ret = new char[len];
        if (this.bufpos + 1 >= len) {
            System.arraycopy(this.buffer, (this.bufpos - len) + 1, ret, 0, len);
        } else {
            System.arraycopy(this.buffer, this.bufsize - ((len - this.bufpos) - 1), ret, 0, (len - this.bufpos) - 1);
            System.arraycopy(this.buffer, 0, ret, (len - this.bufpos) - 1, this.bufpos + 1);
        }
        return ret;
    }

    public void Done() {
        this.buffer = null;
        this.bufline = null;
        this.bufcolumn = null;
    }

    public void adjustBeginLineColumn(int newLine, int newCol) {
        int len;
        int start = this.tokenBegin;
        if (this.bufpos >= this.tokenBegin) {
            len = (this.bufpos - this.tokenBegin) + this.inBuf + 1;
        } else {
            len = (this.bufsize - this.tokenBegin) + this.bufpos + 1 + this.inBuf;
        }
        int i = 0;
        int j = 0;
        int columnDiff = 0;
        while (i < len) {
            int[] iArr = this.bufline;
            int i2 = start % this.bufsize;
            j = i2;
            int i3 = iArr[i2];
            int[] iArr2 = this.bufline;
            start++;
            int k = start % this.bufsize;
            if (i3 != iArr2[k]) {
                break;
            }
            this.bufline[j] = newLine;
            int nextColDiff = (columnDiff + this.bufcolumn[k]) - this.bufcolumn[j];
            this.bufcolumn[j] = newCol + columnDiff;
            columnDiff = nextColDiff;
            i++;
        }
        if (i < len) {
            int newLine2 = newLine + 1;
            this.bufline[j] = newLine;
            this.bufcolumn[j] = newCol + columnDiff;
            while (true) {
                int i4 = i;
                i++;
                if (i4 >= len) {
                    break;
                }
                int[] iArr3 = this.bufline;
                int i5 = start % this.bufsize;
                j = i5;
                start++;
                if (iArr3[i5] != this.bufline[start % this.bufsize]) {
                    int i6 = newLine2;
                    newLine2++;
                    this.bufline[j] = i6;
                } else {
                    this.bufline[j] = newLine2;
                }
            }
        }
        this.line = this.bufline[j];
        this.column = this.bufcolumn[j];
    }

    boolean getTrackLineColumn() {
        return this.trackLineColumn;
    }

    void setTrackLineColumn(boolean tlc) {
        this.trackLineColumn = tlc;
    }
}
