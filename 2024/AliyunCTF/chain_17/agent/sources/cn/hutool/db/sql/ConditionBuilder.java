package cn.hutool.db.sql;

import cn.hutool.core.builder.Builder;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ArrayUtil;
import java.util.ArrayList;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/sql/ConditionBuilder.class */
public class ConditionBuilder implements Builder<String> {
    private static final long serialVersionUID = 1;
    private final Condition[] conditions;
    private List<Object> paramValues;

    public static ConditionBuilder of(Condition... conditions) {
        return new ConditionBuilder(conditions);
    }

    public ConditionBuilder(Condition... conditions) {
        this.conditions = conditions;
    }

    public List<Object> getParamValues() {
        return ListUtil.unmodifiable(this.paramValues);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // cn.hutool.core.builder.Builder
    public String build() {
        if (null == this.paramValues) {
            this.paramValues = new ArrayList();
        } else {
            this.paramValues.clear();
        }
        return build(this.paramValues);
    }

    public String build(List<Object> paramValues) {
        if (ArrayUtil.isEmpty((Object[]) this.conditions)) {
            return "";
        }
        StringBuilder conditionStrBuilder = new StringBuilder();
        boolean isFirst = true;
        for (Condition condition : this.conditions) {
            if (isFirst) {
                isFirst = false;
            } else {
                conditionStrBuilder.append(' ').append(condition.getLinkOperator()).append(' ');
            }
            conditionStrBuilder.append(condition.toString(paramValues));
        }
        return conditionStrBuilder.toString();
    }

    public String toString() {
        return build();
    }
}
