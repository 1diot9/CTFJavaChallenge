package org.springframework.boot.autoconfigure.context;

import java.time.Duration;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.tags.form.ErrorsTag;

@EnableConfigurationProperties
@AutoConfiguration
@ImportRuntimeHints({MessageSourceRuntimeHints.class})
@ConditionalOnMissingBean(name = {AbstractApplicationContext.MESSAGE_SOURCE_BEAN_NAME}, search = SearchStrategy.CURRENT)
@AutoConfigureOrder(Integer.MIN_VALUE)
@Conditional({ResourceBundleCondition.class})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/context/MessageSourceAutoConfiguration.class */
public class MessageSourceAutoConfiguration {
    private static final Resource[] NO_RESOURCES = new Resource[0];

    @ConfigurationProperties(prefix = "spring.messages")
    @Bean
    public MessageSourceProperties messageSourceProperties() {
        return new MessageSourceProperties();
    }

    @Bean
    public MessageSource messageSource(MessageSourceProperties properties) {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        if (StringUtils.hasText(properties.getBasename())) {
            messageSource.setBasenames(StringUtils.commaDelimitedListToStringArray(StringUtils.trimAllWhitespace(properties.getBasename())));
        }
        if (properties.getEncoding() != null) {
            messageSource.setDefaultEncoding(properties.getEncoding().name());
        }
        messageSource.setFallbackToSystemLocale(properties.isFallbackToSystemLocale());
        Duration cacheDuration = properties.getCacheDuration();
        if (cacheDuration != null) {
            messageSource.setCacheMillis(cacheDuration.toMillis());
        }
        messageSource.setAlwaysUseMessageFormat(properties.isAlwaysUseMessageFormat());
        messageSource.setUseCodeAsDefaultMessage(properties.isUseCodeAsDefaultMessage());
        return messageSource;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/context/MessageSourceAutoConfiguration$ResourceBundleCondition.class */
    protected static class ResourceBundleCondition extends SpringBootCondition {
        private static final ConcurrentReferenceHashMap<String, ConditionOutcome> cache = new ConcurrentReferenceHashMap<>();

        protected ResourceBundleCondition() {
        }

        @Override // org.springframework.boot.autoconfigure.condition.SpringBootCondition
        public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
            String basename = context.getEnvironment().getProperty("spring.messages.basename", ErrorsTag.MESSAGES_ATTRIBUTE);
            ConditionOutcome outcome = cache.get(basename);
            if (outcome == null) {
                outcome = getMatchOutcomeForBasename(context, basename);
                cache.put(basename, outcome);
            }
            return outcome;
        }

        private ConditionOutcome getMatchOutcomeForBasename(ConditionContext context, String basename) {
            ConditionMessage.Builder message = ConditionMessage.forCondition("ResourceBundle", new Object[0]);
            for (String name : StringUtils.commaDelimitedListToStringArray(StringUtils.trimAllWhitespace(basename))) {
                for (Resource resource : getResources(context.getClassLoader(), name)) {
                    if (resource.exists()) {
                        return ConditionOutcome.match(message.found("bundle").items(resource));
                    }
                }
            }
            return ConditionOutcome.noMatch(message.didNotFind("bundle with basename " + basename).atAll());
        }

        private Resource[] getResources(ClassLoader classLoader, String name) {
            String target = name.replace('.', '/');
            try {
                return new PathMatchingResourcePatternResolver(classLoader).getResources("classpath*:" + target + ".properties");
            } catch (Exception e) {
                return MessageSourceAutoConfiguration.NO_RESOURCES;
            }
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/context/MessageSourceAutoConfiguration$MessageSourceRuntimeHints.class */
    static class MessageSourceRuntimeHints implements RuntimeHintsRegistrar {
        MessageSourceRuntimeHints() {
        }

        @Override // org.springframework.aot.hint.RuntimeHintsRegistrar
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            hints.resources().registerPattern("messages.properties").registerPattern("messages_*.properties");
        }
    }
}
