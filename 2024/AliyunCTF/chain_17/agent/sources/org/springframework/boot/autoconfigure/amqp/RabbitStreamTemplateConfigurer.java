package org.springframework.boot.autoconfigure.amqp;

import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.rabbit.stream.producer.ProducerCustomizer;
import org.springframework.rabbit.stream.producer.RabbitStreamTemplate;
import org.springframework.rabbit.stream.support.converter.StreamMessageConverter;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/amqp/RabbitStreamTemplateConfigurer.class */
public class RabbitStreamTemplateConfigurer {
    private MessageConverter messageConverter;
    private StreamMessageConverter streamMessageConverter;
    private ProducerCustomizer producerCustomizer;

    public void setMessageConverter(MessageConverter messageConverter) {
        this.messageConverter = messageConverter;
    }

    public void setStreamMessageConverter(StreamMessageConverter streamMessageConverter) {
        this.streamMessageConverter = streamMessageConverter;
    }

    public void setProducerCustomizer(ProducerCustomizer producerCustomizer) {
        this.producerCustomizer = producerCustomizer;
    }

    public void configure(RabbitStreamTemplate template) {
        if (this.messageConverter != null) {
            template.setMessageConverter(this.messageConverter);
        }
        if (this.streamMessageConverter != null) {
            template.setStreamConverter(this.streamMessageConverter);
        }
        if (this.producerCustomizer != null) {
            template.setProducerCustomizer(this.producerCustomizer);
        }
    }
}
