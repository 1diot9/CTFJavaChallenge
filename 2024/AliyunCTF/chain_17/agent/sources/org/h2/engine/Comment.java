package org.h2.engine;

import ch.qos.logback.core.joran.JoranConstants;
import org.h2.message.DbException;
import org.h2.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/engine/Comment.class */
public final class Comment extends DbObject {
    private final int objectType;
    private final String quotedObjectName;
    private String commentText;

    public Comment(Database database, int i, DbObject dbObject) {
        super(database, i, getKey(dbObject), 2);
        this.objectType = dbObject.getType();
        this.quotedObjectName = dbObject.getSQL(0);
    }

    private static String getTypeName(int i) {
        switch (i) {
            case 0:
                return "TABLE";
            case 1:
                return "INDEX";
            case 2:
                return "USER";
            case 3:
                return "SEQUENCE";
            case 4:
                return "TRIGGER";
            case 5:
                return "CONSTRAINT";
            case 6:
            case 8:
            default:
                return "type" + i;
            case 7:
                return "ROLE";
            case 9:
                return "ALIAS";
            case 10:
                return "SCHEMA";
            case 11:
                return "CONSTANT";
            case 12:
                return "DOMAIN";
        }
    }

    @Override // org.h2.engine.DbObject
    public String getCreateSQL() {
        StringBuilder sb = new StringBuilder("COMMENT ON ");
        sb.append(getTypeName(this.objectType)).append(' ').append(this.quotedObjectName).append(" IS ");
        if (this.commentText == null) {
            sb.append(JoranConstants.NULL);
        } else {
            StringUtils.quoteStringSQL(sb, this.commentText);
        }
        return sb.toString();
    }

    @Override // org.h2.engine.DbObject
    public int getType() {
        return 13;
    }

    @Override // org.h2.engine.DbObject
    public void removeChildrenAndResources(SessionLocal sessionLocal) {
        this.database.removeMeta(sessionLocal, getId());
    }

    @Override // org.h2.engine.DbObject
    public void checkRename() {
        throw DbException.getInternalError();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String getKey(DbObject dbObject) {
        StringBuilder append = new StringBuilder(getTypeName(dbObject.getType())).append(' ');
        dbObject.getSQL(append, 0);
        return append.toString();
    }

    public void setCommentText(String str) {
        this.commentText = str;
    }
}
