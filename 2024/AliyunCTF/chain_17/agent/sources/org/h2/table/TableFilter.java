package org.h2.table;

import cn.hutool.core.text.StrPool;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.h2.api.ErrorCode;
import org.h2.command.query.AllColumnsForPlan;
import org.h2.command.query.Select;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.condition.ConditionAndOr;
import org.h2.index.Index;
import org.h2.index.IndexCondition;
import org.h2.index.IndexCursor;
import org.h2.message.DbException;
import org.h2.result.Row;
import org.h2.result.SearchRow;
import org.h2.result.SortOrder;
import org.h2.util.ParserUtil;
import org.h2.util.StringUtils;
import org.h2.util.Utils;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBigint;
import org.h2.value.ValueInteger;
import org.h2.value.ValueNull;
import org.h2.value.ValueSmallint;
import org.h2.value.ValueTinyint;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/table/TableFilter.class */
public class TableFilter implements ColumnResolver {
    private static final int BEFORE_FIRST = 0;
    private static final int FOUND = 1;
    private static final int AFTER_LAST = 2;
    private static final int NULL_ROW = 3;
    public static final Comparator<TableFilter> ORDER_IN_FROM_COMPARATOR;
    private static final TableFilterVisitor JOI_VISITOR;
    protected boolean joinOuterIndirect;
    private SessionLocal session;
    private final Table table;
    private final Select select;
    private String alias;
    private Index index;
    private final IndexHints indexHints;
    private int[] masks;
    private int scanCount;
    private boolean evaluatable;
    private boolean used;
    private Expression filterCondition;
    private Expression joinCondition;
    private SearchRow currentSearchRow;
    private Row current;
    private int state;
    private TableFilter join;
    private boolean joinOuter;
    private TableFilter nestedJoin;
    private LinkedHashMap<Column, Column> commonJoinColumns;
    private TableFilter commonJoinColumnsFilter;
    private ArrayList<Column> commonJoinColumnsToExclude;
    private boolean foundOne;
    private Expression fullCondition;
    private final int hashCode;
    private final int orderInFrom;
    private LinkedHashMap<Column, String> derivedColumnMap;
    static final /* synthetic */ boolean $assertionsDisabled;
    private final ArrayList<IndexCondition> indexConditions = Utils.newSmallArrayList();
    private final IndexCursor cursor = new IndexCursor();

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/table/TableFilter$TableFilterVisitor.class */
    public interface TableFilterVisitor {
        void accept(TableFilter tableFilter);
    }

    static {
        $assertionsDisabled = !TableFilter.class.desiredAssertionStatus();
        ORDER_IN_FROM_COMPARATOR = Comparator.comparing((v0) -> {
            return v0.getOrderInFrom();
        });
        JOI_VISITOR = tableFilter -> {
            tableFilter.joinOuterIndirect = true;
        };
    }

    public TableFilter(SessionLocal sessionLocal, Table table, String str, boolean z, Select select, int i, IndexHints indexHints) {
        this.session = sessionLocal;
        this.table = table;
        this.alias = str;
        this.select = select;
        if (!z) {
            sessionLocal.getUser().checkTableRight(table, 1);
        }
        this.hashCode = sessionLocal.nextObjectId();
        this.orderInFrom = i;
        this.indexHints = indexHints;
    }

    public int getOrderInFrom() {
        return this.orderInFrom;
    }

    public IndexCursor getIndexCursor() {
        return this.cursor;
    }

    @Override // org.h2.table.ColumnResolver
    public Select getSelect() {
        return this.select;
    }

    public Table getTable() {
        return this.table;
    }

    public void lock(SessionLocal sessionLocal) {
        this.table.lock(sessionLocal, 0);
        if (this.join != null) {
            this.join.lock(sessionLocal);
        }
    }

    public PlanItem getBestPlanItem(SessionLocal sessionLocal, TableFilter[] tableFilterArr, int i, AllColumnsForPlan allColumnsForPlan) {
        PlanItem planItem = null;
        SortOrder sortOrder = null;
        if (this.select != null) {
            sortOrder = this.select.getSortOrder();
        }
        if (this.indexConditions.isEmpty()) {
            planItem = new PlanItem();
            planItem.setIndex(this.table.getScanIndex(sessionLocal, null, tableFilterArr, i, sortOrder, allColumnsForPlan));
            planItem.cost = planItem.getIndex().getCost(sessionLocal, null, tableFilterArr, i, sortOrder, allColumnsForPlan);
        }
        int[] iArr = new int[this.table.getColumns().length];
        Iterator<IndexCondition> it = this.indexConditions.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            IndexCondition next = it.next();
            if (next.isEvaluatable()) {
                if (next.isAlwaysFalse()) {
                    iArr = null;
                    break;
                }
                int columnId = next.getColumn().getColumnId();
                if (columnId >= 0) {
                    iArr[columnId] = iArr[columnId] | next.getMask(this.indexConditions);
                }
            }
        }
        PlanItem bestPlanItem = this.table.getBestPlanItem(sessionLocal, iArr, tableFilterArr, i, sortOrder, allColumnsForPlan);
        bestPlanItem.setMasks(iArr);
        bestPlanItem.cost -= ((bestPlanItem.cost * this.indexConditions.size()) / 100.0d) / (i + 1);
        if (planItem != null && planItem.cost < bestPlanItem.cost) {
            bestPlanItem = planItem;
        }
        if (this.nestedJoin != null) {
            setEvaluatable(true);
            bestPlanItem.setNestedJoinPlan(this.nestedJoin.getBestPlanItem(sessionLocal, tableFilterArr, i, allColumnsForPlan));
            bestPlanItem.cost += bestPlanItem.cost * bestPlanItem.getNestedJoinPlan().cost;
        }
        if (this.join != null) {
            setEvaluatable(true);
            do {
                i++;
            } while (tableFilterArr[i] != this.join);
            bestPlanItem.setJoinPlan(this.join.getBestPlanItem(sessionLocal, tableFilterArr, i, allColumnsForPlan));
            bestPlanItem.cost += bestPlanItem.cost * bestPlanItem.getJoinPlan().cost;
        }
        return bestPlanItem;
    }

    public void setPlanItem(PlanItem planItem) {
        if (planItem == null) {
            return;
        }
        setIndex(planItem.getIndex());
        this.masks = planItem.getMasks();
        if (this.nestedJoin != null) {
            if (planItem.getNestedJoinPlan() != null) {
                this.nestedJoin.setPlanItem(planItem.getNestedJoinPlan());
            } else {
                this.nestedJoin.setScanIndexes();
            }
        }
        if (this.join != null) {
            if (planItem.getJoinPlan() != null) {
                this.join.setPlanItem(planItem.getJoinPlan());
            } else {
                this.join.setScanIndexes();
            }
        }
    }

    private void setScanIndexes() {
        if (this.index == null) {
            setIndex(this.table.getScanIndex(this.session));
        }
        if (this.join != null) {
            this.join.setScanIndexes();
        }
        if (this.nestedJoin != null) {
            this.nestedJoin.setScanIndexes();
        }
    }

    public void prepare() {
        int i = 0;
        while (i < this.indexConditions.size()) {
            IndexCondition indexCondition = this.indexConditions.get(i);
            if (!indexCondition.isAlwaysFalse()) {
                Column column = indexCondition.getColumn();
                if (column.getColumnId() >= 0 && this.index.getColumnIndex(column) < 0) {
                    this.indexConditions.remove(i);
                    i--;
                }
            }
            i++;
        }
        if (this.nestedJoin != null) {
            if (this.nestedJoin == this) {
                throw DbException.getInternalError("self join");
            }
            this.nestedJoin.prepare();
        }
        if (this.join != null) {
            if (this.join == this) {
                throw DbException.getInternalError("self join");
            }
            this.join.prepare();
        }
        if (this.filterCondition != null) {
            this.filterCondition = this.filterCondition.optimizeCondition(this.session);
        }
        if (this.joinCondition != null) {
            this.joinCondition = this.joinCondition.optimizeCondition(this.session);
        }
    }

    public void startQuery(SessionLocal sessionLocal) {
        this.session = sessionLocal;
        this.scanCount = 0;
        if (this.nestedJoin != null) {
            this.nestedJoin.startQuery(sessionLocal);
        }
        if (this.join != null) {
            this.join.startQuery(sessionLocal);
        }
    }

    public void reset() {
        if (this.nestedJoin != null) {
            this.nestedJoin.reset();
        }
        if (this.join != null) {
            this.join.reset();
        }
        this.state = 0;
        this.foundOne = false;
    }

    public boolean next() {
        if (this.state == 2) {
            return false;
        }
        if (this.state == 0) {
            this.cursor.find(this.session, this.indexConditions);
            if (!this.cursor.isAlwaysFalse()) {
                if (this.nestedJoin != null) {
                    this.nestedJoin.reset();
                }
                if (this.join != null) {
                    this.join.reset();
                }
            }
        } else if (this.join != null && this.join.next()) {
            return true;
        }
        while (this.state != 3) {
            if (this.cursor.isAlwaysFalse()) {
                this.state = 2;
            } else if (this.nestedJoin != null) {
                if (this.state == 0) {
                    this.state = 1;
                }
            } else {
                int i = this.scanCount + 1;
                this.scanCount = i;
                if ((i & 4095) == 0) {
                    checkTimeout();
                }
                if (this.cursor.next()) {
                    this.currentSearchRow = this.cursor.getSearchRow();
                    this.current = null;
                    this.state = 1;
                } else {
                    this.state = 2;
                }
            }
            if (this.nestedJoin != null && this.state == 1 && !this.nestedJoin.next()) {
                this.state = 2;
                if (this.joinOuter && !this.foundOne) {
                }
            }
            if (this.state == 2) {
                if (!this.joinOuter || this.foundOne) {
                    break;
                }
                setNullRow();
            }
            if (isOk(this.filterCondition)) {
                boolean isOk = isOk(this.joinCondition);
                if (this.state == 1) {
                    if (isOk) {
                        this.foundOne = true;
                    } else {
                        continue;
                    }
                }
                if (this.join != null) {
                    this.join.reset();
                    if (!this.join.next()) {
                        continue;
                    }
                }
                if (this.state == 3 || isOk) {
                    return true;
                }
            } else {
                continue;
            }
        }
        this.state = 2;
        return false;
    }

    public boolean isNullRow() {
        return this.state == 3;
    }

    protected void setNullRow() {
        this.state = 3;
        this.current = this.table.getNullRow();
        this.currentSearchRow = this.current;
        if (this.nestedJoin != null) {
            this.nestedJoin.visit((v0) -> {
                v0.setNullRow();
            });
        }
    }

    private void checkTimeout() {
        this.session.checkCanceled();
    }

    boolean isOk(Expression expression) {
        return expression == null || expression.getBooleanValue(this.session);
    }

    public Row get() {
        if (this.current == null && this.currentSearchRow != null) {
            this.current = this.cursor.get();
        }
        return this.current;
    }

    public void set(Row row) {
        this.current = row;
        this.currentSearchRow = row;
    }

    @Override // org.h2.table.ColumnResolver
    public String getTableAlias() {
        if (this.alias != null) {
            return this.alias;
        }
        return this.table.getName();
    }

    public void addIndexCondition(IndexCondition indexCondition) {
        this.indexConditions.add(indexCondition);
    }

    public void addFilterCondition(Expression expression, boolean z) {
        if (z) {
            if (this.joinCondition == null) {
                this.joinCondition = expression;
                return;
            } else {
                this.joinCondition = new ConditionAndOr(0, this.joinCondition, expression);
                return;
            }
        }
        if (this.filterCondition == null) {
            this.filterCondition = expression;
        } else {
            this.filterCondition = new ConditionAndOr(0, this.filterCondition, expression);
        }
    }

    public void addJoin(TableFilter tableFilter, boolean z, Expression expression) {
        if (expression != null) {
            expression.mapColumns(this, 0, 0);
            MapColumnsVisitor mapColumnsVisitor = new MapColumnsVisitor(expression);
            visit(mapColumnsVisitor);
            tableFilter.visit(mapColumnsVisitor);
        }
        if (this.join == null) {
            this.join = tableFilter;
            tableFilter.joinOuter = z;
            if (z) {
                tableFilter.visit(JOI_VISITOR);
            }
            if (expression != null) {
                tableFilter.mapAndAddFilter(expression);
                return;
            }
            return;
        }
        this.join.addJoin(tableFilter, z, expression);
    }

    public void setNestedJoin(TableFilter tableFilter) {
        this.nestedJoin = tableFilter;
    }

    public void mapAndAddFilter(Expression expression) {
        expression.mapColumns(this, 0, 0);
        addFilterCondition(expression, true);
        if (this.nestedJoin != null) {
            expression.mapColumns(this.nestedJoin, 0, 0);
        }
        if (this.join != null) {
            this.join.mapAndAddFilter(expression);
        }
    }

    public void createIndexConditions() {
        if (this.joinCondition != null) {
            this.joinCondition = this.joinCondition.optimizeCondition(this.session);
            if (this.joinCondition != null) {
                this.joinCondition.createIndexConditions(this.session, this);
                if (this.nestedJoin != null) {
                    this.joinCondition.createIndexConditions(this.session, this.nestedJoin);
                }
            }
        }
        if (this.join != null) {
            this.join.createIndexConditions();
        }
        if (this.nestedJoin != null) {
            this.nestedJoin.createIndexConditions();
        }
    }

    public TableFilter getJoin() {
        return this.join;
    }

    public boolean isJoinOuter() {
        return this.joinOuter;
    }

    public boolean isJoinOuterIndirect() {
        return this.joinOuterIndirect;
    }

    public StringBuilder getPlanSQL(StringBuilder sb, boolean z, int i) {
        if (z) {
            if (this.joinOuter) {
                sb.append("LEFT OUTER JOIN ");
            } else {
                sb.append("INNER JOIN ");
            }
        }
        if (this.nestedJoin != null) {
            StringBuilder sb2 = new StringBuilder();
            TableFilter tableFilter = this.nestedJoin;
            do {
                tableFilter.getPlanSQL(sb2, tableFilter != this.nestedJoin, i).append('\n');
                tableFilter = tableFilter.getJoin();
            } while (tableFilter != null);
            String sb3 = sb2.toString();
            boolean z2 = !sb3.startsWith("(");
            if (z2) {
                sb.append("(\n");
            }
            StringUtils.indent(sb, sb3, 4, false);
            if (z2) {
                sb.append(')');
            }
            if (z) {
                sb.append(" ON ");
                if (this.joinCondition == null) {
                    sb.append("1=1");
                } else {
                    this.joinCondition.getUnenclosedSQL(sb, i);
                }
            }
            return sb;
        }
        if ((this.table instanceof TableView) && ((TableView) this.table).isRecursive()) {
            this.table.getSchema().getSQL(sb, i).append('.');
            ParserUtil.quoteIdentifier(sb, this.table.getName(), i);
        } else {
            this.table.getSQL(sb, i);
        }
        if ((this.table instanceof TableView) && ((TableView) this.table).isInvalid()) {
            throw DbException.get(ErrorCode.VIEW_IS_INVALID_2, this.table.getName(), "not compiled");
        }
        if (this.alias != null) {
            sb.append(' ');
            ParserUtil.quoteIdentifier(sb, this.alias, i);
            if (this.derivedColumnMap != null) {
                sb.append('(');
                boolean z3 = false;
                for (String str : this.derivedColumnMap.values()) {
                    if (z3) {
                        sb.append(", ");
                    }
                    z3 = true;
                    ParserUtil.quoteIdentifier(sb, str, i);
                }
                sb.append(')');
            }
        }
        if (this.indexHints != null) {
            sb.append(" USE INDEX (");
            boolean z4 = true;
            for (String str2 : this.indexHints.getAllowedIndexes()) {
                if (!z4) {
                    sb.append(", ");
                } else {
                    z4 = false;
                }
                ParserUtil.quoteIdentifier(sb, str2, i);
            }
            sb.append(")");
        }
        if (this.index != null && (i & 8) != 0) {
            sb.append('\n');
            StringBuilder append = new StringBuilder().append("/* ").append(this.index.getPlanSQL());
            if (!this.indexConditions.isEmpty()) {
                append.append(": ");
                int size = this.indexConditions.size();
                for (int i2 = 0; i2 < size; i2++) {
                    if (i2 > 0) {
                        append.append("\n    AND ");
                    }
                    append.append(this.indexConditions.get(i2).getSQL(11));
                }
            }
            if (append.indexOf(StrPool.LF, 3) >= 0) {
                append.append('\n');
            }
            StringUtils.indent(sb, append.append(" */").toString(), 4, false);
        }
        if (z) {
            sb.append("\n    ON ");
            if (this.joinCondition == null) {
                sb.append("1=1");
            } else {
                this.joinCondition.getUnenclosedSQL(sb, i);
            }
        }
        if ((i & 8) != 0) {
            if (this.filterCondition != null) {
                sb.append('\n');
                StringUtils.indent(sb, "/* WHERE " + this.filterCondition.getSQL(11, 2) + "\n*/", 4, false);
            }
            if (this.scanCount > 0) {
                sb.append("\n    /* scanCount: ").append(this.scanCount).append(" */");
            }
        }
        return sb;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeUnusableIndexConditions() {
        int i = 0;
        while (i < this.indexConditions.size()) {
            IndexCondition indexCondition = this.indexConditions.get(i);
            if (indexCondition.getMask(this.indexConditions) == 0 || !indexCondition.isEvaluatable()) {
                int i2 = i;
                i--;
                this.indexConditions.remove(i2);
            }
            i++;
        }
    }

    public int[] getMasks() {
        return this.masks;
    }

    public ArrayList<IndexCondition> getIndexConditions() {
        return this.indexConditions;
    }

    public Index getIndex() {
        return this.index;
    }

    public void setIndex(Index index) {
        this.index = index;
        this.cursor.setIndex(index);
    }

    public void setUsed(boolean z) {
        this.used = z;
    }

    public boolean isUsed() {
        return this.used;
    }

    public void removeJoin() {
        this.join = null;
    }

    public Expression getJoinCondition() {
        return this.joinCondition;
    }

    public void removeJoinCondition() {
        this.joinCondition = null;
    }

    public Expression getFilterCondition() {
        return this.filterCondition;
    }

    public void removeFilterCondition() {
        this.filterCondition = null;
    }

    public void setFullCondition(Expression expression) {
        this.fullCondition = expression;
        if (this.join != null) {
            this.join.setFullCondition(expression);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void optimizeFullCondition() {
        if (!this.joinOuter && this.fullCondition != null) {
            this.fullCondition.addFilterConditions(this);
            if (this.nestedJoin != null) {
                this.nestedJoin.optimizeFullCondition();
            }
            if (this.join != null) {
                this.join.optimizeFullCondition();
            }
        }
    }

    public void setEvaluatable(TableFilter tableFilter, boolean z) {
        tableFilter.setEvaluatable(z);
        if (this.filterCondition != null) {
            this.filterCondition.setEvaluatable(tableFilter, z);
        }
        if (this.joinCondition != null) {
            this.joinCondition.setEvaluatable(tableFilter, z);
        }
        if (this.nestedJoin != null && this == tableFilter) {
            this.nestedJoin.setEvaluatable(this.nestedJoin, z);
        }
        if (this.join != null) {
            this.join.setEvaluatable(tableFilter, z);
        }
    }

    public void setEvaluatable(boolean z) {
        this.evaluatable = z;
    }

    @Override // org.h2.table.ColumnResolver
    public String getSchemaName() {
        if (this.alias == null && !(this.table instanceof VirtualTable)) {
            return this.table.getSchema().getName();
        }
        return null;
    }

    @Override // org.h2.table.ColumnResolver
    public Column[] getColumns() {
        return this.table.getColumns();
    }

    @Override // org.h2.table.ColumnResolver
    public Column findColumn(String str) {
        if (this.derivedColumnMap != null) {
            Database database = this.session.getDatabase();
            for (Map.Entry<Column, String> entry : this.derivedColumnMap.entrySet()) {
                if (database.equalsIdentifiers(entry.getValue(), str)) {
                    return entry.getKey();
                }
            }
            return null;
        }
        return this.table.findColumn(str);
    }

    @Override // org.h2.table.ColumnResolver
    public String getColumnName(Column column) {
        LinkedHashMap<Column, String> linkedHashMap = this.derivedColumnMap;
        return linkedHashMap != null ? linkedHashMap.get(column) : column.getName();
    }

    @Override // org.h2.table.ColumnResolver
    public boolean hasDerivedColumnList() {
        return this.derivedColumnMap != null;
    }

    public Column getColumn(String str, boolean z) {
        LinkedHashMap<Column, String> linkedHashMap = this.derivedColumnMap;
        if (linkedHashMap != null) {
            Database database = this.session.getDatabase();
            for (Map.Entry<Column, String> entry : linkedHashMap.entrySet()) {
                if (database.equalsIdentifiers(str, entry.getValue())) {
                    return entry.getKey();
                }
            }
            if (z) {
                return null;
            }
            throw DbException.get(ErrorCode.COLUMN_NOT_FOUND_1, str);
        }
        return this.table.getColumn(str, z);
    }

    @Override // org.h2.table.ColumnResolver
    public Column[] getSystemColumns() {
        if (!this.session.getDatabase().getMode().systemColumns) {
            return null;
        }
        return new Column[]{new Column("oid", TypeInfo.TYPE_INTEGER, this.table, 0), new Column("ctid", TypeInfo.TYPE_VARCHAR, this.table, 0)};
    }

    @Override // org.h2.table.ColumnResolver
    public Column getRowIdColumn() {
        return this.table.getRowIdColumn();
    }

    @Override // org.h2.table.ColumnResolver
    public Value getValue(Column column) {
        if (this.currentSearchRow == null) {
            return null;
        }
        int columnId = column.getColumnId();
        if (columnId == -1) {
            return ValueBigint.get(this.currentSearchRow.getKey());
        }
        if (this.current == null) {
            Value value = this.currentSearchRow.getValue(columnId);
            if (value != null) {
                return value;
            }
            if (columnId == column.getTable().getMainIndexColumn()) {
                return getDelegatedValue(column);
            }
            this.current = this.cursor.get();
            if (this.current == null) {
                return ValueNull.INSTANCE;
            }
        }
        return this.current.getValue(columnId);
    }

    private Value getDelegatedValue(Column column) {
        long key = this.currentSearchRow.getKey();
        switch (column.getType().getValueType()) {
            case 9:
                return ValueTinyint.get((byte) key);
            case 10:
                return ValueSmallint.get((short) key);
            case 11:
                return ValueInteger.get((int) key);
            case 12:
                return ValueBigint.get(key);
            default:
                throw DbException.getInternalError();
        }
    }

    @Override // org.h2.table.ColumnResolver
    public TableFilter getTableFilter() {
        return this;
    }

    public void setAlias(String str) {
        this.alias = str;
    }

    public void setDerivedColumns(ArrayList<String> arrayList) {
        Column[] columns = getColumns();
        int length = columns.length;
        if (length != arrayList.size()) {
            throw DbException.get(ErrorCode.COLUMN_COUNT_DOES_NOT_MATCH);
        }
        LinkedHashMap<Column, String> linkedHashMap = new LinkedHashMap<>();
        for (int i = 0; i < length; i++) {
            String str = arrayList.get(i);
            for (int i2 = 0; i2 < i; i2++) {
                if (str.equals(arrayList.get(i2))) {
                    throw DbException.get(ErrorCode.DUPLICATE_COLUMN_NAME_1, str);
                }
            }
            linkedHashMap.put(columns[i], str);
        }
        this.derivedColumnMap = linkedHashMap;
    }

    public String toString() {
        return this.alias != null ? this.alias : this.table.toString();
    }

    public void addCommonJoinColumns(Column column, Column column2, TableFilter tableFilter) {
        if (this.commonJoinColumns == null) {
            this.commonJoinColumns = new LinkedHashMap<>();
            this.commonJoinColumnsFilter = tableFilter;
        } else if (!$assertionsDisabled && this.commonJoinColumnsFilter != tableFilter) {
            throw new AssertionError();
        }
        this.commonJoinColumns.put(column, column2);
    }

    public void addCommonJoinColumnToExclude(Column column) {
        if (this.commonJoinColumnsToExclude == null) {
            this.commonJoinColumnsToExclude = Utils.newSmallArrayList();
        }
        this.commonJoinColumnsToExclude.add(column);
    }

    public LinkedHashMap<Column, Column> getCommonJoinColumns() {
        return this.commonJoinColumns;
    }

    public TableFilter getCommonJoinColumnsFilter() {
        return this.commonJoinColumnsFilter;
    }

    public boolean isCommonJoinColumnToExclude(Column column) {
        return this.commonJoinColumnsToExclude != null && this.commonJoinColumnsToExclude.contains(column);
    }

    public int hashCode() {
        return this.hashCode;
    }

    public boolean hasInComparisons() {
        Iterator<IndexCondition> it = this.indexConditions.iterator();
        while (it.hasNext()) {
            switch (it.next().getCompareType()) {
                case 10:
                case 11:
                case 12:
                    return true;
            }
        }
        return false;
    }

    public TableFilter getNestedJoin() {
        return this.nestedJoin;
    }

    public void visit(TableFilterVisitor tableFilterVisitor) {
        TableFilter tableFilter = this;
        do {
            tableFilterVisitor.accept(tableFilter);
            TableFilter tableFilter2 = tableFilter.nestedJoin;
            if (tableFilter2 != null) {
                tableFilter2.visit(tableFilterVisitor);
            }
            tableFilter = tableFilter.join;
        } while (tableFilter != null);
    }

    public boolean isEvaluatable() {
        return this.evaluatable;
    }

    public SessionLocal getSession() {
        return this.session;
    }

    public IndexHints getIndexHints() {
        return this.indexHints;
    }

    public boolean isNoFromClauseFilter() {
        return (this.table instanceof DualTable) && this.join == null && this.nestedJoin == null && this.joinCondition == null && this.filterCondition == null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/table/TableFilter$MapColumnsVisitor.class */
    public static final class MapColumnsVisitor implements TableFilterVisitor {
        private final Expression on;

        MapColumnsVisitor(Expression expression) {
            this.on = expression;
        }

        @Override // org.h2.table.TableFilter.TableFilterVisitor
        public void accept(TableFilter tableFilter) {
            this.on.mapColumns(tableFilter, 0, 0);
        }
    }
}
