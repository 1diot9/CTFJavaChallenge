package org.h2.server.web;

import ch.qos.logback.classic.ClassicConstants;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.rolling.helper.IntegerTokenConverter;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.setting.Setting;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import org.h2.bnf.Bnf;
import org.h2.bnf.context.DbColumn;
import org.h2.bnf.context.DbContents;
import org.h2.bnf.context.DbSchema;
import org.h2.bnf.context.DbTableOrView;
import org.h2.command.ParserBase;
import org.h2.engine.Constants;
import org.h2.engine.SysProperties;
import org.h2.jdbc.JdbcException;
import org.h2.message.DbException;
import org.h2.security.SHA256;
import org.h2.store.LobStorageFrontend;
import org.h2.tools.Backup;
import org.h2.tools.ChangeFileEncryption;
import org.h2.tools.ConvertTraceFile;
import org.h2.tools.CreateCluster;
import org.h2.tools.DeleteDbFiles;
import org.h2.tools.Recover;
import org.h2.tools.Restore;
import org.h2.tools.RunScript;
import org.h2.tools.Script;
import org.h2.tools.SimpleResultSet;
import org.h2.util.JdbcUtils;
import org.h2.util.NetUtils;
import org.h2.util.NetworkConnectionInfo;
import org.h2.util.Profiler;
import org.h2.util.ScriptReader;
import org.h2.util.SortedProperties;
import org.h2.util.StringUtils;
import org.h2.util.Tool;
import org.h2.util.Utils;
import org.h2.util.Utils10;
import org.h2.value.DataType;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.cache.interceptor.CacheOperationExpressionEvaluator;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/server/web/WebApp.class */
public class WebApp {
    private static final Comparator<DbTableOrView> SYSTEM_SCHEMA_COMPARATOR = Comparator.comparing((v0) -> {
        return v0.getName();
    }, String.CASE_INSENSITIVE_ORDER);
    protected final WebServer server;
    protected WebSession session;
    protected Properties attributes;
    protected String mimeType;
    protected boolean cache;
    protected boolean stop;
    protected String headerLanguage;
    private Profiler profiler;

    /* JADX INFO: Access modifiers changed from: package-private */
    public WebApp(WebServer webServer) {
        this.server = webServer;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setSession(WebSession webSession, Properties properties) {
        this.session = webSession;
        this.attributes = properties;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String processRequest(String str, NetworkConnectionInfo networkConnectionInfo) {
        String str2;
        int lastIndexOf = str.lastIndexOf(46);
        if (lastIndexOf >= 0) {
            str2 = str.substring(lastIndexOf + 1);
        } else {
            str2 = "";
        }
        if ("ico".equals(str2)) {
            this.mimeType = "image/x-icon";
            this.cache = true;
        } else if (ImgUtil.IMAGE_TYPE_GIF.equals(str2)) {
            this.mimeType = "image/gif";
            this.cache = true;
        } else if ("css".equals(str2)) {
            this.cache = true;
            this.mimeType = "text/css";
        } else if ("html".equals(str2) || "do".equals(str2) || "jsp".equals(str2)) {
            this.cache = false;
            this.mimeType = "text/html";
            if (this.session == null) {
                this.session = this.server.createNewSession(NetUtils.ipToShortForm(null, networkConnectionInfo.getClientAddr(), false).toString());
                if (!"notAllowed.jsp".equals(str)) {
                    str = "index.do";
                }
            }
        } else if ("js".equals(str2)) {
            this.cache = true;
            this.mimeType = "text/javascript";
        } else {
            this.cache = true;
            this.mimeType = "application/octet-stream";
        }
        trace("mimeType=" + this.mimeType);
        trace(str);
        if (str.endsWith(".do")) {
            str = process(str, networkConnectionInfo);
        } else if (str.endsWith(".jsp")) {
            String str3 = str;
            boolean z = -1;
            switch (str3.hashCode()) {
                case -399578700:
                    if (str3.equals("tools.jsp")) {
                        z = true;
                        break;
                    }
                    break;
                case 20728104:
                    if (str3.equals("admin.jsp")) {
                        z = false;
                        break;
                    }
                    break;
            }
            switch (z) {
                case false:
                case true:
                    if (!checkAdmin(str)) {
                        str = process("adminLogin.do", networkConnectionInfo);
                        break;
                    }
                    break;
            }
        }
        return str;
    }

    private static String getComboBox(String[] strArr, String str) {
        StringBuilder sb = new StringBuilder();
        for (String str2 : strArr) {
            sb.append("<option value=\"").append(PageParser.escapeHtmlData(str2)).append('\"');
            if (str2.equals(str)) {
                sb.append(" selected");
            }
            sb.append('>').append(PageParser.escapeHtml(str2)).append("</option>");
        }
        return sb.toString();
    }

    private static String getComboBox(String[][] strArr, String str) {
        StringBuilder sb = new StringBuilder();
        for (String[] strArr2 : strArr) {
            sb.append("<option value=\"").append(PageParser.escapeHtmlData(strArr2[0])).append('\"');
            if (strArr2[0].equals(str)) {
                sb.append(" selected");
            }
            sb.append('>').append(PageParser.escapeHtml(strArr2[1])).append("</option>");
        }
        return sb.toString();
    }

    /* JADX WARN: Failed to find 'out' block for switch in B:5:0x0029. Please report as an issue. */
    private String process(String str, NetworkConnectionInfo networkConnectionInfo) {
        trace("process " + str);
        while (str.endsWith(".do")) {
            String str2 = str;
            boolean z = -1;
            switch (str2.hashCode()) {
                case -1987664716:
                    if (str2.equals("tables.do")) {
                        z = 7;
                        break;
                    }
                    break;
                case -1540145297:
                    if (str2.equals("logout.do")) {
                        z = 2;
                        break;
                    }
                    break;
                case -1495279532:
                    if (str2.equals("adminShutdown.do")) {
                        z = 13;
                        break;
                    }
                    break;
                case -1422503065:
                    if (str2.equals("test.do")) {
                        z = 5;
                        break;
                    }
                    break;
                case -1166701711:
                    if (str2.equals("query.do")) {
                        z = 6;
                        break;
                    }
                    break;
                case -982721154:
                    if (str2.equals("tools.do")) {
                        z = 15;
                        break;
                    }
                    break;
                case -969162870:
                    if (str2.equals("admin.do")) {
                        z = 10;
                        break;
                    }
                    break;
                case -383255278:
                    if (str2.equals("editResult.do")) {
                        z = 8;
                        break;
                    }
                    break;
                case 112461607:
                    if (str2.equals("index.do")) {
                        z = true;
                        break;
                    }
                    break;
                case 174848556:
                    if (str2.equals("settingSave.do")) {
                        z = 4;
                        break;
                    }
                    break;
                case 672372510:
                    if (str2.equals("adminStartTranslate.do")) {
                        z = 12;
                        break;
                    }
                    break;
                case 710659487:
                    if (str2.equals("adminLogin.do")) {
                        z = 16;
                        break;
                    }
                    break;
                case 1223362227:
                    if (str2.equals("autoCompleteList.do")) {
                        z = 14;
                        break;
                    }
                    break;
                case 1553348685:
                    if (str2.equals("adminSave.do")) {
                        z = 11;
                        break;
                    }
                    break;
                case 1653232667:
                    if (str2.equals("getHistory.do")) {
                        z = 9;
                        break;
                    }
                    break;
                case 1669656005:
                    if (str2.equals("settingRemove.do")) {
                        z = 3;
                        break;
                    }
                    break;
                case 2022712624:
                    if (str2.equals("login.do")) {
                        z = false;
                        break;
                    }
                    break;
            }
            switch (z) {
                case false:
                    str = login(networkConnectionInfo);
                    break;
                case true:
                    str = index();
                    break;
                case true:
                    str = logout();
                    break;
                case true:
                    str = settingRemove();
                    break;
                case true:
                    str = settingSave();
                    break;
                case true:
                    str = test(networkConnectionInfo);
                    break;
                case true:
                    str = query();
                    break;
                case true:
                    str = tables();
                    break;
                case true:
                    str = editResult();
                    break;
                case true:
                    str = getHistory();
                    break;
                case true:
                    str = checkAdmin(str) ? admin() : "adminLogin.do";
                    break;
                case true:
                    str = checkAdmin(str) ? adminSave() : "adminLogin.do";
                    break;
                case true:
                    str = checkAdmin(str) ? adminStartTranslate() : "adminLogin.do";
                    break;
                case true:
                    str = checkAdmin(str) ? adminShutdown() : "adminLogin.do";
                    break;
                case true:
                    str = autoCompleteList();
                    break;
                case true:
                    str = checkAdmin(str) ? tools() : "adminLogin.do";
                    break;
                case true:
                    str = adminLogin();
                    break;
                default:
                    str = "error.jsp";
                    break;
            }
        }
        trace("return " + str);
        return str;
    }

    private boolean checkAdmin(String str) {
        Boolean bool = (Boolean) this.session.get("admin");
        if (bool != null && bool.booleanValue()) {
            return true;
        }
        String key = this.server.getKey();
        if (key != null && key.equals(this.session.get("key"))) {
            return true;
        }
        this.session.put("adminBack", str);
        return false;
    }

    private String adminLogin() {
        String property = this.attributes.getProperty("password");
        if (property == null || property.isEmpty() || !this.server.checkAdminPassword(property)) {
            return "adminLogin.jsp";
        }
        String str = (String) this.session.remove("adminBack");
        this.session.put("admin", true);
        return str != null ? str : "admin.do";
    }

    private String autoCompleteList() {
        String join;
        String str = (String) this.attributes.get("query");
        boolean z = false;
        String trim = str.trim();
        if (!trim.isEmpty() && Character.isLowerCase(trim.charAt(0))) {
            z = true;
        }
        try {
            String str2 = str;
            if (str2.endsWith(";")) {
                str2 = str2 + CharSequenceUtil.SPACE;
            }
            ScriptReader scriptReader = new ScriptReader(new StringReader(str2));
            scriptReader.setSkipRemarks(true);
            String str3 = "";
            while (true) {
                String readStatement = scriptReader.readStatement();
                if (readStatement == null) {
                    break;
                }
                str3 = readStatement;
            }
            if (scriptReader.isInsideRemark()) {
                join = scriptReader.isBlockRemark() ? "1#(End Remark)# */\n" : "1#(Newline)#\n";
            } else {
                String str4 = str3;
                while (str4.length() > 0 && str4.charAt(0) <= ' ') {
                    str4 = str4.substring(1);
                }
                String trim2 = str4.trim();
                if (!trim2.isEmpty() && Character.isLowerCase(trim2.charAt(0))) {
                    z = true;
                }
                Bnf bnf = this.session.getBnf();
                if (bnf == null) {
                    return "autoCompleteList.jsp";
                }
                HashMap<String, String> nextTokenList = bnf.getNextTokenList(str4);
                String str5 = "";
                if (str4.length() > 0) {
                    char charAt = str4.charAt(str4.length() - 1);
                    if (!Character.isWhitespace(charAt) && charAt != '.' && charAt >= ' ' && charAt != '\'' && charAt != '\"') {
                        str5 = CharSequenceUtil.SPACE;
                    }
                }
                ArrayList arrayList = new ArrayList(nextTokenList.size());
                for (Map.Entry<String, String> entry : nextTokenList.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    String valueOf = String.valueOf(key.charAt(0));
                    if (Integer.parseInt(valueOf) <= 2) {
                        String substring = key.substring(2);
                        if (Character.isLetter(substring.charAt(0)) && z) {
                            substring = StringUtils.toLowerEnglish(substring);
                            value = StringUtils.toLowerEnglish(value);
                        }
                        if (substring.equals(value) && !".".equals(value)) {
                            value = str5 + value;
                        }
                        arrayList.add(valueOf + "#" + StringUtils.urlEncode(substring).replace('+', ' ') + "#" + StringUtils.urlEncode(value).replace('+', ' '));
                    }
                }
                Collections.sort(arrayList);
                if (str.endsWith(StrPool.LF) || trim.endsWith(";")) {
                    arrayList.add(0, "1#(Newline)#\n");
                }
                join = String.join("|", arrayList);
            }
            this.session.put("autoCompleteList", join);
            return "autoCompleteList.jsp";
        } catch (Throwable th) {
            this.server.traceError(th);
            return "autoCompleteList.jsp";
        }
    }

    private String admin() {
        this.session.put("port", Integer.toString(this.server.getPort()));
        this.session.put("allowOthers", Boolean.toString(this.server.getAllowOthers()));
        this.session.put("webExternalNames", this.server.getExternalNames());
        this.session.put("ssl", String.valueOf(this.server.getSSL()));
        this.session.put("sessions", this.server.getSessions());
        return "admin.jsp";
    }

    private String adminSave() {
        try {
            SortedProperties sortedProperties = new SortedProperties();
            int intValue = Integer.decode((String) this.attributes.get("port")).intValue();
            sortedProperties.setProperty("webPort", Integer.toString(intValue));
            this.server.setPort(intValue);
            boolean parseBoolean = Utils.parseBoolean((String) this.attributes.get("allowOthers"), false, false);
            sortedProperties.setProperty("webAllowOthers", String.valueOf(parseBoolean));
            this.server.setAllowOthers(parseBoolean);
            String str = (String) this.attributes.get("webExternalNames");
            sortedProperties.setProperty("webExternalNames", str);
            this.server.setExternalNames(str);
            boolean parseBoolean2 = Utils.parseBoolean((String) this.attributes.get("ssl"), false, false);
            sortedProperties.setProperty("webSSL", String.valueOf(parseBoolean2));
            this.server.setSSL(parseBoolean2);
            byte[] adminPassword = this.server.getAdminPassword();
            if (adminPassword != null) {
                sortedProperties.setProperty("webAdminPassword", StringUtils.convertBytesToHex(adminPassword));
            }
            this.server.saveProperties(sortedProperties);
        } catch (Exception e) {
            trace(e.toString());
        }
        return admin();
    }

    private String tools() {
        Tool createCluster;
        try {
            String str = (String) this.attributes.get("tool");
            this.session.put("tool", str);
            String[] arraySplit = StringUtils.arraySplit((String) this.attributes.get("args"), ',', false);
            if ("Backup".equals(str)) {
                createCluster = new Backup();
            } else if ("Restore".equals(str)) {
                createCluster = new Restore();
            } else if ("Recover".equals(str)) {
                createCluster = new Recover();
            } else if ("DeleteDbFiles".equals(str)) {
                createCluster = new DeleteDbFiles();
            } else if ("ChangeFileEncryption".equals(str)) {
                createCluster = new ChangeFileEncryption();
            } else if ("Script".equals(str)) {
                createCluster = new Script();
            } else if ("RunScript".equals(str)) {
                createCluster = new RunScript();
            } else if ("ConvertTraceFile".equals(str)) {
                createCluster = new ConvertTraceFile();
            } else if ("CreateCluster".equals(str)) {
                createCluster = new CreateCluster();
            } else {
                throw DbException.getInternalError(str);
            }
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            PrintStream printStream = new PrintStream((OutputStream) byteArrayOutputStream, false, CharsetUtil.UTF_8);
            createCluster.setOut(printStream);
            try {
                createCluster.runTool(arraySplit);
                printStream.flush();
                this.session.put("toolResult", PageParser.escapeHtml(Utils10.byteArrayOutputStreamToString(byteArrayOutputStream, StandardCharsets.UTF_8)));
            } catch (Exception e) {
                this.session.put("toolResult", getStackTrace(0, e, true));
            }
            return "tools.jsp";
        } catch (Exception e2) {
            this.server.traceError(e2);
            return "tools.jsp";
        }
    }

    private String adminStartTranslate() {
        this.session.put("translationFile", this.server.startTranslate((Map) Map.class.cast(this.session.map.get("text"))));
        return "helpTranslate.jsp";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String adminShutdown() {
        this.server.shutdown();
        return "admin.jsp";
    }

    private String index() {
        String[][] strArr = WebServer.LANGUAGES;
        String str = (String) this.attributes.get("language");
        Locale locale = this.session.locale;
        if (str != null) {
            if (locale == null || !StringUtils.toLowerEnglish(locale.getLanguage()).equals(str)) {
                Locale locale2 = new Locale(str, "");
                this.server.readTranslations(this.session, locale2.getLanguage());
                this.session.put("language", str);
                this.session.locale = locale2;
            }
        } else {
            str = (String) this.session.get("language");
        }
        if (str == null) {
            str = this.headerLanguage;
        }
        this.session.put("languageCombo", getComboBox(strArr, str));
        String[] settingNames = this.server.getSettingNames();
        String property = this.attributes.getProperty(Setting.EXT_NAME);
        if (property == null && settingNames.length > 0) {
            property = settingNames[0];
        }
        this.session.put("settingsList", getComboBox(settingNames, property));
        ConnectionInfo setting = this.server.getSetting(property);
        if (setting == null) {
            setting = new ConnectionInfo();
        }
        this.session.put(Setting.EXT_NAME, PageParser.escapeHtmlData(property));
        this.session.put("name", PageParser.escapeHtmlData(property));
        this.session.put("driver", PageParser.escapeHtmlData(setting.driver));
        this.session.put("url", PageParser.escapeHtmlData(setting.url));
        this.session.put(ClassicConstants.USER_MDC_KEY, PageParser.escapeHtmlData(setting.user));
        return "index.jsp";
    }

    private String getHistory() {
        this.session.put("query", PageParser.escapeHtmlData(this.session.getCommand(Integer.parseInt(this.attributes.getProperty("id")))));
        return "query.jsp";
    }

    private static int addColumns(boolean z, DbTableOrView dbTableOrView, StringBuilder sb, int i, boolean z2, StringBuilder sb2) {
        DbColumn[] columns = dbTableOrView.getColumns();
        for (int i2 = 0; columns != null && i2 < columns.length; i2++) {
            DbColumn dbColumn = columns[i2];
            if (sb2.length() > 0) {
                sb2.append(' ');
            }
            sb2.append(dbColumn.getName());
            sb.append("setNode(").append(i).append(z ? ", 1, 1" : ", 2, 2").append(", 'column', '").append(PageParser.escapeJavaScript(dbColumn.getName())).append("', 'javascript:ins(\\'").append(escapeIdentifier(dbColumn.getName())).append("\\')');\n");
            i++;
            if (z && z2) {
                sb.append("setNode(").append(i).append(", 2, 2, 'type', '").append(PageParser.escapeJavaScript(dbColumn.getDataType())).append("', null);\n");
                i++;
            }
        }
        return i;
    }

    private static String escapeIdentifier(String str) {
        return StringUtils.urlEncode(PageParser.escapeJavaScript(str)).replace('+', ' ');
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/server/web/WebApp$IndexInfo.class */
    public static class IndexInfo {
        String name;
        String type;
        String columns;

        IndexInfo() {
        }
    }

    private static int addIndexes(boolean z, DatabaseMetaData databaseMetaData, String str, String str2, StringBuilder sb, int i) throws SQLException {
        String str3;
        try {
            ResultSet indexInfo = databaseMetaData.getIndexInfo(null, str2, str, false, true);
            HashMap hashMap = new HashMap();
            while (indexInfo.next()) {
                String string = indexInfo.getString("INDEX_NAME");
                IndexInfo indexInfo2 = (IndexInfo) hashMap.get(string);
                if (indexInfo2 == null) {
                    int i2 = indexInfo.getInt("TYPE");
                    if (i2 == 1) {
                        str3 = "";
                    } else if (i2 == 2) {
                        str3 = " (${text.tree.hashed})";
                    } else if (i2 == 3) {
                        str3 = "";
                    } else {
                        str3 = null;
                    }
                    if (string != null && str3 != null) {
                        IndexInfo indexInfo3 = new IndexInfo();
                        indexInfo3.name = string;
                        indexInfo3.type = (indexInfo.getBoolean("NON_UNIQUE") ? "${text.tree.nonUnique}" : "${text.tree.unique}") + str3;
                        indexInfo3.columns = indexInfo.getString("COLUMN_NAME");
                        hashMap.put(string, indexInfo3);
                    }
                } else {
                    indexInfo2.columns += ", " + indexInfo.getString("COLUMN_NAME");
                }
            }
            indexInfo.close();
            if (hashMap.size() > 0) {
                String str4 = z ? ", 1, 1" : ", 2, 1";
                String str5 = z ? ", 2, 1" : ", 3, 1";
                String str6 = z ? ", 3, 2" : ", 4, 2";
                sb.append("setNode(").append(i).append(str4).append(", 'index_az', '${text.tree.indexes}', null);\n");
                i++;
                for (IndexInfo indexInfo4 : hashMap.values()) {
                    sb.append("setNode(").append(i).append(str5).append(", 'index', '").append(PageParser.escapeJavaScript(indexInfo4.name)).append("', null);\n");
                    int i3 = i + 1;
                    sb.append("setNode(").append(i3).append(str6).append(", 'type', '").append(indexInfo4.type).append("', null);\n");
                    int i4 = i3 + 1;
                    sb.append("setNode(").append(i4).append(str6).append(", 'type', '").append(PageParser.escapeJavaScript(indexInfo4.columns)).append("', null);\n");
                    i = i4 + 1;
                }
            }
            return i;
        } catch (SQLException e) {
            return i;
        }
    }

    private int addTablesAndViews(DbSchema dbSchema, boolean z, StringBuilder sb, int i) throws SQLException {
        if (dbSchema == null) {
            return i;
        }
        Connection connection = this.session.getConnection();
        DatabaseMetaData metaData = this.session.getMetaData();
        int i2 = z ? 0 : 1;
        boolean z2 = z || !dbSchema.isSystem;
        String str = ", " + i2 + ", " + (z2 ? CustomBooleanEditor.VALUE_1 : "2") + ", ";
        String str2 = ", " + (i2 + 1) + ", 2, ";
        DbTableOrView[] tables = dbSchema.getTables();
        if (tables == null) {
            return i;
        }
        DbContents contents = dbSchema.getContents();
        boolean isOracle = contents.isOracle();
        boolean z3 = tables.length < SysProperties.CONSOLE_MAX_TABLES_LIST_INDEXES;
        PreparedStatement prepareViewDefinitionQuery = z2 ? prepareViewDefinitionQuery(connection, contents) : null;
        Throwable th = null;
        try {
            if (prepareViewDefinitionQuery != null) {
                prepareViewDefinitionQuery.setString(1, dbSchema.name);
            }
            AtomicReference atomicReference = new AtomicReference(prepareViewDefinitionQuery);
            if (dbSchema.isSystem) {
                Arrays.sort(tables, SYSTEM_SCHEMA_COMPARATOR);
                for (DbTableOrView dbTableOrView : tables) {
                    i = addTableOrView(dbSchema, z, sb, i, metaData, false, str, isOracle, z3, dbTableOrView, dbTableOrView.isView(), atomicReference, str2);
                }
            } else {
                for (DbTableOrView dbTableOrView2 : tables) {
                    if (!dbTableOrView2.isView()) {
                        i = addTableOrView(dbSchema, z, sb, i, metaData, z2, str, isOracle, z3, dbTableOrView2, false, null, str2);
                    }
                }
                for (DbTableOrView dbTableOrView3 : tables) {
                    if (dbTableOrView3.isView()) {
                        i = addTableOrView(dbSchema, z, sb, i, metaData, z2, str, isOracle, z3, dbTableOrView3, true, atomicReference, str2);
                    }
                }
            }
            return i;
        } finally {
            if (prepareViewDefinitionQuery != null) {
                if (0 != 0) {
                    try {
                        prepareViewDefinitionQuery.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                } else {
                    prepareViewDefinitionQuery.close();
                }
            }
        }
    }

    private static PreparedStatement prepareViewDefinitionQuery(Connection connection, DbContents dbContents) {
        if (dbContents.mayHaveStandardViews()) {
            try {
                return connection.prepareStatement("SELECT VIEW_DEFINITION FROM INFORMATION_SCHEMA.VIEWS WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ?");
            } catch (SQLException e) {
                dbContents.setMayHaveStandardViews(false);
                return null;
            }
        }
        return null;
    }

    private static int addTableOrView(DbSchema dbSchema, boolean z, StringBuilder sb, int i, DatabaseMetaData databaseMetaData, boolean z2, String str, boolean z3, boolean z4, DbTableOrView dbTableOrView, boolean z5, AtomicReference<PreparedStatement> atomicReference, String str2) throws SQLException {
        String string;
        String quotedName = dbTableOrView.getQuotedName();
        if (!z) {
            quotedName = dbSchema.quotedName + '.' + quotedName;
        }
        sb.append("setNode(").append(i).append(str).append(" '").append(z5 ? "view" : "table").append("', '").append(PageParser.escapeJavaScript(dbTableOrView.getName())).append("', 'javascript:ins(\\'").append(escapeIdentifier(quotedName)).append("\\',true)');\n");
        int i2 = i + 1;
        if (z2) {
            StringBuilder sb2 = new StringBuilder();
            i2 = addColumns(z, dbTableOrView, sb, i2, z4, sb2);
            if (z5) {
                PreparedStatement preparedStatement = atomicReference.get();
                if (preparedStatement != null) {
                    preparedStatement.setString(2, dbTableOrView.getName());
                    try {
                        ResultSet executeQuery = preparedStatement.executeQuery();
                        Throwable th = null;
                        try {
                            try {
                                if (executeQuery.next() && (string = executeQuery.getString(1)) != null) {
                                    sb.append("setNode(").append(i2).append(str2).append(" 'type', '").append(PageParser.escapeJavaScript(string)).append("', null);\n");
                                    i2++;
                                }
                                if (executeQuery != null) {
                                    if (0 != 0) {
                                        try {
                                            executeQuery.close();
                                        } catch (Throwable th2) {
                                            th.addSuppressed(th2);
                                        }
                                    } else {
                                        executeQuery.close();
                                    }
                                }
                            } finally {
                            }
                        } finally {
                        }
                    } catch (SQLException e) {
                        atomicReference.set(null);
                    }
                }
            } else if (!z3 && z4) {
                i2 = addIndexes(z, databaseMetaData, dbTableOrView.getName(), dbSchema.name, sb, i2);
            }
            sb.append("addTable('").append(PageParser.escapeJavaScript(dbTableOrView.getName())).append("', '").append(PageParser.escapeJavaScript(sb2.toString())).append("', ").append(i).append(");\n");
        }
        return i2;
    }

    private String tables() {
        ResultSet executeQuery;
        ResultSet executeQuery2;
        DbContents contents = this.session.getContents();
        boolean z = false;
        try {
            String str = (String) this.session.get("url");
            Connection connection = this.session.getConnection();
            contents.readContents(str, connection);
            this.session.loadBnf();
            z = contents.isH2();
            StringBuilder append = new StringBuilder().append("setNode(0, 0, 0, 'database', '").append(PageParser.escapeJavaScript(str)).append("', null);\n");
            DbSchema defaultSchema = contents.getDefaultSchema();
            int addTablesAndViews = addTablesAndViews(defaultSchema, true, append, 1);
            for (DbSchema dbSchema : contents.getSchemas()) {
                if (dbSchema != defaultSchema && dbSchema != null) {
                    append.append("setNode(").append(addTablesAndViews).append(", 0, 1, 'folder', '").append(PageParser.escapeJavaScript(dbSchema.name)).append("', null);\n");
                    addTablesAndViews = addTablesAndViews(dbSchema, false, append, addTablesAndViews + 1);
                }
            }
            if (z) {
                Statement createStatement = connection.createStatement();
                Throwable th = null;
                try {
                    try {
                        try {
                            executeQuery = createStatement.executeQuery("SELECT SEQUENCE_NAME, BASE_VALUE, INCREMENT FROM INFORMATION_SCHEMA.SEQUENCES ORDER BY SEQUENCE_NAME");
                        } finally {
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        throw th2;
                    }
                } catch (SQLException e) {
                    executeQuery = createStatement.executeQuery("SELECT SEQUENCE_NAME, CURRENT_VALUE, INCREMENT FROM INFORMATION_SCHEMA.SEQUENCES ORDER BY SEQUENCE_NAME");
                }
                int i = 0;
                while (executeQuery.next()) {
                    if (i == 0) {
                        append.append("setNode(").append(addTablesAndViews).append(", 0, 1, 'sequences', '${text.tree.sequences}', null);\n");
                        addTablesAndViews++;
                    }
                    String string = executeQuery.getString(1);
                    String string2 = executeQuery.getString(2);
                    String string3 = executeQuery.getString(3);
                    append.append("setNode(").append(addTablesAndViews).append(", 1, 1, 'sequence', '").append(PageParser.escapeJavaScript(string)).append("', null);\n");
                    int i2 = addTablesAndViews + 1;
                    append.append("setNode(").append(i2).append(", 2, 2, 'type', '${text.tree.current}: ").append(PageParser.escapeJavaScript(string2)).append("', null);\n");
                    addTablesAndViews = i2 + 1;
                    if (!CustomBooleanEditor.VALUE_1.equals(string3)) {
                        append.append("setNode(").append(addTablesAndViews).append(", 2, 2, 'type', '${text.tree.increment}: ").append(PageParser.escapeJavaScript(string3)).append("', null);\n");
                        addTablesAndViews++;
                    }
                    i++;
                }
                executeQuery.close();
                try {
                    executeQuery2 = createStatement.executeQuery("SELECT USER_NAME, IS_ADMIN FROM INFORMATION_SCHEMA.USERS ORDER BY USER_NAME");
                } catch (SQLException e2) {
                    executeQuery2 = createStatement.executeQuery("SELECT NAME, ADMIN FROM INFORMATION_SCHEMA.USERS ORDER BY NAME");
                }
                int i3 = 0;
                while (executeQuery2.next()) {
                    if (i3 == 0) {
                        append.append("setNode(").append(addTablesAndViews).append(", 0, 1, 'users', '${text.tree.users}', null);\n");
                        addTablesAndViews++;
                    }
                    String string4 = executeQuery2.getString(1);
                    String string5 = executeQuery2.getString(2);
                    append.append("setNode(").append(addTablesAndViews).append(", 1, 1, 'user', '").append(PageParser.escapeJavaScript(string4)).append("', null);\n");
                    addTablesAndViews++;
                    if (string5.equalsIgnoreCase(Constants.CLUSTERING_ENABLED)) {
                        append.append("setNode(").append(addTablesAndViews).append(", 2, 2, 'type', '${text.tree.admin}', null);\n");
                        addTablesAndViews++;
                    }
                    i3++;
                }
                executeQuery2.close();
                if (createStatement != null) {
                    if (0 != 0) {
                        try {
                            createStatement.close();
                        } catch (Throwable th3) {
                            th.addSuppressed(th3);
                        }
                    } else {
                        createStatement.close();
                    }
                }
            }
            DatabaseMetaData metaData = this.session.getMetaData();
            append.append("setNode(").append(addTablesAndViews).append(", 0, 0, 'info', '").append(PageParser.escapeJavaScript(metaData.getDatabaseProductName() + CharSequenceUtil.SPACE + metaData.getDatabaseProductVersion())).append("', null);\n").append("refreshQueryTables();");
            this.session.put("tree", append.toString());
            return "tables.jsp";
        } catch (Exception e3) {
            this.session.put("tree", "");
            this.session.put("error", getStackTrace(0, e3, z));
            return "tables.jsp";
        }
    }

    private String getStackTrace(int i, Throwable th, boolean z) {
        try {
            StringWriter stringWriter = new StringWriter();
            th.printStackTrace(new PrintWriter(stringWriter));
            String escapeHtml = PageParser.escapeHtml(stringWriter.toString());
            if (z) {
                escapeHtml = linkToSource(escapeHtml);
            }
            String replaceAll = StringUtils.replaceAll(escapeHtml, "\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
            String str = "<a class=\"error\" href=\"#\" onclick=\"var x=document.getElementById('st" + i + "').style;x.display=x.display==''?'none':'';\">" + PageParser.escapeHtml(th.getMessage()) + "</a>";
            if (th instanceof SQLException) {
                SQLException sQLException = (SQLException) th;
                str = str + CharSequenceUtil.SPACE + sQLException.getSQLState() + "/" + sQLException.getErrorCode();
                if (z) {
                    str = str + " <a href=\"https://h2database.com/javadoc/org/h2/api/ErrorCode.html#c" + sQLException.getErrorCode() + "\">(${text.a.help})</a>";
                }
            }
            return formatAsError(str + "<span style=\"display: none;\" id=\"st" + i + "\"><br />" + replaceAll + "</span>");
        } catch (OutOfMemoryError e) {
            this.server.traceError(th);
            return th.toString();
        }
    }

    private static String linkToSource(String str) {
        try {
            StringBuilder sb = new StringBuilder(str.length());
            int indexOf = str.indexOf("<br />");
            sb.append((CharSequence) str, 0, indexOf);
            while (true) {
                int indexOf2 = str.indexOf("org.h2.", indexOf);
                if (indexOf2 < 0) {
                    sb.append(str.substring(indexOf));
                    break;
                }
                sb.append((CharSequence) str, indexOf, indexOf2);
                int indexOf3 = str.indexOf(41, indexOf2);
                if (indexOf3 < 0) {
                    sb.append(str.substring(indexOf));
                    break;
                }
                String substring = str.substring(indexOf2, indexOf3);
                int lastIndexOf = substring.lastIndexOf(40);
                String substring2 = substring.substring(0, substring.lastIndexOf(46, substring.lastIndexOf(46, lastIndexOf - 1) - 1));
                int lastIndexOf2 = substring.lastIndexOf(58);
                String substring3 = substring.substring(lastIndexOf + 1, lastIndexOf2);
                String substring4 = substring.substring(lastIndexOf2 + 1, substring.length());
                String str2 = substring2.replace('.', '/') + "/" + substring3;
                sb.append("<a href=\"https://h2database.com/html/source.html?file=");
                sb.append(str2);
                sb.append("&line=");
                sb.append(substring4);
                sb.append("&build=");
                sb.append(224);
                sb.append("\">");
                sb.append(substring);
                sb.append("</a>");
                indexOf = indexOf3;
            }
            return sb.toString();
        } catch (Throwable th) {
            return str;
        }
    }

    private static String formatAsError(String str) {
        return "<div class=\"error\">" + str + "</div>";
    }

    private String test(NetworkConnectionInfo networkConnectionInfo) {
        String str;
        String property = this.attributes.getProperty("driver", "");
        String property2 = this.attributes.getProperty("url", "");
        String property3 = this.attributes.getProperty(ClassicConstants.USER_MDC_KEY, "");
        String property4 = this.attributes.getProperty("password", "");
        this.session.put("driver", property);
        this.session.put("url", property2);
        this.session.put(ClassicConstants.USER_MDC_KEY, property3);
        boolean startsWith = property2.startsWith(Constants.START_URL);
        try {
            long currentTimeMillis = System.currentTimeMillis();
            Profiler profiler = new Profiler();
            profiler.startCollecting();
            try {
                Connection connection = this.server.getConnection(property, property2, property3, property4, null, networkConnectionInfo);
                profiler.stopCollecting();
                String top = profiler.getTop(3);
                profiler = new Profiler();
                profiler.startCollecting();
                try {
                    JdbcUtils.closeSilently(connection);
                    profiler.stopCollecting();
                    String top2 = profiler.getTop(3);
                    if (System.currentTimeMillis() - currentTimeMillis > 1000) {
                        str = "<a class=\"error\" href=\"#\" onclick=\"var x=document.getElementById('prof').style;x.display=x.display==''?'none':'';\">${text.login.testSuccessful}</a><span style=\"display: none;\" id=\"prof\"><br />" + PageParser.escapeHtml(top) + "<br />" + PageParser.escapeHtml(top2) + "</span>";
                    } else {
                        str = "<div class=\"success\">${text.login.testSuccessful}</div>";
                    }
                    this.session.put("error", str);
                    return "login.jsp";
                } finally {
                }
            } finally {
            }
        } catch (Exception e) {
            this.session.put("error", getLoginError(e, startsWith));
            return "login.jsp";
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    private String getLoginError(Exception exc, boolean z) {
        if ((exc instanceof JdbcException) && ((JdbcException) exc).getErrorCode() == 90086) {
            return "${text.login.driverNotFound}<br />" + getStackTrace(0, exc, z);
        }
        return getStackTrace(0, exc, z);
    }

    private String login(NetworkConnectionInfo networkConnectionInfo) {
        String property = this.attributes.getProperty("driver", "");
        String property2 = this.attributes.getProperty("url", "");
        String property3 = this.attributes.getProperty(ClassicConstants.USER_MDC_KEY, "");
        String property4 = this.attributes.getProperty("password", "");
        this.session.put("autoCommit", "checked");
        this.session.put("autoComplete", CustomBooleanEditor.VALUE_1);
        this.session.put("maxrows", "1000");
        boolean startsWith = property2.startsWith(Constants.START_URL);
        try {
            this.session.setConnection(this.server.getConnection(property, property2, property3, property4, (String) this.session.get("key"), networkConnectionInfo));
            this.session.put("url", property2);
            this.session.put(ClassicConstants.USER_MDC_KEY, property3);
            this.session.remove("error");
            settingSave();
            return "frame.jsp";
        } catch (Exception e) {
            this.session.put("error", getLoginError(e, startsWith));
            return "login.jsp";
        }
    }

    private String logout() {
        try {
            Connection connection = this.session.getConnection();
            this.session.setConnection(null);
            this.session.remove("conn");
            this.session.remove(CacheOperationExpressionEvaluator.RESULT_VARIABLE);
            this.session.remove("tables");
            this.session.remove(ClassicConstants.USER_MDC_KEY);
            this.session.remove("tool");
            if (connection != null) {
                if (this.session.getShutdownServerOnDisconnect()) {
                    this.server.shutdown();
                } else {
                    connection.close();
                }
            }
        } catch (Exception e) {
            trace(e.toString());
        }
        this.session.remove("admin");
        return "index.do";
    }

    private String query() {
        try {
            ScriptReader scriptReader = new ScriptReader(new StringReader(this.attributes.getProperty("sql").trim()));
            final ArrayList arrayList = new ArrayList();
            while (true) {
                String readStatement = scriptReader.readStatement();
                if (readStatement == null) {
                    break;
                }
                arrayList.add(readStatement);
            }
            final Connection connection = this.session.getConnection();
            if (SysProperties.CONSOLE_STREAM && this.server.getAllowChunked()) {
                String str = new String(this.server.getFile("result.jsp"), StandardCharsets.UTF_8);
                int indexOf = str.indexOf("${result}");
                arrayList.add(0, str.substring(0, indexOf));
                arrayList.add(str.substring(indexOf + "${result}".length()));
                this.session.put("chunks", new Iterator<String>() { // from class: org.h2.server.web.WebApp.1
                    private int i;

                    @Override // java.util.Iterator
                    public boolean hasNext() {
                        return this.i < arrayList.size();
                    }

                    /* JADX WARN: Can't rename method to resolve collision */
                    @Override // java.util.Iterator
                    public String next() {
                        ArrayList arrayList2 = arrayList;
                        int i = this.i;
                        this.i = i + 1;
                        String str2 = (String) arrayList2.get(i);
                        if (this.i == 1 || this.i == arrayList.size()) {
                            return str2;
                        }
                        StringBuilder sb = new StringBuilder();
                        WebApp.this.query(connection, str2, this.i - 1, arrayList.size() - 2, sb);
                        return sb.toString();
                    }
                });
                return "result.jsp";
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < arrayList.size(); i++) {
                query(connection, (String) arrayList.get(i), i, arrayList.size(), sb);
            }
            this.session.put(CacheOperationExpressionEvaluator.RESULT_VARIABLE, sb.toString());
            return "result.jsp";
        } catch (Throwable th) {
            this.session.put(CacheOperationExpressionEvaluator.RESULT_VARIABLE, getStackTrace(0, th, this.session.getContents().isH2()));
            return "result.jsp";
        }
    }

    void query(Connection connection, String str, int i, int i2, StringBuilder sb) {
        if (!str.startsWith(StrPool.AT) || !str.endsWith(".")) {
            sb.append(PageParser.escapeHtml(str + ";")).append("<br />");
        }
        sb.append(getResult(connection, i + 1, str, i2 == 1, str.startsWith("@edit"))).append("<br />");
    }

    private String editResult() {
        ResultSet resultSet = this.session.result;
        int parseInt = Integer.parseInt(this.attributes.getProperty("row"));
        int parseInt2 = Integer.parseInt(this.attributes.getProperty("op"));
        String str = "";
        String str2 = "";
        try {
            if (parseInt2 == 1) {
                boolean z = parseInt < 0;
                if (z) {
                    resultSet.moveToInsertRow();
                } else {
                    resultSet.absolute(parseInt);
                }
                for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
                    unescapeData(this.attributes.getProperty("r" + parseInt + "c" + (i + 1)), resultSet, i + 1);
                }
                if (z) {
                    resultSet.insertRow();
                } else {
                    resultSet.updateRow();
                }
            } else if (parseInt2 == 2) {
                resultSet.absolute(parseInt);
                resultSet.deleteRow();
            } else if (parseInt2 == 3) {
            }
        } catch (Throwable th) {
            str = "<br />" + getStackTrace(0, th, this.session.getContents().isH2());
            str2 = formatAsError(th.getMessage());
        }
        this.session.put(CacheOperationExpressionEvaluator.RESULT_VARIABLE, str2 + getResult(this.session.getConnection(), -1, "@edit " + ((String) this.session.get("resultSetSQL")), true, true) + str);
        return "result.jsp";
    }

    private int getMaxrows() {
        String str = (String) this.session.get("maxrows");
        if (str == null) {
            return 0;
        }
        return Integer.parseInt(str);
    }

    private String getResult(Connection connection, int i, String str, boolean z, boolean z2) {
        boolean execute;
        long updateCount;
        ResultSet resultSet;
        try {
            try {
                String trim = str.trim();
                StringBuilder sb = new StringBuilder();
                String upperEnglish = StringUtils.toUpperEnglish(trim);
                if (upperEnglish.contains("CREATE") || upperEnglish.contains("DROP") || upperEnglish.contains("ALTER") || upperEnglish.contains("RUNSCRIPT")) {
                    sb.append("<script type=\"text/javascript\">parent['h2menu'].location='tables.do?jsessionid=").append(this.attributes.getProperty("jsessionid")).append("';</script>");
                }
                Statement createStatement = (z2 || (z && this.session.getContents().isH2())) ? connection.createStatement(1004, 1008) : connection.createStatement();
                long currentTimeMillis = System.currentTimeMillis();
                boolean z3 = false;
                Object obj = null;
                boolean z4 = false;
                boolean z5 = false;
                if (JdbcUtils.isBuiltIn(trim, "@autocommit_true")) {
                    connection.setAutoCommit(true);
                    this.session.executingStatement = null;
                    return "${text.result.autoCommitOn}";
                }
                if (JdbcUtils.isBuiltIn(trim, "@autocommit_false")) {
                    connection.setAutoCommit(false);
                    this.session.executingStatement = null;
                    return "${text.result.autoCommitOff}";
                }
                if (JdbcUtils.isBuiltIn(trim, "@cancel")) {
                    Statement statement = this.session.executingStatement;
                    if (statement != null) {
                        statement.cancel();
                        sb.append("${text.result.statementWasCanceled}");
                    } else {
                        sb.append("${text.result.noRunningStatement}");
                    }
                    String sb2 = sb.toString();
                    this.session.executingStatement = null;
                    return sb2;
                }
                if (JdbcUtils.isBuiltIn(trim, "@edit")) {
                    z4 = true;
                    trim = StringUtils.trimSubstring(trim, "@edit".length());
                    this.session.put("resultSetSQL", trim);
                }
                if (JdbcUtils.isBuiltIn(trim, "@list")) {
                    z5 = true;
                    trim = StringUtils.trimSubstring(trim, "@list".length());
                }
                if (JdbcUtils.isBuiltIn(trim, "@meta")) {
                    z3 = true;
                    trim = StringUtils.trimSubstring(trim, "@meta".length());
                }
                if (JdbcUtils.isBuiltIn(trim, "@generated")) {
                    obj = true;
                    int length = "@generated".length();
                    int length2 = trim.length();
                    while (true) {
                        if (length >= length2) {
                            break;
                        }
                        char charAt = trim.charAt(length);
                        if (charAt == '(') {
                            ParserBase parserBase = new ParserBase();
                            obj = parserBase.parseColumnList(trim, length);
                            length = parserBase.getLastParseIndex();
                            break;
                        }
                        if (!Character.isWhitespace(charAt)) {
                            break;
                        }
                        length++;
                    }
                    trim = StringUtils.trimSubstring(trim, length);
                } else {
                    if (JdbcUtils.isBuiltIn(trim, "@history")) {
                        sb.append(getCommandHistoryString());
                        String sb3 = sb.toString();
                        this.session.executingStatement = null;
                        return sb3;
                    }
                    if (JdbcUtils.isBuiltIn(trim, "@loop")) {
                        String trimSubstring = StringUtils.trimSubstring(trim, "@loop".length());
                        int indexOf = trimSubstring.indexOf(32);
                        String executeLoop = executeLoop(connection, Integer.decode(trimSubstring.substring(0, indexOf)).intValue(), StringUtils.trimSubstring(trimSubstring, indexOf));
                        this.session.executingStatement = null;
                        return executeLoop;
                    }
                    if (JdbcUtils.isBuiltIn(trim, "@maxrows")) {
                        this.session.put("maxrows", Integer.toString((int) Double.parseDouble(StringUtils.trimSubstring(trim, "@maxrows".length()))));
                        this.session.executingStatement = null;
                        return "${text.result.maxrowsSet}";
                    }
                    if (JdbcUtils.isBuiltIn(trim, "@parameter_meta")) {
                        sb.append(getParameterResultSet(connection.prepareStatement(StringUtils.trimSubstring(trim, "@parameter_meta".length())).getParameterMetaData()));
                        String sb4 = sb.toString();
                        this.session.executingStatement = null;
                        return sb4;
                    }
                    if (JdbcUtils.isBuiltIn(trim, "@password_hash")) {
                        String[] split = JdbcUtils.split(StringUtils.trimSubstring(trim, "@password_hash".length()));
                        String convertBytesToHex = StringUtils.convertBytesToHex(SHA256.getKeyPasswordHash(split[0], split[1].toCharArray()));
                        this.session.executingStatement = null;
                        return convertBytesToHex;
                    }
                    if (JdbcUtils.isBuiltIn(trim, "@prof_start")) {
                        if (this.profiler != null) {
                            this.profiler.stopCollecting();
                        }
                        this.profiler = new Profiler();
                        this.profiler.startCollecting();
                        this.session.executingStatement = null;
                        return "Ok";
                    }
                    if (JdbcUtils.isBuiltIn(trim, "@sleep")) {
                        String trimSubstring2 = StringUtils.trimSubstring(trim, "@sleep".length());
                        int i2 = 1;
                        if (trimSubstring2.length() > 0) {
                            i2 = Integer.parseInt(trimSubstring2);
                        }
                        Thread.sleep(i2 * 1000);
                        this.session.executingStatement = null;
                        return "Ok";
                    }
                    if (JdbcUtils.isBuiltIn(trim, "@transaction_isolation")) {
                        String trimSubstring3 = StringUtils.trimSubstring(trim, "@transaction_isolation".length());
                        if (trimSubstring3.length() > 0) {
                            connection.setTransactionIsolation(Integer.parseInt(trimSubstring3));
                        }
                        sb.append("Transaction Isolation: ").append(connection.getTransactionIsolation()).append("<br />");
                        sb.append(1).append(": read_uncommitted<br />");
                        sb.append(2).append(": read_committed<br />");
                        sb.append(4).append(": repeatable_read<br />");
                        sb.append(6).append(": snapshot<br />");
                        sb.append(8).append(": serializable");
                    }
                }
                if (trim.startsWith(StrPool.AT)) {
                    resultSet = JdbcUtils.getMetaResultSet(connection, trim);
                    if (resultSet == null && JdbcUtils.isBuiltIn(trim, "@prof_stop") && this.profiler != null) {
                        this.profiler.stopCollecting();
                        SimpleResultSet simpleResultSet = new SimpleResultSet();
                        simpleResultSet.addColumn("Top Stack Trace(s)", 12, 0, 0);
                        simpleResultSet.addRow(this.profiler.getTop(3));
                        resultSet = simpleResultSet;
                        this.profiler = null;
                    }
                    if (resultSet == null) {
                        sb.append("?: ").append(trim);
                        String sb5 = sb.toString();
                        this.session.executingStatement = null;
                        return sb5;
                    }
                } else {
                    createStatement.setMaxRows(getMaxrows());
                    this.session.executingStatement = createStatement;
                    if (obj == null) {
                        execute = createStatement.execute(trim);
                    } else if (obj instanceof Boolean) {
                        execute = createStatement.execute(trim, ((Boolean) obj).booleanValue() ? 1 : 2);
                    } else {
                        execute = obj instanceof String[] ? createStatement.execute(trim, (String[]) obj) : createStatement.execute(trim, (int[]) obj);
                    }
                    this.session.addCommand(trim);
                    if (obj != null) {
                        resultSet = createStatement.getGeneratedKeys();
                    } else {
                        if (!execute) {
                            try {
                                updateCount = createStatement.getLargeUpdateCount();
                            } catch (UnsupportedOperationException e) {
                                updateCount = createStatement.getUpdateCount();
                            }
                            sb.append("${text.result.updateCount}: ").append(updateCount);
                            sb.append("<br />(").append(System.currentTimeMillis() - currentTimeMillis).append(" ms)");
                            createStatement.close();
                            String sb6 = sb.toString();
                            this.session.executingStatement = null;
                            return sb6;
                        }
                        resultSet = createStatement.getResultSet();
                    }
                }
                sb.append(getResultSet(trim, resultSet, z3, z5, z4, System.currentTimeMillis() - currentTimeMillis, z));
                if (!z4) {
                    createStatement.close();
                }
                String sb7 = sb.toString();
                this.session.executingStatement = null;
                return sb7;
            } catch (Throwable th) {
                String stackTrace = getStackTrace(i, th, this.session.getContents().isH2());
                this.session.executingStatement = null;
                return stackTrace;
            }
        } catch (Throwable th2) {
            this.session.executingStatement = null;
            throw th2;
        }
    }

    private String executeLoop(Connection connection, int i, String str) throws SQLException {
        boolean z;
        ArrayList arrayList = new ArrayList();
        int i2 = 0;
        while (!this.stop) {
            int indexOf = str.indexOf(63, i2);
            if (indexOf < 0) {
                break;
            }
            if (JdbcUtils.isBuiltIn(str.substring(indexOf), "?/*rnd*/")) {
                arrayList.add(1);
                str = str.substring(0, indexOf) + CoreConstants.NA + str.substring(indexOf + "/*rnd*/".length() + 1);
            } else {
                arrayList.add(0);
            }
            i2 = indexOf + 1;
        }
        Random random = new Random(1L);
        long currentTimeMillis = System.currentTimeMillis();
        if (JdbcUtils.isBuiltIn(str, "@statement")) {
            str = StringUtils.trimSubstring(str, "@statement".length());
            z = false;
            Statement createStatement = connection.createStatement();
            for (int i3 = 0; !this.stop && i3 < i; i3++) {
                String str2 = str;
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    Integer num = (Integer) it.next();
                    int indexOf2 = str2.indexOf(63);
                    if (num.intValue() == 1) {
                        str2 = str2.substring(0, indexOf2) + random.nextInt(i) + str2.substring(indexOf2 + 1);
                    } else {
                        str2 = str2.substring(0, indexOf2) + i3 + str2.substring(indexOf2 + 1);
                    }
                }
                if (createStatement.execute(str2)) {
                    ResultSet resultSet = createStatement.getResultSet();
                    while (!this.stop && resultSet.next()) {
                    }
                    resultSet.close();
                }
            }
        } else {
            z = true;
            PreparedStatement prepareStatement = connection.prepareStatement(str);
            for (int i4 = 0; !this.stop && i4 < i; i4++) {
                for (int i5 = 0; i5 < arrayList.size(); i5++) {
                    if (((Integer) arrayList.get(i5)).intValue() == 1) {
                        prepareStatement.setInt(i5 + 1, random.nextInt(i));
                    } else {
                        prepareStatement.setInt(i5 + 1, i4);
                    }
                }
                if (this.session.getContents().isSQLite()) {
                    prepareStatement.executeUpdate();
                } else if (prepareStatement.execute()) {
                    ResultSet resultSet2 = prepareStatement.getResultSet();
                    while (!this.stop && resultSet2.next()) {
                    }
                    resultSet2.close();
                }
            }
        }
        StringBuilder append = new StringBuilder().append(System.currentTimeMillis() - currentTimeMillis).append(" ms: ").append(i).append(" * ").append(z ? "(Prepared) " : "(Statement) ").append('(');
        int size = arrayList.size();
        for (int i6 = 0; i6 < size; i6++) {
            if (i6 > 0) {
                append.append(", ");
            }
            append.append(((Integer) arrayList.get(i6)).intValue() == 0 ? IntegerTokenConverter.CONVERTER_KEY : "rnd");
        }
        return append.append(") ").append(str).toString();
    }

    private String getCommandHistoryString() {
        StringBuilder sb = new StringBuilder();
        ArrayList<String> commandHistory = this.session.getCommandHistory();
        sb.append("<table cellspacing=0 cellpadding=0><tr><th></th><th>Command</th></tr>");
        for (int size = commandHistory.size() - 1; size >= 0; size--) {
            sb.append("<tr><td><a href=\"getHistory.do?id=").append(size).append("&jsessionid=${sessionId}\" target=\"h2query\" >").append("<img width=16 height=16 src=\"ico_write.gif\" onmouseover = \"this.className ='icon_hover'\" ").append("onmouseout = \"this.className ='icon'\" class=\"icon\" alt=\"${text.resultEdit.edit}\" ").append("title=\"${text.resultEdit.edit}\" border=\"1\"/></a>").append("</td><td>").append(PageParser.escapeHtml(commandHistory.get(size))).append("</td></tr>");
        }
        sb.append("</table>");
        return sb.toString();
    }

    private static String getParameterResultSet(ParameterMetaData parameterMetaData) throws SQLException {
        StringBuilder sb = new StringBuilder();
        if (parameterMetaData == null) {
            return "No parameter meta data";
        }
        sb.append("<table cellspacing=0 cellpadding=0>").append("<tr><th>className</th><th>mode</th><th>type</th>").append("<th>typeName</th><th>precision</th><th>scale</th></tr>");
        for (int i = 0; i < parameterMetaData.getParameterCount(); i++) {
            sb.append("</tr><td>").append(parameterMetaData.getParameterClassName(i + 1)).append("</td><td>").append(parameterMetaData.getParameterMode(i + 1)).append("</td><td>").append(parameterMetaData.getParameterType(i + 1)).append("</td><td>").append(parameterMetaData.getParameterTypeName(i + 1)).append("</td><td>").append(parameterMetaData.getPrecision(i + 1)).append("</td><td>").append(parameterMetaData.getScale(i + 1)).append("</td></tr>");
        }
        sb.append("</table>");
        return sb.toString();
    }

    private String getResultSet(String str, ResultSet resultSet, boolean z, boolean z2, boolean z3, long j, boolean z4) throws SQLException {
        boolean z5;
        int maxrows = getMaxrows();
        long currentTimeMillis = System.currentTimeMillis() - j;
        StringBuilder sb = new StringBuilder();
        if (z3) {
            sb.append("<form id=\"editing\" name=\"editing\" method=\"post\" action=\"editResult.do?jsessionid=${sessionId}\" id=\"mainForm\" target=\"h2result\"><input type=\"hidden\" name=\"op\" value=\"1\" /><input type=\"hidden\" name=\"row\" value=\"\" /><table class=\"resultSet\" cellspacing=\"0\" cellpadding=\"0\" id=\"editTable\">");
        } else {
            sb.append("<table class=\"resultSet\" cellspacing=\"0\" cellpadding=\"0\">");
        }
        if (z) {
            SimpleResultSet simpleResultSet = new SimpleResultSet();
            simpleResultSet.addColumn("#", 4, 0, 0);
            simpleResultSet.addColumn("label", 12, 0, 0);
            simpleResultSet.addColumn("catalog", 12, 0, 0);
            simpleResultSet.addColumn("schema", 12, 0, 0);
            simpleResultSet.addColumn("table", 12, 0, 0);
            simpleResultSet.addColumn("column", 12, 0, 0);
            simpleResultSet.addColumn("type", 4, 0, 0);
            simpleResultSet.addColumn("typeName", 12, 0, 0);
            simpleResultSet.addColumn("class", 12, 0, 0);
            simpleResultSet.addColumn("precision", 4, 0, 0);
            simpleResultSet.addColumn("scale", 4, 0, 0);
            simpleResultSet.addColumn("displaySize", 4, 0, 0);
            simpleResultSet.addColumn("autoIncrement", 16, 0, 0);
            simpleResultSet.addColumn("caseSensitive", 16, 0, 0);
            simpleResultSet.addColumn("currency", 16, 0, 0);
            simpleResultSet.addColumn("nullable", 4, 0, 0);
            simpleResultSet.addColumn("readOnly", 16, 0, 0);
            simpleResultSet.addColumn("searchable", 16, 0, 0);
            simpleResultSet.addColumn("signed", 16, 0, 0);
            simpleResultSet.addColumn("writable", 16, 0, 0);
            simpleResultSet.addColumn("definitelyWritable", 16, 0, 0);
            ResultSetMetaData metaData = resultSet.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                simpleResultSet.addRow(Integer.valueOf(i), metaData.getColumnLabel(i), metaData.getCatalogName(i), metaData.getSchemaName(i), metaData.getTableName(i), metaData.getColumnName(i), Integer.valueOf(metaData.getColumnType(i)), metaData.getColumnTypeName(i), metaData.getColumnClassName(i), Integer.valueOf(metaData.getPrecision(i)), Integer.valueOf(metaData.getScale(i)), Integer.valueOf(metaData.getColumnDisplaySize(i)), Boolean.valueOf(metaData.isAutoIncrement(i)), Boolean.valueOf(metaData.isCaseSensitive(i)), Boolean.valueOf(metaData.isCurrency(i)), Integer.valueOf(metaData.isNullable(i)), Boolean.valueOf(metaData.isReadOnly(i)), Boolean.valueOf(metaData.isSearchable(i)), Boolean.valueOf(metaData.isSigned(i)), Boolean.valueOf(metaData.isWritable(i)), Boolean.valueOf(metaData.isDefinitelyWritable(i)));
            }
            resultSet = simpleResultSet;
        }
        ResultSetMetaData metaData2 = resultSet.getMetaData();
        int columnCount = metaData2.getColumnCount();
        int i2 = 0;
        if (z2) {
            sb.append("<tr><th>Column</th><th>Data</th></tr><tr>");
            while (resultSet.next() && (maxrows <= 0 || i2 < maxrows)) {
                i2++;
                sb.append("<tr><td>Row #</td><td>").append(i2).append("</tr>");
                for (int i3 = 0; i3 < columnCount; i3++) {
                    sb.append("<tr><td>").append(PageParser.escapeHtml(metaData2.getColumnLabel(i3 + 1))).append("</td><td>").append(escapeData(resultSet, i3 + 1)).append("</td></tr>");
                }
            }
        } else {
            sb.append("<tr>");
            if (z3) {
                sb.append("<th>${text.resultEdit.action}</th>");
            }
            for (int i4 = 0; i4 < columnCount; i4++) {
                sb.append("<th>").append(PageParser.escapeHtml(metaData2.getColumnLabel(i4 + 1))).append("</th>");
            }
            sb.append("</tr>");
            while (resultSet.next() && (maxrows <= 0 || i2 < maxrows)) {
                i2++;
                sb.append("<tr>");
                if (z3) {
                    sb.append("<td>").append("<img onclick=\"javascript:editRow(").append(resultSet.getRow()).append(",'${sessionId}', '${text.resultEdit.save}', '${text.resultEdit.cancel}'").append(")\" width=16 height=16 src=\"ico_write.gif\" onmouseover = \"this.className ='icon_hover'\" onmouseout = \"this.className ='icon'\" class=\"icon\" alt=\"${text.resultEdit.edit}\" title=\"${text.resultEdit.edit}\" border=\"1\"/>").append("<img onclick=\"javascript:deleteRow(").append(resultSet.getRow()).append(",'${sessionId}', '${text.resultEdit.delete}', '${text.resultEdit.cancel}'").append(")\" width=16 height=16 src=\"ico_remove.gif\" onmouseover = \"this.className ='icon_hover'\" onmouseout = \"this.className ='icon'\" class=\"icon\" alt=\"${text.resultEdit.delete}\" title=\"${text.resultEdit.delete}\" border=\"1\" /></a>").append("</td>");
                }
                for (int i5 = 0; i5 < columnCount; i5++) {
                    sb.append("<td>").append(escapeData(resultSet, i5 + 1)).append("</td>");
                }
                sb.append("</tr>");
            }
        }
        boolean z6 = false;
        try {
            if (!this.session.getContents().isDB2()) {
                if (resultSet.getConcurrency() == 1008) {
                    if (resultSet.getType() != 1003) {
                        z5 = true;
                        z6 = z5;
                    }
                }
                z5 = false;
                z6 = z5;
            }
        } catch (NullPointerException e) {
        }
        if (z3) {
            ResultSet resultSet2 = this.session.result;
            if (resultSet2 != null) {
                resultSet2.close();
            }
            this.session.result = resultSet;
        } else {
            resultSet.close();
        }
        if (z3) {
            sb.append("<tr><td>").append("<img onclick=\"javascript:editRow(-1, '${sessionId}', '${text.resultEdit.save}', '${text.resultEdit.cancel}'").append(")\" width=16 height=16 src=\"ico_add.gif\" onmouseover = \"this.className ='icon_hover'\" onmouseout = \"this.className ='icon'\" class=\"icon\" alt=\"${text.resultEdit.add}\" title=\"${text.resultEdit.add}\" border=\"1\"/>").append("</td>");
            for (int i6 = 0; i6 < columnCount; i6++) {
                sb.append("<td></td>");
            }
            sb.append("</tr>");
        }
        sb.append("</table>");
        if (z3) {
            sb.append("</form>");
        }
        if (i2 == 0) {
            sb.append("(${text.result.noRows}");
        } else if (i2 == 1) {
            sb.append("(${text.result.1row}");
        } else {
            sb.append('(').append(i2).append(" ${text.result.rows}");
        }
        sb.append(", ");
        sb.append(System.currentTimeMillis() - currentTimeMillis).append(" ms)");
        if (!z3 && z6 && z4) {
            sb.append("<br /><br /><form name=\"editResult\" method=\"post\" action=\"query.do?jsessionid=${sessionId}\" target=\"h2result\"><input type=\"submit\" class=\"button\" value=\"${text.resultEdit.editResult}\" /><input type=\"hidden\" name=\"sql\" value=\"@edit ").append(PageParser.escapeHtmlData(str)).append("\" /></form>");
        }
        return sb.toString();
    }

    private String settingSave() {
        ConnectionInfo connectionInfo = new ConnectionInfo();
        connectionInfo.name = this.attributes.getProperty("name", "");
        connectionInfo.driver = this.attributes.getProperty("driver", "");
        connectionInfo.url = this.attributes.getProperty("url", "");
        connectionInfo.user = this.attributes.getProperty(ClassicConstants.USER_MDC_KEY, "");
        this.server.updateSetting(connectionInfo);
        this.attributes.put(Setting.EXT_NAME, connectionInfo.name);
        this.server.saveProperties(null);
        return "index.do";
    }

    private static String escapeData(ResultSet resultSet, int i) throws SQLException {
        if (DataType.isBinaryColumn(resultSet.getMetaData(), i)) {
            byte[] bytes = resultSet.getBytes(i);
            if (bytes == null) {
                return "<i>null</i>";
            }
            if (bytes.length > 50000) {
                return "<div style='display: none'>=+</div>" + StringUtils.convertBytesToHex(bytes, 3) + "... (" + bytes.length + " ${text.result.bytes})";
            }
            return StringUtils.convertBytesToHex(bytes);
        }
        String string = resultSet.getString(i);
        if (string == null) {
            return "<i>null</i>";
        }
        if (string.length() > 100000) {
            return "<div style='display: none'>=+</div>" + PageParser.escapeHtml(string.substring(0, 100)) + "... (" + string.length() + " ${text.result.characters})";
        }
        if (string.equals("null") || string.startsWith("= ") || string.startsWith("=+")) {
            return "<div style='display: none'>= </div>" + PageParser.escapeHtml(string);
        }
        if (string.equals("")) {
            return "";
        }
        return PageParser.escapeHtml(string);
    }

    private void unescapeData(String str, ResultSet resultSet, int i) throws SQLException {
        if (str.equals("null")) {
            resultSet.updateNull(i);
            return;
        }
        if (str.startsWith("=+")) {
            return;
        }
        if (str.equals("=*")) {
            switch (resultSet.getMetaData().getColumnType(i)) {
                case 91:
                case 93:
                    resultSet.updateString(i, "2001-01-01");
                    return;
                case 92:
                    resultSet.updateString(i, "12:00:00");
                    return;
                default:
                    resultSet.updateString(i, CustomBooleanEditor.VALUE_1);
                    return;
            }
        }
        if (str.startsWith("= ")) {
            str = str.substring(2);
        }
        ResultSetMetaData metaData = resultSet.getMetaData();
        if (DataType.isBinaryColumn(metaData, i)) {
            resultSet.updateBytes(i, StringUtils.convertHexToBytes(str));
            return;
        }
        int columnType = metaData.getColumnType(i);
        if (this.session.getContents().isH2()) {
            resultSet.updateString(i, str);
            return;
        }
        switch (columnType) {
            case -6:
                resultSet.updateShort(i, Short.decode(str).shortValue());
                return;
            case -5:
                resultSet.updateLong(i, Long.decode(str).longValue());
                return;
            case -4:
            case LobStorageFrontend.TABLE_RESULT /* -3 */:
            case -2:
            case -1:
            case 0:
            case 1:
            case 2:
            case 5:
            default:
                resultSet.updateString(i, str);
                return;
            case 3:
                resultSet.updateBigDecimal(i, new BigDecimal(str));
                return;
            case 4:
                resultSet.updateInt(i, Integer.decode(str).intValue());
                return;
            case 6:
            case 8:
                resultSet.updateDouble(i, Double.parseDouble(str));
                return;
            case 7:
                resultSet.updateFloat(i, Float.parseFloat(str));
                return;
        }
    }

    private String settingRemove() {
        this.server.removeSetting(this.attributes.getProperty("name", ""));
        ArrayList<ConnectionInfo> settings = this.server.getSettings();
        if (!settings.isEmpty()) {
            this.attributes.put(Setting.EXT_NAME, settings.get(0));
        }
        this.server.saveProperties(null);
        return "index.do";
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getMimeType() {
        return this.mimeType;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean getCache() {
        return this.cache;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public WebSession getSession() {
        return this.session;
    }

    private void trace(String str) {
        this.server.trace(str);
    }
}
