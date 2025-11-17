package cn.hutool.extra.template;

import java.io.File;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/template/Template.class */
public interface Template {
    void render(Map<?, ?> map, Writer writer);

    void render(Map<?, ?> map, OutputStream outputStream);

    void render(Map<?, ?> map, File file);

    String render(Map<?, ?> map);
}
