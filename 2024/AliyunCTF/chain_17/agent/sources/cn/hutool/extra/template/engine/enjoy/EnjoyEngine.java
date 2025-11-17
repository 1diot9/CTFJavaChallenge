package cn.hutool.extra.template.engine.enjoy;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import com.jfinal.template.Engine;
import com.jfinal.template.source.FileSourceFactory;
import java.io.File;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/template/engine/enjoy/EnjoyEngine.class */
public class EnjoyEngine implements TemplateEngine {
    private Engine engine;
    private TemplateConfig.ResourceMode resourceMode;

    public EnjoyEngine() {
    }

    public EnjoyEngine(TemplateConfig config) {
        init(config);
    }

    public EnjoyEngine(Engine engine) {
        init(engine);
    }

    @Override // cn.hutool.extra.template.TemplateEngine
    public TemplateEngine init(TemplateConfig config) {
        if (null == config) {
            config = TemplateConfig.DEFAULT;
        }
        this.resourceMode = config.getResourceMode();
        init(createEngine(config));
        return this;
    }

    private void init(Engine engine) {
        this.engine = engine;
    }

    @Override // cn.hutool.extra.template.TemplateEngine
    public Template getTemplate(String resource) {
        if (null == this.engine) {
            init(TemplateConfig.DEFAULT);
        }
        if (ObjectUtil.equal(TemplateConfig.ResourceMode.STRING, this.resourceMode)) {
            return EnjoyTemplate.wrap(this.engine.getTemplateByString(resource));
        }
        return EnjoyTemplate.wrap(this.engine.getTemplate(resource));
    }

    public Engine getRawEngine() {
        return this.engine;
    }

    private static Engine createEngine(TemplateConfig config) {
        Engine engine = Engine.create("Hutool-Enjoy-Engine-" + IdUtil.fastSimpleUUID());
        engine.setEncoding(config.getCharsetStr());
        switch (config.getResourceMode()) {
            case CLASSPATH:
                engine.setToClassPathSourceFactory();
                engine.setBaseTemplatePath(config.getPath());
                break;
            case FILE:
                engine.setSourceFactory(new FileSourceFactory());
                engine.setBaseTemplatePath(config.getPath());
                break;
            case WEB_ROOT:
                engine.setSourceFactory(new FileSourceFactory());
                File root = FileUtil.file(FileUtil.getWebRoot(), config.getPath());
                engine.setBaseTemplatePath(FileUtil.getAbsolutePath(root));
                break;
        }
        return engine;
    }
}
