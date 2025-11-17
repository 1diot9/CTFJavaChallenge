package ch.qos.logback.core.pattern;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/pattern/CompositeConverter.class */
public abstract class CompositeConverter<E> extends DynamicConverter<E> {
    Converter<E> childConverter;

    protected abstract String transform(E e, String str);

    @Override // ch.qos.logback.core.pattern.Converter
    public String convert(E event) {
        StringBuilder buf = new StringBuilder();
        Converter<E> converter = this.childConverter;
        while (true) {
            Converter<E> c = converter;
            if (c != null) {
                c.write(buf, event);
                converter = c.next;
            } else {
                String intermediary = buf.toString();
                return transform(event, intermediary);
            }
        }
    }

    public Converter<E> getChildConverter() {
        return this.childConverter;
    }

    public void setChildConverter(Converter<E> child) {
        this.childConverter = child;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("CompositeConverter<");
        if (this.formattingInfo != null) {
            buf.append(this.formattingInfo);
        }
        if (this.childConverter != null) {
            buf.append(", children: ").append(this.childConverter);
        }
        buf.append(">");
        return buf.toString();
    }
}
