package org.h2.schema;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.engine.User;
import org.h2.table.InformationSchemaTable;
import org.h2.table.InformationSchemaTableLegacy;
import org.h2.table.Table;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/schema/InformationSchema.class */
public final class InformationSchema extends MetaSchema {
    private volatile HashMap<String, Table> newTables;
    private volatile HashMap<String, Table> oldTables;

    public InformationSchema(Database database, User user) {
        super(database, -1, database.sysIdentifier("INFORMATION_SCHEMA"), user);
    }

    @Override // org.h2.schema.MetaSchema
    protected Map<String, Table> getMap(SessionLocal sessionLocal) {
        if (sessionLocal == null) {
            return Collections.emptyMap();
        }
        boolean isOldInformationSchema = sessionLocal.isOldInformationSchema();
        HashMap<String, Table> hashMap = isOldInformationSchema ? this.oldTables : this.newTables;
        if (hashMap == null) {
            hashMap = fillMap(isOldInformationSchema);
        }
        return hashMap;
    }

    private synchronized HashMap<String, Table> fillMap(boolean z) {
        HashMap<String, Table> hashMap = z ? this.oldTables : this.newTables;
        if (hashMap == null) {
            hashMap = this.database.newStringMap(64);
            if (z) {
                for (int i = 0; i < 36; i++) {
                    InformationSchemaTableLegacy informationSchemaTableLegacy = new InformationSchemaTableLegacy(this, (-1) - i, i);
                    hashMap.put(informationSchemaTableLegacy.getName(), informationSchemaTableLegacy);
                }
                this.oldTables = hashMap;
            } else {
                for (int i2 = 0; i2 < 35; i2++) {
                    InformationSchemaTable informationSchemaTable = new InformationSchemaTable(this, (-1) - i2, i2);
                    hashMap.put(informationSchemaTable.getName(), informationSchemaTable);
                }
                this.newTables = hashMap;
            }
        }
        return hashMap;
    }
}
