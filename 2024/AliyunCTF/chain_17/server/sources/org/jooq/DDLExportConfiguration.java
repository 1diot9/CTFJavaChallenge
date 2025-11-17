package org.jooq;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/DDLExportConfiguration.class */
public final class DDLExportConfiguration {
    private final EnumSet<DDLFlag> flags;
    private final boolean createSchemaIfNotExists;
    private final boolean createTableIfNotExists;
    private final boolean createIndexIfNotExists;
    private final boolean createDomainIfNotExists;
    private final boolean createSequenceIfNotExists;
    private final boolean createViewIfNotExists;
    private final boolean createMaterializedViewIfNotExists;
    private final boolean createOrReplaceView;
    private final boolean createOrReplaceMaterializedView;
    private final boolean respectCatalogOrder;
    private final boolean respectSchemaOrder;
    private final boolean respectTableOrder;
    private final boolean respectColumnOrder;
    private final boolean respectConstraintOrder;
    private final boolean respectIndexOrder;
    private final boolean respectDomainOrder;
    private final boolean respectSequenceOrder;
    private final boolean defaultSequenceFlags;
    private final boolean includeConstraintsOnViews;

    public DDLExportConfiguration() {
        this(EnumSet.allOf(DDLFlag.class), false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false);
    }

    private DDLExportConfiguration(Collection<DDLFlag> flags, boolean createSchemaIfNotExists, boolean createTableIfNotExists, boolean createIndexIfNotExists, boolean createDomainIfNotExists, boolean createSequenceIfNotExists, boolean createViewIfNotExists, boolean createMaterializedViewIfNotExists, boolean createOrReplaceView, boolean createOrReplaceMaterializedView, boolean respectCatalogOrder, boolean respectSchemaOrder, boolean respectTableOrder, boolean respectColumnOrder, boolean respectConstraintOrder, boolean respectIndexOrder, boolean respectDomainOrder, boolean respectSequenceOrder, boolean defaultSequenceFlags, boolean includeConstraintsOnViews) {
        this.flags = EnumSet.copyOf((Collection) flags);
        this.createSchemaIfNotExists = createSchemaIfNotExists;
        this.createTableIfNotExists = createTableIfNotExists;
        this.createIndexIfNotExists = createIndexIfNotExists;
        this.createDomainIfNotExists = createDomainIfNotExists;
        this.createSequenceIfNotExists = createSequenceIfNotExists;
        this.createViewIfNotExists = createViewIfNotExists;
        this.createMaterializedViewIfNotExists = createMaterializedViewIfNotExists;
        this.createOrReplaceView = createOrReplaceView;
        this.createOrReplaceMaterializedView = createOrReplaceMaterializedView;
        this.respectCatalogOrder = respectCatalogOrder;
        this.respectSchemaOrder = respectSchemaOrder;
        this.respectTableOrder = respectTableOrder;
        this.respectColumnOrder = respectColumnOrder;
        this.respectConstraintOrder = respectConstraintOrder;
        this.respectIndexOrder = respectIndexOrder;
        this.respectDomainOrder = respectDomainOrder;
        this.respectSequenceOrder = respectSequenceOrder;
        this.defaultSequenceFlags = defaultSequenceFlags;
        this.includeConstraintsOnViews = includeConstraintsOnViews;
    }

    public final Set<DDLFlag> flags() {
        return Collections.unmodifiableSet(this.flags);
    }

    public final DDLExportConfiguration flags(DDLFlag... newFlags) {
        return flags(Arrays.asList(newFlags));
    }

    public final DDLExportConfiguration flags(Collection<DDLFlag> newFlags) {
        return new DDLExportConfiguration(newFlags, this.createSchemaIfNotExists, this.createTableIfNotExists, this.createIndexIfNotExists, this.createDomainIfNotExists, this.createSequenceIfNotExists, this.createViewIfNotExists, this.createMaterializedViewIfNotExists, this.createOrReplaceView, this.createOrReplaceMaterializedView, this.respectCatalogOrder, this.respectSchemaOrder, this.respectTableOrder, this.respectColumnOrder, this.respectConstraintOrder, this.respectIndexOrder, this.respectDomainOrder, this.respectSequenceOrder, this.defaultSequenceFlags, this.includeConstraintsOnViews);
    }

    public final boolean createSchemaIfNotExists() {
        return this.createSchemaIfNotExists;
    }

    public final DDLExportConfiguration createSchemaIfNotExists(boolean newCreateSchemaIfNotExists) {
        return new DDLExportConfiguration(this.flags, newCreateSchemaIfNotExists, this.createTableIfNotExists, this.createIndexIfNotExists, this.createDomainIfNotExists, this.createSequenceIfNotExists, this.createViewIfNotExists, this.createMaterializedViewIfNotExists, this.createOrReplaceView, this.createOrReplaceMaterializedView, this.respectCatalogOrder, this.respectSchemaOrder, this.respectTableOrder, this.respectColumnOrder, this.respectConstraintOrder, this.respectIndexOrder, this.respectDomainOrder, this.respectSequenceOrder, this.defaultSequenceFlags, this.includeConstraintsOnViews);
    }

    public final boolean createTableIfNotExists() {
        return this.createTableIfNotExists;
    }

    public final DDLExportConfiguration createTableIfNotExists(boolean newCreateTableIfNotExists) {
        return new DDLExportConfiguration(this.flags, this.createSchemaIfNotExists, newCreateTableIfNotExists, this.createIndexIfNotExists, this.createDomainIfNotExists, this.createSequenceIfNotExists, this.createViewIfNotExists, this.createMaterializedViewIfNotExists, this.createOrReplaceView, this.createOrReplaceMaterializedView, this.respectCatalogOrder, this.respectSchemaOrder, this.respectTableOrder, this.respectColumnOrder, this.respectConstraintOrder, this.respectIndexOrder, this.respectDomainOrder, this.respectSequenceOrder, this.defaultSequenceFlags, this.includeConstraintsOnViews);
    }

    public final boolean createIndexIfNotExists() {
        return this.createIndexIfNotExists;
    }

    public final DDLExportConfiguration createIndexIfNotExists(boolean newCreateIndexIfNotExists) {
        return new DDLExportConfiguration(this.flags, this.createSchemaIfNotExists, this.createTableIfNotExists, newCreateIndexIfNotExists, this.createDomainIfNotExists, this.createSequenceIfNotExists, this.createViewIfNotExists, this.createMaterializedViewIfNotExists, this.createOrReplaceView, this.createOrReplaceMaterializedView, this.respectCatalogOrder, this.respectSchemaOrder, this.respectTableOrder, this.respectColumnOrder, this.respectConstraintOrder, this.respectIndexOrder, this.respectDomainOrder, this.respectSequenceOrder, this.defaultSequenceFlags, this.includeConstraintsOnViews);
    }

    public final boolean createDomainIfNotExists() {
        return this.createDomainIfNotExists;
    }

    public final DDLExportConfiguration createDomainIfNotExists(boolean newCreateDomainIfNotExists) {
        return new DDLExportConfiguration(this.flags, this.createSchemaIfNotExists, this.createTableIfNotExists, this.createIndexIfNotExists, newCreateDomainIfNotExists, this.createSequenceIfNotExists, this.createViewIfNotExists, this.createMaterializedViewIfNotExists, this.createOrReplaceView, this.createOrReplaceMaterializedView, this.respectCatalogOrder, this.respectSchemaOrder, this.respectTableOrder, this.respectColumnOrder, this.respectConstraintOrder, this.respectIndexOrder, this.respectDomainOrder, this.respectSequenceOrder, this.defaultSequenceFlags, this.includeConstraintsOnViews);
    }

    public final boolean createSequenceIfNotExists() {
        return this.createSequenceIfNotExists;
    }

    public final DDLExportConfiguration createSequenceIfNotExists(boolean newCreateSequenceIfNotExists) {
        return new DDLExportConfiguration(this.flags, this.createSchemaIfNotExists, this.createTableIfNotExists, this.createIndexIfNotExists, this.createDomainIfNotExists, newCreateSequenceIfNotExists, this.createViewIfNotExists, this.createMaterializedViewIfNotExists, this.createOrReplaceView, this.createOrReplaceMaterializedView, this.respectCatalogOrder, this.respectSchemaOrder, this.respectTableOrder, this.respectColumnOrder, this.respectConstraintOrder, this.respectIndexOrder, this.respectDomainOrder, this.respectSequenceOrder, this.defaultSequenceFlags, this.includeConstraintsOnViews);
    }

    public final boolean createViewIfNotExists() {
        return this.createViewIfNotExists;
    }

    public final DDLExportConfiguration createViewIfNotExists(boolean newCreateViewIfNotExists) {
        return new DDLExportConfiguration(this.flags, this.createSchemaIfNotExists, this.createTableIfNotExists, this.createIndexIfNotExists, this.createDomainIfNotExists, this.createSequenceIfNotExists, newCreateViewIfNotExists, this.createMaterializedViewIfNotExists, this.createOrReplaceView, this.createOrReplaceMaterializedView, this.respectCatalogOrder, this.respectSchemaOrder, this.respectTableOrder, this.respectColumnOrder, this.respectConstraintOrder, this.respectIndexOrder, this.respectDomainOrder, this.respectSequenceOrder, this.defaultSequenceFlags, this.includeConstraintsOnViews);
    }

    public final boolean createMaterializedViewIfNotExists() {
        return this.createMaterializedViewIfNotExists;
    }

    public final DDLExportConfiguration createMaterializedViewIfNotExists(boolean newCreateMaterializedViewIfNotExists) {
        return new DDLExportConfiguration(this.flags, this.createSchemaIfNotExists, this.createTableIfNotExists, this.createIndexIfNotExists, this.createDomainIfNotExists, this.createSequenceIfNotExists, this.createViewIfNotExists, newCreateMaterializedViewIfNotExists, this.createOrReplaceView, this.createOrReplaceMaterializedView, this.respectCatalogOrder, this.respectSchemaOrder, this.respectTableOrder, this.respectColumnOrder, this.respectConstraintOrder, this.respectIndexOrder, this.respectDomainOrder, this.respectSequenceOrder, this.defaultSequenceFlags, this.includeConstraintsOnViews);
    }

    public final boolean createOrReplaceView() {
        return this.createOrReplaceView;
    }

    public final DDLExportConfiguration createOrReplaceView(boolean newCreateOrReplaceView) {
        return new DDLExportConfiguration(this.flags, this.createSchemaIfNotExists, this.createTableIfNotExists, this.createIndexIfNotExists, this.createDomainIfNotExists, this.createSequenceIfNotExists, this.createViewIfNotExists, this.createMaterializedViewIfNotExists, newCreateOrReplaceView, this.createOrReplaceMaterializedView, this.respectCatalogOrder, this.respectSchemaOrder, this.respectTableOrder, this.respectColumnOrder, this.respectConstraintOrder, this.respectIndexOrder, this.respectDomainOrder, this.respectSequenceOrder, this.defaultSequenceFlags, this.includeConstraintsOnViews);
    }

    public final boolean createOrReplaceMaterializedView() {
        return this.createOrReplaceMaterializedView;
    }

    public final DDLExportConfiguration createOrReplaceMaterializedView(boolean newCreateOrReplaceMaterializedView) {
        return new DDLExportConfiguration(this.flags, this.createSchemaIfNotExists, this.createTableIfNotExists, this.createIndexIfNotExists, this.createDomainIfNotExists, this.createSequenceIfNotExists, this.createViewIfNotExists, this.createMaterializedViewIfNotExists, this.createOrReplaceView, newCreateOrReplaceMaterializedView, this.respectCatalogOrder, this.respectSchemaOrder, this.respectTableOrder, this.respectColumnOrder, this.respectConstraintOrder, this.respectIndexOrder, this.respectDomainOrder, this.respectSequenceOrder, this.defaultSequenceFlags, this.includeConstraintsOnViews);
    }

    public final boolean respectCatalogOrder() {
        return this.respectCatalogOrder;
    }

    public final DDLExportConfiguration respectCatalogOrder(boolean newRespectCatalogOrder) {
        return new DDLExportConfiguration(this.flags, this.createSchemaIfNotExists, this.createTableIfNotExists, this.createIndexIfNotExists, this.createDomainIfNotExists, this.createSequenceIfNotExists, this.createViewIfNotExists, this.createMaterializedViewIfNotExists, this.createOrReplaceView, this.createOrReplaceMaterializedView, newRespectCatalogOrder, this.respectSchemaOrder, this.respectTableOrder, this.respectColumnOrder, this.respectConstraintOrder, this.respectIndexOrder, this.respectDomainOrder, this.respectSequenceOrder, this.defaultSequenceFlags, this.includeConstraintsOnViews);
    }

    public final boolean respectSchemaOrder() {
        return this.respectSchemaOrder;
    }

    public final DDLExportConfiguration respectSchemaOrder(boolean newRespectSchemaOrder) {
        return new DDLExportConfiguration(this.flags, this.createSchemaIfNotExists, this.createTableIfNotExists, this.createIndexIfNotExists, this.createDomainIfNotExists, this.createSequenceIfNotExists, this.createViewIfNotExists, this.createMaterializedViewIfNotExists, this.createOrReplaceView, this.createOrReplaceMaterializedView, this.respectCatalogOrder, newRespectSchemaOrder, this.respectTableOrder, this.respectColumnOrder, this.respectConstraintOrder, this.respectIndexOrder, this.respectDomainOrder, this.respectSequenceOrder, this.defaultSequenceFlags, this.includeConstraintsOnViews);
    }

    public final boolean respectTableOrder() {
        return this.respectTableOrder;
    }

    public final DDLExportConfiguration respectTableOrder(boolean newRespectTableOrder) {
        return new DDLExportConfiguration(this.flags, this.createSchemaIfNotExists, this.createTableIfNotExists, this.createIndexIfNotExists, this.createDomainIfNotExists, this.createSequenceIfNotExists, this.createViewIfNotExists, this.createMaterializedViewIfNotExists, this.createOrReplaceView, this.createOrReplaceMaterializedView, this.respectCatalogOrder, this.respectSchemaOrder, newRespectTableOrder, this.respectColumnOrder, this.respectConstraintOrder, this.respectIndexOrder, this.respectDomainOrder, this.respectSequenceOrder, this.defaultSequenceFlags, this.includeConstraintsOnViews);
    }

    public final boolean respectColumnOrder() {
        return this.respectColumnOrder;
    }

    public final DDLExportConfiguration respectColumnOrder(boolean newRespectColumnOrder) {
        return new DDLExportConfiguration(this.flags, this.createSchemaIfNotExists, this.createTableIfNotExists, this.createIndexIfNotExists, this.createDomainIfNotExists, this.createSequenceIfNotExists, this.createViewIfNotExists, this.createMaterializedViewIfNotExists, this.createOrReplaceView, this.createOrReplaceMaterializedView, this.respectCatalogOrder, this.respectSchemaOrder, this.respectTableOrder, newRespectColumnOrder, this.respectConstraintOrder, this.respectIndexOrder, this.respectDomainOrder, this.respectSequenceOrder, this.defaultSequenceFlags, this.includeConstraintsOnViews);
    }

    public final boolean respectConstraintOrder() {
        return this.respectConstraintOrder;
    }

    public final DDLExportConfiguration respectConstraintOrder(boolean newRespectConstraintOrder) {
        return new DDLExportConfiguration(this.flags, this.createSchemaIfNotExists, this.createTableIfNotExists, this.createIndexIfNotExists, this.createDomainIfNotExists, this.createSequenceIfNotExists, this.createViewIfNotExists, this.createMaterializedViewIfNotExists, this.createOrReplaceView, this.createOrReplaceMaterializedView, this.respectCatalogOrder, this.respectSchemaOrder, this.respectTableOrder, this.respectColumnOrder, newRespectConstraintOrder, this.respectIndexOrder, this.respectDomainOrder, this.respectSequenceOrder, this.defaultSequenceFlags, this.includeConstraintsOnViews);
    }

    public final boolean respectIndexOrder() {
        return this.respectIndexOrder;
    }

    public final DDLExportConfiguration respectIndexOrder(boolean newRespectIndexOrder) {
        return new DDLExportConfiguration(this.flags, this.createSchemaIfNotExists, this.createTableIfNotExists, this.createIndexIfNotExists, this.createDomainIfNotExists, this.createSequenceIfNotExists, this.createViewIfNotExists, this.createMaterializedViewIfNotExists, this.createOrReplaceView, this.createOrReplaceMaterializedView, this.respectCatalogOrder, this.respectSchemaOrder, this.respectTableOrder, this.respectColumnOrder, this.respectConstraintOrder, newRespectIndexOrder, this.respectDomainOrder, this.respectSequenceOrder, this.defaultSequenceFlags, this.includeConstraintsOnViews);
    }

    public final boolean respectDomainOrder() {
        return this.respectDomainOrder;
    }

    public final DDLExportConfiguration respectDomainOrder(boolean newRespectDomainOrder) {
        return new DDLExportConfiguration(this.flags, this.createSchemaIfNotExists, this.createTableIfNotExists, this.createIndexIfNotExists, this.createDomainIfNotExists, this.createSequenceIfNotExists, this.createViewIfNotExists, this.createMaterializedViewIfNotExists, this.createOrReplaceView, this.createOrReplaceMaterializedView, this.respectCatalogOrder, this.respectSchemaOrder, this.respectTableOrder, this.respectColumnOrder, this.respectConstraintOrder, this.respectIndexOrder, newRespectDomainOrder, this.respectSequenceOrder, this.defaultSequenceFlags, this.includeConstraintsOnViews);
    }

    public final boolean respectSequenceOrder() {
        return this.respectSequenceOrder;
    }

    public final DDLExportConfiguration respectSequenceOrder(boolean newRespectSequenceOrder) {
        return new DDLExportConfiguration(this.flags, this.createSchemaIfNotExists, this.createTableIfNotExists, this.createIndexIfNotExists, this.createDomainIfNotExists, this.createSequenceIfNotExists, this.createViewIfNotExists, this.createMaterializedViewIfNotExists, this.createOrReplaceView, this.createOrReplaceMaterializedView, this.respectCatalogOrder, this.respectSchemaOrder, this.respectTableOrder, this.respectColumnOrder, this.respectConstraintOrder, this.respectIndexOrder, this.respectDomainOrder, newRespectSequenceOrder, this.defaultSequenceFlags, this.includeConstraintsOnViews);
    }

    public final boolean defaultSequenceFlags() {
        return this.defaultSequenceFlags;
    }

    public final DDLExportConfiguration defaultSequenceFlags(boolean newDefaultSequenceFlags) {
        return new DDLExportConfiguration(this.flags, this.createSchemaIfNotExists, this.createTableIfNotExists, this.createIndexIfNotExists, this.createDomainIfNotExists, this.createSequenceIfNotExists, this.createViewIfNotExists, this.createMaterializedViewIfNotExists, this.createOrReplaceView, this.createOrReplaceMaterializedView, this.respectCatalogOrder, this.respectSchemaOrder, this.respectTableOrder, this.respectColumnOrder, this.respectConstraintOrder, this.respectIndexOrder, this.respectDomainOrder, this.respectSequenceOrder, newDefaultSequenceFlags, this.includeConstraintsOnViews);
    }

    public final boolean includeConstraintsOnViews() {
        return this.includeConstraintsOnViews;
    }

    public final DDLExportConfiguration includeConstraintsOnViews(boolean newIncludeConstraintsOnViews) {
        return new DDLExportConfiguration(this.flags, this.createSchemaIfNotExists, this.createTableIfNotExists, this.createIndexIfNotExists, this.createDomainIfNotExists, this.createSequenceIfNotExists, this.createViewIfNotExists, this.createMaterializedViewIfNotExists, this.createOrReplaceView, this.createOrReplaceMaterializedView, this.respectCatalogOrder, this.respectSchemaOrder, this.respectTableOrder, this.respectColumnOrder, this.respectConstraintOrder, this.respectIndexOrder, this.respectDomainOrder, this.respectSequenceOrder, this.defaultSequenceFlags, newIncludeConstraintsOnViews);
    }
}
