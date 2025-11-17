package org.h2.command.ddl;

import java.util.ArrayList;
import java.util.Iterator;
import org.h2.api.ErrorCode;
import org.h2.engine.Database;
import org.h2.engine.DbObject;
import org.h2.engine.Right;
import org.h2.engine.RightOwner;
import org.h2.engine.Role;
import org.h2.engine.SessionLocal;
import org.h2.engine.User;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.table.Table;
import org.h2.util.Utils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ddl/GrantRevoke.class */
public class GrantRevoke extends DefineCommand {
    private ArrayList<String> roleNames;
    private int operationType;
    private int rightMask;
    private final ArrayList<Table> tables;
    private Schema schema;
    private RightOwner grantee;

    public GrantRevoke(SessionLocal sessionLocal) {
        super(sessionLocal);
        this.tables = Utils.newSmallArrayList();
    }

    public void setOperationType(int i) {
        this.operationType = i;
    }

    public void addRight(int i) {
        this.rightMask |= i;
    }

    public void addRoleName(String str) {
        if (this.roleNames == null) {
            this.roleNames = Utils.newSmallArrayList();
        }
        this.roleNames.add(str);
    }

    public void setGranteeName(String str) {
        this.grantee = getDatabase().findUserOrRole(str);
        if (this.grantee == null) {
            throw DbException.get(ErrorCode.USER_OR_ROLE_NOT_FOUND_1, str);
        }
    }

    @Override // org.h2.command.Prepared
    public long update() {
        Database database = getDatabase();
        User user = this.session.getUser();
        if (this.roleNames != null) {
            user.checkAdmin();
            Iterator<String> it = this.roleNames.iterator();
            while (it.hasNext()) {
                String next = it.next();
                Role findRole = database.findRole(next);
                if (findRole == null) {
                    throw DbException.get(ErrorCode.ROLE_NOT_FOUND_1, next);
                }
                if (this.operationType == 49) {
                    grantRole(findRole);
                } else if (this.operationType == 50) {
                    revokeRole(findRole);
                } else {
                    throw DbException.getInternalError("type=" + this.operationType);
                }
            }
            return 0L;
        }
        if ((this.rightMask & 16) != 0) {
            user.checkAdmin();
        } else {
            if (this.schema != null) {
                user.checkSchemaOwner(this.schema);
            }
            Iterator<Table> it2 = this.tables.iterator();
            while (it2.hasNext()) {
                user.checkSchemaOwner(it2.next().getSchema());
            }
        }
        if (this.operationType == 49) {
            grantRight();
            return 0L;
        }
        if (this.operationType == 50) {
            revokeRight();
            return 0L;
        }
        throw DbException.getInternalError("type=" + this.operationType);
    }

    private void grantRight() {
        if (this.schema != null) {
            grantRight(this.schema);
        }
        Iterator<Table> it = this.tables.iterator();
        while (it.hasNext()) {
            grantRight(it.next());
        }
    }

    private void grantRight(DbObject dbObject) {
        Database database = getDatabase();
        Right rightForObject = this.grantee.getRightForObject(dbObject);
        if (rightForObject == null) {
            int persistedObjectId = getPersistedObjectId();
            if (persistedObjectId == 0) {
                persistedObjectId = getDatabase().allocateObjectId();
            }
            Right right = new Right(database, persistedObjectId, this.grantee, this.rightMask, dbObject);
            this.grantee.grantRight(dbObject, right);
            database.addDatabaseObject(this.session, right);
            return;
        }
        rightForObject.setRightMask(rightForObject.getRightMask() | this.rightMask);
        database.updateMeta(this.session, rightForObject);
    }

    private void grantRole(Role role) {
        if (role != this.grantee && this.grantee.isRoleGranted(role)) {
            return;
        }
        if ((this.grantee instanceof Role) && role.isRoleGranted((Role) this.grantee)) {
            throw DbException.get(ErrorCode.ROLE_ALREADY_GRANTED_1, role.getTraceSQL());
        }
        Database database = getDatabase();
        Right right = new Right(database, getObjectId(), this.grantee, role);
        database.addDatabaseObject(this.session, right);
        this.grantee.grantRole(role, right);
    }

    private void revokeRight() {
        if (this.schema != null) {
            revokeRight(this.schema);
        }
        Iterator<Table> it = this.tables.iterator();
        while (it.hasNext()) {
            revokeRight(it.next());
        }
    }

    private void revokeRight(DbObject dbObject) {
        Right rightForObject = this.grantee.getRightForObject(dbObject);
        if (rightForObject == null) {
            return;
        }
        int rightMask = rightForObject.getRightMask() & (this.rightMask ^ (-1));
        Database database = getDatabase();
        if (rightMask == 0) {
            database.removeDatabaseObject(this.session, rightForObject);
        } else {
            rightForObject.setRightMask(rightMask);
            database.updateMeta(this.session, rightForObject);
        }
    }

    private void revokeRole(Role role) {
        Right rightForRole = this.grantee.getRightForRole(role);
        if (rightForRole == null) {
            return;
        }
        getDatabase().removeDatabaseObject(this.session, rightForRole);
    }

    @Override // org.h2.command.ddl.DefineCommand, org.h2.command.Prepared
    public boolean isTransactional() {
        return false;
    }

    public void addTable(Table table) {
        this.tables.add(table);
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return this.operationType;
    }
}
