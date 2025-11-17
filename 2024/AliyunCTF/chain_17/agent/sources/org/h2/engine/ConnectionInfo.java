package org.h2.engine;

import cn.hutool.core.text.StrPool;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import org.h2.api.ErrorCode;
import org.h2.command.dml.SetTypes;
import org.h2.message.DbException;
import org.h2.security.SHA256;
import org.h2.store.fs.FileUtils;
import org.h2.store.fs.encrypt.FilePathEncrypt;
import org.h2.store.fs.rec.FilePathRec;
import org.h2.util.IOUtils;
import org.h2.util.NetworkConnectionInfo;
import org.h2.util.SortedProperties;
import org.h2.util.StringUtils;
import org.h2.util.TimeZoneProvider;
import org.h2.util.Utils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/engine/ConnectionInfo.class */
public class ConnectionInfo implements Cloneable {
    private static final HashSet<String> KNOWN_SETTINGS;
    private static final HashSet<String> IGNORED_BY_PARSER;
    private Properties prop = new Properties();
    private String originalURL;
    private String url;
    private String user;
    private byte[] filePasswordHash;
    private byte[] fileEncryptionKey;
    private byte[] userPasswordHash;
    private TimeZoneProvider timeZone;
    private String name;
    private String nameNormalized;
    private boolean remote;
    private boolean ssl;
    private boolean persistent;
    private boolean unnamed;
    private NetworkConnectionInfo networkConnectionInfo;

    public ConnectionInfo(String str) {
        this.name = str;
        this.url = Constants.START_URL + str;
        parseName();
    }

    public ConnectionInfo(String str, Properties properties, String str2, Object obj) {
        String remapURL = remapURL(str);
        this.url = remapURL;
        this.originalURL = remapURL;
        if (!remapURL.startsWith(Constants.START_URL)) {
            throw getFormatException();
        }
        if (properties != null) {
            readProperties(properties);
        }
        if (str2 != null) {
            this.prop.put("USER", str2);
        }
        if (obj != null) {
            this.prop.put("PASSWORD", obj);
        }
        readSettingsFromURL();
        Object remove = this.prop.remove("TIME ZONE");
        if (remove != null) {
            this.timeZone = TimeZoneProvider.ofId(remove.toString());
        }
        setUserName(removeProperty("USER", ""));
        this.name = this.url.substring(Constants.START_URL.length());
        parseName();
        convertPasswords();
        String removeProperty = removeProperty("RECOVER_TEST", (String) null);
        if (removeProperty != null) {
            FilePathRec.register();
            try {
                Utils.callStaticMethod("org.h2.store.RecoverTester.init", removeProperty);
                this.name = "rec:" + this.name;
            } catch (Exception e) {
                throw DbException.convert(e);
            }
        }
    }

    static {
        String[] strArr = {"ACCESS_MODE_DATA", "AUTO_RECONNECT", "AUTO_SERVER", "AUTO_SERVER_PORT", "CACHE_TYPE", "DB_CLOSE_ON_EXIT", "FILE_LOCK", "JMX", "NETWORK_TIMEOUT", "OLD_INFORMATION_SCHEMA", "OPEN_NEW", "PAGE_SIZE", "RECOVER"};
        String[] strArr2 = {"AUTHREALM", "AUTHZPWD", "AUTOCOMMIT", "CIPHER", "CREATE", "FORBID_CREATION", "IGNORE_UNKNOWN_SETTINGS", "IFEXISTS", "INIT", "NO_UPGRADE", "PASSWORD", "PASSWORD_HASH", "RECOVER_TEST", "USER"};
        HashSet<String> hashSet = new HashSet<>(128);
        hashSet.addAll(SetTypes.getTypes());
        for (String str : strArr) {
            if (!hashSet.add(str)) {
                throw DbException.getInternalError(str);
            }
        }
        for (String str2 : strArr2) {
            if (!hashSet.add(str2)) {
                throw DbException.getInternalError(str2);
            }
        }
        KNOWN_SETTINGS = hashSet;
        String[] strArr3 = {"ASSERT", "BINARY_COLLATION", "DB_CLOSE_ON_EXIT", "PAGE_STORE", "UUID_COLLATION"};
        HashSet<String> hashSet2 = new HashSet<>(32);
        for (String str3 : strArr) {
            hashSet2.add(str3);
        }
        for (String str4 : strArr3) {
            hashSet2.add(str4);
        }
        IGNORED_BY_PARSER = hashSet2;
    }

    private static boolean isKnownSetting(String str) {
        return KNOWN_SETTINGS.contains(str);
    }

    public static boolean isIgnoredByParser(String str) {
        return IGNORED_BY_PARSER.contains(str);
    }

    /* renamed from: clone, reason: merged with bridge method [inline-methods] */
    public ConnectionInfo m1459clone() throws CloneNotSupportedException {
        ConnectionInfo connectionInfo = (ConnectionInfo) super.clone();
        connectionInfo.prop = (Properties) this.prop.clone();
        connectionInfo.filePasswordHash = Utils.cloneByteArray(this.filePasswordHash);
        connectionInfo.fileEncryptionKey = Utils.cloneByteArray(this.fileEncryptionKey);
        connectionInfo.userPasswordHash = Utils.cloneByteArray(this.userPasswordHash);
        return connectionInfo;
    }

    private void parseName() {
        if (".".equals(this.name)) {
            this.name = "mem:";
        }
        if (this.name.startsWith("tcp:")) {
            this.remote = true;
            this.name = this.name.substring("tcp:".length());
        } else if (this.name.startsWith("ssl:")) {
            this.remote = true;
            this.ssl = true;
            this.name = this.name.substring("ssl:".length());
        } else if (this.name.startsWith("mem:")) {
            this.persistent = false;
            if ("mem:".equals(this.name)) {
                this.unnamed = true;
            }
        } else if (this.name.startsWith("file:")) {
            this.name = this.name.substring("file:".length());
            this.persistent = true;
        } else {
            this.persistent = true;
        }
        if (this.persistent && !this.remote) {
            this.name = IOUtils.nameSeparatorsToNative(this.name);
        }
    }

    public void setBaseDir(String str) {
        String str2;
        if (this.persistent) {
            String unwrap = FileUtils.unwrap(FileUtils.toRealPath(str));
            boolean isAbsolute = FileUtils.isAbsolute(this.name);
            String str3 = null;
            if (str.endsWith(File.separator)) {
                str = str.substring(0, str.length() - 1);
            }
            if (isAbsolute) {
                str2 = this.name;
            } else {
                String unwrap2 = FileUtils.unwrap(this.name);
                str3 = this.name.substring(0, this.name.length() - unwrap2.length());
                str2 = str + File.separatorChar + unwrap2;
            }
            String unwrap3 = FileUtils.unwrap(FileUtils.toRealPath(str2));
            if (unwrap3.equals(unwrap) || !unwrap3.startsWith(unwrap)) {
                throw DbException.get(ErrorCode.IO_EXCEPTION_1, unwrap3 + " outside " + unwrap);
            }
            if (!unwrap.endsWith("/") && !unwrap.endsWith(StrPool.BACKSLASH) && unwrap3.charAt(unwrap.length()) != '/') {
                throw DbException.get(ErrorCode.IO_EXCEPTION_1, unwrap3 + " outside " + unwrap);
            }
            if (!isAbsolute) {
                this.name = str3 + str + File.separatorChar + FileUtils.unwrap(this.name);
            }
        }
    }

    public boolean isRemote() {
        return this.remote;
    }

    public boolean isPersistent() {
        return this.persistent;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isUnnamedInMemory() {
        return this.unnamed;
    }

    private void readProperties(Properties properties) {
        DbSettings dbSettings = null;
        for (Object obj : properties.keySet().toArray()) {
            String upperEnglish = StringUtils.toUpperEnglish(obj.toString());
            if (this.prop.containsKey(upperEnglish)) {
                throw DbException.get(ErrorCode.DUPLICATE_PROPERTY_1, upperEnglish);
            }
            Object obj2 = properties.get(obj);
            if (isKnownSetting(upperEnglish)) {
                this.prop.put(upperEnglish, obj2);
            } else {
                if (dbSettings == null) {
                    dbSettings = getDbSettings();
                }
                if (dbSettings.containsKey(upperEnglish)) {
                    this.prop.put(upperEnglish, obj2);
                }
            }
        }
    }

    private void readSettingsFromURL() {
        DbSettings dbSettings = DbSettings.DEFAULT;
        int indexOf = this.url.indexOf(59);
        if (indexOf >= 0) {
            String substring = this.url.substring(indexOf + 1);
            this.url = this.url.substring(0, indexOf);
            String str = null;
            for (String str2 : StringUtils.arraySplit(substring, ';', false)) {
                if (!str2.isEmpty()) {
                    int indexOf2 = str2.indexOf(61);
                    if (indexOf2 < 0) {
                        throw getFormatException();
                    }
                    String substring2 = str2.substring(indexOf2 + 1);
                    String upperEnglish = StringUtils.toUpperEnglish(str2.substring(0, indexOf2));
                    if (isKnownSetting(upperEnglish) || dbSettings.containsKey(upperEnglish)) {
                        String property = this.prop.getProperty(upperEnglish);
                        if (property != null && !property.equals(substring2)) {
                            throw DbException.get(ErrorCode.DUPLICATE_PROPERTY_1, upperEnglish);
                        }
                        this.prop.setProperty(upperEnglish, substring2);
                    } else {
                        str = upperEnglish;
                    }
                }
            }
            if (str != null && !Utils.parseBoolean(this.prop.getProperty("IGNORE_UNKNOWN_SETTINGS"), false, false)) {
                throw DbException.get(ErrorCode.UNSUPPORTED_SETTING_1, str);
            }
        }
    }

    private void preservePasswordForAuthentication(Object obj) {
        if ((!isRemote() || isSSL()) && this.prop.containsKey("AUTHREALM") && obj != null) {
            this.prop.put("AUTHZPWD", obj instanceof char[] ? new String((char[]) obj) : obj);
        }
    }

    private char[] removePassword() {
        Object remove = this.prop.remove("PASSWORD");
        preservePasswordForAuthentication(remove);
        if (remove == null) {
            return new char[0];
        }
        if (remove instanceof char[]) {
            return (char[]) remove;
        }
        return remove.toString().toCharArray();
    }

    private void convertPasswords() {
        char[] removePassword = removePassword();
        boolean removeProperty = removeProperty("PASSWORD_HASH", false);
        if (getProperty("CIPHER", (String) null) != null) {
            int i = -1;
            int i2 = 0;
            int length = removePassword.length;
            while (true) {
                if (i2 >= length) {
                    break;
                }
                if (removePassword[i2] != ' ') {
                    i2++;
                } else {
                    i = i2;
                    break;
                }
            }
            if (i < 0) {
                throw DbException.get(ErrorCode.WRONG_PASSWORD_FORMAT);
            }
            char[] copyOfRange = Arrays.copyOfRange(removePassword, i + 1, removePassword.length);
            char[] copyOf = Arrays.copyOf(removePassword, i);
            Arrays.fill(removePassword, (char) 0);
            removePassword = copyOfRange;
            this.fileEncryptionKey = FilePathEncrypt.getPasswordBytes(copyOf);
            this.filePasswordHash = hashPassword(removeProperty, "file", copyOf);
        }
        this.userPasswordHash = hashPassword(removeProperty, this.user, removePassword);
    }

    private static byte[] hashPassword(boolean z, String str, char[] cArr) {
        if (z) {
            return StringUtils.convertHexToBytes(new String(cArr));
        }
        if (str.isEmpty() && cArr.length == 0) {
            return new byte[0];
        }
        return SHA256.getKeyPasswordHash(str, cArr);
    }

    public boolean getProperty(String str, boolean z) {
        return Utils.parseBoolean(getProperty(str, (String) null), z, false);
    }

    public boolean removeProperty(String str, boolean z) {
        return Utils.parseBoolean(removeProperty(str, (String) null), z, false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String removeProperty(String str, String str2) {
        if (SysProperties.CHECK && !isKnownSetting(str)) {
            throw DbException.getInternalError(str);
        }
        Object remove = this.prop.remove(str);
        return remove == null ? str2 : remove.toString();
    }

    public String getName() {
        if (!this.persistent) {
            return this.name;
        }
        if (this.nameNormalized == null) {
            if (!FileUtils.isAbsolute(this.name) && !this.name.contains("./") && !this.name.contains(".\\") && !this.name.contains(":/") && !this.name.contains(":\\")) {
                throw DbException.get(ErrorCode.URL_RELATIVE_TO_CWD, this.originalURL);
            }
            String realPath = FileUtils.toRealPath(this.name + Constants.SUFFIX_MV_FILE);
            if (FileUtils.getName(realPath).length() < Constants.SUFFIX_MV_FILE.length() + 1) {
                throw DbException.get(ErrorCode.INVALID_DATABASE_NAME_1, this.name);
            }
            this.nameNormalized = realPath.substring(0, realPath.length() - Constants.SUFFIX_MV_FILE.length());
        }
        return this.nameNormalized;
    }

    public byte[] getFilePasswordHash() {
        return this.filePasswordHash;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public byte[] getFileEncryptionKey() {
        return this.fileEncryptionKey;
    }

    public String getUserName() {
        return this.user;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public byte[] getUserPasswordHash() {
        return this.userPasswordHash;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String[] getKeys() {
        return (String[]) this.prop.keySet().toArray(new String[this.prop.size()]);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getProperty(String str) {
        Object obj = this.prop.get(str);
        if (!(obj instanceof String)) {
            return null;
        }
        return obj.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getProperty(String str, int i) {
        if (SysProperties.CHECK && !isKnownSetting(str)) {
            throw DbException.getInternalError(str);
        }
        String property = getProperty(str);
        return property == null ? i : Integer.parseInt(property);
    }

    public String getProperty(String str, String str2) {
        if (SysProperties.CHECK && !isKnownSetting(str)) {
            throw DbException.getInternalError(str);
        }
        String property = getProperty(str);
        return property == null ? str2 : property;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getProperty(int i, String str) {
        String property = getProperty(SetTypes.getTypeName(i));
        return property == null ? str : property;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getIntProperty(int i, int i2) {
        String property = getProperty(SetTypes.getTypeName(i), (String) null);
        if (property == null) {
            return i2;
        }
        try {
            return Integer.decode(property).intValue();
        } catch (NumberFormatException e) {
            return i2;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isSSL() {
        return this.ssl;
    }

    public void setUserName(String str) {
        this.user = StringUtils.toUpperEnglish(str);
    }

    public void setUserPasswordHash(byte[] bArr) {
        this.userPasswordHash = bArr;
    }

    public void setFilePasswordHash(byte[] bArr) {
        this.filePasswordHash = bArr;
    }

    public void setFileEncryptionKey(byte[] bArr) {
        this.fileEncryptionKey = bArr;
    }

    public void setProperty(String str, String str2) {
        if (str2 != null) {
            this.prop.setProperty(str, str2);
        }
    }

    public String getURL() {
        return this.url;
    }

    public String getOriginalURL() {
        return this.originalURL;
    }

    public void setOriginalURL(String str) {
        this.originalURL = str;
    }

    public TimeZoneProvider getTimeZone() {
        return this.timeZone;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DbException getFormatException() {
        return DbException.get(ErrorCode.URL_FORMAT_ERROR_2, Constants.URL_FORMAT, this.url);
    }

    public void setServerKey(String str) {
        this.remote = true;
        this.persistent = false;
        this.name = str;
    }

    public NetworkConnectionInfo getNetworkConnectionInfo() {
        return this.networkConnectionInfo;
    }

    public void setNetworkConnectionInfo(NetworkConnectionInfo networkConnectionInfo) {
        this.networkConnectionInfo = networkConnectionInfo;
    }

    public DbSettings getDbSettings() {
        DbSettings dbSettings = DbSettings.DEFAULT;
        HashMap hashMap = new HashMap(64);
        Iterator it = this.prop.keySet().iterator();
        while (it.hasNext()) {
            String obj = it.next().toString();
            if (!isKnownSetting(obj) && dbSettings.containsKey(obj)) {
                hashMap.put(obj, this.prop.getProperty(obj));
            }
        }
        return DbSettings.getInstance(hashMap);
    }

    private static String remapURL(String str) {
        String str2 = SysProperties.URL_MAP;
        if (str2 != null && !str2.isEmpty()) {
            try {
                SortedProperties loadProperties = SortedProperties.loadProperties(str2);
                String property = loadProperties.getProperty(str);
                if (property == null) {
                    loadProperties.put(str, "");
                    loadProperties.store(str2);
                } else {
                    String trim = property.trim();
                    if (!trim.isEmpty()) {
                        return trim;
                    }
                }
            } catch (IOException e) {
                throw DbException.convert(e);
            }
        }
        return str;
    }

    public void cleanAuthenticationInfo() {
        removeProperty("AUTHREALM", false);
        removeProperty("AUTHZPWD", false);
    }
}
