package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function3;
import org.jooq.impl.QOM;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/Lpad.class */
public final class Lpad extends AbstractField<String> implements QOM.Lpad {
    final Field<String> string;
    final Field<? extends Number> length;
    final Field<String> character;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Lpad(Field<String> string, Field<? extends Number> length) {
        super(Names.N_LPAD, Tools.allNotNull(SQLDataType.VARCHAR, string, length));
        this.string = Tools.nullSafeNotNull(string, SQLDataType.VARCHAR);
        this.length = Tools.nullSafeNotNull(length, SQLDataType.INTEGER);
        this.character = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Lpad(Field<String> string, Field<? extends Number> length, Field<String> character) {
        super(Names.N_LPAD, Tools.allNotNull(SQLDataType.VARCHAR, string, length, character));
        this.string = Tools.nullSafeNotNull(string, SQLDataType.VARCHAR);
        this.length = Tools.nullSafeNotNull(length, SQLDataType.INTEGER);
        this.character = Tools.nullSafeNotNull(character, SQLDataType.VARCHAR);
    }

    private final Field<String> characterOrBlank() {
        return this.character == null ? DSL.inline(" ") : this.character;
    }

    /* JADX WARN: Type inference failed for: r0v5, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case SQLITE:
                ctx.visit(Names.N_SUBSTR).sql('(').visit(Names.N_REPLACE).sql('(').visit(Names.N_HEX).sql('(').visit(Names.N_ZEROBLOB).sql('(').visit((Field<?>) this.length).sql(")), '00', ").visit((Field<?>) characterOrBlank()).sql("), 1, ").visit((Field<?>) this.length).sql(" - ").visit(Names.N_LENGTH).sql('(').visit((Field<?>) this.string).sql(")) || ").visit((Field<?>) this.string);
                return;
            default:
                ctx.visit(DSL.function(Names.N_LPAD, getDataType(), (Field<?>[]) new Field[]{this.string, this.length, characterOrBlank()}));
                return;
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator3
    public final Field<String> $arg1() {
        return this.string;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator3
    public final Field<? extends Number> $arg2() {
        return this.length;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.jooq.impl.QOM.UOperator3
    public final Field<String> $arg3() {
        return this.character;
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final QOM.Lpad $arg1(Field<String> newValue) {
        return $constructor().apply(newValue, $arg2(), $arg3());
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final QOM.Lpad $arg2(Field<? extends Number> newValue) {
        return $constructor().apply($arg1(), newValue, $arg3());
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final QOM.Lpad $arg3(Field<String> newValue) {
        return $constructor().apply($arg1(), $arg2(), newValue);
    }

    @Override // org.jooq.impl.QOM.UOperator3
    public final Function3<? super Field<String>, ? super Field<? extends Number>, ? super Field<String>, ? extends QOM.Lpad> $constructor() {
        return (a1, a2, a3) -> {
            return new Lpad(a1, a2, a3);
        };
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        if (!(that instanceof QOM.Lpad)) {
            return super.equals(that);
        }
        QOM.Lpad o = (QOM.Lpad) that;
        return StringUtils.equals($string(), o.$string()) && StringUtils.equals($length(), o.$length()) && StringUtils.equals($character(), o.$character());
    }
}
