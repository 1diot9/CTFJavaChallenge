package org.springframework.boot.autoconfigure.jms.artemis;

import java.lang.reflect.Constructor;
import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.api.core.client.ActiveMQClient;
import org.apache.activemq.artemis.api.core.client.ServerLocator;
import org.apache.activemq.artemis.core.remoting.impl.invm.InVMConnectorFactory;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jms/artemis/ArtemisConnectionFactoryFactory.class */
class ArtemisConnectionFactoryFactory {
    private static final String DEFAULT_BROKER_URL = "tcp://localhost:61616";
    static final String[] EMBEDDED_JMS_CLASSES = {"org.apache.activemq.artemis.jms.server.embedded.EmbeddedJMS", "org.apache.activemq.artemis.core.server.embedded.EmbeddedActiveMQ"};
    private final ArtemisProperties properties;
    private final ListableBeanFactory beanFactory;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ArtemisConnectionFactoryFactory(ListableBeanFactory beanFactory, ArtemisProperties properties) {
        Assert.notNull(beanFactory, "BeanFactory must not be null");
        Assert.notNull(properties, "Properties must not be null");
        this.beanFactory = beanFactory;
        this.properties = properties;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public <T extends ActiveMQConnectionFactory> T createConnectionFactory(Class<T> cls) {
        try {
            startEmbeddedJms();
            return (T) doCreateConnectionFactory(cls);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to create ActiveMQConnectionFactory", e);
        }
    }

    private void startEmbeddedJms() {
        for (String embeddedJmsClass : EMBEDDED_JMS_CLASSES) {
            if (ClassUtils.isPresent(embeddedJmsClass, null)) {
                try {
                    this.beanFactory.getBeansOfType(Class.forName(embeddedJmsClass));
                } catch (Exception e) {
                }
            }
        }
    }

    private <T extends ActiveMQConnectionFactory> T doCreateConnectionFactory(Class<T> cls) throws Exception {
        ArtemisMode mode = this.properties.getMode();
        if (mode == null) {
            mode = deduceMode();
        }
        if (mode == ArtemisMode.EMBEDDED) {
            return (T) createEmbeddedConnectionFactory(cls);
        }
        return (T) createNativeConnectionFactory(cls);
    }

    private ArtemisMode deduceMode() {
        if (this.properties.getEmbedded().isEnabled() && isEmbeddedJmsClassPresent()) {
            return ArtemisMode.EMBEDDED;
        }
        return ArtemisMode.NATIVE;
    }

    private boolean isEmbeddedJmsClassPresent() {
        for (String embeddedJmsClass : EMBEDDED_JMS_CLASSES) {
            if (ClassUtils.isPresent(embeddedJmsClass, null)) {
                return true;
            }
        }
        return false;
    }

    private <T extends ActiveMQConnectionFactory> T createEmbeddedConnectionFactory(Class<T> factoryClass) throws Exception {
        try {
            TransportConfiguration transportConfiguration = new TransportConfiguration(InVMConnectorFactory.class.getName(), this.properties.getEmbedded().generateTransportParameters());
            ServerLocator serviceLocator = ActiveMQClient.createServerLocatorWithoutHA(new TransportConfiguration[]{transportConfiguration});
            return factoryClass.getConstructor(ServerLocator.class).newInstance(serviceLocator);
        } catch (NoClassDefFoundError ex) {
            throw new IllegalStateException("Unable to create InVM Artemis connection, ensure that artemis-jms-server.jar is in the classpath", ex);
        }
    }

    private <T extends ActiveMQConnectionFactory> T createNativeConnectionFactory(Class<T> cls) throws Exception {
        T t = (T) newNativeConnectionFactory(cls);
        String user = this.properties.getUser();
        if (StringUtils.hasText(user)) {
            t.setUser(user);
            t.setPassword(this.properties.getPassword());
        }
        return t;
    }

    private <T extends ActiveMQConnectionFactory> T newNativeConnectionFactory(Class<T> factoryClass) throws Exception {
        String brokerUrl = StringUtils.hasText(this.properties.getBrokerUrl()) ? this.properties.getBrokerUrl() : DEFAULT_BROKER_URL;
        Constructor<T> constructor = factoryClass.getConstructor(String.class);
        return constructor.newInstance(brokerUrl);
    }
}
