package org.jooq.impl;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Param;
import org.jooq.conf.Settings;
import org.jooq.conf.SettingsTools;
import org.jooq.exception.DataAccessException;
import org.jooq.exception.DetachedException;
import org.jooq.impl.DefaultRenderContext;
import org.jooq.tools.JooqLogger;
import org.jooq.tools.jdbc.DefaultConnection;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ParsingConnection.class */
public final class ParsingConnection extends DefaultConnection {
    private static final JooqLogger log = JooqLogger.getLogger((Class<?>) ParsingConnection.class);
    final Configuration configuration;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ParsingConnection(Configuration configuration) {
        super(configuration.connectionProvider().acquire());
        if (getDelegate() == null) {
            if (configuration.connectionFactory() instanceof NoConnectionFactory) {
                throw new DetachedException("ConnectionProvider did not provide a JDBC Connection");
            }
            throw new DetachedException("Attempt to use a ParsingConnection (JDBC) when only an R2BDC ConnectionFactory was configured. Using ParsingConnectionFactory instead.");
        }
        this.configuration = configuration;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ParsingConnection$CacheValue.class */
    public static final class CacheValue {
        final String output;
        final int bindSize;
        final Map<Integer, List<Integer>> bindMapping;

        CacheValue(Configuration configuration, String input, Param<?>[] bindValues) {
            DSLContext ctx = configuration.dsl();
            DefaultRenderContext render = (DefaultRenderContext) ctx.renderContext();
            render.paramType(configuration.settings().getParamType()).visit(ctx.parser().parseQuery(input, bindValues));
            this.output = render.render();
            this.bindSize = render.bindValues().size();
            this.bindMapping = new HashMap();
            for (int i = 0; i < bindValues.length; i++) {
                for (int j = 0; j < render.bindValues().size(); j++) {
                    if (bindValues[i] == render.bindValues().get(j)) {
                        this.bindMapping.computeIfAbsent(Integer.valueOf(i), x -> {
                            return new ArrayList();
                        }).add(Integer.valueOf(j));
                    }
                }
            }
        }

        DefaultRenderContext.Rendered rendered(Param<?>... bindValues) {
            Param<?>[] binds = new Param[this.bindSize];
            for (int i = 0; i < bindValues.length; i++) {
                Iterator<Integer> it = this.bindMapping.getOrDefault(Integer.valueOf(i), Collections.emptyList()).iterator();
                while (it.hasNext()) {
                    int mapped = it.next().intValue();
                    binds[mapped] = bindValues[i];
                }
            }
            return new DefaultRenderContext.Rendered(this.output, new QueryPartList(binds), 0);
        }

        public String toString() {
            return this.output;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final DefaultRenderContext.Rendered translate(Configuration configuration, String sql, Param<?>... bindValues) {
        log.debug("Translating from", sql);
        DefaultRenderContext.Rendered result = null;
        Supplier<CacheValue> miss = () -> {
            log.debug("Translation cache miss", sql);
            return new CacheValue(configuration, sql, bindValues);
        };
        Settings settings = configuration.settings();
        if (CacheType.CACHE_PARSING_CONNECTION.category.predicate.test(settings) && bindValues.length > 0) {
            switch (SettingsTools.getParamType(settings)) {
                case INLINED:
                case NAMED_OR_INLINED:
                    result = miss.get().rendered(bindValues);
                    break;
            }
        }
        if (result == null) {
            result = ((CacheValue) Cache.run(configuration, miss, CacheType.CACHE_PARSING_CONNECTION, () -> {
                return Cache.key(sql, Tools.map(nonNull(bindValues), f -> {
                    return f.getDataType();
                }));
            })).rendered(bindValues);
        }
        log.debug("Translating to", result.sql);
        return result;
    }

    private static Param<?>[] nonNull(Param<?>[] bindValues) {
        for (int i = 0; i < bindValues.length; i++) {
            if (bindValues[i] == null) {
                throw new DataAccessException("Bind value at position " + i + " not set");
            }
        }
        return bindValues;
    }

    @Override // org.jooq.tools.jdbc.DefaultConnection, java.sql.Connection
    public final Statement createStatement() throws SQLException {
        return new ParsingStatement(this, getDelegate().createStatement());
    }

    @Override // org.jooq.tools.jdbc.DefaultConnection, java.sql.Connection
    public final Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return new ParsingStatement(this, getDelegate().createStatement(resultSetType, resultSetConcurrency));
    }

    @Override // org.jooq.tools.jdbc.DefaultConnection, java.sql.Connection
    public final Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return new ParsingStatement(this, getDelegate().createStatement(resultSetType, resultSetConcurrency, resultSetHoldability));
    }

    private final ThrowingFunction<List<List<Param<?>>>, PreparedStatement, SQLException> prepareAndBind(String sql, ThrowingFunction<String, PreparedStatement, SQLException> prepare) {
        return p -> {
            int size = p.size();
            DefaultRenderContext.Rendered rendered = size == 0 ? translate(this.configuration, sql, new Param[0]) : translate(this.configuration, sql, (Param[]) ((List) p.get(0)).toArray(Tools.EMPTY_PARAM));
            PreparedStatement s = (PreparedStatement) prepare.apply(rendered.sql);
            for (int i = 0; i < size; i++) {
                if (i > 0) {
                    rendered = translate(this.configuration, sql, (Param[]) ((List) p.get(i)).toArray(Tools.EMPTY_PARAM));
                }
                new DefaultBindContext(this.configuration, null, s).visit(rendered.bindValues);
                if (size > 1 || (p instanceof ArrayList)) {
                    s.addBatch();
                }
            }
            return s;
        };
    }

    @Override // org.jooq.tools.jdbc.DefaultConnection, java.sql.Connection
    public final PreparedStatement prepareStatement(String sql) throws SQLException {
        return new ParsingStatement(this, prepareAndBind(sql, s -> {
            return getDelegate().prepareStatement(s);
        }));
    }

    @Override // org.jooq.tools.jdbc.DefaultConnection, java.sql.Connection
    public final PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return new ParsingStatement(this, prepareAndBind(sql, s -> {
            return getDelegate().prepareStatement(s, resultSetType, resultSetConcurrency);
        }));
    }

    @Override // org.jooq.tools.jdbc.DefaultConnection, java.sql.Connection
    public final PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return new ParsingStatement(this, prepareAndBind(sql, s -> {
            return getDelegate().prepareStatement(s, resultSetType, resultSetConcurrency, resultSetHoldability);
        }));
    }

    @Override // org.jooq.tools.jdbc.DefaultConnection, java.sql.Connection
    public final PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return new ParsingStatement(this, prepareAndBind(sql, s -> {
            return getDelegate().prepareStatement(s, autoGeneratedKeys);
        }));
    }

    @Override // org.jooq.tools.jdbc.DefaultConnection, java.sql.Connection
    public final PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return new ParsingStatement(this, prepareAndBind(sql, s -> {
            return getDelegate().prepareStatement(s, columnIndexes);
        }));
    }

    @Override // org.jooq.tools.jdbc.DefaultConnection, java.sql.Connection
    public final PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return new ParsingStatement(this, prepareAndBind(sql, s -> {
            return getDelegate().prepareStatement(s, columnNames);
        }));
    }

    @Override // org.jooq.tools.jdbc.DefaultConnection, java.sql.Connection
    public final CallableStatement prepareCall(String sql) throws SQLException {
        return new ParsingStatement(this, prepareAndBind(sql, s -> {
            return getDelegate().prepareCall(s);
        }));
    }

    @Override // org.jooq.tools.jdbc.DefaultConnection, java.sql.Connection
    public final CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return new ParsingStatement(this, prepareAndBind(sql, s -> {
            return getDelegate().prepareCall(s, resultSetType, resultSetConcurrency);
        }));
    }

    @Override // org.jooq.tools.jdbc.DefaultConnection, java.sql.Connection
    public final CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return new ParsingStatement(this, prepareAndBind(sql, s -> {
            return getDelegate().prepareCall(s, resultSetType, resultSetConcurrency, resultSetHoldability);
        }));
    }

    @Override // org.jooq.tools.jdbc.DefaultConnection, java.sql.Connection, java.lang.AutoCloseable
    public final void close() throws SQLException {
        this.configuration.connectionProvider().release(getDelegate());
    }
}
