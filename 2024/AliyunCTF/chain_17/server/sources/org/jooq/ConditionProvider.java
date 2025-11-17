package org.jooq;

import java.util.Collection;

@Deprecated(forRemoval = true, since = "2.6")
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/ConditionProvider.class */
public interface ConditionProvider {
    @Support
    void addConditions(Condition condition);

    @Support
    void addConditions(Condition... conditionArr);

    @Support
    void addConditions(Collection<? extends Condition> collection);

    @Support
    void addConditions(Operator operator, Condition condition);

    @Support
    void addConditions(Operator operator, Condition... conditionArr);

    @Support
    void addConditions(Operator operator, Collection<? extends Condition> collection);
}
