package org.springframework.aop.config;

import org.springframework.beans.factory.parsing.ParseState;

/* loaded from: server.jar:BOOT-INF/lib/spring-aop-6.1.3.jar:org/springframework/aop/config/PointcutEntry.class */
public class PointcutEntry implements ParseState.Entry {
    private final String name;

    public PointcutEntry(String name) {
        this.name = name;
    }

    public String toString() {
        return "Pointcut '" + this.name + "'";
    }
}
