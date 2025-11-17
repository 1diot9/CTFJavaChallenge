package org.h2.command.dml;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.zip.ZipOutputStream;
import org.h2.api.ErrorCode;
import org.h2.command.Prepared;
import org.h2.engine.Constants;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.message.DbException;
import org.h2.mvstore.db.Store;
import org.h2.result.ResultInterface;
import org.h2.store.FileLister;
import org.h2.store.fs.FileUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/dml/BackupCommand.class */
public class BackupCommand extends Prepared {
    private Expression fileNameExpr;

    public BackupCommand(SessionLocal sessionLocal) {
        super(sessionLocal);
    }

    public void setFileName(Expression expression) {
        this.fileNameExpr = expression;
    }

    @Override // org.h2.command.Prepared
    public long update() {
        String string = this.fileNameExpr.getValue(this.session).getString();
        this.session.getUser().checkAdmin();
        backupTo(string);
        return 0L;
    }

    /* JADX WARN: Failed to calculate best type for var: r10v0 ??
    java.lang.NullPointerException
     */
    /* JADX WARN: Failed to calculate best type for var: r9v0 ??
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
    /* JADX WARN: Not initialized variable reg: 10, insn: 0x00e6: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r10 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]), block:B:52:0x00e6 */
    /* JADX WARN: Not initialized variable reg: 9, insn: 0x00e1: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r9 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) A[TRY_LEAVE], block:B:50:0x00e1 */
    /* JADX WARN: Type inference failed for: r10v0, types: [java.lang.Throwable] */
    /* JADX WARN: Type inference failed for: r9v0, types: [java.io.OutputStream] */
    private void backupTo(String str) {
        Database database = getDatabase();
        if (!database.isPersistent()) {
            throw DbException.get(ErrorCode.DATABASE_IS_NOT_PERSISTENT);
        }
        try {
            try {
                Store store = database.getStore();
                store.flush();
                String name = FileUtils.getName(database.getName());
                OutputStream newOutputStream = FileUtils.newOutputStream(str, false);
                Throwable th = null;
                ZipOutputStream zipOutputStream = new ZipOutputStream(newOutputStream);
                database.flush();
                synchronized (database.getLobSyncObject()) {
                    Iterator<String> it = FileLister.getDatabaseFiles(FileLister.getDir(FileUtils.getParent(database.getDatabasePath())), name, true).iterator();
                    while (it.hasNext()) {
                        if (it.next().endsWith(Constants.SUFFIX_MV_FILE)) {
                            store.getMvStore().getFileStore().backup(zipOutputStream);
                        }
                    }
                }
                zipOutputStream.close();
                if (newOutputStream != null) {
                    if (0 != 0) {
                        try {
                            newOutputStream.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        newOutputStream.close();
                    }
                }
            } finally {
            }
        } catch (IOException e) {
            throw DbException.convertIOException(e, str);
        }
    }

    @Override // org.h2.command.Prepared
    public boolean isTransactional() {
        return true;
    }

    public static String correctFileName(String str) {
        String replace = str.replace('\\', '/');
        if (replace.startsWith("/")) {
            replace = replace.substring(1);
        }
        return replace;
    }

    @Override // org.h2.command.Prepared
    public boolean needRecompile() {
        return false;
    }

    @Override // org.h2.command.Prepared
    public ResultInterface queryMeta() {
        return null;
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return 56;
    }
}
