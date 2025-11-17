package cn.hutool.core.map.multi;

import cn.hutool.core.collection.IterUtil;
import cn.hutool.core.collection.TransIter;
import cn.hutool.core.map.multi.Table;
import cn.hutool.core.util.ObjectUtil;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/map/multi/AbsTable.class */
public abstract class AbsTable<R, C, V> implements Table<R, C, V> {
    private Collection<V> values;
    private Set<Table.Cell<R, C, V>> cellSet;

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Table) {
            Table<?, ?, ?> that = (Table) obj;
            return cellSet().equals(that.cellSet());
        }
        return false;
    }

    public int hashCode() {
        return cellSet().hashCode();
    }

    public String toString() {
        return rowMap().toString();
    }

    @Override // cn.hutool.core.map.multi.Table
    public Collection<V> values() {
        Collection<V> result = this.values;
        if (result != null) {
            return result;
        }
        Values values = new Values();
        this.values = values;
        return values;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/map/multi/AbsTable$Values.class */
    private class Values extends AbstractCollection<V> {
        private Values() {
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
        public Iterator<V> iterator() {
            return new TransIter(AbsTable.this.cellSet().iterator(), (v0) -> {
                return v0.getValue();
            });
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean contains(Object o) {
            return AbsTable.this.containsValue(o);
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public void clear() {
            AbsTable.this.clear();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public int size() {
            return AbsTable.this.size();
        }
    }

    @Override // cn.hutool.core.map.multi.Table
    public Set<Table.Cell<R, C, V>> cellSet() {
        Set<Table.Cell<R, C, V>> result = this.cellSet;
        if (result != null) {
            return result;
        }
        CellSet cellSet = new CellSet();
        this.cellSet = cellSet;
        return cellSet;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/map/multi/AbsTable$CellSet.class */
    public class CellSet extends AbstractSet<Table.Cell<R, C, V>> {
        private CellSet() {
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean contains(Object o) {
            if (o instanceof Table.Cell) {
                Table.Cell<R, C, V> cell = (Table.Cell) o;
                Map<C, V> row = AbsTable.this.getRow(cell.getRowKey());
                if (null != row) {
                    return ObjectUtil.equals(row.get(cell.getColumnKey()), cell.getValue());
                }
                return false;
            }
            return false;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean remove(Object o) {
            if (contains(o)) {
                Table.Cell<R, C, V> cell = (Table.Cell) o;
                AbsTable.this.remove(cell.getRowKey(), cell.getColumnKey());
                return false;
            }
            return false;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public void clear() {
            AbsTable.this.clear();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
        public Iterator<Table.Cell<R, C, V>> iterator() {
            return new CellIterator();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public int size() {
            return AbsTable.this.size();
        }
    }

    @Override // java.lang.Iterable
    public Iterator<Table.Cell<R, C, V>> iterator() {
        return new CellIterator();
    }

    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/map/multi/AbsTable$CellIterator.class */
    private class CellIterator implements Iterator<Table.Cell<R, C, V>> {
        final Iterator<Map.Entry<R, Map<C, V>>> rowIterator;
        Map.Entry<R, Map<C, V>> rowEntry;
        Iterator<Map.Entry<C, V>> columnIterator;

        private CellIterator() {
            this.rowIterator = AbsTable.this.rowMap().entrySet().iterator();
            this.columnIterator = IterUtil.empty();
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.rowIterator.hasNext() || this.columnIterator.hasNext();
        }

        @Override // java.util.Iterator
        public Table.Cell<R, C, V> next() {
            if (false == this.columnIterator.hasNext()) {
                this.rowEntry = this.rowIterator.next();
                this.columnIterator = this.rowEntry.getValue().entrySet().iterator();
            }
            Map.Entry<C, V> columnEntry = this.columnIterator.next();
            return new SimpleCell(this.rowEntry.getKey(), columnEntry.getKey(), columnEntry.getValue());
        }

        @Override // java.util.Iterator
        public void remove() {
            this.columnIterator.remove();
            if (this.rowEntry.getValue().isEmpty()) {
                this.rowIterator.remove();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/map/multi/AbsTable$SimpleCell.class */
    public static class SimpleCell<R, C, V> implements Table.Cell<R, C, V>, Serializable {
        private static final long serialVersionUID = 1;
        private final R rowKey;
        private final C columnKey;
        private final V value;

        SimpleCell(R rowKey, C columnKey, V value) {
            this.rowKey = rowKey;
            this.columnKey = columnKey;
            this.value = value;
        }

        @Override // cn.hutool.core.map.multi.Table.Cell
        public R getRowKey() {
            return this.rowKey;
        }

        @Override // cn.hutool.core.map.multi.Table.Cell
        public C getColumnKey() {
            return this.columnKey;
        }

        @Override // cn.hutool.core.map.multi.Table.Cell
        public V getValue() {
            return this.value;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof Table.Cell) {
                Table.Cell<?, ?, ?> other = (Table.Cell) obj;
                return ObjectUtil.equal(this.rowKey, other.getRowKey()) && ObjectUtil.equal(this.columnKey, other.getColumnKey()) && ObjectUtil.equal(this.value, other.getValue());
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(this.rowKey, this.columnKey, this.value);
        }

        public String toString() {
            return "(" + this.rowKey + "," + this.columnKey + ")=" + this.value;
        }
    }
}
