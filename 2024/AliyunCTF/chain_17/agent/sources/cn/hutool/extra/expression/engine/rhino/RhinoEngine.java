package cn.hutool.extra.expression.engine.rhino;

import cn.hutool.core.map.MapUtil;
import cn.hutool.extra.expression.ExpressionEngine;
import java.util.Map;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/expression/engine/rhino/RhinoEngine.class */
public class RhinoEngine implements ExpressionEngine {
    @Override // cn.hutool.extra.expression.ExpressionEngine
    public Object eval(String expression, Map<String, Object> context) {
        Context ctx = Context.enter();
        ScriptableObject initStandardObjects = ctx.initStandardObjects();
        if (MapUtil.isNotEmpty(context)) {
            context.forEach((key, value) -> {
                ScriptableObject.putProperty(initStandardObjects, key, Context.javaToJS(value, initStandardObjects));
            });
        }
        Object result = ctx.evaluateString(initStandardObjects, expression, "rhino.js", 1, (Object) null);
        Context.exit();
        return result;
    }
}
