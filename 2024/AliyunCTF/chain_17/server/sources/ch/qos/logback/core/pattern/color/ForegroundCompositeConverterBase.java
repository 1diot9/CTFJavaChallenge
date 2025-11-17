package ch.qos.logback.core.pattern.color;

import ch.qos.logback.core.pattern.CompositeConverter;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/pattern/color/ForegroundCompositeConverterBase.class */
public abstract class ForegroundCompositeConverterBase<E> extends CompositeConverter<E> {
    private static final String SET_DEFAULT_COLOR = "\u001b[0;39m";

    protected abstract String getForegroundColorCode(E e);

    @Override // ch.qos.logback.core.pattern.CompositeConverter
    protected String transform(E event, String in) {
        return ANSIConstants.ESC_START + getForegroundColorCode(event) + ANSIConstants.ESC_END + in + SET_DEFAULT_COLOR;
    }
}
