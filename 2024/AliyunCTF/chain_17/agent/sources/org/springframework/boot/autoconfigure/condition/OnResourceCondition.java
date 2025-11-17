package org.springframework.boot.autoconfigure.condition;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.context.logging.LoggingApplicationListener;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;

@Order(LoggingApplicationListener.DEFAULT_ORDER)
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/condition/OnResourceCondition.class */
class OnResourceCondition extends SpringBootCondition {
    OnResourceCondition() {
    }

    @Override // org.springframework.boot.autoconfigure.condition.SpringBootCondition
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        MultiValueMap<String, Object> attributes = metadata.getAllAnnotationAttributes(ConditionalOnResource.class.getName(), true);
        ResourceLoader loader = context.getResourceLoader();
        List<String> locations = new ArrayList<>();
        collectValues(locations, (List) attributes.get("resources"));
        Assert.isTrue(!locations.isEmpty(), "@ConditionalOnResource annotations must specify at least one resource location");
        List<String> missing = new ArrayList<>();
        for (String location : locations) {
            String resource = context.getEnvironment().resolvePlaceholders(location);
            if (!loader.getResource(resource).exists()) {
                missing.add(location);
            }
        }
        if (!missing.isEmpty()) {
            return ConditionOutcome.noMatch(ConditionMessage.forCondition((Class<? extends Annotation>) ConditionalOnResource.class, new Object[0]).didNotFind(DefaultBeanDefinitionDocumentReader.RESOURCE_ATTRIBUTE, "resources").items(ConditionMessage.Style.QUOTE, missing));
        }
        return ConditionOutcome.match(ConditionMessage.forCondition((Class<? extends Annotation>) ConditionalOnResource.class, new Object[0]).found("location", "locations").items(locations));
    }

    private void collectValues(List<String> names, List<Object> values) {
        for (Object value : values) {
            for (Object item : (Object[]) value) {
                names.add((String) item);
            }
        }
    }
}
