package org.springframework.boot.context.properties.bind;

import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/bind/AbstractBindHandler.class */
public abstract class AbstractBindHandler implements BindHandler {
    private final BindHandler parent;

    public AbstractBindHandler() {
        this(BindHandler.DEFAULT);
    }

    public AbstractBindHandler(BindHandler parent) {
        Assert.notNull(parent, "Parent must not be null");
        this.parent = parent;
    }

    @Override // org.springframework.boot.context.properties.bind.BindHandler
    public <T> Bindable<T> onStart(ConfigurationPropertyName name, Bindable<T> target, BindContext context) {
        return this.parent.onStart(name, target, context);
    }

    @Override // org.springframework.boot.context.properties.bind.BindHandler
    public Object onSuccess(ConfigurationPropertyName name, Bindable<?> target, BindContext context, Object result) {
        return this.parent.onSuccess(name, target, context, result);
    }

    @Override // org.springframework.boot.context.properties.bind.BindHandler
    public Object onFailure(ConfigurationPropertyName name, Bindable<?> target, BindContext context, Exception error) throws Exception {
        return this.parent.onFailure(name, target, context, error);
    }

    @Override // org.springframework.boot.context.properties.bind.BindHandler
    public void onFinish(ConfigurationPropertyName name, Bindable<?> target, BindContext context, Object result) throws Exception {
        this.parent.onFinish(name, target, context, result);
    }
}
