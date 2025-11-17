package org.h2.util.json;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.springframework.asm.Opcodes;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/json/JSONBytesSource.class */
public final class JSONBytesSource extends JSONTextSource {
    private final byte[] bytes;
    private final int length;
    private int index;

    public static <R> R parse(byte[] bArr, JSONTarget<R> jSONTarget) {
        int length = bArr.length;
        Charset charset = null;
        if (length >= 4) {
            byte b = bArr[0];
            byte b2 = bArr[1];
            byte b3 = bArr[2];
            byte b4 = bArr[3];
            switch (b) {
                case -2:
                    if (b2 == -1) {
                        charset = StandardCharsets.UTF_16BE;
                        break;
                    }
                    break;
                case -1:
                    if (b2 == -2) {
                        if (b3 == 0 && b4 == 0) {
                            charset = Charset.forName("UTF-32LE");
                            break;
                        } else {
                            charset = StandardCharsets.UTF_16LE;
                            break;
                        }
                    }
                    break;
                case 0:
                    if (b2 != 0) {
                        charset = StandardCharsets.UTF_16BE;
                        break;
                    } else if ((b3 == 0 && b4 != 0) || (b3 == -2 && b4 == -1)) {
                        charset = Charset.forName("UTF-32BE");
                        break;
                    }
                    break;
                default:
                    if (b2 == 0) {
                        if (b3 == 0 && b4 == 0) {
                            charset = Charset.forName("UTF-32LE");
                            break;
                        } else {
                            charset = StandardCharsets.UTF_16LE;
                            break;
                        }
                    }
                    break;
            }
        } else if (length >= 2) {
            byte b5 = bArr[0];
            byte b6 = bArr[1];
            if (b5 != 0) {
                if (b6 == 0) {
                    charset = StandardCharsets.UTF_16LE;
                }
            } else if (b6 != 0) {
                charset = StandardCharsets.UTF_16BE;
            }
        }
        (charset == null ? new JSONBytesSource(bArr, jSONTarget) : new JSONStringSource(new String(bArr, charset), jSONTarget)).parse();
        return jSONTarget.getResult();
    }

    public static byte[] normalize(byte[] bArr) {
        return (byte[]) parse(bArr, new JSONByteArrayTarget());
    }

    JSONBytesSource(byte[] bArr, JSONTarget<?> jSONTarget) {
        super(jSONTarget);
        this.bytes = bArr;
        this.length = bArr.length;
        if (nextChar() != 65279) {
            this.index = 0;
        }
    }

    @Override // org.h2.util.json.JSONTextSource
    int nextCharAfterWhitespace() {
        int i = this.index;
        while (i < this.length) {
            int i2 = i;
            i++;
            byte b = this.bytes[i2];
            switch (b) {
                case 9:
                case 10:
                case 13:
                case 32:
                default:
                    if (b < 0) {
                        throw new IllegalArgumentException();
                    }
                    this.index = i;
                    return b;
            }
        }
        return -1;
    }

    @Override // org.h2.util.json.JSONTextSource
    void readKeyword1(String str) {
        int length = str.length() - 1;
        if (this.index + length > this.length) {
            throw new IllegalArgumentException();
        }
        int i = this.index;
        for (int i2 = 1; i2 <= length; i2++) {
            if (this.bytes[i] == str.charAt(i2)) {
                i++;
            } else {
                throw new IllegalArgumentException();
            }
        }
        this.index += length;
    }

    @Override // org.h2.util.json.JSONTextSource
    void parseNumber(boolean z) {
        int i = this.index;
        int i2 = i - 1;
        int skipInt = skipInt(i, z);
        if (skipInt < this.length) {
            byte b = this.bytes[skipInt];
            if (b == 46) {
                skipInt = skipInt(skipInt + 1, false);
                if (skipInt < this.length) {
                    b = this.bytes[skipInt];
                }
            }
            if (b == 69 || b == 101) {
                int i3 = skipInt + 1;
                if (i3 >= this.length) {
                    throw new IllegalArgumentException();
                }
                byte b2 = this.bytes[i3];
                if (b2 == 43 || b2 == 45) {
                    i3++;
                }
                skipInt = skipInt(i3, false);
            }
        }
        this.target.valueNumber(new BigDecimal(new String(this.bytes, i2, skipInt - i2, StandardCharsets.ISO_8859_1)));
        this.index = skipInt;
    }

    private int skipInt(int i, boolean z) {
        byte b;
        while (i < this.length && (b = this.bytes[i]) >= 48 && b <= 57) {
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
        byte[] bArr = this.bytes;
        int i = this.index;
        this.index = i + 1;
        int i2 = bArr[i] & 255;
        if (i2 >= 128) {
            if (i2 >= 224) {
                if (i2 >= 240) {
                    if (this.index + 2 >= this.length) {
                        throw new IllegalArgumentException();
                    }
                    byte[] bArr2 = this.bytes;
                    int i3 = this.index;
                    this.index = i3 + 1;
                    int i4 = bArr2[i3] & 255;
                    byte[] bArr3 = this.bytes;
                    int i5 = this.index;
                    this.index = i5 + 1;
                    int i6 = bArr3[i5] & 255;
                    byte[] bArr4 = this.bytes;
                    int i7 = this.index;
                    this.index = i7 + 1;
                    int i8 = bArr4[i7] & 255;
                    i2 = ((i2 & 15) << 18) + ((i4 & 63) << 12) + ((i6 & 63) << 6) + (i8 & 63);
                    if (i2 < 65536 || i2 > 1114111 || (i4 & Opcodes.CHECKCAST) != 128 || (i6 & Opcodes.CHECKCAST) != 128 || (i8 & Opcodes.CHECKCAST) != 128) {
                        throw new IllegalArgumentException();
                    }
                } else {
                    if (this.index + 1 >= this.length) {
                        throw new IllegalArgumentException();
                    }
                    byte[] bArr5 = this.bytes;
                    int i9 = this.index;
                    this.index = i9 + 1;
                    int i10 = bArr5[i9] & 255;
                    byte[] bArr6 = this.bytes;
                    int i11 = this.index;
                    this.index = i11 + 1;
                    int i12 = bArr6[i11] & 255;
                    i2 = ((i2 & 15) << 12) + ((i10 & 63) << 6) + (i12 & 63);
                    if (i2 < 2048 || (i10 & Opcodes.CHECKCAST) != 128 || (i12 & Opcodes.CHECKCAST) != 128) {
                        throw new IllegalArgumentException();
                    }
                }
            } else {
                if (this.index >= this.length) {
                    throw new IllegalArgumentException();
                }
                byte[] bArr7 = this.bytes;
                int i13 = this.index;
                this.index = i13 + 1;
                int i14 = bArr7[i13] & 255;
                i2 = ((i2 & 31) << 6) + (i14 & 63);
                if (i2 < 128 || (i14 & Opcodes.CHECKCAST) != 128) {
                    throw new IllegalArgumentException();
                }
            }
        }
        return i2;
    }

    @Override // org.h2.util.json.JSONTextSource
    char readHex() {
        if (this.index + 3 >= this.length) {
            throw new IllegalArgumentException();
        }
        try {
            int parseInt = Integer.parseInt(new String(this.bytes, this.index, 4, StandardCharsets.ISO_8859_1), 16);
            this.index += 4;
            return (char) parseInt;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException();
        }
    }

    public String toString() {
        return new String(this.bytes, 0, this.index, StandardCharsets.UTF_8) + "[*]" + new String(this.bytes, this.index, this.length, StandardCharsets.UTF_8);
    }
}
