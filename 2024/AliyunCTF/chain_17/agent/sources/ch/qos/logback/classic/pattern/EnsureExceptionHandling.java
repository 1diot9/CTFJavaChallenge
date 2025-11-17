package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.pattern.CompositeConverter;
import ch.qos.logback.core.pattern.Converter;
import ch.qos.logback.core.pattern.ConverterUtil;
import ch.qos.logback.core.pattern.PostCompileProcessor;

/* loaded from: agent.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/pattern/EnsureExceptionHandling.class */
public class EnsureExceptionHandling implements PostCompileProcessor<ILoggingEvent> {
    @Override // ch.qos.logback.core.pattern.PostCompileProcessor
    public void process(Context context, Converter<ILoggingEvent> head) {
        Converter<ILoggingEvent> exConverter;
        if (head == null) {
            throw new IllegalArgumentException("cannot process empty chain");
        }
        if (!chainHandlesThrowable(head)) {
            Converter<ILoggingEvent> tail = ConverterUtil.findTail(head);
            LoggerContext loggerContext = (LoggerContext) context;
            if (loggerContext.isPackagingDataEnabled()) {
                exConverter = new ExtendedThrowableProxyConverter();
            } else {
                exConverter = new ThrowableProxyConverter();
            }
            tail.setNext(exConverter);
        }
    }

    public boolean chainHandlesThrowable(Converter<ILoggingEvent> head) {
        Converter<ILoggingEvent> converter = head;
        while (true) {
            Converter<ILoggingEvent> c = converter;
            if (c != null) {
                if (c instanceof ThrowableHandlingConverter) {
                    return true;
                }
                if ((c instanceof CompositeConverter) && compositeHandlesThrowable((CompositeConverter) c)) {
                    return true;
                }
                converter = c.getNext();
            } else {
                return false;
            }
        }
    }

    public boolean compositeHandlesThrowable(CompositeConverter<ILoggingEvent> compositeConverter) {
        Converter<ILoggingEvent> childConverter = compositeConverter.getChildConverter();
        Converter<ILoggingEvent> converter = childConverter;
        while (true) {
            Converter<ILoggingEvent> c = converter;
            if (c != null) {
                if (c instanceof ThrowableHandlingConverter) {
                    return true;
                }
                if (c instanceof CompositeConverter) {
                    boolean r = compositeHandlesThrowable((CompositeConverter) c);
                    if (r) {
                        return true;
                    }
                }
                converter = c.getNext();
            } else {
                return false;
            }
        }
    }
}
