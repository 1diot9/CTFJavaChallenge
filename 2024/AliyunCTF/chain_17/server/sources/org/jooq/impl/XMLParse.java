package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.XML;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/XMLParse.class */
public final class XMLParse extends AbstractField<XML> implements QOM.XMLParse {
    private final Field<String> content;
    private final QOM.DocumentOrContent documentOrContent;

    /* JADX INFO: Access modifiers changed from: package-private */
    public XMLParse(Field<String> content, QOM.DocumentOrContent documentOrContent) {
        super(Names.N_XMLPARSE, SQLDataType.XML);
        this.content = content;
        this.documentOrContent = documentOrContent;
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case POSTGRES:
            default:
                acceptStandard(ctx, this.documentOrContent, this.content);
                return;
        }
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    private static final void acceptStandard(Context<?> ctx, QOM.DocumentOrContent documentOrContent, Field<String> content) {
        ctx.visit(Names.N_XMLPARSE).sql('(').visit(documentOrContent == QOM.DocumentOrContent.DOCUMENT ? Keywords.K_DOCUMENT : Keywords.K_CONTENT).sql(' ').visit((Field<?>) content);
        ctx.sql(')');
    }

    @Override // org.jooq.impl.QOM.XMLParse
    public final Field<String> $content() {
        return this.content;
    }

    @Override // org.jooq.impl.QOM.XMLParse
    public final QOM.DocumentOrContent $documentOrContent() {
        return this.documentOrContent;
    }
}
