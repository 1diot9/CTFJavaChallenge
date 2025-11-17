package cn.hutool.core.map.multi;

import cn.hutool.core.builder.Builder;
import cn.hutool.core.collection.ComputeIter;
import cn.hutool.core.collection.IterUtil;
import cn.hutool.core.collection.TransIter;
import cn.hutool.core.map.AbsEntry;
import cn.hutool.core.map.MapUtil;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.lang.invoke.SerializedLambda;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.cglib.core.Constants;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/map/multi/RowKeyTable.class */
public class RowKeyTable<R, C, V> extends AbsTable<R, C, V> {
    final Map<R, Map<C, V>> raw;
    final Builder<? extends Map<C, V>> columnBuilder;
    private Map<C, Map<R, V>> columnMap;
    private Set<C> columnKeySet;

    private static /* synthetic */ Object $deserializeLambda$(SerializedLambda lambda) {
        String implMethodName = lambda.getImplMethodName();
        boolean z = -1;
        switch (implMethodName.hashCode()) {
            case -126646974:
                if (implMethodName.equals("lambda$new$9e12e91d$1")) {
                    z = false;
                    break;
                }
                break;
            case 1818100338:
                if (implMethodName.equals(Constants.CONSTRUCTOR_NAME)) {
                    z = true;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
                if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/builder/Builder") && lambda.getFunctionalInterfaceMethodName().equals(JsonPOJOBuilder.DEFAULT_BUILD_METHOD) && lambda.getFunctionalInterfaceMethodSignature().equals("()Ljava/lang/Object;") && lambda.getImplClass().equals("cn/hutool/core/map/multi/RowKeyTable") && lambda.getImplMethodSignature().equals("(Z)Ljava/util/Map;")) {
                    boolean booleanValue = ((Boolean) lambda.getCapturedArg(0)).booleanValue();
                    return () -> {
                        return MapUtil.newHashMap(booleanValue);
                    };
                }
                break;
            case true:
                if (lambda.getImplMethodKind() == 8 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/builder/Builder") && lambda.getFunctionalInterfaceMethodName().equals(JsonPOJOBuilder.DEFAULT_BUILD_METHOD) && lambda.getFunctionalInterfaceMethodSignature().equals("()Ljava/lang/Object;") && lambda.getImplClass().equals("java/util/HashMap") && lambda.getImplMethodSignature().equals("()V")) {
                    return HashMap::new;
                }
                if (lambda.getImplMethodKind() == 8 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/builder/Builder") && lambda.getFunctionalInterfaceMethodName().equals(JsonPOJOBuilder.DEFAULT_BUILD_METHOD) && lambda.getFunctionalInterfaceMethodSignature().equals("()Ljava/lang/Object;") && lambda.getImplClass().equals("java/util/HashMap") && lambda.getImplMethodSignature().equals("()V")) {
                    return HashMap::new;
                }
                break;
        }
        throw new IllegalArgumentException("Invalid lambda deserialization");
    }

    public RowKeyTable() {
        this(new HashMap());
    }

    public RowKeyTable(boolean isLinked) {
        this(MapUtil.newHashMap(isLinked), () -> {
            return MapUtil.newHashMap(isLinked);
        });
    }

    public RowKeyTable(Map<R, Map<C, V>> raw) {
        this(raw, HashMap::new);
    }

    public RowKeyTable(Map<R, Map<C, V>> raw, Builder<? extends Map<C, V>> columnMapBuilder) {
        this.raw = raw;
        this.columnBuilder = null == columnMapBuilder ? HashMap::new : columnMapBuilder;
    }

    @Override // cn.hutool.core.map.multi.Table
    public Map<R, Map<C, V>> rowMap() {
        return this.raw;
    }

    @Override // cn.hutool.core.map.multi.Table
    public V put(R rowKey, C columnKey, V value) {
        return this.raw.computeIfAbsent(rowKey, key -> {
            return this.columnBuilder.build();
        }).put(columnKey, value);
    }

    @Override // cn.hutool.core.map.multi.Table
    public V remove(R rowKey, C columnKey) {
        Map<C, V> map = getRow(rowKey);
        if (null == map) {
            return null;
        }
        V value = map.remove(columnKey);
        if (map.isEmpty()) {
            this.raw.remove(rowKey);
        }
        return value;
    }

    @Override // cn.hutool.core.map.multi.Table
    public boolean isEmpty() {
        return this.raw.isEmpty();
    }

    @Override // cn.hutool.core.map.multi.Table
    public void clear() {
        this.raw.clear();
    }

    @Override // cn.hutool.core.map.multi.Table
    public boolean containsColumn(C columnKey) {
        if (columnKey == null) {
            return false;
        }
        for (Map<C, V> map : this.raw.values()) {
            if (null != map && map.containsKey(columnKey)) {
                return true;
            }
        }
        return false;
    }

    @Override // cn.hutool.core.map.multi.Table
    public Map<C, Map<R, V>> columnMap() {
        Map<C, Map<R, V>> result = this.columnMap;
        if (result != null) {
            return result;
        }
        ColumnMap columnMap = new ColumnMap();
        this.columnMap = columnMap;
        return columnMap;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/map/multi/RowKeyTable$ColumnMap.class */
    private class ColumnMap extends AbstractMap<C, Map<R, V>> {
        private ColumnMap() {
        }

        @Override // java.util.AbstractMap, java.util.Map
        public Set<Map.Entry<C, Map<R, V>>> entrySet() {
            return new ColumnMapEntrySet();
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/map/multi/RowKeyTable$ColumnMapEntrySet.class */
    private class ColumnMapEntrySet extends AbstractSet<Map.Entry<C, Map<R, V>>> {
        private final Set<C> columnKeySet;

        private ColumnMapEntrySet() {
            this.columnKeySet = RowKeyTable.this.columnKeySet();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
        public Iterator<Map.Entry<C, Map<R, V>>> iterator() {
            return new TransIter(this.columnKeySet.iterator(), c -> {
                return MapUtil.entry(c, RowKeyTable.this.getColumn(c));
            });
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public int size() {
            return this.columnKeySet.size();
        }
    }

    @Override // cn.hutool.core.map.multi.Table
    public Set<C> columnKeySet() {
        Set<C> result = this.columnKeySet;
        if (result != null) {
            return result;
        }
        ColumnKeySet columnKeySet = new ColumnKeySet();
        this.columnKeySet = columnKeySet;
        return columnKeySet;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/map/multi/RowKeyTable$ColumnKeySet.class */
    public class ColumnKeySet extends AbstractSet<C> {
        private ColumnKeySet() {
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
        public Iterator<C> iterator() {
            return new ColumnKeyIterator();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public int size() {
            return IterUtil.size((Iterator<?>) iterator());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/map/multi/RowKeyTable$ColumnKeyIterator.class */
    public class ColumnKeyIterator extends ComputeIter<C> {
        final Map<C, V> seen;
        final Iterator<Map<C, V>> mapIterator;
        Iterator<Map.Entry<C, V>> entryIterator;

        private ColumnKeyIterator() {
            this.seen = RowKeyTable.this.columnBuilder.build();
            this.mapIterator = RowKeyTable.this.raw.values().iterator();
            this.entryIterator = IterUtil.empty();
        }

        @Override // cn.hutool.core.collection.ComputeIter
        protected C computeNext() {
            while (true) {
                if (this.entryIterator.hasNext()) {
                    Map.Entry<C, V> entry = this.entryIterator.next();
                    if (false == this.seen.containsKey(entry.getKey())) {
                        this.seen.put(entry.getKey(), entry.getValue());
                        return entry.getKey();
                    }
                } else if (this.mapIterator.hasNext()) {
                    this.entryIterator = this.mapIterator.next().entrySet().iterator();
                } else {
                    return null;
                }
            }
        }
    }

    @Override // cn.hutool.core.map.multi.Table
    public List<C> columnKeys() {
        Collection<Map<C, V>> values = this.raw.values();
        List<C> result = new ArrayList<>(values.size() * 16);
        for (Map<C, V> map : values) {
            map.forEach((key, value) -> {
                result.add(key);
            });
        }
        return result;
    }

    @Override // cn.hutool.core.map.multi.Table
    public Map<R, V> getColumn(C columnKey) {
        return new Column(columnKey);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/map/multi/RowKeyTable$Column.class */
    public class Column extends AbstractMap<R, V> {
        final C columnKey;

        Column(C columnKey) {
            this.columnKey = columnKey;
        }

        @Override // java.util.AbstractMap, java.util.Map
        public Set<Map.Entry<R, V>> entrySet() {
            return new EntrySet();
        }

        /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/map/multi/RowKeyTable$Column$EntrySet.class */
        private class EntrySet extends AbstractSet<Map.Entry<R, V>> {
            private EntrySet() {
            }

            @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
            public Iterator<Map.Entry<R, V>> iterator() {
                return new EntrySetIterator();
            }

            @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
            public int size() {
                int size = 0;
                for (Map<C, V> map : RowKeyTable.this.raw.values()) {
                    if (map.containsKey(Column.this.columnKey)) {
                        size++;
                    }
                }
                return size;
            }
        }

        /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/map/multi/RowKeyTable$Column$EntrySetIterator.class */
        private class EntrySetIterator extends ComputeIter<Map.Entry<R, V>> {
            final Iterator<Map.Entry<R, Map<C, V>>> iterator;

            private EntrySetIterator() {
                this.iterator = RowKeyTable.this.raw.entrySet().iterator();
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // cn.hutool.core.collection.ComputeIter
            public Map.Entry<R, V> computeNext() {
                while (this.iterator.hasNext()) {
                    final Map.Entry<R, Map<C, V>> entry = this.iterator.next();
                    if (entry.getValue().containsKey(Column.this.columnKey)) {
                        return new AbsEntry<R, V>() { // from class: cn.hutool.core.map.multi.RowKeyTable.Column.EntrySetIterator.1
                            @Override // java.util.Map.Entry
                            public R getKey() {
                                return (R) entry.getKey();
                            }

                            @Override // java.util.Map.Entry
                            public V getValue() {
                                return (V) ((Map) entry.getValue()).get(Column.this.columnKey);
                            }

                            @Override // cn.hutool.core.map.AbsEntry, java.util.Map.Entry
                            public V setValue(V v) {
                                return (V) ((Map) entry.getValue()).put(Column.this.columnKey, v);
                            }
                        };
                    }
                }
                return null;
            }
        }
    }
}
