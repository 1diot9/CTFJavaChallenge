package cn.hutool.extra.template.engine.jetbrick;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.extra.template.AbstractTemplate;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.Writer;
import java.util.Map;
import jetbrick.template.JetTemplate;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/template/engine/jetbrick/JetbrickTemplate.class */
public class JetbrickTemplate extends AbstractTemplate implements Serializable {
    private static final long serialVersionUID = 1;
    private final JetTemplate rawTemplate;

    public static JetbrickTemplate wrap(JetTemplate jetTemplate) {
        if (null == jetTemplate) {
            return null;
        }
        return new JetbrickTemplate(jetTemplate);
    }

    public JetbrickTemplate(JetTemplate jetTemplate) {
        this.rawTemplate = jetTemplate;
    }

    @Override // cn.hutool.extra.template.Template
    public void render(Map<?, ?> bindingMap, Writer writer) {
        Map<String, Object> map = (Map) Convert.convert((TypeReference) new TypeReference<Map<String, Object>>() { // from class: cn.hutool.extra.template.engine.jetbrick.JetbrickTemplate.1
        }, (Object) bindingMap);
        this.rawTemplate.render(map, writer);
    }

    @Override // cn.hutool.extra.template.Template
    public void render(Map<?, ?> bindingMap, OutputStream out) {
        Map<String, Object> map = (Map) Convert.convert((TypeReference) new TypeReference<Map<String, Object>>() { // from class: cn.hutool.extra.template.engine.jetbrick.JetbrickTemplate.2
        }, (Object) bindingMap);
        this.rawTemplate.render(map, out);
    }
}
