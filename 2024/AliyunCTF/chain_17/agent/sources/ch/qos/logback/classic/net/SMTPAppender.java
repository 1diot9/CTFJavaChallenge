package ch.qos.logback.classic.net;

import ch.qos.logback.classic.ClassicConstants;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.boolex.OnErrorEvaluator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.boolex.EventEvaluator;
import ch.qos.logback.core.helpers.CyclicBuffer;
import ch.qos.logback.core.net.SMTPAppenderBase;
import ch.qos.logback.core.pattern.PatternLayoutBase;
import java.util.List;
import java.util.concurrent.Future;
import org.slf4j.Marker;

/* loaded from: agent.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/net/SMTPAppender.class */
public class SMTPAppender extends SMTPAppenderBase<ILoggingEvent> {
    static final String DEFAULT_SUBJECT_PATTERN = "%logger{20} - %m";
    private boolean includeCallerData = false;

    public SMTPAppender() {
    }

    @Override // ch.qos.logback.core.net.SMTPAppenderBase, ch.qos.logback.core.AppenderBase, ch.qos.logback.core.spi.LifeCycle
    public void start() {
        if (this.eventEvaluator == null) {
            OnErrorEvaluator onError = new OnErrorEvaluator();
            onError.setContext(getContext());
            onError.setName("onError");
            onError.start();
            this.eventEvaluator = onError;
        }
        super.start();
    }

    /* JADX WARN: Multi-variable type inference failed */
    public SMTPAppender(EventEvaluator<ILoggingEvent> eventEvaluator) {
        this.eventEvaluator = eventEvaluator;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.net.SMTPAppenderBase
    public void subAppend(CyclicBuffer<ILoggingEvent> cb, ILoggingEvent event) {
        if (this.includeCallerData) {
            event.getCallerData();
        }
        event.prepareForDeferredProcessing();
        cb.add(event);
    }

    @Override // ch.qos.logback.core.net.SMTPAppenderBase
    protected void fillBuffer(CyclicBuffer<ILoggingEvent> cb, StringBuffer sbuf) {
        int len = cb.length();
        for (int i = 0; i < len; i++) {
            ILoggingEvent event = cb.get();
            sbuf.append(this.layout.doLayout(event));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.net.SMTPAppenderBase
    public boolean eventMarksEndOfLife(ILoggingEvent eventObject) {
        List<Marker> markers = eventObject.getMarkerList();
        if (markers == null || markers.isEmpty()) {
            return false;
        }
        for (Marker marker : markers) {
            if (marker.contains(ClassicConstants.FINALIZE_SESSION_MARKER)) {
                return true;
            }
        }
        return false;
    }

    @Override // ch.qos.logback.core.net.SMTPAppenderBase
    protected Layout<ILoggingEvent> makeSubjectLayout(String subjectStr) {
        if (subjectStr == null) {
            subjectStr = DEFAULT_SUBJECT_PATTERN;
        }
        PatternLayout pl = new PatternLayout();
        pl.setContext(getContext());
        pl.setPattern(subjectStr);
        pl.setPostCompileProcessor(null);
        pl.start();
        return pl;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.net.SMTPAppenderBase
    /* renamed from: makeNewToPatternLayout, reason: merged with bridge method [inline-methods] */
    public PatternLayoutBase<ILoggingEvent> makeNewToPatternLayout2(String toPattern) {
        PatternLayout pl = new PatternLayout();
        pl.setPattern(toPattern + "%nopex");
        return pl;
    }

    public boolean isIncludeCallerData() {
        return this.includeCallerData;
    }

    public void setIncludeCallerData(boolean includeCallerData) {
        this.includeCallerData = includeCallerData;
    }

    Future<?> getAsynchronousSendingFuture() {
        return this.asynchronousSendingFuture;
    }
}
