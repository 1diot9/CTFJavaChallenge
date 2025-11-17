package org.jooq.impl;

import java.util.Set;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function3;
import org.jooq.Name;
import org.jooq.Param;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/SetCommand.class */
public final class SetCommand extends AbstractDDLQuery implements QOM.SetCommand {
    final Name name;
    final Param<?> value;
    final boolean local;
    private static final Set<SQLDialect> NO_SUPPORT_BIND_VALUES = SQLDialect.supportedBy(SQLDialect.POSTGRES);

    /* JADX INFO: Access modifiers changed from: package-private */
    public SetCommand(Configuration configuration, Name name, Param<?> value, boolean local) {
        super(configuration);
        this.name = name;
        this.value = value;
        this.local = local;
    }

    /* JADX WARN: Type inference failed for: r0v10, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v5, types: [org.jooq.Context] */
    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        ctx.visit(Keywords.K_SET);
        if (this.local) {
            ctx.sql(' ').visit(Keywords.K_LOCAL);
        }
        ctx.sql(' ').visit(this.name).sql(" = ").paramTypeIf(ParamType.INLINED, NO_SUPPORT_BIND_VALUES.contains(ctx.dialect()), c -> {
            c.visit((Field<?>) this.value);
        });
    }

    @Override // org.jooq.impl.QOM.SetCommand
    public final Name $name() {
        return this.name;
    }

    @Override // org.jooq.impl.QOM.SetCommand
    public final Param<?> $value() {
        return this.value;
    }

    @Override // org.jooq.impl.QOM.SetCommand
    public final boolean $local() {
        return this.local;
    }

    @Override // org.jooq.impl.QOM.SetCommand
    public final QOM.SetCommand $name(Name newValue) {
        return $constructor().apply(newValue, $value(), Boolean.valueOf($local()));
    }

    @Override // org.jooq.impl.QOM.SetCommand
    public final QOM.SetCommand $value(Param<?> newValue) {
        return $constructor().apply($name(), newValue, Boolean.valueOf($local()));
    }

    @Override // org.jooq.impl.QOM.SetCommand
    public final QOM.SetCommand $local(boolean newValue) {
        return $constructor().apply($name(), $value(), Boolean.valueOf(newValue));
    }

    public final Function3<? super Name, ? super Param<?>, ? super Boolean, ? extends QOM.SetCommand> $constructor() {
        return (a1, a2, a3) -> {
            return new SetCommand(configuration(), a1, a2, a3.booleanValue());
        };
    }
}
