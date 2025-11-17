package ch.qos.logback.core.pattern.color;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/pattern/color/RedCompositeConverter.class */
public class RedCompositeConverter<E> extends ForegroundCompositeConverterBase<E> {
    @Override // ch.qos.logback.core.pattern.color.ForegroundCompositeConverterBase
    protected String getForegroundColorCode(E event) {
        return ANSIConstants.RED_FG;
    }
}
