package org.h2.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.h2.api.ErrorCode;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.table.Table;
import org.h2.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/engine/RightOwner.class */
public abstract class RightOwner extends DbObject {
    private HashMap<Role, Right> grantedRoles;
    private HashMap<DbObject, Right> grantedRights;

    /* JADX INFO: Access modifiers changed from: protected */
    public RightOwner(Database database, int i, String str, int i2) {
        super(database, i, StringUtils.toUpperEnglish(str), i2);
    }

    @Override // org.h2.engine.DbObject
    public void rename(String str) {
        super.rename(StringUtils.toUpperEnglish(str));
    }

    public boolean isRoleGranted(Role role) {
        if (role == this) {
            return true;
        }
        if (this.grantedRoles != null) {
            for (Role role2 : this.grantedRoles.keySet()) {
                if (role2 == role || role2.isRoleGranted(role)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean isTableRightGrantedRecursive(Table table, int i) {
        Schema schema = table.getSchema();
        if (schema.getOwner() == this) {
            return true;
        }
        if (this.grantedRights != null) {
            Right right = this.grantedRights.get(null);
            if (right != null && (right.getRightMask() & 16) == 16) {
                return true;
            }
            Right right2 = this.grantedRights.get(schema);
            if (right2 != null && (right2.getRightMask() & i) == i) {
                return true;
            }
            Right right3 = this.grantedRights.get(table);
            if (right3 != null && (right3.getRightMask() & i) == i) {
                return true;
            }
        }
        if (this.grantedRoles != null) {
            Iterator<Role> it = this.grantedRoles.keySet().iterator();
            while (it.hasNext()) {
                if (it.next().isTableRightGrantedRecursive(table, i)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean isSchemaRightGrantedRecursive(Schema schema) {
        Right right;
        if (schema != null && schema.getOwner() == this) {
            return true;
        }
        if (this.grantedRights != null && (right = this.grantedRights.get(null)) != null && (right.getRightMask() & 16) == 16) {
            return true;
        }
        if (this.grantedRoles != null) {
            Iterator<Role> it = this.grantedRoles.keySet().iterator();
            while (it.hasNext()) {
                if (it.next().isSchemaRightGrantedRecursive(schema)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public void grantRight(DbObject dbObject, Right right) {
        if (this.grantedRights == null) {
            this.grantedRights = new HashMap<>();
        }
        this.grantedRights.put(dbObject, right);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void revokeRight(DbObject dbObject) {
        if (this.grantedRights == null) {
            return;
        }
        this.grantedRights.remove(dbObject);
        if (this.grantedRights.size() == 0) {
            this.grantedRights = null;
        }
    }

    public void grantRole(Role role, Right right) {
        if (this.grantedRoles == null) {
            this.grantedRoles = new HashMap<>();
        }
        this.grantedRoles.put(role, right);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void revokeRole(Role role) {
        if (this.grantedRoles == null || this.grantedRoles.get(role) == null) {
            return;
        }
        this.grantedRoles.remove(role);
        if (this.grantedRoles.size() == 0) {
            this.grantedRoles = null;
        }
    }

    public void revokeTemporaryRightsOnRoles() {
        if (this.grantedRoles == null) {
            return;
        }
        ArrayList arrayList = new ArrayList();
        for (Map.Entry<Role, Right> entry : this.grantedRoles.entrySet()) {
            if (entry.getValue().isTemporary() || !entry.getValue().isValid()) {
                arrayList.add(entry.getKey());
            }
        }
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            revokeRole((Role) it.next());
        }
    }

    public Right getRightForObject(DbObject dbObject) {
        if (this.grantedRights == null) {
            return null;
        }
        return this.grantedRights.get(dbObject);
    }

    public Right getRightForRole(Role role) {
        if (this.grantedRoles == null) {
            return null;
        }
        return this.grantedRoles.get(role);
    }

    public final void checkOwnsNoSchemas() {
        for (Schema schema : this.database.getAllSchemas()) {
            if (this == schema.getOwner()) {
                throw DbException.get(ErrorCode.CANNOT_DROP_2, getName(), schema.getName());
            }
        }
    }
}
