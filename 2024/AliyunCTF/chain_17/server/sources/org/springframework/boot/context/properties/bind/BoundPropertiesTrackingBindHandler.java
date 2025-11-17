package org.springframework.boot.context.properties.bind;

import java.util.function.Consumer;
import org.springframework.boot.context.properties.source.ConfigurationProperty;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/bind/BoundPropertiesTrackingBindHandler.class */
public class BoundPropertiesTrackingBindHandler extends AbstractBindHandler {
    private final Consumer<ConfigurationProperty> consumer;

    public BoundPropertiesTrackingBindHandler(Consumer<ConfigurationProperty> consumer) {
        Assert.notNull(consumer, "Consumer must not be null");
        this.consumer = consumer;
    }

    @Override // org.springframework.boot.context.properties.bind.AbstractBindHandler, org.springframework.boot.context.properties.bind.BindHandler
    public Object onSuccess(ConfigurationPropertyName name, Bindable<?> target, BindContext context, Object result) {
        if (context.getConfigurationProperty() != null && name.equals(context.getConfigurationProperty().getName())) {
            this.consumer.accept(context.getConfigurationProperty());
        }
        return super.onSuccess(name, target, context, result);
    }
}
