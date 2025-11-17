package org.h2.engine;

import org.h2.message.DbException;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/engine/Setting.class */
public final class Setting extends DbObject {
    private int intValue;
    private String stringValue;

    public Setting(Database database, int i, String str) {
        super(database, i, str, 10);
    }

    @Override // org.h2.engine.DbObject, org.h2.util.HasSQL
    public String getSQL(int i) {
        return getName();
    }

    @Override // org.h2.engine.DbObject, org.h2.util.HasSQL
    public StringBuilder getSQL(StringBuilder sb, int i) {
        return sb.append(getName());
    }

    public void setIntValue(int i) {
        this.intValue = i;
    }

    public int getIntValue() {
        return this.intValue;
    }

    public void setStringValue(String str) {
        this.stringValue = str;
    }

    public String getStringValue() {
        return this.stringValue;
    }

    @Override // org.h2.engine.DbObject
    public String getCreateSQL() {
        StringBuilder sb = new StringBuilder("SET ");
        getSQL(sb, 0).append(' ');
        if (this.stringValue != null) {
            sb.append(this.stringValue);
        } else {
            sb.append(this.intValue);
        }
        return sb.toString();
    }

    @Override // org.h2.engine.DbObject
    public int getType() {
        return 6;
    }

    @Override // org.h2.engine.DbObject
    public void removeChildrenAndResources(SessionLocal sessionLocal) {
        this.database.removeMeta(sessionLocal, getId());
        invalidate();
    }

    @Override // org.h2.engine.DbObject
    public void checkRename() {
        throw DbException.getUnsupportedException("RENAME");
    }
}
