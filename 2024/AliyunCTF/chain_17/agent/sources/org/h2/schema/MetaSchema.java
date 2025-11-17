package org.h2.schema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.engine.User;
import org.h2.table.Table;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/schema/MetaSchema.class */
public abstract class MetaSchema extends Schema {
    protected abstract Map<String, Table> getMap(SessionLocal sessionLocal);

    public MetaSchema(Database database, int i, String str, User user) {
        super(database, i, str, user, true);
    }

    @Override // org.h2.schema.Schema
    public Table findTableOrView(SessionLocal sessionLocal, String str) {
        Table table = getMap(sessionLocal).get(str);
        if (table != null) {
            return table;
        }
        return super.findTableOrView(sessionLocal, str);
    }

    @Override // org.h2.schema.Schema
    public Collection<Table> getAllTablesAndViews(SessionLocal sessionLocal) {
        Collection<Table> allTablesAndViews = super.getAllTablesAndViews(sessionLocal);
        if (sessionLocal == null) {
            return allTablesAndViews;
        }
        Collection<Table> values = getMap(sessionLocal).values();
        if (allTablesAndViews.isEmpty()) {
            return values;
        }
        ArrayList arrayList = new ArrayList(values.size() + allTablesAndViews.size());
        arrayList.addAll(values);
        arrayList.addAll(allTablesAndViews);
        return arrayList;
    }

    @Override // org.h2.schema.Schema
    public Table getTableOrView(SessionLocal sessionLocal, String str) {
        Table table = getMap(sessionLocal).get(str);
        if (table != null) {
            return table;
        }
        return super.getTableOrView(sessionLocal, str);
    }

    @Override // org.h2.schema.Schema
    public Table getTableOrViewByName(SessionLocal sessionLocal, String str) {
        Table table = getMap(sessionLocal).get(str);
        if (table != null) {
            return table;
        }
        return super.getTableOrViewByName(sessionLocal, str);
    }

    @Override // org.h2.schema.Schema
    public boolean isEmpty() {
        return false;
    }
}
