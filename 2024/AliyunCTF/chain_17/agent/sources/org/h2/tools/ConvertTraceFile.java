package org.h2.tools;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.text.CharSequenceUtil;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import org.h2.message.DbException;
import org.h2.store.fs.FileUtils;
import org.h2.util.IOUtils;
import org.h2.util.StringUtils;
import org.h2.util.Tool;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/tools/ConvertTraceFile.class */
public class ConvertTraceFile extends Tool {
    private final HashMap<String, Stat> stats = new HashMap<>();
    private long timeTotal;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/tools/ConvertTraceFile$Stat.class */
    public static class Stat implements Comparable<Stat> {
        String sql;
        int executeCount;
        long time;
        long resultCount;

        Stat() {
        }

        @Override // java.lang.Comparable
        public int compareTo(Stat stat) {
            if (stat == this) {
                return 0;
            }
            int compare = Long.compare(stat.time, this.time);
            if (compare == 0) {
                compare = Integer.compare(stat.executeCount, this.executeCount);
                if (compare == 0) {
                    compare = this.sql.compareTo(stat.sql);
                }
            }
            return compare;
        }
    }

    public static void main(String... strArr) throws SQLException {
        new ConvertTraceFile().runTool(strArr);
    }

    @Override // org.h2.util.Tool
    public void runTool(String... strArr) throws SQLException {
        String str = "test.trace.db";
        String str2 = "Test";
        String str3 = "test.sql";
        int i = 0;
        while (strArr != null && i < strArr.length) {
            String str4 = strArr[i];
            if (str4.equals("-traceFile")) {
                i++;
                str = strArr[i];
            } else if (str4.equals("-javaClass")) {
                i++;
                str2 = strArr[i];
            } else if (str4.equals("-script")) {
                i++;
                str3 = strArr[i];
            } else {
                if (str4.equals("-help") || str4.equals("-?")) {
                    showUsage();
                    return;
                }
                showUsageAndThrowUnsupportedOption(str4);
            }
            i++;
        }
        try {
            convertFile(str, str2, str3);
        } catch (IOException e) {
            throw DbException.convertIOException(e, str);
        }
    }

    private void convertFile(String str, String str2, String str3) throws IOException {
        LineNumberReader lineNumberReader = new LineNumberReader(IOUtils.getReader(FileUtils.newInputStream(str)));
        PrintWriter printWriter = new PrintWriter(IOUtils.getBufferedWriter(FileUtils.newOutputStream(str2 + FileNameUtil.EXT_JAVA, false)));
        PrintWriter printWriter2 = new PrintWriter(IOUtils.getBufferedWriter(FileUtils.newOutputStream(str3, false)));
        printWriter.println("import java.io.*;");
        printWriter.println("import java.sql.*;");
        printWriter.println("import java.math.*;");
        printWriter.println("import java.util.Calendar;");
        String replace = str2.replace('\\', '/');
        int lastIndexOf = replace.lastIndexOf(47);
        if (lastIndexOf > 0) {
            replace = replace.substring(lastIndexOf + 1);
        }
        printWriter.println("public class " + replace + " {");
        printWriter.println("    public static void main(String... args) throws Exception {");
        printWriter.println("        Class.forName(\"org.h2.Driver\");");
        while (true) {
            String readLine = lineNumberReader.readLine();
            if (readLine == null) {
                break;
            }
            if (readLine.startsWith("/**/")) {
                printWriter.println("        " + readLine.substring(4));
            } else if (readLine.startsWith("/*SQL")) {
                int indexOf = readLine.indexOf("*/");
                String javaDecode = StringUtils.javaDecode(readLine.substring(indexOf + "*/".length()));
                String substring = readLine.substring("/*SQL".length(), indexOf);
                if (substring.length() > 0) {
                    String str4 = javaDecode;
                    int i = 0;
                    long j = 0;
                    String trim = substring.trim();
                    if (trim.length() > 0) {
                        StringTokenizer stringTokenizer = new StringTokenizer(trim, " :");
                        while (stringTokenizer.hasMoreElements()) {
                            String nextToken = stringTokenizer.nextToken();
                            if ("l".equals(nextToken)) {
                                str4 = javaDecode.substring(0, Integer.parseInt(stringTokenizer.nextToken())) + ";";
                            } else if ("#".equals(nextToken)) {
                                i = Integer.parseInt(stringTokenizer.nextToken());
                            } else if ("t".equals(nextToken)) {
                                j = Long.parseLong(stringTokenizer.nextToken());
                            }
                        }
                    }
                    addToStats(str4, i, j);
                }
                printWriter2.println(javaDecode);
            }
        }
        printWriter.println("    }");
        printWriter.println('}');
        lineNumberReader.close();
        printWriter.close();
        if (this.stats.size() > 0) {
            printWriter2.println("-----------------------------------------");
            printWriter2.println("-- SQL Statement Statistics");
            printWriter2.println("-- time: total time in milliseconds (accumulated)");
            printWriter2.println("-- count: how many times the statement ran");
            printWriter2.println("-- result: total update count or row count");
            printWriter2.println("-----------------------------------------");
            printWriter2.println("-- self accu    time   count  result sql");
            int i2 = 0;
            ArrayList arrayList = new ArrayList(this.stats.values());
            Collections.sort(arrayList);
            if (this.timeTotal == 0) {
                this.timeTotal = 1L;
            }
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                Stat stat = (Stat) it.next();
                i2 = (int) (i2 + stat.time);
                StringBuilder sb = new StringBuilder(100);
                sb.append("-- ").append(padNumberLeft((100 * stat.time) / this.timeTotal, 3)).append("% ").append(padNumberLeft((100 * i2) / this.timeTotal, 3)).append('%').append(padNumberLeft(stat.time, 8)).append(padNumberLeft(stat.executeCount, 8)).append(padNumberLeft(stat.resultCount, 8)).append(' ').append(removeNewlines(stat.sql));
                printWriter2.println(sb.toString());
            }
        }
        printWriter2.close();
    }

    private static String removeNewlines(String str) {
        return str == null ? str : str.replace('\r', ' ').replace('\n', ' ');
    }

    private static String padNumberLeft(long j, int i) {
        return StringUtils.pad(Long.toString(j), i, CharSequenceUtil.SPACE, false);
    }

    private void addToStats(String str, int i, long j) {
        Stat stat = this.stats.get(str);
        if (stat == null) {
            stat = new Stat();
            stat.sql = str;
            this.stats.put(str, stat);
        }
        stat.executeCount++;
        stat.resultCount += i;
        stat.time += j;
        this.timeTotal += j;
    }
}
