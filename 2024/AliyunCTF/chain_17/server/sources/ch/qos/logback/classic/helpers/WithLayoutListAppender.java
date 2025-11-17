package ch.qos.logback.classic.helpers;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import java.util.ArrayList;
import java.util.List;

/* loaded from: server.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/helpers/WithLayoutListAppender.class */
public class WithLayoutListAppender extends AppenderBase<ILoggingEvent> {
    public List<String> list = new ArrayList();
    String pattern;
    PatternLayout patternLayout;

    @Override // ch.qos.logback.core.AppenderBase, ch.qos.logback.core.spi.LifeCycle
    public void start() {
        if (this.pattern == null) {
            addError("null pattern disallowed");
            return;
        }
        this.patternLayout = new PatternLayout();
        this.patternLayout.setContext(this.context);
        this.patternLayout.setPattern(this.pattern);
        this.patternLayout.start();
        if (this.patternLayout.isStarted()) {
            super.start();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.AppenderBase
    public void append(ILoggingEvent e) {
        String result = this.patternLayout.doLayout(e);
        this.list.add(result);
    }

    public String getPattern() {
        return this.pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
}
