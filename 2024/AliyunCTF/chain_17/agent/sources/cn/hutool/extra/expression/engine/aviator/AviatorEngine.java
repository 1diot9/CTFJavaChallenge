package cn.hutool.extra.expression.engine.aviator;

import cn.hutool.extra.expression.ExpressionEngine;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.AviatorEvaluatorInstance;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/expression/engine/aviator/AviatorEngine.class */
public class AviatorEngine implements ExpressionEngine {
    private final AviatorEvaluatorInstance engine = AviatorEvaluator.getInstance();

    @Override // cn.hutool.extra.expression.ExpressionEngine
    public Object eval(String expression, Map<String, Object> context) {
        return this.engine.execute(expression, context);
    }

    public AviatorEvaluatorInstance getEngine() {
        return this.engine;
    }
}
