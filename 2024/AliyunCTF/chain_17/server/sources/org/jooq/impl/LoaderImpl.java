package org.jooq.impl;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.jooq.BatchBindStep;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.ExecuteContext;
import org.jooq.ExecuteListener;
import org.jooq.ExecuteListenerProvider;
import org.jooq.Field;
import org.jooq.InsertQuery;
import org.jooq.Loader;
import org.jooq.LoaderCSVOptionsStep;
import org.jooq.LoaderCSVStep;
import org.jooq.LoaderContext;
import org.jooq.LoaderError;
import org.jooq.LoaderFieldMapper;
import org.jooq.LoaderJSONOptionsStep;
import org.jooq.LoaderJSONStep;
import org.jooq.LoaderListenerStep;
import org.jooq.LoaderOptionsStep;
import org.jooq.LoaderRowListener;
import org.jooq.LoaderRowsStep;
import org.jooq.LoaderXMLStep;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.Source;
import org.jooq.Table;
import org.jooq.exception.DataAccessException;
import org.jooq.exception.LoaderConfigurationException;
import org.jooq.tools.JooqLogger;
import org.jooq.tools.StringUtils;
import org.jooq.tools.csv.CSVReader;
import org.jooq.tools.jdbc.DefaultPreparedStatement;
import org.jooq.tools.jdbc.JDBCUtils;
import org.xml.sax.InputSource;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/LoaderImpl.class */
final class LoaderImpl<R extends Record> implements LoaderOptionsStep<R>, LoaderRowsStep<R>, LoaderXMLStep<R>, LoaderCSVStep<R>, LoaderCSVOptionsStep<R>, LoaderJSONStep<R>, LoaderJSONOptionsStep<R>, Loader<R> {
    private static final JooqLogger log = JooqLogger.getLogger((Class<?>) LoaderImpl.class);
    private static final Set<SQLDialect> NO_SUPPORT_ROWCOUNT_ON_DUPLICATE = SQLDialect.supportedBy(SQLDialect.MARIADB, SQLDialect.MYSQL);
    private static final int ON_DUPLICATE_KEY_ERROR = 0;
    private static final int ON_DUPLICATE_KEY_IGNORE = 1;
    private static final int ON_DUPLICATE_KEY_UPDATE = 2;
    private static final int ON_ERROR_ABORT = 0;
    private static final int ON_ERROR_IGNORE = 1;
    private static final int COMMIT_NONE = 0;
    private static final int COMMIT_AFTER = 1;
    private static final int COMMIT_ALL = 2;
    private static final int BATCH_NONE = 0;
    private static final int BATCH_AFTER = 1;
    private static final int BATCH_ALL = 2;
    private static final int BULK_NONE = 0;
    private static final int BULK_AFTER = 1;
    private static final int BULK_ALL = 2;
    private static final int CONTENT_CSV = 0;
    private static final int CONTENT_XML = 1;
    private static final int CONTENT_JSON = 2;
    private static final int CONTENT_ARRAYS = 3;
    private final Configuration configuration;
    private final Table<R> table;
    private Source input;
    private Iterator<? extends Object[]> arrays;
    private Field<?>[] source;
    private Field<?>[] fields;
    private LoaderFieldMapper fieldMapper;
    private boolean fieldsCorresponding;
    private java.util.BitSet primaryKey;
    private LoaderRowListener onRowStart;
    private LoaderRowListener onRowEnd;
    private int ignored;
    private int processed;
    private int stored;
    private int executed;
    private int unexecuted;
    private int uncommitted;
    private int onDuplicate = 0;
    private int onError = 0;
    private int commit = 0;
    private int commitAfter = 1;
    private int batch = 0;
    private int batchAfter = 1;
    private int bulk = 0;
    private int bulkAfter = 1;
    private int content = 0;
    private int ignoreRows = 1;
    private char quote = '\"';
    private char separator = ',';
    private String nullString = null;
    private final LoaderContext rowCtx = new DefaultLoaderContext();
    private final List<LoaderError> errors = new ArrayList();

    @Override // org.jooq.LoaderRowsStep, org.jooq.LoaderCSVStep, org.jooq.LoaderJSONStep
    public /* bridge */ /* synthetic */ LoaderListenerStep fields(Collection collection) {
        return fields((Collection<? extends Field<?>>) collection);
    }

    @Override // org.jooq.LoaderRowsStep, org.jooq.LoaderCSVStep, org.jooq.LoaderJSONStep
    public /* bridge */ /* synthetic */ LoaderListenerStep fields(Field[] fieldArr) {
        return fields((Field<?>[]) fieldArr);
    }

    @Override // org.jooq.LoaderRowsStep, org.jooq.LoaderCSVStep, org.jooq.LoaderJSONStep
    public /* bridge */ /* synthetic */ LoaderCSVOptionsStep fields(Collection collection) {
        return fields((Collection<? extends Field<?>>) collection);
    }

    @Override // org.jooq.LoaderRowsStep, org.jooq.LoaderCSVStep, org.jooq.LoaderJSONStep
    public /* bridge */ /* synthetic */ LoaderCSVOptionsStep fields(Field[] fieldArr) {
        return fields((Field<?>[]) fieldArr);
    }

    @Override // org.jooq.LoaderRowsStep, org.jooq.LoaderCSVStep, org.jooq.LoaderJSONStep
    public /* bridge */ /* synthetic */ LoaderJSONOptionsStep fields(Collection collection) {
        return fields((Collection<? extends Field<?>>) collection);
    }

    @Override // org.jooq.LoaderRowsStep, org.jooq.LoaderCSVStep, org.jooq.LoaderJSONStep
    public /* bridge */ /* synthetic */ LoaderJSONOptionsStep fields(Field[] fieldArr) {
        return fields((Field<?>[]) fieldArr);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public LoaderImpl(Configuration configuration, Table<R> table) {
        this.configuration = configuration;
        this.table = table;
    }

    @Override // org.jooq.LoaderOptionsStep
    public final LoaderImpl<R> onDuplicateKeyError() {
        this.onDuplicate = 0;
        return this;
    }

    @Override // org.jooq.LoaderOptionsStep
    public final LoaderImpl<R> onDuplicateKeyIgnore() {
        if (this.table.getPrimaryKey() == null) {
            throw new IllegalStateException("ON DUPLICATE KEY IGNORE only works on tables with explicit primary keys. Table is not updatable : " + String.valueOf(this.table));
        }
        this.onDuplicate = 1;
        return this;
    }

    @Override // org.jooq.LoaderOptionsStep
    public final LoaderImpl<R> onDuplicateKeyUpdate() {
        if (this.table.getPrimaryKey() == null) {
            throw new IllegalStateException("ON DUPLICATE KEY UPDATE only works on tables with explicit primary keys. Table is not updatable : " + String.valueOf(this.table));
        }
        this.onDuplicate = 2;
        return this;
    }

    @Override // org.jooq.LoaderOptionsStep
    public final LoaderImpl<R> onErrorIgnore() {
        this.onError = 1;
        return this;
    }

    @Override // org.jooq.LoaderOptionsStep
    public final LoaderImpl<R> onErrorAbort() {
        this.onError = 0;
        return this;
    }

    @Override // org.jooq.LoaderOptionsStep
    public final LoaderImpl<R> commitEach() {
        this.commit = 1;
        return this;
    }

    @Override // org.jooq.LoaderOptionsStep
    public final LoaderImpl<R> commitAfter(int number) {
        this.commit = 1;
        this.commitAfter = number;
        return this;
    }

    @Override // org.jooq.LoaderOptionsStep
    public final LoaderImpl<R> commitAll() {
        this.commit = 2;
        return this;
    }

    @Override // org.jooq.LoaderOptionsStep
    public final LoaderImpl<R> commitNone() {
        this.commit = 0;
        return this;
    }

    @Override // org.jooq.LoaderOptionsStep
    public final LoaderImpl<R> batchAll() {
        this.batch = 2;
        return this;
    }

    @Override // org.jooq.LoaderOptionsStep
    public final LoaderImpl<R> batchNone() {
        this.batch = 0;
        return this;
    }

    @Override // org.jooq.LoaderOptionsStep
    public final LoaderImpl<R> batchAfter(int number) {
        this.batch = 1;
        this.batchAfter = number;
        return this;
    }

    @Override // org.jooq.LoaderOptionsStep
    public final LoaderImpl<R> bulkAll() {
        this.bulk = 2;
        return this;
    }

    @Override // org.jooq.LoaderOptionsStep
    public final LoaderImpl<R> bulkNone() {
        this.bulk = 0;
        return this;
    }

    @Override // org.jooq.LoaderOptionsStep
    public final LoaderImpl<R> bulkAfter(int number) {
        this.bulk = 1;
        this.bulkAfter = number;
        return this;
    }

    @Override // org.jooq.LoaderSourceStep
    public final LoaderRowsStep<R> loadArrays(Object[]... a) {
        return loadArrays(Arrays.asList(a));
    }

    @Override // org.jooq.LoaderSourceStep
    public final LoaderRowsStep<R> loadArrays(Iterable<? extends Object[]> a) {
        return loadArrays(a.iterator());
    }

    @Override // org.jooq.LoaderSourceStep
    public final LoaderRowsStep<R> loadArrays(Iterator<? extends Object[]> a) {
        this.content = 3;
        this.arrays = a;
        return this;
    }

    @Override // org.jooq.LoaderSourceStep
    public final LoaderRowsStep<R> loadRecords(Record... records) {
        return loadRecords(Arrays.asList(records));
    }

    @Override // org.jooq.LoaderSourceStep
    public final LoaderRowsStep<R> loadRecords(Iterable<? extends Record> records) {
        return loadRecords(records.iterator());
    }

    @Override // org.jooq.LoaderSourceStep
    public final LoaderRowsStep<R> loadRecords(Iterator<? extends Record> records) {
        return loadArrays(Tools.iterator(records, value -> {
            if (value == null) {
                return null;
            }
            if (this.source == null) {
                this.source = value.fields();
            }
            return value.intoArray();
        }));
    }

    @Override // org.jooq.LoaderSourceStep
    public final LoaderRowsStep<R> loadArrays(Stream<? extends Object[]> a) {
        return loadArrays(a.iterator());
    }

    @Override // org.jooq.LoaderSourceStep
    public final LoaderRowsStep<R> loadRecords(Stream<? extends Record> records) {
        return loadRecords(records.iterator());
    }

    @Override // org.jooq.LoaderSourceStep
    public final LoaderImpl<R> loadCSV(File file) {
        return loadCSV(Source.of(file));
    }

    @Override // org.jooq.LoaderSourceStep
    public final LoaderImpl<R> loadCSV(File file, String charsetName) {
        return loadCSV(Source.of(file, charsetName));
    }

    @Override // org.jooq.LoaderSourceStep
    public final LoaderImpl<R> loadCSV(File file, Charset cs) {
        return loadCSV(Source.of(file, cs));
    }

    @Override // org.jooq.LoaderSourceStep
    public final LoaderImpl<R> loadCSV(File file, CharsetDecoder dec) {
        return loadCSV(Source.of(file, dec));
    }

    @Override // org.jooq.LoaderSourceStep
    public final LoaderImpl<R> loadCSV(String csv) {
        return loadCSV(Source.of(csv));
    }

    @Override // org.jooq.LoaderSourceStep
    public final LoaderImpl<R> loadCSV(InputStream stream) {
        return loadCSV(Source.of(stream));
    }

    @Override // org.jooq.LoaderSourceStep
    public final LoaderImpl<R> loadCSV(InputStream stream, String charsetName) {
        return loadCSV(Source.of(stream, charsetName));
    }

    @Override // org.jooq.LoaderSourceStep
    public final LoaderImpl<R> loadCSV(InputStream stream, Charset cs) {
        return loadCSV(Source.of(stream, cs));
    }

    @Override // org.jooq.LoaderSourceStep
    public final LoaderImpl<R> loadCSV(InputStream stream, CharsetDecoder dec) {
        return loadCSV(Source.of(stream, dec));
    }

    @Override // org.jooq.LoaderSourceStep
    public final LoaderImpl<R> loadCSV(Reader reader) {
        return loadCSV(Source.of(reader));
    }

    @Override // org.jooq.LoaderSourceStep
    public final LoaderImpl<R> loadCSV(Source s) {
        this.content = 0;
        this.input = s;
        return this;
    }

    @Override // org.jooq.LoaderSourceStep
    public final LoaderImpl<R> loadXML(File file) {
        return loadXML(Source.of(file));
    }

    @Override // org.jooq.LoaderSourceStep
    public final LoaderImpl<R> loadXML(File file, String charsetName) {
        return loadXML(Source.of(file, charsetName));
    }

    @Override // org.jooq.LoaderSourceStep
    public final LoaderImpl<R> loadXML(File file, Charset cs) {
        return loadXML(Source.of(file, cs));
    }

    @Override // org.jooq.LoaderSourceStep
    public final LoaderImpl<R> loadXML(File file, CharsetDecoder dec) {
        return loadXML(Source.of(file, dec));
    }

    @Override // org.jooq.LoaderSourceStep
    public final LoaderImpl<R> loadXML(String xml) {
        return loadXML(Source.of(xml));
    }

    @Override // org.jooq.LoaderSourceStep
    public final LoaderImpl<R> loadXML(InputStream stream) {
        return loadXML(Source.of(stream));
    }

    @Override // org.jooq.LoaderSourceStep
    public final LoaderImpl<R> loadXML(InputStream stream, String charsetName) {
        return loadXML(Source.of(stream, charsetName));
    }

    @Override // org.jooq.LoaderSourceStep
    public final LoaderImpl<R> loadXML(InputStream stream, Charset cs) {
        return loadXML(Source.of(stream, cs));
    }

    @Override // org.jooq.LoaderSourceStep
    public final LoaderImpl<R> loadXML(InputStream stream, CharsetDecoder dec) {
        return loadXML(Source.of(stream, dec));
    }

    @Override // org.jooq.LoaderSourceStep
    public final LoaderImpl<R> loadXML(Reader reader) {
        return loadXML(Source.of(reader));
    }

    @Override // org.jooq.LoaderSourceStep
    public final LoaderImpl<R> loadXML(InputSource s) {
        this.content = 1;
        throw new UnsupportedOperationException("This is not yet implemented");
    }

    @Override // org.jooq.LoaderSourceStep
    public final LoaderImpl<R> loadXML(Source s) {
        this.content = 1;
        this.input = s;
        throw new UnsupportedOperationException("This is not yet implemented");
    }

    @Override // org.jooq.LoaderSourceStep
    public final LoaderImpl<R> loadJSON(File file) {
        return loadJSON(Source.of(file));
    }

    @Override // org.jooq.LoaderSourceStep
    public final LoaderImpl<R> loadJSON(File file, String charsetName) {
        return loadJSON(Source.of(file, charsetName));
    }

    @Override // org.jooq.LoaderSourceStep
    public final LoaderImpl<R> loadJSON(File file, Charset cs) {
        return loadJSON(Source.of(file, cs));
    }

    @Override // org.jooq.LoaderSourceStep
    public final LoaderImpl<R> loadJSON(File file, CharsetDecoder dec) {
        return loadJSON(Source.of(file, dec));
    }

    @Override // org.jooq.LoaderSourceStep
    public final LoaderImpl<R> loadJSON(String json) {
        return loadJSON(Source.of(json));
    }

    @Override // org.jooq.LoaderSourceStep
    public final LoaderImpl<R> loadJSON(InputStream stream) {
        return loadJSON(Source.of(stream));
    }

    @Override // org.jooq.LoaderSourceStep
    public final LoaderImpl<R> loadJSON(InputStream stream, String charsetName) {
        return loadJSON(Source.of(stream, charsetName));
    }

    @Override // org.jooq.LoaderSourceStep
    public final LoaderImpl<R> loadJSON(InputStream stream, Charset cs) {
        return loadJSON(Source.of(stream, cs));
    }

    @Override // org.jooq.LoaderSourceStep
    public final LoaderImpl<R> loadJSON(InputStream stream, CharsetDecoder dec) {
        return loadJSON(Source.of(stream, dec));
    }

    @Override // org.jooq.LoaderSourceStep
    public final LoaderImpl<R> loadJSON(Reader reader) {
        return loadJSON(Source.of(reader));
    }

    @Override // org.jooq.LoaderSourceStep
    public final LoaderImpl<R> loadJSON(Source s) {
        this.content = 2;
        this.input = s;
        return this;
    }

    @Override // org.jooq.LoaderRowsStep, org.jooq.LoaderCSVStep, org.jooq.LoaderJSONStep
    public final LoaderImpl<R> fields(Field<?>... f) {
        this.fields = f;
        this.primaryKey = new java.util.BitSet(f.length);
        if (this.table.getPrimaryKey() != null) {
            for (int i = 0; i < this.fields.length; i++) {
                if (this.fields[i] != null && this.table.getPrimaryKey().getFields().contains(this.fields[i])) {
                    this.primaryKey.set(i);
                }
            }
        }
        return this;
    }

    @Override // org.jooq.LoaderRowsStep, org.jooq.LoaderCSVStep, org.jooq.LoaderJSONStep
    public final LoaderImpl<R> fields(Collection<? extends Field<?>> f) {
        return fields((Field<?>[]) f.toArray(Tools.EMPTY_FIELD));
    }

    @Override // org.jooq.LoaderRowsStep, org.jooq.LoaderCSVStep, org.jooq.LoaderJSONStep
    public final LoaderImpl<R> fields(LoaderFieldMapper mapper) {
        this.fieldMapper = mapper;
        return this;
    }

    @Override // org.jooq.LoaderRowsStep, org.jooq.LoaderCSVStep, org.jooq.LoaderJSONStep
    @Deprecated
    public LoaderImpl<R> fieldsFromSource() {
        return fieldsCorresponding();
    }

    @Override // org.jooq.LoaderRowsStep, org.jooq.LoaderCSVStep, org.jooq.LoaderJSONStep
    public LoaderImpl<R> fieldsCorresponding() {
        this.fieldsCorresponding = true;
        return this;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private final void fields0(Object[] row) {
        Field<?>[] f = new Field[row.length];
        if (this.source == null) {
            if (this.fieldsCorresponding) {
                throw new LoaderConfigurationException("Using fieldsCorresponding() requires field names to be available in source.");
            }
            this.source = Tools.fields(row.length);
        }
        if (this.fieldMapper != null) {
            for (int i = 0; i < row.length; i++) {
                final int index = i;
                f[i] = this.fieldMapper.map(new LoaderFieldMapper.LoaderFieldContext() { // from class: org.jooq.impl.LoaderImpl.1
                    @Override // org.jooq.LoaderFieldMapper.LoaderFieldContext
                    public int index() {
                        return index;
                    }

                    @Override // org.jooq.LoaderFieldMapper.LoaderFieldContext
                    public Field<?> field() {
                        return LoaderImpl.this.source[index];
                    }
                });
            }
        } else if (this.fieldsCorresponding) {
            for (int i2 = 0; i2 < row.length; i2++) {
                f[i2] = this.table.field(this.source[i2]);
                if (f[i2] == null) {
                    log.info("No column in target table " + String.valueOf(this.table) + " found for input field " + String.valueOf(this.source[i2]));
                }
            }
        }
        fields(f);
    }

    @Override // org.jooq.LoaderJSONOptionsStep
    public final LoaderImpl<R> ignoreRows(int number) {
        this.ignoreRows = number;
        return this;
    }

    @Override // org.jooq.LoaderCSVOptionsStep
    public final LoaderImpl<R> quote(char q) {
        this.quote = q;
        return this;
    }

    @Override // org.jooq.LoaderCSVOptionsStep
    public final LoaderImpl<R> separator(char s) {
        this.separator = s;
        return this;
    }

    @Override // org.jooq.LoaderCSVOptionsStep
    public final LoaderImpl<R> nullString(String n) {
        this.nullString = n;
        return this;
    }

    @Override // org.jooq.LoaderListenerStep
    public final LoaderImpl<R> onRow(LoaderRowListener l) {
        return onRowEnd(l);
    }

    @Override // org.jooq.LoaderListenerStep
    public final LoaderImpl<R> onRowStart(LoaderRowListener l) {
        this.onRowStart = l;
        return this;
    }

    @Override // org.jooq.LoaderListenerStep
    public final LoaderImpl<R> onRowEnd(LoaderRowListener l) {
        this.onRowEnd = l;
        return this;
    }

    @Override // org.jooq.LoaderLoadStep
    public final LoaderImpl<R> execute() throws IOException {
        checkFlags();
        if (this.content == 0) {
            executeCSV();
        } else {
            if (this.content == 1) {
                throw new UnsupportedOperationException();
            }
            if (this.content == 2) {
                executeJSON();
            } else if (this.content == 3) {
                executeRows();
            } else {
                throw new IllegalStateException();
            }
        }
        return this;
    }

    private final void checkFlags() {
        if (this.bulk != 0 && this.onDuplicate != 0) {
            throw new LoaderConfigurationException("Cannot apply bulk loading with onDuplicateKey flags. Turn off either flag.");
        }
    }

    private final void executeJSON() {
        Reader reader = null;
        try {
            reader = this.input.reader();
            Result<Record> r = new JSONReader(this.configuration.dsl(), null, null, false).read(reader);
            this.source = r.fields();
            List<Object[]> allRecords = Arrays.asList(r.intoArrays());
            executeSQL(allRecords.iterator());
            JDBCUtils.safeClose((Closeable) reader);
        } catch (Throwable th) {
            JDBCUtils.safeClose((Closeable) reader);
            throw th;
        }
    }

    private final void executeCSV() {
        CSVReader reader = null;
        try {
            if (this.ignoreRows == 1) {
                reader = new CSVReader(this.input.reader(), this.separator, this.quote, 0);
                this.source = (Field[]) Tools.fieldsByName(reader.next()).toArray(Tools.EMPTY_FIELD);
            } else {
                reader = new CSVReader(this.input.reader(), this.separator, this.quote, this.ignoreRows);
            }
            executeSQL(reader);
            JDBCUtils.safeClose((Closeable) reader);
        } catch (Throwable th) {
            JDBCUtils.safeClose((Closeable) reader);
            throw th;
        }
    }

    private final void executeRows() {
        executeSQL(this.arrays);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/LoaderImpl$CachedPSListener.class */
    private static final class CachedPSListener implements ExecuteListener, AutoCloseable {
        final Map<String, CachedPS> map = new HashMap();

        private CachedPSListener() {
        }

        @Override // org.jooq.ExecuteListener
        public void prepareStart(ExecuteContext ctx) {
            CachedPS ps = this.map.get(ctx.sql());
            if (ps != null) {
                ctx.statement(ps);
            }
        }

        @Override // org.jooq.ExecuteListener
        public void prepareEnd(ExecuteContext ctx) {
            if (!(ctx.statement() instanceof CachedPS)) {
                CachedPS ps = new CachedPS(ctx.statement());
                this.map.put(ctx.sql(), ps);
                ctx.statement(ps);
            }
        }

        @Override // java.lang.AutoCloseable
        public void close() throws SQLException {
            for (CachedPS ps : this.map.values()) {
                JDBCUtils.safeClose((Statement) ps.getDelegate());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/LoaderImpl$CachedPS.class */
    public static class CachedPS extends DefaultPreparedStatement {
        CachedPS(PreparedStatement delegate) {
            super(delegate);
        }

        @Override // org.jooq.tools.jdbc.DefaultStatement, java.sql.Statement, java.lang.AutoCloseable
        public void close() throws SQLException {
        }
    }

    private final void executeSQL(Iterator<? extends Object[]> iterator) {
        this.configuration.dsl().connection(connection -> {
            Configuration c = this.configuration.derive(new DefaultConnectionProvider(connection));
            if (Boolean.FALSE.equals(c.settings().isCachePreparedStatementInLoader())) {
                executeSQL(iterator, c.dsl());
                return;
            }
            CachedPSListener cache = new CachedPSListener();
            try {
                executeSQL(iterator, c.derive((ExecuteListenerProvider[]) Tools.combine(new DefaultExecuteListenerProvider(cache), (DefaultExecuteListenerProvider[]) c.executeListenerProviders())).dsl());
                cache.close();
            } catch (Throwable th) {
                try {
                    cache.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    private final void executeSQL(Iterator<? extends Object[]> iterator, DSLContext ctx) {
        Object[] row = null;
        BatchBindStep bind = null;
        InsertQuery<R> insert = null;
        boolean newRecord = false;
        while (iterator.hasNext()) {
            Object[] next = iterator.next();
            row = next;
            if (next == null) {
                break;
            }
            try {
                if (row.getClass() != Object[].class) {
                    row = Arrays.copyOf(row, row.length, Object[].class);
                }
                if (this.fields == null) {
                    fields0(row);
                }
                for (int i = 0; i < row.length; i++) {
                    if (StringUtils.equals(this.nullString, row[i])) {
                        row[i] = null;
                    } else if (i < this.fields.length && this.fields[i] != null && this.fields[i].getType() == byte[].class && (row[i] instanceof String)) {
                        row[i] = Base64.getDecoder().decode((String) row[i]);
                    }
                }
                if (row.length < this.fields.length) {
                    row = Arrays.copyOf(row, this.fields.length);
                }
                this.rowCtx.row(row);
                if (this.onRowStart != null) {
                    this.onRowStart.row(this.rowCtx);
                    row = this.rowCtx.row();
                }
                this.processed++;
                this.unexecuted++;
                this.uncommitted++;
                if (insert == null) {
                    insert = ctx.insertQuery(this.table);
                }
                if (newRecord) {
                    newRecord = false;
                    insert.newRecord();
                }
                for (int i2 = 0; i2 < row.length; i2++) {
                    if (i2 < this.fields.length && this.fields[i2] != null) {
                        addValue0(insert, this.fields[i2], row[i2]);
                    }
                }
                if (this.onDuplicate == 2) {
                    insert.onDuplicateKeyUpdate(true);
                    for (int i3 = 0; i3 < row.length; i3++) {
                        if (i3 < this.fields.length && this.fields[i3] != null && !this.primaryKey.get(i3)) {
                            addValueForUpdate0(insert, this.fields[i3], row[i3]);
                        }
                    }
                } else if (this.onDuplicate == 1) {
                    insert.onDuplicateKeyIgnore(true);
                } else if (this.onDuplicate == 0) {
                }
                try {
                } catch (DataAccessException e) {
                    this.errors.add(new LoaderErrorImpl(e, row, this.processed - 1, insert));
                    this.ignored += this.unexecuted;
                    this.unexecuted = 0;
                    if (this.onError == 0) {
                        if (this.onRowEnd != null) {
                            this.onRowEnd.row(this.rowCtx);
                        }
                    }
                }
                if (this.bulk == 0 || (this.bulk != 2 && this.processed % this.bulkAfter == 0)) {
                    if (this.batch != 0) {
                        if (bind == null) {
                            bind = ctx.batch(insert);
                        }
                        bind.bind(insert.getBindValues().toArray());
                        insert = null;
                        if (this.batch == 2 || this.processed % (this.bulkAfter * this.batchAfter) != 0) {
                            if (this.onRowEnd != null) {
                                this.onRowEnd.row(this.rowCtx);
                            }
                        }
                    }
                    int[] rowcounts = {0};
                    int totalRowCounts = 0;
                    if (bind != null) {
                        rowcounts = bind.execute();
                    } else if (insert != null) {
                        rowcounts = new int[]{insert.execute()};
                    }
                    if (this.onDuplicate == 2 && NO_SUPPORT_ROWCOUNT_ON_DUPLICATE.contains(ctx.dialect())) {
                        totalRowCounts = this.unexecuted;
                    } else {
                        for (int rowCount : rowcounts) {
                            totalRowCounts += rowCount;
                        }
                    }
                    this.stored += totalRowCounts;
                    this.ignored += this.unexecuted - totalRowCounts;
                    this.executed++;
                    this.unexecuted = 0;
                    bind = null;
                    insert = null;
                    if (this.commit == 1 && this.processed % (this.bulkAfter * this.batchAfter) == 0 && (this.processed / (this.bulkAfter * this.batchAfter)) % this.commitAfter == 0) {
                        commit();
                    }
                    if (this.onRowEnd != null) {
                        this.onRowEnd.row(this.rowCtx);
                    }
                } else {
                    newRecord = true;
                    if (this.onRowEnd != null) {
                        this.onRowEnd.row(this.rowCtx);
                    }
                }
            } catch (Throwable th) {
                if (this.onRowEnd != null) {
                    this.onRowEnd.row(this.rowCtx);
                }
                throw th;
            }
        }
        if (this.unexecuted != 0) {
            if (bind != null) {
                try {
                    bind.execute();
                } catch (DataAccessException e2) {
                    this.errors.add(new LoaderErrorImpl(e2, row, this.processed - 1, insert));
                    this.ignored += this.unexecuted;
                    this.unexecuted = 0;
                }
            }
            if (insert != null) {
                insert.execute();
            }
            this.stored += this.unexecuted;
            this.executed++;
            this.unexecuted = 0;
        }
        if (this.commit == 1 && this.uncommitted != 0) {
            commit();
        }
        if (this.onError == 0) {
        }
        try {
            if (this.commit == 2) {
                if (this.errors.isEmpty()) {
                    commit();
                } else {
                    this.stored = 0;
                    rollback();
                }
            }
        } catch (DataAccessException e3) {
            this.errors.add(new LoaderErrorImpl(e3, null, this.processed - 1, null));
        }
    }

    private final void commit() {
        this.configuration.dsl().connection((v0) -> {
            v0.commit();
        });
        this.uncommitted = 0;
    }

    private final void rollback() {
        this.configuration.dsl().connection((v0) -> {
            v0.rollback();
        });
    }

    private final <T> void addValue0(InsertQuery<R> insert, Field<T> field, Object row) {
        insert.addValue((Field<Field<T>>) field, (Field<T>) field.getDataType().convert(row));
    }

    private final <T> void addValueForUpdate0(InsertQuery<R> insert, Field<T> field, Object row) {
        insert.addValueForUpdate((Field<Field<T>>) field, (Field<T>) field.getDataType().convert(row));
    }

    @Override // org.jooq.Loader
    public final List<LoaderError> errors() {
        return this.errors;
    }

    @Override // org.jooq.Loader
    public final int processed() {
        return this.processed;
    }

    @Override // org.jooq.Loader
    public final int executed() {
        return this.executed;
    }

    @Override // org.jooq.Loader
    public final int ignored() {
        return this.ignored;
    }

    @Override // org.jooq.Loader
    public final int stored() {
        return this.stored;
    }

    @Override // org.jooq.Loader
    public final LoaderContext result() {
        return this.rowCtx;
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/LoaderImpl$DefaultLoaderContext.class */
    private class DefaultLoaderContext implements LoaderContext {
        Object[] row;

        private DefaultLoaderContext() {
        }

        @Override // org.jooq.LoaderContext
        public final LoaderContext row(Object[] r) {
            this.row = r;
            return this;
        }

        @Override // org.jooq.LoaderContext
        public final Object[] row() {
            return this.row;
        }

        @Override // org.jooq.LoaderContext
        public final List<LoaderError> errors() {
            return LoaderImpl.this.errors;
        }

        @Override // org.jooq.LoaderContext
        public final int processed() {
            return LoaderImpl.this.processed;
        }

        @Override // org.jooq.LoaderContext
        public final int executed() {
            return LoaderImpl.this.executed;
        }

        @Override // org.jooq.LoaderContext
        public final int ignored() {
            return LoaderImpl.this.ignored;
        }

        @Override // org.jooq.LoaderContext
        public final int stored() {
            return LoaderImpl.this.stored;
        }
    }
}
