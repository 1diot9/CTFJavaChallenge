package org.jooq;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Binding.class */
public interface Binding<T, U> extends Serializable {
    @NotNull
    Converter<T, U> converter();

    void sql(BindingSQLContext<U> bindingSQLContext) throws SQLException;

    void register(BindingRegisterContext<U> bindingRegisterContext) throws SQLException;

    void set(BindingSetStatementContext<U> bindingSetStatementContext) throws SQLException;

    void set(BindingSetSQLOutputContext<U> bindingSetSQLOutputContext) throws SQLException;

    void get(BindingGetResultSetContext<U> bindingGetResultSetContext) throws SQLException;

    void get(BindingGetStatementContext<U> bindingGetStatementContext) throws SQLException;

    void get(BindingGetSQLInputContext<U> bindingGetSQLInputContext) throws SQLException;

    @NotNull
    static <T, U> Binding<T, U> of(Converter<T, U> converter, Consumer<? super BindingSQLContext<U>> sqlContext, Consumer<? super BindingGetResultSetContext<U>> getResultSetContext, Consumer<? super BindingSetStatementContext<U>> setStatementContext) {
        return of(converter, sqlContext, getResultSetContext, setStatementContext, ctx -> {
            throw new UnsupportedOperationException();
        }, ctx2 -> {
            throw new UnsupportedOperationException();
        }, ctx3 -> {
            throw new UnsupportedOperationException();
        }, ctx4 -> {
            throw new UnsupportedOperationException();
        });
    }

    @NotNull
    static <T, U> Binding<T, U> of(Converter<T, U> converter, Consumer<? super BindingSQLContext<U>> sqlContext, Consumer<? super BindingGetResultSetContext<U>> getResultSetContext, Consumer<? super BindingSetStatementContext<U>> setStatementContext, Consumer<? super BindingRegisterContext<U>> registerContext, Consumer<? super BindingGetStatementContext<U>> getStatementContext) {
        return of(converter, sqlContext, getResultSetContext, setStatementContext, registerContext, getStatementContext, ctx -> {
            throw new UnsupportedOperationException();
        }, ctx2 -> {
            throw new UnsupportedOperationException();
        });
    }

    @NotNull
    static <T, U> Binding<T, U> of(final Converter<T, U> converter, final Consumer<? super BindingSQLContext<U>> sqlContext, final Consumer<? super BindingGetResultSetContext<U>> getResultSetContext, final Consumer<? super BindingSetStatementContext<U>> setStatementContext, final Consumer<? super BindingRegisterContext<U>> registerContext, final Consumer<? super BindingGetStatementContext<U>> getStatementContext, final Consumer<? super BindingGetSQLInputContext<U>> getSqlInputContext, final Consumer<? super BindingSetSQLOutputContext<U>> setSqlOutputContext) {
        return new Binding<T, U>() { // from class: org.jooq.Binding.1
            @Override // org.jooq.Binding
            public final Converter<T, U> converter() {
                return Converter.this;
            }

            @Override // org.jooq.Binding
            public final void sql(BindingSQLContext<U> ctx) throws SQLException {
                sqlContext.accept(ctx);
            }

            @Override // org.jooq.Binding
            public final void register(BindingRegisterContext<U> ctx) throws SQLException {
                registerContext.accept(ctx);
            }

            @Override // org.jooq.Binding
            public final void set(BindingSetStatementContext<U> ctx) throws SQLException {
                setStatementContext.accept(ctx);
            }

            @Override // org.jooq.Binding
            public final void set(BindingSetSQLOutputContext<U> ctx) throws SQLException {
                setSqlOutputContext.accept(ctx);
            }

            @Override // org.jooq.Binding
            public final void get(BindingGetResultSetContext<U> ctx) throws SQLException {
                getResultSetContext.accept(ctx);
            }

            @Override // org.jooq.Binding
            public final void get(BindingGetStatementContext<U> ctx) throws SQLException {
                getStatementContext.accept(ctx);
            }

            @Override // org.jooq.Binding
            public final void get(BindingGetSQLInputContext<U> ctx) throws SQLException {
                getSqlInputContext.accept(ctx);
            }
        };
    }
}
