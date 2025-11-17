package org.springframework.boot.context.properties.bind.handler;

import org.springframework.boot.context.properties.bind.AbstractBindHandler;
import org.springframework.boot.context.properties.bind.BindContext;
import org.springframework.boot.context.properties.bind.BindHandler;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.core.convert.ConverterNotFoundException;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/bind/handler/IgnoreTopLevelConverterNotFoundBindHandler.class */
public class IgnoreTopLevelConverterNotFoundBindHandler extends AbstractBindHandler {
    public IgnoreTopLevelConverterNotFoundBindHandler() {
    }

    public IgnoreTopLevelConverterNotFoundBindHandler(BindHandler parent) {
        super(parent);
    }

    @Override // org.springframework.boot.context.properties.bind.AbstractBindHandler, org.springframework.boot.context.properties.bind.BindHandler
    public Object onFailure(ConfigurationPropertyName name, Bindable<?> target, BindContext context, Exception error) throws Exception {
        if (context.getDepth() == 0 && (error instanceof ConverterNotFoundException)) {
            return null;
        }
        throw error;
    }
}
