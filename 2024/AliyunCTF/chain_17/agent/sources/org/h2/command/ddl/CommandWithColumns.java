package org.h2.command.ddl;

import java.util.ArrayList;
import java.util.Iterator;
import org.h2.api.ErrorCode;
import org.h2.engine.Constants;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.schema.Sequence;
import org.h2.table.Column;
import org.h2.table.IndexColumn;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ddl/CommandWithColumns.class */
public abstract class CommandWithColumns extends SchemaCommand {
    private ArrayList<DefineCommand> constraintCommands;
    private AlterTableAddConstraint primaryKey;

    public abstract void addColumn(Column column);

    /* JADX INFO: Access modifiers changed from: protected */
    public CommandWithColumns(SessionLocal sessionLocal, Schema schema) {
        super(sessionLocal, schema);
    }

    public void addConstraintCommand(DefineCommand defineCommand) {
        if (!(defineCommand instanceof CreateIndex)) {
            AlterTableAddConstraint alterTableAddConstraint = (AlterTableAddConstraint) defineCommand;
            if (alterTableAddConstraint.getType() == 6 && setPrimaryKey(alterTableAddConstraint)) {
                return;
            }
        }
        getConstraintCommands().add(defineCommand);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void changePrimaryKeysToNotNull(ArrayList<Column> arrayList) {
        if (this.primaryKey != null) {
            IndexColumn[] indexColumns = this.primaryKey.getIndexColumns();
            Iterator<Column> it = arrayList.iterator();
            while (it.hasNext()) {
                Column next = it.next();
                for (IndexColumn indexColumn : indexColumns) {
                    if (next.getName().equals(indexColumn.columnName)) {
                        next.setNullable(false);
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void createConstraints() {
        if (this.constraintCommands != null) {
            Iterator<DefineCommand> it = this.constraintCommands.iterator();
            while (it.hasNext()) {
                DefineCommand next = it.next();
                next.setTransactional(this.transactional);
                next.update();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ArrayList<Sequence> generateSequences(ArrayList<Column> arrayList, boolean z) {
        ArrayList<Sequence> arrayList2 = new ArrayList<>(arrayList == null ? 0 : arrayList.size());
        if (arrayList != null) {
            Iterator<Column> it = arrayList.iterator();
            while (it.hasNext()) {
                Column next = it.next();
                if (next.hasIdentityOptions()) {
                    next.initializeSequence(this.session, getSchema(), getDatabase().allocateObjectId(), z);
                    if (!Constants.CLUSTERING_DISABLED.equals(getDatabase().getCluster())) {
                        throw DbException.getUnsupportedException("CLUSTERING && identity columns");
                    }
                }
                Sequence sequence = next.getSequence();
                if (sequence != null) {
                    arrayList2.add(sequence);
                }
            }
        }
        return arrayList2;
    }

    private ArrayList<DefineCommand> getConstraintCommands() {
        if (this.constraintCommands == null) {
            this.constraintCommands = new ArrayList<>();
        }
        return this.constraintCommands;
    }

    private boolean setPrimaryKey(AlterTableAddConstraint alterTableAddConstraint) {
        if (this.primaryKey != null) {
            IndexColumn[] indexColumns = this.primaryKey.getIndexColumns();
            IndexColumn[] indexColumns2 = alterTableAddConstraint.getIndexColumns();
            int length = indexColumns2.length;
            if (length != indexColumns.length) {
                throw DbException.get(ErrorCode.SECOND_PRIMARY_KEY);
            }
            for (int i = 0; i < length; i++) {
                if (!indexColumns2[i].columnName.equals(indexColumns[i].columnName)) {
                    throw DbException.get(ErrorCode.SECOND_PRIMARY_KEY);
                }
            }
            if (this.primaryKey.getConstraintName() != null) {
                return true;
            }
            this.constraintCommands.remove(this.primaryKey);
        }
        this.primaryKey = alterTableAddConstraint;
        return false;
    }

    public AlterTableAddConstraint getPrimaryKey() {
        return this.primaryKey;
    }
}
