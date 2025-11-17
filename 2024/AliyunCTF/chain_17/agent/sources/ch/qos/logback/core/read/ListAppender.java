package ch.qos.logback.core.read;

import ch.qos.logback.core.AppenderBase;
import java.util.ArrayList;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/read/ListAppender.class */
public class ListAppender<E> extends AppenderBase<E> {
    public List<E> list = new ArrayList();

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.AppenderBase
    public void append(E e) {
        this.list.add(e);
    }
}
