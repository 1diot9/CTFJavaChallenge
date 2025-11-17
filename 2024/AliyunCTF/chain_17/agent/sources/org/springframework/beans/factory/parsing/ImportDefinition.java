package org.springframework.beans.factory.parsing;

import org.springframework.beans.BeanMetadataElement;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/parsing/ImportDefinition.class */
public class ImportDefinition implements BeanMetadataElement {
    private final String importedResource;

    @Nullable
    private final Resource[] actualResources;

    @Nullable
    private final Object source;

    public ImportDefinition(String importedResource) {
        this(importedResource, null, null);
    }

    public ImportDefinition(String importedResource, @Nullable Object source) {
        this(importedResource, null, source);
    }

    public ImportDefinition(String importedResource, @Nullable Resource[] actualResources, @Nullable Object source) {
        Assert.notNull(importedResource, "Imported resource must not be null");
        this.importedResource = importedResource;
        this.actualResources = actualResources;
        this.source = source;
    }

    public final String getImportedResource() {
        return this.importedResource;
    }

    @Nullable
    public final Resource[] getActualResources() {
        return this.actualResources;
    }

    @Override // org.springframework.beans.BeanMetadataElement
    @Nullable
    public final Object getSource() {
        return this.source;
    }
}
