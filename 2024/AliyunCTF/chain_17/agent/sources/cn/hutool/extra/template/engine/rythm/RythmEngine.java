package cn.hutool.extra.template.engine.rythm;

import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import java.util.Properties;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/template/engine/rythm/RythmEngine.class */
public class RythmEngine implements TemplateEngine {
    org.rythmengine.RythmEngine engine;

    public RythmEngine() {
    }

    public RythmEngine(TemplateConfig config) {
        init(config);
    }

    public RythmEngine(org.rythmengine.RythmEngine engine) {
        init(engine);
    }

    @Override // cn.hutool.extra.template.TemplateEngine
    public TemplateEngine init(TemplateConfig config) {
        if (null == config) {
            config = TemplateConfig.DEFAULT;
        }
        init(createEngine(config));
        return this;
    }

    public org.rythmengine.RythmEngine getRawEngine() {
        return this.engine;
    }

    private void init(org.rythmengine.RythmEngine engine) {
        this.engine = engine;
    }

    @Override // cn.hutool.extra.template.TemplateEngine
    public Template getTemplate(String resource) {
        if (null == this.engine) {
            init(TemplateConfig.DEFAULT);
        }
        return RythmTemplate.wrap(this.engine.getTemplate(resource, new Object[0]));
    }

    private static org.rythmengine.RythmEngine createEngine(TemplateConfig config) {
        if (null == config) {
            config = new TemplateConfig();
        }
        Properties props = new Properties();
        String path = config.getPath();
        if (null != path) {
            props.put("home.template", path);
        }
        return new org.rythmengine.RythmEngine(props);
    }
}
