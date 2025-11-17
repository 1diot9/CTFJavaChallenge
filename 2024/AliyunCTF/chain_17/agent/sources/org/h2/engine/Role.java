package org.h2.engine;

import java.util.ArrayList;
import java.util.Iterator;
import org.h2.schema.Schema;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/engine/Role.class */
public final class Role extends RightOwner {
    private final boolean system;

    public Role(Database database, int i, String str, boolean z) {
        super(database, i, str, 13);
        this.system = z;
    }

    public String getCreateSQL(boolean z) {
        if (this.system) {
            return null;
        }
        StringBuilder sb = new StringBuilder("CREATE ROLE ");
        if (z) {
            sb.append("IF NOT EXISTS ");
        }
        return getSQL(sb, 0).toString();
    }

    @Override // org.h2.engine.DbObject
    public String getCreateSQL() {
        return getCreateSQL(false);
    }

    @Override // org.h2.engine.DbObject
    public int getType() {
        return 7;
    }

    @Override // org.h2.engine.DbObject
    public ArrayList<DbObject> getChildren() {
        ArrayList<DbObject> arrayList = new ArrayList<>();
        for (Schema schema : this.database.getAllSchemas()) {
            if (schema.getOwner() == this) {
                arrayList.add(schema);
            }
        }
        return arrayList;
    }

    @Override // org.h2.engine.DbObject
    public void removeChildrenAndResources(SessionLocal sessionLocal) {
        Iterator<RightOwner> it = this.database.getAllUsersAndRoles().iterator();
        while (it.hasNext()) {
            Right rightForRole = it.next().getRightForRole(this);
            if (rightForRole != null) {
                this.database.removeDatabaseObject(sessionLocal, rightForRole);
            }
        }
        Iterator<Right> it2 = this.database.getAllRights().iterator();
        while (it2.hasNext()) {
            Right next = it2.next();
            if (next.getGrantee() == this) {
                this.database.removeDatabaseObject(sessionLocal, next);
            }
        }
        this.database.removeMeta(sessionLocal, getId());
        invalidate();
    }
}
