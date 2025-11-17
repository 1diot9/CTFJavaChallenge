package org.springframework.scripting;

import java.io.IOException;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/scripting/ScriptFactory.class */
public interface ScriptFactory {
    String getScriptSourceLocator();

    @Nullable
    Class<?>[] getScriptInterfaces();

    boolean requiresConfigInterface();

    @Nullable
    Object getScriptedObject(ScriptSource scriptSource, @Nullable Class<?>... actualInterfaces) throws IOException, ScriptCompilationException;

    @Nullable
    Class<?> getScriptedObjectType(ScriptSource scriptSource) throws IOException, ScriptCompilationException;

    boolean requiresScriptedObjectRefresh(ScriptSource scriptSource);
}
