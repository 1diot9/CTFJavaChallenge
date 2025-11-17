package org.h2.command.ddl;

import org.h2.api.ErrorCode;
import org.h2.engine.Constants;
import org.h2.engine.NullsDistinct;
import org.h2.engine.SessionLocal;
import org.h2.index.IndexType;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.table.IndexColumn;
import org.h2.table.Table;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/ddl/CreateIndex.class */
public class CreateIndex extends SchemaCommand {
    private String tableName;
    private String indexName;
    private IndexColumn[] indexColumns;
    private NullsDistinct nullsDistinct;
    private int uniqueColumnCount;
    private boolean primaryKey;
    private boolean hash;
    private boolean spatial;
    private boolean ifTableExists;
    private boolean ifNotExists;
    private String comment;

    public CreateIndex(SessionLocal sessionLocal, Schema schema) {
        super(sessionLocal, schema);
    }

    public void setIfTableExists(boolean z) {
        this.ifTableExists = z;
    }

    public void setIfNotExists(boolean z) {
        this.ifNotExists = z;
    }

    public void setTableName(String str) {
        this.tableName = str;
    }

    public void setIndexName(String str) {
        this.indexName = str;
    }

    public void setIndexColumns(IndexColumn[] indexColumnArr) {
        this.indexColumns = indexColumnArr;
    }

    @Override // org.h2.command.Prepared
    public long update() {
        IndexType createNonUnique;
        boolean isPersistent = getDatabase().isPersistent();
        Table findTableOrView = getSchema().findTableOrView(this.session, this.tableName);
        if (findTableOrView == null) {
            if (this.ifTableExists) {
                return 0L;
            }
            throw DbException.get(ErrorCode.TABLE_OR_VIEW_NOT_FOUND_1, this.tableName);
        }
        if (this.indexName != null && getSchema().findIndex(this.session, this.indexName) != null) {
            if (this.ifNotExists) {
                return 0L;
            }
            throw DbException.get(ErrorCode.INDEX_ALREADY_EXISTS_1, this.indexName);
        }
        this.session.getUser().checkTableRight(findTableOrView, 32);
        findTableOrView.lock(this.session, 2);
        if (!findTableOrView.isPersistIndexes()) {
            isPersistent = false;
        }
        int objectId = getObjectId();
        if (this.indexName == null) {
            if (this.primaryKey) {
                this.indexName = findTableOrView.getSchema().getUniqueIndexName(this.session, findTableOrView, Constants.PREFIX_PRIMARY_KEY);
            } else {
                this.indexName = findTableOrView.getSchema().getUniqueIndexName(this.session, findTableOrView, Constants.PREFIX_INDEX);
            }
        }
        if (this.primaryKey) {
            if (findTableOrView.findPrimaryKey() != null) {
                throw DbException.get(ErrorCode.SECOND_PRIMARY_KEY);
            }
            createNonUnique = IndexType.createPrimaryKey(isPersistent, this.hash);
        } else if (this.uniqueColumnCount > 0) {
            createNonUnique = IndexType.createUnique(isPersistent, this.hash, this.uniqueColumnCount, this.nullsDistinct);
        } else {
            createNonUnique = IndexType.createNonUnique(isPersistent, this.hash, this.spatial);
        }
        IndexColumn.mapColumns(this.indexColumns, findTableOrView);
        findTableOrView.addIndex(this.session, this.indexName, objectId, this.indexColumns, this.uniqueColumnCount, createNonUnique, this.create, this.comment);
        return 0L;
    }

    public void setPrimaryKey(boolean z) {
        this.primaryKey = z;
    }

    public void setUnique(NullsDistinct nullsDistinct, int i) {
        this.nullsDistinct = nullsDistinct;
        this.uniqueColumnCount = i;
    }

    public void setHash(boolean z) {
        this.hash = z;
    }

    public void setSpatial(boolean z) {
        this.spatial = z;
    }

    public void setComment(String str) {
        this.comment = str;
    }

    @Override // org.h2.command.Prepared
    public int getType() {
        return 25;
    }
}
