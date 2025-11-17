package org.apache.tomcat.util.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/json/JavaCharStream.class */
public class JavaCharStream {
    public static final boolean staticFlag = false;
    public int bufpos;
    int bufsize;
    int available;
    int tokenBegin;
    protected int[] bufline;
    protected int[] bufcolumn;
    protected int column;
    protected int line;
    protected boolean prevCharIsCR;
    protected boolean prevCharIsLF;
    protected Reader inputStream;
    protected char[] nextCharBuf;
    protected char[] buffer;
    protected int maxNextCharInd;
    protected int nextCharInd;
    protected int inBuf;
    protected int tabSize;
    protected boolean trackLineColumn;

    static final int hexval(char c) throws IOException {
        switch (c) {
            case '0':
                return 0;
            case '1':
                return 1;
            case '2':
                return 2;
            case '3':
                return 3;
            case '4':
                return 4;
            case '5':
                return 5;
            case '6':
                return 6;
            case '7':
                return 7;
            case '8':
                return 8;
            case '9':
                return 9;
            case ':':
            case ';':
            case '<':
            case '=':
            case '>':
            case '?':
            case '@':
            case 'G':
            case 'H':
            case 'I':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'S':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case '[':
            case '\\':
            case ']':
            case '^':
            case '_':
            case '`':
            default:
                throw new IOException();
            case 'A':
            case 'a':
                return 10;
            case 'B':
            case 'b':
                return 11;
            case 'C':
            case 'c':
                return 12;
            case 'D':
            case 'd':
                return 13;
            case 'E':
            case 'e':
                return 14;
            case 'F':
            case 'f':
                return 15;
        }
    }

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
                this.bufpos += this.bufsize - this.tokenBegin;
            } else {
                System.arraycopy(this.buffer, this.tokenBegin, newbuffer, 0, this.bufsize - this.tokenBegin);
                this.buffer = newbuffer;
                System.arraycopy(this.bufline, this.tokenBegin, newbufline, 0, this.bufsize - this.tokenBegin);
                this.bufline = newbufline;
                System.arraycopy(this.bufcolumn, this.tokenBegin, newbufcolumn, 0, this.bufsize - this.tokenBegin);
                this.bufcolumn = newbufcolumn;
                this.bufpos -= this.tokenBegin;
            }
            int i = this.bufsize + 2048;
            this.bufsize = i;
            this.available = i;
            this.tokenBegin = 0;
        } catch (Throwable t) {
            throw new Error(t.getMessage());
        }
    }

    protected void FillBuff() throws IOException {
        if (this.maxNextCharInd == 4096) {
            this.nextCharInd = 0;
            this.maxNextCharInd = 0;
        }
        try {
            int i = this.inputStream.read(this.nextCharBuf, this.maxNextCharInd, 4096 - this.maxNextCharInd);
            if (i == -1) {
                this.inputStream.close();
                throw new IOException();
            }
            this.maxNextCharInd += i;
        } catch (IOException e) {
            if (this.bufpos != 0) {
                this.bufpos--;
                backup(0);
            } else {
                this.bufline[this.bufpos] = this.line;
                this.bufcolumn[this.bufpos] = this.column;
            }
            throw e;
        }
    }

    protected char ReadByte() throws IOException {
        int i = this.nextCharInd + 1;
        this.nextCharInd = i;
        if (i >= this.maxNextCharInd) {
            FillBuff();
        }
        return this.nextCharBuf[this.nextCharInd];
    }

    public char BeginToken() throws IOException {
        if (this.inBuf > 0) {
            this.inBuf--;
            int i = this.bufpos + 1;
            this.bufpos = i;
            if (i == this.bufsize) {
                this.bufpos = 0;
            }
            this.tokenBegin = this.bufpos;
            return this.buffer[this.bufpos];
        }
        this.tokenBegin = 0;
        this.bufpos = -1;
        return readChar();
    }

    protected void AdjustBuffSize() {
        if (this.available == this.bufsize) {
            if (this.tokenBegin > 2048) {
                this.bufpos = 0;
                this.available = this.tokenBegin;
                return;
            } else {
                ExpandBuff(false);
                return;
            }
        }
        if (this.available > this.tokenBegin) {
            this.available = this.bufsize;
        } else if (this.tokenBegin - this.available < 2048) {
            ExpandBuff(true);
        } else {
            this.available = this.tokenBegin;
        }
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
        char c;
        char c2;
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
        if (i2 == this.available) {
            AdjustBuffSize();
        }
        char[] cArr = this.buffer;
        int i3 = this.bufpos;
        char c3 = ReadByte();
        cArr[i3] = c3;
        if (c3 == '\\') {
            if (this.trackLineColumn) {
                UpdateLineColumn(c3);
            }
            int backSlashCnt = 1;
            while (true) {
                int i4 = this.bufpos + 1;
                this.bufpos = i4;
                if (i4 == this.available) {
                    AdjustBuffSize();
                }
                try {
                    char[] cArr2 = this.buffer;
                    int i5 = this.bufpos;
                    c = ReadByte();
                    cArr2[i5] = c;
                    if (c != '\\') {
                        break;
                    }
                    if (this.trackLineColumn) {
                        UpdateLineColumn(c);
                    }
                    backSlashCnt++;
                } catch (IOException e) {
                    if (backSlashCnt > 1) {
                        backup(backSlashCnt - 1);
                        return '\\';
                    }
                    return '\\';
                }
            }
            if (this.trackLineColumn) {
                UpdateLineColumn(c);
            }
            if (c == 'u' && (backSlashCnt & 1) == 1) {
                int i6 = this.bufpos - 1;
                this.bufpos = i6;
                if (i6 < 0) {
                    this.bufpos = this.bufsize - 1;
                }
                while (true) {
                    try {
                        c2 = ReadByte();
                        if (c2 != 'u') {
                            break;
                        }
                        this.column++;
                    } catch (IOException e2) {
                        throw new Error("Invalid escape character at line " + this.line + " column " + this.column + ".");
                    }
                }
                char[] cArr3 = this.buffer;
                int i7 = this.bufpos;
                char c4 = (char) ((hexval(c2) << 12) | (hexval(ReadByte()) << 8) | (hexval(ReadByte()) << 4) | hexval(ReadByte()));
                cArr3[i7] = c4;
                this.column += 4;
                if (backSlashCnt == 1) {
                    return c4;
                }
                backup(backSlashCnt - 1);
                return '\\';
            }
            backup(backSlashCnt);
            return '\\';
        }
        UpdateLineColumn(c3);
        return c3;
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

    public JavaCharStream(Reader dstream, int startline, int startcolumn, int buffersize) {
        this.bufpos = -1;
        this.column = 0;
        this.line = 1;
        this.prevCharIsCR = false;
        this.prevCharIsLF = false;
        this.maxNextCharInd = 0;
        this.nextCharInd = -1;
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
        this.nextCharBuf = new char[4096];
    }

    public JavaCharStream(Reader dstream, int startline, int startcolumn) {
        this(dstream, startline, startcolumn, 4096);
    }

    public JavaCharStream(Reader dstream) {
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
            this.nextCharBuf = new char[4096];
        }
        this.prevCharIsCR = false;
        this.prevCharIsLF = false;
        this.maxNextCharInd = 0;
        this.inBuf = 0;
        this.tokenBegin = 0;
        this.bufpos = -1;
        this.nextCharInd = -1;
    }

    public void ReInit(Reader dstream, int startline, int startcolumn) {
        ReInit(dstream, startline, startcolumn, 4096);
    }

    public void ReInit(Reader dstream) {
        ReInit(dstream, 1, 1, 4096);
    }

    public JavaCharStream(InputStream dstream, String encoding, int startline, int startcolumn, int buffersize) throws UnsupportedEncodingException {
        this(encoding == null ? new InputStreamReader(dstream) : new InputStreamReader(dstream, encoding), startline, startcolumn, buffersize);
    }

    public JavaCharStream(InputStream dstream, int startline, int startcolumn, int buffersize) {
        this(new InputStreamReader(dstream), startline, startcolumn, 4096);
    }

    public JavaCharStream(InputStream dstream, String encoding, int startline, int startcolumn) throws UnsupportedEncodingException {
        this(dstream, encoding, startline, startcolumn, 4096);
    }

    public JavaCharStream(InputStream dstream, int startline, int startcolumn) {
        this(dstream, startline, startcolumn, 4096);
    }

    public JavaCharStream(InputStream dstream, String encoding) throws UnsupportedEncodingException {
        this(dstream, encoding, 1, 1, 4096);
    }

    public JavaCharStream(InputStream dstream) {
        this(dstream, 1, 1, 4096);
    }

    public void ReInit(InputStream dstream, String encoding, int startline, int startcolumn, int buffersize) throws UnsupportedEncodingException {
        ReInit(encoding == null ? new InputStreamReader(dstream) : new InputStreamReader(dstream, encoding), startline, startcolumn, buffersize);
    }

    public void ReInit(InputStream dstream, int startline, int startcolumn, int buffersize) {
        ReInit(new InputStreamReader(dstream), startline, startcolumn, buffersize);
    }

    public void ReInit(InputStream dstream, String encoding, int startline, int startcolumn) throws UnsupportedEncodingException {
        ReInit(dstream, encoding, startline, startcolumn, 4096);
    }

    public void ReInit(InputStream dstream, int startline, int startcolumn) {
        ReInit(dstream, startline, startcolumn, 4096);
    }

    public void ReInit(InputStream dstream, String encoding) throws UnsupportedEncodingException {
        ReInit(dstream, encoding, 1, 1, 4096);
    }

    public void ReInit(InputStream dstream) {
        ReInit(dstream, 1, 1, 4096);
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
        this.nextCharBuf = null;
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
