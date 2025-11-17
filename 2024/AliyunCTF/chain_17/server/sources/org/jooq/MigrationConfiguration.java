package org.jooq;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/MigrationConfiguration.class */
public final class MigrationConfiguration {
    private final boolean alterTableAddMultiple;
    private final boolean alterTableDropMultiple;
    private final boolean dropSchemaCascade;
    private final boolean dropTableCascade;
    private final boolean alterTableDropCascade;
    private final boolean createOrReplaceView;
    private final boolean createOrReplaceMaterializedView;
    private final boolean respectColumnOrder;

    public MigrationConfiguration() {
        this(false, false, false, false, false, false, false, false);
    }

    private MigrationConfiguration(boolean alterTableAddMultiple, boolean alterTableDropMultiple, boolean dropSchemaCascade, boolean dropTableCascade, boolean alterTableDropCascade, boolean createOrReplaceView, boolean createOrReplaceMaterializedView, boolean respectColumnOrder) {
        this.alterTableAddMultiple = alterTableAddMultiple;
        this.alterTableDropMultiple = alterTableDropMultiple;
        this.dropSchemaCascade = dropSchemaCascade;
        this.dropTableCascade = dropTableCascade;
        this.alterTableDropCascade = alterTableDropCascade;
        this.createOrReplaceView = createOrReplaceView;
        this.createOrReplaceMaterializedView = createOrReplaceMaterializedView;
        this.respectColumnOrder = respectColumnOrder;
    }

    public final boolean alterTableAddMultiple() {
        return this.alterTableAddMultiple;
    }

    public final MigrationConfiguration alterTableAddMultiple(boolean newAlterTableAddMultiple) {
        return new MigrationConfiguration(newAlterTableAddMultiple, this.alterTableDropMultiple, this.dropSchemaCascade, this.dropTableCascade, this.alterTableDropCascade, this.createOrReplaceView, this.createOrReplaceMaterializedView, this.respectColumnOrder);
    }

    public final boolean alterTableDropMultiple() {
        return this.alterTableDropMultiple;
    }

    public final MigrationConfiguration alterTableDropMultiple(boolean newAlterTableDropMultiple) {
        return new MigrationConfiguration(this.alterTableAddMultiple, newAlterTableDropMultiple, this.dropSchemaCascade, this.dropTableCascade, this.alterTableDropCascade, this.createOrReplaceView, this.createOrReplaceMaterializedView, this.respectColumnOrder);
    }

    public final boolean dropSchemaCascade() {
        return this.dropSchemaCascade;
    }

    public final MigrationConfiguration dropSchemaCascade(boolean newDropSchemaCascade) {
        return new MigrationConfiguration(this.alterTableAddMultiple, this.alterTableDropMultiple, newDropSchemaCascade, this.dropTableCascade, this.alterTableDropCascade, this.createOrReplaceView, this.createOrReplaceMaterializedView, this.respectColumnOrder);
    }

    public final boolean dropTableCascade() {
        return this.dropTableCascade;
    }

    public final MigrationConfiguration dropTableCascade(boolean newDropTableCascade) {
        return new MigrationConfiguration(this.alterTableAddMultiple, this.alterTableDropMultiple, this.dropSchemaCascade, newDropTableCascade, this.alterTableDropCascade, this.createOrReplaceView, this.createOrReplaceMaterializedView, this.respectColumnOrder);
    }

    public final boolean alterTableDropCascade() {
        return this.alterTableDropCascade;
    }

    public final MigrationConfiguration alterTableDropCascade(boolean newAlterTableDropCascade) {
        return new MigrationConfiguration(this.alterTableAddMultiple, this.alterTableDropMultiple, this.dropSchemaCascade, this.dropTableCascade, newAlterTableDropCascade, this.createOrReplaceView, this.createOrReplaceMaterializedView, this.respectColumnOrder);
    }

    public final boolean createOrReplaceView() {
        return this.createOrReplaceView;
    }

    public final MigrationConfiguration createOrReplaceView(boolean newCreateOrReplaceView) {
        return new MigrationConfiguration(this.alterTableAddMultiple, this.alterTableDropMultiple, this.dropSchemaCascade, this.dropTableCascade, this.alterTableDropCascade, newCreateOrReplaceView, this.createOrReplaceMaterializedView, this.respectColumnOrder);
    }

    public final boolean createOrReplaceMaterializedView() {
        return this.createOrReplaceMaterializedView;
    }

    public final MigrationConfiguration createOrReplaceMaterializedView(boolean newCreateOrReplaceMaterializedView) {
        return new MigrationConfiguration(this.alterTableAddMultiple, this.alterTableDropMultiple, this.dropSchemaCascade, this.dropTableCascade, this.alterTableDropCascade, this.createOrReplaceView, newCreateOrReplaceMaterializedView, this.respectColumnOrder);
    }

    public final boolean respectColumnOrder() {
        return this.respectColumnOrder;
    }

    public final MigrationConfiguration respectColumnOrder(boolean newRespectColumnOrder) {
        return new MigrationConfiguration(this.alterTableAddMultiple, this.alterTableDropMultiple, this.dropSchemaCascade, this.dropTableCascade, this.alterTableDropCascade, this.createOrReplaceView, this.createOrReplaceMaterializedView, newRespectColumnOrder);
    }
}
