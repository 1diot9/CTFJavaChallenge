package org.h2.fulltext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import org.h2.util.SoftValuesHashMap;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/fulltext/FullTextSettings.class */
final class FullTextSettings {
    private static final HashMap<String, FullTextSettings> SETTINGS = new HashMap<>();
    private boolean initialized;
    private final HashSet<String> ignoreList = new HashSet<>();
    private final HashMap<String, Integer> words = new HashMap<>();
    private final ConcurrentHashMap<Integer, IndexInfo> indexes = new ConcurrentHashMap<>();
    private final WeakHashMap<Connection, SoftValuesHashMap<String, PreparedStatement>> cache = new WeakHashMap<>();
    private String whitespaceChars = " \t\n\r\f+\"*%&/()=?'!,.;:-_#@|^~`{}[]<>\\";

    private FullTextSettings() {
    }

    public void clearIgnored() {
        synchronized (this.ignoreList) {
            this.ignoreList.clear();
        }
    }

    public void addIgnored(Iterable<String> iterable) {
        synchronized (this.ignoreList) {
            Iterator<String> it = iterable.iterator();
            while (it.hasNext()) {
                this.ignoreList.add(normalizeWord(it.next()));
            }
        }
    }

    public void clearWordList() {
        synchronized (this.words) {
            this.words.clear();
        }
    }

    public Integer getWordId(String str) {
        Integer num;
        synchronized (this.words) {
            num = this.words.get(str);
        }
        return num;
    }

    public void addWord(String str, Integer num) {
        synchronized (this.words) {
            this.words.putIfAbsent(str, num);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public IndexInfo getIndexInfo(int i) {
        return this.indexes.get(Integer.valueOf(i));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addIndexInfo(IndexInfo indexInfo) {
        this.indexes.put(Integer.valueOf(indexInfo.id), indexInfo);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String convertWord(String str) {
        String normalizeWord = normalizeWord(str);
        synchronized (this.ignoreList) {
            if (this.ignoreList.contains(normalizeWord)) {
                return null;
            }
            return normalizeWord;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static FullTextSettings getInstance(Connection connection) throws SQLException {
        FullTextSettings fullTextSettings;
        String indexPath = getIndexPath(connection);
        synchronized (SETTINGS) {
            fullTextSettings = SETTINGS.get(indexPath);
            if (fullTextSettings == null) {
                fullTextSettings = new FullTextSettings();
                SETTINGS.put(indexPath, fullTextSettings);
            }
        }
        return fullTextSettings;
    }

    private static String getIndexPath(Connection connection) throws SQLException {
        ResultSet executeQuery = connection.createStatement().executeQuery("CALL COALESCE(DATABASE_PATH(), 'MEM:' || DATABASE())");
        executeQuery.next();
        String string = executeQuery.getString(1);
        if ("MEM:UNNAMED".equals(string)) {
            throw FullText.throwException("Fulltext search for private (unnamed) in-memory databases is not supported.");
        }
        executeQuery.close();
        return string;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized PreparedStatement prepare(Connection connection, String str) throws SQLException {
        SoftValuesHashMap<String, PreparedStatement> softValuesHashMap = this.cache.get(connection);
        if (softValuesHashMap == null) {
            softValuesHashMap = new SoftValuesHashMap<>();
            this.cache.put(connection, softValuesHashMap);
        }
        PreparedStatement preparedStatement = softValuesHashMap.get(str);
        if (preparedStatement != null && preparedStatement.getConnection().isClosed()) {
            preparedStatement = null;
        }
        if (preparedStatement == null) {
            preparedStatement = connection.prepareStatement(str);
            softValuesHashMap.put(str, preparedStatement);
        }
        return preparedStatement;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeAllIndexes() {
        this.indexes.clear();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removeIndexInfo(IndexInfo indexInfo) {
        this.indexes.remove(Integer.valueOf(indexInfo.id));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setInitialized(boolean z) {
        this.initialized = z;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isInitialized() {
        return this.initialized;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void closeAll() {
        synchronized (SETTINGS) {
            SETTINGS.clear();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setWhitespaceChars(String str) {
        this.whitespaceChars = str;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getWhitespaceChars() {
        return this.whitespaceChars;
    }

    private static String normalizeWord(String str) {
        return str.toUpperCase();
    }
}
