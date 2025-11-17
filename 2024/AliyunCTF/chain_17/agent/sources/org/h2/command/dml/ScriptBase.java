package org.h2.command.dml;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import org.h2.api.ErrorCode;
import org.h2.command.Prepared;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.engine.SysProperties;
import org.h2.expression.Expression;
import org.h2.message.DbException;
import org.h2.security.SHA256;
import org.h2.store.FileStore;
import org.h2.store.FileStoreInputStream;
import org.h2.store.FileStoreOutputStream;
import org.h2.store.fs.FileUtils;
import org.h2.tools.CompressTool;
import org.h2.util.IOUtils;
import org.h2.util.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/dml/ScriptBase.class */
public abstract class ScriptBase extends Prepared {
    private static final String SCRIPT_SQL = "script.sql";
    protected OutputStream out;
    protected BufferedReader reader;
    private Expression fileNameExpr;
    private Expression password;
    private String fileName;
    private String cipher;
    private FileStore store;
    private String compressionAlgorithm;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ScriptBase(SessionLocal sessionLocal) {
        super(sessionLocal);
    }

    public void setCipher(String str) {
        this.cipher = str;
    }

    private boolean isEncrypted() {
        return this.cipher != null;
    }

    public void setPassword(Expression expression) {
        this.password = expression;
    }

    public void setFileNameExpr(Expression expression) {
        this.fileNameExpr = expression;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String getFileName() {
        if (this.fileNameExpr != null && this.fileName == null) {
            this.fileName = this.fileNameExpr.optimize(this.session).getValue(this.session).getString();
            if (this.fileName == null || StringUtils.isWhitespaceOrEmpty(this.fileName)) {
                this.fileName = SCRIPT_SQL;
            }
            this.fileName = SysProperties.getScriptDirectory() + this.fileName;
        }
        return this.fileName;
    }

    @Override // org.h2.command.Prepared
    public boolean isTransactional() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void deleteStore() {
        String fileName = getFileName();
        if (fileName != null && FileUtils.isRegularFile(fileName)) {
            FileUtils.delete(fileName);
        }
    }

    private void initStore() {
        Database database = getDatabase();
        byte[] bArr = null;
        if (this.cipher != null && this.password != null) {
            bArr = SHA256.getKeyPasswordHash("script", this.password.optimize(this.session).getValue(this.session).getString().toCharArray());
        }
        this.store = FileStore.open(database, getFileName(), "rw", this.cipher, bArr);
        this.store.setCheckedWriting(false);
        this.store.init();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void openOutput() {
        String fileName = getFileName();
        if (fileName == null) {
            return;
        }
        if (isEncrypted()) {
            initStore();
            this.out = new FileStoreOutputStream(this.store, this.compressionAlgorithm);
            this.out = new BufferedOutputStream(this.out, 131072);
        } else {
            try {
                this.out = new BufferedOutputStream(FileUtils.newOutputStream(fileName, false), 4096);
                this.out = CompressTool.wrapOutputStream(this.out, this.compressionAlgorithm, SCRIPT_SQL);
            } catch (IOException e) {
                throw DbException.convertIOException(e, null);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void openInput(Charset charset) {
        InputStream wrapInputStream;
        String fileName = getFileName();
        if (fileName == null) {
            return;
        }
        if (isEncrypted()) {
            initStore();
            wrapInputStream = new FileStoreInputStream(this.store, this.compressionAlgorithm != null, false);
        } else {
            try {
                wrapInputStream = CompressTool.wrapInputStream(FileUtils.newInputStream(fileName), this.compressionAlgorithm, SCRIPT_SQL);
                if (wrapInputStream == null) {
                    throw DbException.get(ErrorCode.FILE_NOT_FOUND_1, "script.sql in " + fileName);
                }
            } catch (IOException e) {
                throw DbException.convertIOException(e, fileName);
            }
        }
        this.reader = new BufferedReader(new InputStreamReader(wrapInputStream, charset), 4096);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void closeIO() {
        IOUtils.closeSilently(this.out);
        this.out = null;
        IOUtils.closeSilently(this.reader);
        this.reader = null;
        if (this.store != null) {
            this.store.closeSilently();
            this.store = null;
        }
    }

    @Override // org.h2.command.Prepared
    public boolean needRecompile() {
        return false;
    }

    public void setCompressionAlgorithm(String str) {
        this.compressionAlgorithm = str;
    }
}
