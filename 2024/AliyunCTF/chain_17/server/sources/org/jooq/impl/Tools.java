package org.jooq.impl;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import io.r2dbc.spi.R2dbcException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ForkJoinPool;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.jetbrains.annotations.Nullable;
import org.jooq.Asterisk;
import org.jooq.Attachable;
import org.jooq.BindContext;
import org.jooq.Catalog;
import org.jooq.Check;
import org.jooq.Clause;
import org.jooq.CommonTableExpression;
import org.jooq.Condition;
import org.jooq.ConditionProvider;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.ContextConverter;
import org.jooq.Converter;
import org.jooq.ConverterContext;
import org.jooq.Converters;
import org.jooq.Cursor;
import org.jooq.DSLContext;
import org.jooq.DataType;
import org.jooq.EmbeddableRecord;
import org.jooq.EnumType;
import org.jooq.ExecuteContext;
import org.jooq.ExecuteListener;
import org.jooq.Field;
import org.jooq.FieldOrRow;
import org.jooq.FieldOrRowOrSelect;
import org.jooq.Fields;
import org.jooq.ForeignKey;
import org.jooq.Function2;
import org.jooq.Function3;
import org.jooq.JSON;
import org.jooq.JSONB;
import org.jooq.JSONEntry;
import org.jooq.JoinType;
import org.jooq.Name;
import org.jooq.OrderField;
import org.jooq.Param;
import org.jooq.QualifiedAsterisk;
import org.jooq.QualifiedRecord;
import org.jooq.QuantifiedSelect;
import org.jooq.Query;
import org.jooq.QueryPart;
import org.jooq.Record;
import org.jooq.RecordQualifier;
import org.jooq.RenderContext;
import org.jooq.Result;
import org.jooq.ResultOrRows;
import org.jooq.ResultQuery;
import org.jooq.Results;
import org.jooq.Row;
import org.jooq.SQLDialect;
import org.jooq.Schema;
import org.jooq.Scope;
import org.jooq.Select;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.SortField;
import org.jooq.Source;
import org.jooq.Table;
import org.jooq.TableElement;
import org.jooq.TableField;
import org.jooq.TableLike;
import org.jooq.TableRecord;
import org.jooq.UDT;
import org.jooq.UDTRecord;
import org.jooq.UpdatableRecord;
import org.jooq.XML;
import org.jooq.conf.BackslashEscaping;
import org.jooq.conf.NestedCollectionEmulation;
import org.jooq.conf.ParamType;
import org.jooq.conf.ParseNameCase;
import org.jooq.conf.RenderDefaultNullability;
import org.jooq.conf.RenderQuotedNames;
import org.jooq.conf.Settings;
import org.jooq.conf.SettingsTools;
import org.jooq.conf.ThrowExceptions;
import org.jooq.exception.DataAccessException;
import org.jooq.exception.DataException;
import org.jooq.exception.DataTypeException;
import org.jooq.exception.DetachedException;
import org.jooq.exception.ExceptionTools;
import org.jooq.exception.IntegrityConstraintViolationException;
import org.jooq.exception.MappingException;
import org.jooq.exception.NoDataFoundException;
import org.jooq.exception.SQLStateClass;
import org.jooq.exception.TemplatingException;
import org.jooq.exception.TooManyRowsException;
import org.jooq.impl.QOM;
import org.jooq.impl.ResultsImpl;
import org.jooq.tools.Ints;
import org.jooq.tools.JooqLogger;
import org.jooq.tools.StringUtils;
import org.jooq.tools.jdbc.JDBCUtils;
import org.jooq.tools.reflect.Reflect;
import org.jooq.tools.reflect.ReflectException;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.jooq.types.ULong;
import org.jooq.types.UShort;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Tools.class */
public final class Tools {
    static final char ESCAPE = '!';
    private static volatile JPANamespace jpaNamespace;
    private static volatile Boolean isKotlinAvailable;
    private static volatile Reflect ktJvmClassMapping;
    private static volatile Reflect ktKClasses;
    private static volatile Reflect ktKClass;
    private static volatile Reflect ktKTypeParameter;
    private static final int FIELD_NAME_CACHE_SIZE = 128;
    static final JooqLogger log = JooqLogger.getLogger((Class<?>) Tools.class);
    static final byte[] EMPTY_BYTE = new byte[0];
    static final Catalog[] EMPTY_CATALOG = new Catalog[0];
    static final Check<?>[] EMPTY_CHECK = new Check[0];
    static final Clause[] EMPTY_CLAUSE = new Clause[0];
    static final Collection<?>[] EMPTY_COLLECTION = new Collection[0];
    static final CommonTableExpression<?>[] EMPTY_COMMON_TABLE_EXPRESSION = new CommonTableExpression[0];
    static final ExecuteListener[] EMPTY_EXECUTE_LISTENER = new ExecuteListener[0];
    static final Field<?>[] EMPTY_FIELD = new Field[0];
    static final FieldOrRow[] EMPTY_FIELD_OR_ROW = new FieldOrRow[0];
    static final int[] EMPTY_INT = new int[0];
    static final JSONEntry<?>[] EMPTY_JSONENTRY = new JSONEntry[0];
    static final Name[] EMPTY_NAME = new Name[0];
    static final Object[] EMPTY_OBJECT = new Object[0];
    static final Param<?>[] EMPTY_PARAM = new Param[0];
    static final OrderField<?>[] EMPTY_ORDERFIELD = new OrderField[0];
    static final Query[] EMPTY_QUERY = new Query[0];
    static final QueryPart[] EMPTY_QUERYPART = new QueryPart[0];
    static final Record[] EMPTY_RECORD = new Record[0];
    static final Row[] EMPTY_ROW = new Row[0];
    static final Schema[] EMTPY_SCHEMA = new Schema[0];
    static final SortField<?>[] EMPTY_SORTFIELD = new SortField[0];
    static final Source[] EMPTY_SOURCE = new Source[0];
    static final String[] EMPTY_STRING = new String[0];
    static final Table<?>[] EMPTY_TABLE = new Table[0];
    static final TableField<?, ?>[] EMPTY_TABLE_FIELD = new TableField[0];
    static final TableRecord<?>[] EMPTY_TABLE_RECORD = new TableRecord[0];
    static final UpdatableRecord<?>[] EMPTY_UPDATABLE_RECORD = new UpdatableRecord[0];
    static final DataKey[] DATAKEY_RESET_IN_SUBQUERY_SCOPE = (DataKey[]) Stream.concat(Stream.concat(Stream.of((Object[]) BooleanDataKey.values()), Stream.of((Object[]) SimpleDataKey.values())), Stream.of((Object[]) ExtendedDataKey.values())).filter(r2 -> {
        return ((DataKey) r2).resetInSubqueryScope();
    }).toArray(x$0 -> {
        return new DataKey[x$0];
    });
    private static final Object initLock = new Object();
    static int maxConsumedExceptions = 256;
    static int maxConsumedResults = 65536;
    private static final Pattern DASH_PATTERN = Pattern.compile("(-+)");
    private static final Pattern PIPE_PATTERN = Pattern.compile("(?<=\\|)([^|]+)(?=\\|)");
    private static final Pattern PLUS_PATTERN = Pattern.compile("\\+(-+)(?=\\+)");
    private static final char[] WHITESPACE_CHARACTERS = " \t\n\u000b\f\r".toCharArray();
    private static final char[][] JDBC_ESCAPE_PREFIXES = {"{fn ".toCharArray(), "{d ".toCharArray(), "{t ".toCharArray(), "{ts ".toCharArray()};
    private static final char[] TOKEN_SINGLE_LINE_COMMENT = {'-', '-'};
    private static final char[] TOKEN_SINGLE_LINE_COMMENT_C = {'/', '/'};
    private static final char[] TOKEN_HASH = {'#'};
    private static final char[] TOKEN_MULTI_LINE_COMMENT_OPEN = {'/', '*'};
    private static final char[] TOKEN_MULTI_LINE_COMMENT_CLOSE = {'*', '/'};
    private static final char[] TOKEN_APOS = {'\''};
    private static final char[] TOKEN_ESCAPED_APOS = {'\'', '\''};
    private static final char[][] NON_BIND_VARIABLE_SUFFIXES = {new char[]{'?'}, new char[]{'|'}, new char[]{'&'}, new char[]{'@'}, new char[]{'<'}, new char[]{'~'}, new char[]{'#'}, new char[]{'-'}};
    private static final char[][] BIND_VARIABLE_SUFFIXES = {new char[]{'<', '>'}};
    private static final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();
    private static final byte[] HEX_LOOKUP = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 0, 0, 0, 0, 0, 0, 10, 11, 12, 13, 14, 15, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 10, 11, 12, 13, 14, 15, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    static final Set<SQLDialect> REQUIRES_BACKSLASH_ESCAPING = SQLDialect.supportedBy(SQLDialect.MARIADB, SQLDialect.MYSQL);
    static final Set<SQLDialect> NO_SUPPORT_NULL = SQLDialect.supportedBy(SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB, SQLDialect.TRINO);
    static final Set<SQLDialect> NO_SUPPORT_NOT_NULL = SQLDialect.supportedBy(SQLDialect.TRINO);
    static final Set<SQLDialect> NO_SUPPORT_BINARY_TYPE_LENGTH = SQLDialect.supportedBy(SQLDialect.POSTGRES, SQLDialect.TRINO, SQLDialect.YUGABYTEDB);
    static final Set<SQLDialect> NO_SUPPORT_CAST_TYPE_IN_DDL = SQLDialect.supportedBy(SQLDialect.MARIADB, SQLDialect.MYSQL);
    static final Set<SQLDialect> SUPPORT_NON_BIND_VARIABLE_SUFFIXES = SQLDialect.supportedBy(SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB);
    static final Set<SQLDialect> SUPPORT_POSTGRES_LITERALS = SQLDialect.supportedBy(SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB);
    static final Set<SQLDialect> DEFAULT_BEFORE_NULL = SQLDialect.supportedBy(SQLDialect.FIREBIRD, SQLDialect.HSQLDB);
    static final Set<SQLDialect> NO_SUPPORT_TIMESTAMP_PRECISION = SQLDialect.supportedBy(SQLDialect.DERBY);
    static final Set<SQLDialect> DEFAULT_TIMESTAMP_NOT_NULL = SQLDialect.supportedBy(SQLDialect.MARIADB);
    static final Set<SQLDialect> REQUIRES_PARENTHESISED_DEFAULT = SQLDialect.supportedBy(SQLDialect.SQLITE);
    private static final String[] FIELD_NAME_STRINGS = (String[]) IntStream.range(0, 128).mapToObj(Tools::fieldNameString0).toArray(x$0 -> {
        return new String[x$0];
    });
    private static final Name[] FIELD_NAMES = (Name[]) IntStream.range(0, 128).mapToObj(i -> {
        return DSL.name(FIELD_NAME_STRINGS[i]);
    }).toArray(x$0 -> {
        return new Name[x$0];
    });
    static final Lazy<Configuration> CONFIG = Lazy.of(() -> {
        return new DefaultConfiguration();
    });
    static final Lazy<Configuration> CONFIG_UNQUOTED = Lazy.of(() -> {
        DefaultConfiguration c = new DefaultConfiguration();
        c.settings().setRenderQuotedNames(RenderQuotedNames.NEVER);
        return c;
    });
    static final Lazy<DSLContext> CTX = Lazy.of(() -> {
        return DSL.using(CONFIG.get());
    });
    private static final Pattern NEW_LINES = Pattern.compile("[\\r\\n]+");
    private static final Pattern P_PARSE_HTML_ROW = Pattern.compile("<tr>(.*?)</tr>");
    private static final Pattern P_PARSE_HTML_COL_HEAD = Pattern.compile("<th>(.*?)</th>");
    private static final Pattern P_PARSE_HTML_COL_BODY = Pattern.compile("<td>(.*?)</td>");
    private static final Set<SQLDialect> REQUIRE_IDENTITY_AFTER_NULL = SQLDialect.supportedBy(SQLDialect.H2, SQLDialect.MARIADB, SQLDialect.MYSQL);
    private static final Set<SQLDialect> SUPPORT_PG_IDENTITY = SQLDialect.supportedBy(SQLDialect.POSTGRES);

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Tools$DataKey.class */
    public interface DataKey {
        boolean resetInSubqueryScope();

        Object resetValue();

        int resetThreshold();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Tools$JPANamespace.class */
    public enum JPANamespace {
        JAVAX,
        JAKARTA,
        NONE
    }

    Tools() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Tools$BooleanDataKey.class */
    public enum BooleanDataKey implements DataKey {
        DATA_MANDATORY_WHERE_CLAUSE(true, null, 1),
        DATA_COUNT_BIND_VALUES,
        DATA_FORCE_STATIC_STATEMENT,
        DATA_OMIT_CLAUSE_EVENT_EMISSION,
        DATA_WRAP_DERIVED_TABLES_IN_PARENTHESES,
        DATA_ROW_VALUE_EXPRESSION_PREDICATE_SUBQUERY,
        DATA_DEFAULT_TRANSACTION_PROVIDER_AUTOCOMMIT,
        DATA_UNALIAS_ALIASED_EXPRESSIONS,
        DATA_SELECT_NO_DATA,
        DATA_OMIT_INTO_CLAUSE,
        DATA_RENDER_TRAILING_LIMIT_IF_APPLICABLE,
        DATA_FORCE_LIMIT_WITH_ORDER_BY,
        DATA_LIST_ALREADY_INDENTED,
        DATA_CONSTRAINT_REFERENCE,
        DATA_COLLECT_SEMI_ANTI_JOIN,
        DATA_INSERT_SELECT,
        DATA_INSERT_SELECT_WITHOUT_INSERT_COLUMN_LIST,
        DATA_NESTED_SET_OPERATIONS,
        DATA_AS_REQUIRED(true, null, 0),
        DATA_MULTISET_CONDITION,
        DATA_MULTISET_CONTENT,
        DATA_ROW_CONTENT,
        DATA_FORCE_CASE_ELSE_NULL,
        DATA_GROUP_CONCAT_MAX_LEN_SET,
        DATA_LOCK_WAIT_TIMEOUT_SET,
        DATA_PARSE_ON_CONFLICT,
        DATA_STORE_ASSIGNMENT,
        DATA_RENDER_IMPLICIT_JOIN;

        private final boolean resetInSubqueryScope;
        private final Object resetValue;
        private final int resetThreshold;

        BooleanDataKey() {
            this(false, null, 0);
        }

        BooleanDataKey(boolean resetInSubqueryScope, Object resetValue, int resetThreshold) {
            this.resetInSubqueryScope = resetInSubqueryScope;
            this.resetValue = resetValue;
            this.resetThreshold = resetThreshold;
        }

        @Override // org.jooq.impl.Tools.DataKey
        public final boolean resetInSubqueryScope() {
            return this.resetInSubqueryScope;
        }

        @Override // org.jooq.impl.Tools.DataKey
        public final Object resetValue() {
            return this.resetValue;
        }

        @Override // org.jooq.impl.Tools.DataKey
        public final int resetThreshold() {
            return this.resetThreshold;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Tools$SimpleDataKey.class */
    public enum SimpleDataKey implements DataKey {
        DATA_BLOCK_NESTING,
        DATA_RENDERING_DATA_CHANGE_DELTA_TABLE,
        DATA_WINDOW_DEFINITIONS(true, null, 0),
        DATA_DEFAULT_TRANSACTION_PROVIDER_SAVEPOINTS,
        DATA_DEFAULT_TRANSACTION_PROVIDER_CONNECTION,
        DATA_OVERRIDE_ALIASES_IN_ORDER_BY,
        DATA_SELECT_INTO_TABLE,
        DATA_COLLECTED_SEMI_ANTI_JOIN,
        DATA_PREPEND_SQL,
        DATA_APPEND_SQL,
        DATA_DML_TARGET_TABLE,
        DATA_DML_USING_TABLES,
        DATA_ON_DUPLICATE_KEY_WHERE,
        DATA_TOP_LEVEL_CTE,
        DATA_SELECT_ALIASES;

        private final boolean resetInSubqueryScope;
        private final Object resetValue;
        private final int resetThreshold;

        SimpleDataKey() {
            this(false, null, 0);
        }

        SimpleDataKey(boolean resetInSubqueryScope, Object resetValue, int resetThreshold) {
            this.resetInSubqueryScope = resetInSubqueryScope;
            this.resetValue = resetValue;
            this.resetThreshold = resetThreshold;
        }

        @Override // org.jooq.impl.Tools.DataKey
        public final boolean resetInSubqueryScope() {
            return this.resetInSubqueryScope;
        }

        @Override // org.jooq.impl.Tools.DataKey
        public final Object resetValue() {
            return this.resetValue;
        }

        @Override // org.jooq.impl.Tools.DataKey
        public final int resetThreshold() {
            return this.resetThreshold;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Tools$ExtendedDataKey.class */
    public enum ExtendedDataKey implements DataKey {
        DATA_INSERT_ON_DUPLICATE_KEY_UPDATE,
        DATA_TRANSFORM_ROWNUM_TO_LIMIT,
        DATA_WINDOW_FUNCTION,
        DATA_RENDER_TABLE(true, null, 0),
        DATA_EMPTY_ARRAY_BASE_TYPE;

        private final boolean resetInSubqueryScope;
        private final Object resetValue;
        private final int resetThreshold;

        ExtendedDataKey() {
            this(false, null, 0);
        }

        ExtendedDataKey(boolean resetInSubqueryScope, Object resetValue, int resetThreshold) {
            this.resetInSubqueryScope = resetInSubqueryScope;
            this.resetValue = resetValue;
            this.resetThreshold = resetThreshold;
        }

        @Override // org.jooq.impl.Tools.DataKey
        public final boolean resetInSubqueryScope() {
            return this.resetInSubqueryScope;
        }

        @Override // org.jooq.impl.Tools.DataKey
        public final Object resetValue() {
            return this.resetValue;
        }

        @Override // org.jooq.impl.Tools.DataKey
        public final int resetThreshold() {
            return this.resetThreshold;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final List<Row> rows(Result<?> result) {
        return map(result, r -> {
            return r.valuesRow();
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <R extends Record> RecordDelegate<R> newRecord(boolean fetched, Class<R> type) {
        return newRecord(fetched, type, (AbstractRow) null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <R extends Record> RecordDelegate<R> newRecord(boolean fetched, Class<R> type, AbstractRow<R> fields) {
        return newRecord(fetched, type, fields, null);
    }

    static final <R extends Record> RecordDelegate<R> newRecord(boolean fetched, RecordQualifier<R> type) {
        return newRecord(fetched, type, (Configuration) null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <R extends Record> RecordDelegate<R> newRecord(boolean fetched, RecordQualifier<R> type, Configuration configuration) {
        return newRecord(fetched, type.getRecordType(), (AbstractRow) type.fieldsRow(), configuration);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <R extends Record> RecordDelegate<R> newRecord(boolean fetched, Class<? extends R> type, AbstractRow<? extends R> fields, Configuration configuration) {
        return newRecord(fetched, recordFactory(type, fields), configuration);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <R extends Record> RecordDelegate<R> newRecord(boolean fetched, Supplier<R> factory, Configuration configuration) {
        return new RecordDelegate<>(configuration, factory, Boolean.valueOf(fetched));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <R extends Record> AbstractRow<R> row0(FieldsImpl<R> fields) {
        switch (fields.size()) {
            case 1:
                return new RowImpl1((FieldsImpl<?>) fields);
            case 2:
                return new RowImpl2(fields);
            case 3:
                return new RowImpl3(fields);
            case 4:
                return new RowImpl4(fields);
            case 5:
                return new RowImpl5(fields);
            case 6:
                return new RowImpl6(fields);
            case 7:
                return new RowImpl7(fields);
            case 8:
                return new RowImpl8(fields);
            case 9:
                return new RowImpl9(fields);
            case 10:
                return new RowImpl10(fields);
            case 11:
                return new RowImpl11(fields);
            case 12:
                return new RowImpl12(fields);
            case 13:
                return new RowImpl13(fields);
            case 14:
                return new RowImpl14(fields);
            case 15:
                return new RowImpl15(fields);
            case 16:
                return new RowImpl16(fields);
            case 17:
                return new RowImpl17(fields);
            case 18:
                return new RowImpl18(fields);
            case 19:
                return new RowImpl19(fields);
            case 20:
                return new RowImpl20(fields);
            case 21:
                return new RowImpl21(fields);
            case 22:
                return new RowImpl22(fields);
            default:
                return new RowImplN((FieldsImpl<?>) fields);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final AbstractRow<?> row0(Collection<? extends Field<?>> fields) {
        return row0((Field<?>[]) fields.toArray(EMPTY_FIELD));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final AbstractRow<?> row0(Field<?>... fields) {
        return row0(new FieldsImpl(fields));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Class<? extends AbstractRecord> recordType(int length) {
        switch (length) {
            case 1:
                return RecordImpl1.class;
            case 2:
                return RecordImpl2.class;
            case 3:
                return RecordImpl3.class;
            case 4:
                return RecordImpl4.class;
            case 5:
                return RecordImpl5.class;
            case 6:
                return RecordImpl6.class;
            case 7:
                return RecordImpl7.class;
            case 8:
                return RecordImpl8.class;
            case 9:
                return RecordImpl9.class;
            case 10:
                return RecordImpl10.class;
            case 11:
                return RecordImpl11.class;
            case 12:
                return RecordImpl12.class;
            case 13:
                return RecordImpl13.class;
            case 14:
                return RecordImpl14.class;
            case 15:
                return RecordImpl15.class;
            case 16:
                return RecordImpl16.class;
            case 17:
                return RecordImpl17.class;
            case 18:
                return RecordImpl18.class;
            case 19:
                return RecordImpl19.class;
            case 20:
                return RecordImpl20.class;
            case 21:
                return RecordImpl21.class;
            case 22:
                return RecordImpl22.class;
            default:
                return RecordImplN.class;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <R extends Record> Supplier<R> recordFactory(Class<? extends R> type, AbstractRow<? extends R> row) {
        if (type == AbstractRecord.class || type == Record.class || InternalRecord.class.isAssignableFrom(type)) {
            switch (row.size()) {
                case 1:
                    return () -> {
                        return new RecordImpl1(row);
                    };
                case 2:
                    return () -> {
                        return new RecordImpl2(row);
                    };
                case 3:
                    return () -> {
                        return new RecordImpl3(row);
                    };
                case 4:
                    return () -> {
                        return new RecordImpl4(row);
                    };
                case 5:
                    return () -> {
                        return new RecordImpl5(row);
                    };
                case 6:
                    return () -> {
                        return new RecordImpl6(row);
                    };
                case 7:
                    return () -> {
                        return new RecordImpl7(row);
                    };
                case 8:
                    return () -> {
                        return new RecordImpl8(row);
                    };
                case 9:
                    return () -> {
                        return new RecordImpl9(row);
                    };
                case 10:
                    return () -> {
                        return new RecordImpl10(row);
                    };
                case 11:
                    return () -> {
                        return new RecordImpl11(row);
                    };
                case 12:
                    return () -> {
                        return new RecordImpl12(row);
                    };
                case 13:
                    return () -> {
                        return new RecordImpl13(row);
                    };
                case 14:
                    return () -> {
                        return new RecordImpl14(row);
                    };
                case 15:
                    return () -> {
                        return new RecordImpl15(row);
                    };
                case 16:
                    return () -> {
                        return new RecordImpl16(row);
                    };
                case 17:
                    return () -> {
                        return new RecordImpl17(row);
                    };
                case 18:
                    return () -> {
                        return new RecordImpl18(row);
                    };
                case 19:
                    return () -> {
                        return new RecordImpl19(row);
                    };
                case 20:
                    return () -> {
                        return new RecordImpl20(row);
                    };
                case 21:
                    return () -> {
                        return new RecordImpl21(row);
                    };
                case 22:
                    return () -> {
                        return new RecordImpl22(row);
                    };
                default:
                    return () -> {
                        return new RecordImplN((AbstractRow<?>) row);
                    };
            }
        }
        try {
            Constructor<? extends R> constructor = (Constructor) Reflect.accessible(type.getDeclaredConstructor(new Class[0]));
            return () -> {
                try {
                    return (Record) constructor.newInstance(new Object[0]);
                } catch (Exception e) {
                    throw new IllegalStateException("Could not construct new record", e);
                }
            };
        } catch (Exception e) {
            throw new IllegalStateException("Could not construct new record", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final void resetChangedOnNotNull(Record record) {
        int size = record.size();
        for (int i = 0; i < size; i++) {
            if (record.get(i) == null && !record.field(i).getDataType().nullable()) {
                record.changed(i, false);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Configuration configurationOrThrow(Attachable attachable) {
        if (attachable.configuration() == null) {
            throw new DetachedException("No configuration attached: " + String.valueOf(attachable));
        }
        return configuration(attachable.configuration());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Configuration configuration(Attachable attachable) {
        return configuration(attachable.configuration());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Configuration configuration(Configuration configuration) {
        return configuration != null ? configuration : new DefaultConfiguration();
    }

    static final Configuration configuration(Scope scope) {
        return configuration(scope != null ? scope.configuration() : null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T, U> ContextConverter<T, U> converter(Configuration configuration, T instance, Class<T> tType, Class<U> uType) {
        Converter<T, U> result = configuration(configuration).converterProvider().provide(tType, uType);
        if (result == null) {
            result = CONFIG.get().converterProvider().provide(tType, uType);
        }
        if (result == null && tType == Converters.UnknownType.class) {
            result = converter(configuration, instance, instance == null ? Object.class : instance.getClass(), uType);
        }
        if (result == null) {
            return null;
        }
        return ContextConverter.scoped(result);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T, U> ContextConverter<T, U> converterOrFail(Configuration configuration, T instance, Class<T> tType, Class<U> uType) {
        ContextConverter<T, U> result = converter(configuration, instance, tType, uType);
        if (result == null) {
            throw new DataTypeException("No Converter found for types " + tType.getName() + " and " + uType.getName());
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T, U> ContextConverter<T, U> converterOrFail(Attachable attachable, T instance, Class<T> tType, Class<U> uType) {
        return converterOrFail(configuration(attachable), instance, tType, uType);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Settings settings(Attachable attachable) {
        return configuration(attachable).settings();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Settings settings(Configuration configuration) {
        return configuration(configuration).settings();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T> T attach(Attachable attachable, Configuration configuration, Supplier<T> supplier) {
        Configuration previous = attachable.configuration();
        try {
            attachable.attach(configuration);
            T t = supplier.get();
            attachable.attach(previous);
            return t;
        } catch (Throwable th) {
            attachable.attach(previous);
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final boolean attachRecords(Configuration configuration) {
        Settings settings;
        return configuration == null || (settings = configuration.settings()) == null || !Boolean.FALSE.equals(settings.isAttachRecords());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Field<?>[] fieldArray(Collection<? extends Field<?>> fields) {
        if (fields == null) {
            return null;
        }
        return (Field[]) fields.toArray(EMPTY_FIELD);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final DataType<?>[] dataTypes(Class<?>[] types) {
        return (DataType[]) map(types, t -> {
            return t != null ? DSL.getDataType(t) : DSL.getDataType(Object.class);
        }, x$0 -> {
            return new DataType[x$0];
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T> SortField<T> sortField(OrderField<T> field) {
        if (field instanceof SortField) {
            SortField<T> s = (SortField) field;
            return s;
        }
        if (field instanceof Field) {
            Field<T> f = (Field) field;
            return f.sortDefault();
        }
        throw new IllegalArgumentException("Field not supported : " + String.valueOf(field));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final SortField<?>[] sortFields(OrderField<?>[] fields) {
        if (fields instanceof SortField[]) {
            SortField<?>[] s = (SortField[]) fields;
            return s;
        }
        return (SortField[]) map(fields, o -> {
            return sortField(o);
        }, x$0 -> {
            return new SortField[x$0];
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final List<SortField<?>> sortFields(Collection<? extends OrderField<?>> fields) {
        return map(fields, o -> {
            return sortField(o);
        });
    }

    private static final String fieldNameString0(int index) {
        return "v" + index;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final String fieldNameString(int index) {
        return index < 128 ? FIELD_NAME_STRINGS[index] : fieldNameString0(index);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Name fieldName(int index) {
        return index < 128 ? FIELD_NAMES[index] : DSL.name(fieldNameString0(index));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Name[] fieldNames(int length) {
        Name[] result = new Name[length];
        for (int i = 0; i < length; i++) {
            result[i] = fieldName(i);
        }
        return result;
    }

    static final String[] fieldNameStrings(int length) {
        String[] result = new String[length];
        for (int i = 0; i < length; i++) {
            result[i] = fieldNameString(i);
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Field<?>[] fields(int length) {
        return fields(length, SQLDataType.OTHER);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T> Field<T>[] fields(int length, DataType<T> type) {
        Field<T>[] result = new Field[length];
        for (int i = 0; i < length; i++) {
            result[i] = DSL.field(fieldName(i), type);
        }
        return result;
    }

    static final boolean reference(Field<?> field) {
        return (field instanceof TableField) || ((field instanceof SQLField) && ((SQLField) field).delegate.isName);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T> Field<T> unqualified(Field<T> field) {
        return DSL.field(field.getUnqualifiedName(), field.getDataType());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T> SortField<T> unqualified(SortField<T> sortField) {
        return (SortField<T>) sortField.$field(unqualified(sortField.$field()));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final List<Field<?>> unaliasedFields(Collection<? extends Field<?>> fields) {
        return map(fields, (f, i) -> {
            return DSL.field(fieldName(i), f.getDataType()).as((Field<?>) f);
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <R extends Record, O extends Record> ReferenceImpl<R, O> aliasedKey(ForeignKey<R, O> key, Table<R> child, Table<O> parent) {
        return new ReferenceImpl<>(child, key.getQualifiedName(), fieldsByName(child, key.getFieldsArray()), key.getKey(), fieldsByName(parent, key.getKeyFieldsArray()), key.enforced());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final List<Field<?>> aliasedFields(Collection<? extends Field<?>> fields) {
        return map(fields, (f, i) -> {
            return f.as(fieldName(i));
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final List<Field<?>> fieldsByName(String[] fieldNames) {
        return fieldsByName((String) null, fieldNames);
    }

    static final Field<?>[] fieldsByName(Name tableName, int length) {
        Field<?>[] result = new Field[length];
        if (tableName == null) {
            for (int i = 0; i < length; i++) {
                result[i] = DSL.field(fieldName(i));
            }
        } else {
            for (int i2 = 0; i2 < length; i2++) {
                result[i2] = DSL.field(DSL.name(tableName, fieldName(i2)));
            }
        }
        return result;
    }

    static final <R extends Record> TableField<R, ?>[] fieldsByName(Table<R> tableName, Field<?>[] fieldNames) {
        if (tableName == null) {
            return (TableField[]) map(fieldNames, n -> {
                return (TableField) DSL.field(n.getUnqualifiedName(), n.getDataType());
            }, x$0 -> {
                return new TableField[x$0];
            });
        }
        return (TableField[]) map(fieldNames, n2 -> {
            return (TableField) DSL.field(tableName.getQualifiedName().append(n2.getUnqualifiedName()), n2.getDataType());
        }, x$02 -> {
            return new TableField[x$02];
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final List<Field<?>> fieldsByName(Name tableName, Name[] fieldNames) {
        if (tableName == null) {
            return map(fieldNames, n -> {
                return DSL.field(n);
            });
        }
        return map(fieldNames, n2 -> {
            return DSL.field(DSL.name(tableName, n2));
        });
    }

    static final List<Field<?>> fieldsByName(String tableName, String[] fieldNames) {
        if (StringUtils.isEmpty(tableName)) {
            return map(fieldNames, n -> {
                return DSL.field(DSL.name(n));
            });
        }
        return map(fieldNames, n2 -> {
            return DSL.field(DSL.name(tableName, n2));
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final List<Field<?>> fieldsByName(Name[] names) {
        return map(names, n -> {
            return DSL.field(n);
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Name[] names(String[] names) {
        return (Name[]) map(names, n -> {
            return DSL.name(n);
        }, x$0 -> {
            return new Name[x$0];
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final List<Name> names(Collection<?> names) {
        return map(names, n -> {
            if (!(n instanceof Name)) {
                return DSL.name(String.valueOf(n));
            }
            Name name = (Name) n;
            return name;
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final String sanitiseName(Configuration configuration, String name) {
        switch (configuration.family()) {
            default:
                return name;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final List<JSONEntry<?>> jsonEntries(Field<?>[] entries) {
        return map(entries, f -> {
            return DSL.jsonEntry(f);
        });
    }

    private static final IllegalArgumentException fieldExpected(Object value) {
        return new IllegalArgumentException("Cannot interpret argument of type " + String.valueOf(value.getClass()) + " as a Field: " + String.valueOf(value));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T> Field<T>[] castAllIfNeeded(Field<?>[] fields, Class<T> type) {
        Field<T>[] castFields = new Field[fields.length];
        for (int i = 0; i < fields.length; i++) {
            castFields[i] = castIfNeeded(fields[i], type);
        }
        return castFields;
    }

    static final <T> Field<T>[] castAllIfNeeded(Field<?>[] fields, DataType<T> type) {
        Field<T>[] castFields = new Field[fields.length];
        for (int i = 0; i < fields.length; i++) {
            castFields[i] = castIfNeeded(fields[i], type);
        }
        return castFields;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Multi-variable type inference failed */
    public static final <T> Field<T> castIfNeeded(Field<?> field, Class<T> type) {
        if (field.getType().equals(type)) {
            return field;
        }
        return field.cast(type);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Multi-variable type inference failed */
    public static final <T> Field<T> castIfNeeded(Field<?> field, DataType<T> type) {
        if (field.getDataType().equals(type)) {
            return field;
        }
        return field.cast(type);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Multi-variable type inference failed */
    public static final <T> Field<T> castIfNeeded(Field<?> field, Field<T> type) {
        if (field.getDataType().equals(type.getDataType())) {
            return field;
        }
        return field.cast(type);
    }

    static final Param<Byte> field(byte value) {
        return DSL.val(Byte.valueOf(value), SQLDataType.TINYINT);
    }

    static final Param<Byte> field(Byte value) {
        return DSL.val(value, SQLDataType.TINYINT);
    }

    static final Param<UByte> field(UByte value) {
        return DSL.val(value, SQLDataType.TINYINTUNSIGNED);
    }

    static final Param<Short> field(short value) {
        return DSL.val(Short.valueOf(value), SQLDataType.SMALLINT);
    }

    static final Param<Short> field(Short value) {
        return DSL.val(value, SQLDataType.SMALLINT);
    }

    static final Param<UShort> field(UShort value) {
        return DSL.val(value, SQLDataType.SMALLINTUNSIGNED);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Param<Integer> field(int value) {
        return DSL.val(Integer.valueOf(value), SQLDataType.INTEGER);
    }

    static final Param<Integer> field(Integer value) {
        return DSL.val(value, SQLDataType.INTEGER);
    }

    static final Param<UInteger> field(UInteger value) {
        return DSL.val(value, SQLDataType.INTEGERUNSIGNED);
    }

    static final Param<Long> field(long value) {
        return DSL.val(Long.valueOf(value), SQLDataType.BIGINT);
    }

    static final Param<Long> field(Long value) {
        return DSL.val(value, SQLDataType.BIGINT);
    }

    static final Param<ULong> field(ULong value) {
        return DSL.val(value, SQLDataType.BIGINTUNSIGNED);
    }

    static final Param<Float> field(float value) {
        return DSL.val(Float.valueOf(value), SQLDataType.REAL);
    }

    static final Param<Float> field(Float value) {
        return DSL.val(value, SQLDataType.REAL);
    }

    static final Param<Double> field(double value) {
        return DSL.val(Double.valueOf(value), SQLDataType.DOUBLE);
    }

    static final Param<Double> field(Double value) {
        return DSL.val(value, SQLDataType.DOUBLE);
    }

    static final Param<Boolean> field(boolean value) {
        return DSL.val(Boolean.valueOf(value), SQLDataType.BOOLEAN);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Param<Boolean> field(Boolean value) {
        return DSL.val(value, SQLDataType.BOOLEAN);
    }

    static final Param<BigDecimal> field(BigDecimal value) {
        return DSL.val(value, SQLDataType.DECIMAL);
    }

    static final Param<BigInteger> field(BigInteger value) {
        return DSL.val(value, SQLDataType.DECIMAL_INTEGER);
    }

    static final Param<byte[]> field(byte[] value) {
        return DSL.val(value, SQLDataType.VARBINARY);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Param<String> field(String value) {
        return DSL.val(value, SQLDataType.VARCHAR);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Param<Date> field(Date value) {
        return DSL.val(value, SQLDataType.DATE);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Param<Time> field(Time value) {
        return DSL.val(value, SQLDataType.TIME);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Param<Timestamp> field(Timestamp value) {
        return DSL.val(value, SQLDataType.TIMESTAMP);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Param<LocalDate> field(LocalDate value) {
        return DSL.val(value, SQLDataType.LOCALDATE);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Param<LocalTime> field(LocalTime value) {
        return DSL.val(value, SQLDataType.LOCALTIME);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Param<LocalDateTime> field(LocalDateTime value) {
        return DSL.val(value, SQLDataType.LOCALDATETIME);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Param<OffsetTime> field(OffsetTime value) {
        return DSL.val(value, SQLDataType.OFFSETTIME);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Param<OffsetDateTime> field(OffsetDateTime value) {
        return DSL.val(value, SQLDataType.OFFSETDATETIME);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Param<Instant> field(Instant value) {
        return DSL.val(value, SQLDataType.INSTANT);
    }

    static final Param<UUID> field(UUID value) {
        return DSL.val(value, SQLDataType.UUID);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Param<JSON> field(JSON value) {
        return DSL.val(value, SQLDataType.JSON);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Param<JSONB> field(JSONB value) {
        return DSL.val(value, SQLDataType.JSONB);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Param<XML> field(XML value) {
        return DSL.val(value, SQLDataType.XML);
    }

    @Deprecated
    static final Field<Object> field(Name name) {
        return DSL.field(name);
    }

    private static final <T> Field<T> field(Object value, boolean defaultInferred, java.util.function.Function<? super Object, ? extends Param<T>> defaultValue) {
        if (value instanceof Val) {
            Val<?> p1 = (Val) value;
            if (p1.inferredDataType && !defaultInferred) {
                Val<T> p2 = (Val) defaultValue.apply(p1.getValue());
                p2.setInline0(p1.isInline());
                return p2;
            }
            return p1;
        }
        if (value instanceof Field) {
            return (Field) value;
        }
        if ((value instanceof Select) && degree((Select) value) == 1) {
            return DSL.field((Select) value);
        }
        if (value instanceof AbstractRow) {
            AbstractRow<?> r = (AbstractRow) value;
            return r.rf();
        }
        if (value instanceof AbstractTable) {
            AbstractTable<?> t = (AbstractTable) value;
            return t.tf();
        }
        if (value instanceof QueryPart) {
            throw fieldExpected(value);
        }
        return defaultValue.apply(value);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T> Field<T> field(T value) {
        return field(value, true, v -> {
            return DSL.val0(v, true);
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T> Field<T> field(Object value, Field<T> field) {
        return field(value, false, v -> {
            return DSL.val(v, field);
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T> Field<T> field(Object value, Class<T> type) {
        return field(value, false, v -> {
            return DSL.val(v, type);
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T> Field<T> field(Object value, DataType<T> type) {
        return field(value, false, v -> {
            return DSL.val(v, type);
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T> List<Field<T>> fields(T[] values) {
        return Arrays.asList(fieldsArray(values));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T> Field<T>[] fieldsArray(T[] values) {
        return (Field[]) map(values, v -> {
            return field(v);
        }, x$0 -> {
            return new Field[x$0];
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T> List<Field<T>> fields(Object[] values, Field<T> field) {
        if (field == null) {
            return new ArrayList();
        }
        return map(values, v -> {
            return field(v, field);
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final List<Field<?>> fields(Object[] values, Field<?>[] fields) {
        return Arrays.asList(fieldsArray(values, fields));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Field<?>[] fieldsArray(Object[] values, Field<?>[] fields) {
        return (Field[]) map(values, (v, i) -> {
            return field(v, fields[i]);
        }, x$0 -> {
            return new Field[x$0];
        });
    }

    static final <T> List<Field<T>> fields(Object[] values, DataType<T> type) {
        return Arrays.asList(fieldsArray(values, type));
    }

    static final <T> List<Field<T>> fields(Collection<?> values, DataType<T> type) {
        return map(values, v -> {
            return field(v, type);
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T> Field<T>[] fieldsArray(Object[] values, DataType<T> type) {
        return (Field[]) map(values, v -> {
            return field(v, type);
        }, x$0 -> {
            return new Field[x$0];
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final List<Field<?>> fields(Object[] values, DataType<?>[] types) {
        return Arrays.asList(fieldsArray(values, types));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Field<?>[] fieldsArray(Object[] values, DataType<?>[] types) {
        return (Field[]) map(values, (v, i) -> {
            return field(v, types[i]);
        }, x$0 -> {
            return new Field[x$0];
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final IllegalArgumentException indexFail(Fields row, Field<?> field) {
        return new IllegalArgumentException("Field (" + String.valueOf(field) + ") is not contained in Row " + String.valueOf(row));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final int indexOrFail(Fields row, Field<?> field) {
        int result = row.indexOf(field);
        if (result < 0) {
            throw indexFail(row, field);
        }
        return result;
    }

    static final IllegalArgumentException indexFail(Fields row, String fieldName) {
        throw new IllegalArgumentException("Field (" + fieldName + ") is not contained in Row " + String.valueOf(row));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final int indexOrFail(Fields row, String fieldName) {
        int result = row.indexOf(fieldName);
        if (result < 0) {
            throw indexFail(row, fieldName);
        }
        return result;
    }

    static final IllegalArgumentException indexFail(Fields row, Name fieldName) {
        throw new IllegalArgumentException("Field (" + String.valueOf(fieldName) + ") is not contained in Row " + String.valueOf(row));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final int indexOrFail(Fields row, Name fieldName) {
        int result = row.indexOf(fieldName);
        if (result < 0) {
            throw indexFail(row, fieldName);
        }
        return result;
    }

    static final IllegalArgumentException indexFail(Fields row, int fieldIndex) {
        throw new IllegalArgumentException("Field (" + fieldIndex + ") is not contained in Row " + String.valueOf(row));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final int indexOrFail(Fields row, int fieldIndex) {
        Field<?> result = row.field(fieldIndex);
        if (result == null) {
            throw indexFail(row, fieldIndex);
        }
        return fieldIndex;
    }

    private static final <T> List<T> newListWithCapacity(Iterable<?> it) {
        if (!(it instanceof Collection)) {
            return new ArrayList();
        }
        Collection<?> c = (Collection) it;
        return new ArrayList(c.size());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T, R, X extends Throwable> R apply(@Nullable T t, ThrowingFunction<? super T, ? extends R, ? extends X> f) throws Throwable {
        if (t == null) {
            return null;
        }
        return f.apply(t);
    }

    static final <T, R, X extends Throwable> R applyOrElse(@Nullable T t, ThrowingFunction<? super T, ? extends R, ? extends X> f, ThrowingSupplier<? extends R, ? extends X> s) throws Throwable {
        return t == null ? s.get() : f.apply(t);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T, X extends Throwable> T orElse(@Nullable T t, ThrowingSupplier<? extends T, ? extends X> s) throws Throwable {
        return t == null ? s.get() : t;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T> T let(@Nullable T t, Consumer<? super T> consumer) {
        if (t != null) {
            consumer.accept(t);
        }
        return t;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T, E extends Exception> boolean allMatch(T[] array, ThrowingPredicate<? super T, E> test) throws Exception {
        return !anyMatch(array, test.negate());
    }

    static final <T, E extends Exception> boolean allMatch(T[] array, ThrowingIntPredicate<? super T, E> test) throws Exception {
        return !anyMatch(array, test.negate());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T, E extends Exception> boolean allMatch(Iterable<? extends T> it, ThrowingPredicate<? super T, E> test) throws Exception {
        return !anyMatch(it, test.negate());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T, E extends Exception> boolean allMatch(Iterable<? extends T> it, ThrowingIntPredicate<? super T, E> test) throws Exception {
        return !anyMatch(it, test.negate());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T, E extends Exception> boolean anyMatch(T[] array, ThrowingPredicate<? super T, E> test) throws Exception {
        return findAny(array, test, t -> {
            return Boolean.TRUE;
        }) != null;
    }

    static final <T, E extends Exception> boolean anyMatch(T[] array, ThrowingIntPredicate<? super T, E> test) throws Exception {
        return findAny(array, test, t -> {
            return Boolean.TRUE;
        }) != null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T, E extends Exception> boolean anyMatch(Iterable<? extends T> it, ThrowingPredicate<? super T, E> test) throws Exception {
        return findAny(it, test, t -> {
            return Boolean.TRUE;
        }) != null;
    }

    static final <T, E extends Exception> boolean anyMatch(Iterable<? extends T> it, ThrowingIntPredicate<? super T, E> test) throws Exception {
        return findAny(it, test, t -> {
            return Boolean.TRUE;
        }) != null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T, E extends Exception> T findAny(T[] tArr, ThrowingPredicate<? super T, E> throwingPredicate) throws Exception {
        return (T) findAny(tArr, throwingPredicate, t -> {
            return t;
        });
    }

    static final <T, E extends Exception> T findAny(T[] tArr, ThrowingIntPredicate<? super T, E> throwingIntPredicate) throws Exception {
        return (T) findAny(tArr, throwingIntPredicate, t -> {
            return t;
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T, E extends Exception> T findAny(Iterable<? extends T> iterable, ThrowingPredicate<? super T, E> throwingPredicate) throws Exception {
        return (T) findAny(iterable, throwingPredicate, t -> {
            return t;
        });
    }

    static final <T, E extends Exception> T findAny(Iterable<? extends T> iterable, ThrowingIntPredicate<? super T, E> throwingIntPredicate) throws Exception {
        return (T) findAny(iterable, throwingIntPredicate, t -> {
            return t;
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T, U, E extends Exception> U findAny(T[] array, ThrowingPredicate<? super T, E> test, ThrowingFunction<? super T, ? extends U, E> function) throws Exception {
        if (array != null) {
            for (T t : array) {
                if (test.test(t)) {
                    return function.apply(t);
                }
            }
            return null;
        }
        return null;
    }

    static final <T, U, E extends Exception> U findAny(T[] array, ThrowingIntPredicate<? super T, E> test, ThrowingFunction<? super T, ? extends U, E> function) throws Exception {
        if (array != null) {
            int i = 0;
            for (T t : array) {
                int i2 = i;
                i++;
                if (test.test(t, i2)) {
                    return function.apply(t);
                }
            }
            return null;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T, U, E extends Exception> U findAny(Iterable<? extends T> iterable, ThrowingPredicate<? super T, E> throwingPredicate, ThrowingFunction<? super T, ? extends U, E> throwingFunction) throws Exception {
        if (iterable != null) {
            for (T t : iterable) {
                if (throwingPredicate.test(t)) {
                    return throwingFunction.apply(t);
                }
            }
            return null;
        }
        return null;
    }

    static final <T, U, E extends Exception> U findAny(Iterable<? extends T> iterable, ThrowingIntPredicate<? super T, E> throwingIntPredicate, ThrowingFunction<? super T, ? extends U, E> throwingFunction) throws Exception {
        if (iterable != null) {
            int i = 0;
            for (T t : iterable) {
                int i2 = i;
                i++;
                if (throwingIntPredicate.test(t, i2)) {
                    return throwingFunction.apply(t);
                }
            }
            return null;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Condition allNull(Field<?>[] fields) {
        return DSL.and(map(fields, (v0) -> {
            return v0.isNull();
        }));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Condition allNotNull(Field<?>[] fields) {
        return DSL.and(map(fields, (v0) -> {
            return v0.isNotNull();
        }));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T> List<List<T>> chunks(List<T> list, int size) {
        int l;
        if (size <= 0 || size == Integer.MAX_VALUE || (l = list.size()) <= size) {
            return Arrays.asList(list);
        }
        List<List<T>> result = new ArrayList<>();
        int prev = 0;
        int i = size;
        while (true) {
            int next = i;
            if (prev < l) {
                result.add(list.subList(prev, Math.min(next, l)));
                prev = next;
                i = next + size;
            } else {
                return result;
            }
        }
    }

    static final void throwChecked(Throwable t) {
        throwChecked0(t);
    }

    static final <E extends Throwable> void throwChecked0(Throwable throwable) throws Throwable {
        throw throwable;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T, R> java.util.function.Function<T, R> checkedFunction(ThrowingFunction<T, R, Throwable> function) {
        return t -> {
            try {
                return function.apply(t);
            } catch (Throwable e) {
                throwChecked(e);
                throw new IllegalStateException(e);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T, U, E extends Exception> U[] map(T[] array, ThrowingFunction<? super T, ? extends U, E> mapper, IntFunction<U[]> constructor) throws Exception {
        if (array == null) {
            return constructor.apply(0);
        }
        U[] result = constructor.apply(array.length);
        for (int i = 0; i < array.length; i++) {
            result[i] = mapper.apply(array[i]);
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <U, E extends Exception> U[] map(int[] array, ThrowingIntFunction<? extends U, E> mapper, IntFunction<U[]> constructor) throws Exception {
        if (array == null) {
            return constructor.apply(0);
        }
        U[] result = constructor.apply(array.length);
        for (int i = 0; i < array.length; i++) {
            result[i] = mapper.apply(array[i]);
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T, U, E extends Exception> U[] map(Collection<? extends T> collection, ThrowingFunction<? super T, ? extends U, E> throwingFunction, IntFunction<U[]> intFunction) throws Exception {
        if (collection == null) {
            return intFunction.apply(0);
        }
        U[] apply = intFunction.apply(collection.size());
        int i = 0;
        Iterator<? extends T> it = collection.iterator();
        while (it.hasNext()) {
            int i2 = i;
            i++;
            apply[i2] = throwingFunction.apply(it.next());
        }
        return apply;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T, U, E extends Exception> U[] map(T[] array, ThrowingObjIntFunction<? super T, ? extends U, E> mapper, IntFunction<U[]> constructor) throws Exception {
        if (array == null) {
            return constructor.apply(0);
        }
        U[] result = constructor.apply(array.length);
        for (int i = 0; i < array.length; i++) {
            result[i] = mapper.apply(array[i], i);
        }
        return result;
    }

    static final <U, E extends Exception> U[] map(int[] array, ThrowingIntIntFunction<? extends U, E> mapper, IntFunction<U[]> constructor) throws Exception {
        if (array == null) {
            return constructor.apply(0);
        }
        U[] result = constructor.apply(array.length);
        for (int i = 0; i < array.length; i++) {
            result[i] = mapper.apply(array[i], i);
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T, U, E extends Exception> U[] map(Collection<? extends T> collection, ThrowingObjIntFunction<? super T, ? extends U, E> throwingObjIntFunction, IntFunction<U[]> intFunction) throws Exception {
        if (collection == null) {
            return intFunction.apply(0);
        }
        U[] apply = intFunction.apply(collection.size());
        int i = 0;
        Iterator<? extends T> it = collection.iterator();
        while (it.hasNext()) {
            int i2 = i;
            int i3 = i;
            i++;
            apply[i2] = throwingObjIntFunction.apply(it.next(), i3);
        }
        return apply;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T, U, E extends Exception> List<U> map(T[] array, ThrowingFunction<? super T, ? extends U, E> mapper) throws Exception {
        if (array == null) {
            return Collections.emptyList();
        }
        List<U> result = new ArrayList<>(array.length);
        for (T t : array) {
            result.add(mapper.apply(t));
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <U, E extends Exception> List<U> map(int[] array, ThrowingIntFunction<? extends U, E> mapper) throws Exception {
        if (array == null) {
            return Collections.emptyList();
        }
        List<U> result = new ArrayList<>(array.length);
        for (int t : array) {
            result.add(mapper.apply(t));
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T, U, E extends Exception> List<U> map(Iterable<? extends T> iterable, ThrowingFunction<? super T, ? extends U, E> throwingFunction) throws Exception {
        if (iterable == null) {
            return Collections.emptyList();
        }
        List<U> newListWithCapacity = newListWithCapacity(iterable);
        Iterator<? extends T> it = iterable.iterator();
        while (it.hasNext()) {
            newListWithCapacity.add(throwingFunction.apply(it.next()));
        }
        return newListWithCapacity;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T, U, E extends Exception> List<U> flatMap(Iterable<? extends T> iterable, ThrowingFunction<? super T, ? extends List<? extends U>, E> throwingFunction) throws Exception {
        if (iterable == null) {
            return Collections.emptyList();
        }
        List<U> newListWithCapacity = newListWithCapacity(iterable);
        Iterator<? extends T> it = iterable.iterator();
        while (it.hasNext()) {
            newListWithCapacity.addAll(throwingFunction.apply(it.next()));
        }
        return newListWithCapacity;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T, U, E extends Exception> List<U> map(T[] array, ThrowingObjIntFunction<? super T, ? extends U, E> mapper) throws Exception {
        if (array == null) {
            return Collections.emptyList();
        }
        List<U> result = new ArrayList<>(array.length);
        for (int i = 0; i < array.length; i++) {
            result.add(mapper.apply(array[i], i));
        }
        return result;
    }

    static final <U, E extends Exception> List<U> map(int[] array, ThrowingIntIntFunction<? extends U, E> mapper) throws Exception {
        if (array == null) {
            return Collections.emptyList();
        }
        List<U> result = new ArrayList<>(array.length);
        for (int i = 0; i < array.length; i++) {
            result.add(mapper.apply(array[i], i));
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T, U, E extends Exception> List<U> map(Iterable<? extends T> iterable, ThrowingObjIntFunction<? super T, ? extends U, E> throwingObjIntFunction) throws Exception {
        if (iterable == null) {
            return Collections.emptyList();
        }
        List<U> newListWithCapacity = newListWithCapacity(iterable);
        int i = 0;
        Iterator<? extends T> it = iterable.iterator();
        while (it.hasNext()) {
            int i2 = i;
            i++;
            newListWithCapacity.add(throwingObjIntFunction.apply(it.next(), i2));
        }
        return newListWithCapacity;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @SafeVarargs
    public static final <T> T[] reverse(T... array) {
        if (array == null) {
            return null;
        }
        for (int i = 0; i < array.length / 2; i++) {
            T tmp = array[i];
            array[i] = array[(array.length - i) - 1];
            array[(array.length - i) - 1] = tmp;
        }
        return array;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T, U> Iterator<U> iterator(final Iterator<? extends T> iterator, final java.util.function.Function<? super T, ? extends U> mapper) {
        return new Iterator<U>() { // from class: org.jooq.impl.Tools.1
            @Override // java.util.Iterator
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override // java.util.Iterator
            public U next() {
                return (U) mapper.apply(iterator.next());
            }

            @Override // java.util.Iterator
            public void remove() {
                iterator.remove();
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @SafeVarargs
    public static final <T> Iterable<T> reverseIterable(T... array) {
        return reverseIterable(Arrays.asList(array));
    }

    @SafeVarargs
    static final <T> Iterator<T> reverseIterator(T... array) {
        return reverseIterator(Arrays.asList(array));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T> Iterable<T> reverseIterable(List<T> list) {
        return () -> {
            return reverseIterator(list);
        };
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T> Iterator<T> reverseIterator(final List<T> list) {
        return new Iterator<T>() { // from class: org.jooq.impl.Tools.2
            final ListIterator<T> li;

            {
                this.li = list.listIterator(list.size());
            }

            @Override // java.util.Iterator
            public boolean hasNext() {
                return this.li.hasPrevious();
            }

            @Override // java.util.Iterator
            public T next() {
                return this.li.previous();
            }

            @Override // java.util.Iterator
            public void remove() {
                this.li.remove();
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @SafeVarargs
    public static final <T> List<T> list(T... array) {
        return array == null ? Collections.emptyList() : Arrays.asList(array);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Map<Field<?>, Object> mapOfChangedValues(Record record) {
        Map<Field<?>, Object> result = new LinkedHashMap<>();
        int size = record.size();
        for (int i = 0; i < size; i++) {
            if (record.changed(i)) {
                result.put(record.field(i), record.get(i));
            }
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T> T first(Iterable<? extends T> iterable) {
        if (iterable == null) {
            return null;
        }
        Iterator<? extends T> it = iterable.iterator();
        if (it.hasNext()) {
            return it.next();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T> T last(Collection<T> collection) {
        if (collection.isEmpty()) {
            return null;
        }
        if (collection instanceof List) {
            List<T> l = (List) collection;
            return l.get(collection.size() - 1);
        }
        T last = null;
        Iterator<T> it = collection.iterator();
        while (it.hasNext()) {
            last = it.next();
        }
        return last;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final void setFetchSize(ExecuteContext ctx, int fetchSize) throws SQLException {
        int f = SettingsTools.getFetchSize(fetchSize, ctx.settings());
        if (f != 0) {
            if (log.isDebugEnabled()) {
                log.debug("Setting fetch size", Integer.valueOf(f));
            }
            PreparedStatement statement = ctx.statement();
            if (statement != null) {
                statement.setFetchSize(f);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Deprecated
    public static final <R extends Record> R filterOne(List<R> list) throws TooManyRowsException {
        int size = list.size();
        if (size == 0) {
            return null;
        }
        if (size == 1) {
            return list.get(0);
        }
        throw new TooManyRowsException("Too many rows selected : " + size);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <R extends Record> R fetchOne(Cursor<R> cursor) throws TooManyRowsException {
        return (R) fetchOne(cursor, false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <R extends Record> R fetchOne(Cursor<R> cursor, boolean hasLimit1) throws TooManyRowsException {
        try {
            Result<R> result = cursor.fetchNext(hasLimit1 ? 1 : 2);
            int size = result.size();
            if (size == 0) {
                return null;
            }
            if (size == 1) {
                R r = (R) result.get(0);
                cursor.close();
                return r;
            }
            throw exception(cursor, new TooManyRowsException("Cursor returned more than one result"));
        } finally {
            cursor.close();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <R extends Record> R fetchSingle(Cursor<R> cursor) throws NoDataFoundException, TooManyRowsException {
        return (R) fetchSingle(cursor, false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <R extends Record> R fetchSingle(Cursor<R> cursor, boolean hasLimit1) throws NoDataFoundException, TooManyRowsException {
        try {
            Result<R> result = cursor.fetchNext(hasLimit1 ? 1 : 2);
            int size = result.size();
            if (size == 0) {
                throw exception(cursor, new NoDataFoundException("Cursor returned no rows"));
            }
            if (size == 1) {
                R r = (R) result.get(0);
                cursor.close();
                return r;
            }
            throw exception(cursor, new TooManyRowsException("Cursor returned more than one result"));
        } catch (Throwable th) {
            cursor.close();
            throw th;
        }
    }

    private static final RuntimeException exception(Cursor<?> cursor, RuntimeException e) {
        if (cursor instanceof CursorImpl) {
            CursorImpl<?> c = (CursorImpl) cursor;
            c.ctx.exception(e);
            c.listener.exception(c.ctx);
            return c.ctx.exception();
        }
        return e;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <Q extends QueryPart> void visitAutoAliased(Context<?> ctx, Q q, Predicate<? super Context<?>> declaring, BiConsumer<? super Context<?>, ? super Q> visit) {
        QueryPart autoAlias;
        if (declaring.test(ctx) && (q instanceof AutoAlias) && (autoAlias = ((AutoAlias) q).autoAlias(ctx, q)) != null) {
            visit.accept(ctx, autoAlias);
        } else {
            visit.accept(ctx, q);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final void visitSubquery(Context<?> ctx, QueryPart query) {
        visitSubquery(ctx, query, 0, true);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final void visitSubquery(Context<?> ctx, QueryPart query, int characteristics) {
        visitSubquery(ctx, query, characteristics, true);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Type inference failed for: r0v8, types: [org.jooq.Context] */
    public static final void visitSubquery(Context<?> ctx, QueryPart query, int characteristics, boolean parentheses) {
        if (parentheses) {
            ctx.sql('(');
        }
        boolean previousPredicandSubquery = ctx.predicandSubquery();
        boolean previousDerivedTableSubquery = ctx.derivedTableSubquery();
        boolean previousSetOperationSubquery = ctx.setOperationSubquery();
        ctx.subquery(true, query).predicandSubquery((characteristics & 256) != 0).derivedTableSubquery((characteristics & 1) != 0).setOperationSubquery((characteristics & 2) != 0).formatIndentStart().formatNewLine().visit(query).formatIndentEnd().formatNewLine().setOperationSubquery(previousSetOperationSubquery).derivedTableSubquery(previousDerivedTableSubquery).predicandSubquery(previousPredicandSubquery).subquery(false);
        if (parentheses) {
            ctx.sql(')');
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <C extends Context<?>> C visitAll(C ctx, Collection<? extends QueryPart> parts) {
        if (parts != null) {
            for (QueryPart part : parts) {
                ctx.visit(part);
            }
        }
        return ctx;
    }

    static final <C extends Context<?>> C visitAll(C ctx, QueryPart[] parts) {
        if (parts != null) {
            for (QueryPart part : parts) {
                ctx.visit(part);
            }
        }
        return ctx;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <C extends Context<?>> C visitMappedSchema(C ctx, Name qualifiedName) {
        if (qualifiedName.qualified()) {
            Schema s = getMappedSchema(ctx, new SchemaImpl(qualifiedName.qualifier()));
            if (s != null) {
                ctx.visit(s).sql('.');
            }
            ctx.visit(qualifiedName.unqualifiedName());
        } else {
            ctx.visit(qualifiedName);
        }
        return ctx;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final void renderAndBind(Context<?> ctx, String sql, List<QueryPart> substitutes) {
        RenderContext renderContext;
        BindContext bindContext;
        if (Boolean.TRUE.equals(ctx.settings().isRenderPlainSQLTemplatesAsRaw())) {
            ctx.sql(sql);
            return;
        }
        if (ctx instanceof RenderContext) {
            RenderContext r = (RenderContext) ctx;
            renderContext = r;
        } else {
            renderContext = null;
        }
        RenderContext render = renderContext;
        if (ctx instanceof BindContext) {
            BindContext b = (BindContext) ctx;
            bindContext = b;
        } else {
            bindContext = null;
        }
        BindContext bind = bindContext;
        int substituteIndex = 0;
        char[] sqlChars = sql.toCharArray();
        if (render == null) {
            render = new DefaultRenderContext(bind.configuration(), ctx.executeContext());
        }
        SQLDialect family = render.family();
        boolean mysql = DefaultParseContext.SUPPORTS_HASH_COMMENT_SYNTAX.contains(render.dialect());
        char[][][] quotes = Identifiers.QUOTES.get(family);
        boolean needsBackslashEscaping = needsBackslashEscaping(ctx.configuration());
        int i = 0;
        while (i < sqlChars.length) {
            if (peek(sqlChars, i, TOKEN_SINGLE_LINE_COMMENT) || peek(sqlChars, i, TOKEN_SINGLE_LINE_COMMENT_C) || (mysql && peek(sqlChars, i, TOKEN_HASH))) {
                while (i < sqlChars.length && sqlChars[i] != '\r' && sqlChars[i] != '\n') {
                    int i2 = i;
                    i++;
                    render.sql(sqlChars[i2]);
                }
                if (i < sqlChars.length) {
                    render.sql(sqlChars[i]);
                }
            } else if (peek(sqlChars, i, TOKEN_MULTI_LINE_COMMENT_OPEN)) {
                int nestedMultilineCommentLevel = 1;
                do {
                    int i3 = i;
                    i++;
                    render.sql(sqlChars[i3]);
                    if (peek(sqlChars, i, TOKEN_MULTI_LINE_COMMENT_OPEN)) {
                        nestedMultilineCommentLevel++;
                    } else if (peek(sqlChars, i, TOKEN_MULTI_LINE_COMMENT_CLOSE)) {
                        nestedMultilineCommentLevel--;
                    }
                } while (nestedMultilineCommentLevel != 0);
                render.sql(sqlChars[i]);
            } else {
                if (sqlChars[i] == '\'') {
                    int i4 = i;
                    i++;
                    render.sql(sqlChars[i4]);
                    while (i < sqlChars.length) {
                        if (sqlChars[i] == '\\' && needsBackslashEscaping) {
                            int i5 = i;
                            i++;
                            render.sql(sqlChars[i5]);
                        } else if (peek(sqlChars, i, TOKEN_ESCAPED_APOS)) {
                            int i6 = i;
                            i++;
                            render.sql(sqlChars[i6]);
                        } else if (peek(sqlChars, i, TOKEN_APOS)) {
                            render.sql(sqlChars[i]);
                        }
                        int i7 = i;
                        i++;
                        render.sql(sqlChars[i7]);
                    }
                    return;
                }
                if ((sqlChars[i] == 'e' || sqlChars[i] == 'E') && SUPPORT_POSTGRES_LITERALS.contains(ctx.dialect()) && i + 1 < sqlChars.length && sqlChars[i + 1] == '\'') {
                    int i8 = i;
                    int i9 = i + 1;
                    render.sql(sqlChars[i8]);
                    i = i9 + 1;
                    render.sql(sqlChars[i9]);
                    while (true) {
                        if (sqlChars[i] == '\\') {
                            int i10 = i;
                            i++;
                            render.sql(sqlChars[i10]);
                        } else if (peek(sqlChars, i, TOKEN_ESCAPED_APOS)) {
                            int i11 = i;
                            i++;
                            render.sql(sqlChars[i11]);
                        } else if (peek(sqlChars, i, TOKEN_APOS)) {
                            break;
                        }
                        int i12 = i;
                        i++;
                        render.sql(sqlChars[i12]);
                    }
                    render.sql(sqlChars[i]);
                } else {
                    if (peekAny(sqlChars, i, quotes[0])) {
                        int delimiter = 0;
                        int d = 0;
                        while (true) {
                            if (d >= quotes[0].length) {
                                break;
                            }
                            if (!peek(sqlChars, i, quotes[0][d])) {
                                d++;
                            } else {
                                delimiter = d;
                                break;
                            }
                        }
                        for (int d2 = 0; d2 < quotes[0][delimiter].length; d2++) {
                            int i13 = i;
                            i++;
                            render.sql(sqlChars[i13]);
                        }
                        while (i < sqlChars.length) {
                            if (peek(sqlChars, i, quotes[2][delimiter])) {
                                for (int d3 = 0; d3 < quotes[2][delimiter].length; d3++) {
                                    int i14 = i;
                                    i++;
                                    render.sql(sqlChars[i14]);
                                }
                            } else if (!peek(sqlChars, i, quotes[1][delimiter])) {
                                int i15 = i;
                                i++;
                                render.sql(sqlChars[i15]);
                            } else {
                                for (int d4 = 0; d4 < quotes[1][delimiter].length; d4++) {
                                    if (d4 > 0) {
                                        i++;
                                    }
                                    render.sql(sqlChars[i]);
                                }
                            }
                        }
                        return;
                    }
                    if (substituteIndex < substitutes.size() && (sqlChars[i] == '?' || (sqlChars[i] == ':' && i + 1 < sqlChars.length && Character.isJavaIdentifierPart(sqlChars[i + 1]) && (i - 1 < 0 || sqlChars[i - 1] != ':')))) {
                        if (sqlChars[i] == '?' && i + 1 < sqlChars.length && SUPPORT_NON_BIND_VARIABLE_SUFFIXES.contains(ctx.dialect())) {
                            for (char[] candidate : NON_BIND_VARIABLE_SUFFIXES) {
                                if (peek(sqlChars, i + 1, candidate)) {
                                    for (char[] exclude : BIND_VARIABLE_SUFFIXES) {
                                        if (peek(sqlChars, i + 1, exclude)) {
                                            break;
                                        }
                                    }
                                    int j = i;
                                    while (i - j <= candidate.length) {
                                        render.sql(sqlChars[i]);
                                        i++;
                                    }
                                    if (i < sqlChars.length) {
                                        render.sql(sqlChars[i]);
                                    }
                                }
                            }
                        }
                        if (sqlChars[i] == ':') {
                            while (i + 1 < sqlChars.length && Character.isJavaIdentifierPart(sqlChars[i + 1])) {
                                i++;
                            }
                        }
                        int i16 = substituteIndex;
                        substituteIndex++;
                        QueryPart substitute = substitutes.get(i16);
                        if (render.paramType() == ParamType.INLINED || render.paramType() == ParamType.NAMED || render.paramType() == ParamType.NAMED_OR_INLINED) {
                            render.visit(substitute);
                        } else {
                            RenderContext.CastMode previous = render.castMode();
                            ((RenderContext) render.castMode(RenderContext.CastMode.NEVER)).visit(substitute).castMode(previous);
                        }
                        if (bind != null) {
                            bind.visit(substitute);
                        }
                    } else if (sqlChars[i] == '{') {
                        if (peekAny(sqlChars, i, JDBC_ESCAPE_PREFIXES, true)) {
                            render.sql(sqlChars[i]);
                        } else {
                            i++;
                            while (i < sqlChars.length && sqlChars[i] != '}') {
                                i++;
                            }
                            int end = i;
                            Integer index = Ints.tryParse(sql, i, end);
                            if (index != null) {
                                if (index.intValue() < 0 || index.intValue() >= substitutes.size()) {
                                    throw new TemplatingException("No substitute QueryPart provided for placeholder {" + index + "} in plain SQL template: " + sql);
                                }
                                QueryPart substitute2 = substitutes.get(index.intValue());
                                render.visit(substitute2);
                                if (bind != null) {
                                    bind.visit(substitute2);
                                }
                            } else {
                                render.visit(DSL.keyword(sql.substring(i, end)));
                            }
                        }
                    } else {
                        render.sql(sqlChars[i]);
                    }
                }
            }
            i++;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final boolean needsBackslashEscaping(Configuration configuration) {
        BackslashEscaping escaping = SettingsTools.getBackslashEscaping(configuration.settings());
        return escaping == BackslashEscaping.ON || (escaping == BackslashEscaping.DEFAULT && REQUIRES_BACKSLASH_ESCAPING.contains(configuration.dialect()));
    }

    static final boolean peek(char[] sqlChars, int index, char[] peek) {
        return peek(sqlChars, index, peek, false);
    }

    static final boolean peek(char[] sqlChars, int index, char[] peek, boolean anyWhitespace) {
        for (int i = 0; i < peek.length; i++) {
            if (index + i >= sqlChars.length) {
                return false;
            }
            if (sqlChars[index + i] != peek[i]) {
                if (anyWhitespace && peek[i] == ' ') {
                    for (char c : WHITESPACE_CHARACTERS) {
                        if (sqlChars[index + i] == c) {
                            break;
                        }
                    }
                    return false;
                }
                return false;
            }
        }
        return true;
    }

    static final boolean peekAny(char[] sqlChars, int index, char[][] peekAny) {
        return peekAny(sqlChars, index, peekAny, false);
    }

    static final boolean peekAny(char[] sqlChars, int index, char[][] peekAny, boolean anyWhitespace) {
        for (char[] peek : peekAny) {
            if (peek(sqlChars, index, peek, anyWhitespace)) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final List<QueryPart> queryParts(Object... substitutes) {
        if (substitutes == null) {
            return queryParts(null);
        }
        List<QueryPart> result = new ArrayList<>(substitutes.length);
        int length = substitutes.length;
        for (int i = 0; i < length; i++) {
            Object substitute = substitutes[i];
            if (substitute instanceof QueryPart) {
                QueryPart q = (QueryPart) substitute;
                result.add(q);
            } else {
                Class<Object> type = substitute != null ? substitute.getClass() : Object.class;
                result.add(new Val(substitute, DSL.getDataType(type), true));
            }
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T> T[] combine(T t, T[] tArr) {
        T[] tArr2 = (T[]) ((Object[]) java.lang.reflect.Array.newInstance(tArr.getClass().getComponentType(), tArr.length + 1));
        tArr2[0] = t;
        System.arraycopy(tArr, 0, tArr2, 1, tArr.length);
        return tArr2;
    }

    static final <T> T[] combine(T[] tArr, T t) {
        T[] tArr2 = (T[]) Arrays.copyOf(tArr, tArr.length + 1);
        tArr2[tArr.length] = t;
        return tArr2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T> T[] combine(T[] tArr, T[] tArr2) {
        T[] tArr3 = (T[]) Arrays.copyOf(tArr, tArr.length + tArr2.length);
        System.arraycopy(tArr2, 0, tArr3, tArr.length, tArr2.length);
        return tArr3;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Field<?>[] combine(Field<?> field, Field<?>... fields) {
        if (fields == null) {
            return new Field[]{field};
        }
        Field<?>[] result = new Field[fields.length + 1];
        result[0] = field;
        System.arraycopy(fields, 0, result, 1, fields.length);
        return result;
    }

    static final Field<?>[] combine(Field<?> field1, Field<?> field2, Field<?>... fields) {
        if (fields == null) {
            return new Field[]{field1, field2};
        }
        Field<?>[] result = new Field[fields.length + 2];
        result[0] = field1;
        result[1] = field2;
        System.arraycopy(fields, 0, result, 2, fields.length);
        return result;
    }

    static final Field<?>[] combine(Field<?> field1, Field<?> field2, Field<?> field3, Field<?>... fields) {
        if (fields == null) {
            return new Field[]{field1, field2, field3};
        }
        Field<?>[] result = new Field[fields.length + 3];
        result[0] = field1;
        result[1] = field2;
        result[2] = field3;
        System.arraycopy(fields, 0, result, 3, fields.length);
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final RuntimeException translate(String sql, Throwable t) {
        if (t instanceof R2dbcException) {
            R2dbcException e = (R2dbcException) t;
            return translate(sql, e);
        }
        if (t instanceof SQLException) {
            SQLException e2 = (SQLException) t;
            return translate(sql, e2);
        }
        if (t instanceof RuntimeException) {
            RuntimeException e3 = (RuntimeException) t;
            return translate(sql, e3);
        }
        if (t != null) {
            return new DataAccessException("SQL [" + sql + "]; Unspecified Throwable", t);
        }
        return new DataAccessException("SQL [" + sql + "]; Unspecified Throwable");
    }

    static final DataAccessException translate(String sql, R2dbcException e) {
        if (e != null) {
            return translate(sql, e, DataAccessException.sqlStateClass(e));
        }
        return new DataAccessException("SQL [" + sql + "]; Unspecified R2dbcException");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final DataAccessException translate(String sql, SQLException e) {
        if (e != null) {
            return translate(sql, e, DataAccessException.sqlStateClass(e));
        }
        return new DataAccessException("SQL [" + sql + "]; Unspecified SQLException");
    }

    private static final DataAccessException translate(String sql, Exception e, SQLStateClass sqlState) {
        switch (sqlState) {
            case C22_DATA_EXCEPTION:
                return new DataException("SQL [" + sql + "]; " + e.getMessage(), e);
            case C23_INTEGRITY_CONSTRAINT_VIOLATION:
                return new IntegrityConstraintViolationException("SQL [" + sql + "]; " + e.getMessage(), e);
            default:
                return new DataAccessException("SQL [" + sql + "]; " + e.getMessage(), e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final RuntimeException translate(String sql, RuntimeException e) {
        if (e != null) {
            return e;
        }
        return new DataAccessException("SQL [" + sql + "]; Unspecified RuntimeException");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final void safeClose(ExecuteListener listener, ExecuteContext ctx) {
        safeClose(listener, ctx, false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final void safeClose(ExecuteListener listener, ExecuteContext ctx, boolean keepStatement) {
        safeClose(listener, ctx, keepStatement, true);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final void safeClose(ExecuteListener listener, ExecuteContext ctx, boolean keepStatement, boolean keepResultSet) {
        JDBCUtils.safeClose(ctx.resultSet());
        ctx.resultSet(null);
        PreparedStatement statement = ctx.statement();
        if (statement != null) {
            consumeWarnings(ctx, listener);
        }
        if (!keepStatement) {
            if (statement != null) {
                JDBCUtils.safeClose((Statement) statement);
                ctx.statement(null);
            } else {
                Connection connection = DefaultExecuteContext.localConnection();
                if (connection != null && ((DefaultExecuteContext) ctx).connectionProvider != null) {
                    ((DefaultExecuteContext) ctx).connectionProvider.release(connection);
                }
            }
        }
        if (keepResultSet) {
            listener.end(ctx);
        }
        DefaultExecuteContext.clean();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T> void setValue(Record target, Field<T> targetField, Record source, Field<?> sourceField) {
        setValue(target, targetField, source.get(sourceField));
    }

    static final <T> void setValue(AbstractRecord target, Field<T> targetField, int targetIndex, Record source, int sourceIndex) {
        setValue(target, targetField, targetIndex, source.get(sourceIndex));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T> void setValue(Record target, Field<T> targetField, Object value) {
        target.set(targetField, targetField.getDataType().convert(value));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T> void setValue(AbstractRecord target, Field<T> targetField, int targetIndex, Object value) {
        target.set((Field<int>) targetField, targetIndex, (int) targetField.getDataType().convert(value));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T> void copyValue(AbstractRecord target, Field<T> targetField, Record source, Field<?> sourceField) {
        DataType<T> targetType = targetField.getDataType();
        int targetIndex = indexOrFail((Fields) target.fieldsRow(), (Field<?>) targetField);
        int sourceIndex = indexOrFail(source.fieldsRow(), sourceField);
        target.values[targetIndex] = targetType.convert(source.get(sourceIndex));
        target.originals[targetIndex] = targetType.convert(source.original(sourceIndex));
        target.changed.set(targetIndex, source.changed(sourceIndex));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Catalog getMappedCatalog(Scope scope, Catalog catalog) {
        if (scope != null) {
            return scope.configuration().schemaMapping().map(catalog);
        }
        return catalog;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Schema getMappedSchema(Scope scope, Schema schema) {
        if (scope != null) {
            return scope.configuration().schemaMapping().map(schema);
        }
        return schema;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <R extends Record> Table<R> getMappedTable(Scope scope, Table<R> table) {
        if (scope != null) {
            return scope.configuration().schemaMapping().map(table);
        }
        return table;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <R extends UDTRecord<R>> UDT<R> getMappedUDT(Scope scope, UDT<R> udt) {
        if (scope != null) {
            return scope.configuration().schemaMapping().map(udt);
        }
        return udt;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final RecordQualifier<?> getMappedQualifier(Scope scope, RecordQualifier<?> qualifier) {
        if (scope != null) {
            if (qualifier instanceof UDT) {
                UDT<?> u = (UDT) qualifier;
                return scope.configuration().schemaMapping().map(u);
            }
            if (qualifier instanceof Table) {
                Table<?> t = (Table) qualifier;
                return scope.configuration().schemaMapping().map(t);
            }
        }
        return qualifier;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final String getMappedUDTName(Scope scope, Class<? extends QualifiedRecord<?>> type) {
        return getMappedUDTName(scope, (QualifiedRecord<?>) newRecord(false, (Class) type).operate(null));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final String getMappedUDTName(Scope scope, QualifiedRecord<?> record) {
        RecordQualifier<?> udt = record.getQualifier();
        RecordQualifier<?> mappedUDT = getMappedQualifier(scope, udt);
        if (mappedUDT != null && mappedUDT != udt) {
            return mappedUDT.getQualifiedName().unquotedName().toString();
        }
        Schema mapped = getMappedSchema(scope, udt.getSchema());
        StringBuilder sb = new StringBuilder();
        if (mapped != null && !"".equals(mapped.getName())) {
            sb.append(mapped.getName()).append('.');
        }
        sb.append(record.getQualifier().getName());
        return sb.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final RecordQualifier<?> getRecordQualifier(DataType<?> t) {
        return getRecordQualifier(t.getType());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final RecordQualifier<?> getRecordQualifier(Class<?> t) {
        try {
            return ((QualifiedRecord) t.getDeclaredConstructor(new Class[0]).newInstance(new Object[0])).getQualifier();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final String autoAlias(QueryPart part) {
        return "alias_" + Internal.hash(part);
    }

    static final Name autoAliasName(QueryPart part) {
        return DSL.name(autoAlias(part));
    }

    static final Field<String> escapeForLike(Field<?> field) {
        return escapeForLike(field, new DefaultConfiguration());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Field<String> escapeForLike(Field<?> field, Configuration configuration) {
        if (nullSafeDataType(field).isString()) {
            return DSL.escape((Field<String>) field, '!');
        }
        return castIfNeeded(field, String.class);
    }

    static final boolean isParam(Field<?> field) {
        return field instanceof Param;
    }

    static final boolean isParamOrCastParam(Field<?> field) {
        return (field instanceof Param) || ((field instanceof Cast) && isParamOrCastParam(((Cast) field).$field()));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final boolean isVal(Field<?> field) {
        return (field instanceof Val) || ((field instanceof ConvertedVal) && (((ConvertedVal) field).delegate instanceof Val));
    }

    static final boolean isWindow(QueryPart part) {
        return (part instanceof AbstractWindowFunction) && ((AbstractWindowFunction) part).isWindow();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final boolean isSimple(Context<?> ctx, QueryPart part) {
        return (part instanceof SimpleQueryPart) || ((part instanceof SimpleCheckQueryPart) && ((SimpleCheckQueryPart) part).isSimple(ctx));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final boolean isSimple(Context<?> ctx, QueryPart... parts) {
        for (QueryPart part : parts) {
            if (!isSimple(ctx, part)) {
                return false;
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final boolean isRendersSeparator(QueryPart part) {
        return (part instanceof SeparatedQueryPart) && ((SeparatedQueryPart) part).rendersSeparator();
    }

    static final boolean isPossiblyNullable(Field<?> f) {
        return (f instanceof AbstractField) && ((AbstractField) f).isPossiblyNullable();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Val<?> extractVal(Field<?> field) {
        if (field instanceof Val) {
            Val<?> v = (Val) field;
            return v;
        }
        if (field instanceof ConvertedVal) {
            ConvertedVal<?> v2 = (ConvertedVal) field;
            return (Val) v2.delegate;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final boolean hasDefaultConverter(Field<?> field) {
        return field.getConverter() instanceof IdentityConverter;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T> T extractParamValue(Field<T> field) {
        if (isParam(field)) {
            return (T) ((Param) field).getValue();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <R extends Record> SelectQueryImpl<R> selectQueryImpl(QueryPart part) {
        if (part instanceof SelectQueryImpl) {
            SelectQueryImpl s = (SelectQueryImpl) part;
            return s;
        }
        if (part instanceof SelectImpl) {
            SelectImpl s2 = (SelectImpl) part;
            return (SelectQueryImpl) s2.getDelegate();
        }
        if (part instanceof ScalarSubquery) {
            ScalarSubquery<?> s3 = (ScalarSubquery) part;
            return selectQueryImpl(s3.query);
        }
        if (part instanceof QuantifiedSelectImpl) {
            QuantifiedSelectImpl<?> s4 = (QuantifiedSelectImpl) part;
            return selectQueryImpl(s4.query);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final AbstractResultQuery<?> abstractResultQuery(Query query) {
        if (query instanceof AbstractResultQuery) {
            AbstractResultQuery<?> q = (AbstractResultQuery) query;
            return q;
        }
        if (query instanceof AbstractDelegatingQuery) {
            AbstractDelegatingQuery<?, ?> q2 = (AbstractDelegatingQuery) query;
            return abstractResultQuery(q2.getDelegate());
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final InsertQueryImpl<?> insertQueryImpl(Query query) {
        AbstractDMLQuery<?> result = abstractDMLQuery(query);
        if (result instanceof InsertQueryImpl) {
            InsertQueryImpl<?> q = (InsertQueryImpl) result;
            return q;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final UpdateQueryImpl<?> updateQueryImpl(Query query) {
        AbstractDMLQuery<?> result = abstractDMLQuery(query);
        if (result instanceof UpdateQueryImpl) {
            UpdateQueryImpl<?> q = (UpdateQueryImpl) result;
            return q;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final DeleteQueryImpl<?> deleteQueryImpl(Query query) {
        AbstractDMLQuery<?> result = abstractDMLQuery(query);
        if (result instanceof DeleteQueryImpl) {
            DeleteQueryImpl<?> q = (DeleteQueryImpl) result;
            return q;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Type inference failed for: r0v14, types: [org.jooq.CloseableQuery, org.jooq.Query] */
    public static final AbstractDMLQuery<?> abstractDMLQuery(Query query) {
        if (query instanceof AbstractDMLQuery) {
            AbstractDMLQuery<?> q = (AbstractDMLQuery) query;
            return q;
        }
        if (query instanceof AbstractDelegatingDMLQuery) {
            AbstractDelegatingDMLQuery<?, ?> q2 = (AbstractDelegatingDMLQuery) query;
            return abstractDMLQuery(q2.getDelegate());
        }
        if (query instanceof AbstractDMLQueryAsResultQuery) {
            AbstractDMLQueryAsResultQuery<?, ?> q3 = (AbstractDMLQueryAsResultQuery) query;
            return q3.getDelegate();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final int degree(ResultQuery<?> query) {
        return query.types().length;
    }

    static final List<DataType<?>> dataTypes(ResultQuery<?> query) {
        return Arrays.asList(query.dataTypes());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final DataType<?> scalarType(ResultQuery<?> query) {
        List<DataType<?>> list = dataTypes(query);
        if (list.size() != 1) {
            throw new IllegalStateException("Only single-column selects have a scalar type");
        }
        return list.get(0);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final void addConditions(ConditionProvider query, Record record, Field<?>... keys) {
        for (Field<?> field : keys) {
            addCondition(query, record, field);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T> void addCondition(ConditionProvider provider, Record record, Field<T> field) {
        if (SettingsTools.updatablePrimaryKeys(settings(record))) {
            provider.addConditions(condition(field, record.original(field)));
        } else {
            provider.addConditions(condition(field, record.get(field)));
        }
    }

    static final <T> Condition condition(Field<T> field, T value) {
        return value == null ? field.isNull() : field.eq((Field<T>) value);
    }

    static final JPANamespace jpaNamespace() {
        if (jpaNamespace == null) {
            synchronized (initLock) {
                if (jpaNamespace == null) {
                    try {
                        Class.forName(Column.class.getName());
                        jpaNamespace = JPANamespace.JAKARTA;
                    } catch (Throwable th) {
                        try {
                            Class.forName(new String("javax.persistence.") + new String("Column"));
                            jpaNamespace = JPANamespace.JAVAX;
                            JooqLogger.getLogger(Tools.class, "isJPAAvailable", 1).info("javax.persistence.Column was found on the classpath instead of jakarta.persistence.Column. jOOQ 3.16 requires you to upgrade to Jakarta EE if you wish to use JPA annotations in your DefaultRecordMapper");
                        } catch (Throwable th2) {
                            jpaNamespace = JPANamespace.NONE;
                        }
                    }
                }
            }
        }
        return jpaNamespace;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final boolean isKotlinAvailable() {
        if (isKotlinAvailable == null) {
            synchronized (initLock) {
                if (isKotlinAvailable == null) {
                    try {
                        if (ktJvmClassMapping() != null) {
                            if (ktKClasses() != null) {
                                isKotlinAvailable = true;
                            } else {
                                isKotlinAvailable = false;
                                log.info("Kotlin is available, but not kotlin-reflect. Add the kotlin-reflect dependency to better use Kotlin features like data classes");
                            }
                        } else {
                            isKotlinAvailable = false;
                        }
                    } catch (ReflectException e) {
                        isKotlinAvailable = false;
                    }
                }
            }
        }
        return isKotlinAvailable.booleanValue();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Reflect ktJvmClassMapping() {
        if (ktJvmClassMapping == null) {
            synchronized (initLock) {
                if (ktJvmClassMapping == null) {
                    try {
                        ktJvmClassMapping = Reflect.onClass("kotlin.jvm.JvmClassMappingKt");
                    } catch (ReflectException e) {
                    }
                }
            }
        }
        return ktJvmClassMapping;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Reflect ktKClasses() {
        if (ktKClasses == null) {
            synchronized (initLock) {
                if (ktKClasses == null) {
                    try {
                        ktKClasses = Reflect.onClass("kotlin.reflect.full.KClasses");
                    } catch (ReflectException e) {
                    }
                }
            }
        }
        return ktKClasses;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Reflect ktKClass() {
        if (ktKClass == null) {
            synchronized (initLock) {
                if (ktKClass == null) {
                    try {
                        ktKClass = Reflect.onClass("kotlin.reflect.KClass");
                    } catch (ReflectException e) {
                    }
                }
            }
        }
        return ktKClass;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Reflect ktKTypeParameter() {
        if (ktKTypeParameter == null) {
            synchronized (initLock) {
                if (ktKTypeParameter == null) {
                    try {
                        ktKTypeParameter = Reflect.onClass("kotlin.reflect.KTypeParameter");
                    } catch (ReflectException e) {
                    }
                }
            }
        }
        return ktKTypeParameter;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final boolean hasColumnAnnotations(Configuration configuration, Class<?> type) {
        return ((Boolean) Cache.run(configuration, () -> {
            switch (jpaNamespace()) {
                case JAVAX:
                    if (anyMatch(type.getAnnotations(), isJavaxPersistenceAnnotation())) {
                        JooqLogger.getLogger(Tools.class, "hasColumnAnnotations", 1).warn("Type " + String.valueOf(type) + " is annotated with javax.persistence annotation for usage in DefaultRecordMapper, but starting from jOOQ 3.16, only JakartaEE annotations are supported.");
                    }
                    if (anyMatch(map(type.getMethods(), m -> {
                        return Boolean.valueOf(anyMatch(m.getAnnotations(), isJavaxPersistenceAnnotation()));
                    }), b -> {
                        return b.booleanValue();
                    })) {
                        JooqLogger.getLogger(Tools.class, "hasColumnAnnotations", 1).warn("Type " + String.valueOf(type) + " has methods annotated with javax.persistence annotation for usage in DefaultRecordMapper, but starting from jOOQ 3.16, only JakartaEE annotations are supported.");
                    }
                    if (anyMatch(map(type.getDeclaredMethods(), m2 -> {
                        return Boolean.valueOf(anyMatch(m2.getAnnotations(), isJavaxPersistenceAnnotation()));
                    }), b2 -> {
                        return b2.booleanValue();
                    })) {
                        JooqLogger.getLogger(Tools.class, "hasColumnAnnotations", 1).warn("Type " + String.valueOf(type) + " has methods annotated with javax.persistence annotation for usage in DefaultRecordMapper, but starting from jOOQ 3.16, only JakartaEE annotations are supported.");
                    }
                    if (anyMatch(map(type.getFields(), f -> {
                        return Boolean.valueOf(anyMatch(f.getAnnotations(), isJavaxPersistenceAnnotation()));
                    }), b3 -> {
                        return b3.booleanValue();
                    })) {
                        JooqLogger.getLogger(Tools.class, "hasColumnAnnotations", 1).warn("Type " + String.valueOf(type) + " has fields annotated with javax.persistence annotation for usage in DefaultRecordMapper, but starting from jOOQ 3.16, only JakartaEE annotations are supported.");
                    }
                    if (anyMatch(map(type.getDeclaredFields(), f2 -> {
                        return Boolean.valueOf(anyMatch(f2.getAnnotations(), isJavaxPersistenceAnnotation()));
                    }), b4 -> {
                        return b4.booleanValue();
                    })) {
                        JooqLogger.getLogger(Tools.class, "hasColumnAnnotations", 1).warn("Type " + String.valueOf(type) + " has fields annotated with javax.persistence annotation for usage in DefaultRecordMapper, but starting from jOOQ 3.16, only JakartaEE annotations are supported.");
                    }
                    return false;
                case JAKARTA:
                    if (type.getAnnotation(Entity.class) == null && type.getAnnotation(jakarta.persistence.Table.class) == null && !anyMatch(getInstanceMembers(type), m3 -> {
                        return (m3.getAnnotation(Column.class) == null && m3.getAnnotation(Id.class) == null) ? false : true;
                    })) {
                        return Boolean.valueOf(anyMatch(getInstanceMethods(type), m4 -> {
                            return m4.getAnnotation(Column.class) != null;
                        }));
                    }
                    return true;
                case NONE:
                default:
                    return false;
            }
        }, CacheType.REFLECTION_CACHE_HAS_COLUMN_ANNOTATIONS, () -> {
            return type;
        })).booleanValue();
    }

    private static final ThrowingPredicate<? super Annotation, RuntimeException> isJavaxPersistenceAnnotation() {
        return a -> {
            return a.annotationType().getName().startsWith("javax.persistence.");
        };
    }

    static final <T extends AccessibleObject> T accessible(T t, boolean z) {
        return z ? (T) Reflect.accessible(t) : t;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final List<java.lang.reflect.Field> getAnnotatedMembers(Configuration configuration, Class<?> type, String name, boolean makeAccessible) {
        return (List) Cache.run(configuration, () -> {
            List<java.lang.reflect.Field> result = new ArrayList<>();
            for (java.lang.reflect.Field member : getInstanceMembers(type)) {
                Column column = member.getAnnotation(Column.class);
                if (column != null) {
                    if (namesMatch(name, column.name())) {
                        result.add((java.lang.reflect.Field) accessible(member, makeAccessible));
                    }
                } else {
                    Id id = member.getAnnotation(Id.class);
                    if (id != null && namesMatch(name, member.getName())) {
                        result.add((java.lang.reflect.Field) accessible(member, makeAccessible));
                    }
                }
            }
            return result;
        }, CacheType.REFLECTION_CACHE_GET_ANNOTATED_MEMBERS, () -> {
            return Cache.key(type, name, Boolean.valueOf(makeAccessible));
        });
    }

    private static final boolean namesMatch(String name, String annotation) {
        if (annotation.startsWith("\"")) {
            return ("\"" + name + "\"").equals(annotation);
        }
        return name.equalsIgnoreCase(annotation);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final List<java.lang.reflect.Field> getMatchingMembers(Configuration configuration, Class<?> type, String name, boolean makeAccessible) {
        return (List) Cache.run(configuration, () -> {
            List<java.lang.reflect.Field> result = new ArrayList<>();
            String camelCaseLC = StringUtils.toCamelCaseLC(name);
            for (java.lang.reflect.Field member : getInstanceMembers(type)) {
                if (name.equals(member.getName())) {
                    result.add((java.lang.reflect.Field) accessible(member, makeAccessible));
                } else if (camelCaseLC.equals(member.getName())) {
                    result.add((java.lang.reflect.Field) accessible(member, makeAccessible));
                }
            }
            return result;
        }, CacheType.REFLECTION_CACHE_GET_MATCHING_MEMBERS, () -> {
            return Cache.key(type, name, Boolean.valueOf(makeAccessible));
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final List<Method> getAnnotatedSetters(Configuration configuration, Class<?> type, String name, boolean makeAccessible) {
        return (List) Cache.run(configuration, () -> {
            String str;
            Set<SourceMethod> set = new LinkedHashSet<>();
            for (Method method : getInstanceMethods(type)) {
                Column column = method.getAnnotation(Column.class);
                if (column != null && namesMatch(name, column.name())) {
                    if (method.getParameterTypes().length == 1) {
                        set.add(new SourceMethod((Method) accessible(method, makeAccessible)));
                    } else if (method.getParameterTypes().length == 0) {
                        String m = method.getName();
                        if (m.startsWith(BeanUtil.PREFIX_GETTER_GET)) {
                            str = m.substring(3);
                        } else if (m.startsWith(BeanUtil.PREFIX_GETTER_IS)) {
                            str = m.substring(2);
                        } else {
                            str = null;
                        }
                        String suffix = str;
                        if (suffix != null) {
                            try {
                                Method setter = getInstanceMethod(type, "set" + suffix, new Class[]{method.getReturnType()});
                                if (setter.getAnnotation(Column.class) == null) {
                                    set.add(new SourceMethod((Method) accessible(setter, makeAccessible)));
                                }
                            } catch (NoSuchMethodException e) {
                            }
                        }
                    }
                }
            }
            return SourceMethod.methods(set);
        }, CacheType.REFLECTION_CACHE_GET_ANNOTATED_SETTERS, () -> {
            return Cache.key(type, name, Boolean.valueOf(makeAccessible));
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Method getAnnotatedGetter(Configuration configuration, Class<?> type, String name, boolean makeAccessible) {
        return (Method) Cache.run(configuration, () -> {
            for (Method method : getInstanceMethods(type)) {
                Column column = method.getAnnotation(Column.class);
                if (column != null && namesMatch(name, column.name())) {
                    if (method.getParameterTypes().length == 0) {
                        return (Method) accessible(method, makeAccessible);
                    }
                    if (method.getParameterTypes().length == 1) {
                        String m = method.getName();
                        if (m.startsWith("set")) {
                            try {
                                Method getter1 = type.getMethod("get" + m.substring(3), new Class[0]);
                                if (getter1.getAnnotation(Column.class) == null) {
                                    return (Method) accessible(getter1, makeAccessible);
                                }
                            } catch (NoSuchMethodException e) {
                            }
                            try {
                                Method getter2 = type.getMethod("is" + m.substring(3), new Class[0]);
                                if (getter2.getAnnotation(Column.class) == null) {
                                    return (Method) accessible(getter2, makeAccessible);
                                }
                                continue;
                            } catch (NoSuchMethodException e2) {
                            }
                        } else {
                            continue;
                        }
                    } else {
                        continue;
                    }
                }
            }
            return null;
        }, CacheType.REFLECTION_CACHE_GET_ANNOTATED_GETTER, () -> {
            return Cache.key(type, name, Boolean.valueOf(makeAccessible));
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final List<Method> getMatchingSetters(Configuration configuration, Class<?> type, String name, boolean makeAccessible) {
        return (List) Cache.run(configuration, () -> {
            Set<SourceMethod> set = new LinkedHashSet<>();
            String camelCase = StringUtils.toCamelCase(name);
            String camelCaseLC = StringUtils.toLC(camelCase);
            for (Method method : getInstanceMethods(type)) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length == 1) {
                    if (name.equals(method.getName())) {
                        set.add(new SourceMethod((Method) accessible(method, makeAccessible)));
                    } else if (camelCaseLC.equals(method.getName())) {
                        set.add(new SourceMethod((Method) accessible(method, makeAccessible)));
                    } else if (("set" + name).equals(method.getName())) {
                        set.add(new SourceMethod((Method) accessible(method, makeAccessible)));
                    } else if (("set" + camelCase).equals(method.getName())) {
                        set.add(new SourceMethod((Method) accessible(method, makeAccessible)));
                    }
                }
            }
            return SourceMethod.methods(set);
        }, CacheType.REFLECTION_CACHE_GET_MATCHING_SETTERS, () -> {
            return Cache.key(type, name, Boolean.valueOf(makeAccessible));
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Method getMatchingGetter(Configuration configuration, Class<?> type, String name, boolean makeAccessible) {
        return (Method) Cache.run(configuration, () -> {
            String camelCase = StringUtils.toCamelCase(name);
            String camelCaseLC = StringUtils.toLC(camelCase);
            for (Method method : getInstanceMethods(type)) {
                if (method.getParameterTypes().length == 0) {
                    if (name.equals(method.getName())) {
                        return (Method) accessible(method, makeAccessible);
                    }
                    if (camelCaseLC.equals(method.getName())) {
                        return (Method) accessible(method, makeAccessible);
                    }
                    if (("get" + name).equals(method.getName())) {
                        return (Method) accessible(method, makeAccessible);
                    }
                    if (("get" + camelCase).equals(method.getName())) {
                        return (Method) accessible(method, makeAccessible);
                    }
                    if (("is" + name).equals(method.getName())) {
                        return (Method) accessible(method, makeAccessible);
                    }
                    if (("is" + camelCase).equals(method.getName())) {
                        return (Method) accessible(method, makeAccessible);
                    }
                }
            }
            return null;
        }, CacheType.REFLECTION_CACHE_GET_MATCHING_GETTER, () -> {
            return Cache.key(type, name, Boolean.valueOf(makeAccessible));
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Tools$SourceMethod.class */
    public static final class SourceMethod {
        final Method method;

        SourceMethod(Method method) {
            this.method = method;
        }

        static List<Method> methods(Collection<? extends SourceMethod> methods) {
            return Tools.map(methods, s -> {
                return s.method;
            });
        }

        public int hashCode() {
            int result = (31 * 1) + (this.method == null ? 0 : this.method.getName().hashCode());
            return result;
        }

        public boolean equals(Object obj) {
            if (obj instanceof SourceMethod) {
                SourceMethod s = (SourceMethod) obj;
                Method other = s.method;
                if (this.method.getName().equals(other.getName())) {
                    Class<?>[] p1 = this.method.getParameterTypes();
                    Class<?>[] p2 = other.getParameterTypes();
                    return Arrays.equals(p1, p2);
                }
                return false;
            }
            return false;
        }

        public String toString() {
            return this.method.toString();
        }
    }

    private static final Method getInstanceMethod(Class<?> type, String name, Class<?>[] parameters) throws NoSuchMethodException {
        try {
            return type.getMethod(name, parameters);
        } catch (NoSuchMethodException e) {
            do {
                try {
                    return type.getDeclaredMethod(name, parameters);
                } catch (NoSuchMethodException e2) {
                    type = type.getSuperclass();
                    if (type != null) {
                        throw new NoSuchMethodException();
                    }
                }
            } while (type != null);
            throw new NoSuchMethodException();
        }
    }

    private static final Set<Method> getInstanceMethods(Class<?> type) {
        Class<? super Object> superclass;
        Set<Method> result = new LinkedHashSet<>();
        for (Method method : type.getMethods()) {
            if ((method.getModifiers() & 8) == 0) {
                result.add(method);
            }
        }
        do {
            for (Method method2 : type.getDeclaredMethods()) {
                if ((method2.getModifiers() & 8) == 0) {
                    result.add(method2);
                }
            }
            superclass = type.getSuperclass();
            type = superclass;
        } while (superclass != null);
        return result;
    }

    private static final List<java.lang.reflect.Field> getInstanceMembers(Class<?> type) {
        Class<? super Object> superclass;
        List<java.lang.reflect.Field> result = new ArrayList<>();
        for (java.lang.reflect.Field field : type.getFields()) {
            if ((field.getModifiers() & 8) == 0) {
                result.add(field);
            }
        }
        do {
            for (java.lang.reflect.Field field2 : type.getDeclaredFields()) {
                if ((field2.getModifiers() & 8) == 0) {
                    result.add(field2);
                }
            }
            superclass = type.getSuperclass();
            type = superclass;
        } while (superclass != null);
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final String getPropertyName(String methodName) {
        String name = methodName;
        if (name.startsWith(BeanUtil.PREFIX_GETTER_IS) && name.length() > 2) {
            name = name.substring(2, 3).toLowerCase() + name.substring(3);
        } else if (name.startsWith(BeanUtil.PREFIX_GETTER_GET) && name.length() > 3) {
            name = name.substring(3, 4).toLowerCase() + name.substring(4);
        } else if (name.startsWith("set") && name.length() > 3) {
            name = name.substring(3, 4).toLowerCase() + name.substring(4);
        }
        return name;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final void consumeExceptions(Configuration configuration, PreparedStatement stmt, SQLException previous) {
        ThrowExceptions exceptions = configuration.settings().getThrowExceptions();
        if (exceptions == ThrowExceptions.THROW_FIRST) {
        }
    }

    static final void consumeWarnings(ExecuteContext ctx, ExecuteListener listener) {
        if (!Boolean.FALSE.equals(ctx.settings().isFetchWarnings())) {
            try {
                ctx.sqlWarning(ctx.statement().getWarnings());
            } catch (SQLException e) {
                ctx.sqlWarning(new SQLWarning("Could not fetch SQLWarning", e));
            }
        }
        if (ctx.sqlWarning() != null) {
            listener.warning(ctx);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final SQLException executeStatementAndGetFirstResultSet(ExecuteContext ctx, int skipUpdateCounts) throws SQLException {
        boolean moreResults;
        PreparedStatement stmt = ctx.statement();
        try {
            if (skipUpdateCounts > 0) {
                for (int i = 0; i < maxConsumedResults; i++) {
                    if (i == 0) {
                        moreResults = stmt.execute();
                    } else {
                        moreResults = stmt.getMoreResults();
                    }
                    boolean result = moreResults;
                    if (result) {
                        ctx.resultSet(stmt.getResultSet());
                        return null;
                    }
                    int updateCount = stmt.getUpdateCount();
                    if (i == 0) {
                        ctx.resultSet(null);
                        ctx.rows(updateCount);
                    }
                    if (updateCount == -1) {
                        return null;
                    }
                    int i2 = skipUpdateCounts;
                    skipUpdateCounts--;
                    if (i2 == 0) {
                        return null;
                    }
                }
                return null;
            }
            if (stmt.execute()) {
                ctx.resultSet(stmt.getResultSet());
                return null;
            }
            ctx.resultSet(null);
            ctx.rows(stmt.getUpdateCount());
            return null;
        } catch (SQLException e) {
            if (ctx.settings().getThrowExceptions() != ThrowExceptions.THROW_NONE) {
                consumeExceptions(ctx.configuration(), ctx.statement(), e);
                throw e;
            }
            return e;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final void consumeResultSets(ExecuteContext ctx, ExecuteListener listener, Results results, Intern intern, SQLException prev) throws SQLException {
        boolean anyResults = false;
        int rows = ctx.resultSet() == null ? ctx.rows() : 0;
        int i = 0;
        while (i < maxConsumedResults) {
            try {
                if (ctx.resultSet() != null) {
                    anyResults = true;
                    Field<?>[] fields = new MetaDataFieldProvider(ctx.configuration(), ctx.resultSet().getMetaData()).getFields();
                    Cursor<Record> c = new CursorImpl<>(ctx, listener, fields, intern != null ? intern.internIndexes(fields) : null, true, false);
                    results.resultsOrRows().add(new ResultsImpl.ResultOrRowsImpl(c.fetch()));
                } else if (prev == null) {
                    if (rows == -1) {
                        break;
                    } else {
                        results.resultsOrRows().add(new ResultsImpl.ResultOrRowsImpl(rows));
                    }
                }
                if (ctx.statement().getMoreResults()) {
                    ctx.resultSet(ctx.statement().getResultSet());
                } else {
                    rows = ctx.statement().getUpdateCount();
                    ctx.rows(rows);
                    if (rows == -1) {
                        break;
                    } else {
                        ctx.resultSet(null);
                    }
                }
                prev = null;
            } catch (SQLException e) {
                prev = e;
                if (ctx.settings().getThrowExceptions() == ThrowExceptions.THROW_NONE) {
                    ctx.sqlException(e);
                    results.resultsOrRows().add(new ResultsImpl.ResultOrRowsImpl(translate(ctx.sql(), e)));
                } else {
                    consumeExceptions(ctx.configuration(), ctx.statement(), e);
                    throw e;
                }
            }
            i++;
        }
        if (i == maxConsumedResults) {
            log.warn("Maximum consumed results reached: " + maxConsumedResults + ". This is probably a bug. Please report to https://jooq.org/bug");
        }
        if (anyResults) {
            ctx.statement().getMoreResults(3);
        }
        if (ctx.settings().getThrowExceptions() == ThrowExceptions.THROW_NONE) {
            SQLException s1 = null;
            for (ResultOrRows r : results.resultsOrRows()) {
                DataAccessException d = r.exception();
                if (d != null && (d.getCause() instanceof SQLException)) {
                    SQLException s2 = (SQLException) d.getCause();
                    if (s1 != null) {
                        s1.setNextException(s2);
                    }
                    s1 = s2;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final List<String[]> parseTXT(String string, String nullLiteral) {
        String[] strings = NEW_LINES.split(string);
        if (strings.length < 2) {
            throw new DataAccessException("String must contain at least two lines");
        }
        boolean formattedJOOQ = string.charAt(0) == '+';
        boolean formattedOracle = string.charAt(0) == '-';
        if (formattedJOOQ) {
            return parseTXTLines(nullLiteral, strings, PLUS_PATTERN, 0, 1, 3, strings.length - 1);
        }
        if (formattedOracle) {
            return parseTXTLines(nullLiteral, strings, PIPE_PATTERN, 1, 1, 3, strings.length - 1);
        }
        return parseTXTLines(nullLiteral, strings, DASH_PATTERN, 1, 0, 2, strings.length);
    }

    private static final List<String[]> parseTXTLines(String nullLiteral, String[] strings, Pattern pattern, int matchLine, int headerLine, int dataLineStart, int dataLineEnd) {
        List<int[]> positions = new ArrayList<>();
        Matcher m = pattern.matcher(strings[matchLine]);
        while (m.find()) {
            positions.add(new int[]{m.start(1), m.end(1)});
        }
        List<String[]> result = new ArrayList<>();
        parseTXTLine(positions, result, strings[headerLine], nullLiteral);
        for (int j = dataLineStart; j < dataLineEnd; j++) {
            parseTXTLine(positions, result, strings[j], nullLiteral);
        }
        return result;
    }

    private static final void parseTXTLine(List<int[]> positions, List<String[]> result, String string, String nullLiteral) {
        String[] fields = new String[positions.size()];
        result.add(fields);
        int length = string.length();
        for (int i = 0; i < fields.length; i++) {
            int[] position = positions.get(i);
            if (position[0] < length) {
                fields[i] = string.substring(position[0], Math.min(position[1], length)).trim();
            } else {
                fields[i] = null;
            }
            if (StringUtils.equals(fields[i], nullLiteral)) {
                fields[i] = null;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final List<String[]> parseHTML(String string) {
        List<String[]> result = new ArrayList<>();
        Matcher mRow = P_PARSE_HTML_ROW.matcher(string);
        while (mRow.find()) {
            String row = mRow.group(1);
            List<String> col = new ArrayList<>();
            if (result.isEmpty()) {
                Matcher mColHead = P_PARSE_HTML_COL_HEAD.matcher(row);
                while (mColHead.find()) {
                    col.add(mColHead.group(1));
                }
            }
            if (col.isEmpty()) {
                Matcher mColBody = P_PARSE_HTML_COL_BODY.matcher(row);
                while (mColBody.find()) {
                    col.add(mColBody.group(1));
                }
                if (result.isEmpty()) {
                    result.add(fieldNameStrings(col.size()));
                }
            }
            result.add((String[]) col.toArray(EMPTY_STRING));
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final void begin(Context<?> ctx, Consumer<? super Context<?>> runnable) {
        begin(ctx);
        runnable.accept(ctx);
        end(ctx);
    }

    /* JADX WARN: Type inference failed for: r0v10, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v16, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v24, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v6, types: [org.jooq.Context] */
    private static final void begin(Context<?> ctx) {
        switch (ctx.family()) {
            case FIREBIRD:
                ctx.visit(Keywords.K_EXECUTE_BLOCK).formatSeparator().visit(Keywords.K_AS).formatSeparator().visit(Keywords.K_BEGIN).formatIndentStart().formatSeparator();
                return;
            case MARIADB:
                ctx.visit(Keywords.K_BEGIN).sql(' ').visit(Keywords.K_NOT).sql(' ').visit(Keywords.K_ATOMIC).formatIndentStart().formatSeparator();
                return;
            case POSTGRES:
            case YUGABYTEDB:
                if (increment(ctx.data(), SimpleDataKey.DATA_BLOCK_NESTING)) {
                    ctx.visit(Keywords.K_DO).sql(" $").sql(ctx.settings().getRenderDollarQuotedStringToken()).sql('$').formatSeparator();
                }
                ctx.visit(Keywords.K_BEGIN).formatIndentStart().formatSeparator();
                return;
            default:
                return;
        }
    }

    /* JADX WARN: Type inference failed for: r0v10, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v14, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v3, types: [org.jooq.Context] */
    private static final void end(Context<?> ctx) {
        switch (ctx.family()) {
            case FIREBIRD:
            case MARIADB:
                ctx.formatIndentEnd().formatSeparator().visit(Keywords.K_END);
                return;
            case POSTGRES:
            case YUGABYTEDB:
                ctx.formatIndentEnd().formatSeparator().visit(Keywords.K_END);
                if (decrement(ctx.data(), SimpleDataKey.DATA_BLOCK_NESTING)) {
                    ctx.sql(" $").sql(ctx.settings().getRenderDollarQuotedStringToken()).sql('$');
                    return;
                }
                return;
            default:
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final void executeImmediateIf(boolean wrap, Context<?> ctx, Consumer<? super Context<?>> runnable) {
        if (wrap) {
            executeImmediate(ctx, runnable);
        } else {
            runnable.accept(ctx);
            ctx.sql(';');
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final void executeImmediate(Context<?> ctx, Consumer<? super Context<?>> runnable) {
        beginExecuteImmediate(ctx);
        runnable.accept(ctx);
        endExecuteImmediate(ctx);
    }

    /* JADX WARN: Type inference failed for: r0v3, types: [org.jooq.Context] */
    static final void beginExecuteImmediate(Context<?> ctx) {
        switch (ctx.family()) {
            case FIREBIRD:
                ctx.visit(Keywords.K_EXECUTE_STATEMENT).sql(" '").stringLiteral(true).formatIndentStart().formatSeparator();
                return;
            default:
                return;
        }
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    static final void endExecuteImmediate(Context<?> ctx) {
        ctx.formatIndentEnd().formatSeparator().stringLiteral(false).sql("';");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final void tryCatch(Context<?> ctx, DDLStatementType type, Consumer<? super Context<?>> runnable) {
        tryCatch(ctx, type, null, null, runnable);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final void tryCatch(Context<?> ctx, DDLStatementType type, Boolean container, Boolean element, Consumer<? super Context<?>> runnable) {
        switch (ctx.family()) {
            case FIREBIRD:
                begin(ctx, c -> {
                    executeImmediate(c, runnable);
                    c.formatSeparator().visit(Keywords.K_WHEN).sql(" sqlcode -607 ").visit(Keywords.K_DO).formatIndentStart().formatSeparator().visit(Keywords.K_BEGIN).sql(' ').visit(Keywords.K_END).formatIndentEnd();
                });
                return;
            case MARIADB:
            case MYSQL:
                tryCatchMySQL(ctx, type, runnable);
                return;
            case POSTGRES:
            case YUGABYTEDB:
                begin(ctx, c2 -> {
                    String sqlstate;
                    switch (type) {
                        case ALTER_DATABASE:
                            sqlstate = "3D000";
                            break;
                        case ALTER_DOMAIN:
                        case ALTER_TABLE:
                        case ALTER_TYPE:
                            sqlstate = "42704";
                            break;
                        case CREATE_DOMAIN:
                        case CREATE_TYPE:
                            sqlstate = "42710";
                            break;
                        default:
                            sqlstate = "42P07";
                            break;
                    }
                    runnable.accept(c2);
                    c2.sql(';').formatIndentEnd().formatSeparator().visit(Keywords.K_EXCEPTION).formatIndentStart().formatSeparator().visit(Keywords.K_WHEN).sql(' ').visit(Keywords.K_SQLSTATE).sql(' ').visit((Field<?>) DSL.inline(sqlstate)).sql(' ').visit(Keywords.K_THEN).sql(' ').visit(Keywords.K_NULL).sql(';').formatIndentEnd();
                });
                return;
            default:
                runnable.accept(ctx);
                return;
        }
    }

    private static final void tryCatchMySQL(Context<?> ctx, DDLStatementType type, Consumer<? super Context<?>> runnable) {
        List<String> sqlstates = new ArrayList<>();
        switch (ctx.family()) {
            case MARIADB:
                switch (type) {
                    case ALTER_INDEX:
                    case CREATE_INDEX:
                    case DROP_INDEX:
                        sqlstates.add("42000");
                        break;
                }
                sqlstates.add("42S02");
                break;
            case MYSQL:
                switch (type) {
                    case ALTER_INDEX:
                    case CREATE_INDEX:
                    case DROP_INDEX:
                        sqlstates.add("42000");
                        break;
                    case ALTER_VIEW:
                    case CREATE_VIEW:
                    case DROP_VIEW:
                        sqlstates.add("42S01");
                        break;
                }
        }
        begin(ctx, c -> {
            Iterator it = sqlstates.iterator();
            while (it.hasNext()) {
                String sqlstate = (String) it.next();
                c.visit(DSL.keyword("declare continue handler for sqlstate")).sql(' ').visit((Field<?>) DSL.inline(sqlstate)).sql(' ').visit(Keywords.K_BEGIN).sql(' ').visit(Keywords.K_END).sql(';').formatSeparator();
            }
            runnable.accept(c);
            c.sql(';');
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final void toSQLDDLTypeDeclarationForAddition(Context<?> ctx, DataType<?> type) {
        boolean qualify = ctx.qualify();
        toSQLDDLTypeDeclaration(ctx, type);
        ctx.qualify(false);
        toSQLDDLTypeDeclarationIdentityBeforeNull(ctx, type);
        if (DEFAULT_BEFORE_NULL.contains(ctx.dialect())) {
            toSQLDDLTypeDeclarationDefault(ctx, type);
        }
        toSQLDDLTypeDeclarationForAdditionNullability(ctx, type);
        if (!DEFAULT_BEFORE_NULL.contains(ctx.dialect())) {
            toSQLDDLTypeDeclarationDefault(ctx, type);
        }
        toSQLDDLTypeDeclarationIdentityAfterNull(ctx, type);
        ctx.qualify(qualify);
    }

    /* JADX WARN: Type inference failed for: r0v16, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v21, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v26, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v31, types: [org.jooq.Context] */
    private static final void toSQLDDLTypeDeclarationForAdditionNullability(Context<?> ctx, DataType<?> type) {
        switch (type.nullability()) {
            case NOT_NULL:
                if (!NO_SUPPORT_NOT_NULL.contains(ctx.dialect())) {
                    ctx.sql(' ').visit(Keywords.K_NOT_NULL);
                    return;
                }
                return;
            case NULL:
                if (!NO_SUPPORT_NULL.contains(ctx.dialect())) {
                    ctx.sql(' ').visit(Keywords.K_NULL);
                    return;
                }
                return;
            case DEFAULT:
                RenderDefaultNullability nullability = (RenderDefaultNullability) StringUtils.defaultIfNull(ctx.settings().getRenderDefaultNullability(), RenderDefaultNullability.IMPLICIT_NULL);
                switch (nullability) {
                    case EXPLICIT_NULL:
                        if (!NO_SUPPORT_NULL.contains(ctx.dialect())) {
                            ctx.sql(' ').visit(Keywords.K_NULL);
                            return;
                        }
                        return;
                    case IMPLICIT_DEFAULT:
                        return;
                    case IMPLICIT_NULL:
                        if (DEFAULT_TIMESTAMP_NOT_NULL.contains(ctx.dialect()) && type.isTimestamp()) {
                            ctx.sql(' ').visit(Keywords.K_NULL);
                            return;
                        }
                        return;
                    default:
                        throw new UnsupportedOperationException("Nullability not supported: " + String.valueOf(nullability));
                }
            default:
                throw new UnsupportedOperationException("Nullability not supported: " + String.valueOf(type.nullability()));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Type inference failed for: r0v20, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v31, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v36, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v50, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v7, types: [org.jooq.Context] */
    public static final void toSQLDDLTypeDeclarationIdentityBeforeNull(Context<?> ctx, DataType<?> type) {
        if (!REQUIRE_IDENTITY_AFTER_NULL.contains(ctx.dialect()) && type.identity()) {
            switch (ctx.family()) {
                case FIREBIRD:
                case YUGABYTEDB:
                case DERBY:
                    ctx.sql(' ').visit(Keywords.K_GENERATED).sql(' ').visit(Keywords.K_BY).sql(' ').visit(Keywords.K_DEFAULT).sql(' ').visit(Keywords.K_AS).sql(' ').visit(Keywords.K_IDENTITY);
                    return;
                case MARIADB:
                case MYSQL:
                default:
                    return;
                case POSTGRES:
                    if (SUPPORT_PG_IDENTITY.contains(ctx.dialect())) {
                        ctx.sql(' ').visit(Keywords.K_GENERATED).sql(' ').visit(Keywords.K_BY).sql(' ').visit(Keywords.K_DEFAULT).sql(' ').visit(Keywords.K_AS).sql(' ').visit(Keywords.K_IDENTITY);
                        return;
                    }
                    return;
                case CUBRID:
                    ctx.sql(' ').visit(Keywords.K_AUTO_INCREMENT);
                    return;
                case HSQLDB:
                    ctx.sql(' ').visit(Keywords.K_GENERATED).sql(' ').visit(Keywords.K_BY).sql(' ').visit(Keywords.K_DEFAULT).sql(' ').visit(Keywords.K_AS).sql(' ').visit(Keywords.K_IDENTITY).sql('(').visit(Keywords.K_START_WITH).sql(" 1)");
                    return;
                case SQLITE:
                    ctx.sql(' ').visit(Keywords.K_PRIMARY_KEY).sql(' ').visit(Keywords.K_AUTOINCREMENT);
                    return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Type inference failed for: r0v10, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v7, types: [org.jooq.Context] */
    public static final void toSQLDDLTypeDeclarationIdentityAfterNull(Context<?> ctx, DataType<?> type) {
        if (REQUIRE_IDENTITY_AFTER_NULL.contains(ctx.dialect()) && type.identity()) {
            switch (ctx.family()) {
                case MARIADB:
                case MYSQL:
                    ctx.sql(' ').visit(Keywords.K_AUTO_INCREMENT);
                    return;
                case H2:
                    ctx.sql(' ').visit(Keywords.K_GENERATED).sql(' ').visit(Keywords.K_BY).sql(' ').visit(Keywords.K_DEFAULT).sql(' ').visit(Keywords.K_AS).sql(' ').visit(Keywords.K_IDENTITY);
                    return;
                default:
                    return;
            }
        }
    }

    /* JADX WARN: Type inference failed for: r0v13, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v5, types: [org.jooq.Context] */
    private static final void toSQLDDLTypeDeclarationDefault(Context<?> ctx, DataType<?> type) {
        if (type.defaulted()) {
            Field<?> v = type.defaultValue();
            ctx.sql(' ').visit(Keywords.K_DEFAULT).sql(' ');
            if (REQUIRES_PARENTHESISED_DEFAULT.contains(ctx.dialect())) {
                ctx.sql('(').visit(v).sql(')');
            } else {
                ctx.visit(v);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final void toSQLDDLTypeDeclaration(Context<?> ctx, DataType<?> type) {
        toSQLDDLTypeDeclaration0(ctx, type);
    }

    /* JADX WARN: Type inference failed for: r0v127, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v141, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v35, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v51, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v59, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v94, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v99, types: [org.jooq.Context] */
    static final void toSQLDDLTypeDeclaration0(Context<?> ctx, DataType<?> type) {
        DataType<?> elementType = type.getArrayBaseDataType();
        if (type.identity()) {
            int i = AnonymousClass6.$SwitchMap$org$jooq$SQLDialect[ctx.family().ordinal()];
        }
        if (EnumType.class.isAssignableFrom(type.getType())) {
            switch (ctx.family()) {
                case MARIADB:
                case MYSQL:
                case H2:
                    ctx.visit(Keywords.K_ENUM).sql('(');
                    String separator = "";
                    for (EnumType e : enumConstants(type)) {
                        ctx.sql(separator).visit(DSL.inline(e.getLiteral()));
                        separator = ", ";
                    }
                    ctx.sql(')');
                    return;
                case POSTGRES:
                case YUGABYTEDB:
                    if (!storedEnumType(type)) {
                        type = emulateEnumType(type);
                        break;
                    }
                    break;
                case CUBRID:
                case HSQLDB:
                case SQLITE:
                case DERBY:
                default:
                    type = emulateEnumType(type);
                    break;
            }
        }
        if (type.getType() == UUID.class && NO_SUPPORT_CAST_TYPE_IN_DDL.contains(ctx.dialect())) {
            toSQLDDLTypeDeclaration(ctx, SQLDataType.VARCHAR(36));
            return;
        }
        if (type.isTimestamp() && ((type.getBinding() instanceof DateAsTimestampBinding) || (type.getBinding() instanceof LocalDateAsLocalDateTimeBinding))) {
            type = SQLDataType.DATE;
        }
        String typeName = type.getTypeName(ctx.configuration());
        if (type.hasLength() || elementType.hasLength()) {
            if (type.isBinary() && NO_SUPPORT_BINARY_TYPE_LENGTH.contains(ctx.dialect())) {
                ctx.sql(typeName);
            } else if (type.length() > 0) {
                ctx.sql(typeName).sql('(').sql(type.length()).sql(')');
            } else if (NO_SUPPORT_CAST_TYPE_IN_DDL.contains(ctx.dialect())) {
                if (type.isBinary()) {
                    ctx.sql(SQLDataType.BLOB.getTypeName(ctx.configuration()));
                } else {
                    ctx.sql(SQLDataType.CLOB.getTypeName(ctx.configuration()));
                }
            } else {
                String castTypeName = type.getCastTypeName(ctx.configuration());
                if (!typeName.equals(castTypeName)) {
                    ctx.sql(castTypeName);
                } else {
                    ctx.sql(typeName);
                }
            }
        } else if (type.hasPrecision() && type.precision() > 0 && (!type.isTimestamp() || !NO_SUPPORT_TIMESTAMP_PRECISION.contains(ctx.dialect()))) {
            if (NO_SUPPORT_CAST_TYPE_IN_DDL.contains(ctx.dialect())) {
                if (type.hasScale()) {
                    ctx.sql(typeName).sql('(').sql(type.precision()).sql(", ").sql(type.scale()).sql(')');
                } else {
                    ctx.sql(typeName).sql('(').sql(type.precision()).sql(')');
                }
            } else {
                ctx.sql(type.getCastTypeName(ctx.configuration()));
            }
        } else if (type.identity() && ctx.family() == SQLDialect.SQLITE && type.isNumeric()) {
            ctx.sql("integer");
        } else if (type.isOther() && !(type instanceof BuiltInDataType)) {
            ctx.visit(type.getQualifiedName());
        } else {
            ctx.sql(typeName);
        }
        if (type.characterSet() != null && ctx.configuration().data("org.jooq.ddl.ignore-storage-clauses") == null) {
            ctx.sql(' ').visit(Keywords.K_CHARACTER_SET).sql(' ').visit(type.characterSet());
        }
        if (type.collation() != null && ctx.configuration().data("org.jooq.ddl.ignore-storage-clauses") == null) {
            ctx.sql(' ').visit(Keywords.K_COLLATE).sql(' ').visit(type.collation());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final boolean storedEnumType(DataType<EnumType> enumType) {
        return enumConstants(enumType)[0].getName() != null;
    }

    private static final EnumType[] enumConstants(DataType<? extends EnumType> type) {
        EnumType[] enums = (EnumType[]) type.getType().getEnumConstants();
        if (enums == null) {
            throw new DataTypeException("EnumType must be a Java enum");
        }
        return enums;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final DataType<String> emulateEnumType(DataType<? extends EnumType> type) {
        return emulateEnumType(type, enumConstants(type));
    }

    /* JADX WARN: Multi-variable type inference failed */
    private static final DataType<String> emulateEnumType(DataType<? extends EnumType> type, EnumType[] enums) {
        int length = 0;
        for (EnumType e : enums) {
            length = Math.max(length, e.getLiteral().length());
        }
        return SQLDataType.VARCHAR(length).nullability(type.nullability()).defaultValue((Field<String>) type.defaultValue());
    }

    static final <C extends Context<? extends C>> C prependInline(C c, String str, Field<?> field, String str2) {
        if (field instanceof Param) {
            return (C) c.visit(DSL.inline(str + String.valueOf(((Param) field).getValue()) + str2));
        }
        return (C) c.visit(DSL.inline(str).concat(field).concat(DSL.inline(str2)), ParamType.INLINED);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <C extends Context<? extends C>> C prependSQL(C c, Query... queryArr) {
        return (C) preOrAppendSQL(SimpleDataKey.DATA_PREPEND_SQL, c, queryArr);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <C extends Context<? extends C>> C appendSQL(C c, Query... queryArr) {
        return (C) preOrAppendSQL(SimpleDataKey.DATA_APPEND_SQL, c, queryArr);
    }

    private static final <C extends Context<? extends C>> C preOrAppendSQL(SimpleDataKey key, C ctx, Query... queries) {
        ctx.data().compute(key, (k, v) -> {
            String sql = ctx.dsl().renderInlined(ctx.dsl().queries(queries));
            if (v == null) {
                return sql;
            }
            return String.valueOf(v) + sql;
        });
        return ctx;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T> Supplier<T> blocking(Supplier<T> supplier) {
        return blocking(supplier, false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX INFO: Add missing generic type declarations: [T] */
    /* renamed from: org.jooq.impl.Tools$3, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Tools$3.class */
    public class AnonymousClass3<T> implements Supplier<T> {
        volatile T asyncResult;
        final /* synthetic */ Supplier val$supplier;

        AnonymousClass3(Supplier supplier) {
            this.val$supplier = supplier;
        }

        @Override // java.util.function.Supplier
        public T get() {
            try {
                ForkJoinPool.managedBlock(new ForkJoinPool.ManagedBlocker() { // from class: org.jooq.impl.Tools.3.1
                    @Override // java.util.concurrent.ForkJoinPool.ManagedBlocker
                    public boolean block() {
                        AnonymousClass3.this.asyncResult = (T) AnonymousClass3.this.val$supplier.get();
                        return true;
                    }

                    @Override // java.util.concurrent.ForkJoinPool.ManagedBlocker
                    public boolean isReleasable() {
                        return AnonymousClass3.this.asyncResult != null;
                    }
                });
                return this.asyncResult;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T> Supplier<T> blocking(Supplier<T> supplier, boolean threadLocal) {
        return threadLocal ? supplier : new AnonymousClass3(supplier);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <E extends EnumType> E[] enums(Class<? extends E> cls) {
        if (Enum.class.isAssignableFrom(cls)) {
            return (E[]) ((EnumType[]) cls.getEnumConstants());
        }
        try {
            Class<?> loadClass = Thread.currentThread().getContextClassLoader().loadClass(cls.getName() + "$");
            return (E[]) ((EnumType[]) loadClass.getMethod("values", new Class[0]).invoke(loadClass.getField("MODULE$").get(loadClass), new Object[0]));
        } catch (Exception e) {
            throw new MappingException("Error while looking up Scala enum", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final boolean isTime(Class<?> t) {
        return t == Time.class || t == LocalTime.class;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final boolean isTimestamp(Class<?> t) {
        return t == Timestamp.class || t == LocalDateTime.class;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final boolean isDate(Class<?> t) {
        return t == Date.class || t == LocalDate.class;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final boolean hasAmbiguousNames(Collection<? extends Field<?>> fields) {
        if (fields == null) {
            return false;
        }
        Set<String> names = new HashSet<>();
        return anyMatch(fields, f -> {
            return !names.add(f.getName());
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final SelectFieldOrAsterisk qualify(Table<?> table, SelectFieldOrAsterisk field) {
        if (field instanceof Field) {
            Field<?> f = (Field) field;
            return qualify(table, (Field) f);
        }
        if (field instanceof Asterisk) {
            return table.asterisk();
        }
        if (field instanceof QualifiedAsterisk) {
            return table.asterisk();
        }
        throw new UnsupportedOperationException("Unsupported field : " + String.valueOf(field));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Field<?>[] qualify(Table<?> table, Field<?>[] fields) {
        return (Field[]) map(fields, f -> {
            return qualify((Table<?>) table, f);
        }, x$0 -> {
            return new Field[x$0];
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T> Field<T> qualify(Table<?> table, Field<T> field) {
        Field<T> result = table.field(field);
        if (result != null) {
            return result;
        }
        Name[] part = table.getQualifiedName().parts();
        Name[] name = new Name[part.length + 1];
        System.arraycopy(part, 0, name, 0, part.length);
        name[part.length] = field.getUnqualifiedName();
        return DSL.field(DSL.name(name), field.getDataType());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T> Field<T> field(OrderField<T> orderField) {
        if (orderField instanceof Field) {
            Field<T> f = (Field) orderField;
            return f;
        }
        return ((SortField) orderField).$field();
    }

    static final Field<?>[] fields(OrderField<?>[] orderFields) {
        return (Field[]) map(orderFields, f -> {
            return field(f);
        }, x$0 -> {
            return new Field[x$0];
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final List<Field<?>> fields(Collection<? extends OrderField<?>> orderFields) {
        return map(orderFields, f -> {
            return field(f);
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T> Field<T> unalias(Field<T> field) {
        Field<T> result = aliased(field);
        return result != null ? result : field;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T> Field<T> unaliasTable(Field<T> field) {
        Table aliased;
        if ((field instanceof TableField) && (aliased = aliased(((TableField) field).getTable())) != null) {
            return (Field<T>) aliased.field(field.getName());
        }
        return field;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T> Field<T> aliased(Field<T> field) {
        if (field instanceof FieldAlias) {
            FieldAlias<T> f = (FieldAlias) field;
            return f.getAliasedField();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <R extends Record> Table<R> unalias(Table<R> table) {
        Table<R> result = aliased(table);
        return result != null ? result : table;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final TableElement uncollate(TableElement field) {
        if (field instanceof QOM.Collated) {
            QOM.Collated c = (QOM.Collated) field;
            return uncollate(c.$field());
        }
        return field;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final boolean isScalarSubquery(Field<?> field) {
        return uncoerce(field) instanceof ScalarSubquery;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Field<?> uncoerce(Field<?> field) {
        if (!(field instanceof Coerce)) {
            return field;
        }
        Coerce<?> f = (Coerce) field;
        return f.field;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <R extends Record> Table<R> unwrap(Table<R> table) {
        return unwrap(table, true);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <R extends Record> Table<R> unwrap(Table<R> table, boolean unalias) {
        Table<R> r = table;
        if (table instanceof AbstractDelegatingTable) {
            AbstractDelegatingTable<R> t = (AbstractDelegatingTable) table;
            return unwrap(t.delegate);
        }
        if (unalias) {
            Table<R> unalias2 = unalias(table);
            r = unalias2;
            if (unalias2 != table) {
                return unwrap(r);
            }
        }
        return r;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <R extends Record> Table<R> aliased(Table<R> table) {
        if (table instanceof TableImpl) {
            TableImpl<R> t = (TableImpl) table;
            return t.getAliasedTable();
        }
        if (table instanceof TableAlias) {
            TableAlias<R> t2 = (TableAlias) table;
            return t2.getAliasedTable();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <R extends Record> Alias<Table<R>> alias(Table<R> table) {
        if (table instanceof TableImpl) {
            TableImpl<R> t = (TableImpl) table;
            return t.alias;
        }
        if (table instanceof TableAlias) {
            TableAlias<R> t2 = (TableAlias) table;
            return t2.alias;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final boolean toplevel(Map<Object, Object> data, SimpleDataKey key) {
        Integer updateCounts = (Integer) data.get(key);
        if (updateCounts == null) {
            throw new IllegalStateException();
        }
        return updateCounts.intValue() == 1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final void increment(Map<Object, Object> data, SimpleDataKey key, Runnable runnable) {
        increment(data, key);
        runnable.run();
        decrement(data, key);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final boolean increment(Map<Object, Object> data, SimpleDataKey key) {
        boolean result = true;
        Integer updateCounts = (Integer) data.get(key);
        if (updateCounts == null) {
            updateCounts = 0;
        } else {
            result = false;
        }
        data.put(key, Integer.valueOf(updateCounts.intValue() + 1));
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final boolean decrement(Map<Object, Object> data, SimpleDataKey key) {
        boolean result = false;
        Integer updateCounts = (Integer) data.get(key);
        if (updateCounts == null || updateCounts.intValue() == 0) {
            throw new IllegalStateException("Unmatching increment / decrement on key: " + String.valueOf(key));
        }
        if (updateCounts.intValue() == 1) {
            result = true;
        }
        data.put(key, Integer.valueOf(updateCounts.intValue() - 1));
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Field<?> tableField(Table<?> table, Object field) {
        if (field instanceof Field) {
            Field<?> f = (Field) field;
            return f;
        }
        if (field instanceof Name) {
            Name n = (Name) field;
            if (table.fieldsRow().size() == 0) {
                return DSL.field(table.getQualifiedName().append(n.unqualifiedName()));
            }
            return table.field(n);
        }
        if (field instanceof String) {
            String s = (String) field;
            if (table.fieldsRow().size() == 0) {
                return DSL.field(table.getQualifiedName().append(s));
            }
            return table.field(s);
        }
        throw new IllegalArgumentException("Field type not supported: " + String.valueOf(field));
    }

    static final String convertBytesToHex(byte[] value, int offset, int len) {
        int len2 = Math.min(value.length - offset, len);
        char[] buff = new char[2 * len2];
        char[] hex = HEX_DIGITS;
        for (int i = 0; i < len2; i++) {
            int c = value[i + offset] & 255;
            buff[i + i] = hex[c >> 4];
            buff[i + i + 1] = hex[c & 15];
        }
        return new String(buff);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final String convertBytesToHex(byte[] value) {
        return convertBytesToHex(value, 0, value.length);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final byte[] convertHexToBytes(String value, int offset, int len) {
        int len2 = Math.min((value.length() / 2) - offset, len);
        byte[] buff = new byte[len2];
        byte[] lookup = HEX_LOOKUP;
        int max = lookup.length;
        for (int i = 0; i < len2; i++) {
            int pos = (i + offset) * 2;
            char c1 = value.charAt(pos);
            char c2 = value.charAt(pos + 1);
            byte v1 = c1 < max ? lookup[c1] : (byte) 0;
            byte v2 = c2 < max ? lookup[c2] : (byte) 0;
            buff[i] = (byte) ((v1 << 4) + v2);
        }
        return buff;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final byte[] convertHexToBytes(String value) {
        return convertHexToBytes(value, 0, value.length());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    static final boolean isNotEmpty(Iterable<?> it) {
        return !isEmpty(it);
    }

    static final boolean isEmpty(Iterable<?> it) {
        if (it == null) {
            return true;
        }
        if (it instanceof Collection) {
            Collection<?> c = (Collection) it;
            return isEmpty(c);
        }
        Iterator<?> i = it.iterator();
        return !i.hasNext();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final boolean isNotEmpty(Object[] array) {
        return !isEmpty(array);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final boolean nonReplacingEmbeddable(Field<?> field) {
        return (field instanceof EmbeddableTableField) && !((EmbeddableTableField) field).replacesFields;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Multi-variable type inference failed */
    public static final Class<? extends AbstractRecord> embeddedRecordType(Field<?> field) {
        if (field instanceof EmbeddableTableField) {
            EmbeddableTableField<?, ?> e = (EmbeddableTableField) field;
            return e.recordType;
        }
        if ((field instanceof Val) && (((Val) field).value instanceof EmbeddableRecord)) {
            return ((AbstractRecord) ((Val) field).value).getClass();
        }
        if (field.getDataType().isEmbeddable()) {
            return field.getType();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Field<?>[] embeddedFields(Field<?> field) {
        if (field instanceof EmbeddableTableField) {
            EmbeddableTableField<?, ?> e = (EmbeddableTableField) field;
            return e.fields;
        }
        if ((field instanceof Val) && (((Val) field).value instanceof EmbeddableRecord)) {
            return ((EmbeddableRecord) ((Val) field).value).valuesRow().fields();
        }
        if (field instanceof ScalarSubquery) {
            ScalarSubquery<?> s = (ScalarSubquery) field;
            return embeddedFields(s);
        }
        if (field.getDataType().isEmbeddable()) {
            return newInstance(field.getType()).valuesRow().fields();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Row embeddedFieldsRow(Row row) {
        if (hasEmbeddedFields(row.fields())) {
            return DSL.row((Collection<?>) map(flattenCollection(Arrays.asList(row.fields())), f -> {
                return f;
            }));
        }
        return row;
    }

    static final Field<?>[] embeddedFields(ScalarSubquery<?> field) {
        List<Field<?>> select = field.query.getSelect();
        List<Field<?>> result = collect(flattenCollection(select));
        Name tableName = DSL.name("t");
        Name[] fieldNames = fieldNames(result.size());
        TableLike<?> as = new AliasedSelect(field.query, true, true, false, fieldNames).as("t");
        for (int i = 0; i < result.size(); i++) {
            result.set(i, DSL.field(DSL.select(DSL.field(tableName.append(fieldNames[i]), result.get(i).getDataType())).from(as)));
        }
        return (Field[]) result.toArray(EMPTY_FIELD);
    }

    private static final EmbeddableRecord<?> newInstance(Class<? extends EmbeddableRecord<?>> type) {
        try {
            return type.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
        } catch (Exception e) {
            throw new MappingException("Cannot create EmbeddableRecord type", e);
        }
    }

    static final boolean hasEmbeddedFields(Field<?>[] fields) {
        return anyMatch(fields, f -> {
            return f.getDataType().isEmbeddable();
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final boolean hasEmbeddedFields(Iterable<? extends Field<?>> fields) {
        return anyMatch(fields, f -> {
            return f.getDataType().isEmbeddable();
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <E> List<E> collect(Iterable<E> iterable) {
        if (iterable instanceof List) {
            List<E> l = (List) iterable;
            return l;
        }
        List<E> result = new ArrayList<>();
        for (E e : iterable) {
            result.add(e);
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <E> Iterator<E> filter(Iterator<E> iterator, Predicate<? super E> predicate) {
        return filter(iterator, (e, i) -> {
            return predicate.test(e);
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <E> Iterator<E> filter(final Iterator<E> iterator, final ObjIntPredicate<? super E> predicate) {
        return new Iterator<E>() { // from class: org.jooq.impl.Tools.4
            boolean uptodate;
            E next;
            int index;

            private void move() {
                if (!this.uptodate) {
                    this.uptodate = true;
                    while (iterator.hasNext()) {
                        this.next = (E) iterator.next();
                        ObjIntPredicate objIntPredicate = predicate;
                        E e = this.next;
                        int i = this.index;
                        this.index = i + 1;
                        if (objIntPredicate.test(e, i)) {
                            return;
                        }
                    }
                    this.uptodate = false;
                }
            }

            @Override // java.util.Iterator
            public boolean hasNext() {
                move();
                return this.uptodate;
            }

            @Override // java.util.Iterator
            public E next() {
                move();
                if (!this.uptodate) {
                    throw new NoSuchElementException();
                }
                this.uptodate = false;
                return this.next;
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <E> Iterable<E> filter(Iterable<E> iterable, Predicate<? super E> predicate) {
        return () -> {
            return filter(iterable.iterator(), predicate);
        };
    }

    static final <E> Iterable<E> filter(Iterable<E> iterable, ObjIntPredicate<? super E> predicate) {
        return () -> {
            return filter(iterable.iterator(), predicate);
        };
    }

    static final <E> Iterable<E> filter(E[] array, Predicate<? super E> predicate) {
        return filter(Arrays.asList(array), predicate);
    }

    static final <E> Iterable<E> filter(E[] array, ObjIntPredicate<? super E> predicate) {
        return filter(Arrays.asList(array), predicate);
    }

    static final Iterable<Field<?>> flattenFieldOrRow(FieldOrRow fr) {
        if (fr instanceof Field) {
            Field<?> f = (Field) fr;
            return flatten(f);
        }
        return Arrays.asList(((Row) fr).fields());
    }

    static final <C extends Collection<Field<?>>> C flattenFieldOrRows(Collection<? extends FieldOrRow> frs, C c) {
        for (FieldOrRow fr : frs) {
            for (Field<?> f : flattenFieldOrRow(fr)) {
                c.add(f);
            }
        }
        return c;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <E extends Field<?>> Iterable<E> flatten(E field) {
        Iterator<E> it;
        Iterator<E> it1 = Collections.singletonList(field).iterator();
        if (field.getDataType().isEmbeddable()) {
            it = new FlatteningIterator<>(it1, (e, duplicates) -> {
                return Arrays.asList(embeddedFields((Field<?>) field));
            });
        } else {
            it = it1;
        }
        Iterator<E> it2 = it;
        return () -> {
            return it2;
        };
    }

    static final Iterable<Field<?>> flattenCollection(Iterable<? extends Field<?>> iterable) {
        return flattenCollection(iterable, false, false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Iterable<Field<?>> flattenCollection(Iterable<? extends Field<?>> iterable, boolean removeDuplicates, boolean flattenRowFields) {
        return () -> {
            return new FlatteningIterator(iterable.iterator(), (e, duplicates) -> {
                if (flattenRowFields && (e instanceof AbstractRowAsField)) {
                    AbstractRowAsField<?> r = (AbstractRowAsField) e;
                    List<Field<?>> result = new ArrayList<>();
                    for (Field<?> field : flattenCollection(Arrays.asList(r.fields0().fields()), removeDuplicates, flattenRowFields)) {
                        if (duplicates.test(field)) {
                            result.add(field);
                        }
                    }
                    return result;
                }
                return flatten(e);
            });
        };
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Iterable<Map.Entry<FieldOrRow, FieldOrRowOrSelect>> flattenEntrySet(Iterable<Map.Entry<FieldOrRow, FieldOrRowOrSelect>> iterable, boolean removeDuplicates) {
        return () -> {
            return new FlatteningIterator(iterable.iterator(), (e, duplicates) -> {
                Object patt0$temp = e.getKey();
                if (patt0$temp instanceof EmbeddableTableField) {
                    EmbeddableTableField<?, ?> key = (EmbeddableTableField) patt0$temp;
                    List<Map.Entry<FieldOrRow, FieldOrRowOrSelect>> result = new ArrayList<>();
                    Field<?>[] keys = embeddedFields(key);
                    Field<?>[] values = embeddedFields((Field<?>) e.getValue());
                    for (int i = 0; i < keys.length; i++) {
                        result.add(new AbstractMap.SimpleImmutableEntry<>(keys[i], values[i]));
                    }
                    return result;
                }
                return null;
            });
        };
    }

    static final <T> Set<T> lazy(Set<T> set) {
        return set == null ? new HashSet() : set;
    }

    static final <T> List<T> lazy(List<T> list) {
        return list == null ? new ArrayList() : list;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T> List<T> lazy(List<T> list, int size) {
        List<T> result = lazy(list);
        if (result.size() < size) {
            result.addAll(Collections.nCopies(size - result.size(), null));
        }
        return result;
    }

    static final <T> Supplier<T> cached(final Supplier<T> s) {
        return new Supplier<T>() { // from class: org.jooq.impl.Tools.5
            transient T cached;

            @Override // java.util.function.Supplier
            public T get() {
                if (this.cached == null) {
                    this.cached = (T) s.get();
                }
                return this.cached;
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Tools$FlatteningIterator.class */
    public static final class FlatteningIterator<E> implements Iterator<E> {
        private final Iterator<? extends E> delegate;
        private final BiFunction<? super E, Predicate<Object>, ? extends Iterable<E>> flattener;
        private final Predicate<Object> checkDuplicates = e -> {
            Set<Object> lazy = Tools.lazy(this.duplicates);
            this.duplicates = lazy;
            return lazy.add(e);
        };
        private Iterator<E> flatten;
        private E next;
        private Set<Object> duplicates;

        FlatteningIterator(Iterator<? extends E> delegate, BiFunction<? super E, Predicate<Object>, ? extends Iterable<E>> flattener) {
            this.delegate = delegate;
            this.flattener = flattener;
        }

        private final void move() {
            if (this.next == null) {
                if (this.flatten != null) {
                    if (this.flatten.hasNext()) {
                        this.next = this.flatten.next();
                        return;
                    }
                    this.flatten = null;
                }
                if (this.delegate.hasNext()) {
                    this.next = this.delegate.next();
                    Iterable<E> apply = this.flattener.apply(this.next, this.checkDuplicates);
                    if (apply == null) {
                        return;
                    }
                    this.next = null;
                    this.flatten = apply.iterator();
                    move();
                }
            }
        }

        @Override // java.util.Iterator
        public final boolean hasNext() {
            move();
            return this.next != null;
        }

        @Override // java.util.Iterator
        public final E next() {
            move();
            if (this.next == null) {
                throw new NoSuchElementException();
            }
            E result = this.next;
            this.next = null;
            return result;
        }

        @Override // java.util.Iterator
        public final void remove() {
            throw new UnsupportedOperationException("remove");
        }
    }

    static final boolean anyQuoted(Settings settings, Name... names) {
        RenderQuotedNames renderQuotedNames = SettingsTools.getRenderQuotedNames(settings);
        switch (renderQuotedNames) {
            case ALWAYS:
                return true;
            case NEVER:
                return false;
            case EXPLICIT_DEFAULT_QUOTED:
            case EXPLICIT_DEFAULT_UNQUOTED:
                for (Name name : names) {
                    name.unqualifiedName();
                    switch (n.quoted()) {
                        case QUOTED:
                            return true;
                        case DEFAULT:
                            if (renderQuotedNames == RenderQuotedNames.EXPLICIT_DEFAULT_QUOTED) {
                                return true;
                            }
                            break;
                    }
                }
                return false;
            default:
                return false;
        }
    }

    static final String asString(Name name) {
        if (!name.qualified()) {
            return name.first();
        }
        StringBuilder sb = new StringBuilder();
        Name[] parts = name.parts();
        for (int i = 0; i < parts.length; i++) {
            sb.append(parts[i].first());
            if (i < parts.length - 1) {
                sb.append('.');
            }
        }
        return sb.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Failed to find 'out' block for switch in B:2:0x000b. Please report as an issue. */
    public static final String normaliseNameCase(Configuration configuration, String name, boolean quoted, Locale locale) {
        switch (parseNameCase(configuration)) {
            case LOWER_IF_UNQUOTED:
                if (quoted) {
                    return name;
                }
            case LOWER:
                return name.toLowerCase(locale);
            case UPPER_IF_UNQUOTED:
                if (quoted) {
                    return name;
                }
            case UPPER:
                return name.toUpperCase(locale);
            case AS_IS:
            case DEFAULT:
            default:
                return name;
        }
    }

    private static final ParseNameCase parseNameCase(Configuration configuration) {
        ParseNameCase result = (ParseNameCase) StringUtils.defaultIfNull(configuration.settings().getParseNameCase(), ParseNameCase.DEFAULT);
        if (result == ParseNameCase.DEFAULT) {
            switch (((SQLDialect) StringUtils.defaultIfNull(configuration.settings().getParseDialect(), configuration.family())).family()) {
                case MARIADB:
                case MYSQL:
                case SQLITE:
                case DUCKDB:
                case TRINO:
                    return ParseNameCase.AS_IS;
                case POSTGRES:
                case YUGABYTEDB:
                    return ParseNameCase.LOWER_IF_UNQUOTED;
                case CUBRID:
                case HSQLDB:
                case DERBY:
                case H2:
                default:
                    return ParseNameCase.UPPER_IF_UNQUOTED;
            }
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final NestedCollectionEmulation emulateMultiset(Configuration configuration) {
        NestedCollectionEmulation result = (NestedCollectionEmulation) StringUtils.defaultIfNull(configuration.settings().getEmulateMultiset(), NestedCollectionEmulation.DEFAULT);
        if (result == NestedCollectionEmulation.DEFAULT) {
            switch (configuration.family()) {
                case MARIADB:
                case MYSQL:
                case SQLITE:
                case H2:
                case TRINO:
                    return NestedCollectionEmulation.JSON;
                case POSTGRES:
                case YUGABYTEDB:
                    return NestedCollectionEmulation.JSONB;
                case CUBRID:
                case HSQLDB:
                case DERBY:
                case DUCKDB:
                default:
                    return NestedCollectionEmulation.NATIVE;
            }
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final DataType<?> dataType(Field<?> field) {
        return dataType(SQLDataType.OTHER, field, false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T> DataType<T> dataType(DataType<T> dataType, Field<?> field, boolean z) {
        if (field == null) {
            return dataType;
        }
        if (z && field.getType() != dataType.getType()) {
            return dataType.nullable(field.getDataType().nullable());
        }
        return (DataType<T>) field.getDataType();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T> DataType<T> allNotNull(DataType<T> defaultType) {
        return defaultType.notNull();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T> DataType<T> allNotNull(DataType<T> defaultType, Field<?> f1) {
        return dataType(defaultType, f1, true);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T> DataType<T> allNotNull(DataType<T> defaultType, Field<?> f1, Field<?> f2) {
        DataType<T> result = dataType(defaultType, f1, true);
        if (result.nullable()) {
            return result;
        }
        if (dataType(f2).nullable()) {
            return result.null_();
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T> DataType<T> allNotNull(DataType<T> defaultType, Field<?> f1, Field<?> f2, Field<?> f3) {
        DataType<T> result = dataType(defaultType, f1, true);
        if (result.nullable()) {
            return result;
        }
        if (dataType(f2).nullable()) {
            return result.null_();
        }
        if (dataType(f3).nullable()) {
            return result.null_();
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T> DataType<T> allNotNull(DataType<T> defaultType, Field<?>... fields) {
        DataType<T> result = dataType(defaultType, isEmpty(fields) ? null : fields[0], true);
        if (result.nullable()) {
            return result;
        }
        for (Field<?> field : fields) {
            if (dataType(field).nullable()) {
                return result.null_();
            }
        }
        return result;
    }

    static final <T> DataType<T> anyNotNull(DataType<T> defaultType, Field<?> f1) {
        return dataType(defaultType, f1, false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T> DataType<T> anyNotNull(DataType<T> defaultType, Field<?> f1, Field<?> f2) {
        DataType<T> result = dataType(defaultType, f1, false);
        if (!result.nullable()) {
            return result;
        }
        if (!dataType(f2).nullable()) {
            return result.notNull();
        }
        return result;
    }

    static final <T> DataType<T> anyNotNull(DataType<T> defaultType, Field<?> f1, Field<?> f2, Field<?> f3) {
        DataType<T> result = dataType(defaultType, f1, false);
        if (!result.nullable()) {
            return result;
        }
        if (!dataType(f2).nullable()) {
            return result.notNull();
        }
        if (!dataType(f3).nullable()) {
            return result.notNull();
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T> DataType<T> anyNotNull(DataType<T> defaultType, Field<?>... fields) {
        DataType<T> result = dataType(defaultType, isEmpty(fields) ? null : fields[0], false);
        if (!result.nullable()) {
            return result;
        }
        for (Field<?> field : fields) {
            if (!dataType(field).nullable()) {
                return result.notNull();
            }
        }
        return result;
    }

    static final <T> DataType<T> nullable(DataType<T> defaultType, Field<?> f1) {
        return dataType(defaultType, f1, false).null_();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T> DataType<T> nullable(DataType<T> defaultType, Field<?> f1, Field<?> f2) {
        return dataType(defaultType, f1, false).null_();
    }

    static final <T> DataType<T> nullable(DataType<T> defaultType, Field<?> f1, Field<?> f2, Field<?> f3) {
        return dataType(defaultType, f1, false).null_();
    }

    static final <T> DataType<T> nullable(DataType<T> defaultType, Field<?>... fields) {
        return dataType(defaultType, isEmpty(fields) ? null : fields[0], false).null_();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T> Field<T> nullSafe(Field<T> field) {
        return field == null ? DSL.val((Object) null) : field;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T> Field<T> nullSafe(Field<T> field, DataType<?> dataType) {
        if (field == null) {
            return DSL.val((Object) null, dataType);
        }
        if (field instanceof Condition) {
            return (Field<T>) DSL.field((Condition) field);
        }
        return convertVal(field, dataType);
    }

    static final Field<?> nullSafeNoConvertVal(Object field, DataType<?> type) {
        if (field == null) {
            return DSL.val((Object) null, type);
        }
        if (field instanceof Condition) {
            Condition c = (Condition) field;
            return DSL.field(c);
        }
        return field(field);
    }

    static final <T> Field<T> convertVal(Field<T> field, DataType<?> dataType) {
        if (isVal(field)) {
            return extractVal(field).convertTo(dataType);
        }
        return field;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Field<?>[] nullSafe(Field<?>... fields) {
        if (fields == null) {
            return EMPTY_FIELD;
        }
        Field<?>[] result = new Field[fields.length];
        for (int i = 0; i < fields.length; i++) {
            result[i] = nullSafe(fields[i]);
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Field<?>[] nullSafe(Field<?>[] fields, DataType<?> type) {
        if (fields == null) {
            return EMPTY_FIELD;
        }
        Field<?>[] result = new Field[fields.length];
        for (int i = 0; i < fields.length; i++) {
            result[i] = nullSafe(fields[i], type);
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final List<Field<?>> nullSafeList(Field<?>... fields) {
        if (fields == null) {
            return Arrays.asList(EMPTY_FIELD);
        }
        return map(fields, f -> {
            return nullSafe(f);
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final List<Field<?>> nullSafeList(Field<?>[] fields, DataType<?> type) {
        if (fields == null) {
            return Arrays.asList(EMPTY_FIELD);
        }
        return map(fields, f -> {
            return nullSafe(f, (DataType<?>) type);
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T> DataType<T> nullSafeDataType(Field<T> field) {
        return field == null ? (DataType<T>) SQLDataType.OTHER : field.getDataType();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T> DataType<T> nullSafeDataType(Field<?>[] fieldArr) {
        return isEmpty(fieldArr) ? (DataType<T>) SQLDataType.OTHER : (DataType<T>) fieldArr[0].getDataType();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T> Field<T> nullSafeNotNull(Field<T> field, DataType<?> type) {
        return nullableIf(false, nullSafe(field, type));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Field<?> nullSafeNoConvertValNotNull(Field<?> field, DataType<?> type) {
        return nullableIf(false, nullSafeNoConvertVal(field, type));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T> Field<T> nullableIf(boolean z, Field<T> field) {
        if (isVal(field)) {
            return extractVal(field).convertTo(field.getDataType().nullable(z));
        }
        return field;
    }

    static final boolean containsDeclaredTable(Table<?> in, Table<?> search) {
        return ((Boolean) traverseJoins(in, false, (Predicate<? super boolean>) r -> {
            return r.booleanValue();
        }, search(search, t -> {
            return t;
        }))).booleanValue();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final boolean containsDeclaredTable(Iterable<? extends Table<?>> in, Table<?> search) {
        return ((Boolean) traverseJoins(in, false, (Predicate<? super boolean>) r -> {
            return r.booleanValue();
        }, search(search, t -> {
            return t;
        }))).booleanValue();
    }

    private static final BiFunction<Boolean, Table<?>, Boolean> search(Table<?> table, java.util.function.Function<? super Table<?>, ? extends Table<?>> f) {
        Table<?> unaliased = f.apply(table);
        return (r, t) -> {
            return Boolean.valueOf(r.booleanValue() || unaliased.equals(f.apply(t)));
        };
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final boolean containsTable(Table<?> in, Table<?> search, boolean unalias) {
        return ((Boolean) traverseJoins(in, false, (Predicate<? super boolean>) r -> {
            return r.booleanValue();
        }, search(search, t -> {
            return unwrap(t, unalias);
        }))).booleanValue();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final boolean containsTable(Iterable<? extends Table<?>> in, Table<?> search, boolean unalias) {
        return ((Boolean) traverseJoins(in, false, (Predicate<? super boolean>) r -> {
            return r.booleanValue();
        }, search(search, t -> {
            return unwrap(t, unalias);
        }))).booleanValue();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final boolean containsUnaliasedTable(Table<?> in, Table<?> search) {
        return ((Boolean) traverseJoins(in, false, (Predicate<? super boolean>) r -> {
            return r.booleanValue();
        }, search(search, Tools::unwrap))).booleanValue();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final boolean containsUnaliasedTable(Iterable<? extends Table<?>> in, Table<?> search) {
        return ((Boolean) traverseJoins(in, false, (Predicate<? super boolean>) r -> {
            return r.booleanValue();
        }, search(search, Tools::unwrap))).booleanValue();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final void traverseJoins(Iterable<? extends Table<?>> i, Consumer<? super Table<?>> consumer) {
        for (Table<?> t : i) {
            traverseJoins(t, consumer);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final void traverseJoins(Table<?> t, Consumer<? super Table<?>> consumer) {
        traverseJoins(t, (Object) null, (Predicate<? super Object>) null, (BiFunction<? super Object, ? super Table<?>, ? extends Object>) (result, x) -> {
            consumer.accept(x);
            return result;
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    static final <T> T traverseJoins(Iterable<? extends Table<?>> i, T result, Predicate<? super T> abort, BiFunction<? super T, ? super Table<?>, ? extends T> function) {
        for (Table<?> t : i) {
            if (abort != null && abort.test(result)) {
                return result;
            }
            result = traverseJoins(t, result, abort, function);
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T> T traverseJoins(Table<?> table, T t, Predicate<? super T> predicate, BiFunction<? super T, ? super Table<?>, ? extends T> biFunction) {
        return (T) traverseJoins(table, t, predicate, (Predicate<? super JoinTable<?>>) null, (Predicate<? super JoinTable<?>>) null, (BiFunction) null, biFunction);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Multi-variable type inference failed */
    public static final <T> T traverseJoins(Iterable<? extends Table<?>> i, T result, Predicate<? super T> abort, Predicate<? super JoinTable<?>> recurseLhs, Predicate<? super JoinTable<?>> recurseRhs, BiFunction<? super T, ? super JoinType, ? extends T> joinTypeFunction, BiFunction<? super T, ? super Table<?>, ? extends T> tableFunction) {
        for (Table<?> t : i) {
            if (abort != null && abort.test(result)) {
                return result;
            }
            result = traverseJoins(t, result, abort, recurseLhs, recurseRhs, joinTypeFunction, tableFunction);
        }
        return result;
    }

    /* JADX WARN: Multi-variable type inference failed */
    static final <T> T traverseJoins(Table<?> t, T result, Predicate<? super T> abort, Predicate<? super JoinTable<?>> recurseLhs, Predicate<? super JoinTable<?>> recurseRhs, BiFunction<? super T, ? super JoinType, ? extends T> joinTypeFunction, BiFunction<? super T, ? super Table<?>, ? extends T> tableFunction) {
        if (abort != null && abort.test(result)) {
            return result;
        }
        if (t instanceof JoinTable) {
            JoinTable<?> j = (JoinTable) t;
            if (recurseLhs == null || recurseLhs.test(j)) {
                result = traverseJoins(j.lhs, result, abort, recurseLhs, recurseRhs, joinTypeFunction, tableFunction);
                if (abort != null && abort.test(result)) {
                    return result;
                }
            }
            if (joinTypeFunction != null) {
                result = joinTypeFunction.apply(result, j.type);
                if (abort != null && abort.test(result)) {
                    return result;
                }
            }
            if (recurseRhs == null || recurseRhs.test(j)) {
                result = traverseJoins(j.rhs, result, abort, recurseLhs, recurseRhs, joinTypeFunction, tableFunction);
            }
        } else if (tableFunction != null) {
            result = tableFunction.apply(result, t);
        }
        return result;
    }

    static final String capitalize(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    static final String decapitalize(String string) {
        return string.substring(0, 1).toLowerCase() + string.substring(1);
    }

    static final String pascalCase(String snakeCase) {
        return (String) Stream.of((Object[]) snakeCase.toLowerCase().split("_")).map(Tools::capitalize).collect(Collectors.joining());
    }

    static final String camelCase(String snakeCase) {
        return decapitalize(pascalCase(snakeCase));
    }

    static final String characterLiteral(char character) {
        return "'" + (character).replace("\\", "\\\\").replace("'", "\\'") + "'";
    }

    static final String stringLiteral(String string) {
        return "\"" + string.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n\" + \n\"") + "\"";
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final String replaceAll(String string, Matcher matcher, java.util.function.Function<MatchResult, String> replacer) {
        return matcher.replaceAll(replacer);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final int asInt(long value) {
        return asInt(value, () -> {
            return new DataTypeException("Long value too large for int downcast: " + value);
        });
    }

    static final <E extends Exception> int asInt(long value, Supplier<E> e) throws Exception {
        if (value > 2147483647L) {
            throw e.get();
        }
        return (int) value;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T, E extends Exception> T ignoreNPE(ThrowingSupplier<? extends T, ? extends E> supplier, Supplier<? extends T> ifNPE) throws Exception {
        try {
            return supplier.get();
        } catch (NullPointerException e) {
            return ifNPE.get();
        } catch (Exception e2) {
            if (ExceptionTools.getCause(e2, NullPointerException.class) != null) {
                return ifNPE.get();
            }
            throw e2;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T> DataType<T> removeGenerator(Configuration configuration, DataType<T> dataType) {
        return dataType;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final ConverterContext converterContext(Attachable attachable) {
        return new DefaultConverterContext(configuration(attachable));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final ConverterContext converterContext(Configuration configuration) {
        return new DefaultConverterContext(configuration(configuration));
    }

    static final <T1, R> Field<R> derivedTable(Context<?> ctx, Field<T1> f1, org.jooq.Function1<? super Field<T1>, ? extends Field<R>> f) {
        return derivedTableIf(ctx, true, f1, f);
    }

    static final <T1, T2, R> Field<R> derivedTable(Context<?> ctx, Field<T1> f1, Field<T2> f2, Function2<? super Field<T1>, ? super Field<T2>, ? extends Field<R>> f) {
        return derivedTableIf(ctx, true, f1, f2, f);
    }

    static final <T1, T2, T3, R> Field<R> derivedTable(Context<?> ctx, Field<T1> f1, Field<T2> f2, Field<T3> f3, Function3<? super Field<T1>, ? super Field<T2>, ? super Field<T3>, ? extends Field<R>> f) {
        return derivedTableIf(ctx, true, f1, f2, f3, f);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <T1, R> Field<R> derivedTableIf(Context<?> ctx, boolean condition, Field<T1> f1, org.jooq.Function1<? super Field<T1>, ? extends Field<R>> f) {
        if (condition && derivedTableEnabled(ctx) && !isSimple(ctx, f1)) {
            return DSL.field(DSL.select(f.apply(DSL.field(DSL.name("f1"), f1.getDataType()))).from(DSL.select(f1.as("f1")).asTable("t")));
        }
        return f.apply(f1);
    }

    static final <T1, T2, R> Field<R> derivedTableIf(Context<?> ctx, boolean condition, Field<T1> f1, Field<T2> f2, Function2<? super Field<T1>, ? super Field<T2>, ? extends Field<R>> f) {
        if (condition && derivedTableEnabled(ctx) && !isSimple(ctx, f1) && !isSimple(ctx, f2)) {
            return DSL.field(DSL.select(f.apply(DSL.field(DSL.name("f1"), f1.getDataType()), DSL.field(DSL.name("f2"), f2.getDataType()))).from(DSL.select(f1.as("f1"), f2.as("f2")).asTable("t")));
        }
        return f.apply(f1, f2);
    }

    static final <T1, T2, T3, R> Field<R> derivedTableIf(Context<?> ctx, boolean condition, Field<T1> f1, Field<T2> f2, Field<T3> f3, Function3<? super Field<T1>, ? super Field<T2>, ? super Field<T3>, ? extends Field<R>> f) {
        if (condition && derivedTableEnabled(ctx) && !isSimple(ctx, f1) && !isSimple(ctx, f2) && !isSimple(ctx, f3)) {
            return DSL.field(DSL.select(f.apply(DSL.field(DSL.name("f1"), f1.getDataType()), DSL.field(DSL.name("f2"), f2.getDataType()), DSL.field(DSL.name("f3"), f3.getDataType()))).from(DSL.select(f1.as("f1"), f2.as("f2"), f3.as("f3")).asTable("t")));
        }
        return f.apply(f1, f2, f3);
    }

    private static boolean derivedTableEnabled(Context<?> ctx) {
        return (Boolean.FALSE.equals(ctx.settings().isRenderVariablesInDerivedTablesForEmulations()) || DerivedTable.NO_SUPPORT_CORRELATED_DERIVED_TABLE.contains(ctx.dialect())) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final DataType<?> componentDataType(Object[] array) {
        if (!isEmpty(array)) {
            Object obj = array[0];
            if (obj instanceof Field) {
                Field<?> f = (Field) obj;
                return f.getDataType();
            }
        }
        return DSL.getDataType(array.getClass().getComponentType());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Object[] mostSpecificArray(Object[] array) {
        if (isEmpty(array)) {
            return array;
        }
        Class<?> type = null;
        for (Object o : array) {
            if (o != null) {
                if (type == null) {
                    type = o.getClass();
                } else if (type != o.getClass()) {
                    return array;
                }
            }
        }
        if (type == null) {
            return array;
        }
        return Convert.convertArray(array, type);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final <R extends Record> QuantifiedSelect<R> quantify(QOM.Quantifier q, Select<R> select) {
        switch (q) {
            case ANY:
                return DSL.any(select);
            case ALL:
                return DSL.all(select);
            default:
                throw new IllegalArgumentException("Unsupported quantifier: " + String.valueOf(q));
        }
    }
}
