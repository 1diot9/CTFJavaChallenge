package org.springframework.beans.factory.parsing;

import org.springframework.beans.factory.parsing.ParseState;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/parsing/PropertyEntry.class */
public class PropertyEntry implements ParseState.Entry {
    private final String name;

    public PropertyEntry(String name) {
        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException("Invalid property name '" + name + "'");
        }
        this.name = name;
    }

    public String toString() {
        return "Property '" + this.name + "'";
    }
}
