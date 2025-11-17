package org.h2.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import org.h2.api.ErrorCode;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.security.SHA256;
import org.h2.table.DualTable;
import org.h2.table.MetaTable;
import org.h2.table.RangeTable;
import org.h2.table.Table;
import org.h2.util.MathUtils;
import org.h2.util.StringUtils;
import org.h2.util.Utils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/engine/User.class */
public final class User extends RightOwner {
    private final boolean systemUser;
    private byte[] salt;
    private byte[] passwordHash;
    private boolean admin;

    public User(Database database, int i, String str, boolean z) {
        super(database, i, str, 13);
        this.systemUser = z;
    }

    public void setAdmin(boolean z) {
        this.admin = z;
    }

    public boolean isAdmin() {
        return this.admin;
    }

    public void setSaltAndHash(byte[] bArr, byte[] bArr2) {
        this.salt = bArr;
        this.passwordHash = bArr2;
    }

    public void setUserPasswordHash(byte[] bArr) {
        if (bArr != null) {
            if (bArr.length == 0) {
                this.passwordHash = bArr;
                this.salt = bArr;
            } else {
                this.salt = new byte[8];
                MathUtils.randomBytes(this.salt);
                this.passwordHash = SHA256.getHashWithSalt(bArr, this.salt);
            }
        }
    }

    @Override // org.h2.engine.DbObject
    public String getCreateSQL() {
        return getCreateSQL(true);
    }

    public String getCreateSQL(boolean z) {
        StringBuilder sb = new StringBuilder("CREATE USER IF NOT EXISTS ");
        getSQL(sb, 0);
        if (this.comment != null) {
            sb.append(" COMMENT ");
            StringUtils.quoteStringSQL(sb, this.comment);
        }
        if (z) {
            sb.append(" SALT '");
            StringUtils.convertBytesToHex(sb, this.salt).append("' HASH '");
            StringUtils.convertBytesToHex(sb, this.passwordHash).append('\'');
        } else {
            sb.append(" PASSWORD ''");
        }
        if (this.admin) {
            sb.append(" ADMIN");
        }
        return sb.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean validateUserPasswordHash(byte[] bArr) {
        if (bArr.length == 0 && this.passwordHash.length == 0) {
            return true;
        }
        if (bArr.length == 0) {
            bArr = SHA256.getKeyPasswordHash(getName(), new char[0]);
        }
        return Utils.compareSecure(SHA256.getHashWithSalt(bArr, this.salt), this.passwordHash);
    }

    public void checkAdmin() {
        if (!this.admin) {
            throw DbException.get(ErrorCode.ADMIN_RIGHTS_REQUIRED);
        }
    }

    public void checkSchemaAdmin() {
        if (!hasSchemaRight(null)) {
            throw DbException.get(ErrorCode.ADMIN_RIGHTS_REQUIRED);
        }
    }

    public void checkSchemaOwner(Schema schema) {
        if (!hasSchemaRight(schema)) {
            throw DbException.get(ErrorCode.NOT_ENOUGH_RIGHTS_FOR_1, schema.getTraceSQL());
        }
    }

    private boolean hasSchemaRight(Schema schema) {
        if (this.admin || this.database.getPublicRole().isSchemaRightGrantedRecursive(schema)) {
            return true;
        }
        return isSchemaRightGrantedRecursive(schema);
    }

    public void checkTableRight(Table table, int i) {
        if (!hasTableRight(table, i)) {
            throw DbException.get(ErrorCode.NOT_ENOUGH_RIGHTS_FOR_1, table.getTraceSQL());
        }
    }

    public boolean hasTableRight(Table table, int i) {
        if (i != 1 && !this.systemUser) {
            table.checkWritingAllowed();
        }
        if (this.admin || this.database.getPublicRole().isTableRightGrantedRecursive(table, i) || (table instanceof MetaTable) || (table instanceof DualTable) || (table instanceof RangeTable) || table.getTableType() == null) {
            return true;
        }
        if (table.isTemporary() && !table.isGlobalTemporary()) {
            return true;
        }
        return isTableRightGrantedRecursive(table, i);
    }

    @Override // org.h2.engine.DbObject
    public int getType() {
        return 2;
    }

    @Override // org.h2.engine.DbObject
    public ArrayList<DbObject> getChildren() {
        ArrayList<DbObject> arrayList = new ArrayList<>();
        Iterator<Right> it = this.database.getAllRights().iterator();
        while (it.hasNext()) {
            Right next = it.next();
            if (next.getGrantee() == this) {
                arrayList.add(next);
            }
        }
        for (Schema schema : this.database.getAllSchemas()) {
            if (schema.getOwner() == this) {
                arrayList.add(schema);
            }
        }
        return arrayList;
    }

    @Override // org.h2.engine.DbObject
    public void removeChildrenAndResources(SessionLocal sessionLocal) {
        Iterator<Right> it = this.database.getAllRights().iterator();
        while (it.hasNext()) {
            Right next = it.next();
            if (next.getGrantee() == this) {
                this.database.removeDatabaseObject(sessionLocal, next);
            }
        }
        this.database.removeMeta(sessionLocal, getId());
        this.salt = null;
        Arrays.fill(this.passwordHash, (byte) 0);
        this.passwordHash = null;
        invalidate();
    }
}
