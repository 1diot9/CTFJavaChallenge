package org.jooq.impl;

import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import org.jooq.Binding;
import org.jooq.BindingGetSQLInputContext;
import org.jooq.BindingGetStatementContext;
import org.jooq.BindingRegisterContext;
import org.jooq.BindingSQLContext;
import org.jooq.BindingSetSQLOutputContext;
import org.jooq.Field;
import org.jooq.conf.ParamType;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractBinding.class */
public abstract class AbstractBinding<T, U> implements Binding<T, U> {
    @Override // org.jooq.Binding
    public void sql(BindingSQLContext<U> ctx) throws SQLException {
        if (ctx.render().paramType() == ParamType.INLINED) {
            if (ctx.value() == null) {
                ctx.render().visit(Keywords.K_NULL);
                return;
            } else {
                sqlInline(ctx);
                return;
            }
        }
        sqlBind(ctx);
    }

    protected void sqlInline(BindingSQLContext<U> ctx) throws SQLException {
        ctx.render().visit((Field<?>) DSL.inline(String.valueOf(ctx.value())));
    }

    protected void sqlBind(BindingSQLContext<U> ctx) throws SQLException {
        ctx.render().sql(ctx.variable());
    }

    @Override // org.jooq.Binding
    public void register(BindingRegisterContext<U> ctx) throws SQLException {
        throw new SQLFeatureNotSupportedException("AbstractBinding::register");
    }

    @Override // org.jooq.Binding
    public void get(BindingGetStatementContext<U> ctx) throws SQLException {
        throw new SQLFeatureNotSupportedException("AbstractBinding::get");
    }

    @Override // org.jooq.Binding
    public void set(BindingSetSQLOutputContext<U> ctx) throws SQLException {
        throw new SQLFeatureNotSupportedException("AbstractBinding::set");
    }

    @Override // org.jooq.Binding
    public void get(BindingGetSQLInputContext<U> ctx) throws SQLException {
        throw new SQLFeatureNotSupportedException("AbstractBinding::get");
    }
}
