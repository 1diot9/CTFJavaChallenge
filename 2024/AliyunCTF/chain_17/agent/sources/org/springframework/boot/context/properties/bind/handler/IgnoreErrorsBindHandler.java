package org.springframework.boot.context.properties.bind.handler;

import org.springframework.boot.context.properties.bind.AbstractBindHandler;
import org.springframework.boot.context.properties.bind.BindContext;
import org.springframework.boot.context.properties.bind.BindHandler;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/bind/handler/IgnoreErrorsBindHandler.class */
public class IgnoreErrorsBindHandler extends AbstractBindHandler {
    public IgnoreErrorsBindHandler() {
    }

    public IgnoreErrorsBindHandler(BindHandler parent) {
        super(parent);
    }

    @Override // org.springframework.boot.context.properties.bind.AbstractBindHandler, org.springframework.boot.context.properties.bind.BindHandler
    public Object onFailure(ConfigurationPropertyName name, Bindable<?> target, BindContext context, Exception error) throws Exception {
        if (target.getValue() != null) {
            return target.getValue().get();
        }
        return null;
    }
}
