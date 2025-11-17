package org.jooq.impl;

import ch.qos.logback.core.joran.JoranConstants;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import org.jooq.BindContext;
import org.jooq.Configuration;
import org.jooq.Constants;
import org.jooq.ExecuteContext;
import org.jooq.Field;
import org.jooq.Param;
import org.jooq.Query;
import org.jooq.QueryPart;
import org.jooq.QueryPartInternal;
import org.jooq.RenderContext;
import org.jooq.SQLDialect;
import org.jooq.Table;
import org.jooq.conf.ParamType;
import org.jooq.conf.RenderFormatting;
import org.jooq.conf.RenderKeywordCase;
import org.jooq.conf.RenderNameCase;
import org.jooq.conf.RenderQuotedNames;
import org.jooq.conf.Settings;
import org.jooq.conf.SettingsTools;
import org.jooq.exception.ControlFlowSignal;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.AbstractContext;
import org.jooq.impl.ScopeMarker;
import org.jooq.impl.Tools;
import org.jooq.tools.JooqLogger;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultRenderContext.class */
public class DefaultRenderContext extends AbstractContext<RenderContext> implements RenderContext {
    private static final JooqLogger log = JooqLogger.getLogger((Class<?>) DefaultRenderContext.class);
    private static final Pattern IDENTIFIER_PATTERN = Pattern.compile("[A-Za-z][A-Za-z0-9_]*");
    private static final Pattern NEWLINE = Pattern.compile("[\\n\\r]");
    private static final Set<String> SQLITE_KEYWORDS = new HashSet();
    final StringBuilder sql;
    private final QueryPartList<Param<?>> bindValues;
    private int alias;
    private int indent;
    private Deque<Integer> indentLock;
    private boolean separatorRequired;
    private boolean separator;
    private boolean newline;
    RenderKeywordCase cachedRenderKeywordCase;
    RenderNameCase cachedRenderNameCase;
    RenderQuotedNames cachedRenderQuotedNames;
    boolean cachedRenderFormatted;
    String cachedIndentation;
    int cachedIndentWidth;
    String cachedNewline;
    int cachedPrintMargin;

    static {
        SQLITE_KEYWORDS.addAll(Arrays.asList("ABORT", "ACTION", "ADD", "AFTER", "ALL", "ALTER", "ANALYZE", "AND", "AS", "ASC", "ATTACH", "AUTOINCREMENT", "BEFORE", "BEGIN", "BETWEEN", "BY", "CASCADE", "CASE", "CAST", "CHECK", "COLLATE", "COLUMN", "COMMIT", "CONFLICT", "CONSTRAINT", "CREATE", "CROSS", "CURRENT", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "DATABASE", "DEFAULT", "DEFERRABLE", "DEFERRED", "DELETE", "DESC", "DETACH", "DISTINCT", "DO", "DROP", "EACH", "ELSE", "END", "ESCAPE", "EXCEPT", "EXCLUDE", "EXCLUSIVE", "EXISTS", "EXPLAIN", "FAIL", "FILTER", "FOLLOWING", "FOR", "FOREIGN", "FROM", "FULL", "GLOB", "GROUP", "GROUPS", "HAVING", "IF", "IGNORE", "IMMEDIATE", "IN", "INDEX", "INDEXED", "INITIALLY", "INNER", "INSERT", "INSTEAD", "INTERSECT", "INTO", "IS", "ISNULL", "JOIN", "KEY", "LEFT", "LIKE", "LIMIT", "MATCH", "NATURAL", "NO", "NOT", "NOTHING", "NOTNULL", JoranConstants.NULL, "OF", "OFFSET", "ON", "OR", "ORDER", "OTHERS", "OUTER", "OVER", "PARTITION", "PLAN", "PRAGMA", "PRECEDING", "PRIMARY", "QUERY", "RAISE", "RANGE", "RECURSIVE", "REFERENCES", "REGEXP", "REINDEX", "RELEASE", "RENAME", "REPLACE", "RESTRICT", "RIGHT", "ROLLBACK", "ROW", "ROWS", "SAVEPOINT", "SELECT", "SET", "TABLE", "TEMP", "TEMPORARY", "THEN", "TIES", "TO", "TRANSACTION", "TRIGGER", "UNBOUNDED", "UNION", "UNIQUE", "UPDATE", "USING", "VACUUM", "VALUES", "VIEW", "VIRTUAL", "WHEN", "WHERE", "WINDOW", "WITH", "WITHOUT"));
        if (!Boolean.getBoolean("org.jooq.no-logo")) {
            JooqLogger l = JooqLogger.getLogger((Class<?>) Constants.class);
            l.info("\n                                      \n@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n@@@@@@@@@@@@@@@@  @@        @@@@@@@@@@\n@@@@@@@@@@@@@@@@@@@@        @@@@@@@@@@\n@@@@@@@@@@@@@@@@  @@  @@    @@@@@@@@@@\n@@@@@@@@@@  @@@@  @@  @@    @@@@@@@@@@\n@@@@@@@@@@        @@        @@@@@@@@@@\n@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n@@@@@@@@@@        @@        @@@@@@@@@@\n@@@@@@@@@@    @@  @@  @@@@  @@@@@@@@@@\n@@@@@@@@@@    @@  @@  @@@@  @@@@@@@@@@\n@@@@@@@@@@        @@  @  @  @@@@@@@@@@\n@@@@@@@@@@        @@        @@@@@@@@@@\n@@@@@@@@@@@@@@@@@@@@@@@  @@@@@@@@@@@@@\n@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@  " + "Thank you for using jOOQ 3.19.3 (Build date: 2024-01-19T12:43:13Z)" + "\n                                      ");
        }
        if (!Boolean.getBoolean("org.jooq.no-tips")) {
            JooqLogger l2 = JooqLogger.getLogger((Class<?>) Constants.class);
            l2.info("\n\njOOQ tip of the day: " + Tips.randomTip() + "\n");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultRenderContext(Configuration configuration, ExecuteContext ctx) {
        super(configuration, ctx, null);
        Settings settings = configuration.settings();
        this.sql = new StringBuilder();
        this.bindValues = new QueryPartList<>();
        this.cachedRenderKeywordCase = SettingsTools.getRenderKeywordCase(settings);
        this.cachedRenderFormatted = Boolean.TRUE.equals(settings.isRenderFormatted());
        this.cachedRenderNameCase = SettingsTools.getRenderNameCase(settings);
        this.cachedRenderQuotedNames = SettingsTools.getRenderQuotedNames(settings);
        RenderFormatting formatting = settings.getRenderFormatting();
        formatting = formatting == null ? new RenderFormatting() : formatting;
        this.cachedNewline = formatting.getNewline() == null ? "\n" : formatting.getNewline();
        this.cachedIndentation = formatting.getIndentation() == null ? "  " : formatting.getIndentation();
        this.cachedIndentWidth = this.cachedIndentation.length();
        this.cachedPrintMargin = formatting.getPrintMargin() == null ? 80 : formatting.getPrintMargin().intValue();
    }

    DefaultRenderContext(RenderContext context) {
        this(context, true);
    }

    DefaultRenderContext(RenderContext context, boolean copyLocalState) {
        this(context.configuration(), context.executeContext());
        paramType(context.paramType());
        qualify(context.qualify());
        qualifyCatalog(context.qualifyCatalog());
        qualifySchema(context.qualifySchema());
        quote(context.quote());
        castMode(context.castMode());
        topLevelForLanguageContext(context.topLevelForLanguageContext());
        if (copyLocalState) {
            data().putAll(context.data());
            this.declareCTE = context.declareCTE();
            this.declareWindows = context.declareWindows();
            this.declareFields = context.declareFields();
            this.declareTables = context.declareTables();
            this.declareAliases = context.declareAliases();
        }
    }

    @Override // org.jooq.Context
    public final BindContext bindValue(Object value, Field<?> field) throws DataAccessException {
        throw new UnsupportedOperationException();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final QueryPartList<Param<?>> bindValues() {
        return this.bindValues;
    }

    @Override // org.jooq.impl.AbstractContext
    void scopeMarkStart0(QueryPart part) {
        applyNewLine();
        AbstractContext.ScopeStackElement e = this.scopeStack.getOrCreate(part);
        e.positions = new int[]{this.sql.length(), -1};
        e.bindIndex = peekIndex();
        e.indent = this.indent;
        resetSeparatorFlags();
    }

    @Override // org.jooq.impl.AbstractContext
    void scopeMarkEnd0(QueryPart part) {
        applyNewLine();
        AbstractContext.ScopeStackElement e = this.scopeStack.getOrCreate(part);
        e.positions[1] = this.sql.length();
    }

    @Override // org.jooq.impl.AbstractContext, org.jooq.Context
    public QueryPart scopeMapping(QueryPart part) {
        AbstractContext.ScopeStackElement e;
        if (this.scopeStack.inScope() && (part instanceof ScopeMappable) && (e = this.scopeStack.get(part)) != null && e.mapped != null) {
            QueryPart result = e.mapped;
            if (part instanceof ScopeMappableWrapper) {
                result = ((ScopeMappableWrapper) part).wrap(result);
            }
            return result;
        }
        return part;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.AbstractContext, org.jooq.Context
    public RenderContext scopeRegister(QueryPart part, boolean forceNew, QueryPart mapped) {
        AbstractContext.ScopeStackElement e;
        AbstractContext.ScopeStackElement orCreate;
        if (this.scopeStack.inScope()) {
            if (part instanceof TableImpl) {
                Table<?> root = (Table) part;
                List<TableImpl<?>> tables = new ArrayList<>();
                if (!forceNew) {
                    while (true) {
                        Table<?> child = TableImpl.path(root);
                        if (child == null || this.scopeStack.get(root) != null) {
                            break;
                        }
                        tables.add((TableImpl) root);
                        root = child;
                    }
                }
                if (forceNew) {
                    orCreate = this.scopeStack.create(root);
                } else {
                    orCreate = this.scopeStack.getOrCreate(root);
                }
                e = orCreate;
                e.joinNode = AbstractContext.JoinNode.create(this, e.joinNode, root, tables);
            } else if (forceNew) {
                e = this.scopeStack.create(part);
            } else {
                e = this.scopeStack.getOrCreate(part);
            }
            e.mapped = mapped;
        }
        return this;
    }

    @Override // org.jooq.impl.AbstractContext
    void scopeStart0() {
        Iterator<AbstractContext.ScopeStackElement> it = this.scopeStack.iterator();
        while (it.hasNext()) {
            AbstractContext.ScopeStackElement e = it.next();
            if (e.part != e.mapped && !(e.part instanceof ScopeNestable)) {
                this.scopeStack.set(e.part, null);
            }
        }
    }

    @Override // org.jooq.impl.AbstractContext
    void scopeEnd0() {
        ScopeMarker[] markers = ScopeMarker.values();
        AbstractContext.ScopeStackElement[] beforeFirst = new AbstractContext.ScopeStackElement[markers.length];
        AbstractContext.ScopeStackElement[] afterLast = new AbstractContext.ScopeStackElement[markers.length];
        ScopeMarker.ScopeContent[] content = new ScopeMarker.ScopeContent[markers.length];
        for (ScopeMarker marker : markers) {
            if (!marker.topLevelOnly || subqueryLevel() == 0) {
                int i = marker.ordinal();
                ScopeMarker.ScopeContent o = (ScopeMarker.ScopeContent) data(marker.key);
                content[i] = o;
                if (o != null && !o.isEmpty()) {
                    beforeFirst[i] = this.scopeStack.get(marker.beforeFirst);
                    afterLast[i] = this.scopeStack.get(marker.afterLast);
                }
            }
        }
        for (AbstractContext.ScopeStackElement e1 : this.scopeStack.iterable(e -> {
            return e.scopeLevel == this.scopeStack.scopeLevel();
        })) {
            String replacedSQL = null;
            QueryPartList<Param<?>> insertedBindValues = null;
            if (e1.positions != null) {
                if (e1.joinNode != null && e1.joinNode.hasJoinPaths()) {
                    DefaultRenderContext ctx = new DefaultRenderContext((RenderContext) this, false);
                    ctx.data(Tools.BooleanDataKey.DATA_RENDER_IMPLICIT_JOIN, true);
                    replacedSQL = ctx.declareTables(true).sql('(').formatIndentStart(e1.indent).formatIndentStart().formatNewLine().visit(e1.joinNode.joinTree()).formatNewLine().sql(')').render();
                    insertedBindValues = ctx.bindValues();
                } else {
                    int i2 = 0;
                    while (true) {
                        if (i2 >= beforeFirst.length) {
                            break;
                        }
                        AbstractContext.ScopeStackElement e2 = beforeFirst[i2];
                        ScopeMarker.ScopeContent c = content[i2];
                        if (e1 != e2 || c == null) {
                            i2++;
                        } else {
                            DefaultRenderContext ctx2 = new DefaultRenderContext((RenderContext) this, false);
                            markers[i2].renderer.render((DefaultRenderContext) ctx2.formatIndentStart(e2.indent), e2, afterLast[i2], c);
                            replacedSQL = ctx2.render();
                            insertedBindValues = ctx2.bindValues();
                            break;
                        }
                    }
                }
                if (replacedSQL != null) {
                    this.sql.replace(e1.positions[0], e1.positions[1], replacedSQL);
                    int shift = replacedSQL.length() - (e1.positions[1] - e1.positions[0]);
                    Iterator<AbstractContext.ScopeStackElement> it = this.scopeStack.iterator();
                    while (it.hasNext()) {
                        AbstractContext.ScopeStackElement e22 = it.next();
                        if (e22.positions != null && e22.positions[0] > e1.positions[0]) {
                            e22.positions[0] = e22.positions[0] + shift;
                            e22.positions[1] = e22.positions[1] + shift;
                        }
                    }
                    if (insertedBindValues != null) {
                        this.bindValues.addAll(e1.bindIndex - 1, insertedBindValues);
                        Iterator<AbstractContext.ScopeStackElement> it2 = this.scopeStack.iterator();
                        while (it2.hasNext()) {
                            AbstractContext.ScopeStackElement e23 = it2.next();
                            if (e23.positions != null && e23.bindIndex > e1.bindIndex) {
                                e23.bindIndex += insertedBindValues.size();
                            }
                        }
                    }
                }
            }
        }
    }

    @Override // org.jooq.Context
    public final String peekAlias() {
        return "alias_" + (this.alias + 1);
    }

    @Override // org.jooq.Context
    public final String nextAlias() {
        int i = this.alias + 1;
        this.alias = i;
        return "alias_" + i;
    }

    @Override // org.jooq.Context
    public final String render() {
        String str;
        String str2;
        String str3;
        String str4;
        String prepend = null;
        String append = null;
        if (this.topLevel instanceof Query) {
            prepend = (String) data(Tools.SimpleDataKey.DATA_PREPEND_SQL);
            append = (String) data(Tools.SimpleDataKey.DATA_APPEND_SQL);
        }
        String result = this.sql.toString();
        if (prepend == null && append == null) {
            return result;
        }
        if (format()) {
            if (prepend != null) {
                str3 = prepend + (prepend.endsWith(this.cachedNewline) ? "" : this.cachedNewline);
            } else {
                str3 = "";
            }
            if (append != null) {
                str4 = ";" + (append.endsWith(this.cachedNewline) ? "" : this.cachedNewline) + append;
            } else {
                str4 = "";
            }
            return str3 + result + str4;
        }
        if (prepend != null) {
            str = prepend + (prepend.endsWith(" ") ? "" : " ");
        } else {
            str = "";
        }
        if (append != null) {
            str2 = ";" + (append.endsWith(" ") ? "" : " ") + append;
        } else {
            str2 = "";
        }
        return str + result + str2;
    }

    @Override // org.jooq.Context
    public final String render(QueryPart part) {
        return new DefaultRenderContext(this).visit(part).render();
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.Context
    public final RenderContext keyword(String keyword) {
        return visit(DSL.keyword(keyword));
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.Context
    public final RenderContext sql(String s) {
        return sql(s, s == null || !this.cachedRenderFormatted);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.Context
    public final RenderContext sql(String s, boolean literal) {
        if (!literal) {
            s = Tools.replaceAll(s, NEWLINE.matcher(s), r -> {
                return r.group() + indentation();
            });
        }
        if (stringLiteral()) {
            s = StringUtils.replace(s, "'", this.stringLiteralEscapedApos);
        }
        applyNewLine();
        this.sql.append(s);
        resetSeparatorFlags();
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.Context
    public final RenderContext sqlIndentStart(String c) {
        return sql(c).sqlIndentStart();
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.Context
    public final RenderContext sqlIndentEnd(String c) {
        return sqlIndentEnd().sql(c);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.Context
    public final RenderContext sqlIndentStart() {
        return formatIndentStart().formatNewLine();
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.Context
    public final RenderContext sqlIndentEnd() {
        return formatIndentEnd().formatNewLine();
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.Context
    public final RenderContext sql(char c) {
        applyNewLine();
        if (c == '\'' && stringLiteral()) {
            this.sql.append(this.stringLiteralEscapedApos);
        } else {
            this.sql.append(c);
        }
        resetSeparatorFlags();
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.Context
    public final RenderContext sqlIndentStart(char c) {
        return sql(c).sqlIndentStart();
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.Context
    public final RenderContext sqlIndentEnd(char c) {
        return sqlIndentEnd().sql(c);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.Context
    public final RenderContext sql(int i) {
        applyNewLine();
        this.sql.append(i);
        resetSeparatorFlags();
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.Context
    public final RenderContext sql(long l) {
        applyNewLine();
        this.sql.append(l);
        resetSeparatorFlags();
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.Context
    public final RenderContext sql(float f) {
        applyNewLine();
        this.sql.append(floatFormat().format(f));
        resetSeparatorFlags();
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.Context
    public final RenderContext sql(double d) {
        applyNewLine();
        this.sql.append(doubleFormat().format(d));
        resetSeparatorFlags();
        return this;
    }

    private final void resetSeparatorFlags() {
        this.separatorRequired = false;
        this.separator = false;
        this.newline = false;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.Context
    public final RenderContext formatNewLine() {
        if (this.cachedRenderFormatted) {
            this.newline = true;
        }
        return this;
    }

    private final void applyNewLine() {
        if (this.newline) {
            this.sql.append(this.cachedNewline);
            this.sql.append(indentation());
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.Context
    public final RenderContext formatNewLineAfterPrintMargin() {
        if (this.cachedRenderFormatted && this.cachedPrintMargin > 0 && this.sql.length() - this.sql.lastIndexOf(this.cachedNewline) > this.cachedPrintMargin) {
            formatNewLine();
        }
        return this;
    }

    private final String indentation() {
        return StringUtils.leftPad("", this.indent, this.cachedIndentation);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.Context
    public final RenderContext format(boolean format) {
        this.cachedRenderFormatted = format;
        return this;
    }

    @Override // org.jooq.Context
    public final boolean format() {
        return this.cachedRenderFormatted;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.Context
    public final RenderContext formatSeparator() {
        if (!this.separator && !this.newline) {
            if (this.cachedRenderFormatted) {
                formatNewLine();
            } else {
                sql(" ", true);
            }
            this.separator = true;
        }
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.Context
    public final RenderContext separatorRequired(boolean separatorRequired) {
        this.separatorRequired = separatorRequired;
        return this;
    }

    @Override // org.jooq.Context
    public final boolean separatorRequired() {
        return (!this.separatorRequired || this.separator || this.newline) ? false : true;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.Context
    public final RenderContext formatIndentStart() {
        return formatIndentStart(this.cachedIndentWidth);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.Context
    public final RenderContext formatIndentEnd() {
        return formatIndentEnd(this.cachedIndentWidth);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.Context
    public final RenderContext formatIndentStart(int i) {
        if (this.cachedRenderFormatted) {
            this.indent += i;
        }
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.Context
    public final RenderContext formatIndentEnd(int i) {
        if (this.cachedRenderFormatted) {
            this.indent -= i;
        }
        return this;
    }

    private final Deque<Integer> indentLock() {
        if (this.indentLock == null) {
            this.indentLock = new ArrayDeque();
        }
        return this.indentLock;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.Context
    public final RenderContext formatIndentLockStart() {
        if (this.cachedRenderFormatted) {
            indentLock().push(Integer.valueOf(this.indent));
            String[] lines = NEWLINE.split(this.sql);
            this.indent = lines[lines.length - 1].length();
        }
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.Context
    public final RenderContext formatIndentLockEnd() {
        if (this.cachedRenderFormatted) {
            this.indent = indentLock().pop().intValue();
        }
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.Context
    public final RenderContext formatPrintMargin(int margin) {
        this.cachedPrintMargin = margin;
        return this;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.Context
    public final RenderContext literal(String literal) {
        if (literal == null) {
            return this;
        }
        SQLDialect family = family();
        boolean needsQuote = (family != SQLDialect.SQLITE && quote()) || (family == SQLDialect.SQLITE && SQLITE_KEYWORDS.contains(literal.toUpperCase(SettingsTools.renderLocale(configuration().settings())))) || (family == SQLDialect.SQLITE && !IDENTIFIER_PATTERN.matcher(literal).matches());
        String literal2 = applyNameCase(literal);
        if (needsQuote) {
            char[][][] quotes = Identifiers.QUOTES.get(family);
            char start = quotes[0][0][0];
            char end = quotes[1][0][0];
            sql(start);
            if (literal2.indexOf(end) > -1) {
                sql(StringUtils.replace(literal2, new String(quotes[1][0]), new String(quotes[2][0])), true);
            } else {
                sql(literal2, true);
            }
            sql(end);
        } else {
            sql(literal2, true);
        }
        return this;
    }

    @Override // org.jooq.impl.AbstractContext
    final String applyNameCase(String literal) {
        if (RenderNameCase.LOWER == this.cachedRenderNameCase || (RenderNameCase.LOWER_IF_UNQUOTED == this.cachedRenderNameCase && !quote())) {
            return literal.toLowerCase(SettingsTools.renderLocale(configuration().settings()));
        }
        if (RenderNameCase.UPPER == this.cachedRenderNameCase || (RenderNameCase.UPPER_IF_UNQUOTED == this.cachedRenderNameCase && !quote())) {
            return literal.toUpperCase(SettingsTools.renderLocale(configuration().settings()));
        }
        return literal;
    }

    @Override // org.jooq.impl.AbstractContext
    protected final void visit0(QueryPartInternal internal) {
        int before = this.bindValues.size();
        internal.accept(this);
        int after = this.bindValues.size();
        if (after == before && this.paramType != ParamType.INLINED && (internal instanceof Param)) {
            Param<?> param = (Param) internal;
            if (!param.isInline()) {
                this.bindValues.add((QueryPartList<Param<?>>) param);
                Integer threshold = settings().getInlineThreshold();
                if (threshold != null && threshold.intValue() > 0) {
                    checkForceInline(threshold.intValue());
                    return;
                }
                switch (family()) {
                    case POSTGRES:
                    case YUGABYTEDB:
                        checkForceInline(32767);
                        return;
                    case SQLITE:
                        checkForceInline(999);
                        return;
                    default:
                        return;
                }
            }
        }
    }

    private final void checkForceInline(int max) throws ForceInlineSignal {
        if (this.bindValues.size() > max && Boolean.TRUE.equals(data(Tools.BooleanDataKey.DATA_COUNT_BIND_VALUES))) {
            throw new ForceInlineSignal();
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("rendering    [").append(render()).append("]\n");
        sb.append("formatting   [").append(format()).append("]\n");
        sb.append("parameters   [").append(this.paramType).append("]\n");
        toString(sb);
        return sb.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultRenderContext$ForceInlineSignal.class */
    public class ForceInlineSignal extends ControlFlowSignal {
        public ForceInlineSignal() {
            if (DefaultRenderContext.log.isDebugEnabled()) {
                DefaultRenderContext.log.debug("Re-render query", "Forcing bind variable inlining as " + String.valueOf(DefaultRenderContext.this.configuration().dialect()) + " does not support " + DefaultRenderContext.this.peekIndex() + " bind variables (or more) in a single query");
            }
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultRenderContext$Rendered.class */
    static class Rendered {
        String sql;
        QueryPartList<Param<?>> bindValues;
        int skipUpdateCounts;

        /* JADX INFO: Access modifiers changed from: package-private */
        public Rendered(String sql, QueryPartList<Param<?>> bindValues, int skipUpdateCounts) {
            this.sql = sql;
            this.bindValues = bindValues;
            this.skipUpdateCounts = skipUpdateCounts;
        }

        public String toString() {
            return this.sql;
        }
    }
}
