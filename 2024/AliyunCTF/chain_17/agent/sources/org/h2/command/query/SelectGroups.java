package org.h2.command.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.analysis.DataAnalysisOperation;
import org.h2.expression.analysis.PartitionData;
import org.h2.value.Value;
import org.h2.value.ValueRow;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/query/SelectGroups.class */
public abstract class SelectGroups {
    final SessionLocal session;
    final ArrayList<Expression> expressions;
    Object[] currentGroupByExprData;
    private final HashMap<Expression, Integer> exprToIndexInGroupByData = new HashMap<>();
    private final HashMap<DataAnalysisOperation, PartitionData> windowData = new HashMap<>();
    private final HashMap<DataAnalysisOperation, TreeMap<Value, PartitionData>> windowPartitionData = new HashMap<>();
    int currentGroupRowId;
    static final /* synthetic */ boolean $assertionsDisabled;

    abstract void updateCurrentGroupExprData();

    public abstract void nextSource();

    public abstract ValueRow next();

    static {
        $assertionsDisabled = !SelectGroups.class.desiredAssertionStatus();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/query/SelectGroups$Grouped.class */
    public static final class Grouped extends SelectGroups {
        private final int[] groupIndex;
        private TreeMap<ValueRow, Object[]> groupByData;
        private ValueRow currentGroupsKey;
        private Iterator<Map.Entry<ValueRow, Object[]>> cursor;

        Grouped(SessionLocal sessionLocal, ArrayList<Expression> arrayList, int[] iArr) {
            super(sessionLocal, arrayList);
            this.groupIndex = iArr;
        }

        @Override // org.h2.command.query.SelectGroups
        public void reset() {
            super.reset();
            this.groupByData = new TreeMap<>(this.session.getDatabase().getCompareMode());
            this.currentGroupsKey = null;
            this.cursor = null;
        }

        @Override // org.h2.command.query.SelectGroups
        public void nextSource() {
            if (this.groupIndex == null) {
                this.currentGroupsKey = ValueRow.EMPTY;
            } else {
                Value[] valueArr = new Value[this.groupIndex.length];
                for (int i = 0; i < this.groupIndex.length; i++) {
                    valueArr[i] = this.expressions.get(this.groupIndex[i]).getValue(this.session);
                }
                this.currentGroupsKey = ValueRow.get(valueArr);
            }
            Object[] objArr = this.groupByData.get(this.currentGroupsKey);
            if (objArr == null) {
                objArr = createRow();
                this.groupByData.put(this.currentGroupsKey, objArr);
            }
            this.currentGroupByExprData = objArr;
            this.currentGroupRowId++;
        }

        @Override // org.h2.command.query.SelectGroups
        void updateCurrentGroupExprData() {
            if (this.currentGroupsKey != null) {
                this.groupByData.put(this.currentGroupsKey, this.currentGroupByExprData);
            }
        }

        @Override // org.h2.command.query.SelectGroups
        public void done() {
            super.done();
            if (this.groupIndex == null && this.groupByData.size() == 0) {
                this.groupByData.put(ValueRow.EMPTY, createRow());
            }
            this.cursor = this.groupByData.entrySet().iterator();
        }

        @Override // org.h2.command.query.SelectGroups
        public ValueRow next() {
            if (this.cursor.hasNext()) {
                Map.Entry<ValueRow, Object[]> next = this.cursor.next();
                this.currentGroupByExprData = next.getValue();
                this.currentGroupRowId++;
                return next.getKey();
            }
            return null;
        }

        @Override // org.h2.command.query.SelectGroups
        public void remove() {
            this.cursor.remove();
            this.currentGroupByExprData = null;
            this.currentGroupRowId--;
        }

        @Override // org.h2.command.query.SelectGroups
        public void resetLazy() {
            super.resetLazy();
            this.currentGroupsKey = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/query/SelectGroups$Plain.class */
    public static final class Plain extends SelectGroups {
        private ArrayList<Object[]> rows;
        private Iterator<Object[]> cursor;

        Plain(SessionLocal sessionLocal, ArrayList<Expression> arrayList) {
            super(sessionLocal, arrayList);
        }

        @Override // org.h2.command.query.SelectGroups
        public void reset() {
            super.reset();
            this.rows = new ArrayList<>();
            this.cursor = null;
        }

        @Override // org.h2.command.query.SelectGroups
        public void nextSource() {
            Object[] createRow = createRow();
            this.rows.add(createRow);
            this.currentGroupByExprData = createRow;
            this.currentGroupRowId++;
        }

        @Override // org.h2.command.query.SelectGroups
        void updateCurrentGroupExprData() {
            this.rows.set(this.rows.size() - 1, this.currentGroupByExprData);
        }

        @Override // org.h2.command.query.SelectGroups
        public void done() {
            super.done();
            this.cursor = this.rows.iterator();
        }

        @Override // org.h2.command.query.SelectGroups
        public ValueRow next() {
            if (this.cursor.hasNext()) {
                this.currentGroupByExprData = this.cursor.next();
                this.currentGroupRowId++;
                return ValueRow.EMPTY;
            }
            return null;
        }
    }

    public static SelectGroups getInstance(SessionLocal sessionLocal, ArrayList<Expression> arrayList, boolean z, int[] iArr) {
        return z ? new Grouped(sessionLocal, arrayList, iArr) : new Plain(sessionLocal, arrayList);
    }

    SelectGroups(SessionLocal sessionLocal, ArrayList<Expression> arrayList) {
        this.session = sessionLocal;
        this.expressions = arrayList;
    }

    public boolean isCurrentGroup() {
        return this.currentGroupByExprData != null;
    }

    public final Object getCurrentGroupExprData(Expression expression) {
        Integer num = this.exprToIndexInGroupByData.get(expression);
        if (num == null) {
            return null;
        }
        return this.currentGroupByExprData[num.intValue()];
    }

    public final void setCurrentGroupExprData(Expression expression, Object obj) {
        Integer num = this.exprToIndexInGroupByData.get(expression);
        if (num != null) {
            if (!$assertionsDisabled && this.currentGroupByExprData[num.intValue()] != null) {
                throw new AssertionError();
            }
            this.currentGroupByExprData[num.intValue()] = obj;
            return;
        }
        Integer valueOf = Integer.valueOf(this.exprToIndexInGroupByData.size());
        this.exprToIndexInGroupByData.put(expression, valueOf);
        if (valueOf.intValue() >= this.currentGroupByExprData.length) {
            this.currentGroupByExprData = Arrays.copyOf(this.currentGroupByExprData, this.currentGroupByExprData.length * 2);
            updateCurrentGroupExprData();
        }
        this.currentGroupByExprData[valueOf.intValue()] = obj;
    }

    final Object[] createRow() {
        return new Object[Math.max(this.exprToIndexInGroupByData.size(), this.expressions.size())];
    }

    public final PartitionData getWindowExprData(DataAnalysisOperation dataAnalysisOperation, Value value) {
        if (value == null) {
            return this.windowData.get(dataAnalysisOperation);
        }
        TreeMap<Value, PartitionData> treeMap = this.windowPartitionData.get(dataAnalysisOperation);
        if (treeMap != null) {
            return treeMap.get(value);
        }
        return null;
    }

    public final void setWindowExprData(DataAnalysisOperation dataAnalysisOperation, Value value, PartitionData partitionData) {
        if (value == null) {
            PartitionData put = this.windowData.put(dataAnalysisOperation, partitionData);
            if (!$assertionsDisabled && put != null) {
                throw new AssertionError();
            }
            return;
        }
        TreeMap<Value, PartitionData> treeMap = this.windowPartitionData.get(dataAnalysisOperation);
        if (treeMap == null) {
            treeMap = new TreeMap<>(this.session.getDatabase().getCompareMode());
            this.windowPartitionData.put(dataAnalysisOperation, treeMap);
        }
        treeMap.put(value, partitionData);
    }

    public int getCurrentGroupRowId() {
        return this.currentGroupRowId;
    }

    public void reset() {
        this.currentGroupByExprData = null;
        this.exprToIndexInGroupByData.clear();
        this.windowData.clear();
        this.windowPartitionData.clear();
        this.currentGroupRowId = 0;
    }

    public void done() {
        this.currentGroupRowId = 0;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    public void resetLazy() {
        this.currentGroupByExprData = null;
        this.currentGroupRowId = 0;
    }

    public void nextLazyGroup() {
        this.currentGroupByExprData = new Object[Math.max(this.exprToIndexInGroupByData.size(), this.expressions.size())];
    }

    public void nextLazyRow() {
        this.currentGroupRowId++;
    }
}
