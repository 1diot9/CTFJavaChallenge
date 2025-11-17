package org.jooq.impl;

import java.sql.Connection;
import org.jetbrains.annotations.NotNull;
import org.jooq.ConnectionProvider;
import org.jooq.exception.DataAccessException;
import org.jooq.tools.JooqLogger;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultConnectionProvider.class */
public class DefaultConnectionProvider implements ConnectionProvider {
    private static final JooqLogger log = JooqLogger.getLogger((Class<?>) DefaultConnectionProvider.class);
    Connection connection;

    public DefaultConnectionProvider(Connection connection) {
        this.connection = connection;
    }

    @Override // org.jooq.ConnectionProvider
    @NotNull
    public final Connection acquire() {
        return this.connection;
    }

    @Override // org.jooq.ConnectionProvider
    public final void release(Connection released) {
    }

    public final void setConnection(Connection connection) {
        this.connection = connection;
    }

    public final void commit() throws DataAccessException {
        try {
            log.debug("commit");
            this.connection.commit();
        } catch (Exception e) {
            throw new DataAccessException("Cannot commit transaction", e);
        }
    }

    public final void rollback() throws DataAccessException {
        try {
            log.debug("rollback");
            this.connection.rollback();
        } catch (Exception e) {
            throw new DataAccessException("Cannot rollback transaction", e);
        }
    }

    public final void rollback(java.sql.Savepoint savepoint) throws DataAccessException {
        try {
            log.debug("rollback to savepoint");
            this.connection.rollback(savepoint);
        } catch (Exception e) {
            throw new DataAccessException("Cannot rollback transaction", e);
        }
    }

    @NotNull
    public final java.sql.Savepoint setSavepoint() throws DataAccessException {
        try {
            log.debug("set savepoint");
            return this.connection.setSavepoint();
        } catch (Exception e) {
            throw new DataAccessException("Cannot set savepoint", e);
        }
    }

    @NotNull
    public final java.sql.Savepoint setSavepoint(String name) throws DataAccessException {
        try {
            log.debug("set savepoint", name);
            return this.connection.setSavepoint(name);
        } catch (Exception e) {
            throw new DataAccessException("Cannot set savepoint", e);
        }
    }

    public final void releaseSavepoint(java.sql.Savepoint savepoint) throws DataAccessException {
        try {
            log.debug("release savepoint");
            this.connection.releaseSavepoint(savepoint);
        } catch (Exception e) {
            throw new DataAccessException("Cannot release savepoint", e);
        }
    }

    public final void setReadOnly(boolean readOnly) throws DataAccessException {
        try {
            log.debug("setting read only", Boolean.valueOf(readOnly));
            this.connection.setReadOnly(readOnly);
        } catch (Exception e) {
            throw new DataAccessException("Cannot set readOnly", e);
        }
    }

    public final boolean isReadOnly() throws DataAccessException {
        try {
            return this.connection.isReadOnly();
        } catch (Exception e) {
            throw new DataAccessException("Cannot get readOnly", e);
        }
    }

    public final void setAutoCommit(boolean autoCommit) throws DataAccessException {
        try {
            log.debug("setting auto commit", Boolean.valueOf(autoCommit));
            this.connection.setAutoCommit(autoCommit);
        } catch (Exception e) {
            throw new DataAccessException("Cannot set autoCommit", e);
        }
    }

    public final boolean getAutoCommit() throws DataAccessException {
        try {
            return this.connection.getAutoCommit();
        } catch (Exception e) {
            throw new DataAccessException("Cannot get autoCommit", e);
        }
    }

    public final void setHoldability(int holdability) throws DataAccessException {
        try {
            log.debug("setting holdability", Integer.valueOf(holdability));
            this.connection.setHoldability(holdability);
        } catch (Exception e) {
            throw new DataAccessException("Cannot set holdability", e);
        }
    }

    public final int getHoldability() throws DataAccessException {
        try {
            return this.connection.getHoldability();
        } catch (Exception e) {
            throw new DataAccessException("Cannot get holdability", e);
        }
    }

    public final void setTransactionIsolation(int level) throws DataAccessException {
        try {
            log.debug("setting tx isolation", Integer.valueOf(level));
            this.connection.setTransactionIsolation(level);
        } catch (Exception e) {
            throw new DataAccessException("Cannot set transactionIsolation", e);
        }
    }

    public final int getTransactionIsolation() throws DataAccessException {
        try {
            return this.connection.getTransactionIsolation();
        } catch (Exception e) {
            throw new DataAccessException("Cannot get transactionIsolation", e);
        }
    }
}
