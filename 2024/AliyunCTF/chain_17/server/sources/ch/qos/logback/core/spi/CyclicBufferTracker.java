package ch.qos.logback.core.spi;

import ch.qos.logback.core.helpers.CyclicBuffer;
import java.util.ArrayList;
import java.util.List;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/spi/CyclicBufferTracker.class */
public class CyclicBufferTracker<E> extends AbstractComponentTracker<CyclicBuffer<E>> {
    static final int DEFAULT_NUMBER_OF_BUFFERS = 64;
    static final int DEFAULT_BUFFER_SIZE = 256;
    int bufferSize = 256;

    public CyclicBufferTracker() {
        setMaxComponents(64);
    }

    public int getBufferSize() {
        return this.bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.spi.AbstractComponentTracker
    public void processPriorToRemoval(CyclicBuffer<E> component) {
        component.clear();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.spi.AbstractComponentTracker
    public CyclicBuffer<E> buildComponent(String key) {
        return new CyclicBuffer<>(this.bufferSize);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.spi.AbstractComponentTracker
    public boolean isComponentStale(CyclicBuffer<E> eCyclicBuffer) {
        return false;
    }

    List<String> liveKeysAsOrderedList() {
        return new ArrayList(this.liveMap.keySet());
    }

    List<String> lingererKeysAsOrderedList() {
        return new ArrayList(this.lingerersMap.keySet());
    }
}
