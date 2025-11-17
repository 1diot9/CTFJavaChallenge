package org.jooq.impl;

import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jooq.Clause;
import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.GeneratorStatementType;
import org.jooq.Record;
import org.jooq.RenderContext;
import org.jooq.Row;
import org.jooq.SQLDialect;
import org.jooq.Select;
import org.jooq.Table;
import org.jooq.impl.AbstractStoreQuery;
import org.jooq.impl.QOM;
import org.jooq.impl.Tools;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/FieldMapsForInsert.class */
public final class FieldMapsForInsert extends AbstractQueryPart implements QOM.UNotYetImplemented {
    static final Set<SQLDialect> CASTS_NEEDED = SQLDialect.supportedBy(SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB);
    static final Set<SQLDialect> CASTS_NEEDED_FOR_MERGE = SQLDialect.supportedBy(SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB);
    static final Set<SQLDialect> NO_SUPPORT_DEFAULT_EXPRESSION = SQLDialect.supportedBy(SQLDialect.SQLITE);
    final Table<?> table;
    int rows;
    int nextRow = -1;
    final Map<Field<?>, List<Field<?>>> values = new LinkedHashMap();
    final Map<Field<?>, Field<?>> empty = new LinkedHashMap();

    /* JADX INFO: Access modifiers changed from: package-private */
    public FieldMapsForInsert(Table<?> table) {
        this.table = table;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void clear() {
        this.empty.clear();
        this.values.clear();
        this.rows = 0;
        this.nextRow = -1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void from(FieldMapsForInsert i) {
        this.empty.putAll(i.empty);
        for (Map.Entry<Field<?>, List<Field<?>>> e : i.values.entrySet()) {
            this.values.put(e.getKey(), new ArrayList(e.getValue()));
        }
        this.rows = i.rows;
        this.nextRow = i.nextRow;
    }

    /* JADX WARN: Type inference failed for: r0v16, types: [org.jooq.Context] */
    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (!isExecutable()) {
            ctx.formatSeparator().start(Clause.INSERT_VALUES).visit(Keywords.K_DEFAULT_VALUES).end(Clause.INSERT_VALUES);
            return;
        }
        if (this.rows == 1 && supportsValues(ctx)) {
            toSQLValues(ctx);
            return;
        }
        switch (ctx.family()) {
            case TRINO:
            case MARIADB:
                if (supportsValues(ctx)) {
                    toSQLValues(ctx);
                    return;
                } else {
                    toSQLInsertSelect(ctx, insertSelect(ctx, GeneratorStatementType.INSERT));
                    return;
                }
            case FIREBIRD:
                toSQLInsertSelect(ctx, insertSelect(ctx, GeneratorStatementType.INSERT));
                return;
            default:
                toSQLValues(ctx);
                return;
        }
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    private final void toSQLValues(Context<?> ctx) {
        ctx.formatSeparator().start(Clause.INSERT_VALUES).visit(Keywords.K_VALUES).sql(' ');
        toSQL92Values(ctx);
        ctx.end(Clause.INSERT_VALUES);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    public static final void toSQLInsertSelect(Context<?> ctx, Select<?> select) {
        ctx.formatSeparator().start(Clause.INSERT_SELECT).visit(select).end(Clause.INSERT_SELECT);
    }

    private final boolean supportsValues(Context<?> ctx) {
        switch (ctx.family()) {
            case TRINO:
                for (List<Field<?>> row : this.values.values()) {
                    Iterator<Field<?>> it = row.iterator();
                    while (it.hasNext()) {
                        if (it.next() instanceof ScalarSubquery) {
                            return false;
                        }
                    }
                }
                return true;
            case MARIADB:
                for (List<Field<?>> row2 : this.values.values()) {
                    for (Field<?> value : row2) {
                        if (value instanceof ScalarSubquery) {
                            ScalarSubquery<?> s = (ScalarSubquery) value;
                            if (Tools.containsTable((Iterable<? extends Table<?>>) s.query.$from(), this.table, false)) {
                                return false;
                            }
                        }
                    }
                }
                return true;
            default:
                return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Select<Record> insertSelect(Context<?> ctx, GeneratorStatementType statementType) {
        Select<Record> unionAll;
        Select<Record> select = null;
        Map<Field<?>, List<Field<?>>> v = valuesFlattened(ctx, statementType);
        boolean needsCast = CASTS_NEEDED_FOR_MERGE.contains(ctx.dialect()) && ctx.data(Tools.ExtendedDataKey.DATA_INSERT_ON_DUPLICATE_KEY_UPDATE) != null;
        for (int i = 0; i < this.rows; i++) {
            int row = i;
            Select<Record> iteration = DSL.select(Tools.map(v.entrySet(), e -> {
                return patchDefault0(castNullsIfNeeded(ctx, needsCast, (Field) ((List) e.getValue()).get(row)), (Field) e.getKey());
            }));
            if (select == null) {
                unionAll = iteration;
            } else {
                unionAll = select.unionAll(iteration);
            }
            select = unionAll;
        }
        return select;
    }

    final Field<?> castNullsIfNeeded(Context<?> ctx, boolean needsCast, Field<?> f) {
        if (needsCast && (f instanceof Val)) {
            Val<?> val = (Val) f;
            if (val.isInline(ctx) && val.getValue() == null) {
                return f.cast(f.getDataType());
            }
        }
        return f;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Type inference failed for: r0v17, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v29, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v32, types: [org.jooq.Context] */
    public final void toSQL92Values(Context<?> ctx) {
        boolean indent = this.values.size() > 1;
        RenderContext.CastMode previous = ctx.castMode();
        if (!CASTS_NEEDED.contains(ctx.dialect())) {
            ctx.castMode(RenderContext.CastMode.NEVER);
        }
        for (int row = 0; row < this.rows; row++) {
            if (row > 0) {
                ctx.sql(", ");
            }
            ctx.start(Clause.FIELD_ROW).sql('(');
            if (indent) {
                ctx.formatIndentStart();
            }
            String separator = "";
            for (Map.Entry<Field<?>, List<Field<?>>> e : valuesFlattened(ctx, GeneratorStatementType.INSERT).entrySet()) {
                List<Field<?>> list = e.getValue();
                ctx.sql(separator);
                if (indent) {
                    ctx.formatNewLine();
                }
                ctx.visit(patchDefault(ctx, list.get(row), e.getKey()));
                separator = ", ";
            }
            if (indent) {
                ctx.formatIndentEnd().formatNewLine();
            }
            ctx.sql(')').end(Clause.FIELD_ROW);
        }
        if (!CASTS_NEEDED.contains(ctx.dialect())) {
            ctx.castMode(previous);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Field<?> patchDefault(Context<?> ctx, Field<?> d, Field<?> f) {
        if (NO_SUPPORT_DEFAULT_EXPRESSION.contains(ctx.dialect())) {
            return patchDefault0(d, f);
        }
        return d;
    }

    static final Field<?> patchDefault0(Field<?> d, Field<?> f) {
        if (d instanceof Default) {
            return (Field) Tools.orElse(f.getDataType().default_(), () -> {
                return DSL.inline((Object) null, f);
            });
        }
        return d;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void addFields(Collection<?> fields) {
        ArrayList arrayList;
        if (this.rows == 0) {
            newRecord();
        }
        initNextRow();
        for (Object field : fields) {
            Field<?> f = Tools.tableField(this.table, field);
            Field<?> e = this.empty.computeIfAbsent(f, LazyVal::new);
            if (!this.values.containsKey(f)) {
                Map<Field<?>, List<Field<?>>> map = this.values;
                if (this.rows > 0) {
                    arrayList = new ArrayList(Collections.nCopies(this.rows, e));
                } else {
                    arrayList = new ArrayList();
                }
                map.put(f, arrayList);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void set(Collection<? extends Field<?>> fields) {
        initNextRow();
        Iterator<? extends Field<?>> it1 = fields.iterator();
        Iterator<List<Field<?>>> it2 = this.values.values().iterator();
        while (it1.hasNext() && it2.hasNext()) {
            it2.next().set(this.rows - 1, it1.next());
        }
        if (it1.hasNext() || it2.hasNext()) {
            throw new IllegalArgumentException("Added record size (" + fields.size() + ") must match fields size (" + this.values.size() + ")");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final <T> Field<T> set(Field<T> field, Field<T> value) {
        addFields(Collections.singletonList(field));
        return (Field) this.values.get(field).set(this.rows - 1, value);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void set(Map<?, ?> map) {
        addFields(map.keySet());
        map.forEach((k, v) -> {
            Field<?> field = Tools.tableField(this.table, k);
            this.values.get(field).set(this.rows - 1, Tools.field(v, field));
        });
    }

    private final void initNextRow() {
        if (this.rows == this.nextRow) {
            Iterator<List<Field<?>>> v = this.values.values().iterator();
            Iterator<Field<?>> e = this.empty.values().iterator();
            while (v.hasNext() && e.hasNext()) {
                v.next().add(e.next());
            }
            this.rows++;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void newRecord() {
        if (this.nextRow < this.rows) {
            this.nextRow++;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final List<Row> rows() {
        final List<Map<Field<?>, Field<?>>> maps = maps();
        return new AbstractList<Row>() { // from class: org.jooq.impl.FieldMapsForInsert.1
            @Override // java.util.AbstractList, java.util.List
            public Row get(int index) {
                return Tools.row0((Field<?>[]) ((Map) maps.get(index)).values().toArray(Tools.EMPTY_FIELD));
            }

            @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
            public int size() {
                return FieldMapsForInsert.this.rows;
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final List<Map<Field<?>, Field<?>>> maps() {
        return new AbstractList<Map<Field<?>, Field<?>>>() { // from class: org.jooq.impl.FieldMapsForInsert.2
            @Override // java.util.AbstractList, java.util.List
            public Map<Field<?>, Field<?>> get(int index) {
                return FieldMapsForInsert.this.map(index);
            }

            @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
            public int size() {
                return FieldMapsForInsert.this.rows;
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.jooq.impl.FieldMapsForInsert$3, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/FieldMapsForInsert$3.class */
    public class AnonymousClass3 extends AbstractMap<Field<?>, Field<?>> {
        transient Set<Map.Entry<Field<?>, Field<?>>> entrySet;
        final /* synthetic */ int val$index;

        AnonymousClass3(int i) {
            this.val$index = i;
        }

        @Override // java.util.AbstractMap, java.util.Map
        public Set<Map.Entry<Field<?>, Field<?>>> entrySet() {
            if (this.entrySet == null) {
                this.entrySet = new EntrySet();
            }
            return this.entrySet;
        }

        @Override // java.util.AbstractMap, java.util.Map
        public boolean containsKey(Object key) {
            return FieldMapsForInsert.this.values.containsKey(key);
        }

        @Override // java.util.AbstractMap, java.util.Map
        public boolean containsValue(Object value) {
            Collection<List<Field<?>>> values = FieldMapsForInsert.this.values.values();
            int i = this.val$index;
            return Tools.anyMatch(values, list -> {
                return ((Field) list.get(i)).equals(value);
            });
        }

        @Override // java.util.AbstractMap, java.util.Map
        public Field<?> get(Object key) {
            List<Field<?>> list = FieldMapsForInsert.this.values.get(key);
            if (list == null) {
                return null;
            }
            return list.get(this.val$index);
        }

        @Override // java.util.AbstractMap, java.util.Map
        public Field<?> put(Field<?> key, Field<?> value) {
            return FieldMapsForInsert.this.set(key, value);
        }

        @Override // java.util.AbstractMap, java.util.Map
        public Field<?> remove(Object key) {
            List<Field<?>> list = FieldMapsForInsert.this.values.get(key);
            FieldMapsForInsert.this.values.remove(key);
            if (list == null) {
                return null;
            }
            return list.get(this.val$index);
        }

        @Override // java.util.AbstractMap, java.util.Map
        public Set<Field<?>> keySet() {
            return FieldMapsForInsert.this.values.keySet();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: org.jooq.impl.FieldMapsForInsert$3$EntrySet */
        /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/FieldMapsForInsert$3$EntrySet.class */
        public final class EntrySet extends AbstractSet<Map.Entry<Field<?>, Field<?>>> {
            EntrySet() {
            }

            @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
            public final int size() {
                return FieldMapsForInsert.this.values.size();
            }

            @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
            public final void clear() {
                FieldMapsForInsert.this.values.clear();
            }

            @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
            public final Iterator<Map.Entry<Field<?>, Field<?>>> iterator() {
                return new Iterator<Map.Entry<Field<?>, Field<?>>>() { // from class: org.jooq.impl.FieldMapsForInsert.3.EntrySet.1
                    final Iterator<Map.Entry<Field<?>, List<Field<?>>>> delegate;

                    {
                        this.delegate = FieldMapsForInsert.this.values.entrySet().iterator();
                    }

                    @Override // java.util.Iterator
                    public boolean hasNext() {
                        return this.delegate.hasNext();
                    }

                    /* JADX WARN: Can't rename method to resolve collision */
                    @Override // java.util.Iterator
                    public Map.Entry<Field<?>, Field<?>> next() {
                        Map.Entry<Field<?>, List<Field<?>>> entry = this.delegate.next();
                        return new AbstractMap.SimpleImmutableEntry(entry.getKey(), entry.getValue().get(AnonymousClass3.this.val$index));
                    }

                    @Override // java.util.Iterator
                    public void remove() {
                        this.delegate.remove();
                    }
                };
            }
        }
    }

    final Map<Field<?>, Field<?>> map(int index) {
        return new AnonymousClass3(index);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Map<Field<?>, Field<?>> lastMap() {
        return map(this.rows - 1);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean isExecutable() {
        return this.rows > 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Set<Field<?>> toSQLReferenceKeys(Context<?> ctx) {
        if (!isExecutable()) {
            return Collections.emptySet();
        }
        if (this.values.isEmpty()) {
            return Collections.emptySet();
        }
        for (Field<?> field : this.values.keySet()) {
            if (!(field instanceof AbstractStoreQuery.UnknownField)) {
                Set<Field<?>> fields = keysFlattened(ctx, GeneratorStatementType.INSERT);
                if (!fields.isEmpty()) {
                    ctx.data(Tools.BooleanDataKey.DATA_STORE_ASSIGNMENT, true, c -> {
                        c.sql(" (").visit(QueryPartCollectionView.wrap(fields).qualify(false)).sql(')');
                    });
                }
                return fields;
            }
        }
        return Collections.emptySet();
    }

    private static final <E> Iterable<E> removeReadonly(Context<?> ctx, Iterable<E> it, java.util.function.Function<? super E, ? extends Field<?>> f) {
        return it;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Set<Field<?>> keysFlattened(Context<?> ctx, GeneratorStatementType statementType) {
        return valuesFlattened(ctx, statementType).keySet();
    }

    final Map<Field<?>, List<Field<?>>> valuesFlattened(Context<?> ctx, GeneratorStatementType statementType) {
        Map<Field<?>, List<Field<?>>> result = new LinkedHashMap<>();
        for (Map.Entry<Field<?>, List<Field<?>>> entry : removeReadonly(ctx, this.values.entrySet(), (v0) -> {
            return v0.getKey();
        })) {
            Field<?> key = entry.getKey();
            DataType<?> keyType = key.getDataType();
            List<Field<?>> value = entry.getValue();
            if (keyType.isEmbeddable()) {
                List<Iterator<? extends Field<?>>> valueFlattened = new ArrayList<>(value.size());
                Iterator<Field<?>> it = value.iterator();
                while (it.hasNext()) {
                    valueFlattened.add(Tools.flatten(it.next()).iterator());
                }
                for (Field<?> k : Tools.flatten(key)) {
                    List<Field<?>> list = new ArrayList<>(value.size());
                    for (Iterator<? extends Field<?>> v : valueFlattened) {
                        list.add(v.hasNext() ? (Field) v.next() : null);
                    }
                    result.put(k, list);
                }
            } else {
                result.put(key, value);
            }
        }
        return result;
    }
}
