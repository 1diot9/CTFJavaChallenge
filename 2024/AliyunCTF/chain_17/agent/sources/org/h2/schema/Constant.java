package org.h2.schema;

import org.h2.engine.SessionLocal;
import org.h2.expression.ValueExpression;
import org.h2.value.Value;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/schema/Constant.class */
public final class Constant extends SchemaObject {
    private Value value;
    private ValueExpression expression;

    public Constant(Schema schema, int i, String str) {
        super(schema, i, str, 8);
    }

    @Override // org.h2.engine.DbObject
    public String getCreateSQL() {
        StringBuilder sb = new StringBuilder("CREATE CONSTANT ");
        getSQL(sb, 0).append(" VALUE ");
        return this.value.getSQL(sb, 0).toString();
    }

    @Override // org.h2.engine.DbObject
    public int getType() {
        return 11;
    }

    @Override // org.h2.engine.DbObject
    public void removeChildrenAndResources(SessionLocal sessionLocal) {
        this.database.removeMeta(sessionLocal, getId());
        invalidate();
    }

    public void setValue(Value value) {
        this.value = value;
        this.expression = ValueExpression.get(value);
    }

    public ValueExpression getValue() {
        return this.expression;
    }
}
