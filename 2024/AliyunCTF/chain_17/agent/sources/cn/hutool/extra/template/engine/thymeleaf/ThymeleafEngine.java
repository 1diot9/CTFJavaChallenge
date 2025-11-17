package cn.hutool.extra.template.engine.thymeleaf;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.DefaultTemplateResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.StringTemplateResolver;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/template/engine/thymeleaf/ThymeleafEngine.class */
public class ThymeleafEngine implements TemplateEngine {
    org.thymeleaf.TemplateEngine engine;
    TemplateConfig config;

    public ThymeleafEngine() {
    }

    public ThymeleafEngine(TemplateConfig config) {
        init(config);
    }

    public ThymeleafEngine(org.thymeleaf.TemplateEngine engine) {
        init(engine);
    }

    @Override // cn.hutool.extra.template.TemplateEngine
    public TemplateEngine init(TemplateConfig config) {
        if (null == config) {
            config = TemplateConfig.DEFAULT;
        }
        this.config = config;
        init(createEngine(config));
        return this;
    }

    private void init(org.thymeleaf.TemplateEngine engine) {
        this.engine = engine;
    }

    @Override // cn.hutool.extra.template.TemplateEngine
    public Template getTemplate(String resource) {
        if (null == this.engine) {
            init(TemplateConfig.DEFAULT);
        }
        return ThymeleafTemplate.wrap(this.engine, resource, null == this.config ? null : this.config.getCharset());
    }

    public org.thymeleaf.TemplateEngine getRawEngine() {
        return this.engine;
    }

    private static org.thymeleaf.TemplateEngine createEngine(TemplateConfig config) {
        ClassLoaderTemplateResolver defaultTemplateResolver;
        if (null == config) {
            config = new TemplateConfig();
        }
        switch (config.getResourceMode()) {
            case CLASSPATH:
                ClassLoaderTemplateResolver classLoaderResolver = new ClassLoaderTemplateResolver();
                classLoaderResolver.setCharacterEncoding(config.getCharsetStr());
                classLoaderResolver.setTemplateMode(TemplateMode.HTML);
                classLoaderResolver.setPrefix(StrUtil.addSuffixIfNot(config.getPath(), "/"));
                defaultTemplateResolver = classLoaderResolver;
                break;
            case FILE:
                ClassLoaderTemplateResolver fileTemplateResolver = new FileTemplateResolver();
                fileTemplateResolver.setCharacterEncoding(config.getCharsetStr());
                fileTemplateResolver.setTemplateMode(TemplateMode.HTML);
                fileTemplateResolver.setPrefix(StrUtil.addSuffixIfNot(config.getPath(), "/"));
                defaultTemplateResolver = fileTemplateResolver;
                break;
            case WEB_ROOT:
                ClassLoaderTemplateResolver fileTemplateResolver2 = new FileTemplateResolver();
                fileTemplateResolver2.setCharacterEncoding(config.getCharsetStr());
                fileTemplateResolver2.setTemplateMode(TemplateMode.HTML);
                fileTemplateResolver2.setPrefix(StrUtil.addSuffixIfNot(FileUtil.getAbsolutePath(FileUtil.file(FileUtil.getWebRoot(), config.getPath())), "/"));
                defaultTemplateResolver = fileTemplateResolver2;
                break;
            case STRING:
                defaultTemplateResolver = new StringTemplateResolver();
                break;
            default:
                defaultTemplateResolver = new DefaultTemplateResolver();
                break;
        }
        org.thymeleaf.TemplateEngine engine = new org.thymeleaf.TemplateEngine();
        engine.setTemplateResolver(defaultTemplateResolver);
        return engine;
    }
}
