package org.springframework.beans.factory.parsing;

import org.springframework.beans.factory.parsing.ParseState;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/parsing/QualifierEntry.class */
public class QualifierEntry implements ParseState.Entry {
    private final String typeName;

    public QualifierEntry(String typeName) {
        if (!StringUtils.hasText(typeName)) {
            throw new IllegalArgumentException("Invalid qualifier type '" + typeName + "'");
        }
        this.typeName = typeName;
    }

    public String toString() {
        return "Qualifier '" + this.typeName + "'";
    }
}
