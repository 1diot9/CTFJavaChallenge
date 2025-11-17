package org.springframework.boot.autoconfigure.jms;

import jakarta.jms.ConnectionFactory;
import java.util.Arrays;
import javax.naming.NamingException;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnJndi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jndi.JndiLocatorDelegate;
import org.springframework.util.StringUtils;

@EnableConfigurationProperties({JmsProperties.class})
@AutoConfiguration(before = {JmsAutoConfiguration.class})
@ConditionalOnClass({JmsTemplate.class})
@ConditionalOnMissingBean({ConnectionFactory.class})
@Conditional({JndiOrPropertyCondition.class})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jms/JndiConnectionFactoryAutoConfiguration.class */
public class JndiConnectionFactoryAutoConfiguration {
    private static final String[] JNDI_LOCATIONS = {"java:/JmsXA", "java:/XAConnectionFactory"};

    @Bean
    public ConnectionFactory jmsConnectionFactory(JmsProperties properties) throws NamingException {
        JndiLocatorDelegate jndiLocatorDelegate = JndiLocatorDelegate.createDefaultResourceRefLocator();
        if (StringUtils.hasLength(properties.getJndiName())) {
            return (ConnectionFactory) jndiLocatorDelegate.lookup(properties.getJndiName(), ConnectionFactory.class);
        }
        return findJndiConnectionFactory(jndiLocatorDelegate);
    }

    private ConnectionFactory findJndiConnectionFactory(JndiLocatorDelegate jndiLocatorDelegate) {
        for (String name : JNDI_LOCATIONS) {
            try {
                return (ConnectionFactory) jndiLocatorDelegate.lookup(name, ConnectionFactory.class);
            } catch (NamingException e) {
            }
        }
        throw new IllegalStateException("Unable to find ConnectionFactory in JNDI locations " + Arrays.asList(JNDI_LOCATIONS));
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jms/JndiConnectionFactoryAutoConfiguration$JndiOrPropertyCondition.class */
    static class JndiOrPropertyCondition extends AnyNestedCondition {
        JndiOrPropertyCondition() {
            super(ConfigurationCondition.ConfigurationPhase.PARSE_CONFIGURATION);
        }

        @ConditionalOnJndi({"java:/JmsXA", "java:/XAConnectionFactory"})
        /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jms/JndiConnectionFactoryAutoConfiguration$JndiOrPropertyCondition$Jndi.class */
        static class Jndi {
            Jndi() {
            }
        }

        @ConditionalOnProperty(prefix = "spring.jms", name = {"jndi-name"})
        /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jms/JndiConnectionFactoryAutoConfiguration$JndiOrPropertyCondition$Property.class */
        static class Property {
            Property() {
            }
        }
    }
}
