package org.h2.api;

import org.h2.command.ddl.CreateTableData;
import org.h2.table.Table;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/api/TableEngine.class */
public interface TableEngine {
    Table createTable(CreateTableData createTableData);
}
