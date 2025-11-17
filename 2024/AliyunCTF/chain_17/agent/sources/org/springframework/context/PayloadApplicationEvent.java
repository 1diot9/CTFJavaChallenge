package org.springframework.context;

import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/PayloadApplicationEvent.class */
public class PayloadApplicationEvent<T> extends ApplicationEvent implements ResolvableTypeProvider {
    private final T payload;
    private final ResolvableType payloadType;

    public PayloadApplicationEvent(Object source, T payload) {
        this(source, payload, null);
    }

    public PayloadApplicationEvent(Object source, T payload, @Nullable ResolvableType payloadType) {
        super(source);
        Assert.notNull(payload, "Payload must not be null");
        this.payload = payload;
        this.payloadType = payloadType != null ? payloadType : ResolvableType.forInstance(payload);
    }

    public ResolvableType getResolvableType() {
        return ResolvableType.forClassWithGenerics(getClass(), this.payloadType);
    }

    public T getPayload() {
        return this.payload;
    }
}
