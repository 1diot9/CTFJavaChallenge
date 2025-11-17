package org.springframework.beans.factory.parsing;

import java.util.EventListener;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/parsing/ReaderEventListener.class */
public interface ReaderEventListener extends EventListener {
    void defaultsRegistered(DefaultsDefinition defaultsDefinition);

    void componentRegistered(ComponentDefinition componentDefinition);

    void aliasRegistered(AliasDefinition aliasDefinition);

    void importProcessed(ImportDefinition importDefinition);
}
