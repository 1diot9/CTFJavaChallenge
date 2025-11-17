package org.springframework.web.servlet.view.script;

import java.nio.charset.Charset;
import java.util.function.Supplier;
import javax.script.ScriptEngine;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/view/script/ScriptTemplateConfig.class */
public interface ScriptTemplateConfig {
    @Nullable
    ScriptEngine getEngine();

    @Nullable
    Supplier<ScriptEngine> getEngineSupplier();

    @Nullable
    String getEngineName();

    @Nullable
    Boolean isSharedEngine();

    @Nullable
    String[] getScripts();

    @Nullable
    String getRenderObject();

    @Nullable
    String getRenderFunction();

    @Nullable
    String getContentType();

    @Nullable
    Charset getCharset();

    @Nullable
    String getResourceLoaderPath();
}
