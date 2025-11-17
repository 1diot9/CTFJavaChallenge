package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/RegexpReplace.class */
public final class RegexpReplace extends AbstractField<String> implements QOM.UNotYetImplemented {
    private final Field<String> field;
    private final Field<String> pattern;
    private final Field<String> replacement;
    private final boolean all;

    /* JADX INFO: Access modifiers changed from: package-private */
    public RegexpReplace(Field<String> field, Field<String> pattern, Field<String> replacement, boolean all) {
        super(Names.N_REGEXP_REPLACE, field.getDataType());
        this.field = field;
        this.pattern = pattern;
        this.replacement = replacement;
        this.all = all;
    }

    /* JADX WARN: Type inference failed for: r0v17, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v3, types: [org.jooq.Context] */
    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case POSTGRES:
            case YUGABYTEDB:
                ctx.visit(Names.N_REGEXP_REPLACE).sql('(').visit((Field<?>) this.field).sql(", ").visit((Field<?>) this.pattern).sql(", ").visit((Field<?>) this.replacement);
                if (this.all) {
                    ctx.sql(", 'g')");
                    return;
                } else {
                    ctx.sql(')');
                    return;
                }
            case H2:
            case HSQLDB:
            case MARIADB:
            default:
                ctx.visit(Names.N_REGEXP_REPLACE).sql('(').visit((Field<?>) this.field).sql(", ").visit((Field<?>) this.pattern).sql(", ").visit((Field<?>) this.replacement);
                if (this.all) {
                    ctx.sql(')');
                    return;
                } else {
                    ctx.sql(", 1, 1)");
                    return;
                }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String replacement(Context<?> ctx, int group) {
        switch (ctx.family()) {
            case MYSQL:
                return "$" + group;
            default:
                return "\\" + group;
        }
    }
}
