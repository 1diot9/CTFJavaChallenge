package cn.hutool.captcha.generator;

import cn.hutool.core.math.Calculator;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/captcha/generator/MathGenerator.class */
public class MathGenerator implements CodeGenerator {
    private static final long serialVersionUID = -5514819971774091076L;
    private static final String operators = "+-*";
    private final int numberLength;

    public MathGenerator() {
        this(2);
    }

    public MathGenerator(int numberLength) {
        this.numberLength = numberLength;
    }

    @Override // cn.hutool.captcha.generator.CodeGenerator
    public String generate() {
        int limit = getLimit();
        String number1 = Integer.toString(RandomUtil.randomInt(limit));
        String number2 = Integer.toString(RandomUtil.randomInt(limit));
        String number12 = StrUtil.padAfter((CharSequence) number1, this.numberLength, ' ');
        return StrUtil.builder().append(number12).append(RandomUtil.randomChar(operators)).append(StrUtil.padAfter((CharSequence) number2, this.numberLength, ' ')).append('=').toString();
    }

    @Override // cn.hutool.captcha.generator.CodeGenerator
    public boolean verify(String code, String userInputCode) {
        try {
            int result = Integer.parseInt(userInputCode);
            int calculateResult = (int) Calculator.conversion(code);
            return result == calculateResult;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public int getLength() {
        return (this.numberLength * 2) + 2;
    }

    private int getLimit() {
        return Integer.parseInt(CustomBooleanEditor.VALUE_1 + StrUtil.repeat('0', this.numberLength));
    }
}
