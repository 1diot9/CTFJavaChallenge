package cn.hutool.extra.expression.engine;

import cn.hutool.core.lang.Singleton;
import cn.hutool.core.util.ServiceLoaderUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.expression.ExpressionEngine;
import cn.hutool.extra.expression.ExpressionException;
import cn.hutool.log.StaticLog;
import java.lang.invoke.SerializedLambda;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/expression/engine/ExpressionFactory.class */
public class ExpressionFactory {
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
                if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/lang/func/Func0") && lambda.getFunctionalInterfaceMethodName().equals("call") && lambda.getFunctionalInterfaceMethodSignature().equals("()Ljava/lang/Object;") && lambda.getImplClass().equals("cn/hutool/extra/expression/engine/ExpressionFactory") && lambda.getImplMethodSignature().equals("()Lcn/hutool/extra/expression/ExpressionEngine;")) {
                    return ExpressionFactory::create;
                }
                break;
        }
        throw new IllegalArgumentException("Invalid lambda deserialization");
    }

    public static ExpressionEngine get() {
        return (ExpressionEngine) Singleton.get(ExpressionEngine.class.getName(), ExpressionFactory::create);
    }

    public static ExpressionEngine create() {
        ExpressionEngine engine = doCreate();
        StaticLog.debug("Use [{}] Engine As Default.", StrUtil.removeSuffix(engine.getClass().getSimpleName(), "Engine"));
        return engine;
    }

    private static ExpressionEngine doCreate() {
        ExpressionEngine engine = (ExpressionEngine) ServiceLoaderUtil.loadFirstAvailable(ExpressionEngine.class);
        if (null != engine) {
            return engine;
        }
        throw new ExpressionException("No expression jar found ! Please add one of it to your project !");
    }
}
