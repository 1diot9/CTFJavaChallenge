package org.jooq.impl;

import ch.qos.logback.classic.pattern.CallerDataConverter;
import ch.qos.logback.core.joran.JoranConstants;
import com.fasterxml.jackson.core.JsonFactory;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.el.parser.ELParserConstants;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.apache.tomcat.util.codec.binary.BaseNCodec;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.jooq.AggregateFilterStep;
import org.jooq.AggregateFunction;
import org.jooq.AlterDatabaseStep;
import org.jooq.AlterDomainDropConstraintCascadeStep;
import org.jooq.AlterDomainRenameConstraintStep;
import org.jooq.AlterDomainStep;
import org.jooq.AlterIndexStep;
import org.jooq.AlterSchemaStep;
import org.jooq.AlterSequenceFlagsStep;
import org.jooq.AlterSequenceStep;
import org.jooq.AlterTableAddStep;
import org.jooq.AlterTableDropStep;
import org.jooq.AlterTableStep;
import org.jooq.AlterTypeStep;
import org.jooq.AlterViewStep;
import org.jooq.ArrayAggOrderByStep;
import org.jooq.Block;
import org.jooq.CaseConditionStep;
import org.jooq.CaseValueStep;
import org.jooq.CaseWhenStep;
import org.jooq.Catalog;
import org.jooq.CharacterSet;
import org.jooq.Collation;
import org.jooq.Comment;
import org.jooq.CommentOnIsStep;
import org.jooq.CommonTableExpression;
import org.jooq.Comparator;
import org.jooq.Condition;
import org.jooq.Constraint;
import org.jooq.ConstraintEnforcementStep;
import org.jooq.ConstraintTypeStep;
import org.jooq.Context;
import org.jooq.CreateDomainConstraintStep;
import org.jooq.CreateDomainDefaultStep;
import org.jooq.CreateIndexIncludeStep;
import org.jooq.CreateIndexStep;
import org.jooq.CreateIndexWhereStep;
import org.jooq.CreateSequenceFlagsStep;
import org.jooq.CreateViewAsStep;
import org.jooq.DDLQuery;
import org.jooq.DSLContext;
import org.jooq.DataType;
import org.jooq.DatePart;
import org.jooq.Delete;
import org.jooq.DeleteLimitStep;
import org.jooq.DeleteOrderByStep;
import org.jooq.DeleteReturningStep;
import org.jooq.DeleteUsingStep;
import org.jooq.DeleteWhereStep;
import org.jooq.DerivedColumnList;
import org.jooq.Domain;
import org.jooq.DropDomainCascadeStep;
import org.jooq.DropIndexCascadeStep;
import org.jooq.DropIndexOnStep;
import org.jooq.DropSchemaStep;
import org.jooq.DropTableStep;
import org.jooq.DropTypeStep;
import org.jooq.Field;
import org.jooq.FieldOrRow;
import org.jooq.Function2;
import org.jooq.Function3;
import org.jooq.Function4;
import org.jooq.GrantOnStep;
import org.jooq.GrantToStep;
import org.jooq.GrantWithGrantOptionStep;
import org.jooq.GroupConcatOrderByStep;
import org.jooq.GroupConcatSeparatorStep;
import org.jooq.GroupField;
import org.jooq.Index;
import org.jooq.Insert;
import org.jooq.JSON;
import org.jooq.JSONArrayAggNullStep;
import org.jooq.JSONArrayAggOrderByStep;
import org.jooq.JSONArrayAggReturningStep;
import org.jooq.JSONArrayNullStep;
import org.jooq.JSONArrayReturningStep;
import org.jooq.JSONB;
import org.jooq.JSONEntry;
import org.jooq.JSONObjectAggNullStep;
import org.jooq.JSONObjectAggReturningStep;
import org.jooq.JSONObjectNullStep;
import org.jooq.JSONObjectReturningStep;
import org.jooq.JSONTableColumnPathStep;
import org.jooq.JSONTableColumnsStep;
import org.jooq.JSONValueOnStep;
import org.jooq.JoinType;
import org.jooq.Keyword;
import org.jooq.LanguageContext;
import org.jooq.LikeEscapeStep;
import org.jooq.Merge;
import org.jooq.MergeMatchedDeleteStep;
import org.jooq.MergeMatchedStep;
import org.jooq.MergeMatchedWhereStep;
import org.jooq.MergeUsingStep;
import org.jooq.Meta;
import org.jooq.Name;
import org.jooq.OrderField;
import org.jooq.OrderedAggregateFunction;
import org.jooq.OrderedAggregateFunctionOfDeferredType;
import org.jooq.Param;
import org.jooq.ParseContext;
import org.jooq.Privilege;
import org.jooq.QualifiedAsterisk;
import org.jooq.Queries;
import org.jooq.Query;
import org.jooq.QueryPart;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.ResultQuery;
import org.jooq.RevokeFromStep;
import org.jooq.RevokeOnStep;
import org.jooq.Row;
import org.jooq.SQLDialect;
import org.jooq.SQLDialectCategory;
import org.jooq.Schema;
import org.jooq.Select;
import org.jooq.SelectField;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.Sequence;
import org.jooq.SortField;
import org.jooq.SortOrder;
import org.jooq.Statement;
import org.jooq.Table;
import org.jooq.TableElement;
import org.jooq.TableField;
import org.jooq.TableLike;
import org.jooq.TableOnStep;
import org.jooq.TableOptionalOnStep;
import org.jooq.TablePartitionByStep;
import org.jooq.Truncate;
import org.jooq.TruncateCascadeStep;
import org.jooq.TruncateIdentityStep;
import org.jooq.Update;
import org.jooq.UpdateFromStep;
import org.jooq.UpdateLimitStep;
import org.jooq.UpdateOrderByStep;
import org.jooq.UpdateReturningStep;
import org.jooq.UpdateSetFirstStep;
import org.jooq.UpdateWhereStep;
import org.jooq.User;
import org.jooq.VisitListener;
import org.jooq.WindowBeforeOverStep;
import org.jooq.WindowDefinition;
import org.jooq.WindowFromFirstLastStep;
import org.jooq.WindowIgnoreNullsStep;
import org.jooq.WindowOverStep;
import org.jooq.WindowSpecification;
import org.jooq.WindowSpecificationExcludeStep;
import org.jooq.WindowSpecificationOrderByStep;
import org.jooq.WindowSpecificationRowsAndStep;
import org.jooq.WindowSpecificationRowsStep;
import org.jooq.XML;
import org.jooq.XMLAggOrderByStep;
import org.jooq.XMLAttributes;
import org.jooq.XMLTableColumnPathStep;
import org.jooq.XMLTableColumnsFirstStep;
import org.jooq.XMLTableColumnsStep;
import org.jooq.XMLTablePassingStep;
import org.jooq.conf.ParseSearchSchema;
import org.jooq.conf.ParseUnknownFunctions;
import org.jooq.conf.ParseUnsupportedSyntax;
import org.jooq.conf.ParseWithMetaLookups;
import org.jooq.conf.RenderKeywordCase;
import org.jooq.conf.RenderNameCase;
import org.jooq.conf.RenderQuotedNames;
import org.jooq.conf.SettingsTools;
import org.jooq.impl.JSONExists;
import org.jooq.impl.JSONValue;
import org.jooq.impl.QOM;
import org.jooq.impl.ScopeStack;
import org.jooq.impl.Tools;
import org.jooq.tools.StringUtils;
import org.jooq.tools.reflect.Reflect;
import org.jooq.types.DayToSecond;
import org.jooq.types.Interval;
import org.jooq.types.YearToMonth;
import org.jooq.types.YearToSecond;
import org.springframework.asm.Opcodes;
import org.springframework.asm.TypeReference;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.factory.support.PropertiesBeanDefinitionReader;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.web.server.session.HeaderWebSessionIdResolver;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: ParserImpl.java */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultParseContext.class */
public final class DefaultParseContext extends AbstractScope implements ParseContext {
    static final Set<SQLDialect> SUPPORTS_HASH_COMMENT_SYNTAX = SQLDialect.supportedBy(SQLDialect.MARIADB, SQLDialect.MYSQL);
    private static final Pattern P_SEARCH_PATH = Pattern.compile("(?i:select\\s+(pg_catalog\\s*\\.\\s*)?set_config\\s*\\(\\s*'search_path'\\s*,\\s*'([^']*)'\\s*,\\s*\\w+\\s*\\))");
    private static final Set<String> ALTER_KEYWORDS = new HashSet(Arrays.asList("ADD", "ALTER", "COMMENT", "DROP", "MODIFY", "RENAME"));
    private static final String[] KEYWORDS_IN_STATEMENTS = {"ALTER", "BEGIN", "COMMENT", "CREATE", "DECLARE", "DELETE", "DROP", "END", "GO", "GRANT", "INSERT", "MERGE", "RENAME", "REVOKE", "SELECT", "SET", "TRUNCATE", "UPDATE", "USE", "VALUES", "WITH"};
    private static final String[] KEYWORDS_IN_SELECT = {"CONNECT BY", "EXCEPT", "FETCH FIRST", "FETCH NEXT", "FOR JSON", "FOR KEY SHARE", "FOR NO KEY UPDATE", "FOR SHARE", "FOR UPDATE", "FOR XML", "FROM", "GROUP BY", "HAVING", "INTERSECT", "INTO", "LIMIT", "MINUS", "OFFSET", "ON", "ORDER BY", "PARTITION BY", "QUALIFY", "RETURNING", "ROWS", "START WITH", "UNION", "WHERE", "WINDOW"};
    private static final String[] KEYWORDS_IN_FROM = {"CROSS APPLY", "CROSS JOIN", "FULL JOIN", "FULL HASH JOIN", "FULL LOOP JOIN", "FULL LOOKUP JOIN", "FULL MERGE JOIN", "FULL OUTER JOIN", "FULL OUTER HASH JOIN", "FULL OUTER LOOP JOIN", "FULL OUTER LOOKUP JOIN", "FULL OUTER MERGE JOIN", "INNER JOIN", "INNER HASH JOIN", "INNER LOOP JOIN", "INNER LOOKUP JOIN", "INNER MERGE JOIN", "JOIN", "LEFT ANTI JOIN", "LEFT JOIN", "LEFT HASH JOIN", "LEFT LOOP JOIN", "LEFT LOOKUP JOIN", "LEFT MERGE JOIN", "LEFT OUTER JOIN", "LEFT OUTER HASH JOIN", "LEFT OUTER LOOP JOIN", "LEFT OUTER LOOKUP JOIN", "LEFT OUTER MERGE JOIN", "LEFT SEMI JOIN", "NATURAL FULL JOIN", "NATURAL FULL OUTER JOIN", "NATURAL INNER JOIN", "NATURAL JOIN", "NATURAL LEFT JOIN", "NATURAL LEFT OUTER JOIN", "NATURAL RIGHT JOIN", "NATURAL RIGHT OUTER JOIN", "ON", "OUTER APPLY", "PARTITION BY", "RIGHT ANTI JOIN", "RIGHT JOIN", "RIGHT HASH JOIN", "RIGHT LOOP JOIN", "RIGHT LOOKUP JOIN", "RIGHT MERGE JOIN", "RIGHT OUTER JOIN", "RIGHT OUTER HASH JOIN", "RIGHT OUTER LOOP JOIN", "RIGHT OUTER LOOKUP JOIN", "RIGHT OUTER MERGE JOIN", "RIGHT SEMI JOIN", "STRAIGHT_JOIN", "USING"};
    private static final String[] KEYWORDS_IN_SELECT_FROM;
    private static final String[] KEYWORDS_IN_UPDATE_FROM;
    private static final String[] KEYWORDS_IN_DELETE_FROM;
    private static final String[] PIVOT_KEYWORDS;
    private static final Lazy<DDLQuery> IGNORE;
    private static final Lazy<Query> IGNORE_NO_DELIMITER;
    private final DSLContext dsl;
    private final Locale locale;
    private final Meta meta;
    private char[] sql;
    private final ParseWithMetaLookups metaLookups;
    private boolean metaLookupsForceIgnore;
    private final Consumer<Param<?>> bindParamListener;
    private int positionBeforeWhitespace;
    private int position;
    private boolean ignoreHints;
    private final Object[] bindings;
    private int bindIndex;
    private final Map<String, Param<?>> bindParams;
    private String delimiter;
    private LanguageContext languageContext;
    private EnumSet<FunctionKeyword> forbidden;
    private ParseScope scope;

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: ParserImpl.java */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultParseContext$ComputationalOperation.class */
    public enum ComputationalOperation {
        ANY_VALUE,
        AVG,
        MAX,
        MIN,
        SUM,
        PRODUCT,
        EVERY,
        ANY,
        SOME,
        COUNT,
        STDDEV_POP,
        STDDEV_SAMP,
        VAR_POP,
        VAR_SAMP,
        MEDIAN
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* compiled from: ParserImpl.java */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultParseContext$FunctionKeyword.class */
    public enum FunctionKeyword {
        FK_AND,
        FK_IN
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: ParserImpl.java */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultParseContext$SequenceMethod.class */
    public enum SequenceMethod {
        NEXTVAL,
        CURRVAL
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: ParserImpl.java */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultParseContext$TSQLOuterJoinComparator.class */
    public enum TSQLOuterJoinComparator {
        LEFT,
        RIGHT
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: ParserImpl.java */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultParseContext$TruthValue.class */
    public enum TruthValue {
        T_TRUE,
        T_FALSE,
        T_NULL
    }

    static {
        Set<String> set = new TreeSet<>(Arrays.asList(KEYWORDS_IN_FROM));
        set.addAll(Arrays.asList(KEYWORDS_IN_STATEMENTS));
        set.addAll(Arrays.asList("CONNECT BY", "CREATE", "EXCEPT", "FETCH FIRST", "FETCH NEXT", "FOR JSON", "FOR KEY SHARE", "FOR NO KEY UPDATE", "FOR SHARE", "FOR UPDATE", "FOR XML", "FORCE KEY", "FORCE INDEX", "GROUP BY", "HAVING", "IGNORE KEY", "IGNORE INDEX", "INTERSECT", "INTO", "LIMIT", "MINUS", "OFFSET", "ORDER BY", "QUALIFY", "RETURNING", "ROWS", "START WITH", "UNION", "USE KEY", "USE INDEX", "WHERE", "WINDOW"));
        KEYWORDS_IN_SELECT_FROM = (String[]) set.toArray(Tools.EMPTY_STRING);
        Set<String> set2 = new TreeSet<>(Arrays.asList(KEYWORDS_IN_FROM));
        set2.addAll(Arrays.asList("FROM", "SET", "WHERE", "ORDER BY", "LIMIT", "RETURNING"));
        KEYWORDS_IN_UPDATE_FROM = (String[]) set2.toArray(Tools.EMPTY_STRING);
        Set<String> set3 = new TreeSet<>(Arrays.asList(KEYWORDS_IN_FROM));
        set3.addAll(Arrays.asList("FROM", "USING", "ALL", "WHERE", "ORDER BY", "LIMIT", "RETURNING"));
        set3.addAll(Arrays.asList(KEYWORDS_IN_STATEMENTS));
        KEYWORDS_IN_DELETE_FROM = (String[]) set3.toArray(Tools.EMPTY_STRING);
        PIVOT_KEYWORDS = new String[]{"FOR"};
        IGNORE = Lazy.of(() -> {
            return new IgnoreQuery();
        });
        IGNORE_NO_DELIMITER = Lazy.of(() -> {
            return new IgnoreQuery();
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Queries parse() {
        return (Queries) wrap(() -> {
            Query query;
            List<Query> result = new ArrayList<>();
            int p = this.positionBeforeWhitespace;
            do {
                parseDelimiterSpecifications();
                while (parseDelimiterIf(false)) {
                    p = this.positionBeforeWhitespace;
                }
                retainComments(result, p);
                query = patchParsedQuery(parseQuery(false, false));
                if (query != IGNORE.get() && query != IGNORE_NO_DELIMITER.get() && query != null) {
                    result.add(query);
                }
                if (!parseDelimiterIf(true)) {
                    break;
                }
                int i = this.positionBeforeWhitespace;
                p = i;
                if (i < 0) {
                    break;
                }
            } while (!done());
            if (query != null) {
                retainComments(result, p);
            }
            return (Queries) done("Unexpected token or missing query delimiter", this.dsl.queries(result));
        });
    }

    private final void retainComments(List<Query> result, int p) {
        if (Boolean.TRUE.equals(settings().isParseRetainCommentsBetweenQueries()) && p < this.position) {
            for (int i = p; i < this.position; i++) {
                if (character(i) != ' ') {
                    result.add(new IgnoreQuery(substring(p, this.position)));
                    return;
                }
            }
        }
    }

    private final Query patchParsedQuery(Query query) {
        if (isDDLDatabase() && (query instanceof Select)) {
            String string = configuration().deriveSettings(s -> {
                return s.withRenderFormatted(false).withRenderKeywordCase(RenderKeywordCase.LOWER).withRenderNameCase(RenderNameCase.LOWER).withRenderQuotedNames(RenderQuotedNames.NEVER).withRenderSchema(false);
            }).dsl().render(query);
            Matcher matcher = P_SEARCH_PATH.matcher(string);
            if (matcher.find()) {
                String schema = matcher.group(2);
                if (!StringUtils.isBlank(schema)) {
                    return configuration().dsl().setSchema(schema);
                }
                return IGNORE.get();
            }
        }
        return query;
    }

    private boolean isDDLDatabase() {
        return Boolean.TRUE.equals(configuration().data("org.jooq.ddl.parse-for-ddldatabase"));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Query parseQuery0() {
        return (Query) wrap(() -> {
            return (Query) done("Unexpected clause", parseQuery(false, false));
        });
    }

    final Statement parseStatement0() {
        return (Statement) wrap(() -> {
            return (Statement) done("Unexpected content", parseStatementAndSemicolonIf());
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final ResultQuery<?> parseResultQuery0() {
        return (ResultQuery) wrap(() -> {
            return (ResultQuery) done("Unexpected content after end of query input", (ResultQuery) parseQuery(true, false));
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Select<?> parseSelect0() {
        return (Select) wrap(() -> {
            return (Select) done("Unexpected content after end of query input", (Select) parseQuery(true, true));
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Table<?> parseTable0() {
        return (Table) wrap(() -> {
            return (Table) done("Unexpected content after end of table input", parseTable());
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Field<?> parseField0() {
        return (Field) wrap(() -> {
            return (Field) done("Unexpected content after end of field input", parseField());
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Row parseRow0() {
        return (Row) wrap(() -> {
            return (Row) done("Unexpected content after end of row input", parseRow());
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Condition parseCondition0() {
        return (Condition) wrap(() -> {
            return (Condition) done("Unexpected content after end of condition input", parseCondition());
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Name parseName0() {
        return (Name) wrap(() -> {
            return (Name) done("Unexpected content after end of name input", parseName());
        });
    }

    private final void parseDelimiterSpecifications() {
        while (parseKeywordIf("DELIMITER")) {
            delimiter(parseUntilEOL().trim());
        }
    }

    private final boolean parseDelimiterIf(boolean optional) {
        if (parseIf(delimiter())) {
            return true;
        }
        if (peekKeyword("GO")) {
            positionInc(2);
            String line = parseUntilEOLIf();
            if (line != null && !"".equals(line.trim())) {
                throw exception("GO must be only token on line");
            }
            parseWhitespaceIf();
            return true;
        }
        return optional;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Failed to find 'out' block for switch in B:9:0x0029. Please report as an issue. */
    /* JADX WARN: Removed duplicated region for block: B:275:0x0896 A[Catch: ParserException -> 0x0940, all -> 0x094c, TRY_LEAVE, TryCatch #0 {ParserException -> 0x0940, blocks: (B:8:0x001e, B:9:0x0029, B:12:0x00c8, B:14:0x00d1, B:19:0x0101, B:21:0x010e, B:26:0x013a, B:28:0x0144, B:33:0x0172, B:35:0x017c, B:40:0x01af, B:42:0x01b9, B:47:0x01e9, B:49:0x01f3, B:54:0x0223, B:56:0x022d, B:61:0x025e, B:63:0x0268, B:66:0x0296, B:68:0x029d, B:70:0x02a7, B:75:0x02b5, B:77:0x02bf, B:80:0x02e7, B:82:0x02f1, B:83:0x02f8, B:86:0x02fd, B:88:0x0304, B:90:0x030e, B:92:0x0315, B:97:0x0342, B:99:0x034f, B:104:0x037d, B:106:0x0387, B:111:0x03b7, B:113:0x03c1, B:118:0x03ed, B:120:0x03f7, B:125:0x0424, B:127:0x042e, B:130:0x0456, B:132:0x045d, B:134:0x0467, B:139:0x0475, B:141:0x0482, B:146:0x04b0, B:148:0x04ba, B:153:0x04ea, B:155:0x04f7, B:158:0x0521, B:160:0x052b, B:161:0x0532, B:164:0x0537, B:166:0x0541, B:171:0x056e, B:173:0x0578, B:178:0x05a4, B:180:0x05ae, B:185:0x05de, B:187:0x05e8, B:190:0x0614, B:192:0x061e, B:193:0x0625, B:196:0x062a, B:198:0x0634, B:203:0x0660, B:205:0x066a, B:208:0x0692, B:210:0x069a, B:215:0x06c6, B:217:0x06d0, B:222:0x06fc, B:224:0x0709, B:229:0x0735, B:231:0x073f, B:236:0x076b, B:238:0x0775, B:243:0x07a1, B:245:0x07ab, B:250:0x07d7, B:252:0x07e4, B:257:0x0812, B:259:0x081c, B:262:0x0844, B:264:0x084e, B:265:0x0855, B:268:0x085a, B:270:0x0864, B:273:0x088c, B:275:0x0896, B:278:0x08bf, B:280:0x08cc, B:283:0x08f5, B:286:0x091d, B:288:0x0924, B:290:0x092e, B:293:0x0938, B:294:0x093f), top: B:7:0x001e, outer: #1 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private final org.jooq.Query parseQuery(boolean r7, boolean r8) {
        /*
            Method dump skipped, instructions count: 2413
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jooq.impl.DefaultParseContext.parseQuery(boolean, boolean):org.jooq.Query");
    }

    private final Query parseWith(boolean parseSelect) {
        return parseWith(parseSelect, null);
    }

    private final Query parseWith(boolean parseSelect, Integer degree) {
        CommonTableExpression<?> as;
        Query result;
        int parens = 0;
        while (parseIf('(')) {
            parens++;
        }
        parseKeyword("WITH");
        boolean recursive = parseKeywordIf("RECURSIVE");
        List<CommonTableExpression<?>> cte = new ArrayList<>();
        while (!parseKeywordIf("FUNCTION")) {
            Name name = parseIdentifier();
            DerivedColumnList dcl = null;
            if (parseIf('(')) {
                List<Name> columnNames = parseIdentifiers();
                parse(')');
                dcl = name.fields((Name[]) columnNames.toArray(Tools.EMPTY_NAME));
            }
            parseKeyword("AS");
            boolean materialized = parseKeywordIf("MATERIALIZED");
            boolean notMaterialized = !materialized && parseKeywordIf("NOT MATERIALIZED");
            parse('(');
            ResultQuery<?> resultQuery = (ResultQuery) parseQuery(true, false);
            parse(')');
            if (dcl != null) {
                if (materialized) {
                    as = dcl.asMaterialized(resultQuery);
                } else if (notMaterialized) {
                    as = dcl.asNotMaterialized(resultQuery);
                } else {
                    as = dcl.as(resultQuery);
                }
            } else if (materialized) {
                as = name.asMaterialized(resultQuery);
            } else if (notMaterialized) {
                as = name.asNotMaterialized(resultQuery);
            } else {
                as = name.as(resultQuery);
            }
            cte.add(as);
            if (!parseIf(',')) {
                WithImpl with = new WithImpl(this.dsl.configuration(), recursive).with((CommonTableExpression<?>[]) cte.toArray(Tools.EMPTY_COMMON_TABLE_EXPRESSION));
                if (!parseSelect && peekKeyword("DELETE", "DEL")) {
                    result = parseDelete(with, false);
                } else if (!parseSelect && peekKeyword("INSERT", "INS")) {
                    result = parseInsert(with, false);
                } else if (!parseSelect && peekKeyword("MERGE")) {
                    result = parseMerge(with);
                } else if (peekSelect(true)) {
                    result = parseSelect(degree, with);
                } else if (!parseSelect && peekKeyword("UPDATE", "UPD")) {
                    result = parseUpdate(with, false);
                } else {
                    if (!parseWhitespaceIf()) {
                    }
                    if (done()) {
                        throw exception("Missing statement after WITH");
                    }
                    throw exception("Unsupported statement after WITH");
                }
                while (true) {
                    int i = parens;
                    parens--;
                    if (i > 0) {
                        parse(')');
                    } else {
                        return result;
                    }
                }
            }
        }
        throw notImplemented("WITH FUNCTION");
    }

    private final Field<?> parseScalarSubqueryIf() {
        int p = position();
        try {
            if (peekSelectOrWith(true)) {
                parse('(');
                SelectQueryImpl<Record> select = parseWithOrSelect();
                parse(')');
                if (Tools.degree(select) != 1) {
                    throw exception("Select list must contain exactly one column");
                }
                return DSL.field(select);
            }
            return null;
        } catch (ParserException e) {
            if (e.getMessage().contains("Token ')' expected")) {
                position(p);
                return null;
            }
            throw e;
        }
    }

    private final SelectQueryImpl<Record> parseWithOrSelect() {
        return parseWithOrSelect(null);
    }

    private final SelectQueryImpl<Record> parseWithOrSelect(Integer degree) {
        return peekKeyword("WITH") ? (SelectQueryImpl) parseWith(true, degree) : parseSelect(degree, null);
    }

    private final SelectQueryImpl<Record> parseSelect() {
        return parseSelect(null, null);
    }

    private final SelectQueryImpl<Record> parseSelect(Integer degree, WithImpl with) {
        this.scope.scopeStart();
        SelectQueryImpl<Record> result = parseQueryExpressionBody(degree, with, null);
        List<SortField<?>> orderBy = null;
        for (Field<?> field : result.getSelect()) {
            if (Tools.aliased(field) != null) {
                this.scope.scope(field);
            }
        }
        if (parseKeywordIf("ORDER") && (ignoreProEdition() || !parseKeywordIf("SIBLINGS BY") || !requireProEdition())) {
            if (parseKeywordIf("BY")) {
                List<SortField<?>> parseList = parseList(',', c -> {
                    return c.parseSortField();
                });
                orderBy = parseList;
                result.addOrderBy(parseList);
            } else {
                throw expected("SIBLINGS BY", "BY");
            }
        }
        boolean limit = false;
        boolean for_ = false;
        boolean offset = false;
        if (orderBy != null && parseKeywordIf("SEEK")) {
            boolean before = parseKeywordIf("BEFORE");
            if (!before) {
                parseKeywordIf("AFTER");
            }
            List<Field<?>> seek = parseList(',', c2 -> {
                return c2.parseField();
            });
            if (seek.size() != orderBy.size()) {
                throw exception("ORDER BY size (" + orderBy.size() + ") and SEEK size (" + seek.size() + ") must match");
            }
            if (before) {
                result.addSeekBefore(seek);
            } else {
                result.addSeekAfter(seek);
            }
            offset = true;
        }
        while (true) {
            if (!limit) {
                boolean parseSelectLimit = parseSelectLimit(result, offset);
                limit = parseSelectLimit;
                if (parseSelectLimit) {
                    continue;
                }
            }
            if (for_) {
                break;
            }
            boolean parseSelectFor = parseSelectFor(result);
            for_ = parseSelectFor;
            if (!parseSelectFor) {
                break;
            }
        }
        if (parseKeywordIf("WITH CHECK OPTION")) {
            result.setWithCheckOption();
        } else if (parseKeywordIf("WITH READ ONLY")) {
            result.setWithReadOnly();
        }
        this.scope.scopeEnd(result);
        return result;
    }

    private final boolean parseSelectLimit(SelectQueryImpl<Record> result, boolean offset) {
        boolean limit = result.getLimit().isApplicable();
        if (!limit) {
            parseLimit(result, !offset);
        }
        return limit;
    }

    private final boolean parseSelectFor(SelectQueryImpl<Record> result) {
        boolean for_ = parseKeywordIf("FOR");
        if (for_) {
            if (parseKeywordIf("KEY SHARE")) {
                result.setForKeyShare(true);
            } else if (parseKeywordIf("NO KEY UPDATE")) {
                result.setForNoKeyUpdate(true);
            } else if (parseKeywordIf("SHARE")) {
                result.setForShare(true);
            } else if (parseKeywordIf("UPDATE")) {
                result.setForUpdate(true);
            } else if ((ignoreProEdition() || !parseKeywordIf("XML") || !requireProEdition()) && (ignoreProEdition() || !parseKeywordIf("JSONB", JsonFactory.FORMAT_NAME_JSON) || !requireProEdition())) {
                throw expected("UPDATE", "NO KEY UPDATE", "SHARE", "KEY SHARE", "XML", JsonFactory.FORMAT_NAME_JSON);
            }
            if (parseKeywordIf("OF")) {
                if (SelectQueryImpl.NO_SUPPORT_FOR_UPDATE_OF_FIELDS.contains(parseDialect())) {
                    result.setForUpdateOf((Table<?>[]) parseList(',', t -> {
                        return t.parseTable();
                    }).toArray(Tools.EMPTY_TABLE));
                } else {
                    result.setForUpdateOf(parseList(',', c -> {
                        return c.parseField();
                    }));
                }
            }
            if (parseKeywordIf("NOWAIT")) {
                result.setForUpdateNoWait();
            } else if ((ignoreProEdition() || !parseKeywordIf("WAIT") || !requireProEdition()) && parseKeywordIf("SKIP LOCKED")) {
                result.setForUpdateSkipLocked();
            }
        }
        return for_;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private final void parseLimit(SelectQueryImpl<Record> selectQueryImpl, boolean allowOffset) {
        boolean offsetStandard = false;
        boolean offsetPostgres = false;
        if (allowOffset && parseKeywordIf("OFFSET")) {
            selectQueryImpl.addOffset((Field<? extends Number>) parseField());
            if (parseKeywordIf("ROWS", "ROW")) {
                offsetStandard = true;
            } else if (peekKeyword("FETCH")) {
                offsetStandard = true;
            } else {
                offsetPostgres = true;
            }
        }
        if (!offsetStandard && parseKeywordIf("LIMIT")) {
            Field<?> parseField = parseField();
            if (offsetPostgres) {
                selectQueryImpl.addLimit((Field<? extends Number>) parseField);
                if (parseKeywordIf("PERCENT")) {
                    selectQueryImpl.setLimitPercent(true);
                }
                if (parseKeywordIf("WITH TIES")) {
                    selectQueryImpl.setWithTies(true);
                    return;
                }
                return;
            }
            if (allowOffset && parseIf(',')) {
                selectQueryImpl.addLimit((Field<? extends Number>) parseField, (Field<? extends Number>) parseField());
                return;
            }
            if (parseKeywordIf("PERCENT")) {
                selectQueryImpl.setLimitPercent(true);
            }
            if (parseKeywordIf("WITH TIES")) {
                selectQueryImpl.setWithTies(true);
            }
            if (allowOffset && parseKeywordIf("OFFSET")) {
                selectQueryImpl.addLimit((Field<? extends Number>) parseField(), (Field<? extends Number>) parseField);
                return;
            } else {
                selectQueryImpl.addLimit((Field<? extends Number>) parseField);
                return;
            }
        }
        if (!offsetPostgres && parseKeywordIf("FETCH")) {
            parseAndGetKeyword("FIRST", "NEXT");
            if (parseAndGetKeywordIf("ROW", "ROWS") != null) {
                selectQueryImpl.addLimit(DSL.inline(1L));
            } else {
                selectQueryImpl.addLimit((Field<? extends Number>) parseField());
                if (parseKeywordIf("PERCENT")) {
                    selectQueryImpl.setLimitPercent(true);
                }
                parseAndGetKeyword("ROW", "ROWS");
            }
            if (parseKeywordIf("WITH TIES")) {
                selectQueryImpl.setWithTies(true);
                return;
            } else {
                parseKeyword("ONLY");
                return;
            }
        }
        if (!offsetStandard && !offsetPostgres && parseKeywordIf("ROWS")) {
            Long from = parseUnsignedIntegerLiteral();
            if (parseKeywordIf("TO")) {
                Long to = parseUnsignedIntegerLiteral();
                selectQueryImpl.addLimit(Long.valueOf(to.longValue() - from.longValue()));
                selectQueryImpl.addOffset(Long.valueOf(from.longValue() - 1));
                return;
            }
            selectQueryImpl.addLimit(from);
        }
    }

    private final SelectQueryImpl<Record> parseQueryExpressionBody(Integer degree, WithImpl with, SelectQueryImpl<Record> prefix) {
        SelectQueryImpl<Record> lhs = parseQueryTerm(degree, with, prefix);
        SelectQueryImpl<Record> local = lhs;
        while (true) {
            CombineOperator combine = parseCombineOperatorIf(false);
            if (combine != null) {
                this.scope.scopeEnd(local);
                this.scope.scopeStart();
                if (degree == null) {
                    degree = Integer.valueOf(Tools.degree(lhs));
                }
                SelectQueryImpl<Record> rhs = degreeCheck(degree.intValue(), parseQueryTerm(degree, null, null));
                local = rhs;
                switch (combine) {
                    case UNION:
                        lhs = lhs.union((Select<? extends Record>) rhs);
                        break;
                    case UNION_ALL:
                        lhs = lhs.unionAll((Select<? extends Record>) rhs);
                        break;
                    case EXCEPT:
                        lhs = lhs.except((Select<? extends Record>) rhs);
                        break;
                    case EXCEPT_ALL:
                        lhs = lhs.exceptAll((Select<? extends Record>) rhs);
                        break;
                    default:
                        throw internalError();
                }
            } else {
                return lhs;
            }
        }
    }

    private final SelectQueryImpl<Record> parseQueryTerm(Integer degree, WithImpl with, SelectQueryImpl<Record> prefix) {
        SelectQueryImpl<Record> lhs = prefix != null ? prefix : parseQueryPrimary(degree, with);
        SelectQueryImpl<Record> local = lhs;
        while (true) {
            CombineOperator combine = parseCombineOperatorIf(true);
            if (combine != null) {
                this.scope.scopeEnd(local);
                this.scope.scopeStart();
                if (degree == null) {
                    degree = Integer.valueOf(Tools.degree(lhs));
                }
                SelectQueryImpl<Record> rhs = degreeCheck(degree.intValue(), parseQueryPrimary(degree, null));
                local = rhs;
                switch (combine) {
                    case INTERSECT:
                        lhs = lhs.intersect((Select<? extends Record>) rhs);
                        break;
                    case INTERSECT_ALL:
                        lhs = lhs.intersectAll((Select<? extends Record>) rhs);
                        break;
                    default:
                        throw internalError();
                }
            } else {
                return lhs;
            }
        }
    }

    private final SelectQueryImpl<Record> degreeCheck(int expected, SelectQueryImpl<Record> s) {
        if (expected == 0) {
            return s;
        }
        int actual = Tools.degree(s);
        if (actual == 0) {
            return s;
        }
        if (expected != actual) {
            throw exception("Select list must contain " + expected + " columns. Got: " + actual);
        }
        return s;
    }

    private final SelectQueryImpl<Record> parseQueryPrimary(Integer degree, WithImpl with) {
        if (parseIf('(')) {
            SelectQueryImpl<Record> result = parseSelect(degree, with);
            parse(')');
            return result;
        }
        if (peekKeyword("VALUES")) {
            return (SelectQueryImpl) this.dsl.selectQuery(parseTableValueConstructor());
        }
        if (peekKeyword("TABLE")) {
            return (SelectQueryImpl) this.dsl.selectQuery(parseExplicitTable());
        }
        ignoreHints(false);
        parseKeywordUndocumentedAlternatives("SELECT", "SEL");
        String hints = parseHints();
        boolean distinct = parseKeywordIf("DISTINCT", "UNIQUE");
        List<Field<?>> distinctOn = null;
        if (distinct) {
            if (parseKeywordIf("ON")) {
                parse('(');
                distinctOn = parseList(',', c -> {
                    return c.parseField();
                });
                parse(')');
            }
        } else {
            parseKeywordIf("ALL");
        }
        Field<?> field = null;
        Field<?> field2 = null;
        boolean percent = false;
        boolean withTies = false;
        if (parseKeywordIf("TOP")) {
            field = parseField();
            percent = !ignoreProEdition() && parseKeywordIf("PERCENT") && requireProEdition();
            if (parseKeywordIf("START AT")) {
                field2 = parseField();
            } else if (parseKeywordIf("WITH TIES")) {
                withTies = true;
            }
        } else if (parseKeywordIf("SKIP")) {
            field2 = parseField();
            if (parseKeywordIf("FIRST")) {
                field = parseField();
            }
        } else if (parseKeywordIf("FIRST")) {
            field = parseField();
        }
        List<SelectFieldOrAsterisk> select = parseSelectList();
        if (degree != null && !degree.equals(0) && !degree.equals(Integer.valueOf(select.size()))) {
            for (SelectFieldOrAsterisk s : select) {
                if (!(s instanceof Field)) {
                }
            }
            throw exception("Select list must contain " + degree + " columns. Got: " + select.size());
        }
        Table<?> intoTable = null;
        List<Table<?>> from = null;
        if (parseKeywordIf("INTO") && !proEdition()) {
            intoTable = parseTableName();
        }
        if (parseKeywordIf("FROM")) {
            from = parseList(',', (v0) -> {
                return v0.parseTable();
            });
        }
        if (from != null && from.size() == 1 && from.get(0).getName().equalsIgnoreCase("dual")) {
            from = null;
        }
        if (from != null) {
            for (Table<?> table : from) {
                this.scope.scope(table);
            }
        }
        SelectQueryImpl<Record> result2 = new SelectQueryImpl<>(this.dsl.configuration(), with);
        if (hints != null) {
            result2.addHint(hints);
        }
        if (distinct) {
            result2.setDistinct(distinct);
        }
        if (distinctOn != null) {
            result2.addDistinctOn(distinctOn);
        }
        if (!select.isEmpty()) {
            result2.addSelect(select);
        }
        if (intoTable != null) {
            result2.setInto(intoTable);
        }
        if (from != null) {
            result2.addFrom(from);
        }
        boolean where = false;
        boolean connectBy = false;
        boolean startWith = false;
        boolean groupBy = false;
        boolean having = false;
        while (true) {
            if (!where) {
                boolean parseQueryPrimaryWhere = parseQueryPrimaryWhere(result2);
                where = parseQueryPrimaryWhere;
                if (parseQueryPrimaryWhere) {
                    continue;
                }
            }
            if (!connectBy) {
                boolean parseQueryPrimaryConnectBy = parseQueryPrimaryConnectBy(result2);
                connectBy = parseQueryPrimaryConnectBy;
                if (parseQueryPrimaryConnectBy) {
                    continue;
                }
            }
            if (!startWith) {
                boolean parseQueryPrimaryStartWith = parseQueryPrimaryStartWith(result2);
                startWith = parseQueryPrimaryStartWith;
                if (parseQueryPrimaryStartWith) {
                    continue;
                }
            }
            if (!groupBy) {
                boolean parseQueryPrimaryGroupBy = parseQueryPrimaryGroupBy(result2);
                groupBy = parseQueryPrimaryGroupBy;
                if (parseQueryPrimaryGroupBy) {
                    continue;
                }
            }
            if (having) {
                break;
            }
            boolean parseQueryPrimaryHaving = parseQueryPrimaryHaving(result2);
            having = parseQueryPrimaryHaving;
            if (!parseQueryPrimaryHaving) {
                break;
            }
        }
        if (startWith && !connectBy) {
            throw expected("CONNECT BY");
        }
        if (parseKeywordIf("WINDOW")) {
            result2.addWindow(parseWindowDefinitions());
        }
        if (parseKeywordIf("QUALIFY")) {
            result2.addQualify(parseCondition());
        }
        if (field != null) {
            if (field2 != null) {
                result2.addLimit((Field<? extends Number>) field2, (Field<? extends Number>) field);
            } else {
                result2.addLimit((Field<? extends Number>) field);
            }
        }
        if (percent) {
        }
        if (withTies) {
            result2.setWithTies(true);
        }
        return result2;
    }

    private final boolean parseQueryPrimaryWhere(SelectQueryImpl<Record> result) {
        if (parseKeywordIf("WHERE")) {
            result.addConditions(parseCondition());
            return true;
        }
        return false;
    }

    private final boolean parseQueryPrimaryHaving(SelectQueryImpl<Record> result) {
        if (parseKeywordIf("HAVING")) {
            result.addHaving(parseCondition());
            return true;
        }
        return false;
    }

    private final boolean parseQueryPrimaryGroupBy(SelectQueryImpl<Record> result) {
        if (parseKeywordIf("GROUP BY")) {
            if (!parseKeywordIf("ALL") && parseKeywordIf("DISTINCT")) {
                result.setGroupByDistinct(true);
            }
            if (parseIf('(', ')', true)) {
                parse(')');
                result.addGroupBy(new GroupField[0]);
                return true;
            }
            if (parseKeywordIf("ROLLUP")) {
                parse('(');
                result.addGroupBy(DSL.rollup((Field<?>[]) parseList(',', c -> {
                    return c.parseField();
                }).toArray(Tools.EMPTY_FIELD)));
                parse(')');
                return true;
            }
            if (parseKeywordIf("CUBE")) {
                parse('(');
                result.addGroupBy(DSL.cube((Field<?>[]) parseList(',', c2 -> {
                    return c2.parseField();
                }).toArray(Tools.EMPTY_FIELD)));
                parse(')');
                return true;
            }
            if (parseKeywordIf("GROUPING SETS")) {
                parse('(');
                List<List<Field<?>>> fieldSets = parseList(',', c3 -> {
                    return parseFieldsOrEmptyParenthesised();
                });
                parse(')');
                result.addGroupBy(DSL.groupingSets((Collection<? extends Field<?>>[]) fieldSets.toArray(Tools.EMPTY_COLLECTION)));
                return true;
            }
            List<GroupField> groupBy = parseOrdinaryGroupingSets();
            if (parseKeywordIf("WITH ROLLUP")) {
                result.addGroupBy(DSL.rollup((Field<?>[]) groupBy.toArray(Tools.EMPTY_FIELD)));
                return true;
            }
            result.addGroupBy(groupBy);
            return true;
        }
        return false;
    }

    private final List<GroupField> parseOrdinaryGroupingSets() {
        List<GroupField> result = new ArrayList<>();
        do {
            if (peekKeyword("ROW")) {
                result.add(parseField());
            } else {
                FieldOrRow fr = parseFieldOrRow();
                if (fr instanceof Field) {
                    Field<?> f = (Field) fr;
                    result.add(f);
                } else {
                    result.addAll(Arrays.asList(((Row) fr).fields()));
                }
            }
        } while (parseIf(','));
        return result;
    }

    private final boolean parseQueryPrimaryConnectBy(SelectQueryImpl<Record> result) {
        if (!ignoreProEdition() && parseKeywordIf("CONNECT BY") && requireProEdition()) {
            return true;
        }
        return false;
    }

    private final boolean parseQueryPrimaryStartWith(SelectQueryImpl<Record> result) {
        if (!ignoreProEdition() && parseKeywordIf("START WITH") && requireProEdition()) {
            return true;
        }
        return false;
    }

    private final List<WindowDefinition> parseWindowDefinitions() {
        return parseList(',', c -> {
            Name name = parseIdentifier();
            parseKeyword("AS");
            parse('(');
            WindowDefinition result = name.as(parseWindowSpecificationIf(null, true));
            parse(')');
            return result;
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v106, types: [org.jooq.WindowSpecificationRowsStep] */
    /* JADX WARN: Type inference failed for: r0v108, types: [org.jooq.WindowSpecificationRowsStep] */
    /* JADX WARN: Type inference failed for: r0v110, types: [org.jooq.WindowSpecificationRowsStep] */
    /* JADX WARN: Type inference failed for: r0v121, types: [org.jooq.WindowSpecificationRowsStep] */
    /* JADX WARN: Type inference failed for: r0v123, types: [org.jooq.WindowSpecificationRowsStep] */
    /* JADX WARN: Type inference failed for: r0v125, types: [org.jooq.WindowSpecificationRowsStep] */
    /* JADX WARN: Type inference failed for: r0v171, types: [org.jooq.WindowSpecificationRowsStep] */
    /* JADX WARN: Type inference failed for: r0v173, types: [org.jooq.WindowSpecificationRowsStep] */
    /* JADX WARN: Type inference failed for: r0v175, types: [org.jooq.WindowSpecificationRowsStep] */
    /* JADX WARN: Type inference failed for: r0v192, types: [org.jooq.WindowSpecificationRowsStep] */
    /* JADX WARN: Type inference failed for: r0v194, types: [org.jooq.WindowSpecificationRowsStep] */
    /* JADX WARN: Type inference failed for: r0v196, types: [org.jooq.WindowSpecificationRowsStep] */
    /* JADX WARN: Type inference failed for: r0v213, types: [org.jooq.WindowSpecificationRowsStep] */
    /* JADX WARN: Type inference failed for: r0v215, types: [org.jooq.WindowSpecificationRowsStep] */
    /* JADX WARN: Type inference failed for: r0v217, types: [org.jooq.WindowSpecificationRowsStep] */
    /* JADX WARN: Type inference failed for: r0v266, types: [org.jooq.WindowSpecificationRowsStep] */
    /* JADX WARN: Type inference failed for: r0v268, types: [org.jooq.WindowSpecificationRowsStep] */
    /* JADX WARN: Type inference failed for: r0v270, types: [org.jooq.WindowSpecificationRowsStep] */
    /* JADX WARN: Type inference failed for: r0v281, types: [org.jooq.WindowSpecificationRowsStep] */
    /* JADX WARN: Type inference failed for: r0v283, types: [org.jooq.WindowSpecificationRowsStep] */
    /* JADX WARN: Type inference failed for: r0v285, types: [org.jooq.WindowSpecificationRowsStep] */
    /* JADX WARN: Type inference failed for: r0v45, types: [org.jooq.WindowSpecificationRowsStep] */
    /* JADX WARN: Type inference failed for: r0v47, types: [org.jooq.WindowSpecificationRowsStep] */
    /* JADX WARN: Type inference failed for: r0v64, types: [org.jooq.WindowSpecificationRowsStep] */
    /* JADX WARN: Type inference failed for: r0v66, types: [org.jooq.WindowSpecificationRowsStep] */
    /* JADX WARN: Type inference failed for: r0v68, types: [org.jooq.WindowSpecificationRowsStep] */
    /* JADX WARN: Type inference failed for: r0v85, types: [org.jooq.WindowSpecificationRowsStep] */
    /* JADX WARN: Type inference failed for: r0v87, types: [org.jooq.WindowSpecificationRowsStep] */
    /* JADX WARN: Type inference failed for: r0v89, types: [org.jooq.WindowSpecificationRowsStep] */
    private final WindowSpecification parseWindowSpecificationIf(Name windowName, boolean orderByAllowed) {
        WindowSpecificationOrderByStep windowSpecificationOrderByStep;
        WindowSpecificationExcludeStep s2;
        WindowSpecificationExcludeStep groupsFollowing;
        WindowSpecificationExcludeStep s4;
        WindowSpecificationExcludeStep groupsPreceding;
        WindowSpecificationExcludeStep groupsCurrentRow;
        WindowSpecificationExcludeStep groupsUnboundedFollowing;
        WindowSpecificationExcludeStep groupsUnboundedPreceding;
        WindowSpecification result;
        WindowSpecificationRowsAndStep groupsBetweenFollowing;
        WindowSpecificationRowsAndStep s3;
        WindowSpecificationRowsAndStep groupsBetweenPreceding;
        WindowSpecificationRowsAndStep groupsBetweenCurrentRow;
        WindowSpecificationRowsAndStep groupsBetweenUnboundedFollowing;
        WindowSpecificationRowsAndStep groupsBetweenUnboundedPreceding;
        Name windowName2;
        WindowSpecificationRowsStep orderBy;
        if (windowName != null) {
            windowSpecificationOrderByStep = windowName.as();
        } else if (parseKeywordIf("PARTITION BY")) {
            windowSpecificationOrderByStep = DSL.partitionBy(parseList(',', c -> {
                return c.parseField();
            }));
        } else {
            windowSpecificationOrderByStep = null;
        }
        WindowSpecificationOrderByStep s1 = windowSpecificationOrderByStep;
        if (parseKeywordIf("ORDER BY")) {
            if (orderByAllowed) {
                if (s1 == null) {
                    orderBy = DSL.orderBy(parseList(',', c2 -> {
                        return c2.parseSortField();
                    }));
                } else {
                    orderBy = s1.orderBy(parseList(',', c3 -> {
                        return c3.parseSortField();
                    }));
                }
                s2 = orderBy;
            } else {
                throw exception("ORDER BY not allowed");
            }
        } else {
            s2 = s1;
        }
        boolean rows = parseKeywordIf("ROWS");
        boolean range = !rows && parseKeywordIf("RANGE");
        boolean groups = (rows || range || !parseKeywordIf("GROUPS")) ? false : true;
        if ((rows || range || groups) && !orderByAllowed) {
            throw exception("ROWS, RANGE, or GROUPS not allowed");
        }
        if (rows || range || groups) {
            if (parseKeywordIf("BETWEEN")) {
                if (parseKeywordIf("UNBOUNDED")) {
                    if (parseKeywordIf("PRECEDING")) {
                        if (s2 == null) {
                            if (rows) {
                                groupsBetweenUnboundedPreceding = DSL.rowsBetweenUnboundedPreceding();
                            } else if (range) {
                                groupsBetweenUnboundedPreceding = DSL.rangeBetweenUnboundedPreceding();
                            } else {
                                groupsBetweenUnboundedPreceding = DSL.groupsBetweenUnboundedPreceding();
                            }
                        } else if (rows) {
                            groupsBetweenUnboundedPreceding = s2.rowsBetweenUnboundedPreceding();
                        } else if (range) {
                            groupsBetweenUnboundedPreceding = s2.rangeBetweenUnboundedPreceding();
                        } else {
                            groupsBetweenUnboundedPreceding = s2.groupsBetweenUnboundedPreceding();
                        }
                        s3 = groupsBetweenUnboundedPreceding;
                    } else if (parseKeywordIf("FOLLOWING")) {
                        if (s2 == null) {
                            if (rows) {
                                groupsBetweenUnboundedFollowing = DSL.rowsBetweenUnboundedFollowing();
                            } else if (range) {
                                groupsBetweenUnboundedFollowing = DSL.rangeBetweenUnboundedFollowing();
                            } else {
                                groupsBetweenUnboundedFollowing = DSL.groupsBetweenUnboundedFollowing();
                            }
                        } else if (rows) {
                            groupsBetweenUnboundedFollowing = s2.rowsBetweenUnboundedFollowing();
                        } else if (range) {
                            groupsBetweenUnboundedFollowing = s2.rangeBetweenUnboundedFollowing();
                        } else {
                            groupsBetweenUnboundedFollowing = s2.groupsBetweenUnboundedFollowing();
                        }
                        s3 = groupsBetweenUnboundedFollowing;
                    } else {
                        throw expected("FOLLOWING", "PRECEDING");
                    }
                } else if (parseKeywordIf("CURRENT ROW")) {
                    if (s2 == null) {
                        if (rows) {
                            groupsBetweenCurrentRow = DSL.rowsBetweenCurrentRow();
                        } else if (range) {
                            groupsBetweenCurrentRow = DSL.rangeBetweenCurrentRow();
                        } else {
                            groupsBetweenCurrentRow = DSL.groupsBetweenCurrentRow();
                        }
                    } else if (rows) {
                        groupsBetweenCurrentRow = s2.rowsBetweenCurrentRow();
                    } else if (range) {
                        groupsBetweenCurrentRow = s2.rangeBetweenCurrentRow();
                    } else {
                        groupsBetweenCurrentRow = s2.groupsBetweenCurrentRow();
                    }
                    s3 = groupsBetweenCurrentRow;
                } else {
                    Long n = parseUnsignedIntegerLiteralIf();
                    if (n != null) {
                        if (parseKeywordIf("PRECEDING")) {
                            if (s2 == null) {
                                if (rows) {
                                    groupsBetweenPreceding = DSL.rowsBetweenPreceding(n.intValue());
                                } else if (range) {
                                    groupsBetweenPreceding = DSL.rangeBetweenPreceding(n.intValue());
                                } else {
                                    groupsBetweenPreceding = DSL.groupsBetweenPreceding(n.intValue());
                                }
                            } else if (rows) {
                                groupsBetweenPreceding = s2.rowsBetweenPreceding(n.intValue());
                            } else if (range) {
                                groupsBetweenPreceding = s2.rangeBetweenPreceding(n.intValue());
                            } else {
                                groupsBetweenPreceding = s2.groupsBetweenPreceding(n.intValue());
                            }
                            s3 = groupsBetweenPreceding;
                        } else if (parseKeywordIf("FOLLOWING")) {
                            if (s2 == null) {
                                if (rows) {
                                    groupsBetweenFollowing = DSL.rowsBetweenFollowing(n.intValue());
                                } else if (range) {
                                    groupsBetweenFollowing = DSL.rangeBetweenFollowing(n.intValue());
                                } else {
                                    groupsBetweenFollowing = DSL.groupsBetweenFollowing(n.intValue());
                                }
                            } else if (rows) {
                                groupsBetweenFollowing = s2.rowsBetweenFollowing(n.intValue());
                            } else if (range) {
                                groupsBetweenFollowing = s2.rangeBetweenFollowing(n.intValue());
                            } else {
                                groupsBetweenFollowing = s2.groupsBetweenFollowing(n.intValue());
                            }
                            s3 = groupsBetweenFollowing;
                        } else {
                            throw expected("FOLLOWING", "PRECEDING");
                        }
                    } else {
                        throw expected("CURRENT ROW", "UNBOUNDED", "integer literal");
                    }
                }
                parseKeyword("AND");
                if (parseKeywordIf("UNBOUNDED")) {
                    if (parseKeywordIf("PRECEDING")) {
                        s4 = s3.andUnboundedPreceding();
                    } else if (parseKeywordIf("FOLLOWING")) {
                        s4 = s3.andUnboundedFollowing();
                    } else {
                        throw expected("FOLLOWING", "PRECEDING");
                    }
                } else if (parseKeywordIf("CURRENT ROW")) {
                    s4 = s3.andCurrentRow();
                } else {
                    Long n2 = parseUnsignedIntegerLiteral();
                    if (asTrue(n2)) {
                        if (parseKeywordIf("PRECEDING")) {
                            s4 = s3.andPreceding(n2.intValue());
                        } else if (parseKeywordIf("FOLLOWING")) {
                            s4 = s3.andFollowing(n2.intValue());
                        } else {
                            throw expected("FOLLOWING", "PRECEDING");
                        }
                    } else {
                        throw expected("CURRENT ROW", "UNBOUNDED", "integer literal");
                    }
                }
            } else if (parseKeywordIf("UNBOUNDED")) {
                if (parseKeywordIf("PRECEDING")) {
                    if (s2 == null) {
                        if (rows) {
                            groupsUnboundedPreceding = DSL.rowsUnboundedPreceding();
                        } else if (range) {
                            groupsUnboundedPreceding = DSL.rangeUnboundedPreceding();
                        } else {
                            groupsUnboundedPreceding = DSL.groupsUnboundedPreceding();
                        }
                    } else if (rows) {
                        groupsUnboundedPreceding = s2.rowsUnboundedPreceding();
                    } else if (range) {
                        groupsUnboundedPreceding = s2.rangeUnboundedPreceding();
                    } else {
                        groupsUnboundedPreceding = s2.groupsUnboundedPreceding();
                    }
                    s4 = groupsUnboundedPreceding;
                } else if (parseKeywordIf("FOLLOWING")) {
                    if (s2 == null) {
                        if (rows) {
                            groupsUnboundedFollowing = DSL.rowsUnboundedFollowing();
                        } else if (range) {
                            groupsUnboundedFollowing = DSL.rangeUnboundedFollowing();
                        } else {
                            groupsUnboundedFollowing = DSL.groupsUnboundedFollowing();
                        }
                    } else if (rows) {
                        groupsUnboundedFollowing = s2.rowsUnboundedFollowing();
                    } else if (range) {
                        groupsUnboundedFollowing = s2.rangeUnboundedFollowing();
                    } else {
                        groupsUnboundedFollowing = s2.groupsUnboundedFollowing();
                    }
                    s4 = groupsUnboundedFollowing;
                } else {
                    throw expected("FOLLOWING", "PRECEDING");
                }
            } else if (parseKeywordIf("CURRENT ROW")) {
                if (s2 == null) {
                    if (rows) {
                        groupsCurrentRow = DSL.rowsCurrentRow();
                    } else if (range) {
                        groupsCurrentRow = DSL.rangeCurrentRow();
                    } else {
                        groupsCurrentRow = DSL.groupsCurrentRow();
                    }
                } else if (rows) {
                    groupsCurrentRow = s2.rowsCurrentRow();
                } else if (range) {
                    groupsCurrentRow = s2.rangeCurrentRow();
                } else {
                    groupsCurrentRow = s2.groupsCurrentRow();
                }
                s4 = groupsCurrentRow;
            } else {
                Long n3 = parseUnsignedIntegerLiteral();
                if (asTrue(n3)) {
                    if (parseKeywordIf("PRECEDING")) {
                        if (s2 == null) {
                            if (rows) {
                                groupsPreceding = DSL.rowsPreceding(n3.intValue());
                            } else if (range) {
                                groupsPreceding = DSL.rangePreceding(n3.intValue());
                            } else {
                                groupsPreceding = DSL.groupsPreceding(n3.intValue());
                            }
                        } else if (rows) {
                            groupsPreceding = s2.rowsPreceding(n3.intValue());
                        } else if (range) {
                            groupsPreceding = s2.rangePreceding(n3.intValue());
                        } else {
                            groupsPreceding = s2.groupsPreceding(n3.intValue());
                        }
                        s4 = groupsPreceding;
                    } else if (parseKeywordIf("FOLLOWING")) {
                        if (s2 == null) {
                            if (rows) {
                                groupsFollowing = DSL.rowsFollowing(n3.intValue());
                            } else if (range) {
                                groupsFollowing = DSL.rangeFollowing(n3.intValue());
                            } else {
                                groupsFollowing = DSL.groupsFollowing(n3.intValue());
                            }
                        } else if (rows) {
                            groupsFollowing = s2.rowsFollowing(n3.intValue());
                        } else if (range) {
                            groupsFollowing = s2.rangeFollowing(n3.intValue());
                        } else {
                            groupsFollowing = s2.groupsFollowing(n3.intValue());
                        }
                        s4 = groupsFollowing;
                    } else {
                        throw expected("FOLLOWING", "PRECEDING");
                    }
                } else {
                    throw expected("BETWEEN", "CURRENT ROW", "UNBOUNDED", "integer literal");
                }
            }
            if (parseKeywordIf("EXCLUDE")) {
                if (parseKeywordIf("CURRENT ROW")) {
                    result = s4.excludeCurrentRow();
                } else if (parseKeywordIf("TIES")) {
                    result = s4.excludeTies();
                } else if (parseKeywordIf("GROUP")) {
                    result = s4.excludeGroup();
                } else if (parseKeywordIf("NO OTHERS")) {
                    result = s4.excludeNoOthers();
                } else {
                    throw expected("CURRENT ROW", "TIES", "GROUP", "NO OTHERS");
                }
            } else {
                result = s4;
            }
        } else {
            result = s2;
        }
        if (result != null) {
            return result;
        }
        if (windowName == null && (windowName2 = parseIdentifierIf()) != null) {
            return parseWindowSpecificationIf(windowName2, orderByAllowed);
        }
        return null;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private final Query parseDelete(WithImpl with, boolean parseResultQuery) {
        DeleteOrderByStep<?> deleteOrderByStep;
        DeleteReturningStep<?> limit;
        parseKeyword("DELETE", "DEL");
        Field<?> field = null;
        if (parseKeywordIf("TOP")) {
            field = parseField();
        }
        parseKeywordIf("FROM");
        Table<?> table = this.scope.scope(parseJoinedTable(() -> {
            return peekKeyword(KEYWORDS_IN_DELETE_FROM);
        }));
        DeleteUsingStep<?> s1 = with == null ? this.dsl.delete(table) : with.delete((Table) table);
        DeleteWhereStep<?> s2 = parseKeywordIf("USING", "FROM") ? s1.using(parseList(',', t -> {
            return this.scope.scope(parseJoinedTable(() -> {
                return peekKeyword(KEYWORDS_IN_DELETE_FROM);
            }));
        })) : s1;
        if (parseKeywordIf("ALL")) {
            deleteOrderByStep = s2;
        } else if (parseKeywordIf("WHERE")) {
            deleteOrderByStep = s2.where(parseCondition());
        } else {
            deleteOrderByStep = s2;
        }
        DeleteOrderByStep<?> s3 = deleteOrderByStep;
        DeleteLimitStep orderBy = parseKeywordIf("ORDER BY") ? s3.orderBy(parseList(',', c -> {
            return c.parseSortField();
        })) : s3;
        if (field != null || parseKeywordIf("LIMIT")) {
            limit = orderBy.limit((Field<? extends Number>) (field != null ? field : parseField()));
        } else {
            limit = orderBy;
        }
        DeleteReturningStep<?> s5 = limit;
        if (!parseResultQuery ? parseKeywordIf("RETURNING") : parseKeyword("RETURNING")) {
            return s5.returning(parseSelectList());
        }
        return s5;
    }

    /* JADX WARN: Code restructure failed: missing block: B:85:0x0438, code lost:            if (parseKeywordIf("RETURNING") != false) goto L128;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private final org.jooq.Query parseInsert(org.jooq.impl.WithImpl r7, boolean r8) {
        /*
            Method dump skipped, instructions count: 1139
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jooq.impl.DefaultParseContext.parseInsert(org.jooq.impl.WithImpl, boolean):org.jooq.Query");
    }

    /* JADX WARN: Multi-variable type inference failed */
    private final Query parseUpdate(WithImpl with, boolean parseResultQuery) {
        UpdateFromStep<?> s2;
        UpdateWhereStep<?> updateWhereStep;
        UpdateOrderByStep<?> updateOrderByStep;
        UpdateReturningStep<?> limit;
        parseKeywordUndocumentedAlternatives("UPDATE", "UPD");
        Field<?> field = null;
        if (parseKeywordIf("TOP")) {
            field = parseField();
        }
        Table<?> table = this.scope.scope(parseJoinedTable(() -> {
            return peekKeyword(KEYWORDS_IN_UPDATE_FROM);
        }));
        UpdateSetFirstStep<?> s1 = with == null ? this.dsl.update(table) : with.update((Table) table);
        Collection<? extends TableLike<?>> from = parseKeywordIf("FROM") ? parseList(',', t -> {
            return this.scope.scope(parseJoinedTable(() -> {
                return peekKeyword(KEYWORDS_IN_UPDATE_FROM);
            }));
        }) : null;
        parseKeyword("SET");
        if (peek('(')) {
            Row row = parseRow();
            parse('=');
            if (peekSelectOrWith(true)) {
                ((UpdateQueryImpl) ((UpdateImpl) s1).getDelegate()).addValues0(row, parseWithOrSelect(Integer.valueOf(row.size())));
            } else {
                ((UpdateQueryImpl) ((UpdateImpl) s1).getDelegate()).addValues0(row, parseRow(Integer.valueOf(row.size())));
            }
            s2 = (UpdateFromStep) s1;
        } else {
            Map<Field<?>, Object> map = parseSetClauseList();
            s2 = s1.set(map);
        }
        if (from != null) {
            updateWhereStep = s2.from(from);
        } else if (parseKeywordIf("FROM")) {
            updateWhereStep = s2.from(parseList(',', t2 -> {
                return parseJoinedTable(() -> {
                    return peekKeyword(KEYWORDS_IN_UPDATE_FROM);
                });
            }));
        } else {
            updateWhereStep = s2;
        }
        UpdateWhereStep<?> s3 = updateWhereStep;
        if (parseKeywordIf("ALL")) {
            updateOrderByStep = s3;
        } else if (parseKeywordIf("WHERE")) {
            updateOrderByStep = s3.where(parseCondition());
        } else {
            updateOrderByStep = s3;
        }
        UpdateOrderByStep<?> s4 = updateOrderByStep;
        UpdateLimitStep orderBy = parseKeywordIf("ORDER BY") ? s4.orderBy(parseList(',', c -> {
            return c.parseSortField();
        })) : s4;
        if (field != null || parseKeywordIf("LIMIT")) {
            limit = orderBy.limit((Field<? extends Number>) (field != null ? field : parseField()));
        } else {
            limit = orderBy;
        }
        UpdateReturningStep<?> s6 = limit;
        if (!parseResultQuery ? parseKeywordIf("RETURNING") : parseKeyword("RETURNING")) {
            return s6.returning(parseSelectList());
        }
        return s6;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final Map<Field<?>, Object> parseSetClauseList() {
        Map<Field<?>, Object> map = new LinkedHashMap<>();
        do {
            Field<?> field = parseFieldName();
            if (map.containsKey(field)) {
                throw exception("Duplicate column in set clause list: " + String.valueOf(field));
            }
            parse('=');
            Field<?> value = parseKeywordIf("DEFAULT") ? DSL.default_() : parseField();
            map.put(field, value);
        } while (parseIf(','));
        return map;
    }

    private final Merge<?> parseMerge(WithImpl with) {
        MergeMatchedStep<?> whenMatchedThenDelete;
        Merge<?> s3;
        parseKeyword("MERGE");
        parseKeywordIf("INTO");
        Table<?> target = parseTableName();
        if (parseKeywordIf("AS") || !peekKeyword("USING")) {
            target = target.as(parseIdentifier());
        }
        parseKeyword("USING");
        SelectQueryImpl<Record> selectQueryImpl = null;
        Select<?> using = null;
        if (parseIf('(')) {
            using = parseSelect();
            parse(')');
        } else {
            selectQueryImpl = parseTableName();
        }
        TableLike<?> usingTable = parseCorrelationNameIf(selectQueryImpl != null ? selectQueryImpl : using, () -> {
            return peekKeyword("ON");
        });
        parseKeyword("ON");
        Condition on = parseCondition();
        boolean update = false;
        boolean insert = false;
        List<Field<?>> insertColumns = null;
        List<Field<?>> insertValues = null;
        Condition insertWhere = null;
        Condition updateAnd = null;
        Condition updateWhere = null;
        Condition deleteWhere = null;
        MergeUsingStep<?> s1 = with == null ? this.dsl.mergeInto(target) : with.mergeInto(target);
        MergeMatchedStep<?> s2 = s1.using(usingTable).on(on);
        while (true) {
            if (parseKeywordIf("WHEN MATCHED")) {
                update = true;
                if (parseKeywordIf("AND")) {
                    updateAnd = parseCondition();
                }
                if (parseKeywordIf("THEN DELETE")) {
                    if (updateAnd != null) {
                        whenMatchedThenDelete = s2.whenMatchedAnd(updateAnd).thenDelete();
                    } else {
                        whenMatchedThenDelete = s2.whenMatchedThenDelete();
                    }
                    s2 = whenMatchedThenDelete;
                } else {
                    parseKeyword("THEN UPDATE SET");
                    Map<Field<?>, Object> updateSet = parseSetClauseList();
                    if (updateAnd == null && parseKeywordIf("WHERE")) {
                        updateWhere = parseCondition();
                    }
                    if (updateAnd == null && parseKeywordIf("DELETE WHERE")) {
                        deleteWhere = parseCondition();
                    }
                    if (updateAnd != null) {
                        s2.whenMatchedAnd(updateAnd).thenUpdate().set(updateSet);
                    } else {
                        MergeMatchedWhereStep<?> s32 = s2.whenMatchedThenUpdate().set(updateSet);
                        MergeMatchedDeleteStep<?> s4 = updateWhere != null ? s32.where(updateWhere) : s32;
                        s2 = deleteWhere != null ? s4.deleteWhere(deleteWhere) : s32;
                    }
                }
            } else if (!insert) {
                boolean parseKeywordIf = parseKeywordIf("WHEN NOT MATCHED");
                insert = parseKeywordIf;
                if (!parseKeywordIf) {
                    break;
                }
                if (parseKeywordIf("AND")) {
                    insertWhere = parseCondition();
                }
                parseKeyword("THEN INSERT");
                parse('(');
                insertColumns = Tools.fieldsByName((Name[]) parseIdentifiers().toArray(Tools.EMPTY_NAME));
                parse(')');
                parseKeyword("VALUES");
                parse('(');
                insertValues = parseList(',', c -> {
                    return c.parseKeywordIf("DEFAULT") ? DSL.default_() : c.parseField();
                });
                parse(')');
                if (insertColumns.size() != insertValues.size()) {
                    throw exception("Insert column size (" + insertColumns.size() + ") must match values size (" + insertValues.size() + ")");
                }
                if (insertWhere == null && parseKeywordIf("WHERE")) {
                    insertWhere = parseCondition();
                }
            } else {
                break;
            }
        }
        if (!update && !insert) {
            throw exception("At least one of UPDATE or INSERT clauses is required");
        }
        if (insert) {
            if (insertWhere != null) {
                s3 = s2.whenNotMatchedThenInsert(insertColumns).values(insertValues).where(insertWhere);
            } else {
                s3 = s2.whenNotMatchedThenInsert(insertColumns).values(insertValues);
            }
        } else {
            s3 = s2;
        }
        return s3;
    }

    private final Query parseOpen() {
        parseKeyword("OPEN");
        parseKeyword("SCHEMA");
        return parseSetSchema();
    }

    private final Query parseSet() {
        parseKeyword("SET");
        if (parseKeywordIf("CATALOG")) {
            return parseSetCatalog();
        }
        if (parseKeywordIf("CURRENT SCHEMA")) {
            return parseSetSchema();
        }
        if (parseKeywordIf("CURRENT SQLID")) {
            return parseSetSchema();
        }
        if (parseKeywordIf("GENERATOR")) {
            return parseSetGenerator();
        }
        if (parseKeywordIf("SCHEMA")) {
            return parseSetSchema();
        }
        if (parseKeywordIf("SEARCH_PATH")) {
            return parseSetSearchPath();
        }
        return parseSetCommand();
    }

    private final Query parseSetCommand() {
        if (Boolean.TRUE.equals(settings().isParseSetCommands())) {
            Name name = parseIdentifier();
            parseIf('=');
            Object value = parseSignedIntegerLiteralIf();
            return this.dsl.set(name, value != null ? DSL.inline(value) : DSL.inline(parseStringLiteral()));
        }
        parseUntilEOL();
        return IGNORE_NO_DELIMITER.get();
    }

    private final Query parseSetCatalog() {
        return this.dsl.setCatalog(parseCatalogName());
    }

    private final Query parseUse() {
        parseKeyword("USE");
        parseKeywordIf("DATABASE");
        return this.dsl.setCatalog(parseCatalogName());
    }

    private final Query parseSetSchema() {
        parseIf('=');
        return peek('\'') ? this.dsl.setSchema(parseStringLiteral()) : this.dsl.setSchema(parseSchemaName());
    }

    private final Query parseSetSearchPath() {
        if (!parseIf('=')) {
            parseKeyword("TO");
        }
        Schema schema = null;
        do {
            Schema s = parseSchemaName();
            if (schema == null) {
                schema = s;
            }
        } while (parseIf(','));
        return this.dsl.setSchema(schema);
    }

    private final DDLQuery parseCommentOn() {
        CommentOnIsStep s1;
        parseKeyword("COMMENT ON");
        if (parseKeywordIf("COLUMN")) {
            s1 = this.dsl.commentOnColumn(parseFieldName());
        } else if (parseKeywordIf("TABLE")) {
            Table<?> table = parseTableName();
            if (parseIf('(')) {
                CommentOnIsStep s12 = this.dsl.commentOnColumn(table.getQualifiedName().append(parseIdentifier()));
                parseKeyword("IS");
                DDLQuery s2 = s12.is(parseStringLiteral());
                parse(')');
                return s2;
            }
            s1 = this.dsl.commentOnTable(table);
        } else if (parseKeywordIf("VIEW")) {
            s1 = this.dsl.commentOnView(parseTableName());
        } else if (parseKeywordIf("MATERIALIZED VIEW")) {
            s1 = this.dsl.commentOnMaterializedView(parseTableName());
        } else {
            if (parseAndGetKeywordIf("ACCESS METHOD", "AUDIT POLICY", "COLLATION", "CONVERSION", "DATABASE", "DOMAIN", "EDITION", "EXTENSION", "EVENT TRIGGER", "FOREIGN DATA WRAPPER", "FOREIGN TABLE", "INDEX", "INDEXTYPE", "LANGUAGE", "LARGE OBJECT", "MINING MODEL", "OPERATOR", "PROCEDURAL LANGUAGE", "PUBLICATION", "ROLE", "SCHEMA", "SEQUENCE", "SERVER", "STATISTICS", "SUBSCRIPTION", "TABLESPACE", "TEXT SEARCH CONFIGURATION", "TEXT SEARCH DICTIONARY", "TEXT SEARCH PARSER", "TEXT SEARCH TEMPLATE", "TYPE", "VIEW") != null) {
                parseIdentifier();
                parseKeyword("IS");
                parseStringLiteral();
                return IGNORE.get();
            }
            if (parseKeywordIf("CONSTRAINT")) {
                parseIdentifier();
                parseKeyword("ON");
                parseKeywordIf("DOMAIN");
                parseName();
                parseKeyword("IS");
                parseStringLiteral();
                return IGNORE.get();
            }
            if (parseAndGetKeywordIf("POLICY", "RULE", "TRIGGER") != null) {
                parseIdentifier();
                parseKeyword("ON");
                parseIdentifier();
                parseKeyword("IS");
                parseStringLiteral();
                return IGNORE.get();
            }
            if (parseKeywordIf("TRANSFORM FOR")) {
                parseIdentifier();
                parseKeyword("LANGUAGE");
                parseIdentifier();
                parseKeyword("IS");
                parseStringLiteral();
                return IGNORE.get();
            }
            throw unsupportedClause();
        }
        parseKeyword("IS");
        return s1.is(parseStringLiteral());
    }

    private final DDLQuery parseCreate() {
        parseKeyword("CREATE");
        switch (characterUpper()) {
            case 'C':
                if (parseKeywordIf("CACHED TABLE")) {
                    return parseCreateTable(false);
                }
                break;
            case 'D':
                if (parseKeywordIf("DATABASE")) {
                    return parseCreateDatabase();
                }
                if (parseKeywordIf("DOMAIN")) {
                    return parseCreateDomain();
                }
                break;
            case TypeReference.CONSTRUCTOR_REFERENCE /* 69 */:
                if (parseKeywordIf("EXTENSION")) {
                    return parseCreateExtension();
                }
                break;
            case 'F':
                if (parseKeywordIf("FORCE VIEW")) {
                    return parseCreateView(false, false);
                }
                if (parseKeywordIf("FORCE MATERIALIZED VIEW")) {
                    return parseCreateView(false, true);
                }
                if (parseKeywordIf("FULLTEXT INDEX") && requireUnsupportedSyntax()) {
                    return parseCreateIndex(false);
                }
                if (ignoreProEdition() || !parseKeywordIf("FUNCTION") || requireProEdition()) {
                }
                break;
            case TypeReference.CAST /* 71 */:
                if (parseKeywordIf("GENERATOR")) {
                    return parseCreateSequence();
                }
                if (parseKeywordIf("GLOBAL TEMP TABLE", "GLOBAL TEMPORARY TABLE")) {
                    return parseCreateTable(true);
                }
                break;
            case 'I':
                if (parseKeywordIf("INDEX")) {
                    return parseCreateIndex(false);
                }
                break;
            case 'M':
                if (parseKeywordIf("MEMORY TABLE")) {
                    return parseCreateTable(false);
                }
                if (parseKeywordIf("MATERIALIZED VIEW")) {
                    return parseCreateView(false, true);
                }
                break;
            case Opcodes.IASTORE /* 79 */:
                if (parseKeywordIf("OR")) {
                    parseKeyword("REPLACE", "ALTER");
                    if (ignoreProEdition() || !parseKeywordIf("TRIGGER") || !requireProEdition()) {
                        if (parseKeywordIf("VIEW", "FORCE VIEW")) {
                            return parseCreateView(true, false);
                        }
                        if (parseKeywordIf("MATERIALIZED VIEW")) {
                            return parseCreateView(true, true);
                        }
                        if (ignoreProEdition() || !parseKeywordIf("FUNCTION") || !requireProEdition()) {
                            if (parseKeywordIf("PACKAGE")) {
                                throw notImplemented("CREATE PACKAGE", "https://github.com/jOOQ/jOOQ/issues/9190");
                            }
                            if (ignoreProEdition() || !parseKeywordIf("PROC", "PROCEDURE") || !requireProEdition()) {
                                throw expected("FUNCTION", "PACKAGE", "PROCEDURE", "TRIGGER", "VIEW");
                            }
                        }
                    }
                }
                break;
            case 'P':
                if (parseKeywordIf("PACKAGE")) {
                    throw notImplemented("CREATE PACKAGE", "https://github.com/jOOQ/jOOQ/issues/9190");
                }
                if (ignoreProEdition() || !parseKeywordIf("PROC", "PROCEDURE") || requireProEdition()) {
                }
                break;
            case Opcodes.DASTORE /* 82 */:
                if (parseKeywordIf("ROLE")) {
                    throw notImplemented("CREATE ROLE", "https://github.com/jOOQ/jOOQ/issues/10167");
                }
                break;
            case 'S':
                if (parseKeywordIf("SCHEMA")) {
                    return parseCreateSchema();
                }
                if (parseKeywordIf("SEQUENCE")) {
                    return parseCreateSequence();
                }
                if (parseKeywordIf("SPATIAL INDEX") && requireUnsupportedSyntax()) {
                    return parseCreateIndex(false);
                }
                if (parseKeywordIf("SYNONYM")) {
                    throw notImplemented("CREATE SYNONYM", "https://github.com/jOOQ/jOOQ/issues/9574");
                }
                break;
            case Opcodes.BASTORE /* 84 */:
                if (parseKeywordIf("TABLE")) {
                    return parseCreateTable(false);
                }
                if (parseKeywordIf("TEMP TABLE", "TEMPORARY TABLE")) {
                    return parseCreateTable(true);
                }
                if (ignoreProEdition() || !parseKeywordIf("TRIGGER") || !requireProEdition()) {
                    if (parseKeywordIf("TYPE")) {
                        return parseCreateType();
                    }
                    if (parseKeywordIf("TABLESPACE")) {
                        throw notImplemented("CREATE TABLESPACE");
                    }
                }
                break;
            case Opcodes.CASTORE /* 85 */:
                if (parseKeywordIf("UNIQUE INDEX")) {
                    return parseCreateIndex(true);
                }
                if (parseKeywordIf("USER")) {
                    throw notImplemented("CREATE USER", "https://github.com/jOOQ/jOOQ/issues/10167");
                }
                break;
            case Opcodes.SASTORE /* 86 */:
                if (parseKeywordIf("VIEW")) {
                    return parseCreateView(false, false);
                }
                if (parseKeywordIf("VIRTUAL") && parseKeyword("TABLE")) {
                    return parseCreateTable(false);
                }
                break;
        }
        throw expected("FUNCTION", "GENERATOR", "GLOBAL TEMPORARY TABLE", "INDEX", "OR ALTER", "OR REPLACE", "PROCEDURE", "SCHEMA", "SEQUENCE", "TABLE", "TEMPORARY TABLE", "TRIGGER", "TYPE", "UNIQUE INDEX", "VIEW");
    }

    private final Query parseAlter() {
        parseKeyword("ALTER");
        switch (characterUpper()) {
            case 'D':
                if (parseKeywordIf("DATABASE")) {
                    return parseAlterDatabase();
                }
                if (parseKeywordIf("DOMAIN")) {
                    return parseAlterDomain();
                }
                break;
            case TypeReference.CONSTRUCTOR_REFERENCE /* 69 */:
                if (parseKeywordIf("EXTENSION")) {
                    throw notImplemented("ALTER EXTENSION");
                }
                break;
            case 'F':
                if (parseKeywordIf("FUNCTION")) {
                    throw notImplemented("ALTER FUNCTION", "https://github.com/jOOQ/jOOQ/issues/9190");
                }
                break;
            case 'I':
                if (parseKeywordIf("INDEX")) {
                    return parseAlterIndex();
                }
                break;
            case 'M':
                if (parseKeywordIf("MATERIALIZED VIEW")) {
                    return parseAlterView(true);
                }
                break;
            case 'P':
                if (parseKeywordIf("PACKAGE")) {
                    throw notImplemented("ALTER PACKAGE", "https://github.com/jOOQ/jOOQ/issues/9190");
                }
                if (parseKeywordIf("PROCEDURE")) {
                    throw notImplemented("ALTER PROCEDURE", "https://github.com/jOOQ/jOOQ/issues/9190");
                }
                break;
            case Opcodes.DASTORE /* 82 */:
                if (parseKeywordIf("ROLE")) {
                    throw notImplemented("ALTER ROLE", "https://github.com/jOOQ/jOOQ/issues/10167");
                }
                break;
            case 'S':
                if (parseKeywordIf("SCHEMA")) {
                    return parseAlterSchema();
                }
                if (parseKeywordIf("SEQUENCE")) {
                    return parseAlterSequence();
                }
                if (parseKeywordIf(HeaderWebSessionIdResolver.DEFAULT_HEADER_NAME)) {
                    return parseAlterSession();
                }
                if (parseKeywordIf("SYNONYM")) {
                    throw notImplemented("ALTER SYNONYM", "https://github.com/jOOQ/jOOQ/issues/9574");
                }
                break;
            case Opcodes.BASTORE /* 84 */:
                if (parseKeywordIf("TABLE")) {
                    return parseAlterTable();
                }
                if (parseKeywordIf("TYPE")) {
                    return parseAlterType();
                }
                if (parseKeywordIf("TABLESPACE")) {
                    throw notImplemented("ALTER TABLESPACE");
                }
                if (parseKeywordIf("TRIGGER")) {
                    throw notImplemented("ALTER TRIGGER", "https://github.com/jOOQ/jOOQ/issues/6956");
                }
                break;
            case Opcodes.CASTORE /* 85 */:
                if (parseKeywordIf("USER")) {
                    throw notImplemented("ALTER USER", "https://github.com/jOOQ/jOOQ/issues/10167");
                }
                break;
            case Opcodes.SASTORE /* 86 */:
                if (parseKeywordIf("VIEW")) {
                    return parseAlterView(false);
                }
                break;
        }
        throw expected("DOMAIN", "INDEX", "SCHEMA", "SEQUENCE", HeaderWebSessionIdResolver.DEFAULT_HEADER_NAME, "TABLE", "TYPE", "VIEW");
    }

    private final DDLQuery parseDrop() {
        parseKeyword("DROP");
        switch (characterUpper()) {
            case 'D':
                if (parseKeywordIf("DATABASE")) {
                    return parseDropDatabase();
                }
                if (parseKeywordIf("DOMAIN")) {
                    Supplier supplier = this::parseDomainName;
                    DSLContext dSLContext = this.dsl;
                    Objects.requireNonNull(dSLContext);
                    java.util.function.Function function = dSLContext::dropDomainIfExists;
                    DSLContext dSLContext2 = this.dsl;
                    Objects.requireNonNull(dSLContext2);
                    return (DDLQuery) parseCascadeRestrictIf((DropDomainCascadeStep) parseIfExists(supplier, function, dSLContext2::dropDomain), (v0) -> {
                        return v0.cascade();
                    }, (v0) -> {
                        return v0.restrict();
                    });
                }
                break;
            case TypeReference.CONSTRUCTOR_REFERENCE /* 69 */:
                if (parseKeywordIf("EXTENSION")) {
                    return parseDropExtension();
                }
                break;
            case 'F':
                if (ignoreProEdition() || !parseKeywordIf("FUNCTION") || requireProEdition()) {
                }
                break;
            case TypeReference.CAST /* 71 */:
                if (parseKeywordIf("GENERATOR")) {
                    return parseDropSequence();
                }
                break;
            case 'I':
                if (parseKeywordIf("INDEX")) {
                    return parseDropIndex();
                }
                break;
            case 'M':
                if (parseKeywordIf("MATERIALIZED VIEW")) {
                    return parseDropView(true);
                }
                break;
            case 'P':
                if (parseKeywordIf("PACKAGE")) {
                    throw notImplemented("DROP PACKAGE", "https://github.com/jOOQ/jOOQ/issues/9190");
                }
                if (ignoreProEdition() || !parseKeywordIf("PROC", "PROCEDURE") || requireProEdition()) {
                }
                break;
            case Opcodes.DASTORE /* 82 */:
                if (parseKeywordIf("ROLE")) {
                    throw notImplemented("DROP ROLE", "https://github.com/jOOQ/jOOQ/issues/10167");
                }
                break;
            case 'S':
                if (parseKeywordIf("SEQUENCE")) {
                    return parseDropSequence();
                }
                if (parseKeywordIf("SCHEMA")) {
                    Supplier supplier2 = this::parseSchemaName;
                    DSLContext dSLContext3 = this.dsl;
                    Objects.requireNonNull(dSLContext3);
                    java.util.function.Function function2 = dSLContext3::dropSchemaIfExists;
                    DSLContext dSLContext4 = this.dsl;
                    Objects.requireNonNull(dSLContext4);
                    return (DDLQuery) parseCascadeRestrictIf((DropSchemaStep) parseIfExists(supplier2, function2, dSLContext4::dropSchema), (v0) -> {
                        return v0.cascade();
                    }, (v0) -> {
                        return v0.restrict();
                    });
                }
                break;
            case Opcodes.BASTORE /* 84 */:
                if (parseKeywordIf("TABLE")) {
                    return parseDropTable(false);
                }
                if (parseKeywordIf("TEMPORARY TABLE")) {
                    return parseDropTable(true);
                }
                if (ignoreProEdition() || !parseKeywordIf("TRIGGER") || !requireProEdition()) {
                    if (parseKeywordIf("TYPE")) {
                        return (DDLQuery) parseCascadeRestrictIf((DropTypeStep) parseIfExists(this::parseIdentifiers, n -> {
                            return this.dsl.dropTypeIfExists((Name[]) n.toArray(Tools.EMPTY_NAME));
                        }, n2 -> {
                            return this.dsl.dropType((Name[]) n2.toArray(Tools.EMPTY_NAME));
                        }), (v0) -> {
                            return v0.cascade();
                        }, (v0) -> {
                            return v0.restrict();
                        });
                    }
                    if (parseKeywordIf("TABLESPACE")) {
                        throw notImplemented("DROP TABLESPACE");
                    }
                }
                break;
            case Opcodes.CASTORE /* 85 */:
                if (parseKeywordIf("USER")) {
                    throw notImplemented("DROP USER", "https://github.com/jOOQ/jOOQ/issues/10167");
                }
                break;
            case Opcodes.SASTORE /* 86 */:
                if (parseKeywordIf("VIEW")) {
                    return parseDropView(false);
                }
                break;
        }
        throw expected("GENERATOR", "FUNCTION", "INDEX", "PROCEDURE", "SCHEMA", "SEQUENCE", "TABLE", "TEMPORARY TABLE", "TRIGGER", "TYPE", "VIEW");
    }

    private final Truncate<?> parseTruncate() {
        TruncateCascadeStep<?> truncateCascadeStep;
        parseKeyword("TRUNCATE");
        parseKeywordIf("TABLE");
        List<Table<?>> table = parseList(',', ctx -> {
            return parseTableName();
        });
        boolean continueIdentity = parseKeywordIf("CONTINUE IDENTITY");
        boolean restartIdentity = !continueIdentity && parseKeywordIf("RESTART IDENTITY");
        boolean cascade = parseKeywordIf("CASCADE");
        boolean restrict = !cascade && parseKeywordIf("RESTRICT");
        TruncateIdentityStep<?> step1 = this.dsl.truncate(table);
        if (continueIdentity) {
            truncateCascadeStep = step1.continueIdentity();
        } else if (restartIdentity) {
            truncateCascadeStep = step1.restartIdentity();
        } else {
            truncateCascadeStep = step1;
        }
        TruncateCascadeStep<?> step2 = truncateCascadeStep;
        if (cascade) {
            return step2.cascade();
        }
        if (restrict) {
            return step2.restrict();
        }
        return step2;
    }

    private final DDLQuery parseGrant() {
        parseKeyword("GRANT");
        Privilege privilege = parsePrivilege();
        List<Privilege> privileges = null;
        while (parseIf(',')) {
            if (privileges == null) {
                privileges = new ArrayList<>();
                privileges.add(privilege);
            }
            privileges.add(parsePrivilege());
        }
        parseKeyword("ON");
        parseKeywordIf("TABLE");
        Table<?> table = parseTableName();
        parseKeyword("TO");
        User user = parseKeywordIf("PUBLIC") ? null : parseUser();
        GrantOnStep s1 = privileges == null ? this.dsl.grant(privilege) : this.dsl.grant(privileges);
        GrantToStep s2 = s1.on(table);
        GrantWithGrantOptionStep s3 = user == null ? s2.toPublic() : s2.to(user);
        if (parseKeywordIf("WITH GRANT OPTION")) {
            return s3.withGrantOption();
        }
        return s3;
    }

    private final DDLQuery parseRevoke() {
        RevokeOnStep revoke;
        parseKeyword("REVOKE");
        boolean grantOptionFor = parseKeywordIf("GRANT OPTION FOR");
        Privilege privilege = parsePrivilege();
        List<Privilege> privileges = null;
        while (parseIf(',')) {
            if (privileges == null) {
                privileges = new ArrayList<>();
                privileges.add(privilege);
            }
            privileges.add(parsePrivilege());
        }
        parseKeyword("ON");
        parseKeywordIf("TABLE");
        Table<?> table = parseTableName();
        if (grantOptionFor) {
            if (privileges == null) {
                revoke = this.dsl.revokeGrantOptionFor(privilege);
            } else {
                revoke = this.dsl.revokeGrantOptionFor(privileges);
            }
        } else if (privileges == null) {
            revoke = this.dsl.revoke(privilege);
        } else {
            revoke = this.dsl.revoke(privileges);
        }
        RevokeOnStep s1 = revoke;
        parseKeyword("FROM");
        User user = parseKeywordIf("PUBLIC") ? null : parseUser();
        RevokeFromStep s2 = s1.on(table);
        return user == null ? s2.fromPublic() : s2.from(user);
    }

    private final Query parseExec() {
        if (parseKeywordIf("EXEC SP_RENAME")) {
            if (parseKeywordIf("@OBJNAME")) {
                parse('=');
            }
            Name oldName = this.dsl.parser().parseName(parseStringLiteral());
            parse(',');
            if (parseKeywordIf("@NEWNAME")) {
                parse('=');
            }
            Name newName = this.dsl.parser().parseName(parseStringLiteral());
            String objectType = "TABLE";
            if (parseIf(',')) {
                if (parseKeywordIf("@OBJTYPE")) {
                    parse('=');
                }
                if (!parseKeywordIf(JoranConstants.NULL)) {
                    objectType = parseStringLiteral();
                }
            }
            if ("TABLE".equalsIgnoreCase(objectType)) {
                return this.dsl.alterTable(oldName).renameTo(newName.unqualifiedName());
            }
            if ("INDEX".equalsIgnoreCase(objectType)) {
                return this.dsl.alterIndex(oldName).renameTo(newName.unqualifiedName());
            }
            if ("COLUMN".equalsIgnoreCase(objectType)) {
                return this.dsl.alterTable(oldName.qualifier()).renameColumn(oldName.unqualifiedName()).to(newName.unqualifiedName());
            }
            throw exception("Unsupported object type: " + objectType);
        }
        if (ignoreProEdition() || !requireProEdition()) {
        }
        throw unsupportedClause();
    }

    private final Block parseBlock(boolean allowDeclareSection) {
        LanguageContext previous = this.languageContext;
        try {
            if (this.languageContext == LanguageContext.QUERY) {
                this.languageContext = LanguageContext.BLOCK;
            }
            List<Statement> statements = new ArrayList<>();
            if (!allowDeclareSection || ignoreProEdition() || !parseKeywordIf("DECLARE") || !requireProEdition()) {
                parseKeywordIf("EXECUTE BLOCK AS");
            }
            parseKeyword("BEGIN");
            parseKeywordIf("ATOMIC", "NOT ATOMIC");
            statements.addAll(parseStatementsAndPeek("END"));
            parseKeyword("END");
            parseIf(';');
            Block begin = this.dsl.begin(statements);
            this.languageContext = previous;
            return begin;
        } catch (Throwable th) {
            this.languageContext = previous;
            throw th;
        }
    }

    private final void parseSemicolonAfterNonBlocks(Statement result) {
        if (!(result instanceof Block)) {
            parseIf(';');
        } else if ((result instanceof BlockImpl) && !((BlockImpl) result).alwaysWrapInBeginEnd) {
            parseIf(';');
        }
    }

    private final Statement parseStatementAndSemicolon() {
        Statement result = parseStatementAndSemicolonIf();
        if (result == null) {
            throw expected("Statement");
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Statement parseStatementAndSemicolonIf() {
        Statement result = parseStatement();
        parseSemicolonAfterNonBlocks(result);
        return result;
    }

    private final List<Statement> parseStatements(boolean peek, String... keywords) {
        Statement parsed;
        List<Statement> statements = new ArrayList<>();
        while (true) {
            if ((!peek || !peekKeyword(keywords)) && ((peek || !parseKeywordIf(keywords)) && (parsed = parseStatement()) != null)) {
                statements.add(parsed);
                parseSemicolonAfterNonBlocks(parsed);
            }
        }
        return statements;
    }

    private final List<Statement> parseStatementsAndPeek(String... keywords) {
        return parseStatements(true, keywords);
    }

    private final List<Statement> parseStatementsAndKeyword(String... keywords) {
        return parseStatements(false, keywords);
    }

    private final Query parseStartTransaction() {
        parseKeyword("START", "BEGIN");
        parseKeyword("WORK", "TRAN", "TRANSACTION");
        parseKeywordIf("READ WRITE");
        return this.dsl.startTransaction();
    }

    private final Query parseSavepoint() {
        if (parseKeywordIf("SAVEPOINT")) {
            Name n = parseIdentifier();
            parseKeywordIf("UNIQUE");
            parseKeywordIf("ON ROLLBACK RETAIN CURSORS");
            return this.dsl.savepoint(n);
        }
        parseKeyword("SAVE");
        parseKeyword("TRAN", "TRANSACTION");
        return this.dsl.savepoint(parseIdentifier());
    }

    private final Query parseReleaseSavepoint() {
        parseKeyword("RELEASE");
        parseKeywordIf("TO");
        parseKeywordIf("SAVEPOINT");
        return this.dsl.releaseSavepoint(parseIdentifier());
    }

    private final Query parseCommit() {
        parseKeyword("COMMIT");
        parseKeywordIf("WORK", "TRAN", "TRANSACTION");
        return this.dsl.commit();
    }

    private final Query parseRollback() {
        parseKeyword("ROLLBACK");
        if (parseKeywordIf("TRAN", "TRANSACTION TO SAVEPOINT", "TRANSACTION TO", "TRANSACTION", "WORK TO SAVEPOINT", "TO SAVEPOINT", "TO")) {
            return this.dsl.rollback().toSavepoint(parseIdentifier());
        }
        parseKeywordIf("WORK");
        return this.dsl.rollback();
    }

    private final Block parseDo() {
        parseKeyword("DO");
        return (Block) this.dsl.parser().parseQuery(parseStringLiteral());
    }

    private final Statement parseStatement() {
        switch (characterUpper()) {
            case 'C':
                if ((ignoreProEdition() || !peekKeyword("CALL") || !requireProEdition()) && !ignoreProEdition() && peekKeyword("CONTINUE") && requireProEdition()) {
                }
                break;
            case 'D':
                if ((ignoreProEdition() || !peekKeyword("DECLARE") || !requireProEdition()) && !ignoreProEdition() && peekKeyword("DEFINE") && requireProEdition()) {
                }
                break;
            case TypeReference.CONSTRUCTOR_REFERENCE /* 69 */:
                if (ignoreProEdition() || !peekKeyword("EXECUTE PROCEDURE", "EXEC") || requireProEdition()) {
                }
                if ((ignoreProEdition() || !peekKeyword("EXECUTE") || peekKeyword("EXECUTE BLOCK") || !requireProEdition()) && !ignoreProEdition() && peekKeyword("EXIT") && requireProEdition()) {
                }
                break;
            case 'F':
                if (ignoreProEdition() || !peekKeyword("FOR") || requireProEdition()) {
                }
                break;
            case TypeReference.CAST /* 71 */:
                if (ignoreProEdition() || !peekKeyword("GOTO") || requireProEdition()) {
                }
                break;
            case 'I':
                if ((ignoreProEdition() || !peekKeyword("IF") || !requireProEdition()) && !ignoreProEdition() && peekKeyword("ITERATE") && requireProEdition()) {
                }
                break;
            case BaseNCodec.MIME_CHUNK_SIZE /* 76 */:
                if ((ignoreProEdition() || !peekKeyword("LEAVE") || !requireProEdition()) && ((!ignoreProEdition() && peekKeyword("LET") && requireProEdition()) || ignoreProEdition() || !peekKeyword("LOOP") || !requireProEdition())) {
                }
                break;
            case 'N':
                if (peekKeyword(JoranConstants.NULL)) {
                    return parseNullStatement();
                }
                break;
            case Opcodes.DASTORE /* 82 */:
                if ((ignoreProEdition() || !peekKeyword("REPEAT") || !requireProEdition()) && ((!ignoreProEdition() && peekKeyword("RETURN") && requireProEdition()) || ignoreProEdition() || !peekKeyword("RAISE") || !requireProEdition())) {
                }
                break;
            case 'S':
                if ((ignoreProEdition() || !peekKeyword("SET") || !requireProEdition()) && !ignoreProEdition() && peekKeyword("SIGNAL") && requireProEdition()) {
                }
                break;
            case Opcodes.POP /* 87 */:
                if (ignoreProEdition() || !peekKeyword("WHILE") || requireProEdition()) {
                }
                break;
        }
        return parseQuery(false, false);
    }

    private final Statement parseNullStatement() {
        parseKeyword(JoranConstants.NULL);
        return new NullStatement();
    }

    private final Privilege parsePrivilege() {
        if (parseKeywordIf("SELECT")) {
            return DSL.privilege(Keywords.K_SELECT);
        }
        if (parseKeywordIf("INSERT")) {
            return DSL.privilege(Keywords.K_INSERT);
        }
        if (parseKeywordIf("UPDATE")) {
            return DSL.privilege(Keywords.K_UPDATE);
        }
        if (parseKeywordIf("DELETE")) {
            return DSL.privilege(Keywords.K_DELETE);
        }
        throw expected("DELETE", "INSERT", "SELECT", "UPDATE");
    }

    private final User parseUser() {
        return DSL.user(parseName());
    }

    private final DDLQuery parseCreateView(boolean orReplace, boolean materialized) {
        CreateViewAsStep<Record> createView;
        boolean ifNotExists = !orReplace && parseKeywordIf("IF NOT EXISTS");
        Table<?> view = parseTableName();
        Field<?>[] fields = Tools.EMPTY_FIELD;
        if (parseIf('(')) {
            fields = (Field[]) parseList(',', c -> {
                return parseFieldName();
            }).toArray(fields);
            parse(')');
        }
        parseKeyword("AS");
        Select<?> select = parseWithOrSelect();
        int degree = Tools.degree(select);
        if (fields.length > 0 && fields.length != degree) {
            throw exception("Select list size (" + degree + ") must match declared field size (" + fields.length + ")");
        }
        if (ifNotExists) {
            if (materialized) {
                createView = this.dsl.createMaterializedViewIfNotExists(view, fields);
            } else {
                createView = this.dsl.createViewIfNotExists(view, fields);
            }
        } else if (orReplace) {
            if (materialized) {
                createView = this.dsl.createOrReplaceMaterializedView(view, fields);
            } else {
                createView = this.dsl.createOrReplaceView(view, fields);
            }
        } else if (materialized) {
            createView = this.dsl.createMaterializedView(view, fields);
        } else {
            createView = this.dsl.createView(view, fields);
        }
        return createView.as(select);
    }

    private final DDLQuery parseCreateExtension() {
        parseKeywordIf("IF NOT EXISTS");
        parseIdentifier();
        parseKeywordIf("WITH");
        if (parseKeywordIf("SCHEMA")) {
            parseIdentifier();
        }
        if (parseKeywordIf("VERSION") && parseIdentifierIf() == null) {
            parseStringLiteral();
        }
        if (parseKeywordIf("FROM") && parseIdentifierIf() == null) {
            parseStringLiteral();
        }
        parseKeywordIf("CASCADE");
        return IGNORE.get();
    }

    private final DDLQuery parseDropExtension() {
        boolean ifExists = parseKeywordIf("IF EXISTS");
        parseIdentifiers();
        boolean z = ifExists || parseKeywordIf("IF EXISTS");
        if (!parseKeywordIf("CASCADE")) {
            parseKeywordIf("RESTRICT");
        }
        return IGNORE.get();
    }

    private final DDLQuery parseAlterView(boolean materialized) {
        AlterViewStep alterView;
        AlterViewStep alterView2;
        boolean ifExists = parseKeywordIf("IF EXISTS");
        Table<?> oldName = parseTableName();
        Field<?>[] fields = Tools.EMPTY_FIELD;
        if (parseIf('(')) {
            fields = (Field[]) parseList(',', c -> {
                return parseFieldName();
            }).toArray(fields);
            parse(')');
        }
        if (parseKeywordIf("AS")) {
            Select<?> select = parseWithOrSelect();
            int degree = Tools.degree(select);
            if (fields.length > 0 && fields.length != degree) {
                throw exception("Select list size (" + degree + ") must match declared field size (" + fields.length + ")");
            }
            if (fields.length == 0) {
                return this.dsl.alterView(oldName).as(select);
            }
            return this.dsl.alterView(oldName, fields).as(select);
        }
        if (fields.length > 0) {
            throw expected("AS");
        }
        if (parseKeywordIf("RENAME")) {
            parseKeyword("AS", "TO");
            Table<?> newName = parseTableName();
            if (ifExists) {
                if (materialized) {
                    alterView2 = this.dsl.alterMaterializedViewIfExists(oldName);
                } else {
                    alterView2 = this.dsl.alterViewIfExists(oldName);
                }
            } else if (materialized) {
                alterView2 = this.dsl.alterMaterializedView(oldName);
            } else {
                alterView2 = this.dsl.alterView(oldName);
            }
            return alterView2.renameTo(newName);
        }
        if (parseKeywordIf("OWNER TO") && parseUser() != null) {
            return IGNORE.get();
        }
        if (parseKeywordIf("SET")) {
            if (materialized) {
                alterView = this.dsl.alterMaterializedView(oldName);
            } else {
                alterView = this.dsl.alterView(oldName);
            }
            return alterView.comment(parseOptionsDescription());
        }
        throw expected("AS", "OWNER TO", "RENAME", "SET");
    }

    private final Comment parseOptionsDescription() {
        parseKeyword("OPTIONS");
        parse('(');
        parseKeyword("DESCRIPTION");
        parse('=');
        Comment comment = parseComment();
        parse(')');
        return comment;
    }

    private final DDLQuery parseDropView(boolean materialized) {
        if (materialized) {
            Supplier supplier = this::parseTableName;
            DSLContext dSLContext = this.dsl;
            Objects.requireNonNull(dSLContext);
            java.util.function.Function function = dSLContext::dropMaterializedViewIfExists;
            DSLContext dSLContext2 = this.dsl;
            Objects.requireNonNull(dSLContext2);
            return (DDLQuery) parseIfExists(supplier, function, dSLContext2::dropMaterializedView);
        }
        Supplier supplier2 = this::parseTableName;
        DSLContext dSLContext3 = this.dsl;
        Objects.requireNonNull(dSLContext3);
        java.util.function.Function function2 = dSLContext3::dropViewIfExists;
        DSLContext dSLContext4 = this.dsl;
        Objects.requireNonNull(dSLContext4);
        return (DDLQuery) parseIfExists(supplier2, function2, dSLContext4::dropView);
    }

    private final DDLQuery parseCreateSequence() {
        CreateSequenceFlagsStep createSequence;
        boolean ifNotExists = parseKeywordIf("IF NOT EXISTS");
        Sequence<?> schemaName = parseSequenceName();
        if (ifNotExists) {
            createSequence = this.dsl.createSequenceIfNotExists(schemaName);
        } else {
            createSequence = this.dsl.createSequence(schemaName);
        }
        CreateSequenceFlagsStep s = createSequence;
        boolean startWith = false;
        boolean incrementBy = false;
        boolean minvalue = false;
        boolean maxvalue = false;
        boolean cycle = false;
        boolean cache = false;
        while (true) {
            if (!startWith) {
                boolean z = startWith;
                Field<Long> field = parseSequenceStartWithIf();
                boolean z2 = z | (field != null);
                startWith = z2;
                if (z2) {
                    s = s.startWith(field);
                }
            }
            if (!incrementBy) {
                boolean z3 = incrementBy;
                Field<Long> field2 = parseSequenceIncrementByIf();
                boolean z4 = z3 | (field2 != null);
                incrementBy = z4;
                if (z4) {
                    s = s.incrementBy(field2);
                }
            }
            if (!minvalue) {
                boolean z5 = minvalue;
                Field<Long> field3 = parseSequenceMinvalueIf();
                boolean z6 = z5 | (field3 != null);
                minvalue = z6;
                if (z6) {
                    s = s.minvalue(field3);
                }
            }
            if (!minvalue) {
                boolean parseSequenceNoMinvalueIf = minvalue | parseSequenceNoMinvalueIf();
                minvalue = parseSequenceNoMinvalueIf;
                if (parseSequenceNoMinvalueIf) {
                    s = s.noMinvalue();
                }
            }
            if (!maxvalue) {
                boolean z7 = maxvalue;
                Field<Long> field4 = parseSequenceMaxvalueIf();
                boolean z8 = z7 | (field4 != null);
                maxvalue = z8;
                if (z8) {
                    s = s.maxvalue(field4);
                }
            }
            if (!maxvalue) {
                boolean parseSequenceNoMaxvalueIf = maxvalue | parseSequenceNoMaxvalueIf();
                maxvalue = parseSequenceNoMaxvalueIf;
                if (parseSequenceNoMaxvalueIf) {
                    s = s.noMaxvalue();
                }
            }
            if (!cycle) {
                boolean parseKeywordIf = cycle | parseKeywordIf("CYCLE");
                cycle = parseKeywordIf;
                if (parseKeywordIf) {
                    s = s.cycle();
                }
            }
            if (!cycle) {
                boolean parseSequenceNoCycleIf = cycle | parseSequenceNoCycleIf();
                cycle = parseSequenceNoCycleIf;
                if (parseSequenceNoCycleIf) {
                    s = s.noCycle();
                }
            }
            if (!cache) {
                boolean z9 = cache;
                Field<Long> field5 = parseSequenceCacheIf();
                boolean z10 = z9 | (field5 != null);
                cache = z10;
                if (z10) {
                    s = s.cache(field5);
                }
            }
            if (!cache) {
                boolean parseSequenceNoCacheIf = cache | parseSequenceNoCacheIf();
                cache = parseSequenceNoCacheIf;
                if (!parseSequenceNoCacheIf) {
                    break;
                }
                s = s.noCache();
            } else {
                break;
            }
        }
        return s;
    }

    private final DDLQuery parseAlterSequence() {
        AlterSequenceStep alterSequence;
        AlterSequenceFlagsStep restart;
        boolean ifExists = parseKeywordIf("IF EXISTS");
        Sequence<?> sequenceName = parseSequenceName();
        if (ifExists) {
            alterSequence = this.dsl.alterSequenceIfExists(sequenceName);
        } else {
            alterSequence = this.dsl.alterSequence(sequenceName);
        }
        AlterSequenceStep s = alterSequence;
        if (parseKeywordIf("RENAME")) {
            parseKeyword("AS", "TO");
            return s.renameTo(parseSequenceName());
        }
        if (parseKeywordIf("OWNER TO") && parseUser() != null) {
            return IGNORE.get();
        }
        boolean found = false;
        boolean restart2 = false;
        boolean startWith = false;
        boolean incrementBy = false;
        boolean minvalue = false;
        boolean maxvalue = false;
        boolean cycle = false;
        boolean cache = false;
        AlterSequenceFlagsStep s1 = s;
        while (true) {
            if (!startWith) {
                boolean z = startWith;
                Field<Long> field = parseSequenceStartWithIf();
                boolean z2 = z | (field != null);
                startWith = z2;
                if (z2) {
                    restart = s1.startWith(field);
                    s1 = restart;
                    found = true;
                }
            }
            if (!incrementBy) {
                boolean z3 = incrementBy;
                Field<Long> field2 = parseSequenceIncrementByIf();
                boolean z4 = z3 | (field2 != null);
                incrementBy = z4;
                if (z4) {
                    restart = s1.incrementBy(field2);
                    s1 = restart;
                    found = true;
                }
            }
            if (!minvalue) {
                boolean z5 = minvalue;
                Field<Long> field3 = parseSequenceMinvalueIf();
                boolean z6 = z5 | (field3 != null);
                minvalue = z6;
                if (z6) {
                    restart = s1.minvalue(field3);
                    s1 = restart;
                    found = true;
                }
            }
            if (!minvalue) {
                boolean parseSequenceNoMinvalueIf = minvalue | parseSequenceNoMinvalueIf();
                minvalue = parseSequenceNoMinvalueIf;
                if (parseSequenceNoMinvalueIf) {
                    restart = s1.noMinvalue();
                    s1 = restart;
                    found = true;
                }
            }
            if (!maxvalue) {
                boolean z7 = maxvalue;
                Field<Long> field4 = parseSequenceMaxvalueIf();
                boolean z8 = z7 | (field4 != null);
                maxvalue = z8;
                if (z8) {
                    restart = s1.maxvalue(field4);
                    s1 = restart;
                    found = true;
                }
            }
            if (!maxvalue) {
                boolean parseSequenceNoMaxvalueIf = maxvalue | parseSequenceNoMaxvalueIf();
                maxvalue = parseSequenceNoMaxvalueIf;
                if (parseSequenceNoMaxvalueIf) {
                    restart = s1.noMaxvalue();
                    s1 = restart;
                    found = true;
                }
            }
            if (!cycle) {
                boolean parseKeywordIf = cycle | parseKeywordIf("CYCLE");
                cycle = parseKeywordIf;
                if (parseKeywordIf) {
                    restart = s1.cycle();
                    s1 = restart;
                    found = true;
                }
            }
            if (!cycle) {
                boolean parseSequenceNoCycleIf = cycle | parseSequenceNoCycleIf();
                cycle = parseSequenceNoCycleIf;
                if (parseSequenceNoCycleIf) {
                    restart = s1.noCycle();
                    s1 = restart;
                    found = true;
                }
            }
            if (!cache) {
                boolean z9 = cache;
                Field<Long> field5 = parseSequenceCacheIf();
                boolean z10 = z9 | (field5 != null);
                cache = z10;
                if (z10) {
                    restart = s1.cache(field5);
                    s1 = restart;
                    found = true;
                }
            }
            if (!cache) {
                boolean parseSequenceNoCacheIf = cache | parseSequenceNoCacheIf();
                cache = parseSequenceNoCacheIf;
                if (parseSequenceNoCacheIf) {
                    restart = s1.noCache();
                    s1 = restart;
                    found = true;
                }
            }
            if (!restart2) {
                boolean parseKeywordIf2 = restart2 | parseKeywordIf("RESTART");
                restart2 = parseKeywordIf2;
                if (!parseKeywordIf2) {
                    break;
                }
                if (parseKeywordIf("WITH")) {
                    restart = s1.restartWith(parseUnsignedIntegerOrBindVariable());
                } else {
                    restart = s1.restart();
                }
                s1 = restart;
                found = true;
            } else {
                break;
            }
        }
        if (!found) {
            throw expected("CACHE", "CYCLE", "INCREMENT BY", "MAXVALUE", "MINVALUE", "NO CACHE", "NO CYCLE", "NO MAXVALUE", "NO MINVALUE", "OWNER TO", "RENAME TO", "RESTART", "START WITH");
        }
        return s1;
    }

    private final boolean parseSequenceNoCacheIf() {
        return parseKeywordIf("NO CACHE", "NOCACHE");
    }

    private final Field<Long> parseSequenceCacheIf() {
        if (!parseKeywordIf("CACHE")) {
            return null;
        }
        if (!parseIf("=")) {
        }
        return parseUnsignedIntegerOrBindVariable();
    }

    private final boolean parseSequenceNoCycleIf() {
        return parseKeywordIf("NO CYCLE", "NOCYCLE");
    }

    private final boolean parseSequenceNoMaxvalueIf() {
        return parseKeywordIf("NO MAXVALUE", "NOMAXVALUE");
    }

    private final Field<Long> parseSequenceMaxvalueIf() {
        if (!parseKeywordIf("MAXVALUE")) {
            return null;
        }
        if (!parseIf("=")) {
        }
        return parseUnsignedIntegerOrBindVariable();
    }

    private final boolean parseSequenceNoMinvalueIf() {
        return parseKeywordIf("NO MINVALUE", "NOMINVALUE");
    }

    private final Field<Long> parseSequenceMinvalueIf() {
        if (!parseKeywordIf("MINVALUE")) {
            return null;
        }
        if (!parseIf("=")) {
        }
        return parseUnsignedIntegerOrBindVariable();
    }

    private final Field<Long> parseSequenceIncrementByIf() {
        if (!parseKeywordIf("INCREMENT")) {
            return null;
        }
        if (parseKeywordIf("BY") || !parseIf("=")) {
        }
        return parseUnsignedIntegerOrBindVariable();
    }

    private final Field<Long> parseSequenceStartWithIf() {
        if (!parseKeywordIf("START")) {
            return null;
        }
        if (parseKeywordIf("WITH") || !parseIf("=")) {
        }
        return parseUnsignedIntegerOrBindVariable();
    }

    private final Query parseAlterSession() {
        parseKeyword("SET CURRENT_SCHEMA");
        parse('=');
        return this.dsl.setSchema(parseSchemaName());
    }

    private final DDLQuery parseSetGenerator() {
        Sequence<?> sequenceName = parseSequenceName();
        parseKeyword("TO");
        return this.dsl.alterSequence(sequenceName).restartWith((AlterSequenceStep) parseUnsignedIntegerLiteral());
    }

    private final DDLQuery parseDropSequence() {
        boolean ifExists = parseKeywordIf("IF EXISTS");
        Sequence<?> sequenceName = parseSequenceName();
        boolean ifExists2 = ifExists || parseKeywordIf("IF EXISTS");
        parseKeywordIf("RESTRICT");
        if (ifExists2) {
            return this.dsl.dropSequenceIfExists(sequenceName);
        }
        return this.dsl.dropSequence(sequenceName);
    }

    /* JADX WARN: Removed duplicated region for block: B:188:0x0a3b  */
    /* JADX WARN: Removed duplicated region for block: B:190:0x0a49  */
    /* JADX WARN: Removed duplicated region for block: B:205:0x035b  */
    /* JADX WARN: Removed duplicated region for block: B:207:0x0365  */
    /* JADX WARN: Removed duplicated region for block: B:220:0x02b1  */
    /* JADX WARN: Removed duplicated region for block: B:221:0x0297  */
    /* JADX WARN: Removed duplicated region for block: B:223:0x0256  */
    /* JADX WARN: Removed duplicated region for block: B:24:0x0238  */
    /* JADX WARN: Removed duplicated region for block: B:29:0x027d  */
    /* JADX WARN: Removed duplicated region for block: B:32:0x0292  */
    /* JADX WARN: Removed duplicated region for block: B:35:0x02ac  */
    /* JADX WARN: Removed duplicated region for block: B:66:0x03f4  */
    /* JADX WARN: Removed duplicated region for block: B:69:0x041f  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private final org.jooq.DDLQuery parseCreateTable(boolean r12) {
        /*
            Method dump skipped, instructions count: 2695
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jooq.impl.DefaultParseContext.parseCreateTable(boolean):org.jooq.DDLQuery");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: ParserImpl.java */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultParseContext$ParseInlineConstraints.class */
    public static final class ParseInlineConstraints extends Record {
        private final DataType<?> type;
        private final Comment fieldComment;
        private final boolean primary;
        private final boolean identity;
        private final boolean readonly;

        private ParseInlineConstraints(DataType<?> type, Comment fieldComment, boolean primary, boolean identity, boolean readonly) {
            this.type = type;
            this.fieldComment = fieldComment;
            this.primary = primary;
            this.identity = identity;
            this.readonly = readonly;
        }

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, ParseInlineConstraints.class), ParseInlineConstraints.class, "type;fieldComment;primary;identity;readonly", "FIELD:Lorg/jooq/impl/DefaultParseContext$ParseInlineConstraints;->type:Lorg/jooq/DataType;", "FIELD:Lorg/jooq/impl/DefaultParseContext$ParseInlineConstraints;->fieldComment:Lorg/jooq/Comment;", "FIELD:Lorg/jooq/impl/DefaultParseContext$ParseInlineConstraints;->primary:Z", "FIELD:Lorg/jooq/impl/DefaultParseContext$ParseInlineConstraints;->identity:Z", "FIELD:Lorg/jooq/impl/DefaultParseContext$ParseInlineConstraints;->readonly:Z").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, ParseInlineConstraints.class), ParseInlineConstraints.class, "type;fieldComment;primary;identity;readonly", "FIELD:Lorg/jooq/impl/DefaultParseContext$ParseInlineConstraints;->type:Lorg/jooq/DataType;", "FIELD:Lorg/jooq/impl/DefaultParseContext$ParseInlineConstraints;->fieldComment:Lorg/jooq/Comment;", "FIELD:Lorg/jooq/impl/DefaultParseContext$ParseInlineConstraints;->primary:Z", "FIELD:Lorg/jooq/impl/DefaultParseContext$ParseInlineConstraints;->identity:Z", "FIELD:Lorg/jooq/impl/DefaultParseContext$ParseInlineConstraints;->readonly:Z").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, ParseInlineConstraints.class, Object.class), ParseInlineConstraints.class, "type;fieldComment;primary;identity;readonly", "FIELD:Lorg/jooq/impl/DefaultParseContext$ParseInlineConstraints;->type:Lorg/jooq/DataType;", "FIELD:Lorg/jooq/impl/DefaultParseContext$ParseInlineConstraints;->fieldComment:Lorg/jooq/Comment;", "FIELD:Lorg/jooq/impl/DefaultParseContext$ParseInlineConstraints;->primary:Z", "FIELD:Lorg/jooq/impl/DefaultParseContext$ParseInlineConstraints;->identity:Z", "FIELD:Lorg/jooq/impl/DefaultParseContext$ParseInlineConstraints;->readonly:Z").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public DataType<?> type() {
            return this.type;
        }

        public Comment fieldComment() {
            return this.fieldComment;
        }

        public boolean primary() {
            return this.primary;
        }

        public boolean identity() {
            return this.identity;
        }

        public boolean readonly() {
            return this.readonly;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:163:0x0142, code lost:            if (parseKeywordIf("COMPUTE") != false) goto L47;     */
    /* JADX WARN: Code restructure failed: missing block: B:66:0x037e, code lost:            if (0 != 0) goto L149;     */
    /* JADX WARN: Code restructure failed: missing block: B:68:0x0385, code lost:            if (ignoreProEdition() != false) goto L143;     */
    /* JADX WARN: Code restructure failed: missing block: B:70:0x038f, code lost:            if (parseKeywordIf("NO COMPRESS") == false) goto L143;     */
    /* JADX WARN: Code restructure failed: missing block: B:72:0x0396, code lost:            if (requireProEdition() == false) goto L143;     */
    /* JADX WARN: Code restructure failed: missing block: B:74:0x03a0, code lost:            if (ignoreProEdition() != false) goto L149;     */
    /* JADX WARN: Code restructure failed: missing block: B:76:0x03aa, code lost:            if (parseKeywordIf("COMPRESS") == false) goto L149;     */
    /* JADX WARN: Code restructure failed: missing block: B:78:0x03b1, code lost:            if (requireProEdition() == false) goto L149;     */
    /* JADX WARN: Code restructure failed: missing block: B:80:0x03b6, code lost:            if (0 != 0) goto L158;     */
    /* JADX WARN: Code restructure failed: missing block: B:82:0x03bd, code lost:            if (ignoreProEdition() != false) goto L158;     */
    /* JADX WARN: Code restructure failed: missing block: B:84:0x03c7, code lost:            if (parseKeywordIf("SPARSE") == false) goto L158;     */
    /* JADX WARN: Code restructure failed: missing block: B:86:0x03ce, code lost:            if (requireProEdition() == false) goto L158;     */
    /* JADX WARN: Code restructure failed: missing block: B:88:0x03e4, code lost:            return new org.jooq.impl.DefaultParseContext.ParseInlineConstraints(r10, r24, r12, r13, r0);     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private final org.jooq.impl.DefaultParseContext.ParseInlineConstraints parseInlineConstraints(org.jooq.Name r9, org.jooq.DataType<?> r10, java.util.List<? super org.jooq.Constraint> r11, boolean r12, boolean r13, boolean r14) {
        /*
            Method dump skipped, instructions count: 997
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jooq.impl.DefaultParseContext.parseInlineConstraints(org.jooq.Name, org.jooq.DataType, java.util.List, boolean, boolean, boolean):org.jooq.impl.DefaultParseContext$ParseInlineConstraints");
    }

    private final void parseIdentityOptionIf() {
        boolean identityOption;
        if (parseIf('(')) {
            boolean z = false;
            while (true) {
                identityOption = z;
                if (identityOption) {
                    parseIf(',');
                }
                if (parseKeywordIf("START WITH")) {
                    if (!parseKeywordIf("LIMIT VALUE")) {
                        parseUnsignedIntegerOrBindVariable();
                    }
                    z = true;
                } else if (parseKeywordIf("INCREMENT BY") || parseKeywordIf("MAXVALUE") || parseKeywordIf("MINVALUE") || parseKeywordIf("CACHE")) {
                    parseUnsignedIntegerOrBindVariable();
                    z = true;
                } else if (parseKeywordIf("NOMAXVALUE") || parseKeywordIf("NOMINVALUE") || parseKeywordIf("CYCLE") || parseKeywordIf("NOCYCLE") || parseKeywordIf("NOCACHE") || parseKeywordIf("ORDER") || parseKeywordIf("NOORDER")) {
                    z = true;
                } else if (parseSignedIntegerLiteralIf() == null) {
                    break;
                } else {
                    z = true;
                }
            }
            if (!identityOption) {
                throw unsupportedClause();
            }
            parse(')');
        }
    }

    private final boolean parseSerialIf() {
        String s;
        int i = position();
        if (parseFunctionNameIf("NEXTVAL") && parseIf('(') && (s = parseStringLiteralIf()) != null && s.toLowerCase().endsWith("_seq") && parseIf("::") && parseKeywordIf("REGCLASS") && parseIf(')')) {
            return true;
        }
        position(i);
        return false;
    }

    private final boolean parsePrimaryKeyClusteredNonClusteredKeywordIf() {
        if (!parseKeywordIf("PRIMARY KEY")) {
            return false;
        }
        if (!parseKeywordIf("CLUSTERED")) {
            parseKeywordIf("NONCLUSTERED");
        }
        if (!parseKeywordIf("ASC")) {
            parseKeywordIf("DESC");
            return true;
        }
        return true;
    }

    private final DDLQuery parseCreateType() {
        List<String> values;
        boolean ifNotExists = parseKeywordIf("IF NOT EXISTS");
        Name name = parseName();
        if (parseKeywordIf("AS")) {
            if (parseKeywordIf("ENUM")) {
                parse('(');
                if (!parseIf(')')) {
                    values = parseList(',', (v0) -> {
                        return v0.parseStringLiteral();
                    });
                    parse(')');
                } else {
                    values = new ArrayList<>();
                }
                return (ifNotExists ? this.dsl.createTypeIfNotExists(name) : this.dsl.createType(name)).asEnum((String[]) values.toArray(Tools.EMPTY_STRING));
            }
            parseKeywordIf("OBJECT", "STRUCT");
            parse('(');
            List<Field<?>> fields = parseList(',', ctx -> {
                return DSL.field(parseIdentifier(), parseDataType());
            });
            parse(')');
            return (ifNotExists ? this.dsl.createTypeIfNotExists(name) : this.dsl.createType(name)).as(fields);
        }
        if (parseKeywordIf("FROM")) {
            return (ifNotExists ? this.dsl.createDomainIfNotExists(name) : this.dsl.createDomain(name)).as(parseDataType());
        }
        throw expected("AS", "FROM");
    }

    private final Index parseIndexSpecification(Table<?> table) {
        Name name = parseIdentifierIf();
        parseUsingIndexTypeIf();
        return Internal.createIndex(name == null ? AbstractName.NO_NAME : name, table, (OrderField<?>[]) parseParenthesisedSortSpecification(), false);
    }

    private final boolean parseConstraintConflictClauseIf() {
        return parseKeywordIf("ON CONFLICT") && parseKeyword("ROLLBACK", "ABORT", "FAIL", "IGNORE", "REPLACE");
    }

    private final Constraint parseConstraintEnforcementIf(ConstraintEnforcementStep e) {
        boolean onConflict = false;
        boolean deferrable = false;
        boolean initially = false;
        while (true) {
            if (!onConflict) {
                boolean parseConstraintConflictClauseIf = parseConstraintConflictClauseIf();
                onConflict = parseConstraintConflictClauseIf;
                if (parseConstraintConflictClauseIf) {
                    continue;
                }
            }
            if (!deferrable) {
                boolean parseConstraintDeferrableIf = parseConstraintDeferrableIf();
                deferrable = parseConstraintDeferrableIf;
                if (parseConstraintDeferrableIf) {
                    continue;
                }
            }
            if (!initially) {
                boolean parseConstraintInitiallyIf = parseConstraintInitiallyIf();
                initially = parseConstraintInitiallyIf;
                if (!parseConstraintInitiallyIf) {
                    break;
                }
            } else {
                break;
            }
        }
        if (parseKeywordIf("ENABLE", "ENFORCED")) {
            return e.enforced();
        }
        if (parseKeywordIf("DISABLE", "NOT ENFORCED")) {
            return e.notEnforced();
        }
        return e;
    }

    private final boolean parseConstraintDeferrableIf() {
        return parseKeywordIf("DEFERRABLE", "NOT DEFERRABLE");
    }

    private final boolean parseConstraintInitiallyIf() {
        return parseKeywordIf("INITIALLY") && parseKeyword("DEFERRED", "IMMEDIATE");
    }

    private final Constraint parsePrimaryKeySpecification(ConstraintTypeStep constraint) {
        ConstraintEnforcementStep primaryKey;
        parseUsingIndexTypeIf();
        Field<?>[] fieldNames = parseKeyColumnList();
        if (constraint == null) {
            primaryKey = DSL.primaryKey(fieldNames);
        } else {
            primaryKey = constraint.primaryKey(fieldNames);
        }
        ConstraintEnforcementStep e = primaryKey;
        parseUsingIndexTypeIf();
        return parseConstraintEnforcementIf(e);
    }

    private final Constraint parseUniqueSpecification(ConstraintTypeStep constraint) {
        ConstraintEnforcementStep unique;
        Name constraintName;
        parseUsingIndexTypeIf();
        if (constraint == null && (constraintName = parseIdentifierIf()) != null) {
            constraint = DSL.constraint(constraintName);
        }
        Field<?>[] fieldNames = parseKeyColumnList();
        if (constraint == null) {
            unique = DSL.unique(fieldNames);
        } else {
            unique = constraint.unique(fieldNames);
        }
        ConstraintEnforcementStep e = unique;
        parseUsingIndexTypeIf();
        return parseConstraintEnforcementIf(e);
    }

    private final Field<?>[] parseKeyColumnList() {
        SortField<?>[] fieldExpressions = parseParenthesisedSortSpecification();
        Field<?>[] fieldNames = new Field[fieldExpressions.length];
        for (int i = 0; i < fieldExpressions.length; i++) {
            if (fieldExpressions[i].$sortOrder() != SortOrder.DESC) {
                fieldNames[i] = fieldExpressions[i].$field();
            } else {
                throw notImplemented("DESC sorting in constraints");
            }
        }
        return fieldNames;
    }

    private final Constraint parseCheckSpecification(ConstraintTypeStep constraint) {
        ConstraintEnforcementStep check;
        boolean parens = parseIf('(');
        Condition condition = parseCondition();
        if (parens) {
            parse(')');
        }
        if (constraint == null) {
            check = DSL.check(condition);
        } else {
            check = constraint.check(condition);
        }
        ConstraintEnforcementStep e = check;
        return parseConstraintEnforcementIf(e);
    }

    private final Constraint parseForeignKeySpecification(ConstraintTypeStep constraint) {
        Name constraintName = parseIdentifierIf();
        if (constraintName != null && constraint == null) {
            constraint = DSL.constraint(constraintName);
        }
        parse('(');
        Field<?>[] referencing = (Field[]) parseList(',', c -> {
            return parseFieldName();
        }).toArray(Tools.EMPTY_FIELD);
        parse(')');
        parseKeyword("REFERENCES");
        return parseForeignKeyReferenceSpecification(constraint, referencing);
    }

    /* JADX WARN: Code restructure failed: missing block: B:64:0x01ea, code lost:            throw expected("DELETE", "UPDATE");     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private final org.jooq.Constraint parseForeignKeyReferenceSpecification(org.jooq.ConstraintTypeStep r7, org.jooq.Field<?>[] r8) {
        /*
            Method dump skipped, instructions count: 498
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jooq.impl.DefaultParseContext.parseForeignKeyReferenceSpecification(org.jooq.ConstraintTypeStep, org.jooq.Field[]):org.jooq.Constraint");
    }

    private final <O, S extends QueryPart> S parseIfExists(Supplier<? extends O> supplier, java.util.function.Function<? super O, ? extends S> function, java.util.function.Function<? super O, ? extends S> function2) {
        boolean parseKeywordIf = parseKeywordIf("IF EXISTS");
        O o = supplier.get();
        return parseKeywordIf || parseKeywordIf("IF EXISTS") ? function.apply(o) : function2.apply(o);
    }

    /* JADX WARN: Incorrect types in method signature: <S2::Lorg/jooq/QueryPart;S1:TS2;>(TS1;Ljava/util/function/Function<-TS1;+TS2;>;Ljava/util/function/Function<-TS1;+TS2;>;)TS2; */
    private final QueryPart parseCascadeRestrictIf(QueryPart queryPart, java.util.function.Function function, java.util.function.Function function2) {
        boolean cascade = parseKeywordIf("CASCADE");
        boolean restrict = !cascade && parseKeywordIf("RESTRICT");
        if (cascade) {
            return (QueryPart) function.apply(queryPart);
        }
        if (restrict) {
            return (QueryPart) function2.apply(queryPart);
        }
        return queryPart;
    }

    private final DDLQuery parseAlterTable() {
        Table<?> tableName;
        AlterTableStep alterTable;
        AlterTableDropStep dropColumns;
        Constraint constraint;
        boolean ifTableExists = parseKeywordIf("IF EXISTS");
        if (peekKeyword("ONLY")) {
            Name only = parseIdentifier();
            int p = position();
            Table<?> parseTableNameIf = parseTableNameIf();
            tableName = parseTableNameIf;
            if (parseTableNameIf == null || (!tableName.getQualifiedName().qualified() && tableName.getUnqualifiedName().quoted() == Name.Quoted.UNQUOTED && ALTER_KEYWORDS.contains(tableName.getName().toUpperCase()))) {
                tableName = DSL.table(only);
                position(p);
            }
        } else {
            tableName = parseTableName();
        }
        if (ifTableExists) {
            alterTable = this.dsl.alterTableIfExists(tableName);
        } else {
            alterTable = this.dsl.alterTable(tableName);
        }
        AlterTableStep s1 = alterTable;
        switch (characterUpper()) {
            case 'A':
                if (parseKeywordIf("ADD")) {
                    return parseAlterTableAdd(s1, tableName);
                }
                if (parseKeywordIf("ALTER")) {
                    if (parseKeywordIf("CONSTRAINT")) {
                        return parseAlterTableAlterConstraint(s1);
                    }
                    if (!parseKeywordIf("COLUMN")) {
                    }
                    return parseAlterTableAlterColumn(s1);
                }
                break;
            case 'C':
                if (parseKeywordIf("COMMENT")) {
                    if (!parseIf('=')) {
                        parseKeywordIf("IS");
                    }
                    return this.dsl.commentOnTable(tableName).is(parseStringLiteral());
                }
                break;
            case 'D':
                if (parseKeywordIf("DROP")) {
                    if (parseKeywordIf("CONSTRAINT")) {
                        Supplier supplier = this::parseIdentifier;
                        Objects.requireNonNull(s1);
                        java.util.function.Function function = s1::dropConstraintIfExists;
                        Objects.requireNonNull(s1);
                        return (DDLQuery) parseCascadeRestrictIf((AlterTableDropStep) parseIfExists(supplier, function, s1::dropConstraint), (v0) -> {
                            return v0.cascade();
                        }, (v0) -> {
                            return v0.restrict();
                        });
                    }
                    if (parseKeywordIf("UNIQUE")) {
                        if (peek('(')) {
                            constraint = DSL.unique(parseKeyColumnList());
                        } else {
                            constraint = DSL.constraint(parseIdentifier());
                        }
                        return (DDLQuery) parseCascadeRestrictIf(s1.dropUnique(constraint), (v0) -> {
                            return v0.cascade();
                        }, (v0) -> {
                            return v0.restrict();
                        });
                    }
                    if (parseKeywordIf("PRIMARY KEY")) {
                        Name identifier = parseIdentifierIf();
                        return (DDLQuery) parseCascadeRestrictIf(identifier == null ? s1.dropPrimaryKey() : s1.dropPrimaryKey(identifier), (v0) -> {
                            return v0.cascade();
                        }, (v0) -> {
                            return v0.restrict();
                        });
                    }
                    if (parseKeywordIf("FOREIGN KEY")) {
                        return s1.dropForeignKey(parseIdentifier());
                    }
                    if (parseKeywordIf("INDEX") || parseKeywordIf("KEY")) {
                        return this.dsl.dropIndex(parseIdentifier()).on(tableName);
                    }
                    parseKeywordIf("COLUMN");
                    boolean ifColumnExists = parseKeywordIf("IF EXISTS");
                    boolean parens = parseIf('(');
                    Field<?> parseFieldName = parseFieldName();
                    List<Field<?>> fields = null;
                    if (!ifColumnExists) {
                        while (true) {
                            if (!parseIf(',')) {
                                if (parseKeywordIf("DROP")) {
                                    if (!parseKeywordIf("COLUMN")) {
                                    }
                                }
                            }
                            if (fields == null) {
                                fields = new ArrayList<>();
                                fields.add(parseFieldName);
                            }
                            fields.add(parseFieldName());
                        }
                    }
                    if (parens) {
                        parse(')');
                    }
                    if (fields == null) {
                        if (ifColumnExists) {
                            dropColumns = s1.dropColumnIfExists(parseFieldName);
                        } else {
                            dropColumns = s1.dropColumn(parseFieldName);
                        }
                    } else {
                        dropColumns = s1.dropColumns(fields);
                    }
                    return (DDLQuery) parseCascadeRestrictIf(dropColumns, (v0) -> {
                        return v0.cascade();
                    }, (v0) -> {
                        return v0.restrict();
                    });
                }
                break;
            case 'M':
                if (parseKeywordIf("MODIFY")) {
                    if (parseKeywordIf("CONSTRAINT")) {
                        return parseAlterTableAlterConstraint(s1);
                    }
                    if (!parseKeywordIf("COLUMN")) {
                    }
                    return parseAlterTableAlterColumn(s1);
                }
                break;
            case Opcodes.IASTORE /* 79 */:
                if (parseKeywordIf("OWNER TO") && parseUser() != null) {
                    return IGNORE.get();
                }
                break;
            case Opcodes.DASTORE /* 82 */:
                if (parseKeywordIf("RENAME")) {
                    if (parseKeywordIf("AS", "TO")) {
                        Table<?> newName = parseTableName();
                        return s1.renameTo(newName);
                    }
                    if (parseKeywordIf("COLUMN")) {
                        Name oldName = parseIdentifier();
                        parseKeyword("AS", "TO");
                        Name newName2 = parseIdentifier();
                        return s1.renameColumn(oldName).to(newName2);
                    }
                    if (parseKeywordIf("INDEX")) {
                        Name oldName2 = parseIdentifier();
                        parseKeyword("AS", "TO");
                        Name newName3 = parseIdentifier();
                        return s1.renameIndex(oldName2).to(newName3);
                    }
                    if (parseKeywordIf("CONSTRAINT")) {
                        Name oldName3 = parseIdentifier();
                        parseKeyword("AS", "TO");
                        Name newName4 = parseIdentifier();
                        return s1.renameConstraint(oldName3).to(newName4);
                    }
                }
                break;
            case 'S':
                if (parseKeywordIf("SET")) {
                    return s1.comment(parseOptionsDescription());
                }
                break;
        }
        throw expected("ADD", "ALTER", "COMMENT", "DROP", "MODIFY", "OWNER TO", "RENAME", "SET");
    }

    /* JADX WARN: Removed duplicated region for block: B:18:0x00e6  */
    /* JADX WARN: Removed duplicated region for block: B:24:0x011c  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private final org.jooq.DDLQuery parseAlterTableAdd(org.jooq.AlterTableStep r7, org.jooq.Table<?> r8) {
        /*
            Method dump skipped, instructions count: 296
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jooq.impl.DefaultParseContext.parseAlterTableAdd(org.jooq.AlterTableStep, org.jooq.Table):org.jooq.DDLQuery");
    }

    private final DDLQuery parseAlterTableAddFieldFirstBeforeLast(AlterTableAddStep step) {
        if (parseKeywordIf("FIRST")) {
            return step.first();
        }
        if (parseKeywordIf("BEFORE")) {
            return step.before(parseFieldName());
        }
        if (parseKeywordIf("AFTER")) {
            return step.after(parseFieldName());
        }
        return step;
    }

    private final boolean parseIndexOrKeyIf() {
        return ((parseKeywordIf("SPATIAL INDEX") || parseKeywordIf("SPATIAL KEY") || parseKeywordIf("FULLTEXT INDEX") || parseKeywordIf("FULLTEXT KEY")) && requireUnsupportedSyntax()) || parseKeywordIf("INDEX") || parseKeywordIf("KEY");
    }

    private final void parseAlterTableAddFieldsOrConstraints(List<TableElement> list) {
        ConstraintTypeStep constraint = parseConstraintNameSpecification();
        if (parsePrimaryKeyClusteredNonClusteredKeywordIf()) {
            list.add(parsePrimaryKeySpecification(constraint));
            return;
        }
        if (parseKeywordIf("UNIQUE")) {
            if (!parseKeywordIf("KEY", "INDEX")) {
            }
            list.add(parseUniqueSpecification(constraint));
        } else {
            if (parseKeywordIf("FOREIGN KEY")) {
                list.add(parseForeignKeySpecification(constraint));
                return;
            }
            if (parseKeywordIf("CHECK")) {
                list.add(parseCheckSpecification(constraint));
            } else {
                if (constraint != null) {
                    throw expected("CHECK", "FOREIGN KEY", "PRIMARY KEY", "UNIQUE");
                }
                if (!parseKeywordIf("COLUMN")) {
                }
                parseAlterTableAddField(list);
            }
        }
    }

    private final ConstraintTypeStep parseConstraintNameSpecification() {
        if (parseKeywordIf("CONSTRAINT") && !peekKeyword("PRIMARY KEY", "UNIQUE", "FOREIGN KEY", "CHECK")) {
            return DSL.constraint(parseIdentifier());
        }
        return null;
    }

    private final Field<?> parseAlterTableAddField(List<TableElement> list) {
        Name fieldName = parseIdentifier();
        DataType type = parseDataTypeIf(true);
        if (type == null) {
            type = SQLDataType.OTHER;
        }
        int p = list == null ? -1 : list.size();
        ParseInlineConstraints inline = parseInlineConstraints(fieldName, type, list, false, false, false);
        Field<?> result = DSL.field(fieldName, inline.type, inline.fieldComment);
        if (list != null) {
            list.add(p, result);
        }
        return result;
    }

    private final DDLQuery parseAlterTableAlterColumn(AlterTableStep s1) {
        boolean paren = parseIf('(');
        TableField<?, ?> field = parseFieldName();
        if (!paren) {
            if (parseKeywordIf("CONSTRAINT") && parseIdentifier() != null) {
                if (parseKeywordIf(JoranConstants.NULL)) {
                    return s1.alter(field).dropNotNull();
                }
                if (parseNotNullOptionalEnable()) {
                    return s1.alter(field).setNotNull();
                }
                throw expected("NOT NULL", JoranConstants.NULL);
            }
            if (parseKeywordIf("DROP NOT NULL", "SET NULL", JoranConstants.NULL)) {
                return s1.alter(field).dropNotNull();
            }
            if (parseKeywordIf("DROP DEFAULT")) {
                return s1.alter(field).dropDefault();
            }
            if (parseKeywordIf("SET NOT NULL") || parseNotNullOptionalEnable()) {
                return s1.alter(field).setNotNull();
            }
            if (parseKeywordIf("SET DEFAULT")) {
                return s1.alter(field).default_((Field) toField(parseConcat()));
            }
            if (parseKeywordIf("TO", "RENAME TO", "RENAME AS")) {
                return s1.renameColumn(field).to(parseFieldName());
            }
            if (parseKeywordIf("TYPE", "SET DATA TYPE")) {
            }
        }
        DataType<?> type = parseDataType();
        if (parseKeywordIf(JoranConstants.NULL)) {
            type = type.nullable(true);
        } else if (parseNotNullOptionalEnable()) {
            type = type.nullable(false);
        }
        if (paren) {
            parse(')');
        }
        return s1.alter(field).set(type);
    }

    private final boolean parseNotNullOptionalEnable() {
        if (!parseKeywordIf("NOT NULL")) {
            return false;
        }
        if (!parseKeywordIf("ENABLE")) {
        }
        if (!parseConstraintConflictClauseIf()) {
        }
        return true;
    }

    private final DDLQuery parseAlterTableAlterConstraint(AlterTableStep s1) {
        requireProEdition();
        throw expected("ENABLE", "ENFORCED", "DISABLE", "NOT ENFORCED");
    }

    private final DDLQuery parseAlterType() {
        AlterTypeStep s1 = this.dsl.alterType(parseName());
        if (parseKeywordIf("ADD VALUE")) {
            return s1.addValue(parseStringLiteral());
        }
        if (parseKeywordIf("OWNER TO") && parseUser() != null) {
            return IGNORE.get();
        }
        if (parseKeywordIf("RENAME TO")) {
            return s1.renameTo(parseIdentifier());
        }
        if (parseKeywordIf("RENAME VALUE")) {
            return s1.renameValue(parseStringLiteral()).to(parseKeyword("TO") ? parseStringLiteral() : null);
        }
        if (parseKeywordIf("SET SCHEMA")) {
            return s1.setSchema(parseIdentifier());
        }
        throw expected("ADD VALUE", "OWNER TO", "RENAME TO", "RENAME VALUE", "SET SCHEMA");
    }

    private final DDLQuery parseRename() {
        parseKeyword("RENAME");
        switch (characterUpper()) {
            case 'C':
                if (parseKeywordIf("COLUMN")) {
                    TableField<?, ?> oldName = parseFieldName();
                    parseKeyword("AS", "TO");
                    return this.dsl.alterTable(oldName.getTable()).renameColumn(oldName).to(parseFieldName());
                }
                break;
            case 'D':
                if (parseKeywordIf("DATABASE")) {
                    Catalog oldName2 = parseCatalogName();
                    parseKeyword("AS", "TO");
                    return this.dsl.alterDatabase(oldName2).renameTo(parseCatalogName());
                }
                break;
            case 'I':
                if (parseKeywordIf("INDEX")) {
                    Name oldName3 = parseIndexName();
                    parseKeyword("AS", "TO");
                    return this.dsl.alterIndex(oldName3).renameTo(parseIndexName());
                }
                break;
            case 'S':
                if (parseKeywordIf("SCHEMA")) {
                    Schema oldName4 = parseSchemaName();
                    parseKeyword("AS", "TO");
                    return this.dsl.alterSchema(oldName4).renameTo(parseSchemaName());
                }
                if (parseKeywordIf("SEQUENCE")) {
                    Sequence<?> oldName5 = parseSequenceName();
                    parseKeyword("AS", "TO");
                    return this.dsl.alterSequence(oldName5).renameTo(parseSequenceName());
                }
                break;
            case Opcodes.SASTORE /* 86 */:
                if (parseKeywordIf("VIEW")) {
                    Table<?> oldName6 = parseTableName();
                    parseKeyword("AS", "TO");
                    return this.dsl.alterView(oldName6).renameTo(parseTableName());
                }
                break;
        }
        parseKeywordIf("TABLE");
        Table<?> oldName7 = parseTableName();
        parseKeyword("AS", "TO");
        return this.dsl.alterTable(oldName7).renameTo(parseTableName());
    }

    private final DDLQuery parseDropTable(boolean temporary) {
        boolean z;
        DropTableStep dropTable;
        boolean ifExists = parseKeywordIf("IF EXISTS");
        Table<?> tableName = parseTableName();
        boolean ifExists2 = ifExists || parseKeywordIf("IF EXISTS");
        if (parseKeywordIf("CASCADE")) {
            if (!parseKeywordIf("CONSTRAINTS")) {
            }
            z = true;
        } else {
            z = false;
        }
        boolean cascade = z;
        boolean restrict = !cascade && parseKeywordIf("RESTRICT");
        if (ifExists2) {
            dropTable = this.dsl.dropTableIfExists(tableName);
        } else if (temporary) {
            dropTable = this.dsl.dropTemporaryTable(tableName);
        } else {
            dropTable = this.dsl.dropTable(tableName);
        }
        DropTableStep s1 = dropTable;
        if (cascade) {
            return s1.cascade();
        }
        if (restrict) {
            return s1.restrict();
        }
        return s1;
    }

    private final DDLQuery parseCreateDomain() {
        CreateDomainDefaultStep<?> as;
        CreateDomainConstraintStep createDomainConstraintStep;
        ConstraintTypeStep constraint;
        boolean ifNotExists = parseKeywordIf("IF NOT EXISTS");
        Domain<?> domainName = parseDomainName();
        parseKeyword("AS");
        DataType<?> dataType = parseDataType();
        if (ifNotExists) {
            as = this.dsl.createDomainIfNotExists(domainName).as(dataType);
        } else {
            as = this.dsl.createDomain(domainName).as(dataType);
        }
        CreateDomainDefaultStep<?> s1 = as;
        if (parseKeywordIf("DEFAULT")) {
            createDomainConstraintStep = s1.default_(parseField());
        } else {
            createDomainConstraintStep = s1;
        }
        CreateDomainConstraintStep s2 = createDomainConstraintStep;
        List<Constraint> constraints = new ArrayList<>();
        while (true) {
            constraint = parseConstraintNameSpecification();
            if (!parseKeywordIf("CHECK")) {
                break;
            }
            constraints.add(parseCheckSpecification(constraint));
        }
        if (constraint != null) {
            throw expected("CHECK", "CONSTRAINT");
        }
        if (!constraints.isEmpty()) {
            s2 = s2.constraints(constraints);
        }
        return s2;
    }

    private final DDLQuery parseAlterDomain() {
        AlterDomainStep alterDomain;
        AlterDomainRenameConstraintStep s2;
        AlterDomainDropConstraintCascadeStep dropConstraint;
        boolean ifExists = parseKeywordIf("IF EXISTS");
        Domain<?> domainName = parseDomainName();
        if (ifExists) {
            alterDomain = this.dsl.alterDomainIfExists(domainName);
        } else {
            alterDomain = this.dsl.alterDomain(domainName);
        }
        AlterDomainStep s1 = alterDomain;
        if (parseKeywordIf("ADD")) {
            ConstraintTypeStep constraint = parseConstraintNameSpecification();
            if (parseKeywordIf("CHECK")) {
                return s1.add(parseCheckSpecification(constraint));
            }
            throw expected("CHECK", "CONSTRAINT");
        }
        if (parseKeywordIf("DROP CONSTRAINT")) {
            boolean ifConstraintExists = parseKeywordIf("IF EXISTS");
            Constraint constraint2 = DSL.constraint(parseIdentifier());
            if (ifConstraintExists) {
                dropConstraint = s1.dropConstraintIfExists(constraint2);
            } else {
                dropConstraint = s1.dropConstraint(constraint2);
            }
            AlterDomainDropConstraintCascadeStep s22 = dropConstraint;
            if (parseKeywordIf("CASCADE")) {
                return s22.cascade();
            }
            if (parseKeywordIf("RESTRICT")) {
                return s22.restrict();
            }
            return s22;
        }
        if (parseKeywordIf("RENAME")) {
            if (parseKeywordIf("TO", "AS")) {
                return s1.renameTo(parseDomainName());
            }
            if (parseKeywordIf("CONSTRAINT")) {
                boolean ifConstraintExists2 = parseKeywordIf("IF EXISTS");
                Constraint oldName = DSL.constraint(parseIdentifier());
                if (ifConstraintExists2) {
                    s2 = s1.renameConstraintIfExists(oldName);
                } else {
                    s2 = s1.renameConstraint(oldName);
                }
                parseKeyword("AS", "TO");
                return s2.to(DSL.constraint(parseIdentifier()));
            }
            throw expected("CONSTRAINT", "TO", "AS");
        }
        if (parseKeywordIf("SET DEFAULT")) {
            return s1.setDefault((Field) parseField());
        }
        if (parseKeywordIf("DROP DEFAULT")) {
            return s1.dropDefault();
        }
        if (parseKeywordIf("SET NOT NULL")) {
            return s1.setNotNull();
        }
        if (parseKeywordIf("DROP NOT NULL")) {
            return s1.dropNotNull();
        }
        if (parseKeywordIf("OWNER TO")) {
            parseUser();
            return IGNORE.get();
        }
        throw expected("ADD", "DROP", "RENAME", "SET", "OWNER TO");
    }

    private final DDLQuery parseCreateDatabase() {
        boolean ifNotExists = parseKeywordIf("IF NOT EXISTS");
        Catalog catalogName = parseCatalogName();
        parseMySQLCreateDatabaseFlagsIf();
        if (ifNotExists) {
            return this.dsl.createDatabaseIfNotExists(catalogName);
        }
        return this.dsl.createDatabase(catalogName);
    }

    private final void parseMySQLCreateDatabaseFlagsIf() {
        while (true) {
            if (parseKeywordIf("DEFAULT CHARACTER SET", "CHARACTER SET")) {
                if (!parseIf("=")) {
                }
                parseCharacterSet();
            } else if (parseKeywordIf("DEFAULT COLLATE", "COLLATE")) {
                if (!parseIf("=")) {
                }
                parseCollation();
            } else if (parseKeywordIf("DEFAULT ENCRYPTION", "ENCRYPTION")) {
                if (!parseIf("=")) {
                }
                parseCharacterLiteral();
            } else {
                return;
            }
        }
    }

    private final DDLQuery parseAlterDatabase() {
        AlterDatabaseStep alterDatabase;
        boolean ifExists = parseKeywordIf("IF EXISTS");
        Catalog catalogName = parseCatalogName();
        if (ifExists) {
            alterDatabase = this.dsl.alterDatabaseIfExists(catalogName);
        } else {
            alterDatabase = this.dsl.alterDatabase(catalogName);
        }
        AlterDatabaseStep s1 = alterDatabase;
        if (parseKeywordIf("RENAME")) {
            parseKeyword("AS", "TO");
            return s1.renameTo(parseCatalogName());
        }
        if (parseKeywordIf("OWNER TO") && parseUser() != null) {
            return IGNORE.get();
        }
        if (parseAlterDatabaseFlags(true)) {
            return IGNORE.get();
        }
        throw expected("OWNER TO", "RENAME TO");
    }

    private final boolean parseAlterDatabaseFlags(boolean throwOnFail) {
        parseKeywordIf("DEFAULT");
        if (parseCharacterSetSpecificationIf() != null || parseCollateSpecificationIf() != null) {
            return true;
        }
        if (parseKeywordIf("ENCRYPTION")) {
            parseIf('=');
            parseStringLiteral();
            return true;
        }
        if (throwOnFail) {
            throw expected("CHARACTER SET", "COLLATE", "DEFAULT ENCRYPTION");
        }
        return false;
    }

    private final DDLQuery parseDropDatabase() {
        Supplier supplier = this::parseCatalogName;
        DSLContext dSLContext = this.dsl;
        Objects.requireNonNull(dSLContext);
        java.util.function.Function function = dSLContext::dropDatabaseIfExists;
        DSLContext dSLContext2 = this.dsl;
        Objects.requireNonNull(dSLContext2);
        return (DDLQuery) parseIfExists(supplier, function, dSLContext2::dropDatabase);
    }

    private final DDLQuery parseCreateSchema() {
        boolean ifNotExists = parseKeywordIf("IF NOT EXISTS");
        boolean authorization = parseKeywordIf("AUTHORIZATION");
        Schema schemaName = parseSchemaName();
        if (!authorization && parseKeywordIf("AUTHORIZATION")) {
            parseUser();
        }
        parseMySQLCreateDatabaseFlagsIf();
        if (ifNotExists) {
            return this.dsl.createSchemaIfNotExists(schemaName);
        }
        return this.dsl.createSchema(schemaName);
    }

    private final DDLQuery parseAlterSchema() {
        AlterSchemaStep alterSchema;
        boolean ifExists = parseKeywordIf("IF EXISTS");
        Schema schemaName = parseSchemaName();
        if (ifExists) {
            alterSchema = this.dsl.alterSchemaIfExists(schemaName);
        } else {
            alterSchema = this.dsl.alterSchema(schemaName);
        }
        AlterSchemaStep s1 = alterSchema;
        if (parseKeywordIf("RENAME")) {
            parseKeyword("AS", "TO");
            return s1.renameTo(parseSchemaName());
        }
        if (parseKeywordIf("OWNER TO") && parseUser() != null) {
            return IGNORE.get();
        }
        if (parseAlterDatabaseFlags(false)) {
            return IGNORE.get();
        }
        throw expected("OWNER TO", "RENAME TO");
    }

    private final DDLQuery parseCreateIndex(boolean unique) {
        Condition condition;
        CreateIndexStep createIndex;
        CreateIndexWhereStep createIndexWhereStep;
        boolean ifNotExists = parseKeywordIf("IF NOT EXISTS");
        Name indexName = parseIndexNameIf();
        parseUsingIndexTypeIf();
        SortField<?>[] fields = null;
        if (peek('(')) {
            fields = parseParenthesisedSortSpecification();
        }
        parseKeyword("ON");
        Table<?> tableName = parseTableName();
        parseUsingIndexTypeIf();
        if (fields == null) {
            fields = parseParenthesisedSortSpecification();
        }
        parseUsingIndexTypeIf();
        Name[] include = null;
        if (parseKeywordIf("INCLUDE", "COVERING", "STORING")) {
            parse('(');
            include = (Name[]) parseIdentifiers().toArray(Tools.EMPTY_NAME);
            parse(')');
        }
        parseKeywordIf("VISIBLE");
        if (parseKeywordIf("WHERE")) {
            condition = parseCondition();
        } else {
            condition = null;
        }
        Condition condition2 = condition;
        boolean excludeNullKeys = condition2 == null && parseKeywordIf("EXCLUDE NULL KEYS");
        if (ifNotExists) {
            if (unique) {
                createIndex = this.dsl.createUniqueIndexIfNotExists(indexName);
            } else {
                createIndex = this.dsl.createIndexIfNotExists(indexName);
            }
        } else if (unique) {
            if (indexName == null) {
                createIndex = this.dsl.createUniqueIndex();
            } else {
                createIndex = this.dsl.createUniqueIndex(indexName);
            }
        } else if (indexName == null) {
            createIndex = this.dsl.createIndex();
        } else {
            createIndex = this.dsl.createIndex(indexName);
        }
        CreateIndexStep s1 = createIndex;
        CreateIndexIncludeStep s2 = s1.on(tableName, fields);
        if (include != null) {
            createIndexWhereStep = s2.include(include);
        } else {
            createIndexWhereStep = s2;
        }
        CreateIndexWhereStep s3 = createIndexWhereStep;
        if (condition2 != null) {
            return s3.where(condition2);
        }
        if (excludeNullKeys) {
            return s3.excludeNullKeys();
        }
        return s3;
    }

    private SortField<?>[] parseParenthesisedSortSpecification() {
        parse('(');
        SortField<?>[] fields = (SortField[]) parseList(',', c -> {
            return c.parseSortField();
        }).toArray(Tools.EMPTY_SORTFIELD);
        parse(')');
        return fields;
    }

    private final boolean parseUsingIndexTypeIf() {
        if (parseKeywordIf("USING")) {
            parseIdentifier();
            return true;
        }
        return true;
    }

    private final DDLQuery parseAlterIndex() {
        AlterIndexStep alterIndex;
        boolean ifExists = parseKeywordIf("IF EXISTS");
        Name indexName = parseIndexName();
        parseKeyword("RENAME");
        parseKeyword("AS", "TO");
        Name newName = parseIndexName();
        if (ifExists) {
            alterIndex = this.dsl.alterIndexIfExists(indexName);
        } else {
            alterIndex = this.dsl.alterIndex(indexName);
        }
        AlterIndexStep s1 = alterIndex;
        return s1.renameTo(newName);
    }

    private final DDLQuery parseDropIndex() {
        DropIndexOnStep dropIndex;
        DropIndexCascadeStep dropIndexCascadeStep;
        boolean ifExists = parseKeywordIf("IF EXISTS");
        Name indexName = parseIndexName();
        boolean ifExists2 = ifExists || parseKeywordIf("IF EXISTS");
        boolean on = parseKeywordIf("ON");
        Table<?> onTable = on ? parseTableName() : null;
        if (ifExists2) {
            dropIndex = this.dsl.dropIndexIfExists(indexName);
        } else {
            dropIndex = this.dsl.dropIndex(indexName);
        }
        DropIndexOnStep s1 = dropIndex;
        if (on) {
            dropIndexCascadeStep = s1.on(onTable);
        } else {
            dropIndexCascadeStep = s1;
        }
        DropIndexCascadeStep s2 = dropIndexCascadeStep;
        if (parseKeywordIf("CASCADE")) {
            return s2.cascade();
        }
        if (parseKeywordIf("RESTRICT")) {
            return s2.restrict();
        }
        return s2;
    }

    @Override // org.jooq.ParseContext
    public final Condition parseCondition() {
        return toCondition(parseOr());
    }

    private final QueryPart parseOr() {
        QueryPart parseXor = parseXor();
        while (true) {
            QueryPart condition = parseXor;
            if (parseKeywordIf("OR")) {
                parseXor = toCondition(condition).or(toCondition(parseXor()));
            } else {
                return condition;
            }
        }
    }

    private final QueryPart parseXor() {
        QueryPart parseAnd = parseAnd();
        while (true) {
            QueryPart condition = parseAnd;
            if (parseKeywordIf("XOR")) {
                parseAnd = toCondition(condition).xor(toCondition(parseAnd()));
            } else {
                return condition;
            }
        }
    }

    private final QueryPart parseAnd() {
        QueryPart condition;
        QueryPart parseNot = parseNot();
        while (true) {
            condition = parseNot;
            if ((this.forbidden.contains(FunctionKeyword.FK_AND) || !parseKeywordIf("AND")) && !(parseCategory() == SQLDialectCategory.MYSQL && parseIf("&&"))) {
                break;
            }
            parseNot = toCondition(condition).and(toCondition(parseNot()));
        }
        return condition;
    }

    private final QueryPart parseNot() {
        int not = parseNot0();
        QueryPart condition = parsePredicate();
        for (int i = 0; i < not; i++) {
            condition = toCondition(condition).not();
        }
        return condition;
    }

    private final int parseNot0() {
        int not = 0;
        while (true) {
            if (parseKeywordIf("NOT") || (parseCategory() == SQLDialectCategory.MYSQL && parseIf('!'))) {
                not++;
            }
        }
        return not;
    }

    /* JADX WARN: Code restructure failed: missing block: B:308:0x0872, code lost:            if (r0 != false) goto L309;     */
    /* JADX WARN: Code restructure failed: missing block: B:362:0x0a48, code lost:            if (r0 != false) goto L363;     */
    /* JADX WARN: Code restructure failed: missing block: B:380:0x0ab6, code lost:            if (r0 != false) goto L381;     */
    /* JADX WARN: Multi-variable type inference failed */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private final org.jooq.QueryPart parsePredicate() {
        /*
            Method dump skipped, instructions count: 2967
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jooq.impl.DefaultParseContext.parsePredicate():org.jooq.QueryPart");
    }

    private final Condition parseEqualNull() {
        Condition result;
        parse('(');
        FieldOrRow left = parseConcat();
        parse(',');
        if (left instanceof Field) {
            Field f = (Field) left;
            result = f.isNotDistinctFrom((Field) toField(parseConcat()));
        } else {
            result = new RowIsDistinctFrom((Row) left, parseRow(Integer.valueOf(((Row) left).size()), true), true);
        }
        parse(')');
        return result;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private final Condition parsePredicateXMLExistsIf() {
        if (parseKeywordIf("XMLEXISTS")) {
            parse('(');
            Field<?> parseField = parseField();
            QOM.XMLPassingMechanism m = parseXMLPassingMechanism();
            Field<?> parseField2 = parseField();
            parse(')');
            if (m == QOM.XMLPassingMechanism.BY_REF) {
                return DSL.xmlexists((Field<String>) parseField).passingByRef((Field<XML>) parseField2);
            }
            if (m == QOM.XMLPassingMechanism.BY_VALUE) {
                return DSL.xmlexists((Field<String>) parseField).passingByValue((Field<XML>) parseField2);
            }
            return DSL.xmlexists((Field<String>) parseField).passing((Field<XML>) parseField2);
        }
        return null;
    }

    private final Condition parsePredicateJSONExistsIf() {
        if (parseKeywordIf("JSON_EXISTS")) {
            parse('(');
            Field json = parseField();
            parse(',');
            Field<?> parseField = parseField();
            parseJSONExistsOnErrorBehaviourIf();
            parse(')');
            return DSL.jsonExists((Field<JSON>) json, (Field<String>) parseField);
        }
        return null;
    }

    private final QueryPart parseEscapeClauseIf(LikeEscapeStep like) {
        return parseKeywordIf("ESCAPE") ? like.escape(parseCharacterLiteral()) : like;
    }

    @Override // org.jooq.ParseContext
    public final Table<?> parseTable() {
        return parseJoinedTable(() -> {
            return peekKeyword(KEYWORDS_IN_SELECT_FROM);
        });
    }

    private final Table<?> parseLateral(BooleanSupplier forbiddenKeywords) {
        if (parseKeywordIf("LATERAL")) {
            return DSL.lateral(parseTableFactor(forbiddenKeywords));
        }
        return parseTableFactor(forbiddenKeywords);
    }

    private final <R extends Record> Table<R> t(TableLike<R> table) {
        return t(table, false);
    }

    private final <R extends Record> Table<R> t(TableLike<R> table, boolean dummyAlias) {
        if (table instanceof Table) {
            Table<R> t = (Table) table;
            return t;
        }
        if (dummyAlias) {
            return table.asTable("x");
        }
        return table.asTable();
    }

    /* JADX WARN: Multi-variable type inference failed */
    private final Table<?> parseTableFactor(BooleanSupplier forbiddenKeywords) {
        TableLike<?> result;
        XMLTableColumnsFirstStep xMLTableColumnsFirstStep;
        Field field;
        Table<Record1<Integer>> generateSeries;
        if (parseFunctionNameIf("OLD TABLE")) {
            parse('(');
            Query query = parseQuery(false, false);
            parse(')');
            if (query instanceof Merge) {
                Merge<?> q = (Merge) query;
                result = DSL.oldTable(q);
            } else if (query instanceof Update) {
                Update<?> q2 = (Update) query;
                result = DSL.oldTable(q2);
            } else if (query instanceof Delete) {
                Delete<?> q3 = (Delete) query;
                result = DSL.oldTable(q3);
            } else {
                throw expected("UPDATE", "DELETE", "MERGE");
            }
        } else if (parseFunctionNameIf("NEW TABLE")) {
            parse('(');
            Query query2 = parseQuery(false, false);
            parse(')');
            if (query2 instanceof Merge) {
                Merge<?> q4 = (Merge) query2;
                result = DSL.newTable(q4);
            } else if (query2 instanceof Insert) {
                Insert<?> q5 = (Insert) query2;
                result = DSL.newTable(q5);
            } else if (query2 instanceof Update) {
                Update<?> q6 = (Update) query2;
                result = DSL.newTable(q6);
            } else {
                throw expected("INSERT", "UPDATE", "MERGE");
            }
        } else if (parseFunctionNameIf("FINAL TABLE")) {
            parse('(');
            Query query3 = parseQuery(false, false);
            parse(')');
            if (query3 instanceof Merge) {
                Merge<?> q7 = (Merge) query3;
                result = DSL.finalTable(q7);
            } else if (query3 instanceof Insert) {
                Insert<?> q8 = (Insert) query3;
                result = DSL.finalTable(q8);
            } else if (query3 instanceof Update) {
                Update<?> q9 = (Update) query3;
                result = DSL.finalTable(q9);
            } else {
                throw expected("INSERT", "UPDATE", "MERGE");
            }
        } else if (parseFunctionNameIf("UNNEST", "TABLE")) {
            parse('(');
            if (parseFunctionNameIf("GENERATOR")) {
                parse('(');
                Field<?> tl = parseFunctionArgumentIf("TIMELIMIT");
                Field<?> rc = parseFunctionArgumentIf("ROWCOUNT");
                if (tl == null) {
                    parseFunctionArgumentIf("TIMELIMIT");
                }
                parse(')');
                result = DSL.generateSeries((Field<Integer>) DSL.one(), (Field<Integer>) rc);
            } else {
                Field<?> f = parseField();
                if (!f.getType().isArray()) {
                    f = f.coerce(f.getDataType().getArrayDataType());
                }
                result = DSL.unnest(f);
            }
            parse(')');
        } else if (parseFunctionNameIf("GENERATE_SERIES", "SYSTEM_RANGE")) {
            parse('(');
            Field from = toField(parseConcat());
            parse(',');
            Field to = toField(parseConcat());
            if (parseIf(',')) {
                field = toField(parseConcat());
            } else {
                field = null;
            }
            Field step = field;
            parse(')');
            if (step == null) {
                generateSeries = DSL.generateSeries((Field<Integer>) from, (Field<Integer>) to);
            } else {
                generateSeries = DSL.generateSeries((Field<Integer>) from, (Field<Integer>) to, (Field<Integer>) step);
            }
            result = generateSeries;
        } else if (parseFunctionNameIf("JSON_TABLE")) {
            parse('(');
            Field json = parseField();
            parse(',');
            Field path = toField(parseConcat());
            JSONTableColumnsStep s1 = (JSONTableColumnsStep) DSL.jsonTable((Field<JSON>) json, (Field<String>) path);
            parseKeyword("COLUMNS");
            parse('(');
            do {
                Name fieldName = parseIdentifier();
                if (parseKeywordIf("FOR ORDINALITY")) {
                    s1 = s1.column(fieldName).forOrdinality();
                } else {
                    JSONTableColumnPathStep s2 = s1.column(fieldName, parseDataType());
                    s1 = parseKeywordIf("PATH") ? s2.path(parseStringLiteral()) : s2;
                }
            } while (parseIf(','));
            parse(')');
            parse(')');
            result = s1;
        } else if (parseFunctionNameIf("OPENJSON") && requireProEdition()) {
            result = null;
        } else if (peekFunctionNameIf("VALUES")) {
            result = parseTableValueConstructor();
        } else if (parseFunctionNameIf("XMLTABLE")) {
            parse('(');
            XMLTablePassingStep xmltable = DSL.xmltable((Field<String>) toField(parseConcat()));
            QOM.XMLPassingMechanism m = parseXMLPassingMechanismIf();
            Field<?> parseField = m == null ? null : parseField();
            if (m == QOM.XMLPassingMechanism.BY_REF) {
                xMLTableColumnsFirstStep = xmltable.passingByRef((Field<XML>) parseField);
            } else if (m == QOM.XMLPassingMechanism.BY_VALUE) {
                xMLTableColumnsFirstStep = xmltable.passingByValue((Field<XML>) parseField);
            } else if (m == QOM.XMLPassingMechanism.DEFAULT) {
                xMLTableColumnsFirstStep = xmltable.passing((Field<XML>) parseField);
            } else {
                xMLTableColumnsFirstStep = xmltable;
            }
            XMLTableColumnsStep s22 = (XMLTableColumnsStep) xMLTableColumnsFirstStep;
            parseKeyword("COLUMNS");
            do {
                Name fieldName2 = parseIdentifier();
                if (parseKeywordIf("FOR ORDINALITY")) {
                    s22 = s22.column(fieldName2).forOrdinality();
                } else {
                    XMLTableColumnPathStep s3 = s22.column(fieldName2, parseDataType());
                    s22 = parseKeywordIf("PATH") ? s3.path(parseStringLiteral()) : s3;
                }
            } while (parseIf(','));
            parse(')');
            result = s22;
        } else if (parseIf('(')) {
            if (peekKeyword("SELECT", "SEL", "WITH")) {
                SelectQueryImpl<Record> select = parseWithOrSelect();
                parse(')');
                result = parseQueryExpressionBody(null, null, select);
            } else if (peekKeyword("VALUES")) {
                result = parseTableValueConstructor();
                parse(')');
            } else {
                result = parseJoinedTable(forbiddenKeywords);
                parse(')');
            }
        } else {
            result = parseTableName();
        }
        if ((ignoreProEdition() || !parseKeywordIf("VERSIONS BETWEEN") || !requireProEdition()) && ((!ignoreProEdition() && parseForPeriodIf() && requireProEdition()) || ignoreProEdition() || !parseKeywordIf("AS OF") || !requireProEdition())) {
        }
        if (parseKeywordIf("WITH ORDINALITY")) {
            result = t(result).withOrdinality();
        }
        if (ignoreProEdition() || !parseKeywordIf("PIVOT") || requireProEdition()) {
        }
        TableLike<?> result2 = parseCorrelationNameIf(result, forbiddenKeywords);
        int p = position();
        if (peekKeyword("WITH CHECK OPTION", "WITH READ ONLY") || !parseKeywordIf("WITH")) {
            while (true) {
                if (parseKeywordIf("USE KEY", "USE INDEX")) {
                    if (parseKeywordIf("FOR JOIN")) {
                        result2 = t(result2).useIndexForJoin(parseParenthesisedIdentifiers());
                    } else if (parseKeywordIf("FOR ORDER BY")) {
                        result2 = t(result2).useIndexForOrderBy(parseParenthesisedIdentifiers());
                    } else if (parseKeywordIf("FOR GROUP BY")) {
                        result2 = t(result2).useIndexForGroupBy(parseParenthesisedIdentifiers());
                    } else {
                        result2 = t(result2).useIndex(parseParenthesisedIdentifiers());
                    }
                } else if (parseKeywordIf("FORCE KEY", "FORCE INDEX")) {
                    if (parseKeywordIf("FOR JOIN")) {
                        result2 = t(result2).forceIndexForJoin(parseParenthesisedIdentifiers());
                    } else if (parseKeywordIf("FOR ORDER BY")) {
                        result2 = t(result2).forceIndexForOrderBy(parseParenthesisedIdentifiers());
                    } else if (parseKeywordIf("FOR GROUP BY")) {
                        result2 = t(result2).forceIndexForGroupBy(parseParenthesisedIdentifiers());
                    } else {
                        result2 = t(result2).forceIndex(parseParenthesisedIdentifiers());
                    }
                } else {
                    if (!parseKeywordIf("IGNORE KEY", "IGNORE INDEX")) {
                        break;
                    }
                    if (parseKeywordIf("FOR JOIN")) {
                        result2 = t(result2).ignoreIndexForJoin(parseParenthesisedIdentifiers());
                    } else if (parseKeywordIf("FOR ORDER BY")) {
                        result2 = t(result2).ignoreIndexForOrderBy(parseParenthesisedIdentifiers());
                    } else if (parseKeywordIf("FOR GROUP BY")) {
                        result2 = t(result2).ignoreIndexForGroupBy(parseParenthesisedIdentifiers());
                    } else {
                        result2 = t(result2).ignoreIndex(parseParenthesisedIdentifiers());
                    }
                }
            }
        } else if (ignoreProEdition() || !parseIf('(') || !requireProEdition()) {
            position(p);
        }
        return t(result2);
    }

    private final boolean parseForPeriodIf() {
        return (!peekKeyword("FOR") || peekKeyword("FOR JSON") || peekKeyword("FOR KEY SHARE") || peekKeyword("FOR NO KEY UPDATE") || peekKeyword("FOR SHARE") || peekKeyword("FOR UPDATE") || peekKeyword("FOR XML") || !parseKeyword("FOR")) ? false : true;
    }

    private final String[] parseParenthesisedIdentifiers() {
        return (String[]) parseParenthesised(c -> {
            return (String[]) Tools.map(parseIdentifiers(), (v0) -> {
                return v0.last();
            }, x$0 -> {
                return new String[x$0];
            });
        });
    }

    private final Field<?> parseFunctionArgumentIf(String parameterName) {
        if (parseKeywordIf(parameterName) && parse(ParameterizedMessage.ERROR_SEPARATOR)) {
            return parseField();
        }
        return null;
    }

    private final TableLike<?> parseCorrelationNameIf(TableLike<?> result, BooleanSupplier forbiddenKeywords) {
        Name alias = null;
        List<Name> columnAliases = null;
        if (parseKeywordIf("AS")) {
            alias = parseIdentifier();
        } else if (!forbiddenKeywords.getAsBoolean()) {
            alias = parseIdentifierIf();
        }
        if (alias != null) {
            if (parseIf('(')) {
                columnAliases = parseIdentifiers();
                parse(')');
            }
            if (columnAliases != null) {
                result = t(result, true).as(alias, columnAliases);
            } else {
                result = t(result, true).as(alias);
            }
        }
        return result;
    }

    private final Row parseTableValueConstructorRow(Integer degree) {
        if (parseKeywordIf("ROW")) {
            return parseTuple(degree);
        }
        Field<?> r = null;
        if (degree == null || degree.intValue() == 1) {
            r = parseScalarSubqueryIf();
        }
        if (r != null) {
            return DSL.row((SelectField) r);
        }
        if (peek('(')) {
            return parseTuple(degree);
        }
        if (degree == null || degree.intValue() == 1) {
            return DSL.row((SelectField) parseField());
        }
        throw exception("Expected row of degree: " + degree);
    }

    private final Table<?> parseTableValueConstructor() {
        parseKeyword("VALUES");
        List<Row> rows = new ArrayList<>();
        Integer degree = null;
        do {
            Row row = parseTableValueConstructorRow(degree);
            rows.add(row);
            if (degree == null) {
                degree = Integer.valueOf(row.size());
            }
        } while (parseIf(','));
        return DSL.values0((Row[]) rows.toArray(Tools.EMPTY_ROW));
    }

    private final Table<?> parseExplicitTable() {
        parseKeyword("TABLE");
        return parseTableName();
    }

    private final Row parseTuple() {
        return parseTuple(null, false);
    }

    private final Row parseTuple(Integer degree) {
        return parseTuple(degree, false);
    }

    private final Row parseTupleIf(Integer degree) {
        return parseTupleIf(degree, false);
    }

    private final Row parseTuple(Integer degree, boolean allowDoubleParens) {
        List<? extends FieldOrRow> fieldsOrRows;
        Row row;
        parse('(');
        if (allowDoubleParens) {
            fieldsOrRows = parseList(',', c -> {
                return parseFieldOrRow();
            });
        } else {
            fieldsOrRows = parseList(',', c2 -> {
                return c2.parseField();
            });
        }
        if (fieldsOrRows.size() == 0) {
            row = DSL.row((SelectField<?>[]) new SelectField[0]);
        } else if (fieldsOrRows.get(0) instanceof Field) {
            row = DSL.row((Collection<?>) fieldsOrRows);
        } else if (fieldsOrRows.size() == 1) {
            row = (Row) fieldsOrRows.get(0);
        } else {
            throw exception("Unsupported row size");
        }
        if (degree != null && row.size() != degree.intValue()) {
            throw exception("Expected row of degree: " + degree + ". Got: " + row.size());
        }
        parse(')');
        return row;
    }

    private final Row parseTupleIf(Integer degree, boolean allowDoubleParens) {
        if (peek('(')) {
            return parseTuple(degree, allowDoubleParens);
        }
        return null;
    }

    private final Table<?> parseJoinedTable(BooleanSupplier forbiddenKeywords) {
        Table<?> parseLateral = parseLateral(forbiddenKeywords);
        while (true) {
            Table<?> result = parseLateral;
            Table<?> joined = parseJoinedTableIf(result, forbiddenKeywords);
            if (joined == null) {
                return result;
            }
            parseLateral = joined;
        }
    }

    /* JADX WARN: Failed to find 'out' block for switch in B:15:0x006d. Please report as an issue. */
    private final Table<?> parseJoinedTableIf(Table<?> left, BooleanSupplier forbiddenKeywords) {
        position();
        if (ignoreProEdition() || !parseKeywordIf("PARTITION BY") || requireProEdition()) {
        }
        Join join = parseJoinTypeIf();
        if (join == null) {
            return null;
        }
        Table<?> right = join.type.qualified() ? parseJoinedTable(forbiddenKeywords) : parseLateral(forbiddenKeywords);
        TableOptionalOnStep<?> s0 = left.join(right, join.type, join.hint);
        TableOnStep<?> s2 = (TablePartitionByStep) s0;
        switch (join.type) {
            case LEFT_OUTER_JOIN:
            case FULL_OUTER_JOIN:
            case RIGHT_OUTER_JOIN:
                if (ignoreProEdition() || !parseKeywordIf("PARTITION BY") || requireProEdition()) {
                }
                break;
            case JOIN:
            case STRAIGHT_JOIN:
            case LEFT_SEMI_JOIN:
            case LEFT_ANTI_JOIN:
                if (parseKeywordIf("ON")) {
                    return s2.on(parseCondition());
                }
                if (parseKeywordIf("USING")) {
                    return parseJoinUsing(s2);
                }
                if (join.type == JoinType.JOIN) {
                    return s0;
                }
                throw expected("ON", "USING");
            case CROSS_JOIN:
                if (parseKeywordIf("ON")) {
                    return left.join(right).on(parseCondition());
                }
                if (parseKeywordIf("USING")) {
                    return parseJoinUsing(left.join(right));
                }
            default:
                return s0;
        }
    }

    private final Table<?> parseJoinUsing(TableOnStep<?> join) {
        Table<?> result;
        parse('(');
        if (parseIf(')')) {
            result = join.using(new Field[0]);
        } else {
            result = join.using(Tools.fieldsByName((Name[]) parseIdentifiers().toArray(Tools.EMPTY_NAME)));
            parse(')');
        }
        return result;
    }

    private final List<SelectFieldOrAsterisk> parseSelectList() {
        List<SelectFieldOrAsterisk> result = new ArrayList<>();
        do {
            if (parseIf('*')) {
                if (parseKeywordIf("EXCEPT")) {
                    parse('(');
                    result.add(DSL.asterisk().except((Field<?>[]) parseList(',', c -> {
                        return parseFieldName();
                    }).toArray(Tools.EMPTY_FIELD)));
                    parse(')');
                } else {
                    result.add(DSL.asterisk());
                }
            } else {
                QualifiedAsterisk qa = parseQualifiedAsteriskIf();
                if (qa != null) {
                    if (parseKeywordIf("EXCEPT")) {
                        parse('(');
                        result.add(qa.except((Field<?>[]) parseList(',', c2 -> {
                            return parseFieldName();
                        }).toArray(Tools.EMPTY_FIELD)));
                        parse(')');
                    } else {
                        result.add(qa);
                    }
                } else {
                    Name alias = null;
                    SelectField<?> field = null;
                    if (0 == 0) {
                        field = parseSelectField();
                        if (parseKeywordIf("AS")) {
                            alias = parseIdentifier(true, false);
                        } else if (!peekKeyword(KEYWORDS_IN_SELECT) && !peekKeyword(KEYWORDS_IN_STATEMENTS)) {
                            alias = parseIdentifierIf(true, false);
                        }
                    }
                    result.add(alias == null ? field : field.as(alias));
                }
            }
        } while (parseIf(','));
        return result;
    }

    @Override // org.jooq.ParseContext
    public final SortField<?> parseSortField() {
        SortField<?> sort;
        Field<?> field = parseField();
        if (parseKeywordIf("DESC")) {
            sort = field.desc();
        } else if (parseKeywordIf("ASC")) {
            sort = field.asc();
        } else {
            sort = field.sortDefault();
        }
        if (parseKeywordIf("NULLS FIRST")) {
            sort = sort.nullsFirst();
        } else if (parseKeywordIf("NULLS LAST")) {
            sort = sort.nullsLast();
        }
        return sort;
    }

    private final List<Field<?>> parseFieldsOrEmptyParenthesised() {
        parse('(');
        if (parseIf(')')) {
            return Collections.emptyList();
        }
        List<Field<?>> result = parseList(',', c -> {
            return c.parseField();
        });
        parse(')');
        return result;
    }

    private final SelectField<?> parseSelectField() {
        return (SelectField) parseFieldOrRow();
    }

    private final Row parseRow() {
        return parseRow(null);
    }

    private final Row parseRowIf() {
        return parseRowIf(null);
    }

    private final Row parseRow(Integer degree) {
        parseFunctionNameIf("ROW");
        return parseTuple(degree);
    }

    private final Row parseRowIf(Integer degree) {
        parseFunctionNameIf("ROW");
        return parseTupleIf(degree);
    }

    private final Row parseRow(Integer degree, boolean allowDoubleParens) {
        parseFunctionNameIf("ROW");
        return parseTuple(degree, allowDoubleParens);
    }

    public final FieldOrRow parseFieldOrRow() {
        return toFieldOrRow(parseOr());
    }

    @Override // org.jooq.ParseContext
    public final Field<?> parseField() {
        return toField(parseOr());
    }

    private final String parseHints() {
        StringBuilder sb = new StringBuilder();
        do {
            int p = position();
            if (parseIf('/', false)) {
                parse('*', false);
                int i = position();
                while (i < this.sql.length) {
                    switch (this.sql[i]) {
                        case '*':
                            if (i + 1 < this.sql.length && this.sql[i + 1] == '/') {
                                break;
                            }
                            break;
                    }
                    i++;
                }
                position(i + 2);
                if (sb.length() > 0) {
                    sb.append(' ');
                }
                sb.append(substring(p, position()));
            }
        } while (parseWhitespaceIf());
        ignoreHints(true);
        if (sb.length() > 0) {
            return sb.toString();
        }
        return null;
    }

    private final Condition toCondition(QueryPart part) {
        if (part == null) {
            return null;
        }
        if (part instanceof Condition) {
            Condition c = (Condition) part;
            return c;
        }
        if (part instanceof Field) {
            Field f = (Field) part;
            DataType dataType = f.getDataType();
            if (dataType.isBoolean()) {
                return DSL.condition((Field<Boolean>) f);
            }
            if (dataType.isNumeric()) {
                return f.ne((Field) DSL.zero());
            }
            if (dataType.isOther() && ((part instanceof TableFieldImpl) || (part instanceof Val))) {
                return DSL.condition((Field<Boolean>) part);
            }
            throw expected("Boolean field");
        }
        throw expected("Condition");
    }

    private final FieldOrRow toFieldOrRow(QueryPart part) {
        if (part == null) {
            return null;
        }
        if (part instanceof Field) {
            Field<?> f = (Field) part;
            return f;
        }
        if (part instanceof Row) {
            Row r = (Row) part;
            return r;
        }
        throw expected("Field or row");
    }

    private final Field<?> toField(QueryPart part) {
        if (part == null) {
            return null;
        }
        if (part instanceof Field) {
            Field<?> f = (Field) part;
            return f;
        }
        throw expected("Field");
    }

    private final FieldOrRow parseConcat() {
        FieldOrRow r = parseCollated();
        if (r instanceof Field) {
            while (parseIf("||")) {
                r = concatOperator((Field) r, toField(parseCollated()));
            }
        }
        return r;
    }

    private final Field<?> concatOperator(Field<?> a1, Field<?> a2) {
        return (a1.getDataType().isArray() && a2.getDataType().isArray()) ? DSL.arrayConcat(a1, a2) : DSL.concat((Field<?>[]) new Field[]{a1, a2});
    }

    private final FieldOrRow parseCollated() {
        FieldOrRow r = parseNumericOp();
        if ((r instanceof Field) && parseKeywordIf("COLLATE")) {
            r = ((Field) r).collate(parseCollation());
        }
        return r;
    }

    private final Field<?> parseFieldNumericOpParenthesised() {
        parse('(');
        Field<?> r = toField(parseNumericOp());
        parse(')');
        return r;
    }

    private final Field<?> parseFieldParenthesised() {
        parse('(');
        Field<?> r = parseField();
        parse(')');
        return r;
    }

    private final <Q extends QueryPart> Q parseFunctionArgs1(org.jooq.Function1<? super Field, ? extends Q> finisher) {
        parse('(');
        Field<?> f1 = parseField();
        parse(')');
        return finisher.apply(f1);
    }

    private final <Q extends QueryPart> Q parseFunctionArgs2(org.jooq.Function1<? super Field, ? extends Q> finisher1, Function2<? super Field, ? super Field, ? extends Q> finisher2) {
        parse('(');
        Field<?> f1 = parseField();
        Field<?> f2 = parseIf(',') ? parseField() : null;
        parse(')');
        return f2 == null ? finisher1.apply(f1) : finisher2.apply(f1, f2);
    }

    private final <Q extends QueryPart> Q parseFunctionArgs2(Function2<? super Field, ? super Field, ? extends Q> function2) {
        return (Q) parseFunctionArgs2(this::parseField, function2);
    }

    private final <Q extends QueryPart> Q parseFunctionArgs2(Supplier<? extends Field<?>> argument, Function2<? super Field, ? super Field, ? extends Q> finisher) {
        parse('(');
        Field<?> f1 = argument.get();
        parse(',');
        Field<?> f2 = argument.get();
        parse(')');
        return finisher.apply(f1, f2);
    }

    private final <Q extends QueryPart> Q parseFunctionArgs3(Function2<? super Field, ? super Field, ? extends Q> finisher2, Function3<? super Field, ? super Field, ? super Field, ? extends Q> finisher3) {
        parse('(');
        Field<?> f1 = parseField();
        parse(',');
        Field<?> f2 = parseField();
        Field<?> f3 = parseIf(',') ? parseField() : null;
        parse(')');
        return f3 == null ? finisher2.apply(f1, f2) : finisher3.apply(f1, f2, f3);
    }

    private final <Q extends QueryPart> Q parseFunctionArgs3(Function3<? super Field, ? super Field, ? super Field, ? extends Q> finisher) {
        parse('(');
        Field<?> f1 = parseField();
        parse(',');
        Field<?> f2 = parseField();
        parse(',');
        Field<?> f3 = parseField();
        parse(')');
        return finisher.apply(f1, f2, f3);
    }

    private final <Q extends QueryPart> Q parseFunctionArgs4(Function4<? super Field, ? super Field, ? super Field, ? super Field, ? extends Q> finisher) {
        parse('(');
        Field<?> f1 = parseField();
        parse(',');
        Field<?> f2 = parseField();
        parse(',');
        Field<?> f3 = parseField();
        parse(',');
        Field<?> f4 = parseField();
        parse(')');
        return finisher.apply(f1, f2, f3, f4);
    }

    private final boolean parseEmptyParens() {
        return parse('(') && parse(')');
    }

    private final boolean parseEmptyParensIf() {
        if (!parseIf('(') || !parse(')')) {
        }
        return true;
    }

    private final FieldOrRow parseNumericOp() {
        FieldOrRow l = parseSum();
        if (l instanceof Field) {
            while (true) {
                if (parseIf("<<")) {
                    l = ((Field) l).shl((Field<? extends Number>) parseSum());
                } else if (parseIf(">>")) {
                    l = ((Field) l).shr((Field<? extends Number>) parseSum());
                } else if (parseIf("->>")) {
                    Field r = (Field) parseSum();
                    if (r.getDataType().isNumeric()) {
                        if (((Field) l).getType() == JSON.class) {
                            l = DSL.jsonGetElementAsText((Field<JSON>) l, (Field<Integer>) r);
                        } else {
                            l = DSL.jsonbGetElementAsText((Field<JSONB>) l, (Field<Integer>) r);
                        }
                    } else if (((Field) l).getType() == JSON.class) {
                        l = DSL.jsonGetAttributeAsText((Field<JSON>) l, (Field<String>) r);
                    } else {
                        l = DSL.jsonbGetAttributeAsText((Field<JSONB>) l, (Field<String>) r);
                    }
                } else {
                    if (!parseIf("->")) {
                        break;
                    }
                    Field r2 = (Field) parseSum();
                    if (r2.getDataType().isNumeric()) {
                        if (((Field) l).getType() == JSON.class) {
                            l = DSL.jsonGetElement((Field<JSON>) l, (Field<Integer>) r2);
                        } else {
                            l = DSL.jsonbGetElement((Field<JSONB>) l, (Field<Integer>) r2);
                        }
                    } else if (((Field) l).getType() == JSON.class) {
                        l = DSL.jsonGetAttribute((Field<JSON>) l, (Field<String>) r2);
                    } else {
                        l = DSL.jsonbGetAttribute((Field<JSONB>) l, (Field<String>) r2);
                    }
                }
            }
        }
        return l;
    }

    private final FieldOrRow parseSum() {
        FieldOrRow r = parseFactor();
        if (r instanceof Field) {
            while (true) {
                if (parseIf('+')) {
                    r = parseSumRightOperand(r, true);
                } else {
                    if (peek("->") || !parseIf('-')) {
                        break;
                    }
                    r = parseSumRightOperand(r, false);
                }
            }
        }
        return r;
    }

    private final Field parseSumRightOperand(FieldOrRow r, boolean add) {
        Field rhs = (Field) parseFactor();
        if (!ignoreProEdition() && parseKeywordIf("YEAR", "YEARS") && requireProEdition()) {
            DatePart datePart = DatePart.YEAR;
        } else if (!ignoreProEdition() && parseKeywordIf("MONTH", "MONTHS") && requireProEdition()) {
            DatePart datePart2 = DatePart.MONTH;
        } else if (!ignoreProEdition() && parseKeywordIf("DAY", "DAYS") && requireProEdition()) {
            DatePart datePart3 = DatePart.DAY;
        } else if (!ignoreProEdition() && parseKeywordIf("HOUR", "HOURS") && requireProEdition()) {
            DatePart datePart4 = DatePart.HOUR;
        } else if (!ignoreProEdition() && parseKeywordIf("MINUTE", "MINUTES") && requireProEdition()) {
            DatePart datePart5 = DatePart.MINUTE;
        } else if (!ignoreProEdition() && parseKeywordIf("SECOND", "SECONDS") && requireProEdition()) {
            DatePart datePart6 = DatePart.SECOND;
        }
        Field lhs = (Field) r;
        if (add) {
            return lhs.add((Field<?>) rhs);
        }
        if (lhs.getDataType().isDate() && rhs.getDataType().isDate()) {
            return DSL.dateDiff((Field<Date>) lhs, (Field<Date>) rhs);
        }
        if (lhs.getDataType().isTimestamp() && rhs.getDataType().isTimestamp()) {
            return DSL.timestampDiff((Field<Timestamp>) lhs, (Field<Timestamp>) rhs);
        }
        return lhs.sub((Field<?>) rhs);
    }

    private final FieldOrRow parseFactor() {
        FieldOrRow r = parseExp();
        if (r instanceof Field) {
            while (true) {
                if (!peek("*=") && parseIf('*')) {
                    r = ((Field) r).mul((Field<? extends Number>) parseExp());
                } else if (parseIf('/')) {
                    r = ((Field) r).div((Field<? extends Number>) parseExp());
                } else {
                    if (!parseIf('%')) {
                        break;
                    }
                    r = ((Field) r).mod((Field<? extends Number>) parseExp());
                }
            }
        }
        return r;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private final FieldOrRow parseExp() {
        FieldOrRow r = parseUnaryOps();
        if (r instanceof Field) {
            while (true) {
                if ((peek("^=") || !parseIf('^')) && !parseIf(SecurityConstraint.ROLE_ALL_AUTHENTICATED_USERS)) {
                    break;
                }
                r = ((Field) r).pow((Field<? extends Number>) toField(parseUnaryOps()));
            }
        }
        return r;
    }

    private final FieldOrRow parseUnaryOps() {
        FieldOrRow r;
        if (ignoreProEdition() || !parseKeywordIf("CONNECT_BY_ROOT") || requireProEdition()) {
        }
        if (parseIf('~')) {
            return toField(parseUnaryOps()).bitNot();
        }
        Sign sign = parseSign();
        if (sign == Sign.NONE) {
            r = parseTerm();
        } else if (sign == Sign.PLUS) {
            r = toField(parseTerm());
        } else {
            Field<Number> parseFieldUnsignedNumericLiteralIf = parseFieldUnsignedNumericLiteralIf(Sign.MINUS);
            r = parseFieldUnsignedNumericLiteralIf;
            if (parseFieldUnsignedNumericLiteralIf == null) {
                r = toField(parseTerm()).neg();
            }
        }
        if (ignoreProEdition() || !parseTokensIf('(', '+', ')') || requireProEdition()) {
        }
        position();
        if ((r instanceof TableField) && parseIf('(')) {
            throw exception("Unknown function");
        }
        while (parseIf("::")) {
            r = DSL.cast(toField(r), (DataType) parseDataType());
        }
        if (parseIf('[')) {
            r = DSL.arrayGet((Field) toField(r), (Field<Integer>) parseField());
            parse(']');
        }
        return parseMethodCallIf(r);
    }

    private final FieldOrRow parseMethodCallIf(FieldOrRow r) {
        return r;
    }

    private final FieldOrRow parseMethodCallIf0(FieldOrRow r) {
        return r;
    }

    private final Sign parseSign() {
        Sign sign = Sign.NONE;
        while (true) {
            Sign sign2 = sign;
            if (parseIf('+')) {
                sign = sign2 == Sign.NONE ? Sign.PLUS : sign2;
            } else if (parseIf('-')) {
                sign = sign2 == Sign.NONE ? Sign.MINUS : sign2.invert();
            } else {
                return sign2;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: ParserImpl.java */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultParseContext$Sign.class */
    public enum Sign {
        NONE,
        PLUS,
        MINUS;

        final Sign invert() {
            if (this == PLUS) {
                return MINUS;
            }
            if (this == MINUS) {
                return PLUS;
            }
            return NONE;
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:504:0x09e1, code lost:            if (parseFunctionNameIf("GEOGRAPHY::STGEOMFROMWKB") != false) goto L509;     */
    /* JADX WARN: Code restructure failed: missing block: B:506:0x09e8, code lost:            if (requireProEdition() != false) goto L520;     */
    /* JADX WARN: Code restructure failed: missing block: B:512:0x0a06, code lost:            if (parseFunctionNameIf("GEOGRAPHY::STGEOMFROMTEXT") != false) goto L518;     */
    /* JADX WARN: Code restructure failed: missing block: B:514:0x0a0d, code lost:            if (requireProEdition() != false) goto L520;     */
    /* JADX WARN: Failed to find 'out' block for switch in B:2:0x0004. Please report as an issue. */
    /* JADX WARN: Removed duplicated region for block: B:1295:0x1838  */
    /* JADX WARN: Removed duplicated region for block: B:1297:0x1845  */
    /* JADX WARN: Removed duplicated region for block: B:1405:0x1a67  */
    /* JADX WARN: Removed duplicated region for block: B:1407:0x1a69  */
    /* JADX WARN: Removed duplicated region for block: B:517:0x0a1a  */
    /* JADX WARN: Removed duplicated region for block: B:519:0x0a22  */
    /* JADX WARN: Removed duplicated region for block: B:833:0x0fbc  */
    /* JADX WARN: Removed duplicated region for block: B:835:0x0fce  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private final org.jooq.FieldOrRow parseTerm() {
        /*
            Method dump skipped, instructions count: 6777
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jooq.impl.DefaultParseContext.parseTerm():org.jooq.FieldOrRow");
    }

    private final Field<?> parseFieldAddDatePart(DatePart part) {
        return (Field) parseFunctionArgs2((f1, f2) -> {
            return DSL.dateAdd((Field<Date>) f1, (Field<? extends Number>) f2, part);
        });
    }

    private final boolean peekSelectOrWith(boolean peekIntoParens) {
        return peekKeyword("WITH", false, peekIntoParens, false) || peekSelect(peekIntoParens);
    }

    private final boolean peekSelect(boolean peekIntoParens) {
        return peekKeyword("SELECT", false, peekIntoParens, false) || peekKeyword("SEL", false, peekIntoParens, false);
    }

    private final Field<?> parseFieldSysConnectByPathIf() {
        if (ignoreProEdition() || !parseFunctionNameIf("SYS_CONNECT_BY_PATH") || requireProEdition()) {
        }
        return null;
    }

    private final Field<?> parseFieldBitwiseFunctionIf() {
        int p = position();
        char c1 = character(p + 1);
        char c2 = character(p + 2);
        boolean agg = false;
        if (c1 != 'I' && c1 != 'i') {
            return null;
        }
        if (c2 != 'T' && c2 != 't' && c2 != 'N' && c2 != 'n') {
            return null;
        }
        if (!parseKeywordIf("BIT_AND") && !parseKeywordIf("BITWISE_AND") && !parseKeywordIf("BITAND") && !parseKeywordIf("BIN_AND")) {
            boolean parseKeywordIf = parseKeywordIf("BIT_AND_AGG");
            agg = parseKeywordIf;
            if (!parseKeywordIf) {
                boolean parseKeywordIf2 = parseKeywordIf("BITWISE_AND_AGG");
                agg = parseKeywordIf2;
                if (!parseKeywordIf2) {
                    boolean parseKeywordIf3 = parseKeywordIf("BITAND_AGG");
                    agg = parseKeywordIf3;
                    if (!parseKeywordIf3) {
                        boolean parseKeywordIf4 = parseKeywordIf("BIN_AND_AGG");
                        agg = parseKeywordIf4;
                        if (!parseKeywordIf4) {
                            if (!parseKeywordIf("BIT_NAND") && !parseKeywordIf("BITNAND") && !parseKeywordIf("BIN_NAND")) {
                                boolean parseKeywordIf5 = parseKeywordIf("BIT_NAND_AGG");
                                agg = parseKeywordIf5;
                                if (!parseKeywordIf5) {
                                    boolean parseKeywordIf6 = parseKeywordIf("BITNAND_AGG");
                                    agg = parseKeywordIf6;
                                    if (!parseKeywordIf6) {
                                        boolean parseKeywordIf7 = parseKeywordIf("BIN_NAND_AGG");
                                        agg = parseKeywordIf7;
                                        if (!parseKeywordIf7) {
                                            if (!parseKeywordIf("BIT_OR") && !parseKeywordIf("BITWISE_OR") && !parseKeywordIf("BITOR") && !parseKeywordIf("BIN_OR")) {
                                                boolean parseKeywordIf8 = parseKeywordIf("BIT_OR_AGG");
                                                agg = parseKeywordIf8;
                                                if (!parseKeywordIf8) {
                                                    boolean parseKeywordIf9 = parseKeywordIf("BITWISE_OR_AGG");
                                                    agg = parseKeywordIf9;
                                                    if (!parseKeywordIf9) {
                                                        boolean parseKeywordIf10 = parseKeywordIf("BITOR_AGG");
                                                        agg = parseKeywordIf10;
                                                        if (!parseKeywordIf10) {
                                                            boolean parseKeywordIf11 = parseKeywordIf("BIN_OR_AGG");
                                                            agg = parseKeywordIf11;
                                                            if (!parseKeywordIf11) {
                                                                if (!parseKeywordIf("BIT_NOR") && !parseKeywordIf("BITNOR") && !parseKeywordIf("BIN_NOR")) {
                                                                    boolean parseKeywordIf12 = parseKeywordIf("BIT_NOR_AGG");
                                                                    agg = parseKeywordIf12;
                                                                    if (!parseKeywordIf12) {
                                                                        boolean parseKeywordIf13 = parseKeywordIf("BITNOR_AGG");
                                                                        agg = parseKeywordIf13;
                                                                        if (!parseKeywordIf13) {
                                                                            boolean parseKeywordIf14 = parseKeywordIf("BIN_NOR_AGG");
                                                                            agg = parseKeywordIf14;
                                                                            if (!parseKeywordIf14) {
                                                                                if (!parseKeywordIf("BIT_XOR") && !parseKeywordIf("BITWISE_XOR") && !parseKeywordIf("BITXOR") && !parseKeywordIf("BIN_XOR")) {
                                                                                    boolean parseKeywordIf15 = parseKeywordIf("BIT_XOR_AGG");
                                                                                    agg = parseKeywordIf15;
                                                                                    if (!parseKeywordIf15) {
                                                                                        boolean parseKeywordIf16 = parseKeywordIf("BITXOR_AGG");
                                                                                        agg = parseKeywordIf16;
                                                                                        if (!parseKeywordIf16) {
                                                                                            boolean parseKeywordIf17 = parseKeywordIf("BIN_XOR_AGG");
                                                                                            agg = parseKeywordIf17;
                                                                                            if (!parseKeywordIf17) {
                                                                                                if (!parseKeywordIf("BIT_XNOR") && !parseKeywordIf("BITXNOR") && !parseKeywordIf("BIN_XNOR")) {
                                                                                                    boolean parseKeywordIf18 = parseKeywordIf("BIT_XNOR_AGG");
                                                                                                    agg = parseKeywordIf18;
                                                                                                    if (!parseKeywordIf18) {
                                                                                                        boolean parseKeywordIf19 = parseKeywordIf("BITXNOR_AGG");
                                                                                                        agg = parseKeywordIf19;
                                                                                                        if (!parseKeywordIf19) {
                                                                                                            boolean parseKeywordIf20 = parseKeywordIf("BIN_XNOR_AGG");
                                                                                                            agg = parseKeywordIf20;
                                                                                                            if (!parseKeywordIf20) {
                                                                                                                if (parseKeywordIf("BIT_NOT", "BITNOT", "BIN_NOT", "BITWISE_NOT")) {
                                                                                                                    parse('(');
                                                                                                                    Field<?> x = toField(parseNumericOp());
                                                                                                                    parse(')');
                                                                                                                    return DSL.bitNot(x);
                                                                                                                }
                                                                                                                if (parseKeywordIf("BIN_SHL", "BITSHIFTLEFT", "BITWISE_LEFT_SHIFT")) {
                                                                                                                    parse('(');
                                                                                                                    Field<?> x2 = toField(parseNumericOp());
                                                                                                                    parse(',');
                                                                                                                    Field<?> y = toField(parseNumericOp());
                                                                                                                    parse(')');
                                                                                                                    return DSL.shl((Field) x2, (Field<? extends Number>) y);
                                                                                                                }
                                                                                                                if (parseKeywordIf("BIN_SHR", "BITSHIFTRIGHT", "BITWISE_RIGHT_SHIFT")) {
                                                                                                                    parse('(');
                                                                                                                    Field<?> x3 = toField(parseNumericOp());
                                                                                                                    parse(',');
                                                                                                                    Field<?> y2 = toField(parseNumericOp());
                                                                                                                    parse(')');
                                                                                                                    return DSL.shr((Field) x3, (Field<? extends Number>) y2);
                                                                                                                }
                                                                                                                return null;
                                                                                                            }
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                                parse('(');
                                                                                                Field<?> x4 = toField(parseNumericOp());
                                                                                                if ((agg && parse(')')) || parseIf(')')) {
                                                                                                    return parseAggregateFunctionIf(false, DSL.bitXNorAgg(x4));
                                                                                                }
                                                                                                parse(',');
                                                                                                Field<?> y3 = toField(parseNumericOp());
                                                                                                parse(')');
                                                                                                return DSL.bitXNor(x4, y3);
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                                parse('(');
                                                                                Field<?> x5 = toField(parseNumericOp());
                                                                                if ((agg && parse(')')) || parseIf(')')) {
                                                                                    return parseAggregateFunctionIf(false, DSL.bitXorAgg(x5));
                                                                                }
                                                                                parse(',');
                                                                                Field<?> y4 = toField(parseNumericOp());
                                                                                parse(')');
                                                                                return DSL.bitXor(x5, y4);
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                                parse('(');
                                                                if (parseKeywordIf("DISTINCT", "ALL")) {
                                                                    agg = true;
                                                                }
                                                                Field<?> x6 = toField(parseNumericOp());
                                                                if ((agg && parse(')')) || parseIf(')')) {
                                                                    return parseAggregateFunctionIf(false, DSL.bitNorAgg(x6));
                                                                }
                                                                parse(',');
                                                                Field<?> y5 = toField(parseNumericOp());
                                                                parse(')');
                                                                return DSL.bitNor(x6, y5);
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            parse('(');
                                            if (parseKeywordIf("DISTINCT", "ALL")) {
                                                agg = true;
                                            }
                                            Field<?> x7 = toField(parseNumericOp());
                                            if ((agg && parse(')')) || parseIf(')')) {
                                                return parseAggregateFunctionIf(false, DSL.bitOrAgg(x7));
                                            }
                                            parse(',');
                                            Field<?> y6 = toField(parseNumericOp());
                                            parse(')');
                                            return DSL.bitOr(x7, y6);
                                        }
                                    }
                                }
                            }
                            parse('(');
                            if (parseKeywordIf("DISTINCT", "ALL")) {
                                agg = true;
                            }
                            Field<?> x8 = toField(parseNumericOp());
                            if ((agg && parse(')')) || parseIf(')')) {
                                return parseAggregateFunctionIf(false, DSL.bitNandAgg(x8));
                            }
                            parse(',');
                            Field<?> y7 = toField(parseNumericOp());
                            parse(')');
                            return DSL.bitNand(x8, y7);
                        }
                    }
                }
            }
        }
        parse('(');
        if (parseKeywordIf("DISTINCT", "ALL")) {
            agg = true;
        }
        Field<?> x9 = toField(parseNumericOp());
        if ((agg && parse(')')) || parseIf(')')) {
            return parseAggregateFunctionIf(false, DSL.bitAndAgg(x9));
        }
        parse(',');
        Field<?> y8 = toField(parseNumericOp());
        parse(')');
        return DSL.bitAnd(x9, y8);
    }

    private final Field<?> parseFieldNewIdIf() {
        if (parseFunctionNameIf("NEWID")) {
            parse('(');
            Long l = parseSignedIntegerLiteralIf();
            if (l != null && l.longValue() != -1) {
                throw expected("No argument or -1 expected");
            }
            parse(')');
            return DSL.uuid();
        }
        return null;
    }

    private final Field<?> parseNextValueIf() {
        if (parseKeywordIf("NEXT VALUE FOR")) {
            return DSL.sequence(parseName()).nextval();
        }
        return null;
    }

    private final Field<?> parseNextvalCurrvalIf(SequenceMethod method) {
        Sequence sequence;
        if (parseFunctionNameIf(method.name())) {
            parse('(');
            Name name = parseNameIf();
            if (name != null) {
                sequence = DSL.sequence(name);
            } else {
                sequence = DSL.sequence(this.dsl.parser().parseName(parseStringLiteral()));
            }
            Sequence s = sequence;
            parse(')');
            if (method == SequenceMethod.NEXTVAL) {
                return s.nextval();
            }
            if (method == SequenceMethod.CURRVAL) {
                return s.currval();
            }
            throw exception("Only NEXTVAL and CURRVAL methods supported");
        }
        return null;
    }

    private final Field<?> parseFieldXMLSerializeIf() {
        if (parseFunctionNameIf("XMLSERIALIZE")) {
            parse('(');
            boolean content = parseKeywordIf("CONTENT");
            if (!content) {
                parseKeywordIf("DOCUMENT");
            }
            Field<?> parseField = parseField();
            parseKeyword("AS");
            DataType<?> type = parseCastDataType();
            parse(')');
            return content ? DSL.xmlserializeContent((Field<XML>) parseField, (DataType) type) : DSL.xmlserializeDocument((Field<XML>) parseField, (DataType) type);
        }
        return null;
    }

    private final Field<?> parseFieldXMLConcatIf() {
        if (parseFunctionNameIf("XMLCONCAT")) {
            parse('(');
            List<Field<?>> fields = parseList(',', c -> {
                return c.parseField();
            });
            parse(')');
            return DSL.xmlconcat(fields);
        }
        return null;
    }

    private final Field<?> parseFieldXMLElementIf() {
        if (parseFunctionNameIf("XMLELEMENT")) {
            parse('(');
            parseKeywordIf("NAME");
            if (parseIf(')')) {
                return DSL.xmlelement(DSL.systemName("NAME"), (Field<?>[]) new Field[0]);
            }
            Name name = parseIdentifier();
            XMLAttributes attr = null;
            List<Field<?>> content = new ArrayList<>();
            while (parseIf(',')) {
                if (attr == null && parseKeywordIf("XMLATTRIBUTES")) {
                    parse('(');
                    List<Field<?>> attrs = parseAliasedXMLContent();
                    parse(')');
                    attr = DSL.xmlattributes(attrs);
                } else {
                    content.add(parseField());
                }
            }
            parse(')');
            if (attr == null) {
                return DSL.xmlelement(name, content);
            }
            return DSL.xmlelement(name, attr, content);
        }
        return null;
    }

    private final Field<?> parseFieldXMLDocumentIf() {
        if (ignoreProEdition() || !parseFunctionNameIf("XMLDOCUMENT") || requireProEdition()) {
        }
        return null;
    }

    private final Field<?> parseFieldXMLPIIf() {
        if (parseFunctionNameIf("XMLPI")) {
            parse('(');
            parseKeyword("NAME");
            Name target = parseIdentifier();
            Field<?> content = parseIf(',') ? parseField() : null;
            parse(')');
            return content == null ? DSL.xmlpi(target) : DSL.xmlpi(target, content);
        }
        return null;
    }

    private final Field<?> parseFieldXMLForestIf() {
        if (parseFunctionNameIf("XMLFOREST")) {
            parse('(');
            List<Field<?>> content = parseAliasedXMLContent();
            parse(')');
            return DSL.xmlforest(content);
        }
        return null;
    }

    private final Field<?> parseFieldXMLParseIf() {
        QOM.DocumentOrContent documentOrContent;
        if (parseFunctionNameIf("XMLPARSE")) {
            parse('(');
            if (parseKeywordIf("DOCUMENT")) {
                documentOrContent = QOM.DocumentOrContent.DOCUMENT;
            } else if (parseKeywordIf("CONTENT")) {
                documentOrContent = QOM.DocumentOrContent.CONTENT;
            } else {
                throw expected("CONTENT", "DOCUMENT");
            }
            Field<?> parseField = parseField();
            parse(')');
            if (documentOrContent == QOM.DocumentOrContent.DOCUMENT) {
                return DSL.xmlparseDocument((Field<String>) parseField);
            }
            return DSL.xmlparseContent((Field<String>) parseField);
        }
        return null;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private final Field<?> parseFieldXMLQueryIf() {
        if (parseFunctionNameIf("XMLQUERY")) {
            parse('(');
            Field<?> parseField = parseField();
            QOM.XMLPassingMechanism m = parseXMLPassingMechanism();
            Field<?> parseField2 = parseField();
            parseKeywordIf("RETURNING CONTENT");
            parse(')');
            if (m == QOM.XMLPassingMechanism.BY_REF) {
                return DSL.xmlquery((Field<String>) parseField).passingByRef((Field<XML>) parseField2);
            }
            return DSL.xmlquery((Field<String>) parseField).passing((Field<XML>) parseField2);
        }
        return null;
    }

    private final QOM.XMLPassingMechanism parseXMLPassingMechanism() {
        QOM.XMLPassingMechanism result = parseXMLPassingMechanismIf();
        if (result == null) {
            throw expected("PASSING");
        }
        return result;
    }

    private final QOM.XMLPassingMechanism parseXMLPassingMechanismIf() {
        if (!parseKeywordIf("PASSING")) {
            return null;
        }
        if (!parseKeywordIf("BY")) {
            return QOM.XMLPassingMechanism.DEFAULT;
        }
        if (parseKeywordIf("REF")) {
            return QOM.XMLPassingMechanism.BY_REF;
        }
        if (parseKeywordIf("VALUE")) {
            return QOM.XMLPassingMechanism.BY_VALUE;
        }
        throw expected("REF", "VALUE");
    }

    private final List<Field<?>> parseAliasedXMLContent() {
        List<Field<?>> result = new ArrayList<>();
        do {
            Field<?> field = parseField();
            if (parseKeywordIf("AS")) {
                field = field.as(parseIdentifier(true, false));
            }
            result.add(field);
        } while (parseIf(','));
        return result;
    }

    private final AggregateFilterStep<?> parseXMLAggFunctionIf() {
        if (parseFunctionNameIf("XMLAGG")) {
            parse('(');
            parseKeywordIf("ALL");
            XMLAggOrderByStep<XML> xmlagg = DSL.xmlagg(parseField());
            AggregateFilterStep<?> s2 = xmlagg;
            if (parseKeywordIf("ORDER BY")) {
                s2 = xmlagg.orderBy(parseList(',', c -> {
                    return c.parseSortField();
                }));
            }
            parse(')');
            return s2;
        }
        return null;
    }

    private final Field<?> parseFieldJSONValueIf() {
        if (parseFunctionNameIf("JSON_VALUE")) {
            parse('(');
            Field json = parseField();
            parse(',');
            JSONValueOnStep<?> s1 = DSL.jsonValue((Field<JSON>) json, (Field<String>) parseField());
            parseJSONValueBehaviourIf();
            DataType<?> returning = parseJSONReturningIf();
            parse(')');
            return returning == null ? s1 : s1.returning(returning);
        }
        return null;
    }

    private final JSONValue.Behaviour parseJSONValueBehaviourIf() {
        if (!ignoreProEdition() && parseKeywordIf("ERROR") && requireProEdition()) {
            return JSONValue.Behaviour.ERROR;
        }
        if (!ignoreProEdition() && parseKeywordIf(JoranConstants.NULL) && requireProEdition()) {
            return JSONValue.Behaviour.NULL;
        }
        if (!ignoreProEdition() && parseKeywordIf("DEFAULT") && requireProEdition()) {
            return JSONValue.Behaviour.DEFAULT;
        }
        return null;
    }

    private final JSONExists.Behaviour parseJSONExistsOnErrorBehaviourIf() {
        if (!ignoreProEdition() && parseKeywordIf("ERROR") && parseKeyword("ON ERROR") && requireProEdition()) {
            return JSONExists.Behaviour.ERROR;
        }
        if (!ignoreProEdition() && parseKeywordIf("TRUE") && parseKeyword("ON ERROR") && requireProEdition()) {
            return JSONExists.Behaviour.TRUE;
        }
        if (!ignoreProEdition() && parseKeywordIf("FALSE") && parseKeyword("ON ERROR") && requireProEdition()) {
            return JSONExists.Behaviour.FALSE;
        }
        if (!ignoreProEdition() && parseKeywordIf("UNKNOWN") && parseKeyword("ON ERROR") && requireProEdition()) {
            return JSONExists.Behaviour.UNKNOWN;
        }
        return null;
    }

    private final DataType<?> parseJSONReturningIf() {
        if (parseKeywordIf("RETURNING")) {
            return parseDataType();
        }
        return null;
    }

    private final Field<?> parseFieldJSONLiteralIf() {
        if (parseKeywordIf(JsonFactory.FORMAT_NAME_JSON)) {
            if (parseIf('{')) {
                if (parseIf('}')) {
                    return DSL.jsonObject();
                }
                List<JSONEntry<?>> entries = parseList(',', ctx -> {
                    Field key = parseField();
                    parse(':');
                    return DSL.key((Field<String>) key).value((Field) parseField());
                });
                parse('}');
                return DSL.jsonObject(entries);
            }
            if (parseIf('[')) {
                if (parseIf(']')) {
                    return DSL.jsonArray((Field<?>[]) new Field[0]);
                }
                List<Field<?>> fields = parseList(',', c -> {
                    return parseField();
                });
                parse(']');
                return DSL.jsonArray(fields);
            }
            throw expected(PropertyAccessor.PROPERTY_KEY_PREFIX, "{");
        }
        return null;
    }

    private final Field<?> parseFieldJSONArrayConstructorIf() {
        JSONArrayNullStep<?> jsonbArray;
        JSONArrayReturningStep<?> jSONArrayReturningStep;
        boolean jsonb = false;
        if (!parseFunctionNameIf("JSON_ARRAY", "JSON_BUILD_ARRAY")) {
            boolean parseFunctionNameIf = parseFunctionNameIf("JSONB_BUILD_ARRAY");
            jsonb = parseFunctionNameIf;
            if (!parseFunctionNameIf) {
                return null;
            }
        }
        parse('(');
        if (parseIf(')')) {
            return jsonb ? DSL.jsonbArray((Field<?>[]) new Field[0]) : DSL.jsonArray((Field<?>[]) new Field[0]);
        }
        List<Field<?>> result = null;
        QOM.JSONOnNull onNull = parseJSONNullTypeIf();
        DataType<?> returning = parseJSONReturningIf();
        if (onNull == null && returning == null) {
            result = parseList(',', c -> {
                return c.parseField();
            });
            onNull = parseJSONNullTypeIf();
            returning = parseJSONReturningIf();
        }
        parse(')');
        if (result == null) {
            jsonbArray = jsonb ? DSL.jsonbArray((Field<?>[]) new Field[0]) : DSL.jsonArray((Field<?>[]) new Field[0]);
        } else {
            jsonbArray = jsonb ? DSL.jsonbArray(result) : DSL.jsonArray(result);
        }
        JSONArrayNullStep<?> s1 = jsonbArray;
        if (onNull == QOM.JSONOnNull.NULL_ON_NULL) {
            jSONArrayReturningStep = s1.nullOnNull();
        } else if (onNull == QOM.JSONOnNull.ABSENT_ON_NULL) {
            jSONArrayReturningStep = s1.absentOnNull();
        } else {
            jSONArrayReturningStep = s1;
        }
        JSONArrayReturningStep<?> s2 = jSONArrayReturningStep;
        return returning == null ? s2 : s2.returning(returning);
    }

    private final AggregateFilterStep<?> parseJSONArrayAggFunctionIf() {
        JSONArrayAggOrderByStep<?> jsonArrayAggDistinct;
        boolean jsonb = false;
        if (!parseFunctionNameIf("JSON_ARRAYAGG", "JSON_AGG", "JSON_GROUP_ARRAY")) {
            boolean parseFunctionNameIf = parseFunctionNameIf("JSONB_AGG");
            jsonb = parseFunctionNameIf;
            if (!parseFunctionNameIf) {
                return null;
            }
        }
        parse('(');
        boolean distinct = parseSetQuantifier();
        if (jsonb) {
            jsonArrayAggDistinct = distinct ? DSL.jsonbArrayAggDistinct(parseField()) : DSL.jsonbArrayAgg(parseField());
        } else {
            jsonArrayAggDistinct = distinct ? DSL.jsonArrayAggDistinct(parseField()) : DSL.jsonArrayAgg(parseField());
        }
        JSONArrayAggOrderByStep<?> s1 = jsonArrayAggDistinct;
        JSONArrayAggNullStep<?> s2 = jsonArrayAggDistinct;
        JSONArrayAggReturningStep<?> s3 = jsonArrayAggDistinct;
        AggregateFilterStep<?> result = jsonArrayAggDistinct;
        if (parseKeywordIf("ORDER BY")) {
            JSONArrayAggReturningStep<?> orderBy = s1.orderBy(parseList(',', c -> {
                return c.parseSortField();
            }));
            s2 = orderBy;
            s3 = orderBy;
            result = orderBy;
        }
        QOM.JSONOnNull onNull = parseJSONNullTypeIf();
        if (onNull != null) {
            JSONArrayAggReturningStep<?> absentOnNull = onNull == QOM.JSONOnNull.ABSENT_ON_NULL ? s2.absentOnNull() : s2.nullOnNull();
            s3 = absentOnNull;
            result = absentOnNull;
        }
        DataType<?> returning = parseJSONReturningIf();
        if (returning != null) {
            result = s3.returning(returning);
        }
        parse(')');
        return result;
    }

    private final Field<?> parseFieldArrayConstructIf() {
        if ((parseFunctionNameIf("ARRAY_CONSTRUCT") || parseFunctionNameIf("ARRAY_CONSTRUCT_COMPACT")) && requireProEdition()) {
        }
        return null;
    }

    private final Field<?> parseFieldObjectConstructIf() {
        if ((parseFunctionNameIf("OBJECT_CONSTRUCT") || parseFunctionNameIf("OBJECT_CONSTRUCT_KEEP_NULL")) && requireProEdition()) {
        }
        return null;
    }

    private final Field<?> parseFieldJSONObjectConstructorIf() {
        List<JSONEntry<?>> result;
        JSONObjectReturningStep<?> jSONObjectReturningStep;
        boolean jsonb = false;
        if (!parseFunctionNameIf("JSON_OBJECT", "JSON_BUILD_OBJECT")) {
            boolean parseFunctionNameIf = parseFunctionNameIf("JSONB_BUILD_OBJECT");
            jsonb = parseFunctionNameIf;
            if (!parseFunctionNameIf) {
                return null;
            }
        }
        parse('(');
        if (parseIf(')')) {
            return jsonb ? DSL.jsonbObject() : DSL.jsonObject();
        }
        QOM.JSONOnNull onNull = parseJSONNullTypeIf();
        DataType<?> returning = parseJSONReturningIf();
        if (onNull == null && returning == null) {
            result = parseList(',', c -> {
                return parseJSONEntry();
            });
            onNull = parseJSONNullTypeIf();
            returning = parseJSONReturningIf();
        } else {
            result = new ArrayList<>();
        }
        parse(')');
        JSONObjectNullStep<?> s1 = jsonb ? DSL.jsonbObject(result) : DSL.jsonObject(result);
        if (onNull == QOM.JSONOnNull.NULL_ON_NULL) {
            jSONObjectReturningStep = s1.nullOnNull();
        } else if (onNull == QOM.JSONOnNull.ABSENT_ON_NULL) {
            jSONObjectReturningStep = s1.absentOnNull();
        } else {
            jSONObjectReturningStep = s1;
        }
        JSONObjectReturningStep<?> s2 = jSONObjectReturningStep;
        return returning == null ? s2 : s2.returning(returning);
    }

    private final AggregateFilterStep<?> parseJSONObjectAggFunctionIf() {
        boolean jsonb = false;
        if (!parseFunctionNameIf("JSON_OBJECTAGG", "JSON_OBJECT_AGG", "JSON_GROUP_OBJECT")) {
            boolean parseFunctionNameIf = parseFunctionNameIf("JSONB_OBJECT_AGG");
            jsonb = parseFunctionNameIf;
            if (!parseFunctionNameIf) {
                if (!parseFunctionNameIf("OBJECT_AGG") || requireProEdition()) {
                }
                return null;
            }
        }
        parse('(');
        parseKeywordIf("ALL");
        AggregateFilterStep<?> jsonbObjectAgg = jsonb ? DSL.jsonbObjectAgg(parseJSONEntry()) : DSL.jsonObjectAgg(parseJSONEntry());
        JSONObjectAggNullStep<?> s1 = jsonbObjectAgg;
        JSONObjectAggReturningStep<?> s2 = jsonbObjectAgg;
        AggregateFilterStep<?> result = jsonbObjectAgg;
        QOM.JSONOnNull onNull = parseJSONNullTypeIf();
        if (onNull != null) {
            JSONObjectAggReturningStep<?> absentOnNull = onNull == QOM.JSONOnNull.ABSENT_ON_NULL ? s1.absentOnNull() : s1.nullOnNull();
            s2 = absentOnNull;
            result = absentOnNull;
        }
        DataType<?> returning = parseJSONReturningIf();
        if (returning != null) {
            result = s2.returning(returning);
        }
        parse(')');
        return result;
    }

    private final QOM.JSONOnNull parseJSONNullTypeIf() {
        if (parseKeywordIf("NULL ON NULL")) {
            return QOM.JSONOnNull.NULL_ON_NULL;
        }
        if (parseKeywordIf("ABSENT ON NULL")) {
            return QOM.JSONOnNull.ABSENT_ON_NULL;
        }
        return null;
    }

    private final JSONEntry<?> parseJSONEntry() {
        return parseJSONEntry(true);
    }

    private final JSONEntry<?> parseJSONEntry(boolean supportKeyValue) {
        boolean valueRequired = supportKeyValue && parseKeywordIf("KEY");
        Field<?> parseField = parseField();
        if (!supportKeyValue || !parseKeywordIf("VALUE")) {
            if (valueRequired) {
                throw expected("VALUE");
            }
            parse(',');
        }
        return DSL.key((Field<String>) parseField).value((Field) parseField());
    }

    private final Field<?> parseArrayValueConstructorIf() {
        List<Field<?>> fields;
        if (parseKeywordIf("ARRAY")) {
            if (parseIf('[')) {
                if (parseIf(']')) {
                    fields = Collections.emptyList();
                } else {
                    fields = parseList(',', c -> {
                        return c.parseField();
                    });
                    parse(']');
                }
                return DSL.array(fields);
            }
            if (parseIf('(')) {
                SelectQueryImpl select = parseWithOrSelect(1);
                parse(')');
                return DSL.array(select);
            }
            throw expected(PropertyAccessor.PROPERTY_KEY_PREFIX, "(");
        }
        return null;
    }

    private final Field<?> parseMultisetValueConstructorIf() {
        if (parseKeywordIf("MULTISET")) {
            if (parseIf('(')) {
                SelectQueryImpl select = parseWithOrSelect();
                parse(')');
                return DSL.multiset(select);
            }
            throw expected("(");
        }
        return null;
    }

    private final Field<?> parseFieldLogIf() {
        if (parseFunctionNameIf("LOG")) {
            parse('(');
            Field f1 = toField(parseNumericOp());
            Field f2 = parseIf(',') ? toField(parseNumericOp()) : null;
            parse(')');
            switch (parseFamily()) {
                case POSTGRES:
                case SQLITE:
                case YUGABYTEDB:
                    return f2 == null ? DSL.log10((Field<? extends Number>) f1) : DSL.log((Field<? extends Number>) f2, (Field<? extends Number>) f1);
                default:
                    return f2 == null ? DSL.ln((Field<? extends Number>) f1) : DSL.log((Field<? extends Number>) f2, (Field<? extends Number>) f1);
            }
        }
        return null;
    }

    private final Field<?> parseFieldTruncIf() {
        String part;
        DatePart p;
        boolean forceNumericPrecision = false;
        if (!parseFunctionNameIf("TRUNC")) {
            boolean parseFunctionNameIf = false | parseFunctionNameIf("TRUNCATE", "TRUNCNUM");
            forceNumericPrecision = parseFunctionNameIf;
            if (!parseFunctionNameIf) {
                return null;
            }
        }
        parse('(');
        Field<?> arg1 = parseField();
        if ((forceNumericPrecision && parse(',')) || parseIf(',')) {
            if (!forceNumericPrecision && (part = parseStringLiteralIf()) != null) {
                String part2 = part.toUpperCase();
                if ("YY".equals(part2) || "YYYY".equals(part2) || "YEAR".equals(part2)) {
                    p = DatePart.YEAR;
                } else if ("MM".equals(part2) || "MONTH".equals(part2)) {
                    p = DatePart.MONTH;
                } else if ("DD".equals(part2)) {
                    p = DatePart.DAY;
                } else if ("HH".equals(part2)) {
                    p = DatePart.HOUR;
                } else if ("MI".equals(part2)) {
                    p = DatePart.MINUTE;
                } else if ("SS".equals(part2)) {
                    p = DatePart.SECOND;
                } else {
                    throw exception("Unsupported date part");
                }
                parse(')');
                return DSL.trunc(arg1, p);
            }
            Field<?> arg2 = toField(parseNumericOp());
            parse(')');
            return DSL.trunc((Field) arg1, (Field<Integer>) arg2);
        }
        parse(')');
        if (arg1 instanceof CurrentDate) {
            return arg1;
        }
        if (arg1.getDataType().isDateTime()) {
            return DSL.trunc(arg1, DatePart.DAY);
        }
        if (arg1.getDataType().isNumeric()) {
            return DSL.trunc(arg1, DSL.inline(0));
        }
        return DSL.trunc(arg1);
    }

    private final Field<?> parseFieldRoundIf() {
        if (parseFunctionNameIf("ROUND")) {
            parse('(');
            Field arg1 = toField(parseNumericOp());
            Field arg2 = parseIf(',') ? toField(parseNumericOp()) : null;
            parse(')');
            return arg2 == null ? DSL.round(arg1) : DSL.round(arg1, (Field<Integer>) arg2);
        }
        return null;
    }

    private final Field<?> parseFieldLeastIf() {
        if (parseFunctionNameIf("LEAST", "MINVALUE")) {
            parse('(');
            List<Field<?>> fields = parseList(',', c -> {
                return c.parseField();
            });
            parse(')');
            return DSL.least((Field) fields.get(0), fields.size() > 1 ? (Field[]) fields.subList(1, fields.size()).toArray(Tools.EMPTY_FIELD) : Tools.EMPTY_FIELD);
        }
        return null;
    }

    private final Field<?> parseFieldGreatestIf() {
        if (parseFunctionNameIf("GREATEST", "MAXVALUE")) {
            parse('(');
            List<Field<?>> fields = parseList(',', c -> {
                return c.parseField();
            });
            parse(')');
            return DSL.greatest((Field) fields.get(0), fields.size() > 1 ? (Field[]) fields.subList(1, fields.size()).toArray(Tools.EMPTY_FIELD) : Tools.EMPTY_FIELD);
        }
        return null;
    }

    private final Field<?> parseFieldGroupingIdIf() {
        if (ignoreProEdition() || !parseFunctionNameIf("GROUPING_ID") || requireProEdition()) {
        }
        return null;
    }

    private final Field<?> parseFieldTimestampLiteralIf() {
        int p = position();
        if (parseKeywordIf("TIMESTAMP")) {
            if (parseKeywordIf("WITHOUT TIME ZONE")) {
                return DSL.inline(parseTimestampLiteral());
            }
            if (parseIf('(')) {
                Field<?> f = parseField();
                parse(')');
                return DSL.timestamp((Field<? extends java.util.Date>) f);
            }
            if (peek('\'')) {
                return DSL.inline(parseTimestampLiteral());
            }
            position(p);
            return DSL.field(parseIdentifier());
        }
        return null;
    }

    private final Timestamp parseTimestampLiteral() {
        Timestamp timestamp = (Timestamp) Convert.convert(parseStringLiteral(), Timestamp.class);
        if (timestamp == null) {
            throw exception("Illegal timestamp literal");
        }
        return timestamp;
    }

    private final Field<?> parseFieldTimeLiteralIf() {
        int p = position();
        if (parseKeywordIf("TIME")) {
            if (parseKeywordIf("WITHOUT TIME ZONE")) {
                return DSL.inline(parseTimeLiteral());
            }
            if (parseIf('(')) {
                Field<?> f = parseField();
                parse(')');
                return DSL.time((Field<? extends java.util.Date>) f);
            }
            if (peek('\'')) {
                return DSL.inline(parseTimeLiteral());
            }
            position(p);
            return DSL.field(parseIdentifier());
        }
        return null;
    }

    private final Time parseTimeLiteral() {
        Time time = (Time) Convert.convert(parseStringLiteral(), Time.class);
        if (time == null) {
            throw exception("Illegal time literal");
        }
        return time;
    }

    private final Field<?> parseFieldIntervalLiteralIf(boolean parseUnknownSyntaxAsIdentifier) {
        int p = position();
        if (parseKeywordIf("INTERVAL")) {
            if (peek('\'')) {
                return DSL.inline(parseIntervalLiteral());
            }
            Long interval = parseUnsignedIntegerLiteralIf();
            if (interval != null) {
                DatePart part = parseIntervalDatePart();
                long l = interval.longValue();
                int i = Tools.asInt(l);
                switch (part) {
                    case YEAR:
                        return DSL.inline(new YearToMonth(i));
                    case QUARTER:
                        return DSL.inline(new YearToMonth(0, 3 * i));
                    case MONTH:
                        return DSL.inline(new YearToMonth(0, i));
                    case WEEK:
                        return DSL.inline(new DayToSecond(7 * i));
                    case DAY:
                        return DSL.inline(new DayToSecond(i));
                    case HOUR:
                        return DSL.inline(new DayToSecond(0, i));
                    case MINUTE:
                        return DSL.inline(new DayToSecond(0, 0, i));
                    case SECOND:
                        return DSL.inline(new DayToSecond(0, 0, 0, i));
                    case MILLISECOND:
                        return DSL.inline(new DayToSecond(0, 0, 0, Tools.asInt(l / 1000), (int) ((l % 1000) * 1000000)));
                    case MICROSECOND:
                        return DSL.inline(new DayToSecond(0, 0, 0, Tools.asInt(l / 1000000), (int) ((l % 1000000) * 1000)));
                    case NANOSECOND:
                        return DSL.inline(new DayToSecond(0, 0, 0, Tools.asInt(l / 1000000000), (int) (l % 1000000000)));
                    default:
                        return null;
                }
            }
            position(p);
            if (parseUnknownSyntaxAsIdentifier) {
                return DSL.field(parseIdentifier());
            }
            return null;
        }
        return null;
    }

    private final Field<?> parseFieldMySQLIntervalLiteralIf(BiFunction<? super Field<?>, ? super DatePart, ? extends Field<?>> f) {
        if (parseKeywordIf("INTERVAL")) {
            Field<?> interval = parseField();
            DatePart part = parseIntervalDatePart();
            return f.apply(interval, part);
        }
        return null;
    }

    /* JADX WARN: Failed to find 'out' block for switch in B:13:0x0059. Please report as an issue. */
    private final Interval parsePostgresIntervalLiteralIf() {
        int p = position();
        if (parseIf('\'')) {
            parseIf('@');
            Number year = null;
            Number month = null;
            Number day = null;
            Number hour = null;
            Number minute = null;
            Number second = null;
            do {
                boolean minus = parseIf('-');
                if (!minus) {
                    parseIf('+');
                }
                Number n = parseUnsignedNumericLiteralIf(minus ? Sign.MINUS : Sign.NONE);
                if (n != null) {
                    switch (characterUpper()) {
                        case 'D':
                            if (parseKeywordIf("D") || parseKeywordIf("DAY") || parseKeywordIf("DAYS")) {
                                if (day == null) {
                                    day = n;
                                } else {
                                    throw exception("Day part already defined");
                                }
                            }
                            break;
                        case 'H':
                            if (parseKeywordIf("H") || parseKeywordIf("HOUR") || parseKeywordIf("HOURS")) {
                                if (hour == null) {
                                    hour = n;
                                } else {
                                    throw exception("Hour part already defined");
                                }
                            }
                            break;
                        case 'M':
                            if (parseKeywordIf("M") || parseKeywordIf("MIN") || parseKeywordIf("MINS") || parseKeywordIf("MINUTE") || parseKeywordIf("MINUTES")) {
                                if (minute == null) {
                                    minute = n;
                                } else {
                                    throw exception("Minute part already defined");
                                }
                            } else if (parseKeywordIf("MON") || parseKeywordIf("MONS") || parseKeywordIf("MONTH") || parseKeywordIf("MONTHS")) {
                                if (month == null) {
                                    month = n;
                                } else {
                                    throw exception("Month part already defined");
                                }
                            }
                            break;
                        case 'S':
                            if (parseKeywordIf("S") || parseKeywordIf("SEC") || parseKeywordIf("SECS") || parseKeywordIf("SECOND") || parseKeywordIf("SECONDS")) {
                                if (second == null) {
                                    second = n;
                                } else {
                                    throw exception("Second part already defined");
                                }
                            }
                            break;
                        case Opcodes.DUP /* 89 */:
                            if (parseKeywordIf("Y") || parseKeywordIf("YEAR") || parseKeywordIf("YEARS")) {
                                if (year == null) {
                                    year = n;
                                } else {
                                    throw exception("Year part already defined");
                                }
                            }
                            break;
                    }
                }
            } while (!parseIf('\''));
            int months = (month == null ? 0 : month.intValue()) + (year == null ? 0 : Tools.asInt((long) (year.doubleValue() * 12.0d)));
            double seconds = (month == null ? 0.0d : (month.doubleValue() % 1.0d) * 30.0d * 86400.0d) + (day == null ? 0.0d : day.doubleValue() * 86400.0d) + (hour == null ? 0.0d : hour.doubleValue() * 3600.0d) + (minute == null ? 0.0d : minute.doubleValue() * 60.0d) + (second == null ? 0.0d : second.doubleValue());
            return new YearToSecond(new YearToMonth(0, months), new DayToSecond(0, 0, 0, Tools.asInt((long) seconds), Tools.asInt((long) ((seconds % 1.0d) * 1.0E9d))));
        }
        position(p);
        return null;
    }

    private final boolean parseIntervalPrecisionKeywordIf(String keyword) {
        if (parseKeywordIf(keyword)) {
            if (parseIf('(')) {
                parseUnsignedIntegerLiteral();
                parse(')');
                return true;
            }
            return true;
        }
        return false;
    }

    private final Interval parseIntervalLiteral() {
        Interval result = parsePostgresIntervalLiteralIf();
        if (result != null) {
            return result;
        }
        String string = parseStringLiteral();
        if (parseIntervalPrecisionKeywordIf("YEAR")) {
            if (parseKeywordIf("TO") && parseIntervalPrecisionKeywordIf("MONTH")) {
                return (Interval) requireNotNull(YearToMonth.yearToMonth(string), "Illegal interval literal");
            }
            return (Interval) requireNotNull(YearToMonth.year(string), "Illegal interval literal");
        }
        if (parseIntervalPrecisionKeywordIf("MONTH")) {
            return (Interval) requireNotNull(YearToMonth.month(string), "Illegal interval literal");
        }
        if (parseIntervalPrecisionKeywordIf("DAY")) {
            if (parseKeywordIf("TO")) {
                if (parseIntervalPrecisionKeywordIf("SECOND")) {
                    return (Interval) requireNotNull(DayToSecond.dayToSecond(string), "Illegal interval literal");
                }
                if (parseIntervalPrecisionKeywordIf("MINUTE")) {
                    return (Interval) requireNotNull(DayToSecond.dayToMinute(string), "Illegal interval literal");
                }
                if (parseIntervalPrecisionKeywordIf("HOUR")) {
                    return (Interval) requireNotNull(DayToSecond.dayToHour(string), "Illegal interval literal");
                }
                throw expected("HOUR", "MINUTE", "SECOND");
            }
            return (Interval) requireNotNull(DayToSecond.day(string), "Illegal interval literal");
        }
        if (parseIntervalPrecisionKeywordIf("HOUR")) {
            if (parseKeywordIf("TO")) {
                if (parseIntervalPrecisionKeywordIf("SECOND")) {
                    return (Interval) requireNotNull(DayToSecond.hourToSecond(string), "Illegal interval literal");
                }
                if (parseIntervalPrecisionKeywordIf("MINUTE")) {
                    return (Interval) requireNotNull(DayToSecond.hourToMinute(string), "Illegal interval literal");
                }
                throw expected("MINUTE", "SECOND");
            }
            return (Interval) requireNotNull(DayToSecond.hour(string), "Illegal interval literal");
        }
        if (parseIntervalPrecisionKeywordIf("MINUTE")) {
            if (parseKeywordIf("TO") && parseIntervalPrecisionKeywordIf("SECOND")) {
                return (Interval) requireNotNull(DayToSecond.minuteToSecond(string), "Illegal interval literal");
            }
            return (Interval) requireNotNull(DayToSecond.minute(string), "Illegal interval literal");
        }
        if (parseIntervalPrecisionKeywordIf("SECOND")) {
            return (Interval) requireNotNull(DayToSecond.second(string), "Illegal interval literal");
        }
        DayToSecond ds = DayToSecond.valueOf(string);
        if (ds != null) {
            return ds;
        }
        YearToMonth ym = YearToMonth.valueOf(string);
        if (ym != null) {
            return ym;
        }
        YearToSecond ys = YearToSecond.valueOf(string);
        if (ys != null) {
            return ys;
        }
        throw exception("Illegal interval literal");
    }

    private final <T> T requireNotNull(T value, String message) {
        if (value != null) {
            return value;
        }
        throw exception(message);
    }

    private final Field<?> parseFieldDateLiteralIf() {
        int p = position();
        if (parseKeywordIf("DATE")) {
            if (parseIf('(')) {
                Field<?> f = parseField();
                parse(')');
                return DSL.date((Field<? extends java.util.Date>) f);
            }
            if (peek('\'')) {
                return DSL.inline(parseDateLiteral());
            }
            position(p);
            return DSL.field(parseIdentifier());
        }
        return null;
    }

    /* JADX WARN: Code restructure failed: missing block: B:5:0x0028, code lost:            r0 = parseDatePart();        parse(',');        r0 = parseField();        parse(')');     */
    /* JADX WARN: Code restructure failed: missing block: B:6:0x0045, code lost:            return org.jooq.impl.DSL.trunc(r0, r0);     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private final org.jooq.Field<?> parseFieldDateTruncIf() {
        /*
            r4 = this;
            r0 = r4
            java.lang.String r1 = "DATE_TRUNC"
            java.lang.String r2 = "DATETIME_TRUNC"
            boolean r0 = r0.parseFunctionNameIf(r1, r2)
            if (r0 == 0) goto L46
            r0 = r4
            r1 = 40
            boolean r0 = r0.parse(r1)
            int[] r0 = org.jooq.impl.DefaultParseContext.AnonymousClass1.$SwitchMap$org$jooq$SQLDialect
            r1 = r4
            org.jooq.SQLDialect r1 = r1.parseFamily()
            int r1 = r1.ordinal()
            r0 = r0[r1]
            switch(r0) {
                default: goto L28;
            }
        L28:
            r0 = r4
            org.jooq.DatePart r0 = r0.parseDatePart()
            r6 = r0
            r0 = r4
            r1 = 44
            boolean r0 = r0.parse(r1)
            r0 = r4
            org.jooq.Field r0 = r0.parseField()
            r5 = r0
            r0 = r4
            r1 = 41
            boolean r0 = r0.parse(r1)
            r0 = r5
            r1 = r6
            org.jooq.Field r0 = org.jooq.impl.DSL.trunc(r0, r1)
            return r0
        L46:
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jooq.impl.DefaultParseContext.parseFieldDateTruncIf():org.jooq.Field");
    }

    private final Field<?> parseFieldDateAddIf() {
        boolean sub = false;
        if (parseFunctionNameIf("DATEADD")) {
            parse('(');
            DatePart part = parseDatePart();
            parse(',');
            Field<?> parseField = parseField();
            parse(',');
            Field<?> parseField2 = parseField();
            parse(')');
            return DSL.dateAdd((Field<Date>) parseField2, (Field<? extends Number>) parseField, part);
        }
        if (!parseFunctionNameIf("DATE_ADD")) {
            boolean parseFunctionNameIf = parseFunctionNameIf("DATE_SUB");
            sub = parseFunctionNameIf;
            if (!parseFunctionNameIf) {
                return null;
            }
        }
        boolean s = sub;
        parse('(');
        Field<?> d = parseField();
        Field<?> date = d.getDataType().isDateTime() ? d : d.coerce(SQLDataType.TIMESTAMP);
        parse(',');
        Field<?> interval = parseFieldIntervalLiteralIf(false);
        if (interval == null) {
            Field<?> interval2 = parseFieldMySQLIntervalLiteralIf((i, p) -> {
                return s ? DSL.dateSub((Field<Date>) date, (Field<? extends Number>) i, p) : DSL.dateAdd((Field<Date>) date, (Field<? extends Number>) i, p);
            });
            if (interval2 != null) {
                parse(')');
                return interval2;
            }
            return null;
        }
        parse(')');
        return s ? DSL.dateSub((Field<Date>) date, (Field<? extends Number>) interval) : DSL.dateAdd((Field<Date>) date, (Field<? extends Number>) interval);
    }

    private final Field<?> parseFieldDateDiffIf() {
        if (parseFunctionNameIf("DATEDIFF")) {
            parse('(');
            DatePart datePart = parseDatePartIf();
            if (datePart != null) {
                parse(',');
            }
            Field<?> parseField = parseField();
            if (parseIf(',')) {
                Field<?> parseField2 = parseField();
                parse(')');
                if (datePart != null) {
                    return DSL.dateDiff(datePart, (Field<Date>) parseField, (Field<Date>) parseField2);
                }
                return DSL.dateDiff((Field<Date>) parseField, (Field<Date>) parseField2);
            }
            parse(')');
            if (datePart != null) {
                return DSL.dateDiff((Field<Date>) DSL.field(datePart.toName()), (Field<Date>) parseField);
            }
            throw unsupportedClause();
        }
        return null;
    }

    private final Date parseDateLiteral() {
        try {
            return Date.valueOf(parseStringLiteral());
        } catch (IllegalArgumentException e) {
            throw exception("Illegal date literal");
        }
    }

    private final Field<?> parseFieldExtractIf() {
        if (parseFunctionNameIf("EXTRACT")) {
            parse('(');
            DatePart part = parseDatePart();
            parseKeyword("FROM");
            Field<?> field = parseField();
            parse(')');
            return DSL.extract(field, part);
        }
        return null;
    }

    private final Field<?> parseFieldDatePartIf() {
        if (parseFunctionNameIf("DATEPART", "DATE_PART")) {
            parse('(');
            DatePart part = parseDatePart();
            parse(',');
            Field<?> field = parseField();
            parse(')');
            return DSL.extract(field, part);
        }
        return null;
    }

    private final DatePart parseDatePart() {
        DatePart result = parseDatePartIf();
        if (result == null) {
            throw expected("DatePart");
        }
        return result;
    }

    private final DatePart parseDatePartIf() {
        int p = position();
        boolean string = parseIf('\'');
        DatePart result = parseDatePartIf0();
        if (result == null) {
            position(p);
        } else if (string) {
            parse('\'');
        }
        if (parseIf("::")) {
            parseDataType();
        }
        return result;
    }

    private final DatePart parseDatePartIf0() {
        char character = characterUpper();
        switch (character) {
            case 'C':
                if (parseKeywordIf("CENTURY") || parseKeywordIf("CENTURIES")) {
                    return DatePart.CENTURY;
                }
                return null;
            case 'D':
                if (parseKeywordIf("DAYOFYEAR") || parseKeywordIf("DAY_OF_YEAR") || parseKeywordIf("DOY") || parseKeywordIf("DY")) {
                    return DatePart.DAY_OF_YEAR;
                }
                if (parseKeywordIf("DAY_OF_WEEK") || parseKeywordIf("DAYOFWEEK") || parseKeywordIf("DW")) {
                    return DatePart.DAY_OF_WEEK;
                }
                if (parseKeywordIf("DAY") || parseKeywordIf("DAYS") || parseKeywordIf("DD") || parseKeywordIf("D")) {
                    return DatePart.DAY;
                }
                if (parseKeywordIf("DECADE") || parseKeywordIf("DECADES")) {
                    return DatePart.DECADE;
                }
                return null;
            case TypeReference.CONSTRUCTOR_REFERENCE /* 69 */:
                if (parseKeywordIf("EPOCH")) {
                    return DatePart.EPOCH;
                }
                return null;
            case 'F':
            case TypeReference.CAST /* 71 */:
            case 'J':
            case TypeReference.METHOD_REFERENCE_TYPE_ARGUMENT /* 75 */:
            case BaseNCodec.MIME_CHUNK_SIZE /* 76 */:
            case Opcodes.IASTORE /* 79 */:
            case 'P':
            case Opcodes.DASTORE /* 82 */:
            case Opcodes.CASTORE /* 85 */:
            case Opcodes.SASTORE /* 86 */:
            case 'X':
            default:
                return null;
            case 'H':
                if (parseKeywordIf("HOUR") || parseKeywordIf("HOURS") || parseKeywordIf("HH")) {
                    return DatePart.HOUR;
                }
                return null;
            case 'I':
                if (parseKeywordIf("ISODOW") || parseKeywordIf("ISO_DAY_OF_WEEK")) {
                    return DatePart.ISO_DAY_OF_WEEK;
                }
                break;
            case 'M':
                break;
            case 'N':
                if (parseKeywordIf("N")) {
                    return DatePart.MINUTE;
                }
                if (parseKeywordIf("NANOSECOND") || parseKeywordIf("NANOSECONDS") || parseKeywordIf("NS")) {
                    return DatePart.NANOSECOND;
                }
                return null;
            case Opcodes.FASTORE /* 81 */:
                if (parseKeywordIf("QUARTER") || parseKeywordIf("QUARTERS") || parseKeywordIf("QQ") || parseKeywordIf("Q")) {
                    return DatePart.QUARTER;
                }
                return null;
            case 'S':
                if (parseKeywordIf("SECOND") || parseKeywordIf("SECONDS") || parseKeywordIf("SS") || parseKeywordIf("S")) {
                    return DatePart.SECOND;
                }
                return null;
            case Opcodes.BASTORE /* 84 */:
                if (parseKeywordIf("TIMEZONE")) {
                    return DatePart.TIMEZONE;
                }
                if (parseKeywordIf("TIMEZONE_HOUR")) {
                    return DatePart.TIMEZONE_HOUR;
                }
                if (parseKeywordIf("TIMEZONE_MINUTE")) {
                    return DatePart.TIMEZONE_MINUTE;
                }
                return null;
            case Opcodes.POP /* 87 */:
                if (parseKeywordIf("WEEK") || parseKeywordIf("WEEKS") || parseKeywordIf("WEEK_OF_YEAR") || parseKeywordIf("WK") || parseKeywordIf("WW")) {
                    return DatePart.WEEK;
                }
                if (parseKeywordIf("WEEKDAY") || parseKeywordIf("W")) {
                    return DatePart.DAY_OF_WEEK;
                }
                return null;
            case Opcodes.DUP /* 89 */:
                if (parseKeywordIf("YEAR") || parseKeywordIf("YEARS") || parseKeywordIf("YYYY") || parseKeywordIf("YY")) {
                    return DatePart.YEAR;
                }
                if (parseKeywordIf("Y")) {
                    return DatePart.DAY_OF_YEAR;
                }
                return null;
        }
        if (parseKeywordIf("MINUTE") || parseKeywordIf("MINUTES") || parseKeywordIf("MI")) {
            return DatePart.MINUTE;
        }
        if (parseKeywordIf("MILLENNIUM") || parseKeywordIf("MILLENNIUMS") || parseKeywordIf("MILLENNIA")) {
            return DatePart.MILLENNIUM;
        }
        if (parseKeywordIf("MICROSECOND") || parseKeywordIf("MICROSECONDS") || parseKeywordIf("MCS")) {
            return DatePart.MICROSECOND;
        }
        if (parseKeywordIf("MILLISECOND") || parseKeywordIf("MILLISECONDS") || parseKeywordIf("MS")) {
            return DatePart.MILLISECOND;
        }
        if (parseKeywordIf("MONTH") || parseKeywordIf("MONTHS") || parseKeywordIf("MM") || parseKeywordIf("M")) {
            return DatePart.MONTH;
        }
        return null;
    }

    private final DatePart parseIntervalDatePart() {
        char character = characterUpper();
        switch (character) {
            case 'D':
                if (parseKeywordIf("DAY") || parseKeywordIf("DAYS")) {
                    return DatePart.DAY;
                }
                break;
            case 'H':
                if (parseKeywordIf("HOUR") || parseKeywordIf("HOURS")) {
                    return DatePart.HOUR;
                }
                break;
            case 'M':
                if (parseKeywordIf("MINUTE") || parseKeywordIf("MINUTES")) {
                    return DatePart.MINUTE;
                }
                if (parseKeywordIf("MICROSECOND") || parseKeywordIf("MICROSECONDS")) {
                    return DatePart.MICROSECOND;
                }
                if (parseKeywordIf("MILLISECOND") || parseKeywordIf("MILLISECONDS")) {
                    return DatePart.MILLISECOND;
                }
                if (parseKeywordIf("MONTH") || parseKeywordIf("MONTHS")) {
                    return DatePart.MONTH;
                }
                break;
            case 'N':
                if (parseKeywordIf("NANOSECOND") || parseKeywordIf("NANOSECONDS")) {
                    return DatePart.NANOSECOND;
                }
                break;
            case Opcodes.FASTORE /* 81 */:
                if (parseKeywordIf("QUARTER") || parseKeywordIf("QUARTERS")) {
                    return DatePart.QUARTER;
                }
                break;
            case 'S':
                if (parseKeywordIf("SECOND") || parseKeywordIf("SECONDS")) {
                    return DatePart.SECOND;
                }
                break;
            case Opcodes.POP /* 87 */:
                if (parseKeywordIf("WEEK") || parseKeywordIf("WEEKS")) {
                    return DatePart.WEEK;
                }
                break;
            case Opcodes.DUP /* 89 */:
                if (parseKeywordIf("YEAR") || parseKeywordIf("YEARS")) {
                    return DatePart.YEAR;
                }
                break;
        }
        throw expected("Interval DatePart");
    }

    private final Field<?> parseFieldConcatIf() {
        if (parseFunctionNameIf("CONCAT")) {
            parse('(');
            Field<String> result = DSL.concat((Field<?>[]) parseList(',', c -> {
                return c.parseField();
            }).toArray(Tools.EMPTY_FIELD));
            parse(')');
            return result;
        }
        return null;
    }

    private final Field<?> parseFieldOverlayIf() {
        Field<?> field;
        if (parseFunctionNameIf("OVERLAY")) {
            parse('(');
            Field<?> parseField = parseField();
            parseKeyword("PLACING");
            Field<?> parseField2 = parseField();
            parseKeyword("FROM");
            Field<?> parseField3 = parseField();
            if (parseKeywordIf("FOR")) {
                field = parseField();
            } else {
                field = null;
            }
            Field<?> field2 = field;
            parse(')');
            return field2 == null ? DSL.overlay((Field<String>) parseField, (Field<String>) parseField2, (Field<? extends Number>) parseField3) : DSL.overlay((Field<String>) parseField, (Field<String>) parseField2, (Field<? extends Number>) parseField3, (Field<? extends Number>) field2);
        }
        return null;
    }

    private final Field<?> parseFieldPositionIf() {
        if (parseFunctionNameIf("POSITION")) {
            parse('(');
            this.forbidden.add(FunctionKeyword.FK_IN);
            Field<?> parseField = parseField();
            parseKeyword("IN");
            this.forbidden.remove(FunctionKeyword.FK_IN);
            Field<?> parseField2 = parseField();
            parse(')');
            return DSL.position((Field<String>) parseField2, (Field<String>) parseField);
        }
        return null;
    }

    private final Field<?> parseFieldLocateIf() {
        boolean locate = parseFunctionNameIf("LOCATE");
        if (locate || parseFunctionNameIf("LOCATE_IN_STRING")) {
            parse('(');
            Field<?> parseField = parseField();
            parse(',');
            Field<?> parseField2 = parseField();
            Field<?> parseField3 = parseIf(',') ? parseField() : null;
            parse(')');
            return locate ? parseField3 == null ? DSL.position((Field<String>) parseField2, (Field<String>) parseField) : DSL.position((Field<String>) parseField2, (Field<String>) parseField, (Field<? extends Number>) parseField3) : parseField3 == null ? DSL.position((Field<String>) parseField, (Field<String>) parseField2) : DSL.position((Field<String>) parseField, (Field<String>) parseField2, (Field<? extends Number>) parseField3);
        }
        return null;
    }

    private final Field<?> parseFieldRegexpReplaceIf() {
        boolean all = parseFunctionNameIf("REGEXP_REPLACE_ALL");
        boolean first = !all && parseFunctionNameIf("REGEXP_REPLACE_FIRST");
        boolean ifx = (all || first || !parseFunctionNameIf("REGEX_REPLACE")) ? false : true;
        if (all || first || ifx || parseFunctionNameIf("REGEXP_REPLACE")) {
            parse('(');
            Field field = parseField();
            parse(',');
            Field pattern = parseField();
            Field replacement = parseIf(',') ? parseField() : null;
            if (replacement == null) {
                replacement = DSL.inline("");
            } else if (ifx) {
                if (parseIf(',')) {
                    if (1 == parseUnsignedIntegerLiteral().longValue()) {
                        first = true;
                    } else {
                        throw expected("Only a limit of 1 is currently supported");
                    }
                }
            } else if (!all && !first) {
                if (parseIf(',')) {
                    String s = parseStringLiteralIf();
                    if (s != null) {
                        if (s.contains("g")) {
                            all = true;
                        }
                    } else {
                        Long i1 = parseUnsignedIntegerLiteral();
                        parse(',');
                        Long i2 = parseUnsignedIntegerLiteral();
                        Long l = 1L;
                        if (l.equals(i1)) {
                            Long l2 = 1L;
                            if (l2.equals(i2)) {
                                all = true;
                            }
                        }
                        throw expected("Only start and occurence values of 1 are currently supported");
                    }
                }
                if (!all) {
                    switch (parseFamily()) {
                        case POSTGRES:
                        case YUGABYTEDB:
                            first = true;
                            break;
                    }
                }
            }
            parse(')');
            if (first) {
                return DSL.regexpReplaceFirst((Field<String>) field, (Field<String>) pattern, (Field<String>) replacement);
            }
            return DSL.regexpReplaceAll((Field<String>) field, (Field<String>) pattern, (Field<String>) replacement);
        }
        if (parseFunctionNameIf("REPLACE_REGEXPR")) {
            parse('(');
            this.forbidden.add(FunctionKeyword.FK_IN);
            Field pattern2 = parseField();
            parseKeyword("IN");
            this.forbidden.remove(FunctionKeyword.FK_IN);
            Field field2 = parseField();
            Field replacement2 = parseKeywordIf("WITH") ? parseField() : DSL.inline("");
            boolean first2 = parseKeywordIf("OCCURRENCE") && !parseKeywordIf("ALL") && parse(CustomBooleanEditor.VALUE_1);
            parse(')');
            if (first2) {
                return DSL.regexpReplaceFirst((Field<String>) field2, (Field<String>) pattern2, (Field<String>) replacement2);
            }
            return DSL.regexpReplaceAll((Field<String>) field2, (Field<String>) pattern2, (Field<String>) replacement2);
        }
        return null;
    }

    /* JADX WARN: Code restructure failed: missing block: B:27:0x006c, code lost:            if (r0 == false) goto L30;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private final org.jooq.Field<?> parseFieldSubstringIf() {
        /*
            r4 = this;
            r0 = r4
            java.lang.String r1 = "SUBSTRING"
            boolean r0 = r0.parseFunctionNameIf(r1)
            r5 = r0
            r0 = r5
            if (r0 != 0) goto L1a
            r0 = r4
            java.lang.String r1 = "SUBSTR"
            boolean r0 = r0.parseFunctionNameIf(r1)
            if (r0 == 0) goto L1a
            r0 = 1
            goto L1b
        L1a:
            r0 = 0
        L1b:
            r6 = r0
            r0 = r6
            if (r0 != 0) goto L3c
            r0 = r4
            boolean r0 = r0.ignoreProEdition()
            if (r0 != 0) goto L3c
            r0 = r4
            java.lang.String r1 = "DBMS_LOB.SUBSTR"
            boolean r0 = r0.parseFunctionNameIf(r1)
            if (r0 == 0) goto L3c
            r0 = r4
            boolean r0 = r0.requireProEdition()
            if (r0 == 0) goto L3c
            r0 = 1
            goto L3d
        L3c:
            r0 = 0
        L3d:
            r7 = r0
            r0 = r5
            if (r0 != 0) goto L46
            r0 = r6
            if (r0 == 0) goto Lcb
        L46:
            r0 = r6
            if (r0 != 0) goto L4e
            r0 = 1
            goto L4f
        L4e:
            r0 = 0
        L4f:
            r8 = r0
            r0 = r4
            r1 = 40
            boolean r0 = r0.parse(r1)
            r0 = r4
            org.jooq.Field r0 = r0.parseField()
            r9 = r0
            r0 = r6
            if (r0 != 0) goto L6f
            r0 = r4
            java.lang.String r1 = "FROM"
            boolean r0 = r0.parseKeywordIf(r1)
            r1 = r0
            r8 = r1
            if (r0 != 0) goto L76
        L6f:
            r0 = r4
            r1 = 44
            boolean r0 = r0.parse(r1)
        L76:
            r0 = r4
            r1 = r4
            org.jooq.FieldOrRow r1 = r1.parseNumericOp()
            org.jooq.Field r0 = r0.toField(r1)
            r10 = r0
            r0 = r8
            if (r0 == 0) goto L8f
            r0 = r4
            java.lang.String r1 = "FOR"
            boolean r0 = r0.parseKeywordIf(r1)
            if (r0 != 0) goto L9d
        L8f:
            r0 = r8
            if (r0 != 0) goto La8
            r0 = r4
            r1 = 44
            boolean r0 = r0.parseIf(r1)
            if (r0 == 0) goto La8
        L9d:
            r0 = r4
            r1 = r4
            org.jooq.FieldOrRow r1 = r1.parseNumericOp()
            org.jooq.Field r0 = r0.toField(r1)
            goto La9
        La8:
            r0 = 0
        La9:
            r11 = r0
            r0 = r4
            r1 = 41
            boolean r0 = r0.parse(r1)
            r0 = r11
            if (r0 != 0) goto Lc1
            r0 = r9
            r1 = r10
            org.jooq.Field r0 = org.jooq.impl.DSL.substring(r0, r1)
            goto Lca
        Lc1:
            r0 = r9
            r1 = r10
            r2 = r11
            org.jooq.Field r0 = org.jooq.impl.DSL.substring(r0, r1, r2)
        Lca:
            return r0
        Lcb:
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jooq.impl.DefaultParseContext.parseFieldSubstringIf():org.jooq.Field");
    }

    private final Field<?> parseFieldTrimIf() {
        if (parseFunctionNameIf("TRIM")) {
            parse('(');
            int p = position();
            boolean leading = parseKeywordIf("LEADING", "L");
            boolean trailing = !leading && parseKeywordIf("TRAILING", "T");
            boolean both = (leading || trailing || !parseKeywordIf("BOTH", "B")) ? false : true;
            if (leading || trailing || both) {
                if (parseIf(',') || parseIf(')')) {
                    position(p);
                } else if (parseKeywordIf("FROM")) {
                    Field<?> parseField = parseField();
                    parse(')');
                    return leading ? DSL.ltrim((Field<String>) parseField) : trailing ? DSL.rtrim((Field<String>) parseField) : DSL.trim((Field<String>) parseField);
                }
            }
            if (parseKeywordIf("FROM")) {
                if (parseIf(',') || parseIf(')')) {
                    position(p);
                } else {
                    Field<?> parseField2 = parseField();
                    parse(')');
                    return DSL.trim((Field<String>) parseField2);
                }
            }
            Field<?> parseField3 = parseField();
            if (parseKeywordIf("FROM")) {
                Field<?> parseField4 = parseField();
                parse(')');
                return leading ? DSL.ltrim((Field<String>) parseField4, (Field<String>) parseField3) : trailing ? DSL.rtrim((Field<String>) parseField4, (Field<String>) parseField3) : DSL.trim((Field<String>) parseField4, (Field<String>) parseField3);
            }
            Field<?> parseField5 = parseIf(',') ? parseField() : null;
            parse(')');
            return parseField5 == null ? DSL.trim((Field<String>) parseField3) : DSL.trim((Field<String>) parseField3, (Field<String>) parseField5);
        }
        return null;
    }

    private final Field<?> parseFieldTranslateIf() {
        if (parseFunctionNameIf("TRANSLATE", "OTRANSLATE")) {
            return (Field) parseFunctionArgs3(DSL::translate);
        }
        return null;
    }

    private final Field<?> parseFieldDecodeIf() {
        if (parseFunctionNameIf("DECODE", "DECODE_ORACLE", "MAP")) {
            parse('(');
            List<Field<?>> fields = parseList(',', c -> {
                return c.parseField();
            });
            int size = fields.size();
            if (size < 3) {
                throw expected("At least three arguments to DECODE()");
            }
            parse(')');
            return DSL.decode((Field) fields.get(0), (Field) fields.get(1), (Field) fields.get(2), size == 3 ? Tools.EMPTY_FIELD : (Field[]) fields.subList(3, size).toArray(Tools.EMPTY_FIELD));
        }
        return null;
    }

    private final Field<?> parseFieldChooseIf() {
        if (parseFunctionNameIf("CHOOSE", "ELT")) {
            parse('(');
            Field<?> parseField = parseField();
            parse(',');
            List<Field<?>> fields = parseList(',', c -> {
                return c.parseField();
            });
            parse(')');
            return DSL.choose((Field<Integer>) parseField, fields.toArray(Tools.EMPTY_FIELD));
        }
        return null;
    }

    private final Field<?> parseFieldIfIf() {
        if (parseFunctionNameIf("IF", "IIF")) {
            parse('(');
            Condition c = parseCondition();
            parse(',');
            Field<?> f1 = parseField();
            parse(',');
            Field<?> f2 = parseField();
            parse(')');
            return DSL.iif(c, f1, f2);
        }
        return null;
    }

    private final Field<?> parseFieldCoalesceIf() {
        if (parseFunctionNameIf("COALESCE")) {
            parse('(');
            List<Field<?>> fields = parseList(',', c -> {
                return c.parseField();
            });
            parse(')');
            Field[] a = Tools.EMPTY_FIELD;
            return DSL.coalesce((Field) fields.get(0), (Field<?>[]) (fields.size() == 1 ? a : (Field[]) fields.subList(1, fields.size()).toArray(a)));
        }
        return null;
    }

    private final <T> Field<?> parseFieldFieldIf() {
        if (parseFunctionNameIf("FIELD")) {
            parse('(');
            Field<?> f1 = parseField();
            parse(',');
            List<T> parseList = parseList(',', (java.util.function.Function) c -> {
                return c.parseField();
            });
            parse(')');
            return DSL.field((Field) f1, (Field[]) parseList.toArray(Tools.EMPTY_FIELD));
        }
        return null;
    }

    private final Field<?> parseFieldCaseIf() {
        Field result;
        Field result2;
        if (parseKeywordIf("CASE")) {
            if (parseKeywordIf("WHEN")) {
                CaseConditionStep step = null;
                do {
                    Condition condition = parseCondition();
                    parseKeyword("THEN");
                    Field value = parseField();
                    step = step == null ? DSL.when(condition, value) : step.when(condition, value);
                } while (parseKeywordIf("WHEN"));
                if (parseKeywordIf("ELSE")) {
                    result2 = step.otherwise((Field) parseField());
                } else {
                    result2 = step;
                }
                parseKeyword("END");
                return result2;
            }
            CaseValueStep init = DSL.choose((Field) parseField());
            CaseWhenStep step2 = null;
            parseKeyword("WHEN");
            do {
                Field when = parseField();
                parseKeyword("THEN");
                Field then = parseField();
                step2 = step2 == null ? init.when(when, then) : step2.when(when, then);
            } while (parseKeywordIf("WHEN"));
            if (parseKeywordIf("ELSE")) {
                result = step2.otherwise((Field) parseField());
            } else {
                result = step2;
            }
            parseKeyword("END");
            return result;
        }
        return null;
    }

    private final Field<?> parseFieldCastIf() {
        boolean cast = parseFunctionNameIf("CAST");
        boolean coerce = !cast && parseFunctionNameIf("COERCE");
        boolean tryCast = (cast || coerce || !parseFunctionNameIf("TRY_CAST", "SAFE_CAST")) ? false : true;
        if (cast || coerce || tryCast) {
            parse('(');
            Field<?> field = parseField();
            parseKeyword("AS");
            DataType<?> type = parseCastDataType();
            if (!tryCast) {
                tryCast = parseKeywordIf("DEFAULT NULL ON CONVERSION ERROR");
            }
            parse(')');
            if (tryCast) {
                return DSL.tryCast(field, (DataType) type);
            }
            if (cast) {
                return DSL.cast(field, (DataType) type);
            }
            return DSL.coerce(field, (DataType) type);
        }
        return null;
    }

    private final Field<?> parseFieldConvertIf() {
        if (parseFunctionNameIf("CONVERT")) {
            parse('(');
            DataType<?> type = parseDataType();
            parse(',');
            Field<?> field = parseField();
            Long style = null;
            if (!ignoreProEdition() && parseIf(',') && requireProEdition()) {
                style = parseUnsignedIntegerLiteral();
            }
            parse(')');
            if (style == null) {
                return DSL.cast(field, (DataType) type);
            }
            return null;
        }
        return null;
    }

    private final Field<Boolean> parseBooleanValueExpressionIf() {
        TruthValue truth = parseTruthValueIf();
        if (truth != null) {
            switch (truth) {
                case T_TRUE:
                    return DSL.inline(true);
                case T_FALSE:
                    return DSL.inline(false);
                case T_NULL:
                    return DSL.inline((Boolean) null);
                default:
                    throw exception("Truth value not supported: " + String.valueOf(truth));
            }
        }
        return null;
    }

    private final Field<?> parseAggregateFunctionIf() {
        return parseAggregateFunctionIf(false);
    }

    private final Field<?> parseAggregateFunctionIf(boolean basic) {
        return parseAggregateFunctionIf(basic, null);
    }

    /* JADX WARN: Failed to apply debug info
    jadx.core.utils.exceptions.JadxOverflowException: Type inference error: updates count limit reached
    	at jadx.core.dex.visitors.typeinference.TypeUpdateInfo.requestUpdate(TypeUpdateInfo.java:35)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:210)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:114)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.ifListener(TypeUpdate.java:633)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:188)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.moveListener(TypeUpdate.java:447)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:188)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.allSameListener(TypeUpdate.java:473)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:183)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.allSameListener(TypeUpdate.java:473)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:183)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.moveListener(TypeUpdate.java:447)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:183)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.moveListener(TypeUpdate.java:447)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:183)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.allSameListener(TypeUpdate.java:473)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:183)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.allSameListener(TypeUpdate.java:466)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:188)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.moveListener(TypeUpdate.java:447)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:188)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.moveListener(TypeUpdate.java:447)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:188)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.allSameListener(TypeUpdate.java:466)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:188)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.allSameListener(TypeUpdate.java:466)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:188)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.allSameListener(TypeUpdate.java:466)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:188)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.allSameListener(TypeUpdate.java:466)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:188)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.allSameListener(TypeUpdate.java:466)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:188)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.moveListener(TypeUpdate.java:447)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:188)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.moveListener(TypeUpdate.java:447)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:183)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.allSameListener(TypeUpdate.java:473)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:183)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.allSameListener(TypeUpdate.java:466)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:188)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.allSameListener(TypeUpdate.java:466)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:188)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.moveListener(TypeUpdate.java:447)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:188)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.moveListener(TypeUpdate.java:447)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:183)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.allSameListener(TypeUpdate.java:473)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:183)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.allSameListener(TypeUpdate.java:466)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:188)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.allSameListener(TypeUpdate.java:466)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:188)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.allSameListener(TypeUpdate.java:466)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:188)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.allSameListener(TypeUpdate.java:466)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:188)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.moveListener(TypeUpdate.java:447)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:188)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.moveListener(TypeUpdate.java:447)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:183)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.allSameListener(TypeUpdate.java:473)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:183)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.allSameListener(TypeUpdate.java:466)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:188)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.moveListener(TypeUpdate.java:447)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:188)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.moveListener(TypeUpdate.java:447)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:188)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.moveListener(TypeUpdate.java:447)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:183)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.moveListener(TypeUpdate.java:447)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:183)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:83)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.applyWithWiderIgnoreUnknown(TypeUpdate.java:74)
    	at jadx.core.dex.visitors.debuginfo.DebugInfoApplyVisitor.applyDebugInfo(DebugInfoApplyVisitor.java:137)
    	at jadx.core.dex.visitors.debuginfo.DebugInfoApplyVisitor.applyDebugInfo(DebugInfoApplyVisitor.java:133)
    	at jadx.core.dex.visitors.debuginfo.DebugInfoApplyVisitor.searchAndApplyVarDebugInfo(DebugInfoApplyVisitor.java:75)
    	at jadx.core.dex.visitors.debuginfo.DebugInfoApplyVisitor.lambda$applyDebugInfo$0(DebugInfoApplyVisitor.java:68)
    	at java.base/java.util.ArrayList.forEach(Unknown Source)
    	at jadx.core.dex.visitors.debuginfo.DebugInfoApplyVisitor.applyDebugInfo(DebugInfoApplyVisitor.java:68)
    	at jadx.core.dex.visitors.debuginfo.DebugInfoApplyVisitor.visit(DebugInfoApplyVisitor.java:55)
     */
    private final Field<?> parseAggregateFunctionIf(boolean basic, AggregateFunction<?> f) {
        Field<?> result = null;
        AggregateFunction<?> parseCountIf = f != null ? f : parseCountIf();
        AggregateFilterStep<?> filter = parseCountIf;
        AggregateFunction<?> aggregateFunction = parseCountIf;
        AggregateFunction<?> aggregateFunction2 = parseCountIf;
        if (filter == null) {
            Field<?> parseGeneralSetFunctionIf = parseGeneralSetFunctionIf();
            if (parseGeneralSetFunctionIf != null && !(parseGeneralSetFunctionIf instanceof AggregateFunction)) {
                return parseGeneralSetFunctionIf;
            }
            AggregateFunction<?> aggregateFunction3 = (AggregateFunction) parseGeneralSetFunctionIf;
            filter = aggregateFunction3;
            aggregateFunction = aggregateFunction3;
            aggregateFunction2 = aggregateFunction3;
        }
        if (filter == null && !basic) {
            AggregateFunction<?> parseBinarySetFunctionIf = parseBinarySetFunctionIf();
            filter = parseBinarySetFunctionIf;
            aggregateFunction = parseBinarySetFunctionIf;
        }
        if (filter == null && !basic) {
            AggregateFilterStep<?> parseOrderedSetFunctionIf = parseOrderedSetFunctionIf();
            filter = parseOrderedSetFunctionIf;
            aggregateFunction = parseOrderedSetFunctionIf;
        }
        if (filter == null && !basic) {
            AggregateFilterStep<?> parseArrayAggFunctionIf = parseArrayAggFunctionIf();
            filter = parseArrayAggFunctionIf;
            aggregateFunction = parseArrayAggFunctionIf;
        }
        if (filter == null && !basic) {
            AggregateFilterStep<?> parseMultisetAggFunctionIf = parseMultisetAggFunctionIf();
            filter = parseMultisetAggFunctionIf;
            aggregateFunction = parseMultisetAggFunctionIf;
        }
        if (filter == null && !basic) {
            AggregateFilterStep<?> parseXMLAggFunctionIf = parseXMLAggFunctionIf();
            filter = parseXMLAggFunctionIf;
            aggregateFunction = parseXMLAggFunctionIf;
        }
        if (filter == null && !basic) {
            AggregateFilterStep<?> parseJSONArrayAggFunctionIf = parseJSONArrayAggFunctionIf();
            filter = parseJSONArrayAggFunctionIf;
            aggregateFunction = parseJSONArrayAggFunctionIf;
        }
        if (filter == null && !basic) {
            AggregateFilterStep<?> parseJSONObjectAggFunctionIf = parseJSONObjectAggFunctionIf();
            filter = parseJSONObjectAggFunctionIf;
            aggregateFunction = parseJSONObjectAggFunctionIf;
        }
        if (filter == null) {
            aggregateFunction = parseCountIfIf();
        }
        if (filter == null && aggregateFunction == null) {
            if (!basic) {
                return parseSpecialAggregateFunctionIf();
            }
            return null;
        }
        if (aggregateFunction2 != null && filter != null && !basic && !ignoreProEdition() && parseKeywordIf("KEEP")) {
            requireProEdition();
        } else if (filter != null && !basic && parseKeywordIf("FILTER")) {
            parse('(');
            parseKeyword("WHERE");
            Condition parseCondition = parseCondition();
            parse(')');
            WindowBeforeOverStep<?> filterWhere = filter.filterWhere(parseCondition);
            aggregateFunction = filterWhere;
            result = filterWhere;
        } else if (filter != null) {
            result = filter;
        } else {
            result = aggregateFunction;
        }
        if (!basic && parseKeywordIf("OVER")) {
            Object parseWindowNameOrSpecification = parseWindowNameOrSpecification(filter != null);
            if (parseWindowNameOrSpecification instanceof Name) {
                result = aggregateFunction.over((Name) parseWindowNameOrSpecification);
            } else if (parseWindowNameOrSpecification instanceof WindowSpecification) {
                result = aggregateFunction.over((WindowSpecification) parseWindowNameOrSpecification);
            } else {
                result = aggregateFunction.over();
            }
        }
        return result;
    }

    private final Field<?> parseSpecialAggregateFunctionIf() {
        GroupConcatOrderByStep s1;
        GroupConcatSeparatorStep s2;
        AggregateFunction<String> s3;
        if (parseFunctionNameIf("GROUP_CONCAT")) {
            parse('(');
            if (parseKeywordIf("DISTINCT")) {
                s1 = DSL.groupConcatDistinct(parseField());
            } else {
                if (!parseKeywordIf("ALL")) {
                }
                s1 = DSL.groupConcat(parseField());
            }
            if (parseKeywordIf("ORDER BY")) {
                s2 = s1.orderBy(parseList(',', c -> {
                    return c.parseSortField();
                }));
            } else {
                s2 = s1;
            }
            if (parseKeywordIf("SEPARATOR")) {
                s3 = s2.separator(parseStringLiteral());
            } else {
                s3 = s2;
            }
            parse(')');
            return s3;
        }
        return null;
    }

    private final Object parseWindowNameOrSpecification(boolean orderByAllowed) {
        Object result;
        if (parseIf('(')) {
            result = parseWindowSpecificationIf(null, orderByAllowed);
            parse(')');
        } else {
            result = parseIdentifier();
        }
        return result;
    }

    private final Field<?> parseFieldRankIf() {
        if (parseFunctionNameIf("RANK")) {
            parse('(');
            if (parseIf(')')) {
                return parseWindowFunction(null, null, DSL.rank());
            }
            parseKeywordIf("ALL");
            List<Field<?>> args = parseList(',', c -> {
                return c.parseField();
            });
            parse(')');
            return DSL.rank(args).withinGroupOrderBy(parseWithinGroupN());
        }
        return null;
    }

    private final Field<?> parseFieldDenseRankIf() {
        if (parseFunctionNameIf("DENSE_RANK", "DENSERANK")) {
            parse('(');
            if (parseIf(')')) {
                return parseWindowFunction(null, null, DSL.denseRank());
            }
            parseKeywordIf("ALL");
            List<Field<?>> args = parseList(',', c -> {
                return c.parseField();
            });
            parse(')');
            return DSL.denseRank(args).withinGroupOrderBy(parseWithinGroupN());
        }
        return null;
    }

    private final Field<?> parseFieldPercentRankIf() {
        if (parseFunctionNameIf("PERCENT_RANK")) {
            parse('(');
            if (parseIf(')')) {
                return parseWindowFunction(null, null, DSL.percentRank());
            }
            parseKeywordIf("ALL");
            List<Field<?>> args = parseList(',', c -> {
                return c.parseField();
            });
            parse(')');
            return DSL.percentRank(args).withinGroupOrderBy(parseWithinGroupN());
        }
        return null;
    }

    private final Field<?> parseFieldCumeDistIf() {
        if (parseFunctionNameIf("CUME_DIST")) {
            parse('(');
            if (parseIf(')')) {
                return parseWindowFunction(null, null, DSL.cumeDist());
            }
            parseKeywordIf("ALL");
            List<Field<?>> args = parseList(',', c -> {
                return c.parseField();
            });
            parse(')');
            return DSL.cumeDist(args).withinGroupOrderBy(parseWithinGroupN());
        }
        return null;
    }

    private final Field<?> parseFieldNtileIf() {
        if (parseFunctionNameIf("NTILE")) {
            parse('(');
            int number = Tools.asInt(parseUnsignedIntegerLiteral().longValue());
            parse(')');
            return parseWindowFunction(null, null, DSL.ntile(number));
        }
        return null;
    }

    private final Field<?> parseFieldLeadLagIf() {
        WindowIgnoreNullsStep lag;
        boolean lead = parseFunctionNameIf("LEAD");
        boolean lag2 = !lead && parseFunctionNameIf("LAG");
        if (lead || lag2) {
            parse('(');
            Field<?> parseField = parseField();
            Integer f2 = null;
            Field<?> field = null;
            if (parseIf(',')) {
                f2 = Integer.valueOf(Tools.asInt(parseUnsignedIntegerLiteral().longValue()));
                if (parseIf(',')) {
                    field = parseField();
                }
            }
            if (lead) {
                if (f2 == null) {
                    lag = DSL.lead(parseField);
                } else if (field == null) {
                    lag = DSL.lead(parseField, f2.intValue());
                } else {
                    lag = DSL.lead((Field) parseField, f2.intValue(), (Field) field);
                }
            } else if (f2 == null) {
                lag = DSL.lag(parseField);
            } else if (field == null) {
                lag = DSL.lag(parseField, f2.intValue());
            } else {
                lag = DSL.lag((Field) parseField, f2.intValue(), (Field) field);
            }
            WindowIgnoreNullsStep s1 = lag;
            WindowOverStep<?> s2 = parseWindowRespectIgnoreNulls(s1, s1);
            parse(')');
            return parseWindowFunction(null, s1, s2);
        }
        return null;
    }

    private final Field<?> parseFieldFirstValueIf() {
        if (parseFunctionNameIf("FIRST_VALUE")) {
            parse('(');
            WindowIgnoreNullsStep<Void> s1 = DSL.firstValue(parseField());
            WindowOverStep<?> s2 = parseWindowRespectIgnoreNulls(s1, s1);
            parse(')');
            return parseWindowFunction(null, s1, s2);
        }
        return null;
    }

    private final Field<?> parseFieldLastValueIf() {
        if (parseFunctionNameIf("LAST_VALUE")) {
            parse('(');
            WindowIgnoreNullsStep<Void> s1 = DSL.lastValue(parseField());
            WindowOverStep<?> s2 = parseWindowRespectIgnoreNulls(s1, s1);
            parse(')');
            return parseWindowFunction(null, s1, s2);
        }
        return null;
    }

    private final Field<?> parseFieldNthValueIf() {
        if (parseFunctionNameIf("NTH_VALUE")) {
            parse('(');
            Field<?> f1 = parseField();
            parse(',');
            Field<?> f2 = parseField();
            WindowFromFirstLastStep<?> s1 = DSL.nthValue((Field) f1, (Field<Integer>) f2);
            WindowIgnoreNullsStep s2 = parseWindowFromFirstLast(s1, s1);
            WindowOverStep<?> s3 = parseWindowRespectIgnoreNulls(s2, s2);
            parse(')');
            return parseWindowFunction(s1, s2, s3);
        }
        return null;
    }

    private final Field<?> parseWindowFunction(WindowFromFirstLastStep s1, WindowIgnoreNullsStep s2, WindowOverStep<?> s3) {
        WindowOverStep<?> s32 = parseWindowRespectIgnoreNulls(parseWindowFromFirstLast(s1, s2), s3);
        parseKeyword("OVER");
        Object nameOrSpecification = parseWindowNameOrSpecification(true);
        if (nameOrSpecification instanceof Name) {
            Name n = (Name) nameOrSpecification;
            return s32.over(n);
        }
        if (nameOrSpecification instanceof WindowSpecification) {
            WindowSpecification w = (WindowSpecification) nameOrSpecification;
            return s32.over(w);
        }
        return s32.over();
    }

    private final WindowOverStep<?> parseWindowRespectIgnoreNulls(WindowIgnoreNullsStep s2, WindowOverStep<?> s3) {
        if (s2 != null) {
            if (parseKeywordIf("RESPECT NULLS")) {
                s3 = s2.respectNulls();
            } else if (parseKeywordIf("IGNORE NULLS")) {
                s3 = s2.ignoreNulls();
            } else {
                s3 = s2;
            }
        }
        return s3;
    }

    private final WindowIgnoreNullsStep parseWindowFromFirstLast(WindowFromFirstLastStep s1, WindowIgnoreNullsStep s2) {
        if (s1 != null) {
            if (parseKeywordIf("FROM FIRST")) {
                s2 = s1.fromFirst();
            } else if (parseKeywordIf("FROM LAST")) {
                s2 = s1.fromLast();
            } else {
                s2 = s1;
            }
        }
        return s2;
    }

    private final AggregateFunction<?> parseBinarySetFunctionIf() {
        switch (characterUpper()) {
            case 'C':
                if (parseFunctionNameIf("CORR")) {
                    return parseBinarySetFunction(DSL::corr);
                }
                if (parseFunctionNameIf("COVAR_POP")) {
                    return parseBinarySetFunction(DSL::covarPop);
                }
                if (parseFunctionNameIf("COVAR_SAMP")) {
                    return parseBinarySetFunction(DSL::covarSamp);
                }
                return null;
            case Opcodes.DASTORE /* 82 */:
                if (parseFunctionNameIf("REGR_AVGX")) {
                    return parseBinarySetFunction(DSL::regrAvgX);
                }
                if (parseFunctionNameIf("REGR_AVGY")) {
                    return parseBinarySetFunction(DSL::regrAvgY);
                }
                if (parseFunctionNameIf("REGR_COUNT")) {
                    return parseBinarySetFunction(DSL::regrCount);
                }
                if (parseFunctionNameIf("REGR_INTERCEPT")) {
                    return parseBinarySetFunction(DSL::regrIntercept);
                }
                if (parseFunctionNameIf("REGR_R2")) {
                    return parseBinarySetFunction(DSL::regrR2);
                }
                if (parseFunctionNameIf("REGR_SLOPE")) {
                    return parseBinarySetFunction(DSL::regrSlope);
                }
                if (parseFunctionNameIf("REGR_SXX")) {
                    return parseBinarySetFunction(DSL::regrSXX);
                }
                if (parseFunctionNameIf("REGR_SXY")) {
                    return parseBinarySetFunction(DSL::regrSXY);
                }
                if (parseFunctionNameIf("REGR_SYY")) {
                    return parseBinarySetFunction(DSL::regrSYY);
                }
                return null;
            default:
                return null;
        }
    }

    private final AggregateFunction<?> parseBinarySetFunction(BiFunction<? super Field<? extends Number>, ? super Field<? extends Number>, ? extends AggregateFunction<?>> function) {
        parse('(');
        parseKeywordIf("ALL");
        Field<?> parseField = parseField();
        parse(',');
        Field<?> parseField2 = parseField();
        parse(')');
        return function.apply(parseField, parseField2);
    }

    private final AggregateFilterStep<?> parseOrderedSetFunctionIf() {
        boolean optionalWithinGroup = false;
        OrderedAggregateFunction<?> orderedN = parseHypotheticalSetFunctionIf();
        if (orderedN == null) {
            orderedN = parseInverseDistributionFunctionIf();
        }
        if (orderedN == null) {
            OrderedAggregateFunction<String> parseListaggFunctionIf = parseListaggFunctionIf();
            orderedN = parseListaggFunctionIf;
            optionalWithinGroup = parseListaggFunctionIf != null;
        }
        if (orderedN != null) {
            return orderedN.withinGroupOrderBy(parseWithinGroupN(optionalWithinGroup));
        }
        OrderedAggregateFunctionOfDeferredType ordered1 = parseModeIf();
        if (ordered1 != null) {
            return ordered1.withinGroupOrderBy(parseWithinGroup1());
        }
        return null;
    }

    private final AggregateFilterStep<?> parseArrayAggFunctionIf() {
        ArrayAggOrderByStep<?> arrayAgg;
        if (parseKeywordIf("ARRAY_AGG")) {
            parse('(');
            boolean distinct = parseSetQuantifier();
            Field<?> a1 = parseField();
            List<SortField<?>> sort = null;
            if (parseKeywordIf("ORDER BY")) {
                sort = parseList(',', c -> {
                    return c.parseSortField();
                });
            }
            parse(')');
            if (distinct) {
                arrayAgg = DSL.arrayAggDistinct(a1);
            } else {
                arrayAgg = DSL.arrayAgg(a1);
            }
            ArrayAggOrderByStep<?> s1 = arrayAgg;
            return sort == null ? s1 : s1.orderBy(sort);
        }
        return null;
    }

    private final AggregateFilterStep<?> parseMultisetAggFunctionIf() {
        if (parseKeywordIf("MULTISET_AGG")) {
            parse('(');
            parseKeywordIf("ALL");
            List<Field<?>> fields = parseList(',', c -> {
                return c.parseField();
            });
            List<SortField<?>> sort = null;
            if (parseKeywordIf("ORDER BY")) {
                sort = parseList(',', c2 -> {
                    return c2.parseSortField();
                });
            }
            parse(')');
            ArrayAggOrderByStep<?> s1 = DSL.multisetAgg(fields);
            return sort == null ? s1 : s1.orderBy(sort);
        }
        return null;
    }

    private final List<SortField<?>> parseWithinGroupN() {
        return parseWithinGroupN(false);
    }

    private final List<SortField<?>> parseWithinGroupN(boolean optional) {
        if (optional) {
            if (!parseKeywordIf("WITHIN GROUP")) {
                return Collections.emptyList();
            }
        } else {
            parseKeyword("WITHIN GROUP");
        }
        parse('(');
        parseKeyword("ORDER BY");
        List<SortField<?>> result = parseList(',', c -> {
            return c.parseSortField();
        });
        parse(')');
        return result;
    }

    private final SortField<?> parseWithinGroup1() {
        parseKeyword("WITHIN GROUP");
        parse('(');
        parseKeyword("ORDER BY");
        SortField<?> result = parseSortField();
        parse(')');
        return result;
    }

    private final OrderedAggregateFunction<?> parseHypotheticalSetFunctionIf() {
        OrderedAggregateFunction<?> ordered;
        if (parseFunctionNameIf("RANK")) {
            parse('(');
            ordered = DSL.rank(parseList(',', c -> {
                return c.parseField();
            }));
            parse(')');
        } else if (parseFunctionNameIf("DENSE_RANK")) {
            parse('(');
            ordered = DSL.denseRank(parseList(',', c2 -> {
                return c2.parseField();
            }));
            parse(')');
        } else if (parseFunctionNameIf("PERCENT_RANK")) {
            parse('(');
            ordered = DSL.percentRank(parseList(',', c3 -> {
                return c3.parseField();
            }));
            parse(')');
        } else if (parseFunctionNameIf("CUME_DIST")) {
            parse('(');
            ordered = DSL.cumeDist(parseList(',', c4 -> {
                return c4.parseField();
            }));
            parse(')');
        } else {
            ordered = null;
        }
        return ordered;
    }

    private final OrderedAggregateFunction<BigDecimal> parseInverseDistributionFunctionIf() {
        OrderedAggregateFunction<BigDecimal> ordered;
        if (parseFunctionNameIf("PERCENTILE_CONT")) {
            parse('(');
            parseKeywordIf("ALL");
            ordered = DSL.percentileCont((Field<? extends Number>) parseField());
            parse(')');
        } else if (parseFunctionNameIf("PERCENTILE_DISC")) {
            parse('(');
            parseKeywordIf("ALL");
            ordered = DSL.percentileDisc((Field<? extends Number>) parseField());
            parse(')');
        } else {
            ordered = null;
        }
        return ordered;
    }

    private final OrderedAggregateFunction<String> parseListaggFunctionIf() {
        OrderedAggregateFunction<String> ordered;
        if (parseFunctionNameIf("LISTAGG", "STRING_AGG")) {
            parse('(');
            boolean distinct = parseSetQuantifier();
            Field<?> field = parseField();
            if (parseIf(',')) {
                ordered = distinct ? DSL.listAggDistinct(field, parseStringLiteral()) : DSL.listAgg(field, parseStringLiteral());
            } else {
                ordered = distinct ? DSL.listAggDistinct(field) : DSL.listAgg(field);
            }
            parse(')');
        } else {
            ordered = null;
        }
        return ordered;
    }

    private final OrderedAggregateFunctionOfDeferredType parseModeIf() {
        OrderedAggregateFunctionOfDeferredType ordered;
        if (parseFunctionNameIf("MODE")) {
            parse('(');
            parse(')');
            ordered = DSL.mode();
        } else {
            ordered = null;
        }
        return ordered;
    }

    private final Field<?> parseGeneralSetFunctionIf() {
        boolean distinct;
        ComputationalOperation operation = parseComputationalOperationIf();
        if (operation == null) {
            return null;
        }
        parse('(');
        switch (operation) {
            case ANY_VALUE:
            case AVG:
            case MAX:
            case MIN:
            case SUM:
            case PRODUCT:
            case EVERY:
            case ANY:
                distinct = parseSetQuantifier();
                break;
            default:
                parseKeywordIf("ALL");
                distinct = false;
                break;
        }
        Field arg = parseField();
        switch (operation.ordinal()) {
            case 2:
            case 3:
                if (!distinct && parseIf(',')) {
                    List<Field<?>> fields = parseList(',', c -> {
                        return c.parseField();
                    });
                    parse(')');
                    return operation == ComputationalOperation.MAX ? DSL.greatest(arg, (Field<?>[]) fields.toArray(Tools.EMPTY_FIELD)) : DSL.least(arg, (Field<?>[]) fields.toArray(Tools.EMPTY_FIELD));
                }
                break;
        }
        parse(')');
        switch (operation) {
            case ANY_VALUE:
                return DSL.anyValue(arg);
            case AVG:
                return distinct ? DSL.avgDistinct(arg) : DSL.avg(arg);
            case MAX:
                return distinct ? DSL.maxDistinct(arg) : DSL.max(arg);
            case MIN:
                return distinct ? DSL.minDistinct(arg) : DSL.min(arg);
            case SUM:
                return distinct ? DSL.sumDistinct(arg) : DSL.sum(arg);
            case PRODUCT:
                return distinct ? DSL.productDistinct(arg) : DSL.product(arg);
            case EVERY:
                return DSL.every((Field<Boolean>) arg);
            case ANY:
                return DSL.boolOr((Field<Boolean>) arg);
            case SOME:
            case COUNT:
            default:
                throw exception("Unsupported computational operation");
            case STDDEV_POP:
                return DSL.stddevPop(arg);
            case STDDEV_SAMP:
                return DSL.stddevSamp(arg);
            case VAR_POP:
                return DSL.varPop(arg);
            case VAR_SAMP:
                return DSL.varSamp(arg);
            case MEDIAN:
                return DSL.median(arg);
        }
    }

    private final AggregateFunction<?> parseCountIf() {
        Field<?>[] fieldArr;
        if (parseFunctionNameIf("COUNT")) {
            parse('(');
            boolean distinct = parseSetQuantifier();
            if (parseIf('*') && parse(')')) {
                if (distinct) {
                    return DSL.countDistinct(DSL.asterisk());
                }
                return DSL.count();
            }
            Field<?>[] fields = null;
            QualifiedAsterisk asterisk = null;
            Row row = parseRowIf();
            if (row != null) {
                fields = row.fields();
            } else {
                QualifiedAsterisk parseQualifiedAsteriskIf = parseQualifiedAsteriskIf();
                asterisk = parseQualifiedAsteriskIf;
                if (parseQualifiedAsteriskIf == null) {
                    if (distinct) {
                        fieldArr = (Field[]) parseList(',', c -> {
                            return c.parseField();
                        }).toArray(Tools.EMPTY_FIELD);
                    } else {
                        fieldArr = new Field[]{parseField()};
                    }
                    fields = fieldArr;
                }
            }
            parse(')');
            if (distinct) {
                if (fields == null) {
                    return DSL.countDistinct(asterisk);
                }
                if (fields.length == 1) {
                    return DSL.countDistinct(fields[0]);
                }
                return DSL.countDistinct(fields);
            }
            if (fields == null) {
                return DSL.count(asterisk);
            }
            return DSL.count(fields[0]);
        }
        return null;
    }

    private final WindowBeforeOverStep<Integer> parseCountIfIf() {
        if (parseFunctionNameIf("COUNTIF", "COUNT_IF")) {
            parse('(');
            Condition condition = parseCondition();
            parse(')');
            return DSL.count().filterWhere(condition);
        }
        return null;
    }

    private final boolean parseSetQuantifier() {
        boolean distinct = parseKeywordIf("DISTINCT");
        if (!distinct) {
            parseKeywordIf("ALL");
        }
        return distinct;
    }

    private final Domain<?> parseDomainName() {
        return DSL.domain(parseName());
    }

    private final Catalog parseCatalogName() {
        return DSL.catalog(parseName());
    }

    private final Schema parseSchemaName() {
        return DSL.schema(parseName());
    }

    private final Table<?> parseTableName() {
        return lookupTable(position(), parseName());
    }

    private final Table<?> parseTableNameIf() {
        int positionBeforeName = position();
        Name name = parseNameIf();
        if (name == null) {
            return null;
        }
        return lookupTable(positionBeforeName, name);
    }

    private final Field<?> parseFieldNameOrSequenceExpression() {
        List<Field<?>> arguments;
        int positionBeforeName = position();
        Name name = parseName();
        if (name.qualified()) {
            String first = name.first();
            String last = name.last();
            if ("NEXTVAL".equalsIgnoreCase(last)) {
                return DSL.sequence(name.qualifier()).nextval();
            }
            if ("CURRVAL".equalsIgnoreCase(last)) {
                return DSL.sequence(name.qualifier()).currval();
            }
            if (Boolean.TRUE.equals(data(Tools.BooleanDataKey.DATA_PARSE_ON_CONFLICT)) && "EXCLUDED".equalsIgnoreCase(first)) {
                return DSL.excluded(DSL.field(name.unqualifiedName()));
            }
        }
        if (this.dsl.settings().getParseUnknownFunctions() == ParseUnknownFunctions.IGNORE && peek('(') && !peekTokens('(', '+', ')')) {
            position();
            parse('(');
            if (!parseIf(')')) {
                arguments = parseList(',', c -> {
                    return c.parseField();
                });
                parse(')');
            } else {
                arguments = new ArrayList<>();
            }
            if (isDDLDatabase()) {
                return DSL.inline((Object) null);
            }
            return DSL.function(name, Object.class, (Field<?>[]) arguments.toArray(Tools.EMPTY_FIELD));
        }
        return lookupField(positionBeforeName, name);
    }

    private final TableField<?, ?> parseFieldName() {
        return (TableField) lookupField(this.position, parseName());
    }

    private final Sequence<?> parseSequenceName() {
        return DSL.sequence(parseName());
    }

    private final Name parseIndexName() {
        Name result = parseNameIf();
        if (result == null) {
            throw expected("Identifier");
        }
        return result;
    }

    private final Name parseIndexNameIf() {
        if (!peekKeyword("ON")) {
            return parseNameIf();
        }
        return null;
    }

    private final Collation parseCollation() {
        return DSL.collation(parseNameOrStringLiteral());
    }

    private final CharacterSet parseCharacterSet() {
        return DSL.characterSet(parseNameOrStringLiteral());
    }

    public final Name parseNameOrStringLiteral() {
        Name result = parseNameIf();
        if (result == null) {
            return DSL.name(parseStringLiteral());
        }
        return result;
    }

    @Override // org.jooq.ParseContext
    public final Name parseName() {
        Name result = parseNameIf();
        if (result == null) {
            throw expected("Identifier");
        }
        return result;
    }

    @Override // org.jooq.ParseContext
    public final Name parseNameIf() {
        Name identifier = parseIdentifierIf();
        if (identifier == null) {
            return null;
        }
        if (peek('.') && !peek(CallerDataConverter.DEFAULT_RANGE_DELIMITER)) {
            return parseNameQualified('.', identifier);
        }
        return identifier;
    }

    private final Name parseNameQualified(char separator, Name firstPart) {
        List<Name> result = new ArrayList<>();
        result.add(firstPart);
        while (parseIf(separator)) {
            result.add(parseIdentifier());
        }
        return DSL.name((Name[]) result.toArray(Tools.EMPTY_NAME));
    }

    private final QualifiedAsterisk parseQualifiedAsteriskIf() {
        int positionBeforeName = position();
        Name i1 = parseIdentifierIf();
        if (i1 == null) {
            return null;
        }
        if (parseIf('.')) {
            List<Name> result = null;
            do {
                Name i2 = parseIdentifierIf();
                if (i2 != null) {
                    if (result == null) {
                        result = new ArrayList<>();
                        result.add(i1);
                    }
                    result.add(i2);
                } else {
                    parse('*');
                    return lookupQualifiedAsterisk(positionBeforeName, result == null ? i1 : DSL.name((Name[]) result.toArray(Tools.EMPTY_NAME)));
                }
            } while (parseIf('.'));
        }
        position(positionBeforeName);
        return null;
    }

    private final List<Name> parseIdentifiers() {
        LinkedHashSet<Name> result = new LinkedHashSet<>();
        while (result.add(parseIdentifier())) {
            if (!parseIf(',')) {
                return new ArrayList(result);
            }
        }
        throw exception("Duplicate identifier encountered");
    }

    @Override // org.jooq.ParseContext
    public final Name parseIdentifier() {
        return parseIdentifier(false, false);
    }

    private final Name parseIdentifier(boolean allowAposQuotes, boolean allowPartAsStart) {
        Name result = parseIdentifierIf(allowAposQuotes, allowPartAsStart);
        if (result == null) {
            throw expected("Identifier");
        }
        return result;
    }

    @Override // org.jooq.ParseContext
    public final Name parseIdentifierIf() {
        return parseIdentifierIf(false, false);
    }

    private final Name parseIdentifierIf(boolean allowAposQuotes, boolean allowPartAsStart) {
        char quoteEnd = parseQuote(allowAposQuotes);
        boolean quoted = quoteEnd != 0;
        int start = position();
        StringBuilder sb = new StringBuilder();
        if (quoted) {
            while (true) {
                char c = character();
                if ((c == quoteEnd || !hasMore() || !positionInc()) && (character(this.position + 1) != quoteEnd || !hasMore(1) || !positionInc(2))) {
                    break;
                }
                sb.append(c);
            }
        } else if (!allowPartAsStart ? isIdentifierStart() : isIdentifierPart()) {
            do {
                sb.append(character());
                positionInc();
                if (!isIdentifierPart()) {
                    break;
                }
            } while (hasMore());
        }
        if (position() == start) {
            return null;
        }
        String name = Tools.normaliseNameCase(configuration(), sb.toString(), quoted, this.locale);
        if (quoted) {
            if (character() != quoteEnd) {
                throw exception("Quoted identifier must terminate in " + quoteEnd + ". Start of identifier: " + StringUtils.abbreviate(sb.toString(), 30));
            }
            positionInc();
            parseWhitespaceIf();
            return DSL.quotedName(name);
        }
        parseWhitespaceIf();
        return DSL.unquotedName(name);
    }

    private final char parseQuote(boolean allowAposQuotes) {
        if (parseIf('\"', false)) {
            return '\"';
        }
        if (parseIf('`', false)) {
            return '`';
        }
        if (parseIf('[', false)) {
            return ']';
        }
        return (allowAposQuotes && parseIf('\'', false)) ? '\'' : (char) 0;
    }

    private final DataType<?> parseCastDataType() {
        char character = characterUpper();
        switch (character) {
            case 'S':
                if (parseKeywordIf("SIGNED")) {
                    if (!parseKeywordIf("INTEGER")) {
                    }
                    return SQLDataType.BIGINT;
                }
                break;
            case Opcodes.CASTORE /* 85 */:
                if (parseKeywordIf("UNSIGNED")) {
                    if (!parseKeywordIf("INTEGER")) {
                    }
                    return SQLDataType.BIGINTUNSIGNED;
                }
                break;
        }
        return parseDataType();
    }

    @Override // org.jooq.ParseContext
    public final DataType<?> parseDataType() {
        DataType<?> result = parseDataTypeIf(true);
        if (result == null) {
            throw expected("Data type");
        }
        return result;
    }

    /* JADX WARN: Code restructure failed: missing block: B:10:0x0035, code lost:            if (r6 != false) goto L15;     */
    /* JADX WARN: Code restructure failed: missing block: B:14:0x0039, code lost:            return r5;     */
    /* JADX WARN: Code restructure failed: missing block: B:2:0x0007, code lost:            if (r5 != null) goto L4;     */
    /* JADX WARN: Code restructure failed: missing block: B:3:0x000a, code lost:            r6 = parseKeywordIf("ARRAY");     */
    /* JADX WARN: Code restructure failed: missing block: B:4:0x0018, code lost:            if (parseIf('[') == false) goto L7;     */
    /* JADX WARN: Code restructure failed: missing block: B:5:0x001b, code lost:            parseUnsignedIntegerLiteralIf();        parse(']');        r6 = true;     */
    /* JADX WARN: Code restructure failed: missing block: B:7:0x002a, code lost:            if (r6 == false) goto L10;     */
    /* JADX WARN: Code restructure failed: missing block: B:8:0x002d, code lost:            r5 = r5.getArrayDataType();     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private final org.jooq.DataType<?> parseDataTypeIf(boolean r4) {
        /*
            r3 = this;
            r0 = r3
            r1 = r4
            org.jooq.DataType r0 = r0.parseDataTypePrefixIf(r1)
            r5 = r0
            r0 = r5
            if (r0 == 0) goto L38
        La:
            r0 = r3
            java.lang.String r1 = "ARRAY"
            boolean r0 = r0.parseKeywordIf(r1)
            r6 = r0
            r0 = r3
            r1 = 91
            boolean r0 = r0.parseIf(r1)
            if (r0 == 0) goto L29
            r0 = r3
            java.lang.Long r0 = r0.parseUnsignedIntegerLiteralIf()
            r0 = r3
            r1 = 93
            boolean r0 = r0.parse(r1)
            r0 = 1
            r6 = r0
        L29:
            r0 = r6
            if (r0 == 0) goto L34
            r0 = r5
            org.jooq.DataType r0 = r0.getArrayDataType()
            r5 = r0
        L34:
            r0 = r6
            if (r0 != 0) goto La
        L38:
            r0 = r5
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jooq.impl.DefaultParseContext.parseDataTypeIf(boolean):org.jooq.DataType");
    }

    private final DataType<?> parseDataTypePrefixIf(boolean parseUnknownTypes) {
        Name name;
        char character = characterUpper();
        if (character == '[' || character == '\"' || character == '`') {
            character = characterNextUpper();
        }
        switch (character) {
            case 'A':
                if (parseKeywordOrIdentifierIf("ARRAY")) {
                    return SQLDataType.OTHER.getArrayDataType();
                }
                if (parseKeywordIf("AUTO_INCREMENT")) {
                    parseDataTypeIdentityArgsIf();
                    return SQLDataType.INTEGER.identity(true);
                }
                break;
            case 'B':
                if (parseKeywordOrIdentifierIf("BIGINT")) {
                    return parseUnsigned(parseAndIgnoreDataTypeLength(SQLDataType.BIGINT));
                }
                if (parseKeywordOrIdentifierIf("BIGSERIAL")) {
                    return SQLDataType.BIGINT.identity(true);
                }
                if (parseKeywordOrIdentifierIf("BINARY")) {
                    if (parseKeywordIf("VARYING")) {
                        return parseDataTypeLength(SQLDataType.VARBINARY);
                    }
                    return parseDataTypeLength(SQLDataType.BINARY);
                }
                if (parseKeywordOrIdentifierIf("BIT")) {
                    return parseDataTypeLength(SQLDataType.BIT);
                }
                if (parseKeywordOrIdentifierIf("BLOB")) {
                    if (parseKeywordIf("SUB_TYPE")) {
                        if (parseKeywordIf(CustomBooleanEditor.VALUE_0, "BINARY")) {
                            return parseDataTypeLength(SQLDataType.BLOB);
                        }
                        if (parseKeywordIf(CustomBooleanEditor.VALUE_1, "TEXT")) {
                            return parseDataTypeLength(SQLDataType.CLOB);
                        }
                        throw expected(CustomBooleanEditor.VALUE_0, "BINARY", CustomBooleanEditor.VALUE_1, "TEXT");
                    }
                    return parseDataTypeLength(SQLDataType.BLOB);
                }
                if (parseKeywordOrIdentifierIf("BOOLEAN") || parseKeywordOrIdentifierIf("BOOL")) {
                    return SQLDataType.BOOLEAN;
                }
                if (parseKeywordOrIdentifierIf("BYTEA")) {
                    return SQLDataType.BLOB;
                }
                break;
            case 'C':
                if (parseKeywordOrIdentifierIf("CHAR") || parseKeywordOrIdentifierIf("CHARACTER")) {
                    if (parseKeywordIf("VARYING")) {
                        return parseDataTypeCollation(parseDataTypeLength(SQLDataType.VARCHAR, SQLDataType.VARBINARY, () -> {
                            return parseKeywordIf("FOR BIT DATA");
                        }));
                    }
                    if (parseKeywordIf("LARGE OBJECT")) {
                        return parseDataTypeCollation(parseDataTypeLength(SQLDataType.CLOB));
                    }
                    return parseDataTypeCollation(parseDataTypeLength(SQLDataType.CHAR, SQLDataType.BINARY, () -> {
                        return parseKeywordIf("FOR BIT DATA");
                    }));
                }
                if (parseKeywordOrIdentifierIf("CITEXT")) {
                    return parseDataTypeCollation(parseAndIgnoreDataTypeLength(SQLDataType.CLOB));
                }
                if (parseKeywordOrIdentifierIf("CLOB")) {
                    return parseDataTypeCollation(parseDataTypeLength(SQLDataType.CLOB));
                }
                break;
            case 'D':
                if (parseKeywordOrIdentifierIf("DATE")) {
                    return SQLDataType.DATE;
                }
                if (parseKeywordOrIdentifierIf("DATETIME")) {
                    return parseDataTypePrecisionIf(SQLDataType.TIMESTAMP);
                }
                if (parseKeywordOrIdentifierIf("DECIMAL") || parseKeywordOrIdentifierIf("DEC")) {
                    return parseDataTypePrecisionScaleIf(SQLDataType.DECIMAL);
                }
                if (parseKeywordOrIdentifierIf("DOUBLE PRECISION") || parseKeywordOrIdentifierIf("DOUBLE")) {
                    return parseAndIgnoreDataTypePrecisionScaleIf(SQLDataType.DOUBLE);
                }
                break;
            case TypeReference.CONSTRUCTOR_REFERENCE /* 69 */:
                if (parseKeywordOrIdentifierIf("ENUM")) {
                    return parseDataTypeCollation(parseDataTypeEnum());
                }
                break;
            case 'F':
                if (parseKeywordOrIdentifierIf("FLOAT")) {
                    return parseAndIgnoreDataTypePrecisionScaleIf(SQLDataType.FLOAT);
                }
                break;
            case TypeReference.CAST /* 71 */:
                if ((ignoreProEdition() || ((!parseKeywordOrIdentifierIf("GEOMETRY") && !parseKeywordOrIdentifierIf("SDO_GEOMETRY")) || !requireProEdition())) && !ignoreProEdition() && parseKeywordOrIdentifierIf("GEOGRAPHY") && requireProEdition()) {
                }
                break;
            case 'I':
                if (parseKeywordOrIdentifierIf("INTEGER") || parseKeywordOrIdentifierIf("INT") || parseKeywordOrIdentifierIf("INT4")) {
                    return parseUnsigned(parseAndIgnoreDataTypeLength(SQLDataType.INTEGER));
                }
                if (parseKeywordOrIdentifierIf("INT2")) {
                    return SQLDataType.SMALLINT;
                }
                if (parseKeywordOrIdentifierIf("INT8")) {
                    return SQLDataType.BIGINT;
                }
                if (parseKeywordIf("INTERVAL")) {
                    if (parseKeywordIf("YEAR")) {
                        parseDataTypePrecisionIf();
                        parseKeyword("TO MONTH");
                        return SQLDataType.INTERVALYEARTOMONTH;
                    }
                    if (parseKeywordIf("DAY")) {
                        parseDataTypePrecisionIf();
                        parseKeyword("TO SECOND");
                        parseDataTypePrecisionIf();
                        return SQLDataType.INTERVALDAYTOSECOND;
                    }
                    return SQLDataType.INTERVAL;
                }
                if (parseKeywordIf("IDENTITY")) {
                    parseDataTypeIdentityArgsIf();
                    return SQLDataType.INTEGER.identity(true);
                }
                break;
            case 'J':
                if (parseKeywordOrIdentifierIf(JsonFactory.FORMAT_NAME_JSON)) {
                    return SQLDataType.JSON;
                }
                if (parseKeywordOrIdentifierIf("JSONB")) {
                    return SQLDataType.JSONB;
                }
                break;
            case BaseNCodec.MIME_CHUNK_SIZE /* 76 */:
                if (parseKeywordOrIdentifierIf("LONGBLOB")) {
                    return SQLDataType.BLOB;
                }
                if (parseKeywordOrIdentifierIf("LONGTEXT")) {
                    return parseDataTypeCollation(SQLDataType.CLOB);
                }
                if (parseKeywordOrIdentifierIf("LONG NVARCHAR")) {
                    return parseDataTypeCollation(parseDataTypeLength(SQLDataType.LONGNVARCHAR));
                }
                if (parseKeywordOrIdentifierIf("LONG VARBINARY") || parseKeywordOrIdentifierIf("LONGVARBINARY")) {
                    return parseDataTypeCollation(parseDataTypeLength(SQLDataType.LONGVARBINARY));
                }
                if (parseKeywordOrIdentifierIf("LONG VARCHAR") || parseKeywordOrIdentifierIf("LONGVARCHAR")) {
                    return parseDataTypeCollation(parseDataTypeLength(SQLDataType.LONGVARCHAR, SQLDataType.LONGVARBINARY, () -> {
                        return parseKeywordIf("FOR BIT DATA");
                    }));
                }
                break;
            case 'M':
                if (parseKeywordOrIdentifierIf("MEDIUMBLOB")) {
                    return SQLDataType.BLOB;
                }
                if (parseKeywordOrIdentifierIf("MEDIUMINT")) {
                    return parseUnsigned(parseAndIgnoreDataTypeLength(SQLDataType.INTEGER));
                }
                if (parseKeywordOrIdentifierIf("MEDIUMTEXT")) {
                    return parseDataTypeCollation(SQLDataType.CLOB);
                }
                break;
            case 'N':
                if (parseKeywordIf("NATIONAL CHARACTER") || parseKeywordIf("NATIONAL CHAR")) {
                    if (parseKeywordIf("VARYING")) {
                        return parseDataTypeCollation(parseDataTypeLength(SQLDataType.NVARCHAR));
                    }
                    if (parseKeywordIf("LARGE OBJECT")) {
                        return parseDataTypeCollation(parseDataTypeLength(SQLDataType.NCLOB));
                    }
                    return parseDataTypeCollation(parseDataTypeLength(SQLDataType.NCHAR));
                }
                if (parseKeywordOrIdentifierIf("NCHAR")) {
                    if (parseKeywordIf("VARYING")) {
                        return parseDataTypeCollation(parseDataTypeLength(SQLDataType.NVARCHAR));
                    }
                    if (parseKeywordIf("LARGE OBJECT")) {
                        return parseDataTypeCollation(parseDataTypeLength(SQLDataType.NCLOB));
                    }
                    return parseDataTypeCollation(parseDataTypeLength(SQLDataType.NCHAR));
                }
                if (parseKeywordOrIdentifierIf("NCLOB")) {
                    return parseDataTypeCollation(parseDataTypeLength(SQLDataType.NCLOB));
                }
                if (parseKeywordOrIdentifierIf("NUMBER") || parseKeywordOrIdentifierIf("NUMERIC")) {
                    return parseDataTypePrecisionScaleIf(SQLDataType.NUMERIC);
                }
                if (parseKeywordOrIdentifierIf("NVARCHAR") || parseKeywordOrIdentifierIf("NVARCHAR2")) {
                    return parseDataTypeCollation(parseDataTypeLength(SQLDataType.NVARCHAR));
                }
                break;
            case Opcodes.IASTORE /* 79 */:
                if (parseKeywordOrIdentifierIf("OTHER")) {
                    return SQLDataType.OTHER;
                }
                break;
            case Opcodes.DASTORE /* 82 */:
                if (parseKeywordOrIdentifierIf("REAL")) {
                    return parseAndIgnoreDataTypePrecisionScaleIf(SQLDataType.REAL);
                }
                break;
            case 'S':
                if (parseKeywordOrIdentifierIf("SERIAL4") || parseKeywordOrIdentifierIf("SERIAL")) {
                    return SQLDataType.INTEGER.identity(true);
                }
                if (parseKeywordOrIdentifierIf("SERIAL8")) {
                    return SQLDataType.BIGINT.identity(true);
                }
                if (parseKeywordOrIdentifierIf("SET")) {
                    return parseDataTypeCollation(parseDataTypeEnum());
                }
                if (parseKeywordOrIdentifierIf("SMALLINT")) {
                    return parseUnsigned(parseAndIgnoreDataTypeLength(SQLDataType.SMALLINT));
                }
                if (parseKeywordOrIdentifierIf("SMALLSERIAL") || parseKeywordOrIdentifierIf("SERIAL2")) {
                    return SQLDataType.SMALLINT.identity(true);
                }
                if (parseKeywordOrIdentifierIf("STRING")) {
                    return parseDataTypeCollation(parseDataTypeLength(SQLDataType.VARCHAR));
                }
                break;
            case Opcodes.BASTORE /* 84 */:
                if (parseKeywordOrIdentifierIf("TEXT")) {
                    return parseDataTypeCollation(parseAndIgnoreDataTypeLength(SQLDataType.CLOB));
                }
                if (parseKeywordOrIdentifierIf("TIMESTAMPTZ")) {
                    return parseDataTypePrecisionIf(SQLDataType.TIMESTAMPWITHTIMEZONE);
                }
                if (parseKeywordOrIdentifierIf("TIMESTAMP")) {
                    Integer precision = parseDataTypePrecisionIf();
                    if (parseKeywordOrIdentifierIf("WITH TIME ZONE")) {
                        return precision == null ? SQLDataType.TIMESTAMPWITHTIMEZONE : SQLDataType.TIMESTAMPWITHTIMEZONE(precision.intValue());
                    }
                    if (!parseKeywordOrIdentifierIf("WITHOUT TIME ZONE")) {
                    }
                    return precision == null ? SQLDataType.TIMESTAMP : SQLDataType.TIMESTAMP(precision.intValue());
                }
                if (parseKeywordOrIdentifierIf("TIMETZ")) {
                    return parseDataTypePrecisionIf(SQLDataType.TIMEWITHTIMEZONE);
                }
                if (parseKeywordOrIdentifierIf("TIME")) {
                    Integer precision2 = parseDataTypePrecisionIf();
                    if (parseKeywordOrIdentifierIf("WITH TIME ZONE")) {
                        return precision2 == null ? SQLDataType.TIMEWITHTIMEZONE : SQLDataType.TIMEWITHTIMEZONE(precision2.intValue());
                    }
                    if (!parseKeywordOrIdentifierIf("WITHOUT TIME ZONE")) {
                    }
                    return precision2 == null ? SQLDataType.TIME : SQLDataType.TIME(precision2.intValue());
                }
                if (parseKeywordOrIdentifierIf("TINYBLOB")) {
                    return SQLDataType.BLOB;
                }
                if (parseKeywordOrIdentifierIf("TINYINT")) {
                    return parseUnsigned(parseAndIgnoreDataTypeLength(SQLDataType.TINYINT));
                }
                if (parseKeywordOrIdentifierIf("TINYTEXT")) {
                    return parseDataTypeCollation(SQLDataType.CLOB);
                }
                break;
            case Opcodes.CASTORE /* 85 */:
                if (parseKeywordOrIdentifierIf("UUID")) {
                    return SQLDataType.UUID;
                }
                if (parseKeywordOrIdentifierIf("UNIQUEIDENTIFIER")) {
                    return SQLDataType.UUID;
                }
                break;
            case Opcodes.SASTORE /* 86 */:
                if (parseKeywordOrIdentifierIf("VARCHAR") || parseKeywordOrIdentifierIf("VARCHAR2") || parseKeywordOrIdentifierIf("VARCHAR_IGNORECASE")) {
                    return parseDataTypeCollation(parseDataTypeLength(SQLDataType.VARCHAR, SQLDataType.VARBINARY, () -> {
                        return parseKeywordIf("FOR BIT DATA");
                    }));
                }
                if (parseKeywordOrIdentifierIf("VARBINARY")) {
                    return parseDataTypeLength(SQLDataType.VARBINARY);
                }
                break;
            case 'X':
                if (parseKeywordOrIdentifierIf("XML")) {
                    return SQLDataType.XML;
                }
                break;
            case Opcodes.DUP /* 89 */:
                if (parseKeywordOrIdentifierIf("YEAR")) {
                    return parseDataTypeLength(SQLDataType.YEAR);
                }
                break;
        }
        if (parseUnknownTypes && (name = parseNameIf()) != null) {
            return parseDataTypeLength(new DefaultDataType<>(this.dsl.dialect(), Object.class, name));
        }
        return null;
    }

    private final void parseDataTypeIdentityArgsIf() {
        if (parseIf('(')) {
            parseList(',', c -> {
                return c.parseField();
            });
            parse(')');
        }
    }

    private final boolean parseKeywordOrIdentifierIf(String keyword) {
        int p = position();
        char quoteEnd = parseQuote(false);
        boolean result = parseKeywordIf(keyword);
        if (!result) {
            position(p);
        } else if (quoteEnd != 0) {
            parse(quoteEnd);
        }
        return result;
    }

    private final DataType<?> parseUnsigned(DataType result) {
        if (parseKeywordIf("UNSIGNED")) {
            if (result == SQLDataType.TINYINT) {
                return SQLDataType.TINYINTUNSIGNED;
            }
            if (result == SQLDataType.SMALLINT) {
                return SQLDataType.SMALLINTUNSIGNED;
            }
            if (result == SQLDataType.INTEGER) {
                return SQLDataType.INTEGERUNSIGNED;
            }
            if (result == SQLDataType.BIGINT) {
                return SQLDataType.BIGINTUNSIGNED;
            }
        }
        return result;
    }

    private final DataType<?> parseAndIgnoreDataTypeLength(DataType<?> result) {
        if (parseIf('(')) {
            parseUnsignedIntegerLiteral();
            parse(')');
        }
        return result;
    }

    private final DataType<?> parseDataTypeLength(DataType<?> in) {
        return parseDataTypeLength(in, in, () -> {
            return false;
        });
    }

    private final DataType<?> parseDataTypeLength(DataType<?> in, DataType<?> alternative, BooleanSupplier alternativeIfTrue) {
        Integer length = null;
        if (parseIf('(')) {
            if (!parseKeywordIf("MAX")) {
                length = Integer.valueOf(Tools.asInt(parseUnsignedIntegerLiteral().longValue()));
            }
            if ((in == SQLDataType.VARCHAR || in == SQLDataType.CHAR) && !parseKeywordIf("BYTE")) {
                parseKeywordIf("CHAR");
            }
            parse(')');
        }
        DataType<?> result = alternativeIfTrue.getAsBoolean() ? alternative : in;
        return length == null ? result : result.length(length.intValue());
    }

    private final DataType<?> parseDataTypeCollation(DataType<?> result) {
        CharacterSet cs = parseCharacterSetSpecificationIf();
        if (cs != null) {
            result = result.characterSet(cs);
        }
        Collation col = parseCollateSpecificationIf();
        if (col != null) {
            result = result.collation(col);
        }
        return result;
    }

    private final CharacterSet parseCharacterSetSpecificationIf() {
        if (parseKeywordIf("CHARACTER SET", "CHARSET")) {
            parseIf('=');
            return parseCharacterSet();
        }
        return null;
    }

    private final Collation parseCollateSpecificationIf() {
        if (parseKeywordIf("COLLATE")) {
            parseIf('=');
            return parseCollation();
        }
        return null;
    }

    private final DataType<?> parseAndIgnoreDataTypePrecisionScaleIf(DataType<?> result) {
        if (parseIf('(')) {
            parseUnsignedIntegerLiteral();
            if (parseIf(',')) {
                parseUnsignedIntegerLiteral();
            }
            parse(')');
        }
        return result;
    }

    private final Integer parseDataTypePrecisionIf() {
        Integer precision = null;
        if (parseIf('(')) {
            precision = Integer.valueOf(Tools.asInt(parseUnsignedIntegerLiteral().longValue()));
            parse(')');
        }
        return precision;
    }

    private final DataType<?> parseDataTypePrecisionIf(DataType<?> result) {
        if (parseIf('(')) {
            int precision = Tools.asInt(parseUnsignedIntegerLiteral().longValue());
            result = result.precision(precision);
            parse(')');
        }
        return result;
    }

    private final DataType<?> parseDataTypePrecisionScaleIf(DataType<?> result) {
        if (parseIf('(')) {
            int precision = parseIf('*') ? 38 : Tools.asInt(parseUnsignedIntegerLiteral().longValue());
            if (parseIf(',')) {
                result = result.precision(precision, Tools.asInt(parseSignedIntegerLiteral().longValue()));
            } else {
                result = result.precision(precision);
            }
            parse(')');
        }
        return result;
    }

    private final DataType<?> parseDataTypeEnum() {
        parse('(');
        List<String> literals = new ArrayList<>();
        int length = 0;
        do {
            String literal = parseStringLiteral();
            length = Math.max(length, literal.length());
            literals.add(literal);
        } while (parseIf(','));
        parse(')');
        String className = "GeneratedEnum" + (literals.hashCode() & 134217727);
        StringBuilder content = new StringBuilder();
        content.append("package org.jooq.impl;\nenum ").append(className).append(" implements org.jooq.EnumType {\n");
        for (int i = 0; i < literals.size(); i++) {
            content.append("  E").append(i).append("(\"").append(literals.get(i).replace("\"", "\\\"")).append("\"),\n");
        }
        content.append("  ;\n  final String literal;\n  private ").append(className).append("(String literal) { this.literal = literal; }\n  @Override\n  public String getName() {\n    return null;\n  }\n  @Override\n  public String getLiteral() {\n    return literal;\n  }\n}");
        return SQLDataType.VARCHAR(length).asEnumDataType((Class) Reflect.compile("org.jooq.impl." + className, content.toString()).get());
    }

    private final char parseCharacterLiteral() {
        parse('\'', false);
        char c = character();
        if (c == '\'') {
            parse('\'', false);
        }
        positionInc();
        parse('\'');
        return c;
    }

    private final Field<?> parseBindVariableIf() {
        String paramName;
        int p = position();
        switch (character()) {
            case '?':
                parse('?');
                paramName = this.bindIndex;
                break;
            default:
                String prefix = (String) StringUtils.defaultIfNull(settings().getParseNamedParamPrefix(), ":");
                if (parseIf(prefix, false)) {
                    if (":".equals(prefix)) {
                        parseWhitespaceIf();
                    }
                    Name identifier = parseIdentifier(false, true);
                    paramName = identifier.last();
                    if (PropertiesBeanDefinitionReader.CONSTRUCTOR_ARG_PREFIX.equals(prefix) && paramName.endsWith(PropertiesBeanDefinitionReader.CONSTRUCTOR_ARG_PREFIX)) {
                        position(p);
                        return null;
                    }
                } else {
                    return null;
                }
                break;
        }
        Object binding = nextBinding();
        if (binding instanceof Field) {
            Field<?> f = (Field) binding;
            return f;
        }
        Param<?> param = DSL.param(paramName, binding);
        if (this.bindParamListener != null) {
            this.bindParams.put(paramName, param);
        }
        return param;
    }

    private final Comment parseComment() {
        return DSL.comment(parseStringLiteral());
    }

    private final String parseStringLiteral(String literal) {
        String value = parseStringLiteral();
        if (!literal.equals(value)) {
            throw expected("String literal: '" + literal + "'");
        }
        return value;
    }

    @Override // org.jooq.ParseContext
    public final String parseStringLiteral() {
        String result = parseStringLiteralIf();
        if (result == null) {
            throw expected("String literal");
        }
        return result;
    }

    @Override // org.jooq.ParseContext
    public final String parseStringLiteralIf() {
        if (parseIf('q', '\'', false) || parseIf('Q', '\'', false)) {
            return parseOracleQuotedStringLiteral();
        }
        if (parseIf('e', '\'', false) || parseIf('E', '\'', false)) {
            return parseUnquotedStringLiteral(true, '\'');
        }
        if (peek('\'')) {
            return parseUnquotedStringLiteral(false, '\'');
        }
        if (parseIf('n', '\'', false) || parseIf('N', '\'', false)) {
            return parseUnquotedStringLiteral(true, '\'');
        }
        if (peek('$')) {
            return parseDollarQuotedStringLiteralIf();
        }
        return null;
    }

    private final Boolean parseBitLiteralIf() {
        if (parseIf("B'", false) || parseIf("b'", false)) {
            boolean result = !parseIf('0') && parseIf('1');
            if (parseIf('0') || parseIf('1')) {
                throw exception("Currently, only BIT(1) literals are supported");
            }
            parse('\'');
            return Boolean.valueOf(result);
        }
        return null;
    }

    /* JADX WARN: Code restructure failed: missing block: B:27:0x0090, code lost:            if (r6 != '\'') goto L33;     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x0093, code lost:            positionInc();        parseWhitespaceIf();     */
    /* JADX WARN: Code restructure failed: missing block: B:29:0x00a1, code lost:            return r0.toByteArray();     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x00a9, code lost:            throw exception("Binary literal not terminated");     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private final byte[] parseBinaryLiteralIf() {
        /*
            r4 = this;
            r0 = r4
            java.lang.String r1 = "X'"
            r2 = 0
            boolean r0 = r0.parseIf(r1, r2)
            if (r0 != 0) goto L16
            r0 = r4
            java.lang.String r1 = "x'"
            r2 = 0
            boolean r0 = r0.parseIf(r1, r2)
            if (r0 == 0) goto Laa
        L16:
            r0 = r4
            r1 = 39
            boolean r0 = r0.parseIf(r1)
            if (r0 == 0) goto L23
            byte[] r0 = org.jooq.impl.Tools.EMPTY_BYTE
            return r0
        L23:
            java.io.ByteArrayOutputStream r0 = new java.io.ByteArrayOutputStream
            r1 = r0
            r1.<init>()
            r5 = r0
            r0 = 0
            r6 = r0
        L2d:
            r0 = r4
            boolean r0 = r0.hasMore()
            if (r0 == 0) goto L47
            r0 = r4
            char r0 = r0.character()
            r6 = r0
            r0 = r6
            r1 = 32
            if (r0 != r1) goto L47
            r0 = r4
            boolean r0 = r0.positionInc()
            goto L2d
        L47:
            r0 = r4
            char r0 = r0.characterNext()
            r7 = r0
            r0 = r6
            r1 = 39
            if (r0 != r1) goto L55
            goto L8d
        L55:
            r0 = r7
            r1 = 39
            if (r0 != r1) goto L63
            r0 = r4
            java.lang.String r1 = "Unexpected token: \"'\""
            org.jooq.impl.ParserException r0 = r0.exception(r1)
            throw r0
        L63:
            r0 = r5
            r1 = r6
            r2 = r7
            java.lang.String r1 = r1 + r2     // Catch: java.lang.NumberFormatException -> L76
            r2 = 16
            int r1 = java.lang.Integer.parseInt(r1, r2)     // Catch: java.lang.NumberFormatException -> L76
            r0.write(r1)     // Catch: java.lang.NumberFormatException -> L76
            goto L80
        L76:
            r8 = move-exception
            r0 = r4
            java.lang.String r1 = "Illegal character for binary literal"
            org.jooq.impl.ParserException r0 = r0.exception(r1)
            throw r0
        L80:
            r0 = r4
            r1 = 2
            boolean r0 = r0.positionInc(r1)
            r0 = r4
            boolean r0 = r0.hasMore()
            if (r0 != 0) goto L2d
        L8d:
            r0 = r6
            r1 = 39
            if (r0 != r1) goto La2
            r0 = r4
            boolean r0 = r0.positionInc()
            r0 = r4
            boolean r0 = r0.parseWhitespaceIf()
            r0 = r5
            byte[] r0 = r0.toByteArray()
            return r0
        La2:
            r0 = r4
            java.lang.String r1 = "Binary literal not terminated"
            org.jooq.impl.ParserException r0 = r0.exception(r1)
            throw r0
        Laa:
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jooq.impl.DefaultParseContext.parseBinaryLiteralIf():byte[]");
    }

    private final String parseOracleQuotedStringLiteral() {
        char end;
        parse('\'', false);
        char start = character();
        switch (start) {
            case '\t':
            case '\n':
            case '\r':
            case ' ':
                throw exception("Illegal quote string character");
            case '(':
                end = ')';
                positionInc();
                break;
            case '<':
                end = '>';
                positionInc();
                break;
            case '[':
                end = ']';
                positionInc();
                break;
            case '{':
                end = '}';
                positionInc();
                break;
            default:
                end = start;
                positionInc();
                break;
        }
        StringBuilder sb = new StringBuilder();
        int i = position();
        while (i < this.sql.length) {
            char c = character(i);
            if (c == end) {
                if (character(i + 1) == '\'') {
                    position(i + 2);
                    parseWhitespaceIf();
                    return sb.toString();
                }
                i++;
            }
            sb.append(c);
            i++;
        }
        throw exception("Quoted string literal not terminated");
    }

    private final String parseDollarQuotedStringLiteralIf() {
        int previous = position();
        if (!peek('$')) {
            return null;
        }
        parse('$');
        int openTokenEnd = previous;
        int closeTokenStart = -1;
        int closeTokenEnd = -1;
        for (int i = position(); i < this.sql.length; i++) {
            char c = character(i);
            if (!Character.isJavaIdentifierPart(c)) {
                return null;
            }
            openTokenEnd++;
            if (c == '$') {
                break;
            }
        }
        position(openTokenEnd + 1);
        for (int i2 = position(); i2 < this.sql.length; i2++) {
            if (character(i2) == '$') {
                if (closeTokenStart != -1) {
                    int i3 = i2;
                    closeTokenEnd = i3;
                    if (openTokenEnd - previous == i3 - closeTokenStart) {
                        break;
                    }
                    closeTokenStart = closeTokenEnd;
                } else {
                    closeTokenStart = i2;
                }
            } else if (closeTokenStart > -1 && character(i2) != character(i2 - (closeTokenStart - previous))) {
                closeTokenStart = -1;
            }
        }
        if (closeTokenEnd != -1) {
            position(closeTokenEnd + 1);
            return substring(openTokenEnd + 1, closeTokenStart);
        }
        position(previous);
        return null;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Failed to find 'out' block for switch in B:5:0x0029. Please report as an issue. */
    /* JADX WARN: Failed to find 'out' block for switch in B:9:0x0058. Please report as an issue. */
    private final String parseUnquotedStringLiteral(boolean postgresEscaping, char delim) {
        parse(delim, false);
        StringBuilder sb = new StringBuilder();
        int i = position();
        while (i < this.sql.length) {
            char c1 = character(i);
            switch (c1) {
                case '\'':
                    if (character(i + 1) != delim) {
                        position(i + 1);
                        parseWhitespaceIf();
                        return sb.toString();
                    }
                    i++;
                    sb.append(c1);
                    i++;
                case '\\':
                    if (postgresEscaping) {
                        i++;
                        char c2 = character(i);
                        switch (c2) {
                            case Opcodes.CASTORE /* 85 */:
                                sb.appendCodePoint(Integer.parseInt(new String(this.sql, i + 1, 8), 16));
                                i += 8;
                                i++;
                            case Opcodes.FADD /* 98 */:
                                c1 = '\b';
                                break;
                            case Opcodes.FSUB /* 102 */:
                                c1 = '\f';
                                break;
                            case Opcodes.FDIV /* 110 */:
                                c1 = '\n';
                                break;
                            case Opcodes.FREM /* 114 */:
                                c1 = '\r';
                                break;
                            case 't':
                                c1 = '\t';
                                break;
                            case Opcodes.LNEG /* 117 */:
                                c1 = (char) Integer.parseInt(new String(this.sql, i + 1, 4), 16);
                                i += 4;
                                break;
                            case 'x':
                                char c3 = character(i + 1);
                                char c4 = character(i + 2);
                                int d3 = Character.digit(c3, 16);
                                if (d3 != -1) {
                                    i++;
                                    int d4 = Character.digit(c4, 16);
                                    if (d4 != -1) {
                                        c1 = (char) ((16 * d3) + d4);
                                        i++;
                                        break;
                                    } else {
                                        c1 = (char) d3;
                                        break;
                                    }
                                } else {
                                    throw exception("Illegal hexadecimal byte value");
                                }
                            default:
                                if (Character.digit(c2, 8) != -1) {
                                    char c32 = character(i + 1);
                                    if (Character.digit(c32, 8) != -1) {
                                        i++;
                                        char c42 = character(i + 1);
                                        if (Character.digit(c42, 8) != -1) {
                                            i++;
                                            c1 = (char) Integer.parseInt(c2 + c32 + c42, 8);
                                            break;
                                        } else {
                                            c1 = (char) Integer.parseInt(c2 + c32, 8);
                                            break;
                                        }
                                    } else {
                                        c1 = (char) Integer.parseInt(c2, 8);
                                        break;
                                    }
                                } else {
                                    c1 = c2;
                                    break;
                                }
                        }
                    }
                    sb.append(c1);
                    i++;
                default:
                    sb.append(c1);
                    i++;
            }
        }
        throw exception("String literal not terminated");
    }

    private final Field<Number> parseFieldUnsignedNumericLiteral(Sign sign) {
        Field<Number> result = parseFieldUnsignedNumericLiteralIf(sign);
        if (result == null) {
            throw expected("Unsigned numeric literal");
        }
        return result;
    }

    private final Field<Number> parseFieldUnsignedNumericLiteralIf(Sign sign) {
        Number r = parseUnsignedNumericLiteralIf(sign);
        if (r == null) {
            return null;
        }
        return DSL.inline(r);
    }

    private final Number parseUnsignedNumericLiteralIf(Sign sign) {
        int p = position();
        parseDigits();
        boolean decimal = false | parseIf('.', false);
        if (decimal) {
            parseDigits();
        }
        if (p == position()) {
            return null;
        }
        if (parseIf('e', false) || parseIf('E', false)) {
            parseIf('-', false);
            parseDigits();
            String s = substring(p, position());
            parseWhitespaceIf();
            return Double.valueOf(sign == Sign.MINUS ? -Double.parseDouble(s) : Double.parseDouble(s));
        }
        String s2 = substring(p, position());
        parseWhitespaceIf();
        if (decimal) {
            return sign == Sign.MINUS ? new BigDecimal(s2).negate() : new BigDecimal(s2);
        }
        try {
            return Long.valueOf(sign == Sign.MINUS ? -Long.valueOf(s2).longValue() : Long.valueOf(s2).longValue());
        } catch (Exception e) {
            return sign == Sign.MINUS ? new BigInteger(s2).negate() : new BigInteger(s2);
        }
    }

    private final void parseDigits() {
        while (true) {
            char c = character();
            if (c >= '0' && c <= '9') {
                positionInc();
            } else {
                return;
            }
        }
    }

    private final Field<Integer> parseZeroOne() {
        if (parseIf('0')) {
            return DSL.zero();
        }
        if (parseIf('1')) {
            return DSL.one();
        }
        throw expected("0 or 1");
    }

    private final Field<Integer> parseZeroOneDefault() {
        if (parseIf('0')) {
            return DSL.zero();
        }
        if (parseIf('1')) {
            return DSL.one();
        }
        if (parseKeywordIf("DEFAULT")) {
            return DSL.defaultValue(SQLDataType.INTEGER);
        }
        throw expected("0 or 1");
    }

    @Override // org.jooq.ParseContext
    public final Long parseSignedIntegerLiteral() {
        Long result = parseSignedIntegerLiteralIf();
        if (result == null) {
            throw expected("Signed integer");
        }
        return result;
    }

    @Override // org.jooq.ParseContext
    public final Long parseSignedIntegerLiteralIf() {
        Long unsigned;
        long longValue;
        Sign sign = parseSign();
        if (sign == Sign.MINUS) {
            unsigned = parseUnsignedIntegerLiteral();
        } else {
            unsigned = parseUnsignedIntegerLiteralIf();
        }
        if (unsigned == null) {
            return null;
        }
        if (sign == Sign.MINUS) {
            longValue = -unsigned.longValue();
        } else {
            longValue = unsigned.longValue();
        }
        return Long.valueOf(longValue);
    }

    private final <T> List<T> parseList(char separator, java.util.function.Function<? super ParseContext, ? extends T> element) {
        return parseList(c -> {
            return c.parseIf(separator);
        }, element);
    }

    @Override // org.jooq.ParseContext
    public final <T> List<T> parseList(String separator, java.util.function.Function<? super ParseContext, ? extends T> element) {
        return parseList(c -> {
            return c.parseIf(separator);
        }, element);
    }

    @Override // org.jooq.ParseContext
    public final <T> List<T> parseList(Predicate<? super ParseContext> separator, java.util.function.Function<? super ParseContext, ? extends T> element) {
        List<T> result = new ArrayList<>();
        do {
            result.add(element.apply(this));
        } while (separator.test(this));
        return result;
    }

    @Override // org.jooq.ParseContext
    public final <T> T parseParenthesised(java.util.function.Function<? super ParseContext, ? extends T> function) {
        return (T) parseParenthesised('(', (java.util.function.Function) function, ')');
    }

    @Override // org.jooq.ParseContext
    public final <T> T parseParenthesised(char open, java.util.function.Function<? super ParseContext, ? extends T> content, char close) {
        parse(open);
        T result = content.apply(this);
        parse(close);
        return result;
    }

    @Override // org.jooq.ParseContext
    public final <T> T parseParenthesised(String open, java.util.function.Function<? super ParseContext, ? extends T> content, String close) {
        parse(open);
        T result = content.apply(this);
        parse(close);
        return result;
    }

    private final Field<Long> parseUnsignedIntegerOrBindVariable() {
        Long i = parseUnsignedIntegerLiteralIf();
        if (i != null) {
            return DSL.inline(i);
        }
        Field parseBindVariableIf = parseBindVariableIf();
        if (parseBindVariableIf != null) {
            return parseBindVariableIf;
        }
        throw expected("Unsigned integer or bind variable");
    }

    @Override // org.jooq.ParseContext
    public final Long parseUnsignedIntegerLiteral() {
        Long result = parseUnsignedIntegerLiteralIf();
        if (result == null) {
            throw expected("Unsigned integer literal");
        }
        return result;
    }

    @Override // org.jooq.ParseContext
    public final Long parseUnsignedIntegerLiteralIf() {
        int p = position();
        parseDigits();
        if (p == position()) {
            return null;
        }
        String s = substring(p, position());
        parseWhitespaceIf();
        return Long.valueOf(s);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: ParserImpl.java */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultParseContext$Join.class */
    public static final class Join extends Record {
        private final JoinType type;
        private final QOM.JoinHint hint;

        private Join(JoinType type, QOM.JoinHint hint) {
            this.type = type;
            this.hint = hint;
        }

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, Join.class), Join.class, "type;hint", "FIELD:Lorg/jooq/impl/DefaultParseContext$Join;->type:Lorg/jooq/JoinType;", "FIELD:Lorg/jooq/impl/DefaultParseContext$Join;->hint:Lorg/jooq/impl/QOM$JoinHint;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, Join.class), Join.class, "type;hint", "FIELD:Lorg/jooq/impl/DefaultParseContext$Join;->type:Lorg/jooq/JoinType;", "FIELD:Lorg/jooq/impl/DefaultParseContext$Join;->hint:Lorg/jooq/impl/QOM$JoinHint;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, Join.class, Object.class), Join.class, "type;hint", "FIELD:Lorg/jooq/impl/DefaultParseContext$Join;->type:Lorg/jooq/JoinType;", "FIELD:Lorg/jooq/impl/DefaultParseContext$Join;->hint:Lorg/jooq/impl/QOM$JoinHint;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public JoinType type() {
            return this.type;
        }

        public QOM.JoinHint hint() {
            return this.hint;
        }
    }

    private final Join parseJoinTypeIf() {
        if (parseKeywordIf("CROSS")) {
            if (parseKeywordIf("JOIN")) {
                return new Join(JoinType.CROSS_JOIN, null);
            }
            if (parseKeywordIf("APPLY")) {
                return new Join(JoinType.CROSS_APPLY, null);
            }
            return null;
        }
        if (parseKeywordIf("INNER")) {
            QOM.JoinHint hint = parseJoinHintIf();
            if (asTrue(hint) && parseKeyword("JOIN")) {
                return new Join(JoinType.JOIN, hint);
            }
        }
        if (parseKeywordIf("JOIN")) {
            QOM.JoinHint hint2 = parseJoinHintIf();
            if (asTrue(hint2)) {
                return new Join(JoinType.JOIN, hint2);
            }
        }
        if (parseKeywordIf("LEFT")) {
            if (parseKeywordIf("SEMI") && parseKeyword("JOIN")) {
                return new Join(JoinType.LEFT_SEMI_JOIN, null);
            }
            if (parseKeywordIf("ANTI") && parseKeyword("JOIN")) {
                return new Join(JoinType.LEFT_ANTI_JOIN, null);
            }
            if (!parseKeywordIf("OUTER")) {
            }
            QOM.JoinHint hint3 = parseJoinHintIf();
            if (asTrue(hint3) && parseKeyword("JOIN")) {
                return new Join(JoinType.LEFT_OUTER_JOIN, hint3);
            }
            return null;
        }
        if (parseKeywordIf("RIGHT")) {
            if (!parseKeywordIf("OUTER")) {
            }
            QOM.JoinHint hint4 = parseJoinHintIf();
            if (asTrue(hint4) && parseKeyword("JOIN")) {
                return new Join(JoinType.RIGHT_OUTER_JOIN, hint4);
            }
        }
        if (parseKeywordIf("FULL")) {
            if (!parseKeywordIf("OUTER")) {
            }
            QOM.JoinHint hint5 = parseJoinHintIf();
            if (asTrue(hint5) && parseKeyword("JOIN")) {
                return new Join(JoinType.FULL_OUTER_JOIN, hint5);
            }
        }
        if (parseKeywordIf("OUTER APPLY")) {
            return new Join(JoinType.OUTER_APPLY, null);
        }
        if (parseKeywordIf("NATURAL")) {
            if (parseKeywordIf("LEFT")) {
                if (!parseKeywordIf("OUTER")) {
                }
                if (parseKeyword("JOIN")) {
                    return new Join(JoinType.NATURAL_LEFT_OUTER_JOIN, null);
                }
            }
            if (parseKeywordIf("RIGHT")) {
                if (!parseKeywordIf("OUTER")) {
                }
                if (parseKeyword("JOIN")) {
                    return new Join(JoinType.NATURAL_RIGHT_OUTER_JOIN, null);
                }
            }
            if (parseKeywordIf("FULL")) {
                if (!parseKeywordIf("OUTER")) {
                }
                if (parseKeyword("JOIN")) {
                    return new Join(JoinType.NATURAL_FULL_OUTER_JOIN, null);
                }
            }
            if (!parseKeywordIf("INNER")) {
            }
            if (parseKeyword("JOIN")) {
                return new Join(JoinType.NATURAL_JOIN, null);
            }
            return null;
        }
        if (parseKeywordIf("STRAIGHT_JOIN")) {
            return new Join(JoinType.STRAIGHT_JOIN, null);
        }
        return null;
    }

    private final QOM.JoinHint parseJoinHintIf() {
        if (parseKeywordIf("HASH")) {
            return QOM.JoinHint.HASH;
        }
        if (parseKeywordIf("LOOP", "LOOKUP")) {
            return QOM.JoinHint.LOOP;
        }
        if (parseKeywordIf("MERGE")) {
            return QOM.JoinHint.MERGE;
        }
        return null;
    }

    private final TruthValue parseTruthValueIf() {
        if (parseKeywordIf("TRUE")) {
            return TruthValue.T_TRUE;
        }
        if (parseKeywordIf("FALSE")) {
            return TruthValue.T_FALSE;
        }
        if (parseKeywordIf(JoranConstants.NULL)) {
            return TruthValue.T_NULL;
        }
        return null;
    }

    private final CombineOperator parseCombineOperatorIf(boolean intersectOnly) {
        if (!intersectOnly && parseKeywordIf("UNION")) {
            if (parseKeywordIf("ALL")) {
                return CombineOperator.UNION_ALL;
            }
            if (parseKeywordIf("DISTINCT")) {
                return CombineOperator.UNION;
            }
            return CombineOperator.UNION;
        }
        if (!intersectOnly && parseKeywordIf("EXCEPT", "MINUS")) {
            if (parseKeywordIf("ALL")) {
                return CombineOperator.EXCEPT_ALL;
            }
            if (parseKeywordIf("DISTINCT")) {
                return CombineOperator.EXCEPT;
            }
            return CombineOperator.EXCEPT;
        }
        if (intersectOnly && parseKeywordIf("INTERSECT")) {
            if (parseKeywordIf("ALL")) {
                return CombineOperator.INTERSECT_ALL;
            }
            if (parseKeywordIf("DISTINCT")) {
                return CombineOperator.INTERSECT;
            }
            return CombineOperator.INTERSECT;
        }
        return null;
    }

    private final ComputationalOperation parseComputationalOperationIf() {
        switch (characterUpper()) {
            case 'A':
                if (parseFunctionNameIf("ANY")) {
                    return ComputationalOperation.ANY;
                }
                if (parseFunctionNameIf("ANY_VALUE", "ARBITRARY")) {
                    return ComputationalOperation.ANY_VALUE;
                }
                if (parseFunctionNameIf("AVG")) {
                    return ComputationalOperation.AVG;
                }
                return null;
            case 'B':
                if (parseFunctionNameIf("BOOL_AND", "BOOLAND_AGG")) {
                    return ComputationalOperation.EVERY;
                }
                if (parseFunctionNameIf("BOOL_OR", "BOOLOR_AGG")) {
                    return ComputationalOperation.ANY;
                }
                return null;
            case 'C':
            case 'D':
            case 'F':
            case TypeReference.CAST /* 71 */:
            case 'H':
            case 'I':
            case 'J':
            case TypeReference.METHOD_REFERENCE_TYPE_ARGUMENT /* 75 */:
            case 'N':
            case Opcodes.IASTORE /* 79 */:
            case Opcodes.FASTORE /* 81 */:
            case Opcodes.DASTORE /* 82 */:
            case Opcodes.BASTORE /* 84 */:
            case Opcodes.CASTORE /* 85 */:
            default:
                return null;
            case TypeReference.CONSTRUCTOR_REFERENCE /* 69 */:
                if (parseFunctionNameIf("EVERY")) {
                    return ComputationalOperation.EVERY;
                }
                return null;
            case BaseNCodec.MIME_CHUNK_SIZE /* 76 */:
                if (parseFunctionNameIf("LOGICAL_AND")) {
                    return ComputationalOperation.EVERY;
                }
                if (parseFunctionNameIf("LOGICAL_OR")) {
                    return ComputationalOperation.ANY;
                }
                return null;
            case 'M':
                if (parseFunctionNameIf("MAX")) {
                    return ComputationalOperation.MAX;
                }
                if (parseFunctionNameIf("MEDIAN")) {
                    return ComputationalOperation.MEDIAN;
                }
                if (parseFunctionNameIf("MIN")) {
                    return ComputationalOperation.MIN;
                }
                if (parseFunctionNameIf("MUL")) {
                    return ComputationalOperation.PRODUCT;
                }
                return null;
            case 'P':
                if (parseFunctionNameIf("PRODUCT")) {
                    return ComputationalOperation.PRODUCT;
                }
                return null;
            case 'S':
                if (parseFunctionNameIf("SUM")) {
                    return ComputationalOperation.SUM;
                }
                if (parseFunctionNameIf("SOME")) {
                    return ComputationalOperation.ANY;
                }
                if (parseFunctionNameIf("STDDEV", "STDEVP", "STDDEV_POP")) {
                    return ComputationalOperation.STDDEV_POP;
                }
                if (parseFunctionNameIf("STDDEV_SAMP", "STDEV", "STDEV_SAMP")) {
                    return ComputationalOperation.STDDEV_SAMP;
                }
                return null;
            case Opcodes.SASTORE /* 86 */:
                if (parseFunctionNameIf("VAR_POP", "VARIANCE", "VARP")) {
                    return ComputationalOperation.VAR_POP;
                }
                if (parseFunctionNameIf("VAR_SAMP", "VARIANCE_SAMP", "VAR")) {
                    return ComputationalOperation.VAR_SAMP;
                }
                return null;
        }
    }

    private final Comparator parseComparatorIf() {
        if (parseIf("=") || parseKeywordIf("EQ")) {
            return Comparator.EQUALS;
        }
        if (parseIf("!=") || parseIf("<>") || parseIf("^=") || parseKeywordIf("NE")) {
            return Comparator.NOT_EQUALS;
        }
        if (parseIf(">=") || parseKeywordIf("GE")) {
            return Comparator.GREATER_OR_EQUAL;
        }
        if (parseIf(">") || parseKeywordIf("GT")) {
            return Comparator.GREATER;
        }
        if (parseIf("<=>")) {
            return Comparator.IS_NOT_DISTINCT_FROM;
        }
        if (parseIf("<=") || parseKeywordIf("LE")) {
            return Comparator.LESS_OR_EQUAL;
        }
        if (parseIf("<") || parseKeywordIf("LT")) {
            return Comparator.LESS;
        }
        return null;
    }

    private final TSQLOuterJoinComparator parseTSQLOuterJoinComparatorIf() {
        if (parseIf("*=")) {
            return TSQLOuterJoinComparator.LEFT;
        }
        if (parseIf("=*")) {
            return TSQLOuterJoinComparator.RIGHT;
        }
        return null;
    }

    private final String parseUntilEOL() {
        String result = parseUntilEOLIf();
        if (result == null) {
            throw expected("Content before EOL");
        }
        return result;
    }

    private final String parseUntilEOLIf() {
        int start = position();
        int stop = start;
        while (true) {
            if (stop >= this.sql.length) {
                break;
            }
            char c = character(stop);
            if (c == '\r') {
                if (character(stop + 1) == '\n') {
                    stop++;
                }
            } else {
                if (c == '\n') {
                    break;
                }
                stop++;
            }
        }
        if (start == stop) {
            return null;
        }
        position(stop);
        parseWhitespaceIf();
        return substring(start, stop);
    }

    private final boolean parseTokens(char... tokens) {
        boolean result = parseTokensIf(tokens);
        if (!result) {
            throw expected(new String(tokens));
        }
        return result;
    }

    private final boolean parseTokensIf(char... tokens) {
        int p = position();
        for (char token : tokens) {
            if (!parseIf(token)) {
                position(p);
                return false;
            }
        }
        return true;
    }

    private final boolean peekTokens(char... tokens) {
        int p = position();
        for (char token : tokens) {
            if (!parseIf(token)) {
                position(p);
                return false;
            }
        }
        position(p);
        return true;
    }

    @Override // org.jooq.ParseContext
    public final boolean parse(String string) {
        boolean result = parseIf(string);
        if (!result) {
            throw expected(string);
        }
        return result;
    }

    @Override // org.jooq.ParseContext
    public final boolean parseIf(String string) {
        return parseIf(string, true);
    }

    private final boolean parseIf(String string, boolean skipAfterWhitespace) {
        boolean result = peek(string);
        if (result) {
            positionInc(string.length());
            if (skipAfterWhitespace) {
                parseWhitespaceIf();
            }
        }
        return result;
    }

    @Override // org.jooq.ParseContext
    public final boolean parse(char c) {
        return parse(c, true);
    }

    private final boolean parse(char c, boolean skipAfterWhitespace) {
        if (!parseIf(c, skipAfterWhitespace)) {
            throw expected("Token '" + c + "'");
        }
        return true;
    }

    @Override // org.jooq.ParseContext
    public final boolean parseIf(char c) {
        return parseIf(c, true);
    }

    private final boolean parseIf(char c, boolean skipAfterWhitespace) {
        boolean result = peek(c);
        if (result) {
            positionInc();
            if (skipAfterWhitespace) {
                parseWhitespaceIf();
            }
        }
        return result;
    }

    private final boolean parseIf(char c, char peek, boolean skipAfterWhitespace) {
        if (character() != c || characterNext() != peek) {
            return false;
        }
        positionInc();
        if (skipAfterWhitespace) {
            parseWhitespaceIf();
            return true;
        }
        return true;
    }

    private final boolean peekFunctionNameIf(String name) {
        return peekKeyword(name, false, false, true);
    }

    @Override // org.jooq.ParseContext
    public final boolean parseFunctionNameIf(String name) {
        return peekKeyword(name, true, false, true);
    }

    private final boolean parseFunctionNameIf(String name1, String name2) {
        return parseFunctionNameIf(name1) || parseFunctionNameIf(name2);
    }

    private final boolean parseFunctionNameIf(String name1, String name2, String name3) {
        return parseFunctionNameIf(name1) || parseFunctionNameIf(name2) || parseFunctionNameIf(name3);
    }

    @Override // org.jooq.ParseContext
    public final boolean parseFunctionNameIf(String... names) {
        return Tools.anyMatch(names, n -> {
            return parseFunctionNameIf(n);
        });
    }

    private final boolean parseOperator(String operator) {
        if (!parseOperatorIf(operator)) {
            throw expected("Operator '" + operator + "'");
        }
        return true;
    }

    private final boolean parseOperatorIf(String operator) {
        return peekOperator(operator, true);
    }

    private final boolean peekOperator(String operator) {
        return peekOperator(operator, false);
    }

    private final boolean peekOperator(String operator, boolean updatePosition) {
        int length = operator.length();
        int p = position();
        if (this.sql.length < p + length) {
            return false;
        }
        int pos = afterWhitespace(p, false);
        int i = 0;
        while (i < length) {
            if (this.sql[pos] == operator.charAt(i)) {
                i++;
                pos++;
            } else {
                return false;
            }
        }
        if (isOperatorPart(pos)) {
            return false;
        }
        if (updatePosition) {
            position(pos);
            parseWhitespaceIf();
            return true;
        }
        return true;
    }

    @Override // org.jooq.ParseContext
    public final boolean parseKeyword(String keyword) {
        if (!parseKeywordIf(keyword)) {
            throw expected("Keyword '" + keyword + "'");
        }
        return true;
    }

    private final boolean parseKeyword(String keyword1, String keyword2) {
        if (parseKeywordIf(keyword1, keyword2)) {
            return true;
        }
        throw expected(keyword1, keyword2);
    }

    private final boolean parseKeyword(String keyword1, String keyword2, String keyword3) {
        if (parseKeywordIf(keyword1, keyword2, keyword3)) {
            return true;
        }
        throw expected(keyword1, keyword2, keyword3);
    }

    private final boolean parseKeywordUndocumentedAlternatives(String keyword, String undocumented1) {
        if (parseKeywordIf(undocumented1)) {
            return true;
        }
        return parseKeyword(keyword);
    }

    @Override // org.jooq.ParseContext
    public final boolean parseKeywordIf(String keyword) {
        return peekKeyword(keyword, true, false, false);
    }

    private final boolean parseKeywordIf(String keyword1, String keyword2) {
        return parseKeywordIf(keyword1) || parseKeywordIf(keyword2);
    }

    private final boolean parseKeywordIf(String keyword1, String keyword2, String keyword3) {
        return parseKeywordIf(keyword1) || parseKeywordIf(keyword2) || parseKeywordIf(keyword3);
    }

    private final boolean parseKeywordIf(String keyword1, String keyword2, String keyword3, String keyword4) {
        return parseKeywordIf(keyword1) || parseKeywordIf(keyword2) || parseKeywordIf(keyword3) || parseKeywordIf(keyword4);
    }

    @Override // org.jooq.ParseContext
    public final boolean parseKeywordIf(String... keywords) {
        return Tools.anyMatch(keywords, k -> {
            return parseKeywordIf(k);
        });
    }

    @Override // org.jooq.ParseContext
    public final boolean parseKeyword(String... keywords) {
        if (parseKeywordIf(keywords)) {
            return true;
        }
        throw expected(keywords);
    }

    private final Keyword parseAndGetKeyword(String... keywords) {
        Keyword result = parseAndGetKeywordIf(keywords);
        if (result == null) {
            throw expected(keywords);
        }
        return result;
    }

    private final Keyword parseAndGetKeywordIf(String... keywords) {
        return (Keyword) Tools.findAny(keywords, k -> {
            return parseKeywordIf(k);
        }, k2 -> {
            return DSL.keyword(k2.toLowerCase());
        });
    }

    private final Keyword parseAndGetKeywordIf(String keyword) {
        if (parseKeywordIf(keyword)) {
            return DSL.keyword(keyword.toLowerCase());
        }
        return null;
    }

    @Override // org.jooq.ParseContext
    public final boolean peek(char c) {
        return character() == c;
    }

    @Override // org.jooq.ParseContext
    public final boolean peek(String string) {
        return peek(string, position());
    }

    private final boolean peek(String string, int p) {
        int length = string.length();
        if (this.sql.length < p + length) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            if (this.sql[p + i] != string.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    @Override // org.jooq.ParseContext
    public final boolean peekKeyword(String... keywords) {
        return Tools.anyMatch(keywords, k -> {
            return peekKeyword(k);
        });
    }

    @Override // org.jooq.ParseContext
    public final boolean peekKeyword(String keyword) {
        return peekKeyword(keyword, false, false, false);
    }

    private final boolean peekKeyword(String keyword1, String keyword2) {
        return peekKeyword(keyword1) || peekKeyword(keyword2);
    }

    private final boolean peekKeyword(String keyword1, String keyword2, String keyword3) {
        return peekKeyword(keyword1) || peekKeyword(keyword2) || peekKeyword(keyword3);
    }

    private final boolean peekKeyword(String keyword, boolean updatePosition, boolean peekIntoParens, boolean requireFunction) {
        int length = keyword.length();
        int p = position();
        if (this.sql.length < p + length) {
            return false;
        }
        int skip = afterWhitespace(p, peekIntoParens) - p;
        for (int i = 0; i < length; i++) {
            char c = keyword.charAt(i);
            int pos = p + i + skip;
            switch (c) {
                case ' ':
                    if (!Character.isWhitespace(character(pos))) {
                        return false;
                    }
                    skip += (afterWhitespace(pos) - pos) - 1;
                    break;
                default:
                    if (upper(character(pos)) != c) {
                        return false;
                    }
                    break;
            }
        }
        int pos2 = p + length + skip;
        if (isIdentifierPart(pos2) || character(pos2) == '.') {
            return false;
        }
        if (requireFunction && character(afterWhitespace(pos2)) != '(') {
            return false;
        }
        if (updatePosition) {
            positionInc(length + skip);
            parseWhitespaceIf();
            return true;
        }
        return true;
    }

    private final boolean parseWhitespaceIf() {
        this.positionBeforeWhitespace = position();
        position(afterWhitespace(this.positionBeforeWhitespace));
        return this.positionBeforeWhitespace != position();
    }

    private final int afterWhitespace(int p) {
        return afterWhitespace(p, false);
    }

    /* JADX WARN: Code restructure failed: missing block: B:52:0x0175, code lost:            r10 = 0;     */
    /* JADX WARN: Code restructure failed: missing block: B:72:0x01a8, code lost:            continue;     */
    /* JADX WARN: Failed to find 'out' block for switch in B:111:0x029e. Please report as an issue. */
    /* JADX WARN: Failed to find 'out' block for switch in B:33:0x00e3. Please report as an issue. */
    /* JADX WARN: Failed to find 'out' block for switch in B:85:0x01f3. Please report as an issue. */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private final int afterWhitespace(int r8, boolean r9) {
        /*
            Method dump skipped, instructions count: 742
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jooq.impl.DefaultParseContext.afterWhitespace(int, boolean):int");
    }

    private final boolean peekIgnoreComment(boolean ignoreComment, String ignoreCommentStart, String ignoreCommentStop, boolean checkIgnoreComment, int i) {
        if (checkIgnoreComment) {
            if (!ignoreComment) {
                ignoreComment = peek(ignoreCommentStart, i);
            } else {
                ignoreComment = !peek(ignoreCommentStop, i);
            }
        }
        return ignoreComment;
    }

    private final char upper(char c) {
        return (c < 'a' || c > 'z') ? c : (char) (c - ' ');
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* compiled from: ParserImpl.java */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultParseContext$IgnoreQuery.class */
    public static final class IgnoreQuery extends AbstractDDLQuery implements QOM.UEmpty {
        final String sql;

        IgnoreQuery() {
            this("/* ignored */");
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public IgnoreQuery(String sql) {
            super(Tools.CONFIG.get());
            this.sql = sql;
        }

        @Override // org.jooq.QueryPartInternal
        public void accept(Context<?> ctx) {
            ctx.sql(this.sql);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultParseContext(DSLContext dsl, Meta meta, ParseWithMetaLookups metaLookups, String sqlString, Object[] bindings) {
        super(dsl.configuration());
        this.position = 0;
        this.ignoreHints = true;
        this.bindIndex = 0;
        this.bindParams = new LinkedHashMap();
        this.delimiter = ";";
        this.languageContext = LanguageContext.QUERY;
        this.forbidden = EnumSet.noneOf(FunctionKeyword.class);
        this.scope = new ParseScope();
        this.dsl = dsl;
        this.locale = SettingsTools.parseLocale(dsl.settings());
        this.meta = meta;
        this.metaLookups = metaLookups;
        this.sql = sqlString != null ? sqlString.toCharArray() : new char[0];
        this.bindings = bindings;
        this.bindParamListener = (Consumer) dsl.configuration().data("org.jooq.parser.param-collector");
        parseWhitespaceIf();
    }

    @Override // org.jooq.ParseContext
    public final SQLDialect parseDialect() {
        SQLDialect result = settings().getParseDialect();
        if (result == null) {
            result = SQLDialect.DEFAULT;
        }
        return result;
    }

    @Override // org.jooq.ParseContext
    public final SQLDialect parseFamily() {
        return parseDialect().family();
    }

    @Override // org.jooq.ParseContext
    public final SQLDialectCategory parseCategory() {
        return parseDialect().category();
    }

    @Override // org.jooq.ParseContext
    public final LanguageContext languageContext() {
        return this.languageContext;
    }

    private final ParseWithMetaLookups metaLookups() {
        if (metaLookupsForceIgnore()) {
            return ParseWithMetaLookups.OFF;
        }
        return this.metaLookups;
    }

    private final boolean metaLookupsForceIgnore() {
        return this.metaLookupsForceIgnore;
    }

    private final DefaultParseContext metaLookupsForceIgnore(boolean m) {
        this.metaLookupsForceIgnore = m;
        return this;
    }

    private final boolean proEdition() {
        return configuration().commercial();
    }

    private final boolean ignoreProEdition() {
        return !proEdition() && Boolean.TRUE.equals(settings().isParseIgnoreCommercialOnlyFeatures());
    }

    private final boolean requireProEdition() {
        if (!proEdition()) {
            throw exception("Feature only supported in pro edition");
        }
        return true;
    }

    private final boolean requireUnsupportedSyntax() {
        if (this.dsl.configuration().settings().getParseUnsupportedSyntax() == ParseUnsupportedSyntax.FAIL) {
            throw exception("Syntax not supported");
        }
        return true;
    }

    private final String substring(int startPosition, int endPosition) {
        return new String(this.sql, startPosition, endPosition - startPosition);
    }

    private final ParserException internalError() {
        return exception("Internal Error");
    }

    private final ParserException expected(String object) {
        return init(new ParserException(mark(), object + " expected"));
    }

    private final ParserException expected(String... objects) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < objects.length; i++) {
            if (i == 0) {
                sb.append(objects[i]);
            } else if (i == 1 && objects.length == 2) {
                sb.append(" or ").append(objects[i]);
            } else if (i == objects.length - 1) {
                sb.append(", or ").append(objects[i]);
            } else {
                sb.append(", ").append(objects[i]);
            }
        }
        return init(new ParserException(mark(), sb.toString() + " expected"));
    }

    private final ParserException notImplemented(String feature) {
        return notImplemented(feature, "https://github.com/jOOQ/jOOQ/issues/10171");
    }

    private final ParserException notImplemented(String feature, String link) {
        return init(new ParserException(mark(), feature + " not yet implemented. If you're interested in this feature, please comment on " + link));
    }

    private final ParserException unsupportedClause() {
        return init(new ParserException(mark(), "Unsupported clause"));
    }

    @Override // org.jooq.ParseContext
    public final ParserException exception(String message) {
        return init(new ParserException(mark(), message));
    }

    private final ParserException init(ParserException e) {
        int[] line = line();
        return e.position(this.position).line(line[0]).column(line[1]);
    }

    private final Object nextBinding() {
        int i = this.bindIndex;
        this.bindIndex = i + 1;
        if (i < this.bindings.length) {
            return this.bindings[this.bindIndex - 1];
        }
        if (this.bindings.length == 0) {
            return null;
        }
        throw exception("No binding provided for bind index " + this.bindIndex);
    }

    private final int[] line() {
        int line = 1;
        int column = 1;
        int i = 0;
        while (i < this.position) {
            if (this.sql[i] == '\r') {
                line++;
                column = 1;
                if (i + 1 < this.sql.length && this.sql[i + 1] == '\n') {
                    i++;
                }
            } else if (this.sql[i] == '\n') {
                line++;
                column = 1;
            } else {
                column++;
            }
            i++;
        }
        return new int[]{line, column};
    }

    private final char characterUpper() {
        return Character.toUpperCase(character());
    }

    @Override // org.jooq.ParseContext
    public final char character() {
        return character(this.position);
    }

    @Override // org.jooq.ParseContext
    public final char character(int pos) {
        if (pos < 0 || pos >= this.sql.length) {
            return ' ';
        }
        return this.sql[pos];
    }

    private final char characterNextUpper() {
        return Character.toUpperCase(characterNext());
    }

    private final char characterNext() {
        return character(this.position + 1);
    }

    @Override // org.jooq.ParseContext
    public final char[] characters() {
        return this.sql;
    }

    @Override // org.jooq.ParseContext
    public final ParseContext characters(char[] newCharacters) {
        this.sql = newCharacters;
        return this;
    }

    @Override // org.jooq.ParseContext
    public final int position() {
        return this.position;
    }

    @Override // org.jooq.ParseContext
    public final boolean position(int newPosition) {
        this.position = newPosition;
        return true;
    }

    private final boolean positionInc() {
        return positionInc(1);
    }

    private final boolean positionInc(int inc) {
        return position(this.position + inc);
    }

    private final String delimiter() {
        return this.delimiter;
    }

    private final void delimiter(String newDelimiter) {
        this.delimiter = newDelimiter;
    }

    private final boolean ignoreHints() {
        return this.ignoreHints;
    }

    private final void ignoreHints(boolean newIgnoreHints) {
        this.ignoreHints = newIgnoreHints;
    }

    private final boolean isOperatorPart(int pos) {
        return isOperatorPart(character(pos));
    }

    private final boolean isOperatorPart(char character) {
        switch (character) {
            case '!':
            case '#':
            case '%':
            case '&':
            case '*':
            case ELParserConstants.EMPTY /* 43 */:
            case '-':
            case '/':
            case ':':
            case '<':
            case '=':
            case Opcodes.V18 /* 62 */:
            case '?':
            case '@':
            case Opcodes.DUP2_X2 /* 94 */:
            case '|':
            case '~':
                return true;
            default:
                return false;
        }
    }

    private final boolean isIdentifierPart() {
        return isIdentifierPart(character());
    }

    private final boolean isIdentifierPart(int pos) {
        return isIdentifierPart(character(pos));
    }

    private final boolean isIdentifierPart(char character) {
        return Character.isJavaIdentifierPart(character) || ((character == '@' || character == '#') && character != this.delimiter.charAt(0));
    }

    private final boolean isIdentifierStart() {
        return isIdentifierStart(character());
    }

    private final boolean isIdentifierStart(int pos) {
        return isIdentifierStart(character(pos));
    }

    private final boolean isIdentifierStart(char character) {
        return Character.isJavaIdentifierStart(character) || ((character == '@' || character == '#') && character != this.delimiter.charAt(0));
    }

    private final boolean hasMore() {
        return this.position < this.sql.length;
    }

    private final boolean hasMore(int offset) {
        return this.position + offset < this.sql.length;
    }

    private final boolean done() {
        return this.position >= this.sql.length && (this.bindings.length == 0 || this.bindings.length == this.bindIndex);
    }

    private final <Q extends QueryPart> Q done(String str, Q q) {
        if (done()) {
            return (Q) notify(q);
        }
        throw exception(str);
    }

    private final <Q extends QueryPart> Q wrap(Supplier<Q> supplier) {
        try {
            return supplier.get();
        } catch (ParserException e) {
            throw e;
        }
    }

    private final <Q extends QueryPart> Q notify(Q result) {
        if (this.bindParamListener != null) {
            Map<String, Param<?>> params = new LinkedHashMap<>();
            this.dsl.configuration().deriveAppending(VisitListener.onVisitStart(ctx -> {
                QueryPart patt0$temp = ctx.queryPart();
                if (patt0$temp instanceof Param) {
                    Param<?> p = (Param) patt0$temp;
                    if (!params.containsKey(p.getParamName())) {
                        params.put(p.getParamName(), p);
                    }
                }
            })).dsl().render(result);
            for (String name : this.bindParams.keySet()) {
                this.bindParamListener.accept(params.get(name));
            }
        }
        return result;
    }

    private final boolean asTrue(Object o) {
        return true;
    }

    private final String mark() {
        int[] line = line();
        return "[" + line[0] + ":" + line[1] + "] " + (this.position > 50 ? "..." : "") + substring(Math.max(0, this.position - 50), this.position) + "[*]" + substring(this.position, Math.min(this.sql.length, this.position + 80)) + (this.sql.length > this.position + 80 ? "..." : "");
    }

    private final <T> T newScope(Supplier<T> scoped) {
        ParseScope old = this.scope;
        try {
            this.scope = new ParseScope();
            T t = scoped.get();
            this.scope = old;
            return t;
        } catch (Throwable th) {
            this.scope = old;
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: ParserImpl.java */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultParseContext$ParseScope.class */
    public class ParseScope {
        private boolean scopeClear = false;
        private final ScopeStack<Name, Table<?>> tableScope = new ScopeStack<>();
        private final ScopeStack<Name, Field<?>> fieldScope = new ScopeStack<>();
        private final ScopeStack<Name, QualifiedAsteriskProxy> lookupQualifiedAsterisks = new ScopeStack<>();
        private final ScopeStack<Name, FieldProxy<?>> lookupFields = new ScopeStack<>();

        private ParseScope() {
        }

        private final Table<?> scope(Table<?> table) {
            this.tableScope.set(table.getQualifiedName(), table);
            return table;
        }

        private final Field<?> scope(Field<?> field) {
            this.fieldScope.set(field.getQualifiedName(), field);
            return field;
        }

        private final void scopeResolve() {
            if (!this.lookupFields.isEmpty()) {
                unknownField(this.lookupFields.iterator().next());
            }
            if (!this.lookupQualifiedAsterisks.isEmpty()) {
                unknownTable(this.lookupQualifiedAsterisks.iterator().next());
            }
        }

        private final void scopeStart() {
            this.tableScope.scopeStart();
            this.fieldScope.scopeStart();
            this.lookupFields.scopeStart();
            this.lookupFields.setAll(null);
            this.lookupQualifiedAsterisks.scopeStart();
            this.lookupQualifiedAsterisks.setAll(null);
        }

        private final void scopeEnd(Query scopeOwner) {
            List<FieldProxy<?>> fields = new ArrayList<>();
            for (QualifiedAsteriskProxy lookup : DefaultParseContext.this.scope.lookupQualifiedAsterisks.iterableAtScopeLevel()) {
                Iterator<Table<?>> it = DefaultParseContext.this.scope.tableScope.iterator();
                while (true) {
                    if (it.hasNext()) {
                        Table<?> t = it.next();
                        if (t.getName().equals(lookup.$table().getName())) {
                            lookup.delegate((QualifiedAsteriskImpl) t.asterisk());
                            break;
                        }
                    } else {
                        unknownTable(lookup);
                        break;
                    }
                }
            }
            for (FieldProxy<?> lookup2 : DefaultParseContext.this.scope.lookupFields.iterableAtScopeLevel()) {
                ScopeStack.Value<Field<?>> found = null;
                Iterator<Field<?>> it2 = DefaultParseContext.this.scope.fieldScope.iterator();
                while (it2.hasNext()) {
                    Field<?> f = it2.next();
                    if (f.getName().equals(lookup2.getName())) {
                        if (found != null) {
                            DefaultParseContext.this.position(lookup2.position());
                            throw DefaultParseContext.this.exception("Ambiguous field identifier");
                        }
                        found = new ScopeStack.Value<>(0, f);
                    }
                }
                ScopeStack.Value<Field<?>> found2 = DefaultParseContext.this.resolveInTableScope(DefaultParseContext.this.scope.tableScope.valueIterable(), lookup2.getQualifiedName(), lookup2, found);
                if (found2 != null && !(found2.value() instanceof FieldProxy)) {
                    lookup2.delegate((AbstractField) found2.value());
                } else {
                    lookup2.scopeOwner(scopeOwner);
                    fields.add(lookup2);
                }
            }
            DefaultParseContext.this.scope.lookupQualifiedAsterisks.scopeEnd();
            DefaultParseContext.this.scope.lookupFields.scopeEnd();
            DefaultParseContext.this.scope.tableScope.scopeEnd();
            DefaultParseContext.this.scope.fieldScope.scopeEnd();
            for (FieldProxy<?> r : fields) {
                if (DefaultParseContext.this.scope.lookupFields.get(r.getQualifiedName()) == null) {
                    if (DefaultParseContext.this.scope.lookupFields.inScope()) {
                        DefaultParseContext.this.scope.lookupFields.set(r.getQualifiedName(), r);
                    } else {
                        unknownField(r);
                    }
                }
            }
        }

        private final void scopeClear() {
            this.scopeClear = true;
        }

        private final void unknownField(FieldProxy<?> field) {
            if (!this.scopeClear && DefaultParseContext.this.metaLookups() == ParseWithMetaLookups.THROW_ON_FAILURE) {
                DefaultParseContext.this.position(field.position());
                throw DefaultParseContext.this.exception("Unknown field identifier");
            }
        }

        private final void unknownTable(QualifiedAsteriskProxy asterisk) {
            if (!this.scopeClear && DefaultParseContext.this.metaLookups() == ParseWithMetaLookups.THROW_ON_FAILURE) {
                DefaultParseContext.this.position(asterisk.position());
                throw DefaultParseContext.this.exception("Unknown table identifier");
            }
        }
    }

    private final ScopeStack.Value<Field<?>> resolveInTableScope(Iterable<ScopeStack.Value<Table<?>>> tables, Name lookupName, FieldProxy<?> lookup, ScopeStack.Value<Field<?>> found) {
        for (ScopeStack.Value<Table<?>> t : tables) {
            Table<?> value = t.value();
            if (value instanceof JoinTable) {
                JoinTable j = (JoinTable) value;
                found = resolveInTableScope(Arrays.asList(new ScopeStack.Value(t.scopeLevel(), j.lhs), new ScopeStack.Value(t.scopeLevel(), j.rhs)), lookupName, lookup, found);
            } else if (lookupName.qualified()) {
                Name q = lookupName.qualifier();
                boolean x = q.qualified();
                if ((x && q.equals(t.value().getQualifiedName())) || (!x && q.last().equals(t.value().getName()))) {
                    ScopeStack.Value<Field<?>> of = ScopeStack.Value.of(t.scopeLevel(), t.value().field(lookup.getName()));
                    found = of;
                    if (of != null) {
                        break;
                    }
                }
            } else {
                ScopeStack.Value<Field<?>> f = ScopeStack.Value.of(t.scopeLevel(), t.value().field(lookup.getName()));
                if (f == null) {
                    continue;
                } else if (found == null || found.scopeLevel() < f.scopeLevel()) {
                    found = f;
                } else {
                    position(lookup.position());
                    throw exception("Ambiguous field identifier");
                }
            }
        }
        return found;
    }

    private final Table<?> lookupTable(int positionBeforeName, Name name) {
        if (this.meta != null) {
            List<Table<?>> tables = this.meta.getTables(name);
            if (!tables.isEmpty()) {
                for (Table<?> table : tables) {
                    if (table.getQualifiedName().qualified() == name.qualified()) {
                        return tables.get(0);
                    }
                }
            }
            if (!name.qualified()) {
                for (ParseSearchSchema schema : settings().getParseSearchPath()) {
                    List<Table<?>> tables2 = this.meta.getTables(DSL.name(schema.getCatalog(), schema.getSchema()).append(name));
                    if (tables2.size() == 1) {
                        return tables2.get(0);
                    }
                }
            }
        }
        if (metaLookups() == ParseWithMetaLookups.THROW_ON_FAILURE) {
            position(positionBeforeName);
            throw exception("Unknown table identifier");
        }
        return DSL.table(name);
    }

    private final QualifiedAsterisk lookupQualifiedAsterisk(int positionBeforeName, Name name) {
        if (metaLookups() == ParseWithMetaLookups.OFF || this.scope.lookupQualifiedAsterisks.scopeLevel() < 0) {
            return DSL.table(name).asterisk();
        }
        QualifiedAsteriskProxy asterisk = this.scope.lookupQualifiedAsterisks.get(name);
        if (asterisk == null) {
            ScopeStack<Name, QualifiedAsteriskProxy> scopeStack = this.scope.lookupQualifiedAsterisks;
            QualifiedAsteriskProxy qualifiedAsteriskProxy = new QualifiedAsteriskProxy((QualifiedAsteriskImpl) DSL.table(name).asterisk(), positionBeforeName);
            asterisk = qualifiedAsteriskProxy;
            scopeStack.set(name, qualifiedAsteriskProxy);
        }
        return asterisk;
    }

    private final Field<?> lookupField(int positionBeforeName, Name name) {
        if (metaLookups() == ParseWithMetaLookups.OFF || this.scope.lookupFields.scopeLevel() < 0) {
            return DSL.field(name);
        }
        FieldProxy<?> field = this.scope.lookupFields.get(name);
        if (field == null) {
            ScopeStack<Name, FieldProxy<?>> scopeStack = this.scope.lookupFields;
            FieldProxy<?> fieldProxy = new FieldProxy<>((AbstractField) DSL.field(name), positionBeforeName);
            field = fieldProxy;
            scopeStack.set(name, fieldProxy);
        }
        return field;
    }

    public String toString() {
        return mark();
    }

    public final <T> T data(Object key, Object value, java.util.function.Function<? super DefaultParseContext, ? extends T> function) {
        Object previous = data(key, value);
        try {
            T apply = function.apply(this);
            if (previous == null) {
                data().remove(key);
            } else {
                data(key, previous);
            }
            return apply;
        } catch (Throwable th) {
            if (previous == null) {
                data().remove(key);
            } else {
                data(key, previous);
            }
            throw th;
        }
    }
}
