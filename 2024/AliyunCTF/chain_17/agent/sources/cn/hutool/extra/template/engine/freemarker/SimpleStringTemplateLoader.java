package cn.hutool.extra.template.engine.freemarker;

import freemarker.cache.TemplateLoader;
import java.io.Reader;
import java.io.StringReader;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/template/engine/freemarker/SimpleStringTemplateLoader.class */
public class SimpleStringTemplateLoader implements TemplateLoader {
    public Object findTemplateSource(String name) {
        return name;
    }

    public long getLastModified(Object templateSource) {
        return System.currentTimeMillis();
    }

    public Reader getReader(Object templateSource, String encoding) {
        return new StringReader((String) templateSource);
    }

    public void closeTemplateSource(Object templateSource) {
    }
}
