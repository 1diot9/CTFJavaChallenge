package org.springframework.boot.availability;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/availability/ApplicationAvailabilityBean.class */
public class ApplicationAvailabilityBean implements ApplicationAvailability, ApplicationListener<AvailabilityChangeEvent<?>> {
    private final Map<Class<? extends AvailabilityState>, AvailabilityChangeEvent<?>> events;
    private final Log logger;

    public ApplicationAvailabilityBean() {
        this(LogFactory.getLog((Class<?>) ApplicationAvailabilityBean.class));
    }

    ApplicationAvailabilityBean(Log logger) {
        this.events = new ConcurrentHashMap();
        this.logger = logger;
    }

    @Override // org.springframework.boot.availability.ApplicationAvailability
    public <S extends AvailabilityState> S getState(Class<S> cls, S s) {
        Assert.notNull(cls, "StateType must not be null");
        Assert.notNull(s, "DefaultState must not be null");
        S s2 = (S) getState(cls);
        return s2 != null ? s2 : s;
    }

    @Override // org.springframework.boot.availability.ApplicationAvailability
    public <S extends AvailabilityState> S getState(Class<S> stateType) {
        AvailabilityChangeEvent<S> event = getLastChangeEvent(stateType);
        if (event != null) {
            return event.getState();
        }
        return null;
    }

    @Override // org.springframework.boot.availability.ApplicationAvailability
    public <S extends AvailabilityState> AvailabilityChangeEvent<S> getLastChangeEvent(Class<S> stateType) {
        return (AvailabilityChangeEvent) this.events.get(stateType);
    }

    @Override // org.springframework.context.ApplicationListener
    public void onApplicationEvent(AvailabilityChangeEvent<?> event) {
        Class<? extends AvailabilityState> type = getStateType(event.getState());
        if (this.logger.isDebugEnabled()) {
            this.logger.debug(getLogMessage(type, event));
        }
        this.events.put(type, event);
    }

    private <S extends AvailabilityState> Object getLogMessage(Class<S> type, AvailabilityChangeEvent<?> event) {
        AvailabilityChangeEvent<S> lastChangeEvent = getLastChangeEvent(type);
        StringBuilder message = new StringBuilder("Application availability state " + type.getSimpleName() + " changed");
        message.append(lastChangeEvent != null ? " from " + lastChangeEvent.getState() : "");
        message.append(" to " + event.getState());
        message.append(getSourceDescription(event.getSource()));
        return message;
    }

    private String getSourceDescription(Object source) {
        if (source == null || (source instanceof ApplicationEventPublisher)) {
            return "";
        }
        return ": " + (source instanceof Throwable ? source : source.getClass().getName());
    }

    /* JADX WARN: Multi-variable type inference failed */
    private Class<? extends AvailabilityState> getStateType(AvailabilityState state) {
        return state instanceof Enum ? ((Enum) state).getDeclaringClass() : state.getClass();
    }
}
