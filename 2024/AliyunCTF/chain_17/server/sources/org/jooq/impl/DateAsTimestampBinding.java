package org.jooq.impl;

import java.sql.SQLException;
import java.sql.Timestamp;
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

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DateAsTimestampBinding.class */
public class DateAsTimestampBinding implements Binding<Timestamp, Timestamp> {
    private final Converter<Timestamp, Timestamp> converter = Converters.identity(Timestamp.class);
    private final Binding<Timestamp, Timestamp> delegate = DefaultBinding.binding(this.converter);

    @Override // org.jooq.Binding
    public final Converter<Timestamp, Timestamp> converter() {
        return this.converter;
    }

    @Override // org.jooq.Binding
    public final void sql(BindingSQLContext<Timestamp> ctx) throws SQLException {
        this.delegate.sql(ctx);
    }

    @Override // org.jooq.Binding
    public final void register(BindingRegisterContext<Timestamp> ctx) throws SQLException {
        this.delegate.register(ctx);
    }

    @Override // org.jooq.Binding
    public final void set(BindingSetStatementContext<Timestamp> ctx) throws SQLException {
        this.delegate.set(ctx);
    }

    @Override // org.jooq.Binding
    public final void set(BindingSetSQLOutputContext<Timestamp> ctx) throws SQLException {
        this.delegate.set(ctx);
    }

    @Override // org.jooq.Binding
    public final void get(BindingGetResultSetContext<Timestamp> ctx) throws SQLException {
        this.delegate.get(ctx);
    }

    @Override // org.jooq.Binding
    public final void get(BindingGetStatementContext<Timestamp> ctx) throws SQLException {
        this.delegate.get(ctx);
    }

    @Override // org.jooq.Binding
    public final void get(BindingGetSQLInputContext<Timestamp> ctx) throws SQLException {
        this.delegate.get(ctx);
    }
}
