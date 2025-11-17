package org.h2.util.json;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/json/JSONNull.class */
public final class JSONNull extends JSONValue {
    public static final JSONNull NULL = new JSONNull();

    private JSONNull() {
    }

    @Override // org.h2.util.json.JSONValue
    public void addTo(JSONTarget<?> jSONTarget) {
        jSONTarget.valueNull();
    }
}
