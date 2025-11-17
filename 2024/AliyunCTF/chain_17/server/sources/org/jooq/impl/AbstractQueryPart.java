package org.jooq.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import org.jooq.Clause;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.DSLContext;
import org.jooq.QueryPart;
import org.jooq.QueryPartInternal;
import org.jooq.exception.DataAccessException;
import org.jooq.exception.SQLDialectNotSupportedException;
import org.jooq.tools.JooqLogger;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractQueryPart.class */
public abstract class AbstractQueryPart implements QueryPartInternal {
    private static final JooqLogger log = JooqLogger.getLogger(AbstractQueryPart.class, "serialization", 100);

    /* JADX INFO: Access modifiers changed from: package-private */
    public Configuration configuration() {
        return Tools.CONFIG.get();
    }

    @Override // org.jooq.QueryPartInternal
    @Deprecated
    public Clause[] clauses(Context<?> ctx) {
        return null;
    }

    @Override // org.jooq.QueryPartInternal
    public boolean rendersContent(Context<?> ctx) {
        return true;
    }

    @Override // org.jooq.QueryPartInternal
    public boolean declaresFields() {
        return false;
    }

    @Override // org.jooq.QueryPartInternal
    public boolean declaresTables() {
        return false;
    }

    @Override // org.jooq.QueryPartInternal
    public boolean declaresWindows() {
        return false;
    }

    @Override // org.jooq.QueryPartInternal
    public boolean declaresCTE() {
        return false;
    }

    @Override // org.jooq.QueryPartInternal
    public boolean generatesCast() {
        return false;
    }

    @Override // org.jooq.QueryPart
    public boolean equals(Object that) {
        DSLContext dSLContext;
        if (this == that) {
            return true;
        }
        if (that instanceof QueryPart) {
            QueryPart q = (QueryPart) that;
            DSLContext dsl1 = Tools.configuration(configuration()).dsl();
            if (that instanceof AbstractQueryPart) {
                AbstractQueryPart a = (AbstractQueryPart) that;
                dSLContext = Tools.configuration(a.configuration()).dsl();
            } else {
                dSLContext = dsl1;
            }
            DSLContext dsl2 = dSLContext;
            String sql1 = dsl1.renderInlined(this);
            String sql2 = dsl2.renderInlined(q);
            return sql1.equals(sql2);
        }
        return false;
    }

    @Override // org.jooq.QueryPart
    public int hashCode() {
        return create().renderInlined(this).hashCode();
    }

    @Override // org.jooq.QueryPart
    public String toString() {
        try {
            Configuration configuration = Tools.configuration(configuration());
            return create(configuration.deriveSettings(s -> {
                return s.withRenderFormatted(true);
            })).renderInlined(this);
        } catch (SQLDialectNotSupportedException e) {
            return "[ ... " + e.getMessage() + " ... ]";
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Deprecated
    public final DSLContext create() {
        return create(configuration());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Deprecated
    public final DSLContext create(Configuration configuration) {
        return DSL.using(configuration);
    }

    @Deprecated
    protected final DSLContext create(Context<?> ctx) {
        return DSL.using(ctx.configuration());
    }

    protected final DataAccessException translate(String sql, SQLException e) {
        return Tools.translate(sql, e);
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        if (log.isWarnEnabled()) {
            log.warn("DEPRECATION", "A QueryPart of type " + String.valueOf(getClass()) + " has been deserialised. Serialization support is deprecated in jOOQ. Please contact https://github.com/jOOQ/jOOQ/issues/11506 and state your use-case to see if it can be implemented otherwise.");
        }
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        if (log.isWarnEnabled()) {
            log.warn("DEPRECATION", "A QueryPart of type " + String.valueOf(getClass()) + " has been serialised. Serialization support is deprecated in jOOQ. Please contact https://github.com/jOOQ/jOOQ/issues/11506 and state your use-case to see if it can be implemented otherwise.");
        }
    }
}
