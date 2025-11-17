package org.springframework.cglib.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/core/CollectionUtils.class */
public class CollectionUtils {
    private CollectionUtils() {
    }

    public static Map bucket(Collection c, Transformer t) {
        Map buckets = new HashMap();
        for (Object value : c) {
            Object key = t.transform(value);
            List bucket = (List) buckets.get(key);
            if (bucket == null) {
                LinkedList linkedList = new LinkedList();
                bucket = linkedList;
                buckets.put(key, linkedList);
            }
            bucket.add(value);
        }
        return buckets;
    }

    public static void reverse(Map source, Map target) {
        for (Object key : source.keySet()) {
            target.put(source.get(key), key);
        }
    }

    public static Collection filter(Collection c, Predicate p) {
        c.removeIf(o -> {
            return !p.evaluate(o);
        });
        return c;
    }

    public static List transform(Collection c, Transformer t) {
        List result = new ArrayList(c.size());
        Iterator it = c.iterator();
        while (it.hasNext()) {
            result.add(t.transform(it.next()));
        }
        return result;
    }

    public static Map getIndexMap(List list) {
        Map indexes = new HashMap();
        int index = 0;
        Iterator it = list.iterator();
        while (it.hasNext()) {
            int i = index;
            index++;
            indexes.put(it.next(), Integer.valueOf(i));
        }
        return indexes;
    }
}
