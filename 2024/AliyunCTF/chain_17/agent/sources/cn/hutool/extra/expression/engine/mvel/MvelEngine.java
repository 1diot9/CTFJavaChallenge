package cn.hutool.extra.expression.engine.mvel;

import cn.hutool.extra.expression.ExpressionEngine;
import java.util.Map;
import org.mvel2.MVEL;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/expression/engine/mvel/MvelEngine.class */
public class MvelEngine implements ExpressionEngine {
    @Override // cn.hutool.extra.expression.ExpressionEngine
    public Object eval(String expression, Map<String, Object> context) {
        return MVEL.eval(expression, context);
    }
}
