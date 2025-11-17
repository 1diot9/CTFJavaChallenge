package cn.hutool.captcha.generator;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/captcha/generator/RandomGenerator.class */
public class RandomGenerator extends AbstractGenerator {
    private static final long serialVersionUID = -7802758587765561876L;

    public RandomGenerator(int count) {
        super(count);
    }

    public RandomGenerator(String baseStr, int length) {
        super(baseStr, length);
    }

    @Override // cn.hutool.captcha.generator.CodeGenerator
    public String generate() {
        return RandomUtil.randomString(this.baseStr, this.length);
    }

    @Override // cn.hutool.captcha.generator.CodeGenerator
    public boolean verify(String code, String userInputCode) {
        if (StrUtil.isNotBlank(userInputCode)) {
            return StrUtil.equalsIgnoreCase(code, userInputCode);
        }
        return false;
    }
}
