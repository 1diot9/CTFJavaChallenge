package ch.qos.logback.core.pattern;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/pattern/IdentityCompositeConverter.class */
public class IdentityCompositeConverter<E> extends CompositeConverter<E> {
    @Override // ch.qos.logback.core.pattern.CompositeConverter
    protected String transform(E event, String in) {
        return in;
    }
}
