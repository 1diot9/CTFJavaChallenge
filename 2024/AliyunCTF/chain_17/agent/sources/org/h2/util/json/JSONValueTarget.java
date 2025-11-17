package org.h2.util.json;

import java.math.BigDecimal;
import java.util.ArrayDeque;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/json/JSONValueTarget.class */
public final class JSONValueTarget extends JSONTarget<JSONValue> {
    private final ArrayDeque<JSONValue> stack = new ArrayDeque<>();
    private final ArrayDeque<String> names = new ArrayDeque<>();
    private boolean needSeparator;
    private String memberName;
    private JSONValue result;

    @Override // org.h2.util.json.JSONTarget
    public void startObject() {
        beforeValue();
        this.names.push(this.memberName != null ? this.memberName : "");
        this.memberName = null;
        this.stack.push(new JSONObject());
    }

    @Override // org.h2.util.json.JSONTarget
    public void endObject() {
        if (this.memberName != null) {
            throw new IllegalStateException();
        }
        JSONValue poll = this.stack.poll();
        if (!(poll instanceof JSONObject)) {
            throw new IllegalStateException();
        }
        this.memberName = this.names.pop();
        afterValue(poll);
    }

    @Override // org.h2.util.json.JSONTarget
    public void startArray() {
        beforeValue();
        this.names.push(this.memberName != null ? this.memberName : "");
        this.memberName = null;
        this.stack.push(new JSONArray());
    }

    @Override // org.h2.util.json.JSONTarget
    public void endArray() {
        JSONValue poll = this.stack.poll();
        if (!(poll instanceof JSONArray)) {
            throw new IllegalStateException();
        }
        this.memberName = this.names.pop();
        afterValue(poll);
    }

    @Override // org.h2.util.json.JSONTarget
    public void member(String str) {
        if (this.memberName != null || !(this.stack.peek() instanceof JSONObject)) {
            throw new IllegalStateException();
        }
        this.memberName = str;
        beforeValue();
    }

    @Override // org.h2.util.json.JSONTarget
    public void valueNull() {
        beforeValue();
        afterValue(JSONNull.NULL);
    }

    @Override // org.h2.util.json.JSONTarget
    public void valueFalse() {
        beforeValue();
        afterValue(JSONBoolean.FALSE);
    }

    @Override // org.h2.util.json.JSONTarget
    public void valueTrue() {
        beforeValue();
        afterValue(JSONBoolean.TRUE);
    }

    @Override // org.h2.util.json.JSONTarget
    public void valueNumber(BigDecimal bigDecimal) {
        beforeValue();
        afterValue(new JSONNumber(bigDecimal));
    }

    @Override // org.h2.util.json.JSONTarget
    public void valueString(String str) {
        beforeValue();
        afterValue(new JSONString(str));
    }

    private void beforeValue() {
        if (this.memberName == null && (this.stack.peek() instanceof JSONObject)) {
            throw new IllegalStateException();
        }
        if (this.needSeparator) {
            if (this.stack.isEmpty()) {
                throw new IllegalStateException();
            }
            this.needSeparator = false;
        }
    }

    private void afterValue(JSONValue jSONValue) {
        JSONValue peek = this.stack.peek();
        if (peek == null) {
            this.result = jSONValue;
        } else if (peek instanceof JSONObject) {
            ((JSONObject) peek).addMember(this.memberName, jSONValue);
        } else {
            ((JSONArray) peek).addElement(jSONValue);
        }
        this.needSeparator = true;
        this.memberName = null;
    }

    @Override // org.h2.util.json.JSONTarget
    public boolean isPropertyExpected() {
        return this.memberName == null && (this.stack.peek() instanceof JSONObject);
    }

    @Override // org.h2.util.json.JSONTarget
    public boolean isValueSeparatorExpected() {
        return this.needSeparator;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.h2.util.json.JSONTarget
    public JSONValue getResult() {
        if (!this.stack.isEmpty() || this.result == null) {
            throw new IllegalStateException();
        }
        return this.result;
    }
}
