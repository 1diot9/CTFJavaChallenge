package org.h2.tools;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import org.apache.tomcat.websocket.BasicAuthenticator;
import org.h2.api.ErrorCode;
import org.h2.message.DbException;
import org.h2.mvstore.DataUtils;
import org.h2.store.fs.FileUtils;
import org.h2.util.IOUtils;
import org.h2.util.JdbcUtils;
import org.h2.util.StringUtils;
import org.h2.util.Utils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/tools/Csv.class */
public class Csv implements SimpleRowSource {
    private String[] columnNames;
    private String characterSet;
    private boolean caseSensitiveColumnNames;
    private boolean preserveWhitespace;
    private char lineComment;
    private String fileName;
    private BufferedReader input;
    private char[] inputBuffer;
    private int inputBufferPos;
    private int inputBufferEnd;
    private Writer output;
    private boolean endOfLine;
    private boolean endOfFile;
    private char escapeCharacter = '\"';
    private char fieldDelimiter = '\"';
    private char fieldSeparatorRead = ',';
    private String fieldSeparatorWrite = ",";
    private boolean writeColumnHeader = true;
    private String lineSeparator = System.lineSeparator();
    private String nullString = "";
    private boolean quotedNulls = false;
    private int inputBufferStart = -1;

    private int writeResultSet(ResultSet resultSet) throws SQLException {
        try {
            try {
                int i = 0;
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();
                String[] strArr = new String[columnCount];
                for (int i2 = 0; i2 < columnCount; i2++) {
                    strArr[i2] = metaData.getColumnLabel(i2 + 1);
                }
                if (this.writeColumnHeader) {
                    writeRow(strArr);
                }
                while (resultSet.next()) {
                    for (int i3 = 0; i3 < columnCount; i3++) {
                        strArr[i3] = resultSet.getString(i3 + 1);
                    }
                    writeRow(strArr);
                    i++;
                }
                this.output.close();
                int i4 = i;
                close();
                JdbcUtils.closeSilently(resultSet);
                return i4;
            } catch (IOException e) {
                throw DbException.convertIOException(e, null);
            }
        } catch (Throwable th) {
            close();
            JdbcUtils.closeSilently(resultSet);
            throw th;
        }
    }

    public int write(Writer writer, ResultSet resultSet) throws SQLException {
        this.output = writer;
        return writeResultSet(resultSet);
    }

    public int write(String str, ResultSet resultSet, String str2) throws SQLException {
        init(str, str2);
        try {
            initWrite();
            return writeResultSet(resultSet);
        } catch (IOException e) {
            throw convertException("IOException writing " + str, e);
        }
    }

    public int write(Connection connection, String str, String str2, String str3) throws SQLException {
        Statement createStatement = connection.createStatement();
        int write = write(str, createStatement.executeQuery(str2), str3);
        createStatement.close();
        return write;
    }

    public ResultSet read(String str, String[] strArr, String str2) throws SQLException {
        init(str, str2);
        try {
            return readResultSet(strArr);
        } catch (IOException e) {
            throw convertException("IOException reading " + str, e);
        }
    }

    public ResultSet read(Reader reader, String[] strArr) throws IOException {
        init(null, null);
        this.input = reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader, 4096);
        return readResultSet(strArr);
    }

    private ResultSet readResultSet(String[] strArr) throws IOException {
        this.columnNames = strArr;
        initRead();
        SimpleResultSet simpleResultSet = new SimpleResultSet(this);
        makeColumnNamesUnique();
        for (String str : this.columnNames) {
            simpleResultSet.addColumn(str, 12, Integer.MAX_VALUE, 0);
        }
        return simpleResultSet;
    }

    private void makeColumnNamesUnique() {
        for (int i = 0; i < this.columnNames.length; i++) {
            StringBuilder sb = new StringBuilder();
            String str = this.columnNames[i];
            if (str == null || str.isEmpty()) {
                sb.append('C').append(i + 1);
            } else {
                sb.append(str);
            }
            int i2 = 0;
            while (i2 < i) {
                if (sb.toString().equals(this.columnNames[i2])) {
                    sb.append('1');
                    i2 = -1;
                }
                i2++;
            }
            this.columnNames[i] = sb.toString();
        }
    }

    private void init(String str, String str2) {
        this.fileName = str;
        this.characterSet = str2;
    }

    private void initWrite() throws IOException {
        if (this.output == null) {
            try {
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(FileUtils.newOutputStream(this.fileName, false), 4096);
                this.output = new BufferedWriter(this.characterSet != null ? new OutputStreamWriter(bufferedOutputStream, this.characterSet) : new OutputStreamWriter(bufferedOutputStream));
            } catch (Exception e) {
                close();
                throw DataUtils.convertToIOException(e);
            }
        }
    }

    private void writeRow(String[] strArr) throws IOException {
        for (int i = 0; i < strArr.length; i++) {
            if (i > 0 && this.fieldSeparatorWrite != null) {
                this.output.write(this.fieldSeparatorWrite);
            }
            String str = strArr[i];
            if (str != null) {
                if (this.escapeCharacter != 0) {
                    if (this.fieldDelimiter != 0) {
                        this.output.write(this.fieldDelimiter);
                    }
                    this.output.write(escape(str));
                    if (this.fieldDelimiter != 0) {
                        this.output.write(this.fieldDelimiter);
                    }
                } else {
                    this.output.write(str);
                }
            } else if (this.nullString != null) {
                if (this.quotedNulls && this.fieldDelimiter != 0) {
                    this.output.write(this.fieldDelimiter);
                    this.output.write(this.nullString);
                    this.output.write(this.fieldDelimiter);
                } else {
                    this.output.write(this.nullString);
                }
            }
        }
        this.output.write(this.lineSeparator);
    }

    private String escape(String str) {
        if (str.indexOf(this.fieldDelimiter) < 0 && (this.escapeCharacter == this.fieldDelimiter || str.indexOf(this.escapeCharacter) < 0)) {
            return str;
        }
        int length = str.length();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char charAt = str.charAt(i);
            if (charAt == this.fieldDelimiter || charAt == this.escapeCharacter) {
                sb.append(this.escapeCharacter);
            }
            sb.append(charAt);
        }
        return sb.toString();
    }

    private void initRead() throws IOException {
        if (this.input == null) {
            try {
                this.input = FileUtils.newBufferedReader(this.fileName, this.characterSet != null ? Charset.forName(this.characterSet) : StandardCharsets.UTF_8);
            } catch (IOException e) {
                close();
                throw e;
            }
        }
        this.input.mark(1);
        if (this.input.read() != 65279) {
            this.input.reset();
        }
        this.inputBuffer = new char[8192];
        if (this.columnNames == null) {
            readHeader();
        }
    }

    private void readHeader() throws IOException {
        ArrayList arrayList = new ArrayList();
        while (true) {
            String readValue = readValue();
            if (readValue == null) {
                if (this.endOfLine) {
                    if (this.endOfFile || !arrayList.isEmpty()) {
                        break;
                    }
                } else {
                    arrayList.add("COLUMN" + arrayList.size());
                }
            } else {
                if (readValue.isEmpty()) {
                    readValue = "COLUMN" + arrayList.size();
                } else if (!this.caseSensitiveColumnNames && isSimpleColumnName(readValue)) {
                    readValue = StringUtils.toUpperEnglish(readValue);
                }
                arrayList.add(readValue);
                if (this.endOfLine) {
                    break;
                }
            }
        }
        this.columnNames = (String[]) arrayList.toArray(new String[0]);
    }

    private static boolean isSimpleColumnName(String str) {
        int length = str.length();
        for (int i = 0; i < length; i++) {
            char charAt = str.charAt(i);
            if (i == 0) {
                if (charAt != '_' && !Character.isLetter(charAt)) {
                    return false;
                }
            } else if (charAt != '_' && !Character.isLetterOrDigit(charAt)) {
                return false;
            }
        }
        return str.length() != 0;
    }

    private void pushBack() {
        this.inputBufferPos--;
    }

    private int readChar() throws IOException {
        if (this.inputBufferPos >= this.inputBufferEnd) {
            return readBuffer();
        }
        char[] cArr = this.inputBuffer;
        int i = this.inputBufferPos;
        this.inputBufferPos = i + 1;
        return cArr[i];
    }

    private int readBuffer() throws IOException {
        int i;
        if (this.endOfFile) {
            return -1;
        }
        if (this.inputBufferStart >= 0) {
            i = this.inputBufferPos - this.inputBufferStart;
            if (i > 0) {
                char[] cArr = this.inputBuffer;
                if (i + 4096 > cArr.length) {
                    this.inputBuffer = new char[cArr.length * 2];
                }
                System.arraycopy(cArr, this.inputBufferStart, this.inputBuffer, 0, i);
            }
            this.inputBufferStart = 0;
        } else {
            i = 0;
        }
        this.inputBufferPos = i;
        int read = this.input.read(this.inputBuffer, i, 4096);
        if (read == -1) {
            this.inputBufferEnd = -1024;
            this.endOfFile = true;
            this.inputBufferPos++;
            return -1;
        }
        this.inputBufferEnd = i + read;
        char[] cArr2 = this.inputBuffer;
        int i2 = this.inputBufferPos;
        this.inputBufferPos = i2 + 1;
        return cArr2[i2];
    }

    private String readValue() throws IOException {
        int readChar;
        int readChar2;
        int i;
        int readChar3;
        int readChar4;
        this.endOfLine = false;
        this.inputBufferStart = this.inputBufferPos;
        do {
            readChar = readChar();
            if (readChar == this.fieldDelimiter) {
                boolean z = false;
                this.inputBufferStart = this.inputBufferPos;
                while (true) {
                    readChar2 = readChar();
                    if (readChar2 == this.fieldDelimiter) {
                        readChar2 = readChar();
                        if (readChar2 != this.fieldDelimiter) {
                            i = 2;
                            break;
                        }
                        z = true;
                    } else if (readChar2 == this.escapeCharacter) {
                        readChar2 = readChar();
                        if (readChar2 < 0) {
                            i = 1;
                            break;
                        }
                        z = true;
                    } else if (readChar2 < 0) {
                        i = 1;
                        break;
                    }
                }
                String str = new String(this.inputBuffer, this.inputBufferStart, (this.inputBufferPos - this.inputBufferStart) - i);
                if (z) {
                    str = unEscape(str);
                }
                this.inputBufferStart = -1;
                while (true) {
                    if (readChar2 == this.fieldSeparatorRead) {
                        break;
                    }
                    if (readChar2 != 10 && readChar2 >= 0 && readChar2 != 13) {
                        if (readChar2 != 32 && readChar2 != 9) {
                            pushBack();
                            break;
                        }
                        readChar2 = readChar();
                    } else {
                        break;
                    }
                }
                return str;
            }
            if (readChar == 10 || readChar < 0 || readChar == 13) {
                this.endOfLine = true;
                return null;
            }
            if (readChar == this.fieldSeparatorRead) {
                return null;
            }
        } while (readChar <= 32);
        if (this.lineComment != 0 && readChar == this.lineComment) {
            this.inputBufferStart = -1;
            do {
                readChar4 = readChar();
                if (readChar4 == 10 || readChar4 < 0) {
                    break;
                }
            } while (readChar4 != 13);
            this.endOfLine = true;
            return null;
        }
        do {
            readChar3 = readChar();
            if (readChar3 == this.fieldSeparatorRead) {
                break;
            }
            if (readChar3 == 10 || readChar3 < 0) {
                break;
            }
        } while (readChar3 != 13);
        this.endOfLine = true;
        String str2 = new String(this.inputBuffer, this.inputBufferStart, (this.inputBufferPos - this.inputBufferStart) - 1);
        if (!this.preserveWhitespace) {
            str2 = str2.trim();
        }
        this.inputBufferStart = -1;
        return readNull(str2);
    }

    private String readNull(String str) {
        if (str.equals(this.nullString)) {
            return null;
        }
        return str;
    }

    private String unEscape(String str) {
        StringBuilder sb = new StringBuilder(str.length());
        int i = 0;
        char[] cArr = null;
        while (true) {
            int indexOf = str.indexOf(this.escapeCharacter, i);
            if (indexOf < 0) {
                indexOf = str.indexOf(this.fieldDelimiter, i);
                if (indexOf < 0) {
                    break;
                }
            }
            if (cArr == null) {
                cArr = str.toCharArray();
            }
            sb.append(cArr, i, indexOf - i);
            if (indexOf == str.length() - 1) {
                i = str.length();
                break;
            }
            sb.append(cArr[indexOf + 1]);
            i = indexOf + 2;
        }
        sb.append((CharSequence) str, i, str.length());
        return sb.toString();
    }

    /* JADX WARN: Code restructure failed: missing block: B:34:0x008f, code lost:            return r0;     */
    @Override // org.h2.tools.SimpleRowSource
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public java.lang.Object[] readRow() throws java.sql.SQLException {
        /*
            r5 = this;
            r0 = r5
            java.io.BufferedReader r0 = r0.input
            if (r0 != 0) goto L9
            r0 = 0
            return r0
        L9:
            r0 = r5
            java.lang.String[] r0 = r0.columnNames
            int r0 = r0.length
            java.lang.String[] r0 = new java.lang.String[r0]
            r6 = r0
            r0 = 0
            r7 = r0
        L14:
            r0 = r5
            java.lang.String r0 = r0.readValue()     // Catch: java.io.IOException -> L72
            r8 = r0
            r0 = r8
            if (r0 != 0) goto L31
            r0 = r5
            boolean r0 = r0.endOfLine     // Catch: java.io.IOException -> L72
            if (r0 == 0) goto L31
            r0 = r7
            if (r0 != 0) goto L6f
            r0 = r5
            boolean r0 = r0.endOfFile     // Catch: java.io.IOException -> L72
            if (r0 == 0) goto L14
            r0 = 0
            return r0
        L31:
            r0 = r7
            r1 = r6
            int r1 = r1.length     // Catch: java.io.IOException -> L72
            if (r0 >= r1) goto L62
            r0 = r5
            boolean r0 = r0.quotedNulls     // Catch: java.io.IOException -> L72
            if (r0 == 0) goto L5b
            r0 = r6
            r1 = r7
            int r7 = r7 + 1
            r2 = r8
            if (r2 == 0) goto L56
            r2 = r8
            r3 = r5
            java.lang.String r3 = r3.nullString     // Catch: java.io.IOException -> L72
            boolean r2 = r2.equals(r3)     // Catch: java.io.IOException -> L72
            if (r2 != 0) goto L56
            r2 = r8
            goto L57
        L56:
            r2 = 0
        L57:
            r0[r1] = r2     // Catch: java.io.IOException -> L72
            goto L62
        L5b:
            r0 = r6
            r1 = r7
            int r7 = r7 + 1
            r2 = r8
            r0[r1] = r2     // Catch: java.io.IOException -> L72
        L62:
            r0 = r5
            boolean r0 = r0.endOfLine     // Catch: java.io.IOException -> L72
            if (r0 == 0) goto L6c
            goto L6f
        L6c:
            goto L14
        L6f:
            goto L8e
        L72:
            r7 = move-exception
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r1 = r0
            r1.<init>()
            java.lang.String r1 = "IOException reading from "
            java.lang.StringBuilder r0 = r0.append(r1)
            r1 = r5
            java.lang.String r1 = r1.fileName
            java.lang.StringBuilder r0 = r0.append(r1)
            java.lang.String r0 = r0.toString()
            r1 = r7
            java.sql.SQLException r0 = convertException(r0, r1)
            throw r0
        L8e:
            r0 = r6
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.tools.Csv.readRow():java.lang.Object[]");
    }

    private static SQLException convertException(String str, Exception exc) {
        return DbException.getJdbcSQLException(ErrorCode.IO_EXCEPTION_1, exc, str);
    }

    @Override // org.h2.tools.SimpleRowSource
    public void close() {
        IOUtils.closeSilently(this.input);
        this.input = null;
        IOUtils.closeSilently(this.output);
        this.output = null;
    }

    @Override // org.h2.tools.SimpleRowSource
    public void reset() throws SQLException {
        throw new SQLException("Method is not supported", "CSV");
    }

    public void setFieldSeparatorWrite(String str) {
        this.fieldSeparatorWrite = str;
    }

    public String getFieldSeparatorWrite() {
        return this.fieldSeparatorWrite;
    }

    public void setCaseSensitiveColumnNames(boolean z) {
        this.caseSensitiveColumnNames = z;
    }

    public boolean getCaseSensitiveColumnNames() {
        return this.caseSensitiveColumnNames;
    }

    public void setFieldSeparatorRead(char c) {
        this.fieldSeparatorRead = c;
    }

    public char getFieldSeparatorRead() {
        return this.fieldSeparatorRead;
    }

    public void setLineCommentCharacter(char c) {
        this.lineComment = c;
    }

    public char getLineCommentCharacter() {
        return this.lineComment;
    }

    public void setFieldDelimiter(char c) {
        this.fieldDelimiter = c;
    }

    public char getFieldDelimiter() {
        return this.fieldDelimiter;
    }

    public void setEscapeCharacter(char c) {
        this.escapeCharacter = c;
    }

    public char getEscapeCharacter() {
        return this.escapeCharacter;
    }

    public void setLineSeparator(String str) {
        this.lineSeparator = str;
    }

    public String getLineSeparator() {
        return this.lineSeparator;
    }

    public void setQuotedNulls(boolean z) {
        this.quotedNulls = z;
    }

    public boolean isQuotedNulls() {
        return this.quotedNulls;
    }

    public void setNullString(String str) {
        this.nullString = str;
    }

    public String getNullString() {
        return this.nullString;
    }

    public void setPreserveWhitespace(boolean z) {
        this.preserveWhitespace = z;
    }

    public boolean getPreserveWhitespace() {
        return this.preserveWhitespace;
    }

    public void setWriteColumnHeader(boolean z) {
        this.writeColumnHeader = z;
    }

    public boolean getWriteColumnHeader() {
        return this.writeColumnHeader;
    }

    public String setOptions(String str) {
        String str2 = null;
        for (String str3 : StringUtils.arraySplit(str, ' ', false)) {
            if (!str3.isEmpty()) {
                int indexOf = str3.indexOf(61);
                String trimSubstring = StringUtils.trimSubstring(str3, 0, indexOf);
                String substring = str3.substring(indexOf + 1);
                char charAt = substring.isEmpty() ? (char) 0 : substring.charAt(0);
                if (isParam(trimSubstring, "escape", "esc", "escapeCharacter")) {
                    setEscapeCharacter(charAt);
                } else if (isParam(trimSubstring, "fieldDelimiter", "fieldDelim")) {
                    setFieldDelimiter(charAt);
                } else if (isParam(trimSubstring, "fieldSeparator", "fieldSep")) {
                    setFieldSeparatorRead(charAt);
                    setFieldSeparatorWrite(substring);
                } else if (isParam(trimSubstring, "lineComment", "lineCommentCharacter")) {
                    setLineCommentCharacter(charAt);
                } else if (isParam(trimSubstring, "lineSeparator", "lineSep")) {
                    setLineSeparator(substring);
                } else if (isParam(trimSubstring, "null", "nullString")) {
                    setNullString(substring);
                } else if (isParam(trimSubstring, "quotedNulls")) {
                    setQuotedNulls(Utils.parseBoolean(substring, false, false));
                } else if (isParam(trimSubstring, BasicAuthenticator.charsetparam, "characterSet")) {
                    str2 = substring;
                } else if (isParam(trimSubstring, "preserveWhitespace")) {
                    setPreserveWhitespace(Utils.parseBoolean(substring, false, false));
                } else if (isParam(trimSubstring, "writeColumnHeader")) {
                    setWriteColumnHeader(Utils.parseBoolean(substring, true, false));
                } else if (isParam(trimSubstring, "caseSensitiveColumnNames")) {
                    setCaseSensitiveColumnNames(Utils.parseBoolean(substring, false, false));
                } else {
                    throw DbException.getUnsupportedException(trimSubstring);
                }
            }
        }
        return str2;
    }

    private static boolean isParam(String str, String... strArr) {
        for (String str2 : strArr) {
            if (str.equalsIgnoreCase(str2)) {
                return true;
            }
        }
        return false;
    }
}
