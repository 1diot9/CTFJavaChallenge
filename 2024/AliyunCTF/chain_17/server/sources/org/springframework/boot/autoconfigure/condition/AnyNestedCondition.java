package org.springframework.boot.autoconfigure.condition;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.AbstractNestedCondition;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.core.annotation.Order;

@Order(2147483627)
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/condition/AnyNestedCondition.class */
public abstract class AnyNestedCondition extends AbstractNestedCondition {
    public AnyNestedCondition(ConfigurationCondition.ConfigurationPhase configurationPhase) {
        super(configurationPhase);
    }

    @Override // org.springframework.boot.autoconfigure.condition.AbstractNestedCondition
    protected ConditionOutcome getFinalMatchOutcome(AbstractNestedCondition.MemberMatchOutcomes memberOutcomes) {
        boolean match = !memberOutcomes.getMatches().isEmpty();
        List<ConditionMessage> messages = new ArrayList<>();
        messages.add(ConditionMessage.forCondition("AnyNestedCondition", new Object[0]).because(memberOutcomes.getMatches().size() + " matched " + memberOutcomes.getNonMatches().size() + " did not"));
        for (ConditionOutcome outcome : memberOutcomes.getAll()) {
            messages.add(outcome.getConditionMessage());
        }
        return new ConditionOutcome(match, ConditionMessage.of(messages));
    }
}
