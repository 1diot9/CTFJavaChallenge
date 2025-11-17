package org.springframework.boot.autoconfigure.condition;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.AbstractNestedCondition;
import org.springframework.context.annotation.ConfigurationCondition;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/condition/AllNestedConditions.class */
public abstract class AllNestedConditions extends AbstractNestedCondition {
    public AllNestedConditions(ConfigurationCondition.ConfigurationPhase configurationPhase) {
        super(configurationPhase);
    }

    @Override // org.springframework.boot.autoconfigure.condition.AbstractNestedCondition
    protected ConditionOutcome getFinalMatchOutcome(AbstractNestedCondition.MemberMatchOutcomes memberOutcomes) {
        boolean match = hasSameSize(memberOutcomes.getMatches(), memberOutcomes.getAll());
        List<ConditionMessage> messages = new ArrayList<>();
        messages.add(ConditionMessage.forCondition("AllNestedConditions", new Object[0]).because(memberOutcomes.getMatches().size() + " matched " + memberOutcomes.getNonMatches().size() + " did not"));
        for (ConditionOutcome outcome : memberOutcomes.getAll()) {
            messages.add(outcome.getConditionMessage());
        }
        return new ConditionOutcome(match, ConditionMessage.of(messages));
    }

    private boolean hasSameSize(List<?> list1, List<?> list2) {
        return list1.size() == list2.size();
    }
}
