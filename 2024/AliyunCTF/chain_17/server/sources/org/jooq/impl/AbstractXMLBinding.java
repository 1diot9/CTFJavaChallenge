package org.jooq.impl;

import java.sql.SQLException;
import org.jooq.Binding;
import org.jooq.BindingGetResultSetContext;
import org.jooq.BindingGetSQLInputContext;
import org.jooq.BindingGetStatementContext;
import org.jooq.BindingRegisterContext;
import org.jooq.BindingSQLContext;
import org.jooq.BindingSetSQLOutputContext;
import org.jooq.BindingSetStatementContext;
import org.jooq.Field;
import org.jooq.XML;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractXMLBinding.class */
abstract class AbstractXMLBinding<T> implements Binding<XML, T> {
    @Override // org.jooq.Binding
    public final void sql(BindingSQLContext<T> bindingSQLContext) throws SQLException {
        bindingSQLContext.render().visit((Field<?>) DSL.val((XML) bindingSQLContext.convert(converter()).value()));
    }

    @Override // org.jooq.Binding
    public final void register(BindingRegisterContext<T> ctx) throws SQLException {
        ctx.statement().registerOutParameter(ctx.index(), 12);
    }

    @Override // org.jooq.Binding
    public final void set(BindingSetStatementContext<T> ctx) throws SQLException {
        ctx.statement().setString(ctx.index(), (String) org.jooq.tools.Convert.convert(ctx.convert(converter()).value(), String.class));
    }

    @Override // org.jooq.Binding
    public final void get(BindingGetResultSetContext<T> bindingGetResultSetContext) throws SQLException {
        bindingGetResultSetContext.convert(converter()).value(XML.xmlOrNull(bindingGetResultSetContext.resultSet().getString(bindingGetResultSetContext.index())));
    }

    @Override // org.jooq.Binding
    public final void get(BindingGetStatementContext<T> bindingGetStatementContext) throws SQLException {
        bindingGetStatementContext.convert(converter()).value(XML.xmlOrNull(bindingGetStatementContext.statement().getString(bindingGetStatementContext.index())));
    }

    @Override // org.jooq.Binding
    public final void set(BindingSetSQLOutputContext<T> ctx) throws SQLException {
        ctx.output().writeString((String) org.jooq.tools.Convert.convert(ctx.convert(converter()).value(), String.class));
    }

    @Override // org.jooq.Binding
    public final void get(BindingGetSQLInputContext<T> bindingGetSQLInputContext) throws SQLException {
        bindingGetSQLInputContext.convert(converter()).value(XML.xmlOrNull(bindingGetSQLInputContext.input().readString()));
    }
}
