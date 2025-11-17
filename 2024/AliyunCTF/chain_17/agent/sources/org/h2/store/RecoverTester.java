package org.h2.store;

import ch.qos.logback.core.util.FileSize;
import cn.hutool.core.text.CharSequenceUtil;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashSet;
import org.h2.engine.ConnectionInfo;
import org.h2.engine.Constants;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.store.fs.FileUtils;
import org.h2.store.fs.Recorder;
import org.h2.store.fs.rec.FilePathRec;
import org.h2.tools.Recover;
import org.h2.util.IOUtils;
import org.h2.util.StringUtils;
import org.h2.util.Utils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/store/RecoverTester.class */
public class RecoverTester implements Recorder {
    private static final RecoverTester instance = new RecoverTester();
    private int verifyCount;
    private volatile boolean testing;
    private String testDatabase = "memFS:reopen";
    private int writeCount = Utils.getProperty("h2.recoverTestOffset", 0);
    private int testEvery = Utils.getProperty("h2.recoverTest", 64);
    private final long maxFileSize = (Utils.getProperty("h2.recoverTestMaxFileSize", Integer.MAX_VALUE) * FileSize.KB_COEFFICIENT) * FileSize.KB_COEFFICIENT;
    private final HashSet<String> knownErrors = new HashSet<>();

    public static synchronized void init(String str) {
        if (StringUtils.isNumber(str)) {
            instance.setTestEvery(Integer.parseInt(str));
        }
        FilePathRec.setRecorder(instance);
    }

    @Override // org.h2.store.fs.Recorder
    public void log(int i, String str, byte[] bArr, long j) {
        if ((i != 8 && i != 7) || !str.endsWith(Constants.SUFFIX_MV_FILE)) {
            return;
        }
        this.writeCount++;
        if (this.writeCount % this.testEvery != 0 || FileUtils.size(str) > this.maxFileSize || this.testing) {
            return;
        }
        this.testing = true;
        PrintWriter printWriter = null;
        try {
            try {
                printWriter = new PrintWriter(new OutputStreamWriter(FileUtils.newOutputStream(str + ".log", true)));
                testDatabase(str, printWriter);
                IOUtils.closeSilently(printWriter);
                this.testing = false;
            } catch (IOException e) {
                throw DbException.convertIOException(e, null);
            }
        } catch (Throwable th) {
            IOUtils.closeSilently(printWriter);
            this.testing = false;
            throw th;
        }
    }

    private synchronized void testDatabase(String str, PrintWriter printWriter) {
        printWriter.println("+ write #" + this.writeCount + " verify #" + this.verifyCount);
        try {
            IOUtils.copyFiles(str, this.testDatabase + Constants.SUFFIX_MV_FILE);
            this.verifyCount++;
            Database database = new Database(new ConnectionInfo(Constants.START_URL + this.testDatabase + ";FILE_LOCK=NO;TRACE_LEVEL_FILE=0", null, "", ""), null);
            SessionLocal systemSession = database.getSystemSession();
            systemSession.prepare("script to '" + this.testDatabase + ".sql'").query(0L);
            systemSession.prepare("shutdown immediately").update();
            database.removeSession(null);
        } catch (DbException e) {
            int errorCode = DbException.toSQLException(e).getErrorCode();
            if (errorCode == 28000 || errorCode == 90049) {
                return;
            }
            e.printStackTrace(System.out);
            printWriter.println("begin ------------------------------ " + this.writeCount);
            try {
                Recover.execute(str.substring(0, str.lastIndexOf(47)), null);
            } catch (SQLException e2) {
            }
            this.testDatabase += "X";
            try {
                IOUtils.copyFiles(str, this.testDatabase + Constants.SUFFIX_MV_FILE);
                new Database(new ConnectionInfo(Constants.START_URL + this.testDatabase + ";FILE_LOCK=NO", null, null, null), null).removeSession(null);
            } catch (Exception e3) {
                e = e3;
                int i = 0;
                if (e instanceof DbException) {
                    e = ((DbException) e).getSQLException();
                    i = ((SQLException) e).getErrorCode();
                }
                if (i == 28000 || i == 90049) {
                    return;
                }
                StringBuilder sb = new StringBuilder();
                StackTraceElement[] stackTrace = e.getStackTrace();
                for (int i2 = 0; i2 < 10 && i2 < stackTrace.length; i2++) {
                    sb.append(stackTrace[i2].toString()).append('\n');
                }
                String sb2 = sb.toString();
                if (!this.knownErrors.contains(sb2)) {
                    printWriter.println(this.writeCount + " code: " + i + CharSequenceUtil.SPACE + e.toString());
                    e.printStackTrace(System.out);
                    this.knownErrors.add(sb2);
                    return;
                }
                printWriter.println(this.writeCount + " code: " + i);
            }
        } catch (Exception e4) {
            int i3 = 0;
            if (e4 instanceof SQLException) {
                i3 = ((SQLException) e4).getErrorCode();
            }
            if (i3 == 28000 || i3 == 90049) {
                return;
            }
            e4.printStackTrace(System.out);
            printWriter.println("begin ------------------------------ " + this.writeCount);
            Recover.execute(str.substring(0, str.lastIndexOf(47)), null);
            this.testDatabase += "X";
            IOUtils.copyFiles(str, this.testDatabase + Constants.SUFFIX_MV_FILE);
            new Database(new ConnectionInfo(Constants.START_URL + this.testDatabase + ";FILE_LOCK=NO", null, null, null), null).removeSession(null);
        }
    }

    public void setTestEvery(int i) {
        this.testEvery = i;
    }
}
