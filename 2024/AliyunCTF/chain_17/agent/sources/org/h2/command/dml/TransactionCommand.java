package org.h2.command.dml;

import org.h2.command.Prepared;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.result.ResultInterface;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/dml/TransactionCommand.class */
public class TransactionCommand extends Prepared {
    private final int type;
    private String savepointName;
    private String transactionName;

    public TransactionCommand(SessionLocal sessionLocal, int i) {
        super(sessionLocal);
        this.type = i;
    }

    public void setSavepointName(String str) {
        this.savepointName = str;
    }

    @Override // org.h2.command.Prepared
    public long update() {
        switch (this.type) {
            case 69:
                this.session.setAutoCommit(true);
                return 0L;
            case 70:
                this.session.setAutoCommit(false);
                return 0L;
            case 71:
                this.session.commit(false);
                return 0L;
            case 72:
                this.session.rollback();
                return 0L;
            case 73:
                this.session.getUser().checkAdmin();
                getDatabase().checkpoint();
                return 0L;
            case 74:
                this.session.addSavepoint(this.savepointName);
                return 0L;
            case 75:
                this.session.rollbackToSavepoint(this.savepointName);
                return 0L;
            case 76:
                this.session.getUser().checkAdmin();
                getDatabase().sync();
                return 0L;
            case 77:
                this.session.prepareCommit(this.transactionName);
                return 0L;
            case 78:
                this.session.getUser().checkAdmin();
                this.session.setPreparedTransaction(this.transactionName, true);
                return 0L;
            case 79:
                this.session.getUser().checkAdmin();
                this.session.setPreparedTransaction(this.transactionName, false);
                return 0L;
            case 80:
            case 82:
            case 84:
                this.session.commit(false);
                break;
            case 81:
                break;
            case 83:
                this.session.begin();
                return 0L;
            default:
                throw DbException.getInternalError("type=" + this.type);
        }
        this.session.getUser().checkAdmin();
        this.session.throttle();
        Database database = getDatabase();
        if (database.setExclusiveSession(this.session, true)) {
            database.setCompactMode(this.type);
            database.setCloseDelay(0);
            this.session.close();
            return 0L;
        }
        return 0L;
    }

    @Override // org.h2.command.Prepared
    public boolean isTransactional() {
        return true;
    }

    @Override // org.h2.command.Prepared
    public boolean needRecompile() {
        return false;
    }

    public void setTransactionName(String str) {
        this.transactionName = str;
    }

    @Override // org.h2.command.Prepared
    public ResultInterface queryMeta() {
        return null;
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return this.type;
    }

    @Override // org.h2.command.Prepared
    public boolean isCacheable() {
        return true;
    }
}
