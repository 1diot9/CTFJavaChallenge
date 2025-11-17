package org.h2.schema;

import org.h2.engine.DbObject;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/schema/SchemaObject.class */
public abstract class SchemaObject extends DbObject {
    private final Schema schema;

    /* JADX INFO: Access modifiers changed from: protected */
    public SchemaObject(Schema schema, int i, String str, int i2) {
        super(schema.getDatabase(), i, str, i2);
        this.schema = schema;
    }

    public final Schema getSchema() {
        return this.schema;
    }

    @Override // org.h2.engine.DbObject, org.h2.util.HasSQL
    public String getSQL(int i) {
        return getSQL(new StringBuilder(), i).toString();
    }

    @Override // org.h2.engine.DbObject, org.h2.util.HasSQL
    public StringBuilder getSQL(StringBuilder sb, int i) {
        this.schema.getSQL(sb, i).append('.');
        return super.getSQL(sb, i);
    }

    public boolean isHidden() {
        return false;
    }
}
