package org.jooq.impl;

import ch.qos.logback.core.CoreConstants;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Param;
import org.jooq.RenderContext;
import org.jooq.conf.ParamType;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.QOM;
import org.jooq.impl.Tools;
import org.jooq.tools.JooqLogger;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Val.class */
public final class Val<T> extends AbstractParam<T> implements QOM.UEmpty {
    private static final JooqLogger log = JooqLogger.getLogger((Class<?>) Val.class);
    private static final ConcurrentHashMap<Class<?>, Object> legacyWarnings = new ConcurrentHashMap<>();
    final boolean inferredDataType;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Val(T value, DataType<T> type, boolean inferredDataType) {
        super(value, type(value, type));
        this.inferredDataType = inferredDataType;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Val(T value, DataType<T> type, boolean inferredDataType, String paramName) {
        super(value, type(value, type), paramName);
        this.inferredDataType = inferredDataType;
    }

    private static final <T> DataType<T> type(T value, DataType<T> type) {
        return value == null ? type.null_() : type.notNull();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final <U> Param<U> convertTo(DataType<U> type) {
        DataType<T> dataType = getDataType();
        if (dataType instanceof DataTypeProxy) {
            DataTypeProxy<?> p = (DataTypeProxy) dataType;
            if ((p.type() instanceof LegacyConvertedDataType) && type == SQLDataType.OTHER) {
                type = p.type();
                if (legacyWarnings.size() < 8 && legacyWarnings.put(type.getType(), "") == null) {
                    log.warn("Deprecation", "User-defined, converted data type " + String.valueOf(type.getType()) + " was registered statically, which will be unsupported in the future, see https://github.com/jOOQ/jOOQ/issues/9492. Please use explicit data types in generated code, or e.g. with DSL.val(Object, DataType), or DSL.inline(Object, DataType).", new SQLWarning("Static type registry usage"));
                }
            }
            return convertTo0(type);
        }
        if (type instanceof ConvertedDataType) {
            return convertTo0(type);
        }
        if (SQLDataType.OTHER.equals(getDataType())) {
            return new ConvertedVal(this, type);
        }
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Val<T> copy(Object newValue) {
        Val<T> w = new Val<>(getDataType().convert(newValue), getDataType(), this.inferredDataType, getParamName());
        w.setInline0(isInline());
        return w;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final <U> Val<U> convertTo0(DataType<U> type) {
        Val<U> w = new Val<>(type.convert(getValue()), type, this.inferredDataType, getParamName());
        w.setInline0(isInline());
        return w;
    }

    @Override // org.jooq.impl.AbstractParam, org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public void accept(Context<?> ctx) {
        if (getDataType().isEmbeddable()) {
            if (AbstractRowAsField.forceMultisetContent(ctx, () -> {
                return Tools.embeddedFields(this).length > 1;
            })) {
                AbstractRowAsField.acceptMultisetContent(ctx, Tools.row0(Tools.embeddedFields(this)), this, this::acceptDefaultEmbeddable);
                return;
            } else {
                acceptDefaultEmbeddable(ctx);
                return;
            }
        }
        if (ctx instanceof RenderContext) {
            RenderContext r = (RenderContext) ctx;
            ParamType paramType = ctx.paramType();
            if (isInline(ctx)) {
                ctx.paramType(ParamType.INLINED);
            }
            try {
                getBinding().sql(new DefaultBindingSQLContext(ctx.configuration(), ctx.data(), r, this.value, getBindVariable(ctx)));
                ctx.paramType(paramType);
                return;
            } catch (SQLException e) {
                throw new DataAccessException("Error while generating SQL for Binding", e);
            }
        }
        if (!isInline(ctx)) {
            ctx.bindValue(this.value, this);
        }
    }

    private void acceptDefaultEmbeddable(Context<?> ctx) {
        ctx.data(Tools.BooleanDataKey.DATA_LIST_ALREADY_INDENTED, true, c -> {
            c.visit(QueryPartListView.wrap(Tools.embeddedFields(this)));
        });
    }

    @NotNull
    final String getBindVariable(Context<?> ctx) {
        if (ctx.paramType() == ParamType.NAMED || ctx.paramType() == ParamType.NAMED_OR_INLINED) {
            int index = ctx.nextIndex();
            String prefix = (String) StringUtils.defaultIfNull(ctx.settings().getRenderNamedParamPrefix(), ":");
            if (StringUtils.isBlank(getParamName())) {
                return prefix + index;
            }
            return prefix + getParamName();
        }
        return CoreConstants.NA;
    }

    @Override // org.jooq.impl.AbstractParam, org.jooq.Param
    public final Param<T> $value(T newValue) {
        return copy(newValue);
    }

    @Override // org.jooq.impl.AbstractParam, org.jooq.Param
    public final Param<T> $inline(boolean inline) {
        Val<T> w = new Val<>(this.value, getDataType(), this.inferredDataType, getParamName());
        w.setInline0(inline);
        return w;
    }
}
