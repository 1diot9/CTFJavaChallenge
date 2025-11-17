package org.jooq.impl;

import java.io.Reader;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.Set;
import org.jooq.Binding;
import org.jooq.BindingGetResultSetContext;
import org.jooq.BindingGetSQLInputContext;
import org.jooq.BindingGetStatementContext;
import org.jooq.BindingRegisterContext;
import org.jooq.BindingSQLContext;
import org.jooq.BindingSetSQLOutputContext;
import org.jooq.BindingSetStatementContext;
import org.jooq.Converter;
import org.jooq.Converters;
import org.jooq.Field;
import org.jooq.SQLDialect;
import org.jooq.Scope;
import org.jooq.Source;
import org.jooq.conf.ParamType;
import org.jooq.tools.jdbc.JDBCUtils;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ClobBinding.class */
public class ClobBinding implements Binding<String, String> {
    static final Set<SQLDialect> NO_SUPPORT_NULL_LOBS = SQLDialect.supportedBy(SQLDialect.FIREBIRD, SQLDialect.HSQLDB);
    static final Set<SQLDialect> NO_SUPPORT_LOBS = SQLDialect.supportedBy(SQLDialect.POSTGRES, SQLDialect.SQLITE, SQLDialect.TRINO, SQLDialect.YUGABYTEDB);

    @Override // org.jooq.Binding
    public final Converter<String, String> converter() {
        return Converters.identity(String.class);
    }

    @Override // org.jooq.Binding
    public final void sql(BindingSQLContext<String> ctx) throws SQLException {
        if (ctx.render().paramType() == ParamType.INLINED) {
            ctx.render().visit((Field<?>) DSL.inline(ctx.convert(converter()).value(), SQLDataType.CLOB));
        } else {
            ctx.render().sql(ctx.variable());
        }
    }

    @Override // org.jooq.Binding
    public final void register(BindingRegisterContext<String> ctx) throws SQLException {
        if (!NO_SUPPORT_LOBS.contains(ctx.dialect())) {
            ctx.statement().registerOutParameter(ctx.index(), 2005);
        } else {
            ctx.statement().registerOutParameter(ctx.index(), 12);
        }
    }

    @Override // org.jooq.Binding
    public final void set(BindingSetStatementContext<String> ctx) throws SQLException {
        if (!NO_SUPPORT_LOBS.contains(ctx.dialect())) {
            Clob clob = newClob(ctx, ctx.value());
            if (clob == null && NO_SUPPORT_NULL_LOBS.contains(ctx.dialect())) {
                ctx.statement().setNull(ctx.index(), 2005);
                return;
            } else {
                ctx.statement().setClob(ctx.index(), clob);
                return;
            }
        }
        ctx.statement().setString(ctx.index(), ctx.value());
    }

    @Override // org.jooq.Binding
    public final void set(BindingSetSQLOutputContext<String> ctx) throws SQLException {
        if (!NO_SUPPORT_LOBS.contains(ctx.dialect())) {
            ctx.output().writeClob(newClob(ctx, ctx.value()));
        } else {
            ctx.output().writeString(ctx.value());
        }
    }

    @Override // org.jooq.Binding
    public final void get(BindingGetResultSetContext<String> ctx) throws SQLException {
        String read;
        if (!NO_SUPPORT_LOBS.contains(ctx.dialect())) {
            Clob clob = ctx.resultSet().getClob(ctx.index());
            if (clob == null) {
                read = null;
            } else {
                try {
                    read = read(ctx, clob);
                } finally {
                    JDBCUtils.safeFree(clob);
                }
            }
            ctx.value(read);
            return;
        }
        ctx.value(ctx.resultSet().getString(ctx.index()));
    }

    @Override // org.jooq.Binding
    public final void get(BindingGetStatementContext<String> ctx) throws SQLException {
        String read;
        if (!NO_SUPPORT_LOBS.contains(ctx.dialect())) {
            Clob clob = ctx.statement().getClob(ctx.index());
            if (clob == null) {
                read = null;
            } else {
                try {
                    read = read(ctx, clob);
                } finally {
                    JDBCUtils.safeFree(clob);
                }
            }
            ctx.value(read);
            return;
        }
        ctx.value(ctx.statement().getString(ctx.index()));
    }

    @Override // org.jooq.Binding
    public final void get(BindingGetSQLInputContext<String> ctx) throws SQLException {
        String read;
        if (!NO_SUPPORT_LOBS.contains(ctx.dialect())) {
            Clob clob = ctx.input().readClob();
            if (clob == null) {
                read = null;
            } else {
                try {
                    read = read(ctx, clob);
                } finally {
                    JDBCUtils.safeFree(clob);
                }
            }
            ctx.value(read);
            return;
        }
        ctx.value(ctx.input().readString());
    }

    static final String read(Scope ctx, Clob clob) throws SQLException {
        switch (ctx.family()) {
            case FIREBIRD:
            case SQLITE:
                Reader r = clob.getCharacterStream();
                if (r == null) {
                    return null;
                }
                return Source.of(r).readString();
            default:
                return clob.getSubString(1L, Tools.asInt(clob.length()));
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:7:0x001c, code lost:            r0 = org.jooq.impl.DefaultExecuteContext.localConnection().createClob();        r5.autoFree(r0);        r0.setString(1, r6);     */
    /* JADX WARN: Code restructure failed: missing block: B:8:0x0037, code lost:            return r0;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    static final java.sql.Clob newClob(org.jooq.ResourceManagingScope r5, java.lang.String r6) throws java.sql.SQLException {
        /*
            r0 = r6
            if (r0 != 0) goto L6
            r0 = 0
            return r0
        L6:
            int[] r0 = org.jooq.impl.ClobBinding.AnonymousClass1.$SwitchMap$org$jooq$SQLDialect
            r1 = r5
            org.jooq.SQLDialect r1 = r1.dialect()
            int r1 = r1.ordinal()
            r0 = r0[r1]
            switch(r0) {
                default: goto L1c;
            }
        L1c:
            java.sql.Connection r0 = org.jooq.impl.DefaultExecuteContext.localConnection()
            java.sql.Clob r0 = r0.createClob()
            r7 = r0
            r0 = r5
            r1 = r7
            java.sql.Clob r0 = r0.autoFree(r1)
            r0 = r7
            r1 = 1
            r2 = r6
            int r0 = r0.setString(r1, r2)
            r0 = r7
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jooq.impl.ClobBinding.newClob(org.jooq.ResourceManagingScope, java.lang.String):java.sql.Clob");
    }
}
