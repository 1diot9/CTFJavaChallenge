package org.h2.table;

import java.util.ArrayList;
import org.h2.api.ErrorCode;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.index.Index;
import org.h2.index.RangeIndex;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.value.TypeInfo;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/table/RangeTable.class */
public class RangeTable extends VirtualTable {
    public static final String NAME = "SYSTEM_RANGE";
    public static final String ALIAS = "GENERATE_SERIES";
    private Expression min;
    private Expression max;
    private Expression step;
    private boolean optimized;
    private final RangeIndex index;

    public RangeTable(Schema schema, Expression expression, Expression expression2) {
        super(schema, 0, NAME);
        this.min = expression;
        this.max = expression2;
        Column[] columnArr = {new Column("X", TypeInfo.TYPE_BIGINT)};
        setColumns(columnArr);
        this.index = new RangeIndex(this, IndexColumn.wrap(columnArr));
    }

    public RangeTable(Schema schema, Expression expression, Expression expression2, Expression expression3) {
        this(schema, expression, expression2);
        this.step = expression3;
    }

    @Override // org.h2.schema.SchemaObject, org.h2.engine.DbObject, org.h2.util.HasSQL
    public StringBuilder getSQL(StringBuilder sb, int i) {
        sb.append(NAME).append('(');
        this.min.getUnenclosedSQL(sb, i).append(", ");
        this.max.getUnenclosedSQL(sb, i);
        if (this.step != null) {
            this.step.getUnenclosedSQL(sb.append(", "), i);
        }
        return sb.append(')');
    }

    @Override // org.h2.table.Table
    public boolean canGetRowCount(SessionLocal sessionLocal) {
        return true;
    }

    @Override // org.h2.table.Table
    public long getRowCount(SessionLocal sessionLocal) {
        long step = getStep(sessionLocal);
        if (step == 0) {
            throw DbException.get(ErrorCode.STEP_SIZE_MUST_NOT_BE_ZERO);
        }
        long max = getMax(sessionLocal) - getMin(sessionLocal);
        if (step > 0) {
            if (max < 0) {
                return 0L;
            }
        } else if (max > 0) {
            return 0L;
        }
        return (max / step) + 1;
    }

    @Override // org.h2.table.VirtualTable, org.h2.table.Table
    public TableType getTableType() {
        return TableType.SYSTEM_TABLE;
    }

    @Override // org.h2.table.Table
    public Index getScanIndex(SessionLocal sessionLocal) {
        return this.index;
    }

    @Override // org.h2.table.VirtualTable, org.h2.table.Table
    public ArrayList<Index> getIndexes() {
        ArrayList<Index> arrayList = new ArrayList<>(2);
        arrayList.add(this.index);
        arrayList.add(this.index);
        return arrayList;
    }

    public long getMin(SessionLocal sessionLocal) {
        optimize(sessionLocal);
        return this.min.getValue(sessionLocal).getLong();
    }

    public long getMax(SessionLocal sessionLocal) {
        optimize(sessionLocal);
        return this.max.getValue(sessionLocal).getLong();
    }

    public long getStep(SessionLocal sessionLocal) {
        optimize(sessionLocal);
        if (this.step == null) {
            return 1L;
        }
        return this.step.getValue(sessionLocal).getLong();
    }

    private void optimize(SessionLocal sessionLocal) {
        if (!this.optimized) {
            this.min = this.min.optimize(sessionLocal);
            this.max = this.max.optimize(sessionLocal);
            if (this.step != null) {
                this.step = this.step.optimize(sessionLocal);
            }
            this.optimized = true;
        }
    }

    @Override // org.h2.table.Table
    public long getMaxDataModificationId() {
        return 0L;
    }

    @Override // org.h2.table.Table
    public long getRowCountApproximation(SessionLocal sessionLocal) {
        return 100L;
    }

    @Override // org.h2.table.Table
    public boolean isDeterministic() {
        return true;
    }
}
