package org.h2.table;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Objects;
import org.h2.message.DbException;
import org.h2.util.JdbcUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/table/TableLinkConnection.class */
public class TableLinkConnection {
    private final HashMap<TableLinkConnection, TableLinkConnection> map;
    private final String driver;
    private final String url;
    private final String user;
    private final String password;
    private Connection conn;
    private int useCounter;
    private boolean autocommit = true;

    private TableLinkConnection(HashMap<TableLinkConnection, TableLinkConnection> hashMap, String str, String str2, String str3, String str4) {
        this.map = hashMap;
        this.driver = str;
        this.url = str2;
        this.user = str3;
        this.password = str4;
    }

    public static TableLinkConnection open(HashMap<TableLinkConnection, TableLinkConnection> hashMap, String str, String str2, String str3, String str4, boolean z) {
        TableLinkConnection tableLinkConnection;
        TableLinkConnection tableLinkConnection2 = new TableLinkConnection(hashMap, str, str2, str3, str4);
        if (!z) {
            tableLinkConnection2.open();
            return tableLinkConnection2;
        }
        synchronized (hashMap) {
            TableLinkConnection tableLinkConnection3 = hashMap.get(tableLinkConnection2);
            if (tableLinkConnection3 == null) {
                tableLinkConnection2.open();
                hashMap.put(tableLinkConnection2, tableLinkConnection2);
                tableLinkConnection3 = tableLinkConnection2;
            }
            tableLinkConnection3.useCounter++;
            tableLinkConnection = tableLinkConnection3;
        }
        return tableLinkConnection;
    }

    private void open() {
        try {
            this.conn = JdbcUtils.getConnection(this.driver, this.url, this.user, this.password);
        } catch (SQLException e) {
            throw DbException.convert(e);
        }
    }

    public int hashCode() {
        return ((Objects.hashCode(this.driver) ^ Objects.hashCode(this.url)) ^ Objects.hashCode(this.user)) ^ Objects.hashCode(this.password);
    }

    public boolean equals(Object obj) {
        if (obj instanceof TableLinkConnection) {
            TableLinkConnection tableLinkConnection = (TableLinkConnection) obj;
            return Objects.equals(this.driver, tableLinkConnection.driver) && Objects.equals(this.url, tableLinkConnection.url) && Objects.equals(this.user, tableLinkConnection.user) && Objects.equals(this.password, tableLinkConnection.password);
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Connection getConnection() {
        return this.conn;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void close(boolean z) {
        boolean z2 = false;
        synchronized (this.map) {
            int i = this.useCounter - 1;
            this.useCounter = i;
            if (i <= 0 || z) {
                z2 = true;
                this.map.remove(this);
            }
        }
        if (z2) {
            JdbcUtils.closeSilently(this.conn);
        }
    }

    public void setAutoCommit(boolean z) {
        this.autocommit = z;
    }

    public boolean getAutocommit() {
        return this.autocommit;
    }
}
