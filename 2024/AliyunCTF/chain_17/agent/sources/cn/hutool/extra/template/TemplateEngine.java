package cn.hutool.extra.template;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/template/TemplateEngine.class */
public interface TemplateEngine {
    TemplateEngine init(TemplateConfig templateConfig);

    Template getTemplate(String str);
}
