package cn.hutool.db.sql;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/db/sql/ConditionGroup.class */
public class ConditionGroup extends Condition {
    private Condition[] conditions;

    /* JADX WARN: Type inference failed for: r1v3, types: [cn.hutool.db.sql.Condition[], java.lang.Object[][]] */
    public void addConditions(Condition... conditions) {
        if (null == this.conditions) {
            this.conditions = conditions;
        } else {
            this.conditions = (Condition[]) ArrayUtil.addAll((Object[][]) new Condition[]{this.conditions, conditions});
        }
    }

    @Override // cn.hutool.db.sql.Condition
    public String toString(List<Object> paramValues) {
        if (ArrayUtil.isEmpty((Object[]) this.conditions)) {
            return "";
        }
        StringBuilder conditionStrBuilder = StrUtil.builder();
        conditionStrBuilder.append("(");
        conditionStrBuilder.append(ConditionBuilder.of(this.conditions).build(paramValues));
        conditionStrBuilder.append(")");
        return conditionStrBuilder.toString();
    }
}
