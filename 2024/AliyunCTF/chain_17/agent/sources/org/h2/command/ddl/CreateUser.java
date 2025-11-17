package org.h2.command.ddl;

import org.h2.api.ErrorCode;
import org.h2.engine.Database;
import org.h2.engine.RightOwner;
import org.h2.engine.SessionLocal;
import org.h2.engine.User;
import org.h2.expression.Expression;
import org.h2.message.DbException;
import org.h2.security.SHA256;
import org.h2.util.StringUtils;
import org.h2.value.DataType;
import org.h2.value.Value;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ddl/CreateUser.class */
public class CreateUser extends DefineCommand {
    private String userName;
    private boolean admin;
    private Expression password;
    private Expression salt;
    private Expression hash;
    private boolean ifNotExists;
    private String comment;

    public CreateUser(SessionLocal sessionLocal) {
        super(sessionLocal);
    }

    public void setIfNotExists(boolean z) {
        this.ifNotExists = z;
    }

    public void setUserName(String str) {
        this.userName = str;
    }

    public void setPassword(Expression expression) {
        this.password = expression;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void setSaltAndHash(User user, SessionLocal sessionLocal, Expression expression, Expression expression2) {
        user.setSaltAndHash(getByteArray(sessionLocal, expression), getByteArray(sessionLocal, expression2));
    }

    private static byte[] getByteArray(SessionLocal sessionLocal, Expression expression) {
        Value value = expression.optimize(sessionLocal).getValue(sessionLocal);
        if (DataType.isBinaryStringType(value.getValueType())) {
            byte[] bytes = value.getBytes();
            return bytes == null ? new byte[0] : bytes;
        }
        String string = value.getString();
        return string == null ? new byte[0] : StringUtils.convertHexToBytes(string);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void setPassword(User user, SessionLocal sessionLocal, Expression expression) {
        byte[] keyPasswordHash;
        String string = expression.optimize(sessionLocal).getValue(sessionLocal).getString();
        char[] charArray = string == null ? new char[0] : string.toCharArray();
        String name = user.getName();
        if (name.isEmpty() && charArray.length == 0) {
            keyPasswordHash = new byte[0];
        } else {
            keyPasswordHash = SHA256.getKeyPasswordHash(name, charArray);
        }
        user.setUserPasswordHash(keyPasswordHash);
    }

    @Override // org.h2.command.Prepared
    public long update() {
        this.session.getUser().checkAdmin();
        Database database = getDatabase();
        RightOwner findUserOrRole = database.findUserOrRole(this.userName);
        if (findUserOrRole != null) {
            if (findUserOrRole instanceof User) {
                if (this.ifNotExists) {
                    return 0L;
                }
                throw DbException.get(ErrorCode.USER_ALREADY_EXISTS_1, this.userName);
            }
            throw DbException.get(ErrorCode.ROLE_ALREADY_EXISTS_1, this.userName);
        }
        User user = new User(database, getObjectId(), this.userName, false);
        user.setAdmin(this.admin);
        user.setComment(this.comment);
        if (this.hash != null && this.salt != null) {
            setSaltAndHash(user, this.session, this.salt, this.hash);
        } else if (this.password != null) {
            setPassword(user, this.session, this.password);
        } else {
            throw DbException.getInternalError();
        }
        database.addDatabaseObject(this.session, user);
        return 0L;
    }

    public void setSalt(Expression expression) {
        this.salt = expression;
    }

    public void setHash(Expression expression) {
        this.hash = expression;
    }

    public void setAdmin(boolean z) {
        this.admin = z;
    }

    public void setComment(String str) {
        this.comment = str;
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return 32;
    }
}
