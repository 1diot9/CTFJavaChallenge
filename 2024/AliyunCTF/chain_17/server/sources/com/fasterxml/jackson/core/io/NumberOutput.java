package com.fasterxml.jackson.core.io;

import com.fasterxml.jackson.core.io.schubfach.DoubleToDecimal;
import com.fasterxml.jackson.core.io.schubfach.FloatToDecimal;
import org.apache.logging.log4j.message.StructuredDataId;
import org.springframework.asm.Opcodes;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;

/* loaded from: server.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/io/NumberOutput.class */
public final class NumberOutput {
    private static int MILLION = 1000000;
    private static int BILLION = 1000000000;
    private static long BILLION_L = 1000000000;
    private static long MIN_INT_AS_LONG = -2147483648L;
    private static long MAX_INT_AS_LONG = 2147483647L;
    static final String SMALLEST_INT = String.valueOf(Integer.MIN_VALUE);
    static final String SMALLEST_LONG = String.valueOf(Long.MIN_VALUE);
    private static final int[] TRIPLET_TO_CHARS = new int[1000];
    private static final String[] sSmallIntStrs;
    private static final String[] sSmallIntStrs2;

    static {
        int fullIx = 0;
        for (int i1 = 0; i1 < 10; i1++) {
            for (int i2 = 0; i2 < 10; i2++) {
                for (int i3 = 0; i3 < 10; i3++) {
                    int enc = ((i1 + 48) << 16) | ((i2 + 48) << 8) | (i3 + 48);
                    int i = fullIx;
                    fullIx++;
                    TRIPLET_TO_CHARS[i] = enc;
                }
            }
        }
        sSmallIntStrs = new String[]{CustomBooleanEditor.VALUE_0, CustomBooleanEditor.VALUE_1, "2", "3", "4", "5", "6", "7", "8", "9", "10"};
        sSmallIntStrs2 = new String[]{StructuredDataId.RESERVED, "-2", "-3", "-4", "-5", "-6", "-7", "-8", "-9", "-10"};
    }

    public static int outputInt(int v, char[] b, int off) {
        int off2;
        if (v < 0) {
            if (v == Integer.MIN_VALUE) {
                return _outputSmallestI(b, off);
            }
            off++;
            b[off] = '-';
            v = -v;
        }
        if (v < MILLION) {
            if (v < 1000) {
                if (v < 10) {
                    b[off] = (char) (48 + v);
                    return off + 1;
                }
                return _leading3(v, b, off);
            }
            int thousands = v / 1000;
            return _full3(v - (thousands * 1000), b, _leading3(thousands, b, off));
        }
        if (v >= BILLION) {
            int v2 = v - BILLION;
            if (v2 >= BILLION) {
                v2 -= BILLION;
                int i = off;
                off2 = off + 1;
                b[i] = '2';
            } else {
                int i2 = off;
                off2 = off + 1;
                b[i2] = '1';
            }
            return _outputFullBillion(v2, b, off2);
        }
        int newValue = v / 1000;
        int ones = v - (newValue * 1000);
        int newValue2 = newValue / 1000;
        return _full3(ones, b, _full3(newValue - (newValue2 * 1000), b, _leading3(newValue2, b, off)));
    }

    public static int outputInt(int v, byte[] b, int off) {
        int off2;
        int off3;
        if (v < 0) {
            if (v == Integer.MIN_VALUE) {
                return _outputSmallestI(b, off);
            }
            off++;
            b[off] = 45;
            v = -v;
        }
        if (v < MILLION) {
            if (v < 1000) {
                if (v < 10) {
                    int i = off;
                    off3 = off + 1;
                    b[i] = (byte) (48 + v);
                } else {
                    off3 = _leading3(v, b, off);
                }
            } else {
                int thousands = v / 1000;
                off3 = _full3(v - (thousands * 1000), b, _leading3(thousands, b, off));
            }
            return off3;
        }
        if (v >= BILLION) {
            int v2 = v - BILLION;
            if (v2 >= BILLION) {
                v2 -= BILLION;
                int i2 = off;
                off2 = off + 1;
                b[i2] = 50;
            } else {
                int i3 = off;
                off2 = off + 1;
                b[i3] = 49;
            }
            return _outputFullBillion(v2, b, off2);
        }
        int newValue = v / 1000;
        int ones = v - (newValue * 1000);
        int newValue2 = newValue / 1000;
        return _full3(ones, b, _full3(newValue - (newValue2 * 1000), b, _leading3(newValue2, b, off)));
    }

    public static int outputLong(long v, char[] b, int off) {
        int off2;
        if (v < 0) {
            if (v > MIN_INT_AS_LONG) {
                return outputInt((int) v, b, off);
            }
            if (v == Long.MIN_VALUE) {
                return _outputSmallestL(b, off);
            }
            off++;
            b[off] = '-';
            v = -v;
        } else if (v <= MAX_INT_AS_LONG) {
            return outputInt((int) v, b, off);
        }
        long upper = v / BILLION_L;
        long v2 = v - (upper * BILLION_L);
        if (upper < BILLION_L) {
            off2 = _outputUptoBillion((int) upper, b, off);
        } else {
            long hi = upper / BILLION_L;
            long upper2 = upper - (hi * BILLION_L);
            off2 = _outputFullBillion((int) upper2, b, _leading3((int) hi, b, off));
        }
        return _outputFullBillion((int) v2, b, off2);
    }

    public static int outputLong(long v, byte[] b, int off) {
        int off2;
        if (v < 0) {
            if (v > MIN_INT_AS_LONG) {
                return outputInt((int) v, b, off);
            }
            if (v == Long.MIN_VALUE) {
                return _outputSmallestL(b, off);
            }
            off++;
            b[off] = 45;
            v = -v;
        } else if (v <= MAX_INT_AS_LONG) {
            return outputInt((int) v, b, off);
        }
        long upper = v / BILLION_L;
        long v2 = v - (upper * BILLION_L);
        if (upper < BILLION_L) {
            off2 = _outputUptoBillion((int) upper, b, off);
        } else {
            long hi = upper / BILLION_L;
            long upper2 = upper - (hi * BILLION_L);
            off2 = _outputFullBillion((int) upper2, b, _leading3((int) hi, b, off));
        }
        return _outputFullBillion((int) v2, b, off2);
    }

    public static String toString(int v) {
        if (v < sSmallIntStrs.length) {
            if (v >= 0) {
                return sSmallIntStrs[v];
            }
            int v2 = (-v) - 1;
            if (v2 < sSmallIntStrs2.length) {
                return sSmallIntStrs2[v2];
            }
        }
        return Integer.toString(v);
    }

    public static String toString(long v) {
        if (v <= 2147483647L && v >= -2147483648L) {
            return toString((int) v);
        }
        return Long.toString(v);
    }

    public static String toString(double v) {
        return toString(v, false);
    }

    public static String toString(double v, boolean useFastWriter) {
        return useFastWriter ? DoubleToDecimal.toString(v) : Double.toString(v);
    }

    public static String toString(float v) {
        return toString(v, false);
    }

    public static String toString(float v, boolean useFastWriter) {
        return useFastWriter ? FloatToDecimal.toString(v) : Float.toString(v);
    }

    public static boolean notFinite(double value) {
        return !Double.isFinite(value);
    }

    public static boolean notFinite(float value) {
        return !Float.isFinite(value);
    }

    private static int _outputUptoBillion(int v, char[] b, int off) {
        if (v < MILLION) {
            if (v < 1000) {
                return _leading3(v, b, off);
            }
            int thousands = v / 1000;
            int ones = v - (thousands * 1000);
            return _outputUptoMillion(b, off, thousands, ones);
        }
        int thousands2 = v / 1000;
        int ones2 = v - (thousands2 * 1000);
        int millions = thousands2 / 1000;
        int thousands3 = thousands2 - (millions * 1000);
        int off2 = _leading3(millions, b, off);
        int enc = TRIPLET_TO_CHARS[thousands3];
        int off3 = off2 + 1;
        b[off2] = (char) (enc >> 16);
        int off4 = off3 + 1;
        b[off3] = (char) ((enc >> 8) & Opcodes.LAND);
        int off5 = off4 + 1;
        b[off4] = (char) (enc & Opcodes.LAND);
        int enc2 = TRIPLET_TO_CHARS[ones2];
        int off6 = off5 + 1;
        b[off5] = (char) (enc2 >> 16);
        int off7 = off6 + 1;
        b[off6] = (char) ((enc2 >> 8) & Opcodes.LAND);
        int off8 = off7 + 1;
        b[off7] = (char) (enc2 & Opcodes.LAND);
        return off8;
    }

    private static int _outputFullBillion(int v, char[] b, int off) {
        int thousands = v / 1000;
        int ones = v - (thousands * 1000);
        int millions = thousands / 1000;
        int enc = TRIPLET_TO_CHARS[millions];
        int off2 = off + 1;
        b[off] = (char) (enc >> 16);
        int off3 = off2 + 1;
        b[off2] = (char) ((enc >> 8) & Opcodes.LAND);
        int off4 = off3 + 1;
        b[off3] = (char) (enc & Opcodes.LAND);
        int enc2 = TRIPLET_TO_CHARS[thousands - (millions * 1000)];
        int off5 = off4 + 1;
        b[off4] = (char) (enc2 >> 16);
        int off6 = off5 + 1;
        b[off5] = (char) ((enc2 >> 8) & Opcodes.LAND);
        int off7 = off6 + 1;
        b[off6] = (char) (enc2 & Opcodes.LAND);
        int enc3 = TRIPLET_TO_CHARS[ones];
        int off8 = off7 + 1;
        b[off7] = (char) (enc3 >> 16);
        int off9 = off8 + 1;
        b[off8] = (char) ((enc3 >> 8) & Opcodes.LAND);
        int off10 = off9 + 1;
        b[off9] = (char) (enc3 & Opcodes.LAND);
        return off10;
    }

    private static int _outputUptoBillion(int v, byte[] b, int off) {
        if (v < MILLION) {
            if (v < 1000) {
                return _leading3(v, b, off);
            }
            int thousands = v / 1000;
            int ones = v - (thousands * 1000);
            return _outputUptoMillion(b, off, thousands, ones);
        }
        int thousands2 = v / 1000;
        int ones2 = v - (thousands2 * 1000);
        int millions = thousands2 / 1000;
        int thousands3 = thousands2 - (millions * 1000);
        int off2 = _leading3(millions, b, off);
        int enc = TRIPLET_TO_CHARS[thousands3];
        int off3 = off2 + 1;
        b[off2] = (byte) (enc >> 16);
        int off4 = off3 + 1;
        b[off3] = (byte) (enc >> 8);
        int off5 = off4 + 1;
        b[off4] = (byte) enc;
        int enc2 = TRIPLET_TO_CHARS[ones2];
        int off6 = off5 + 1;
        b[off5] = (byte) (enc2 >> 16);
        int off7 = off6 + 1;
        b[off6] = (byte) (enc2 >> 8);
        int off8 = off7 + 1;
        b[off7] = (byte) enc2;
        return off8;
    }

    private static int _outputFullBillion(int v, byte[] b, int off) {
        int thousands = v / 1000;
        int ones = v - (thousands * 1000);
        int millions = thousands / 1000;
        int thousands2 = thousands - (millions * 1000);
        int enc = TRIPLET_TO_CHARS[millions];
        int off2 = off + 1;
        b[off] = (byte) (enc >> 16);
        int off3 = off2 + 1;
        b[off2] = (byte) (enc >> 8);
        int off4 = off3 + 1;
        b[off3] = (byte) enc;
        int enc2 = TRIPLET_TO_CHARS[thousands2];
        int off5 = off4 + 1;
        b[off4] = (byte) (enc2 >> 16);
        int off6 = off5 + 1;
        b[off5] = (byte) (enc2 >> 8);
        int off7 = off6 + 1;
        b[off6] = (byte) enc2;
        int enc3 = TRIPLET_TO_CHARS[ones];
        int off8 = off7 + 1;
        b[off7] = (byte) (enc3 >> 16);
        int off9 = off8 + 1;
        b[off8] = (byte) (enc3 >> 8);
        int off10 = off9 + 1;
        b[off9] = (byte) enc3;
        return off10;
    }

    private static int _outputUptoMillion(char[] b, int off, int thousands, int ones) {
        int enc = TRIPLET_TO_CHARS[thousands];
        if (thousands > 9) {
            if (thousands > 99) {
                off++;
                b[off] = (char) (enc >> 16);
            }
            int i = off;
            off++;
            b[i] = (char) ((enc >> 8) & Opcodes.LAND);
        }
        int i2 = off;
        int off2 = off + 1;
        b[i2] = (char) (enc & Opcodes.LAND);
        int enc2 = TRIPLET_TO_CHARS[ones];
        int off3 = off2 + 1;
        b[off2] = (char) (enc2 >> 16);
        int off4 = off3 + 1;
        b[off3] = (char) ((enc2 >> 8) & Opcodes.LAND);
        int off5 = off4 + 1;
        b[off4] = (char) (enc2 & Opcodes.LAND);
        return off5;
    }

    private static int _outputUptoMillion(byte[] b, int off, int thousands, int ones) {
        int enc = TRIPLET_TO_CHARS[thousands];
        if (thousands > 9) {
            if (thousands > 99) {
                off++;
                b[off] = (byte) (enc >> 16);
            }
            int i = off;
            off++;
            b[i] = (byte) (enc >> 8);
        }
        int i2 = off;
        int off2 = off + 1;
        b[i2] = (byte) enc;
        int enc2 = TRIPLET_TO_CHARS[ones];
        int off3 = off2 + 1;
        b[off2] = (byte) (enc2 >> 16);
        int off4 = off3 + 1;
        b[off3] = (byte) (enc2 >> 8);
        int off5 = off4 + 1;
        b[off4] = (byte) enc2;
        return off5;
    }

    private static int _leading3(int t, char[] b, int off) {
        int enc = TRIPLET_TO_CHARS[t];
        if (t > 9) {
            if (t > 99) {
                off++;
                b[off] = (char) (enc >> 16);
            }
            int i = off;
            off++;
            b[i] = (char) ((enc >> 8) & Opcodes.LAND);
        }
        int i2 = off;
        int off2 = off + 1;
        b[i2] = (char) (enc & Opcodes.LAND);
        return off2;
    }

    private static int _leading3(int t, byte[] b, int off) {
        int enc = TRIPLET_TO_CHARS[t];
        if (t > 9) {
            if (t > 99) {
                off++;
                b[off] = (byte) (enc >> 16);
            }
            int i = off;
            off++;
            b[i] = (byte) (enc >> 8);
        }
        int i2 = off;
        int off2 = off + 1;
        b[i2] = (byte) enc;
        return off2;
    }

    private static int _full3(int t, char[] b, int off) {
        int enc = TRIPLET_TO_CHARS[t];
        int off2 = off + 1;
        b[off] = (char) (enc >> 16);
        int off3 = off2 + 1;
        b[off2] = (char) ((enc >> 8) & Opcodes.LAND);
        int off4 = off3 + 1;
        b[off3] = (char) (enc & Opcodes.LAND);
        return off4;
    }

    private static int _full3(int t, byte[] b, int off) {
        int enc = TRIPLET_TO_CHARS[t];
        int off2 = off + 1;
        b[off] = (byte) (enc >> 16);
        int off3 = off2 + 1;
        b[off2] = (byte) (enc >> 8);
        int off4 = off3 + 1;
        b[off3] = (byte) enc;
        return off4;
    }

    private static int _outputSmallestL(char[] b, int off) {
        int len = SMALLEST_LONG.length();
        SMALLEST_LONG.getChars(0, len, b, off);
        return off + len;
    }

    private static int _outputSmallestL(byte[] b, int off) {
        int len = SMALLEST_LONG.length();
        for (int i = 0; i < len; i++) {
            int i2 = off;
            off++;
            b[i2] = (byte) SMALLEST_LONG.charAt(i);
        }
        return off;
    }

    private static int _outputSmallestI(char[] b, int off) {
        int len = SMALLEST_INT.length();
        SMALLEST_INT.getChars(0, len, b, off);
        return off + len;
    }

    private static int _outputSmallestI(byte[] b, int off) {
        int len = SMALLEST_INT.length();
        for (int i = 0; i < len; i++) {
            int i2 = off;
            off++;
            b[i2] = (byte) SMALLEST_INT.charAt(i);
        }
        return off;
    }
}
