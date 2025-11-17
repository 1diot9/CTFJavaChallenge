package ch.qos.logback.core.helpers;

import java.util.ArrayList;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/helpers/CyclicBuffer.class */
public class CyclicBuffer<E> {
    E[] ea;
    int first;
    int last;
    int numElems;
    int maxSize;

    public CyclicBuffer(int maxSize) throws IllegalArgumentException {
        if (maxSize < 1) {
            throw new IllegalArgumentException("The maxSize argument (" + maxSize + ") is not a positive integer.");
        }
        init(maxSize);
    }

    public CyclicBuffer(CyclicBuffer<E> cyclicBuffer) {
        this.maxSize = cyclicBuffer.maxSize;
        this.ea = (E[]) new Object[this.maxSize];
        System.arraycopy(cyclicBuffer.ea, 0, this.ea, 0, this.maxSize);
        this.last = cyclicBuffer.last;
        this.first = cyclicBuffer.first;
        this.numElems = cyclicBuffer.numElems;
    }

    private void init(int i) {
        this.maxSize = i;
        this.ea = (E[]) new Object[i];
        this.first = 0;
        this.last = 0;
        this.numElems = 0;
    }

    public void clear() {
        init(this.maxSize);
    }

    public void add(E event) {
        this.ea[this.last] = event;
        int i = this.last + 1;
        this.last = i;
        if (i == this.maxSize) {
            this.last = 0;
        }
        if (this.numElems < this.maxSize) {
            this.numElems++;
            return;
        }
        int i2 = this.first + 1;
        this.first = i2;
        if (i2 == this.maxSize) {
            this.first = 0;
        }
    }

    public E get(int i) {
        if (i < 0 || i >= this.numElems) {
            return null;
        }
        return this.ea[(this.first + i) % this.maxSize];
    }

    public int getMaxSize() {
        return this.maxSize;
    }

    public E get() {
        E r = null;
        if (this.numElems > 0) {
            this.numElems--;
            r = this.ea[this.first];
            this.ea[this.first] = null;
            int i = this.first + 1;
            this.first = i;
            if (i == this.maxSize) {
                this.first = 0;
            }
        }
        return r;
    }

    public List<E> asList() {
        List<E> tList = new ArrayList<>();
        for (int i = 0; i < length(); i++) {
            tList.add(get(i));
        }
        return tList;
    }

    public int length() {
        return this.numElems;
    }

    public void resize(int i) {
        if (i < 0) {
            throw new IllegalArgumentException("Negative array size [" + i + "] not allowed.");
        }
        if (i == this.numElems) {
            return;
        }
        E[] eArr = (E[]) new Object[i];
        int i2 = i < this.numElems ? i : this.numElems;
        for (int i3 = 0; i3 < i2; i3++) {
            eArr[i3] = this.ea[this.first];
            this.ea[this.first] = null;
            int i4 = this.first + 1;
            this.first = i4;
            if (i4 == this.numElems) {
                this.first = 0;
            }
        }
        this.ea = eArr;
        this.first = 0;
        this.numElems = i2;
        this.maxSize = i;
        if (i2 == i) {
            this.last = 0;
        } else {
            this.last = i2;
        }
    }
}
