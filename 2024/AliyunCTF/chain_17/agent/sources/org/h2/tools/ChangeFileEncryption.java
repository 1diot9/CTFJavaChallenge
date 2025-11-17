package org.h2.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import org.h2.engine.Constants;
import org.h2.message.DbException;
import org.h2.mvstore.MVStore;
import org.h2.store.FileLister;
import org.h2.store.fs.FilePath;
import org.h2.store.fs.FileUtils;
import org.h2.store.fs.encrypt.FileEncrypt;
import org.h2.store.fs.encrypt.FilePathEncrypt;
import org.h2.util.Tool;
import org.springframework.aop.framework.autoproxy.target.QuickTargetSourceCreator;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/tools/ChangeFileEncryption.class */
public class ChangeFileEncryption extends Tool {
    private String directory;
    private String cipherType;
    private byte[] decryptKey;
    private byte[] encryptKey;

    public static void main(String... strArr) {
        try {
            new ChangeFileEncryption().runTool(strArr);
        } catch (SQLException e) {
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

    @Override // org.h2.util.Tool
    public void runTool(String... strArr) throws SQLException {
        String str = ".";
        String str2 = null;
        char[] cArr = null;
        char[] cArr2 = null;
        String str3 = null;
        boolean z = false;
        int i = 0;
        while (strArr != null && i < strArr.length) {
            String str4 = strArr[i];
            if (str4.equals("-dir")) {
                i++;
                str = strArr[i];
            } else if (str4.equals("-cipher")) {
                i++;
                str2 = strArr[i];
            } else if (str4.equals("-db")) {
                i++;
                str3 = strArr[i];
            } else if (str4.equals("-decrypt")) {
                i++;
                cArr = strArr[i].toCharArray();
            } else if (str4.equals("-encrypt")) {
                i++;
                cArr2 = strArr[i].toCharArray();
            } else if (str4.equals("-quiet")) {
                z = true;
            } else {
                if (str4.equals("-help") || str4.equals("-?")) {
                    showUsage();
                    return;
                }
                showUsageAndThrowUnsupportedOption(str4);
            }
            i++;
        }
        if ((cArr2 == null && cArr == null) || str2 == null) {
            showUsage();
            throw new SQLException("Encryption or decryption password not set, or cipher not set");
        }
        try {
            process(str, str3, str2, cArr, cArr2, z);
        } catch (Exception e) {
            throw DbException.toSQLException(e);
        }
    }

    public static void execute(String str, String str2, String str3, char[] cArr, char[] cArr2, boolean z) throws SQLException {
        try {
            new ChangeFileEncryption().process(str, str2, str3, cArr, cArr2, z);
        } catch (Exception e) {
            throw DbException.toSQLException(e);
        }
    }

    private void process(String str, String str2, String str3, char[] cArr, char[] cArr2, boolean z) throws SQLException {
        String dir = FileLister.getDir(str);
        ChangeFileEncryption changeFileEncryption = new ChangeFileEncryption();
        if (cArr2 != null) {
            for (char c : cArr2) {
                if (c == ' ') {
                    throw new SQLException("The file password may not contain spaces");
                }
            }
            changeFileEncryption.encryptKey = FilePathEncrypt.getPasswordBytes(cArr2);
        }
        if (cArr != null) {
            changeFileEncryption.decryptKey = FilePathEncrypt.getPasswordBytes(cArr);
        }
        changeFileEncryption.out = this.out;
        changeFileEncryption.directory = dir;
        changeFileEncryption.cipherType = str3;
        FileLister.tryUnlockDatabase(FileLister.getDatabaseFiles(dir, str2, true), "encryption");
        ArrayList<String> databaseFiles = FileLister.getDatabaseFiles(dir, str2, false);
        if (databaseFiles.isEmpty() && !z) {
            printNoDatabaseFilesFound(dir, str2);
        }
        Iterator<String> it = databaseFiles.iterator();
        while (it.hasNext()) {
            String next = it.next();
            String str4 = dir + "/temp.db";
            FileUtils.delete(str4);
            FileUtils.move(next, str4);
            FileUtils.move(str4, next);
        }
        Iterator<String> it2 = databaseFiles.iterator();
        while (it2.hasNext()) {
            String next2 = it2.next();
            if (!FileUtils.isDirectory(next2)) {
                changeFileEncryption.process(next2, z, cArr);
            }
        }
    }

    private void process(String str, boolean z, char[] cArr) throws SQLException {
        if (str.endsWith(Constants.SUFFIX_MV_FILE)) {
            try {
                copyMvStore(str, z, cArr);
            } catch (IOException e) {
                throw DbException.convertIOException(e, "Error encrypting / decrypting file " + str);
            }
        }
    }

    /* JADX WARN: Failed to calculate best type for var: r16v1 ??
    java.lang.NullPointerException
     */
    /* JADX WARN: Failed to calculate best type for var: r17v0 ??
    java.lang.NullPointerException
     */
    /* JADX WARN: Finally extract failed */
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
    /* JADX WARN: Not initialized variable reg: 16, insn: 0x01ab: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r16 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) A[TRY_LEAVE], block:B:92:0x01ab */
    /* JADX WARN: Not initialized variable reg: 17, insn: 0x01b0: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r17 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]), block:B:94:0x01b0 */
    /* JADX WARN: Type inference failed for: r16v1, types: [java.io.InputStream] */
    /* JADX WARN: Type inference failed for: r17v0, types: [java.lang.Throwable] */
    private void copyMvStore(String str, boolean z, char[] cArr) throws IOException, SQLException {
        ?? r16;
        ?? r17;
        if (FileUtils.isDirectory(str)) {
            return;
        }
        try {
            new MVStore.Builder().fileName(str).readOnly().encryptionKey(cArr).open().close();
            String str2 = this.directory + "/temp.db";
            FileChannel fileChannel = getFileChannel(str, "r", this.decryptKey);
            Throwable th = null;
            try {
                try {
                    InputStream newInputStream = Channels.newInputStream(fileChannel);
                    Throwable th2 = null;
                    FileUtils.delete(str2);
                    OutputStream newOutputStream = Channels.newOutputStream(getFileChannel(str2, "rw", this.encryptKey));
                    Throwable th3 = null;
                    try {
                        byte[] bArr = new byte[4096];
                        long size = fileChannel.size();
                        long nanoTime = System.nanoTime();
                        while (size > 0) {
                            if (!z) {
                                if (System.nanoTime() - nanoTime > TimeUnit.SECONDS.toNanos(1L)) {
                                    this.out.println(str + ": " + (100 - ((100 * size) / size)) + QuickTargetSourceCreator.PREFIX_THREAD_LOCAL);
                                    nanoTime = System.nanoTime();
                                }
                            }
                            int read = newInputStream.read(bArr, 0, (int) Math.min(bArr.length, size));
                            newOutputStream.write(bArr, 0, read);
                            size -= read;
                        }
                        if (newOutputStream != null) {
                            if (0 != 0) {
                                try {
                                    newOutputStream.close();
                                } catch (Throwable th4) {
                                    th3.addSuppressed(th4);
                                }
                            } else {
                                newOutputStream.close();
                            }
                        }
                        if (newInputStream != null) {
                            if (0 != 0) {
                                try {
                                    newInputStream.close();
                                } catch (Throwable th5) {
                                    th2.addSuppressed(th5);
                                }
                            } else {
                                newInputStream.close();
                            }
                        }
                        FileUtils.delete(str);
                        FileUtils.move(str2, str);
                    } catch (Throwable th6) {
                        if (newOutputStream != null) {
                            if (0 != 0) {
                                try {
                                    newOutputStream.close();
                                } catch (Throwable th7) {
                                    th3.addSuppressed(th7);
                                }
                            } else {
                                newOutputStream.close();
                            }
                        }
                        throw th6;
                    }
                } catch (Throwable th8) {
                    if (r16 != 0) {
                        if (r17 != 0) {
                            try {
                                r16.close();
                            } catch (Throwable th9) {
                                r17.addSuppressed(th9);
                            }
                        } else {
                            r16.close();
                        }
                    }
                    throw th8;
                }
            } finally {
                if (fileChannel != null) {
                    if (0 != 0) {
                        try {
                            fileChannel.close();
                        } catch (Throwable th10) {
                            th.addSuppressed(th10);
                        }
                    } else {
                        fileChannel.close();
                    }
                }
            }
        } catch (IllegalStateException e) {
            throw new SQLException("error decrypting file " + str, e);
        }
    }

    private static FileChannel getFileChannel(String str, String str2, byte[] bArr) throws IOException {
        FileChannel open = FilePath.get(str).open(str2);
        if (bArr != null) {
            open = new FileEncrypt(str, bArr, open);
        }
        return open;
    }
}
