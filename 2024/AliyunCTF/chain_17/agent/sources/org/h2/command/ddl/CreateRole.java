package org.h2.command.ddl;

import org.h2.api.ErrorCode;
import org.h2.engine.Database;
import org.h2.engine.RightOwner;
import org.h2.engine.Role;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ddl/CreateRole.class */
public class CreateRole extends DefineCommand {
    private String roleName;
    private boolean ifNotExists;

    public CreateRole(SessionLocal sessionLocal) {
        super(sessionLocal);
    }

    public void setIfNotExists(boolean z) {
        this.ifNotExists = z;
    }

    public void setRoleName(String str) {
        this.roleName = str;
    }

    @Override // org.h2.command.Prepared
    public long update() {
        this.session.getUser().checkAdmin();
        Database database = getDatabase();
        RightOwner findUserOrRole = database.findUserOrRole(this.roleName);
        if (findUserOrRole != null) {
            if (findUserOrRole instanceof Role) {
                if (this.ifNotExists) {
                    return 0L;
                }
                throw DbException.get(ErrorCode.ROLE_ALREADY_EXISTS_1, this.roleName);
            }
            throw DbException.get(ErrorCode.USER_ALREADY_EXISTS_1, this.roleName);
        }
        database.addDatabaseObject(this.session, new Role(database, getObjectId(), this.roleName, false));
        return 0L;
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return 27;
    }
}
