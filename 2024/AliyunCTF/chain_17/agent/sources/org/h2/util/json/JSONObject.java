package org.h2.util.json;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/json/JSONObject.class */
public final class JSONObject extends JSONValue {
    private final ArrayList<AbstractMap.SimpleImmutableEntry<String, JSONValue>> members = new ArrayList<>();

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addMember(String str, JSONValue jSONValue) {
        this.members.add(new AbstractMap.SimpleImmutableEntry<>(str, jSONValue));
    }

    @Override // org.h2.util.json.JSONValue
    public void addTo(JSONTarget<?> jSONTarget) {
        jSONTarget.startObject();
        Iterator<AbstractMap.SimpleImmutableEntry<String, JSONValue>> it = this.members.iterator();
        while (it.hasNext()) {
            AbstractMap.SimpleImmutableEntry<String, JSONValue> next = it.next();
            jSONTarget.member(next.getKey());
            next.getValue().addTo(jSONTarget);
        }
        jSONTarget.endObject();
    }

    public Map.Entry<String, JSONValue>[] getMembers() {
        return (Map.Entry[]) this.members.toArray(new Map.Entry[0]);
    }

    public JSONValue getFirst(String str) {
        Iterator<AbstractMap.SimpleImmutableEntry<String, JSONValue>> it = this.members.iterator();
        while (it.hasNext()) {
            AbstractMap.SimpleImmutableEntry<String, JSONValue> next = it.next();
            if (str.equals(next.getKey())) {
                return next.getValue();
            }
        }
        return null;
    }
}
