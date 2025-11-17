package org.h2.command.query;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import org.h2.api.ErrorCode;
import org.h2.command.query.Query;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.expression.ExpressionList;
import org.h2.expression.ExpressionVisitor;
import org.h2.expression.Parameter;
import org.h2.message.DbException;
import org.h2.result.LocalResult;
import org.h2.result.ResultInterface;
import org.h2.result.ResultTarget;
import org.h2.table.Column;
import org.h2.table.ColumnResolver;
import org.h2.table.Table;
import org.h2.table.TableFilter;
import org.h2.table.TableValueConstructorTable;
import org.h2.value.TypeInfo;
import org.h2.value.Value;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/query/TableValueConstructor.class */
public class TableValueConstructor extends Query {
    private final ArrayList<ArrayList<Expression>> rows;
    TableValueConstructorTable table;
    private TableValueColumnResolver columnResolver;
    private double cost;

    public TableValueConstructor(SessionLocal sessionLocal, ArrayList<ArrayList<Expression>> arrayList) {
        super(sessionLocal);
        this.rows = arrayList;
        int size = arrayList.get(0).size();
        this.visibleColumnCount = size;
        if (size > 16384) {
            throw DbException.get(ErrorCode.TOO_MANY_COLUMNS_1, "16384");
        }
        Iterator<ArrayList<Expression>> it = arrayList.iterator();
        while (it.hasNext()) {
            Iterator<Expression> it2 = it.next().iterator();
            while (it2.hasNext()) {
                if (!it2.next().isConstant()) {
                    return;
                }
            }
        }
        createTable();
    }

    public static void getVisibleResult(SessionLocal sessionLocal, ResultTarget resultTarget, Column[] columnArr, ArrayList<ArrayList<Expression>> arrayList) {
        int length = columnArr.length;
        Iterator<ArrayList<Expression>> it = arrayList.iterator();
        while (it.hasNext()) {
            ArrayList<Expression> next = it.next();
            Value[] valueArr = new Value[length];
            for (int i = 0; i < length; i++) {
                valueArr[i] = next.get(i).getValue(sessionLocal).convertTo(columnArr[i].getType(), sessionLocal);
            }
            resultTarget.addRow(valueArr);
        }
    }

    public static void getValuesSQL(StringBuilder sb, int i, ArrayList<ArrayList<Expression>> arrayList) {
        sb.append("VALUES ");
        int size = arrayList.size();
        for (int i2 = 0; i2 < size; i2++) {
            if (i2 > 0) {
                sb.append(", ");
            }
            Expression.writeExpressions(sb.append('('), arrayList.get(i2), i).append(')');
        }
    }

    @Override // org.h2.command.query.Query
    public boolean isUnion() {
        return false;
    }

    @Override // org.h2.command.query.Query
    protected ResultInterface queryWithoutCache(long j, ResultTarget resultTarget) {
        Query.OffsetFetch offsetFetch = getOffsetFetch(j);
        long j2 = offsetFetch.offset;
        long j3 = offsetFetch.fetch;
        boolean z = offsetFetch.fetchPercent;
        int i = this.visibleColumnCount;
        int i2 = this.resultColumnCount;
        LocalResult localResult = new LocalResult(this.session, this.expressionArray, i, i2);
        if (this.sort != null) {
            localResult.setSortOrder(this.sort);
        }
        if (this.distinct) {
            localResult.setDistinct();
        }
        Column[] columns = this.table.getColumns();
        if (i == i2) {
            getVisibleResult(this.session, localResult, columns, this.rows);
        } else {
            Iterator<ArrayList<Expression>> it = this.rows.iterator();
            while (it.hasNext()) {
                ArrayList<Expression> next = it.next();
                Value[] valueArr = new Value[i2];
                for (int i3 = 0; i3 < i; i3++) {
                    valueArr[i3] = next.get(i3).getValue(this.session).convertTo(columns[i3].getType(), this.session);
                }
                this.columnResolver.currentRow = valueArr;
                for (int i4 = i; i4 < i2; i4++) {
                    valueArr[i4] = this.expressionArray[i4].getValue(this.session);
                }
                localResult.addRow(valueArr);
            }
            this.columnResolver.currentRow = null;
        }
        return finishResult(localResult, j2, j3, z, resultTarget);
    }

    @Override // org.h2.command.query.Query
    public void init() {
        if (this.checkInit) {
            throw DbException.getInternalError();
        }
        this.checkInit = true;
        if (this.withTies && !hasOrder()) {
            throw DbException.get(ErrorCode.WITH_TIES_WITHOUT_ORDER_BY);
        }
    }

    @Override // org.h2.command.query.Query
    public void prepareExpressions() {
        if (this.columnResolver == null) {
            createTable();
        }
        if (this.orderList != null) {
            ArrayList<String> arrayList = new ArrayList<>();
            Iterator<Expression> it = this.expressions.iterator();
            while (it.hasNext()) {
                arrayList.add(it.next().getSQL(0, 2));
            }
            if (initOrder(arrayList, false, null)) {
                prepareOrder(this.orderList, this.expressions.size());
            }
        }
        this.resultColumnCount = this.expressions.size();
        for (int i = 0; i < this.resultColumnCount; i++) {
            this.expressions.get(i).mapColumns(this.columnResolver, 0, 0);
        }
        for (int i2 = this.visibleColumnCount; i2 < this.resultColumnCount; i2++) {
            this.expressions.set(i2, this.expressions.get(i2).optimize(this.session));
        }
        if (this.sort != null) {
            cleanupOrder();
        }
        this.expressionArray = (Expression[]) this.expressions.toArray(new Expression[0]);
    }

    @Override // org.h2.command.query.Query
    public void preparePlan() {
        double d = 0.0d;
        int i = this.visibleColumnCount;
        Iterator<ArrayList<Expression>> it = this.rows.iterator();
        while (it.hasNext()) {
            ArrayList<Expression> next = it.next();
            for (int i2 = 0; i2 < i; i2++) {
                d += next.get(i2).getCost();
            }
        }
        this.cost = d + this.rows.size();
        this.isPrepared = true;
    }

    private void createTable() {
        int size = this.rows.size();
        ArrayList<Expression> arrayList = this.rows.get(0);
        int size2 = arrayList.size();
        TypeInfo[] typeInfoArr = new TypeInfo[size2];
        for (int i = 0; i < size2; i++) {
            Expression optimize = arrayList.get(i).optimize(this.session);
            arrayList.set(i, optimize);
            TypeInfo type = optimize.getType();
            if (type.getValueType() == -1) {
                type = TypeInfo.TYPE_VARCHAR;
            }
            typeInfoArr[i] = type;
        }
        for (int i2 = 1; i2 < size; i2++) {
            ArrayList<Expression> arrayList2 = this.rows.get(i2);
            for (int i3 = 0; i3 < size2; i3++) {
                Expression optimize2 = arrayList2.get(i3).optimize(this.session);
                arrayList2.set(i3, optimize2);
                typeInfoArr[i3] = TypeInfo.getHigherType(typeInfoArr[i3], optimize2.getType());
            }
        }
        Column[] columnArr = new Column[size2];
        int i4 = 0;
        while (i4 < size2) {
            TypeInfo typeInfo = typeInfoArr[i4];
            int i5 = i4;
            i4++;
            columnArr[i5] = new Column("C" + i4, typeInfo);
        }
        Database database = this.session.getDatabase();
        ArrayList<Expression> arrayList3 = new ArrayList<>(size2);
        for (int i6 = 0; i6 < size2; i6++) {
            arrayList3.add(new ExpressionColumn(database, null, null, columnArr[i6].getName()));
        }
        this.expressions = arrayList3;
        this.table = new TableValueConstructorTable(database.getMainSchema(), this.session, columnArr, this.rows);
        this.columnResolver = new TableValueColumnResolver();
    }

    @Override // org.h2.command.query.Query
    public double getCost() {
        return this.cost;
    }

    @Override // org.h2.command.query.Query
    public HashSet<Table> getTables() {
        HashSet<Table> hashSet = new HashSet<>(1, 1.0f);
        hashSet.add(this.table);
        return hashSet;
    }

    @Override // org.h2.command.query.Query
    public void setForUpdate(ForUpdate forUpdate) {
        throw DbException.get(ErrorCode.RESULT_SET_READONLY);
    }

    @Override // org.h2.command.query.Query
    public void mapColumns(ColumnResolver columnResolver, int i) {
        int i2 = this.visibleColumnCount;
        Iterator<ArrayList<Expression>> it = this.rows.iterator();
        while (it.hasNext()) {
            ArrayList<Expression> next = it.next();
            for (int i3 = 0; i3 < i2; i3++) {
                next.get(i3).mapColumns(columnResolver, i, 0);
            }
        }
    }

    @Override // org.h2.command.query.Query
    public void setEvaluatable(TableFilter tableFilter, boolean z) {
        int i = this.visibleColumnCount;
        Iterator<ArrayList<Expression>> it = this.rows.iterator();
        while (it.hasNext()) {
            ArrayList<Expression> next = it.next();
            for (int i2 = 0; i2 < i; i2++) {
                next.get(i2).setEvaluatable(tableFilter, z);
            }
        }
    }

    @Override // org.h2.command.query.Query
    public void addGlobalCondition(Parameter parameter, int i, int i2) {
    }

    @Override // org.h2.command.query.Query
    public boolean allowGlobalConditions() {
        return false;
    }

    @Override // org.h2.command.query.Query
    public boolean isEverything(ExpressionVisitor expressionVisitor) {
        ExpressionVisitor incrementQueryLevel = expressionVisitor.incrementQueryLevel(1);
        for (Expression expression : this.expressionArray) {
            if (!expression.isEverything(incrementQueryLevel)) {
                return false;
            }
        }
        return true;
    }

    @Override // org.h2.command.query.Query
    public void updateAggregate(SessionLocal sessionLocal, int i) {
        int i2 = this.visibleColumnCount;
        Iterator<ArrayList<Expression>> it = this.rows.iterator();
        while (it.hasNext()) {
            ArrayList<Expression> next = it.next();
            for (int i3 = 0; i3 < i2; i3++) {
                next.get(i3).updateAggregate(sessionLocal, i);
            }
        }
    }

    @Override // org.h2.command.query.Query
    public void fireBeforeSelectTriggers() {
    }

    @Override // org.h2.command.Prepared
    public String getPlanSQL(int i) {
        StringBuilder sb = new StringBuilder();
        getValuesSQL(sb, i, this.rows);
        appendEndOfQueryToSQL(sb, i, this.expressionArray);
        return sb.toString();
    }

    @Override // org.h2.command.query.Query
    public Table toTable(String str, Column[] columnArr, ArrayList<Parameter> arrayList, boolean z, Query query) {
        if (!hasOrder() && this.offsetExpr == null && this.fetchExpr == null && this.table != null) {
            return this.table;
        }
        return super.toTable(str, columnArr, arrayList, z, query);
    }

    @Override // org.h2.command.query.Query
    public boolean isConstantQuery() {
        if (!super.isConstantQuery()) {
            return false;
        }
        Iterator<ArrayList<Expression>> it = this.rows.iterator();
        while (it.hasNext()) {
            ArrayList<Expression> next = it.next();
            for (int i = 0; i < this.visibleColumnCount; i++) {
                if (!next.get(i).isConstant()) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override // org.h2.command.query.Query
    public Expression getIfSingleRow() {
        if (this.offsetExpr != null || this.fetchExpr != null || this.rows.size() != 1) {
            return null;
        }
        ArrayList<Expression> arrayList = this.rows.get(0);
        if (this.visibleColumnCount == 1) {
            return arrayList.get(0);
        }
        Expression[] expressionArr = new Expression[this.visibleColumnCount];
        for (int i = 0; i < this.visibleColumnCount; i++) {
            expressionArr[i] = arrayList.get(i);
        }
        return new ExpressionList(expressionArr, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/query/TableValueConstructor$TableValueColumnResolver.class */
    public final class TableValueColumnResolver implements ColumnResolver {
        Value[] currentRow;

        TableValueColumnResolver() {
        }

        @Override // org.h2.table.ColumnResolver
        public Column[] getColumns() {
            return TableValueConstructor.this.table.getColumns();
        }

        @Override // org.h2.table.ColumnResolver
        public Column findColumn(String str) {
            return TableValueConstructor.this.table.findColumn(str);
        }

        @Override // org.h2.table.ColumnResolver
        public Value getValue(Column column) {
            return this.currentRow[column.getColumnId()];
        }

        @Override // org.h2.table.ColumnResolver
        public Expression optimize(ExpressionColumn expressionColumn, Column column) {
            return TableValueConstructor.this.expressions.get(column.getColumnId());
        }
    }
}
