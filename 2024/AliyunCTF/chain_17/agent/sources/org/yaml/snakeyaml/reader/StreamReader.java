package org.yaml.snakeyaml.reader;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.scanner.Constant;

/* loaded from: agent.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/reader/StreamReader.class */
public class StreamReader {
    private String name;
    private final Reader stream;
    private int[] dataWindow;
    private int dataLength;
    private int pointer;
    private boolean eof;
    private int index;
    private int documentIndex;
    private int line;
    private int column;
    private final char[] buffer;
    private static final int BUFFER_SIZE = 1025;

    public StreamReader(String stream) {
        this(new StringReader(stream));
        this.name = "'string'";
    }

    public StreamReader(Reader reader) {
        this.pointer = 0;
        this.index = 0;
        this.documentIndex = 0;
        this.line = 0;
        this.column = 0;
        if (reader == null) {
            throw new NullPointerException("Reader must be provided.");
        }
        this.name = "'reader'";
        this.dataWindow = new int[0];
        this.dataLength = 0;
        this.stream = reader;
        this.eof = false;
        this.buffer = new char[BUFFER_SIZE];
    }

    public static boolean isPrintable(String data) {
        int length = data.length();
        int i = 0;
        while (true) {
            int offset = i;
            if (offset < length) {
                int codePoint = data.codePointAt(offset);
                if (!isPrintable(codePoint)) {
                    return false;
                }
                i = offset + Character.charCount(codePoint);
            } else {
                return true;
            }
        }
    }

    public static boolean isPrintable(int c) {
        return (c >= 32 && c <= 126) || c == 9 || c == 10 || c == 13 || c == 133 || (c >= 160 && c <= 55295) || ((c >= 57344 && c <= 65533) || (c >= 65536 && c <= 1114111));
    }

    public Mark getMark() {
        return new Mark(this.name, this.index, this.line, this.column, this.dataWindow, this.pointer);
    }

    public void forward() {
        forward(1);
    }

    public void forward(int length) {
        for (int i = 0; i < length && ensureEnoughData(); i++) {
            int[] iArr = this.dataWindow;
            int i2 = this.pointer;
            this.pointer = i2 + 1;
            int c = iArr[i2];
            moveIndices(1);
            if (Constant.LINEBR.has(c) || (c == 13 && ensureEnoughData() && this.dataWindow[this.pointer] != 10)) {
                this.line++;
                this.column = 0;
            } else if (c != 65279) {
                this.column++;
            }
        }
    }

    public int peek() {
        if (ensureEnoughData()) {
            return this.dataWindow[this.pointer];
        }
        return 0;
    }

    public int peek(int index) {
        if (ensureEnoughData(index)) {
            return this.dataWindow[this.pointer + index];
        }
        return 0;
    }

    public String prefix(int length) {
        if (length == 0) {
            return "";
        }
        if (ensureEnoughData(length)) {
            return new String(this.dataWindow, this.pointer, length);
        }
        return new String(this.dataWindow, this.pointer, Math.min(length, this.dataLength - this.pointer));
    }

    public String prefixForward(int length) {
        String prefix = prefix(length);
        this.pointer += length;
        moveIndices(length);
        this.column += length;
        return prefix;
    }

    private boolean ensureEnoughData() {
        return ensureEnoughData(0);
    }

    private boolean ensureEnoughData(int size) {
        if (!this.eof && this.pointer + size >= this.dataLength) {
            update();
        }
        return this.pointer + size < this.dataLength;
    }

    private void update() {
        try {
            int read = this.stream.read(this.buffer, 0, 1024);
            if (read > 0) {
                int cpIndex = this.dataLength - this.pointer;
                this.dataWindow = Arrays.copyOfRange(this.dataWindow, this.pointer, this.dataLength + read);
                if (Character.isHighSurrogate(this.buffer[read - 1])) {
                    if (this.stream.read(this.buffer, read, 1) == -1) {
                        this.eof = true;
                    } else {
                        read++;
                    }
                }
                int nonPrintable = 32;
                int i = 0;
                while (i < read) {
                    int codePoint = Character.codePointAt(this.buffer, i);
                    this.dataWindow[cpIndex] = codePoint;
                    if (isPrintable(codePoint)) {
                        i += Character.charCount(codePoint);
                    } else {
                        nonPrintable = codePoint;
                        i = read;
                    }
                    cpIndex++;
                }
                this.dataLength = cpIndex;
                this.pointer = 0;
                if (nonPrintable != 32) {
                    throw new ReaderException(this.name, cpIndex - 1, nonPrintable, "special characters are not allowed");
                }
            } else {
                this.eof = true;
            }
        } catch (IOException ioe) {
            throw new YAMLException(ioe);
        }
    }

    public int getColumn() {
        return this.column;
    }

    private void moveIndices(int length) {
        this.index += length;
        this.documentIndex += length;
    }

    public int getDocumentIndex() {
        return this.documentIndex;
    }

    public void resetDocumentIndex() {
        this.documentIndex = 0;
    }

    public int getIndex() {
        return this.index;
    }

    public int getLine() {
        return this.line;
    }
}
