package org.springframework.boot.autoconfigure.transaction.jta;

import jakarta.transaction.Transaction;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.XADataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.artemis.ArtemisAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizationAutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration(before = {XADataSourceAutoConfiguration.class, ActiveMQAutoConfiguration.class, ArtemisAutoConfiguration.class, HibernateJpaAutoConfiguration.class, TransactionAutoConfiguration.class, TransactionManagerCustomizationAutoConfiguration.class})
@ConditionalOnClass({Transaction.class})
@ConditionalOnProperty(prefix = "spring.jta", value = {"enabled"}, matchIfMissing = true)
@Import({JndiJtaConfiguration.class})
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/transaction/jta/JtaAutoConfiguration.class */
public class JtaAutoConfiguration {
}
