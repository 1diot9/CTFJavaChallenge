package org.jooq.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.XMLAttributes;
import org.jooq.impl.QOM;
import org.jooq.impl.Tools;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/XMLAttributesImpl.class */
public final class XMLAttributesImpl extends AbstractQueryPart implements XMLAttributes {
    static final XMLAttributes EMPTY = new XMLAttributesImpl(Collections.emptyList());
    final SelectFieldList<Field<?>> attributes;

    /* JADX INFO: Access modifiers changed from: package-private */
    public XMLAttributesImpl(Collection<? extends Field<?>> attributes) {
        this.attributes = new SelectFieldList<>(attributes);
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        boolean format = ctx.format() && (this.attributes.size() > 1 || (this.attributes.size() == 1 && !Tools.isSimple(ctx, Tools.unalias((Field) this.attributes.get(0)))));
        ctx.data(Tools.BooleanDataKey.DATA_AS_REQUIRED, true, c -> {
            c.visit(Names.N_XMLATTRIBUTES).sql('(');
            if (format) {
                c.formatIndentStart().formatNewLine();
            }
            c.declareFields(true, x -> {
                x.visit(new SelectFieldList(this.attributes).map((java.util.function.Function) XMLElement.xmlCastMapper(ctx)));
            });
            if (format) {
                c.formatIndentEnd().formatNewLine();
            }
            c.sql(')');
        });
    }

    @Override // org.jooq.XMLAttributes
    public final QOM.UnmodifiableList<? extends Field<?>> $attributes() {
        return QOM.unmodifiable((List) this.attributes);
    }
}
