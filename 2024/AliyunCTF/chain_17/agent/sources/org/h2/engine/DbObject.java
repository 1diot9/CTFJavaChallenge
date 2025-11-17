package org.h2.engine;

import java.util.ArrayList;
import org.h2.command.ParserBase;
import org.h2.message.DbException;
import org.h2.message.Trace;
import org.h2.table.Table;
import org.h2.util.HasSQL;
import org.h2.util.ParserUtil;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/engine/DbObject.class */
public abstract class DbObject implements HasSQL {
    public static final int TABLE_OR_VIEW = 0;
    public static final int INDEX = 1;
    public static final int USER = 2;
    public static final int SEQUENCE = 3;
    public static final int TRIGGER = 4;
    public static final int CONSTRAINT = 5;
    public static final int SETTING = 6;
    public static final int ROLE = 7;
    public static final int RIGHT = 8;
    public static final int FUNCTION_ALIAS = 9;
    public static final int SCHEMA = 10;
    public static final int CONSTANT = 11;
    public static final int DOMAIN = 12;
    public static final int COMMENT = 13;
    public static final int AGGREGATE = 14;
    public static final int SYNONYM = 15;
    protected Database database;
    protected Trace trace;
    protected String comment;
    private int id;
    private String objectName;
    private long modificationId;
    private boolean temporary;

    public abstract String getCreateSQL();

    public abstract int getType();

    public abstract void removeChildrenAndResources(SessionLocal sessionLocal);

    /* JADX INFO: Access modifiers changed from: protected */
    public DbObject(Database database, int i, String str, int i2) {
        this.database = database;
        this.trace = database.getTrace(i2);
        this.id = i;
        this.objectName = str;
        this.modificationId = database.getModificationMetaId();
    }

    public final void setModified() {
        this.modificationId = this.database == null ? -1L : this.database.getNextModificationMetaId();
    }

    public final long getModificationId() {
        return this.modificationId;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void setObjectName(String str) {
        this.objectName = str;
    }

    @Override // org.h2.util.HasSQL
    public String getSQL(int i) {
        return ParserBase.quoteIdentifier(this.objectName, i);
    }

    @Override // org.h2.util.HasSQL
    public StringBuilder getSQL(StringBuilder sb, int i) {
        return ParserUtil.quoteIdentifier(sb, this.objectName, i);
    }

    public ArrayList<DbObject> getChildren() {
        return null;
    }

    public final Database getDatabase() {
        return this.database;
    }

    public final int getId() {
        return this.id;
    }

    public final String getName() {
        return this.objectName;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void invalidate() {
        if (this.id == -1) {
            throw DbException.getInternalError();
        }
        setModified();
        this.id = -1;
        this.database = null;
        this.trace = null;
        this.objectName = null;
    }

    public final boolean isValid() {
        return this.id != -1;
    }

    public String getCreateSQLForCopy(Table table, String str) {
        throw DbException.getInternalError(toString());
    }

    public String getCreateSQLForMeta() {
        return getCreateSQL();
    }

    public String getDropSQL() {
        return null;
    }

    public void checkRename() {
    }

    public void rename(String str) {
        checkRename();
        this.objectName = str;
        setModified();
    }

    public boolean isTemporary() {
        return this.temporary;
    }

    public void setTemporary(boolean z) {
        this.temporary = z;
    }

    public void setComment(String str) {
        this.comment = (str == null || str.isEmpty()) ? null : str;
    }

    public String getComment() {
        return this.comment;
    }

    public String toString() {
        return this.objectName + ":" + this.id + ":" + super.toString();
    }
}
