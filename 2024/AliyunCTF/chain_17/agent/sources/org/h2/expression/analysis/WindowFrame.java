package org.h2.expression.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.h2.api.ErrorCode;
import org.h2.engine.SessionLocal;
import org.h2.expression.BinaryOperation;
import org.h2.expression.ValueExpression;
import org.h2.message.DbException;
import org.h2.result.SortOrder;
import org.h2.table.ColumnResolver;
import org.h2.value.Value;
import org.h2.value.ValueNull;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/analysis/WindowFrame.class */
public final class WindowFrame {
    private final WindowFrameUnits units;
    private final WindowFrameBound starting;
    private final WindowFrameBound following;
    private final WindowFrameExclusion exclusion;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/analysis/WindowFrame$Itr.class */
    public static abstract class Itr implements Iterator<Value[]> {
        final ArrayList<Value[]> orderedRows;
        int cursor;

        Itr(ArrayList<Value[]> arrayList) {
            this.orderedRows = arrayList;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/analysis/WindowFrame$PlainItr.class */
    public static class PlainItr extends Itr {
        final int endIndex;

        PlainItr(ArrayList<Value[]> arrayList, int i, int i2) {
            super(arrayList);
            this.endIndex = i2;
            this.cursor = i;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.cursor <= this.endIndex;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Iterator
        public Value[] next() {
            if (this.cursor > this.endIndex) {
                throw new NoSuchElementException();
            }
            ArrayList<Value[]> arrayList = this.orderedRows;
            int i = this.cursor;
            this.cursor = i + 1;
            return arrayList.get(i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/analysis/WindowFrame$PlainReverseItr.class */
    public static class PlainReverseItr extends Itr {
        final int startIndex;

        PlainReverseItr(ArrayList<Value[]> arrayList, int i, int i2) {
            super(arrayList);
            this.startIndex = i;
            this.cursor = i2;
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.cursor >= this.startIndex;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Iterator
        public Value[] next() {
            if (this.cursor < this.startIndex) {
                throw new NoSuchElementException();
            }
            ArrayList<Value[]> arrayList = this.orderedRows;
            int i = this.cursor;
            this.cursor = i - 1;
            return arrayList.get(i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/analysis/WindowFrame$BiItr.class */
    public static class BiItr extends PlainItr {
        final int end1;
        final int start1;

        BiItr(ArrayList<Value[]> arrayList, int i, int i2, int i3, int i4) {
            super(arrayList, i, i4);
            this.end1 = i2;
            this.start1 = i3;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.h2.expression.analysis.WindowFrame.PlainItr, java.util.Iterator
        public Value[] next() {
            if (this.cursor > this.endIndex) {
                throw new NoSuchElementException();
            }
            Value[] valueArr = this.orderedRows.get(this.cursor);
            this.cursor = this.cursor != this.end1 ? this.cursor + 1 : this.start1;
            return valueArr;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/analysis/WindowFrame$BiReverseItr.class */
    public static class BiReverseItr extends PlainReverseItr {
        final int end1;
        final int start1;

        BiReverseItr(ArrayList<Value[]> arrayList, int i, int i2, int i3, int i4) {
            super(arrayList, i, i4);
            this.end1 = i2;
            this.start1 = i3;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.h2.expression.analysis.WindowFrame.PlainReverseItr, java.util.Iterator
        public Value[] next() {
            if (this.cursor < this.startIndex) {
                throw new NoSuchElementException();
            }
            Value[] valueArr = this.orderedRows.get(this.cursor);
            this.cursor = this.cursor != this.start1 ? this.cursor - 1 : this.end1;
            return valueArr;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/analysis/WindowFrame$TriItr.class */
    public static final class TriItr extends BiItr {
        private final int end2;
        private final int start2;

        TriItr(ArrayList<Value[]> arrayList, int i, int i2, int i3, int i4, int i5, int i6) {
            super(arrayList, i, i2, i3, i6);
            this.end2 = i4;
            this.start2 = i5;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.h2.expression.analysis.WindowFrame.BiItr, org.h2.expression.analysis.WindowFrame.PlainItr, java.util.Iterator
        public Value[] next() {
            if (this.cursor > this.endIndex) {
                throw new NoSuchElementException();
            }
            Value[] valueArr = this.orderedRows.get(this.cursor);
            this.cursor = this.cursor != this.end1 ? this.cursor != this.end2 ? this.cursor + 1 : this.start2 : this.start1;
            return valueArr;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/expression/analysis/WindowFrame$TriReverseItr.class */
    public static final class TriReverseItr extends BiReverseItr {
        private final int end2;
        private final int start2;

        TriReverseItr(ArrayList<Value[]> arrayList, int i, int i2, int i3, int i4, int i5, int i6) {
            super(arrayList, i, i2, i3, i6);
            this.end2 = i4;
            this.start2 = i5;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.h2.expression.analysis.WindowFrame.BiReverseItr, org.h2.expression.analysis.WindowFrame.PlainReverseItr, java.util.Iterator
        public Value[] next() {
            if (this.cursor < this.startIndex) {
                throw new NoSuchElementException();
            }
            Value[] valueArr = this.orderedRows.get(this.cursor);
            this.cursor = this.cursor != this.start1 ? this.cursor != this.start2 ? this.cursor - 1 : this.end2 : this.end1;
            return valueArr;
        }
    }

    public static Iterator<Value[]> iterator(Window window, SessionLocal sessionLocal, ArrayList<Value[]> arrayList, SortOrder sortOrder, int i, boolean z) {
        WindowFrame windowFrame = window.getWindowFrame();
        if (windowFrame != null) {
            return windowFrame.iterator(sessionLocal, arrayList, sortOrder, i, z);
        }
        int size = arrayList.size() - 1;
        return plainIterator(arrayList, 0, window.getOrderBy() == null ? size : toGroupEnd(arrayList, sortOrder, i, size), z);
    }

    public static int getEndIndex(Window window, SessionLocal sessionLocal, ArrayList<Value[]> arrayList, SortOrder sortOrder, int i) {
        WindowFrame windowFrame = window.getWindowFrame();
        if (windowFrame != null) {
            return windowFrame.getEndIndex(sessionLocal, arrayList, sortOrder, i);
        }
        int size = arrayList.size() - 1;
        return window.getOrderBy() == null ? size : toGroupEnd(arrayList, sortOrder, i, size);
    }

    private static Iterator<Value[]> plainIterator(ArrayList<Value[]> arrayList, int i, int i2, boolean z) {
        if (i2 < i) {
            return Collections.emptyIterator();
        }
        return z ? new PlainReverseItr(arrayList, i, i2) : new PlainItr(arrayList, i, i2);
    }

    private static Iterator<Value[]> biIterator(ArrayList<Value[]> arrayList, int i, int i2, int i3, int i4, boolean z) {
        return z ? new BiReverseItr(arrayList, i, i2, i3, i4) : new BiItr(arrayList, i, i2, i3, i4);
    }

    private static Iterator<Value[]> triIterator(ArrayList<Value[]> arrayList, int i, int i2, int i3, int i4, int i5, int i6, boolean z) {
        return z ? new TriReverseItr(arrayList, i, i2, i3, i4, i5, i6) : new TriItr(arrayList, i, i2, i3, i4, i5, i6);
    }

    private static int toGroupStart(ArrayList<Value[]> arrayList, SortOrder sortOrder, int i, int i2) {
        Value[] valueArr = arrayList.get(i);
        while (i > i2 && sortOrder.compare(valueArr, arrayList.get(i - 1)) == 0) {
            i--;
        }
        return i;
    }

    private static int toGroupEnd(ArrayList<Value[]> arrayList, SortOrder sortOrder, int i, int i2) {
        Value[] valueArr = arrayList.get(i);
        while (i < i2 && sortOrder.compare(valueArr, arrayList.get(i + 1)) == 0) {
            i++;
        }
        return i;
    }

    private static int getIntOffset(WindowFrameBound windowFrameBound, Value[] valueArr, SessionLocal sessionLocal) {
        int i;
        Value value = windowFrameBound.isVariable() ? valueArr[windowFrameBound.getExpressionIndex()] : windowFrameBound.getValue().getValue(sessionLocal);
        if (value == ValueNull.INSTANCE || (i = value.getInt()) < 0) {
            throw DbException.get(ErrorCode.INVALID_PRECEDING_OR_FOLLOWING_1, value.getTraceSQL());
        }
        return i;
    }

    private static Value[] getCompareRow(SessionLocal sessionLocal, ArrayList<Value[]> arrayList, SortOrder sortOrder, int i, WindowFrameBound windowFrameBound, boolean z) {
        Value convertTo;
        int i2 = sortOrder.getQueryColumnIndexes()[0];
        Value[] valueArr = arrayList.get(i);
        Value value = valueArr[i2];
        int valueType = value.getValueType();
        Value valueOffset = getValueOffset(windowFrameBound, arrayList.get(i), sessionLocal);
        switch (valueType) {
            case 0:
                convertTo = ValueNull.INSTANCE;
                break;
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            default:
                throw DbException.getInvalidValueException("unsupported type of sort key for RANGE units", value.getTraceSQL());
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
                try {
                    convertTo = new BinaryOperation(z ^ ((sortOrder.getSortTypes()[0] & 1) != 0) ? BinaryOperation.OpType.PLUS : BinaryOperation.OpType.MINUS, ValueExpression.get(value), ValueExpression.get(valueOffset)).optimize(sessionLocal).getValue(sessionLocal).convertTo(valueType);
                    break;
                } catch (DbException e) {
                    switch (e.getErrorCode()) {
                        case ErrorCode.NUMERIC_VALUE_OUT_OF_RANGE_1 /* 22003 */:
                        case ErrorCode.NUMERIC_VALUE_OUT_OF_RANGE_2 /* 22004 */:
                            return null;
                        default:
                            throw e;
                    }
                }
        }
        Value[] valueArr2 = (Value[]) valueArr.clone();
        valueArr2[i2] = convertTo;
        return valueArr2;
    }

    private static Value getValueOffset(WindowFrameBound windowFrameBound, Value[] valueArr, SessionLocal sessionLocal) {
        Value value = windowFrameBound.isVariable() ? valueArr[windowFrameBound.getExpressionIndex()] : windowFrameBound.getValue().getValue(sessionLocal);
        if (value == ValueNull.INSTANCE || value.getSignum() < 0) {
            throw DbException.get(ErrorCode.INVALID_PRECEDING_OR_FOLLOWING_1, value.getTraceSQL());
        }
        return value;
    }

    public WindowFrame(WindowFrameUnits windowFrameUnits, WindowFrameBound windowFrameBound, WindowFrameBound windowFrameBound2, WindowFrameExclusion windowFrameExclusion) {
        this.units = windowFrameUnits;
        this.starting = windowFrameBound;
        if (windowFrameBound2 != null && windowFrameBound2.getType() == WindowFrameBoundType.CURRENT_ROW) {
            windowFrameBound2 = null;
        }
        this.following = windowFrameBound2;
        this.exclusion = windowFrameExclusion;
    }

    public WindowFrameUnits getUnits() {
        return this.units;
    }

    public WindowFrameBound getStarting() {
        return this.starting;
    }

    public WindowFrameBound getFollowing() {
        return this.following;
    }

    public WindowFrameExclusion getExclusion() {
        return this.exclusion;
    }

    public boolean isValid() {
        WindowFrameBoundType type = this.starting.getType();
        WindowFrameBoundType type2 = this.following != null ? this.following.getType() : WindowFrameBoundType.CURRENT_ROW;
        return (type == WindowFrameBoundType.UNBOUNDED_FOLLOWING || type2 == WindowFrameBoundType.UNBOUNDED_PRECEDING || type.compareTo(type2) > 0) ? false : true;
    }

    public boolean isVariableBounds() {
        if (this.starting.isVariable()) {
            return true;
        }
        if (this.following != null && this.following.isVariable()) {
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void mapColumns(ColumnResolver columnResolver, int i, int i2) {
        this.starting.mapColumns(columnResolver, i, i2);
        if (this.following != null) {
            this.following.mapColumns(columnResolver, i, i2);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void optimize(SessionLocal sessionLocal) {
        this.starting.optimize(sessionLocal);
        if (this.following != null) {
            this.following.optimize(sessionLocal);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateAggregate(SessionLocal sessionLocal, int i) {
        this.starting.updateAggregate(sessionLocal, i);
        if (this.following != null) {
            this.following.updateAggregate(sessionLocal, i);
        }
    }

    public Iterator<Value[]> iterator(SessionLocal sessionLocal, ArrayList<Value[]> arrayList, SortOrder sortOrder, int i, boolean z) {
        int groupEnd;
        int index = getIndex(sessionLocal, arrayList, sortOrder, i, this.starting, false);
        if (this.following != null) {
            groupEnd = getIndex(sessionLocal, arrayList, sortOrder, i, this.following, true);
        } else {
            groupEnd = this.units == WindowFrameUnits.ROWS ? i : toGroupEnd(arrayList, sortOrder, i, arrayList.size() - 1);
        }
        int i2 = groupEnd;
        if (i2 < index) {
            return Collections.emptyIterator();
        }
        int size = arrayList.size();
        if (index >= size || i2 < 0) {
            return Collections.emptyIterator();
        }
        if (index < 0) {
            index = 0;
        }
        if (i2 >= size) {
            i2 = size - 1;
        }
        if (this.exclusion != WindowFrameExclusion.EXCLUDE_NO_OTHERS) {
            return complexIterator(arrayList, sortOrder, i, index, i2, z);
        }
        return plainIterator(arrayList, index, i2, z);
    }

    public int getStartIndex(SessionLocal sessionLocal, ArrayList<Value[]> arrayList, SortOrder sortOrder, int i) {
        if (this.exclusion != WindowFrameExclusion.EXCLUDE_NO_OTHERS) {
            throw new UnsupportedOperationException();
        }
        int index = getIndex(sessionLocal, arrayList, sortOrder, i, this.starting, false);
        if (index < 0) {
            index = 0;
        }
        return index;
    }

    private int getEndIndex(SessionLocal sessionLocal, ArrayList<Value[]> arrayList, SortOrder sortOrder, int i) {
        int groupEnd;
        if (this.exclusion != WindowFrameExclusion.EXCLUDE_NO_OTHERS) {
            throw new UnsupportedOperationException();
        }
        if (this.following != null) {
            groupEnd = getIndex(sessionLocal, arrayList, sortOrder, i, this.following, true);
        } else {
            groupEnd = this.units == WindowFrameUnits.ROWS ? i : toGroupEnd(arrayList, sortOrder, i, arrayList.size() - 1);
        }
        int i2 = groupEnd;
        int size = arrayList.size();
        if (i2 >= size) {
            i2 = size - 1;
        }
        return i2;
    }

    private int getIndex(SessionLocal sessionLocal, ArrayList<Value[]> arrayList, SortOrder sortOrder, int i, WindowFrameBound windowFrameBound, boolean z) {
        int i2;
        int size = arrayList.size();
        int i3 = size - 1;
        switch (windowFrameBound.getType()) {
            case UNBOUNDED_PRECEDING:
                i2 = -1;
                break;
            case PRECEDING:
                switch (this.units) {
                    case ROWS:
                        int intOffset = getIntOffset(windowFrameBound, arrayList.get(i), sessionLocal);
                        i2 = intOffset > i ? -1 : i - intOffset;
                        break;
                    case GROUPS:
                        int intOffset2 = getIntOffset(windowFrameBound, arrayList.get(i), sessionLocal);
                        if (!z) {
                            int groupStart = toGroupStart(arrayList, sortOrder, i, 0);
                            while (true) {
                                i2 = groupStart;
                                if (intOffset2 > 0 && i2 > 0) {
                                    intOffset2--;
                                    groupStart = toGroupStart(arrayList, sortOrder, i2 - 1, 0);
                                }
                            }
                            if (intOffset2 > 0) {
                                i2 = -1;
                                break;
                            }
                        } else if (intOffset2 == 0) {
                            i2 = toGroupEnd(arrayList, sortOrder, i, i3);
                            break;
                        } else {
                            int i4 = i;
                            while (true) {
                                i2 = i4;
                                if (intOffset2 > 0 && i2 >= 0) {
                                    intOffset2--;
                                    i4 = toGroupStart(arrayList, sortOrder, i2, 0) - 1;
                                }
                            }
                        }
                        break;
                    case RANGE:
                        Value[] compareRow = getCompareRow(sessionLocal, arrayList, sortOrder, i, windowFrameBound, false);
                        if (compareRow != null) {
                            i2 = Collections.binarySearch(arrayList, compareRow, sortOrder);
                            if (i2 >= 0) {
                                if (!z) {
                                    while (i2 > 0 && sortOrder.compare(compareRow, arrayList.get(i2 - 1)) == 0) {
                                        i2--;
                                    }
                                } else {
                                    while (i2 < i3 && sortOrder.compare(compareRow, arrayList.get(i2 + 1)) == 0) {
                                        i2++;
                                    }
                                }
                            } else {
                                i2 ^= -1;
                                if (!z) {
                                    if (i2 == 0) {
                                        i2 = -1;
                                        break;
                                    }
                                } else {
                                    i2--;
                                    break;
                                }
                            }
                        } else {
                            i2 = -1;
                            break;
                        }
                        break;
                    default:
                        throw DbException.getUnsupportedException("units=" + this.units);
                }
            case CURRENT_ROW:
                switch (this.units) {
                    case ROWS:
                        i2 = i;
                        break;
                    case GROUPS:
                    case RANGE:
                        i2 = z ? toGroupEnd(arrayList, sortOrder, i, i3) : toGroupStart(arrayList, sortOrder, i, 0);
                        break;
                    default:
                        throw DbException.getUnsupportedException("units=" + this.units);
                }
            case FOLLOWING:
                switch (this.units) {
                    case ROWS:
                        int intOffset3 = getIntOffset(windowFrameBound, arrayList.get(i), sessionLocal);
                        i2 = intOffset3 > i3 - i ? size : i + intOffset3;
                        break;
                    case GROUPS:
                        int intOffset4 = getIntOffset(windowFrameBound, arrayList.get(i), sessionLocal);
                        if (z) {
                            int groupEnd = toGroupEnd(arrayList, sortOrder, i, i3);
                            while (true) {
                                i2 = groupEnd;
                                if (intOffset4 > 0 && i2 < i3) {
                                    intOffset4--;
                                    groupEnd = toGroupEnd(arrayList, sortOrder, i2 + 1, i3);
                                }
                            }
                            if (intOffset4 > 0) {
                                i2 = size;
                                break;
                            }
                        } else if (intOffset4 == 0) {
                            i2 = toGroupStart(arrayList, sortOrder, i, 0);
                            break;
                        } else {
                            int i5 = i;
                            while (true) {
                                i2 = i5;
                                if (intOffset4 > 0 && i2 <= i3) {
                                    intOffset4--;
                                    i5 = toGroupEnd(arrayList, sortOrder, i2, i3) + 1;
                                }
                            }
                        }
                        break;
                    case RANGE:
                        Value[] compareRow2 = getCompareRow(sessionLocal, arrayList, sortOrder, i, windowFrameBound, true);
                        if (compareRow2 != null) {
                            i2 = Collections.binarySearch(arrayList, compareRow2, sortOrder);
                            if (i2 >= 0) {
                                if (z) {
                                    while (i2 < i3 && sortOrder.compare(compareRow2, arrayList.get(i2 + 1)) == 0) {
                                        i2++;
                                    }
                                } else {
                                    while (i2 > 0 && sortOrder.compare(compareRow2, arrayList.get(i2 - 1)) == 0) {
                                        i2--;
                                    }
                                }
                            } else {
                                i2 ^= -1;
                                if (z && i2 != size) {
                                    i2--;
                                    break;
                                }
                            }
                        } else {
                            i2 = size;
                            break;
                        }
                        break;
                    default:
                        throw DbException.getUnsupportedException("units=" + this.units);
                }
            case UNBOUNDED_FOLLOWING:
                i2 = size;
                break;
            default:
                throw DbException.getUnsupportedException("window frame bound type=" + windowFrameBound.getType());
        }
        return i2;
    }

    private Iterator<Value[]> complexIterator(ArrayList<Value[]> arrayList, SortOrder sortOrder, int i, int i2, int i3, boolean z) {
        if (this.exclusion == WindowFrameExclusion.EXCLUDE_CURRENT_ROW) {
            if (i >= i2 && i <= i3) {
                if (i == i2) {
                    i2++;
                } else if (i == i3) {
                    i3--;
                } else {
                    return biIterator(arrayList, i2, i - 1, i + 1, i3, z);
                }
            }
        } else {
            int groupStart = toGroupStart(arrayList, sortOrder, i, i2);
            int groupEnd = toGroupEnd(arrayList, sortOrder, i, i3);
            boolean z2 = this.exclusion == WindowFrameExclusion.EXCLUDE_TIES;
            if (z2) {
                if (i == groupStart) {
                    groupStart++;
                    z2 = false;
                } else if (i == groupEnd) {
                    groupEnd--;
                    z2 = false;
                }
            }
            if (groupStart <= groupEnd && groupEnd >= i2 && groupStart <= i3) {
                if (z2) {
                    if (i2 == groupStart) {
                        if (i3 == groupEnd) {
                            return Collections.singleton(arrayList.get(i)).iterator();
                        }
                        return biIterator(arrayList, i, i, groupEnd + 1, i3, z);
                    }
                    if (i3 == groupEnd) {
                        return biIterator(arrayList, i2, groupStart - 1, i, i, z);
                    }
                    return triIterator(arrayList, i2, groupStart - 1, i, i, groupEnd + 1, i3, z);
                }
                if (i2 >= groupStart) {
                    i2 = groupEnd + 1;
                } else if (i3 <= groupEnd) {
                    i3 = groupStart - 1;
                } else {
                    return biIterator(arrayList, i2, groupStart - 1, groupEnd + 1, i3, z);
                }
            }
        }
        return plainIterator(arrayList, i2, i3, z);
    }

    public StringBuilder getSQL(StringBuilder sb, int i) {
        sb.append(this.units.getSQL());
        if (this.following == null) {
            sb.append(' ');
            this.starting.getSQL(sb, false, i);
        } else {
            sb.append(" BETWEEN ");
            this.starting.getSQL(sb, false, i).append(" AND ");
            this.following.getSQL(sb, true, i);
        }
        if (this.exclusion != WindowFrameExclusion.EXCLUDE_NO_OTHERS) {
            sb.append(' ').append(this.exclusion.getSQL());
        }
        return sb;
    }
}
