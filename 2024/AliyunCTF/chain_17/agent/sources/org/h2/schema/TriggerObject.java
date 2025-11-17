package org.h2.schema;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import org.h2.api.ErrorCode;
import org.h2.api.Trigger;
import org.h2.command.CommandInterface;
import org.h2.engine.SessionLocal;
import org.h2.jdbc.JdbcConnection;
import org.h2.jdbc.JdbcResultSet;
import org.h2.jdbc.JdbcStatement;
import org.h2.message.DbException;
import org.h2.result.ResultInterface;
import org.h2.result.Row;
import org.h2.result.SimpleResult;
import org.h2.table.Column;
import org.h2.table.Table;
import org.h2.tools.TriggerAdapter;
import org.h2.util.JdbcUtils;
import org.h2.util.SourceCompiler;
import org.h2.util.StringUtils;
import org.h2.value.Value;
import org.h2.value.ValueToObjectConverter;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/schema/TriggerObject.class */
public final class TriggerObject extends SchemaObject {
    public static final int DEFAULT_QUEUE_SIZE = 1024;
    private boolean insteadOf;
    private boolean before;
    private int typeMask;
    private boolean rowBased;
    private boolean onRollback;
    private int queueSize;
    private boolean noWait;
    private Table table;
    private String triggerClassName;
    private String triggerSource;
    private Trigger triggerCallback;

    public TriggerObject(Schema schema, int i, String str, Table table) {
        super(schema, i, str, 12);
        this.queueSize = 1024;
        this.table = table;
        setTemporary(table.isTemporary());
    }

    public void setBefore(boolean z) {
        this.before = z;
    }

    public boolean isInsteadOf() {
        return this.insteadOf;
    }

    public void setInsteadOf(boolean z) {
        this.insteadOf = z;
    }

    private synchronized void load() {
        Object loadFromSource;
        if (this.triggerCallback != null) {
            return;
        }
        try {
            JdbcConnection createConnection = this.database.getSystemSession().createConnection(false);
            if (this.triggerClassName != null) {
                loadFromSource = JdbcUtils.loadUserClass(this.triggerClassName).getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            } else {
                loadFromSource = loadFromSource();
            }
            this.triggerCallback = (Trigger) loadFromSource;
            this.triggerCallback.init(createConnection, getSchema().getName(), getName(), this.table.getName(), this.before, this.typeMask);
        } catch (Throwable th) {
            this.triggerCallback = null;
            String[] strArr = new String[3];
            strArr[0] = getName();
            strArr[1] = this.triggerClassName != null ? this.triggerClassName : "..source..";
            strArr[2] = th.toString();
            throw DbException.get(ErrorCode.ERROR_CREATING_TRIGGER_OBJECT_3, th, strArr);
        }
    }

    private Trigger loadFromSource() {
        SourceCompiler compiler = this.database.getCompiler();
        synchronized (compiler) {
            String str = "org.h2.dynamic.trigger." + getName();
            compiler.setSource(str, this.triggerSource);
            try {
                try {
                    if (SourceCompiler.isJavaxScriptSource(this.triggerSource)) {
                        return (Trigger) compiler.getCompiledScript(str).eval();
                    }
                    Method method = compiler.getMethod(str);
                    if (method.getParameterTypes().length > 0) {
                        throw new IllegalStateException("No parameters are allowed for a trigger");
                    }
                    return (Trigger) method.invoke(null, new Object[0]);
                } catch (DbException e) {
                    throw e;
                }
            } catch (Exception e2) {
                throw DbException.get(ErrorCode.SYNTAX_ERROR_1, e2, this.triggerSource);
            }
        }
    }

    public void setTriggerClassName(String str, boolean z) {
        setTriggerAction(str, null, z);
    }

    public void setTriggerSource(String str, boolean z) {
        setTriggerAction(null, str, z);
    }

    private void setTriggerAction(String str, String str2, boolean z) {
        this.triggerClassName = str;
        this.triggerSource = str2;
        try {
            load();
        } catch (DbException e) {
            if (!z) {
                throw e;
            }
        }
    }

    public void fire(SessionLocal sessionLocal, int i, boolean z) {
        if (this.rowBased || this.before != z || (this.typeMask & i) == 0) {
            return;
        }
        load();
        JdbcConnection createConnection = sessionLocal.createConnection(false);
        boolean z2 = false;
        if (i != 8) {
            z2 = sessionLocal.setCommitOrRollbackDisabled(true);
        }
        Value lastIdentity = sessionLocal.getLastIdentity();
        try {
            try {
                if (this.triggerCallback instanceof TriggerAdapter) {
                    ((TriggerAdapter) this.triggerCallback).fire(createConnection, (ResultSet) null, (ResultSet) null);
                } else {
                    this.triggerCallback.fire(createConnection, null, null);
                }
                sessionLocal.setLastIdentity(lastIdentity);
                if (i != 8) {
                    sessionLocal.setCommitOrRollbackDisabled(z2);
                }
            } catch (Throwable th) {
                throw getErrorExecutingTrigger(th);
            }
        } catch (Throwable th2) {
            sessionLocal.setLastIdentity(lastIdentity);
            if (i != 8) {
                sessionLocal.setCommitOrRollbackDisabled(z2);
            }
            throw th2;
        }
    }

    private static Object[] convertToObjectList(Row row, JdbcConnection jdbcConnection) {
        if (row == null) {
            return null;
        }
        int columnCount = row.getColumnCount();
        Object[] objArr = new Object[columnCount];
        for (int i = 0; i < columnCount; i++) {
            objArr[i] = ValueToObjectConverter.valueToDefaultObject(row.getValue(i), jdbcConnection, false);
        }
        return objArr;
    }

    public boolean fireRow(SessionLocal sessionLocal, Table table, Row row, Row row2, boolean z, boolean z2) {
        DbException errorExecutingTrigger;
        Value[] updateRow;
        if (!this.rowBased || this.before != z) {
            return false;
        }
        if (z2 && !this.onRollback) {
            return false;
        }
        load();
        boolean z3 = false;
        if ((this.typeMask & 1) != 0 && row == null && row2 != null) {
            z3 = true;
        }
        if ((this.typeMask & 2) != 0 && row != null && row2 != null) {
            z3 = true;
        }
        if ((this.typeMask & 4) != 0 && row != null && row2 == null) {
            z3 = true;
        }
        if (!z3) {
            return false;
        }
        JdbcConnection createConnection = sessionLocal.createConnection(false);
        boolean autoCommit = sessionLocal.getAutoCommit();
        boolean commitOrRollbackDisabled = sessionLocal.setCommitOrRollbackDisabled(true);
        Value lastIdentity = sessionLocal.getLastIdentity();
        try {
            try {
                sessionLocal.setAutoCommit(false);
                if (this.triggerCallback instanceof TriggerAdapter) {
                    JdbcResultSet createResultSet = row != null ? createResultSet(createConnection, table, row, false) : null;
                    JdbcResultSet createResultSet2 = row2 != null ? createResultSet(createConnection, table, row2, this.before) : null;
                    try {
                        ((TriggerAdapter) this.triggerCallback).fire(createConnection, createResultSet, createResultSet2);
                        if (createResultSet2 != null && (updateRow = createResultSet2.getUpdateRow()) != null) {
                            boolean z4 = false;
                            int length = updateRow.length;
                            for (int i = 0; i < length; i++) {
                                Value value = updateRow[i];
                                if (value != null) {
                                    z4 = true;
                                    row2.setValue(i, value);
                                }
                            }
                            if (z4) {
                                table.convertUpdateRow(sessionLocal, row2, true);
                            }
                        }
                    } finally {
                    }
                } else {
                    Object[] convertToObjectList = convertToObjectList(row, createConnection);
                    Object[] convertToObjectList2 = convertToObjectList(row2, createConnection);
                    Object[] copyOf = (!this.before || convertToObjectList2 == null) ? null : Arrays.copyOf(convertToObjectList2, convertToObjectList2.length);
                    try {
                        this.triggerCallback.fire(createConnection, convertToObjectList, convertToObjectList2);
                        if (copyOf != null) {
                            boolean z5 = false;
                            for (int i2 = 0; i2 < convertToObjectList2.length; i2++) {
                                Object obj = convertToObjectList2[i2];
                                if (obj != copyOf[i2]) {
                                    z5 = true;
                                    row2.setValue(i2, ValueToObjectConverter.objectToValue(sessionLocal, obj, -1));
                                }
                            }
                            if (z5) {
                                table.convertUpdateRow(sessionLocal, row2, true);
                            }
                        }
                    } finally {
                    }
                }
                sessionLocal.setLastIdentity(lastIdentity);
                sessionLocal.setCommitOrRollbackDisabled(commitOrRollbackDisabled);
                sessionLocal.setAutoCommit(autoCommit);
            } catch (Exception e) {
                if (!this.onRollback) {
                    throw DbException.convert(e);
                }
                sessionLocal.setLastIdentity(lastIdentity);
                sessionLocal.setCommitOrRollbackDisabled(commitOrRollbackDisabled);
                sessionLocal.setAutoCommit(autoCommit);
            }
            return this.insteadOf;
        } catch (Throwable th) {
            sessionLocal.setLastIdentity(lastIdentity);
            sessionLocal.setCommitOrRollbackDisabled(commitOrRollbackDisabled);
            sessionLocal.setAutoCommit(autoCommit);
            throw th;
        }
    }

    private static JdbcResultSet createResultSet(JdbcConnection jdbcConnection, Table table, Row row, boolean z) throws SQLException {
        SimpleResult simpleResult = new SimpleResult(table.getSchema().getName(), table.getName());
        for (Column column : table.getColumns()) {
            simpleResult.addColumn(column.getName(), column.getType());
        }
        simpleResult.addRow(row.getValueList());
        simpleResult.addRow(row.getValueList());
        JdbcResultSet jdbcResultSet = new JdbcResultSet(jdbcConnection, (JdbcStatement) null, (CommandInterface) null, (ResultInterface) simpleResult, -1, false, false, z);
        jdbcResultSet.next();
        return jdbcResultSet;
    }

    private DbException getErrorExecutingTrigger(Throwable th) {
        if (th instanceof DbException) {
            return (DbException) th;
        }
        if (th instanceof SQLException) {
            return DbException.convert(th);
        }
        String[] strArr = new String[3];
        strArr[0] = getName();
        strArr[1] = this.triggerClassName != null ? this.triggerClassName : "..source..";
        strArr[2] = th.toString();
        return DbException.get(ErrorCode.ERROR_EXECUTING_TRIGGER_3, th, strArr);
    }

    public int getTypeMask() {
        return this.typeMask;
    }

    public void setTypeMask(int i) {
        this.typeMask = i;
    }

    public void setRowBased(boolean z) {
        this.rowBased = z;
    }

    public boolean isRowBased() {
        return this.rowBased;
    }

    public void setQueueSize(int i) {
        this.queueSize = i;
    }

    public int getQueueSize() {
        return this.queueSize;
    }

    public void setNoWait(boolean z) {
        this.noWait = z;
    }

    public boolean isNoWait() {
        return this.noWait;
    }

    public void setOnRollback(boolean z) {
        this.onRollback = z;
    }

    public boolean isOnRollback() {
        return this.onRollback;
    }

    @Override // org.h2.engine.DbObject
    public String getCreateSQLForCopy(Table table, String str) {
        StringBuilder sb = new StringBuilder("CREATE FORCE TRIGGER ");
        sb.append(str);
        if (this.insteadOf) {
            sb.append(" INSTEAD OF ");
        } else if (this.before) {
            sb.append(" BEFORE ");
        } else {
            sb.append(" AFTER ");
        }
        getTypeNameList(sb).append(" ON ");
        table.getSQL(sb, 0);
        if (this.rowBased) {
            sb.append(" FOR EACH ROW");
        }
        if (this.noWait) {
            sb.append(" NOWAIT");
        } else {
            sb.append(" QUEUE ").append(this.queueSize);
        }
        if (this.triggerClassName != null) {
            StringUtils.quoteStringSQL(sb.append(" CALL "), this.triggerClassName);
        } else {
            StringUtils.quoteStringSQL(sb.append(" AS "), this.triggerSource);
        }
        return sb.toString();
    }

    public StringBuilder getTypeNameList(StringBuilder sb) {
        boolean z = false;
        if ((this.typeMask & 1) != 0) {
            z = true;
            sb.append("INSERT");
        }
        if ((this.typeMask & 2) != 0) {
            if (z) {
                sb.append(", ");
            }
            z = true;
            sb.append("UPDATE");
        }
        if ((this.typeMask & 4) != 0) {
            if (z) {
                sb.append(", ");
            }
            z = true;
            sb.append("DELETE");
        }
        if ((this.typeMask & 8) != 0) {
            if (z) {
                sb.append(", ");
            }
            z = true;
            sb.append("SELECT");
        }
        if (this.onRollback) {
            if (z) {
                sb.append(", ");
            }
            sb.append("ROLLBACK");
        }
        return sb;
    }

    @Override // org.h2.engine.DbObject
    public String getCreateSQL() {
        return getCreateSQLForCopy(this.table, getSQL(0));
    }

    @Override // org.h2.engine.DbObject
    public int getType() {
        return 4;
    }

    @Override // org.h2.engine.DbObject
    public void removeChildrenAndResources(SessionLocal sessionLocal) {
        this.table.removeTrigger(this);
        this.database.removeMeta(sessionLocal, getId());
        if (this.triggerCallback != null) {
            try {
                this.triggerCallback.remove();
            } catch (SQLException e) {
                throw DbException.convert(e);
            }
        }
        this.table = null;
        this.triggerClassName = null;
        this.triggerSource = null;
        this.triggerCallback = null;
        invalidate();
    }

    public Table getTable() {
        return this.table;
    }

    public boolean isBefore() {
        return this.before;
    }

    public String getTriggerClassName() {
        return this.triggerClassName;
    }

    public String getTriggerSource() {
        return this.triggerSource;
    }

    public void close() throws SQLException {
        if (this.triggerCallback != null) {
            this.triggerCallback.close();
        }
    }

    public boolean isSelectTrigger() {
        return (this.typeMask & 8) != 0;
    }
}
