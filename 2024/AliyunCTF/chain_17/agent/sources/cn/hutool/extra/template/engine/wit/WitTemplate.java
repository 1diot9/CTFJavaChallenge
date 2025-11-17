package cn.hutool.extra.template.engine.wit;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.extra.template.AbstractTemplate;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.Writer;
import java.util.Map;
import org.febit.wit.Template;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/template/engine/wit/WitTemplate.class */
public class WitTemplate extends AbstractTemplate implements Serializable {
    private static final long serialVersionUID = 1;
    private final Template rawTemplate;

    public static WitTemplate wrap(Template witTemplate) {
        if (null == witTemplate) {
            return null;
        }
        return new WitTemplate(witTemplate);
    }

    public WitTemplate(Template witTemplate) {
        this.rawTemplate = witTemplate;
    }

    @Override // cn.hutool.extra.template.Template
    public void render(Map<?, ?> bindingMap, Writer writer) {
        Map<String, Object> map = (Map) Convert.convert((TypeReference) new TypeReference<Map<String, Object>>() { // from class: cn.hutool.extra.template.engine.wit.WitTemplate.1
        }, (Object) bindingMap);
        this.rawTemplate.merge(map, writer);
    }

    @Override // cn.hutool.extra.template.Template
    public void render(Map<?, ?> bindingMap, OutputStream out) {
        Map<String, Object> map = (Map) Convert.convert((TypeReference) new TypeReference<Map<String, Object>>() { // from class: cn.hutool.extra.template.engine.wit.WitTemplate.2
        }, (Object) bindingMap);
        this.rawTemplate.merge(map, out);
    }
}
