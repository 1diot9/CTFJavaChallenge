package org.h2.command.ddl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import org.h2.api.ErrorCode;
import org.h2.command.dml.Insert;
import org.h2.command.query.Query;
import org.h2.engine.Database;
import org.h2.engine.DbObject;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.schema.Sequence;
import org.h2.table.Column;
import org.h2.table.Table;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ddl/CreateTable.class */
public class CreateTable extends CommandWithColumns {
    private final CreateTableData data;
    private boolean ifNotExists;
    private boolean onCommitDrop;
    private boolean onCommitTruncate;
    private Query asQuery;
    private String comment;
    private boolean withNoData;

    public CreateTable(SessionLocal sessionLocal, Schema schema) {
        super(sessionLocal, schema);
        this.data = new CreateTableData();
        this.data.persistIndexes = true;
        this.data.persistData = true;
    }

    public void setQuery(Query query) {
        this.asQuery = query;
    }

    public void setTemporary(boolean z) {
        this.data.temporary = z;
    }

    public void setTableName(String str) {
        this.data.tableName = str;
    }

    @Override // org.h2.command.ddl.CommandWithColumns
    public void addColumn(Column column) {
        this.data.columns.add(column);
    }

    public ArrayList<Column> getColumns() {
        return this.data.columns;
    }

    public void setIfNotExists(boolean z) {
        this.ifNotExists = z;
    }

    @Override // org.h2.command.Prepared
    public long update() {
        Schema schema = getSchema();
        boolean z = this.data.temporary && !this.data.globalTemporary;
        Database database = getDatabase();
        if (this.data.tableEngine != null || database.getSettings().defaultTableEngine != null) {
            this.session.getUser().checkAdmin();
        } else if (!z) {
            this.session.getUser().checkSchemaOwner(schema);
        }
        if (!database.isPersistent()) {
            this.data.persistIndexes = false;
        }
        if (!z) {
            database.lockMeta(this.session);
        }
        if (schema.resolveTableOrView(this.session, this.data.tableName) != null) {
            if (this.ifNotExists) {
                return 0L;
            }
            throw DbException.get(ErrorCode.TABLE_OR_VIEW_ALREADY_EXISTS_1, this.data.tableName);
        }
        if (this.asQuery != null) {
            this.asQuery.prepare();
            if (this.data.columns.isEmpty()) {
                generateColumnsFromQuery();
            } else {
                if (this.data.columns.size() != this.asQuery.getColumnCount()) {
                    throw DbException.get(ErrorCode.COLUMN_COUNT_DOES_NOT_MATCH);
                }
                ArrayList<Column> arrayList = this.data.columns;
                for (int i = 0; i < arrayList.size(); i++) {
                    Column column = arrayList.get(i);
                    if (column.getType().getValueType() == -1) {
                        arrayList.set(i, new Column(column.getName(), this.asQuery.getExpressions().get(i).getType()));
                    }
                }
            }
        }
        changePrimaryKeysToNotNull(this.data.columns);
        this.data.id = getObjectId();
        this.data.session = this.session;
        Table createTable = schema.createTable(this.data);
        ArrayList<Sequence> generateSequences = generateSequences(this.data.columns, this.data.temporary);
        createTable.setComment(this.comment);
        if (z) {
            if (this.onCommitDrop) {
                createTable.setOnCommitDrop(true);
            }
            if (this.onCommitTruncate) {
                createTable.setOnCommitTruncate(true);
            }
            this.session.addLocalTempTable(createTable);
        } else {
            database.lockMeta(this.session);
            database.addSchemaObject(this.session, createTable);
        }
        try {
            Iterator<Column> it = this.data.columns.iterator();
            while (it.hasNext()) {
                it.next().prepareExpressions(this.session);
            }
            Iterator<Sequence> it2 = generateSequences.iterator();
            while (it2.hasNext()) {
                createTable.addSequence(it2.next());
            }
            createConstraints();
            HashSet<DbObject> hashSet = new HashSet<>();
            createTable.addDependencies(hashSet);
            Iterator<DbObject> it3 = hashSet.iterator();
            while (it3.hasNext()) {
                DbObject next = it3.next();
                if (next != createTable) {
                    if (next.getType() == 0 && (next instanceof Table)) {
                        Table table = (Table) next;
                        if (table.getId() > createTable.getId()) {
                            throw DbException.get(ErrorCode.FEATURE_NOT_SUPPORTED_1, "Table depends on another table with a higher ID: " + table + ", this is currently not supported, as it would prevent the database from being re-opened");
                        }
                    }
                }
            }
            if (this.asQuery != null && !this.withNoData) {
                insertAsData(z, database, createTable);
            }
            return 0L;
        } catch (DbException e) {
            try {
                database.checkPowerOff();
                database.removeSchemaObject(this.session, createTable);
                if (!this.transactional) {
                    this.session.commit(true);
                }
            } catch (Throwable th) {
                e.addSuppressed(th);
            }
            throw e;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void insertAsData(Table table) {
        insertAsData(false, getDatabase(), table);
    }

    private void insertAsData(boolean z, Database database, Table table) {
        boolean z2 = false;
        if (!z) {
            database.unlockMeta(this.session);
            for (Column column : table.getColumns()) {
                Sequence sequence = column.getSequence();
                if (sequence != null) {
                    z2 = true;
                    sequence.setTemporary(true);
                }
            }
        }
        try {
            this.session.startStatementWithinTransaction(null);
            Insert insert = new Insert(this.session);
            insert.setQuery(this.asQuery);
            insert.setTable(table);
            insert.setInsertFromSelect(true);
            insert.prepare();
            insert.update();
            this.session.endStatement();
            if (z2) {
                database.lockMeta(this.session);
                for (Column column2 : table.getColumns()) {
                    Sequence sequence2 = column2.getSequence();
                    if (sequence2 != null) {
                        sequence2.setTemporary(false);
                        sequence2.flush(this.session);
                    }
                }
            }
        } catch (Throwable th) {
            this.session.endStatement();
            throw th;
        }
    }

    private void generateColumnsFromQuery() {
        int columnCount = this.asQuery.getColumnCount();
        ArrayList<Expression> expressions = this.asQuery.getExpressions();
        for (int i = 0; i < columnCount; i++) {
            Expression expression = expressions.get(i);
            addColumn(new Column(expression.getColumnNameForView(this.session, i), expression.getType()));
        }
    }

    public void setPersistIndexes(boolean z) {
        this.data.persistIndexes = z;
    }

    public void setGlobalTemporary(boolean z) {
        this.data.globalTemporary = z;
    }

    public void setOnCommitDrop() {
        this.onCommitDrop = true;
    }

    public void setOnCommitTruncate() {
        this.onCommitTruncate = true;
    }

    public void setComment(String str) {
        this.comment = str;
    }

    public void setPersistData(boolean z) {
        this.data.persistData = z;
        if (!z) {
            this.data.persistIndexes = false;
        }
    }

    public void setWithNoData(boolean z) {
        this.withNoData = z;
    }

    public void setTableEngine(String str) {
        this.data.tableEngine = str;
    }

    public void setTableEngineParams(ArrayList<String> arrayList) {
        this.data.tableEngineParams = arrayList;
    }

    public void setHidden(boolean z) {
        this.data.isHidden = z;
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return 30;
    }
}
