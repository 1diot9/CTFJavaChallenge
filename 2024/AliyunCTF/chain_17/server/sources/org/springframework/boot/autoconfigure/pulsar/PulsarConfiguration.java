package org.springframework.boot.autoconfigure.pulsar;

import java.util.ArrayList;
import java.util.List;
import org.apache.pulsar.client.admin.PulsarAdminBuilder;
import org.apache.pulsar.client.api.ClientBuilder;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.common.schema.SchemaType;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.pulsar.PulsarProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.util.LambdaSafe;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.pulsar.core.DefaultPulsarClientFactory;
import org.springframework.pulsar.core.DefaultSchemaResolver;
import org.springframework.pulsar.core.DefaultTopicResolver;
import org.springframework.pulsar.core.PulsarAdminBuilderCustomizer;
import org.springframework.pulsar.core.PulsarAdministration;
import org.springframework.pulsar.core.PulsarClientBuilderCustomizer;
import org.springframework.pulsar.core.PulsarClientFactory;
import org.springframework.pulsar.core.SchemaResolver;
import org.springframework.pulsar.core.TopicResolver;
import org.springframework.pulsar.function.PulsarFunction;
import org.springframework.pulsar.function.PulsarFunctionAdministration;
import org.springframework.pulsar.function.PulsarSink;
import org.springframework.pulsar.function.PulsarSource;

@EnableConfigurationProperties({PulsarProperties.class})
@Configuration(proxyBeanMethods = false)
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/pulsar/PulsarConfiguration.class */
class PulsarConfiguration {
    private final PulsarProperties properties;
    private final PulsarPropertiesMapper propertiesMapper;

    PulsarConfiguration(PulsarProperties properties) {
        this.properties = properties;
        this.propertiesMapper = new PulsarPropertiesMapper(properties);
    }

    @ConditionalOnMissingBean({PulsarConnectionDetails.class})
    @Bean
    PropertiesPulsarConnectionDetails pulsarConnectionDetails() {
        return new PropertiesPulsarConnectionDetails(this.properties);
    }

    @ConditionalOnMissingBean({PulsarClientFactory.class})
    @Bean
    DefaultPulsarClientFactory pulsarClientFactory(PulsarConnectionDetails connectionDetails, ObjectProvider<PulsarClientBuilderCustomizer> customizersProvider) {
        List<PulsarClientBuilderCustomizer> allCustomizers = new ArrayList<>();
        allCustomizers.add(builder -> {
            this.propertiesMapper.customizeClientBuilder(builder, connectionDetails);
        });
        allCustomizers.addAll(customizersProvider.orderedStream().toList());
        DefaultPulsarClientFactory clientFactory = new DefaultPulsarClientFactory(clientBuilder -> {
            applyClientBuilderCustomizers(allCustomizers, clientBuilder);
        });
        return clientFactory;
    }

    private void applyClientBuilderCustomizers(List<PulsarClientBuilderCustomizer> customizers, ClientBuilder clientBuilder) {
        customizers.forEach(customizer -> {
            customizer.customize(clientBuilder);
        });
    }

    @ConditionalOnMissingBean
    @Bean
    PulsarClient pulsarClient(PulsarClientFactory clientFactory) throws PulsarClientException {
        return clientFactory.createClient();
    }

    @ConditionalOnMissingBean
    @Bean
    PulsarAdministration pulsarAdministration(PulsarConnectionDetails connectionDetails, ObjectProvider<PulsarAdminBuilderCustomizer> pulsarAdminBuilderCustomizers) {
        List<PulsarAdminBuilderCustomizer> allCustomizers = new ArrayList<>();
        allCustomizers.add(builder -> {
            this.propertiesMapper.customizeAdminBuilder(builder, connectionDetails);
        });
        allCustomizers.addAll(pulsarAdminBuilderCustomizers.orderedStream().toList());
        return new PulsarAdministration(adminBuilder -> {
            applyAdminBuilderCustomizers(allCustomizers, adminBuilder);
        });
    }

    private void applyAdminBuilderCustomizers(List<PulsarAdminBuilderCustomizer> customizers, PulsarAdminBuilder adminBuilder) {
        customizers.forEach(customizer -> {
            customizer.customize(adminBuilder);
        });
    }

    @ConditionalOnMissingBean({SchemaResolver.class})
    @Bean
    DefaultSchemaResolver pulsarSchemaResolver(ObjectProvider<SchemaResolver.SchemaResolverCustomizer<?>> schemaResolverCustomizers) {
        DefaultSchemaResolver schemaResolver = new DefaultSchemaResolver();
        addCustomSchemaMappings(schemaResolver, this.properties.getDefaults().getTypeMappings());
        applySchemaResolverCustomizers(schemaResolverCustomizers.orderedStream().toList(), schemaResolver);
        return schemaResolver;
    }

    private void addCustomSchemaMappings(DefaultSchemaResolver schemaResolver, List<PulsarProperties.Defaults.TypeMapping> typeMappings) {
        if (typeMappings != null) {
            typeMappings.forEach(typeMapping -> {
                addCustomSchemaMapping(schemaResolver, typeMapping);
            });
        }
    }

    private void addCustomSchemaMapping(DefaultSchemaResolver schemaResolver, PulsarProperties.Defaults.TypeMapping typeMapping) {
        PulsarProperties.Defaults.SchemaInfo schemaInfo = typeMapping.schemaInfo();
        if (schemaInfo != null) {
            Class<?> messageType = typeMapping.messageType();
            SchemaType schemaType = schemaInfo.schemaType();
            Class<?> messageKeyType = schemaInfo.messageKeyType();
            Schema<?> schema = (Schema) schemaResolver.resolveSchema(schemaType, messageType, messageKeyType).orElseThrow();
            schemaResolver.addCustomSchemaMapping(typeMapping.messageType(), schema);
        }
    }

    private void applySchemaResolverCustomizers(List<SchemaResolver.SchemaResolverCustomizer<?>> customizers, DefaultSchemaResolver schemaResolver) {
        LambdaSafe.callbacks(SchemaResolver.SchemaResolverCustomizer.class, customizers, schemaResolver, new Object[0]).invoke(customizer -> {
            customizer.customize(schemaResolver);
        });
    }

    @ConditionalOnMissingBean({TopicResolver.class})
    @Bean
    DefaultTopicResolver pulsarTopicResolver() {
        DefaultTopicResolver topicResolver = new DefaultTopicResolver();
        List<PulsarProperties.Defaults.TypeMapping> typeMappings = this.properties.getDefaults().getTypeMappings();
        if (typeMappings != null) {
            typeMappings.forEach(typeMapping -> {
                addCustomTopicMapping(topicResolver, typeMapping);
            });
        }
        return topicResolver;
    }

    private void addCustomTopicMapping(DefaultTopicResolver topicResolver, PulsarProperties.Defaults.TypeMapping typeMapping) {
        String topicName = typeMapping.topicName();
        if (topicName != null) {
            topicResolver.addCustomTopicMapping(typeMapping.messageType(), topicName);
        }
    }

    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = {"spring.pulsar.function.enabled"}, havingValue = "true", matchIfMissing = true)
    @Bean
    PulsarFunctionAdministration pulsarFunctionAdministration(PulsarAdministration pulsarAdministration, ObjectProvider<PulsarFunction> pulsarFunctions, ObjectProvider<PulsarSink> pulsarSinks, ObjectProvider<PulsarSource> pulsarSources) {
        PulsarProperties.Function properties = this.properties.getFunction();
        return new PulsarFunctionAdministration(pulsarAdministration, pulsarFunctions, pulsarSinks, pulsarSources, properties.isFailFast(), properties.isPropagateFailures(), properties.isPropagateStopFailures());
    }
}
