package cn.hutool.extra.expression;

import cn.hutool.extra.expression.engine.ExpressionFactory;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/expression/ExpressionUtil.class */
public class ExpressionUtil {
    public static ExpressionEngine getEngine() {
        return ExpressionFactory.get();
    }

    public static Object eval(String expression, Map<String, Object> context) {
        return getEngine().eval(expression, context);
    }
}
