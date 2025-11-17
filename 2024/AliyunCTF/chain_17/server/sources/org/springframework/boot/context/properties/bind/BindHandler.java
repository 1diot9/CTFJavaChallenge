package org.springframework.boot.context.properties.bind;

import org.springframework.boot.context.properties.source.ConfigurationPropertyName;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/bind/BindHandler.class */
public interface BindHandler {
    public static final BindHandler DEFAULT = new BindHandler() { // from class: org.springframework.boot.context.properties.bind.BindHandler.1
    };

    default <T> Bindable<T> onStart(ConfigurationPropertyName name, Bindable<T> target, BindContext context) {
        return target;
    }

    default Object onSuccess(ConfigurationPropertyName name, Bindable<?> target, BindContext context, Object result) {
        return result;
    }

    default Object onCreate(ConfigurationPropertyName name, Bindable<?> target, BindContext context, Object result) {
        return result;
    }

    default Object onFailure(ConfigurationPropertyName name, Bindable<?> target, BindContext context, Exception error) throws Exception {
        throw error;
    }

    default void onFinish(ConfigurationPropertyName name, Bindable<?> target, BindContext context, Object result) throws Exception {
    }
}
