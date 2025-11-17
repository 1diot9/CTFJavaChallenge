package org.springframework.boot.availability;

import org.springframework.boot.availability.AvailabilityState;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.core.ResolvableType;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/availability/AvailabilityChangeEvent.class */
public class AvailabilityChangeEvent<S extends AvailabilityState> extends PayloadApplicationEvent<S> {
    public AvailabilityChangeEvent(Object source, S state) {
        super(source, state);
    }

    public S getState() {
        return (S) getPayload();
    }

    @Override // org.springframework.context.PayloadApplicationEvent, org.springframework.core.ResolvableTypeProvider
    public ResolvableType getResolvableType() {
        return ResolvableType.forClassWithGenerics(getClass(), (Class<?>[]) new Class[]{getStateType()});
    }

    private Class<?> getStateType() {
        S state = getState();
        if (state instanceof Enum) {
            return ((Enum) state).getDeclaringClass();
        }
        return state.getClass();
    }

    public static <S extends AvailabilityState> void publish(ApplicationContext context, S state) {
        Assert.notNull(context, "Context must not be null");
        publish(context, context, state);
    }

    public static <S extends AvailabilityState> void publish(ApplicationEventPublisher publisher, Object source, S state) {
        Assert.notNull(publisher, "Publisher must not be null");
        publisher.publishEvent((ApplicationEvent) new AvailabilityChangeEvent(source, state));
    }
}
