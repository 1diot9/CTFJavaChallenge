package cn.hutool.extra.template;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.StringWriter;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/template/AbstractTemplate.class */
public abstract class AbstractTemplate implements Template {
    @Override // cn.hutool.extra.template.Template
    public void render(Map<?, ?> bindingMap, File file) {
        BufferedOutputStream out = null;
        try {
            out = FileUtil.getOutputStream(file);
            render(bindingMap, out);
            IoUtil.close((Closeable) out);
        } catch (Throwable th) {
            IoUtil.close((Closeable) out);
            throw th;
        }
    }

    @Override // cn.hutool.extra.template.Template
    public String render(Map<?, ?> bindingMap) {
        StringWriter writer = new StringWriter();
        render(bindingMap, writer);
        return writer.toString();
    }
}
