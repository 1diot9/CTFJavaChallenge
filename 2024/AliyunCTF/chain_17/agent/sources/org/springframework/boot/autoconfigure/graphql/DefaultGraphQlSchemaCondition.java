package org.springframework.boot.autoconfigure.graphql;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.AnnotatedTypeMetadata;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/graphql/DefaultGraphQlSchemaCondition.class */
class DefaultGraphQlSchemaCondition extends SpringBootCondition implements ConfigurationCondition {
    DefaultGraphQlSchemaCondition() {
    }

    @Override // org.springframework.context.annotation.ConfigurationCondition
    public ConfigurationCondition.ConfigurationPhase getConfigurationPhase() {
        return ConfigurationCondition.ConfigurationPhase.REGISTER_BEAN;
    }

    @Override // org.springframework.boot.autoconfigure.condition.SpringBootCondition
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        boolean match = false;
        List<ConditionMessage> messages = new ArrayList<>(2);
        ConditionMessage.Builder message = ConditionMessage.forCondition((Class<? extends Annotation>) ConditionalOnGraphQlSchema.class, new Object[0]);
        Binder binder = Binder.get(context.getEnvironment());
        GraphQlProperties.Schema schema = (GraphQlProperties.Schema) binder.bind("spring.graphql.schema", GraphQlProperties.Schema.class).orElse(new GraphQlProperties.Schema());
        ResourcePatternResolver resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(context.getResourceLoader());
        List<Resource> schemaResources = resolveSchemaResources(resourcePatternResolver, schema.getLocations(), schema.getFileExtensions());
        if (!schemaResources.isEmpty()) {
            match = true;
            messages.add(message.found("schema", "schemas").items(ConditionMessage.Style.QUOTE, schemaResources));
        } else {
            messages.add(message.didNotFind("schema files in locations").items(ConditionMessage.Style.QUOTE, Arrays.asList(schema.getLocations())));
        }
        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        String[] customizerBeans = beanFactory.getBeanNamesForType(GraphQlSourceBuilderCustomizer.class, false, false);
        if (customizerBeans.length != 0) {
            match = true;
            messages.add(message.found("customizer", "customizers").items(Arrays.asList(customizerBeans)));
        } else {
            messages.add(message.didNotFind("GraphQlSourceBuilderCustomizer").atAll());
        }
        return new ConditionOutcome(match, ConditionMessage.of(messages));
    }

    private List<Resource> resolveSchemaResources(ResourcePatternResolver resolver, String[] locations, String[] extensions) {
        List<Resource> resources = new ArrayList<>();
        for (String location : locations) {
            for (String extension : extensions) {
                resources.addAll(resolveSchemaResources(resolver, location + "*" + extension));
            }
        }
        return resources;
    }

    private List<Resource> resolveSchemaResources(ResourcePatternResolver resolver, String pattern) {
        try {
            return Arrays.asList(resolver.getResources(pattern));
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }
}
