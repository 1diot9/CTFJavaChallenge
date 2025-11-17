package org.h2.constraint;

import java.util.HashSet;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionVisitor;
import org.h2.index.Index;
import org.h2.result.Row;
import org.h2.schema.Schema;
import org.h2.schema.SchemaObject;
import org.h2.table.Column;
import org.h2.table.Table;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/constraint/Constraint.class */
public abstract class Constraint extends SchemaObject implements Comparable<Constraint> {
    protected Table table;

    public abstract Type getConstraintType();

    public abstract void checkRow(SessionLocal sessionLocal, Table table, Row row, Row row2);

    public abstract boolean usesIndex(Index index);

    public abstract void setIndexOwner(Index index);

    public abstract HashSet<Column> getReferencedColumns(Table table);

    public abstract String getCreateSQLWithoutIndexes();

    public abstract boolean isBefore();

    public abstract void checkExistingData(SessionLocal sessionLocal);

    public abstract void rebuild();

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/constraint/Constraint$Type.class */
    public enum Type {
        CHECK,
        PRIMARY_KEY,
        UNIQUE,
        REFERENTIAL,
        DOMAIN;

        public String getSqlName() {
            if (this == PRIMARY_KEY) {
                return "PRIMARY KEY";
            }
            if (this == REFERENTIAL) {
                return "FOREIGN KEY";
            }
            return name();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Constraint(Schema schema, int i, String str, Table table) {
        super(schema, i, str, 1);
        this.table = table;
        if (table != null) {
            setTemporary(table.isTemporary());
        }
    }

    public Expression getExpression() {
        return null;
    }

    public Index getIndex() {
        return null;
    }

    public ConstraintUnique getReferencedConstraint() {
        return null;
    }

    @Override // org.h2.engine.DbObject
    public int getType() {
        return 5;
    }

    public Table getTable() {
        return this.table;
    }

    public Table getRefTable() {
        return this.table;
    }

    @Override // java.lang.Comparable
    public int compareTo(Constraint constraint) {
        if (this == constraint) {
            return 0;
        }
        return Integer.compare(getConstraintType().ordinal(), constraint.getConstraintType().ordinal());
    }

    @Override // org.h2.schema.SchemaObject
    public boolean isHidden() {
        return this.table != null && this.table.isHidden();
    }

    public boolean isEverything(ExpressionVisitor expressionVisitor) {
        return true;
    }
}
