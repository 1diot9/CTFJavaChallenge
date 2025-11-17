package org.jooq.impl;

import java.util.Collection;
import java.util.Set;
import org.jooq.Clause;
import org.jooq.Condition;
import org.jooq.ConstraintEnforcementStep;
import org.jooq.ConstraintForeignKeyOnStep;
import org.jooq.ConstraintForeignKeyReferencesStep1;
import org.jooq.ConstraintForeignKeyReferencesStep10;
import org.jooq.ConstraintForeignKeyReferencesStep11;
import org.jooq.ConstraintForeignKeyReferencesStep12;
import org.jooq.ConstraintForeignKeyReferencesStep13;
import org.jooq.ConstraintForeignKeyReferencesStep14;
import org.jooq.ConstraintForeignKeyReferencesStep15;
import org.jooq.ConstraintForeignKeyReferencesStep16;
import org.jooq.ConstraintForeignKeyReferencesStep17;
import org.jooq.ConstraintForeignKeyReferencesStep18;
import org.jooq.ConstraintForeignKeyReferencesStep19;
import org.jooq.ConstraintForeignKeyReferencesStep2;
import org.jooq.ConstraintForeignKeyReferencesStep20;
import org.jooq.ConstraintForeignKeyReferencesStep21;
import org.jooq.ConstraintForeignKeyReferencesStep22;
import org.jooq.ConstraintForeignKeyReferencesStep3;
import org.jooq.ConstraintForeignKeyReferencesStep4;
import org.jooq.ConstraintForeignKeyReferencesStep5;
import org.jooq.ConstraintForeignKeyReferencesStep6;
import org.jooq.ConstraintForeignKeyReferencesStep7;
import org.jooq.ConstraintForeignKeyReferencesStep8;
import org.jooq.ConstraintForeignKeyReferencesStep9;
import org.jooq.ConstraintForeignKeyReferencesStepN;
import org.jooq.ConstraintTypeStep;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Keyword;
import org.jooq.Name;
import org.jooq.SQLDialect;
import org.jooq.Table;
import org.jooq.impl.QOM;
import org.jooq.impl.Tools;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ConstraintImpl.class */
public final class ConstraintImpl extends AbstractNamed implements QOM.UNotYetImplemented, ConstraintTypeStep, ConstraintForeignKeyOnStep, ConstraintForeignKeyReferencesStepN, ConstraintForeignKeyReferencesStep1, ConstraintForeignKeyReferencesStep2, ConstraintForeignKeyReferencesStep3, ConstraintForeignKeyReferencesStep4, ConstraintForeignKeyReferencesStep5, ConstraintForeignKeyReferencesStep6, ConstraintForeignKeyReferencesStep7, ConstraintForeignKeyReferencesStep8, ConstraintForeignKeyReferencesStep9, ConstraintForeignKeyReferencesStep10, ConstraintForeignKeyReferencesStep11, ConstraintForeignKeyReferencesStep12, ConstraintForeignKeyReferencesStep13, ConstraintForeignKeyReferencesStep14, ConstraintForeignKeyReferencesStep15, ConstraintForeignKeyReferencesStep16, ConstraintForeignKeyReferencesStep17, ConstraintForeignKeyReferencesStep18, ConstraintForeignKeyReferencesStep19, ConstraintForeignKeyReferencesStep20, ConstraintForeignKeyReferencesStep21, ConstraintForeignKeyReferencesStep22 {
    private static final Clause[] CLAUSES = {Clause.CONSTRAINT};
    private static final Set<SQLDialect> NO_SUPPORT_PK = SQLDialect.supportedBy(SQLDialect.TRINO);
    private static final Set<SQLDialect> NO_SUPPORT_UK = SQLDialect.supportedBy(SQLDialect.IGNITE, SQLDialect.TRINO);
    private static final Set<SQLDialect> NO_SUPPORT_FK = SQLDialect.supportedBy(SQLDialect.IGNITE, SQLDialect.TRINO);
    private static final Set<SQLDialect> NO_SUPPORT_CHECK = SQLDialect.supportedBy(SQLDialect.IGNITE, SQLDialect.TRINO);
    private Field<?>[] unique;
    private Field<?>[] primaryKey;
    private Field<?>[] foreignKey;
    private Table<?> referencesTable;
    private Field<?>[] references;
    private Action onDelete;
    private Action onUpdate;
    private Condition check;
    private boolean enforced;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.jooq.impl.ConstraintImpl$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ConstraintImpl$1.class */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jooq$SQLDialect = new int[SQLDialect.values().length];
    }

    @Override // org.jooq.ConstraintTypeStep
    public /* bridge */ /* synthetic */ ConstraintEnforcementStep unique(Collection collection) {
        return unique((Collection<? extends Field<?>>) collection);
    }

    @Override // org.jooq.ConstraintTypeStep
    public /* bridge */ /* synthetic */ ConstraintEnforcementStep unique(Field[] fieldArr) {
        return unique((Field<?>[]) fieldArr);
    }

    @Override // org.jooq.ConstraintTypeStep
    public /* bridge */ /* synthetic */ ConstraintForeignKeyReferencesStepN foreignKey(Collection collection) {
        return foreignKey((Collection<? extends Field<?>>) collection);
    }

    @Override // org.jooq.ConstraintTypeStep
    public /* bridge */ /* synthetic */ ConstraintForeignKeyReferencesStepN foreignKey(Field[] fieldArr) {
        return foreignKey((Field<?>[]) fieldArr);
    }

    @Override // org.jooq.ConstraintTypeStep
    public /* bridge */ /* synthetic */ ConstraintEnforcementStep primaryKey(Collection collection) {
        return primaryKey((Collection<? extends Field<?>>) collection);
    }

    @Override // org.jooq.ConstraintTypeStep
    public /* bridge */ /* synthetic */ ConstraintEnforcementStep primaryKey(Field[] fieldArr) {
        return primaryKey((Field<?>[]) fieldArr);
    }

    @Override // org.jooq.ConstraintForeignKeyReferencesStepN
    public /* bridge */ /* synthetic */ ConstraintForeignKeyOnStep references(Table table, Collection collection) {
        return references((Table<?>) table, (Collection<? extends Field<?>>) collection);
    }

    @Override // org.jooq.ConstraintForeignKeyReferencesStepN
    public /* bridge */ /* synthetic */ ConstraintForeignKeyOnStep references(Table table, Field[] fieldArr) {
        return references((Table<?>) table, (Field<?>[]) fieldArr);
    }

    @Override // org.jooq.ConstraintForeignKeyReferencesStepN
    public /* bridge */ /* synthetic */ ConstraintForeignKeyOnStep references(Name name, Collection collection) {
        return references(name, (Collection<? extends Name>) collection);
    }

    @Override // org.jooq.ConstraintForeignKeyReferencesStepN
    public /* bridge */ /* synthetic */ ConstraintForeignKeyOnStep references(String str, Collection collection) {
        return references(str, (Collection<? extends String>) collection);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConstraintImpl() {
        this(null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConstraintImpl(Name name) {
        super(name, null);
        this.enforced = true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Field<?>[] $unique() {
        return this.unique;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Field<?>[] $primaryKey() {
        return this.primaryKey;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Field<?>[] $foreignKey() {
        return this.foreignKey;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Table<?> $referencesTable() {
        return this.referencesTable;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Field<?>[] $references() {
        return this.references;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Action $onDelete() {
        return this.onDelete;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Action $onUpdate() {
        return this.onUpdate;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Condition $check() {
        return this.check;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean $enforced() {
        return this.enforced;
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }

    /* JADX WARN: Type inference failed for: r0v16, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v21, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v36, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v41, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v46, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v52, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v59, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v64, types: [org.jooq.Context] */
    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        boolean named = !getQualifiedName().equals(AbstractName.NO_NAME);
        if (named && Boolean.TRUE.equals(ctx.data(Tools.BooleanDataKey.DATA_CONSTRAINT_REFERENCE))) {
            ctx.visit(getQualifiedName());
            return;
        }
        if (named) {
            ctx.visit(Keywords.K_CONSTRAINT).sql(' ').visit(getUnqualifiedName()).sql(' ');
        }
        if (this.unique != null) {
            ctx.visit(Keywords.K_UNIQUE).sql(" (").visit(QueryPartListView.wrap(this.unique).qualify(false)).sql(')');
        } else if (this.primaryKey != null) {
            ctx.visit(Keywords.K_PRIMARY_KEY);
            ctx.sql(" (").visit(QueryPartListView.wrap(this.primaryKey).qualify(false)).sql(')');
        } else if (this.foreignKey != null) {
            ctx.visit(Keywords.K_FOREIGN_KEY).sql(" (").visit(QueryPartListView.wrap(this.foreignKey).qualify(false)).sql(") ").visit(Keywords.K_REFERENCES).sql(' ').visit(this.referencesTable);
            if (this.references.length > 0) {
                ctx.sql(" (").visit(QueryPartListView.wrap(this.references).qualify(false)).sql(')');
            }
            if (this.onDelete != null) {
                ctx.sql(' ').visit(Keywords.K_ON_DELETE).sql(' ').visit(this.onDelete.keyword);
            }
            if (this.onUpdate != null) {
                ctx.sql(' ').visit(Keywords.K_ON_UPDATE).sql(' ').visit(this.onUpdate.keyword);
            }
        } else if (this.check != null) {
            ctx.visit(Keywords.K_CHECK).sql(" (").qualify(false, c -> {
                c.visit(this.check);
            }).sql(')');
        }
        if (!this.enforced) {
            acceptEnforced(ctx, this.enforced);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Type inference failed for: r0v4, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v9, types: [org.jooq.Context] */
    public static void acceptEnforced(Context<?> ctx, boolean enforced) {
        switch (AnonymousClass1.$SwitchMap$org$jooq$SQLDialect[ctx.family().ordinal()]) {
            default:
                if (enforced) {
                    ctx.sql(' ').visit(Keywords.K_ENFORCED);
                    return;
                } else {
                    ctx.sql(' ').visit(Keywords.K_NOT).sql(' ').visit(Keywords.K_ENFORCED);
                    return;
                }
        }
    }

    @Override // org.jooq.ConstraintTypeStep
    public final ConstraintImpl unique(String... fields) {
        return unique((Collection<? extends Field<?>>) Tools.fieldsByName(fields));
    }

    @Override // org.jooq.ConstraintTypeStep
    public final ConstraintImpl unique(Name... fields) {
        return unique((Collection<? extends Field<?>>) Tools.fieldsByName(fields));
    }

    @Override // org.jooq.ConstraintTypeStep
    public final ConstraintImpl unique(Field<?>... fields) {
        this.unique = fields;
        return this;
    }

    @Override // org.jooq.ConstraintTypeStep
    public final ConstraintImpl unique(Collection<? extends Field<?>> fields) {
        return unique((Field<?>[]) fields.toArray(Tools.EMPTY_FIELD));
    }

    @Override // org.jooq.ConstraintTypeStep
    public final ConstraintImpl check(Condition condition) {
        this.check = condition;
        return this;
    }

    @Override // org.jooq.ConstraintTypeStep
    public final ConstraintImpl primaryKey(String... fields) {
        return primaryKey((Collection<? extends Field<?>>) Tools.fieldsByName(fields));
    }

    @Override // org.jooq.ConstraintTypeStep
    public final ConstraintImpl primaryKey(Name... fields) {
        return primaryKey((Collection<? extends Field<?>>) Tools.fieldsByName(fields));
    }

    @Override // org.jooq.ConstraintTypeStep
    public final ConstraintImpl primaryKey(Field<?>... fields) {
        this.primaryKey = fields;
        return this;
    }

    @Override // org.jooq.ConstraintTypeStep
    public final ConstraintImpl primaryKey(Collection<? extends Field<?>> fields) {
        return primaryKey((Field<?>[]) fields.toArray(Tools.EMPTY_FIELD));
    }

    @Override // org.jooq.ConstraintTypeStep
    public final ConstraintImpl foreignKey(String... fields) {
        return foreignKey((Collection<? extends Field<?>>) Tools.fieldsByName(fields));
    }

    @Override // org.jooq.ConstraintTypeStep
    public final ConstraintImpl foreignKey(Name... fields) {
        return foreignKey((Collection<? extends Field<?>>) Tools.fieldsByName(fields));
    }

    @Override // org.jooq.ConstraintTypeStep
    public final ConstraintImpl foreignKey(Field<?>... fields) {
        this.foreignKey = fields;
        return this;
    }

    @Override // org.jooq.ConstraintTypeStep
    public final ConstraintImpl foreignKey(Collection<? extends Field<?>> fields) {
        return foreignKey((Field<?>[]) fields.toArray(Tools.EMPTY_FIELD));
    }

    @Override // org.jooq.ConstraintForeignKeyReferencesStepN, org.jooq.ConstraintForeignKeyReferencesStep1, org.jooq.ConstraintForeignKeyReferencesStep2, org.jooq.ConstraintForeignKeyReferencesStep3, org.jooq.ConstraintForeignKeyReferencesStep4, org.jooq.ConstraintForeignKeyReferencesStep5, org.jooq.ConstraintForeignKeyReferencesStep6, org.jooq.ConstraintForeignKeyReferencesStep7, org.jooq.ConstraintForeignKeyReferencesStep8, org.jooq.ConstraintForeignKeyReferencesStep9, org.jooq.ConstraintForeignKeyReferencesStep10, org.jooq.ConstraintForeignKeyReferencesStep11, org.jooq.ConstraintForeignKeyReferencesStep12, org.jooq.ConstraintForeignKeyReferencesStep13, org.jooq.ConstraintForeignKeyReferencesStep14, org.jooq.ConstraintForeignKeyReferencesStep15, org.jooq.ConstraintForeignKeyReferencesStep16, org.jooq.ConstraintForeignKeyReferencesStep17, org.jooq.ConstraintForeignKeyReferencesStep18, org.jooq.ConstraintForeignKeyReferencesStep19, org.jooq.ConstraintForeignKeyReferencesStep20, org.jooq.ConstraintForeignKeyReferencesStep21, org.jooq.ConstraintForeignKeyReferencesStep22
    public final ConstraintImpl references(String table) {
        return references((Table<?>) DSL.table(DSL.name(table)), Tools.EMPTY_FIELD);
    }

    @Override // org.jooq.ConstraintForeignKeyReferencesStepN
    public final ConstraintImpl references(String table, String... fields) {
        return references((Table<?>) DSL.table(DSL.name(table)), (Collection<? extends Field<?>>) Tools.fieldsByName(fields));
    }

    @Override // org.jooq.ConstraintForeignKeyReferencesStepN
    public final ConstraintImpl references(String table, Collection<? extends String> fields) {
        return references(table, (String[]) fields.toArray(Tools.EMPTY_STRING));
    }

    @Override // org.jooq.ConstraintForeignKeyReferencesStepN, org.jooq.ConstraintForeignKeyReferencesStep1, org.jooq.ConstraintForeignKeyReferencesStep2, org.jooq.ConstraintForeignKeyReferencesStep3, org.jooq.ConstraintForeignKeyReferencesStep4, org.jooq.ConstraintForeignKeyReferencesStep5, org.jooq.ConstraintForeignKeyReferencesStep6, org.jooq.ConstraintForeignKeyReferencesStep7, org.jooq.ConstraintForeignKeyReferencesStep8, org.jooq.ConstraintForeignKeyReferencesStep9, org.jooq.ConstraintForeignKeyReferencesStep10, org.jooq.ConstraintForeignKeyReferencesStep11, org.jooq.ConstraintForeignKeyReferencesStep12, org.jooq.ConstraintForeignKeyReferencesStep13, org.jooq.ConstraintForeignKeyReferencesStep14, org.jooq.ConstraintForeignKeyReferencesStep15, org.jooq.ConstraintForeignKeyReferencesStep16, org.jooq.ConstraintForeignKeyReferencesStep17, org.jooq.ConstraintForeignKeyReferencesStep18, org.jooq.ConstraintForeignKeyReferencesStep19, org.jooq.ConstraintForeignKeyReferencesStep20, org.jooq.ConstraintForeignKeyReferencesStep21, org.jooq.ConstraintForeignKeyReferencesStep22
    public final ConstraintImpl references(Name table) {
        return references((Table<?>) DSL.table(table), Tools.EMPTY_FIELD);
    }

    @Override // org.jooq.ConstraintForeignKeyReferencesStepN
    public final ConstraintImpl references(Name table, Name... fields) {
        return references((Table<?>) DSL.table(table), (Collection<? extends Field<?>>) Tools.fieldsByName(fields));
    }

    @Override // org.jooq.ConstraintForeignKeyReferencesStepN
    public final ConstraintImpl references(Name table, Collection<? extends Name> fields) {
        return references(table, (Name[]) fields.toArray(Tools.EMPTY_NAME));
    }

    @Override // org.jooq.ConstraintForeignKeyReferencesStepN, org.jooq.ConstraintForeignKeyReferencesStep1, org.jooq.ConstraintForeignKeyReferencesStep2, org.jooq.ConstraintForeignKeyReferencesStep3, org.jooq.ConstraintForeignKeyReferencesStep4, org.jooq.ConstraintForeignKeyReferencesStep5, org.jooq.ConstraintForeignKeyReferencesStep6, org.jooq.ConstraintForeignKeyReferencesStep7, org.jooq.ConstraintForeignKeyReferencesStep8, org.jooq.ConstraintForeignKeyReferencesStep9, org.jooq.ConstraintForeignKeyReferencesStep10, org.jooq.ConstraintForeignKeyReferencesStep11, org.jooq.ConstraintForeignKeyReferencesStep12, org.jooq.ConstraintForeignKeyReferencesStep13, org.jooq.ConstraintForeignKeyReferencesStep14, org.jooq.ConstraintForeignKeyReferencesStep15, org.jooq.ConstraintForeignKeyReferencesStep16, org.jooq.ConstraintForeignKeyReferencesStep17, org.jooq.ConstraintForeignKeyReferencesStep18, org.jooq.ConstraintForeignKeyReferencesStep19, org.jooq.ConstraintForeignKeyReferencesStep20, org.jooq.ConstraintForeignKeyReferencesStep21, org.jooq.ConstraintForeignKeyReferencesStep22
    public final ConstraintImpl references(Table table) {
        return references((Table<?>) table, Tools.EMPTY_FIELD);
    }

    @Override // org.jooq.ConstraintForeignKeyReferencesStepN
    public final ConstraintImpl references(Table<?> table, Field<?>... fields) {
        this.referencesTable = table;
        this.references = fields;
        return this;
    }

    @Override // org.jooq.ConstraintForeignKeyReferencesStepN
    public final ConstraintImpl references(Table<?> table, Collection<? extends Field<?>> fields) {
        return references(table, (Field<?>[]) fields.toArray(Tools.EMPTY_FIELD));
    }

    @Override // org.jooq.ConstraintForeignKeyOnStep
    public final ConstraintImpl onDeleteNoAction() {
        this.onDelete = Action.NO_ACTION;
        return this;
    }

    @Override // org.jooq.ConstraintForeignKeyOnStep
    public final ConstraintImpl onDeleteRestrict() {
        this.onDelete = Action.RESTRICT;
        return this;
    }

    @Override // org.jooq.ConstraintForeignKeyOnStep
    public final ConstraintImpl onDeleteCascade() {
        this.onDelete = Action.CASCADE;
        return this;
    }

    @Override // org.jooq.ConstraintForeignKeyOnStep
    public final ConstraintImpl onDeleteSetNull() {
        this.onDelete = Action.SET_NULL;
        return this;
    }

    @Override // org.jooq.ConstraintForeignKeyOnStep
    public final ConstraintImpl onDeleteSetDefault() {
        this.onDelete = Action.SET_DEFAULT;
        return this;
    }

    @Override // org.jooq.ConstraintForeignKeyOnStep
    public final ConstraintImpl onUpdateNoAction() {
        this.onUpdate = Action.NO_ACTION;
        return this;
    }

    @Override // org.jooq.ConstraintForeignKeyOnStep
    public final ConstraintImpl onUpdateRestrict() {
        this.onUpdate = Action.RESTRICT;
        return this;
    }

    @Override // org.jooq.ConstraintForeignKeyOnStep
    public final ConstraintImpl onUpdateCascade() {
        this.onUpdate = Action.CASCADE;
        return this;
    }

    @Override // org.jooq.ConstraintForeignKeyOnStep
    public final ConstraintImpl onUpdateSetNull() {
        this.onUpdate = Action.SET_NULL;
        return this;
    }

    @Override // org.jooq.ConstraintForeignKeyOnStep
    public final ConstraintImpl onUpdateSetDefault() {
        this.onUpdate = Action.SET_DEFAULT;
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ConstraintTypeStep
    public final <T1> ConstraintImpl foreignKey(Field<T1> field1) {
        return foreignKey((Field<?>[]) new Field[]{field1});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ConstraintTypeStep
    public final <T1, T2> ConstraintImpl foreignKey(Field<T1> field1, Field<T2> field2) {
        return foreignKey((Field<?>[]) new Field[]{field1, field2});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ConstraintTypeStep
    public final <T1, T2, T3> ConstraintImpl foreignKey(Field<T1> field1, Field<T2> field2, Field<T3> field3) {
        return foreignKey((Field<?>[]) new Field[]{field1, field2, field3});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ConstraintTypeStep
    public final <T1, T2, T3, T4> ConstraintImpl foreignKey(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4) {
        return foreignKey((Field<?>[]) new Field[]{field1, field2, field3, field4});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ConstraintTypeStep
    public final <T1, T2, T3, T4, T5> ConstraintImpl foreignKey(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5) {
        return foreignKey((Field<?>[]) new Field[]{field1, field2, field3, field4, field5});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ConstraintTypeStep
    public final <T1, T2, T3, T4, T5, T6> ConstraintImpl foreignKey(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6) {
        return foreignKey((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ConstraintTypeStep
    public final <T1, T2, T3, T4, T5, T6, T7> ConstraintImpl foreignKey(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7) {
        return foreignKey((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ConstraintTypeStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8> ConstraintImpl foreignKey(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8) {
        return foreignKey((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ConstraintTypeStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9> ConstraintImpl foreignKey(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9) {
        return foreignKey((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ConstraintTypeStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> ConstraintImpl foreignKey(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10) {
        return foreignKey((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ConstraintTypeStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> ConstraintImpl foreignKey(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11) {
        return foreignKey((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ConstraintTypeStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> ConstraintImpl foreignKey(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12) {
        return foreignKey((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ConstraintTypeStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> ConstraintImpl foreignKey(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13) {
        return foreignKey((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ConstraintTypeStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> ConstraintImpl foreignKey(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14) {
        return foreignKey((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ConstraintTypeStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> ConstraintImpl foreignKey(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15) {
        return foreignKey((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ConstraintTypeStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> ConstraintImpl foreignKey(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16) {
        return foreignKey((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ConstraintTypeStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> ConstraintImpl foreignKey(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17) {
        return foreignKey((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ConstraintTypeStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> ConstraintImpl foreignKey(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18) {
        return foreignKey((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ConstraintTypeStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> ConstraintImpl foreignKey(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19) {
        return foreignKey((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ConstraintTypeStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> ConstraintImpl foreignKey(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20) {
        return foreignKey((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ConstraintTypeStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21> ConstraintImpl foreignKey(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21) {
        return foreignKey((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ConstraintTypeStep
    public final <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22> ConstraintImpl foreignKey(Field<T1> field1, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7, Field<T8> field8, Field<T9> field9, Field<T10> field10, Field<T11> field11, Field<T12> field12, Field<T13> field13, Field<T14> field14, Field<T15> field15, Field<T16> field16, Field<T17> field17, Field<T18> field18, Field<T19> field19, Field<T20> field20, Field<T21> field21, Field<T22> field22) {
        return foreignKey((Field<?>[]) new Field[]{field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21, field22});
    }

    @Override // org.jooq.ConstraintTypeStep
    public final ConstraintImpl foreignKey(Name field1) {
        return foreignKey(field1);
    }

    @Override // org.jooq.ConstraintTypeStep
    public final ConstraintImpl foreignKey(Name field1, Name field2) {
        return foreignKey(field1, field2);
    }

    @Override // org.jooq.ConstraintTypeStep
    public final ConstraintImpl foreignKey(Name field1, Name field2, Name field3) {
        return foreignKey(field1, field2, field3);
    }

    @Override // org.jooq.ConstraintTypeStep
    public final ConstraintImpl foreignKey(Name field1, Name field2, Name field3, Name field4) {
        return foreignKey(field1, field2, field3, field4);
    }

    @Override // org.jooq.ConstraintTypeStep
    public final ConstraintImpl foreignKey(Name field1, Name field2, Name field3, Name field4, Name field5) {
        return foreignKey(field1, field2, field3, field4, field5);
    }

    @Override // org.jooq.ConstraintTypeStep
    public final ConstraintImpl foreignKey(Name field1, Name field2, Name field3, Name field4, Name field5, Name field6) {
        return foreignKey(field1, field2, field3, field4, field5, field6);
    }

    @Override // org.jooq.ConstraintTypeStep
    public final ConstraintImpl foreignKey(Name field1, Name field2, Name field3, Name field4, Name field5, Name field6, Name field7) {
        return foreignKey(field1, field2, field3, field4, field5, field6, field7);
    }

    @Override // org.jooq.ConstraintTypeStep
    public final ConstraintImpl foreignKey(Name field1, Name field2, Name field3, Name field4, Name field5, Name field6, Name field7, Name field8) {
        return foreignKey(field1, field2, field3, field4, field5, field6, field7, field8);
    }

    @Override // org.jooq.ConstraintTypeStep
    public final ConstraintImpl foreignKey(Name field1, Name field2, Name field3, Name field4, Name field5, Name field6, Name field7, Name field8, Name field9) {
        return foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9);
    }

    @Override // org.jooq.ConstraintTypeStep
    public final ConstraintImpl foreignKey(Name field1, Name field2, Name field3, Name field4, Name field5, Name field6, Name field7, Name field8, Name field9, Name field10) {
        return foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10);
    }

    @Override // org.jooq.ConstraintTypeStep
    public final ConstraintImpl foreignKey(Name field1, Name field2, Name field3, Name field4, Name field5, Name field6, Name field7, Name field8, Name field9, Name field10, Name field11) {
        return foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11);
    }

    @Override // org.jooq.ConstraintTypeStep
    public final ConstraintImpl foreignKey(Name field1, Name field2, Name field3, Name field4, Name field5, Name field6, Name field7, Name field8, Name field9, Name field10, Name field11, Name field12) {
        return foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12);
    }

    @Override // org.jooq.ConstraintTypeStep
    public final ConstraintImpl foreignKey(Name field1, Name field2, Name field3, Name field4, Name field5, Name field6, Name field7, Name field8, Name field9, Name field10, Name field11, Name field12, Name field13) {
        return foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13);
    }

    @Override // org.jooq.ConstraintTypeStep
    public final ConstraintImpl foreignKey(Name field1, Name field2, Name field3, Name field4, Name field5, Name field6, Name field7, Name field8, Name field9, Name field10, Name field11, Name field12, Name field13, Name field14) {
        return foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14);
    }

    @Override // org.jooq.ConstraintTypeStep
    public final ConstraintImpl foreignKey(Name field1, Name field2, Name field3, Name field4, Name field5, Name field6, Name field7, Name field8, Name field9, Name field10, Name field11, Name field12, Name field13, Name field14, Name field15) {
        return foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15);
    }

    @Override // org.jooq.ConstraintTypeStep
    public final ConstraintImpl foreignKey(Name field1, Name field2, Name field3, Name field4, Name field5, Name field6, Name field7, Name field8, Name field9, Name field10, Name field11, Name field12, Name field13, Name field14, Name field15, Name field16) {
        return foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16);
    }

    @Override // org.jooq.ConstraintTypeStep
    public final ConstraintImpl foreignKey(Name field1, Name field2, Name field3, Name field4, Name field5, Name field6, Name field7, Name field8, Name field9, Name field10, Name field11, Name field12, Name field13, Name field14, Name field15, Name field16, Name field17) {
        return foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17);
    }

    @Override // org.jooq.ConstraintTypeStep
    public final ConstraintImpl foreignKey(Name field1, Name field2, Name field3, Name field4, Name field5, Name field6, Name field7, Name field8, Name field9, Name field10, Name field11, Name field12, Name field13, Name field14, Name field15, Name field16, Name field17, Name field18) {
        return foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18);
    }

    @Override // org.jooq.ConstraintTypeStep
    public final ConstraintImpl foreignKey(Name field1, Name field2, Name field3, Name field4, Name field5, Name field6, Name field7, Name field8, Name field9, Name field10, Name field11, Name field12, Name field13, Name field14, Name field15, Name field16, Name field17, Name field18, Name field19) {
        return foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19);
    }

    @Override // org.jooq.ConstraintTypeStep
    public final ConstraintImpl foreignKey(Name field1, Name field2, Name field3, Name field4, Name field5, Name field6, Name field7, Name field8, Name field9, Name field10, Name field11, Name field12, Name field13, Name field14, Name field15, Name field16, Name field17, Name field18, Name field19, Name field20) {
        return foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20);
    }

    @Override // org.jooq.ConstraintTypeStep
    public final ConstraintImpl foreignKey(Name field1, Name field2, Name field3, Name field4, Name field5, Name field6, Name field7, Name field8, Name field9, Name field10, Name field11, Name field12, Name field13, Name field14, Name field15, Name field16, Name field17, Name field18, Name field19, Name field20, Name field21) {
        return foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21);
    }

    @Override // org.jooq.ConstraintTypeStep
    public final ConstraintImpl foreignKey(Name field1, Name field2, Name field3, Name field4, Name field5, Name field6, Name field7, Name field8, Name field9, Name field10, Name field11, Name field12, Name field13, Name field14, Name field15, Name field16, Name field17, Name field18, Name field19, Name field20, Name field21, Name field22) {
        return foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21, field22);
    }

    @Override // org.jooq.ConstraintTypeStep
    public final ConstraintImpl foreignKey(String field1) {
        return foreignKey(field1);
    }

    @Override // org.jooq.ConstraintTypeStep
    public final ConstraintImpl foreignKey(String field1, String field2) {
        return foreignKey(field1, field2);
    }

    @Override // org.jooq.ConstraintTypeStep
    public final ConstraintImpl foreignKey(String field1, String field2, String field3) {
        return foreignKey(field1, field2, field3);
    }

    @Override // org.jooq.ConstraintTypeStep
    public final ConstraintImpl foreignKey(String field1, String field2, String field3, String field4) {
        return foreignKey(field1, field2, field3, field4);
    }

    @Override // org.jooq.ConstraintTypeStep
    public final ConstraintImpl foreignKey(String field1, String field2, String field3, String field4, String field5) {
        return foreignKey(field1, field2, field3, field4, field5);
    }

    @Override // org.jooq.ConstraintTypeStep
    public final ConstraintImpl foreignKey(String field1, String field2, String field3, String field4, String field5, String field6) {
        return foreignKey(field1, field2, field3, field4, field5, field6);
    }

    @Override // org.jooq.ConstraintTypeStep
    public final ConstraintImpl foreignKey(String field1, String field2, String field3, String field4, String field5, String field6, String field7) {
        return foreignKey(field1, field2, field3, field4, field5, field6, field7);
    }

    @Override // org.jooq.ConstraintTypeStep
    public final ConstraintImpl foreignKey(String field1, String field2, String field3, String field4, String field5, String field6, String field7, String field8) {
        return foreignKey(field1, field2, field3, field4, field5, field6, field7, field8);
    }

    @Override // org.jooq.ConstraintTypeStep
    public final ConstraintImpl foreignKey(String field1, String field2, String field3, String field4, String field5, String field6, String field7, String field8, String field9) {
        return foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9);
    }

    @Override // org.jooq.ConstraintTypeStep
    public final ConstraintImpl foreignKey(String field1, String field2, String field3, String field4, String field5, String field6, String field7, String field8, String field9, String field10) {
        return foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10);
    }

    @Override // org.jooq.ConstraintTypeStep
    public final ConstraintImpl foreignKey(String field1, String field2, String field3, String field4, String field5, String field6, String field7, String field8, String field9, String field10, String field11) {
        return foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11);
    }

    @Override // org.jooq.ConstraintTypeStep
    public final ConstraintImpl foreignKey(String field1, String field2, String field3, String field4, String field5, String field6, String field7, String field8, String field9, String field10, String field11, String field12) {
        return foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12);
    }

    @Override // org.jooq.ConstraintTypeStep
    public final ConstraintImpl foreignKey(String field1, String field2, String field3, String field4, String field5, String field6, String field7, String field8, String field9, String field10, String field11, String field12, String field13) {
        return foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13);
    }

    @Override // org.jooq.ConstraintTypeStep
    public final ConstraintImpl foreignKey(String field1, String field2, String field3, String field4, String field5, String field6, String field7, String field8, String field9, String field10, String field11, String field12, String field13, String field14) {
        return foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14);
    }

    @Override // org.jooq.ConstraintTypeStep
    public final ConstraintImpl foreignKey(String field1, String field2, String field3, String field4, String field5, String field6, String field7, String field8, String field9, String field10, String field11, String field12, String field13, String field14, String field15) {
        return foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15);
    }

    @Override // org.jooq.ConstraintTypeStep
    public final ConstraintImpl foreignKey(String field1, String field2, String field3, String field4, String field5, String field6, String field7, String field8, String field9, String field10, String field11, String field12, String field13, String field14, String field15, String field16) {
        return foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16);
    }

    @Override // org.jooq.ConstraintTypeStep
    public final ConstraintImpl foreignKey(String field1, String field2, String field3, String field4, String field5, String field6, String field7, String field8, String field9, String field10, String field11, String field12, String field13, String field14, String field15, String field16, String field17) {
        return foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17);
    }

    @Override // org.jooq.ConstraintTypeStep
    public final ConstraintImpl foreignKey(String field1, String field2, String field3, String field4, String field5, String field6, String field7, String field8, String field9, String field10, String field11, String field12, String field13, String field14, String field15, String field16, String field17, String field18) {
        return foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18);
    }

    @Override // org.jooq.ConstraintTypeStep
    public final ConstraintImpl foreignKey(String field1, String field2, String field3, String field4, String field5, String field6, String field7, String field8, String field9, String field10, String field11, String field12, String field13, String field14, String field15, String field16, String field17, String field18, String field19) {
        return foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19);
    }

    @Override // org.jooq.ConstraintTypeStep
    public final ConstraintImpl foreignKey(String field1, String field2, String field3, String field4, String field5, String field6, String field7, String field8, String field9, String field10, String field11, String field12, String field13, String field14, String field15, String field16, String field17, String field18, String field19, String field20) {
        return foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20);
    }

    @Override // org.jooq.ConstraintTypeStep
    public final ConstraintImpl foreignKey(String field1, String field2, String field3, String field4, String field5, String field6, String field7, String field8, String field9, String field10, String field11, String field12, String field13, String field14, String field15, String field16, String field17, String field18, String field19, String field20, String field21) {
        return foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21);
    }

    @Override // org.jooq.ConstraintTypeStep
    public final ConstraintImpl foreignKey(String field1, String field2, String field3, String field4, String field5, String field6, String field7, String field8, String field9, String field10, String field11, String field12, String field13, String field14, String field15, String field16, String field17, String field18, String field19, String field20, String field21, String field22) {
        return foreignKey(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21, field22);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ConstraintForeignKeyReferencesStep1
    public final ConstraintImpl references(Table table, Field t1) {
        return references((Table<?>) table, (Field<?>[]) new Field[]{t1});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ConstraintForeignKeyReferencesStep2
    public final ConstraintImpl references(Table table, Field t1, Field t2) {
        return references((Table<?>) table, (Field<?>[]) new Field[]{t1, t2});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ConstraintForeignKeyReferencesStep3
    public final ConstraintImpl references(Table table, Field t1, Field t2, Field t3) {
        return references((Table<?>) table, (Field<?>[]) new Field[]{t1, t2, t3});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ConstraintForeignKeyReferencesStep4
    public final ConstraintImpl references(Table table, Field t1, Field t2, Field t3, Field t4) {
        return references((Table<?>) table, (Field<?>[]) new Field[]{t1, t2, t3, t4});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ConstraintForeignKeyReferencesStep5
    public final ConstraintImpl references(Table table, Field t1, Field t2, Field t3, Field t4, Field t5) {
        return references((Table<?>) table, (Field<?>[]) new Field[]{t1, t2, t3, t4, t5});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ConstraintForeignKeyReferencesStep6
    public final ConstraintImpl references(Table table, Field t1, Field t2, Field t3, Field t4, Field t5, Field t6) {
        return references((Table<?>) table, (Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ConstraintForeignKeyReferencesStep7
    public final ConstraintImpl references(Table table, Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7) {
        return references((Table<?>) table, (Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ConstraintForeignKeyReferencesStep8
    public final ConstraintImpl references(Table table, Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7, Field t8) {
        return references((Table<?>) table, (Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7, t8});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ConstraintForeignKeyReferencesStep9
    public final ConstraintImpl references(Table table, Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7, Field t8, Field t9) {
        return references((Table<?>) table, (Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7, t8, t9});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ConstraintForeignKeyReferencesStep10
    public final ConstraintImpl references(Table table, Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7, Field t8, Field t9, Field t10) {
        return references((Table<?>) table, (Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ConstraintForeignKeyReferencesStep11
    public final ConstraintImpl references(Table table, Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7, Field t8, Field t9, Field t10, Field t11) {
        return references((Table<?>) table, (Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ConstraintForeignKeyReferencesStep12
    public final ConstraintImpl references(Table table, Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7, Field t8, Field t9, Field t10, Field t11, Field t12) {
        return references((Table<?>) table, (Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ConstraintForeignKeyReferencesStep13
    public final ConstraintImpl references(Table table, Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7, Field t8, Field t9, Field t10, Field t11, Field t12, Field t13) {
        return references((Table<?>) table, (Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ConstraintForeignKeyReferencesStep14
    public final ConstraintImpl references(Table table, Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7, Field t8, Field t9, Field t10, Field t11, Field t12, Field t13, Field t14) {
        return references((Table<?>) table, (Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ConstraintForeignKeyReferencesStep15
    public final ConstraintImpl references(Table table, Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7, Field t8, Field t9, Field t10, Field t11, Field t12, Field t13, Field t14, Field t15) {
        return references((Table<?>) table, (Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ConstraintForeignKeyReferencesStep16
    public final ConstraintImpl references(Table table, Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7, Field t8, Field t9, Field t10, Field t11, Field t12, Field t13, Field t14, Field t15, Field t16) {
        return references((Table<?>) table, (Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ConstraintForeignKeyReferencesStep17
    public final ConstraintImpl references(Table table, Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7, Field t8, Field t9, Field t10, Field t11, Field t12, Field t13, Field t14, Field t15, Field t16, Field t17) {
        return references((Table<?>) table, (Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ConstraintForeignKeyReferencesStep18
    public final ConstraintImpl references(Table table, Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7, Field t8, Field t9, Field t10, Field t11, Field t12, Field t13, Field t14, Field t15, Field t16, Field t17, Field t18) {
        return references((Table<?>) table, (Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ConstraintForeignKeyReferencesStep19
    public final ConstraintImpl references(Table table, Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7, Field t8, Field t9, Field t10, Field t11, Field t12, Field t13, Field t14, Field t15, Field t16, Field t17, Field t18, Field t19) {
        return references((Table<?>) table, (Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ConstraintForeignKeyReferencesStep20
    public final ConstraintImpl references(Table table, Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7, Field t8, Field t9, Field t10, Field t11, Field t12, Field t13, Field t14, Field t15, Field t16, Field t17, Field t18, Field t19, Field t20) {
        return references((Table<?>) table, (Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ConstraintForeignKeyReferencesStep21
    public final ConstraintImpl references(Table table, Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7, Field t8, Field t9, Field t10, Field t11, Field t12, Field t13, Field t14, Field t15, Field t16, Field t17, Field t18, Field t19, Field t20, Field t21) {
        return references((Table<?>) table, (Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21});
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.ConstraintForeignKeyReferencesStep22
    public final ConstraintImpl references(Table table, Field t1, Field t2, Field t3, Field t4, Field t5, Field t6, Field t7, Field t8, Field t9, Field t10, Field t11, Field t12, Field t13, Field t14, Field t15, Field t16, Field t17, Field t18, Field t19, Field t20, Field t21, Field t22) {
        return references((Table<?>) table, (Field<?>[]) new Field[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, t22});
    }

    @Override // org.jooq.ConstraintForeignKeyReferencesStep1
    public final ConstraintImpl references(Name table, Name field1) {
        return references(table, field1);
    }

    @Override // org.jooq.ConstraintForeignKeyReferencesStep2
    public final ConstraintImpl references(Name table, Name field1, Name field2) {
        return references(table, field1, field2);
    }

    @Override // org.jooq.ConstraintForeignKeyReferencesStep3
    public final ConstraintImpl references(Name table, Name field1, Name field2, Name field3) {
        return references(table, field1, field2, field3);
    }

    @Override // org.jooq.ConstraintForeignKeyReferencesStep4
    public final ConstraintImpl references(Name table, Name field1, Name field2, Name field3, Name field4) {
        return references(table, field1, field2, field3, field4);
    }

    @Override // org.jooq.ConstraintForeignKeyReferencesStep5
    public final ConstraintImpl references(Name table, Name field1, Name field2, Name field3, Name field4, Name field5) {
        return references(table, field1, field2, field3, field4, field5);
    }

    @Override // org.jooq.ConstraintForeignKeyReferencesStep6
    public final ConstraintImpl references(Name table, Name field1, Name field2, Name field3, Name field4, Name field5, Name field6) {
        return references(table, field1, field2, field3, field4, field5, field6);
    }

    @Override // org.jooq.ConstraintForeignKeyReferencesStep7
    public final ConstraintImpl references(Name table, Name field1, Name field2, Name field3, Name field4, Name field5, Name field6, Name field7) {
        return references(table, field1, field2, field3, field4, field5, field6, field7);
    }

    @Override // org.jooq.ConstraintForeignKeyReferencesStep8
    public final ConstraintImpl references(Name table, Name field1, Name field2, Name field3, Name field4, Name field5, Name field6, Name field7, Name field8) {
        return references(table, field1, field2, field3, field4, field5, field6, field7, field8);
    }

    @Override // org.jooq.ConstraintForeignKeyReferencesStep9
    public final ConstraintImpl references(Name table, Name field1, Name field2, Name field3, Name field4, Name field5, Name field6, Name field7, Name field8, Name field9) {
        return references(table, field1, field2, field3, field4, field5, field6, field7, field8, field9);
    }

    @Override // org.jooq.ConstraintForeignKeyReferencesStep10
    public final ConstraintImpl references(Name table, Name field1, Name field2, Name field3, Name field4, Name field5, Name field6, Name field7, Name field8, Name field9, Name field10) {
        return references(table, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10);
    }

    @Override // org.jooq.ConstraintForeignKeyReferencesStep11
    public final ConstraintImpl references(Name table, Name field1, Name field2, Name field3, Name field4, Name field5, Name field6, Name field7, Name field8, Name field9, Name field10, Name field11) {
        return references(table, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11);
    }

    @Override // org.jooq.ConstraintForeignKeyReferencesStep12
    public final ConstraintImpl references(Name table, Name field1, Name field2, Name field3, Name field4, Name field5, Name field6, Name field7, Name field8, Name field9, Name field10, Name field11, Name field12) {
        return references(table, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12);
    }

    @Override // org.jooq.ConstraintForeignKeyReferencesStep13
    public final ConstraintImpl references(Name table, Name field1, Name field2, Name field3, Name field4, Name field5, Name field6, Name field7, Name field8, Name field9, Name field10, Name field11, Name field12, Name field13) {
        return references(table, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13);
    }

    @Override // org.jooq.ConstraintForeignKeyReferencesStep14
    public final ConstraintImpl references(Name table, Name field1, Name field2, Name field3, Name field4, Name field5, Name field6, Name field7, Name field8, Name field9, Name field10, Name field11, Name field12, Name field13, Name field14) {
        return references(table, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14);
    }

    @Override // org.jooq.ConstraintForeignKeyReferencesStep15
    public final ConstraintImpl references(Name table, Name field1, Name field2, Name field3, Name field4, Name field5, Name field6, Name field7, Name field8, Name field9, Name field10, Name field11, Name field12, Name field13, Name field14, Name field15) {
        return references(table, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15);
    }

    @Override // org.jooq.ConstraintForeignKeyReferencesStep16
    public final ConstraintImpl references(Name table, Name field1, Name field2, Name field3, Name field4, Name field5, Name field6, Name field7, Name field8, Name field9, Name field10, Name field11, Name field12, Name field13, Name field14, Name field15, Name field16) {
        return references(table, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16);
    }

    @Override // org.jooq.ConstraintForeignKeyReferencesStep17
    public final ConstraintImpl references(Name table, Name field1, Name field2, Name field3, Name field4, Name field5, Name field6, Name field7, Name field8, Name field9, Name field10, Name field11, Name field12, Name field13, Name field14, Name field15, Name field16, Name field17) {
        return references(table, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17);
    }

    @Override // org.jooq.ConstraintForeignKeyReferencesStep18
    public final ConstraintImpl references(Name table, Name field1, Name field2, Name field3, Name field4, Name field5, Name field6, Name field7, Name field8, Name field9, Name field10, Name field11, Name field12, Name field13, Name field14, Name field15, Name field16, Name field17, Name field18) {
        return references(table, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18);
    }

    @Override // org.jooq.ConstraintForeignKeyReferencesStep19
    public final ConstraintImpl references(Name table, Name field1, Name field2, Name field3, Name field4, Name field5, Name field6, Name field7, Name field8, Name field9, Name field10, Name field11, Name field12, Name field13, Name field14, Name field15, Name field16, Name field17, Name field18, Name field19) {
        return references(table, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19);
    }

    @Override // org.jooq.ConstraintForeignKeyReferencesStep20
    public final ConstraintImpl references(Name table, Name field1, Name field2, Name field3, Name field4, Name field5, Name field6, Name field7, Name field8, Name field9, Name field10, Name field11, Name field12, Name field13, Name field14, Name field15, Name field16, Name field17, Name field18, Name field19, Name field20) {
        return references(table, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20);
    }

    @Override // org.jooq.ConstraintForeignKeyReferencesStep21
    public final ConstraintImpl references(Name table, Name field1, Name field2, Name field3, Name field4, Name field5, Name field6, Name field7, Name field8, Name field9, Name field10, Name field11, Name field12, Name field13, Name field14, Name field15, Name field16, Name field17, Name field18, Name field19, Name field20, Name field21) {
        return references(table, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21);
    }

    @Override // org.jooq.ConstraintForeignKeyReferencesStep22
    public final ConstraintImpl references(Name table, Name field1, Name field2, Name field3, Name field4, Name field5, Name field6, Name field7, Name field8, Name field9, Name field10, Name field11, Name field12, Name field13, Name field14, Name field15, Name field16, Name field17, Name field18, Name field19, Name field20, Name field21, Name field22) {
        return references(table, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21, field22);
    }

    @Override // org.jooq.ConstraintForeignKeyReferencesStep1
    public final ConstraintImpl references(String table, String field1) {
        return references(table, field1);
    }

    @Override // org.jooq.ConstraintForeignKeyReferencesStep2
    public final ConstraintImpl references(String table, String field1, String field2) {
        return references(table, field1, field2);
    }

    @Override // org.jooq.ConstraintForeignKeyReferencesStep3
    public final ConstraintImpl references(String table, String field1, String field2, String field3) {
        return references(table, field1, field2, field3);
    }

    @Override // org.jooq.ConstraintForeignKeyReferencesStep4
    public final ConstraintImpl references(String table, String field1, String field2, String field3, String field4) {
        return references(table, field1, field2, field3, field4);
    }

    @Override // org.jooq.ConstraintForeignKeyReferencesStep5
    public final ConstraintImpl references(String table, String field1, String field2, String field3, String field4, String field5) {
        return references(table, field1, field2, field3, field4, field5);
    }

    @Override // org.jooq.ConstraintForeignKeyReferencesStep6
    public final ConstraintImpl references(String table, String field1, String field2, String field3, String field4, String field5, String field6) {
        return references(table, field1, field2, field3, field4, field5, field6);
    }

    @Override // org.jooq.ConstraintForeignKeyReferencesStep7
    public final ConstraintImpl references(String table, String field1, String field2, String field3, String field4, String field5, String field6, String field7) {
        return references(table, field1, field2, field3, field4, field5, field6, field7);
    }

    @Override // org.jooq.ConstraintForeignKeyReferencesStep8
    public final ConstraintImpl references(String table, String field1, String field2, String field3, String field4, String field5, String field6, String field7, String field8) {
        return references(table, field1, field2, field3, field4, field5, field6, field7, field8);
    }

    @Override // org.jooq.ConstraintForeignKeyReferencesStep9
    public final ConstraintImpl references(String table, String field1, String field2, String field3, String field4, String field5, String field6, String field7, String field8, String field9) {
        return references(table, field1, field2, field3, field4, field5, field6, field7, field8, field9);
    }

    @Override // org.jooq.ConstraintForeignKeyReferencesStep10
    public final ConstraintImpl references(String table, String field1, String field2, String field3, String field4, String field5, String field6, String field7, String field8, String field9, String field10) {
        return references(table, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10);
    }

    @Override // org.jooq.ConstraintForeignKeyReferencesStep11
    public final ConstraintImpl references(String table, String field1, String field2, String field3, String field4, String field5, String field6, String field7, String field8, String field9, String field10, String field11) {
        return references(table, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11);
    }

    @Override // org.jooq.ConstraintForeignKeyReferencesStep12
    public final ConstraintImpl references(String table, String field1, String field2, String field3, String field4, String field5, String field6, String field7, String field8, String field9, String field10, String field11, String field12) {
        return references(table, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12);
    }

    @Override // org.jooq.ConstraintForeignKeyReferencesStep13
    public final ConstraintImpl references(String table, String field1, String field2, String field3, String field4, String field5, String field6, String field7, String field8, String field9, String field10, String field11, String field12, String field13) {
        return references(table, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13);
    }

    @Override // org.jooq.ConstraintForeignKeyReferencesStep14
    public final ConstraintImpl references(String table, String field1, String field2, String field3, String field4, String field5, String field6, String field7, String field8, String field9, String field10, String field11, String field12, String field13, String field14) {
        return references(table, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14);
    }

    @Override // org.jooq.ConstraintForeignKeyReferencesStep15
    public final ConstraintImpl references(String table, String field1, String field2, String field3, String field4, String field5, String field6, String field7, String field8, String field9, String field10, String field11, String field12, String field13, String field14, String field15) {
        return references(table, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15);
    }

    @Override // org.jooq.ConstraintForeignKeyReferencesStep16
    public final ConstraintImpl references(String table, String field1, String field2, String field3, String field4, String field5, String field6, String field7, String field8, String field9, String field10, String field11, String field12, String field13, String field14, String field15, String field16) {
        return references(table, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16);
    }

    @Override // org.jooq.ConstraintForeignKeyReferencesStep17
    public final ConstraintImpl references(String table, String field1, String field2, String field3, String field4, String field5, String field6, String field7, String field8, String field9, String field10, String field11, String field12, String field13, String field14, String field15, String field16, String field17) {
        return references(table, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17);
    }

    @Override // org.jooq.ConstraintForeignKeyReferencesStep18
    public final ConstraintImpl references(String table, String field1, String field2, String field3, String field4, String field5, String field6, String field7, String field8, String field9, String field10, String field11, String field12, String field13, String field14, String field15, String field16, String field17, String field18) {
        return references(table, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18);
    }

    @Override // org.jooq.ConstraintForeignKeyReferencesStep19
    public final ConstraintImpl references(String table, String field1, String field2, String field3, String field4, String field5, String field6, String field7, String field8, String field9, String field10, String field11, String field12, String field13, String field14, String field15, String field16, String field17, String field18, String field19) {
        return references(table, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19);
    }

    @Override // org.jooq.ConstraintForeignKeyReferencesStep20
    public final ConstraintImpl references(String table, String field1, String field2, String field3, String field4, String field5, String field6, String field7, String field8, String field9, String field10, String field11, String field12, String field13, String field14, String field15, String field16, String field17, String field18, String field19, String field20) {
        return references(table, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20);
    }

    @Override // org.jooq.ConstraintForeignKeyReferencesStep21
    public final ConstraintImpl references(String table, String field1, String field2, String field3, String field4, String field5, String field6, String field7, String field8, String field9, String field10, String field11, String field12, String field13, String field14, String field15, String field16, String field17, String field18, String field19, String field20, String field21) {
        return references(table, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21);
    }

    @Override // org.jooq.ConstraintForeignKeyReferencesStep22
    public final ConstraintImpl references(String table, String field1, String field2, String field3, String field4, String field5, String field6, String field7, String field8, String field9, String field10, String field11, String field12, String field13, String field14, String field15, String field16, String field17, String field18, String field19, String field20, String field21, String field22) {
        return references(table, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21, field22);
    }

    @Override // org.jooq.ConstraintEnforcementStep
    public final ConstraintImpl enforced() {
        this.enforced = true;
        return this;
    }

    @Override // org.jooq.ConstraintEnforcementStep
    public final ConstraintImpl notEnforced() {
        this.enforced = false;
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ConstraintImpl$Action.class */
    public enum Action {
        NO_ACTION("no action"),
        RESTRICT("restrict"),
        CASCADE("cascade"),
        SET_NULL("set null"),
        SET_DEFAULT("set default");

        Keyword keyword;

        Action(String sql) {
            this.keyword = DSL.keyword(sql);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean matchingPrimaryKey(Field<?> identity) {
        return identity != null && this.primaryKey != null && this.primaryKey.length == 1 && this.primaryKey[0].getName().equals(identity.getName());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean supported(Context<?> ctx) {
        return ((this.primaryKey == null || NO_SUPPORT_PK.contains(ctx.dialect())) && (this.unique == null || NO_SUPPORT_UK.contains(ctx.dialect())) && ((this.references == null || NO_SUPPORT_FK.contains(ctx.dialect())) && (this.check == null || NO_SUPPORT_CHECK.contains(ctx.dialect())))) ? false : true;
    }
}
