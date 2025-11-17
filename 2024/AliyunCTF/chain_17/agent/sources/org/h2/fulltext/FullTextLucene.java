package org.h2.fulltext;

import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexFormatTooOldException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.FSDirectory;
import org.h2.api.Trigger;
import org.h2.command.Parser;
import org.h2.engine.SessionLocal;
import org.h2.expression.ExpressionColumn;
import org.h2.jdbc.JdbcConnection;
import org.h2.store.fs.FileUtils;
import org.h2.tools.SimpleResultSet;
import org.h2.util.StringUtils;
import org.h2.util.Utils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/fulltext/FullTextLucene.class */
public class FullTextLucene extends FullText {
    protected static final boolean STORE_DOCUMENT_TEXT_IN_INDEX = Utils.getProperty("h2.storeDocumentTextInIndex", false);
    private static final HashMap<String, IndexAccess> INDEX_ACCESS = new HashMap<>();
    private static final String TRIGGER_PREFIX = "FTL_";
    private static final String SCHEMA = "FTL";
    private static final String LUCENE_FIELD_DATA = "_DATA";
    private static final String LUCENE_FIELD_QUERY = "_QUERY";
    private static final String LUCENE_FIELD_MODIFIED = "_modified";
    private static final String LUCENE_FIELD_COLUMN_PREFIX = "_";
    private static final String IN_MEMORY_PREFIX = "mem:";

    public static void init(Connection connection) throws SQLException {
        Statement createStatement = connection.createStatement();
        Throwable th = null;
        try {
            createStatement.execute("CREATE SCHEMA IF NOT EXISTS FTL");
            createStatement.execute("CREATE TABLE IF NOT EXISTS FTL.INDEXES(SCHEMA VARCHAR, `TABLE` VARCHAR, COLUMNS VARCHAR, PRIMARY KEY(SCHEMA, `TABLE`))");
            String name = FullTextLucene.class.getName();
            createStatement.execute("CREATE ALIAS IF NOT EXISTS FTL_CREATE_INDEX FOR '" + name + ".createIndex'");
            createStatement.execute("CREATE ALIAS IF NOT EXISTS FTL_DROP_INDEX FOR '" + name + ".dropIndex'");
            createStatement.execute("CREATE ALIAS IF NOT EXISTS FTL_SEARCH FOR '" + name + ".search'");
            createStatement.execute("CREATE ALIAS IF NOT EXISTS FTL_SEARCH_DATA FOR '" + name + ".searchData'");
            createStatement.execute("CREATE ALIAS IF NOT EXISTS FTL_REINDEX FOR '" + name + ".reindex'");
            createStatement.execute("CREATE ALIAS IF NOT EXISTS FTL_DROP_ALL FOR '" + name + ".dropAll'");
            if (createStatement != null) {
                if (0 != 0) {
                    try {
                        createStatement.close();
                        return;
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                        return;
                    }
                }
                createStatement.close();
            }
        } catch (Throwable th3) {
            if (createStatement != null) {
                if (0 != 0) {
                    try {
                        createStatement.close();
                    } catch (Throwable th4) {
                        th.addSuppressed(th4);
                    }
                } else {
                    createStatement.close();
                }
            }
            throw th3;
        }
    }

    public static void createIndex(Connection connection, String str, String str2, String str3) throws SQLException {
        init(connection);
        PreparedStatement prepareStatement = connection.prepareStatement("INSERT INTO FTL.INDEXES(SCHEMA, `TABLE`, COLUMNS) VALUES(?, ?, ?)");
        prepareStatement.setString(1, str);
        prepareStatement.setString(2, str2);
        prepareStatement.setString(3, str3);
        prepareStatement.execute();
        createTrigger(connection, str, str2);
        indexExistingRows(connection, str, str2);
    }

    public static void dropIndex(Connection connection, String str, String str2) throws SQLException {
        init(connection);
        PreparedStatement prepareStatement = connection.prepareStatement("DELETE FROM FTL.INDEXES WHERE SCHEMA=? AND `TABLE`=?");
        prepareStatement.setString(1, str);
        prepareStatement.setString(2, str2);
        if (prepareStatement.executeUpdate() != 0) {
            reindex(connection);
        }
    }

    public static void reindex(Connection connection) throws SQLException {
        init(connection);
        removeAllTriggers(connection, TRIGGER_PREFIX);
        removeIndexFiles(connection);
        ResultSet executeQuery = connection.createStatement().executeQuery("SELECT * FROM FTL.INDEXES");
        while (executeQuery.next()) {
            String string = executeQuery.getString("SCHEMA");
            String string2 = executeQuery.getString("TABLE");
            createTrigger(connection, string, string2);
            indexExistingRows(connection, string, string2);
        }
    }

    public static void dropAll(Connection connection) throws SQLException {
        connection.createStatement().execute("DROP SCHEMA IF EXISTS FTL CASCADE");
        removeAllTriggers(connection, TRIGGER_PREFIX);
        removeIndexFiles(connection);
    }

    public static ResultSet search(Connection connection, String str, int i, int i2) throws SQLException {
        return search(connection, str, i, i2, false);
    }

    public static ResultSet searchData(Connection connection, String str, int i, int i2) throws SQLException {
        return search(connection, str, i, i2, true);
    }

    protected static SQLException convertException(Exception exc) {
        return new SQLException("Error while indexing document", "FULLTEXT", exc);
    }

    private static void createTrigger(Connection connection, String str, String str2) throws SQLException {
        createOrDropTrigger(connection, str, str2, true);
    }

    private static void createOrDropTrigger(Connection connection, String str, String str2, boolean z) throws SQLException {
        Statement createStatement = connection.createStatement();
        String str3 = StringUtils.quoteIdentifier(str) + "." + StringUtils.quoteIdentifier(TRIGGER_PREFIX + str2);
        createStatement.execute("DROP TRIGGER IF EXISTS " + str3);
        if (z) {
            StringBuilder sb = new StringBuilder("CREATE TRIGGER IF NOT EXISTS ");
            sb.append(str3).append(" AFTER INSERT, UPDATE, DELETE, ROLLBACK ON ");
            StringUtils.quoteIdentifier(sb, str).append('.');
            StringUtils.quoteIdentifier(sb, str2).append(" FOR EACH ROW CALL \"").append(FullTextTrigger.class.getName()).append('\"');
            createStatement.execute(sb.toString());
        }
    }

    protected static IndexAccess getIndexAccess(Connection connection) throws SQLException {
        IndexAccess indexAccess;
        String indexPath = getIndexPath(connection);
        synchronized (INDEX_ACCESS) {
            IndexAccess indexAccess2 = INDEX_ACCESS.get(indexPath);
            while (true) {
                if (indexAccess2 != null) {
                    break;
                }
                try {
                    ByteBuffersDirectory byteBuffersDirectory = indexPath.startsWith(IN_MEMORY_PREFIX) ? new ByteBuffersDirectory() : FSDirectory.open(Paths.get(indexPath, new String[0]));
                    IndexWriterConfig indexWriterConfig = new IndexWriterConfig(new StandardAnalyzer());
                    indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
                    indexAccess2 = new IndexAccess(new IndexWriter(byteBuffersDirectory, indexWriterConfig));
                    INDEX_ACCESS.put(indexPath, indexAccess2);
                    break;
                } catch (IOException e) {
                    throw convertException(e);
                } catch (IndexFormatTooOldException e2) {
                    reindex(connection);
                }
            }
            indexAccess = indexAccess2;
        }
        return indexAccess;
    }

    protected static String getIndexPath(Connection connection) throws SQLException {
        ResultSet executeQuery = connection.createStatement().executeQuery("CALL DATABASE_PATH()");
        executeQuery.next();
        String string = executeQuery.getString(1);
        if (string == null) {
            return IN_MEMORY_PREFIX + connection.getCatalog();
        }
        int lastIndexOf = string.lastIndexOf(58);
        if (lastIndexOf > 1) {
            string = string.substring(lastIndexOf + 1);
        }
        executeQuery.close();
        return string;
    }

    private static void indexExistingRows(Connection connection, String str, String str2) throws SQLException {
        FullTextTrigger fullTextTrigger = new FullTextTrigger();
        fullTextTrigger.init(connection, str, null, str2, false, 1);
        ResultSet executeQuery = connection.createStatement().executeQuery("SELECT * FROM " + StringUtils.quoteIdentifier(str) + "." + StringUtils.quoteIdentifier(str2));
        int columnCount = executeQuery.getMetaData().getColumnCount();
        while (executeQuery.next()) {
            Object[] objArr = new Object[columnCount];
            for (int i = 0; i < columnCount; i++) {
                objArr[i] = executeQuery.getObject(i + 1);
            }
            fullTextTrigger.insert(objArr, false);
        }
        fullTextTrigger.commitIndex();
    }

    private static void removeIndexFiles(Connection connection) throws SQLException {
        String indexPath = getIndexPath(connection);
        removeIndexAccess(indexPath);
        if (!indexPath.startsWith(IN_MEMORY_PREFIX)) {
            FileUtils.deleteRecursive(indexPath, false);
        }
    }

    protected static void removeIndexAccess(String str) throws SQLException {
        synchronized (INDEX_ACCESS) {
            try {
                IndexAccess remove = INDEX_ACCESS.remove(str);
                if (remove != null) {
                    remove.close();
                }
            } catch (Exception e) {
                throw convertException(e);
            }
        }
    }

    /* JADX WARN: Finally extract failed */
    protected static ResultSet search(Connection connection, String str, int i, int i2, boolean z) throws SQLException {
        SimpleResultSet createResultSet = createResultSet(z);
        if (connection.getMetaData().getURL().startsWith("jdbc:columnlist:")) {
            return createResultSet;
        }
        if (str == null || StringUtils.isWhitespaceOrEmpty(str)) {
            return createResultSet;
        }
        try {
            IndexAccess indexAccess = getIndexAccess(connection);
            IndexSearcher searcher = indexAccess.getSearcher();
            try {
                TopDocs search = searcher.search(new StandardQueryParser(indexAccess.writer.getAnalyzer()).parse(str, LUCENE_FIELD_DATA), (i == 0 ? 100 : i) + i2);
                long j = search.totalHits.value;
                if (i == 0) {
                    i = (int) j;
                }
                int length = search.scoreDocs.length;
                for (int i3 = 0; i3 < i; i3++) {
                    if (i3 + i2 >= j || i3 + i2 >= length) {
                        break;
                    }
                    ScoreDoc scoreDoc = search.scoreDocs[i3 + i2];
                    Document doc = searcher.doc(scoreDoc.doc);
                    float f = scoreDoc.score;
                    String str2 = doc.get(LUCENE_FIELD_QUERY);
                    if (z) {
                        int indexOf = str2.indexOf(" WHERE ");
                        SessionLocal sessionLocal = (SessionLocal) ((JdbcConnection) connection).getSession();
                        ExpressionColumn expressionColumn = (ExpressionColumn) new Parser(sessionLocal).parseExpression(str2.substring(0, indexOf));
                        String originalTableAliasName = expressionColumn.getOriginalTableAliasName();
                        String columnName = expressionColumn.getColumnName(sessionLocal, -1);
                        String[][] parseKey = parseKey(connection, str2.substring(indexOf + " WHERE ".length()));
                        createResultSet.addRow(originalTableAliasName, columnName, parseKey[0], parseKey[1], Float.valueOf(f));
                    } else {
                        createResultSet.addRow(str2, Float.valueOf(f));
                    }
                }
                indexAccess.returnSearcher(searcher);
                return createResultSet;
            } catch (Throwable th) {
                indexAccess.returnSearcher(searcher);
                throw th;
            }
        } catch (Exception e) {
            throw convertException(e);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/fulltext/FullTextLucene$FullTextTrigger.class */
    public static final class FullTextTrigger implements Trigger {
        private String schema;
        private String table;
        private int[] keys;
        private int[] indexColumns;
        private String[] columns;
        private int[] columnTypes;
        private String indexPath;
        private IndexAccess indexAccess;
        private final FieldType DOC_ID_FIELD_TYPE = new FieldType(TextField.TYPE_STORED);

        public FullTextTrigger() {
            this.DOC_ID_FIELD_TYPE.setTokenized(false);
            this.DOC_ID_FIELD_TYPE.freeze();
        }

        @Override // org.h2.api.Trigger
        public void init(Connection connection, String str, String str2, String str3, boolean z, int i) throws SQLException {
            String string;
            this.schema = str;
            this.table = str3;
            this.indexPath = FullTextLucene.getIndexPath(connection);
            this.indexAccess = FullTextLucene.getIndexAccess(connection);
            ArrayList newSmallArrayList = Utils.newSmallArrayList();
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet columns = metaData.getColumns(null, StringUtils.escapeMetaDataPattern(str), StringUtils.escapeMetaDataPattern(str3), null);
            ArrayList newSmallArrayList2 = Utils.newSmallArrayList();
            while (columns.next()) {
                newSmallArrayList2.add(columns.getString("COLUMN_NAME"));
            }
            this.columnTypes = new int[newSmallArrayList2.size()];
            this.columns = (String[]) newSmallArrayList2.toArray(new String[0]);
            ResultSet columns2 = metaData.getColumns(null, StringUtils.escapeMetaDataPattern(str), StringUtils.escapeMetaDataPattern(str3), null);
            int i2 = 0;
            while (columns2.next()) {
                this.columnTypes[i2] = columns2.getInt("DATA_TYPE");
                i2++;
            }
            if (newSmallArrayList.isEmpty()) {
                ResultSet primaryKeys = metaData.getPrimaryKeys(null, StringUtils.escapeMetaDataPattern(str), str3);
                while (primaryKeys.next()) {
                    newSmallArrayList.add(primaryKeys.getString("COLUMN_NAME"));
                }
            }
            if (newSmallArrayList.isEmpty()) {
                throw FullText.throwException("No primary key for table " + str3);
            }
            ArrayList newSmallArrayList3 = Utils.newSmallArrayList();
            PreparedStatement prepareStatement = connection.prepareStatement("SELECT COLUMNS FROM FTL.INDEXES WHERE SCHEMA=? AND `TABLE`=?");
            prepareStatement.setString(1, str);
            prepareStatement.setString(2, str3);
            ResultSet executeQuery = prepareStatement.executeQuery();
            if (executeQuery.next() && (string = executeQuery.getString(1)) != null) {
                Collections.addAll(newSmallArrayList3, StringUtils.arraySplit(string, ',', true));
            }
            if (newSmallArrayList3.isEmpty()) {
                newSmallArrayList3.addAll(newSmallArrayList2);
            }
            this.keys = new int[newSmallArrayList.size()];
            FullText.setColumns(this.keys, newSmallArrayList, newSmallArrayList2);
            this.indexColumns = new int[newSmallArrayList3.size()];
            FullText.setColumns(this.indexColumns, newSmallArrayList3, newSmallArrayList2);
        }

        @Override // org.h2.api.Trigger
        public void fire(Connection connection, Object[] objArr, Object[] objArr2) throws SQLException {
            if (objArr != null) {
                if (objArr2 != null) {
                    if (FullText.hasChanged(objArr, objArr2, this.indexColumns)) {
                        delete(objArr, false);
                        insert(objArr2, true);
                        return;
                    }
                    return;
                }
                delete(objArr, true);
                return;
            }
            if (objArr2 != null) {
                insert(objArr2, true);
            }
        }

        @Override // org.h2.api.Trigger
        public void close() throws SQLException {
            FullTextLucene.removeIndexAccess(this.indexPath);
        }

        void commitIndex() throws SQLException {
            try {
                this.indexAccess.commit();
            } catch (IOException e) {
                throw FullTextLucene.convertException(e);
            }
        }

        void insert(Object[] objArr, boolean z) throws SQLException {
            String query = getQuery(objArr);
            Document document = new Document();
            document.add(new Field(FullTextLucene.LUCENE_FIELD_QUERY, query, this.DOC_ID_FIELD_TYPE));
            document.add(new Field(FullTextLucene.LUCENE_FIELD_MODIFIED, DateTools.timeToString(System.currentTimeMillis(), DateTools.Resolution.SECOND), TextField.TYPE_STORED));
            StringBuilder sb = new StringBuilder();
            int length = this.indexColumns.length;
            for (int i = 0; i < length; i++) {
                int i2 = this.indexColumns[i];
                String str = this.columns[i2];
                String asString = FullText.asString(objArr[i2], this.columnTypes[i2]);
                if (str.startsWith("_")) {
                    str = "_" + str;
                }
                document.add(new Field(str, asString, TextField.TYPE_NOT_STORED));
                if (i > 0) {
                    sb.append(' ');
                }
                sb.append(asString);
            }
            document.add(new Field(FullTextLucene.LUCENE_FIELD_DATA, sb.toString(), FullTextLucene.STORE_DOCUMENT_TEXT_IN_INDEX ? TextField.TYPE_STORED : TextField.TYPE_NOT_STORED));
            try {
                this.indexAccess.writer.addDocument(document);
                if (z) {
                    commitIndex();
                }
            } catch (IOException e) {
                throw FullTextLucene.convertException(e);
            }
        }

        private void delete(Object[] objArr, boolean z) throws SQLException {
            try {
                this.indexAccess.writer.deleteDocuments(new Term[]{new Term(FullTextLucene.LUCENE_FIELD_QUERY, getQuery(objArr))});
                if (z) {
                    commitIndex();
                }
            } catch (IOException e) {
                throw FullTextLucene.convertException(e);
            }
        }

        private String getQuery(Object[] objArr) throws SQLException {
            StringBuilder sb = new StringBuilder();
            if (this.schema != null) {
                StringUtils.quoteIdentifier(sb, this.schema).append('.');
            }
            StringUtils.quoteIdentifier(sb, this.table).append(" WHERE ");
            int length = this.keys.length;
            for (int i = 0; i < length; i++) {
                if (i > 0) {
                    sb.append(" AND ");
                }
                int i2 = this.keys[i];
                StringUtils.quoteIdentifier(sb, this.columns[i2]);
                Object obj = objArr[i2];
                if (obj == null) {
                    sb.append(" IS NULL");
                } else {
                    sb.append('=').append(FullText.quoteSQL(obj, this.columnTypes[i2]));
                }
            }
            return sb.toString();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/fulltext/FullTextLucene$IndexAccess.class */
    public static final class IndexAccess {
        final IndexWriter writer;
        private IndexSearcher searcher;

        IndexAccess(IndexWriter indexWriter) throws IOException {
            this.writer = indexWriter;
            initializeSearcher();
        }

        synchronized IndexSearcher getSearcher() throws IOException {
            if (!this.searcher.getIndexReader().tryIncRef()) {
                initializeSearcher();
            }
            return this.searcher;
        }

        private void initializeSearcher() throws IOException {
            this.searcher = new IndexSearcher(DirectoryReader.open(this.writer));
        }

        synchronized void returnSearcher(IndexSearcher indexSearcher) throws IOException {
            indexSearcher.getIndexReader().decRef();
        }

        public synchronized void commit() throws IOException {
            this.writer.commit();
            returnSearcher(this.searcher);
            this.searcher = new IndexSearcher(DirectoryReader.open(this.writer));
        }

        public synchronized void close() throws IOException {
            this.searcher = null;
            this.writer.close();
        }
    }
}
