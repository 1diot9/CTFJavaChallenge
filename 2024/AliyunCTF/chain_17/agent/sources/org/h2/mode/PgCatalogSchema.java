package org.h2.mode;

import java.util.HashMap;
import java.util.Map;
import org.h2.engine.Constants;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.engine.User;
import org.h2.schema.MetaSchema;
import org.h2.table.Table;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/mode/PgCatalogSchema.class */
public final class PgCatalogSchema extends MetaSchema {
    private volatile HashMap<String, Table> tables;

    public PgCatalogSchema(Database database, User user) {
        super(database, Constants.PG_CATALOG_SCHEMA_ID, database.sysIdentifier(Constants.SCHEMA_PG_CATALOG), user);
    }

    @Override // org.h2.schema.MetaSchema
    protected Map<String, Table> getMap(SessionLocal sessionLocal) {
        HashMap<String, Table> hashMap = this.tables;
        if (hashMap == null) {
            hashMap = fillMap();
        }
        return hashMap;
    }

    private synchronized HashMap<String, Table> fillMap() {
        HashMap<String, Table> hashMap = this.tables;
        if (hashMap == null) {
            hashMap = this.database.newStringMap();
            for (int i = 0; i < 19; i++) {
                PgCatalogTable pgCatalogTable = new PgCatalogTable(this, Constants.PG_CATALOG_SCHEMA_ID - i, i);
                hashMap.put(pgCatalogTable.getName(), pgCatalogTable);
            }
            this.tables = hashMap;
        }
        return hashMap;
    }
}
