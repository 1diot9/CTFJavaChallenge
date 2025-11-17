package org.jooq.impl;

import org.jooq.BindContext;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.QualifiedRecord;
import org.jooq.RecordQualifier;
import org.jooq.RenderContext;
import org.jooq.conf.ParamType;
import org.jooq.exception.SQLDialectNotSupportedException;
import org.jooq.impl.DefaultBinding;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QualifiedRecordConstant.class */
public final class QualifiedRecordConstant<R extends QualifiedRecord<R>> extends AbstractParam<R> implements QOM.UNotYetImplemented {
    final RecordQualifier<R> qualifier;

    /* JADX INFO: Access modifiers changed from: package-private */
    public QualifiedRecordConstant(R value, RecordQualifier<R> qualifier) {
        super(value, qualifier.getDataType());
        this.qualifier = qualifier;
    }

    @Override // org.jooq.impl.AbstractParam, org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public void accept(Context<?> ctx) {
        if (ctx instanceof RenderContext) {
            toSQL0((RenderContext) ctx);
        } else {
            bind0((BindContext) ctx);
        }
    }

    final void toSQL0(RenderContext ctx) {
        ParamType paramType = ctx.paramType();
        if (isInline()) {
            ctx.paramType(ParamType.INLINED);
        }
        switch (ctx.family()) {
            case POSTGRES:
            case YUGABYTEDB:
                toSQLInline(ctx);
                break;
            default:
                toSQLInline(ctx);
                break;
        }
        if (isInline()) {
            ctx.paramType(paramType);
        }
    }

    private final void toSQLInline(RenderContext ctx) {
        Cast.renderCastIf(ctx, c -> {
            if (this.value == 0) {
                c.visit(Keywords.K_NULL);
                return;
            }
            switch (c.family()) {
                case POSTGRES:
                case YUGABYTEDB:
                    c.visit(Keywords.K_ROW);
                    break;
                default:
                    c.visit(mappedQualifier(ctx));
                    break;
            }
            c.sql('(');
            String separator = "";
            for (Field<?> field : ((QualifiedRecord) this.value).fields()) {
                c.sql(separator);
                c.visit((Field<?>) DSL.val(((QualifiedRecord) this.value).get(field), field));
                separator = ", ";
            }
            c.sql(')');
        }, c2 -> {
            c2.visit(mappedQualifier(ctx));
        }, () -> {
            return DefaultBinding.DefaultRecordBinding.REQUIRE_RECORD_CAST.contains(ctx.dialect());
        });
    }

    private final RecordQualifier<?> mappedQualifier(RenderContext renderContext) {
        RecordQualifier<?> mappedQualifier = Tools.getMappedQualifier(renderContext, this.qualifier);
        return mappedQualifier != null ? mappedQualifier : this.qualifier;
    }

    @Deprecated
    private final String getInlineConstructor(RenderContext ctx) {
        switch (ctx.family()) {
            case POSTGRES:
            case YUGABYTEDB:
                return "ROW";
            default:
                return Tools.getMappedUDTName(ctx, (QualifiedRecord<?>) this.value);
        }
    }

    final void bind0(BindContext ctx) {
        switch (ctx.family()) {
            case POSTGRES:
            case YUGABYTEDB:
                if (this.value != 0) {
                    for (Field<?> field : ((QualifiedRecord) this.value).fields()) {
                        ctx.visit((Field<?>) DSL.val(((QualifiedRecord) this.value).get(field), field.getDataType()));
                    }
                    return;
                }
                return;
            default:
                throw new SQLDialectNotSupportedException("UDTs not supported in dialect " + String.valueOf(ctx.dialect()));
        }
    }
}
