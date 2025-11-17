package org.h2.command.ddl;

import java.util.Arrays;
import java.util.Iterator;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.index.Cursor;
import org.h2.result.Row;
import org.h2.schema.Schema;
import org.h2.table.Column;
import org.h2.table.Table;
import org.h2.table.TableType;
import org.h2.value.DataType;
import org.h2.value.Value;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ddl/Analyze.class */
public class Analyze extends DefineCommand {
    private int sampleRows;
    private Table table;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ddl/Analyze$SelectivityData.class */
    public static final class SelectivityData {
        private long distinctCount;
        private int size;
        private boolean zeroElement;
        private int[] elements = new int[8];
        private int maxSize = 7;

        SelectivityData() {
        }

        void add(Value value) {
            int currentSize = currentSize();
            if (currentSize >= 10000) {
                this.size = 0;
                Arrays.fill(this.elements, 0);
                this.zeroElement = false;
                this.distinctCount += currentSize;
            }
            int hashCode = value.hashCode();
            if (hashCode == 0) {
                this.zeroElement = true;
                return;
            }
            if (this.size >= this.maxSize) {
                rehash();
            }
            add(hashCode);
        }

        int getSelectivity(long j) {
            int currentSize;
            if (j == 0) {
                currentSize = 0;
            } else {
                currentSize = (int) ((100 * (this.distinctCount + currentSize())) / j);
                if (currentSize <= 0) {
                    currentSize = 1;
                }
            }
            return currentSize;
        }

        private int currentSize() {
            int i = this.size;
            if (this.zeroElement) {
                i++;
            }
            return i;
        }

        private void add(int i) {
            int length = this.elements.length;
            int i2 = length - 1;
            int i3 = i & i2;
            int i4 = 1;
            do {
                int i5 = this.elements[i3];
                if (i5 == 0) {
                    this.size++;
                    this.elements[i3] = i;
                    return;
                } else {
                    if (i5 == i) {
                        return;
                    }
                    int i6 = i4;
                    i4++;
                    i3 = (i3 + i6) & i2;
                }
            } while (i4 <= length);
        }

        private void rehash() {
            this.size = 0;
            int[] iArr = this.elements;
            int length = iArr.length << 1;
            this.elements = new int[length];
            this.maxSize = (int) ((length * 90) / 100);
            for (int i : iArr) {
                if (i != 0) {
                    add(i);
                }
            }
        }
    }

    public Analyze(SessionLocal sessionLocal) {
        super(sessionLocal);
        this.sampleRows = getDatabase().getSettings().analyzeSample;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    @Override // org.h2.command.Prepared
    public long update() {
        this.session.getUser().checkAdmin();
        Database database = getDatabase();
        if (this.table != null) {
            analyzeTable(this.session, this.table, this.sampleRows, true);
            return 0L;
        }
        Iterator<Schema> it = database.getAllSchemasNoMeta().iterator();
        while (it.hasNext()) {
            Iterator<Table> it2 = it.next().getAllTablesAndViews(null).iterator();
            while (it2.hasNext()) {
                analyzeTable(this.session, it2.next(), this.sampleRows, true);
            }
        }
        return 0L;
    }

    public static void analyzeTable(SessionLocal sessionLocal, Table table, int i, boolean z) {
        if (table.isValid() && table.getTableType() == TableType.TABLE && !table.isHidden() && sessionLocal != null) {
            if (z || (!sessionLocal.getDatabase().isSysTableLocked() && !table.hasSelectTrigger())) {
                if (!table.isTemporary() || table.isGlobalTemporary() || sessionLocal.findLocalTempTable(table.getName()) != null) {
                    if ((table.isLockedExclusively() && !table.isLockedExclusivelyBy(sessionLocal)) || !sessionLocal.getUser().hasTableRight(table, 1) || sessionLocal.getCancel() != 0) {
                        return;
                    }
                    table.lock(sessionLocal, 0);
                    Column[] columns = table.getColumns();
                    int length = columns.length;
                    if (length == 0) {
                        return;
                    }
                    Cursor find = table.getScanIndex(sessionLocal).find(sessionLocal, null, null);
                    if (find.next()) {
                        SelectivityData[] selectivityDataArr = new SelectivityData[length];
                        for (int i2 = 0; i2 < length; i2++) {
                            if (!DataType.isLargeObject(columns[i2].getType().getValueType())) {
                                selectivityDataArr[i2] = new SelectivityData();
                            }
                        }
                        long j = 0;
                        do {
                            Row row = find.get();
                            for (int i3 = 0; i3 < length; i3++) {
                                SelectivityData selectivityData = selectivityDataArr[i3];
                                if (selectivityData != null) {
                                    selectivityData.add(row.getValue(i3));
                                }
                            }
                            j++;
                            if (i > 0 && j >= i) {
                                break;
                            }
                        } while (find.next());
                        for (int i4 = 0; i4 < length; i4++) {
                            SelectivityData selectivityData2 = selectivityDataArr[i4];
                            if (selectivityData2 != null) {
                                columns[i4].setSelectivity(selectivityData2.getSelectivity(j));
                            }
                        }
                    } else {
                        for (Column column : columns) {
                            column.setSelectivity(0);
                        }
                    }
                    sessionLocal.getDatabase().updateMeta(sessionLocal, table);
                }
            }
        }
    }

    public void setTop(int i) {
        this.sampleRows = i;
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return 21;
    }
}
