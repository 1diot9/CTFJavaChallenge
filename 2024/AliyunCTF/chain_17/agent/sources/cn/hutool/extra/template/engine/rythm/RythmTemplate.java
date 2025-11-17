package cn.hutool.extra.template.engine.rythm;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.extra.template.AbstractTemplate;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.Writer;
import java.util.Map;
import org.rythmengine.template.ITemplate;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/template/engine/rythm/RythmTemplate.class */
public class RythmTemplate extends AbstractTemplate implements Serializable {
    private static final long serialVersionUID = -132774960373894911L;
    private final ITemplate rawTemplate;

    public static RythmTemplate wrap(ITemplate template) {
        if (null == template) {
            return null;
        }
        return new RythmTemplate(template);
    }

    public RythmTemplate(ITemplate rawTemplate) {
        this.rawTemplate = rawTemplate;
    }

    @Override // cn.hutool.extra.template.Template
    public void render(Map<?, ?> bindingMap, Writer writer) {
        Map<String, Object> map = (Map) Convert.convert((TypeReference) new TypeReference<Map<String, Object>>() { // from class: cn.hutool.extra.template.engine.rythm.RythmTemplate.1
        }, (Object) bindingMap);
        this.rawTemplate.__setRenderArgs(map);
        this.rawTemplate.render(writer);
    }

    @Override // cn.hutool.extra.template.Template
    public void render(Map<?, ?> bindingMap, OutputStream out) {
        this.rawTemplate.__setRenderArgs(new Object[]{bindingMap});
        this.rawTemplate.render(out);
    }
}
