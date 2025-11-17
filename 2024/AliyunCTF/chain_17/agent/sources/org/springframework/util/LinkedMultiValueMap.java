package org.springframework.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/LinkedMultiValueMap.class */
public class LinkedMultiValueMap<K, V> extends MultiValueMapAdapter<K, V> implements Serializable, Cloneable {
    private static final long serialVersionUID = 3801124242820219131L;

    public LinkedMultiValueMap() {
        super(new LinkedHashMap());
    }

    public LinkedMultiValueMap(int expectedSize) {
        super(CollectionUtils.newLinkedHashMap(expectedSize));
    }

    public LinkedMultiValueMap(Map<K, List<V>> otherMap) {
        super(new LinkedHashMap(otherMap));
    }

    public LinkedMultiValueMap<K, V> deepCopy() {
        LinkedMultiValueMap<K, V> copy = new LinkedMultiValueMap<>(size());
        forEach((key, values) -> {
            copy.put((LinkedMultiValueMap) key, (List) new ArrayList(values));
        });
        return copy;
    }

    /* renamed from: clone, reason: merged with bridge method [inline-methods] */
    public LinkedMultiValueMap<K, V> m2641clone() {
        return new LinkedMultiValueMap<>(this);
    }
}
