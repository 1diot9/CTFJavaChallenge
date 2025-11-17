package org.springframework.boot.autoconfigure.mail;

import jakarta.activation.MimeType;
import jakarta.mail.internet.MimeMessage;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.context.annotation.Import;
import org.springframework.mail.MailSender;

@EnableConfigurationProperties({MailProperties.class})
@AutoConfiguration
@ConditionalOnClass({MimeMessage.class, MimeType.class, MailSender.class})
@ConditionalOnMissingBean({MailSender.class})
@Conditional({MailSenderCondition.class})
@Import({MailSenderJndiConfiguration.class, MailSenderPropertiesConfiguration.class})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/mail/MailSenderAutoConfiguration.class */
public class MailSenderAutoConfiguration {

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/mail/MailSenderAutoConfiguration$MailSenderCondition.class */
    static class MailSenderCondition extends AnyNestedCondition {
        MailSenderCondition() {
            super(ConfigurationCondition.ConfigurationPhase.PARSE_CONFIGURATION);
        }

        @ConditionalOnProperty(prefix = "spring.mail", name = {"host"})
        /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/mail/MailSenderAutoConfiguration$MailSenderCondition$HostProperty.class */
        static class HostProperty {
            HostProperty() {
            }
        }

        @ConditionalOnProperty(prefix = "spring.mail", name = {"jndi-name"})
        /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/mail/MailSenderAutoConfiguration$MailSenderCondition$JndiNameProperty.class */
        static class JndiNameProperty {
            JndiNameProperty() {
            }
        }
    }
}
