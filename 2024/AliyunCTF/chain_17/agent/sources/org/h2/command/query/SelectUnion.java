package org.h2.command.query;

import java.util.ArrayList;
import java.util.HashSet;
import org.h2.api.ErrorCode;
import org.h2.command.query.Query;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.expression.ExpressionVisitor;
import org.h2.expression.Parameter;
import org.h2.message.DbException;
import org.h2.result.LazyResult;
import org.h2.result.LocalResult;
import org.h2.result.ResultInterface;
import org.h2.result.ResultTarget;
import org.h2.table.Column;
import org.h2.table.ColumnResolver;
import org.h2.table.Table;
import org.h2.table.TableFilter;
import org.h2.value.TypeInfo;
import org.h2.value.Value;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/query/SelectUnion.class */
public class SelectUnion extends Query {
    private final UnionType unionType;
    final Query left;
    final Query right;
    private ForUpdate forUpdate;

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/query/SelectUnion$UnionType.class */
    public enum UnionType {
        UNION,
        UNION_ALL,
        EXCEPT,
        INTERSECT
    }

    public SelectUnion(SessionLocal sessionLocal, UnionType unionType, Query query, Query query2) {
        super(sessionLocal);
        this.unionType = unionType;
        this.left = query;
        this.right = query2;
    }

    @Override // org.h2.command.query.Query
    public boolean isUnion() {
        return true;
    }

    public UnionType getUnionType() {
        return this.unionType;
    }

    public Query getLeft() {
        return this.left;
    }

    public Query getRight() {
        return this.right;
    }

    private Value[] convert(Value[] valueArr, int i) {
        Value[] valueArr2;
        if (i == valueArr.length) {
            valueArr2 = valueArr;
        } else {
            valueArr2 = new Value[i];
        }
        for (int i2 = 0; i2 < i; i2++) {
            valueArr2[i2] = valueArr[i2].convertTo(this.expressions.get(i2).getType(), this.session);
        }
        return valueArr2;
    }

    public LocalResult getEmptyResult() {
        return createLocalResult(this.left.getColumnCount());
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Failed to find 'out' block for switch in B:58:0x01b9. Please report as an issue. */
    @Override // org.h2.command.query.Query
    protected ResultInterface queryWithoutCache(long j, ResultTarget resultTarget) {
        Query.OffsetFetch offsetFetch = getOffsetFetch(j);
        long j2 = offsetFetch.offset;
        long j3 = offsetFetch.fetch;
        boolean z = offsetFetch.fetchPercent;
        if (this.session.getDatabase().getSettings().optimizeInsertFromSelect && this.unionType == UnionType.UNION_ALL && resultTarget != null && this.sort == null && !this.distinct && j3 < 0 && j2 == 0) {
            this.left.query(0L, resultTarget);
            this.right.query(0L, resultTarget);
            return null;
        }
        int columnCount = this.left.getColumnCount();
        if (this.session.isLazyQueryExecution() && this.unionType == UnionType.UNION_ALL && !this.distinct && this.sort == null && !this.randomAccessResult && this.forUpdate == null && j2 == 0 && !z && !this.withTies && isReadOnly() && j3 != 0) {
            LazyResultUnion lazyResultUnion = new LazyResultUnion(this.expressionArray, columnCount);
            if (j3 > 0) {
                lazyResultUnion.setLimit(j3);
            }
            return lazyResultUnion;
        }
        LocalResult createLocalResult = createLocalResult(columnCount);
        if (this.sort != null) {
            createLocalResult.setSortOrder(this.sort);
        }
        if (this.distinct) {
            this.left.setDistinctIfPossible();
            this.right.setDistinctIfPossible();
            createLocalResult.setDistinct();
        }
        switch (this.unionType) {
            case UNION:
            case EXCEPT:
                this.left.setDistinctIfPossible();
                this.right.setDistinctIfPossible();
                createLocalResult.setDistinct();
                break;
            case UNION_ALL:
                break;
            case INTERSECT:
                this.left.setDistinctIfPossible();
                this.right.setDistinctIfPossible();
                break;
            default:
                throw DbException.getInternalError("type=" + this.unionType);
        }
        ResultInterface query = this.left.query(0L);
        ResultInterface query2 = this.right.query(0L);
        query.reset();
        query2.reset();
        switch (this.unionType) {
            case UNION:
            case UNION_ALL:
                while (query.next()) {
                    createLocalResult.addRow(convert(query.currentRow(), columnCount));
                }
                while (query2.next()) {
                    createLocalResult.addRow(convert(query2.currentRow(), columnCount));
                }
                query.close();
                query2.close();
                return finishResult(createLocalResult, j2, j3, z, resultTarget);
            case EXCEPT:
                while (query.next()) {
                    createLocalResult.addRow(convert(query.currentRow(), columnCount));
                }
                while (query2.next()) {
                    createLocalResult.removeDistinct(convert(query2.currentRow(), columnCount));
                }
                query.close();
                query2.close();
                return finishResult(createLocalResult, j2, j3, z, resultTarget);
            case INTERSECT:
                LocalResult createLocalResult2 = createLocalResult(columnCount);
                createLocalResult2.setDistinct();
                while (query.next()) {
                    createLocalResult2.addRow(convert(query.currentRow(), columnCount));
                }
                while (query2.next()) {
                    Value[] convert = convert(query2.currentRow(), columnCount);
                    if (createLocalResult2.containsDistinct(convert)) {
                        createLocalResult.addRow(convert);
                    }
                }
                createLocalResult2.close();
                query.close();
                query2.close();
                return finishResult(createLocalResult, j2, j3, z, resultTarget);
            default:
                throw DbException.getInternalError("type=" + this.unionType);
        }
    }

    private LocalResult createLocalResult(int i) {
        return new LocalResult(this.session, this.expressionArray, i, i);
    }

    @Override // org.h2.command.query.Query
    public void init() {
        if (this.checkInit) {
            throw DbException.getInternalError();
        }
        this.checkInit = true;
        this.left.init();
        this.right.init();
        int columnCount = this.left.getColumnCount();
        if (columnCount != this.right.getColumnCount()) {
            throw DbException.get(ErrorCode.COLUMN_COUNT_DOES_NOT_MATCH);
        }
        ArrayList<Expression> expressions = this.left.getExpressions();
        this.expressions = new ArrayList<>(columnCount);
        for (int i = 0; i < columnCount; i++) {
            this.expressions.add(expressions.get(i));
        }
        this.visibleColumnCount = columnCount;
        if (this.withTies && !hasOrder()) {
            throw DbException.get(ErrorCode.WITH_TIES_WITHOUT_ORDER_BY);
        }
    }

    @Override // org.h2.command.query.Query
    public void prepareExpressions() {
        this.left.prepareExpressions();
        this.right.prepareExpressions();
        int columnCount = this.left.getColumnCount();
        this.expressions = new ArrayList<>(columnCount);
        ArrayList<Expression> expressions = this.left.getExpressions();
        ArrayList<Expression> expressions2 = this.right.getExpressions();
        for (int i = 0; i < columnCount; i++) {
            Expression expression = expressions.get(i);
            this.expressions.add(new ExpressionColumn(this.session.getDatabase(), new Column(expression.getAlias(this.session, i), TypeInfo.getHigherType(expression.getType(), expressions2.get(i).getType()))));
        }
        if (this.orderList != null && initOrder(null, true, null)) {
            prepareOrder(this.orderList, this.expressions.size());
            cleanupOrder();
        }
        this.resultColumnCount = this.expressions.size();
        this.expressionArray = (Expression[]) this.expressions.toArray(new Expression[0]);
    }

    @Override // org.h2.command.query.Query
    public void preparePlan() {
        this.left.preparePlan();
        this.right.preparePlan();
        this.isPrepared = true;
    }

    @Override // org.h2.command.query.Query
    public double getCost() {
        return this.left.getCost() + this.right.getCost();
    }

    @Override // org.h2.command.query.Query
    public HashSet<Table> getTables() {
        HashSet<Table> tables = this.left.getTables();
        tables.addAll(this.right.getTables());
        return tables;
    }

    @Override // org.h2.command.query.Query
    public ForUpdate getForUpdate() {
        return this.forUpdate;
    }

    @Override // org.h2.command.query.Query
    public void setForUpdate(ForUpdate forUpdate) {
        this.left.setForUpdate(forUpdate);
        this.right.setForUpdate(forUpdate);
        this.forUpdate = forUpdate;
    }

    @Override // org.h2.command.query.Query
    public void mapColumns(ColumnResolver columnResolver, int i) {
        this.left.mapColumns(columnResolver, i);
        this.right.mapColumns(columnResolver, i);
    }

    @Override // org.h2.command.query.Query
    public void setEvaluatable(TableFilter tableFilter, boolean z) {
        this.left.setEvaluatable(tableFilter, z);
        this.right.setEvaluatable(tableFilter, z);
    }

    @Override // org.h2.command.query.Query
    public void addGlobalCondition(Parameter parameter, int i, int i2) {
        addParameter(parameter);
        switch (this.unionType) {
            case UNION:
            case UNION_ALL:
            case INTERSECT:
                this.left.addGlobalCondition(parameter, i, i2);
                this.right.addGlobalCondition(parameter, i, i2);
                return;
            case EXCEPT:
                this.left.addGlobalCondition(parameter, i, i2);
                return;
            default:
                throw DbException.getInternalError("type=" + this.unionType);
        }
    }

    @Override // org.h2.command.Prepared
    public String getPlanSQL(int i) {
        StringBuilder append = new StringBuilder().append('(').append(this.left.getPlanSQL(i)).append(')');
        switch (this.unionType) {
            case UNION:
                append.append("\nUNION\n");
                break;
            case EXCEPT:
                append.append("\nEXCEPT\n");
                break;
            case UNION_ALL:
                append.append("\nUNION ALL\n");
                break;
            case INTERSECT:
                append.append("\nINTERSECT\n");
                break;
            default:
                throw DbException.getInternalError("type=" + this.unionType);
        }
        append.append('(').append(this.right.getPlanSQL(i)).append(')');
        appendEndOfQueryToSQL(append, i, (Expression[]) this.expressions.toArray(new Expression[0]));
        if (this.forUpdate != null) {
            this.forUpdate.getSQL(append, i);
        }
        return append.toString();
    }

    @Override // org.h2.command.query.Query
    public boolean isEverything(ExpressionVisitor expressionVisitor) {
        return this.left.isEverything(expressionVisitor) && this.right.isEverything(expressionVisitor);
    }

    @Override // org.h2.command.query.Query
    public void updateAggregate(SessionLocal sessionLocal, int i) {
        this.left.updateAggregate(sessionLocal, i);
        this.right.updateAggregate(sessionLocal, i);
    }

    @Override // org.h2.command.query.Query
    public void fireBeforeSelectTriggers() {
        this.left.fireBeforeSelectTriggers();
        this.right.fireBeforeSelectTriggers();
    }

    @Override // org.h2.command.query.Query
    public boolean allowGlobalConditions() {
        return this.left.allowGlobalConditions() && this.right.allowGlobalConditions();
    }

    @Override // org.h2.command.query.Query
    public boolean isConstantQuery() {
        return super.isConstantQuery() && this.left.isConstantQuery() && this.right.isConstantQuery();
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/query/SelectUnion$LazyResultUnion.class */
    private final class LazyResultUnion extends LazyResult {
        int columnCount;
        ResultInterface l;
        ResultInterface r;
        boolean leftDone;
        boolean rightDone;

        LazyResultUnion(Expression[] expressionArr, int i) {
            super(SelectUnion.this.getSession(), expressionArr);
            this.columnCount = i;
        }

        @Override // org.h2.result.ResultInterface
        public int getVisibleColumnCount() {
            return this.columnCount;
        }

        @Override // org.h2.result.LazyResult
        protected Value[] fetchNextRow() {
            if (this.rightDone) {
                return null;
            }
            if (!this.leftDone) {
                if (this.l == null) {
                    this.l = SelectUnion.this.left.query(0L);
                    this.l.reset();
                }
                if (this.l.next()) {
                    return this.l.currentRow();
                }
                this.leftDone = true;
            }
            if (this.r == null) {
                this.r = SelectUnion.this.right.query(0L);
                this.r.reset();
            }
            if (this.r.next()) {
                return this.r.currentRow();
            }
            this.rightDone = true;
            return null;
        }

        @Override // org.h2.result.LazyResult, org.h2.result.ResultInterface, java.lang.AutoCloseable
        public void close() {
            super.close();
            if (this.l != null) {
                this.l.close();
            }
            if (this.r != null) {
                this.r.close();
            }
        }

        @Override // org.h2.result.LazyResult, org.h2.result.ResultInterface
        public void reset() {
            super.reset();
            if (this.l != null) {
                this.l.reset();
            }
            if (this.r != null) {
                this.r.reset();
            }
            this.leftDone = false;
            this.rightDone = false;
        }
    }
}
