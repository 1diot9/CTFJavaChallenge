package cn.hutool.extra.tokenizer;

import cn.hutool.extra.tokenizer.engine.TokenizerFactory;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/tokenizer/TokenizerUtil.class */
public class TokenizerUtil {
    public static TokenizerEngine createEngine() {
        return TokenizerFactory.create();
    }
}
