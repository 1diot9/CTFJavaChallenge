package org.h2.tools;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.h2.command.dml.BackupCommand;
import org.h2.engine.Constants;
import org.h2.message.DbException;
import org.h2.store.FileLister;
import org.h2.store.fs.FileUtils;
import org.h2.util.IOUtils;
import org.h2.util.Tool;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/tools/Backup.class */
public class Backup extends Tool {
    public static void main(String... strArr) throws SQLException {
        new Backup().runTool(strArr);
    }

    @Override // org.h2.util.Tool
    public void runTool(String... strArr) throws SQLException {
        String str = "backup.zip";
        String str2 = ".";
        String str3 = null;
        boolean z = false;
        int i = 0;
        while (strArr != null && i < strArr.length) {
            String str4 = strArr[i];
            if (str4.equals("-dir")) {
                i++;
                str2 = strArr[i];
            } else if (str4.equals("-db")) {
                i++;
                str3 = strArr[i];
            } else if (str4.equals("-quiet")) {
                z = true;
            } else if (str4.equals("-file")) {
                i++;
                str = strArr[i];
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
            process(str, str2, str3, z);
        } catch (Exception e) {
            throw DbException.toSQLException(e);
        }
    }

    public static void execute(String str, String str2, String str3, boolean z) throws SQLException {
        try {
            new Backup().process(str, str2, str3, z);
        } catch (Exception e) {
            throw DbException.toSQLException(e);
        }
    }

    /* JADX WARN: Failed to calculate best type for var: r12v0 ??
    java.lang.NullPointerException
     */
    /* JADX WARN: Failed to calculate best type for var: r13v0 ??
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
    /* JADX WARN: Not initialized variable reg: 12, insn: 0x01ba: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r12 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) A[TRY_LEAVE], block:B:99:0x01ba */
    /* JADX WARN: Not initialized variable reg: 13, insn: 0x01bf: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r13 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]), block:B:101:0x01bf */
    /* JADX WARN: Type inference failed for: r12v0, types: [java.util.zip.ZipOutputStream] */
    /* JADX WARN: Type inference failed for: r13v0, types: [java.lang.Throwable] */
    private void process(String str, String str2, String str3, boolean z) throws SQLException {
        List databaseFiles;
        ?? r12;
        ?? r13;
        boolean z2 = str3 != null && str3.isEmpty();
        if (z2) {
            databaseFiles = FileUtils.newDirectoryStream(str2);
        } else {
            databaseFiles = FileLister.getDatabaseFiles(str2, str3, true);
        }
        if (databaseFiles.isEmpty()) {
            if (!z) {
                printNoDatabaseFilesFound(str2, str3);
                return;
            }
            return;
        }
        if (!z) {
            FileLister.tryUnlockDatabase(databaseFiles, "backup");
        }
        String realPath = FileUtils.toRealPath(str);
        FileUtils.delete(realPath);
        try {
            try {
                try {
                    OutputStream newOutputStream = FileUtils.newOutputStream(realPath, false);
                    ZipOutputStream zipOutputStream = new ZipOutputStream(newOutputStream);
                    Throwable th = null;
                    String str4 = "";
                    for (String str5 : databaseFiles) {
                        if (z2 || str5.endsWith(Constants.SUFFIX_MV_FILE)) {
                            str4 = FileUtils.getParent(str5);
                            break;
                        }
                    }
                    for (String str6 : databaseFiles) {
                        String realPath2 = FileUtils.toRealPath(str6);
                        if (!realPath2.startsWith(str4)) {
                            throw DbException.getInternalError(realPath2 + " does not start with " + str4);
                        }
                        if (!realPath2.endsWith(realPath) && !FileUtils.isDirectory(str6)) {
                            zipOutputStream.putNextEntry(new ZipEntry(BackupCommand.correctFileName(realPath2.substring(str4.length()))));
                            InputStream inputStream = null;
                            try {
                                inputStream = FileUtils.newInputStream(str6);
                                IOUtils.copyAndCloseInput(inputStream, zipOutputStream);
                                IOUtils.closeSilently(inputStream);
                            } catch (FileNotFoundException e) {
                                IOUtils.closeSilently(inputStream);
                            } catch (Throwable th2) {
                                IOUtils.closeSilently(inputStream);
                                throw th2;
                            }
                            zipOutputStream.closeEntry();
                            if (!z) {
                                this.out.println("Processed: " + str6);
                            }
                        }
                    }
                    if (zipOutputStream != null) {
                        if (0 != 0) {
                            try {
                                zipOutputStream.close();
                            } catch (Throwable th3) {
                                th.addSuppressed(th3);
                            }
                        } else {
                            zipOutputStream.close();
                        }
                    }
                    IOUtils.closeSilently(newOutputStream);
                } catch (Throwable th4) {
                    if (r12 != 0) {
                        if (r13 != 0) {
                            try {
                                r12.close();
                            } catch (Throwable th5) {
                                r13.addSuppressed(th5);
                            }
                        } else {
                            r12.close();
                        }
                    }
                    throw th4;
                }
            } catch (IOException e2) {
                throw DbException.convertIOException(e2, realPath);
            }
        } catch (Throwable th6) {
            IOUtils.closeSilently(null);
            throw th6;
        }
    }
}
