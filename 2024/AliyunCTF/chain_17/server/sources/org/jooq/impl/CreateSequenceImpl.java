package org.jooq.impl;

import java.util.Set;
import org.jooq.Clause;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.CreateSequenceFinalStep;
import org.jooq.CreateSequenceFlagsStep;
import org.jooq.Field;
import org.jooq.Function11;
import org.jooq.SQLDialect;
import org.jooq.Sequence;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/CreateSequenceImpl.class */
public final class CreateSequenceImpl extends AbstractDDLQuery implements QOM.CreateSequence, CreateSequenceFlagsStep, CreateSequenceFinalStep {
    final Sequence<?> sequence;
    final boolean ifNotExists;
    Field<? extends Number> startWith;
    Field<? extends Number> incrementBy;
    Field<? extends Number> minvalue;
    boolean noMinvalue;
    Field<? extends Number> maxvalue;
    boolean noMaxvalue;
    QOM.CycleOption cycle;
    Field<? extends Number> cache;
    boolean noCache;
    private static final Clause[] CLAUSES = {Clause.CREATE_SEQUENCE};
    private static final Set<SQLDialect> NO_SUPPORT_IF_NOT_EXISTS = SQLDialect.supportedUntil(SQLDialect.DERBY, SQLDialect.FIREBIRD);
    private static final Set<SQLDialect> REQUIRES_START_WITH = SQLDialect.supportedBy(SQLDialect.DERBY);
    private static final Set<SQLDialect> NO_SUPPORT_CACHE = SQLDialect.supportedBy(SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.HSQLDB);
    private static final Set<SQLDialect> NO_SEPARATOR = SQLDialect.supportedBy(SQLDialect.CUBRID, SQLDialect.MARIADB);
    private static final Set<SQLDialect> OMIT_NO_CACHE = SQLDialect.supportedBy(SQLDialect.FIREBIRD, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB);
    private static final Set<SQLDialect> OMIT_NO_CYCLE = SQLDialect.supportedBy(SQLDialect.FIREBIRD);
    private static final Set<SQLDialect> OMIT_NO_MINVALUE = SQLDialect.supportedBy(SQLDialect.FIREBIRD);
    private static final Set<SQLDialect> OMIT_NO_MAXVALUE = SQLDialect.supportedBy(SQLDialect.FIREBIRD);

    @Override // org.jooq.CreateSequenceFlagsStep
    public /* bridge */ /* synthetic */ CreateSequenceFlagsStep cache(Field field) {
        return cache((Field<? extends Number>) field);
    }

    @Override // org.jooq.CreateSequenceFlagsStep
    public /* bridge */ /* synthetic */ CreateSequenceFlagsStep maxvalue(Field field) {
        return maxvalue((Field<? extends Number>) field);
    }

    @Override // org.jooq.CreateSequenceFlagsStep
    public /* bridge */ /* synthetic */ CreateSequenceFlagsStep minvalue(Field field) {
        return minvalue((Field<? extends Number>) field);
    }

    @Override // org.jooq.CreateSequenceFlagsStep
    public /* bridge */ /* synthetic */ CreateSequenceFlagsStep incrementBy(Field field) {
        return incrementBy((Field<? extends Number>) field);
    }

    @Override // org.jooq.CreateSequenceFlagsStep
    public /* bridge */ /* synthetic */ CreateSequenceFlagsStep startWith(Field field) {
        return startWith((Field<? extends Number>) field);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CreateSequenceImpl(Configuration configuration, Sequence<?> sequence, boolean ifNotExists) {
        this(configuration, sequence, ifNotExists, null, null, null, false, null, false, null, null, false);
    }

    CreateSequenceImpl(Configuration configuration, Sequence<?> sequence, boolean ifNotExists, Field<? extends Number> startWith, Field<? extends Number> incrementBy, Field<? extends Number> minvalue, boolean noMinvalue, Field<? extends Number> maxvalue, boolean noMaxvalue, QOM.CycleOption cycle, Field<? extends Number> cache, boolean noCache) {
        super(configuration);
        this.sequence = sequence;
        this.ifNotExists = ifNotExists;
        this.startWith = startWith;
        this.incrementBy = incrementBy;
        this.minvalue = minvalue;
        this.noMinvalue = noMinvalue;
        this.maxvalue = maxvalue;
        this.noMaxvalue = noMaxvalue;
        this.cycle = cycle;
        this.cache = cache;
        this.noCache = noCache;
    }

    @Override // org.jooq.CreateSequenceFlagsStep
    public final CreateSequenceImpl startWith(Number startWith) {
        return startWith(Tools.field(startWith, this.sequence.getDataType()));
    }

    @Override // org.jooq.CreateSequenceFlagsStep
    public final CreateSequenceImpl startWith(Field<? extends Number> startWith) {
        this.startWith = startWith;
        return this;
    }

    @Override // org.jooq.CreateSequenceFlagsStep
    public final CreateSequenceImpl incrementBy(Number incrementBy) {
        return incrementBy(Tools.field(incrementBy, this.sequence.getDataType()));
    }

    @Override // org.jooq.CreateSequenceFlagsStep
    public final CreateSequenceImpl incrementBy(Field<? extends Number> incrementBy) {
        this.incrementBy = incrementBy;
        return this;
    }

    @Override // org.jooq.CreateSequenceFlagsStep
    public final CreateSequenceImpl minvalue(Number minvalue) {
        return minvalue(Tools.field(minvalue, this.sequence.getDataType()));
    }

    @Override // org.jooq.CreateSequenceFlagsStep
    public final CreateSequenceImpl minvalue(Field<? extends Number> minvalue) {
        this.minvalue = minvalue;
        return this;
    }

    @Override // org.jooq.CreateSequenceFlagsStep
    public final CreateSequenceImpl noMinvalue() {
        this.noMinvalue = true;
        return this;
    }

    @Override // org.jooq.CreateSequenceFlagsStep
    public final CreateSequenceImpl maxvalue(Number maxvalue) {
        return maxvalue(Tools.field(maxvalue, this.sequence.getDataType()));
    }

    @Override // org.jooq.CreateSequenceFlagsStep
    public final CreateSequenceImpl maxvalue(Field<? extends Number> maxvalue) {
        this.maxvalue = maxvalue;
        return this;
    }

    @Override // org.jooq.CreateSequenceFlagsStep
    public final CreateSequenceImpl noMaxvalue() {
        this.noMaxvalue = true;
        return this;
    }

    @Override // org.jooq.CreateSequenceFlagsStep
    public final CreateSequenceImpl cycle() {
        this.cycle = QOM.CycleOption.CYCLE;
        return this;
    }

    @Override // org.jooq.CreateSequenceFlagsStep
    public final CreateSequenceImpl noCycle() {
        this.cycle = QOM.CycleOption.NO_CYCLE;
        return this;
    }

    @Override // org.jooq.CreateSequenceFlagsStep
    public final CreateSequenceImpl cache(Number cache) {
        return cache(Tools.field(cache, this.sequence.getDataType()));
    }

    @Override // org.jooq.CreateSequenceFlagsStep
    public final CreateSequenceImpl cache(Field<? extends Number> cache) {
        this.cache = cache;
        return this;
    }

    @Override // org.jooq.CreateSequenceFlagsStep
    public final CreateSequenceImpl noCache() {
        this.noCache = true;
        return this;
    }

    private final boolean supportsIfNotExists(Context<?> ctx) {
        return !NO_SUPPORT_IF_NOT_EXISTS.contains(ctx.dialect());
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (this.ifNotExists && !supportsIfNotExists(ctx)) {
            Tools.tryCatch(ctx, DDLStatementType.CREATE_SEQUENCE, c -> {
                accept0(c);
            });
        } else {
            accept0(ctx);
        }
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v106, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v19, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v32, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v43, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v54, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v69, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v74, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v79, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v82, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v87, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v92, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v99, types: [org.jooq.Context] */
    private final void accept0(Context<?> ctx) {
        ctx.start(Clause.CREATE_SEQUENCE_SEQUENCE).visit(Keywords.K_CREATE).sql(' ').visit(ctx.family() == SQLDialect.CUBRID ? Keywords.K_SERIAL : Keywords.K_SEQUENCE).sql(' ');
        if (this.ifNotExists && supportsIfNotExists(ctx)) {
            ctx.visit(Keywords.K_IF_NOT_EXISTS).sql(' ');
        }
        ctx.visit(this.sequence);
        String noSeparator = NO_SEPARATOR.contains(ctx.dialect()) ? "" : " ";
        if (this.startWith == null && REQUIRES_START_WITH.contains(ctx.dialect())) {
            ctx.sql(' ').visit(Keywords.K_START_WITH).sql(" 1");
        } else if (this.startWith != null) {
            ctx.sql(' ').visit(Keywords.K_START_WITH).sql(' ').visit((Field<?>) this.startWith);
        }
        if (this.incrementBy != null) {
            ctx.sql(' ').visit(Keywords.K_INCREMENT_BY).sql(' ').visit((Field<?>) this.incrementBy);
        }
        if (this.minvalue != null) {
            ctx.sql(' ').visit(Keywords.K_MINVALUE).sql(' ').visit((Field<?>) this.minvalue);
        } else if (this.noMinvalue && !OMIT_NO_MINVALUE.contains(ctx.dialect())) {
            ctx.sql(' ').visit(Keywords.K_NO).sql(noSeparator).visit(Keywords.K_MINVALUE);
        }
        if (this.maxvalue != null) {
            ctx.sql(' ').visit(Keywords.K_MAXVALUE).sql(' ').visit((Field<?>) this.maxvalue);
        } else if (this.noMaxvalue && !OMIT_NO_MAXVALUE.contains(ctx.dialect())) {
            ctx.sql(' ').visit(Keywords.K_NO).sql(noSeparator).visit(Keywords.K_MAXVALUE);
        }
        if (this.cycle == QOM.CycleOption.CYCLE) {
            ctx.sql(' ').visit(Keywords.K_CYCLE);
        } else if (this.cycle == QOM.CycleOption.NO_CYCLE && !OMIT_NO_CYCLE.contains(ctx.dialect())) {
            ctx.sql(' ').visit(Keywords.K_NO).sql(noSeparator).visit(Keywords.K_CYCLE);
        }
        if (!NO_SUPPORT_CACHE.contains(ctx.dialect())) {
            if (this.cache != null) {
                ctx.sql(' ').visit(Keywords.K_CACHE).sql(' ').visit((Field<?>) this.cache);
            } else if (this.noCache && !OMIT_NO_CACHE.contains(ctx.dialect())) {
                ctx.sql(' ').visit(Keywords.K_NO).sql(noSeparator).visit(Keywords.K_CACHE);
            }
        }
        ctx.end(Clause.CREATE_SEQUENCE_SEQUENCE);
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }

    @Override // org.jooq.impl.QOM.CreateSequence
    public final Sequence<?> $sequence() {
        return this.sequence;
    }

    @Override // org.jooq.impl.QOM.CreateSequence
    public final boolean $ifNotExists() {
        return this.ifNotExists;
    }

    @Override // org.jooq.impl.QOM.CreateSequence
    public final Field<? extends Number> $startWith() {
        return this.startWith;
    }

    @Override // org.jooq.impl.QOM.CreateSequence
    public final Field<? extends Number> $incrementBy() {
        return this.incrementBy;
    }

    @Override // org.jooq.impl.QOM.CreateSequence
    public final Field<? extends Number> $minvalue() {
        return this.minvalue;
    }

    @Override // org.jooq.impl.QOM.CreateSequence
    public final boolean $noMinvalue() {
        return this.noMinvalue;
    }

    @Override // org.jooq.impl.QOM.CreateSequence
    public final Field<? extends Number> $maxvalue() {
        return this.maxvalue;
    }

    @Override // org.jooq.impl.QOM.CreateSequence
    public final boolean $noMaxvalue() {
        return this.noMaxvalue;
    }

    @Override // org.jooq.impl.QOM.CreateSequence
    public final QOM.CycleOption $cycle() {
        return this.cycle;
    }

    @Override // org.jooq.impl.QOM.CreateSequence
    public final Field<? extends Number> $cache() {
        return this.cache;
    }

    @Override // org.jooq.impl.QOM.CreateSequence
    public final boolean $noCache() {
        return this.noCache;
    }

    @Override // org.jooq.impl.QOM.CreateSequence
    public final QOM.CreateSequence $sequence(Sequence<?> newValue) {
        return $constructor().apply(newValue, Boolean.valueOf($ifNotExists()), $startWith(), $incrementBy(), $minvalue(), Boolean.valueOf($noMinvalue()), $maxvalue(), Boolean.valueOf($noMaxvalue()), $cycle(), $cache(), Boolean.valueOf($noCache()));
    }

    @Override // org.jooq.impl.QOM.CreateSequence
    public final QOM.CreateSequence $ifNotExists(boolean newValue) {
        return $constructor().apply($sequence(), Boolean.valueOf(newValue), $startWith(), $incrementBy(), $minvalue(), Boolean.valueOf($noMinvalue()), $maxvalue(), Boolean.valueOf($noMaxvalue()), $cycle(), $cache(), Boolean.valueOf($noCache()));
    }

    @Override // org.jooq.impl.QOM.CreateSequence
    public final QOM.CreateSequence $startWith(Field<? extends Number> newValue) {
        return $constructor().apply($sequence(), Boolean.valueOf($ifNotExists()), newValue, $incrementBy(), $minvalue(), Boolean.valueOf($noMinvalue()), $maxvalue(), Boolean.valueOf($noMaxvalue()), $cycle(), $cache(), Boolean.valueOf($noCache()));
    }

    @Override // org.jooq.impl.QOM.CreateSequence
    public final QOM.CreateSequence $incrementBy(Field<? extends Number> newValue) {
        return $constructor().apply($sequence(), Boolean.valueOf($ifNotExists()), $startWith(), newValue, $minvalue(), Boolean.valueOf($noMinvalue()), $maxvalue(), Boolean.valueOf($noMaxvalue()), $cycle(), $cache(), Boolean.valueOf($noCache()));
    }

    @Override // org.jooq.impl.QOM.CreateSequence
    public final QOM.CreateSequence $minvalue(Field<? extends Number> newValue) {
        return $constructor().apply($sequence(), Boolean.valueOf($ifNotExists()), $startWith(), $incrementBy(), newValue, Boolean.valueOf($noMinvalue()), $maxvalue(), Boolean.valueOf($noMaxvalue()), $cycle(), $cache(), Boolean.valueOf($noCache()));
    }

    @Override // org.jooq.impl.QOM.CreateSequence
    public final QOM.CreateSequence $noMinvalue(boolean newValue) {
        return $constructor().apply($sequence(), Boolean.valueOf($ifNotExists()), $startWith(), $incrementBy(), $minvalue(), Boolean.valueOf(newValue), $maxvalue(), Boolean.valueOf($noMaxvalue()), $cycle(), $cache(), Boolean.valueOf($noCache()));
    }

    @Override // org.jooq.impl.QOM.CreateSequence
    public final QOM.CreateSequence $maxvalue(Field<? extends Number> newValue) {
        return $constructor().apply($sequence(), Boolean.valueOf($ifNotExists()), $startWith(), $incrementBy(), $minvalue(), Boolean.valueOf($noMinvalue()), newValue, Boolean.valueOf($noMaxvalue()), $cycle(), $cache(), Boolean.valueOf($noCache()));
    }

    @Override // org.jooq.impl.QOM.CreateSequence
    public final QOM.CreateSequence $noMaxvalue(boolean newValue) {
        return $constructor().apply($sequence(), Boolean.valueOf($ifNotExists()), $startWith(), $incrementBy(), $minvalue(), Boolean.valueOf($noMinvalue()), $maxvalue(), Boolean.valueOf(newValue), $cycle(), $cache(), Boolean.valueOf($noCache()));
    }

    @Override // org.jooq.impl.QOM.CreateSequence
    public final QOM.CreateSequence $cycle(QOM.CycleOption newValue) {
        return $constructor().apply($sequence(), Boolean.valueOf($ifNotExists()), $startWith(), $incrementBy(), $minvalue(), Boolean.valueOf($noMinvalue()), $maxvalue(), Boolean.valueOf($noMaxvalue()), newValue, $cache(), Boolean.valueOf($noCache()));
    }

    @Override // org.jooq.impl.QOM.CreateSequence
    public final QOM.CreateSequence $cache(Field<? extends Number> newValue) {
        return $constructor().apply($sequence(), Boolean.valueOf($ifNotExists()), $startWith(), $incrementBy(), $minvalue(), Boolean.valueOf($noMinvalue()), $maxvalue(), Boolean.valueOf($noMaxvalue()), $cycle(), newValue, Boolean.valueOf($noCache()));
    }

    @Override // org.jooq.impl.QOM.CreateSequence
    public final QOM.CreateSequence $noCache(boolean newValue) {
        return $constructor().apply($sequence(), Boolean.valueOf($ifNotExists()), $startWith(), $incrementBy(), $minvalue(), Boolean.valueOf($noMinvalue()), $maxvalue(), Boolean.valueOf($noMaxvalue()), $cycle(), $cache(), Boolean.valueOf(newValue));
    }

    public final Function11<? super Sequence<?>, ? super Boolean, ? super Field<? extends Number>, ? super Field<? extends Number>, ? super Field<? extends Number>, ? super Boolean, ? super Field<? extends Number>, ? super Boolean, ? super QOM.CycleOption, ? super Field<? extends Number>, ? super Boolean, ? extends QOM.CreateSequence> $constructor() {
        return (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11) -> {
            return new CreateSequenceImpl(configuration(), a1, a2.booleanValue(), a3, a4, a5, a6.booleanValue(), a7, a8.booleanValue(), a9, a10, a11.booleanValue());
        };
    }
}
