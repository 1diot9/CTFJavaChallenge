package org.jooq.impl;

import java.util.Set;
import java.util.UUID;
import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.JSONEntry;
import org.jooq.JSONEntryValueStep;
import org.jooq.Param;
import org.jooq.Record1;
import org.jooq.SQLDialect;
import org.jooq.Scope;
import org.jooq.Select;
import org.jooq.conf.NestedCollectionEmulation;
import org.springframework.beans.factory.support.PropertiesBeanDefinitionReader;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/JSONEntryImpl.class */
public final class JSONEntryImpl<T> extends AbstractQueryPart implements JSONEntry<T>, JSONEntryValueStep {
    static final Set<SQLDialect> SUPPORT_JSON_MERGE_PRESERVE = SQLDialect.supportedBy(SQLDialect.MARIADB, SQLDialect.MYSQL);
    private final Field<String> key;
    private final Field<T> value;

    /* JADX INFO: Access modifiers changed from: package-private */
    public JSONEntryImpl(Field<String> key) {
        this(key, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public JSONEntryImpl(Field<String> key, Field<T> value) {
        this.key = key;
        this.value = value;
    }

    @Override // org.jooq.JSONEntry
    public final Field<String> key() {
        return this.key;
    }

    @Override // org.jooq.JSONEntry
    public final Field<T> value() {
        return this.value;
    }

    @Override // org.jooq.JSONEntryValueStep
    public final <X> JSONEntry<X> value(X newValue) {
        return value((Field) Tools.field(newValue));
    }

    @Override // org.jooq.JSONEntryValueStep
    public final <X> JSONEntry<X> value(Field<X> newValue) {
        return new JSONEntryImpl(this.key, newValue);
    }

    @Override // org.jooq.JSONEntryValueStep
    public final <X> JSONEntry<X> value(Select<? extends Record1<X>> newValue) {
        return value((Field) DSL.field(newValue));
    }

    /* JADX WARN: Type inference failed for: r0v11, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v19, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v3, types: [org.jooq.Context] */
    @Override // org.jooq.QueryPartInternal
    public void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case MARIADB:
            case MYSQL:
            case POSTGRES:
            case SQLITE:
            case YUGABYTEDB:
                ctx.visit((Field<?>) this.key).sql(", ").visit(jsonCast(ctx, this.value));
                return;
            case TRINO:
                ctx.visit(Keywords.K_KEY).sql(' ').visit(jsonCast(ctx, this.key)).sql(' ').visit(Keywords.K_VALUE).sql(' ').visit(jsonCast(ctx, this.value));
                return;
            default:
                ctx.visit(Keywords.K_KEY).sql(' ').visit((Field<?>) this.key).sql(' ').visit(Keywords.K_VALUE).sql(' ').visit(jsonCast(ctx, this.value));
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final java.util.function.Function<? super Field<?>, ? extends Field<?>> jsonCastMapper(Context<?> ctx) {
        return field -> {
            return jsonCast(ctx, field);
        };
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Field<?> jsonCast(Context<?> ctx, Field<?> field) {
        return jsonCast(ctx, field, false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Field<?> jsonCast(Context<?> ctx, Field<?> field, boolean castJSONTypes) {
        DataType<?> type = field.getDataType();
        switch (ctx.family()) {
            case MARIADB:
            case MYSQL:
                if (type.getSQLDataType() == SQLDataType.BIT) {
                    return field.cast(SQLDataType.BOOLEAN);
                }
                if (isType(type, Boolean.class)) {
                    return DSL.inlined((Field) field);
                }
                if (castJSONTypes && type.isJSON()) {
                    return ctx.family() == SQLDialect.MYSQL ? field.cast(field.getDataType()) : DSL.function(Names.N_JSON_EXTRACT, field.getDataType(), (Field<?>[]) new Field[]{field, DSL.inline(PropertiesBeanDefinitionReader.CONSTRUCTOR_ARG_PREFIX)});
                }
                break;
            case POSTGRES:
            case YUGABYTEDB:
                if (field instanceof Param) {
                    if (!field.getDataType().isOther()) {
                        return field.cast(field.getDataType());
                    }
                    return field.cast(SQLDataType.VARCHAR);
                }
                return field;
            case SQLITE:
                if (isType(type, Boolean.class)) {
                    return DSL.function(Names.N_JSON, SQLDataType.JSON, booleanCase(field));
                }
                if (type.isBinary()) {
                    return DSL.when(field.isNotNull(), DSL.function(Names.N_HEX, SQLDataType.VARCHAR, field));
                }
                if (castJSONTypes && type.isJSON()) {
                    return DSL.function(Names.N_JSON, SQLDataType.JSON, field);
                }
                break;
            case TRINO:
                if (type.getSQLDataType() == SQLDataType.CHAR || type.isUUID() || (type.isTemporal() && !type.isTimestamp())) {
                    return field.cast(SQLDataType.VARCHAR);
                }
                if (type.isBinary()) {
                    return DSL.function(Names.N_TO_HEX, SQLDataType.VARCHAR, field);
                }
                return field;
            case H2:
                if (isType(type, UUID.class)) {
                    return field.cast(SQLDataType.VARCHAR(36));
                }
                if (type.isEnum()) {
                    return field.cast(SQLDataType.VARCHAR);
                }
                if (type.isTemporal()) {
                    return field.cast(SQLDataType.VARCHAR);
                }
                if (type.isBinary()) {
                    return DSL.function(Names.N_RAWTOHEX, SQLDataType.VARCHAR, field);
                }
                break;
        }
        return field;
    }

    private static Field<String> booleanCase(Field<?> field) {
        return DSL.case_((Field) field).when((Field) DSL.inline(true), (Field) DSL.inline("true")).when((Field) DSL.inline(false), (Field) DSL.inline("false"));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T> Field<T> unescapeNestedJSON(Context<?> ctx, Field<T> value) {
        if (isJSON(ctx, value.getDataType())) {
            switch (ctx.family()) {
                case MARIADB:
                case MYSQL:
                case SQLITE:
                    return DSL.function(Names.N_JSON_EXTRACT, value.getDataType(), (Field<?>[]) new Field[]{value, DSL.inline(PropertiesBeanDefinitionReader.CONSTRUCTOR_ARG_PREFIX)});
            }
        }
        return value;
    }

    static final boolean isType(DataType<?> t, Class<?> type) {
        return ConvertedDataType.delegate(t).getType() == type;
    }

    static final boolean isJSON(Context<?> ctx, DataType<?> type) {
        DataType<?> t = ConvertedDataType.delegate(type);
        return t.isJSON() || (t.isEmbeddable() && AbstractRowAsField.forceMultisetContent(ctx, () -> {
            return t.getRow().size() > 1;
        }) && emulateMultisetWithJSON(ctx)) || ((t.isRecord() && AbstractRowAsField.forceMultisetContent(ctx, () -> {
            return t.getRow().size() > 1;
        }) && emulateMultisetWithJSON(ctx)) || (t.isMultiset() && emulateMultisetWithJSON(ctx)));
    }

    private static final boolean emulateMultisetWithJSON(Scope scope) {
        return Tools.emulateMultiset(scope.configuration()) == NestedCollectionEmulation.JSON || Tools.emulateMultiset(scope.configuration()) == NestedCollectionEmulation.JSONB;
    }

    static final Field<?> booleanValAsVarchar(Field<?> field) {
        if (!(field instanceof Val)) {
            return booleanCase(field);
        }
        Val<?> v = (Val) field;
        return v.convertTo0(SQLDataType.VARCHAR);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Field<?> jsonMerge(Scope scope, String empty, Field<?>... fields) {
        return DSL.function(SUPPORT_JSON_MERGE_PRESERVE.contains(scope.dialect()) ? Names.N_JSON_MERGE_PRESERVE : Names.N_JSON_MERGE, fields[0].getDataType(), Tools.combine((Field<?>) DSL.inline(empty), fields));
    }

    @Override // org.jooq.JSONEntry
    public final Field<String> $key() {
        return this.key;
    }

    @Override // org.jooq.JSONEntry
    public final Field<?> $value() {
        return this.value;
    }
}
