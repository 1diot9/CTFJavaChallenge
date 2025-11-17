package org.jooq.impl;

import java.sql.NClob;
import java.sql.SQLException;
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
import org.jooq.ResourceManagingScope;
import org.jooq.SQLDialect;
import org.jooq.tools.jdbc.JDBCUtils;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/NClobBinding.class */
public class NClobBinding implements Binding<String, String> {

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.jooq.impl.NClobBinding$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/NClobBinding$1.class */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jooq$SQLDialect = new int[SQLDialect.values().length];
    }

    @Override // org.jooq.Binding
    public final Converter<String, String> converter() {
        return Converters.identity(String.class);
    }

    @Override // org.jooq.Binding
    public final void sql(BindingSQLContext<String> ctx) throws SQLException {
        ctx.render().visit((Field<?>) DSL.val(ctx.value(), SQLDataType.NCLOB));
    }

    @Override // org.jooq.Binding
    public final void register(BindingRegisterContext<String> ctx) throws SQLException {
        ctx.statement().registerOutParameter(ctx.index(), 2011);
    }

    @Override // org.jooq.Binding
    public final void set(BindingSetStatementContext<String> ctx) throws SQLException {
        ctx.statement().setClob(ctx.index(), newNClob(ctx, ctx.value()));
    }

    @Override // org.jooq.Binding
    public final void set(BindingSetSQLOutputContext<String> ctx) throws SQLException {
        ctx.output().writeClob(newNClob(ctx, ctx.value()));
    }

    @Override // org.jooq.Binding
    public final void get(BindingGetResultSetContext<String> ctx) throws SQLException {
        String subString;
        NClob clob = ctx.resultSet().getNClob(ctx.index());
        if (clob == null) {
            subString = null;
        } else {
            try {
                subString = clob.getSubString(1L, Tools.asInt(clob.length()));
            } finally {
                JDBCUtils.safeFree(clob);
            }
        }
        ctx.value(subString);
    }

    @Override // org.jooq.Binding
    public final void get(BindingGetStatementContext<String> ctx) throws SQLException {
        String subString;
        NClob clob = ctx.statement().getNClob(ctx.index());
        if (clob == null) {
            subString = null;
        } else {
            try {
                subString = clob.getSubString(1L, Tools.asInt(clob.length()));
            } finally {
                JDBCUtils.safeFree(clob);
            }
        }
        ctx.value(subString);
    }

    @Override // org.jooq.Binding
    public final void get(BindingGetSQLInputContext<String> ctx) throws SQLException {
        String subString;
        NClob clob = ctx.input().readNClob();
        if (clob == null) {
            subString = null;
        } else {
            try {
                subString = clob.getSubString(1L, Tools.asInt(clob.length()));
            } finally {
                JDBCUtils.safeFree(clob);
            }
        }
        ctx.value(subString);
    }

    static final NClob newNClob(ResourceManagingScope scope, String string) throws SQLException {
        switch (AnonymousClass1.$SwitchMap$org$jooq$SQLDialect[scope.dialect().ordinal()]) {
            default:
                NClob clob = DefaultExecuteContext.localConnection().createNClob();
                scope.autoFree(clob);
                clob.setString(1L, string);
                return clob;
        }
    }
}
