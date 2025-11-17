package org.springframework.beans.factory.parsing;

import java.util.ArrayList;
import java.util.List;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/parsing/CompositeComponentDefinition.class */
public class CompositeComponentDefinition extends AbstractComponentDefinition {
    private final String name;

    @Nullable
    private final Object source;
    private final List<ComponentDefinition> nestedComponents = new ArrayList();

    public CompositeComponentDefinition(String name, @Nullable Object source) {
        Assert.notNull(name, "Name must not be null");
        this.name = name;
        this.source = source;
    }

    @Override // org.springframework.beans.factory.parsing.ComponentDefinition
    public String getName() {
        return this.name;
    }

    @Override // org.springframework.beans.BeanMetadataElement
    @Nullable
    public Object getSource() {
        return this.source;
    }

    public void addNestedComponent(ComponentDefinition component) {
        Assert.notNull(component, "ComponentDefinition must not be null");
        this.nestedComponents.add(component);
    }

    public ComponentDefinition[] getNestedComponents() {
        return (ComponentDefinition[]) this.nestedComponents.toArray(new ComponentDefinition[0]);
    }
}
