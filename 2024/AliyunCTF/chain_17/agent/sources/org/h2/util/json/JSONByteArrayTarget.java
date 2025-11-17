package org.h2.util.json;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import org.h2.util.ByteStack;
import org.springframework.asm.Opcodes;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/json/JSONByteArrayTarget.class */
public final class JSONByteArrayTarget extends JSONTarget<byte[]> {
    private static final byte[] NULL_BYTES = "null".getBytes(StandardCharsets.ISO_8859_1);
    private static final byte[] FALSE_BYTES = "false".getBytes(StandardCharsets.ISO_8859_1);
    private static final byte[] TRUE_BYTES = "true".getBytes(StandardCharsets.ISO_8859_1);
    private static final byte[] U00_BYTES = "\\u00".getBytes(StandardCharsets.ISO_8859_1);
    private final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    private final ByteStack stack = new ByteStack();
    private boolean needSeparator;
    private boolean afterName;

    public static ByteArrayOutputStream encodeString(ByteArrayOutputStream byteArrayOutputStream, String str) {
        byteArrayOutputStream.write(34);
        int i = 0;
        int length = str.length();
        while (i < length) {
            char charAt = str.charAt(i);
            switch (charAt) {
                case '\b':
                    byteArrayOutputStream.write(92);
                    byteArrayOutputStream.write(98);
                    break;
                case '\t':
                    byteArrayOutputStream.write(92);
                    byteArrayOutputStream.write(116);
                    break;
                case '\n':
                    byteArrayOutputStream.write(92);
                    byteArrayOutputStream.write(Opcodes.FDIV);
                    break;
                case '\f':
                    byteArrayOutputStream.write(92);
                    byteArrayOutputStream.write(102);
                    break;
                case '\r':
                    byteArrayOutputStream.write(92);
                    byteArrayOutputStream.write(Opcodes.FREM);
                    break;
                case '\"':
                    byteArrayOutputStream.write(92);
                    byteArrayOutputStream.write(34);
                    break;
                case '\\':
                    byteArrayOutputStream.write(92);
                    byteArrayOutputStream.write(92);
                    break;
                default:
                    if (charAt >= ' ') {
                        if (charAt < 128) {
                            byteArrayOutputStream.write(charAt);
                            break;
                        } else if (charAt < 2048) {
                            byteArrayOutputStream.write(192 | (charAt >> 6));
                            byteArrayOutputStream.write(128 | (charAt & '?'));
                            break;
                        } else if (!Character.isSurrogate(charAt)) {
                            byteArrayOutputStream.write(224 | (charAt >> '\f'));
                            byteArrayOutputStream.write(128 | ((charAt >> 6) & 63));
                            byteArrayOutputStream.write(128 | (charAt & '?'));
                            break;
                        } else {
                            if (Character.isHighSurrogate(charAt)) {
                                i++;
                                if (i < length) {
                                    char charAt2 = str.charAt(i);
                                    if (Character.isLowSurrogate(charAt2)) {
                                        int codePoint = Character.toCodePoint(charAt, charAt2);
                                        byteArrayOutputStream.write(240 | (codePoint >> 18));
                                        byteArrayOutputStream.write(128 | ((codePoint >> 12) & 63));
                                        byteArrayOutputStream.write(128 | ((codePoint >> 6) & 63));
                                        byteArrayOutputStream.write(128 | (codePoint & 63));
                                        break;
                                    }
                                }
                            }
                            throw new IllegalArgumentException();
                        }
                    } else {
                        byteArrayOutputStream.write(U00_BYTES, 0, 4);
                        byteArrayOutputStream.write(JSONStringTarget.HEX[(charAt >>> 4) & 15]);
                        byteArrayOutputStream.write(JSONStringTarget.HEX[charAt & 15]);
                        break;
                    }
            }
            i++;
        }
        byteArrayOutputStream.write(34);
        return byteArrayOutputStream;
    }

    @Override // org.h2.util.json.JSONTarget
    public void startObject() {
        beforeValue();
        this.afterName = false;
        this.stack.push((byte) 1);
        this.baos.write(123);
    }

    @Override // org.h2.util.json.JSONTarget
    public void endObject() {
        if (this.afterName || this.stack.poll(-1) != 1) {
            throw new IllegalStateException();
        }
        this.baos.write(125);
        afterValue();
    }

    @Override // org.h2.util.json.JSONTarget
    public void startArray() {
        beforeValue();
        this.afterName = false;
        this.stack.push((byte) 2);
        this.baos.write(91);
    }

    @Override // org.h2.util.json.JSONTarget
    public void endArray() {
        if (this.stack.poll(-1) != 2) {
            throw new IllegalStateException();
        }
        this.baos.write(93);
        afterValue();
    }

    @Override // org.h2.util.json.JSONTarget
    public void member(String str) {
        if (this.afterName || this.stack.peek(-1) != 1) {
            throw new IllegalStateException();
        }
        this.afterName = true;
        beforeValue();
        encodeString(this.baos, str).write(58);
    }

    @Override // org.h2.util.json.JSONTarget
    public void valueNull() {
        beforeValue();
        this.baos.write(NULL_BYTES, 0, 4);
        afterValue();
    }

    @Override // org.h2.util.json.JSONTarget
    public void valueFalse() {
        beforeValue();
        this.baos.write(FALSE_BYTES, 0, 5);
        afterValue();
    }

    @Override // org.h2.util.json.JSONTarget
    public void valueTrue() {
        beforeValue();
        this.baos.write(TRUE_BYTES, 0, 4);
        afterValue();
    }

    @Override // org.h2.util.json.JSONTarget
    public void valueNumber(BigDecimal bigDecimal) {
        beforeValue();
        String bigDecimal2 = bigDecimal.toString();
        int indexOf = bigDecimal2.indexOf(69);
        byte[] bytes = bigDecimal2.getBytes(StandardCharsets.ISO_8859_1);
        if (indexOf >= 0) {
            int i = indexOf + 1;
            if (bigDecimal2.charAt(i) == '+') {
                this.baos.write(bytes, 0, i);
                this.baos.write(bytes, i + 1, (bytes.length - i) - 1);
                afterValue();
            }
        }
        this.baos.write(bytes, 0, bytes.length);
        afterValue();
    }

    @Override // org.h2.util.json.JSONTarget
    public void valueString(String str) {
        beforeValue();
        encodeString(this.baos, str);
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
            this.baos.write(44);
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
    public byte[] getResult() {
        if (!this.stack.isEmpty() || this.baos.size() == 0) {
            throw new IllegalStateException();
        }
        return this.baos.toByteArray();
    }
}
