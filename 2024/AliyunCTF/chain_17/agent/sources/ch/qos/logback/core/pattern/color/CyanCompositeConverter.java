package ch.qos.logback.core.pattern.color;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/pattern/color/CyanCompositeConverter.class */
public class CyanCompositeConverter<E> extends ForegroundCompositeConverterBase<E> {
    @Override // ch.qos.logback.core.pattern.color.ForegroundCompositeConverterBase
    protected String getForegroundColorCode(E event) {
        return ANSIConstants.CYAN_FG;
    }
}
