package com.fasterxml.jackson.core.io.schubfach;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/io/schubfach/DoubleToDecimal.class */
public final class DoubleToDecimal {
    static final int P = 53;
    private static final int W = 11;
    static final int Q_MIN = -1074;
    static final int Q_MAX = 971;
    static final int E_MIN = -323;
    static final int E_MAX = 309;
    static final long C_TINY = 3;
    static final int K_MIN = -324;
    static final int K_MAX = 292;
    static final int H = 17;
    private static final long C_MIN = 4503599627370496L;
    private static final int BQ_MASK = 2047;
    private static final long T_MASK = 4503599627370495L;
    private static final long MASK_63 = Long.MAX_VALUE;
    private static final int MASK_28 = 268435455;
    private static final int NON_SPECIAL = 0;
    private static final int PLUS_ZERO = 1;
    private static final int MINUS_ZERO = 2;
    private static final int PLUS_INF = 3;
    private static final int MINUS_INF = 4;
    private static final int NAN = 5;
    public final int MAX_CHARS = 24;
    private final byte[] bytes = new byte[24];
    private int index;

    private DoubleToDecimal() {
    }

    public static String toString(double v) {
        return new DoubleToDecimal().toDecimalString(v);
    }

    private String toDecimalString(double v) {
        switch (toDecimal(v)) {
            case 0:
                return charsToString();
            case 1:
                return "0.0";
            case 2:
                return "-0.0";
            case 3:
                return "Infinity";
            case 4:
                return "-Infinity";
            default:
                return "NaN";
        }
    }

    private int toDecimal(double v) {
        long bits = Double.doubleToRawLongBits(v);
        long t = bits & T_MASK;
        int bq = ((int) (bits >>> 52)) & 2047;
        if (bq >= 2047) {
            if (t != 0) {
                return 5;
            }
            return bits > 0 ? 3 : 4;
        }
        this.index = -1;
        if (bits < 0) {
            append(45);
        }
        if (bq == 0) {
            if (t == 0) {
                return bits == 0 ? 1 : 2;
            }
            if (t < C_TINY) {
                return toDecimal(Q_MIN, 10 * t, -1);
            }
            return toDecimal(Q_MIN, t, 0);
        }
        int mq = 1075 - bq;
        long c = C_MIN | t;
        if ((0 < mq) & (mq < 53)) {
            long f = c >> mq;
            if ((f << mq) == c) {
                return toChars(f, 0);
            }
        }
        return toDecimal(-mq, c, 0);
    }

    private int toDecimal(int q, long c, int dk) {
        long cbl;
        int k;
        int out = ((int) c) & 1;
        long cb = c << 2;
        long cbr = cb + 2;
        if ((c != C_MIN) | (q == Q_MIN)) {
            cbl = cb - 2;
            k = MathUtils.flog10pow2(q);
        } else {
            cbl = cb - 1;
            k = MathUtils.flog10threeQuartersPow2(q);
        }
        int h = q + MathUtils.flog2pow10(-k) + 2;
        long g1 = MathUtils.g1(k);
        long g0 = MathUtils.g0(k);
        long vb = rop(g1, g0, cb << h);
        long vbl = rop(g1, g0, cbl << h);
        long vbr = rop(g1, g0, cbr << h);
        long s = vb >> 2;
        if (s >= 100) {
            long sp10 = 10 * MathUtils.multiplyHigh(s, 1844674407370955168L);
            long tp10 = sp10 + 10;
            boolean upin = vbl + ((long) out) <= (sp10 << 2);
            boolean wpin = (tp10 << 2) + ((long) out) <= vbr;
            if (upin != wpin) {
                return toChars(upin ? sp10 : tp10, k);
            }
        }
        long t = s + 1;
        boolean uin = vbl + ((long) out) <= (s << 2);
        boolean win = (t << 2) + ((long) out) <= vbr;
        if (uin != win) {
            return toChars(uin ? s : t, k + dk);
        }
        long cmp = vb - ((s + t) << 1);
        return toChars((cmp < 0 || (cmp == 0 && (s & 1) == 0)) ? s : t, k + dk);
    }

    private static long rop(long g1, long g0, long cp) {
        long x1 = MathUtils.multiplyHigh(g0, cp);
        long y0 = g1 * cp;
        long y1 = MathUtils.multiplyHigh(g1, cp);
        long z = (y0 >>> 1) + x1;
        long vbp = y1 + (z >>> 63);
        return vbp | (((z & Long.MAX_VALUE) + Long.MAX_VALUE) >>> 63);
    }

    private int toChars(long f, int e) {
        int len = MathUtils.flog10pow2(64 - Long.numberOfLeadingZeros(f));
        if (f >= MathUtils.pow10(len)) {
            len++;
        }
        long f2 = f * MathUtils.pow10(17 - len);
        int e2 = e + len;
        long hm = MathUtils.multiplyHigh(f2, 193428131138340668L) >>> 20;
        int l = (int) (f2 - (100000000 * hm));
        int h = (int) ((hm * 1441151881) >>> 57);
        int m = (int) (hm - (100000000 * h));
        if (0 < e2 && e2 <= 7) {
            return toChars1(h, m, l, e2);
        }
        if (-3 < e2 && e2 <= 0) {
            return toChars2(h, m, l, e2);
        }
        return toChars3(h, m, l, e2);
    }

    private int toChars1(int h, int m, int l, int e) {
        appendDigit(h);
        int y = y(m);
        int i = 1;
        while (i < e) {
            int t = 10 * y;
            appendDigit(t >>> 28);
            y = t & MASK_28;
            i++;
        }
        append(46);
        while (i <= 8) {
            int t2 = 10 * y;
            appendDigit(t2 >>> 28);
            y = t2 & MASK_28;
            i++;
        }
        lowDigits(l);
        return 0;
    }

    private int toChars2(int h, int m, int l, int e) {
        appendDigit(0);
        append(46);
        while (e < 0) {
            appendDigit(0);
            e++;
        }
        appendDigit(h);
        append8Digits(m);
        lowDigits(l);
        return 0;
    }

    private int toChars3(int h, int m, int l, int e) {
        appendDigit(h);
        append(46);
        append8Digits(m);
        lowDigits(l);
        exponent(e - 1);
        return 0;
    }

    private void lowDigits(int l) {
        if (l != 0) {
            append8Digits(l);
        }
        removeTrailingZeroes();
    }

    private void append8Digits(int m) {
        int y = y(m);
        for (int i = 0; i < 8; i++) {
            int t = 10 * y;
            appendDigit(t >>> 28);
            y = t & MASK_28;
        }
    }

    private void removeTrailingZeroes() {
        while (this.bytes[this.index] == 48) {
            this.index--;
        }
        if (this.bytes[this.index] == 46) {
            this.index++;
        }
    }

    private int y(int a) {
        return ((int) (MathUtils.multiplyHigh((a + 1) << 28, 193428131138340668L) >>> 20)) - 1;
    }

    private void exponent(int e) {
        append(69);
        if (e < 0) {
            append(45);
            e = -e;
        }
        if (e < 10) {
            appendDigit(e);
            return;
        }
        if (e >= 100) {
            int d = (e * 1311) >>> 17;
            appendDigit(d);
            e -= 100 * d;
        }
        int d2 = (e * 103) >>> 10;
        appendDigit(d2);
        appendDigit(e - (10 * d2));
    }

    private void append(int c) {
        byte[] bArr = this.bytes;
        int i = this.index + 1;
        this.index = i;
        bArr[i] = (byte) c;
    }

    private void appendDigit(int d) {
        byte[] bArr = this.bytes;
        int i = this.index + 1;
        this.index = i;
        bArr[i] = (byte) (48 + d);
    }

    private String charsToString() {
        return new String(this.bytes, 0, 0, this.index + 1);
    }
}
