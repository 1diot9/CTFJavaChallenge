package org.jooq.impl;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jooq.AggregateFunction;
import org.jooq.BindContext;
import org.jooq.Binding;
import org.jooq.Catalog;
import org.jooq.Clause;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.Converter;
import org.jooq.DSLContext;
import org.jooq.DataType;
import org.jooq.ExecuteContext;
import org.jooq.ExecuteListener;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.Package;
import org.jooq.Parameter;
import org.jooq.RenderContext;
import org.jooq.Result;
import org.jooq.Results;
import org.jooq.Routine;
import org.jooq.SQLDialect;
import org.jooq.Schema;
import org.jooq.conf.SettingsTools;
import org.jooq.conf.ThrowExceptions;
import org.jooq.exception.ControlFlowSignal;
import org.jooq.impl.QOM;
import org.jooq.impl.ResultsImpl;

@org.jooq.Internal
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractRoutine.class */
public abstract class AbstractRoutine<T> extends AbstractNamed implements Routine<T>, QOM.UNotYetImplemented {
    private static final Clause[] CLAUSES = {Clause.FIELD, Clause.FIELD_FUNCTION};
    private static final Set<SQLDialect> REQUIRE_SELECT_FROM = SQLDialect.supportedBy(SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB);
    private static final Set<SQLDialect> REQUIRE_DISAMBIGUATE_OVERLOADS = SQLDialect.supportedBy(SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB);
    private static final Set<SQLDialect> SUPPORT_NAMED_ARGUMENTS = SQLDialect.supportedBy(SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB);
    private final Schema schema;
    private final List<Parameter<?>> allParameters;
    private final List<Parameter<?>> inParameters;
    private final List<Parameter<?>> outParameters;
    private final DataType<T> type;
    private Parameter<T> returnParameter;
    private ResultsImpl results;
    private Boolean isSQLUsable;
    private boolean overloaded;
    private boolean hasUnnamedParameters;
    private final Map<Parameter<?>, Field<?>> inValues;
    private final Set<Parameter<?>> inValuesDefaulted;
    private final Set<Parameter<?>> inValuesNonDefaulted;
    private transient Field<T> function;
    private Configuration configuration;
    private final Map<Parameter<?>, Object> outValues;
    private final Map<Parameter<?>, Integer> resultIndexes;

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public /* bridge */ /* synthetic */ boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public /* bridge */ /* synthetic */ int hashCode() {
        return super.hashCode();
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.Named
    public /* bridge */ /* synthetic */ Name getQualifiedName() {
        return super.getQualifiedName();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public /* bridge */ /* synthetic */ String toString() {
        return super.toString();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public /* bridge */ /* synthetic */ boolean generatesCast() {
        return super.generatesCast();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public /* bridge */ /* synthetic */ boolean declaresCTE() {
        return super.declaresCTE();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public /* bridge */ /* synthetic */ boolean declaresWindows() {
        return super.declaresWindows();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public /* bridge */ /* synthetic */ boolean declaresTables() {
        return super.declaresTables();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public /* bridge */ /* synthetic */ boolean declaresFields() {
        return super.declaresFields();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public /* bridge */ /* synthetic */ boolean rendersContent(Context context) {
        return super.rendersContent(context);
    }

    protected AbstractRoutine(String name, Schema schema) {
        this(name, schema, null, null, null, null);
    }

    protected AbstractRoutine(String name, Schema schema, Package pkg) {
        this(name, schema, pkg, null, null, null);
    }

    protected AbstractRoutine(String name, Schema schema, DataType<T> type) {
        this(name, schema, null, type, null, null);
    }

    protected <X> AbstractRoutine(String name, Schema schema, DataType<X> type, Converter<X, T> converter) {
        this(name, schema, null, type, converter, null);
    }

    protected <X> AbstractRoutine(String name, Schema schema, DataType<X> type, Binding<X, T> binding) {
        this(name, schema, null, type, null, binding);
    }

    protected <X, Y> AbstractRoutine(String name, Schema schema, DataType<X> type, Converter<Y, T> converter, Binding<X, Y> binding) {
        this(name, schema, null, type, converter, binding);
    }

    protected AbstractRoutine(String name, Schema schema, Package pkg, DataType<T> type) {
        this(name, schema, pkg, type, null, null);
    }

    protected <X> AbstractRoutine(String name, Schema schema, Package pkg, DataType<X> type, Converter<X, T> converter) {
        this(name, schema, pkg, type, converter, null);
    }

    protected <X> AbstractRoutine(String name, Schema schema, Package pkg, DataType<X> type, Binding<X, T> binding) {
        this(name, schema, pkg, type, null, binding);
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected <X, Y> AbstractRoutine(String str, Schema schema, Package r9, DataType<X> dataType, Converter<Y, T> converter, Binding<X, Y> binding) {
        super(qualify(r9 != 0 ? r9 : schema, DSL.name(str)), CommentImpl.NO_COMMENT);
        DataType<T> dataType2;
        this.resultIndexes = new HashMap();
        this.schema = schema;
        this.allParameters = new ArrayList();
        this.inParameters = new ArrayList();
        this.outParameters = new ArrayList();
        this.results = new ResultsImpl(null);
        this.inValues = new HashMap();
        this.inValuesDefaulted = new HashSet();
        this.inValuesNonDefaulted = new HashSet();
        this.outValues = new HashMap();
        if (converter == null && binding == null) {
            dataType2 = (DataType<T>) dataType;
        } else {
            dataType2 = (DataType<T>) dataType.asConvertedDataType(DefaultBinding.newBinding(converter, dataType, binding));
        }
        this.type = dataType2;
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected final <N extends Number> void setNumber(Parameter<N> parameter, Number value) {
        setValue(parameter, (Number) Convert.convert(value, parameter.getType()));
    }

    protected final void setNumber(Parameter<? extends Number> parameter, Field<? extends Number> value) {
        setField(parameter, value);
    }

    @Override // org.jooq.Routine
    public final <Z> void setValue(Parameter<Z> parameter, Z value) {
        set(parameter, value);
    }

    @Override // org.jooq.Routine
    public final <Z> void set(Parameter<Z> parameter, Z value) {
        setField(parameter, DSL.val(value, parameter.getDataType()));
    }

    protected final void setField(Parameter<?> parameter, Field<?> value) {
        if (value == null) {
            setField(parameter, DSL.val((Object) null, parameter.getDataType()));
            return;
        }
        this.inValues.put(parameter, value);
        this.inValuesDefaulted.remove(parameter);
        this.inValuesNonDefaulted.add(parameter);
    }

    @Override // org.jooq.Attachable
    public final void attach(Configuration c) {
        this.configuration = c;
    }

    @Override // org.jooq.Attachable
    public final void detach() {
        attach(null);
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.Attachable
    public final Configuration configuration() {
        return this.configuration;
    }

    @Override // org.jooq.Routine
    public final int execute(Configuration c) {
        return ((Integer) Tools.attach(this, c, this::execute)).intValue();
    }

    @Override // org.jooq.Routine
    public final int execute() {
        Configuration config = Tools.configurationOrThrow(this);
        SQLDialect family = config.family();
        this.results.clear();
        this.outValues.clear();
        if (isSQLUsable() && REQUIRE_SELECT_FROM.contains(config.dialect())) {
            return executeSelectFromPOSTGRES();
        }
        if (this.type == null) {
            return executeCallableStatement();
        }
        switch (family) {
            case HSQLDB:
                if (SQLDataType.RESULT.equals(this.type.getSQLDataType())) {
                    return executeSelectFromHSQLDB();
                }
                break;
            case FIREBIRD:
            case H2:
                break;
            default:
                return executeCallableStatement();
        }
        return executeSelect();
    }

    private final int executeSelectFromHSQLDB() {
        DSLContext create = create(this.configuration);
        this.outValues.put(this.returnParameter, create.selectFrom(DSL.table((Field<?>) asField())).fetch());
        return 0;
    }

    private final int executeSelectFromPOSTGRES() {
        Result<?> result;
        DSLContext create = create(this.configuration);
        List<Field<?>> fields = new ArrayList<>(1 + this.outParameters.size());
        if (this.returnParameter != null) {
            fields.add(DSL.field(DSL.name(getName()), this.returnParameter.getDataType()));
        }
        for (Parameter<?> p : this.outParameters) {
            fields.add(DSL.field(DSL.name(p.getName()), p.getDataType()));
        }
        if (fields.size() == 1 && fields.get(0).getDataType().isUDT()) {
            result = create.select(DSL.field("row(t.*)", fields.get(0).getDataType())).from("{0} as t", asField()).fetch();
        } else {
            result = (fields.size() == 1 && fields.get(0).getDataType().isRecord()) ? create.select(asField()).fetch() : create.select(fields).from("{0}", asField()).fetch();
        }
        int i = 0;
        if (this.returnParameter != null) {
            i = 0 + 1;
            this.outValues.put(this.returnParameter, this.returnParameter.getDataType().convert(result.getValue(0, 0)));
        }
        for (Parameter<?> p2 : this.outParameters) {
            int i2 = i;
            i++;
            this.outValues.put(p2, p2.getDataType().convert(result.getValue(0, i2)));
        }
        return 0;
    }

    private final int executeSelect() {
        Field<T> field = asField();
        this.outValues.put(this.returnParameter, create(this.configuration).select(field).fetchOne(field));
        return 0;
    }

    private final int executeCallableStatement() {
        ExecuteContext ctx = new DefaultExecuteContext(this.configuration, this);
        ExecuteListener listener = ExecuteListeners.get(ctx);
        try {
            try {
                try {
                    try {
                        listener.start(ctx);
                        listener.renderStart(ctx);
                        ctx.sql(create(this.configuration).render(this));
                        listener.renderEnd(ctx);
                        listener.prepareStart(ctx);
                        if (ctx.statement() == null) {
                            ctx.statement(ctx.connection().prepareCall(ctx.sql()));
                        }
                        Tools.setFetchSize(ctx, 0);
                        listener.prepareEnd(ctx);
                        int t = SettingsTools.getQueryTimeout(0, ctx.settings());
                        if (t != 0) {
                            ctx.statement().setQueryTimeout(t);
                        }
                        listener.bindStart(ctx);
                        new DefaultBindContext(this.configuration, ctx, ctx.statement()).visit(this);
                        registerOutParameters(ctx);
                        listener.bindEnd(ctx);
                        SQLException e = execute0(ctx, listener);
                        if (ctx.family() != SQLDialect.FIREBIRD) {
                            Tools.consumeResultSets(ctx, listener, this.results, null, e);
                        }
                        listener.outStart(ctx);
                        fetchOutParameters(ctx);
                        listener.outEnd(ctx);
                        Tools.safeClose(listener, ctx);
                        return 0;
                    } catch (SQLException e2) {
                        ctx.sqlException(e2);
                        listener.exception(ctx);
                        throw ctx.exception();
                    }
                } catch (ControlFlowSignal e3) {
                    throw e3;
                }
            } catch (RuntimeException e4) {
                ctx.exception(e4);
                listener.exception(ctx);
                throw ctx.exception();
            }
        } catch (Throwable th) {
            Tools.safeClose(listener, ctx);
            throw th;
        }
    }

    private final SQLException execute0(ExecuteContext ctx, ExecuteListener listener) throws SQLException {
        listener.executeStart(ctx);
        SQLException e = Tools.executeStatementAndGetFirstResultSet(ctx, 0);
        listener.executeEnd(ctx);
        if (e != null) {
            this.results.resultsOrRows().add(new ResultsImpl.ResultOrRowsImpl(Tools.translate(ctx.sql(), e)));
        }
        return e;
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (ctx instanceof RenderContext) {
            toSQL0((RenderContext) ctx);
        } else {
            bind0((BindContext) ctx);
        }
    }

    final void bind0(BindContext context) {
        List<Parameter<?>> all = getParameters0(context.configuration());
        List<Parameter<?>> in = getInParameters0(context.configuration());
        for (Parameter<?> parameter : all) {
            if (!in.contains(parameter) || !this.inValuesDefaulted.contains(parameter)) {
                bind1(context, parameter, getInValues().get(parameter) != null, resultParameter(context.configuration(), parameter));
            }
        }
    }

    private final void bind1(BindContext context, Parameter<?> parameter, boolean bindAsIn, boolean bindAsOut) {
        int index = context.peekIndex();
        if (bindAsOut) {
            this.resultIndexes.put(parameter, Integer.valueOf(index));
        }
        if (bindAsIn) {
            context.visit(getInValues().get(parameter));
            if (index == context.peekIndex() && bindAsOut) {
                context.nextIndex();
                return;
            }
            return;
        }
        context.nextIndex();
    }

    final void toSQL0(RenderContext context) {
        toSQLDeclare(context);
        toSQLBegin(context);
        if (getReturnParameter0(context.configuration()) != null) {
            toSQLAssign(context);
        }
        toSQLCall(context);
        context.sql(" (");
        String separator = "";
        List<Parameter<?>> all = getParameters0(context.configuration());
        boolean defaulted = false;
        Map<Integer, Parameter<?>> indexes = new LinkedHashMap<>();
        for (int i = 0; i < all.size(); i++) {
            Parameter<?> parameter = all.get(i);
            if (!parameter.equals(getReturnParameter0(context.configuration()))) {
                if (this.inValuesDefaulted.contains(parameter)) {
                    boolean z = defaulted | true;
                    defaulted = z;
                    if (z) {
                    }
                }
                indexes.put(Integer.valueOf(i), parameter);
            }
        }
        if (0 != 0) {
            context.formatIndentStart().formatNewLine();
        }
        int i2 = 0;
        for (Map.Entry<Integer, Parameter<?>> entry : indexes.entrySet()) {
            Parameter<?> parameter2 = entry.getValue();
            int index = entry.getKey().intValue();
            context.sql(separator);
            if (0 != 0) {
                int i3 = i2;
                i2++;
                if (i3 > 0) {
                    context.formatNewLine();
                }
            }
            if (defaulted && context.family() == SQLDialect.POSTGRES) {
                context.visit(parameter2.getUnqualifiedName()).sql(" := ");
            }
            if (getOutParameters0(context.configuration()).contains(parameter2)) {
                toSQLOutParam(context, parameter2, index);
            } else {
                toSQLInParam(context, parameter2, index, getInValues().get(parameter2));
            }
            separator = ", ";
        }
        if (0 != 0) {
            context.formatIndentEnd().formatNewLine();
        }
        context.sql(')');
        toSQLEnd(context);
    }

    private final void toSQLEnd(RenderContext context) {
        if (isSQLUsable() || context.family() != SQLDialect.POSTGRES) {
            context.sql(" }");
        }
    }

    private final void toSQLDeclare(RenderContext context) {
    }

    private final void toSQLBegin(RenderContext context) {
        if (isSQLUsable() || context.family() != SQLDialect.POSTGRES) {
            context.sql("{ ");
        }
    }

    private final void toSQLAssign(RenderContext context) {
        context.sql("? = ");
    }

    private final void toSQLCall(RenderContext context) {
        context.sql("call ");
        context.visit(getQualifiedName(context));
    }

    private final void toSQLOutParam(RenderContext ctx, Parameter<?> parameter, int index) {
        ctx.sql('?');
    }

    private final void toSQLInParam(RenderContext ctx, Parameter<?> parameter, int index, Field<?> value) {
        ctx.visit(value);
    }

    private final Name getQualifiedName(Context<?> ctx) {
        Schema mapped;
        List<Name> list = new ArrayList<>();
        if (ctx.qualifySchema() && (mapped = Tools.getMappedSchema(ctx, getSchema())) != null && !"".equals(mapped.getName())) {
            list.addAll(Arrays.asList(mapped.getQualifiedName().parts()));
        }
        list.add(getUnqualifiedName());
        return DSL.name((Name[]) list.toArray(Tools.EMPTY_NAME));
    }

    private final void fetchOutParameters(ExecuteContext ctx) throws SQLException {
        for (Parameter<?> parameter : getParameters0(ctx.configuration())) {
            if (resultParameter(ctx.configuration(), parameter)) {
                try {
                    fetchOutParameter(ctx, parameter);
                } catch (SQLException e) {
                    if (ctx.settings().getThrowExceptions() != ThrowExceptions.THROW_NONE) {
                        throw e;
                    }
                }
            }
        }
    }

    private final <U> void fetchOutParameter(ExecuteContext ctx, Parameter<U> parameter) throws SQLException {
        DefaultBindingGetStatementContext<U> out = new DefaultBindingGetStatementContext<>(ctx, (CallableStatement) ctx.statement(), this.resultIndexes.get(parameter).intValue());
        parameter.getBinding().get(out);
        this.outValues.put(parameter, out.value());
    }

    private final void registerOutParameters(ExecuteContext ctx) throws SQLException {
        Configuration c = ctx.configuration();
        CallableStatement statement = (CallableStatement) ctx.statement();
        for (Parameter<?> parameter : getParameters0(ctx.configuration())) {
            if (resultParameter(c, parameter)) {
                registerOutParameter(ctx, statement, parameter);
            }
        }
    }

    private final <U> void registerOutParameter(ExecuteContext ctx, CallableStatement statement, Parameter<U> parameter) throws SQLException {
        parameter.getBinding().register(new DefaultBindingRegisterContext(ctx, statement, this.resultIndexes.get(parameter).intValue()));
    }

    @Override // org.jooq.Routine
    public final T getReturnValue() {
        if (this.returnParameter != null) {
            return (T) getValue(this.returnParameter);
        }
        return null;
    }

    @Override // org.jooq.Routine
    public final Results getResults() {
        return this.results;
    }

    @Override // org.jooq.Routine
    public final <Z> Z getValue(Parameter<Z> parameter) {
        return (Z) get(parameter);
    }

    @Override // org.jooq.Routine
    public final <Z> Z get(Parameter<Z> parameter) {
        return (Z) this.outValues.get(parameter);
    }

    @Override // org.jooq.Routine
    public final <Z> Z getInValue(Parameter<Z> parameter) {
        return (Z) this.inValues.get(parameter);
    }

    protected final Map<Parameter<?>, Field<?>> getInValues() {
        return this.inValues;
    }

    @Override // org.jooq.Routine
    public final List<Parameter<?>> getOutParameters() {
        return Collections.unmodifiableList(this.outParameters);
    }

    @Override // org.jooq.Routine
    public final List<Parameter<?>> getInParameters() {
        return Collections.unmodifiableList(this.inParameters);
    }

    @Override // org.jooq.Routine
    public final List<Parameter<?>> getParameters() {
        return Collections.unmodifiableList(this.allParameters);
    }

    private final List<Parameter<?>> getOutParameters0(Configuration c) {
        return getOutParameters();
    }

    private final List<Parameter<?>> getInParameters0(Configuration c) {
        return getInParameters();
    }

    private final List<Parameter<?>> getParameters0(Configuration c) {
        return getParameters();
    }

    @Override // org.jooq.Qualified
    public final Catalog getCatalog() {
        if (getSchema() == null) {
            return null;
        }
        return getSchema().getCatalog();
    }

    @Override // org.jooq.Qualified
    public final Schema getSchema() {
        return this.schema;
    }

    @Override // org.jooq.Routine
    public final Parameter<T> getReturnParameter() {
        return this.returnParameter;
    }

    private final Parameter<T> getReturnParameter0(Configuration c) {
        return getReturnParameter();
    }

    protected final void setOverloaded(boolean overloaded) {
        this.overloaded = overloaded;
    }

    protected final boolean isOverloaded() {
        return this.overloaded;
    }

    protected final void setSQLUsable(boolean isSQLUsable) {
        this.isSQLUsable = Boolean.valueOf(isSQLUsable);
    }

    protected final boolean isSQLUsable() {
        return !Boolean.FALSE.equals(this.isSQLUsable);
    }

    private final boolean pgArgNeedsCasting(Parameter<?> parameter) {
        return isOverloaded() || parameter.getType() == Byte.class || parameter.getType() == Short.class;
    }

    private final boolean hasUnnamedParameters() {
        return this.hasUnnamedParameters;
    }

    private final void addParameter(Parameter<?> parameter) {
        this.allParameters.add(parameter);
        this.hasUnnamedParameters |= parameter.isUnnamed();
    }

    private final boolean resultParameter(Configuration c, Parameter<?> parameter) {
        return parameter.equals(getReturnParameter0(c)) || getOutParameters0(c).contains(parameter);
    }

    protected final void addInParameter(Parameter<?> parameter) {
        addParameter(parameter);
        this.inParameters.add(parameter);
        this.inValues.put(parameter, DSL.val((Object) null, parameter.getDataType()));
        if (parameter.isDefaulted()) {
            this.inValuesDefaulted.add(parameter);
        } else {
            this.inValuesNonDefaulted.add(parameter);
        }
    }

    protected final void addInOutParameter(Parameter<?> parameter) {
        addInParameter(parameter);
        this.outParameters.add(parameter);
    }

    protected final void addOutParameter(Parameter<?> parameter) {
        addParameter(parameter);
        this.outParameters.add(parameter);
    }

    protected final void setReturnParameter(Parameter<T> parameter) {
        addParameter(parameter);
        this.returnParameter = parameter;
    }

    public final Field<T> asField() {
        if (this.function == null) {
            this.function = new RoutineField();
        }
        return this.function;
    }

    public final Field<T> asField(String alias) {
        return asField().as(alias);
    }

    public final AggregateFunction<T> asAggregateFunction() {
        Field<?>[] array = new Field[getInParameters().size()];
        int i = 0;
        for (Parameter<?> p : getInParameters()) {
            array[i] = getInValues().get(p);
            i++;
        }
        return new DefaultAggregateFunction(false, getQualifiedName(), (DataType) this.type, array);
    }

    @Deprecated
    protected static final <T> Parameter<T> createParameter(String name, DataType<T> type) {
        return createParameter(name, (DataType) type, false, (Converter) null, (Binding) null);
    }

    @Deprecated
    protected static final <T> Parameter<T> createParameter(String name, DataType<T> type, boolean isDefaulted) {
        return createParameter(name, type, isDefaulted, (Converter) null, (Binding) null);
    }

    @Deprecated
    protected static final <T, U> Parameter<U> createParameter(String name, DataType<T> type, boolean isDefaulted, Converter<T, U> converter) {
        return createParameter(name, type, isDefaulted, converter, (Binding) null);
    }

    @Deprecated
    protected static final <T, U> Parameter<U> createParameter(String name, DataType<T> type, boolean isDefaulted, Binding<T, U> binding) {
        return createParameter(name, type, isDefaulted, (Converter) null, binding);
    }

    @Deprecated
    protected static final <T, X, U> Parameter<U> createParameter(String name, DataType<T> type, boolean isDefaulted, Converter<X, U> converter, Binding<T, X> binding) {
        return createParameter(name, type, isDefaulted, false, converter, binding);
    }

    @Deprecated
    protected static final <T> Parameter<T> createParameter(String name, DataType<T> type, boolean isDefaulted, boolean isUnnamed) {
        return createParameter(name, type, isDefaulted, isUnnamed, null, null);
    }

    @Deprecated
    protected static final <T, U> Parameter<U> createParameter(String name, DataType<T> type, boolean isDefaulted, boolean isUnnamed, Converter<T, U> converter) {
        return createParameter(name, type, isDefaulted, isUnnamed, converter, null);
    }

    @Deprecated
    protected static final <T, U> Parameter<U> createParameter(String name, DataType<T> type, boolean isDefaulted, boolean isUnnamed, Binding<T, U> binding) {
        return createParameter(name, type, isDefaulted, isUnnamed, null, binding);
    }

    @Deprecated
    protected static final <T, X, U> Parameter<U> createParameter(String name, DataType<T> type, boolean isDefaulted, boolean isUnnamed, Converter<X, U> converter, Binding<T, X> binding) {
        return Internal.createParameter(name, type, isDefaulted, isUnnamed, converter, binding);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractRoutine$RoutineField.class */
    public class RoutineField extends AbstractField<T> implements QOM.UNotYetImplemented {
        /* JADX WARN: Illegal instructions before constructor call */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        RoutineField() {
            /*
                r4 = this;
                r0 = r4
                r1 = r5
                org.jooq.impl.AbstractRoutine.this = r1
                r0 = r4
                r1 = r5
                org.jooq.Name r1 = r1.getQualifiedName()
                r2 = r5
                org.jooq.DataType<T> r2 = r2.type
                if (r2 != 0) goto L17
                org.jooq.DataType<org.jooq.Result<org.jooq.Record>> r2 = org.jooq.impl.SQLDataType.RESULT
                goto L1b
            L17:
                r2 = r5
                org.jooq.DataType<T> r2 = r2.type
            L1b:
                r0.<init>(r1, r2)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.jooq.impl.AbstractRoutine.RoutineField.<init>(org.jooq.impl.AbstractRoutine):void");
        }

        @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
        public void accept(Context<?> ctx) {
            ctx.family();
            List<Field<?>> fields = new ArrayList<>(AbstractRoutine.this.getInParameters0(ctx.configuration()).size());
            DataType<T> dataType = getDataType();
            for (Parameter<?> parameter : AbstractRoutine.this.getInParameters0(ctx.configuration())) {
                if (!AbstractRoutine.this.inValuesDefaulted.contains(parameter)) {
                    if (AbstractRoutine.REQUIRE_DISAMBIGUATE_OVERLOADS.contains(ctx.dialect())) {
                        if (AbstractRoutine.this.hasUnnamedParameters() || !AbstractRoutine.SUPPORT_NAMED_ARGUMENTS.contains(ctx.dialect())) {
                            if (AbstractRoutine.this.pgArgNeedsCasting(parameter)) {
                                fields.add(new Cast<>(AbstractRoutine.this.getInValues().get(parameter), parameter.getDataType()));
                            } else {
                                fields.add(AbstractRoutine.this.getInValues().get(parameter));
                            }
                        } else if (AbstractRoutine.this.pgArgNeedsCasting(parameter)) {
                            fields.add(DSL.field("{0} := {1}", DSL.name(parameter.getName()), new Cast(AbstractRoutine.this.getInValues().get(parameter), parameter.getDataType())));
                        } else {
                            fields.add(DSL.field("{0} := {1}", DSL.name(parameter.getName()), AbstractRoutine.this.getInValues().get(parameter)));
                        }
                    } else {
                        fields.add(AbstractRoutine.this.getInValues().get(parameter));
                    }
                }
            }
            Field<?> result = new Function(0 != 0 ? DSL.name((String) null) : AbstractRoutine.this.getQualifiedName(ctx), dataType, false, (Field[]) fields.toArray(Tools.EMPTY_FIELD));
            if (Boolean.TRUE.equals(Tools.settings(ctx.configuration()).isRenderScalarSubqueriesForStoredFunctions())) {
                result = DSL.select(result).asField();
            }
            ctx.visit(result);
        }
    }

    @Override // org.jooq.Qualified
    public final Schema $schema() {
        return this.schema;
    }
}
