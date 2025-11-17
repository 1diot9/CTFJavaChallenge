package org.h2.index;

import org.h2.api.ErrorCode;
import org.h2.command.query.AllColumnsForPlan;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.result.Row;
import org.h2.result.SearchRow;
import org.h2.result.SortOrder;
import org.h2.table.IndexColumn;
import org.h2.table.RangeTable;
import org.h2.table.TableFilter;
import org.h2.value.Value;
import org.h2.value.ValueBigint;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/index/RangeIndex.class */
public class RangeIndex extends VirtualTableIndex {
    private final RangeTable rangeTable;

    public RangeIndex(RangeTable rangeTable, IndexColumn[] indexColumnArr) {
        super(rangeTable, "RANGE_INDEX", indexColumnArr);
        this.rangeTable = rangeTable;
    }

    @Override // org.h2.index.Index
    public Cursor find(SessionLocal sessionLocal, SearchRow searchRow, SearchRow searchRow2) {
        long min = this.rangeTable.getMin(sessionLocal);
        long max = this.rangeTable.getMax(sessionLocal);
        long step = this.rangeTable.getStep(sessionLocal);
        if (step == 0) {
            throw DbException.get(ErrorCode.STEP_SIZE_MUST_NOT_BE_ZERO);
        }
        if (searchRow != null) {
            try {
                long j = searchRow.getValue(0).getLong();
                if (step > 0) {
                    if (j > min) {
                        min += ((((j - min) + step) - 1) / step) * step;
                    }
                } else if (j > max) {
                    max = j;
                }
            } catch (DbException e) {
            }
        }
        if (searchRow2 != null) {
            try {
                long j2 = searchRow2.getValue(0).getLong();
                if (step > 0) {
                    if (j2 < max) {
                        max = j2;
                    }
                } else if (j2 < min) {
                    min -= ((((min - j2) - step) - 1) / step) * step;
                }
            } catch (DbException e2) {
            }
        }
        return new RangeCursor(min, max, step);
    }

    @Override // org.h2.index.Index
    public double getCost(SessionLocal sessionLocal, int[] iArr, TableFilter[] tableFilterArr, int i, SortOrder sortOrder, AllColumnsForPlan allColumnsForPlan) {
        return 1.0d;
    }

    @Override // org.h2.index.Index, org.h2.engine.DbObject
    public String getCreateSQL() {
        return null;
    }

    @Override // org.h2.index.Index
    public boolean canGetFirstOrLast() {
        return true;
    }

    @Override // org.h2.index.Index
    public Cursor findFirstOrLast(SessionLocal sessionLocal, boolean z) {
        Row row;
        long min = this.rangeTable.getMin(sessionLocal);
        long max = this.rangeTable.getMax(sessionLocal);
        long step = this.rangeTable.getStep(sessionLocal);
        if (step == 0) {
            throw DbException.get(ErrorCode.STEP_SIZE_MUST_NOT_BE_ZERO);
        }
        if (step <= 0 ? min < max : min > max) {
            row = null;
        } else {
            Value[] valueArr = new Value[1];
            valueArr[0] = ValueBigint.get(z ^ ((min > max ? 1 : (min == max ? 0 : -1)) >= 0) ? min : max);
            row = Row.get(valueArr, 1);
        }
        return new SingleRowCursor(row);
    }

    @Override // org.h2.index.Index
    public String getPlanSQL() {
        return "range index";
    }
}
