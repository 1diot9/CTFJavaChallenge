package org.springframework.boot.autoconfigure.amqp;

import java.util.List;
import java.util.concurrent.Executor;
import org.aopalliance.aop.Advice;
import org.springframework.amqp.rabbit.config.AbstractRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.amqp.RabbitRetryTemplateCustomizer;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/amqp/AbstractRabbitListenerContainerFactoryConfigurer.class */
public abstract class AbstractRabbitListenerContainerFactoryConfigurer<T extends AbstractRabbitListenerContainerFactory<?>> {
    private MessageConverter messageConverter;
    private MessageRecoverer messageRecoverer;
    private List<RabbitRetryTemplateCustomizer> retryTemplateCustomizers;
    private final RabbitProperties rabbitProperties;
    private Executor taskExecutor;

    public abstract void configure(T factory, ConnectionFactory connectionFactory);

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractRabbitListenerContainerFactoryConfigurer(RabbitProperties rabbitProperties) {
        this.rabbitProperties = rabbitProperties;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setMessageConverter(MessageConverter messageConverter) {
        this.messageConverter = messageConverter;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setMessageRecoverer(MessageRecoverer messageRecoverer) {
        this.messageRecoverer = messageRecoverer;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setRetryTemplateCustomizers(List<RabbitRetryTemplateCustomizer> retryTemplateCustomizers) {
        this.retryTemplateCustomizers = retryTemplateCustomizers;
    }

    public void setTaskExecutor(Executor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final RabbitProperties getRabbitProperties() {
        return this.rabbitProperties;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void configure(T factory, ConnectionFactory connectionFactory, RabbitProperties.AmqpContainer configuration) {
        Assert.notNull(factory, "Factory must not be null");
        Assert.notNull(connectionFactory, "ConnectionFactory must not be null");
        Assert.notNull(configuration, "Configuration must not be null");
        factory.setConnectionFactory(connectionFactory);
        if (this.messageConverter != null) {
            factory.setMessageConverter(this.messageConverter);
        }
        factory.setAutoStartup(Boolean.valueOf(configuration.isAutoStartup()));
        if (configuration.getAcknowledgeMode() != null) {
            factory.setAcknowledgeMode(configuration.getAcknowledgeMode());
        }
        if (configuration.getPrefetch() != null) {
            factory.setPrefetchCount(configuration.getPrefetch());
        }
        if (configuration.getDefaultRequeueRejected() != null) {
            factory.setDefaultRequeueRejected(configuration.getDefaultRequeueRejected());
        }
        if (configuration.getIdleEventInterval() != null) {
            factory.setIdleEventInterval(Long.valueOf(configuration.getIdleEventInterval().toMillis()));
        }
        factory.setMissingQueuesFatal(Boolean.valueOf(configuration.isMissingQueuesFatal()));
        factory.setDeBatchingEnabled(Boolean.valueOf(configuration.isDeBatchingEnabled()));
        factory.setForceStop(configuration.isForceStop());
        if (this.taskExecutor != null) {
            factory.setTaskExecutor(this.taskExecutor);
        }
        RabbitProperties.ListenerRetry retryConfig = configuration.getRetry();
        if (retryConfig.isEnabled()) {
            RetryInterceptorBuilder.StatelessRetryInterceptorBuilder stateless = retryConfig.isStateless() ? RetryInterceptorBuilder.stateless() : RetryInterceptorBuilder.stateful();
            RetryTemplate retryTemplate = new RetryTemplateFactory(this.retryTemplateCustomizers).createRetryTemplate(retryConfig, RabbitRetryTemplateCustomizer.Target.LISTENER);
            stateless.retryOperations(retryTemplate);
            MessageRecoverer recoverer = this.messageRecoverer != null ? this.messageRecoverer : new RejectAndDontRequeueRecoverer();
            stateless.recoverer(recoverer);
            factory.setAdviceChain(new Advice[]{stateless.build()});
        }
    }
}
