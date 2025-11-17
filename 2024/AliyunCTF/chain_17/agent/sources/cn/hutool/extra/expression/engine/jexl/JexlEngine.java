package cn.hutool.extra.expression.engine.jexl;

import cn.hutool.extra.expression.ExpressionEngine;
import java.util.Map;
import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.MapContext;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/expression/engine/jexl/JexlEngine.class */
public class JexlEngine implements ExpressionEngine {
    private final org.apache.commons.jexl3.JexlEngine engine = new JexlBuilder().cache(512).strict(true).silent(false).create();

    @Override // cn.hutool.extra.expression.ExpressionEngine
    public Object eval(String expression, Map<String, Object> context) {
        MapContext mapContext = new MapContext(context);
        try {
            return this.engine.createExpression(expression).evaluate(mapContext);
        } catch (Exception e) {
            return this.engine.createScript(expression).execute(mapContext);
        }
    }

    public org.apache.commons.jexl3.JexlEngine getEngine() {
        return this.engine;
    }
}
