package org.jooq.impl;

import java.time.Instant;
import java.util.Map;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.Scope;
import org.jooq.conf.Settings;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractScope.class */
public abstract class AbstractScope implements Scope {
    final Configuration configuration;
    final Map<Object, Object> data;
    final Instant creationTime;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractScope(Configuration configuration) {
        this(configuration, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractScope(Configuration configuration, Map<Object, Object> data) {
        configuration = configuration == null ? new DefaultConfiguration() : configuration;
        data = data == null ? new DataMap() : data;
        this.configuration = configuration;
        this.data = data;
        this.creationTime = configuration.clock().instant();
    }

    @Override // org.jooq.Scope
    public final Instant creationTime() {
        return this.creationTime;
    }

    @Override // org.jooq.Scope
    public final Configuration configuration() {
        return this.configuration;
    }

    @Override // org.jooq.Scope
    public final DSLContext dsl() {
        return configuration().dsl();
    }

    @Override // org.jooq.Scope
    public final Settings settings() {
        return Tools.settings(configuration());
    }

    @Override // org.jooq.Scope
    public final SQLDialect dialect() {
        return Tools.configuration(configuration()).dialect();
    }

    @Override // org.jooq.Scope
    public final SQLDialect family() {
        return dialect().family();
    }

    @Override // org.jooq.Scope
    public final Map<Object, Object> data() {
        return this.data;
    }

    @Override // org.jooq.Scope
    public final Object data(Object key) {
        return this.data.get(key);
    }

    @Override // org.jooq.Scope
    public final Object data(Object key, Object value) {
        return this.data.put(key, value);
    }
}
