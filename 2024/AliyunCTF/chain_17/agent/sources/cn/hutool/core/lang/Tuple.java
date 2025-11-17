package cn.hutool.core.lang;

import cn.hutool.core.clone.CloneSupport;
import cn.hutool.core.collection.ArrayIter;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ArrayUtil;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/Tuple.class */
public class Tuple extends CloneSupport<Tuple> implements Iterable<Object>, Serializable {
    private static final long serialVersionUID = -7689304393482182157L;
    private final Object[] members;
    private int hashCode;
    private boolean cacheHash;

    public Tuple(Object... members) {
        this.members = members;
    }

    public <T> T get(int i) {
        return (T) this.members[i];
    }

    public Object[] getMembers() {
        return this.members;
    }

    public final List<Object> toList() {
        return ListUtil.toList(this.members);
    }

    public Tuple setCacheHash(boolean cacheHash) {
        this.cacheHash = cacheHash;
        return this;
    }

    public int size() {
        return this.members.length;
    }

    public boolean contains(Object value) {
        return ArrayUtil.contains(this.members, value);
    }

    public final Stream<Object> stream() {
        return Arrays.stream(this.members);
    }

    public final Stream<Object> parallelStream() {
        return StreamSupport.stream(spliterator(), true);
    }

    public final Tuple sub(int start, int end) {
        return new Tuple(ArrayUtil.sub(this.members, start, end));
    }

    public int hashCode() {
        if (this.cacheHash && 0 != this.hashCode) {
            return this.hashCode;
        }
        int result = (31 * 1) + Arrays.deepHashCode(this.members);
        if (this.cacheHash) {
            this.hashCode = result;
        }
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Tuple other = (Tuple) obj;
        return false != Arrays.deepEquals(this.members, other.members);
    }

    public String toString() {
        return Arrays.toString(this.members);
    }

    @Override // java.lang.Iterable
    public Iterator<Object> iterator() {
        return new ArrayIter(this.members);
    }

    @Override // java.lang.Iterable
    public final Spliterator<Object> spliterator() {
        return Spliterators.spliterator(this.members, 16);
    }
}
