package org.h2.constraint;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import org.h2.constraint.Constraint;
import org.h2.engine.NullsDistinct;
import org.h2.engine.SessionLocal;
import org.h2.index.Index;
import org.h2.result.Row;
import org.h2.schema.Schema;
import org.h2.table.Column;
import org.h2.table.IndexColumn;
import org.h2.table.Table;
import org.h2.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/constraint/ConstraintUnique.class */
public class ConstraintUnique extends Constraint {
    private Index index;
    private boolean indexOwner;
    private IndexColumn[] columns;
    private final boolean primaryKey;
    private NullsDistinct nullsDistinct;

    public ConstraintUnique(Schema schema, int i, String str, Table table, boolean z, NullsDistinct nullsDistinct) {
        super(schema, i, str, table);
        this.primaryKey = z;
        this.nullsDistinct = nullsDistinct;
    }

    @Override // org.h2.constraint.Constraint
    public Constraint.Type getConstraintType() {
        return this.primaryKey ? Constraint.Type.PRIMARY_KEY : Constraint.Type.UNIQUE;
    }

    @Override // org.h2.engine.DbObject
    public String getCreateSQLForCopy(Table table, String str) {
        return getCreateSQLForCopy(table, str, true);
    }

    private String getCreateSQLForCopy(Table table, String str, boolean z) {
        StringBuilder sb = new StringBuilder("ALTER TABLE ");
        table.getSQL(sb, 0).append(" ADD CONSTRAINT ");
        if (table.isHidden()) {
            sb.append("IF NOT EXISTS ");
        }
        sb.append(str);
        if (this.comment != null) {
            sb.append(" COMMENT ");
            StringUtils.quoteStringSQL(sb, this.comment);
        }
        sb.append(' ').append(getConstraintType().getSqlName()).append('(');
        IndexColumn.writeColumns(sb, this.columns, 0).append(')');
        if (z && this.indexOwner && table == this.table) {
            sb.append(" INDEX ");
            this.index.getSQL(sb, 0);
        }
        return sb.toString();
    }

    @Override // org.h2.constraint.Constraint
    public String getCreateSQLWithoutIndexes() {
        return getCreateSQLForCopy(this.table, getSQL(0), false);
    }

    @Override // org.h2.engine.DbObject
    public String getCreateSQL() {
        return getCreateSQLForCopy(this.table, getSQL(0));
    }

    public void setColumns(IndexColumn[] indexColumnArr) {
        this.columns = indexColumnArr;
    }

    public IndexColumn[] getColumns() {
        return this.columns;
    }

    public void setIndex(Index index, boolean z) {
        this.index = index;
        this.indexOwner = z;
    }

    @Override // org.h2.engine.DbObject
    public void removeChildrenAndResources(SessionLocal sessionLocal) {
        if (this.table.getConstraints() != null) {
            Iterator it = new ArrayList(this.table.getConstraints()).iterator();
            while (it.hasNext()) {
                Constraint constraint = (Constraint) it.next();
                if (constraint.getReferencedConstraint() == this) {
                    this.database.removeSchemaObject(sessionLocal, constraint);
                }
            }
        }
        this.table.removeConstraint(this);
        if (this.indexOwner) {
            this.table.removeIndexOrTransferOwnership(sessionLocal, this.index);
        }
        this.database.removeMeta(sessionLocal, getId());
        this.index = null;
        this.columns = null;
        this.table = null;
        invalidate();
    }

    @Override // org.h2.constraint.Constraint
    public void checkRow(SessionLocal sessionLocal, Table table, Row row, Row row2) {
    }

    @Override // org.h2.constraint.Constraint
    public boolean usesIndex(Index index) {
        return index == this.index;
    }

    @Override // org.h2.constraint.Constraint
    public void setIndexOwner(Index index) {
        this.indexOwner = true;
    }

    @Override // org.h2.constraint.Constraint
    public HashSet<Column> getReferencedColumns(Table table) {
        HashSet<Column> hashSet = new HashSet<>();
        for (IndexColumn indexColumn : this.columns) {
            hashSet.add(indexColumn.column);
        }
        return hashSet;
    }

    @Override // org.h2.constraint.Constraint
    public boolean isBefore() {
        return true;
    }

    @Override // org.h2.constraint.Constraint
    public void checkExistingData(SessionLocal sessionLocal) {
    }

    @Override // org.h2.constraint.Constraint
    public Index getIndex() {
        return this.index;
    }

    @Override // org.h2.constraint.Constraint
    public void rebuild() {
    }

    public NullsDistinct getNullsDistinct() {
        return this.nullsDistinct;
    }
}
