package org.h2.server.web;

import ch.qos.logback.classic.ClassicConstants;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import org.h2.bnf.Bnf;
import org.h2.bnf.context.DbContents;
import org.h2.bnf.context.DbContextRule;
import org.h2.message.DbException;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/server/web/WebSession.class */
public class WebSession {
    private static final int MAX_HISTORY = 1000;
    long lastAccess;
    Locale locale;
    Statement executingStatement;
    ResultSet result;
    private final WebServer server;
    private final ArrayList<String> commandHistory;
    private Connection conn;
    private DatabaseMetaData meta;
    private Bnf bnf;
    private boolean shutdownServerOnDisconnect;
    final HashMap<String, Object> map = new HashMap<>();
    private DbContents contents = new DbContents();

    /* JADX INFO: Access modifiers changed from: package-private */
    public WebSession(WebServer webServer) {
        this.server = webServer;
        this.commandHistory = webServer.getCommandHistoryList();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void put(String str, Object obj) {
        this.map.put(str, obj);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Object get(String str) {
        if ("sessions".equals(str)) {
            return this.server.getSessions();
        }
        return this.map.get(str);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Object remove(String str) {
        return this.map.remove(str);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Bnf getBnf() {
        return this.bnf;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void loadBnf() {
        try {
            Bnf bnf = Bnf.getInstance(null);
            DbContextRule dbContextRule = new DbContextRule(this.contents, 0);
            DbContextRule dbContextRule2 = new DbContextRule(this.contents, 3);
            DbContextRule dbContextRule3 = new DbContextRule(this.contents, 2);
            DbContextRule dbContextRule4 = new DbContextRule(this.contents, 1);
            DbContextRule dbContextRule5 = new DbContextRule(this.contents, 5);
            DbContextRule dbContextRule6 = new DbContextRule(this.contents, 4);
            bnf.updateTopic("procedure", new DbContextRule(this.contents, 6));
            bnf.updateTopic("column_name", dbContextRule);
            bnf.updateTopic("new_table_alias", dbContextRule2);
            bnf.updateTopic("table_alias", dbContextRule3);
            bnf.updateTopic("column_alias", dbContextRule6);
            bnf.updateTopic("table_name", dbContextRule4);
            bnf.updateTopic("schema_name", dbContextRule5);
            bnf.linkStatements();
            this.bnf = bnf;
        } catch (Exception e) {
            this.server.traceError(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getCommand(int i) {
        return this.commandHistory.get(i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addCommand(String str) {
        if (str == null) {
            return;
        }
        String trim = str.trim();
        if (trim.isEmpty()) {
            return;
        }
        if (this.commandHistory.size() > 1000) {
            this.commandHistory.remove(0);
        }
        int indexOf = this.commandHistory.indexOf(trim);
        if (indexOf >= 0) {
            this.commandHistory.remove(indexOf);
        }
        this.commandHistory.add(trim);
        if (this.server.isCommandHistoryAllowed()) {
            this.server.saveCommandHistoryList(this.commandHistory);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ArrayList<String> getCommandHistory() {
        return this.commandHistory;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public HashMap<String, Object> getInfo() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.putAll(this.map);
        hashMap.put("lastAccess", new Timestamp(this.lastAccess).toString());
        try {
            hashMap.put("url", this.conn == null ? "${text.admin.notConnected}" : this.conn.getMetaData().getURL());
            hashMap.put(ClassicConstants.USER_MDC_KEY, this.conn == null ? "-" : this.conn.getMetaData().getUserName());
            hashMap.put("lastQuery", this.commandHistory.isEmpty() ? "" : this.commandHistory.get(0));
            hashMap.put("executing", this.executingStatement == null ? "${text.admin.no}" : "${text.admin.yes}");
        } catch (SQLException e) {
            DbException.traceThrowable(e);
        }
        return hashMap;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setConnection(Connection connection) throws SQLException {
        this.conn = connection;
        if (connection == null) {
            this.meta = null;
        } else {
            this.meta = connection.getMetaData();
        }
        this.contents = new DbContents();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DatabaseMetaData getMetaData() {
        return this.meta;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Connection getConnection() {
        return this.conn;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DbContents getContents() {
        return this.contents;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setShutdownServerOnDisconnect() {
        this.shutdownServerOnDisconnect = true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean getShutdownServerOnDisconnect() {
        return this.shutdownServerOnDisconnect;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void close() {
        if (this.executingStatement != null) {
            try {
                this.executingStatement.cancel();
            } catch (Exception e) {
            }
        }
        if (this.conn != null) {
            try {
                this.conn.close();
            } catch (Exception e2) {
            }
        }
    }
}
