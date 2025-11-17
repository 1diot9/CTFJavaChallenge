package cn.hutool.extra.template.engine.enjoy;

import cn.hutool.extra.template.AbstractTemplate;
import com.jfinal.template.Template;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.Writer;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/template/engine/enjoy/EnjoyTemplate.class */
public class EnjoyTemplate extends AbstractTemplate implements Serializable {
    private static final long serialVersionUID = 1;
    private final Template rawTemplate;

    public static EnjoyTemplate wrap(Template EnjoyTemplate) {
        if (null == EnjoyTemplate) {
            return null;
        }
        return new EnjoyTemplate(EnjoyTemplate);
    }

    public EnjoyTemplate(Template EnjoyTemplate) {
        this.rawTemplate = EnjoyTemplate;
    }

    @Override // cn.hutool.extra.template.Template
    public void render(Map<?, ?> bindingMap, Writer writer) {
        this.rawTemplate.render(bindingMap, writer);
    }

    @Override // cn.hutool.extra.template.Template
    public void render(Map<?, ?> bindingMap, OutputStream out) {
        this.rawTemplate.render(bindingMap, out);
    }
}
