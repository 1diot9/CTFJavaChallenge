package org.jooq.impl;

import java.util.AbstractList;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.jooq.Catalog;
import org.jooq.Check;
import org.jooq.Comment;
import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.Constraint;
import org.jooq.DataType;
import org.jooq.Delete;
import org.jooq.Domain;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Index;
import org.jooq.Insert;
import org.jooq.Merge;
import org.jooq.Meta;
import org.jooq.Name;
import org.jooq.Named;
import org.jooq.Nullability;
import org.jooq.OrderField;
import org.jooq.Query;
import org.jooq.Record;
import org.jooq.ResultQuery;
import org.jooq.SQLDialect;
import org.jooq.Schema;
import org.jooq.Select;
import org.jooq.Sequence;
import org.jooq.SortField;
import org.jooq.SortOrder;
import org.jooq.Table;
import org.jooq.TableElement;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.Update;
import org.jooq.conf.InterpreterNameLookupCaseSensitivity;
import org.jooq.conf.InterpreterSearchSchema;
import org.jooq.conf.SettingsTools;
import org.jooq.exception.DataAccessException;
import org.jooq.exception.DataDefinitionException;
import org.jooq.impl.ConstraintImpl;
import org.jooq.impl.DefaultParseContext;
import org.jooq.impl.QOM;
import org.jooq.tools.JooqLogger;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Interpreter.class */
public final class Interpreter {
    private static final JooqLogger log = JooqLogger.getLogger((Class<?>) Interpreter.class);
    private final Configuration configuration;
    private final InterpreterNameLookupCaseSensitivity caseSensitivity;
    private final Locale locale;
    private final MutableSchema defaultSchema;
    private MutableSchema currentSchema;
    private boolean delayForeignKeyDeclarations;
    private final Map<Name, MutableCatalog> catalogs = new LinkedHashMap();
    private final Map<Name, MutableCatalog.InterpretedCatalog> interpretedCatalogs = new HashMap();
    private final Map<Name, MutableSchema.InterpretedSchema> interpretedSchemas = new HashMap();
    private final Map<Name, MutableTable.InterpretedTable> interpretedTables = new HashMap();
    private final Map<Name, UniqueKeyImpl<Record>> interpretedUniqueKeys = new HashMap();
    private final Map<Name, ReferenceImpl<Record, ?>> interpretedForeignKeys = new HashMap();
    private final Map<Name, Index> interpretedIndexes = new HashMap();
    private final Map<Name, MutableDomain.InterpretedDomain> interpretedDomains = new HashMap();
    private final Map<Name, MutableSequence.InterpretedSequence> interpretedSequences = new HashMap();
    private final Deque<DelayedForeignKey> delayedForeignKeyDeclarations = new ArrayDeque();
    private final MutableCatalog defaultCatalog = new MutableCatalog(AbstractName.NO_NAME);

    /* JADX INFO: Access modifiers changed from: package-private */
    public Interpreter(Configuration configuration) {
        this.configuration = configuration;
        this.delayForeignKeyDeclarations = Boolean.TRUE.equals(configuration.settings().isInterpreterDelayForeignKeyDeclarations());
        this.caseSensitivity = caseSensitivity(configuration);
        this.locale = SettingsTools.interpreterLocale(configuration.settings());
        this.catalogs.put(this.defaultCatalog.name(), this.defaultCatalog);
        this.defaultSchema = new MutableSchema(AbstractName.NO_NAME, this.defaultCatalog);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Meta meta() {
        applyDelayedForeignKeys();
        return new AbstractMeta(this.configuration) { // from class: org.jooq.impl.Interpreter.1
            @Override // org.jooq.impl.AbstractMeta
            final List<Catalog> getCatalogs0() throws DataAccessException {
                return Tools.map(Interpreter.this.catalogs.values(), c -> {
                    return c.interpretedCatalog();
                });
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void accept(Query query) {
        invalidateCaches();
        if (log.isDebugEnabled()) {
            log.debug(query);
        }
        if (query instanceof CreateSchemaImpl) {
            CreateSchemaImpl q = (CreateSchemaImpl) query;
            accept0(q);
            return;
        }
        if (query instanceof AlterSchemaImpl) {
            AlterSchemaImpl q2 = (AlterSchemaImpl) query;
            accept0(q2);
            return;
        }
        if (query instanceof DropSchemaImpl) {
            DropSchemaImpl q3 = (DropSchemaImpl) query;
            accept0(q3);
            return;
        }
        if (query instanceof CreateTableImpl) {
            CreateTableImpl q4 = (CreateTableImpl) query;
            accept0(q4);
            return;
        }
        if (query instanceof AlterTableImpl) {
            AlterTableImpl q5 = (AlterTableImpl) query;
            accept0(q5);
            return;
        }
        if (query instanceof DropTableImpl) {
            DropTableImpl q6 = (DropTableImpl) query;
            accept0(q6);
            return;
        }
        if (query instanceof TruncateImpl) {
            TruncateImpl<?> q7 = (TruncateImpl) query;
            accept0(q7);
            return;
        }
        if (query instanceof CreateViewImpl) {
            CreateViewImpl<?> q8 = (CreateViewImpl) query;
            accept0(q8);
            return;
        }
        if (query instanceof AlterViewImpl) {
            AlterViewImpl q9 = (AlterViewImpl) query;
            accept0(q9);
            return;
        }
        if (query instanceof DropViewImpl) {
            DropViewImpl q10 = (DropViewImpl) query;
            accept0(q10);
            return;
        }
        if (query instanceof CreateSequenceImpl) {
            CreateSequenceImpl q11 = (CreateSequenceImpl) query;
            accept0(q11);
            return;
        }
        if (query instanceof AlterSequenceImpl) {
            AlterSequenceImpl<?> q12 = (AlterSequenceImpl) query;
            accept0(q12);
            return;
        }
        if (query instanceof DropSequenceImpl) {
            DropSequenceImpl q13 = (DropSequenceImpl) query;
            accept0(q13);
            return;
        }
        if (query instanceof CreateIndexImpl) {
            CreateIndexImpl q14 = (CreateIndexImpl) query;
            accept0(q14);
            return;
        }
        if (query instanceof AlterIndexImpl) {
            AlterIndexImpl q15 = (AlterIndexImpl) query;
            accept0(q15);
            return;
        }
        if (query instanceof DropIndexImpl) {
            DropIndexImpl q16 = (DropIndexImpl) query;
            accept0(q16);
            return;
        }
        if (query instanceof CreateDomainImpl) {
            CreateDomainImpl<?> q17 = (CreateDomainImpl) query;
            accept0(q17);
            return;
        }
        if (query instanceof AlterDomainImpl) {
            AlterDomainImpl<?> q18 = (AlterDomainImpl) query;
            accept0(q18);
            return;
        }
        if (query instanceof DropDomainImpl) {
            DropDomainImpl q19 = (DropDomainImpl) query;
            accept0(q19);
            return;
        }
        if (query instanceof CommentOnImpl) {
            CommentOnImpl q20 = (CommentOnImpl) query;
            accept0(q20);
            return;
        }
        if (query instanceof SetSchema) {
            SetSchema q21 = (SetSchema) query;
            accept0(q21);
            return;
        }
        if (!(query instanceof Select) && !(query instanceof Update) && !(query instanceof Insert) && !(query instanceof Delete) && !(query instanceof Merge)) {
            if (query instanceof SetCommand) {
                SetCommand q22 = (SetCommand) query;
                accept0(q22);
            } else if (!(query instanceof DefaultParseContext.IgnoreQuery)) {
                throw unsupportedQuery(query);
            }
        }
    }

    private final void invalidateCaches() {
        this.interpretedCatalogs.clear();
        this.interpretedSchemas.clear();
        this.interpretedTables.clear();
        this.interpretedUniqueKeys.clear();
        this.interpretedForeignKeys.clear();
        this.interpretedIndexes.clear();
        this.interpretedSequences.clear();
    }

    private final void accept0(CreateSchemaImpl query) {
        Schema schema = query.$schema();
        if (getSchema(schema, false) != null) {
            if (!query.$ifNotExists()) {
                throw alreadyExists(schema);
            }
        } else {
            getSchema(schema, true);
        }
    }

    private final void accept0(AlterSchemaImpl query) {
        Schema schema = query.$schema();
        MutableSchema oldSchema = getSchema(schema);
        if (oldSchema == null) {
            if (!query.$ifExists()) {
                throw notExists(schema);
            }
        } else {
            if (query.$renameTo() != null) {
                Schema renameTo = query.$renameTo();
                if (getSchema(renameTo, false) != null) {
                    throw alreadyExists(renameTo);
                }
                oldSchema.name((UnqualifiedName) renameTo.getUnqualifiedName());
                return;
            }
            throw unsupportedQuery(query);
        }
    }

    private final void accept0(DropSchemaImpl query) {
        Schema schema = query.$schema();
        MutableSchema mutableSchema = getSchema(schema);
        if (mutableSchema == null) {
            if (!query.$ifExists()) {
                throw notExists(schema);
            }
        } else {
            if (mutableSchema.isEmpty() || query.$cascade() == QOM.Cascade.CASCADE) {
                mutableSchema.catalog.schemas.remove(mutableSchema);
                return;
            }
            throw schemaNotEmpty(schema);
        }
    }

    private final void accept0(CreateTableImpl query) {
        Table<?> table = query.$table();
        MutableSchema schema = getSchema(table.getSchema(), true);
        MutableTable existing = schema.table(table);
        if (existing != null) {
            if (!query.$ifNotExists()) {
                throw alreadyExists(table, existing);
            }
            return;
        }
        MutableTable mt = newTable(table, schema, query.$columns(), query.$select(), query.$comment(), query.$temporary() ? TableOptions.temporaryTable(query.$onCommit()) : TableOptions.table());
        for (Constraint constraint : query.$constraints()) {
            addConstraint(query, (ConstraintImpl) constraint, mt);
        }
        for (Index index : query.$indexes()) {
            IndexImpl impl = (IndexImpl) index;
            mt.indexes.add(new MutableIndex((UnqualifiedName) impl.getUnqualifiedName(), mt, mt.sortFields(Arrays.asList(impl.$fields())), impl.$unique(), impl.$where()));
        }
    }

    private final void addForeignKey(MutableTable mt, ConstraintImpl impl) {
        if (this.delayForeignKeyDeclarations) {
            delayForeignKey(mt, impl);
        } else {
            addForeignKey0(mt, impl);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Interpreter$DelayedForeignKey.class */
    public static class DelayedForeignKey {
        final MutableTable table;
        final ConstraintImpl constraint;

        DelayedForeignKey(MutableTable mt, ConstraintImpl constraint) {
            this.table = mt;
            this.constraint = constraint;
        }
    }

    private final void delayForeignKey(MutableTable mt, ConstraintImpl impl) {
        this.delayedForeignKeyDeclarations.add(new DelayedForeignKey(mt, impl));
    }

    private final void applyDelayedForeignKeys() {
        Iterator<DelayedForeignKey> it = this.delayedForeignKeyDeclarations.iterator();
        while (it.hasNext()) {
            DelayedForeignKey key = it.next();
            addForeignKey0(key.table, key.constraint);
            it.remove();
        }
    }

    private final void addForeignKey0(MutableTable mt, ConstraintImpl impl) {
        MutableSchema ms = getSchema(impl.$referencesTable().getSchema());
        if (ms == null) {
            throw notExists(impl.$referencesTable().getSchema());
        }
        MutableTable mrf = ms.table(impl.$referencesTable());
        MutableUniqueKey mu = null;
        if (mrf == null) {
            throw notExists(impl.$referencesTable());
        }
        List<MutableField> mfs = mt.fields(impl.$foreignKey(), true);
        List<MutableField> mrfs = mrf.fields(impl.$references(), true);
        if (!mrfs.isEmpty()) {
            mu = mrf.uniqueKey(mrfs);
        } else if (mrf.primaryKey != null && mrf.primaryKey.fields.size() == mfs.size()) {
            MutableUniqueKey mutableUniqueKey = mrf.primaryKey;
            mu = mutableUniqueKey;
            mrfs = mutableUniqueKey.fields;
        }
        if (mu == null) {
            throw primaryKeyNotExists(impl.$referencesTable());
        }
        mt.foreignKeys.add(new MutableForeignKey((UnqualifiedName) impl.getUnqualifiedName(), mt, mfs, mu, mrfs, impl.$onDelete(), impl.$onUpdate(), impl.$enforced()));
    }

    private final void drop(List<MutableTable> tables, MutableTable table, QOM.Cascade cascade) {
        boolean[] zArr = cascade == QOM.Cascade.CASCADE ? new boolean[]{false} : new boolean[]{true, false};
        int length = zArr.length;
        for (int i = 0; i < length; i++) {
            boolean check = zArr[i];
            if (table.primaryKey != null) {
                cascade(table.primaryKey, (List<MutableField>) null, check ? QOM.Cascade.RESTRICT : QOM.Cascade.CASCADE);
            }
            cascade(table.uniqueKeys, (List<MutableField>) null, check);
        }
        Iterator<MutableTable> it = tables.iterator();
        while (it.hasNext()) {
            if (it.next().nameEquals(table.name())) {
                it.remove();
                return;
            }
        }
    }

    private final void dropColumns(MutableTable table, List<MutableField> fields, QOM.Cascade cascade) {
        Iterator<MutableIndex> it1 = table.indexes.iterator();
        boolean[] zArr = cascade == QOM.Cascade.CASCADE ? new boolean[]{false} : new boolean[]{true, false};
        int length = zArr.length;
        for (int i = 0; i < length; i++) {
            boolean check = zArr[i];
            if (table.primaryKey != null && Tools.anyMatch(table.primaryKey.fields, t1 -> {
                return fields.contains(t1);
            })) {
                cascade(table.primaryKey, fields, check ? QOM.Cascade.RESTRICT : QOM.Cascade.CASCADE);
                if (!check) {
                    table.primaryKey = null;
                }
            }
            cascade(table.uniqueKeys, fields, check);
        }
        cascade((List<? extends MutableKey>) table.foreignKeys, fields, false);
        while (it1.hasNext()) {
            Iterator<MutableSortField> it = it1.next().fields.iterator();
            while (true) {
                if (it.hasNext()) {
                    MutableSortField msf = it.next();
                    if (fields.contains(msf.field)) {
                        it1.remove();
                        break;
                    }
                }
            }
        }
        table.fields.removeAll(fields);
    }

    private final void cascade(List<? extends MutableKey> keys, List<MutableField> fields, boolean check) {
        Iterator<? extends MutableKey> it2 = keys.iterator();
        while (it2.hasNext()) {
            MutableKey key = it2.next();
            if (fields == null || Tools.anyMatch(key.fields, t1 -> {
                return fields.contains(t1);
            })) {
                if (key instanceof MutableUniqueKey) {
                    MutableUniqueKey k = (MutableUniqueKey) key;
                    cascade(k, fields, check ? QOM.Cascade.RESTRICT : QOM.Cascade.CASCADE);
                }
                if (!check) {
                    it2.remove();
                }
            }
        }
    }

    private final void cascade(MutableUniqueKey key, List<MutableField> fields, QOM.Cascade cascade) {
        for (MutableTable mt : tables()) {
            Iterator<MutableForeignKey> it = mt.foreignKeys.iterator();
            while (it.hasNext()) {
                MutableForeignKey mfk = it.next();
                if (mfk.referencedKey.equals(key)) {
                    if (cascade == QOM.Cascade.CASCADE) {
                        it.remove();
                    } else {
                        if (fields == null) {
                            throw new DataDefinitionException("Cannot drop constraint " + String.valueOf(key) + " because other objects depend on it");
                        }
                        if (fields.size() == 1) {
                            throw new DataDefinitionException("Cannot drop column " + String.valueOf(fields.get(0)) + " because other objects depend on it");
                        }
                        throw new DataDefinitionException("Cannot drop columns " + String.valueOf(fields) + " because other objects depend on them");
                    }
                }
            }
        }
    }

    private final void accept0(AlterTableImpl query) {
        Nullability nullability;
        Table<?> table = query.$table();
        MutableSchema schema = getSchema(table.getSchema());
        MutableTable existing = schema.table(table);
        if (existing == null) {
            if (!query.$ifExists()) {
                throw notExists(table);
            }
            return;
        }
        if (!existing.options.type().isTable()) {
            throw objectNotTable(table);
        }
        if (query.$add() != null) {
            for (TableElement fc : query.$add()) {
                if ((fc instanceof Field) && find(existing.fields, (Field) fc) != null) {
                    throw alreadyExists(fc);
                }
                if ((fc instanceof Constraint) && !fc.getUnqualifiedName().empty() && existing.constraint((Constraint) fc) != null) {
                    throw alreadyExists(fc);
                }
            }
            if (query.$addFirst()) {
                for (Field<?> f : assertFields(query, Tools.reverseIterable(query.$add()))) {
                    addField(existing, 0, (UnqualifiedName) f.getUnqualifiedName(), f.getDataType());
                }
                return;
            }
            if (query.$addBefore() != null) {
                int index = indexOrFail(existing.fields, query.$addBefore());
                for (Field<?> f2 : assertFields(query, Tools.reverseIterable(query.$add()))) {
                    addField(existing, index, (UnqualifiedName) f2.getUnqualifiedName(), f2.getDataType());
                }
                return;
            }
            if (query.$addAfter() != null) {
                int index2 = indexOrFail(existing.fields, query.$addAfter()) + 1;
                for (Field<?> f3 : assertFields(query, Tools.reverseIterable(query.$add()))) {
                    addField(existing, index2, (UnqualifiedName) f3.getUnqualifiedName(), f3.getDataType());
                }
                return;
            }
            for (TableElement fc2 : query.$add()) {
                if (fc2 instanceof Field) {
                    addField(existing, Integer.MAX_VALUE, (UnqualifiedName) fc2.getUnqualifiedName(), ((Field) fc2).getDataType());
                } else if (fc2 instanceof ConstraintImpl) {
                    ConstraintImpl c = (ConstraintImpl) fc2;
                    addConstraint(query, c, existing);
                } else {
                    throw unsupportedQuery(query);
                }
            }
            return;
        }
        if (query.$addColumn() != null) {
            if (find(existing.fields, query.$addColumn()) != null) {
                if (!query.$ifNotExistsColumn()) {
                    throw alreadyExists(query.$addColumn());
                }
                return;
            }
            UnqualifiedName name = (UnqualifiedName) query.$addColumn().getUnqualifiedName();
            DataType<?> dataType = query.$addColumnType();
            if (query.$addFirst()) {
                addField(existing, 0, name, dataType);
                return;
            }
            if (query.$addBefore() != null) {
                addField(existing, indexOrFail(existing.fields, query.$addBefore()), name, dataType);
                return;
            } else if (query.$addAfter() != null) {
                addField(existing, indexOrFail(existing.fields, query.$addAfter()) + 1, name, dataType);
                return;
            } else {
                addField(existing, Integer.MAX_VALUE, name, dataType);
                return;
            }
        }
        if (query.$addConstraint() != null) {
            addConstraint(query, (ConstraintImpl) query.$addConstraint(), existing);
            return;
        }
        if (query.$alterColumn() != null) {
            MutableField existingField = (MutableField) find(existing.fields, query.$alterColumn());
            if (existingField == null) {
                if (!query.$ifExistsColumn()) {
                    throw notExists(query.$alterColumn());
                }
                return;
            }
            if (query.$alterColumnNullability() != null) {
                existingField.type = existingField.type.nullability(query.$alterColumnNullability());
                return;
            }
            if (query.$alterColumnType() != null) {
                DataType<?> $alterColumnType = query.$alterColumnType();
                if (query.$alterColumnType().nullability() == Nullability.DEFAULT) {
                    nullability = existingField.type.nullability();
                } else {
                    nullability = query.$alterColumnType().nullability();
                }
                existingField.type = $alterColumnType.nullability(nullability);
                return;
            }
            if (query.$alterColumnDefault() != null) {
                existingField.type = existingField.type.default_(query.$alterColumnDefault());
                return;
            } else {
                if (query.$alterColumnDropDefault()) {
                    existingField.type = existingField.type.default_((Field<?>) null);
                    return;
                }
                throw unsupportedQuery(query);
            }
        }
        if (query.$renameTo() != null && checkNotExists(schema, query.$renameTo())) {
            existing.name((UnqualifiedName) query.$renameTo().getUnqualifiedName());
            return;
        }
        if (query.$renameColumn() != null) {
            MutableField mf = (MutableField) find(existing.fields, query.$renameColumn());
            if (mf == null) {
                throw notExists(query.$renameColumn());
            }
            if (find(existing.fields, query.$renameColumnTo()) != null) {
                throw alreadyExists(query.$renameColumnTo());
            }
            mf.name((UnqualifiedName) query.$renameColumnTo().getUnqualifiedName());
            return;
        }
        if (query.$renameConstraint() != null) {
            MutableConstraint mc = existing.constraint(query.$renameConstraint(), true);
            if (existing.constraint(query.$renameConstraintTo()) != null) {
                throw alreadyExists(query.$renameConstraintTo());
            }
            mc.name((UnqualifiedName) query.$renameConstraintTo().getUnqualifiedName());
            return;
        }
        if (query.$alterConstraint() != null) {
            existing.constraint(query.$alterConstraint(), true).enforced = query.$alterConstraintEnforced();
            return;
        }
        if (query.$dropColumns() != null) {
            List<MutableField> fields = existing.fields((Field[]) query.$dropColumns().toArray(Tools.EMPTY_FIELD), false);
            if (fields.size() < query.$dropColumns().size() && !query.$ifExistsColumn()) {
                existing.fields((Field[]) query.$dropColumns().toArray(Tools.EMPTY_FIELD), true);
            }
            dropColumns(existing, fields, query.$dropCascade());
            return;
        }
        if (query.$dropConstraint() != null) {
            ConstraintImpl impl = (ConstraintImpl) query.$dropConstraint();
            if (impl.getUnqualifiedName().empty()) {
                if (impl.$foreignKey() != null) {
                    throw new DataDefinitionException("Cannot drop unnamed foreign key");
                }
                if (impl.$check() != null) {
                    throw new DataDefinitionException("Cannot drop unnamed check constraint");
                }
                if (impl.$unique() != null) {
                    Iterator<MutableUniqueKey> uks = existing.uniqueKeys.iterator();
                    while (uks.hasNext()) {
                        MutableUniqueKey key = uks.next();
                        if (key.fieldsEquals(impl.$unique())) {
                            cascade(key, (List<MutableField>) null, query.$dropCascade());
                            uks.remove();
                            return;
                        }
                    }
                }
            } else {
                Iterator<MutableForeignKey> fks = existing.foreignKeys.iterator();
                while (fks.hasNext()) {
                    if (fks.next().nameEquals((UnqualifiedName) impl.getUnqualifiedName())) {
                        fks.remove();
                        return;
                    }
                }
                if (query.$dropConstraintType() != ConstraintType.FOREIGN_KEY) {
                    Iterator<MutableUniqueKey> uks2 = existing.uniqueKeys.iterator();
                    while (uks2.hasNext()) {
                        MutableUniqueKey key2 = uks2.next();
                        if (key2.nameEquals((UnqualifiedName) impl.getUnqualifiedName())) {
                            cascade(key2, (List<MutableField>) null, query.$dropCascade());
                            uks2.remove();
                            return;
                        }
                    }
                    Iterator<MutableCheck> chks = existing.checks.iterator();
                    while (chks.hasNext()) {
                        MutableCheck check = chks.next();
                        if (check.nameEquals((UnqualifiedName) impl.getUnqualifiedName())) {
                            chks.remove();
                            return;
                        }
                    }
                    if (existing.primaryKey != null && existing.primaryKey.nameEquals((UnqualifiedName) impl.getUnqualifiedName())) {
                        cascade(existing.primaryKey, (List<MutableField>) null, query.$dropCascade());
                        existing.primaryKey = null;
                        return;
                    }
                }
            }
            Iterator<DelayedForeignKey> it = this.delayedForeignKeyDeclarations.iterator();
            while (it.hasNext()) {
                DelayedForeignKey key3 = it.next();
                if (existing.equals(key3.table) && key3.constraint.getUnqualifiedName().equals(impl.getUnqualifiedName())) {
                    it.remove();
                    return;
                }
            }
            if (!query.$ifExistsConstraint()) {
                throw notExists(query.$dropConstraint());
            }
            return;
        }
        if (query.$dropConstraintType() == ConstraintType.PRIMARY_KEY) {
            if (existing.primaryKey != null) {
                existing.primaryKey = null;
                return;
            }
            throw primaryKeyNotExists(table);
        }
        throw unsupportedQuery(query);
    }

    private final Iterable<Field<?>> assertFields(Query query, Iterable<TableElement> fields) {
        return () -> {
            return new Iterator<Field<?>>() { // from class: org.jooq.impl.Interpreter.2
                final Iterator<TableElement> it;

                {
                    this.it = fields.iterator();
                }

                @Override // java.util.Iterator
                public boolean hasNext() {
                    return this.it.hasNext();
                }

                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.util.Iterator
                public Field<?> next() {
                    TableElement next = this.it.next();
                    if (next instanceof Field) {
                        Field<?> f = (Field) next;
                        return f;
                    }
                    throw Interpreter.unsupportedQuery(query);
                }

                @Override // java.util.Iterator
                public void remove() {
                    this.it.remove();
                }
            };
        };
    }

    private final void addField(MutableTable existing, int index, UnqualifiedName name, DataType<?> dataType) {
        MutableField field = new MutableField(name, existing, dataType);
        for (MutableField mf : existing.fields) {
            if (mf.nameEquals(field.name())) {
                throw columnAlreadyExists(field.qualifiedName());
            }
            if (mf.type.identity() && dataType.identity()) {
                throw new DataDefinitionException("Table can only have one identity: " + String.valueOf(mf.qualifiedName()));
            }
        }
        if (index == Integer.MAX_VALUE) {
            existing.fields.add(field);
        } else {
            existing.fields.add(index, field);
        }
    }

    private final void addConstraint(Query query, ConstraintImpl impl, MutableTable existing) {
        if (!impl.getUnqualifiedName().empty() && existing.constraint(impl) != null) {
            throw alreadyExists(impl);
        }
        if (impl.$primaryKey() != null) {
            if (existing.primaryKey != null) {
                throw alreadyExists(impl);
            }
            existing.primaryKey = new MutableUniqueKey((UnqualifiedName) impl.getUnqualifiedName(), existing, existing.fields(impl.$primaryKey(), true), impl.$enforced());
        } else if (impl.$unique() != null) {
            existing.uniqueKeys.add(new MutableUniqueKey((UnqualifiedName) impl.getUnqualifiedName(), existing, existing.fields(impl.$unique(), true), impl.$enforced()));
        } else if (impl.$foreignKey() != null) {
            addForeignKey(existing, impl);
        } else {
            if (impl.$check() != null) {
                existing.checks.add(new MutableCheck((UnqualifiedName) impl.getUnqualifiedName(), existing, impl.$check(), impl.$enforced()));
                return;
            }
            throw unsupportedQuery(query);
        }
    }

    private final void accept0(DropTableImpl query) {
        Table<?> table = query.$table();
        MutableSchema schema = getSchema(table.getSchema());
        MutableTable existing = schema.table(table);
        if (existing == null) {
            if (!query.$ifExists()) {
                throw notExists(table);
            }
        } else {
            if (!existing.options.type().isTable()) {
                throw objectNotTable(table);
            }
            if (query.$temporary() && existing.options.type() != TableOptions.TableType.TEMPORARY) {
                throw objectNotTemporaryTable(table);
            }
            drop(schema.tables, existing, query.$cascade());
        }
    }

    private final void accept0(TruncateImpl<?> query) {
        for (Table<?> table : query.$table()) {
            MutableSchema schema = getSchema(table.getSchema());
            MutableTable existing = schema.table(table);
            if (existing == null) {
                throw notExists(table);
            }
            if (!existing.options.type().isTable()) {
                throw objectNotTable(table);
            }
            if (query.$cascade() != QOM.Cascade.CASCADE && existing.hasReferencingKeys()) {
                throw new DataDefinitionException("Cannot truncate table referenced by other tables. Use CASCADE: " + String.valueOf(table));
            }
        }
    }

    private final void accept0(CreateViewImpl<?> query) {
        Table<?> table = query.$view();
        MutableSchema schema = getSchema(table.getSchema(), true);
        MutableTable existing = schema.table(table);
        if (existing != null) {
            if (existing.options.type() != TableOptions.TableType.VIEW && !query.$materialized()) {
                throw objectNotView(table);
            }
            if (existing.options.type() != TableOptions.TableType.MATERIALIZED_VIEW && query.$materialized()) {
                throw objectNotMaterializedView(table);
            }
            if (query.$orReplace()) {
                drop(schema.tables, existing, QOM.Cascade.RESTRICT);
            } else {
                if (!query.$ifNotExists()) {
                    throw viewAlreadyExists(table);
                }
                return;
            }
        }
        ResultQuery<? extends Object> $query = query.$query();
        if ($query instanceof Select) {
            Select<?> s = (Select) $query;
            newTable(table, schema, query.$fields(), s, null, query.$materialized() ? TableOptions.materializedView(s) : TableOptions.view(s));
        } else {
            newTable(table, schema, query.$fields(), null, null, query.$materialized() ? TableOptions.view() : TableOptions.materializedView());
        }
    }

    private final void accept0(AlterViewImpl query) {
        Table<?> table = query.$view();
        MutableSchema schema = getSchema(table.getSchema());
        MutableTable existing = schema.table(table);
        if (existing == null) {
            if (!query.$ifExists()) {
                if (query.$materialized()) {
                    throw materializedViewNotExists(table);
                }
                throw viewNotExists(table);
            }
            return;
        }
        if (existing.options.type() != TableOptions.TableType.VIEW && !query.$materialized()) {
            throw objectNotView(table);
        }
        if (existing.options.type() != TableOptions.TableType.MATERIALIZED_VIEW && query.$materialized()) {
            throw objectNotMaterializedView(table);
        }
        if (query.$renameTo() != null && checkNotExists(schema, query.$renameTo())) {
            existing.name((UnqualifiedName) query.$renameTo().getUnqualifiedName());
        } else {
            if (query.$as() != null) {
                initTable(existing, query.$fields(), query.$as(), TableOptions.view(query.$as()));
                return;
            }
            throw unsupportedQuery(query);
        }
    }

    private final void accept0(DropViewImpl query) {
        Table<?> table = query.$view();
        MutableSchema schema = getSchema(table.getSchema());
        MutableTable existing = schema.table(table);
        if (existing == null) {
            if (!query.$ifExists()) {
                if (query.$materialized()) {
                    throw materializedViewNotExists(table);
                }
                throw viewNotExists(table);
            }
            return;
        }
        if (existing.options.type() != TableOptions.TableType.VIEW && !query.$materialized()) {
            throw objectNotView(table);
        }
        if (existing.options.type() != TableOptions.TableType.MATERIALIZED_VIEW && query.$materialized()) {
            throw objectNotMaterializedView(table);
        }
        drop(schema.tables, existing, QOM.Cascade.RESTRICT);
    }

    private final void accept0(CreateSequenceImpl query) {
        Sequence<?> sequence = query.$sequence();
        MutableSchema schema = getSchema(sequence.getSchema(), true);
        MutableSequence existing = schema.sequence(sequence);
        if (existing != null) {
            if (!query.$ifNotExists()) {
                throw alreadyExists(sequence);
            }
            return;
        }
        MutableSequence ms = new MutableSequence((UnqualifiedName) sequence.getUnqualifiedName(), schema);
        ms.startWith = query.$startWith();
        ms.incrementBy = query.$incrementBy();
        ms.minvalue = query.$noMinvalue() ? null : query.$minvalue();
        ms.maxvalue = query.$noMaxvalue() ? null : query.$maxvalue();
        ms.cycle = query.$cycle() == QOM.CycleOption.CYCLE;
        ms.cache = query.$noCache() ? null : query.$cache();
    }

    /* JADX WARN: Removed duplicated region for block: B:34:0x00c9  */
    /* JADX WARN: Removed duplicated region for block: B:39:0x0101  */
    /* JADX WARN: Removed duplicated region for block: B:48:0x0124  */
    /* JADX WARN: Removed duplicated region for block: B:53:0x0158  */
    /* JADX WARN: Removed duplicated region for block: B:57:0x016e  */
    /* JADX WARN: Removed duplicated region for block: B:59:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:64:0x0141  */
    /* JADX WARN: Removed duplicated region for block: B:69:0x00e6  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private final void accept0(org.jooq.impl.AlterSequenceImpl<?> r5) {
        /*
            Method dump skipped, instructions count: 372
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jooq.impl.Interpreter.accept0(org.jooq.impl.AlterSequenceImpl):void");
    }

    private final void accept0(DropSequenceImpl query) {
        Sequence<?> sequence = query.$sequence();
        MutableSchema schema = getSchema(sequence.getSchema());
        MutableSequence existing = schema.sequence(sequence);
        if (existing == null) {
            if (!query.$ifExists()) {
                throw notExists(sequence);
            }
        } else {
            schema.sequences.remove(existing);
        }
    }

    private final void accept0(CreateIndexImpl query) {
        Index index = query.$index();
        Table<?> table = query.$table();
        MutableSchema schema = getSchema(table.getSchema());
        MutableTable mt = schema.table(table);
        if (mt == null) {
            throw notExists(table);
        }
        MutableIndex existing = (MutableIndex) find(mt.indexes, index);
        if (existing != null) {
            if (!query.$ifNotExists()) {
                throw alreadyExists(index);
            }
        } else {
            List<MutableSortField> mtf = mt.sortFields(query.$on());
            mt.indexes.add(new MutableIndex((UnqualifiedName) index.getUnqualifiedName(), mt, mtf, query.$unique(), query.$where()));
        }
    }

    private final void accept0(AlterIndexImpl query) {
        Index index = query.$index();
        Table<?> table = query.$on() != null ? query.$on() : index.getTable();
        MutableIndex existing = index(table, index, query.$ifExists(), true);
        if (existing != null) {
            if (query.$renameTo() != null) {
                if (index(table, query.$renameTo(), false, false) == null) {
                    existing.name((UnqualifiedName) query.$renameTo().getUnqualifiedName());
                    return;
                }
                throw alreadyExists(query.$renameTo());
            }
            throw unsupportedQuery(query);
        }
    }

    private final void accept0(DropIndexImpl query) {
        Index index = query.$index();
        Table<?> table = query.$on() != null ? query.$on() : index.getTable();
        MutableIndex existing = index(table, index, query.$ifExists(), true);
        if (existing != null) {
            existing.table.indexes.remove(existing);
        }
    }

    private final void accept0(CreateDomainImpl<?> query) {
        Domain<?> domain = query.$domain();
        MutableSchema schema = getSchema(domain.getSchema(), true);
        MutableDomain existing = schema.domain(domain);
        if (existing != null) {
            if (!query.$ifNotExists()) {
                throw alreadyExists(domain);
            }
            return;
        }
        MutableDomain md = new MutableDomain((UnqualifiedName) domain.getUnqualifiedName(), schema, query.$dataType());
        if (query.$default_() != null) {
            md.dataType = md.dataType.default_(query.$default_());
        }
        if (query.$constraints() != null) {
            for (Constraint constraint : query.$constraints()) {
                if (((ConstraintImpl) constraint).$check() != null) {
                    md.checks.add(new MutableCheck(this, constraint));
                }
            }
        }
    }

    private final void accept0(AlterDomainImpl<?> query) {
        Domain<?> domain = query.$domain();
        MutableSchema schema = getSchema(domain.getSchema());
        MutableDomain existing = schema.domain(domain);
        if (existing == null) {
            if (!query.$ifExists()) {
                throw notExists(domain);
            }
            return;
        }
        if (query.$addConstraint() != null) {
            Constraint addConstraint = query.$addConstraint();
            if (find(existing.checks, addConstraint) != null) {
                throw alreadyExists(addConstraint);
            }
            existing.checks.add(new MutableCheck(this, addConstraint));
            return;
        }
        if (query.$dropConstraint() != null) {
            Constraint dropConstraint = query.$dropConstraint();
            MutableCheck mc = (MutableCheck) find(existing.checks, dropConstraint);
            if (mc == null) {
                if (!query.$dropConstraintIfExists()) {
                    throw notExists(dropConstraint);
                }
                return;
            } else {
                existing.checks.remove(mc);
                return;
            }
        }
        if (query.$renameTo() != null) {
            Domain<?> renameTo = query.$renameTo();
            if (schema.domain(renameTo) != null) {
                throw alreadyExists(renameTo);
            }
            existing.name((UnqualifiedName) renameTo.getUnqualifiedName());
            return;
        }
        if (query.$renameConstraint() != null) {
            Constraint renameConstraint = query.$renameConstraint();
            Constraint renameConstraintTo = query.$renameConstraintTo();
            MutableCheck mc2 = (MutableCheck) find(existing.checks, renameConstraint);
            if (mc2 == null) {
                if (!query.$renameConstraintIfExists()) {
                    throw notExists(renameConstraint);
                }
                return;
            } else {
                if (find(existing.checks, renameConstraintTo) != null) {
                    throw alreadyExists(renameConstraintTo);
                }
                mc2.name((UnqualifiedName) renameConstraintTo.getUnqualifiedName());
                return;
            }
        }
        if (query.$setDefault() != null) {
            existing.dataType = existing.dataType.defaultValue(query.$setDefault());
        } else {
            if (query.$dropDefault()) {
                existing.dataType = existing.dataType.defaultValue((Field<?>) null);
                return;
            }
            throw unsupportedQuery(query);
        }
    }

    private final void accept0(DropDomainImpl query) {
        Domain<?> domain = query.$domain();
        MutableSchema schema = getSchema(domain.getSchema());
        MutableDomain existing = schema.domain(domain);
        if (existing == null) {
            if (!query.$ifExists()) {
                throw notExists(domain);
            }
        } else {
            if (query.$cascade() != QOM.Cascade.CASCADE && !existing.fields.isEmpty()) {
                throw new DataDefinitionException("Domain " + String.valueOf(domain.getQualifiedName()) + " is still being referenced by fields.");
            }
            List<MutableField> field = new ArrayList<>(existing.fields);
            for (MutableField mf : field) {
                dropColumns(mf.table, existing.fields, QOM.Cascade.CASCADE);
            }
            schema.domains.remove(existing);
        }
    }

    private final void accept0(CommentOnImpl query) {
        if (query.$table() != null) {
            MutableTable existing = table(query.$table());
            if (query.$isView() && existing.options.type() != TableOptions.TableType.VIEW) {
                throw objectNotView(query.$table());
            }
            if (query.$isMaterializedView() && existing.options.type() != TableOptions.TableType.MATERIALIZED_VIEW) {
                throw objectNotMaterializedView(query.$table());
            }
            table(query.$table()).comment(query.$comment());
            return;
        }
        if (query.$field() != null) {
            field(query.$field()).comment(query.$comment());
            return;
        }
        throw unsupportedQuery(query);
    }

    private final void accept0(SetSchema query) {
        MutableSchema schema = getSchema(query.$schema());
        if (schema == null) {
            throw notExists(query.$schema());
        }
        this.currentSchema = schema;
    }

    private final void accept0(SetCommand query) {
        if ("foreign_key_checks".equals(query.$name().last().toLowerCase(this.locale))) {
            this.delayForeignKeyDeclarations = !((Boolean) Convert.convert(query.$value().getValue(), Boolean.TYPE)).booleanValue();
            if (!this.delayForeignKeyDeclarations) {
                applyDelayedForeignKeys();
                return;
            }
            return;
        }
        throw unsupportedQuery(query);
    }

    private static final DataDefinitionException unsupportedQuery(Query query) {
        return new DataDefinitionException("Unsupported query: " + query.getSQL());
    }

    private static final DataDefinitionException schemaNotEmpty(Schema schema) {
        return new DataDefinitionException("Schema is not empty: " + String.valueOf(schema.getQualifiedName()));
    }

    private static final DataDefinitionException objectNotTable(Table<?> table) {
        return new DataDefinitionException("Object is not a table: " + String.valueOf(table.getQualifiedName()));
    }

    private static final DataDefinitionException objectNotTemporaryTable(Table<?> table) {
        return new DataDefinitionException("Object is not a temporary table: " + String.valueOf(table.getQualifiedName()));
    }

    private static final DataDefinitionException objectNotView(Table<?> table) {
        return new DataDefinitionException("Object is not a view: " + String.valueOf(table.getQualifiedName()));
    }

    private static final DataDefinitionException objectNotMaterializedView(Table<?> table) {
        return new DataDefinitionException("Object is not a materialized view: " + String.valueOf(table.getQualifiedName()));
    }

    private static final DataDefinitionException viewNotExists(Table<?> view) {
        return new DataDefinitionException("View does not exist: " + String.valueOf(view.getQualifiedName()));
    }

    private static final DataDefinitionException materializedViewNotExists(Table<?> view) {
        return new DataDefinitionException("Materialized view does not exist: " + String.valueOf(view.getQualifiedName()));
    }

    private static final DataDefinitionException viewAlreadyExists(Table<?> view) {
        return new DataDefinitionException("View already exists: " + String.valueOf(view.getQualifiedName()));
    }

    private static final DataDefinitionException materializedViewAlreadyExists(Table<?> view) {
        return new DataDefinitionException("Materialized view already exists: " + String.valueOf(view.getQualifiedName()));
    }

    private static final DataDefinitionException columnAlreadyExists(Name name) {
        return new DataDefinitionException("Column already exists: " + String.valueOf(name));
    }

    private static final DataDefinitionException notExists(Named named) {
        return new DataDefinitionException(named.getClass().getSimpleName() + " does not exist: " + String.valueOf(named.getQualifiedName()));
    }

    private static final DataDefinitionException alreadyExists(Named named) {
        return new DataDefinitionException(named.getClass().getSimpleName() + " already exists: " + String.valueOf(named.getQualifiedName()));
    }

    private static final DataDefinitionException primaryKeyNotExists(Named named) {
        return new DataDefinitionException("Primary key does not exist on table: " + String.valueOf(named));
    }

    private final Iterable<MutableTable> tables() {
        List<MutableTable> result = new ArrayList<>();
        for (MutableCatalog catalog : this.catalogs.values()) {
            for (MutableSchema schema : catalog.schemas) {
                result.addAll(schema.tables);
            }
        }
        return result;
    }

    private final MutableSchema getSchema(Schema input) {
        return getSchema(input, false);
    }

    private final MutableSchema getSchema(Schema input, boolean create) {
        if (input == null) {
            return currentSchema(create);
        }
        MutableCatalog catalog = this.defaultCatalog;
        if (input.getCatalog() != null) {
            Name catalogName = input.getCatalog().getUnqualifiedName();
            MutableCatalog mutableCatalog = this.catalogs.get(catalogName);
            catalog = mutableCatalog;
            if (mutableCatalog == null && create) {
                Map<Name, MutableCatalog> map = this.catalogs;
                MutableCatalog mutableCatalog2 = new MutableCatalog((UnqualifiedName) catalogName);
                catalog = mutableCatalog2;
                map.put(catalogName, mutableCatalog2);
            }
        }
        if (catalog == null) {
            return null;
        }
        MutableSchema mutableSchema = this.defaultSchema;
        MutableSchema mutableSchema2 = (MutableSchema) find(catalog.schemas, input);
        MutableSchema schema = mutableSchema2;
        if (mutableSchema2 == null && create) {
            schema = new MutableSchema((UnqualifiedName) input.getUnqualifiedName(), catalog);
        }
        return schema;
    }

    private final MutableSchema currentSchema(boolean create) {
        if (this.currentSchema == null) {
            this.currentSchema = getInterpreterSearchPathSchema(create);
        }
        return this.currentSchema;
    }

    private final MutableSchema getInterpreterSearchPathSchema(boolean create) {
        List<InterpreterSearchSchema> searchPath = this.configuration.settings().getInterpreterSearchPath();
        if (searchPath.isEmpty()) {
            return this.defaultSchema;
        }
        InterpreterSearchSchema schema = searchPath.get(0);
        return getSchema(DSL.schema(DSL.name(schema.getCatalog(), schema.getSchema())), create);
    }

    private final MutableTable newTable(Table<?> table, MutableSchema schema, List<? extends Field<?>> columns, Select<?> select, Comment comment, TableOptions options) {
        return initTable(new MutableTable((UnqualifiedName) table.getUnqualifiedName(), schema, comment, options), columns, select, options);
    }

    private final MutableTable initTable(MutableTable t, List<? extends Field<?>> columns, Select<?> select, TableOptions options) {
        t.fields.clear();
        t.options = options;
        if (!columns.isEmpty()) {
            for (int i = 0; i < columns.size(); i++) {
                addField(t, Integer.MAX_VALUE, (UnqualifiedName) columns.get(i).getUnqualifiedName(), columns.get(i).getDataType());
            }
        } else if (select != null) {
            for (Field<?> column : FieldsImpl.fieldsRow0((FieldsTrait) select).fields()) {
                addField(t, Integer.MAX_VALUE, (UnqualifiedName) column.getUnqualifiedName(), column.getDataType());
            }
        }
        return t;
    }

    private final MutableTable table(Table<?> table) {
        return table(table, true);
    }

    private final MutableTable table(Table<?> table, boolean throwIfNotExists) {
        MutableTable result = getSchema(table.getSchema()).table(table);
        if (result == null && throwIfNotExists) {
            throw notExists(table);
        }
        return result;
    }

    private final MutableIndex index(Table<?> table, Index index, boolean ifExists, boolean throwIfNotExists) {
        return (MutableIndex) tableElement(table, index, mt -> {
            return mt.indexes;
        }, ifExists, throwIfNotExists);
    }

    private final <N extends MutableNamed> N tableElement(Table<?> table, Named named, java.util.function.Function<? super MutableTable, ? extends List<N>> function, boolean z, boolean z2) {
        MutableTable mutableTable = null;
        MutableNamed mutableNamed = null;
        if (table != null) {
            mutableTable = getSchema(table.getSchema()).table(table);
        } else {
            Iterator<MutableTable> it = tables().iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                MutableTable next = it.next();
                MutableNamed find = find((List<? extends MutableNamed>) function.apply(next), named);
                mutableNamed = find;
                if (find != null) {
                    mutableTable = next;
                    MutableSchema mutableSchema = next.schema;
                    break;
                }
            }
        }
        if (mutableTable != null) {
            mutableNamed = find((List<? extends MutableNamed>) function.apply(mutableTable), named);
        } else if (table != null && z2) {
            throw notExists(table);
        }
        if (mutableNamed == null && !z && z2) {
            throw notExists(named);
        }
        return (N) mutableNamed;
    }

    private static final boolean checkNotExists(MutableSchema schema, Table<?> table) {
        MutableTable mt = schema.table(table);
        if (mt != null) {
            throw alreadyExists(table, mt);
        }
        return true;
    }

    private static final DataDefinitionException alreadyExists(Table<?> t, MutableTable mt) {
        if (mt.options.type() == TableOptions.TableType.VIEW) {
            return viewAlreadyExists(t);
        }
        if (mt.options.type() == TableOptions.TableType.MATERIALIZED_VIEW) {
            return materializedViewAlreadyExists(t);
        }
        return alreadyExists(t);
    }

    private final MutableField field(Field<?> field) {
        return field(field, true);
    }

    private final MutableField field(Field<?> field, boolean throwIfNotExists) {
        MutableTable table = table(DSL.table(field.getQualifiedName().qualifier()), throwIfNotExists);
        if (table == null) {
            return null;
        }
        MutableField result = (MutableField) find(table.fields, field);
        if (result == null && throwIfNotExists) {
            throw notExists(field);
        }
        return result;
    }

    private static final <M extends MutableNamed> M find(M m, UnqualifiedName name) {
        if (m != null && m.nameEquals(name)) {
            return m;
        }
        return null;
    }

    private static final <M extends MutableNamed> M find(M m, Named named) {
        return (M) find(m, (UnqualifiedName) named.getUnqualifiedName());
    }

    private static final <M extends MutableNamed> M find(List<? extends M> list, Named named) {
        UnqualifiedName unqualifiedName = (UnqualifiedName) named.getUnqualifiedName();
        Iterator<? extends M> it = list.iterator();
        while (it.hasNext()) {
            M m = (M) find(it.next(), unqualifiedName);
            if (m != null) {
                return m;
            }
        }
        return null;
    }

    private static final int indexOrFail(List<? extends MutableNamed> list, Named named) {
        int result = -1;
        int i = 0;
        while (true) {
            if (i >= list.size()) {
                break;
            }
            if (!list.get(i).nameEquals((UnqualifiedName) named.getUnqualifiedName())) {
                i++;
            } else {
                result = i;
                break;
            }
        }
        if (result == -1) {
            throw notExists(named);
        }
        return result;
    }

    private static final InterpreterNameLookupCaseSensitivity caseSensitivity(Configuration configuration) {
        InterpreterNameLookupCaseSensitivity result = (InterpreterNameLookupCaseSensitivity) StringUtils.defaultIfNull(configuration.settings().getInterpreterNameLookupCaseSensitivity(), InterpreterNameLookupCaseSensitivity.DEFAULT);
        if (result == InterpreterNameLookupCaseSensitivity.DEFAULT) {
            switch (((SQLDialect) StringUtils.defaultIfNull(configuration.settings().getInterpreterDialect(), configuration.family())).family()) {
                case MARIADB:
                case MYSQL:
                case SQLITE:
                case TRINO:
                    return InterpreterNameLookupCaseSensitivity.NEVER;
                case DEFAULT:
                default:
                    return InterpreterNameLookupCaseSensitivity.WHEN_QUOTED;
            }
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Interpreter$MutableNamed.class */
    public abstract class MutableNamed {
        private UnqualifiedName name;
        private String upper;
        private Comment comment;

        abstract MutableNamed parent();

        abstract void onDrop();

        MutableNamed(Interpreter interpreter, UnqualifiedName name) {
            this(name, null);
        }

        MutableNamed(UnqualifiedName name, Comment comment) {
            this.comment = comment;
            name(name);
        }

        Name qualifiedName() {
            MutableNamed parent = parent();
            if (parent == null) {
                return this.name;
            }
            return parent.qualifiedName().append(this.name);
        }

        UnqualifiedName name() {
            return this.name;
        }

        void name(UnqualifiedName n) {
            this.name = n;
            this.upper = this.name.last().toUpperCase(Interpreter.this.locale);
        }

        Comment comment() {
            return this.comment;
        }

        void comment(Comment c) {
            this.comment = c;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public boolean nameEquals(UnqualifiedName other) {
            switch (Interpreter.this.caseSensitivity) {
                case ALWAYS:
                    return this.name.last().equals(other.last());
                case WHEN_QUOTED:
                    return Tools.normaliseNameCase(Interpreter.this.configuration, this.name.last(), this.name.quoted() == Name.Quoted.QUOTED, Interpreter.this.locale).equals(Tools.normaliseNameCase(Interpreter.this.configuration, other.last(), other.quoted() == Name.Quoted.QUOTED, Interpreter.this.locale));
                case NEVER:
                    return this.upper.equalsIgnoreCase(other.last().toUpperCase(Interpreter.this.locale));
                case DEFAULT:
                default:
                    throw new IllegalStateException();
            }
        }

        public String toString() {
            return qualifiedName().toString();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Interpreter$MutableCatalog.class */
    public final class MutableCatalog extends MutableNamed {
        List<MutableSchema> schemas;

        MutableCatalog(UnqualifiedName name) {
            super(name, null);
            this.schemas = new MutableNamedList();
        }

        @Override // org.jooq.impl.Interpreter.MutableNamed
        final void onDrop() {
            this.schemas.clear();
        }

        @Override // org.jooq.impl.Interpreter.MutableNamed
        final MutableNamed parent() {
            return null;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public final InterpretedCatalog interpretedCatalog() {
            return Interpreter.this.interpretedCatalogs.computeIfAbsent(qualifiedName(), n -> {
                return new InterpretedCatalog();
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Interpreter$MutableCatalog$InterpretedCatalog.class */
        public final class InterpretedCatalog extends CatalogImpl {
            InterpretedCatalog() {
                super(MutableCatalog.this.name(), MutableCatalog.this.comment());
            }

            @Override // org.jooq.impl.CatalogImpl, org.jooq.Catalog
            public final List<Schema> getSchemas() {
                return Tools.map(MutableCatalog.this.schemas, s -> {
                    return s.interpretedSchema();
                });
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Interpreter$MutableSchema.class */
    public final class MutableSchema extends MutableNamed {
        MutableCatalog catalog;
        List<MutableTable> tables;
        List<MutableDomain> domains;
        List<MutableSequence> sequences;

        MutableSchema(UnqualifiedName name, MutableCatalog catalog) {
            super(Interpreter.this, name);
            this.tables = new MutableNamedList();
            this.domains = new MutableNamedList();
            this.sequences = new MutableNamedList();
            this.catalog = catalog;
            this.catalog.schemas.add(this);
        }

        @Override // org.jooq.impl.Interpreter.MutableNamed
        final void onDrop() {
            for (MutableTable table : this.tables) {
                for (MutableForeignKey referencingKey : table.referencingKeys()) {
                    referencingKey.table.foreignKeys.remove(referencingKey);
                }
            }
            this.tables.clear();
            this.domains.clear();
            this.sequences.clear();
        }

        @Override // org.jooq.impl.Interpreter.MutableNamed
        final MutableNamed parent() {
            return this.catalog;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public final InterpretedSchema interpretedSchema() {
            return Interpreter.this.interpretedSchemas.computeIfAbsent(qualifiedName(), n -> {
                return new InterpretedSchema(this.catalog.interpretedCatalog());
            });
        }

        final boolean isEmpty() {
            return this.tables.isEmpty();
        }

        final MutableTable table(Named t) {
            return (MutableTable) Interpreter.find(this.tables, t);
        }

        final MutableDomain domain(Named d) {
            return (MutableDomain) Interpreter.find(this.domains, d);
        }

        final MutableSequence sequence(Named s) {
            return (MutableSequence) Interpreter.find(this.sequences, s);
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Interpreter$MutableSchema$InterpretedSchema.class */
        public final class InterpretedSchema extends SchemaImpl {
            InterpretedSchema(MutableCatalog.InterpretedCatalog catalog) {
                super(MutableSchema.this.name(), catalog, MutableSchema.this.comment());
            }

            @Override // org.jooq.impl.SchemaImpl, org.jooq.Schema
            public final List<Table<?>> getTables() {
                return Tools.map(MutableSchema.this.tables, t -> {
                    return t.interpretedTable();
                });
            }

            @Override // org.jooq.impl.SchemaImpl, org.jooq.Schema
            public final List<Domain<?>> getDomains() {
                return Tools.map(MutableSchema.this.domains, d -> {
                    return d.interpretedDomain();
                });
            }

            @Override // org.jooq.impl.SchemaImpl, org.jooq.Schema
            public final List<Sequence<?>> getSequences() {
                return Tools.map(MutableSchema.this.sequences, s -> {
                    return s.interpretedSequence();
                });
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Interpreter$MutableTable.class */
    public final class MutableTable extends MutableNamed {
        MutableSchema schema;
        TableOptions options;
        List<MutableField> fields;
        MutableUniqueKey primaryKey;
        List<MutableUniqueKey> uniqueKeys;
        List<MutableForeignKey> foreignKeys;
        List<MutableCheck> checks;
        List<MutableIndex> indexes;

        MutableTable(UnqualifiedName name, MutableSchema schema, Comment comment, TableOptions options) {
            super(name, comment);
            this.fields = new MutableNamedList();
            this.uniqueKeys = new MutableNamedList();
            this.foreignKeys = new MutableNamedList();
            this.checks = new MutableNamedList();
            this.indexes = new MutableNamedList();
            this.schema = schema;
            this.options = options;
            schema.tables.add(this);
        }

        @Override // org.jooq.impl.Interpreter.MutableNamed
        final void onDrop() {
            if (this.primaryKey != null) {
                this.primaryKey.onDrop();
            }
            this.uniqueKeys.clear();
            this.foreignKeys.clear();
            this.checks.clear();
            this.indexes.clear();
            this.fields.clear();
        }

        @Override // org.jooq.impl.Interpreter.MutableNamed
        final MutableNamed parent() {
            return this.schema;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public final InterpretedTable interpretedTable() {
            return Interpreter.this.interpretedTables.computeIfAbsent(qualifiedName(), n -> {
                return new InterpretedTable(this.schema.interpretedSchema());
            });
        }

        boolean hasReferencingKeys() {
            if (this.primaryKey != null && !this.primaryKey.referencingKeys.isEmpty()) {
                return true;
            }
            return Tools.anyMatch(this.uniqueKeys, uk -> {
                return !uk.referencingKeys.isEmpty();
            });
        }

        List<MutableForeignKey> referencingKeys() {
            List<MutableForeignKey> result = new ArrayList<>();
            if (this.primaryKey != null) {
                result.addAll(this.primaryKey.referencingKeys);
            }
            for (MutableUniqueKey uk : this.uniqueKeys) {
                result.addAll(uk.referencingKeys);
            }
            return result;
        }

        final MutableConstraint constraint(Constraint constraint, boolean failIfNotFound) {
            MutableConstraint result = (MutableConstraint) Interpreter.find(this.foreignKeys, constraint);
            if (result != null) {
                return result;
            }
            MutableConstraint result2 = (MutableConstraint) Interpreter.find(this.uniqueKeys, constraint);
            if (result2 != null) {
                return result2;
            }
            MutableConstraint result3 = (MutableConstraint) Interpreter.find(this.checks, constraint);
            if (result3 != null) {
                return result3;
            }
            MutableConstraint result4 = (MutableConstraint) Interpreter.find(this.primaryKey, constraint);
            if (result4 != null) {
                return result4;
            }
            if (failIfNotFound) {
                throw Interpreter.notExists(constraint);
            }
            return null;
        }

        final MutableNamed constraint(Constraint constraint) {
            return constraint(constraint, false);
        }

        final List<MutableField> fields(Field<?>[] fs, boolean failIfNotFound) {
            List<MutableField> result = new ArrayList<>();
            for (Field<?> f : fs) {
                MutableField mf = (MutableField) Interpreter.find(this.fields, f);
                if (mf != null) {
                    result.add(mf);
                } else if (failIfNotFound) {
                    throw new DataDefinitionException("Field does not exist in table: " + String.valueOf(f.getQualifiedName()));
                }
            }
            return result;
        }

        final List<MutableSortField> sortFields(Collection<? extends OrderField<?>> ofs) {
            return Tools.map(ofs, of -> {
                SortField<?> sf = Tools.sortField(of);
                MutableField mf = (MutableField) Interpreter.find(this.fields, sf.$field());
                if (mf == null) {
                    throw new DataDefinitionException("Field does not exist in table: " + String.valueOf(sf.$field().getQualifiedName()));
                }
                return new MutableSortField(mf, sf.$sortOrder());
            });
        }

        final MutableUniqueKey uniqueKey(List<MutableField> mrfs) {
            Set<MutableField> set = new HashSet<>(mrfs);
            if (this.primaryKey != null && set.equals(new HashSet(this.primaryKey.fields))) {
                return this.primaryKey;
            }
            return (MutableUniqueKey) Tools.findAny(this.uniqueKeys, mu -> {
                return set.equals(new HashSet(mu.fields));
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Interpreter$MutableTable$InterpretedTable.class */
        public final class InterpretedTable extends TableImpl<Record> {
            InterpretedTable(MutableSchema.InterpretedSchema schema) {
                super(MutableTable.this.name(), schema, (Table<?>) null, (ForeignKey) null, (Table) null, (Field<?>[]) null, MutableTable.this.comment(), MutableTable.this.options);
                for (MutableField field : MutableTable.this.fields) {
                    createField(field.name(), field.type, field.comment() != null ? field.comment().getComment() : null);
                }
            }

            @Override // org.jooq.impl.TableImpl, org.jooq.impl.AbstractTable, org.jooq.Table
            public final UniqueKey<Record> getPrimaryKey() {
                if (MutableTable.this.primaryKey != null) {
                    return MutableTable.this.primaryKey.interpretedKey();
                }
                return null;
            }

            @Override // org.jooq.impl.TableImpl, org.jooq.impl.AbstractTable, org.jooq.Table
            public final List<UniqueKey<Record>> getUniqueKeys() {
                return Tools.map(MutableTable.this.uniqueKeys, uk -> {
                    return uk.interpretedKey();
                });
            }

            @Override // org.jooq.impl.TableImpl, org.jooq.impl.AbstractTable, org.jooq.Table
            public List<ForeignKey<Record, ?>> getReferences() {
                return Tools.map(MutableTable.this.foreignKeys, fk -> {
                    return fk.interpretedKey();
                });
            }

            @Override // org.jooq.impl.TableImpl, org.jooq.impl.AbstractTable, org.jooq.Table
            public List<Check<Record>> getChecks() {
                return Tools.map(MutableTable.this.checks, c -> {
                    return new CheckImpl(this, c.name(), c.condition, c.enforced);
                });
            }

            @Override // org.jooq.impl.TableImpl, org.jooq.impl.AbstractTable, org.jooq.Table
            public final List<Index> getIndexes() {
                return Tools.map(MutableTable.this.indexes, i -> {
                    return i.interpretedIndex();
                });
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Interpreter$MutableDomain.class */
    public final class MutableDomain extends MutableNamed {
        MutableSchema schema;
        DataType<?> dataType;
        List<MutableCheck> checks;
        List<MutableField> fields;

        MutableDomain(UnqualifiedName name, MutableSchema schema, DataType<?> dataType) {
            super(Interpreter.this, name);
            this.checks = new MutableNamedList();
            this.fields = new MutableNamedList();
            this.schema = schema;
            this.dataType = dataType;
            schema.domains.add(this);
        }

        @Override // org.jooq.impl.Interpreter.MutableNamed
        final void onDrop() {
            this.schema.domains.remove(this);
        }

        @Override // org.jooq.impl.Interpreter.MutableNamed
        final MutableNamed parent() {
            return this.schema;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public final InterpretedDomain interpretedDomain() {
            return Interpreter.this.interpretedDomains.computeIfAbsent(qualifiedName(), n -> {
                return new InterpretedDomain(this.schema.interpretedSchema());
            });
        }

        final Check<?>[] interpretedChecks() {
            return (Check[]) Tools.map(this.checks, c -> {
                return new CheckImpl(null, c.name(), c.condition, c.enforced);
            }, x$0 -> {
                return new Check[x$0];
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Interpreter$MutableDomain$InterpretedDomain.class */
        public final class InterpretedDomain extends DomainImpl {
            InterpretedDomain(Schema schema) {
                super(schema, MutableDomain.this.name(), MutableDomain.this.dataType, MutableDomain.this.interpretedChecks());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Interpreter$MutableSequence.class */
    public final class MutableSequence extends MutableNamed {
        MutableSchema schema;
        Field<? extends Number> startWith;
        Field<? extends Number> incrementBy;
        Field<? extends Number> minvalue;
        Field<? extends Number> maxvalue;
        boolean cycle;
        Field<? extends Number> cache;

        MutableSequence(UnqualifiedName name, MutableSchema schema) {
            super(Interpreter.this, name);
            this.schema = schema;
            schema.sequences.add(this);
        }

        @Override // org.jooq.impl.Interpreter.MutableNamed
        final void onDrop() {
        }

        @Override // org.jooq.impl.Interpreter.MutableNamed
        final MutableNamed parent() {
            return this.schema;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public final InterpretedSequence interpretedSequence() {
            return Interpreter.this.interpretedSequences.computeIfAbsent(qualifiedName(), n -> {
                return new InterpretedSequence(this.schema.interpretedSchema());
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Interpreter$MutableSequence$InterpretedSequence.class */
        public final class InterpretedSequence extends SequenceImpl<Long> {
            InterpretedSequence(Schema schema) {
                super(MutableSequence.this.name(), schema, SQLDataType.BIGINT, false, MutableSequence.this.startWith, MutableSequence.this.incrementBy, MutableSequence.this.minvalue, MutableSequence.this.maxvalue, MutableSequence.this.cycle, MutableSequence.this.cache);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Interpreter$MutableConstraint.class */
    public abstract class MutableConstraint extends MutableNamed {
        MutableTable table;
        boolean enforced;

        MutableConstraint(UnqualifiedName name, MutableTable table, boolean enforced) {
            super(Interpreter.this, name);
            this.table = table;
            this.enforced = enforced;
        }

        @Override // org.jooq.impl.Interpreter.MutableNamed
        final MutableNamed parent() {
            return this.table;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Interpreter$MutableKey.class */
    public abstract class MutableKey extends MutableConstraint {
        List<MutableField> fields;

        MutableKey(UnqualifiedName name, MutableTable table, List<MutableField> fields, boolean enforced) {
            super(name, table, enforced);
            this.fields = fields;
        }

        final boolean fieldsEquals(Field<?>[] f) {
            if (this.fields.size() != f.length) {
                return false;
            }
            return Tools.allMatch(this.fields, (x, i) -> {
                return x.nameEquals((UnqualifiedName) f[i].getUnqualifiedName());
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Interpreter$MutableCheck.class */
    public final class MutableCheck extends MutableConstraint {
        Condition condition;

        MutableCheck(Interpreter interpreter, Constraint constraint) {
            this((UnqualifiedName) constraint.getUnqualifiedName(), null, ((ConstraintImpl) constraint).$check(), ((ConstraintImpl) constraint).$enforced());
        }

        MutableCheck(UnqualifiedName name, MutableTable table, Condition condition, boolean enforced) {
            super(name, table, enforced);
            this.condition = condition;
        }

        @Override // org.jooq.impl.Interpreter.MutableNamed
        final void onDrop() {
        }

        @Override // org.jooq.impl.Interpreter.MutableNamed
        final Name qualifiedName() {
            if (name().empty()) {
                return super.qualifiedName().append(this.condition.toString());
            }
            return super.qualifiedName();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Interpreter$MutableUniqueKey.class */
    public final class MutableUniqueKey extends MutableKey {
        List<MutableForeignKey> referencingKeys;

        MutableUniqueKey(UnqualifiedName name, MutableTable table, List<MutableField> fields, boolean enforced) {
            super(name, table, fields, enforced);
            this.referencingKeys = new MutableNamedList();
        }

        @Override // org.jooq.impl.Interpreter.MutableNamed
        final void onDrop() {
            this.referencingKeys.clear();
        }

        @Override // org.jooq.impl.Interpreter.MutableNamed
        final Name qualifiedName() {
            if (name().empty()) {
                return super.qualifiedName().append(this.fields.toString());
            }
            return super.qualifiedName();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public final UniqueKeyImpl<Record> interpretedKey() {
            Name qualifiedName = qualifiedName();
            UniqueKeyImpl<Record> result = Interpreter.this.interpretedUniqueKeys.get(qualifiedName);
            if (result == null) {
                MutableTable.InterpretedTable t = this.table.interpretedTable();
                Map<Name, UniqueKeyImpl<Record>> map = Interpreter.this.interpretedUniqueKeys;
                UniqueKeyImpl<Record> uniqueKeyImpl = new UniqueKeyImpl<>(t, name(), (TableField[]) Tools.map(this.fields, f -> {
                    return (TableField) t.field(f.name());
                }, x$0 -> {
                    return new TableField[x$0];
                }), this.enforced);
                result = uniqueKeyImpl;
                map.put(qualifiedName, uniqueKeyImpl);
                for (MutableForeignKey referencingKey : this.referencingKeys) {
                    result.references.add(referencingKey.interpretedKey());
                }
            }
            return result;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Interpreter$MutableForeignKey.class */
    public final class MutableForeignKey extends MutableKey {
        MutableUniqueKey referencedKey;
        List<MutableField> referencedFields;
        ConstraintImpl.Action onDelete;
        ConstraintImpl.Action onUpdate;

        MutableForeignKey(UnqualifiedName name, MutableTable table, List<MutableField> fields, MutableUniqueKey referencedKey, List<MutableField> referencedFields, ConstraintImpl.Action onDelete, ConstraintImpl.Action onUpdate, boolean enforced) {
            super(name, table, fields, enforced);
            this.referencedKey = referencedKey;
            this.referencedKey.referencingKeys.add(this);
            this.referencedFields = referencedFields;
            this.onDelete = onDelete;
            this.onUpdate = onUpdate;
        }

        @Override // org.jooq.impl.Interpreter.MutableNamed
        final void onDrop() {
            this.referencedKey.referencingKeys.remove(this);
        }

        @Override // org.jooq.impl.Interpreter.MutableNamed
        final Name qualifiedName() {
            if (name().empty()) {
                return super.qualifiedName().append(this.referencedKey.qualifiedName());
            }
            return super.qualifiedName();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public final ForeignKey<Record, ?> interpretedKey() {
            Name qualifiedName = qualifiedName();
            ReferenceImpl<Record, ?> result = Interpreter.this.interpretedForeignKeys.get(qualifiedName);
            if (result == null) {
                MutableTable.InterpretedTable t = this.table.interpretedTable();
                UniqueKeyImpl<Record> uk = this.referencedKey.interpretedKey();
                Map<Name, ReferenceImpl<Record, ?>> map = Interpreter.this.interpretedForeignKeys;
                ReferenceImpl<Record, ?> referenceImpl = new ReferenceImpl<>(t, name(), (TableField[]) Tools.map(this.fields, f -> {
                    return (TableField) t.field(f.name());
                }, x$0 -> {
                    return new TableField[x$0];
                }), uk, (TableField[]) Tools.map(this.referencedFields, f2 -> {
                    return (TableField) uk.getTable().field(f2.name());
                }, x$02 -> {
                    return new TableField[x$02];
                }), this.enforced);
                result = referenceImpl;
                map.put(qualifiedName, referenceImpl);
            }
            return result;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Interpreter$MutableIndex.class */
    public final class MutableIndex extends MutableNamed {
        MutableTable table;
        List<MutableSortField> fields;
        boolean unique;
        Condition where;

        MutableIndex(UnqualifiedName name, MutableTable table, List<MutableSortField> fields, boolean unique, Condition where) {
            super(Interpreter.this, name);
            this.table = table;
            this.fields = fields;
            this.unique = unique;
            this.where = where;
        }

        @Override // org.jooq.impl.Interpreter.MutableNamed
        final void onDrop() {
        }

        @Override // org.jooq.impl.Interpreter.MutableNamed
        final MutableNamed parent() {
            return this.table;
        }

        @Override // org.jooq.impl.Interpreter.MutableNamed
        final Name qualifiedName() {
            return super.qualifiedName();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public final Index interpretedIndex() {
            Name qualifiedName = qualifiedName();
            Index result = Interpreter.this.interpretedIndexes.get(qualifiedName);
            if (result == null) {
                Table<?> t = this.table.interpretedTable();
                Map<Name, Index> map = Interpreter.this.interpretedIndexes;
                IndexImpl indexImpl = new IndexImpl(name(), t, (OrderField[]) Tools.map(this.fields, msf -> {
                    return t.field(msf.name()).sort(msf.sort);
                }, x$0 -> {
                    return new SortField[x$0];
                }), this.where, this.unique);
                result = indexImpl;
                map.put(qualifiedName, indexImpl);
            }
            return result;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Interpreter$MutableField.class */
    public final class MutableField extends MutableNamed {
        MutableTable table;
        DataType<?> type;
        MutableDomain domain;

        MutableField(UnqualifiedName name, MutableTable table, DataType<?> type) {
            super(Interpreter.this, name);
            this.table = table;
            this.type = type;
            this.domain = table.schema.domain(type);
            if (this.domain != null) {
                this.domain.fields.add(this);
            }
        }

        @Override // org.jooq.impl.Interpreter.MutableNamed
        final void onDrop() {
            if (this.domain != null) {
                this.domain.fields.remove(this);
            }
        }

        @Override // org.jooq.impl.Interpreter.MutableNamed
        final MutableNamed parent() {
            return this.table;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Interpreter$MutableSortField.class */
    public final class MutableSortField extends MutableNamed {
        MutableField field;
        SortOrder sort;

        MutableSortField(MutableField field, SortOrder sort) {
            super(Interpreter.this, field.name());
            this.field = field;
            this.sort = sort;
        }

        @Override // org.jooq.impl.Interpreter.MutableNamed
        final void onDrop() {
        }

        @Override // org.jooq.impl.Interpreter.MutableNamed
        final MutableNamed parent() {
            return this.field.parent();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Interpreter$MutableNamedList.class */
    private final class MutableNamedList<N extends MutableNamed> extends AbstractList<N> {
        private final List<N> delegate = new ArrayList();

        private MutableNamedList() {
        }

        @Override // java.util.AbstractList, java.util.List
        public N get(int index) {
            return this.delegate.get(index);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            return this.delegate.size();
        }

        @Override // java.util.AbstractList, java.util.List
        public N set(int index, N element) {
            return this.delegate.set(index, element);
        }

        @Override // java.util.AbstractList, java.util.List
        public void add(int index, N element) {
            this.delegate.add(index, element);
        }

        @Override // java.util.AbstractList, java.util.List
        public N remove(int index) {
            N removed = this.delegate.remove(index);
            removed.onDrop();
            return removed;
        }
    }

    public String toString() {
        return meta().toString();
    }
}
