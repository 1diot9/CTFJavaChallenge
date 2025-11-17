package org.springframework.context.event;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.runtime.ObjectMethods;
import org.springframework.context.ApplicationEvent;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/event/EventExpressionRootObject.class */
final class EventExpressionRootObject extends Record {
    private final ApplicationEvent event;
    private final Object[] args;

    /* JADX INFO: Access modifiers changed from: package-private */
    public EventExpressionRootObject(ApplicationEvent event, Object[] args) {
        this.event = event;
        this.args = args;
    }

    @Override // java.lang.Record
    public final String toString() {
        return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, EventExpressionRootObject.class), EventExpressionRootObject.class, "event;args", "FIELD:Lorg/springframework/context/event/EventExpressionRootObject;->event:Lorg/springframework/context/ApplicationEvent;", "FIELD:Lorg/springframework/context/event/EventExpressionRootObject;->args:[Ljava/lang/Object;").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final int hashCode() {
        return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, EventExpressionRootObject.class), EventExpressionRootObject.class, "event;args", "FIELD:Lorg/springframework/context/event/EventExpressionRootObject;->event:Lorg/springframework/context/ApplicationEvent;", "FIELD:Lorg/springframework/context/event/EventExpressionRootObject;->args:[Ljava/lang/Object;").dynamicInvoker().invoke(this) /* invoke-custom */;
    }

    @Override // java.lang.Record
    public final boolean equals(Object o) {
        return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, EventExpressionRootObject.class, Object.class), EventExpressionRootObject.class, "event;args", "FIELD:Lorg/springframework/context/event/EventExpressionRootObject;->event:Lorg/springframework/context/ApplicationEvent;", "FIELD:Lorg/springframework/context/event/EventExpressionRootObject;->args:[Ljava/lang/Object;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
    }

    public ApplicationEvent event() {
        return this.event;
    }

    public Object[] args() {
        return this.args;
    }
}
