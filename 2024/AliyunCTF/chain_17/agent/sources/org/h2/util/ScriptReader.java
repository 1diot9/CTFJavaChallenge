package org.h2.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import org.h2.message.DbException;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/ScriptReader.class */
public class ScriptReader implements Closeable {
    private final Reader reader;
    private int bufferPos;
    private int bufferEnd;
    private boolean endOfFile;
    private boolean insideRemark;
    private boolean blockRemark;
    private boolean skipRemarks;
    private int remarkStart;
    private int bufferStart = -1;
    private char[] buffer = new char[8192];

    public ScriptReader(Reader reader) {
        this.reader = reader;
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        try {
            this.reader.close();
        } catch (IOException e) {
            throw DbException.convertIOException(e, null);
        }
    }

    public String readStatement() {
        if (this.endOfFile) {
            return null;
        }
        try {
            return readStatementLoop();
        } catch (IOException e) {
            throw DbException.convertIOException(e, null);
        }
    }

    private String readStatementLoop() throws IOException {
        int read;
        int read2;
        int read3;
        int read4;
        int read5;
        this.bufferStart = this.bufferPos;
        int read6 = read();
        while (true) {
            if (read6 < 0) {
                this.endOfFile = true;
                if (this.bufferPos - 1 == this.bufferStart) {
                    return null;
                }
            } else if (read6 != 59) {
                switch (read6) {
                    case 34:
                        do {
                            read4 = read();
                            if (read4 < 0) {
                            }
                            read6 = read();
                            break;
                        } while (read4 != 34);
                        read6 = read();
                    case 35:
                    case 37:
                    case 38:
                    case 40:
                    case 41:
                    case 42:
                    case 43:
                    case 44:
                    case 46:
                    default:
                        read6 = read();
                        break;
                    case 36:
                        read6 = read();
                        if (read6 == 36 && (this.bufferPos - this.bufferStart < 3 || this.buffer[this.bufferPos - 3] <= ' ')) {
                            while (true) {
                                int read7 = read();
                                if (read7 >= 0 && (read7 != 36 || ((read = read()) >= 0 && read != 36))) {
                                }
                            }
                            read6 = read();
                            break;
                        }
                        break;
                    case 39:
                        do {
                            read5 = read();
                            if (read5 < 0) {
                            }
                            read6 = read();
                            break;
                        } while (read5 != 39);
                        read6 = read();
                    case 45:
                        read6 = read();
                        if (read6 != 45) {
                            break;
                        } else {
                            startRemark(false);
                            do {
                                read2 = read();
                                if (read2 < 0) {
                                    clearRemark();
                                } else {
                                    if (read2 != 13) {
                                    }
                                    endRemark();
                                }
                                read6 = read();
                                break;
                            } while (read2 != 10);
                            endRemark();
                            read6 = read();
                        }
                    case 47:
                        read6 = read();
                        if (read6 != 42) {
                            if (read6 != 47) {
                                break;
                            } else {
                                startRemark(false);
                                do {
                                    read3 = read();
                                    if (read3 < 0) {
                                        clearRemark();
                                    } else {
                                        if (read3 != 13) {
                                        }
                                        endRemark();
                                    }
                                    read6 = read();
                                    break;
                                } while (read3 != 10);
                                endRemark();
                                read6 = read();
                            }
                        } else {
                            startRemark(true);
                            int i = 1;
                            while (true) {
                                int read8 = read();
                                if (read8 >= 0) {
                                    if (read8 == 42) {
                                        int read9 = read();
                                        if (read9 < 0) {
                                            clearRemark();
                                        } else if (read9 == 47) {
                                            i--;
                                            if (i == 0) {
                                                endRemark();
                                            }
                                        } else {
                                            continue;
                                        }
                                    } else if (read8 != 47) {
                                        continue;
                                    } else {
                                        int read10 = read();
                                        if (read10 < 0) {
                                            clearRemark();
                                        } else if (read10 == 42) {
                                            i++;
                                        }
                                    }
                                }
                            }
                            read6 = read();
                            break;
                        }
                }
            }
        }
        return new String(this.buffer, this.bufferStart, (this.bufferPos - 1) - this.bufferStart);
    }

    private void startRemark(boolean z) {
        this.blockRemark = z;
        this.remarkStart = this.bufferPos - 2;
        this.insideRemark = true;
    }

    private void endRemark() {
        clearRemark();
        this.insideRemark = false;
    }

    private void clearRemark() {
        if (this.skipRemarks) {
            Arrays.fill(this.buffer, this.remarkStart, this.bufferPos, ' ');
        }
    }

    private int read() throws IOException {
        if (this.bufferPos >= this.bufferEnd) {
            return readBuffer();
        }
        char[] cArr = this.buffer;
        int i = this.bufferPos;
        this.bufferPos = i + 1;
        return cArr[i];
    }

    private int readBuffer() throws IOException {
        if (this.endOfFile) {
            return -1;
        }
        int i = this.bufferPos - this.bufferStart;
        if (i > 0) {
            char[] cArr = this.buffer;
            if (i + 4096 > cArr.length) {
                if (cArr.length >= 1073741823) {
                    throw new IOException("Error in parsing script, statement size exceeds 1G, first 80 characters of statement looks like: " + new String(this.buffer, this.bufferStart, 80));
                }
                this.buffer = new char[cArr.length * 2];
            }
            System.arraycopy(cArr, this.bufferStart, this.buffer, 0, i);
        }
        this.remarkStart -= this.bufferStart;
        this.bufferStart = 0;
        this.bufferPos = i;
        int read = this.reader.read(this.buffer, i, 4096);
        if (read == -1) {
            this.bufferEnd = -1024;
            this.endOfFile = true;
            this.bufferPos++;
            return -1;
        }
        this.bufferEnd = i + read;
        char[] cArr2 = this.buffer;
        int i2 = this.bufferPos;
        this.bufferPos = i2 + 1;
        return cArr2[i2];
    }

    public boolean isInsideRemark() {
        return this.insideRemark;
    }

    public boolean isBlockRemark() {
        return this.blockRemark;
    }

    public void setSkipRemarks(boolean z) {
        this.skipRemarks = z;
    }
}
