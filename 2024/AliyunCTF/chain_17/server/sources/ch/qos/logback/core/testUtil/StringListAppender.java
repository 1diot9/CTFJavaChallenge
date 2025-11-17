package ch.qos.logback.core.testUtil;

import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.Layout;
import java.util.ArrayList;
import java.util.List;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/testUtil/StringListAppender.class */
public class StringListAppender<E> extends AppenderBase<E> {
    Layout<E> layout;
    public List<String> strList = new ArrayList();

    @Override // ch.qos.logback.core.AppenderBase, ch.qos.logback.core.spi.LifeCycle
    public void start() {
        this.strList.clear();
        if (this.layout == null || !this.layout.isStarted()) {
            return;
        }
        super.start();
    }

    @Override // ch.qos.logback.core.AppenderBase, ch.qos.logback.core.spi.LifeCycle
    public void stop() {
        super.stop();
    }

    @Override // ch.qos.logback.core.AppenderBase
    protected void append(E eventObject) {
        String res = this.layout.doLayout(eventObject);
        this.strList.add(res);
    }

    public Layout<E> getLayout() {
        return this.layout;
    }

    public void setLayout(Layout<E> layout) {
        this.layout = layout;
    }
}
