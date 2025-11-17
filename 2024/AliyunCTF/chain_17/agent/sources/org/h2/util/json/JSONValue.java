package org.h2.util.json;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/json/JSONValue.class */
public abstract class JSONValue {
    public abstract void addTo(JSONTarget<?> jSONTarget);

    public final String toString() {
        JSONStringTarget jSONStringTarget = new JSONStringTarget();
        addTo(jSONStringTarget);
        return jSONStringTarget.getResult();
    }
}
