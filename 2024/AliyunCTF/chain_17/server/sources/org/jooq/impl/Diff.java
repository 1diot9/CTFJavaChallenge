package org.jooq.impl;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.jooq.AlterSequenceFlagsStep;
import org.jooq.Catalog;
import org.jooq.Check;
import org.jooq.Configuration;
import org.jooq.DDLExportConfiguration;
import org.jooq.DSLContext;
import org.jooq.DataType;
import org.jooq.Domain;
import org.jooq.DropTableFinalStep;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Index;
import org.jooq.Meta;
import org.jooq.MigrationConfiguration;
import org.jooq.Name;
import org.jooq.Named;
import org.jooq.Nullability;
import org.jooq.Queries;
import org.jooq.Query;
import org.jooq.SQLDialect;
import org.jooq.Schema;
import org.jooq.Sequence;
import org.jooq.Table;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Diff.class */
public final class Diff {
    private static final Set<SQLDialect> NO_SUPPORT_PK_NAMES = SQLDialect.supportedBy(SQLDialect.IGNITE, SQLDialect.MARIADB, SQLDialect.MYSQL);
    private final MigrationConfiguration migrateConf;
    private final DDLExportConfiguration exportConf;
    private final DSLContext ctx;
    private final Meta meta1;
    private final Meta meta2;
    private final DDL ddl;
    private final Merge<Table<?>> MERGE_TABLE = new Merge<Table<?>>() { // from class: org.jooq.impl.Diff.1
        @Override // org.jooq.impl.Diff.Merge
        public void merge(DiffResult r, Table<?> t1, Table<?> t2) {
            boolean m1 = t1.getTableType() == TableOptions.TableType.MATERIALIZED_VIEW;
            boolean m2 = t2.getTableType() == TableOptions.TableType.MATERIALIZED_VIEW;
            boolean v1 = t1.getTableType() == TableOptions.TableType.VIEW;
            boolean v2 = t2.getTableType() == TableOptions.TableType.VIEW;
            if ((v1 && v2) || (m1 && m2)) {
                if (!Arrays.equals(t1.fields(), t2.fields()) || ((t2.getOptions().select() != null && !t2.getOptions().select().equals(t1.getOptions().select())) || (t2.getOptions().source() != null && !t2.getOptions().source().equals(t1.getOptions().source())))) {
                    replaceView(r, t1, t2, true);
                    return;
                }
            } else {
                if (v1 != v2 || m1 != m2) {
                    replaceView(r, t1, t2, false);
                    return;
                }
                Diff.this.appendColumns(r, t1, Arrays.asList(t1.fields()), Arrays.asList(t2.fields()));
                Diff.this.appendPrimaryKey(r, t1, Arrays.asList(t1.getPrimaryKey()), Arrays.asList(t2.getPrimaryKey()));
                Diff.this.appendUniqueKeys(r, t1, Diff.this.removePrimary(t1.getKeys()), Diff.this.removePrimary(t2.getKeys()));
                Diff.this.appendForeignKeys(r, t1, t1.getReferences(), t2.getReferences());
                Diff.this.appendChecks(r, t1, t1.getChecks(), t2.getChecks());
                Diff.this.appendIndexes(r, t1, t1.getIndexes(), t2.getIndexes());
            }
            String c1 = StringUtils.defaultString(t1.getComment());
            String c2 = StringUtils.defaultString(t2.getComment());
            if (!c1.equals(c2)) {
                if (v2) {
                    r.queries.add(Diff.this.ctx.commentOnView(t2).is(c2));
                } else {
                    r.queries.add(Diff.this.ctx.commentOnTable(t2).is(c2));
                }
            }
        }

        private void replaceView(DiffResult r, Table<?> v1, Table<?> v2, boolean canReplace) {
            if (!canReplace || ((v2.getTableType() == TableOptions.TableType.VIEW && !Diff.this.migrateConf.createOrReplaceView()) || (v2.getTableType() == TableOptions.TableType.MATERIALIZED_VIEW && !Diff.this.migrateConf.createOrReplaceMaterializedView()))) {
                Diff.this.dropTable().drop(r, v1);
            }
            Diff.this.createTable().create(r, v2);
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Diff$Create.class */
    public interface Create<N extends Named> {
        void create(DiffResult diffResult, N n);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Diff$Drop.class */
    public interface Drop<N extends Named> {
        void drop(DiffResult diffResult, N n);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Diff$Merge.class */
    public interface Merge<N extends Named> {
        void merge(DiffResult diffResult, N n, N n2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Diff(Configuration configuration, MigrationConfiguration migrateConf, Meta meta1, Meta meta2) {
        this.migrateConf = migrateConf;
        this.exportConf = new DDLExportConfiguration().createOrReplaceView(migrateConf.createOrReplaceView()).createOrReplaceMaterializedView(migrateConf.createOrReplaceMaterializedView());
        this.ctx = configuration.dsl();
        this.meta1 = meta1;
        this.meta2 = meta2;
        this.ddl = new DDL(this.ctx, this.exportConf);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Queries queries() {
        return this.ctx.queries(appendCatalogs(new DiffResult(), this.meta1.getCatalogs(), this.meta2.getCatalogs()).queries);
    }

    private final DiffResult appendCatalogs(DiffResult result, List<Catalog> l1, List<Catalog> l2) {
        return append(result, l1, l2, null, null, null, (r, c1, c2) -> {
            appendSchemas(r, c1.getSchemas(), c2.getSchemas());
        });
    }

    private final DiffResult appendSchemas(DiffResult result, List<Schema> l1, List<Schema> l2) {
        return append(result, l1, l2, null, (r, s) -> {
            r.queries.addAll(Arrays.asList(this.ctx.ddl(s).queries()));
        }, (r2, s2) -> {
            if (s2.getTables().isEmpty() && s2.getSequences().isEmpty()) {
                if (!StringUtils.isEmpty(s2.getName())) {
                    r2.queries.add(this.ctx.dropSchema(s2));
                    return;
                }
                return;
            }
            if (this.migrateConf.dropSchemaCascade()) {
                for (Table<?> t1 : s2.getTables()) {
                    for (UniqueKey<?> uk : t1.getKeys()) {
                        r2.droppedFks.addAll(uk.getReferences());
                    }
                }
                if (!StringUtils.isEmpty(s2.getName())) {
                    r2.queries.add(this.ctx.dropSchema(s2).cascade());
                    return;
                }
                return;
            }
            for (Table<?> t2 : s2.getTables()) {
                dropTable().drop(r2, t2);
            }
            for (Sequence<?> seq : s2.getSequences()) {
                dropSequence().drop(r2, seq);
            }
            if (!StringUtils.isEmpty(s2.getName())) {
                r2.queries.add(this.ctx.dropSchema(s2));
            }
        }, (r3, s1, s22) -> {
            appendDomains(r3, s1.getDomains(), s22.getDomains());
            appendTables(r3, s1.getTables(), s22.getTables());
            appendSequences(r3, s1.getSequences(), s22.getSequences());
        });
    }

    private final Drop<Sequence<?>> dropSequence() {
        return (r, s) -> {
            r.queries.add(this.ctx.dropSequence((Sequence<?>) s));
        };
    }

    private final DiffResult appendSequences(DiffResult result, List<? extends Sequence<?>> l1, List<? extends Sequence<?>> l2) {
        return append(result, l1, l2, null, (r, s) -> {
            r.queries.add(this.ddl.createSequence(s));
        }, dropSequence(), (r2, s1, s2) -> {
            AlterSequenceFlagsStep stmt = null;
            AlterSequenceFlagsStep stmt0 = this.ctx.alterSequence(s1);
            if (s2.getStartWith() != null && !s2.getStartWith().equals(s1.getStartWith())) {
                stmt = ((AlterSequenceFlagsStep) StringUtils.defaultIfNull(null, stmt0)).startWith(s2.getStartWith());
            } else if (s2.getStartWith() == null && s1.getStartWith() != null) {
                stmt = ((AlterSequenceFlagsStep) StringUtils.defaultIfNull(null, stmt0)).startWith((AlterSequenceFlagsStep) 1);
            }
            if (s2.getIncrementBy() != null && !s2.getIncrementBy().equals(s1.getIncrementBy())) {
                stmt = ((AlterSequenceFlagsStep) StringUtils.defaultIfNull(stmt, stmt0)).incrementBy(s2.getIncrementBy());
            } else if (s2.getIncrementBy() == null && s1.getIncrementBy() != null) {
                stmt = ((AlterSequenceFlagsStep) StringUtils.defaultIfNull(stmt, stmt0)).incrementBy((AlterSequenceFlagsStep) 1);
            }
            if (s2.getMinvalue() != null && !s2.getMinvalue().equals(s1.getMinvalue())) {
                stmt = ((AlterSequenceFlagsStep) StringUtils.defaultIfNull(stmt, stmt0)).minvalue(s2.getMinvalue());
            } else if (s2.getMinvalue() == null && s1.getMinvalue() != null) {
                stmt = ((AlterSequenceFlagsStep) StringUtils.defaultIfNull(stmt, stmt0)).noMinvalue();
            }
            if (s2.getMaxvalue() != null && !s2.getMaxvalue().equals(s1.getMaxvalue())) {
                stmt = ((AlterSequenceFlagsStep) StringUtils.defaultIfNull(stmt, stmt0)).maxvalue(s2.getMaxvalue());
            } else if (s2.getMaxvalue() == null && s1.getMaxvalue() != null) {
                stmt = ((AlterSequenceFlagsStep) StringUtils.defaultIfNull(stmt, stmt0)).noMaxvalue();
            }
            if (s2.getCache() != null && !s2.getCache().equals(s1.getCache())) {
                stmt = ((AlterSequenceFlagsStep) StringUtils.defaultIfNull(stmt, stmt0)).cache(s2.getCache());
            } else if (s2.getCache() == null && s1.getCache() != null) {
                stmt = ((AlterSequenceFlagsStep) StringUtils.defaultIfNull(stmt, stmt0)).noCache();
            }
            if (s2.getCycle() && !s1.getCycle()) {
                stmt = ((AlterSequenceFlagsStep) StringUtils.defaultIfNull(stmt, stmt0)).cycle();
            } else if (!s2.getCycle() && s1.getCycle()) {
                stmt = ((AlterSequenceFlagsStep) StringUtils.defaultIfNull(stmt, stmt0)).noCycle();
            }
            if (stmt != null) {
                r2.queries.add(stmt);
            }
        });
    }

    private final DiffResult appendDomains(DiffResult result, List<? extends Domain<?>> l1, List<? extends Domain<?>> l2) {
        return append(result, l1, l2, null, (r, d) -> {
            r.queries.add(this.ddl.createDomain(d));
        }, (r2, d2) -> {
            r2.queries.add(this.ctx.dropDomain((Domain<?>) d2));
        }, (r3, d1, d22) -> {
            if (!d1.getDataType().getSQLDataType().equals(d22.getDataType().getSQLDataType())) {
                r3.queries.addAll(Arrays.asList(this.ctx.dropDomain((Domain<?>) d1), this.ddl.createDomain(d22)));
                return;
            }
            if (d1.getDataType().defaulted() && !d22.getDataType().defaulted()) {
                r3.queries.add(this.ctx.alterDomain(d1).dropDefault());
            } else if (d22.getDataType().defaulted() && !d22.getDataType().defaultValue().equals(d1.getDataType().defaultValue())) {
                r3.queries.add(this.ctx.alterDomain(d1).setDefault(d22.getDataType().defaultValue()));
            }
            appendChecks(r3, (Domain<?>) d1, d1.getChecks(), d22.getChecks());
        });
    }

    private final Create<Table<?>> createTable() {
        return (r, t) -> {
            r.queries.addAll(Arrays.asList(this.ddl.queries((Table<?>[]) new Table[]{t}).queries()));
        };
    }

    private final Drop<Table<?>> dropTable() {
        return (r, t) -> {
            DropTableFinalStep dropTable;
            for (UniqueKey<?> uk : t.getKeys()) {
                for (ForeignKey<?, ?> fk : uk.getReferences()) {
                    if (r.droppedFks.add(fk) && !this.migrateConf.dropTableCascade()) {
                        r.queries.add(this.ctx.alterTable((Table<?>) fk.getTable()).dropForeignKey(fk.constraint()));
                    }
                }
            }
            if (t.getTableType() == TableOptions.TableType.VIEW) {
                r.queries.add(this.ctx.dropView((Table<?>) t));
                return;
            }
            if (t.getTableType() == TableOptions.TableType.MATERIALIZED_VIEW) {
                r.queries.add(this.ctx.dropMaterializedView((Table<?>) t));
                return;
            }
            if (t.getTableType() == TableOptions.TableType.TEMPORARY) {
                r.queries.add(this.ctx.dropTemporaryTable((Table<?>) t));
                return;
            }
            List<Query> list = r.queries;
            if (this.migrateConf.dropTableCascade()) {
                dropTable = this.ctx.dropTable((Table<?>) t).cascade();
            } else {
                dropTable = this.ctx.dropTable((Table<?>) t);
            }
            list.add(dropTable);
        };
    }

    private final DiffResult appendTables(DiffResult result, List<? extends Table<?>> l1, List<? extends Table<?>> l2) {
        return append(result, l1, l2, null, createTable(), dropTable(), this.MERGE_TABLE);
    }

    private final List<UniqueKey<?>> removePrimary(List<? extends UniqueKey<?>> list) {
        List<UniqueKey<?>> result = new ArrayList<>();
        for (UniqueKey<?> uk : list) {
            if (!uk.isPrimary()) {
                result.add(uk);
            }
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.jooq.impl.Diff$3, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Diff$3.class */
    public static /* synthetic */ class AnonymousClass3 {
        static final /* synthetic */ int[] $SwitchMap$org$jooq$SQLDialect = new int[SQLDialect.values().length];

        static {
            try {
                $SwitchMap$org$jooq$SQLDialect[SQLDialect.MARIADB.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
        }
    }

    private final boolean isSynthetic(Field<?> f) {
        int i = AnonymousClass3.$SwitchMap$org$jooq$SQLDialect[this.ctx.family().ordinal()];
        return false;
    }

    private final boolean isSynthetic(UniqueKey<?> pk) {
        int i = AnonymousClass3.$SwitchMap$org$jooq$SQLDialect[this.ctx.family().ordinal()];
        return false;
    }

    private final DiffResult appendColumns(DiffResult result, final Table<?> t1, List<? extends Field<?>> l1, List<? extends Field<?>> l2) {
        List<Field<?>> add = new ArrayList<>();
        List<Field<?>> drop = new ArrayList<>();
        DiffResult result2 = append(result, l1, l2, null, (r, f) -> {
            if (!isSynthetic((Field<?>) f)) {
                if (this.migrateConf.alterTableAddMultiple()) {
                    add.add(f);
                } else {
                    r.queries.add(this.ctx.alterTable((Table<?>) t1).add((Field<?>) f));
                }
            }
        }, (r2, f2) -> {
            if (!isSynthetic((Field<?>) f2)) {
                if (this.migrateConf.alterTableDropMultiple()) {
                    drop.add(f2);
                } else {
                    r2.queries.add(this.ctx.alterTable((Table<?>) t1).drop((Field<?>) f2));
                }
            }
        }, new Merge<Field<?>>() { // from class: org.jooq.impl.Diff.2
            @Override // org.jooq.impl.Diff.Merge
            public void merge(DiffResult r3, Field<?> f1, Field<?> f22) {
                DataType<?> type1 = f1.getDataType();
                DataType<?> type2 = f22.getDataType();
                if (typeNameDifference(type1, type2)) {
                    r3.queries.add(Diff.this.ctx.alterTable(t1).alter(f1).set(type2.nullability(Nullability.DEFAULT)));
                }
                if (type1.nullable() && !type2.nullable()) {
                    r3.queries.add(Diff.this.ctx.alterTable(t1).alter(f1).setNotNull());
                } else if (!type1.nullable() && type2.nullable()) {
                    r3.queries.add(Diff.this.ctx.alterTable(t1).alter(f1).dropNotNull());
                }
                Field<?> d1 = type1.defaultValue();
                Field<?> d2 = type2.defaultValue();
                if (type1.defaulted() && !type2.defaulted()) {
                    r3.queries.add(Diff.this.ctx.alterTable(t1).alter(f1).dropDefault());
                } else if (type2.defaulted() && (!type1.defaulted() || !d2.equals(d1))) {
                    r3.queries.add(Diff.this.ctx.alterTable(t1).alter(f1).setDefault((Field) d2));
                }
                if ((!type1.hasLength() || !type2.hasLength() || (type1.lengthDefined() == type2.lengthDefined() && type1.length() == type2.length())) && (!type1.hasPrecision() || !type2.hasPrecision() || !precisionDifference(type1, type2))) {
                    if (!type1.hasScale() || !type2.hasScale()) {
                        return;
                    }
                    if (type1.scaleDefined() == type2.scaleDefined() && type1.scale() == type2.scale()) {
                        return;
                    }
                }
                r3.queries.add(Diff.this.ctx.alterTable(t1).alter(f1).set(type2));
            }

            private final boolean typeNameDifference(DataType<?> type1, DataType<?> type2) {
                if (type1.getTypeName().equals(type2.getTypeName())) {
                    return false;
                }
                return (type1.getType() == BigDecimal.class && type2.getType() == BigDecimal.class) ? false : true;
            }

            private final boolean precisionDifference(DataType<?> type1, DataType<?> type2) {
                boolean d1 = defaultPrecision(type1);
                boolean d2 = defaultPrecision(type2);
                return (d1 || d2) ? d1 != d2 : type1.precision() != type2.precision();
            }

            private final boolean defaultPrecision(DataType<?> type) {
                if (!type.isDateTime()) {
                    return false;
                }
                if (!type.precisionDefined() || Tools.NO_SUPPORT_TIMESTAMP_PRECISION.contains(Diff.this.ctx.dialect())) {
                    return true;
                }
                if (Boolean.FALSE.equals(Diff.this.ctx.settings().isMigrationIgnoreDefaultTimestampPrecisionDiffs())) {
                    return false;
                }
                switch (AnonymousClass3.$SwitchMap$org$jooq$SQLDialect[Diff.this.ctx.family().ordinal()]) {
                    case 1:
                        return type.precision() == 0;
                    default:
                        return type.precision() == 6;
                }
            }
        });
        if (!drop.isEmpty()) {
            result2.queries.add(0, this.ctx.alterTable(t1).drop(drop));
        }
        if (!add.isEmpty()) {
            result2.queries.add(this.ctx.alterTable(t1).add(add));
        }
        return result2;
    }

    private final DiffResult appendPrimaryKey(DiffResult result, Table<?> t1, List<? extends UniqueKey<?>> pk1, List<? extends UniqueKey<?>> pk2) {
        Create<UniqueKey<?>> create = (r, pk) -> {
            if (!isSynthetic((UniqueKey<?>) pk)) {
                r.queries.add(this.ctx.alterTable((Table<?>) t1).add(pk.constraint()));
            }
        };
        Drop<UniqueKey<?>> drop = (r2, pk3) -> {
            if (!isSynthetic((UniqueKey<?>) pk3)) {
                if (StringUtils.isEmpty(pk3.getName())) {
                    r2.queries.add(this.ctx.alterTable((Table<?>) t1).dropPrimaryKey());
                } else {
                    r2.queries.add(this.ctx.alterTable((Table<?>) t1).dropPrimaryKey(pk3.constraint()));
                }
            }
        };
        return append(result, pk1, pk2, Comparators.KEY_COMP, create, drop, keyMerge(t1, create, drop, ConstraintType.PRIMARY_KEY), true);
    }

    private final DiffResult appendUniqueKeys(DiffResult result, Table<?> t1, List<? extends UniqueKey<?>> uk1, List<? extends UniqueKey<?>> uk2) {
        Create<UniqueKey<?>> create = (r, u) -> {
            r.queries.add(this.ctx.alterTable((Table<?>) t1).add(u.constraint()));
        };
        Drop<UniqueKey<?>> drop = (r2, u2) -> {
            r2.queries.add(this.ctx.alterTable((Table<?>) t1).dropUnique(u2.constraint()));
        };
        return append(result, uk1, uk2, Comparators.KEY_COMP, create, drop, keyMerge(t1, create, drop, ConstraintType.UNIQUE), true);
    }

    private final <K extends Named> Merge<K> keyMerge(Table<?> t1, Create<K> create, Drop<K> drop, ConstraintType type) {
        return (r, k1, k2) -> {
            Name n1 = k1.getUnqualifiedName();
            Name n2 = k2.getUnqualifiedName();
            if (n1.empty() ^ n2.empty()) {
                drop.drop(r, k1);
                create.create(r, k2);
            } else if (Comparators.NAMED_COMP.compare(k1, k2) != 0) {
                if (type != ConstraintType.PRIMARY_KEY || !NO_SUPPORT_PK_NAMES.contains(this.ctx.dialect())) {
                    r.queries.add(this.ctx.alterTable((Table<?>) t1).renameConstraint(n1).to(n2));
                }
            }
        };
    }

    private final <K extends Named> Merge<K> keyMerge(Domain<?> d1, Create<K> create, Drop<K> drop) {
        return (r, k1, k2) -> {
            Name n1 = k1.getUnqualifiedName();
            Name n2 = k2.getUnqualifiedName();
            if (n1.empty() ^ n2.empty()) {
                drop.drop(r, k1);
                create.create(r, k2);
            } else if (Comparators.NAMED_COMP.compare(k1, k2) != 0) {
                r.queries.add(this.ctx.alterDomain(d1).renameConstraint(n1).to(n2));
            }
        };
    }

    private final DiffResult appendForeignKeys(DiffResult result, Table<?> t1, List<? extends ForeignKey<?, ?>> fk1, List<? extends ForeignKey<?, ?>> fk2) {
        Create<ForeignKey<?, ?>> create = (r, fk) -> {
            r.queries.add(this.ctx.alterTable((Table<?>) t1).add(fk.constraint()));
        };
        Drop<ForeignKey<?, ?>> drop = (r2, fk3) -> {
            if (r2.droppedFks.add(fk3)) {
                r2.queries.add(this.ctx.alterTable((Table<?>) t1).dropForeignKey(fk3.constraint()));
            }
        };
        return append(result, fk1, fk2, Comparators.FOREIGN_KEY_COMP, create, drop, keyMerge(t1, create, drop, ConstraintType.FOREIGN_KEY), true);
    }

    private final DiffResult appendChecks(DiffResult result, Table<?> t1, List<? extends Check<?>> c1, List<? extends Check<?>> c2) {
        Create<Check<?>> create = (r, c) -> {
            r.queries.add(this.ctx.alterTable((Table<?>) t1).add(c.constraint()));
        };
        Drop<Check<?>> drop = (r2, c3) -> {
            r2.queries.add(this.ctx.alterTable((Table<?>) t1).drop(c3.constraint()));
        };
        return append(result, c1, c2, Comparators.CHECK_COMP, create, drop, keyMerge(t1, create, drop, ConstraintType.CHECK), true);
    }

    private final DiffResult appendChecks(DiffResult result, Domain<?> d1, List<? extends Check<?>> c1, List<? extends Check<?>> c2) {
        Create<Check<?>> create = (r, c) -> {
            r.queries.add(this.ctx.alterDomain(d1).add(c.constraint()));
        };
        Drop<Check<?>> drop = (r2, c3) -> {
            r2.queries.add(this.ctx.alterDomain(d1).dropConstraint(c3.constraint()));
        };
        return append(result, c1, c2, Comparators.CHECK_COMP, create, drop, keyMerge(d1, create, drop), true);
    }

    private final DiffResult appendIndexes(DiffResult result, Table<?> t1, List<? extends Index> l1, List<? extends Index> l2) {
        Create<Index> create = (r, i) -> {
            r.queries.add((i.getUnique() ? this.ctx.createUniqueIndex(i) : this.ctx.createIndex(i)).on((Table<?>) t1, i.getFields()));
        };
        Drop<Index> drop = (r2, i2) -> {
            r2.queries.add(this.ctx.dropIndex(i2).on((Table<?>) t1));
        };
        return append(result, l1, l2, Comparators.INDEX_COMP, create, drop, (r3, ix1, ix2) -> {
            if (Comparators.INDEX_COMP.compare(ix1, ix2) != 0) {
                drop.drop(r3, ix1);
                create.create(r3, ix2);
            } else if (Comparators.NAMED_COMP.compare(ix1, ix2) != 0) {
                r3.queries.add(this.ctx.alterTable((Table<?>) t1).renameIndex(ix1).to(ix2));
            }
        }, true);
    }

    private final <N extends Named> DiffResult append(DiffResult result, List<? extends N> l1, List<? extends N> l2, Comparator<? super N> comp, Create<N> create, Drop<N> drop, Merge<N> merge) {
        return append(result, l1, l2, comp, create, drop, merge, false);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v47, types: [org.jooq.Named] */
    /* JADX WARN: Type inference failed for: r0v52, types: [org.jooq.Named] */
    private final <N extends Named> DiffResult append(DiffResult diffResult, List<? extends N> list, List<? extends N> list2, Comparator<? super N> comparator, Create<N> create, Drop<N> drop, Merge<N> merge, boolean z) {
        int compare;
        if (comparator == null) {
            comparator = Comparators.NAMED_COMP;
        }
        N n = null;
        N n2 = null;
        Iterator sorted = sorted(list, comparator);
        Iterator sorted2 = sorted(list2, comparator);
        DiffResult diffResult2 = z ? new DiffResult(new ArrayList(), diffResult.droppedFks) : diffResult;
        DiffResult diffResult3 = z ? new DiffResult(new ArrayList(), diffResult.droppedFks) : diffResult;
        DiffResult diffResult4 = z ? new DiffResult(new ArrayList(), diffResult.droppedFks) : diffResult;
        while (true) {
            if (n == null && sorted.hasNext()) {
                n = (Named) sorted.next();
            }
            if (n2 == null && sorted2.hasNext()) {
                n2 = (Named) sorted2.next();
            }
            if (n == null && n2 == null) {
                break;
            }
            if (n == null) {
                compare = 1;
            } else if (n2 == null) {
                compare = -1;
            } else {
                compare = comparator.compare((Object) n, (Object) n2);
            }
            int i = compare;
            if (i < 0) {
                if (drop != null) {
                    drop.drop(diffResult2, n);
                }
                n = null;
            } else if (i > 0) {
                if (create != null) {
                    create.create(diffResult4, n2);
                }
                n2 = null;
            } else {
                if (merge != null) {
                    merge.merge(diffResult3, n, n2);
                }
                n2 = null;
                n = null;
            }
        }
        if (z) {
            diffResult.addAll(diffResult2);
            diffResult.addAll(diffResult3);
            diffResult.addAll(diffResult4);
        }
        return diffResult;
    }

    private static final <N extends Named> Iterator<N> sorted(List<N> list, Comparator<? super N> comp) {
        List<N> result = new ArrayList<>(list);
        result.sort(comp);
        return result.iterator();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Diff$DiffResult.class */
    public static final class DiffResult extends Record {
        private final List<Query> queries;
        private final Set<ForeignKey<?, ?>> droppedFks;

        private DiffResult(List<Query> queries, Set<ForeignKey<?, ?>> droppedFks) {
            this.queries = queries;
            this.droppedFks = droppedFks;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, DiffResult.class), DiffResult.class, "queries;droppedFks", "FIELD:Lorg/jooq/impl/Diff$DiffResult;->queries:Ljava/util/List;", "FIELD:Lorg/jooq/impl/Diff$DiffResult;->droppedFks:Ljava/util/Set;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, DiffResult.class, Object.class), DiffResult.class, "queries;droppedFks", "FIELD:Lorg/jooq/impl/Diff$DiffResult;->queries:Ljava/util/List;", "FIELD:Lorg/jooq/impl/Diff$DiffResult;->droppedFks:Ljava/util/Set;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public List<Query> queries() {
            return this.queries;
        }

        public Set<ForeignKey<?, ?>> droppedFks() {
            return this.droppedFks;
        }

        DiffResult() {
            this(new ArrayList(), new HashSet());
        }

        void addAll(DiffResult other) {
            this.queries.addAll(other.queries);
            this.droppedFks.addAll(other.droppedFks);
        }

        @Override // java.lang.Record
        public String toString() {
            return this.queries.toString();
        }
    }
}
