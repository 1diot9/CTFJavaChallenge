package org.springframework.boot.autoconfigure.kafka;

import java.util.Objects;
import java.util.function.Function;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.AfterRollbackProcessor;
import org.springframework.kafka.listener.BatchInterceptor;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.ConsumerAwareRebalanceListener;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.listener.RecordInterceptor;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;
import org.springframework.kafka.support.converter.BatchMessageConverter;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.transaction.KafkaAwareTransactionManager;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/kafka/ConcurrentKafkaListenerContainerFactoryConfigurer.class */
public class ConcurrentKafkaListenerContainerFactoryConfigurer {
    private KafkaProperties properties;
    private BatchMessageConverter batchMessageConverter;
    private RecordMessageConverter recordMessageConverter;
    private RecordFilterStrategy<Object, Object> recordFilterStrategy;
    private KafkaTemplate<Object, Object> replyTemplate;
    private KafkaAwareTransactionManager<Object, Object> transactionManager;
    private ConsumerAwareRebalanceListener rebalanceListener;
    private CommonErrorHandler commonErrorHandler;
    private AfterRollbackProcessor<Object, Object> afterRollbackProcessor;
    private RecordInterceptor<Object, Object> recordInterceptor;
    private BatchInterceptor<Object, Object> batchInterceptor;
    private Function<MessageListenerContainer, String> threadNameSupplier;
    private SimpleAsyncTaskExecutor listenerTaskExecutor;

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setKafkaProperties(KafkaProperties properties) {
        this.properties = properties;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setBatchMessageConverter(BatchMessageConverter batchMessageConverter) {
        this.batchMessageConverter = batchMessageConverter;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setRecordMessageConverter(RecordMessageConverter recordMessageConverter) {
        this.recordMessageConverter = recordMessageConverter;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setRecordFilterStrategy(RecordFilterStrategy<Object, Object> recordFilterStrategy) {
        this.recordFilterStrategy = recordFilterStrategy;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setReplyTemplate(KafkaTemplate<Object, Object> replyTemplate) {
        this.replyTemplate = replyTemplate;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setTransactionManager(KafkaAwareTransactionManager<Object, Object> transactionManager) {
        this.transactionManager = transactionManager;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setRebalanceListener(ConsumerAwareRebalanceListener rebalanceListener) {
        this.rebalanceListener = rebalanceListener;
    }

    public void setCommonErrorHandler(CommonErrorHandler commonErrorHandler) {
        this.commonErrorHandler = commonErrorHandler;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setAfterRollbackProcessor(AfterRollbackProcessor<Object, Object> afterRollbackProcessor) {
        this.afterRollbackProcessor = afterRollbackProcessor;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setRecordInterceptor(RecordInterceptor<Object, Object> recordInterceptor) {
        this.recordInterceptor = recordInterceptor;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setBatchInterceptor(BatchInterceptor<Object, Object> batchInterceptor) {
        this.batchInterceptor = batchInterceptor;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setThreadNameSupplier(Function<MessageListenerContainer, String> threadNameSupplier) {
        this.threadNameSupplier = threadNameSupplier;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setListenerTaskExecutor(SimpleAsyncTaskExecutor listenerTaskExecutor) {
        this.listenerTaskExecutor = listenerTaskExecutor;
    }

    public void configure(ConcurrentKafkaListenerContainerFactory<Object, Object> listenerFactory, ConsumerFactory<Object, Object> consumerFactory) {
        listenerFactory.setConsumerFactory(consumerFactory);
        configureListenerFactory(listenerFactory);
        configureContainer(listenerFactory.getContainerProperties());
    }

    private void configureListenerFactory(ConcurrentKafkaListenerContainerFactory<Object, Object> factory) {
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        KafkaProperties.Listener properties = this.properties.getListener();
        Objects.requireNonNull(properties);
        PropertyMapper.Source from = map.from(properties::getConcurrency);
        Objects.requireNonNull(factory);
        from.to(factory::setConcurrency);
        Objects.requireNonNull(properties);
        PropertyMapper.Source from2 = map.from(properties::isAutoStartup);
        Objects.requireNonNull(factory);
        from2.to(factory::setAutoStartup);
        PropertyMapper.Source from3 = map.from((PropertyMapper) this.batchMessageConverter);
        Objects.requireNonNull(factory);
        from3.to(factory::setBatchMessageConverter);
        PropertyMapper.Source from4 = map.from((PropertyMapper) this.recordMessageConverter);
        Objects.requireNonNull(factory);
        from4.to(factory::setRecordMessageConverter);
        PropertyMapper.Source from5 = map.from((PropertyMapper) this.recordFilterStrategy);
        Objects.requireNonNull(factory);
        from5.to(factory::setRecordFilterStrategy);
        PropertyMapper.Source from6 = map.from((PropertyMapper) this.replyTemplate);
        Objects.requireNonNull(factory);
        from6.to(factory::setReplyTemplate);
        if (properties.getType().equals(KafkaProperties.Listener.Type.BATCH)) {
            factory.setBatchListener(true);
        }
        PropertyMapper.Source from7 = map.from((PropertyMapper) this.commonErrorHandler);
        Objects.requireNonNull(factory);
        from7.to(factory::setCommonErrorHandler);
        PropertyMapper.Source from8 = map.from((PropertyMapper) this.afterRollbackProcessor);
        Objects.requireNonNull(factory);
        from8.to(factory::setAfterRollbackProcessor);
        PropertyMapper.Source from9 = map.from((PropertyMapper) this.recordInterceptor);
        Objects.requireNonNull(factory);
        from9.to(factory::setRecordInterceptor);
        PropertyMapper.Source from10 = map.from((PropertyMapper) this.batchInterceptor);
        Objects.requireNonNull(factory);
        from10.to(factory::setBatchInterceptor);
        PropertyMapper.Source from11 = map.from((PropertyMapper) this.threadNameSupplier);
        Objects.requireNonNull(factory);
        from11.to(factory::setThreadNameSupplier);
        Objects.requireNonNull(properties);
        PropertyMapper.Source from12 = map.from(properties::getChangeConsumerThreadName);
        Objects.requireNonNull(factory);
        from12.to((v1) -> {
            r1.setChangeConsumerThreadName(v1);
        });
    }

    private void configureContainer(ContainerProperties container) {
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        KafkaProperties.Listener properties = this.properties.getListener();
        Objects.requireNonNull(properties);
        PropertyMapper.Source from = map.from(properties::getAckMode);
        Objects.requireNonNull(container);
        from.to(container::setAckMode);
        Objects.requireNonNull(properties);
        PropertyMapper.Source from2 = map.from(properties::getAsyncAcks);
        Objects.requireNonNull(container);
        from2.to((v1) -> {
            r1.setAsyncAcks(v1);
        });
        Objects.requireNonNull(properties);
        PropertyMapper.Source from3 = map.from(properties::getClientId);
        Objects.requireNonNull(container);
        from3.to(container::setClientId);
        Objects.requireNonNull(properties);
        PropertyMapper.Source from4 = map.from(properties::getAckCount);
        Objects.requireNonNull(container);
        from4.to((v1) -> {
            r1.setAckCount(v1);
        });
        Objects.requireNonNull(properties);
        PropertyMapper.Source as = map.from(properties::getAckTime).as((v0) -> {
            return v0.toMillis();
        });
        Objects.requireNonNull(container);
        as.to((v1) -> {
            r1.setAckTime(v1);
        });
        Objects.requireNonNull(properties);
        PropertyMapper.Source as2 = map.from(properties::getPollTimeout).as((v0) -> {
            return v0.toMillis();
        });
        Objects.requireNonNull(container);
        as2.to((v1) -> {
            r1.setPollTimeout(v1);
        });
        Objects.requireNonNull(properties);
        PropertyMapper.Source from5 = map.from(properties::getNoPollThreshold);
        Objects.requireNonNull(container);
        from5.to((v1) -> {
            r1.setNoPollThreshold(v1);
        });
        PropertyMapper.Source as3 = map.from((PropertyMapper) properties.getIdleBetweenPolls()).as((v0) -> {
            return v0.toMillis();
        });
        Objects.requireNonNull(container);
        as3.to((v1) -> {
            r1.setIdleBetweenPolls(v1);
        });
        Objects.requireNonNull(properties);
        PropertyMapper.Source as4 = map.from(properties::getIdleEventInterval).as((v0) -> {
            return v0.toMillis();
        });
        Objects.requireNonNull(container);
        as4.to(container::setIdleEventInterval);
        Objects.requireNonNull(properties);
        PropertyMapper.Source as5 = map.from(properties::getIdlePartitionEventInterval).as((v0) -> {
            return v0.toMillis();
        });
        Objects.requireNonNull(container);
        as5.to(container::setIdlePartitionEventInterval);
        Objects.requireNonNull(properties);
        PropertyMapper.Source as6 = map.from(properties::getMonitorInterval).as((v0) -> {
            return v0.getSeconds();
        }).as((v0) -> {
            return v0.intValue();
        });
        Objects.requireNonNull(container);
        as6.to((v1) -> {
            r1.setMonitorInterval(v1);
        });
        Objects.requireNonNull(properties);
        PropertyMapper.Source from6 = map.from(properties::getLogContainerConfig);
        Objects.requireNonNull(container);
        from6.to((v1) -> {
            r1.setLogContainerConfig(v1);
        });
        Objects.requireNonNull(properties);
        PropertyMapper.Source from7 = map.from(properties::isMissingTopicsFatal);
        Objects.requireNonNull(container);
        from7.to((v1) -> {
            r1.setMissingTopicsFatal(v1);
        });
        Objects.requireNonNull(properties);
        PropertyMapper.Source from8 = map.from(properties::isImmediateStop);
        Objects.requireNonNull(container);
        from8.to((v1) -> {
            r1.setStopImmediate(v1);
        });
        Objects.requireNonNull(properties);
        PropertyMapper.Source from9 = map.from(properties::isObservationEnabled);
        Objects.requireNonNull(container);
        from9.to((v1) -> {
            r1.setObservationEnabled(v1);
        });
        PropertyMapper.Source from10 = map.from((PropertyMapper) this.transactionManager);
        Objects.requireNonNull(container);
        from10.to((v1) -> {
            r1.setTransactionManager(v1);
        });
        PropertyMapper.Source from11 = map.from((PropertyMapper) this.rebalanceListener);
        Objects.requireNonNull(container);
        from11.to((v1) -> {
            r1.setConsumerRebalanceListener(v1);
        });
        PropertyMapper.Source from12 = map.from((PropertyMapper) this.listenerTaskExecutor);
        Objects.requireNonNull(container);
        from12.to((v1) -> {
            r1.setListenerTaskExecutor(v1);
        });
    }
}
