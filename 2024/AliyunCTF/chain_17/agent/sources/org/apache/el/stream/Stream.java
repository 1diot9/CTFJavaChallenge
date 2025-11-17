package org.apache.el.stream;

import jakarta.el.ELException;
import jakarta.el.LambdaExpression;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import org.apache.el.lang.ELArithmetic;
import org.apache.el.lang.ELSupport;
import org.apache.el.util.MessageFactory;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:org/apache/el/stream/Stream.class */
public class Stream {
    private final Iterator<Object> iterator;

    public Stream(Iterator<Object> iterator) {
        this.iterator = iterator;
    }

    public Stream filter(final LambdaExpression le) {
        Iterator<Object> downStream = new OpIterator() { // from class: org.apache.el.stream.Stream.1
            @Override // org.apache.el.stream.Stream.OpIterator
            protected void findNext() {
                while (Stream.this.iterator.hasNext()) {
                    Object obj = Stream.this.iterator.next();
                    if (ELSupport.coerceToBoolean(null, le.invoke(obj), true).booleanValue()) {
                        this.next = obj;
                        this.foundNext = true;
                        return;
                    }
                }
            }
        };
        return new Stream(downStream);
    }

    public Stream map(final LambdaExpression le) {
        Iterator<Object> downStream = new OpIterator() { // from class: org.apache.el.stream.Stream.2
            @Override // org.apache.el.stream.Stream.OpIterator
            protected void findNext() {
                if (Stream.this.iterator.hasNext()) {
                    Object obj = Stream.this.iterator.next();
                    this.next = le.invoke(obj);
                    this.foundNext = true;
                }
            }
        };
        return new Stream(downStream);
    }

    public Stream flatMap(final LambdaExpression le) {
        Iterator<Object> downStream = new OpIterator() { // from class: org.apache.el.stream.Stream.3
            private Iterator<?> inner;

            @Override // org.apache.el.stream.Stream.OpIterator
            protected void findNext() {
                do {
                    if (Stream.this.iterator.hasNext() || (this.inner != null && this.inner.hasNext())) {
                        if (this.inner == null || !this.inner.hasNext()) {
                            this.inner = ((Stream) le.invoke(Stream.this.iterator.next())).iterator;
                        }
                    } else {
                        return;
                    }
                } while (!this.inner.hasNext());
                this.next = this.inner.next();
                this.foundNext = true;
            }
        };
        return new Stream(downStream);
    }

    public Stream distinct() {
        Iterator<Object> downStream = new OpIterator() { // from class: org.apache.el.stream.Stream.4
            private Set<Object> values = new HashSet();

            @Override // org.apache.el.stream.Stream.OpIterator
            protected void findNext() {
                while (Stream.this.iterator.hasNext()) {
                    Object obj = Stream.this.iterator.next();
                    if (this.values.add(obj)) {
                        this.next = obj;
                        this.foundNext = true;
                        return;
                    }
                }
            }
        };
        return new Stream(downStream);
    }

    public Stream sorted() {
        Iterator<Object> downStream = new OpIterator() { // from class: org.apache.el.stream.Stream.5
            private Iterator<Object> sorted = null;

            @Override // org.apache.el.stream.Stream.OpIterator
            protected void findNext() {
                if (this.sorted == null) {
                    sort();
                }
                if (this.sorted.hasNext()) {
                    this.next = this.sorted.next();
                    this.foundNext = true;
                }
            }

            private void sort() {
                List list = new ArrayList();
                while (Stream.this.iterator.hasNext()) {
                    list.add(Stream.this.iterator.next());
                }
                Collections.sort(list);
                this.sorted = list.iterator();
            }
        };
        return new Stream(downStream);
    }

    public Stream sorted(final LambdaExpression le) {
        Iterator<Object> downStream = new OpIterator() { // from class: org.apache.el.stream.Stream.6
            private Iterator<Object> sorted = null;

            @Override // org.apache.el.stream.Stream.OpIterator
            protected void findNext() {
                if (this.sorted == null) {
                    sort(le);
                }
                if (this.sorted.hasNext()) {
                    this.next = this.sorted.next();
                    this.foundNext = true;
                }
            }

            private void sort(LambdaExpression le2) {
                List list = new ArrayList();
                LambdaExpressionComparator lambdaExpressionComparator = new LambdaExpressionComparator(le2);
                while (Stream.this.iterator.hasNext()) {
                    list.add(Stream.this.iterator.next());
                }
                list.sort(lambdaExpressionComparator);
                this.sorted = list.iterator();
            }
        };
        return new Stream(downStream);
    }

    public Object forEach(LambdaExpression le) {
        while (this.iterator.hasNext()) {
            le.invoke(this.iterator.next());
        }
        return null;
    }

    public Stream peek(final LambdaExpression le) {
        Iterator<Object> downStream = new OpIterator() { // from class: org.apache.el.stream.Stream.7
            @Override // org.apache.el.stream.Stream.OpIterator
            protected void findNext() {
                if (Stream.this.iterator.hasNext()) {
                    Object obj = Stream.this.iterator.next();
                    le.invoke(obj);
                    this.next = obj;
                    this.foundNext = true;
                }
            }
        };
        return new Stream(downStream);
    }

    public Iterator<?> iterator() {
        return this.iterator;
    }

    public Stream limit(Number count) {
        return substream(0, count);
    }

    public Stream substream(Number start) {
        return substream(start, Integer.MAX_VALUE);
    }

    public Stream substream(final Number start, final Number end) {
        Iterator<Object> downStream = new OpIterator() { // from class: org.apache.el.stream.Stream.8
            private final int startPos;
            private final int endPos;
            private int itemCount = 0;

            {
                this.startPos = start.intValue();
                this.endPos = end.intValue();
            }

            @Override // org.apache.el.stream.Stream.OpIterator
            protected void findNext() {
                while (this.itemCount < this.startPos && Stream.this.iterator.hasNext()) {
                    Stream.this.iterator.next();
                    this.itemCount++;
                }
                if (this.itemCount < this.endPos && Stream.this.iterator.hasNext()) {
                    this.itemCount++;
                    this.next = Stream.this.iterator.next();
                    this.foundNext = true;
                }
            }
        };
        return new Stream(downStream);
    }

    public List<Object> toList() {
        List<Object> result = new ArrayList<>();
        while (this.iterator.hasNext()) {
            result.add(this.iterator.next());
        }
        return result;
    }

    public Object[] toArray() {
        List<Object> result = new ArrayList<>();
        while (this.iterator.hasNext()) {
            result.add(this.iterator.next());
        }
        return result.toArray(new Object[0]);
    }

    public Optional reduce(LambdaExpression le) {
        Object seed = null;
        if (this.iterator.hasNext()) {
            seed = this.iterator.next();
        }
        if (seed == null) {
            return Optional.EMPTY;
        }
        return new Optional(reduce(seed, le));
    }

    public Object reduce(Object seed, LambdaExpression le) {
        Object obj = seed;
        while (true) {
            Object result = obj;
            if (this.iterator.hasNext()) {
                obj = le.invoke(result, this.iterator.next());
            } else {
                return result;
            }
        }
    }

    public Optional max() {
        return compare(true);
    }

    public Optional max(LambdaExpression le) {
        return compare(true, le);
    }

    public Optional min() {
        return compare(false);
    }

    public Optional min(LambdaExpression le) {
        return compare(false, le);
    }

    public Optional average() {
        Number sum;
        long count = 0;
        Number number = 0L;
        while (true) {
            sum = number;
            if (!this.iterator.hasNext()) {
                break;
            }
            count++;
            number = ELArithmetic.add(sum, this.iterator.next());
        }
        if (count == 0) {
            return Optional.EMPTY;
        }
        return new Optional(ELArithmetic.divide((Object) sum, (Object) Long.valueOf(count)));
    }

    public Number sum() {
        Number number = 0L;
        while (true) {
            Number sum = number;
            if (this.iterator.hasNext()) {
                number = ELArithmetic.add(sum, this.iterator.next());
            } else {
                return sum;
            }
        }
    }

    public Long count() {
        long j = 0;
        while (true) {
            long count = j;
            if (this.iterator.hasNext()) {
                this.iterator.next();
                j = count + 1;
            } else {
                return Long.valueOf(count);
            }
        }
    }

    public Optional anyMatch(LambdaExpression le) {
        Boolean match;
        if (!this.iterator.hasNext()) {
            return Optional.EMPTY;
        }
        Boolean bool = Boolean.FALSE;
        while (true) {
            match = bool;
            if (match.booleanValue() || !this.iterator.hasNext()) {
                break;
            }
            bool = (Boolean) le.invoke(this.iterator.next());
        }
        return new Optional(match);
    }

    public Optional allMatch(LambdaExpression le) {
        Boolean match;
        if (!this.iterator.hasNext()) {
            return Optional.EMPTY;
        }
        Boolean bool = Boolean.TRUE;
        while (true) {
            match = bool;
            if (!match.booleanValue() || !this.iterator.hasNext()) {
                break;
            }
            bool = (Boolean) le.invoke(this.iterator.next());
        }
        return new Optional(match);
    }

    public Optional noneMatch(LambdaExpression le) {
        Boolean match;
        if (!this.iterator.hasNext()) {
            return Optional.EMPTY;
        }
        Boolean bool = Boolean.FALSE;
        while (true) {
            match = bool;
            if (match.booleanValue() || !this.iterator.hasNext()) {
                break;
            }
            bool = (Boolean) le.invoke(this.iterator.next());
        }
        return new Optional(Boolean.valueOf(!match.booleanValue()));
    }

    public Optional findFirst() {
        if (this.iterator.hasNext()) {
            return new Optional(this.iterator.next());
        }
        return Optional.EMPTY;
    }

    private Optional compare(boolean isMax) {
        Comparable result = null;
        if (this.iterator.hasNext()) {
            Object obj = this.iterator.next();
            if (obj instanceof Comparable) {
                result = (Comparable) obj;
            } else {
                throw new ELException(MessageFactory.get("stream.compare.notComparable"));
            }
        }
        while (this.iterator.hasNext()) {
            Object obj2 = this.iterator.next();
            if (obj2 instanceof Comparable) {
                if (isMax && ((Comparable) obj2).compareTo(result) > 0) {
                    result = (Comparable) obj2;
                } else if (!isMax && ((Comparable) obj2).compareTo(result) < 0) {
                    result = (Comparable) obj2;
                }
            } else {
                throw new ELException(MessageFactory.get("stream.compare.notComparable"));
            }
        }
        if (result == null) {
            return Optional.EMPTY;
        }
        return new Optional(result);
    }

    private Optional compare(boolean isMax, LambdaExpression le) {
        Object result = null;
        if (this.iterator.hasNext()) {
            result = this.iterator.next();
        }
        while (this.iterator.hasNext()) {
            Object obj = this.iterator.next();
            if (isMax && ELSupport.coerceToNumber(null, le.invoke(obj, result), Integer.class).intValue() > 0) {
                result = obj;
            } else if (!isMax && ELSupport.coerceToNumber(null, le.invoke(obj, result), Integer.class).intValue() < 0) {
                result = obj;
            }
        }
        if (result == null) {
            return Optional.EMPTY;
        }
        return new Optional(result);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:org/apache/el/stream/Stream$LambdaExpressionComparator.class */
    public static class LambdaExpressionComparator implements Comparator<Object> {
        private final LambdaExpression le;

        LambdaExpressionComparator(LambdaExpression le) {
            this.le = le;
        }

        @Override // java.util.Comparator
        public int compare(Object o1, Object o2) {
            return ELSupport.coerceToNumber(null, this.le.invoke(o1, o2), Integer.class).intValue();
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:org/apache/el/stream/Stream$OpIterator.class */
    private static abstract class OpIterator implements Iterator<Object> {
        protected boolean foundNext = false;
        protected Object next;

        protected abstract void findNext();

        private OpIterator() {
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            if (this.foundNext) {
                return true;
            }
            findNext();
            return this.foundNext;
        }

        @Override // java.util.Iterator
        public Object next() {
            if (this.foundNext) {
                this.foundNext = false;
                return this.next;
            }
            findNext();
            if (this.foundNext) {
                this.foundNext = false;
                return this.next;
            }
            throw new NoSuchElementException();
        }

        @Override // java.util.Iterator
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
