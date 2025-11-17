package org.jooq.impl;

import org.jooq.Catalog;
import org.jooq.Context;
import org.jooq.Name;
import org.jooq.Qualified;
import org.jooq.Schema;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/QualifiedImpl.class */
public final class QualifiedImpl extends AbstractNamed implements Qualified, QOM.UTransient {
    /* JADX INFO: Access modifiers changed from: package-private */
    public QualifiedImpl(Name name) {
        super(name, CommentImpl.NO_COMMENT);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Type inference failed for: r0v6, types: [org.jooq.Context] */
    public static final void acceptMappedSchemaPrefix(Context<?> ctx, Schema schema) {
        Schema mappedSchema = Tools.getMappedSchema(ctx, schema);
        if (mappedSchema != null && !"".equals(mappedSchema.getName())) {
            ctx.visit(mappedSchema).sql('.');
        }
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        acceptMappedSchemaPrefix(ctx, getSchema());
        ctx.visit(getUnqualifiedName());
    }

    @Override // org.jooq.Qualified
    public final Catalog getCatalog() {
        Schema schema = getSchema();
        if (schema != null) {
            return schema.getCatalog();
        }
        return null;
    }

    @Override // org.jooq.Qualified
    public final Schema getSchema() {
        if (getQualifiedName().qualified()) {
            return new SchemaImpl(getQualifiedName().qualifier());
        }
        return null;
    }

    @Override // org.jooq.Qualified
    public final Schema $schema() {
        return getSchema();
    }
}
