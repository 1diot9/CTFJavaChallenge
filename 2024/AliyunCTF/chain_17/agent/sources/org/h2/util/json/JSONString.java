package org.h2.util.json;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/json/JSONString.class */
public final class JSONString extends JSONValue {
    private final String value;

    /* JADX INFO: Access modifiers changed from: package-private */
    public JSONString(String str) {
        this.value = str;
    }

    @Override // org.h2.util.json.JSONValue
    public void addTo(JSONTarget<?> jSONTarget) {
        jSONTarget.valueString(this.value);
    }

    public String getString() {
        return this.value;
    }
}
