package cn.hutool.extra.tokenizer.engine;

import cn.hutool.core.lang.Singleton;
import cn.hutool.core.util.ServiceLoaderUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.tokenizer.TokenizerEngine;
import cn.hutool.extra.tokenizer.TokenizerException;
import cn.hutool.log.StaticLog;
import java.lang.invoke.SerializedLambda;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/tokenizer/engine/TokenizerFactory.class */
public class TokenizerFactory {
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
                if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/lang/func/Func0") && lambda.getFunctionalInterfaceMethodName().equals("call") && lambda.getFunctionalInterfaceMethodSignature().equals("()Ljava/lang/Object;") && lambda.getImplClass().equals("cn/hutool/extra/tokenizer/engine/TokenizerFactory") && lambda.getImplMethodSignature().equals("()Lcn/hutool/extra/tokenizer/TokenizerEngine;")) {
                    return TokenizerFactory::create;
                }
                break;
        }
        throw new IllegalArgumentException("Invalid lambda deserialization");
    }

    public static TokenizerEngine get() {
        return (TokenizerEngine) Singleton.get(TokenizerEngine.class.getName(), TokenizerFactory::create);
    }

    public static TokenizerEngine create() {
        TokenizerEngine engine = doCreate();
        StaticLog.debug("Use [{}] Tokenizer Engine As Default.", StrUtil.removeSuffix(engine.getClass().getSimpleName(), "Engine"));
        return engine;
    }

    private static TokenizerEngine doCreate() {
        TokenizerEngine engine = (TokenizerEngine) ServiceLoaderUtil.loadFirstAvailable(TokenizerEngine.class);
        if (null != engine) {
            return engine;
        }
        throw new TokenizerException("No tokenizer found ! Please add some tokenizer jar to your project !");
    }
}
