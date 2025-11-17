package org.h2.command.ddl;

import org.h2.api.ErrorCode;
import org.h2.engine.Database;
import org.h2.engine.Role;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ddl/DropRole.class */
public class DropRole extends DefineCommand {
    private String roleName;
    private boolean ifExists;

    public DropRole(SessionLocal sessionLocal) {
        super(sessionLocal);
    }

    public void setRoleName(String str) {
        this.roleName = str;
    }

    @Override // org.h2.command.Prepared
    public long update() {
        this.session.getUser().checkAdmin();
        Database database = getDatabase();
        Role findRole = database.findRole(this.roleName);
        if (findRole == null) {
            if (!this.ifExists) {
                throw DbException.get(ErrorCode.ROLE_NOT_FOUND_1, this.roleName);
            }
            return 0L;
        }
        if (findRole == database.getPublicRole()) {
            throw DbException.get(ErrorCode.ROLE_CAN_NOT_BE_DROPPED_1, this.roleName);
        }
        findRole.checkOwnsNoSchemas();
        database.removeDatabaseObject(this.session, findRole);
        return 0L;
    }

    public void setIfExists(boolean z) {
        this.ifExists = z;
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return 41;
    }
}
