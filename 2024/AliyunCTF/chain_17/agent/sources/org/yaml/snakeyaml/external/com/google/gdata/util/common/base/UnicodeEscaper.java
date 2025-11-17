package org.yaml.snakeyaml.external.com.google.gdata.util.common.base;

import java.io.IOException;

/* loaded from: agent.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/external/com/google/gdata/util/common/base/UnicodeEscaper.class */
public abstract class UnicodeEscaper implements Escaper {
    private static final int DEST_PAD = 32;
    private static final ThreadLocal<char[]> DEST_TL;
    static final /* synthetic */ boolean $assertionsDisabled;

    protected abstract char[] escape(int i);

    static {
        $assertionsDisabled = !UnicodeEscaper.class.desiredAssertionStatus();
        DEST_TL = new ThreadLocal<char[]>() { // from class: org.yaml.snakeyaml.external.com.google.gdata.util.common.base.UnicodeEscaper.2
            /* JADX INFO: Access modifiers changed from: protected */
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.lang.ThreadLocal
            public char[] initialValue() {
                return new char[1024];
            }
        };
    }

    protected int nextEscapeIndex(CharSequence csq, int start, int end) {
        int index;
        int cp;
        int i = start;
        while (true) {
            index = i;
            if (index >= end || (cp = codePointAt(csq, index, end)) < 0 || escape(cp) != null) {
                break;
            }
            i = index + (Character.isSupplementaryCodePoint(cp) ? 2 : 1);
        }
        return index;
    }

    @Override // org.yaml.snakeyaml.external.com.google.gdata.util.common.base.Escaper
    public String escape(String string) {
        int end = string.length();
        int index = nextEscapeIndex(string, 0, end);
        return index == end ? string : escapeSlow(string, index);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final String escapeSlow(String s, int index) {
        int end = s.length();
        char[] dest = DEST_TL.get();
        int destIndex = 0;
        int unescapedChunkStart = 0;
        while (index < end) {
            int cp = codePointAt(s, index, end);
            if (cp < 0) {
                throw new IllegalArgumentException("Trailing high surrogate at end of input");
            }
            char[] escaped = escape(cp);
            if (escaped != null) {
                int charsSkipped = index - unescapedChunkStart;
                int sizeNeeded = destIndex + charsSkipped + escaped.length;
                if (dest.length < sizeNeeded) {
                    int destLength = sizeNeeded + (end - index) + 32;
                    dest = growBuffer(dest, destIndex, destLength);
                }
                if (charsSkipped > 0) {
                    s.getChars(unescapedChunkStart, index, dest, destIndex);
                    destIndex += charsSkipped;
                }
                if (escaped.length > 0) {
                    System.arraycopy(escaped, 0, dest, destIndex, escaped.length);
                    destIndex += escaped.length;
                }
            }
            unescapedChunkStart = index + (Character.isSupplementaryCodePoint(cp) ? 2 : 1);
            index = nextEscapeIndex(s, unescapedChunkStart, end);
        }
        int charsSkipped2 = end - unescapedChunkStart;
        if (charsSkipped2 > 0) {
            int endIndex = destIndex + charsSkipped2;
            if (dest.length < endIndex) {
                dest = growBuffer(dest, destIndex, endIndex);
            }
            s.getChars(unescapedChunkStart, end, dest, destIndex);
            destIndex = endIndex;
        }
        return new String(dest, 0, destIndex);
    }

    @Override // org.yaml.snakeyaml.external.com.google.gdata.util.common.base.Escaper
    public Appendable escape(final Appendable out) {
        if ($assertionsDisabled || out != null) {
            return new Appendable() { // from class: org.yaml.snakeyaml.external.com.google.gdata.util.common.base.UnicodeEscaper.1
                int pendingHighSurrogate = -1;
                final char[] decodedChars = new char[2];

                @Override // java.lang.Appendable
                public Appendable append(CharSequence csq) throws IOException {
                    return append(csq, 0, csq.length());
                }

                @Override // java.lang.Appendable
                public Appendable append(CharSequence csq, int start, int end) throws IOException {
                    int index = start;
                    if (index < end) {
                        int unescapedChunkStart = index;
                        if (this.pendingHighSurrogate != -1) {
                            index++;
                            char c = csq.charAt(index);
                            if (!Character.isLowSurrogate(c)) {
                                throw new IllegalArgumentException("Expected low surrogate character but got " + c);
                            }
                            char[] escaped = UnicodeEscaper.this.escape(Character.toCodePoint((char) this.pendingHighSurrogate, c));
                            if (escaped != null) {
                                outputChars(escaped, escaped.length);
                                unescapedChunkStart++;
                            } else {
                                out.append((char) this.pendingHighSurrogate);
                            }
                            this.pendingHighSurrogate = -1;
                        }
                        while (true) {
                            int index2 = UnicodeEscaper.this.nextEscapeIndex(csq, index, end);
                            if (index2 > unescapedChunkStart) {
                                out.append(csq, unescapedChunkStart, index2);
                            }
                            if (index2 == end) {
                                break;
                            }
                            int cp = UnicodeEscaper.codePointAt(csq, index2, end);
                            if (cp < 0) {
                                this.pendingHighSurrogate = -cp;
                                break;
                            }
                            char[] escaped2 = UnicodeEscaper.this.escape(cp);
                            if (escaped2 != null) {
                                outputChars(escaped2, escaped2.length);
                            } else {
                                int len = Character.toChars(cp, this.decodedChars, 0);
                                outputChars(this.decodedChars, len);
                            }
                            index = index2 + (Character.isSupplementaryCodePoint(cp) ? 2 : 1);
                            unescapedChunkStart = index;
                        }
                    }
                    return this;
                }

                @Override // java.lang.Appendable
                public Appendable append(char c) throws IOException {
                    if (this.pendingHighSurrogate != -1) {
                        if (!Character.isLowSurrogate(c)) {
                            throw new IllegalArgumentException("Expected low surrogate character but got '" + c + "' with value " + ((int) c));
                        }
                        char[] escaped = UnicodeEscaper.this.escape(Character.toCodePoint((char) this.pendingHighSurrogate, c));
                        if (escaped != null) {
                            outputChars(escaped, escaped.length);
                        } else {
                            out.append((char) this.pendingHighSurrogate);
                            out.append(c);
                        }
                        this.pendingHighSurrogate = -1;
                    } else if (Character.isHighSurrogate(c)) {
                        this.pendingHighSurrogate = c;
                    } else {
                        if (Character.isLowSurrogate(c)) {
                            throw new IllegalArgumentException("Unexpected low surrogate character '" + c + "' with value " + ((int) c));
                        }
                        char[] escaped2 = UnicodeEscaper.this.escape(c);
                        if (escaped2 != null) {
                            outputChars(escaped2, escaped2.length);
                        } else {
                            out.append(c);
                        }
                    }
                    return this;
                }

                private void outputChars(char[] chars, int len) throws IOException {
                    for (int n = 0; n < len; n++) {
                        out.append(chars[n]);
                    }
                }
            };
        }
        throw new AssertionError();
    }

    protected static final int codePointAt(CharSequence seq, int index, int end) {
        if (index < end) {
            int index2 = index + 1;
            char c1 = seq.charAt(index);
            if (c1 < 55296 || c1 > 57343) {
                return c1;
            }
            if (c1 <= 56319) {
                if (index2 == end) {
                    return -c1;
                }
                char c2 = seq.charAt(index2);
                if (Character.isLowSurrogate(c2)) {
                    return Character.toCodePoint(c1, c2);
                }
                throw new IllegalArgumentException("Expected low surrogate but got char '" + c2 + "' with value " + ((int) c2) + " at index " + index2);
            }
            throw new IllegalArgumentException("Unexpected low surrogate character '" + c1 + "' with value " + ((int) c1) + " at index " + (index2 - 1));
        }
        throw new IndexOutOfBoundsException("Index exceeds specified range");
    }

    private static final char[] growBuffer(char[] dest, int index, int size) {
        char[] copy = new char[size];
        if (index > 0) {
            System.arraycopy(dest, 0, copy, 0, index);
        }
        return copy;
    }
}
