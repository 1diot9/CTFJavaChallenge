package org.jooq.impl;

import java.lang.Number;
import org.jooq.Catalog;
import org.jooq.Clause;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Keyword;
import org.jooq.Name;
import org.jooq.Record1;
import org.jooq.SQLDialect;
import org.jooq.Schema;
import org.jooq.Select;
import org.jooq.Sequence;
import org.jooq.impl.QOM;

@org.jooq.Internal
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/SequenceImpl.class */
public class SequenceImpl<T extends Number> extends AbstractTypedNamed<T> implements Sequence<T>, QOM.UNotYetImplemented {
    private static final Clause[] CLAUSES = {Clause.SEQUENCE, Clause.SEQUENCE_REFERENCE};
    private final boolean nameIsPlainSQL;
    private final Schema schema;
    private final Field<T> startWith;
    private final Field<T> incrementBy;
    private final Field<T> minvalue;
    private final Field<T> maxvalue;
    private final boolean cycle;
    private final Field<T> cache;
    private final SequenceFunction<T> currval;
    private final SequenceFunction<T> nextval;

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

    @Deprecated
    public SequenceImpl(String name, Schema schema, DataType<T> type) {
        this(name, schema, (DataType) type, false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SequenceImpl(String name, Schema schema, DataType<T> type, boolean nameIsPlainSQL) {
        this(DSL.name(name), schema, type, nameIsPlainSQL);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SequenceImpl(Name name, Schema schema, DataType<T> type, boolean nameIsPlainSQL) {
        this(name, schema, type, nameIsPlainSQL, null, null, null, null, false, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SequenceImpl(Name name, Schema schema, DataType<T> type, boolean nameIsPlainSQL, Field<T> startWith, Field<T> incrementBy, Field<T> minvalue, Field<T> maxvalue, boolean cycle, Field<T> cache) {
        super(qualify(schema, name), CommentImpl.NO_COMMENT, type);
        this.schema = schema;
        this.nameIsPlainSQL = nameIsPlainSQL;
        this.startWith = startWith;
        this.incrementBy = incrementBy;
        this.minvalue = minvalue;
        this.maxvalue = maxvalue;
        this.cycle = cycle;
        this.cache = cache;
        this.currval = new SequenceFunction<>(SequenceMethod.CURRVAL, this);
        this.nextval = new SequenceFunction<>(SequenceMethod.NEXTVAL, this);
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

    @Override // org.jooq.Sequence
    public final Field<T> getStartWith() {
        return this.startWith;
    }

    @Override // org.jooq.Sequence
    public final Field<T> getIncrementBy() {
        return this.incrementBy;
    }

    @Override // org.jooq.Sequence
    public final Field<T> getMinvalue() {
        return this.minvalue;
    }

    @Override // org.jooq.Sequence
    public final Field<T> getMaxvalue() {
        return this.maxvalue;
    }

    @Override // org.jooq.Sequence
    public final boolean getCycle() {
        return this.cycle;
    }

    @Override // org.jooq.Sequence
    public final Field<T> getCache() {
        return this.cache;
    }

    @Override // org.jooq.Sequence
    public final Field<T> currval() {
        return this.currval;
    }

    @Override // org.jooq.Sequence
    public final Field<T> nextval() {
        return this.nextval;
    }

    @Override // org.jooq.Sequence
    public final Select<Record1<T>> nextvals(int i) {
        return DSL.select(nextval().as(Names.N_NEXTVAL)).from(DSL.generateSeries(1, i).as(Names.N_GENERATE_SERIES));
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/SequenceImpl$SequenceMethod.class */
    private enum SequenceMethod {
        CURRVAL(Keywords.K_CURRVAL, Names.N_CURRVAL),
        NEXTVAL(Keywords.K_NEXTVAL, Names.N_NEXTVAL);

        final Keyword keyword;
        final Name name;

        SequenceMethod(Keyword keyword, Name name) {
            this.keyword = keyword;
            this.name = name;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/SequenceImpl$SequenceFunction.class */
    public static class SequenceFunction<T extends Number> extends AbstractField<T> implements QOM.UNotYetImplemented {
        private final SequenceMethod method;
        private final SequenceImpl<T> sequence;

        SequenceFunction(SequenceMethod method, SequenceImpl<T> sequence) {
            super(method.name, sequence.getDataType());
            this.method = method;
            this.sequence = sequence;
        }

        /* JADX WARN: Type inference failed for: r0v14, types: [org.jooq.Context] */
        /* JADX WARN: Type inference failed for: r0v21, types: [org.jooq.Context] */
        /* JADX WARN: Type inference failed for: r0v25, types: [org.jooq.Context] */
        /* JADX WARN: Type inference failed for: r0v30, types: [org.jooq.Context] */
        /* JADX WARN: Type inference failed for: r0v34, types: [org.jooq.Context] */
        /* JADX WARN: Type inference failed for: r0v38, types: [org.jooq.Context] */
        /* JADX WARN: Type inference failed for: r0v47, types: [org.jooq.Context] */
        /* JADX WARN: Type inference failed for: r0v50, types: [org.jooq.Context] */
        /* JADX WARN: Type inference failed for: r0v7, types: [org.jooq.Context] */
        @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
        public final void accept(Context<?> ctx) {
            Configuration configuration = ctx.configuration();
            SQLDialect family = configuration.family();
            switch (family) {
                case POSTGRES:
                case YUGABYTEDB:
                    ctx.visit(this.method.keyword).sql('(');
                    ctx.sql('\'').stringLiteral(true).visit(this.sequence).stringLiteral(false).sql('\'');
                    ctx.sql(')');
                    return;
                case CUBRID:
                    ctx.visit(this.sequence).sql('.');
                    if (this.method == SequenceMethod.NEXTVAL) {
                        ctx.visit(DSL.keyword("next_value"));
                        return;
                    } else {
                        ctx.visit(DSL.keyword("current_value"));
                        return;
                    }
                case DERBY:
                case FIREBIRD:
                case H2:
                case HSQLDB:
                case MARIADB:
                    if (this.method == SequenceMethod.NEXTVAL) {
                        ctx.visit(Keywords.K_NEXT_VALUE_FOR).sql(' ').visit(this.sequence);
                        return;
                    }
                    if (family == SQLDialect.HSQLDB || family == SQLDialect.H2) {
                        ctx.visit(Keywords.K_CURRENT_VALUE_FOR).sql(' ').visit(this.sequence);
                        return;
                    }
                    if (family == SQLDialect.MARIADB) {
                        ctx.visit(Keywords.K_PREVIOUS_VALUE_FOR).sql(' ').visit(this.sequence);
                        return;
                    } else if (family == SQLDialect.FIREBIRD) {
                        ctx.visit(Names.N_GEN_ID).sql('(').visit(this.sequence).sql(", 0)");
                        return;
                    } else {
                        ctx.visit(this.sequence).sql('.').visit(this.method.keyword);
                        return;
                    }
                default:
                    ctx.visit(this.sequence).sql('.').visit(this.method.keyword);
                    return;
            }
        }

        @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
        public boolean equals(Object that) {
            if (!(that instanceof SequenceFunction)) {
                return super.equals(that);
            }
            SequenceFunction<?> s = (SequenceFunction) that;
            return this.method == s.method && this.sequence.equals(s.sequence);
        }
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        QualifiedImpl.acceptMappedSchemaPrefix(ctx, getSchema());
        if (this.nameIsPlainSQL) {
            ctx.sql(getName());
        } else {
            ctx.visit(getUnqualifiedName());
        }
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }

    @Override // org.jooq.Qualified
    public final Schema $schema() {
        return this.schema;
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (that instanceof SequenceImpl) {
            SequenceImpl<?> s = (SequenceImpl) that;
            return getQualifiedName().equals(s.getQualifiedName());
        }
        return super.equals(that);
    }
}
