package org.h2.tools;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.h2.api.Trigger;
import org.h2.message.DbException;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/tools/TriggerAdapter.class */
public abstract class TriggerAdapter implements Trigger {
    protected String schemaName;
    protected String triggerName;
    protected String tableName;
    protected boolean before;
    protected int type;

    public abstract void fire(Connection connection, ResultSet resultSet, ResultSet resultSet2) throws SQLException;

    @Override // org.h2.api.Trigger
    public void init(Connection connection, String str, String str2, String str3, boolean z, int i) throws SQLException {
        this.schemaName = str;
        this.triggerName = str2;
        this.tableName = str3;
        this.before = z;
        this.type = i;
    }

    @Override // org.h2.api.Trigger
    public final void fire(Connection connection, Object[] objArr, Object[] objArr2) throws SQLException {
        throw DbException.getInternalError();
    }
}
