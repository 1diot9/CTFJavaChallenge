package org.jooq.impl;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.SQLDialect;
import org.jooq.XML;
import org.jooq.XMLAttributes;
import org.jooq.impl.QOM;
import org.jooq.impl.Tools;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/XMLElement.class */
public final class XMLElement extends AbstractField<XML> implements QOM.XMLElement {
    private final Name elementName;
    private final XMLAttributes attributes;
    private final QueryPartList<Field<?>> content;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.jooq.impl.XMLElement$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/XMLElement$1.class */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jooq$SQLDialect = new int[SQLDialect.values().length];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public XMLElement(Name elementName, XMLAttributes attributes, Collection<? extends Field<?>> content) {
        super(Names.N_XMLELEMENT, SQLDataType.XML);
        this.elementName = elementName;
        this.attributes = attributes == null ? XMLAttributesImpl.EMPTY : attributes;
        this.content = new QueryPartList<>(content);
    }

    /* JADX WARN: Type inference failed for: r0v18, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v25, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        boolean hasContent = !this.content.isEmpty();
        boolean hasAttributes = !((XMLAttributesImpl) this.attributes).attributes.isEmpty();
        boolean format = hasAttributes || !this.content.isSimple(ctx);
        Consumer<Context<?>> accept0 = c -> {
            c.visit(Keywords.K_NAME).sql(' ').visit(this.elementName);
            if (hasAttributes) {
                if (format) {
                    c.sql(',').formatSeparator().visit(this.attributes);
                } else {
                    c.sql(", ").visit(this.attributes);
                }
            }
            if (hasContent) {
                if (format) {
                    c.sql(',').formatSeparator().visit(QueryPartCollectionView.wrap(this.content).map(xmlCastMapper(ctx)));
                } else {
                    c.sql(", ").visit(QueryPartCollectionView.wrap(this.content).map(xmlCastMapper(ctx)));
                }
            }
        };
        ctx.visit(Names.N_XMLELEMENT).sql('(');
        if (format) {
            ctx.formatIndentStart().formatNewLine().data(Tools.BooleanDataKey.DATA_LIST_ALREADY_INDENTED, true, c2 -> {
                accept0.accept(c2);
            }).formatIndentEnd().formatNewLine();
        } else {
            accept0.accept(ctx);
        }
        ctx.sql(')');
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final java.util.function.Function<? super Field<?>, ? extends Field<?>> xmlCastMapper(Context<?> ctx) {
        return field -> {
            return xmlCast(ctx, field);
        };
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Field<?> xmlCast(Context<?> ctx, Field<?> field) {
        field.getDataType();
        switch (AnonymousClass1.$SwitchMap$org$jooq$SQLDialect[ctx.family().ordinal()]) {
            default:
                if (field != field && Tools.aliased(field) != null) {
                    return field.as(field);
                }
                return field;
        }
    }

    @Override // org.jooq.impl.QOM.XMLElement
    public final Name $elementName() {
        return this.elementName;
    }

    @Override // org.jooq.impl.QOM.XMLElement
    public final XMLAttributes $attributes() {
        return this.attributes;
    }

    @Override // org.jooq.impl.QOM.XMLElement
    public final QOM.UnmodifiableList<? extends Field<?>> $content() {
        return QOM.unmodifiable((List) this.content);
    }
}
