package org.h2.tools;

import cn.hutool.core.text.StrPool;
import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.h2.jdbc.JdbcConnection;
import org.h2.util.Tool;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/tools/CreateCluster.class */
public class CreateCluster extends Tool {
    public static void main(String... strArr) throws SQLException {
        new CreateCluster().runTool(strArr);
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
            if (str6.equals("-urlSource")) {
                i++;
                str = strArr[i];
            } else if (str6.equals("-urlTarget")) {
                i++;
                str2 = strArr[i];
            } else if (str6.equals("-user")) {
                i++;
                str3 = strArr[i];
            } else if (str6.equals("-password")) {
                i++;
                str4 = strArr[i];
            } else if (str6.equals("-serverList")) {
                i++;
                str5 = strArr[i];
            } else {
                if (str6.equals("-help") || str6.equals("-?")) {
                    showUsage();
                    return;
                }
                showUsageAndThrowUnsupportedOption(str6);
            }
            i++;
        }
        if (str == null || str2 == null || str5 == null) {
            showUsage();
            throw new SQLException("Source URL, target URL, or server list not set");
        }
        process(str, str2, str3, str4, str5);
    }

    public void execute(String str, String str2, String str3, String str4, String str5) throws SQLException {
        process(str, str2, str3, str4, str5);
    }

    /* JADX WARN: Finally extract failed */
    private static void process(String str, String str2, String str3, String str4, String str5) throws SQLException {
        JdbcConnection jdbcConnection = new JdbcConnection(str + ";CLUSTER=''", null, str3, str4, false);
        Throwable th = null;
        try {
            Statement createStatement = jdbcConnection.createStatement();
            Throwable th2 = null;
            try {
                createStatement.execute("SET EXCLUSIVE 2");
                try {
                    performTransfer(createStatement, str2, str3, str4, str5);
                    createStatement.execute("SET EXCLUSIVE FALSE");
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
                    if (jdbcConnection != null) {
                        if (0 == 0) {
                            jdbcConnection.close();
                            return;
                        }
                        try {
                            jdbcConnection.close();
                        } catch (Throwable th4) {
                            th.addSuppressed(th4);
                        }
                    }
                } catch (Throwable th5) {
                    createStatement.execute("SET EXCLUSIVE FALSE");
                    throw th5;
                }
            } catch (Throwable th6) {
                if (createStatement != null) {
                    if (0 != 0) {
                        try {
                            createStatement.close();
                        } catch (Throwable th7) {
                            th2.addSuppressed(th7);
                        }
                    } else {
                        createStatement.close();
                    }
                }
                throw th6;
            }
        } catch (Throwable th8) {
            if (jdbcConnection != null) {
                if (0 != 0) {
                    try {
                        jdbcConnection.close();
                    } catch (Throwable th9) {
                        th.addSuppressed(th9);
                    }
                } else {
                    jdbcConnection.close();
                }
            }
            throw th8;
        }
    }

    /* JADX WARN: Failed to calculate best type for var: r18v1 ??
    java.lang.NullPointerException
     */
    /* JADX WARN: Failed to calculate best type for var: r19v1 ??
    java.lang.NullPointerException
     */
    /* JADX WARN: Multi-variable type inference failed. Error: java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.RegisterArg.getSVar()" because the return value of "jadx.core.dex.nodes.InsnNode.getResult()" is null
    	at jadx.core.dex.visitors.typeinference.AbstractTypeConstraint.collectRelatedVars(AbstractTypeConstraint.java:31)
    	at jadx.core.dex.visitors.typeinference.AbstractTypeConstraint.<init>(AbstractTypeConstraint.java:19)
    	at jadx.core.dex.visitors.typeinference.TypeSearch$1.<init>(TypeSearch.java:376)
    	at jadx.core.dex.visitors.typeinference.TypeSearch.makeMoveConstraint(TypeSearch.java:376)
    	at jadx.core.dex.visitors.typeinference.TypeSearch.makeConstraint(TypeSearch.java:361)
    	at jadx.core.dex.visitors.typeinference.TypeSearch.collectConstraints(TypeSearch.java:341)
    	at java.base/java.util.ArrayList.forEach(Unknown Source)
    	at jadx.core.dex.visitors.typeinference.TypeSearch.run(TypeSearch.java:60)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.runMultiVariableSearch(FixTypesVisitor.java:116)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:91)
     */
    /* JADX WARN: Not initialized variable reg: 18, insn: 0x01c0: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r18 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) A[TRY_LEAVE], block:B:83:0x01c0 */
    /* JADX WARN: Not initialized variable reg: 19, insn: 0x01c5: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r19 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]), block:B:85:0x01c5 */
    /* JADX WARN: Type inference failed for: r0v0, types: [java.io.PipedReader, org.h2.jdbc.JdbcConnection] */
    /* JADX WARN: Type inference failed for: r16v0, types: [java.lang.Throwable, org.h2.jdbc.JdbcConnection] */
    /* JADX WARN: Type inference failed for: r16v1 */
    /* JADX WARN: Type inference failed for: r16v2 */
    /* JADX WARN: Type inference failed for: r18v1, types: [java.sql.Statement] */
    /* JADX WARN: Type inference failed for: r19v1, types: [java.lang.Throwable] */
    private static void performTransfer(Statement statement, String str, String str2, String str3, String str4) throws SQLException {
        ?? r18;
        ?? r19;
        ?? jdbcConnection = new JdbcConnection(str + ";CLUSTER=''", null, str2, str3, false);
        Throwable th = null;
        try {
            Statement createStatement = jdbcConnection.createStatement();
            boolean z = 0;
            try {
                try {
                    createStatement.execute("DROP ALL OBJECTS DELETE FILES");
                    if (createStatement != null) {
                        if (0 != 0) {
                            try {
                                createStatement.close();
                            } catch (Throwable th2) {
                                z.addSuppressed(th2);
                            }
                        } else {
                            createStatement.close();
                        }
                    }
                    try {
                        try {
                            PipedReader pipedReader = new PipedReader();
                            Throwable th3 = null;
                            try {
                                Future<?> startWriter = startWriter(pipedReader, statement);
                                JdbcConnection jdbcConnection2 = new JdbcConnection(str, null, str2, str3, false);
                                Throwable th4 = null;
                                try {
                                    Statement createStatement2 = jdbcConnection2.createStatement();
                                    Throwable th5 = null;
                                    RunScript.execute(jdbcConnection2, pipedReader);
                                    try {
                                        startWriter.get();
                                        statement.executeUpdate("SET CLUSTER '" + str4 + "'");
                                        createStatement2.executeUpdate("SET CLUSTER '" + str4 + "'");
                                        if (createStatement2 != null) {
                                            if (0 != 0) {
                                                try {
                                                    createStatement2.close();
                                                } catch (Throwable th6) {
                                                    th5.addSuppressed(th6);
                                                }
                                            } else {
                                                createStatement2.close();
                                            }
                                        }
                                        if (jdbcConnection2 != null) {
                                            if (0 != 0) {
                                                try {
                                                    jdbcConnection2.close();
                                                } catch (Throwable th7) {
                                                    th4.addSuppressed(th7);
                                                }
                                            } else {
                                                jdbcConnection2.close();
                                            }
                                        }
                                        if (pipedReader != null) {
                                            if (0 != 0) {
                                                try {
                                                    pipedReader.close();
                                                } catch (Throwable th8) {
                                                    th3.addSuppressed(th8);
                                                }
                                            } else {
                                                pipedReader.close();
                                            }
                                        }
                                    } catch (InterruptedException e) {
                                        throw new SQLException(e);
                                    } catch (ExecutionException e2) {
                                        throw new SQLException(e2.getCause());
                                    }
                                } catch (Throwable th9) {
                                    if (r18 != 0) {
                                        if (r19 != 0) {
                                            try {
                                                r18.close();
                                            } catch (Throwable th10) {
                                                r19.addSuppressed(th10);
                                            }
                                        } else {
                                            r18.close();
                                        }
                                    }
                                    throw th9;
                                }
                            } catch (Throwable th11) {
                                if (0 != 0) {
                                    if (th2 != null) {
                                        try {
                                            z.close();
                                        } catch (Throwable th12) {
                                            th2.addSuppressed(th12);
                                        }
                                    } else {
                                        z.close();
                                    }
                                }
                                throw th11;
                            }
                        } catch (IOException e3) {
                            throw new SQLException(e3);
                        }
                    } finally {
                    }
                } catch (Throwable th13) {
                    if (createStatement != null) {
                        if (z) {
                            try {
                                createStatement.close();
                            } catch (Throwable th14) {
                                z.addSuppressed(th14);
                            }
                        } else {
                            createStatement.close();
                        }
                    }
                    throw th13;
                }
            } finally {
            }
        } finally {
            if (jdbcConnection != 0) {
                if (0 != 0) {
                    try {
                        jdbcConnection.close();
                    } catch (Throwable th15) {
                        th.addSuppressed(th15);
                    }
                } else {
                    jdbcConnection.close();
                }
            }
        }
    }

    private static Future<?> startWriter(PipedReader pipedReader, Statement statement) throws IOException {
        ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(1);
        PipedWriter pipedWriter = new PipedWriter(pipedReader);
        Future<?> submit = newFixedThreadPool.submit(() -> {
            Throwable th = null;
            try {
                try {
                    ResultSet executeQuery = statement.executeQuery("SCRIPT");
                    Throwable th2 = null;
                    while (executeQuery.next()) {
                        try {
                            try {
                                pipedWriter.write(executeQuery.getString(1) + StrPool.LF);
                            } catch (Throwable th3) {
                                if (executeQuery != null) {
                                    if (th2 != null) {
                                        try {
                                            executeQuery.close();
                                        } catch (Throwable th4) {
                                            th2.addSuppressed(th4);
                                        }
                                    } else {
                                        executeQuery.close();
                                    }
                                }
                                throw th3;
                            }
                        } finally {
                        }
                    }
                    if (executeQuery != null) {
                        if (0 != 0) {
                            try {
                                executeQuery.close();
                            } catch (Throwable th5) {
                                th2.addSuppressed(th5);
                            }
                        } else {
                            executeQuery.close();
                        }
                    }
                    if (pipedWriter != null) {
                        if (0 != 0) {
                            try {
                                pipedWriter.close();
                            } catch (Throwable th6) {
                                th.addSuppressed(th6);
                            }
                        } else {
                            pipedWriter.close();
                        }
                    }
                } catch (IOException | SQLException e) {
                    throw new IllegalStateException("Producing script from the source DB is failing.", e);
                }
            } catch (Throwable th7) {
                if (pipedWriter != null) {
                    if (0 != 0) {
                        try {
                            pipedWriter.close();
                        } catch (Throwable th8) {
                            th.addSuppressed(th8);
                        }
                    } else {
                        pipedWriter.close();
                    }
                }
                throw th7;
            }
        });
        newFixedThreadPool.shutdown();
        return submit;
    }
}
