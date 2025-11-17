package org.h2.util.json;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Function;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/json/JSONArray.class */
public final class JSONArray extends JSONValue {
    private final ArrayList<JSONValue> elements = new ArrayList<>();

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addElement(JSONValue jSONValue) {
        this.elements.add(jSONValue);
    }

    @Override // org.h2.util.json.JSONValue
    public void addTo(JSONTarget<?> jSONTarget) {
        jSONTarget.startArray();
        Iterator<JSONValue> it = this.elements.iterator();
        while (it.hasNext()) {
            it.next().addTo(jSONTarget);
        }
        jSONTarget.endArray();
    }

    public int length() {
        return this.elements.size();
    }

    public JSONValue[] getArray() {
        return (JSONValue[]) this.elements.toArray(new JSONValue[0]);
    }

    public <E> E[] getArray(Class<E> cls, Function<JSONValue, E> function) {
        int size = this.elements.size();
        E[] eArr = (E[]) ((Object[]) Array.newInstance((Class<?>) cls, size));
        for (int i = 0; i < size; i++) {
            eArr[i] = function.apply(this.elements.get(i));
        }
        return eArr;
    }

    public JSONValue getElement(int i) {
        if (i >= 0 && i < this.elements.size()) {
            return this.elements.get(i);
        }
        return null;
    }
}
