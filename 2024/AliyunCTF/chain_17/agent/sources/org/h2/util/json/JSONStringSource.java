package org.h2.util.json;

import java.math.BigDecimal;
import org.h2.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/json/JSONStringSource.class */
public final class JSONStringSource extends JSONTextSource {
    private final String string;
    private final int length;
    private int index;

    public static <R> R parse(String str, JSONTarget<R> jSONTarget) {
        new JSONStringSource(str, jSONTarget).parse();
        return jSONTarget.getResult();
    }

    public static byte[] normalize(String str) {
        return (byte[]) parse(str, new JSONByteArrayTarget());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public JSONStringSource(String str, JSONTarget<?> jSONTarget) {
        super(jSONTarget);
        this.string = str;
        this.length = str.length();
        if (this.length == 0) {
            throw new IllegalArgumentException();
        }
        if (str.charAt(this.index) == 65279) {
            this.index++;
        }
    }

    @Override // org.h2.util.json.JSONTextSource
    int nextCharAfterWhitespace() {
        int i = this.index;
        while (i < this.length) {
            int i2 = i;
            i++;
            char charAt = this.string.charAt(i2);
            switch (charAt) {
                case '\t':
                case '\n':
                case '\r':
                case ' ':
                default:
                    this.index = i;
                    return charAt;
            }
        }
        return -1;
    }

    @Override // org.h2.util.json.JSONTextSource
    void readKeyword1(String str) {
        int length = str.length() - 1;
        if (!this.string.regionMatches(this.index, str, 1, length)) {
            throw new IllegalArgumentException();
        }
        this.index += length;
    }

    @Override // org.h2.util.json.JSONTextSource
    void parseNumber(boolean z) {
        int i = this.index;
        int i2 = i - 1;
        int skipInt = skipInt(i, z);
        if (skipInt < this.length) {
            char charAt = this.string.charAt(skipInt);
            if (charAt == '.') {
                skipInt = skipInt(skipInt + 1, false);
                if (skipInt < this.length) {
                    charAt = this.string.charAt(skipInt);
                }
            }
            if (charAt == 'E' || charAt == 'e') {
                int i3 = skipInt + 1;
                if (i3 >= this.length) {
                    throw new IllegalArgumentException();
                }
                char charAt2 = this.string.charAt(i3);
                if (charAt2 == '+' || charAt2 == '-') {
                    i3++;
                }
                skipInt = skipInt(i3, false);
            }
        }
        this.target.valueNumber(new BigDecimal(this.string.substring(i2, skipInt)));
        this.index = skipInt;
    }

    private int skipInt(int i, boolean z) {
        char charAt;
        while (i < this.length && (charAt = this.string.charAt(i)) >= '0' && charAt <= '9') {
            z = true;
            i++;
        }
        if (!z) {
            throw new IllegalArgumentException();
        }
        return i;
    }

    @Override // org.h2.util.json.JSONTextSource
    int nextChar() {
        if (this.index >= this.length) {
            throw new IllegalArgumentException();
        }
        String str = this.string;
        int i = this.index;
        this.index = i + 1;
        return str.charAt(i);
    }

    @Override // org.h2.util.json.JSONTextSource
    char readHex() {
        if (this.index + 3 >= this.length) {
            throw new IllegalArgumentException();
        }
        try {
            String str = this.string;
            int i = this.index;
            int i2 = this.index + 4;
            this.index = i2;
            return (char) Integer.parseInt(str.substring(i, i2), 16);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException();
        }
    }

    public String toString() {
        return StringUtils.addAsterisk(this.string, this.index);
    }
}
