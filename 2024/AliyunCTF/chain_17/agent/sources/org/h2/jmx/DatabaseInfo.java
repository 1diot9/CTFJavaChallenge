package org.h2.jmx;

import ch.qos.logback.core.util.FileSize;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.h2.command.Command;
import org.h2.engine.ConnectionInfo;
import org.h2.engine.Constants;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.table.Table;
import org.h2.util.NetworkConnectionInfo;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/jmx/DatabaseInfo.class */
public class DatabaseInfo implements DatabaseInfoMBean {
    private static final Map<String, ObjectName> MBEANS = new HashMap();
    private final Database database;

    private DatabaseInfo(Database database) {
        if (database == null) {
            throw new IllegalArgumentException("Argument 'database' must not be null");
        }
        this.database = database;
    }

    private static ObjectName getObjectName(String str, String str2) throws JMException {
        String replace = str.replace(':', '_');
        String replace2 = str2.replace(':', '_');
        Hashtable hashtable = new Hashtable();
        hashtable.put("name", replace);
        hashtable.put("path", replace2);
        return new ObjectName("org.h2", hashtable);
    }

    public static void registerMBean(ConnectionInfo connectionInfo, Database database) throws JMException {
        String name = connectionInfo.getName();
        if (!MBEANS.containsKey(name)) {
            MBeanServer platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
            ObjectName objectName = getObjectName(database.getShortName(), name);
            MBEANS.put(name, objectName);
            platformMBeanServer.registerMBean(new DocumentedMBean(new DatabaseInfo(database), DatabaseInfoMBean.class), objectName);
        }
    }

    public static void unregisterMBean(String str) throws Exception {
        ObjectName remove = MBEANS.remove(str);
        if (remove != null) {
            ManagementFactory.getPlatformMBeanServer().unregisterMBean(remove);
        }
    }

    @Override // org.h2.jmx.DatabaseInfoMBean
    public boolean isExclusive() {
        return this.database.getExclusiveSession() != null;
    }

    @Override // org.h2.jmx.DatabaseInfoMBean
    public boolean isReadOnly() {
        return this.database.isReadOnly();
    }

    @Override // org.h2.jmx.DatabaseInfoMBean
    public String getMode() {
        return this.database.getMode().getName();
    }

    @Override // org.h2.jmx.DatabaseInfoMBean
    public int getTraceLevel() {
        return this.database.getTraceSystem().getLevelFile();
    }

    @Override // org.h2.jmx.DatabaseInfoMBean
    public void setTraceLevel(int i) {
        this.database.getTraceSystem().setLevelFile(i);
    }

    @Override // org.h2.jmx.DatabaseInfoMBean
    public long getFileWriteCount() {
        if (this.database.isPersistent()) {
            return this.database.getStore().getMvStore().getFileStore().getWriteCount();
        }
        return 0L;
    }

    @Override // org.h2.jmx.DatabaseInfoMBean
    public long getFileReadCount() {
        if (this.database.isPersistent()) {
            return this.database.getStore().getMvStore().getFileStore().getReadCount();
        }
        return 0L;
    }

    @Override // org.h2.jmx.DatabaseInfoMBean
    public long getFileSize() {
        long j = 0;
        if (this.database.isPersistent()) {
            j = this.database.getStore().getMvStore().getFileStore().size();
        }
        return j / FileSize.KB_COEFFICIENT;
    }

    @Override // org.h2.jmx.DatabaseInfoMBean
    public int getCacheSizeMax() {
        if (this.database.isPersistent()) {
            return this.database.getStore().getMvStore().getCacheSize() * 1024;
        }
        return 0;
    }

    @Override // org.h2.jmx.DatabaseInfoMBean
    public void setCacheSizeMax(int i) {
        if (this.database.isPersistent()) {
            this.database.setCacheSize(i);
        }
    }

    @Override // org.h2.jmx.DatabaseInfoMBean
    public int getCacheSize() {
        if (this.database.isPersistent()) {
            return this.database.getStore().getMvStore().getCacheSizeUsed() * 1024;
        }
        return 0;
    }

    @Override // org.h2.jmx.DatabaseInfoMBean
    public String getVersion() {
        return Constants.FULL_VERSION;
    }

    @Override // org.h2.jmx.DatabaseInfoMBean
    public String listSettings() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : this.database.getSettings().getSortedSettings()) {
            sb.append(entry.getKey()).append(" = ").append(entry.getValue()).append('\n');
        }
        return sb.toString();
    }

    @Override // org.h2.jmx.DatabaseInfoMBean
    public String listSessions() {
        StringBuilder sb = new StringBuilder();
        for (SessionLocal sessionLocal : this.database.getSessions(false)) {
            sb.append("session id: ").append(sessionLocal.getId());
            sb.append(" user: ").append(sessionLocal.getUser().getName()).append('\n');
            NetworkConnectionInfo networkConnectionInfo = sessionLocal.getNetworkConnectionInfo();
            if (networkConnectionInfo != null) {
                sb.append("server: ").append(networkConnectionInfo.getServer()).append('\n').append("clientAddr: ").append(networkConnectionInfo.getClient()).append('\n');
                String clientInfo = networkConnectionInfo.getClientInfo();
                if (clientInfo != null) {
                    sb.append("clientInfo: ").append(clientInfo).append('\n');
                }
            }
            sb.append("connected: ").append(sessionLocal.getSessionStart().getString()).append('\n');
            Command currentCommand = sessionLocal.getCurrentCommand();
            if (currentCommand != null) {
                sb.append("statement: ").append(currentCommand).append('\n').append("started: ").append(sessionLocal.getCommandStartOrEnd().getString()).append('\n');
            }
            for (Table table : sessionLocal.getLocks()) {
                if (table.isLockedExclusivelyBy(sessionLocal)) {
                    sb.append("write lock on ");
                } else {
                    sb.append("read lock on ");
                }
                sb.append(table.getSchema().getName()).append('.').append(table.getName()).append('\n');
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}
