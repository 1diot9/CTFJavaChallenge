package org.h2.util;

import ch.qos.logback.classic.ClassicConstants;
import ch.qos.logback.core.joran.JoranConstants;
import cn.hutool.db.dialect.DriverNamePool;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import javax.naming.Context;
import javax.sql.DataSource;
import org.h2.api.ErrorCode;
import org.h2.api.JavaObjectSerializer;
import org.h2.engine.Constants;
import org.h2.engine.SysProperties;
import org.h2.jdbc.JdbcConnection;
import org.h2.jdbc.JdbcPreparedStatement;
import org.h2.message.DbException;
import org.h2.tools.SimpleResultSet;
import org.h2.util.Utils;
import org.h2.value.Value;
import org.h2.value.ValueLob;
import org.h2.value.ValueToObjectConverter;
import org.h2.value.ValueUuid;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/JdbcUtils.class */
public class JdbcUtils {
    public static JavaObjectSerializer serializer;
    private static boolean allowAllClasses;
    private static HashSet<String> allowedClassNames;
    private static String[] allowedClassNamePrefixes;
    private static final String[] DRIVERS = {"h2:", DriverNamePool.DRIVER_H2, "Cache:", "com.intersys.jdbc.CacheDriver", "daffodilDB://", "in.co.daffodil.db.rmi.RmiDaffodilDBDriver", "daffodil", "in.co.daffodil.db.jdbc.DaffodilDBDriver", "db2:", "com.ibm.db2.jcc.DB2Driver", "derby:net:", "org.apache.derby.client.ClientAutoloadedDriver", "derby://", "org.apache.derby.client.ClientAutoloadedDriver", "derby:", "org.apache.derby.iapi.jdbc.AutoloadedDriver", "FrontBase:", "com.frontbase.jdbc.FBJDriver", "firebirdsql:", "org.firebirdsql.jdbc.FBDriver", "hsqldb:", "org.hsqldb.jdbcDriver", "informix-sqli:", "com.informix.jdbc.IfxDriver", "jtds:", "net.sourceforge.jtds.jdbc.Driver", "microsoft:", "com.microsoft.jdbc.sqlserver.SQLServerDriver", "mimer:", "com.mimer.jdbc.Driver", "mysql:", DriverNamePool.DRIVER_MYSQL_V6, "mariadb:", DriverNamePool.DRIVER_MARIADB, "odbc:", "sun.jdbc.odbc.JdbcOdbcDriver", "oracle:", DriverNamePool.DRIVER_ORACLE_OLD, "pervasive:", "com.pervasive.jdbc.v2.Driver", "pointbase:micro:", "com.pointbase.me.jdbc.jdbcDriver", "pointbase:", "com.pointbase.jdbc.jdbcUniversalDriver", "postgresql:", DriverNamePool.DRIVER_POSTGRESQL, "sybase:", "com.sybase.jdbc3.jdbc.SybDriver", "sqlserver:", DriverNamePool.DRIVER_SQLSERVER, "teradata:", "com.ncr.teradata.TeraDriver"};
    private static final byte[] UUID_PREFIX = "¬í��\u0005sr��\u000ejava.util.UUID¼\u0099\u0003÷\u0098m\u0085/\u0002��\u0002J��\fleastSigBitsJ��\u000bmostSigBitsxp".getBytes(StandardCharsets.ISO_8859_1);
    private static final ArrayList<Utils.ClassFactory> userClassFactories = new ArrayList<>();

    static {
        String str = SysProperties.JAVA_OBJECT_SERIALIZER;
        if (str != null) {
            try {
                serializer = (JavaObjectSerializer) loadUserClass(str).getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            } catch (Exception e) {
                throw DbException.convert(e);
            }
        }
    }

    private JdbcUtils() {
    }

    public static void addClassFactory(Utils.ClassFactory classFactory) {
        userClassFactories.add(classFactory);
    }

    public static void removeClassFactory(Utils.ClassFactory classFactory) {
        userClassFactories.remove(classFactory);
    }

    public static <Z> Class<Z> loadUserClass(String str) {
        if (allowedClassNames == null) {
            String str2 = SysProperties.ALLOWED_CLASSES;
            ArrayList arrayList = new ArrayList();
            boolean z = false;
            HashSet<String> hashSet = new HashSet<>();
            for (String str3 : StringUtils.arraySplit(str2, ',', true)) {
                if (str3.equals("*")) {
                    z = true;
                } else if (str3.endsWith("*")) {
                    arrayList.add(str3.substring(0, str3.length() - 1));
                } else {
                    hashSet.add(str3);
                }
            }
            allowedClassNamePrefixes = (String[]) arrayList.toArray(new String[0]);
            allowAllClasses = z;
            allowedClassNames = hashSet;
        }
        if (!allowAllClasses && !allowedClassNames.contains(str)) {
            boolean z2 = false;
            String[] strArr = allowedClassNamePrefixes;
            int length = strArr.length;
            int i = 0;
            while (true) {
                if (i >= length) {
                    break;
                }
                if (!str.startsWith(strArr[i])) {
                    i++;
                } else {
                    z2 = true;
                    break;
                }
            }
            if (!z2) {
                throw DbException.get(ErrorCode.ACCESS_DENIED_TO_CLASS_1, str);
            }
        }
        Iterator<Utils.ClassFactory> it = userClassFactories.iterator();
        while (it.hasNext()) {
            Utils.ClassFactory next = it.next();
            if (next.match(str)) {
                try {
                    Class<Z> cls = (Class<Z>) next.loadClass(str);
                    if (cls != null) {
                        return cls;
                    }
                } catch (Exception e) {
                    throw DbException.get(ErrorCode.CLASS_NOT_FOUND_1, e, str);
                }
            }
        }
        try {
            return (Class<Z>) Class.forName(str);
        } catch (ClassNotFoundException e2) {
            try {
                return (Class<Z>) Class.forName(str, true, Thread.currentThread().getContextClassLoader());
            } catch (Exception e3) {
                throw DbException.get(ErrorCode.CLASS_NOT_FOUND_1, e2, str);
            }
        } catch (NoClassDefFoundError e4) {
            throw DbException.get(ErrorCode.CLASS_NOT_FOUND_1, e4, str);
        } catch (Error e5) {
            throw DbException.get(ErrorCode.GENERAL_ERROR_1, e5, str);
        }
    }

    public static void closeSilently(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
            }
        }
    }

    public static void closeSilently(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
            }
        }
    }

    public static void closeSilently(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
            }
        }
    }

    public static Connection getConnection(String str, String str2, String str3, String str4) throws SQLException {
        return getConnection(str, str2, str3, str4, null, false);
    }

    public static Connection getConnection(String str, String str2, String str3, String str4, NetworkConnectionInfo networkConnectionInfo, boolean z) throws SQLException {
        if (str2.startsWith(Constants.START_URL)) {
            JdbcConnection jdbcConnection = new JdbcConnection(str2, null, str3, str4, z);
            if (networkConnectionInfo != null) {
                jdbcConnection.getSession().setNetworkConnectionInfo(networkConnectionInfo);
            }
            return jdbcConnection;
        }
        if (StringUtils.isNullOrEmpty(str)) {
            load(str2);
        } else {
            Class loadUserClass = loadUserClass(str);
            try {
                if (Driver.class.isAssignableFrom(loadUserClass)) {
                    Driver driver = (Driver) loadUserClass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
                    Properties properties = new Properties();
                    if (str3 != null) {
                        properties.setProperty(ClassicConstants.USER_MDC_KEY, str3);
                    }
                    if (str4 != null) {
                        properties.setProperty("password", str4);
                    }
                    Connection connect = driver.connect(str2, properties);
                    if (connect != null) {
                        return connect;
                    }
                    throw new SQLException("Driver " + str + " is not suitable for " + str2, "08001");
                }
                if (Context.class.isAssignableFrom(loadUserClass)) {
                    if (!str2.startsWith("java:")) {
                        throw new SQLException("Only java scheme is supported for JNDI lookups", "08001");
                    }
                    DataSource dataSource = (DataSource) ((Context) loadUserClass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0])).lookup(str2);
                    if (StringUtils.isNullOrEmpty(str3) && StringUtils.isNullOrEmpty(str4)) {
                        return dataSource.getConnection();
                    }
                    return dataSource.getConnection(str3, str4);
                }
            } catch (Exception e) {
                throw DbException.toSQLException(e);
            }
        }
        return DriverManager.getConnection(str2, str3, str4);
    }

    public static String getDriver(String str) {
        if (str.startsWith("jdbc:")) {
            String substring = str.substring("jdbc:".length());
            for (int i = 0; i < DRIVERS.length; i += 2) {
                if (substring.startsWith(DRIVERS[i])) {
                    return DRIVERS[i + 1];
                }
            }
            return null;
        }
        return null;
    }

    public static void load(String str) {
        String driver = getDriver(str);
        if (driver != null) {
            loadUserClass(driver);
        }
    }

    public static byte[] serialize(Object obj, JavaObjectSerializer javaObjectSerializer) {
        try {
            if (javaObjectSerializer != null) {
                return javaObjectSerializer.serialize(obj);
            }
            if (serializer != null) {
                return serializer.serialize(obj);
            }
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            new ObjectOutputStream(byteArrayOutputStream).writeObject(obj);
            return byteArrayOutputStream.toByteArray();
        } catch (Throwable th) {
            throw DbException.get(ErrorCode.SERIALIZATION_FAILED_1, th, th.toString());
        }
    }

    public static Object deserialize(byte[] bArr, JavaObjectSerializer javaObjectSerializer) {
        ObjectInputStream objectInputStream;
        try {
            if (javaObjectSerializer != null) {
                return javaObjectSerializer.deserialize(bArr);
            }
            if (serializer != null) {
                return serializer.deserialize(bArr);
            }
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bArr);
            if (SysProperties.USE_THREAD_CONTEXT_CLASS_LOADER) {
                final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
                objectInputStream = new ObjectInputStream(byteArrayInputStream) { // from class: org.h2.util.JdbcUtils.1
                    @Override // java.io.ObjectInputStream
                    protected Class<?> resolveClass(ObjectStreamClass objectStreamClass) throws IOException, ClassNotFoundException {
                        try {
                            return Class.forName(objectStreamClass.getName(), true, contextClassLoader);
                        } catch (ClassNotFoundException e) {
                            return super.resolveClass(objectStreamClass);
                        }
                    }
                };
            } else {
                objectInputStream = new ObjectInputStream(byteArrayInputStream);
            }
            return objectInputStream.readObject();
        } catch (Throwable th) {
            throw DbException.get(ErrorCode.DESERIALIZATION_FAILED_1, th, th.toString());
        }
    }

    public static ValueUuid deserializeUuid(byte[] bArr) {
        if (bArr.length == 80) {
            for (int i = 0; i < 64; i++) {
                if (bArr[i] == UUID_PREFIX[i]) {
                }
            }
            return ValueUuid.get(Bits.readLong(bArr, 72), Bits.readLong(bArr, 64));
        }
        throw DbException.get(ErrorCode.DESERIALIZATION_FAILED_1, "Is not a UUID");
    }

    public static void set(PreparedStatement preparedStatement, int i, Value value, JdbcConnection jdbcConnection) throws SQLException {
        if (preparedStatement instanceof JdbcPreparedStatement) {
            if (value instanceof ValueLob) {
                setLob(preparedStatement, i, (ValueLob) value);
                return;
            } else {
                preparedStatement.setObject(i, value);
                return;
            }
        }
        setOther(preparedStatement, i, value, jdbcConnection);
    }

    private static void setOther(PreparedStatement preparedStatement, int i, Value value, JdbcConnection jdbcConnection) throws SQLException {
        int valueType = value.getValueType();
        switch (valueType) {
            case 0:
                preparedStatement.setNull(i, 0);
                return;
            case 1:
                try {
                    preparedStatement.setObject(i, value.getString(), 1);
                    return;
                } catch (SQLException e) {
                    preparedStatement.setString(i, value.getString());
                    return;
                }
            case 2:
            case 4:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
            case 36:
                preparedStatement.setString(i, value.getString());
                return;
            case 3:
            case 7:
                setLob(preparedStatement, i, (ValueLob) value);
                return;
            case 5:
            case 6:
            case 37:
            case 38:
                preparedStatement.setBytes(i, value.getBytesNoCopy());
                return;
            case 8:
                preparedStatement.setBoolean(i, value.getBoolean());
                return;
            case 9:
                preparedStatement.setByte(i, value.getByte());
                return;
            case 10:
                preparedStatement.setShort(i, value.getShort());
                return;
            case 11:
                preparedStatement.setInt(i, value.getInt());
                return;
            case 12:
                preparedStatement.setLong(i, value.getLong());
                return;
            case 13:
            case 16:
                preparedStatement.setBigDecimal(i, value.getBigDecimal());
                return;
            case 14:
                preparedStatement.setFloat(i, value.getFloat());
                return;
            case 15:
                preparedStatement.setDouble(i, value.getDouble());
                return;
            case 17:
                try {
                    preparedStatement.setObject(i, JSR310Utils.valueToLocalDate(value, null), 91);
                    return;
                } catch (SQLException e2) {
                    preparedStatement.setDate(i, LegacyDateTimeUtils.toDate(null, null, value));
                    return;
                }
            case 18:
                try {
                    preparedStatement.setObject(i, JSR310Utils.valueToLocalTime(value, null), 92);
                    return;
                } catch (SQLException e3) {
                    preparedStatement.setTime(i, LegacyDateTimeUtils.toTime(null, null, value));
                    return;
                }
            case 19:
                try {
                    preparedStatement.setObject(i, JSR310Utils.valueToOffsetTime(value, null), 2013);
                    return;
                } catch (SQLException e4) {
                    preparedStatement.setString(i, value.getString());
                    return;
                }
            case 20:
                try {
                    preparedStatement.setObject(i, JSR310Utils.valueToLocalDateTime(value, null), 93);
                    return;
                } catch (SQLException e5) {
                    preparedStatement.setTimestamp(i, LegacyDateTimeUtils.toTimestamp(null, null, value));
                    return;
                }
            case 21:
                try {
                    preparedStatement.setObject(i, JSR310Utils.valueToOffsetDateTime(value, null), 2014);
                    return;
                } catch (SQLException e6) {
                    preparedStatement.setString(i, value.getString());
                    return;
                }
            case 35:
                preparedStatement.setObject(i, deserialize(value.getBytesNoCopy(), jdbcConnection.getJavaObjectSerializer()), 2000);
                return;
            case 39:
                preparedStatement.setBytes(i, value.getBytes());
                return;
            case 40:
                preparedStatement.setArray(i, preparedStatement.getConnection().createArrayOf(JoranConstants.NULL, (Object[]) ValueToObjectConverter.valueToDefaultObject(value, jdbcConnection, true)));
                return;
            default:
                throw DbException.getUnsupportedException(Value.getTypeName(valueType));
        }
    }

    private static void setLob(PreparedStatement preparedStatement, int i, ValueLob valueLob) throws SQLException {
        if (valueLob.getValueType() == 7) {
            long octetLength = valueLob.octetLength();
            preparedStatement.setBinaryStream(i, valueLob.getInputStream(), octetLength > 2147483647L ? -1 : (int) octetLength);
        } else {
            long charLength = valueLob.charLength();
            preparedStatement.setCharacterStream(i, valueLob.getReader(), charLength > 2147483647L ? -1 : (int) charLength);
        }
    }

    public static ResultSet getMetaResultSet(Connection connection, String str) throws SQLException {
        String sQLException;
        int[] iArr;
        DatabaseMetaData metaData = connection.getMetaData();
        if (isBuiltIn(str, "@best_row_identifier")) {
            String[] split = split(str);
            return metaData.getBestRowIdentifier(split[1], split[2], split[3], split[4] == null ? 0 : Integer.parseInt(split[4]), Boolean.parseBoolean(split[5]));
        }
        if (isBuiltIn(str, "@catalogs")) {
            return metaData.getCatalogs();
        }
        if (isBuiltIn(str, "@columns")) {
            String[] split2 = split(str);
            return metaData.getColumns(split2[1], split2[2], split2[3], split2[4]);
        }
        if (isBuiltIn(str, "@column_privileges")) {
            String[] split3 = split(str);
            return metaData.getColumnPrivileges(split3[1], split3[2], split3[3], split3[4]);
        }
        if (isBuiltIn(str, "@cross_references")) {
            String[] split4 = split(str);
            return metaData.getCrossReference(split4[1], split4[2], split4[3], split4[4], split4[5], split4[6]);
        }
        if (isBuiltIn(str, "@exported_keys")) {
            String[] split5 = split(str);
            return metaData.getExportedKeys(split5[1], split5[2], split5[3]);
        }
        if (isBuiltIn(str, "@imported_keys")) {
            String[] split6 = split(str);
            return metaData.getImportedKeys(split6[1], split6[2], split6[3]);
        }
        if (isBuiltIn(str, "@index_info")) {
            String[] split7 = split(str);
            return metaData.getIndexInfo(split7[1], split7[2], split7[3], Boolean.parseBoolean(split7[4]), Boolean.parseBoolean(split7[5]));
        }
        if (isBuiltIn(str, "@primary_keys")) {
            String[] split8 = split(str);
            return metaData.getPrimaryKeys(split8[1], split8[2], split8[3]);
        }
        if (isBuiltIn(str, "@procedures")) {
            String[] split9 = split(str);
            return metaData.getProcedures(split9[1], split9[2], split9[3]);
        }
        if (isBuiltIn(str, "@procedure_columns")) {
            String[] split10 = split(str);
            return metaData.getProcedureColumns(split10[1], split10[2], split10[3], split10[4]);
        }
        if (isBuiltIn(str, "@schemas")) {
            return metaData.getSchemas();
        }
        if (isBuiltIn(str, "@tables")) {
            String[] split11 = split(str);
            return metaData.getTables(split11[1], split11[2], split11[3], split11[4] == null ? null : StringUtils.arraySplit(split11[4], ',', false));
        }
        if (isBuiltIn(str, "@table_privileges")) {
            String[] split12 = split(str);
            return metaData.getTablePrivileges(split12[1], split12[2], split12[3]);
        }
        if (isBuiltIn(str, "@table_types")) {
            return metaData.getTableTypes();
        }
        if (isBuiltIn(str, "@type_info")) {
            return metaData.getTypeInfo();
        }
        if (isBuiltIn(str, "@udts")) {
            String[] split13 = split(str);
            if (split13[4] == null) {
                iArr = null;
            } else {
                String[] arraySplit = StringUtils.arraySplit(split13[4], ',', false);
                iArr = new int[arraySplit.length];
                for (int i = 0; i < arraySplit.length; i++) {
                    iArr[i] = Integer.parseInt(arraySplit[i]);
                }
            }
            return metaData.getUDTs(split13[1], split13[2], split13[3], iArr);
        }
        if (isBuiltIn(str, "@version_columns")) {
            String[] split14 = split(str);
            return metaData.getVersionColumns(split14[1], split14[2], split14[3]);
        }
        if (isBuiltIn(str, "@memory")) {
            SimpleResultSet simpleResultSet = new SimpleResultSet();
            simpleResultSet.addColumn("Type", 12, 0, 0);
            simpleResultSet.addColumn("KB", 12, 0, 0);
            simpleResultSet.addRow("Used Memory", Long.toString(Utils.getMemoryUsed()));
            simpleResultSet.addRow("Free Memory", Long.toString(Utils.getMemoryFree()));
            return simpleResultSet;
        }
        if (isBuiltIn(str, "@info")) {
            SimpleResultSet simpleResultSet2 = new SimpleResultSet();
            simpleResultSet2.addColumn("KEY", 12, 0, 0);
            simpleResultSet2.addColumn("VALUE", 12, 0, 0);
            simpleResultSet2.addRow("conn.getCatalog", connection.getCatalog());
            simpleResultSet2.addRow("conn.getAutoCommit", Boolean.toString(connection.getAutoCommit()));
            simpleResultSet2.addRow("conn.getTransactionIsolation", Integer.toString(connection.getTransactionIsolation()));
            simpleResultSet2.addRow("conn.getWarnings", String.valueOf(connection.getWarnings()));
            try {
                sQLException = String.valueOf(connection.getTypeMap());
            } catch (SQLException e) {
                sQLException = e.toString();
            }
            simpleResultSet2.addRow("conn.getTypeMap", sQLException);
            simpleResultSet2.addRow("conn.isReadOnly", Boolean.toString(connection.isReadOnly()));
            simpleResultSet2.addRow("conn.getHoldability", Integer.toString(connection.getHoldability()));
            addDatabaseMetaData(simpleResultSet2, metaData);
            return simpleResultSet2;
        }
        if (isBuiltIn(str, "@attributes")) {
            String[] split15 = split(str);
            return metaData.getAttributes(split15[1], split15[2], split15[3], split15[4]);
        }
        if (isBuiltIn(str, "@super_tables")) {
            String[] split16 = split(str);
            return metaData.getSuperTables(split16[1], split16[2], split16[3]);
        }
        if (isBuiltIn(str, "@super_types")) {
            String[] split17 = split(str);
            return metaData.getSuperTypes(split17[1], split17[2], split17[3]);
        }
        if (isBuiltIn(str, "@pseudo_columns")) {
            String[] split18 = split(str);
            return metaData.getPseudoColumns(split18[1], split18[2], split18[3], split18[4]);
        }
        return null;
    }

    private static void addDatabaseMetaData(SimpleResultSet simpleResultSet, DatabaseMetaData databaseMetaData) {
        Method[] declaredMethods = DatabaseMetaData.class.getDeclaredMethods();
        Arrays.sort(declaredMethods, Comparator.comparing((v0) -> {
            return v0.toString();
        }));
        for (Method method : declaredMethods) {
            if (method.getParameterTypes().length == 0) {
                try {
                    simpleResultSet.addRow("meta." + method.getName(), String.valueOf(method.invoke(databaseMetaData, new Object[0])));
                } catch (InvocationTargetException e) {
                    simpleResultSet.addRow("meta." + method.getName(), e.getTargetException().toString());
                } catch (Exception e2) {
                    simpleResultSet.addRow("meta." + method.getName(), e2.toString());
                }
            }
        }
    }

    public static boolean isBuiltIn(String str, String str2) {
        return str.regionMatches(true, 0, str2, 0, str2.length());
    }

    public static String[] split(String str) {
        String[] arraySplit = StringUtils.arraySplit(str, ' ', true);
        String[] strArr = new String[Math.max(10, arraySplit.length)];
        System.arraycopy(arraySplit, 0, strArr, 0, arraySplit.length);
        for (int i = 0; i < strArr.length; i++) {
            if ("null".equals(strArr[i])) {
                strArr[i] = null;
            }
        }
        return strArr;
    }
}
