package org.h2.tools;

import ch.qos.logback.core.CoreConstants;
import cn.hutool.core.text.StrPool;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.h2.engine.Constants;
import org.h2.server.web.ConnectionInfo;
import org.h2.util.JdbcUtils;
import org.h2.util.ScriptReader;
import org.h2.util.SortedProperties;
import org.h2.util.StringUtils;
import org.h2.util.Tool;
import org.h2.util.Utils;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/tools/Shell.class */
public class Shell extends Tool implements Runnable {
    private static final int MAX_ROW_BUFFER = 5000;
    private static final int HISTORY_COUNT = 20;
    private static final char BOX_VERTICAL = '|';
    private BufferedReader reader;
    private Connection conn;
    private Statement stat;
    private boolean listMode;
    private boolean stopHide;
    private PrintStream err = System.err;
    private InputStream in = System.in;
    private int maxColumnSize = 100;
    private final ArrayList<String> history = new ArrayList<>();
    private String serverPropertiesDir = Constants.SERVER_PROPERTIES_DIR;

    public static void main(String... strArr) throws SQLException {
        new Shell().runTool(strArr);
    }

    public void setErr(PrintStream printStream) {
        this.err = printStream;
    }

    public void setIn(InputStream inputStream) {
        this.in = inputStream;
    }

    public void setInReader(BufferedReader bufferedReader) {
        this.reader = bufferedReader;
    }

    @Override // org.h2.util.Tool
    public void runTool(String... strArr) throws SQLException {
        String str = null;
        String str2 = null;
        String str3 = "";
        String str4 = "";
        String str5 = null;
        int i = 0;
        while (strArr != null && i < strArr.length) {
            String str6 = strArr[i];
            if (str6.equals("-url")) {
                i++;
                str2 = strArr[i];
            } else if (str6.equals("-user")) {
                i++;
                str3 = strArr[i];
            } else if (str6.equals("-password")) {
                i++;
                str4 = strArr[i];
            } else if (str6.equals("-driver")) {
                i++;
                str = strArr[i];
            } else if (str6.equals("-sql")) {
                i++;
                str5 = strArr[i];
            } else if (str6.equals("-properties")) {
                i++;
                this.serverPropertiesDir = strArr[i];
            } else if (str6.equals("-help") || str6.equals("-?")) {
                showUsage();
                return;
            } else if (str6.equals("-list")) {
                this.listMode = true;
            } else {
                showUsageAndThrowUnsupportedOption(str6);
            }
            i++;
        }
        if (str2 != null) {
            this.conn = JdbcUtils.getConnection(str, str2, str3, str4);
            this.stat = this.conn.createStatement();
        }
        if (str5 == null) {
            promptLoop();
            return;
        }
        ScriptReader scriptReader = new ScriptReader(new StringReader(str5));
        while (true) {
            String readStatement = scriptReader.readStatement();
            if (readStatement == null) {
                break;
            } else {
                execute(readStatement);
            }
        }
        if (this.conn != null) {
            this.conn.close();
        }
    }

    public void runTool(Connection connection, String... strArr) throws SQLException {
        this.conn = connection;
        this.stat = connection.createStatement();
        runTool(strArr);
    }

    private void showHelp() {
        println("Commands are case insensitive; SQL statements end with ';'");
        println("help or ?      Display this help");
        println("list           Toggle result list / stack trace mode");
        println("maxwidth       Set maximum column width (default is 100)");
        println("autocommit     Enable or disable autocommit");
        println("history        Show the last 20 statements");
        println("quit or exit   Close the connection and exit");
        println("");
    }

    private void promptLoop() {
        String readLine;
        println("");
        println("Welcome to H2 Shell " + Constants.FULL_VERSION);
        println("Exit with Ctrl+C");
        if (this.conn != null) {
            showHelp();
        }
        String str = null;
        if (this.reader == null) {
            this.reader = new BufferedReader(new InputStreamReader(this.in));
        }
        while (true) {
            try {
                if (this.conn == null) {
                    connect();
                    showHelp();
                }
                if (str == null) {
                    print("sql> ");
                } else {
                    print("...> ");
                }
                readLine = readLine();
            } catch (IOException e) {
                println(e.getMessage());
            } catch (SQLException e2) {
                println("SQL Exception: " + e2.getMessage());
                str = null;
            } catch (Exception e3) {
                println("Exception: " + e3.toString());
                e3.printStackTrace(this.err);
            }
            if (readLine == null) {
                break;
            }
            String trim = readLine.trim();
            if (!trim.isEmpty()) {
                boolean endsWith = trim.endsWith(";");
                if (endsWith) {
                    readLine = readLine.substring(0, readLine.lastIndexOf(59));
                    trim = trim.substring(0, trim.length() - 1);
                }
                String lowerEnglish = StringUtils.toLowerEnglish(trim);
                if ("exit".equals(lowerEnglish) || "quit".equals(lowerEnglish)) {
                    break;
                }
                if ("help".equals(lowerEnglish) || CoreConstants.NA.equals(lowerEnglish)) {
                    showHelp();
                } else if (BeanDefinitionParserDelegate.LIST_ELEMENT.equals(lowerEnglish)) {
                    this.listMode = !this.listMode;
                    println("Result list mode is now " + (this.listMode ? CustomBooleanEditor.VALUE_ON : CustomBooleanEditor.VALUE_OFF));
                } else if ("history".equals(lowerEnglish)) {
                    int size = this.history.size();
                    for (int i = 0; i < size; i++) {
                        println("#" + (1 + i) + ": " + this.history.get(i).replace('\n', ' ').replace('\r', ' '));
                    }
                    if (!this.history.isEmpty()) {
                        println("To re-run a statement, type the number and press and enter");
                    } else {
                        println("No history");
                    }
                } else if (lowerEnglish.startsWith("autocommit")) {
                    String trimSubstring = StringUtils.trimSubstring(lowerEnglish, "autocommit".length());
                    if ("true".equals(trimSubstring)) {
                        this.conn.setAutoCommit(true);
                    } else if ("false".equals(trimSubstring)) {
                        this.conn.setAutoCommit(false);
                    } else {
                        println("Usage: autocommit [true|false]");
                    }
                    println("Autocommit is now " + this.conn.getAutoCommit());
                } else if (lowerEnglish.startsWith("maxwidth")) {
                    try {
                        this.maxColumnSize = Integer.parseInt(StringUtils.trimSubstring(lowerEnglish, "maxwidth".length()));
                    } catch (NumberFormatException e4) {
                        println("Usage: maxwidth <integer value>");
                    }
                    println("Maximum column width is now " + this.maxColumnSize);
                } else {
                    boolean z = true;
                    if (str == null) {
                        if (StringUtils.isNumber(readLine)) {
                            int parseInt = Integer.parseInt(readLine);
                            if (parseInt == 0 || parseInt > this.history.size()) {
                                println("Not found");
                            } else {
                                str = this.history.get(parseInt - 1);
                                z = false;
                                println(str);
                                endsWith = true;
                            }
                        } else {
                            str = readLine;
                        }
                    } else {
                        str = str + StrPool.LF + readLine;
                    }
                    if (endsWith) {
                        if (z) {
                            this.history.add(0, str);
                            if (this.history.size() > 20) {
                                this.history.remove(20);
                            }
                        }
                        execute(str);
                        str = null;
                    }
                }
            }
        }
        if (this.conn != null) {
            try {
                this.conn.close();
                println("Connection closed");
            } catch (SQLException e5) {
                println("SQL Exception: " + e5.getMessage());
                e5.printStackTrace(this.err);
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v48, types: [java.util.Properties] */
    private void connect() throws IOException, SQLException {
        SortedProperties loadProperties;
        String str = "jdbc:h2:~/test";
        String str2 = "";
        String str3 = null;
        try {
            if ("null".equals(this.serverPropertiesDir)) {
                loadProperties = new Properties();
            } else {
                loadProperties = SortedProperties.loadProperties(this.serverPropertiesDir + "/" + Constants.SERVER_PROPERTIES_NAME);
            }
            String str4 = null;
            boolean z = false;
            int i = 0;
            while (true) {
                String property = loadProperties.getProperty(Integer.toString(i));
                if (property == null) {
                    break;
                }
                z = true;
                str4 = property;
                i++;
            }
            if (z) {
                ConnectionInfo connectionInfo = new ConnectionInfo(str4);
                str = connectionInfo.url;
                str2 = connectionInfo.user;
                str3 = connectionInfo.driver;
            }
        } catch (IOException e) {
        }
        println("[Enter]   " + str);
        print("URL       ");
        String trim = readLine(str).trim();
        if (str3 == null) {
            str3 = JdbcUtils.getDriver(trim);
        }
        if (str3 != null) {
            println("[Enter]   " + str3);
        }
        print("Driver    ");
        String trim2 = readLine(str3).trim();
        println("[Enter]   " + str2);
        print("User      ");
        String readLine = readLine(str2);
        this.conn = trim.startsWith(Constants.START_URL) ? connectH2(trim2, trim, readLine) : JdbcUtils.getConnection(trim2, trim, readLine, readPassword());
        this.stat = this.conn.createStatement();
        println("Connected");
    }

    private Connection connectH2(String str, String str2, String str3) throws IOException, SQLException {
        while (true) {
            String readPassword = readPassword();
            try {
                return JdbcUtils.getConnection(str, str2 + ";IFEXISTS=TRUE", str3, readPassword);
            } catch (SQLException e) {
                if (e.getErrorCode() == 90146) {
                    println("Type the same password again to confirm database creation.");
                    if (readPassword.equals(readPassword())) {
                        return JdbcUtils.getConnection(str, str2, str3, readPassword);
                    }
                    println("Passwords don't match. Try again.");
                } else {
                    throw e;
                }
            }
        }
    }

    protected void print(String str) {
        this.out.print(str);
        this.out.flush();
    }

    private void println(String str) {
        this.out.println(str);
        this.out.flush();
    }

    private String readPassword() throws IOException {
        try {
            Object callStaticMethod = Utils.callStaticMethod("java.lang.System.console", new Object[0]);
            print("Password  ");
            char[] cArr = (char[]) Utils.callMethod(callStaticMethod, "readPassword", new Object[0]);
            if (cArr == null) {
                return null;
            }
            return new String(cArr);
        } catch (Exception e) {
            Thread thread = new Thread(this, "Password hider");
            this.stopHide = false;
            thread.start();
            print("Password  > ");
            String readLine = readLine();
            this.stopHide = true;
            try {
                thread.join();
            } catch (InterruptedException e2) {
            }
            print("\b\b");
            return readLine;
        }
    }

    @Override // java.lang.Runnable
    public void run() {
        while (!this.stopHide) {
            print("\b\b><");
            try {
                Thread.sleep(10L);
            } catch (InterruptedException e) {
            }
        }
    }

    private String readLine(String str) throws IOException {
        String readLine = readLine();
        return readLine.isEmpty() ? str : readLine;
    }

    private String readLine() throws IOException {
        String readLine = this.reader.readLine();
        if (readLine == null) {
            throw new IOException("Aborted");
        }
        return readLine;
    }

    /* JADX WARN: Finally extract failed */
    private void execute(String str) {
        long updateCount;
        if (StringUtils.isWhitespaceOrEmpty(str)) {
            return;
        }
        long nanoTime = System.nanoTime();
        ResultSet resultSet = null;
        try {
            try {
                if (str.startsWith(StrPool.AT)) {
                    resultSet = JdbcUtils.getMetaResultSet(this.conn, str);
                    printResult(resultSet, this.listMode);
                } else if (this.stat.execute(str)) {
                    resultSet = this.stat.getResultSet();
                    int printResult = printResult(resultSet, this.listMode);
                    println("(" + printResult + (printResult == 1 ? " row, " : " rows, ") + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - nanoTime) + " ms)");
                } else {
                    try {
                        updateCount = this.stat.getLargeUpdateCount();
                    } catch (UnsupportedOperationException e) {
                        updateCount = this.stat.getUpdateCount();
                    }
                    println("(Update count: " + updateCount + ", " + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - nanoTime) + " ms)");
                }
                JdbcUtils.closeSilently(resultSet);
            } catch (Throwable th) {
                JdbcUtils.closeSilently((ResultSet) null);
                throw th;
            }
        } catch (SQLException e2) {
            println("Error: " + e2.toString());
            if (this.listMode) {
                e2.printStackTrace(this.err);
            }
        }
    }

    private int printResult(ResultSet resultSet, boolean z) throws SQLException {
        if (z) {
            return printResultAsList(resultSet);
        }
        return printResultAsTable(resultSet);
    }

    private int printResultAsTable(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        boolean z = false;
        ArrayList<String[]> arrayList = new ArrayList<>();
        String[] strArr = new String[columnCount];
        for (int i = 0; i < columnCount; i++) {
            String columnLabel = metaData.getColumnLabel(i + 1);
            strArr[i] = columnLabel == null ? "" : columnLabel;
        }
        arrayList.add(strArr);
        int i2 = 0;
        while (resultSet.next()) {
            i2++;
            z |= loadRow(resultSet, columnCount, arrayList);
            if (i2 > 5000) {
                printRows(arrayList, columnCount);
                arrayList.clear();
            }
        }
        printRows(arrayList, columnCount);
        arrayList.clear();
        if (z) {
            println("(data is partially truncated)");
        }
        return i2;
    }

    private boolean loadRow(ResultSet resultSet, int i, ArrayList<String[]> arrayList) throws SQLException {
        boolean z = false;
        String[] strArr = new String[i];
        for (int i2 = 0; i2 < i; i2++) {
            String string = resultSet.getString(i2 + 1);
            if (string == null) {
                string = "null";
            }
            if (i > 1 && string.length() > this.maxColumnSize) {
                string = string.substring(0, this.maxColumnSize);
                z = true;
            }
            strArr[i2] = string;
        }
        arrayList.add(strArr);
        return z;
    }

    private int[] printRows(ArrayList<String[]> arrayList, int i) {
        int[] iArr = new int[i];
        for (int i2 = 0; i2 < i; i2++) {
            int i3 = 0;
            Iterator<String[]> it = arrayList.iterator();
            while (it.hasNext()) {
                i3 = Math.max(i3, it.next()[i2].length());
            }
            if (i > 1) {
                i3 = Math.min(this.maxColumnSize, i3);
            }
            iArr[i2] = i3;
        }
        Iterator<String[]> it2 = arrayList.iterator();
        while (it2.hasNext()) {
            String[] next = it2.next();
            StringBuilder sb = new StringBuilder();
            for (int i4 = 0; i4 < i; i4++) {
                if (i4 > 0) {
                    sb.append(' ').append('|').append(' ');
                }
                String str = next[i4];
                sb.append(str);
                if (i4 < i - 1) {
                    for (int length = str.length(); length < iArr[i4]; length++) {
                        sb.append(' ');
                    }
                }
            }
            println(sb.toString());
        }
        return iArr;
    }

    private int printResultAsList(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int i = 0;
        int columnCount = metaData.getColumnCount();
        String[] strArr = new String[columnCount];
        for (int i2 = 0; i2 < columnCount; i2++) {
            String columnLabel = metaData.getColumnLabel(i2 + 1);
            strArr[i2] = columnLabel;
            i = Math.max(i, columnLabel.length());
        }
        StringBuilder sb = new StringBuilder();
        int i3 = 0;
        while (resultSet.next()) {
            i3++;
            sb.setLength(0);
            if (i3 > 1) {
                println("");
            }
            for (int i4 = 0; i4 < columnCount; i4++) {
                if (i4 > 0) {
                    sb.append('\n');
                }
                String str = strArr[i4];
                sb.append(str);
                for (int length = str.length(); length < i; length++) {
                    sb.append(' ');
                }
                sb.append(": ").append(resultSet.getString(i4 + 1));
            }
            println(sb.toString());
        }
        if (i3 == 0) {
            for (int i5 = 0; i5 < columnCount; i5++) {
                if (i5 > 0) {
                    sb.append('\n');
                }
                sb.append(strArr[i5]);
            }
            println(sb.toString());
        }
        return i3;
    }
}
