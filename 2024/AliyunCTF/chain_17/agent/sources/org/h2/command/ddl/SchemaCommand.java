package org.h2.command.ddl;

import org.h2.engine.SessionLocal;
import org.h2.schema.Schema;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ddl/SchemaCommand.class */
public abstract class SchemaCommand extends DefineCommand {
    private final Schema schema;

    public SchemaCommand(SessionLocal sessionLocal, Schema schema) {
        super(sessionLocal);
        this.schema = schema;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final Schema getSchema() {
        return this.schema;
    }
}
