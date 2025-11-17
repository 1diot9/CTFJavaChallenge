package org.h2.util.json;

import org.springframework.asm.Opcodes;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/json/JSONTextSource.class */
public abstract class JSONTextSource {
    final JSONTarget<?> target;
    private final StringBuilder builder = new StringBuilder();

    abstract int nextCharAfterWhitespace();

    abstract void readKeyword1(String str);

    abstract void parseNumber(boolean z);

    abstract int nextChar();

    abstract char readHex();

    /* JADX INFO: Access modifiers changed from: package-private */
    public JSONTextSource(JSONTarget<?> jSONTarget) {
        this.target = jSONTarget;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Code restructure failed: missing block: B:82:0x0058, code lost:            throw new java.lang.IllegalArgumentException();     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final void parse() {
        /*
            Method dump skipped, instructions count: 411
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.util.json.JSONTextSource.parse():void");
    }

    private String readString() {
        this.builder.setLength(0);
        boolean z = false;
        while (true) {
            int nextChar = nextChar();
            switch (nextChar) {
                case 34:
                    if (z) {
                        throw new IllegalArgumentException();
                    }
                    return this.builder.toString();
                case 92:
                    int nextChar2 = nextChar();
                    switch (nextChar2) {
                        case 34:
                        case 47:
                        case 92:
                            appendNonSurrogate((char) nextChar2, z);
                            break;
                        case 98:
                            appendNonSurrogate('\b', z);
                            break;
                        case 102:
                            appendNonSurrogate('\f', z);
                            break;
                        case Opcodes.FDIV /* 110 */:
                            appendNonSurrogate('\n', z);
                            break;
                        case Opcodes.FREM /* 114 */:
                            appendNonSurrogate('\r', z);
                            break;
                        case 116:
                            appendNonSurrogate('\t', z);
                            break;
                        case Opcodes.LNEG /* 117 */:
                            z = appendChar(readHex(), z);
                            break;
                        default:
                            throw new IllegalArgumentException();
                    }
                default:
                    if (Character.isBmpCodePoint(nextChar)) {
                        z = appendChar((char) nextChar, z);
                        break;
                    } else {
                        if (z) {
                            throw new IllegalArgumentException();
                        }
                        this.builder.appendCodePoint(nextChar);
                        z = false;
                        break;
                    }
            }
        }
    }

    private void appendNonSurrogate(char c, boolean z) {
        if (z) {
            throw new IllegalArgumentException();
        }
        this.builder.append(c);
    }

    private boolean appendChar(char c, boolean z) {
        if (z != Character.isLowSurrogate(c)) {
            throw new IllegalArgumentException();
        }
        if (z) {
            z = false;
        } else if (Character.isHighSurrogate(c)) {
            z = true;
        }
        this.builder.append(c);
        return z;
    }
}
