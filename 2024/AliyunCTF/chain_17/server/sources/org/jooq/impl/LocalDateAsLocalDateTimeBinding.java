package org.jooq.impl;

import java.sql.SQLException;
import java.time.LocalDateTime;
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

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/LocalDateAsLocalDateTimeBinding.class */
public class LocalDateAsLocalDateTimeBinding implements Binding<LocalDateTime, LocalDateTime> {
    private final Converter<LocalDateTime, LocalDateTime> converter = Converters.identity(LocalDateTime.class);
    private final Binding<LocalDateTime, LocalDateTime> delegate = DefaultBinding.binding(this.converter);

    @Override // org.jooq.Binding
    public final Converter<LocalDateTime, LocalDateTime> converter() {
        return this.converter;
    }

    @Override // org.jooq.Binding
    public final void sql(BindingSQLContext<LocalDateTime> ctx) throws SQLException {
        this.delegate.sql(ctx);
    }

    @Override // org.jooq.Binding
    public final void register(BindingRegisterContext<LocalDateTime> ctx) throws SQLException {
        this.delegate.register(ctx);
    }

    @Override // org.jooq.Binding
    public final void set(BindingSetStatementContext<LocalDateTime> ctx) throws SQLException {
        this.delegate.set(ctx);
    }

    @Override // org.jooq.Binding
    public final void set(BindingSetSQLOutputContext<LocalDateTime> ctx) throws SQLException {
        this.delegate.set(ctx);
    }

    @Override // org.jooq.Binding
    public final void get(BindingGetResultSetContext<LocalDateTime> ctx) throws SQLException {
        this.delegate.get(ctx);
    }

    @Override // org.jooq.Binding
    public final void get(BindingGetStatementContext<LocalDateTime> ctx) throws SQLException {
        this.delegate.get(ctx);
    }

    @Override // org.jooq.Binding
    public final void get(BindingGetSQLInputContext<LocalDateTime> ctx) throws SQLException {
        this.delegate.get(ctx);
    }
}
