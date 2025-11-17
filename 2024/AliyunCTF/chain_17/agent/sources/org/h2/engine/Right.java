package org.h2.engine;

import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.table.Table;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/engine/Right.class */
public final class Right extends DbObject {
    public static final int SELECT = 1;
    public static final int DELETE = 2;
    public static final int INSERT = 4;
    public static final int UPDATE = 8;
    public static final int ALTER_ANY_SCHEMA = 16;
    public static final int SCHEMA_OWNER = 32;
    public static final int ALL = 15;
    private RightOwner grantee;
    private Role grantedRole;
    private int grantedRight;
    private DbObject grantedObject;

    public Right(Database database, int i, RightOwner rightOwner, Role role) {
        super(database, i, "RIGHT_" + i, 13);
        this.grantee = rightOwner;
        this.grantedRole = role;
    }

    public Right(Database database, int i, RightOwner rightOwner, int i2, DbObject dbObject) {
        super(database, i, Integer.toString(i), 13);
        this.grantee = rightOwner;
        this.grantedRight = i2;
        this.grantedObject = dbObject;
    }

    private static boolean appendRight(StringBuilder sb, int i, int i2, String str, boolean z) {
        if ((i & i2) != 0) {
            if (z) {
                sb.append(", ");
            }
            sb.append(str);
            return true;
        }
        return z;
    }

    public String getRights() {
        StringBuilder sb = new StringBuilder();
        if (this.grantedRight == 15) {
            sb.append("ALL");
        } else {
            appendRight(sb, this.grantedRight, 16, "ALTER ANY SCHEMA", appendRight(sb, this.grantedRight, 8, "UPDATE", appendRight(sb, this.grantedRight, 4, "INSERT", appendRight(sb, this.grantedRight, 2, "DELETE", appendRight(sb, this.grantedRight, 1, "SELECT", false)))));
        }
        return sb.toString();
    }

    public Role getGrantedRole() {
        return this.grantedRole;
    }

    public DbObject getGrantedObject() {
        return this.grantedObject;
    }

    public DbObject getGrantee() {
        return this.grantee;
    }

    @Override // org.h2.engine.DbObject
    public String getCreateSQLForCopy(Table table, String str) {
        return getCreateSQLForCopy(table);
    }

    private String getCreateSQLForCopy(DbObject dbObject) {
        StringBuilder sb = new StringBuilder();
        sb.append("GRANT ");
        if (this.grantedRole != null) {
            this.grantedRole.getSQL(sb, 0);
        } else {
            sb.append(getRights());
            if (dbObject != null) {
                if (dbObject instanceof Schema) {
                    sb.append(" ON SCHEMA ");
                    dbObject.getSQL(sb, 0);
                } else if (dbObject instanceof Table) {
                    sb.append(" ON ");
                    dbObject.getSQL(sb, 0);
                }
            }
        }
        sb.append(" TO ");
        this.grantee.getSQL(sb, 0);
        return sb.toString();
    }

    @Override // org.h2.engine.DbObject
    public String getCreateSQL() {
        return getCreateSQLForCopy(this.grantedObject);
    }

    @Override // org.h2.engine.DbObject
    public int getType() {
        return 8;
    }

    @Override // org.h2.engine.DbObject
    public void removeChildrenAndResources(SessionLocal sessionLocal) {
        if (this.grantedRole != null) {
            this.grantee.revokeRole(this.grantedRole);
        } else {
            this.grantee.revokeRight(this.grantedObject);
        }
        this.database.removeMeta(sessionLocal, getId());
        this.grantedRole = null;
        this.grantedObject = null;
        this.grantee = null;
        invalidate();
    }

    @Override // org.h2.engine.DbObject
    public void checkRename() {
        throw DbException.getInternalError();
    }

    public void setRightMask(int i) {
        this.grantedRight = i;
    }

    public int getRightMask() {
        return this.grantedRight;
    }
}
