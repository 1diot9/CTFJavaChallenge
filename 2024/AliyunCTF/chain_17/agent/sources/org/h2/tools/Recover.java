package org.h2.tools;

import cn.hutool.core.text.CharSequenceUtil;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.SequenceInputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import org.h2.engine.Constants;
import org.h2.engine.MetaRecord;
import org.h2.message.DbException;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStore;
import org.h2.mvstore.MVStoreTool;
import org.h2.mvstore.StreamStore;
import org.h2.mvstore.db.LobStorageMap;
import org.h2.mvstore.db.ValueDataType;
import org.h2.mvstore.tx.TransactionMap;
import org.h2.mvstore.tx.TransactionStore;
import org.h2.mvstore.type.DataType;
import org.h2.mvstore.type.MetaType;
import org.h2.mvstore.type.StringDataType;
import org.h2.result.Row;
import org.h2.store.DataHandler;
import org.h2.store.FileLister;
import org.h2.store.FileStore;
import org.h2.store.LobStorageInterface;
import org.h2.store.fs.FileUtils;
import org.h2.util.IOUtils;
import org.h2.util.SmallLRUCache;
import org.h2.util.StringUtils;
import org.h2.util.TempFileDeleter;
import org.h2.util.Tool;
import org.h2.value.CompareMode;
import org.h2.value.Value;
import org.h2.value.ValueCollectionBase;
import org.h2.value.ValueLob;
import org.h2.value.lob.LobData;
import org.h2.value.lob.LobDataDatabase;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/tools/Recover.class */
public class Recover extends Tool implements DataHandler {
    private String databaseName;
    private int storageId;
    private String storageName;
    private int recordLength;
    private int valueId;
    private boolean trace;
    private ArrayList<MetaRecord> schema;
    private HashSet<Integer> objectIdSet;
    private HashMap<Integer, String> tableMap;
    private HashMap<String, String> columnTypeMap;
    private boolean lobMaps;

    public static void main(String... strArr) throws SQLException {
        new Recover().runTool(strArr);
    }

    @Override // org.h2.util.Tool
    public void runTool(String... strArr) throws SQLException {
        String str = ".";
        String str2 = null;
        int i = 0;
        while (strArr != null && i < strArr.length) {
            String str3 = strArr[i];
            if ("-dir".equals(str3)) {
                i++;
                str = strArr[i];
            } else if ("-db".equals(str3)) {
                i++;
                str2 = strArr[i];
            } else if ("-trace".equals(str3)) {
                this.trace = true;
            } else {
                if (str3.equals("-help") || str3.equals("-?")) {
                    showUsage();
                    return;
                }
                showUsageAndThrowUnsupportedOption(str3);
            }
            i++;
        }
        process(str, str2);
    }

    public static InputStream readBlobMap(Connection connection, long j, long j2) throws SQLException {
        final PreparedStatement prepareStatement = connection.prepareStatement("SELECT DATA FROM INFORMATION_SCHEMA.LOB_BLOCKS WHERE LOB_ID = ? AND SEQ = ? AND ? > 0");
        prepareStatement.setLong(1, j);
        prepareStatement.setLong(3, j2);
        return new SequenceInputStream(new Enumeration<InputStream>() { // from class: org.h2.tools.Recover.1
            private int seq;
            private byte[] data = fetch();

            private byte[] fetch() {
                try {
                    PreparedStatement preparedStatement = prepareStatement;
                    int i = this.seq;
                    this.seq = i + 1;
                    preparedStatement.setInt(2, i);
                    ResultSet executeQuery = prepareStatement.executeQuery();
                    if (executeQuery.next()) {
                        return executeQuery.getBytes(1);
                    }
                    return null;
                } catch (SQLException e) {
                    throw DbException.convert(e);
                }
            }

            @Override // java.util.Enumeration
            public boolean hasMoreElements() {
                return this.data != null;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.util.Enumeration
            public InputStream nextElement() {
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.data);
                this.data = fetch();
                return byteArrayInputStream;
            }
        });
    }

    public static Reader readClobMap(Connection connection, long j, long j2) throws Exception {
        return new BufferedReader(new InputStreamReader(readBlobMap(connection, j, j2), StandardCharsets.UTF_8));
    }

    private void trace(String str) {
        if (this.trace) {
            this.out.println(str);
        }
    }

    private void traceError(String str, Throwable th) {
        this.out.println(str + ": " + th.toString());
        if (this.trace) {
            th.printStackTrace(this.out);
        }
    }

    public static void execute(String str, String str2) throws SQLException {
        try {
            new Recover().process(str, str2);
        } catch (DbException e) {
            throw DbException.toSQLException(e);
        }
    }

    private void process(String str, String str2) {
        ArrayList<String> databaseFiles = FileLister.getDatabaseFiles(str, str2, true);
        if (databaseFiles.isEmpty()) {
            printNoDatabaseFilesFound(str, str2);
        }
        Iterator<String> it = databaseFiles.iterator();
        while (it.hasNext()) {
            String next = it.next();
            if (next.endsWith(Constants.SUFFIX_MV_FILE)) {
                String substring = next.substring(0, next.length() - Constants.SUFFIX_MV_FILE.length());
                PrintWriter writer = getWriter(next, ".txt");
                Throwable th = null;
                try {
                    try {
                        MVStoreTool.dump(next, writer, true);
                        MVStoreTool.info(next, writer);
                        if (writer != null) {
                            if (0 != 0) {
                                try {
                                    writer.close();
                                } catch (Throwable th2) {
                                    th.addSuppressed(th2);
                                }
                            } else {
                                writer.close();
                            }
                        }
                        writer = getWriter(substring + ".h2.db", ".sql");
                        Throwable th3 = null;
                        try {
                            try {
                                dumpMVStoreFile(writer, next);
                                if (writer != null) {
                                    if (0 != 0) {
                                        try {
                                            writer.close();
                                        } catch (Throwable th4) {
                                            th3.addSuppressed(th4);
                                        }
                                    } else {
                                        writer.close();
                                    }
                                }
                            } finally {
                            }
                        } finally {
                        }
                    } finally {
                    }
                } finally {
                }
            }
        }
    }

    private PrintWriter getWriter(String str, String str2) {
        String str3 = str.substring(0, str.length() - 3) + str2;
        trace("Created file: " + str3);
        try {
            return new PrintWriter(IOUtils.getBufferedWriter(FileUtils.newOutputStream(str3, false)));
        } catch (IOException e) {
            throw DbException.convertIOException(e, null);
        }
    }

    private void getSQL(StringBuilder sb, String str, Value value) {
        long charLength;
        String str2;
        if (value instanceof ValueLob) {
            ValueLob valueLob = (ValueLob) value;
            LobData lobData = valueLob.getLobData();
            if (lobData instanceof LobDataDatabase) {
                LobDataDatabase lobDataDatabase = (LobDataDatabase) lobData;
                int valueType = value.getValueType();
                long lobId = lobDataDatabase.getLobId();
                if (valueType == 7) {
                    charLength = valueLob.octetLength();
                    str2 = "BLOB";
                    sb.append("READ_BLOB");
                } else {
                    charLength = valueLob.charLength();
                    str2 = "CLOB";
                    sb.append("READ_CLOB");
                }
                if (this.lobMaps) {
                    sb.append("_MAP");
                } else {
                    sb.append("_DB");
                }
                this.columnTypeMap.put(str, str2);
                sb.append('(').append(lobId).append(", ").append(charLength).append(')');
                return;
            }
        }
        value.getSQL(sb, 4);
    }

    private void setDatabaseName(String str) {
        this.databaseName = str;
    }

    private void dumpMVStoreFile(PrintWriter printWriter, String str) {
        Value[] list;
        printWriter.println("-- MVStore");
        String name = getClass().getName();
        printWriter.println("CREATE ALIAS IF NOT EXISTS READ_BLOB_MAP FOR '" + name + ".readBlobMap';");
        printWriter.println("CREATE ALIAS IF NOT EXISTS READ_CLOB_MAP FOR '" + name + ".readClobMap';");
        resetSchema();
        setDatabaseName(str.substring(0, str.length() - Constants.SUFFIX_MV_FILE.length()));
        try {
            MVStore open = new MVStore.Builder().fileName(str).recoveryMode().readOnly().open();
            Throwable th = null;
            try {
                dumpLobMaps(printWriter, open);
                printWriter.println("-- Layout");
                dumpLayout(printWriter, open);
                printWriter.println("-- Meta");
                dumpMeta(printWriter, open);
                printWriter.println("-- Types");
                dumpTypes(printWriter, open);
                printWriter.println("-- Tables");
                TransactionStore transactionStore = new TransactionStore(open, new ValueDataType());
                try {
                    transactionStore.init();
                } catch (Throwable th2) {
                    writeError(printWriter, th2);
                }
                for (String str2 : open.getMapNames()) {
                    if (str2.startsWith("table.")) {
                        if (Integer.parseInt(str2.substring("table.".length())) == 0) {
                            TransactionMap openMap = transactionStore.begin().openMap(str2);
                            Iterator keyIterator = openMap.keyIterator(null);
                            while (keyIterator.hasNext()) {
                                try {
                                    writeMetaRow((Row) openMap.get((Long) keyIterator.next()));
                                } catch (Throwable th3) {
                                    writeError(printWriter, th3);
                                }
                            }
                        }
                    }
                }
                writeSchemaSET(printWriter);
                printWriter.println("---- Table Data ----");
                for (String str3 : open.getMapNames()) {
                    if (str3.startsWith("table.")) {
                        String substring = str3.substring("table.".length());
                        if (Integer.parseInt(substring) != 0) {
                            TransactionMap openMap2 = transactionStore.begin().openMap(str3);
                            Iterator keyIterator2 = openMap2.keyIterator(null);
                            boolean z = false;
                            while (keyIterator2.hasNext()) {
                                Object obj = openMap2.get(keyIterator2.next());
                                if (obj instanceof Row) {
                                    list = ((Row) obj).getValueList();
                                    this.recordLength = list.length;
                                } else {
                                    list = ((ValueCollectionBase) obj).getList();
                                    this.recordLength = list.length - 1;
                                }
                                if (!z) {
                                    setStorage(Integer.parseInt(substring));
                                    StringBuilder sb = new StringBuilder();
                                    this.valueId = 0;
                                    while (this.valueId < this.recordLength) {
                                        String str4 = this.storageName + "." + this.valueId;
                                        sb.setLength(0);
                                        getSQL(sb, str4, list[this.valueId]);
                                        this.valueId++;
                                    }
                                    createTemporaryTable(printWriter);
                                    z = true;
                                }
                                StringBuilder sb2 = new StringBuilder();
                                sb2.append("INSERT INTO O_").append(substring).append(" VALUES(");
                                this.valueId = 0;
                                while (this.valueId < this.recordLength) {
                                    if (this.valueId > 0) {
                                        sb2.append(", ");
                                    }
                                    getSQL(sb2, this.storageName + "." + this.valueId, list[this.valueId]);
                                    this.valueId++;
                                }
                                sb2.append(");");
                                printWriter.println(sb2.toString());
                            }
                        }
                    }
                }
                writeSchema(printWriter);
                printWriter.println("DROP ALIAS READ_BLOB_MAP;");
                printWriter.println("DROP ALIAS READ_CLOB_MAP;");
                printWriter.println("DROP TABLE IF EXISTS INFORMATION_SCHEMA.LOB_BLOCKS;");
                if (open != null) {
                    if (0 != 0) {
                        try {
                            open.close();
                        } catch (Throwable th4) {
                            th.addSuppressed(th4);
                        }
                    } else {
                        open.close();
                    }
                }
            } finally {
            }
        } catch (Throwable th5) {
            writeError(printWriter, th5);
        }
    }

    private static void dumpLayout(PrintWriter printWriter, MVStore mVStore) {
        for (Map.Entry<String, String> entry : mVStore.getLayoutMap().entrySet()) {
            printWriter.println("-- " + entry.getKey() + " = " + entry.getValue());
        }
    }

    private static void dumpMeta(PrintWriter printWriter, MVStore mVStore) {
        for (Map.Entry<String, String> entry : mVStore.getMetaMap().entrySet()) {
            printWriter.println("-- " + entry.getKey() + " = " + entry.getValue());
        }
    }

    private static void dumpTypes(PrintWriter printWriter, MVStore mVStore) {
        for (Map.Entry entry : mVStore.openMap("_", new MVMap.Builder().keyType((DataType) StringDataType.INSTANCE).valueType((DataType) new MetaType(null, null))).entrySet()) {
            printWriter.println("-- " + ((String) entry.getKey()) + " = " + entry.getValue());
        }
    }

    private void dumpLobMaps(PrintWriter printWriter, MVStore mVStore) {
        this.lobMaps = mVStore.hasMap("lobData");
        if (!this.lobMaps) {
            return;
        }
        TransactionStore transactionStore = new TransactionStore(mVStore);
        MVMap<Long, byte[]> openLobDataMap = LobStorageMap.openLobDataMap(transactionStore);
        StreamStore streamStore = new StreamStore(openLobDataMap);
        MVMap<Long, LobStorageMap.BlobMeta> openLobMap = LobStorageMap.openLobMap(transactionStore);
        printWriter.println("-- LOB");
        printWriter.println("CREATE TABLE IF NOT EXISTS INFORMATION_SCHEMA.LOB_BLOCKS(LOB_ID BIGINT, SEQ INT, DATA VARBINARY, PRIMARY KEY(LOB_ID, SEQ));");
        boolean z = false;
        for (Map.Entry<Long, LobStorageMap.BlobMeta> entry : openLobMap.entrySet()) {
            long longValue = entry.getKey().longValue();
            InputStream inputStream = streamStore.get(entry.getValue().streamStoreId);
            byte[] bArr = new byte[8192];
            int i = 0;
            while (true) {
                try {
                    int readFully = IOUtils.readFully(inputStream, bArr, bArr.length);
                    if (readFully > 0) {
                        printWriter.print("INSERT INTO INFORMATION_SCHEMA.LOB_BLOCKS VALUES(" + longValue + ", " + i + ", X'");
                        printWriter.print(StringUtils.convertBytesToHex(bArr, readFully));
                        printWriter.println("');");
                    }
                    if (readFully != 8192) {
                        break;
                    } else {
                        i++;
                    }
                } catch (IOException e) {
                    writeError(printWriter, e);
                    z = true;
                }
            }
        }
        printWriter.println("-- lobMap.size: " + openLobMap.sizeAsLong());
        printWriter.println("-- lobData.size: " + openLobDataMap.sizeAsLong());
        if (z) {
            printWriter.println("-- lobMap");
            for (Long l : openLobMap.keyList()) {
                printWriter.println("--     " + l + CharSequenceUtil.SPACE + StreamStore.toString(openLobMap.get(l).streamStoreId));
            }
            printWriter.println("-- lobData");
            for (Long l2 : openLobDataMap.keyList()) {
                printWriter.println("--     " + l2 + " len " + openLobDataMap.get(l2).length);
            }
        }
    }

    private String setStorage(int i) {
        this.storageId = i;
        this.storageName = "O_" + Integer.toString(i).replace('-', 'M');
        return this.storageName;
    }

    private void writeMetaRow(Row row) {
        MetaRecord metaRecord = new MetaRecord(row);
        int objectType = metaRecord.getObjectType();
        if (objectType == 1 && metaRecord.getSQL().startsWith("CREATE PRIMARY KEY ")) {
            return;
        }
        this.schema.add(metaRecord);
        if (objectType == 0) {
            this.tableMap.put(Integer.valueOf(metaRecord.getId()), extractTableOrViewName(metaRecord.getSQL()));
        }
    }

    private void resetSchema() {
        this.schema = new ArrayList<>();
        this.objectIdSet = new HashSet<>();
        this.tableMap = new HashMap<>();
        this.columnTypeMap = new HashMap<>();
    }

    private void writeSchemaSET(PrintWriter printWriter) {
        printWriter.println("---- Schema SET ----");
        Iterator<MetaRecord> it = this.schema.iterator();
        while (it.hasNext()) {
            MetaRecord next = it.next();
            if (next.getObjectType() == 6) {
                printWriter.println(next.getSQL() + ";");
            }
        }
    }

    private void writeSchema(PrintWriter printWriter) {
        printWriter.println("---- Schema ----");
        Collections.sort(this.schema);
        Iterator<MetaRecord> it = this.schema.iterator();
        while (it.hasNext()) {
            MetaRecord next = it.next();
            if (next.getObjectType() != 6 && !isSchemaObjectTypeDelayed(next)) {
                printWriter.println(next.getSQL() + ";");
            }
        }
        boolean z = false;
        for (Map.Entry<Integer, String> entry : this.tableMap.entrySet()) {
            Integer key = entry.getKey();
            String value = entry.getValue();
            if (this.objectIdSet.contains(key) && isLobTable(value)) {
                setStorage(key.intValue());
                printWriter.println("DELETE FROM " + value + ";");
                printWriter.println("INSERT INTO " + value + " SELECT * FROM " + this.storageName + ";");
                if (value.equals("INFORMATION_SCHEMA.LOBS") || value.equalsIgnoreCase("\"INFORMATION_SCHEMA\".\"LOBS\"")) {
                    printWriter.println("UPDATE " + value + " SET `TABLE` = -2;");
                    z = true;
                }
            }
        }
        for (Map.Entry<Integer, String> entry2 : this.tableMap.entrySet()) {
            Integer key2 = entry2.getKey();
            String value2 = entry2.getValue();
            if (this.objectIdSet.contains(key2)) {
                setStorage(key2.intValue());
                if (!isLobTable(value2)) {
                    printWriter.println("INSERT INTO " + value2 + " SELECT * FROM " + this.storageName + ";");
                }
            }
        }
        Iterator<Integer> it2 = this.objectIdSet.iterator();
        while (it2.hasNext()) {
            setStorage(it2.next().intValue());
            printWriter.println("DROP TABLE " + this.storageName + ";");
        }
        if (z) {
            printWriter.println("DELETE FROM INFORMATION_SCHEMA.LOBS WHERE `TABLE` = -2;");
        }
        ArrayList arrayList = new ArrayList();
        Iterator<MetaRecord> it3 = this.schema.iterator();
        while (it3.hasNext()) {
            MetaRecord next2 = it3.next();
            if (isSchemaObjectTypeDelayed(next2)) {
                String sql = next2.getSQL();
                if (next2.getObjectType() == 5 && sql.endsWith("NOCHECK") && sql.contains(" FOREIGN KEY") && sql.contains("REFERENCES ")) {
                    arrayList.add(sql);
                } else {
                    printWriter.println(sql + ';');
                }
            }
        }
        Iterator it4 = arrayList.iterator();
        while (it4.hasNext()) {
            printWriter.println(((String) it4.next()) + ';');
        }
    }

    private static boolean isLobTable(String str) {
        return str.startsWith("INFORMATION_SCHEMA.LOB") || str.startsWith("\"INFORMATION_SCHEMA\".\"LOB") || str.startsWith("\"information_schema\".\"lob");
    }

    private static boolean isSchemaObjectTypeDelayed(MetaRecord metaRecord) {
        switch (metaRecord.getObjectType()) {
            case 1:
            case 4:
            case 5:
                return true;
            case 2:
            case 3:
            default:
                return false;
        }
    }

    private void createTemporaryTable(PrintWriter printWriter) {
        if (!this.objectIdSet.contains(Integer.valueOf(this.storageId))) {
            this.objectIdSet.add(Integer.valueOf(this.storageId));
            printWriter.write("CREATE TABLE ");
            printWriter.write(this.storageName);
            printWriter.write(40);
            for (int i = 0; i < this.recordLength; i++) {
                if (i > 0) {
                    printWriter.print(", ");
                }
                printWriter.write(67);
                printWriter.print(i);
                printWriter.write(32);
                String str = this.columnTypeMap.get(this.storageName + "." + i);
                printWriter.write(str == null ? "VARCHAR" : str);
            }
            printWriter.println(");");
            printWriter.flush();
        }
    }

    private static String extractTableOrViewName(String str) {
        String substring;
        int indexOf = str.indexOf(" TABLE ");
        int indexOf2 = str.indexOf(" VIEW ");
        if (indexOf > 0 && indexOf2 > 0) {
            if (indexOf < indexOf2) {
                indexOf2 = -1;
            } else {
                indexOf = -1;
            }
        }
        if (indexOf2 > 0) {
            substring = str.substring(indexOf2 + " VIEW ".length());
        } else if (indexOf > 0) {
            substring = str.substring(indexOf + " TABLE ".length());
        } else {
            return "UNKNOWN";
        }
        if (substring.startsWith("IF NOT EXISTS ")) {
            substring = substring.substring("IF NOT EXISTS ".length());
        }
        boolean z = false;
        for (int i = 0; i < substring.length(); i++) {
            char charAt = substring.charAt(i);
            if (charAt == '\"') {
                z = !z;
            } else if (!z && (charAt <= ' ' || charAt == '(')) {
                return substring.substring(0, i);
            }
        }
        return "UNKNOWN";
    }

    private void writeError(PrintWriter printWriter, Throwable th) {
        if (printWriter != null) {
            printWriter.println("// error: " + th);
        }
        traceError("Error", th);
    }

    @Override // org.h2.store.DataHandler
    public String getDatabasePath() {
        return this.databaseName;
    }

    @Override // org.h2.store.DataHandler
    public FileStore openFile(String str, String str2, boolean z) {
        return FileStore.open(this, str, "rw");
    }

    @Override // org.h2.store.DataHandler
    public void checkPowerOff() {
    }

    @Override // org.h2.store.DataHandler
    public void checkWritingAllowed() {
    }

    @Override // org.h2.store.DataHandler
    public int getMaxLengthInplaceLob() {
        throw DbException.getInternalError();
    }

    @Override // org.h2.store.DataHandler
    public Object getLobSyncObject() {
        return this;
    }

    @Override // org.h2.store.DataHandler
    public SmallLRUCache<String, String[]> getLobFileListCache() {
        return null;
    }

    @Override // org.h2.store.DataHandler
    public TempFileDeleter getTempFileDeleter() {
        return TempFileDeleter.getInstance();
    }

    @Override // org.h2.store.DataHandler
    public LobStorageInterface getLobStorage() {
        return null;
    }

    @Override // org.h2.store.DataHandler
    public int readLob(long j, byte[] bArr, long j2, byte[] bArr2, int i, int i2) {
        throw DbException.getInternalError();
    }

    @Override // org.h2.store.DataHandler
    public CompareMode getCompareMode() {
        return CompareMode.getInstance(null, 0);
    }
}
