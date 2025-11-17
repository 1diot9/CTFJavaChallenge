package org.springframework.boot.autoconfigure.amqp;

import java.util.List;
import java.util.Objects;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.amqp.RabbitRetryTemplateCustomizer;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/amqp/RabbitTemplateConfigurer.class */
public class RabbitTemplateConfigurer {
    private MessageConverter messageConverter;
    private List<RabbitRetryTemplateCustomizer> retryTemplateCustomizers;
    private final RabbitProperties rabbitProperties;

    public RabbitTemplateConfigurer(RabbitProperties rabbitProperties) {
        Assert.notNull(rabbitProperties, "RabbitProperties must not be null");
        this.rabbitProperties = rabbitProperties;
    }

    public void setMessageConverter(MessageConverter messageConverter) {
        this.messageConverter = messageConverter;
    }

    public void setRetryTemplateCustomizers(List<RabbitRetryTemplateCustomizer> retryTemplateCustomizers) {
        this.retryTemplateCustomizers = retryTemplateCustomizers;
    }

    protected final RabbitProperties getRabbitProperties() {
        return this.rabbitProperties;
    }

    public void configure(RabbitTemplate template, ConnectionFactory connectionFactory) {
        PropertyMapper map = PropertyMapper.get();
        template.setConnectionFactory(connectionFactory);
        if (this.messageConverter != null) {
            template.setMessageConverter(this.messageConverter);
        }
        template.setMandatory(determineMandatoryFlag());
        RabbitProperties.Template templateProperties = this.rabbitProperties.getTemplate();
        if (templateProperties.getRetry().isEnabled()) {
            template.setRetryTemplate(new RetryTemplateFactory(this.retryTemplateCustomizers).createRetryTemplate(templateProperties.getRetry(), RabbitRetryTemplateCustomizer.Target.SENDER));
        }
        Objects.requireNonNull(templateProperties);
        PropertyMapper.Source as = map.from(templateProperties::getReceiveTimeout).whenNonNull().as((v0) -> {
            return v0.toMillis();
        });
        Objects.requireNonNull(template);
        as.to((v1) -> {
            r1.setReceiveTimeout(v1);
        });
        Objects.requireNonNull(templateProperties);
        PropertyMapper.Source as2 = map.from(templateProperties::getReplyTimeout).whenNonNull().as((v0) -> {
            return v0.toMillis();
        });
        Objects.requireNonNull(template);
        as2.to((v1) -> {
            r1.setReplyTimeout(v1);
        });
        Objects.requireNonNull(templateProperties);
        PropertyMapper.Source from = map.from(templateProperties::getExchange);
        Objects.requireNonNull(template);
        from.to(template::setExchange);
        Objects.requireNonNull(templateProperties);
        PropertyMapper.Source from2 = map.from(templateProperties::getRoutingKey);
        Objects.requireNonNull(template);
        from2.to(template::setRoutingKey);
        Objects.requireNonNull(templateProperties);
        PropertyMapper.Source whenNonNull = map.from(templateProperties::getDefaultReceiveQueue).whenNonNull();
        Objects.requireNonNull(template);
        whenNonNull.to(template::setDefaultReceiveQueue);
    }

    private boolean determineMandatoryFlag() {
        Boolean mandatory = this.rabbitProperties.getTemplate().getMandatory();
        return mandatory != null ? mandatory.booleanValue() : this.rabbitProperties.isPublisherReturns();
    }
}
