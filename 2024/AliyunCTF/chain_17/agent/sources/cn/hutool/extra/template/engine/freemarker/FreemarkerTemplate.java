package cn.hutool.extra.template.engine.freemarker;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.extra.template.AbstractTemplate;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/template/engine/freemarker/FreemarkerTemplate.class */
public class FreemarkerTemplate extends AbstractTemplate implements Serializable {
    private static final long serialVersionUID = -8157926902932567280L;
    Template rawTemplate;

    public static FreemarkerTemplate wrap(Template beetlTemplate) {
        if (null == beetlTemplate) {
            return null;
        }
        return new FreemarkerTemplate(beetlTemplate);
    }

    public FreemarkerTemplate(Template freemarkerTemplate) {
        this.rawTemplate = freemarkerTemplate;
    }

    @Override // cn.hutool.extra.template.Template
    public void render(Map<?, ?> bindingMap, Writer writer) {
        try {
            this.rawTemplate.process(bindingMap, writer);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        } catch (TemplateException e2) {
            throw new cn.hutool.extra.template.TemplateException((Throwable) e2);
        }
    }

    @Override // cn.hutool.extra.template.Template
    public void render(Map<?, ?> bindingMap, OutputStream out) {
        render(bindingMap, IoUtil.getWriter(out, Charset.forName(this.rawTemplate.getEncoding())));
    }
}
