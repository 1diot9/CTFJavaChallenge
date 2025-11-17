package cn.hutool.captcha.generator;

import cn.hutool.core.util.RandomUtil;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/captcha/generator/AbstractGenerator.class */
public abstract class AbstractGenerator implements CodeGenerator {
    private static final long serialVersionUID = 8685744597154953479L;
    protected final String baseStr;
    protected final int length;

    public AbstractGenerator(int count) {
        this(RandomUtil.BASE_CHAR_NUMBER, count);
    }

    public AbstractGenerator(String baseStr, int length) {
        this.baseStr = baseStr;
        this.length = length;
    }

    public int getLength() {
        return this.length;
    }
}
