package cn.hutool.extra.expression.engine.jfireel;

import cn.hutool.extra.expression.ExpressionEngine;
import com.jfirer.jfireel.expression.Expression;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/expression/engine/jfireel/JfireELEngine.class */
public class JfireELEngine implements ExpressionEngine {
    @Override // cn.hutool.extra.expression.ExpressionEngine
    public Object eval(String expression, Map<String, Object> context) {
        return Expression.parse(expression).calculate(context);
    }
}
