package org.h2.tools;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;
import org.h2.message.DbException;
import org.h2.store.fs.FileUtils;
import org.h2.util.IOUtils;
import org.h2.util.JdbcUtils;
import org.h2.util.ScriptReader;
import org.h2.util.StringUtils;
import org.h2.util.Tool;
import org.slf4j.Marker;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/tools/RunScript.class */
public class RunScript extends Tool {
    private boolean showResults;
    private boolean checkResults;

    public static void main(String... strArr) throws SQLException {
        new RunScript().runTool(strArr);
    }

    @Override // org.h2.util.Tool
    public void runTool(String... strArr) throws SQLException {
        String str = null;
        String str2 = "";
        String str3 = "";
        String str4 = "backup.sql";
        String str5 = null;
        boolean z = false;
        boolean z2 = false;
        int i = 0;
        while (strArr != null && i < strArr.length) {
            String str6 = strArr[i];
            if (str6.equals("-url")) {
                i++;
                str = strArr[i];
            } else if (str6.equals("-user")) {
                i++;
                str2 = strArr[i];
            } else if (str6.equals("-password")) {
                i++;
                str3 = strArr[i];
            } else if (str6.equals("-continueOnError")) {
                z = true;
            } else if (str6.equals("-checkResults")) {
                this.checkResults = true;
            } else if (str6.equals("-showResults")) {
                this.showResults = true;
            } else if (str6.equals("-script")) {
                i++;
                str4 = strArr[i];
            } else if (str6.equals("-time")) {
                z2 = true;
            } else if (str6.equals("-driver")) {
                i++;
                JdbcUtils.loadUserClass(strArr[i]);
            } else if (str6.equals("-options")) {
                StringBuilder sb = new StringBuilder();
                while (true) {
                    i++;
                    if (i >= strArr.length) {
                        break;
                    } else {
                        sb.append(' ').append(strArr[i]);
                    }
                }
                str5 = sb.toString();
            } else {
                if (str6.equals("-help") || str6.equals("-?")) {
                    showUsage();
                    return;
                }
                showUsageAndThrowUnsupportedOption(str6);
            }
            i++;
        }
        if (str == null) {
            showUsage();
            throw new SQLException("URL not set");
        }
        long nanoTime = System.nanoTime();
        if (str5 != null) {
            processRunscript(str, str2, str3, str4, str5);
        } else {
            process(str, str2, str3, str4, null, z);
        }
        if (z2) {
            this.out.println("Done in " + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - nanoTime) + " ms");
        }
    }

    public static ResultSet execute(Connection connection, Reader reader) throws SQLException {
        Statement createStatement = connection.createStatement();
        ResultSet resultSet = null;
        ScriptReader scriptReader = new ScriptReader(reader);
        while (true) {
            String readStatement = scriptReader.readStatement();
            if (readStatement != null) {
                if (!StringUtils.isWhitespaceOrEmpty(readStatement) && createStatement.execute(readStatement)) {
                    if (resultSet != null) {
                        resultSet.close();
                    }
                    resultSet = createStatement.getResultSet();
                }
            } else {
                return resultSet;
            }
        }
    }

    private void process(Connection connection, String str, boolean z, Charset charset) throws SQLException, IOException {
        BufferedReader newBufferedReader = FileUtils.newBufferedReader(str, charset);
        try {
            process(connection, z, FileUtils.getParent(str), newBufferedReader, charset);
            IOUtils.closeSilently(newBufferedReader);
        } catch (Throwable th) {
            IOUtils.closeSilently(newBufferedReader);
            throw th;
        }
    }

    private void process(Connection connection, boolean z, String str, Reader reader, Charset charset) throws SQLException, IOException {
        Statement createStatement = connection.createStatement();
        ScriptReader scriptReader = new ScriptReader(reader);
        while (true) {
            String readStatement = scriptReader.readStatement();
            if (readStatement != null) {
                String trim = readStatement.trim();
                if (!trim.isEmpty()) {
                    if (trim.startsWith(StrPool.AT) && StringUtils.toUpperEnglish(trim).startsWith("@INCLUDE")) {
                        String trimSubstring = StringUtils.trimSubstring(readStatement, "@INCLUDE".length());
                        if (!FileUtils.isAbsolute(trimSubstring)) {
                            trimSubstring = str + File.separatorChar + trimSubstring;
                        }
                        process(connection, trimSubstring, z, charset);
                    } else {
                        try {
                            if (this.showResults && !trim.startsWith("-->")) {
                                this.out.print(readStatement + ";");
                            }
                            if (this.showResults || this.checkResults) {
                                if (createStatement.execute(readStatement)) {
                                    ResultSet resultSet = createStatement.getResultSet();
                                    int columnCount = resultSet.getMetaData().getColumnCount();
                                    StringBuilder sb = new StringBuilder();
                                    while (resultSet.next()) {
                                        sb.append("\n-->");
                                        for (int i = 0; i < columnCount; i++) {
                                            String string = resultSet.getString(i + 1);
                                            if (string != null) {
                                                string = StringUtils.replaceAll(StringUtils.replaceAll(StringUtils.replaceAll(string, "\r\n", StrPool.LF), StrPool.LF, "\n-->    "), StrPool.CR, "\r-->    ");
                                            }
                                            sb.append(' ').append(string);
                                        }
                                    }
                                    sb.append("\n;");
                                    String sb2 = sb.toString();
                                    if (this.showResults) {
                                        this.out.print(sb2);
                                    }
                                    if (this.checkResults) {
                                        String replaceAll = StringUtils.replaceAll(StringUtils.replaceAll(scriptReader.readStatement() + ";", "\r\n", StrPool.LF), StrPool.CR, StrPool.LF);
                                        if (!replaceAll.equals(sb2)) {
                                            throw new SQLException("Unexpected output for:\n" + readStatement.trim() + "\nGot:\n" + StringUtils.replaceAll(sb2, CharSequenceUtil.SPACE, Marker.ANY_NON_NULL_MARKER) + "\nExpected:\n" + StringUtils.replaceAll(replaceAll, CharSequenceUtil.SPACE, Marker.ANY_NON_NULL_MARKER));
                                            break;
                                        }
                                    }
                                }
                            } else {
                                createStatement.execute(readStatement);
                            }
                        } catch (Exception e) {
                            if (z) {
                                e.printStackTrace(this.out);
                            } else {
                                throw DbException.toSQLException(e);
                            }
                        }
                    }
                }
            } else {
                return;
            }
        }
    }

    private static void processRunscript(String str, String str2, String str3, String str4, String str5) throws SQLException {
        Connection connection = JdbcUtils.getConnection(null, str, str2, str3);
        Throwable th = null;
        try {
            Statement createStatement = connection.createStatement();
            Throwable th2 = null;
            try {
                try {
                    createStatement.execute("RUNSCRIPT FROM '" + str4 + "' " + str5);
                    if (createStatement != null) {
                        if (0 != 0) {
                            try {
                                createStatement.close();
                            } catch (Throwable th3) {
                                th2.addSuppressed(th3);
                            }
                        } else {
                            createStatement.close();
                        }
                    }
                    if (connection != null) {
                        if (0 == 0) {
                            connection.close();
                            return;
                        }
                        try {
                            connection.close();
                        } catch (Throwable th4) {
                            th.addSuppressed(th4);
                        }
                    }
                } catch (Throwable th5) {
                    if (createStatement != null) {
                        if (th2 != null) {
                            try {
                                createStatement.close();
                            } catch (Throwable th6) {
                                th2.addSuppressed(th6);
                            }
                        } else {
                            createStatement.close();
                        }
                    }
                    throw th5;
                }
            } catch (Throwable th7) {
                th2 = th7;
                throw th7;
            }
        } catch (Throwable th8) {
            if (connection != null) {
                if (0 != 0) {
                    try {
                        connection.close();
                    } catch (Throwable th9) {
                        th.addSuppressed(th9);
                    }
                } else {
                    connection.close();
                }
            }
            throw th8;
        }
    }

    public static void execute(String str, String str2, String str3, String str4, Charset charset, boolean z) throws SQLException {
        new RunScript().process(str, str2, str3, str4, charset, z);
    }

    void process(String str, String str2, String str3, String str4, Charset charset, boolean z) throws SQLException {
        if (charset == null) {
            charset = StandardCharsets.UTF_8;
        }
        try {
            Connection connection = JdbcUtils.getConnection(null, str, str2, str3);
            Throwable th = null;
            try {
                try {
                    process(connection, str4, z, charset);
                    if (connection != null) {
                        if (0 != 0) {
                            try {
                                connection.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            connection.close();
                        }
                    }
                } finally {
                }
            } finally {
            }
        } catch (IOException e) {
            throw DbException.convertIOException(e, str4);
        }
    }
}
