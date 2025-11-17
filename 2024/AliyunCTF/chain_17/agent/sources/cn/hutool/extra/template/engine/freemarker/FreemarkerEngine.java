package cn.hutool.extra.template.engine.freemarker;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateException;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import java.io.IOException;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/template/engine/freemarker/FreemarkerEngine.class */
public class FreemarkerEngine implements TemplateEngine {
    private Configuration cfg;

    public FreemarkerEngine() {
    }

    public FreemarkerEngine(TemplateConfig config) {
        init(config);
    }

    public FreemarkerEngine(Configuration freemarkerCfg) {
        init(freemarkerCfg);
    }

    @Override // cn.hutool.extra.template.TemplateEngine
    public TemplateEngine init(TemplateConfig config) {
        if (null == config) {
            config = TemplateConfig.DEFAULT;
        }
        init(createCfg(config));
        return this;
    }

    private void init(Configuration freemarkerCfg) {
        this.cfg = freemarkerCfg;
    }

    @Override // cn.hutool.extra.template.TemplateEngine
    public Template getTemplate(String resource) {
        if (null == this.cfg) {
            init(TemplateConfig.DEFAULT);
        }
        try {
            return FreemarkerTemplate.wrap(this.cfg.getTemplate(resource));
        } catch (IOException e) {
            throw new IORuntimeException(e);
        } catch (Exception e2) {
            throw new TemplateException(e2);
        }
    }

    public Configuration getConfiguration() {
        return this.cfg;
    }

    private static Configuration createCfg(TemplateConfig config) {
        if (null == config) {
            config = new TemplateConfig();
        }
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_28);
        cfg.setLocalizedLookup(false);
        cfg.setDefaultEncoding(config.getCharset().toString());
        switch (config.getResourceMode()) {
            case CLASSPATH:
                cfg.setTemplateLoader(new ClassTemplateLoader(ClassUtil.getClassLoader(), config.getPath()));
                break;
            case FILE:
                try {
                    cfg.setTemplateLoader(new FileTemplateLoader(FileUtil.file(config.getPath())));
                    break;
                } catch (IOException e) {
                    throw new IORuntimeException(e);
                }
            case WEB_ROOT:
                try {
                    cfg.setTemplateLoader(new FileTemplateLoader(FileUtil.file(FileUtil.getWebRoot(), config.getPath())));
                    break;
                } catch (IOException e2) {
                    throw new IORuntimeException(e2);
                }
            case STRING:
                cfg.setTemplateLoader(new SimpleStringTemplateLoader());
                break;
        }
        return cfg;
    }
}
