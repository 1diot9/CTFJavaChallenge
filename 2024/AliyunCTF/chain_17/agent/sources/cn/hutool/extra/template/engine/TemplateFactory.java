package cn.hutool.extra.template.engine;

import cn.hutool.core.lang.Singleton;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.ServiceLoaderUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateException;
import cn.hutool.log.StaticLog;
import java.lang.invoke.SerializedLambda;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/template/engine/TemplateFactory.class */
public class TemplateFactory {
    private static /* synthetic */ Object $deserializeLambda$(SerializedLambda lambda) {
        String implMethodName = lambda.getImplMethodName();
        boolean z = -1;
        switch (implMethodName.hashCode()) {
            case -1352294148:
                if (implMethodName.equals("create")) {
                    z = false;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
                if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/lang/func/Func0") && lambda.getFunctionalInterfaceMethodName().equals("call") && lambda.getFunctionalInterfaceMethodSignature().equals("()Ljava/lang/Object;") && lambda.getImplClass().equals("cn/hutool/extra/template/engine/TemplateFactory") && lambda.getImplMethodSignature().equals("()Lcn/hutool/extra/template/TemplateEngine;")) {
                    return TemplateFactory::create;
                }
                break;
        }
        throw new IllegalArgumentException("Invalid lambda deserialization");
    }

    public static TemplateEngine get() {
        return (TemplateEngine) Singleton.get(TemplateEngine.class.getName(), TemplateFactory::create);
    }

    public static TemplateEngine create() {
        return create(new TemplateConfig());
    }

    public static TemplateEngine create(TemplateConfig config) {
        TemplateEngine engine = doCreate(config);
        StaticLog.debug("Use [{}] Engine As Default.", StrUtil.removeSuffix(engine.getClass().getSimpleName(), "Engine"));
        return engine;
    }

    private static TemplateEngine doCreate(TemplateConfig config) {
        TemplateEngine engine;
        Class<? extends TemplateEngine> customEngineClass = config.getCustomEngine();
        if (null != customEngineClass) {
            engine = (TemplateEngine) ReflectUtil.newInstance(customEngineClass, new Object[0]);
        } else {
            engine = (TemplateEngine) ServiceLoaderUtil.loadFirstAvailable(TemplateEngine.class);
        }
        if (null != engine) {
            return engine.init(config);
        }
        throw new TemplateException("No template found ! Please add one of template jar to your project !");
    }
}
