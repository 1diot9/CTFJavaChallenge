package org.h2.command;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.h2.api.ErrorCode;
import org.h2.command.dml.DataChangeStatement;
import org.h2.engine.Database;
import org.h2.engine.DbObject;
import org.h2.engine.DbSettings;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.expression.Parameter;
import org.h2.expression.ParameterInterface;
import org.h2.index.Index;
import org.h2.message.DbException;
import org.h2.result.LocalResult;
import org.h2.result.ResultInterface;
import org.h2.result.ResultTarget;
import org.h2.result.ResultWithGeneratedKeys;
import org.h2.table.Column;
import org.h2.table.DataChangeDeltaTable;
import org.h2.table.Table;
import org.h2.table.TableView;
import org.h2.util.StringUtils;
import org.h2.util.Utils;
import org.h2.value.Value;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/CommandContainer.class */
public class CommandContainer extends Command {
    private Prepared prepared;
    private boolean readOnlyKnown;
    private boolean readOnly;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/CommandContainer$GeneratedKeysCollector.class */
    public static final class GeneratedKeysCollector implements ResultTarget {
        private final int[] indexes;
        private final LocalResult result;

        GeneratedKeysCollector(int[] iArr, LocalResult localResult) {
            this.indexes = iArr;
            this.result = localResult;
        }

        @Override // org.h2.result.ResultTarget
        public void limitsWereApplied() {
        }

        @Override // org.h2.result.ResultTarget
        public long getRowCount() {
            return 0L;
        }

        @Override // org.h2.result.ResultTarget
        public void addRow(Value... valueArr) {
            int length = this.indexes.length;
            Value[] valueArr2 = new Value[length];
            for (int i = 0; i < length; i++) {
                valueArr2[i] = valueArr[this.indexes[i]];
            }
            this.result.addRow(valueArr2);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void clearCTE(SessionLocal sessionLocal, Prepared prepared) {
        List<TableView> cteCleanups = prepared.getCteCleanups();
        if (cteCleanups != null) {
            clearCTE(sessionLocal, cteCleanups);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void clearCTE(SessionLocal sessionLocal, List<TableView> list) {
        for (TableView tableView : list) {
            if (tableView.getName() != null) {
                sessionLocal.removeLocalTempTable(tableView);
            }
        }
    }

    public CommandContainer(SessionLocal sessionLocal, String str, Prepared prepared) {
        super(sessionLocal, str);
        prepared.setCommand(this);
        this.prepared = prepared;
    }

    @Override // org.h2.command.Command, org.h2.command.CommandInterface
    public ArrayList<? extends ParameterInterface> getParameters() {
        ArrayList<Parameter> parameters = this.prepared.getParameters();
        if (parameters.size() > 0 && this.prepared.isWithParamValues()) {
            parameters = new ArrayList<>();
        }
        return parameters;
    }

    @Override // org.h2.command.Command
    public boolean isTransactional() {
        return this.prepared.isTransactional();
    }

    @Override // org.h2.command.Command, org.h2.command.CommandInterface
    public boolean isQuery() {
        return this.prepared.isQuery();
    }

    private void recompileIfRequired() {
        if (this.prepared.needRecompile()) {
            this.prepared.setModificationMetaId(0L);
            String sql = this.prepared.getSQL();
            ArrayList<Token> sQLTokens = this.prepared.getSQLTokens();
            Parser parser = new Parser(this.session);
            parser.setSuppliedParameters(this.prepared.getParameters());
            this.prepared = parser.parse(sql, sQLTokens);
            long modificationMetaId = this.prepared.getModificationMetaId();
            this.prepared.setModificationMetaId(0L);
            this.prepared.prepare();
            this.prepared.setModificationMetaId(modificationMetaId);
        }
    }

    @Override // org.h2.command.Command
    public ResultWithGeneratedKeys update(Object obj) {
        ResultWithGeneratedKeys of;
        recompileIfRequired();
        Database database = getDatabase();
        setProgress(database, 5);
        start();
        this.prepared.checkParameters();
        if (obj != null && !Boolean.FALSE.equals(obj)) {
            if ((this.prepared instanceof DataChangeStatement) && this.prepared.getType() != 58) {
                of = executeUpdateWithGeneratedKeys((DataChangeStatement) this.prepared, obj);
            } else {
                of = new ResultWithGeneratedKeys.WithKeys(this.prepared.update(), new LocalResult());
            }
        } else {
            of = ResultWithGeneratedKeys.of(this.prepared.update());
        }
        this.prepared.trace(database, this.startTimeNanos, of.getUpdateCount());
        setProgress(database, 6);
        return of;
    }

    private ResultWithGeneratedKeys executeUpdateWithGeneratedKeys(DataChangeStatement dataChangeStatement, Object obj) {
        ArrayList arrayList;
        Expression effectiveDefaultExpression;
        Database database = getDatabase();
        Table table = dataChangeStatement.getTable();
        if (Boolean.TRUE.equals(obj)) {
            arrayList = Utils.newSmallArrayList();
            Column[] columns = table.getColumns();
            Index findPrimaryKey = table.findPrimaryKey();
            for (Column column : columns) {
                if (column.isIdentity() || (((effectiveDefaultExpression = column.getEffectiveDefaultExpression()) != null && !effectiveDefaultExpression.isConstant()) || (findPrimaryKey != null && findPrimaryKey.getColumnIndex(column) >= 0))) {
                    arrayList.add(new ExpressionColumn(database, column));
                }
            }
        } else if (obj instanceof int[]) {
            int[] iArr = (int[]) obj;
            Column[] columns2 = table.getColumns();
            int length = columns2.length;
            arrayList = new ArrayList(iArr.length);
            for (int i : iArr) {
                if (i < 1 || i > length) {
                    throw DbException.get(ErrorCode.COLUMN_NOT_FOUND_1, "Index: " + i);
                }
                arrayList.add(new ExpressionColumn(database, columns2[i - 1]));
            }
        } else if (obj instanceof String[]) {
            String[] strArr = (String[]) obj;
            arrayList = new ArrayList(strArr.length);
            for (String str : strArr) {
                Column findColumn = table.findColumn(str);
                if (findColumn == null) {
                    DbSettings settings = database.getSettings();
                    if (settings.databaseToUpper) {
                        findColumn = table.findColumn(StringUtils.toUpperEnglish(str));
                    } else if (settings.databaseToLower) {
                        findColumn = table.findColumn(StringUtils.toLowerEnglish(str));
                    }
                    if (findColumn == null) {
                        for (Column column2 : table.getColumns()) {
                            if (column2.getName().equalsIgnoreCase(str)) {
                                findColumn = column2;
                            }
                        }
                        throw DbException.get(ErrorCode.COLUMN_NOT_FOUND_1, str);
                    }
                    continue;
                }
                arrayList.add(new ExpressionColumn(database, findColumn));
            }
        } else {
            throw DbException.getInternalError();
        }
        int size = arrayList.size();
        if (size == 0) {
            return new ResultWithGeneratedKeys.WithKeys(dataChangeStatement.update(), new LocalResult());
        }
        int[] iArr2 = new int[size];
        ExpressionColumn[] expressionColumnArr = (ExpressionColumn[]) arrayList.toArray(new ExpressionColumn[0]);
        for (int i2 = 0; i2 < size; i2++) {
            iArr2[i2] = expressionColumnArr[i2].getColumn().getColumnId();
        }
        LocalResult localResult = new LocalResult(this.session, expressionColumnArr, size, size);
        return new ResultWithGeneratedKeys.WithKeys(dataChangeStatement.update(new GeneratedKeysCollector(iArr2, localResult), DataChangeDeltaTable.ResultOption.FINAL), localResult);
    }

    @Override // org.h2.command.Command
    public ResultInterface query(long j) {
        recompileIfRequired();
        Database database = getDatabase();
        setProgress(database, 5);
        start();
        this.prepared.checkParameters();
        ResultInterface query = this.prepared.query(j);
        this.prepared.trace(database, this.startTimeNanos, query.isLazy() ? 0L : query.getRowCount());
        setProgress(database, 6);
        return query;
    }

    @Override // org.h2.command.Command, org.h2.command.CommandInterface
    public void stop() {
        super.stop();
        clearCTE(this.session, this.prepared);
    }

    @Override // org.h2.command.Command
    public boolean canReuse() {
        return super.canReuse() && this.prepared.getCteCleanups() == null;
    }

    @Override // org.h2.command.Command
    public boolean isReadOnly() {
        if (!this.readOnlyKnown) {
            this.readOnly = this.prepared.isReadOnly();
            this.readOnlyKnown = true;
        }
        return this.readOnly;
    }

    @Override // org.h2.command.Command
    public ResultInterface queryMeta() {
        return this.prepared.queryMeta();
    }

    @Override // org.h2.command.Command
    public boolean isCacheable() {
        return this.prepared.isCacheable();
    }

    @Override // org.h2.command.CommandInterface
    public int getCommandType() {
        return this.prepared.getType();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void clearCTE() {
        clearCTE(this.session, this.prepared);
    }

    @Override // org.h2.command.Command
    public Set<DbObject> getDependencies() {
        HashSet<DbObject> hashSet = new HashSet<>();
        this.prepared.collectDependencies(hashSet);
        return hashSet;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.h2.command.Command
    public boolean isRetryable() {
        return this.prepared.isRetryable();
    }
}
