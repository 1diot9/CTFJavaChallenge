package org.jooq.impl;

import java.sql.PreparedStatement;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.jooq.BindContext;
import org.jooq.Clause;
import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.ConverterContext;
import org.jooq.DSLContext;
import org.jooq.ExecuteContext;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.InverseForeignKey;
import org.jooq.JoinType;
import org.jooq.LanguageContext;
import org.jooq.QueryPart;
import org.jooq.QueryPartInternal;
import org.jooq.RenderContext;
import org.jooq.SQLDialect;
import org.jooq.Table;
import org.jooq.VisitContext;
import org.jooq.VisitListener;
import org.jooq.VisitListenerProvider;
import org.jooq.conf.InvocationOrder;
import org.jooq.conf.ParamCastMode;
import org.jooq.conf.ParamType;
import org.jooq.conf.RenderImplicitJoinType;
import org.jooq.conf.Settings;
import org.jooq.conf.SettingsTools;
import org.jooq.conf.StatementType;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.QOM;
import org.jooq.impl.Tools;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractContext.class */
public abstract class AbstractContext<C extends Context<C>> extends AbstractScope implements Context<C> {
    final ExecuteContext ctx;
    final PreparedStatement stmt;
    boolean declareFields;
    boolean declareTables;
    boolean declareAliases;
    boolean declareWindows;
    boolean declareCTE;
    int subquery;
    java.util.BitSet subqueryScopedNestedSetOperations;
    boolean predicandSubquery;
    boolean derivedTableSubquery;
    boolean setOperationSubquery;
    int stringLiteral;
    String stringLiteralEscapedApos;
    int index;
    int scopeMarking;
    final ScopeStack<QueryPart, ScopeStackElement> scopeStack;
    int skipUpdateCounts;
    private final VisitListener[] visitListenersStart;
    private final VisitListener[] visitListenersEnd;
    private final Deque<Clause> visitClauses;
    private final AbstractContext<C>.DefaultVisitContext visitContext;
    private final Deque<QueryPart> visitParts;
    final ParamType forcedParamType;
    final boolean castModeOverride;
    RenderContext.CastMode castMode;
    LanguageContext languageContext;
    ParamType paramType;
    boolean quote;
    boolean qualify;
    boolean qualifySchema;
    boolean qualifyCatalog;
    QueryPart topLevel;
    QueryPart topLevelForLanguageContext;
    private transient DecimalFormat doubleFormat;
    private transient DecimalFormat floatFormat;

    protected abstract void visit0(QueryPartInternal queryPartInternal);

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractContext(Configuration configuration, ExecuteContext ctx, PreparedStatement stmt) {
        super(configuration);
        VisitListener[] visitListenerArr;
        ParamType paramType;
        RenderContext.CastMode castMode;
        VisitListener[] visitListenerArr2;
        VisitListener[] visitListenerArr3;
        this.stringLiteralEscapedApos = "'";
        this.paramType = ParamType.INDEXED;
        this.quote = true;
        this.qualify = true;
        this.qualifySchema = true;
        this.qualifyCatalog = true;
        this.ctx = ctx;
        this.stmt = stmt;
        VisitListenerProvider[] providers = configuration.visitListenerProviders();
        if (providers.length > 0 || 0 != 0) {
            visitListenerArr = new VisitListener[providers.length + (0 != 0 ? 1 : 0)];
        } else {
            visitListenerArr = null;
        }
        VisitListener[] visitListeners = visitListenerArr;
        if (visitListeners != null) {
            for (int i = 0; i < providers.length; i++) {
                visitListeners[i] = providers[i].provide();
            }
            this.visitContext = new DefaultVisitContext();
            this.visitParts = new ArrayDeque();
            this.visitClauses = new ArrayDeque();
            if (configuration.settings().getVisitListenerStartInvocationOrder() != InvocationOrder.REVERSE) {
                visitListenerArr2 = visitListeners;
            } else {
                visitListenerArr2 = (VisitListener[]) Tools.reverse((VisitListener[]) visitListeners.clone());
            }
            this.visitListenersStart = visitListenerArr2;
            if (configuration.settings().getVisitListenerEndInvocationOrder() != InvocationOrder.REVERSE) {
                visitListenerArr3 = visitListeners;
            } else {
                visitListenerArr3 = (VisitListener[]) Tools.reverse((VisitListener[]) visitListeners.clone());
            }
            this.visitListenersEnd = visitListenerArr3;
        } else {
            this.visitContext = null;
            this.visitParts = null;
            this.visitClauses = null;
            this.visitListenersStart = null;
            this.visitListenersEnd = null;
        }
        if (SettingsTools.getStatementType(settings()) == StatementType.STATIC_STATEMENT) {
            paramType = ParamType.INLINED;
        } else if (SettingsTools.getParamType(settings()) == ParamType.FORCE_INDEXED) {
            paramType = ParamType.INDEXED;
        } else {
            paramType = null;
        }
        this.forcedParamType = paramType;
        ParamCastMode m = settings().getParamCastMode();
        this.castModeOverride = (m == ParamCastMode.DEFAULT || m == null) ? false : true;
        if (m == ParamCastMode.ALWAYS) {
            castMode = RenderContext.CastMode.ALWAYS;
        } else if (m == ParamCastMode.NEVER) {
            castMode = RenderContext.CastMode.NEVER;
        } else {
            castMode = RenderContext.CastMode.DEFAULT;
        }
        this.castMode = castMode;
        this.languageContext = LanguageContext.QUERY;
        this.scopeStack = new ScopeStack<>((k, v) -> {
            if (k == DataKeyScopeStackPart.INSTANCE) {
                return new DataKeyScopeStackElement(k, v);
            }
            if (k == ScopeDefinerScopeStackPart.INSTANCE) {
                return new ScopeDefinerScopeStackElement(k, v);
            }
            return new DefaultScopeStackElement(k, v);
        });
    }

    @Override // org.jooq.ExecuteScope
    public final ConverterContext converterContext() {
        return this.ctx != null ? this.ctx.converterContext() : Tools.converterContext(configuration());
    }

    @Override // org.jooq.ExecuteScope
    public final ExecuteContext executeContext() {
        return this.ctx;
    }

    @Override // org.jooq.Context
    public final C visit(Condition part) {
        return visit((QueryPart) part);
    }

    @Override // org.jooq.Context
    public final C visit(Field<?> part) {
        if (!(part instanceof Condition)) {
            return visit((QueryPart) part);
        }
        Condition c = (Condition) part;
        return visit((QueryPart) DSL.field(c));
    }

    @Override // org.jooq.Context
    public final C visit(QueryPart part) {
        if (part != null) {
            if (this.topLevel == null) {
                this.topLevelForLanguageContext = part;
                this.topLevel = part;
                if (executeContext() != null || !Boolean.TRUE.equals(settings().isTransformPatterns()) || configuration().requireCommercial(() -> {
                    return "SQL transformations are a commercial only feature. Please consider upgrading to the jOOQ Professional Edition or jOOQ Enterprise Edition.";
                })) {
                }
            }
            Clause[] clauses = Tools.isNotEmpty(this.visitListenersStart) ? clause(part) : null;
            if (clauses != null) {
                for (Clause clause : clauses) {
                    start(clause);
                }
            }
            QueryPart replacement = start(part);
            if (replacement != null) {
                QueryPartInternal internal = (QueryPartInternal) scopeMapping(replacement);
                if (declareFields() && !internal.declaresFields()) {
                    boolean aliases = declareAliases();
                    declareFields(false);
                    visit0(internal);
                    declareFields(true);
                    declareAliases(aliases);
                } else if (declareTables() && !internal.declaresTables()) {
                    boolean aliases2 = declareAliases();
                    declareTables(false);
                    visit0(internal);
                    declareTables(true);
                    declareAliases(aliases2);
                } else if (declareWindows() && !internal.declaresWindows()) {
                    declareWindows(false);
                    visit0(internal);
                    declareWindows(true);
                } else if (declareCTE() && !internal.declaresCTE()) {
                    declareCTE(false);
                    visit0(internal);
                    declareCTE(true);
                } else if (!this.castModeOverride && castMode() != RenderContext.CastMode.DEFAULT && !internal.generatesCast()) {
                    RenderContext.CastMode previous = castMode();
                    castMode(RenderContext.CastMode.DEFAULT);
                    visit0(internal);
                    castMode(previous);
                } else {
                    visit0(internal);
                }
            }
            end(replacement);
            if (clauses != null) {
                for (int i = clauses.length - 1; i >= 0; i--) {
                    end(clauses[i]);
                }
            }
        }
        return this;
    }

    @Override // org.jooq.Context
    public final C visitSubquery(QueryPart part) {
        Tools.visitSubquery(this, part);
        return this;
    }

    private final C toggle(boolean b, BooleanSupplier get, BooleanConsumer set, Consumer<? super C> consumer) {
        boolean previous = get.getAsBoolean();
        try {
            set.accept(b);
            consumer.accept(this);
            set.accept(previous);
            return this;
        } catch (Throwable th) {
            set.accept(previous);
            throw th;
        }
    }

    private final <T> C toggle(T t, Supplier<T> get, Consumer<T> set, Consumer<? super C> consumer) {
        T previous = get.get();
        try {
            set.accept(t);
            consumer.accept(this);
            set.accept(previous);
            return this;
        } catch (Throwable th) {
            set.accept(previous);
            throw th;
        }
    }

    @Override // org.jooq.Context
    public final C data(Object key, Object value, Consumer<? super C> consumer) {
        return toggle((AbstractContext<C>) value, (Supplier<AbstractContext<C>>) () -> {
            return data(key);
        }, (Consumer<AbstractContext<C>>) v -> {
            if (v == null) {
                data().remove(key);
            } else {
                data(key, v);
            }
        }, (Consumer) consumer);
    }

    private final Clause[] clause(QueryPart part) {
        if ((part instanceof QueryPartInternal) && !Boolean.TRUE.equals(data(Tools.BooleanDataKey.DATA_OMIT_CLAUSE_EVENT_EMISSION))) {
            return ((QueryPartInternal) part).clauses(this);
        }
        return null;
    }

    @Override // org.jooq.Context
    public final C start(Clause clause) {
        if (clause != null && this.visitClauses != null) {
            this.visitClauses.addLast(clause);
            for (VisitListener listener : this.visitListenersStart) {
                listener.clauseStart(this.visitContext);
            }
        }
        return this;
    }

    @Override // org.jooq.Context
    public final C end(Clause clause) {
        if (clause != null && this.visitClauses != null) {
            for (VisitListener listener : this.visitListenersEnd) {
                listener.clauseEnd(this.visitContext);
            }
            if (this.visitClauses.removeLast() != clause) {
                throw new IllegalStateException("Mismatch between visited clauses!");
            }
        }
        return this;
    }

    private final QueryPart start(QueryPart part) {
        if (this.visitParts != null) {
            this.visitParts.addLast(part);
            for (VisitListener listener : this.visitListenersStart) {
                listener.visitStart(this.visitContext);
            }
            return this.visitParts.peekLast();
        }
        return part;
    }

    private final void end(QueryPart part) {
        if (this.visitParts != null) {
            for (VisitListener listener : this.visitListenersEnd) {
                listener.visitEnd(this.visitContext);
            }
            if (this.visitParts.removeLast() != part) {
                throw new RuntimeException("Mismatch between visited query parts");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractContext$DefaultVisitContext.class */
    public class DefaultVisitContext implements VisitContext {
        private DefaultVisitContext() {
        }

        @Override // org.jooq.Scope
        public final Instant creationTime() {
            return AbstractContext.this.creationTime();
        }

        @Override // org.jooq.Scope
        public final Map<Object, Object> data() {
            return AbstractContext.this.data();
        }

        @Override // org.jooq.Scope
        public final Object data(Object key) {
            return AbstractContext.this.data(key);
        }

        @Override // org.jooq.Scope
        public final Object data(Object key, Object value) {
            return AbstractContext.this.data(key, value);
        }

        @Override // org.jooq.Scope
        public final Configuration configuration() {
            return AbstractContext.this.configuration();
        }

        @Override // org.jooq.Scope
        public final DSLContext dsl() {
            return AbstractContext.this.dsl();
        }

        @Override // org.jooq.Scope
        public final Settings settings() {
            return Tools.settings(configuration());
        }

        @Override // org.jooq.Scope
        public final SQLDialect dialect() {
            return Tools.configuration(configuration()).dialect();
        }

        @Override // org.jooq.Scope
        public final SQLDialect family() {
            return dialect().family();
        }

        @Override // org.jooq.VisitContext
        public final Clause clause() {
            return AbstractContext.this.visitClauses.peekLast();
        }

        @Override // org.jooq.VisitContext
        public final Clause[] clauses() {
            return (Clause[]) AbstractContext.this.visitClauses.toArray(Tools.EMPTY_CLAUSE);
        }

        @Override // org.jooq.VisitContext
        public final int clausesLength() {
            return AbstractContext.this.visitClauses.size();
        }

        @Override // org.jooq.VisitContext
        public final QueryPart queryPart() {
            return AbstractContext.this.visitParts.peekLast();
        }

        @Override // org.jooq.VisitContext
        public final void queryPart(QueryPart part) {
            AbstractContext.this.visitParts.pollLast();
            AbstractContext.this.visitParts.addLast(part);
        }

        @Override // org.jooq.VisitContext
        public final QueryPart[] queryParts() {
            return (QueryPart[]) AbstractContext.this.visitParts.toArray(Tools.EMPTY_QUERYPART);
        }

        @Override // org.jooq.VisitContext
        public final int queryPartsLength() {
            return AbstractContext.this.visitParts.size();
        }

        @Override // org.jooq.VisitContext
        public final Context<?> context() {
            return AbstractContext.this;
        }

        @Override // org.jooq.VisitContext
        public final RenderContext renderContext() {
            Context<?> context = context();
            if (!(context instanceof RenderContext)) {
                return null;
            }
            RenderContext c = (RenderContext) context;
            return c;
        }

        @Override // org.jooq.VisitContext
        public final BindContext bindContext() {
            Context<?> context = context();
            if (!(context instanceof BindContext)) {
                return null;
            }
            BindContext c = (BindContext) context;
            return c;
        }
    }

    @Override // org.jooq.Context
    public final boolean declareFields() {
        return this.declareFields;
    }

    @Override // org.jooq.Context
    public final C declareFields(boolean d) {
        this.declareFields = d;
        declareAliases(d);
        return this;
    }

    @Override // org.jooq.Context
    public C declareFields(boolean f, Consumer<? super C> consumer) {
        return toggle(f, this::declareFields, this::declareFields, consumer);
    }

    @Override // org.jooq.Context
    public final boolean declareTables() {
        return this.declareTables;
    }

    @Override // org.jooq.Context
    public final C declareTables(boolean d) {
        this.declareTables = d;
        declareAliases(d);
        return this;
    }

    @Override // org.jooq.Context
    public C declareTables(boolean f, Consumer<? super C> consumer) {
        return toggle(f, this::declareTables, this::declareTables, consumer);
    }

    @Override // org.jooq.Context
    public final boolean declareAliases() {
        return this.declareAliases;
    }

    @Override // org.jooq.Context
    public final C declareAliases(boolean d) {
        this.declareAliases = d;
        return this;
    }

    @Override // org.jooq.Context
    public C declareAliases(boolean f, Consumer<? super C> consumer) {
        return toggle(f, this::declareAliases, this::declareAliases, consumer);
    }

    @Override // org.jooq.Context
    public final boolean declareWindows() {
        return this.declareWindows;
    }

    @Override // org.jooq.Context
    public final C declareWindows(boolean d) {
        this.declareWindows = d;
        return this;
    }

    @Override // org.jooq.Context
    public C declareWindows(boolean f, Consumer<? super C> consumer) {
        return toggle(f, this::declareWindows, this::declareWindows, consumer);
    }

    @Override // org.jooq.Context
    public final boolean declareCTE() {
        return this.declareCTE;
    }

    @Override // org.jooq.Context
    public final C declareCTE(boolean d) {
        this.declareCTE = d;
        return this;
    }

    @Override // org.jooq.Context
    public C declareCTE(boolean f, Consumer<? super C> consumer) {
        return toggle(f, this::declareCTE, this::declareCTE, consumer);
    }

    @Override // org.jooq.Context
    public final int scopeLevel() {
        return this.scopeStack.scopeLevel();
    }

    @Override // org.jooq.Context
    public final QueryPart topLevel() {
        return this.topLevel;
    }

    @Override // org.jooq.Context
    public final C topLevel(QueryPart t) {
        this.topLevel = t;
        return this;
    }

    @Override // org.jooq.Context
    public final QueryPart topLevelForLanguageContext() {
        return this.topLevelForLanguageContext;
    }

    @Override // org.jooq.Context
    public final C topLevelForLanguageContext(QueryPart t) {
        this.topLevelForLanguageContext = t;
        return this;
    }

    @Override // org.jooq.Context
    public final int subqueryLevel() {
        return this.subquery;
    }

    @Override // org.jooq.Context
    public final boolean predicandSubquery() {
        return this.predicandSubquery;
    }

    @Override // org.jooq.Context
    public final C predicandSubquery(boolean s) {
        this.predicandSubquery = s;
        return this;
    }

    @Override // org.jooq.Context
    public final boolean derivedTableSubquery() {
        return this.derivedTableSubquery;
    }

    @Override // org.jooq.Context
    public final C derivedTableSubquery(boolean s) {
        this.derivedTableSubquery = s;
        return this;
    }

    @Override // org.jooq.Context
    public final boolean setOperationSubquery() {
        return this.setOperationSubquery;
    }

    @Override // org.jooq.Context
    public final C setOperationSubquery(boolean s) {
        this.setOperationSubquery = s;
        return this;
    }

    @Override // org.jooq.Context
    public final boolean subquery() {
        return this.subquery > 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final C subquery0(boolean s, boolean setOperation, QueryPart part) {
        setOperationSubquery(setOperation);
        if (s) {
            this.subquery++;
            if (!setOperation && Boolean.TRUE.equals(data(Tools.BooleanDataKey.DATA_NESTED_SET_OPERATIONS))) {
                data().remove(Tools.BooleanDataKey.DATA_NESTED_SET_OPERATIONS);
                if (this.subqueryScopedNestedSetOperations == null) {
                    this.subqueryScopedNestedSetOperations = new java.util.BitSet();
                }
                this.subqueryScopedNestedSetOperations.set(this.subquery);
            }
            scopeStart(part);
        } else {
            scopeEnd();
            if (!setOperation) {
                if (this.subqueryScopedNestedSetOperations != null && this.subqueryScopedNestedSetOperations.get(this.subquery)) {
                    data(Tools.BooleanDataKey.DATA_NESTED_SET_OPERATIONS, true);
                } else {
                    data().remove(Tools.BooleanDataKey.DATA_NESTED_SET_OPERATIONS);
                }
            }
            this.subquery--;
        }
        return this;
    }

    @Override // org.jooq.Context
    public final C subquery(boolean s) {
        return subquery(s, null);
    }

    @Override // org.jooq.Context
    public final C subquery(boolean s, QueryPart part) {
        return subquery0(s, false, part);
    }

    @Override // org.jooq.Context
    public final C scopeStart() {
        return scopeStart(null);
    }

    @Override // org.jooq.Context
    public final C scopeStart(QueryPart part) {
        this.scopeStack.scopeStart();
        if (part != null) {
            ((ScopeDefinerScopeStackElement) this.scopeStack.getOrCreate(ScopeDefinerScopeStackPart.INSTANCE)).scopeDefiner = part;
        }
        scopeStart0();
        resetDataKeys((DataKeyScopeStackElement) this.scopeStack.getOrCreate(DataKeyScopeStackPart.INSTANCE));
        return this;
    }

    @Override // org.jooq.Context
    public final QueryPart scopePart() {
        ScopeDefinerScopeStackElement e = (ScopeDefinerScopeStackElement) this.scopeStack.get(ScopeDefinerScopeStackPart.INSTANCE);
        if (e != null) {
            return e.scopeDefiner;
        }
        return null;
    }

    @Override // org.jooq.Context
    public final C scopeRegister(QueryPart part) {
        return scopeRegister(part, false);
    }

    @Override // org.jooq.Context
    public final C scopeRegister(QueryPart part, boolean forceNew) {
        return scopeRegister(part, forceNew, null);
    }

    @Override // org.jooq.Context
    public C scopeRegister(QueryPart part, boolean forceNew, QueryPart mapped) {
        return this;
    }

    @Override // org.jooq.Context
    public final boolean inScope(QueryPart part) {
        return this.scopeStack.get(part) != null;
    }

    @Override // org.jooq.Context
    public final boolean inCurrentScope(QueryPart part) {
        return this.scopeStack.getCurrentScope(part) != null;
    }

    @Override // org.jooq.Context
    public QueryPart scopeMapping(QueryPart part) {
        return part;
    }

    @Override // org.jooq.Context
    public final C scopeRegisterAndMark(QueryPart queryPart, boolean z) {
        return (C) scopeRegister(queryPart, z).scopeMarkStart(queryPart).scopeMarkEnd(queryPart);
    }

    @Override // org.jooq.Context
    public final C scopeMarkStart(QueryPart part) {
        if (this.scopeStack.inScope()) {
            int i = this.scopeMarking;
            this.scopeMarking = i + 1;
            if (i == 0) {
                scopeMarkStart0(part);
            }
        }
        return this;
    }

    @Override // org.jooq.Context
    public final C scopeMarkEnd(QueryPart part) {
        if (this.scopeStack.inScope()) {
            int i = this.scopeMarking - 1;
            this.scopeMarking = i;
            if (i == 0) {
                scopeMarkEnd0(part);
            }
        }
        return this;
    }

    @Override // org.jooq.Context
    public final C scopeEnd() {
        restoreDataKeys((DataKeyScopeStackElement) this.scopeStack.getOrCreate(DataKeyScopeStackPart.INSTANCE));
        scopeEnd0();
        this.scopeStack.scopeEnd();
        return this;
    }

    void scopeStart0() {
    }

    void scopeMarkStart0(QueryPart part) {
    }

    void scopeMarkEnd0(QueryPart part) {
    }

    void scopeEnd0() {
    }

    final void resetDataKeys(DataKeyScopeStackElement e) {
        for (int i = 0; i < Tools.DATAKEY_RESET_IN_SUBQUERY_SCOPE.length; i++) {
            Tools.DataKey key = Tools.DATAKEY_RESET_IN_SUBQUERY_SCOPE[i];
            if (subqueryLevel() >= key.resetThreshold() && data().containsKey(key)) {
                List<Object> lazy = Tools.lazy(e.restoreDataKeys, i + 1);
                e.restoreDataKeys = lazy;
                lazy.set(i, data().put(key, key.resetValue()));
            }
        }
    }

    final void restoreDataKeys(DataKeyScopeStackElement e) {
        for (int i = 0; i < Tools.DATAKEY_RESET_IN_SUBQUERY_SCOPE.length; i++) {
            Tools.DataKey key = Tools.DATAKEY_RESET_IN_SUBQUERY_SCOPE[i];
            if (subqueryLevel() >= key.resetThreshold() && e.restoreDataKeys != null && i < e.restoreDataKeys.size()) {
                data().put(key, e.restoreDataKeys.get(i));
            }
        }
    }

    String applyNameCase(String literal) {
        return literal;
    }

    @Override // org.jooq.Context
    public final boolean stringLiteral() {
        return this.stringLiteral > 0;
    }

    @Override // org.jooq.Context
    public final C stringLiteral(boolean s) {
        if (s) {
            this.stringLiteral++;
            this.stringLiteralEscapedApos += this.stringLiteralEscapedApos;
        } else {
            this.stringLiteral--;
            this.stringLiteralEscapedApos = this.stringLiteralEscapedApos.substring(0, this.stringLiteralEscapedApos.length() / 2);
        }
        return this;
    }

    @Override // org.jooq.Context
    public final int nextIndex() {
        int i = this.index + 1;
        this.index = i;
        return i;
    }

    @Override // org.jooq.Context
    public final int peekIndex() {
        return this.index + 1;
    }

    @Override // org.jooq.Context
    public final int skipUpdateCounts() {
        return this.skipUpdateCounts + (this.ctx != null ? this.ctx.skipUpdateCounts() : 0);
    }

    @Override // org.jooq.Context
    public final C skipUpdateCount() {
        return skipUpdateCounts(1);
    }

    @Override // org.jooq.Context
    public final C skipUpdateCounts(int skip) {
        this.skipUpdateCounts += skip;
        return this;
    }

    @Override // org.jooq.Context
    public final DecimalFormat floatFormat() {
        if (this.floatFormat == null) {
            this.floatFormat = new DecimalFormat("0.#######E0", DecimalFormatSymbols.getInstance(Locale.US));
        }
        return this.floatFormat;
    }

    @Override // org.jooq.Context
    public final DecimalFormat doubleFormat() {
        if (this.doubleFormat == null) {
            this.doubleFormat = new DecimalFormat("0.################E0", DecimalFormatSymbols.getInstance(Locale.US));
        }
        return this.doubleFormat;
    }

    @Override // org.jooq.Context
    public final ParamType paramType() {
        return this.forcedParamType != null ? this.forcedParamType : this.paramType;
    }

    @Override // org.jooq.Context
    public final C paramType(ParamType p) {
        this.paramType = p == null ? ParamType.INDEXED : p;
        return this;
    }

    @Override // org.jooq.Context
    public final C visit(QueryPart part, ParamType p) {
        return paramType(p, c -> {
            c.visit(part);
        });
    }

    @Override // org.jooq.Context
    public final C paramTypeIf(ParamType p, boolean condition) {
        if (condition) {
            paramType(p);
        }
        return this;
    }

    @Override // org.jooq.Context
    public final C paramType(ParamType p, Consumer<? super C> runnable) {
        return toggle((AbstractContext<C>) p, (Supplier<AbstractContext<C>>) this::paramType, (Consumer<AbstractContext<C>>) this::paramType, (Consumer) runnable);
    }

    @Override // org.jooq.Context
    public final C paramTypeIf(ParamType p, boolean condition, Consumer<? super C> runnable) {
        if (condition) {
            paramType(p, runnable);
        } else {
            runnable.accept(this);
        }
        return this;
    }

    @Override // org.jooq.Context
    public final boolean quote() {
        return this.quote;
    }

    @Override // org.jooq.Context
    public final C quote(boolean q) {
        this.quote = q;
        return this;
    }

    @Override // org.jooq.Context
    public final C quote(boolean q, Consumer<? super C> consumer) {
        return toggle(q, this::quote, this::quote, consumer);
    }

    @Override // org.jooq.Context
    public final boolean qualify() {
        return this.qualify;
    }

    @Override // org.jooq.Context
    public final C qualify(boolean q) {
        this.qualify = q;
        return this;
    }

    @Override // org.jooq.Context
    public final C qualify(boolean q, Consumer<? super C> consumer) {
        return toggle(q, this::qualify, this::qualify, consumer);
    }

    @Override // org.jooq.Context
    public final boolean qualifySchema() {
        return this.qualify && this.qualifySchema;
    }

    @Override // org.jooq.Context
    public final C qualifySchema(boolean q) {
        this.qualifySchema = q;
        return this;
    }

    @Override // org.jooq.Context
    public final C qualifySchema(boolean q, Consumer<? super C> consumer) {
        return toggle(q, this::qualifySchema, this::qualifySchema, consumer);
    }

    @Override // org.jooq.Context
    public final boolean qualifyCatalog() {
        return this.qualify && this.qualifyCatalog;
    }

    @Override // org.jooq.Context
    public final C qualifyCatalog(boolean q) {
        this.qualifyCatalog = q;
        return this;
    }

    @Override // org.jooq.Context
    public final C qualifyCatalog(boolean q, Consumer<? super C> consumer) {
        return toggle(q, this::qualifyCatalog, this::qualifyCatalog, consumer);
    }

    @Override // org.jooq.Context
    public final LanguageContext languageContext() {
        return this.languageContext;
    }

    @Override // org.jooq.Context
    public final C languageContext(LanguageContext context) {
        this.languageContext = context;
        return this;
    }

    @Override // org.jooq.Context
    public final C languageContext(LanguageContext context, Consumer<? super C> consumer) {
        return toggle((AbstractContext<C>) context, (Supplier<AbstractContext<C>>) this::languageContext, (Consumer<AbstractContext<C>>) this::languageContext, (Consumer) consumer);
    }

    @Override // org.jooq.Context
    public final C languageContext(LanguageContext context, QueryPart newTopLevelForLanguageContext, Consumer<? super C> consumer) {
        return toggle((AbstractContext<C>) context, (Supplier<AbstractContext<C>>) this::languageContext, (Consumer<AbstractContext<C>>) this::languageContext, (Consumer) c -> {
            toggle((AbstractContext<C>) newTopLevelForLanguageContext, (Supplier<AbstractContext<C>>) this::topLevelForLanguageContext, (Consumer<AbstractContext<C>>) this::topLevelForLanguageContext, consumer);
        });
    }

    @Override // org.jooq.Context
    public final C languageContextIf(LanguageContext context, boolean condition) {
        if (condition) {
            languageContext(context);
        }
        return this;
    }

    @Override // org.jooq.Context
    public final RenderContext.CastMode castMode() {
        return this.castMode;
    }

    @Override // org.jooq.Context
    public final C castMode(RenderContext.CastMode mode) {
        this.castMode = mode;
        return this;
    }

    @Override // org.jooq.Context
    public final C castMode(RenderContext.CastMode mode, Consumer<? super C> consumer) {
        return toggle((AbstractContext<C>) mode, (Supplier<AbstractContext<C>>) this::castMode, (Consumer<AbstractContext<C>>) this::castMode, (Consumer) consumer);
    }

    @Override // org.jooq.Context
    public final C castModeIf(RenderContext.CastMode mode, boolean condition) {
        if (condition) {
            castMode(mode);
        }
        return this;
    }

    @Override // org.jooq.Context
    public final PreparedStatement statement() {
        return this.stmt;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void toString(StringBuilder sb) {
        sb.append("bind index   [");
        sb.append(this.index);
        sb.append("]");
        sb.append("\ncast mode    [");
        sb.append(this.castMode);
        sb.append("]");
        sb.append("\ndeclaring    [");
        if (this.declareFields) {
            sb.append("fields");
            if (this.declareAliases) {
                sb.append(" and aliases");
            }
        } else if (this.declareTables) {
            sb.append("tables");
            if (this.declareAliases) {
                sb.append(" and aliases");
            }
        } else if (this.declareWindows) {
            sb.append("windows");
        } else if (this.declareCTE) {
            sb.append("cte");
        } else {
            sb.append("-");
        }
        sb.append("]\nsubquery     [");
        sb.append(this.subquery);
        sb.append("]");
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractContext$JoinNode.class */
    static class JoinNode {
        final Context<?> ctx;
        final Table<?> table;
        final Map<ForeignKey<?, ?>, JoinNode> pathsToOne = new LinkedHashMap();
        final Map<InverseForeignKey<?, ?>, JoinNode> pathsToMany = new LinkedHashMap();
        int references;

        JoinNode(Context<?> ctx, Table<?> table) {
            this.ctx = ctx;
            this.table = table;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static final JoinNode create(Context<?> ctx, JoinNode result, Table<?> root, List<TableImpl<?>> tables) {
            if (result == null) {
                result = new JoinNode(ctx, root);
            }
            JoinNode node = result;
            for (int i = tables.size() - 1; i >= 0; i--) {
                TableImpl<?> t = tables.get(i);
                if (t.childPath != null) {
                    node = node.pathsToOne.computeIfAbsent(t.childPath, k -> {
                        return new JoinNode(ctx, t);
                    });
                } else {
                    node = node.pathsToMany.computeIfAbsent(t.parentPath, k2 -> {
                        return new JoinNode(ctx, t);
                    });
                }
                if (i == 0) {
                    node.references++;
                }
            }
            return result;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Table<?> joinTree() {
            return joinTree(null);
        }

        Table<?> joinTree(JoinType joinType) {
            Table<?> result = this.table;
            if (this.ctx.data(Tools.BooleanDataKey.DATA_RENDER_IMPLICIT_JOIN) != null && TableImpl.path(result) != null) {
                result = Tools.unwrap(result).as(result);
            }
            for (Map.Entry<ForeignKey<?, ?>, JoinNode> e : this.pathsToOne.entrySet()) {
                Table<?> t = e.getValue().joinTree(joinType);
                if (skippable(e.getKey(), e.getValue())) {
                    result = appendToManyPaths(result, e.getValue(), joinType);
                } else {
                    result = result.join(t, joinType != null ? joinType : joinType(t, e.getKey().nullable() ? JoinType.LEFT_OUTER_JOIN : JoinType.JOIN)).on(JoinTable.onKey0(e.getKey(), result, t));
                }
            }
            return appendToManyPaths(result, this, joinType);
        }

        private static final Table<?> appendToManyPaths(Table<?> result, JoinNode node, JoinType joinType) {
            for (Map.Entry<InverseForeignKey<?, ?>, JoinNode> e : node.pathsToMany.entrySet()) {
                Table<?> t = e.getValue().joinTree();
                result = result.join(t, joinType != null ? joinType : node.joinToManyType(t)).on(JoinTable.onKey0(e.getKey().getForeignKey(), t, result));
            }
            return result;
        }

        private final boolean skippable(ForeignKey<?, ?> fk, JoinNode node) {
            if (node.references != 0 || !node.pathsToOne.isEmpty()) {
                return false;
            }
            for (Map.Entry<InverseForeignKey<?, ?>, JoinNode> path : node.pathsToMany.entrySet()) {
                if (!fk.getKeyFields().equals(path.getKey().getFields())) {
                    return false;
                }
            }
            return true;
        }

        private final JoinType joinType(Table<?> t, JoinType onDefault) {
            RenderImplicitJoinType type = (RenderImplicitJoinType) StringUtils.defaultIfNull(Tools.settings(this.ctx.configuration()).getRenderImplicitJoinType(), RenderImplicitJoinType.DEFAULT);
            switch (type) {
                case INNER_JOIN:
                    return JoinType.JOIN;
                case LEFT_JOIN:
                    return JoinType.LEFT_OUTER_JOIN;
                case THROW:
                    if (!allInScope(t)) {
                        throw new DataAccessException("Implicit to-one JOIN of " + this.ctx.dsl().renderContext().declareTables(true).render(this.table) + " isn't supported with Settings.renderImplicitJoinType = " + String.valueOf(type) + ". Either change Settings value, or use explicit path join, see https://www.jooq.org/doc/latest/manual/sql-building/sql-statements/select-statement/explicit-path-join/");
                    }
                    return JoinType.LEFT_OUTER_JOIN;
                case DEFAULT:
                default:
                    return onDefault;
            }
        }

        private final JoinType joinToManyType(Table<?> t) {
            RenderImplicitJoinType type = (RenderImplicitJoinType) StringUtils.defaultIfNull(Tools.settings(this.ctx.configuration()).getRenderImplicitJoinToManyType(), RenderImplicitJoinType.DEFAULT);
            switch (type) {
                case INNER_JOIN:
                    return JoinType.JOIN;
                case LEFT_JOIN:
                    return JoinType.LEFT_OUTER_JOIN;
                case THROW:
                case DEFAULT:
                default:
                    if (!allInScope(t)) {
                        throw new DataAccessException("Implicit to-many JOIN of " + this.ctx.dsl().renderContext().declareTables(true).render(this.table) + " isn't supported with Settings.renderImplicitJoinToManyType = " + String.valueOf(type) + ". Either change Settings value, or use explicit path join, see https://www.jooq.org/doc/latest/manual/sql-building/sql-statements/select-statement/explicit-path-join/");
                    }
                    return JoinType.LEFT_OUTER_JOIN;
            }
        }

        private final boolean allInScope(Table<?> t) {
            if (t instanceof TableImpl) {
                return this.ctx.inScope(t);
            }
            if (t instanceof JoinTable) {
                JoinTable<?> j = (JoinTable) t;
                return ((Boolean) Tools.traverseJoins(j, true, (Predicate<? super boolean>) b -> {
                    return !b.booleanValue();
                }, (BiFunction<? super boolean, ? super Table<?>, ? extends boolean>) (b2, x) -> {
                    return Boolean.valueOf(b2.booleanValue() && this.ctx.inScope(x));
                })).booleanValue();
            }
            return false;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public final boolean hasJoinPaths() {
            return (this.pathsToOne.isEmpty() && this.pathsToMany.isEmpty()) ? false : true;
        }

        public String toString() {
            return joinTree().toString();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractContext$ScopeStackElement.class */
    public static abstract class ScopeStackElement {
        final int scopeLevel;
        final QueryPart part;
        QueryPart mapped;
        int[] positions;
        int bindIndex;
        int indent;
        JoinNode joinNode;

        ScopeStackElement(QueryPart part, int scopeLevel) {
            this.part = part;
            this.mapped = part;
            this.scopeLevel = scopeLevel;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractContext$AbstractScopeStackPart.class */
    static abstract class AbstractScopeStackPart extends AbstractQueryPart implements QOM.UEmpty {
        AbstractScopeStackPart() {
        }

        @Override // org.jooq.QueryPartInternal
        public final void accept(Context<?> ctx) {
        }

        @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
        public boolean equals(Object that) {
            return this == that;
        }

        @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
        public int hashCode() {
            return 0;
        }

        @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
        public String toString() {
            return getClass().getSimpleName();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractContext$DataKeyScopeStackPart.class */
    public static final class DataKeyScopeStackPart extends AbstractScopeStackPart {
        static final DataKeyScopeStackPart INSTANCE = new DataKeyScopeStackPart();

        private DataKeyScopeStackPart() {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractContext$ScopeDefinerScopeStackPart.class */
    public static final class ScopeDefinerScopeStackPart extends AbstractScopeStackPart {
        static final ScopeDefinerScopeStackPart INSTANCE = new ScopeDefinerScopeStackPart();

        private ScopeDefinerScopeStackPart() {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractContext$DataKeyScopeStackElement.class */
    public static final class DataKeyScopeStackElement extends ScopeStackElement {
        List<Object> restoreDataKeys;

        DataKeyScopeStackElement(QueryPart part, int scopeLevel) {
            super(part, scopeLevel);
        }

        public String toString() {
            return "RestoreDataKeys [" + String.valueOf(this.restoreDataKeys) + "]";
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractContext$ScopeDefinerScopeStackElement.class */
    public static final class ScopeDefinerScopeStackElement extends ScopeStackElement {
        QueryPart scopeDefiner;

        ScopeDefinerScopeStackElement(QueryPart part, int scopeLevel) {
            super(part, scopeLevel);
        }

        public String toString() {
            return String.valueOf(this.scopeDefiner);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractContext$DefaultScopeStackElement.class */
    public static final class DefaultScopeStackElement extends ScopeStackElement {
        DefaultScopeStackElement(QueryPart part, int scopeLevel) {
            super(part, scopeLevel);
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            if (this.positions != null) {
                sb.append(Arrays.toString(this.positions));
            }
            sb.append(this.part);
            if (this.mapped != null) {
                sb.append(" (" + String.valueOf(this.mapped) + ")");
            }
            return sb.toString();
        }
    }
}
