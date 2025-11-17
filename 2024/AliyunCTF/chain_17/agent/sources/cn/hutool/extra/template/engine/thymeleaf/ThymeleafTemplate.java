package cn.hutool.extra.template.engine.thymeleaf;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.template.AbstractTemplate;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Map;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/template/engine/thymeleaf/ThymeleafTemplate.class */
public class ThymeleafTemplate extends AbstractTemplate implements Serializable {
    private static final long serialVersionUID = 781284916568562509L;
    private final TemplateEngine engine;
    private final String template;
    private final Charset charset;

    public static ThymeleafTemplate wrap(TemplateEngine engine, String template, Charset charset) {
        if (null == engine) {
            return null;
        }
        return new ThymeleafTemplate(engine, template, charset);
    }

    public ThymeleafTemplate(TemplateEngine engine, String template, Charset charset) {
        this.engine = engine;
        this.template = template;
        this.charset = (Charset) ObjectUtil.defaultIfNull(charset, CharsetUtil.CHARSET_UTF_8);
    }

    @Override // cn.hutool.extra.template.Template
    public void render(Map<?, ?> bindingMap, Writer writer) {
        Map<String, Object> map = (Map) Convert.convert((TypeReference) new TypeReference<Map<String, Object>>() { // from class: cn.hutool.extra.template.engine.thymeleaf.ThymeleafTemplate.1
        }, (Object) bindingMap);
        Context context = new Context(Locale.getDefault(), map);
        this.engine.process(this.template, context, writer);
    }

    @Override // cn.hutool.extra.template.Template
    public void render(Map<?, ?> bindingMap, OutputStream out) {
        render(bindingMap, IoUtil.getWriter(out, this.charset));
    }
}
