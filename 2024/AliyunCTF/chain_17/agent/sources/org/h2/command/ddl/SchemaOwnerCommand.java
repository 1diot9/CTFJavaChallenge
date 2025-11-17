package org.h2.command.ddl;

import org.h2.engine.SessionLocal;
import org.h2.schema.Schema;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ddl/SchemaOwnerCommand.class */
abstract class SchemaOwnerCommand extends SchemaCommand {
    abstract long update(Schema schema);

    /* JADX INFO: Access modifiers changed from: package-private */
    public SchemaOwnerCommand(SessionLocal sessionLocal, Schema schema) {
        super(sessionLocal, schema);
    }

    @Override // org.h2.command.Prepared
    public final long update() {
        Schema schema = getSchema();
        this.session.getUser().checkSchemaOwner(schema);
        return update(schema);
    }
}
