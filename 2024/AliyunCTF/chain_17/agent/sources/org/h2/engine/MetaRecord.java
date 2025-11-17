package org.h2.engine;

import java.sql.SQLException;
import java.util.Comparator;
import org.h2.api.DatabaseEventListener;
import org.h2.command.Prepared;
import org.h2.message.DbException;
import org.h2.result.SearchRow;
import org.h2.value.ValueInteger;
import org.h2.value.ValueVarchar;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/engine/MetaRecord.class */
public class MetaRecord implements Comparable<MetaRecord> {
    static final Comparator<Prepared> CONSTRAINTS_COMPARATOR = (prepared, prepared2) -> {
        int type = prepared.getType();
        int type2 = prepared2.getType();
        boolean z = type == 6 || type == 4;
        if (z == (type2 == 6 || type2 == 4)) {
            return prepared.getPersistedObjectId() - prepared2.getPersistedObjectId();
        }
        return z ? -1 : 1;
    };
    private final int id;
    private final int objectType;
    private final String sql;

    public static void populateRowFromDBObject(DbObject dbObject, SearchRow searchRow) {
        searchRow.setValue(0, ValueInteger.get(dbObject.getId()));
        searchRow.setValue(1, ValueInteger.get(0));
        searchRow.setValue(2, ValueInteger.get(dbObject.getType()));
        searchRow.setValue(3, ValueVarchar.get(dbObject.getCreateSQLForMeta()));
    }

    public MetaRecord(SearchRow searchRow) {
        this.id = searchRow.getValue(0).getInt();
        this.objectType = searchRow.getValue(2).getInt();
        this.sql = searchRow.getValue(3).getString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void prepareAndExecute(Database database, SessionLocal sessionLocal, DatabaseEventListener databaseEventListener) {
        try {
            Prepared prepare = sessionLocal.prepare(this.sql);
            prepare.setPersistedObjectId(this.id);
            prepare.update();
        } catch (DbException e) {
            throwException(database, databaseEventListener, e, this.sql);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Prepared prepare(Database database, SessionLocal sessionLocal, DatabaseEventListener databaseEventListener) {
        try {
            Prepared prepare = sessionLocal.prepare(this.sql);
            prepare.setPersistedObjectId(this.id);
            return prepare;
        } catch (DbException e) {
            throwException(database, databaseEventListener, e, this.sql);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void execute(Database database, Prepared prepared, DatabaseEventListener databaseEventListener, String str) {
        try {
            prepared.update();
        } catch (DbException e) {
            throwException(database, databaseEventListener, e, str);
        }
    }

    private static void throwException(Database database, DatabaseEventListener databaseEventListener, DbException dbException, String str) {
        DbException addSQL = dbException.addSQL(str);
        SQLException sQLException = addSQL.getSQLException();
        database.getTrace(2).error(sQLException, str);
        if (databaseEventListener != null) {
            databaseEventListener.exceptionThrown(sQLException, str);
            return;
        }
        throw addSQL;
    }

    public int getId() {
        return this.id;
    }

    public int getObjectType() {
        return this.objectType;
    }

    public String getSQL() {
        return this.sql;
    }

    @Override // java.lang.Comparable
    public int compareTo(MetaRecord metaRecord) {
        int createOrder = getCreateOrder();
        int createOrder2 = metaRecord.getCreateOrder();
        if (createOrder != createOrder2) {
            return createOrder - createOrder2;
        }
        return getId() - metaRecord.getId();
    }

    private int getCreateOrder() {
        switch (this.objectType) {
            case 0:
                return 7;
            case 1:
                return 8;
            case 2:
                return 1;
            case 3:
                return 5;
            case 4:
                return 10;
            case 5:
                return 9;
            case 6:
                return 0;
            case 7:
                return 12;
            case 8:
                return 13;
            case 9:
                return 3;
            case 10:
                return 2;
            case 11:
                return 6;
            case 12:
                return 4;
            case 13:
                return 15;
            case 14:
                return 14;
            case 15:
                return 11;
            default:
                throw DbException.getInternalError("type=" + this.objectType);
        }
    }

    public String toString() {
        return "MetaRecord [id=" + this.id + ", objectType=" + this.objectType + ", sql=" + this.sql + ']';
    }
}
