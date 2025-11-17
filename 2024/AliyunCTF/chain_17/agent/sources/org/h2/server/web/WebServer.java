package org.h2.server.web;

import cn.hutool.core.text.StrPool;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.h2.engine.Constants;
import org.h2.engine.SysProperties;
import org.h2.message.DbException;
import org.h2.security.SHA256;
import org.h2.server.Service;
import org.h2.server.ShutdownHandler;
import org.h2.store.fs.FileUtils;
import org.h2.util.JdbcUtils;
import org.h2.util.MathUtils;
import org.h2.util.NetUtils;
import org.h2.util.NetworkConnectionInfo;
import org.h2.util.SortedProperties;
import org.h2.util.StringUtils;
import org.h2.util.Tool;
import org.h2.util.Utils;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/server/web/WebServer.class */
public class WebServer implements Service {
    private static final String COMMAND_HISTORY = "commandHistory";
    private static int ticker;
    private int port;
    private boolean allowOthers;
    private String externalNames;
    private boolean isDaemon;
    private boolean ssl;
    private byte[] adminPassword;
    private long lastTimeoutCheck;
    private String startDateTime;
    private ServerSocket serverSocket;
    private String host;
    private String url;
    private ShutdownHandler shutdownHandler;
    private Thread listenerThread;
    boolean virtualThreads;
    private String key;
    private boolean allowSecureCreation;
    private boolean trace;
    private TranslateThread translateThread;
    private String commandHistoryString;
    private static final String DEFAULT_LANGUAGE = "en";
    static final String[][] LANGUAGES = {new String[]{"cs", "Čeština"}, new String[]{"de", "Deutsch"}, new String[]{DEFAULT_LANGUAGE, "English"}, new String[]{"es", "Español"}, new String[]{"fr", "Français"}, new String[]{"hi", "Hindi हिंदी"}, new String[]{"hu", "Magyar"}, new String[]{"ko", "한국어"}, new String[]{"in", "Indonesia"}, new String[]{"it", "Italiano"}, new String[]{"ja", "日本語"}, new String[]{"nl", "Nederlands"}, new String[]{"pl", "Polski"}, new String[]{"pt_BR", "Português (Brasil)"}, new String[]{"pt_PT", "Português (Europeu)"}, new String[]{"ru", "русский"}, new String[]{"sk", "Slovensky"}, new String[]{"tr", "Türkçe"}, new String[]{"uk", "Українська"}, new String[]{"zh_CN", "中文 (简体)"}, new String[]{"zh_TW", "中文 (繁體)"}};
    private static final String[] GENERIC = {"Generic JNDI Data Source|javax.naming.InitialContext|java:comp/env/jdbc/Test|sa", "Generic Teradata|com.teradata.jdbc.TeraDriver|jdbc:teradata://whomooz/|", "Generic Snowflake|com.snowflake.client.jdbc.SnowflakeDriver|jdbc:snowflake://accountName.snowflakecomputing.com|", "Generic Redshift|com.amazon.redshift.jdbc42.Driver|jdbc:redshift://endpoint:5439/database|", "Generic Impala|org.cloudera.impala.jdbc41.Driver|jdbc:impala://clustername:21050/default|", "Generic Hive 2|org.apache.hive.jdbc.HiveDriver|jdbc:hive2://clustername:10000/default|", "Generic Hive|org.apache.hadoop.hive.jdbc.HiveDriver|jdbc:hive://clustername:10000/default|", "Generic Azure SQL|com.microsoft.sqlserver.jdbc.SQLServerDriver|jdbc:sqlserver://name.database.windows.net:1433|", "Generic Firebird Server|org.firebirdsql.jdbc.FBDriver|jdbc:firebirdsql:localhost:c:/temp/firebird/test|sysdba", "Generic SQLite|org.sqlite.JDBC|jdbc:sqlite:test|sa", "Generic DB2|com.ibm.db2.jcc.DB2Driver|jdbc:db2://localhost/test|", "Generic Oracle|oracle.jdbc.driver.OracleDriver|jdbc:oracle:thin:@localhost:1521:XE|sa", "Generic MS SQL Server 2000|com.microsoft.jdbc.sqlserver.SQLServerDriver|jdbc:microsoft:sqlserver://localhost:1433;DatabaseName=sqlexpress|sa", "Generic MS SQL Server 2005|com.microsoft.sqlserver.jdbc.SQLServerDriver|jdbc:sqlserver://localhost;DatabaseName=test|sa", "Generic PostgreSQL|org.postgresql.Driver|jdbc:postgresql:test|", "Generic MySQL|com.mysql.cj.jdbc.Driver|jdbc:mysql://localhost:3306/test|", "Generic MariaDB|org.mariadb.jdbc.Driver|jdbc:mariadb://localhost:3306/test|", "Generic HSQLDB|org.hsqldb.jdbcDriver|jdbc:hsqldb:test;hsqldb.default_table_type=cached|sa", "Generic Derby (Server)|org.apache.derby.client.ClientAutoloadedDriver|jdbc:derby://localhost:1527/test;create=true|sa", "Generic Derby (Embedded)|org.apache.derby.iapi.jdbc.AutoloadedDriver|jdbc:derby:test;create=true|sa", "Generic H2 (Server)|org.h2.Driver|jdbc:h2:tcp://localhost/~/test|sa", "Generic H2 (Embedded)|org.h2.Driver|jdbc:h2:~/test|sa"};
    private static final long SESSION_TIMEOUT = SysProperties.CONSOLE_TIMEOUT;
    private final Set<WebThread> running = Collections.synchronizedSet(new HashSet());
    private final HashMap<String, ConnectionInfo> connInfoMap = new HashMap<>();
    private final HashMap<String, WebSession> sessions = new HashMap<>();
    private final HashSet<String> languages = new HashSet<>();
    private boolean ifExists = true;
    private boolean allowChunked = true;
    private String serverPropertiesDir = Constants.SERVER_PROPERTIES_DIR;

    /* JADX INFO: Access modifiers changed from: package-private */
    public byte[] getFile(String str) throws IOException {
        trace("getFile <" + str + ">");
        byte[] resource = Utils.getResource("/org/h2/server/web/res/" + str);
        if (resource == null) {
            trace(" null");
        } else {
            trace(" size=" + resource.length);
        }
        return resource;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void remove(WebThread webThread) {
        this.running.remove(webThread);
    }

    private static String generateSessionId() {
        return StringUtils.convertBytesToHex(MathUtils.secureRandomBytes(16));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public WebSession getSession(String str) {
        long currentTimeMillis = System.currentTimeMillis();
        if (this.lastTimeoutCheck + SESSION_TIMEOUT < currentTimeMillis) {
            Iterator it = new ArrayList(this.sessions.keySet()).iterator();
            while (it.hasNext()) {
                String str2 = (String) it.next();
                if (this.sessions.get(str2).lastAccess + SESSION_TIMEOUT < currentTimeMillis) {
                    trace("timeout for " + str2);
                    this.sessions.remove(str2);
                }
            }
            this.lastTimeoutCheck = currentTimeMillis;
        }
        WebSession webSession = this.sessions.get(str);
        if (webSession != null) {
            webSession.lastAccess = System.currentTimeMillis();
        }
        return webSession;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public WebSession createNewSession(String str) {
        String generateSessionId;
        do {
            generateSessionId = generateSessionId();
        } while (this.sessions.get(generateSessionId) != null);
        WebSession webSession = new WebSession(this);
        webSession.lastAccess = System.currentTimeMillis();
        webSession.put("sessionId", generateSessionId);
        webSession.put("ip", str);
        webSession.put("language", DEFAULT_LANGUAGE);
        webSession.put("frame-border", CustomBooleanEditor.VALUE_0);
        webSession.put("frameset-border", "4");
        this.sessions.put(generateSessionId, webSession);
        readTranslations(webSession, DEFAULT_LANGUAGE);
        return getSession(generateSessionId);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getStartDateTime() {
        if (this.startDateTime == null) {
            this.startDateTime = DateTimeFormatter.ofPattern("EEE, d MMM yyyy HH:mm:ss z", Locale.ENGLISH).format(ZonedDateTime.now(ZoneId.of("UTC")));
        }
        return this.startDateTime;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getKey() {
        return this.key;
    }

    public void setKey(String str) {
        if (!this.allowOthers) {
            this.key = str;
        }
    }

    public void setAllowSecureCreation(boolean z) {
        if (!this.allowOthers) {
            this.allowSecureCreation = z;
        }
    }

    @Override // org.h2.server.Service
    public void init(String... strArr) {
        int i = 0;
        while (strArr != null && i < strArr.length) {
            if ("-properties".equals(strArr[i])) {
                i++;
                this.serverPropertiesDir = strArr[i];
            }
            i++;
        }
        Properties loadProperties = loadProperties();
        this.port = SortedProperties.getIntProperty(loadProperties, "webPort", Constants.DEFAULT_HTTP_PORT);
        this.ssl = SortedProperties.getBooleanProperty(loadProperties, "webSSL", false);
        this.allowOthers = SortedProperties.getBooleanProperty(loadProperties, "webAllowOthers", false);
        setExternalNames(SortedProperties.getStringProperty(loadProperties, "webExternalNames", null));
        setAdminPassword(SortedProperties.getStringProperty(loadProperties, "webAdminPassword", null));
        this.commandHistoryString = loadProperties.getProperty(COMMAND_HISTORY);
        int i2 = 0;
        while (strArr != null && i2 < strArr.length) {
            String str = strArr[i2];
            if (Tool.isOption(str, "-webPort")) {
                i2++;
                this.port = Integer.decode(strArr[i2]).intValue();
            } else if (Tool.isOption(str, "-webSSL")) {
                this.ssl = true;
            } else if (Tool.isOption(str, "-webAllowOthers")) {
                this.allowOthers = true;
            } else if (Tool.isOption(str, "-webExternalNames")) {
                i2++;
                setExternalNames(strArr[i2]);
            } else if (Tool.isOption(str, "-webDaemon")) {
                this.isDaemon = true;
            } else if (Tool.isOption(str, "-webVirtualThreads")) {
                i2++;
                this.virtualThreads = Utils.parseBoolean(strArr[i2], this.virtualThreads, true);
            } else if (Tool.isOption(str, "-baseDir")) {
                i2++;
                SysProperties.setBaseDir(strArr[i2]);
            } else if (Tool.isOption(str, "-ifExists")) {
                this.ifExists = true;
            } else if (Tool.isOption(str, "-ifNotExists")) {
                this.ifExists = false;
            } else if (Tool.isOption(str, "-webAdminPassword")) {
                i2++;
                setAdminPassword(strArr[i2]);
            } else if (Tool.isOption(str, "-properties")) {
                i2++;
            } else if (Tool.isOption(str, "-trace")) {
                this.trace = true;
            }
            i2++;
        }
        for (String[] strArr2 : LANGUAGES) {
            this.languages.add(strArr2[0]);
        }
        if (this.allowOthers) {
            this.key = null;
        }
        updateURL();
    }

    @Override // org.h2.server.Service
    public String getURL() {
        updateURL();
        return this.url;
    }

    public String getHost() {
        if (this.host == null) {
            updateURL();
        }
        return this.host;
    }

    private void updateURL() {
        try {
            this.host = StringUtils.toLowerEnglish(NetUtils.getLocalAddress());
            StringBuilder append = new StringBuilder(this.ssl ? "https" : "http").append("://").append(this.host).append(':').append(this.port);
            if (this.key != null && this.serverSocket != null) {
                append.append("?key=").append(this.key);
            }
            this.url = append.toString();
        } catch (NoClassDefFoundError e) {
        }
    }

    @Override // org.h2.server.Service
    public void start() {
        this.serverSocket = NetUtils.createServerSocket(this.port, this.ssl);
        this.port = this.serverSocket.getLocalPort();
        updateURL();
    }

    @Override // org.h2.server.Service
    public void listen() {
        this.listenerThread = Thread.currentThread();
        while (this.serverSocket != null) {
            try {
                WebThread webThread = new WebThread(this.serverSocket.accept(), this);
                this.running.add(webThread);
                webThread.start();
            } catch (Exception e) {
                trace(e.toString());
                return;
            }
        }
    }

    @Override // org.h2.server.Service
    public boolean isRunning(boolean z) {
        if (this.serverSocket == null) {
            return false;
        }
        try {
            NetUtils.createLoopbackSocket(this.port, this.ssl).close();
            return true;
        } catch (Exception e) {
            if (z) {
                traceError(e);
                return false;
            }
            return false;
        }
    }

    public boolean isStopped() {
        return this.serverSocket == null;
    }

    @Override // org.h2.server.Service
    public void stop() {
        if (this.serverSocket != null) {
            try {
                this.serverSocket.close();
            } catch (IOException e) {
                traceError(e);
            }
            this.serverSocket = null;
        }
        if (this.listenerThread != null) {
            try {
                this.listenerThread.join(1000L);
            } catch (InterruptedException e2) {
                DbException.traceThrowable(e2);
            }
        }
        Iterator it = new ArrayList(this.sessions.values()).iterator();
        while (it.hasNext()) {
            ((WebSession) it.next()).close();
        }
        Iterator it2 = new ArrayList(this.running).iterator();
        while (it2.hasNext()) {
            WebThread webThread = (WebThread) it2.next();
            try {
                webThread.stopNow();
                webThread.join(100);
            } catch (Exception e3) {
                traceError(e3);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void trace(String str) {
        if (this.trace) {
            System.out.println(str);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void traceError(Throwable th) {
        if (this.trace) {
            th.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean supportsLanguage(String str) {
        return this.languages.contains(str);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void readTranslations(WebSession webSession, String str) {
        Properties properties = new Properties();
        try {
            trace("translation: " + str);
            String str2 = new String(getFile("_text_" + str + ".prop"), StandardCharsets.UTF_8);
            trace("  " + str2);
            properties = SortedProperties.fromLines(str2);
            for (Map.Entry entry : properties.entrySet()) {
                String str3 = (String) entry.getValue();
                if (str3.startsWith("#")) {
                    entry.setValue(str3.substring(1));
                }
            }
        } catch (IOException e) {
            DbException.traceThrowable(e);
        }
        webSession.put("text", new HashMap(properties));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ArrayList<HashMap<String, Object>> getSessions() {
        ArrayList<HashMap<String, Object>> arrayList = new ArrayList<>(this.sessions.size());
        Iterator<WebSession> it = this.sessions.values().iterator();
        while (it.hasNext()) {
            arrayList.add(it.next().getInfo());
        }
        return arrayList;
    }

    @Override // org.h2.server.Service
    public String getType() {
        return "Web Console";
    }

    @Override // org.h2.server.Service
    public String getName() {
        return "H2 Console Server";
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setAllowOthers(boolean z) {
        if (z) {
            this.key = null;
        }
        this.allowOthers = z;
    }

    @Override // org.h2.server.Service
    public boolean getAllowOthers() {
        return this.allowOthers;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setExternalNames(String str) {
        this.externalNames = str != null ? StringUtils.toLowerEnglish(str) : null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getExternalNames() {
        return this.externalNames;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setSSL(boolean z) {
        this.ssl = z;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setPort(int i) {
        this.port = i;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean getSSL() {
        return this.ssl;
    }

    @Override // org.h2.server.Service
    public int getPort() {
        return this.port;
    }

    public boolean isCommandHistoryAllowed() {
        return this.commandHistoryString != null;
    }

    public void setCommandHistoryAllowed(boolean z) {
        if (z) {
            if (this.commandHistoryString == null) {
                this.commandHistoryString = "";
                return;
            }
            return;
        }
        this.commandHistoryString = null;
    }

    public ArrayList<String> getCommandHistoryList() {
        ArrayList<String> arrayList = new ArrayList<>();
        if (this.commandHistoryString == null) {
            return arrayList;
        }
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (true) {
            if (i == this.commandHistoryString.length() || this.commandHistoryString.charAt(i) == ';') {
                if (sb.length() > 0) {
                    arrayList.add(sb.toString());
                    sb.delete(0, sb.length());
                }
                if (i == this.commandHistoryString.length()) {
                    return arrayList;
                }
            } else if (this.commandHistoryString.charAt(i) == '\\' && i < this.commandHistoryString.length() - 1) {
                i++;
                sb.append(this.commandHistoryString.charAt(i));
            } else {
                sb.append(this.commandHistoryString.charAt(i));
            }
            i++;
        }
    }

    public void saveCommandHistoryList(ArrayList<String> arrayList) {
        StringBuilder sb = new StringBuilder();
        Iterator<String> it = arrayList.iterator();
        while (it.hasNext()) {
            String next = it.next();
            if (sb.length() > 0) {
                sb.append(';');
            }
            sb.append(next.replace(StrPool.BACKSLASH, "\\\\").replace(";", "\\;"));
        }
        this.commandHistoryString = sb.toString();
        saveProperties(null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConnectionInfo getSetting(String str) {
        return this.connInfoMap.get(str);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateSetting(ConnectionInfo connectionInfo) {
        this.connInfoMap.put(connectionInfo.name, connectionInfo);
        int i = ticker;
        ticker = i + 1;
        connectionInfo.lastAccess = i;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeSetting(String str) {
        this.connInfoMap.remove(str);
    }

    private Properties loadProperties() {
        try {
            if ("null".equals(this.serverPropertiesDir)) {
                return new Properties();
            }
            return SortedProperties.loadProperties(this.serverPropertiesDir + "/" + Constants.SERVER_PROPERTIES_NAME);
        } catch (Exception e) {
            DbException.traceThrowable(e);
            return new Properties();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String[] getSettingNames() {
        ArrayList<ConnectionInfo> settings = getSettings();
        String[] strArr = new String[settings.size()];
        for (int i = 0; i < settings.size(); i++) {
            strArr[i] = settings.get(i).name;
        }
        return strArr;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized ArrayList<ConnectionInfo> getSettings() {
        ArrayList<ConnectionInfo> arrayList = new ArrayList<>();
        if (this.connInfoMap.size() == 0) {
            Properties loadProperties = loadProperties();
            if (loadProperties.size() == 0) {
                for (String str : GENERIC) {
                    ConnectionInfo connectionInfo = new ConnectionInfo(str);
                    arrayList.add(connectionInfo);
                    updateSetting(connectionInfo);
                }
            } else {
                int i = 0;
                while (true) {
                    String property = loadProperties.getProperty(Integer.toString(i));
                    if (property == null) {
                        break;
                    }
                    ConnectionInfo connectionInfo2 = new ConnectionInfo(property);
                    arrayList.add(connectionInfo2);
                    updateSetting(connectionInfo2);
                    i++;
                }
            }
        } else {
            arrayList.addAll(this.connInfoMap.values());
        }
        Collections.sort(arrayList);
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void saveProperties(Properties properties) {
        if (properties == null) {
            try {
                Properties loadProperties = loadProperties();
                properties = new SortedProperties();
                properties.setProperty("webPort", Integer.toString(SortedProperties.getIntProperty(loadProperties, "webPort", this.port)));
                properties.setProperty("webAllowOthers", Boolean.toString(SortedProperties.getBooleanProperty(loadProperties, "webAllowOthers", this.allowOthers)));
                if (this.externalNames != null) {
                    properties.setProperty("webExternalNames", this.externalNames);
                }
                properties.setProperty("webSSL", Boolean.toString(SortedProperties.getBooleanProperty(loadProperties, "webSSL", this.ssl)));
                if (this.adminPassword != null) {
                    properties.setProperty("webAdminPassword", StringUtils.convertBytesToHex(this.adminPassword));
                }
                if (this.commandHistoryString != null) {
                    properties.setProperty(COMMAND_HISTORY, this.commandHistoryString);
                }
            } catch (Exception e) {
                DbException.traceThrowable(e);
                return;
            }
        }
        ArrayList<ConnectionInfo> settings = getSettings();
        int size = settings.size();
        for (int i = 0; i < size; i++) {
            ConnectionInfo connectionInfo = settings.get(i);
            if (connectionInfo != null) {
                properties.setProperty(Integer.toString((size - i) - 1), connectionInfo.getString());
            }
        }
        if (!"null".equals(this.serverPropertiesDir)) {
            OutputStream newOutputStream = FileUtils.newOutputStream(this.serverPropertiesDir + "/" + Constants.SERVER_PROPERTIES_NAME, false);
            properties.store(newOutputStream, "H2 Server Properties");
            newOutputStream.close();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Connection getConnection(String str, String str2, String str3, String str4, String str5, NetworkConnectionInfo networkConnectionInfo) throws SQLException {
        return JdbcUtils.getConnection(str.trim(), str2.trim(), str3.trim(), str4, networkConnectionInfo, this.ifExists && !(this.allowSecureCreation && this.key != null && this.key.equals(str5)));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void shutdown() {
        if (this.shutdownHandler != null) {
            this.shutdownHandler.shutdown();
        }
    }

    public void setShutdownHandler(ShutdownHandler shutdownHandler) {
        this.shutdownHandler = shutdownHandler;
    }

    public String addSession(Connection connection) throws SQLException {
        WebSession createNewSession = createNewSession("local");
        createNewSession.setShutdownServerOnDisconnect();
        createNewSession.setConnection(connection);
        createNewSession.put("url", connection.getMetaData().getURL());
        return this.url + "/frame.jsp?jsessionid=" + ((String) createNewSession.get("sessionId"));
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/server/web/WebServer$TranslateThread.class */
    private class TranslateThread extends Thread {
        private final Path file = Paths.get("translation.properties", new String[0]);
        private final Map<Object, Object> translation;
        private volatile boolean stopNow;

        TranslateThread(Map<Object, Object> map) {
            this.translation = map;
        }

        public String getFileName() {
            return this.file.toAbsolutePath().toString();
        }

        public void stopNow() {
            this.stopNow = true;
            try {
                join();
            } catch (InterruptedException e) {
            }
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            while (!this.stopNow) {
                try {
                    SortedProperties sortedProperties = new SortedProperties();
                    if (Files.exists(this.file, new LinkOption[0])) {
                        sortedProperties.load(Files.newInputStream(this.file, new OpenOption[0]));
                        this.translation.putAll(sortedProperties);
                    } else {
                        OutputStream newOutputStream = Files.newOutputStream(this.file, new OpenOption[0]);
                        sortedProperties.putAll(this.translation);
                        sortedProperties.store(newOutputStream, "Translation");
                    }
                    Thread.sleep(1000L);
                } catch (Exception e) {
                    WebServer.this.traceError(e);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String startTranslate(Map<Object, Object> map) {
        if (this.translateThread != null) {
            this.translateThread.stopNow();
        }
        this.translateThread = new TranslateThread(map);
        this.translateThread.setDaemon(true);
        this.translateThread.start();
        return this.translateThread.getFileName();
    }

    @Override // org.h2.server.Service
    public boolean isDaemon() {
        return this.isDaemon;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setAllowChunked(boolean z) {
        this.allowChunked = z;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean getAllowChunked() {
        return this.allowChunked;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public byte[] getAdminPassword() {
        return this.adminPassword;
    }

    void setAdminPassword(String str) {
        if (str == null || str.isEmpty()) {
            this.adminPassword = null;
        } else {
            if (str.length() != 128) {
                throw new IllegalArgumentException("Use result of org.h2.server.web.WebServer.encodeAdminPassword(String)");
            }
            this.adminPassword = StringUtils.convertHexToBytes(str);
        }
    }

    public static String encodeAdminPassword(String str) {
        if (str.length() < 12) {
            throw new IllegalArgumentException("Min length: 12");
        }
        byte[] secureRandomBytes = MathUtils.secureRandomBytes(32);
        byte[] hashWithSalt = SHA256.getHashWithSalt(str.getBytes(StandardCharsets.UTF_8), secureRandomBytes);
        byte[] copyOf = Arrays.copyOf(secureRandomBytes, 64);
        System.arraycopy(hashWithSalt, 0, copyOf, 32, 32);
        return StringUtils.convertBytesToHex(copyOf);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean checkAdminPassword(String str) {
        if (this.adminPassword == null) {
            return false;
        }
        byte[] copyOf = Arrays.copyOf(this.adminPassword, 32);
        byte[] bArr = new byte[32];
        System.arraycopy(this.adminPassword, 32, bArr, 0, 32);
        return Utils.compareSecure(bArr, SHA256.getHashWithSalt(str.getBytes(StandardCharsets.UTF_8), copyOf));
    }
}
