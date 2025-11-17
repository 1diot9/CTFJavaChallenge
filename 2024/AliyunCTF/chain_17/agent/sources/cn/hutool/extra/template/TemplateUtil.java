package cn.hutool.extra.template;

import cn.hutool.extra.template.engine.TemplateFactory;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/template/TemplateUtil.class */
public class TemplateUtil {
    public static TemplateEngine createEngine() {
        return TemplateFactory.create();
    }

    public static TemplateEngine createEngine(TemplateConfig config) {
        return TemplateFactory.create(config);
    }
}
