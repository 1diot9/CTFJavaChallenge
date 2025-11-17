package org.h2.command.ddl;

import org.h2.engine.Comment;
import org.h2.engine.Database;
import org.h2.engine.DbObject;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.table.Table;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ddl/SetComment.class */
public class SetComment extends DefineCommand {
    private String schemaName;
    private String objectName;
    private boolean column;
    private String columnName;
    private int objectType;
    private Expression expr;

    public SetComment(SessionLocal sessionLocal) {
        super(sessionLocal);
    }

    @Override // org.h2.command.Prepared
    public long update() {
        Database database = getDatabase();
        DbObject dbObject = null;
        int i = 50000;
        if (this.schemaName == null) {
            this.schemaName = this.session.getCurrentSchemaName();
        }
        switch (this.objectType) {
            case 0:
                Schema schema = database.getSchema(this.schemaName);
                this.session.getUser().checkSchemaOwner(schema);
                dbObject = schema.getTableOrView(this.session, this.objectName);
                break;
            case 1:
                Schema schema2 = database.getSchema(this.schemaName);
                this.session.getUser().checkSchemaOwner(schema2);
                dbObject = schema2.getIndex(this.objectName);
                break;
            case 2:
                this.session.getUser().checkAdmin();
                this.schemaName = null;
                dbObject = database.getUser(this.objectName);
                break;
            case 3:
                Schema schema3 = database.getSchema(this.schemaName);
                this.session.getUser().checkSchemaOwner(schema3);
                dbObject = schema3.getSequence(this.objectName);
                break;
            case 4:
                Schema schema4 = database.getSchema(this.schemaName);
                this.session.getUser().checkSchemaOwner(schema4);
                dbObject = schema4.findTrigger(this.objectName);
                i = 90042;
                break;
            case 5:
                Schema schema5 = database.getSchema(this.schemaName);
                this.session.getUser().checkSchemaOwner(schema5);
                dbObject = schema5.getConstraint(this.objectName);
                break;
            case 7:
                this.session.getUser().checkAdmin();
                this.schemaName = null;
                dbObject = database.findRole(this.objectName);
                i = 90070;
                break;
            case 9:
                Schema schema6 = database.getSchema(this.schemaName);
                this.session.getUser().checkSchemaOwner(schema6);
                dbObject = schema6.findFunction(this.objectName);
                i = 90077;
                break;
            case 10:
                this.schemaName = null;
                Schema schema7 = database.getSchema(this.objectName);
                this.session.getUser().checkSchemaOwner(schema7);
                dbObject = schema7;
                break;
            case 11:
                Schema schema8 = database.getSchema(this.schemaName);
                this.session.getUser().checkSchemaOwner(schema8);
                dbObject = schema8.getConstant(this.objectName);
                break;
            case 12:
                Schema schema9 = database.getSchema(this.schemaName);
                this.session.getUser().checkSchemaOwner(schema9);
                dbObject = schema9.findDomain(this.objectName);
                i = 90120;
                break;
        }
        if (dbObject == null) {
            throw DbException.get(i, this.objectName);
        }
        String string = this.expr.optimize(this.session).getValue(this.session).getString();
        if (string != null && string.isEmpty()) {
            string = null;
        }
        if (this.column) {
            ((Table) dbObject).getColumn(this.columnName).setComment(string);
        } else {
            dbObject.setComment(string);
        }
        if (this.column || this.objectType == 0 || this.objectType == 2 || this.objectType == 1 || this.objectType == 5) {
            database.updateMeta(this.session, dbObject);
            return 0L;
        }
        Comment findComment = database.findComment(dbObject);
        if (findComment == null) {
            if (string != null) {
                Comment comment = new Comment(database, getObjectId(), dbObject);
                comment.setCommentText(string);
                database.addDatabaseObject(this.session, comment);
                return 0L;
            }
            return 0L;
        }
        if (string == null) {
            database.removeDatabaseObject(this.session, findComment);
            return 0L;
        }
        findComment.setCommentText(string);
        database.updateMeta(this.session, findComment);
        return 0L;
    }

    public void setCommentExpression(Expression expression) {
        this.expr = expression;
    }

    public void setObjectName(String str) {
        this.objectName = str;
    }

    public void setObjectType(int i) {
        this.objectType = i;
    }

    public void setColumnName(String str) {
        this.columnName = str;
    }

    public void setSchemaName(String str) {
        this.schemaName = str;
    }

    public void setColumn(boolean z) {
        this.column = z;
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return 52;
    }
}
