package org.h2.command.ddl;

import org.h2.api.ErrorCode;
import org.h2.engine.Database;
import org.h2.engine.RightOwner;
import org.h2.engine.SessionLocal;
import org.h2.engine.User;
import org.h2.message.DbException;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ddl/DropUser.class */
public class DropUser extends DefineCommand {
    private boolean ifExists;
    private String userName;

    public DropUser(SessionLocal sessionLocal) {
        super(sessionLocal);
    }

    public void setIfExists(boolean z) {
        this.ifExists = z;
    }

    public void setUserName(String str) {
        this.userName = str;
    }

    @Override // org.h2.command.Prepared
    public long update() {
        this.session.getUser().checkAdmin();
        Database database = getDatabase();
        User findUser = database.findUser(this.userName);
        if (findUser == null) {
            if (!this.ifExists) {
                throw DbException.get(ErrorCode.USER_NOT_FOUND_1, this.userName);
            }
            return 0L;
        }
        if (findUser == this.session.getUser()) {
            int i = 0;
            for (RightOwner rightOwner : database.getAllUsersAndRoles()) {
                if ((rightOwner instanceof User) && ((User) rightOwner).isAdmin()) {
                    i++;
                }
            }
            if (i == 1) {
                throw DbException.get(ErrorCode.CANNOT_DROP_CURRENT_USER);
            }
        }
        findUser.checkOwnsNoSchemas();
        database.removeDatabaseObject(this.session, findUser);
        return 0L;
    }

    @Override // org.h2.command.ddl.DefineCommand, org.h2.command.Prepared
    public boolean isTransactional() {
        return false;
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return 46;
    }
}
