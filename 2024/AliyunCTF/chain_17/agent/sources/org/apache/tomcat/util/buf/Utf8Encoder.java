package org.apache.tomcat.util.buf;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.StandardCharsets;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.asm.Opcodes;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/buf/Utf8Encoder.class */
public class Utf8Encoder extends CharsetEncoder {
    public Utf8Encoder() {
        super(StandardCharsets.UTF_8, 1.1f, 4.0f);
    }

    @Override // java.nio.charset.CharsetEncoder
    protected CoderResult encodeLoop(CharBuffer in, ByteBuffer out) {
        if (in.hasArray() && out.hasArray()) {
            return encodeHasArray(in, out);
        }
        return encodeNotHasArray(in, out);
    }

    private CoderResult encodeHasArray(CharBuffer in, ByteBuffer out) {
        int outRemaining = out.remaining();
        int pos = in.position();
        int limit = in.limit();
        byte[] bArr = out.array();
        char[] cArr = in.array();
        int outPos = out.position();
        int rem = in.remaining();
        int x = pos;
        while (x < pos + rem) {
            int jchar = cArr[x] & 65535;
            if (jchar <= 127) {
                if (outRemaining < 1) {
                    in.position(x);
                    out.position(outPos);
                    return CoderResult.OVERFLOW;
                }
                int i = outPos;
                outPos++;
                bArr[i] = (byte) (jchar & Const.MAX_ARRAY_DIMENSIONS);
                outRemaining--;
            } else if (jchar <= 2047) {
                if (outRemaining < 2) {
                    in.position(x);
                    out.position(outPos);
                    return CoderResult.OVERFLOW;
                }
                int i2 = outPos;
                int outPos2 = outPos + 1;
                bArr[i2] = (byte) (Opcodes.CHECKCAST + ((jchar >> 6) & 31));
                outPos = outPos2 + 1;
                bArr[outPos2] = (byte) (128 + (jchar & 63));
                outRemaining -= 2;
            } else if (jchar >= 55296 && jchar <= 57343) {
                if (limit <= x + 1) {
                    in.position(x);
                    out.position(outPos);
                    return CoderResult.UNDERFLOW;
                }
                if (outRemaining < 4) {
                    in.position(x);
                    out.position(outPos);
                    return CoderResult.OVERFLOW;
                }
                if (jchar >= 56320) {
                    in.position(x);
                    out.position(outPos);
                    return CoderResult.malformedForLength(1);
                }
                int jchar2 = cArr[x + 1] & 65535;
                if (jchar2 < 56320) {
                    in.position(x);
                    out.position(outPos);
                    return CoderResult.malformedForLength(1);
                }
                int n = ((jchar << 10) + jchar2) - 56613888;
                int i3 = outPos;
                int outPos3 = outPos + 1;
                bArr[i3] = (byte) (240 + ((n >> 18) & 7));
                int outPos4 = outPos3 + 1;
                bArr[outPos3] = (byte) (128 + ((n >> 12) & 63));
                int outPos5 = outPos4 + 1;
                bArr[outPos4] = (byte) (128 + ((n >> 6) & 63));
                outPos = outPos5 + 1;
                bArr[outPos5] = (byte) (128 + (n & 63));
                outRemaining -= 4;
                x++;
            } else {
                if (outRemaining < 3) {
                    in.position(x);
                    out.position(outPos);
                    return CoderResult.OVERFLOW;
                }
                int i4 = outPos;
                int outPos6 = outPos + 1;
                bArr[i4] = (byte) (224 + ((jchar >> 12) & 15));
                int outPos7 = outPos6 + 1;
                bArr[outPos6] = (byte) (128 + ((jchar >> 6) & 63));
                outPos = outPos7 + 1;
                bArr[outPos7] = (byte) (128 + (jchar & 63));
                outRemaining -= 3;
            }
            if (outRemaining != 0) {
                x++;
            } else {
                in.position(x + 1);
                out.position(outPos);
                if (x + 1 == limit) {
                    return CoderResult.UNDERFLOW;
                }
                return CoderResult.OVERFLOW;
            }
        }
        if (rem != 0) {
            in.position(x);
            out.position(outPos);
        }
        return CoderResult.UNDERFLOW;
    }

    private CoderResult encodeNotHasArray(CharBuffer in, ByteBuffer out) {
        int outRemaining = out.remaining();
        int pos = in.position();
        int limit = in.limit();
        while (pos < limit) {
            if (outRemaining == 0) {
                CoderResult coderResult = CoderResult.OVERFLOW;
                in.position(pos);
                return coderResult;
            }
            try {
                int jchar = in.get() & 65535;
                if (jchar <= 127) {
                    if (outRemaining < 1) {
                        CoderResult coderResult2 = CoderResult.OVERFLOW;
                        in.position(pos);
                        return coderResult2;
                    }
                    out.put((byte) jchar);
                    outRemaining--;
                } else if (jchar <= 2047) {
                    if (outRemaining < 2) {
                        CoderResult coderResult3 = CoderResult.OVERFLOW;
                        in.position(pos);
                        return coderResult3;
                    }
                    out.put((byte) (Opcodes.CHECKCAST + ((jchar >> 6) & 31)));
                    out.put((byte) (128 + (jchar & 63)));
                    outRemaining -= 2;
                } else if (jchar < 55296 || jchar > 57343) {
                    if (outRemaining < 3) {
                        CoderResult coderResult4 = CoderResult.OVERFLOW;
                        in.position(pos);
                        return coderResult4;
                    }
                    out.put((byte) (224 + ((jchar >> 12) & 15)));
                    out.put((byte) (128 + ((jchar >> 6) & 63)));
                    out.put((byte) (128 + (jchar & 63)));
                    outRemaining -= 3;
                } else {
                    if (limit <= pos + 1) {
                        CoderResult coderResult5 = CoderResult.UNDERFLOW;
                        in.position(pos);
                        return coderResult5;
                    }
                    if (outRemaining < 4) {
                        CoderResult coderResult6 = CoderResult.OVERFLOW;
                        in.position(pos);
                        return coderResult6;
                    }
                    if (jchar >= 56320) {
                        CoderResult malformedForLength = CoderResult.malformedForLength(1);
                        in.position(pos);
                        return malformedForLength;
                    }
                    int jchar2 = in.get() & 65535;
                    if (jchar2 < 56320) {
                        CoderResult malformedForLength2 = CoderResult.malformedForLength(1);
                        in.position(pos);
                        return malformedForLength2;
                    }
                    int n = ((jchar << 10) + jchar2) - 56613888;
                    out.put((byte) (240 + ((n >> 18) & 7)));
                    out.put((byte) (128 + ((n >> 12) & 63)));
                    out.put((byte) (128 + ((n >> 6) & 63)));
                    out.put((byte) (128 + (n & 63)));
                    outRemaining -= 4;
                    pos++;
                }
                pos++;
            } finally {
                in.position(pos);
            }
        }
        return CoderResult.UNDERFLOW;
    }
}
