package cn.hutool.extra.expression.engine.qlexpress;

import cn.hutool.extra.expression.ExpressionEngine;
import cn.hutool.extra.expression.ExpressionException;
import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import java.util.List;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/expression/engine/qlexpress/QLExpressEngine.class */
public class QLExpressEngine implements ExpressionEngine {
    private final ExpressRunner engine = new ExpressRunner();

    @Override // cn.hutool.extra.expression.ExpressionEngine
    public Object eval(String expression, Map<String, Object> context) {
        DefaultContext<String, Object> defaultContext = new DefaultContext<>();
        defaultContext.putAll(context);
        try {
            return this.engine.execute(expression, defaultContext, (List) null, true, false);
        } catch (Exception e) {
            throw new ExpressionException(e);
        }
    }
}
