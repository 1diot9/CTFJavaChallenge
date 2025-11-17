package org.h2.command.ddl;

import java.util.ArrayList;
import java.util.Iterator;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.message.DbException;
import org.h2.schema.Domain;
import org.h2.schema.Schema;
import org.h2.table.Table;
import org.h2.util.Utils;
import org.h2.value.DataType;
import org.h2.value.TypeInfo;
import org.h2.value.Value;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ddl/CreateDomain.class */
public class CreateDomain extends SchemaOwnerCommand {
    private String typeName;
    private boolean ifNotExists;
    private TypeInfo dataType;
    private Domain parentDomain;
    private Expression defaultExpression;
    private Expression onUpdateExpression;
    private String comment;
    private ArrayList<AlterDomainAddConstraint> constraintCommands;

    public CreateDomain(SessionLocal sessionLocal, Schema schema) {
        super(sessionLocal, schema);
    }

    public void setTypeName(String str) {
        this.typeName = str;
    }

    public void setIfNotExists(boolean z) {
        this.ifNotExists = z;
    }

    public void setDataType(TypeInfo typeInfo) {
        this.dataType = typeInfo;
    }

    public void setParentDomain(Domain domain) {
        this.parentDomain = domain;
    }

    public void setDefaultExpression(Expression expression) {
        this.defaultExpression = expression;
    }

    public void setOnUpdateExpression(Expression expression) {
        this.onUpdateExpression = expression;
    }

    public void setComment(String str) {
        this.comment = str;
    }

    @Override // org.h2.command.ddl.SchemaOwnerCommand
    long update(Schema schema) {
        DataType typeByName;
        if (schema.findDomain(this.typeName) != null) {
            if (this.ifNotExists) {
                return 0L;
            }
            throw DbException.get(90119, this.typeName);
        }
        if (this.typeName.indexOf(32) < 0 && (typeByName = DataType.getTypeByName(this.typeName, getDatabase().getMode())) != null) {
            if (getDatabase().equalsIdentifiers(this.typeName, Value.getTypeName(typeByName.type))) {
                throw DbException.get(90119, this.typeName);
            }
            Table firstUserTable = getDatabase().getFirstUserTable();
            if (firstUserTable != null) {
                StringBuilder append = new StringBuilder(this.typeName).append(" (");
                firstUserTable.getSQL(append, 3).append(')');
                throw DbException.get(90119, append.toString());
            }
        }
        Domain domain = new Domain(schema, getObjectId(), this.typeName);
        domain.setDataType(this.dataType != null ? this.dataType : this.parentDomain.getDataType());
        domain.setDomain(this.parentDomain);
        domain.setDefaultExpression(this.session, this.defaultExpression);
        domain.setOnUpdateExpression(this.session, this.onUpdateExpression);
        domain.setComment(this.comment);
        schema.getDatabase().addSchemaObject(this.session, domain);
        if (this.constraintCommands != null) {
            Iterator<AlterDomainAddConstraint> it = this.constraintCommands.iterator();
            while (it.hasNext()) {
                it.next().update();
            }
            return 0L;
        }
        return 0L;
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return 33;
    }

    public void addConstraintCommand(AlterDomainAddConstraint alterDomainAddConstraint) {
        if (this.constraintCommands == null) {
            this.constraintCommands = Utils.newSmallArrayList();
        }
        this.constraintCommands.add(alterDomainAddConstraint);
    }
}
