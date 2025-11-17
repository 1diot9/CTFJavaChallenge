package org.jooq.impl;

import java.util.Set;
import org.jooq.AlterTypeFinalStep;
import org.jooq.AlterTypeRenameValueToStep;
import org.jooq.AlterTypeStep;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function7;
import org.jooq.Name;
import org.jooq.SQLDialect;
import org.jooq.Schema;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AlterTypeImpl.class */
final class AlterTypeImpl extends AbstractDDLQuery implements QOM.AlterType, AlterTypeStep, AlterTypeRenameValueToStep, AlterTypeFinalStep {
    final Name type;
    final boolean ifExists;
    Name renameTo;
    Schema setSchema;
    Field<String> addValue;
    Field<String> renameValue;
    Field<String> renameValueTo;
    private static final Set<SQLDialect> NO_SUPPORT_IF_EXISTS = SQLDialect.supportedUntil(SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB);

    @Override // org.jooq.AlterTypeStep
    public /* bridge */ /* synthetic */ AlterTypeRenameValueToStep renameValue(Field field) {
        return renameValue((Field<String>) field);
    }

    @Override // org.jooq.AlterTypeStep
    public /* bridge */ /* synthetic */ AlterTypeFinalStep addValue(Field field) {
        return addValue((Field<String>) field);
    }

    @Override // org.jooq.AlterTypeRenameValueToStep
    public /* bridge */ /* synthetic */ AlterTypeFinalStep to(Field field) {
        return to((Field<String>) field);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AlterTypeImpl(Configuration configuration, Name type, boolean ifExists) {
        this(configuration, type, ifExists, null, null, null, null, null);
    }

    AlterTypeImpl(Configuration configuration, Name type, boolean ifExists, Name renameTo, Schema setSchema, Field<String> addValue, Field<String> renameValue, Field<String> renameValueTo) {
        super(configuration);
        this.type = type;
        this.ifExists = ifExists;
        this.renameTo = renameTo;
        this.setSchema = setSchema;
        this.addValue = addValue;
        this.renameValue = renameValue;
        this.renameValueTo = renameValueTo;
    }

    @Override // org.jooq.AlterTypeStep
    public final AlterTypeImpl renameTo(String renameTo) {
        return renameTo(DSL.name(renameTo));
    }

    @Override // org.jooq.AlterTypeStep
    public final AlterTypeImpl renameTo(Name renameTo) {
        this.renameTo = renameTo;
        return this;
    }

    @Override // org.jooq.AlterTypeStep
    public final AlterTypeImpl setSchema(String setSchema) {
        return setSchema(DSL.schema(DSL.name(setSchema)));
    }

    @Override // org.jooq.AlterTypeStep
    public final AlterTypeImpl setSchema(Name setSchema) {
        return setSchema(DSL.schema(setSchema));
    }

    @Override // org.jooq.AlterTypeStep
    public final AlterTypeImpl setSchema(Schema setSchema) {
        this.setSchema = setSchema;
        return this;
    }

    @Override // org.jooq.AlterTypeStep
    public final AlterTypeImpl addValue(String addValue) {
        return addValue((Field<String>) Tools.field(addValue));
    }

    @Override // org.jooq.AlterTypeStep
    public final AlterTypeImpl addValue(Field<String> addValue) {
        this.addValue = addValue;
        return this;
    }

    @Override // org.jooq.AlterTypeStep
    public final AlterTypeImpl renameValue(String renameValue) {
        return renameValue((Field<String>) Tools.field(renameValue));
    }

    @Override // org.jooq.AlterTypeStep
    public final AlterTypeImpl renameValue(Field<String> renameValue) {
        this.renameValue = renameValue;
        return this;
    }

    @Override // org.jooq.AlterTypeRenameValueToStep
    public final AlterTypeImpl to(String renameValueTo) {
        return to((Field<String>) Tools.field(renameValueTo));
    }

    @Override // org.jooq.AlterTypeRenameValueToStep
    public final AlterTypeImpl to(Field<String> renameValueTo) {
        this.renameValueTo = renameValueTo;
        return this;
    }

    private final boolean supportsIfExists(Context<?> ctx) {
        return !NO_SUPPORT_IF_EXISTS.contains(ctx.dialect());
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (this.ifExists && !supportsIfExists(ctx)) {
            Tools.tryCatch(ctx, DDLStatementType.ALTER_TYPE, c -> {
                accept0(c);
            });
        } else {
            accept0(ctx);
        }
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v16, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v26, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v32, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v38, types: [org.jooq.Context] */
    private final void accept0(Context<?> ctx) {
        ctx.visit(Keywords.K_ALTER).sql(' ').visit(Keywords.K_TYPE).sql(' ').visit(this.type).sql(' ');
        if (this.renameTo != null) {
            ctx.visit(Keywords.K_RENAME_TO).sql(' ').qualify(false, c -> {
                c.visit(this.renameTo);
            });
            return;
        }
        if (this.setSchema != null) {
            ctx.visit(Keywords.K_SET).sql(' ').visit(Keywords.K_SCHEMA).sql(' ').visit(this.setSchema);
        } else if (this.addValue != null) {
            ctx.visit(Keywords.K_ADD).sql(' ').visit(Keywords.K_VALUE).sql(' ').visit((Field<?>) this.addValue);
        } else if (this.renameValue != null) {
            ctx.visit(Keywords.K_RENAME).sql(' ').visit(Keywords.K_VALUE).sql(' ').visit((Field<?>) this.renameValue).sql(' ').visit(Keywords.K_TO).sql(' ').visit((Field<?>) this.renameValueTo);
        }
    }

    @Override // org.jooq.impl.QOM.AlterType
    public final Name $type() {
        return this.type;
    }

    @Override // org.jooq.impl.QOM.AlterType
    public final boolean $ifExists() {
        return this.ifExists;
    }

    @Override // org.jooq.impl.QOM.AlterType
    public final Name $renameTo() {
        return this.renameTo;
    }

    @Override // org.jooq.impl.QOM.AlterType
    public final Schema $setSchema() {
        return this.setSchema;
    }

    @Override // org.jooq.impl.QOM.AlterType
    public final Field<String> $addValue() {
        return this.addValue;
    }

    @Override // org.jooq.impl.QOM.AlterType
    public final Field<String> $renameValue() {
        return this.renameValue;
    }

    @Override // org.jooq.impl.QOM.AlterType
    public final Field<String> $renameValueTo() {
        return this.renameValueTo;
    }

    @Override // org.jooq.impl.QOM.AlterType
    public final QOM.AlterType $type(Name newValue) {
        return $constructor().apply(newValue, Boolean.valueOf($ifExists()), $renameTo(), $setSchema(), $addValue(), $renameValue(), $renameValueTo());
    }

    @Override // org.jooq.impl.QOM.AlterType
    public final QOM.AlterType $ifExists(boolean newValue) {
        return $constructor().apply($type(), Boolean.valueOf(newValue), $renameTo(), $setSchema(), $addValue(), $renameValue(), $renameValueTo());
    }

    @Override // org.jooq.impl.QOM.AlterType
    public final QOM.AlterType $renameTo(Name newValue) {
        return $constructor().apply($type(), Boolean.valueOf($ifExists()), newValue, $setSchema(), $addValue(), $renameValue(), $renameValueTo());
    }

    @Override // org.jooq.impl.QOM.AlterType
    public final QOM.AlterType $setSchema(Schema newValue) {
        return $constructor().apply($type(), Boolean.valueOf($ifExists()), $renameTo(), newValue, $addValue(), $renameValue(), $renameValueTo());
    }

    @Override // org.jooq.impl.QOM.AlterType
    public final QOM.AlterType $addValue(Field<String> newValue) {
        return $constructor().apply($type(), Boolean.valueOf($ifExists()), $renameTo(), $setSchema(), newValue, $renameValue(), $renameValueTo());
    }

    @Override // org.jooq.impl.QOM.AlterType
    public final QOM.AlterType $renameValue(Field<String> newValue) {
        return $constructor().apply($type(), Boolean.valueOf($ifExists()), $renameTo(), $setSchema(), $addValue(), newValue, $renameValueTo());
    }

    @Override // org.jooq.impl.QOM.AlterType
    public final QOM.AlterType $renameValueTo(Field<String> newValue) {
        return $constructor().apply($type(), Boolean.valueOf($ifExists()), $renameTo(), $setSchema(), $addValue(), $renameValue(), newValue);
    }

    public final Function7<? super Name, ? super Boolean, ? super Name, ? super Schema, ? super Field<String>, ? super Field<String>, ? super Field<String>, ? extends QOM.AlterType> $constructor() {
        return (a1, a2, a3, a4, a5, a6, a7) -> {
            return new AlterTypeImpl(configuration(), a1, a2.booleanValue(), a3, a4, a5, a6, a7);
        };
    }
}
