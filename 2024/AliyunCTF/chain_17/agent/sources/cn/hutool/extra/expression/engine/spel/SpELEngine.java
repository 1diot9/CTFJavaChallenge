package cn.hutool.extra.expression.engine.spel;

import cn.hutool.extra.expression.ExpressionEngine;
import java.util.Map;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/expression/engine/spel/SpELEngine.class */
public class SpELEngine implements ExpressionEngine {
    private final ExpressionParser parser = new SpelExpressionParser();

    @Override // cn.hutool.extra.expression.ExpressionEngine
    public Object eval(String expression, Map<String, Object> context) {
        EvaluationContext evaluationContext = new StandardEvaluationContext();
        evaluationContext.getClass();
        context.forEach(evaluationContext::setVariable);
        return this.parser.parseExpression(expression).getValue(evaluationContext);
    }
}
