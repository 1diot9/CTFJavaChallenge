package org.h2.util.json;

import java.math.BigDecimal;
import org.h2.util.ByteStack;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/json/JSONValidationTargetWithoutUniqueKeys.class */
public final class JSONValidationTargetWithoutUniqueKeys extends JSONValidationTarget {
    private static final byte OBJECT = 1;
    private static final byte ARRAY = 2;
    private JSONItemType type;
    private final ByteStack stack = new ByteStack();
    private boolean needSeparator;
    private boolean afterName;

    @Override // org.h2.util.json.JSONTarget
    public void startObject() {
        beforeValue();
        this.afterName = false;
        this.stack.push((byte) 1);
    }

    @Override // org.h2.util.json.JSONTarget
    public void endObject() {
        if (this.afterName || this.stack.poll(-1) != 1) {
            throw new IllegalStateException();
        }
        afterValue(JSONItemType.OBJECT);
    }

    @Override // org.h2.util.json.JSONTarget
    public void startArray() {
        beforeValue();
        this.afterName = false;
        this.stack.push((byte) 2);
    }

    @Override // org.h2.util.json.JSONTarget
    public void endArray() {
        if (this.stack.poll(-1) != 2) {
            throw new IllegalStateException();
        }
        afterValue(JSONItemType.ARRAY);
    }

    @Override // org.h2.util.json.JSONTarget
    public void member(String str) {
        if (this.afterName || this.stack.peek(-1) != 1) {
            throw new IllegalStateException();
        }
        this.afterName = true;
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
        if (!this.afterName && this.stack.peek(-1) == 1) {
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
        this.needSeparator = true;
        this.afterName = false;
        if (this.stack.isEmpty()) {
            this.type = jSONItemType;
        }
    }

    @Override // org.h2.util.json.JSONTarget
    public boolean isPropertyExpected() {
        return !this.afterName && this.stack.peek(-1) == 1;
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
