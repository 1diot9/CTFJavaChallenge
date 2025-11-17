package org.springframework.boot.autoconfigure.condition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.io.Resource;
import org.springframework.core.type.AnnotatedTypeMetadata;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/condition/ResourceCondition.class */
public abstract class ResourceCondition extends SpringBootCondition {
    private final String name;
    private final String property;
    private final String[] resourceLocations;

    /* JADX INFO: Access modifiers changed from: protected */
    public ResourceCondition(String name, String property, String... resourceLocations) {
        this.name = name;
        this.property = property;
        this.resourceLocations = resourceLocations;
    }

    @Override // org.springframework.boot.autoconfigure.condition.SpringBootCondition
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        if (context.getEnvironment().containsProperty(this.property)) {
            return ConditionOutcome.match(startConditionMessage().foundExactly("property " + this.property));
        }
        return getResourceOutcome(context, metadata);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ConditionOutcome getResourceOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        List<String> found = new ArrayList<>();
        for (String location : this.resourceLocations) {
            Resource resource = context.getResourceLoader().getResource(location);
            if (resource != null && resource.exists()) {
                found.add(location);
            }
        }
        if (found.isEmpty()) {
            ConditionMessage message = startConditionMessage().didNotFind(DefaultBeanDefinitionDocumentReader.RESOURCE_ATTRIBUTE, "resources").items(ConditionMessage.Style.QUOTE, Arrays.asList(this.resourceLocations));
            return ConditionOutcome.noMatch(message);
        }
        ConditionMessage message2 = startConditionMessage().found(DefaultBeanDefinitionDocumentReader.RESOURCE_ATTRIBUTE, "resources").items(ConditionMessage.Style.QUOTE, found);
        return ConditionOutcome.match(message2);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final ConditionMessage.Builder startConditionMessage() {
        return ConditionMessage.forCondition("ResourceCondition", "(" + this.name + ")");
    }
}
