package org.h2.util.json;

import java.math.BigDecimal;
import org.h2.util.ByteStack;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/json/JSONStringTarget.class */
public final class JSONStringTarget extends JSONTarget<String> {
    static final char[] HEX = "0123456789abcdef".toCharArray();
    static final byte OBJECT = 1;
    static final byte ARRAY = 2;
    private final StringBuilder builder;
    private final ByteStack stack;
    private final boolean asciiPrintableOnly;
    private boolean needSeparator;
    private boolean afterName;

    public static StringBuilder encodeString(StringBuilder sb, String str, boolean z) {
        sb.append('\"');
        int length = str.length();
        for (int i = 0; i < length; i++) {
            char charAt = str.charAt(i);
            switch (charAt) {
                case '\b':
                    sb.append("\\b");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\"':
                    sb.append("\\\"");
                    break;
                case '\'':
                    if (z) {
                        sb.append("\\u0027");
                        break;
                    } else {
                        sb.append('\'');
                        break;
                    }
                case '\\':
                    sb.append("\\\\");
                    break;
                default:
                    if (charAt < ' ') {
                        sb.append("\\u00").append(HEX[(charAt >>> 4) & 15]).append(HEX[charAt & 15]);
                        break;
                    } else if (!z || charAt <= 127) {
                        sb.append(charAt);
                        break;
                    } else {
                        sb.append("\\u").append(HEX[(charAt >>> '\f') & 15]).append(HEX[(charAt >>> '\b') & 15]).append(HEX[(charAt >>> 4) & 15]).append(HEX[charAt & 15]);
                        break;
                    }
                    break;
            }
        }
        return sb.append('\"');
    }

    public JSONStringTarget() {
        this(false);
    }

    public JSONStringTarget(boolean z) {
        this.builder = new StringBuilder();
        this.stack = new ByteStack();
        this.asciiPrintableOnly = z;
    }

    @Override // org.h2.util.json.JSONTarget
    public void startObject() {
        beforeValue();
        this.afterName = false;
        this.stack.push((byte) 1);
        this.builder.append('{');
    }

    @Override // org.h2.util.json.JSONTarget
    public void endObject() {
        if (this.afterName || this.stack.poll(-1) != 1) {
            throw new IllegalStateException();
        }
        this.builder.append('}');
        afterValue();
    }

    @Override // org.h2.util.json.JSONTarget
    public void startArray() {
        beforeValue();
        this.afterName = false;
        this.stack.push((byte) 2);
        this.builder.append('[');
    }

    @Override // org.h2.util.json.JSONTarget
    public void endArray() {
        if (this.stack.poll(-1) != 2) {
            throw new IllegalStateException();
        }
        this.builder.append(']');
        afterValue();
    }

    @Override // org.h2.util.json.JSONTarget
    public void member(String str) {
        if (this.afterName || this.stack.peek(-1) != 1) {
            throw new IllegalStateException();
        }
        this.afterName = true;
        beforeValue();
        encodeString(this.builder, str, this.asciiPrintableOnly).append(':');
    }

    @Override // org.h2.util.json.JSONTarget
    public void valueNull() {
        beforeValue();
        this.builder.append("null");
        afterValue();
    }

    @Override // org.h2.util.json.JSONTarget
    public void valueFalse() {
        beforeValue();
        this.builder.append("false");
        afterValue();
    }

    @Override // org.h2.util.json.JSONTarget
    public void valueTrue() {
        beforeValue();
        this.builder.append("true");
        afterValue();
    }

    @Override // org.h2.util.json.JSONTarget
    public void valueNumber(BigDecimal bigDecimal) {
        beforeValue();
        String bigDecimal2 = bigDecimal.toString();
        int indexOf = bigDecimal2.indexOf(69);
        if (indexOf >= 0) {
            int i = indexOf + 1;
            if (bigDecimal2.charAt(i) == '+') {
                this.builder.append((CharSequence) bigDecimal2, 0, i).append((CharSequence) bigDecimal2, i + 1, bigDecimal2.length());
                afterValue();
            }
        }
        this.builder.append(bigDecimal2);
        afterValue();
    }

    @Override // org.h2.util.json.JSONTarget
    public void valueString(String str) {
        beforeValue();
        encodeString(this.builder, str, this.asciiPrintableOnly);
        afterValue();
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
            this.builder.append(',');
        }
    }

    private void afterValue() {
        this.needSeparator = true;
        this.afterName = false;
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
    @Override // org.h2.util.json.JSONTarget
    public String getResult() {
        if (!this.stack.isEmpty() || this.builder.length() == 0) {
            throw new IllegalStateException();
        }
        return this.builder.toString();
    }
}
