package cn.hutool.extra.template.engine.velocity;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.template.AbstractTemplate;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.Writer;
import java.util.Map;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/template/engine/velocity/VelocityTemplate.class */
public class VelocityTemplate extends AbstractTemplate implements Serializable {
    private static final long serialVersionUID = -132774960373894911L;
    private final Template rawTemplate;
    private String charset;

    public static VelocityTemplate wrap(Template template) {
        if (null == template) {
            return null;
        }
        return new VelocityTemplate(template);
    }

    public VelocityTemplate(Template rawTemplate) {
        this.rawTemplate = rawTemplate;
    }

    @Override // cn.hutool.extra.template.Template
    public void render(Map<?, ?> bindingMap, Writer writer) {
        this.rawTemplate.merge(toContext(bindingMap), writer);
        IoUtil.flush(writer);
    }

    @Override // cn.hutool.extra.template.Template
    public void render(Map<?, ?> bindingMap, OutputStream out) {
        if (null == this.charset) {
            loadEncoding();
        }
        render(bindingMap, IoUtil.getWriter(out, CharsetUtil.charset(this.charset)));
    }

    private VelocityContext toContext(Map<?, ?> bindingMap) {
        Map<String, Object> map = (Map) Convert.convert((TypeReference) new TypeReference<Map<String, Object>>() { // from class: cn.hutool.extra.template.engine.velocity.VelocityTemplate.1
        }, (Object) bindingMap);
        return new VelocityContext(map);
    }

    private void loadEncoding() {
        String charset = (String) Velocity.getProperty("resource.default_encoding");
        this.charset = StrUtil.isEmpty(charset) ? CharsetUtil.UTF_8 : charset;
    }
}
