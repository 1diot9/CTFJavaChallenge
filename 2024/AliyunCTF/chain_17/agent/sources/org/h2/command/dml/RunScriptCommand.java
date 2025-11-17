package org.h2.command.dml;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.h2.command.CommandContainer;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.message.DbException;
import org.h2.result.ResultInterface;
import org.h2.util.ScriptReader;
import org.h2.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/dml/RunScriptCommand.class */
public class RunScriptCommand extends ScriptBase {
    private static final char UTF8_BOM = 65279;
    private Charset charset;
    private boolean quirksMode;
    private boolean variableBinary;
    private boolean from1X;

    @Override // org.h2.command.dml.ScriptBase
    public /* bridge */ /* synthetic */ void setCompressionAlgorithm(String str) {
        super.setCompressionAlgorithm(str);
    }

    @Override // org.h2.command.dml.ScriptBase, org.h2.command.Prepared
    public /* bridge */ /* synthetic */ boolean needRecompile() {
        return super.needRecompile();
    }

    @Override // org.h2.command.dml.ScriptBase, org.h2.command.Prepared
    public /* bridge */ /* synthetic */ boolean isTransactional() {
        return super.isTransactional();
    }

    @Override // org.h2.command.dml.ScriptBase
    public /* bridge */ /* synthetic */ void setFileNameExpr(Expression expression) {
        super.setFileNameExpr(expression);
    }

    @Override // org.h2.command.dml.ScriptBase
    public /* bridge */ /* synthetic */ void setPassword(Expression expression) {
        super.setPassword(expression);
    }

    @Override // org.h2.command.dml.ScriptBase
    public /* bridge */ /* synthetic */ void setCipher(String str) {
        super.setCipher(str);
    }

    public RunScriptCommand(SessionLocal sessionLocal) {
        super(sessionLocal);
        this.charset = StandardCharsets.UTF_8;
    }

    @Override // org.h2.command.Prepared
    public long update() {
        this.session.getUser().checkAdmin();
        int i = 0;
        boolean isQuirksMode = this.session.isQuirksMode();
        boolean isVariableBinary = this.session.isVariableBinary();
        try {
            try {
                openInput(this.charset);
                this.reader.mark(1);
                if (this.reader.read() != UTF8_BOM) {
                    this.reader.reset();
                }
                if (this.quirksMode) {
                    this.session.setQuirksMode(true);
                }
                if (this.variableBinary) {
                    this.session.setVariableBinary(true);
                }
                ScriptReader scriptReader = new ScriptReader(this.reader);
                while (true) {
                    String readStatement = scriptReader.readStatement();
                    if (readStatement == null) {
                        break;
                    }
                    execute(readStatement);
                    i++;
                    if ((i & 127) == 0) {
                        checkCanceled();
                    }
                }
                scriptReader.close();
                if (this.quirksMode) {
                    this.session.setQuirksMode(isQuirksMode);
                }
                if (this.variableBinary) {
                    this.session.setVariableBinary(isVariableBinary);
                }
                closeIO();
                return i;
            } catch (IOException e) {
                throw DbException.convertIOException(e, null);
            }
        } catch (Throwable th) {
            if (this.quirksMode) {
                this.session.setQuirksMode(isQuirksMode);
            }
            if (this.variableBinary) {
                this.session.setVariableBinary(isVariableBinary);
            }
            closeIO();
            throw th;
        }
    }

    private void execute(String str) {
        int indexOf;
        if (this.from1X) {
            str = str.trim();
            if (str.startsWith("--")) {
                int i = 2;
                int length = str.length();
                while (i < length) {
                    int i2 = i;
                    i++;
                    char charAt = str.charAt(i2);
                    if (charAt == '\n' || charAt == '\r') {
                        str = StringUtils.trimSubstring(str, i);
                    }
                }
                return;
            }
            if (str.startsWith("INSERT INTO SYSTEM_LOB_STREAM VALUES(") && (indexOf = str.indexOf(", NULL, '")) >= 0) {
                str = new StringBuilder(str.length() + 1).append((CharSequence) str, 0, indexOf + 8).append("X'").append((CharSequence) str, indexOf + 9, str.length()).toString();
            }
        }
        try {
            CommandContainer commandContainer = new CommandContainer(this.session, str, this.session.prepare(str));
            if (commandContainer.isQuery()) {
                commandContainer.executeQuery(0L, false);
            } else {
                commandContainer.executeUpdate(null);
            }
        } catch (DbException e) {
            throw e.addSQL(str);
        }
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public void setQuirksMode(boolean z) {
        this.quirksMode = z;
    }

    public void setVariableBinary(boolean z) {
        this.variableBinary = z;
    }

    public void setFrom1X() {
        this.from1X = true;
        this.quirksMode = true;
        this.variableBinary = true;
    }

    @Override // org.h2.command.Prepared
    public ResultInterface queryMeta() {
        return null;
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return 64;
    }
}
