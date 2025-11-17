package org.h2.command;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.h2.api.ErrorCode;
import org.h2.engine.Database;
import org.h2.engine.DbObject;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.Parameter;
import org.h2.message.DbException;
import org.h2.message.Trace;
import org.h2.result.ResultInterface;
import org.h2.table.TableView;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/Prepared.class */
public abstract class Prepared {
    protected SessionLocal session;
    protected String sqlStatement;
    protected ArrayList<Token> sqlTokens;
    protected ArrayList<Parameter> parameters;
    private boolean withParamValues;
    protected boolean prepareAlways;
    private Command command;
    private int persistedObjectId;
    private long currentRowNumber;
    private int rowScanCount;
    private List<TableView> cteCleanups;
    protected boolean create = true;
    private long modificationMetaId = getDatabase().getModificationMetaId();

    public abstract boolean isTransactional();

    public abstract ResultInterface queryMeta();

    public abstract int getType();

    public Prepared(SessionLocal sessionLocal) {
        this.session = sessionLocal;
    }

    public boolean isReadOnly() {
        return false;
    }

    public boolean needRecompile() {
        Database database = getDatabase();
        if (database == null) {
            throw DbException.get(ErrorCode.CONNECTION_BROKEN_1, "database closed");
        }
        return this.prepareAlways || this.modificationMetaId < database.getModificationMetaId() || database.getSettings().recompileAlways;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public long getModificationMetaId() {
        return this.modificationMetaId;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setModificationMetaId(long j) {
        this.modificationMetaId = j;
    }

    public void setParameterList(ArrayList<Parameter> arrayList) {
        this.parameters = arrayList;
    }

    public ArrayList<Parameter> getParameters() {
        return this.parameters;
    }

    public boolean isWithParamValues() {
        return this.withParamValues;
    }

    public void setWithParamValues(boolean z) {
        this.withParamValues = z;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void checkParameters() {
        if (this.persistedObjectId < 0) {
            this.persistedObjectId ^= -1;
        }
        if (this.parameters != null) {
            Iterator<Parameter> it = this.parameters.iterator();
            while (it.hasNext()) {
                it.next().checkSet();
            }
        }
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public boolean isQuery() {
        return false;
    }

    public void prepare() {
    }

    public long update() {
        throw DbException.get(ErrorCode.METHOD_NOT_ALLOWED_FOR_QUERY);
    }

    public ResultInterface query(long j) {
        throw DbException.get(ErrorCode.METHOD_ONLY_ALLOWED_FOR_QUERY);
    }

    public final void setSQL(String str, ArrayList<Token> arrayList) {
        this.sqlStatement = str;
        this.sqlTokens = arrayList;
    }

    public final String getSQL() {
        return this.sqlStatement;
    }

    public final ArrayList<Token> getSQLTokens() {
        return this.sqlTokens;
    }

    public int getPersistedObjectId() {
        int i = this.persistedObjectId;
        if (i >= 0) {
            return i;
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getObjectId() {
        int i = this.persistedObjectId;
        if (i == 0) {
            i = getDatabase().allocateObjectId();
        } else if (i < 0) {
            throw DbException.getInternalError("Prepared.getObjectId() was called before");
        }
        this.persistedObjectId ^= -1;
        return i;
    }

    public String getPlanSQL(int i) {
        return null;
    }

    public void checkCanceled() {
        this.session.checkCanceled();
        Command currentCommand = this.command != null ? this.command : this.session.getCurrentCommand();
        if (currentCommand != null) {
            currentCommand.checkCanceled();
        }
    }

    public void setPersistedObjectId(int i) {
        this.persistedObjectId = i;
        this.create = false;
    }

    public void setSession(SessionLocal sessionLocal) {
        this.session = sessionLocal;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void trace(Database database, long j, long j2) {
        if (this.session.getTrace().isInfoEnabled() && j > 0) {
            long nanoTime = System.nanoTime() - j;
            this.session.getTrace().infoSQL(this.sqlStatement, Trace.formatParams(this.parameters), j2, nanoTime / 1000000);
        }
        if (database != null && database.getQueryStatistics() && j != 0) {
            database.getQueryStatisticsData().update(toString(), System.nanoTime() - j, j2);
        }
    }

    public void setPrepareAlways(boolean z) {
        this.prepareAlways = z;
    }

    public void setCurrentRowNumber(long j) {
        int i = this.rowScanCount + 1;
        this.rowScanCount = i;
        if ((i & 127) == 0) {
            checkCanceled();
        }
        this.currentRowNumber = j;
        setProgress();
    }

    public long getCurrentRowNumber() {
        return this.currentRowNumber;
    }

    private void setProgress() {
        if ((this.currentRowNumber & 127) == 0) {
            getDatabase().setProgress(7, this.sqlStatement, this.currentRowNumber, 0L);
        }
    }

    public String toString() {
        return this.sqlStatement;
    }

    public static String getSimpleSQL(Expression[] expressionArr) {
        return Expression.writeExpressions(new StringBuilder(), expressionArr, 3).toString();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final DbException setRow(DbException dbException, long j, String str) {
        StringBuilder sb = new StringBuilder();
        if (this.sqlStatement != null) {
            sb.append(this.sqlStatement);
        }
        sb.append(" -- ");
        if (j > 0) {
            sb.append("row #").append(j + 1).append(' ');
        }
        sb.append('(').append(str).append(')');
        return dbException.addSQL(sb.toString());
    }

    public boolean isCacheable() {
        return false;
    }

    public List<TableView> getCteCleanups() {
        return this.cteCleanups;
    }

    public void setCteCleanups(List<TableView> list) {
        this.cteCleanups = list;
    }

    public final SessionLocal getSession() {
        return this.session;
    }

    public void collectDependencies(HashSet<DbObject> hashSet) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final Database getDatabase() {
        return this.session.getDatabase();
    }

    public boolean isRetryable() {
        return true;
    }
}
