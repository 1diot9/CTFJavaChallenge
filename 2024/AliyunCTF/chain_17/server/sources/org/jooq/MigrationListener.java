package org.jooq;

import java.util.EventListener;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/MigrationListener.class */
public interface MigrationListener extends EventListener {
    default void migrationStart(MigrationContext ctx) {
    }

    default void migrationEnd(MigrationContext ctx) {
    }

    default void queriesStart(MigrationContext ctx) {
    }

    default void queriesEnd(MigrationContext ctx) {
    }

    default void queryStart(MigrationContext ctx) {
    }

    default void queryEnd(MigrationContext ctx) {
    }
}
