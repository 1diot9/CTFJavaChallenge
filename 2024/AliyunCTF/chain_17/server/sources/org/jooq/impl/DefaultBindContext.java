package org.jooq.impl;

import io.r2dbc.spi.R2dbcException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.jooq.BindContext;
import org.jooq.Binding;
import org.jooq.Configuration;
import org.jooq.ExecuteContext;
import org.jooq.Field;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultBindContext.class */
final class DefaultBindContext extends AbstractBindContext {
    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultBindContext(Configuration configuration, ExecuteContext ctx, PreparedStatement stmt) {
        super(configuration, ctx, stmt);
    }

    @Override // org.jooq.impl.AbstractBindContext
    protected final BindContext bindValue0(Object value, Field<?> field) throws SQLException {
        ExecuteContext simpleExecuteContext;
        int nextIndex = nextIndex();
        try {
            Binding<?, ?> binding = field.getBinding();
            if (executeContext() != null) {
                simpleExecuteContext = executeContext();
            } else {
                simpleExecuteContext = new SimpleExecuteContext(configuration(), data());
            }
            binding.set(new DefaultBindingSetStatementContext(simpleExecuteContext, this.stmt, nextIndex, value));
            return this;
        } catch (R2dbcException e) {
            throw new SQLException("Error while writing value at R2DBC bind index: " + (nextIndex - 1), e.getSqlState(), e.getErrorCode(), e);
        } catch (SQLException e2) {
            throw new SQLException("Error while writing value at JDBC bind index: " + nextIndex, e2.getSQLState(), e2.getErrorCode(), e2);
        } catch (Exception e3) {
            throw new SQLException("Error while writing value at bind index: " + nextIndex, e3);
        }
    }
}
