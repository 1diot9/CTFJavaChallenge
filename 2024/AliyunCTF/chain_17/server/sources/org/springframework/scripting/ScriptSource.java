package org.springframework.scripting;

import java.io.IOException;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scripting/ScriptSource.class */
public interface ScriptSource {
    String getScriptAsString() throws IOException;

    boolean isModified();

    @Nullable
    String suggestedClassName();
}
