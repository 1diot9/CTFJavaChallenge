package org.h2.expression.aggregate;

import java.util.ArrayList;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.index.Index;
import org.h2.mvstore.db.MVSpatialIndex;
import org.h2.table.Column;
import org.h2.table.TableFilter;
import org.h2.util.geometry.GeometryUtils;
import org.h2.value.Value;
import org.h2.value.ValueGeometry;
import org.h2.value.ValueNull;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/aggregate/AggregateDataEnvelope.class */
public final class AggregateDataEnvelope extends AggregateData {
    private double[] envelope;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Index getGeometryColumnIndex(Expression expression) {
        TableFilter tableFilter;
        ArrayList<Index> indexes;
        if (expression instanceof ExpressionColumn) {
            ExpressionColumn expressionColumn = (ExpressionColumn) expression;
            Column column = expressionColumn.getColumn();
            if (column.getType().getValueType() == 37 && (tableFilter = expressionColumn.getTableFilter()) != null && (indexes = tableFilter.getTable().getIndexes()) != null) {
                int size = indexes.size();
                for (int i = 1; i < size; i++) {
                    Index index = indexes.get(i);
                    if ((index instanceof MVSpatialIndex) && index.isFirstColumn(column)) {
                        return index;
                    }
                }
                return null;
            }
            return null;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.h2.expression.aggregate.AggregateData
    public void add(SessionLocal sessionLocal, Value value) {
        if (value == ValueNull.INSTANCE) {
            return;
        }
        this.envelope = GeometryUtils.union(this.envelope, value.convertToGeometry(null).getEnvelopeNoCopy());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.h2.expression.aggregate.AggregateData
    public Value getValue(SessionLocal sessionLocal) {
        return ValueGeometry.fromEnvelope(this.envelope);
    }
}
