package org.h2.util.json;

import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/json/JSONValidationTargetWithUniqueKeys.class */
public final class JSONValidationTargetWithUniqueKeys extends JSONValidationTarget {
    private final ArrayDeque<Object> stack = new ArrayDeque<>();
    private final ArrayDeque<String> names = new ArrayDeque<>();
    private boolean needSeparator;
    private String memberName;
    private JSONItemType type;

    @Override // org.h2.util.json.JSONTarget
    public void startObject() {
        beforeValue();
        this.names.push(this.memberName != null ? this.memberName : "");
        this.memberName = null;
        this.stack.push(new HashSet());
    }

    @Override // org.h2.util.json.JSONTarget
    public void endObject() {
        if (this.memberName != null) {
            throw new IllegalStateException();
        }
        if (!(this.stack.poll() instanceof HashSet)) {
            throw new IllegalStateException();
        }
        this.memberName = this.names.pop();
        afterValue(JSONItemType.OBJECT);
    }

    @Override // org.h2.util.json.JSONTarget
    public void startArray() {
        beforeValue();
        this.names.push(this.memberName != null ? this.memberName : "");
        this.memberName = null;
        this.stack.push(Collections.emptyList());
    }

    @Override // org.h2.util.json.JSONTarget
    public void endArray() {
        if (!(this.stack.poll() instanceof List)) {
            throw new IllegalStateException();
        }
        this.memberName = this.names.pop();
        afterValue(JSONItemType.ARRAY);
    }

    @Override // org.h2.util.json.JSONTarget
    public void member(String str) {
        if (this.memberName != null || !(this.stack.peek() instanceof HashSet)) {
            throw new IllegalStateException();
        }
        this.memberName = str;
        beforeValue();
    }

    @Override // org.h2.util.json.JSONTarget
    public void valueNull() {
        beforeValue();
        afterValue(JSONItemType.SCALAR);
    }

    @Override // org.h2.util.json.JSONTarget
    public void valueFalse() {
        beforeValue();
        afterValue(JSONItemType.SCALAR);
    }

    @Override // org.h2.util.json.JSONTarget
    public void valueTrue() {
        beforeValue();
        afterValue(JSONItemType.SCALAR);
    }

    @Override // org.h2.util.json.JSONTarget
    public void valueNumber(BigDecimal bigDecimal) {
        beforeValue();
        afterValue(JSONItemType.SCALAR);
    }

    @Override // org.h2.util.json.JSONTarget
    public void valueString(String str) {
        beforeValue();
        afterValue(JSONItemType.SCALAR);
    }

    private void beforeValue() {
        if (this.memberName == null && (this.stack.peek() instanceof HashSet)) {
            throw new IllegalStateException();
        }
        if (this.needSeparator) {
            if (this.stack.isEmpty()) {
                throw new IllegalStateException();
            }
            this.needSeparator = false;
        }
    }

    private void afterValue(JSONItemType jSONItemType) {
        Object peek = this.stack.peek();
        if (peek == null) {
            this.type = jSONItemType;
        } else if ((peek instanceof HashSet) && !((HashSet) peek).add(this.memberName)) {
            throw new IllegalStateException();
        }
        this.needSeparator = true;
        this.memberName = null;
    }

    @Override // org.h2.util.json.JSONTarget
    public boolean isPropertyExpected() {
        return this.memberName == null && (this.stack.peek() instanceof HashSet);
    }

    @Override // org.h2.util.json.JSONTarget
    public boolean isValueSeparatorExpected() {
        return this.needSeparator;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.h2.util.json.JSONValidationTarget, org.h2.util.json.JSONTarget
    public JSONItemType getResult() {
        if (!this.stack.isEmpty() || this.type == null) {
            throw new IllegalStateException();
        }
        return this.type;
    }
}
