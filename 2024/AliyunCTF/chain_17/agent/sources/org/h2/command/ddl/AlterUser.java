package org.h2.command.ddl;

import org.h2.api.ErrorCode;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.engine.User;
import org.h2.expression.Expression;
import org.h2.message.DbException;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ddl/AlterUser.class */
public class AlterUser extends DefineCommand {
    private int type;
    private User user;
    private String newName;
    private Expression password;
    private Expression salt;
    private Expression hash;
    private boolean admin;

    public AlterUser(SessionLocal sessionLocal) {
        super(sessionLocal);
    }

    public void setType(int i) {
        this.type = i;
    }

    public void setNewName(String str) {
        this.newName = str;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setAdmin(boolean z) {
        this.admin = z;
    }

    public void setSalt(Expression expression) {
        this.salt = expression;
    }

    public void setHash(Expression expression) {
        this.hash = expression;
    }

    public void setPassword(Expression expression) {
        this.password = expression;
    }

    @Override // org.h2.command.Prepared
    public long update() {
        Database database = getDatabase();
        switch (this.type) {
            case 17:
                this.session.getUser().checkAdmin();
                this.user.setAdmin(this.admin);
                break;
            case 18:
                this.session.getUser().checkAdmin();
                if (database.findUser(this.newName) != null || this.newName.equals(this.user.getName())) {
                    throw DbException.get(ErrorCode.USER_ALREADY_EXISTS_1, this.newName);
                }
                database.renameDatabaseObject(this.session, this.user, this.newName);
                break;
            case 19:
                if (this.user != this.session.getUser()) {
                    this.session.getUser().checkAdmin();
                }
                if (this.hash != null && this.salt != null) {
                    CreateUser.setSaltAndHash(this.user, this.session, this.salt, this.hash);
                    break;
                } else {
                    CreateUser.setPassword(this.user, this.session, this.password);
                    break;
                }
                break;
            default:
                throw DbException.getInternalError("type=" + this.type);
        }
        database.updateMeta(this.session, this.user);
        return 0L;
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return this.type;
    }
}
