package cn.hutool.json;

import java.util.Iterator;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/json/JSONObjectIter.class */
public class JSONObjectIter implements Iterable<JSONObject> {
    Iterator<Object> iterator;

    public JSONObjectIter(Iterator<Object> iterator) {
        this.iterator = iterator;
    }

    @Override // java.lang.Iterable
    public Iterator<JSONObject> iterator() {
        return new Iterator<JSONObject>() { // from class: cn.hutool.json.JSONObjectIter.1
            @Override // java.util.Iterator
            public boolean hasNext() {
                return JSONObjectIter.this.iterator.hasNext();
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.util.Iterator
            public JSONObject next() {
                return (JSONObject) JSONObjectIter.this.iterator.next();
            }

            @Override // java.util.Iterator
            public void remove() {
                JSONObjectIter.this.iterator.remove();
            }
        };
    }
}
