package org.springframework.boot.autoconfigure.jms.activemq;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQProperties;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jms/activemq/ActiveMQConnectionFactoryFactory.class */
class ActiveMQConnectionFactoryFactory {
    private final ActiveMQProperties properties;
    private final List<ActiveMQConnectionFactoryCustomizer> factoryCustomizers;
    private final ActiveMQConnectionDetails connectionDetails;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ActiveMQConnectionFactoryFactory(ActiveMQProperties properties, List<ActiveMQConnectionFactoryCustomizer> factoryCustomizers, ActiveMQConnectionDetails connectionDetails) {
        Assert.notNull(properties, "Properties must not be null");
        this.properties = properties;
        this.factoryCustomizers = factoryCustomizers != null ? factoryCustomizers : Collections.emptyList();
        this.connectionDetails = connectionDetails;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public <T extends ActiveMQConnectionFactory> T createConnectionFactory(Class<T> cls) {
        try {
            return (T) doCreateConnectionFactory(cls);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to create ActiveMQConnectionFactory", e);
        }
    }

    private <T extends ActiveMQConnectionFactory> T doCreateConnectionFactory(Class<T> cls) throws Exception {
        T t = (T) createConnectionFactoryInstance(cls);
        if (this.properties.getCloseTimeout() != null) {
            t.setCloseTimeout((int) this.properties.getCloseTimeout().toMillis());
        }
        t.setNonBlockingRedelivery(this.properties.isNonBlockingRedelivery());
        if (this.properties.getSendTimeout() != null) {
            t.setSendTimeout((int) this.properties.getSendTimeout().toMillis());
        }
        ActiveMQProperties.Packages packages = this.properties.getPackages();
        if (packages.getTrustAll() != null) {
            t.setTrustAllPackages(packages.getTrustAll().booleanValue());
        }
        if (!packages.getTrusted().isEmpty()) {
            t.setTrustedPackages(packages.getTrusted());
        }
        customize(t);
        return t;
    }

    private <T extends ActiveMQConnectionFactory> T createConnectionFactoryInstance(Class<T> factoryClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        String brokerUrl = this.connectionDetails.getBrokerUrl();
        String user = this.connectionDetails.getUser();
        String password = this.connectionDetails.getPassword();
        return (StringUtils.hasLength(user) && StringUtils.hasLength(password)) ? factoryClass.getConstructor(String.class, String.class, String.class).newInstance(user, password, brokerUrl) : factoryClass.getConstructor(String.class).newInstance(brokerUrl);
    }

    private void customize(ActiveMQConnectionFactory connectionFactory) {
        for (ActiveMQConnectionFactoryCustomizer factoryCustomizer : this.factoryCustomizers) {
            factoryCustomizer.customize(connectionFactory);
        }
    }
}
