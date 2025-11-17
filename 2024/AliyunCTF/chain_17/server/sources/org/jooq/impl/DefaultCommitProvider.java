package org.jooq.impl;

import org.jetbrains.annotations.ApiStatus;
import org.jooq.CommitProvider;
import org.jooq.Commits;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.migrations.xml.jaxb.MigrationsType;

@ApiStatus.Experimental
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultCommitProvider.class */
public class DefaultCommitProvider implements CommitProvider {
    private final DSLContext ctx;
    private final MigrationsType migrations;

    public DefaultCommitProvider(Configuration configuration, MigrationsType migrations) {
        this.ctx = configuration.dsl();
        this.migrations = migrations;
    }

    @Override // org.jooq.CommitProvider
    public Commits provide() {
        return this.ctx.migrations().commits().load(this.migrations);
    }
}
