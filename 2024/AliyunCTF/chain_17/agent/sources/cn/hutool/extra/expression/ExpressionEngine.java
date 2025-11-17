package cn.hutool.extra.expression;

import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/extra/expression/ExpressionEngine.class */
public interface ExpressionEngine {
    Object eval(String str, Map<String, Object> map);
}
