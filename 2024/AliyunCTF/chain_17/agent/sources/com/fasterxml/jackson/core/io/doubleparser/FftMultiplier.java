package com.fasterxml.jackson.core.io.doubleparser;

import java.math.BigInteger;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/io/doubleparser/FftMultiplier.class */
class FftMultiplier {
    public static final double COS_0_25;
    public static final double SIN_0_25;
    private static final int FFT_THRESHOLD = 33220;
    private static final int MAX_MAG_LENGTH = 67108864;
    private static final int ROOTS3_CACHE_SIZE = 20;
    private static final int ROOTS_CACHE2_SIZE = 20;
    private static final int TOOM_COOK_THRESHOLD = 1920;
    private static volatile ComplexVector[] ROOTS2_CACHE;
    private static volatile ComplexVector[] ROOTS3_CACHE;
    static final /* synthetic */ boolean $assertionsDisabled;

    FftMultiplier() {
    }

    static {
        $assertionsDisabled = !FftMultiplier.class.desiredAssertionStatus();
        COS_0_25 = Math.cos(0.7853981633974483d);
        SIN_0_25 = Math.sin(0.7853981633974483d);
        ROOTS2_CACHE = new ComplexVector[20];
        ROOTS3_CACHE = new ComplexVector[20];
    }

    static int bitsPerFftPoint(int bitLen) {
        if (bitLen <= 9728) {
            return 19;
        }
        if (bitLen <= 18432) {
            return 18;
        }
        if (bitLen <= 69632) {
            return 17;
        }
        if (bitLen <= 262144) {
            return 16;
        }
        if (bitLen <= 983040) {
            return 15;
        }
        if (bitLen <= 3670016) {
            return 14;
        }
        if (bitLen <= 13631488) {
            return 13;
        }
        if (bitLen <= 25165824) {
            return 12;
        }
        if (bitLen <= 92274688) {
            return 11;
        }
        if (bitLen <= 335544320) {
            return 10;
        }
        if (bitLen <= 1207959552) {
            return 9;
        }
        return 8;
    }

    private static ComplexVector calculateRootsOfUnity(int n) {
        if (n == 1) {
            ComplexVector v = new ComplexVector(1);
            v.real(0, 1.0d);
            v.imag(0, 0.0d);
            return v;
        }
        ComplexVector roots = new ComplexVector(n);
        roots.set(0, 1.0d, 0.0d);
        roots.set(n / 2, COS_0_25, SIN_0_25);
        double angleTerm = 1.5707963267948966d / n;
        for (int i = 1; i < n / 2; i++) {
            double angle = angleTerm * i;
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            roots.set(i, cos, sin);
            roots.set(n - i, sin, cos);
        }
        return roots;
    }

    private static void fft(ComplexVector a, ComplexVector[] roots) {
        int n = a.length;
        int logN = 31 - Integer.numberOfLeadingZeros(n);
        MutableComplex a0 = new MutableComplex();
        MutableComplex a1 = new MutableComplex();
        MutableComplex a2 = new MutableComplex();
        MutableComplex a3 = new MutableComplex();
        MutableComplex omega1 = new MutableComplex();
        MutableComplex omega2 = new MutableComplex();
        int s = logN;
        while (s >= 2) {
            ComplexVector rootsS = roots[s - 2];
            int m = 1 << s;
            int i = 0;
            while (true) {
                int i2 = i;
                if (i2 < n) {
                    for (int j = 0; j < m / 4; j++) {
                        omega1.set(rootsS, j);
                        omega1.squareInto(omega2);
                        int idx0 = i2 + j;
                        int idx1 = i2 + j + (m / 4);
                        int idx2 = i2 + j + (m / 2);
                        int idx3 = i2 + j + ((m * 3) / 4);
                        a.addInto(idx0, a, idx1, a0);
                        a0.add(a, idx2);
                        a0.add(a, idx3);
                        a.subtractTimesIInto(idx0, a, idx1, a1);
                        a1.subtract(a, idx2);
                        a1.addTimesI(a, idx3);
                        a1.multiplyConjugate(omega1);
                        a.subtractInto(idx0, a, idx1, a2);
                        a2.add(a, idx2);
                        a2.subtract(a, idx3);
                        a2.multiplyConjugate(omega2);
                        a.addTimesIInto(idx0, a, idx1, a3);
                        a3.subtract(a, idx2);
                        a3.subtractTimesI(a, idx3);
                        a3.multiply(omega1);
                        a0.copyInto(a, idx0);
                        a1.copyInto(a, idx1);
                        a2.copyInto(a, idx2);
                        a3.copyInto(a, idx3);
                    }
                    i = i2 + m;
                }
            }
            s -= 2;
        }
        if (s > 0) {
            for (int i3 = 0; i3 < n; i3 += 2) {
                a.copyInto(i3, a0);
                a.copyInto(i3 + 1, a1);
                a.add(i3, a1);
                a0.subtractInto(a1, a, i3 + 1);
            }
        }
    }

    private static void fft3(ComplexVector a0, ComplexVector a1, ComplexVector a2, int sign, double scale) {
        double omegaImag = sign * (-0.5d) * Math.sqrt(3.0d);
        for (int i = 0; i < a0.length; i++) {
            double a0Real = a0.real(i) + a1.real(i) + a2.real(i);
            double a0Imag = a0.imag(i) + a1.imag(i) + a2.imag(i);
            double c = omegaImag * (a2.imag(i) - a1.imag(i));
            double d = omegaImag * (a1.real(i) - a2.real(i));
            double e = 0.5d * (a1.real(i) + a2.real(i));
            double f = 0.5d * (a1.imag(i) + a2.imag(i));
            double a1Real = (a0.real(i) - e) + c;
            double a1Imag = (a0.imag(i) + d) - f;
            double a2Real = (a0.real(i) - e) - c;
            double a2Imag = (a0.imag(i) - d) - f;
            a0.real(i, a0Real * scale);
            a0.imag(i, a0Imag * scale);
            a1.real(i, a1Real * scale);
            a1.imag(i, a1Imag * scale);
            a2.real(i, a2Real * scale);
            a2.imag(i, a2Imag * scale);
        }
    }

    private static void fftMixedRadix(ComplexVector a, ComplexVector[] roots2, ComplexVector roots3) {
        int oneThird = a.length / 3;
        ComplexVector a0 = new ComplexVector(a, 0, oneThird);
        ComplexVector a1 = new ComplexVector(a, oneThird, oneThird * 2);
        ComplexVector a2 = new ComplexVector(a, oneThird * 2, a.length);
        fft3(a0, a1, a2, 1, 1.0d);
        MutableComplex omega = new MutableComplex();
        for (int i = 0; i < a.length / 4; i++) {
            omega.set(roots3, i);
            a1.multiplyConjugate(i, omega);
            a2.multiplyConjugate(i, omega);
            a2.multiplyConjugate(i, omega);
        }
        for (int i2 = a.length / 4; i2 < oneThird; i2++) {
            omega.set(roots3, i2 - (a.length / 4));
            a1.multiplyConjugateTimesI(i2, omega);
            a2.multiplyConjugateTimesI(i2, omega);
            a2.multiplyConjugateTimesI(i2, omega);
        }
        fft(a0, roots2);
        fft(a1, roots2);
        fft(a2, roots2);
    }

    static BigInteger fromFftVector(ComplexVector fftVec, int signum, int bitsPerFftPoint) {
        if (!$assertionsDisabled && bitsPerFftPoint > 25) {
            throw new AssertionError(bitsPerFftPoint + " does not fit into an int with slack");
        }
        int fftLen = (int) Math.min(fftVec.length, (2147483648L / bitsPerFftPoint) + 1);
        int magLen = (int) ((8 * ((fftLen * bitsPerFftPoint) + 31)) / 32);
        byte[] mag = new byte[magLen];
        int base = 1 << bitsPerFftPoint;
        int bitMask = base - 1;
        int bitPadding = 32 - bitsPerFftPoint;
        long carry = 0;
        int bitLength = mag.length * 8;
        int bitIdx = bitLength - bitsPerFftPoint;
        int magComponent = 0;
        int prevIdx = Math.min(Math.max(0, bitIdx >> 3), mag.length - 4);
        for (int part = 0; part <= 1; part++) {
            for (int fftIdx = 0; fftIdx < fftLen; fftIdx++) {
                long fftElem = Math.round(fftVec.part(fftIdx, part)) + carry;
                carry = fftElem >> bitsPerFftPoint;
                int idx = Math.min(Math.max(0, bitIdx >> 3), mag.length - 4);
                int magComponent2 = magComponent >>> ((prevIdx - idx) << 3);
                int shift = (bitPadding - bitIdx) + (idx << 3);
                magComponent = (int) (magComponent2 | ((fftElem & bitMask) << shift));
                FastDoubleSwar.writeIntBE(mag, idx, magComponent);
                prevIdx = idx;
                bitIdx -= bitsPerFftPoint;
            }
        }
        return new BigInteger(signum, mag);
    }

    private static ComplexVector[] getRootsOfUnity2(int logN) {
        ComplexVector[] roots = new ComplexVector[logN + 1];
        for (int i = logN; i >= 0; i -= 2) {
            if (i < 20) {
                if (ROOTS2_CACHE[i] == null) {
                    ROOTS2_CACHE[i] = calculateRootsOfUnity(1 << i);
                }
                roots[i] = ROOTS2_CACHE[i];
            } else {
                roots[i] = calculateRootsOfUnity(1 << i);
            }
        }
        return roots;
    }

    private static ComplexVector getRootsOfUnity3(int logN) {
        if (logN < 20) {
            if (ROOTS3_CACHE[logN] == null) {
                ROOTS3_CACHE[logN] = calculateRootsOfUnity(3 << logN);
            }
            return ROOTS3_CACHE[logN];
        }
        return calculateRootsOfUnity(3 << logN);
    }

    private static void ifft(ComplexVector a, ComplexVector[] roots) {
        int n = a.length;
        int logN = 31 - Integer.numberOfLeadingZeros(n);
        MutableComplex a0 = new MutableComplex();
        MutableComplex a1 = new MutableComplex();
        MutableComplex a2 = new MutableComplex();
        MutableComplex a3 = new MutableComplex();
        MutableComplex b0 = new MutableComplex();
        MutableComplex b1 = new MutableComplex();
        MutableComplex b2 = new MutableComplex();
        MutableComplex b3 = new MutableComplex();
        int s = 1;
        if (logN % 2 != 0) {
            for (int i = 0; i < n; i += 2) {
                a.copyInto(i + 1, a2);
                a.copyInto(i, a0);
                a.add(i, a2);
                a0.subtractInto(a2, a, i + 1);
            }
            s = 1 + 1;
        }
        MutableComplex omega1 = new MutableComplex();
        MutableComplex omega2 = new MutableComplex();
        while (s <= logN) {
            ComplexVector rootsS = roots[s - 1];
            int m = 1 << (s + 1);
            int i2 = 0;
            while (true) {
                int i3 = i2;
                if (i3 < n) {
                    for (int j = 0; j < m / 4; j++) {
                        omega1.set(rootsS, j);
                        omega1.squareInto(omega2);
                        int idx0 = i3 + j;
                        int idx1 = i3 + j + (m / 4);
                        int idx2 = i3 + j + (m / 2);
                        int idx3 = i3 + j + ((m * 3) / 4);
                        a.copyInto(idx0, a0);
                        a.multiplyInto(idx1, omega1, a1);
                        a.multiplyInto(idx2, omega2, a2);
                        a.multiplyConjugateInto(idx3, omega1, a3);
                        a0.addInto(a1, b0);
                        b0.add(a2);
                        b0.add(a3);
                        a0.addTimesIInto(a1, b1);
                        b1.subtract(a2);
                        b1.subtractTimesI(a3);
                        a0.subtractInto(a1, b2);
                        b2.add(a2);
                        b2.subtract(a3);
                        a0.subtractTimesIInto(a1, b3);
                        b3.subtract(a2);
                        b3.addTimesI(a3);
                        b0.copyInto(a, idx0);
                        b1.copyInto(a, idx1);
                        b2.copyInto(a, idx2);
                        b3.copyInto(a, idx3);
                    }
                    i2 = i3 + m;
                }
            }
            s += 2;
        }
        for (int i4 = 0; i4 < n; i4++) {
            a.timesTwoToThe(i4, -logN);
        }
    }

    private static void ifftMixedRadix(ComplexVector a, ComplexVector[] roots2, ComplexVector roots3) {
        int oneThird = a.length / 3;
        ComplexVector a0 = new ComplexVector(a, 0, oneThird);
        ComplexVector a1 = new ComplexVector(a, oneThird, oneThird * 2);
        ComplexVector a2 = new ComplexVector(a, oneThird * 2, a.length);
        ifft(a0, roots2);
        ifft(a1, roots2);
        ifft(a2, roots2);
        MutableComplex omega = new MutableComplex();
        for (int i = 0; i < a.length / 4; i++) {
            omega.set(roots3, i);
            a1.multiply(i, omega);
            a2.multiply(i, omega);
            a2.multiply(i, omega);
        }
        for (int i2 = a.length / 4; i2 < oneThird; i2++) {
            omega.set(roots3, i2 - (a.length / 4));
            a1.multiplyByIAnd(i2, omega);
            a2.multiplyByIAnd(i2, omega);
            a2.multiplyByIAnd(i2, omega);
        }
        fft3(a0, a1, a2, -1, 0.3333333333333333d);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static BigInteger multiply(BigInteger a, BigInteger b) {
        if (b.signum() == 0 || a.signum() == 0) {
            return BigInteger.ZERO;
        }
        if (b == a) {
            return square(b);
        }
        int xlen = a.bitLength();
        int ylen = b.bitLength();
        if (xlen + ylen > 2147483648L) {
            throw new ArithmeticException("BigInteger would overflow supported range");
        }
        if (xlen > TOOM_COOK_THRESHOLD && ylen > TOOM_COOK_THRESHOLD && (xlen > FFT_THRESHOLD || ylen > FFT_THRESHOLD)) {
            return multiplyFft(a, b);
        }
        return a.multiply(b);
    }

    static BigInteger multiplyFft(BigInteger a, BigInteger b) {
        int signum = a.signum() * b.signum();
        byte[] aMag = (a.signum() < 0 ? a.negate() : a).toByteArray();
        byte[] bMag = (b.signum() < 0 ? b.negate() : b).toByteArray();
        int bitLen = Math.max(aMag.length, bMag.length) * 8;
        int bitsPerPoint = bitsPerFftPoint(bitLen);
        int fftLen = (((bitLen + bitsPerPoint) - 1) / bitsPerPoint) + 1;
        int logFFTLen = 32 - Integer.numberOfLeadingZeros(fftLen - 1);
        int fftLen2 = 1 << logFFTLen;
        int fftLen3 = (fftLen2 * 3) / 4;
        if (fftLen < fftLen3 && logFFTLen > 3) {
            ComplexVector[] roots2 = getRootsOfUnity2(logFFTLen - 2);
            ComplexVector weights = getRootsOfUnity3(logFFTLen - 2);
            ComplexVector twiddles = getRootsOfUnity3(logFFTLen - 4);
            ComplexVector aVec = toFftVector(aMag, fftLen3, bitsPerPoint);
            aVec.applyWeights(weights);
            fftMixedRadix(aVec, roots2, twiddles);
            ComplexVector bVec = toFftVector(bMag, fftLen3, bitsPerPoint);
            bVec.applyWeights(weights);
            fftMixedRadix(bVec, roots2, twiddles);
            aVec.multiplyPointwise(bVec);
            ifftMixedRadix(aVec, roots2, twiddles);
            aVec.applyInverseWeights(weights);
            return fromFftVector(aVec, signum, bitsPerPoint);
        }
        ComplexVector[] roots = getRootsOfUnity2(logFFTLen);
        ComplexVector aVec2 = toFftVector(aMag, fftLen2, bitsPerPoint);
        aVec2.applyWeights(roots[logFFTLen]);
        fft(aVec2, roots);
        ComplexVector bVec2 = toFftVector(bMag, fftLen2, bitsPerPoint);
        bVec2.applyWeights(roots[logFFTLen]);
        fft(bVec2, roots);
        aVec2.multiplyPointwise(bVec2);
        ifft(aVec2, roots);
        aVec2.applyInverseWeights(roots[logFFTLen]);
        return fromFftVector(aVec2, signum, bitsPerPoint);
    }

    static BigInteger square(BigInteger a) {
        if (a.signum() == 0) {
            return BigInteger.ZERO;
        }
        return a.bitLength() < FFT_THRESHOLD ? a.multiply(a) : squareFft(a);
    }

    static BigInteger squareFft(BigInteger a) {
        byte[] mag = a.toByteArray();
        int bitLen = mag.length * 8;
        int bitsPerPoint = bitsPerFftPoint(bitLen);
        int fftLen = (((bitLen + bitsPerPoint) - 1) / bitsPerPoint) + 1;
        int logFFTLen = 32 - Integer.numberOfLeadingZeros(fftLen - 1);
        int fftLen2 = 1 << logFFTLen;
        int fftLen3 = (fftLen2 * 3) / 4;
        if (fftLen < fftLen3) {
            ComplexVector vec = toFftVector(mag, fftLen3, bitsPerPoint);
            ComplexVector[] roots2 = getRootsOfUnity2(logFFTLen - 2);
            ComplexVector weights = getRootsOfUnity3(logFFTLen - 2);
            ComplexVector twiddles = getRootsOfUnity3(logFFTLen - 4);
            vec.applyWeights(weights);
            fftMixedRadix(vec, roots2, twiddles);
            vec.squarePointwise();
            ifftMixedRadix(vec, roots2, twiddles);
            vec.applyInverseWeights(weights);
            return fromFftVector(vec, 1, bitsPerPoint);
        }
        ComplexVector vec2 = toFftVector(mag, fftLen2, bitsPerPoint);
        ComplexVector[] roots = getRootsOfUnity2(logFFTLen);
        vec2.applyWeights(roots[logFFTLen]);
        fft(vec2, roots);
        vec2.squarePointwise();
        ifft(vec2, roots);
        vec2.applyInverseWeights(roots[logFFTLen]);
        return fromFftVector(vec2, 1, bitsPerPoint);
    }

    static ComplexVector toFftVector(byte[] mag, int fftLen, int bitsPerFftPoint) {
        if (!$assertionsDisabled && bitsPerFftPoint > 25) {
            throw new AssertionError(bitsPerFftPoint + " does not fit into an int with slack");
        }
        ComplexVector fftVec = new ComplexVector(fftLen);
        if (mag.length < 4) {
            byte[] paddedMag = new byte[4];
            System.arraycopy(mag, 0, paddedMag, 4 - mag.length, mag.length);
            mag = paddedMag;
        }
        int base = 1 << bitsPerFftPoint;
        int halfBase = base / 2;
        int bitMask = base - 1;
        int bitPadding = 32 - bitsPerFftPoint;
        int bitLength = mag.length * 8;
        int carry = 0;
        int fftIdx = 0;
        int i = bitLength;
        while (true) {
            int bitIdx = i - bitsPerFftPoint;
            if (bitIdx <= (-bitsPerFftPoint)) {
                break;
            }
            int idx = Math.min(Math.max(0, bitIdx >> 3), mag.length - 4);
            int shift = (bitPadding - bitIdx) + (idx << 3);
            int fftPoint = ((FastDoubleSwar.readIntBE(mag, idx) >>> shift) & bitMask) + carry;
            carry = (halfBase - fftPoint) >>> 31;
            fftVec.real(fftIdx, fftPoint - (base & (-carry)));
            fftIdx++;
            i = bitIdx;
        }
        if (carry > 0) {
            fftVec.real(fftIdx, carry);
        }
        return fftVec;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/io/doubleparser/FftMultiplier$ComplexVector.class */
    public static final class ComplexVector {
        private static final int COMPLEX_SIZE_SHIFT = 1;
        private static final int IMAG = 1;
        private static final int REAL = 0;
        private final double[] a;
        private final int length;
        private final int offset;

        ComplexVector(int length) {
            this.a = new double[length << 1];
            this.length = length;
            this.offset = 0;
        }

        ComplexVector(ComplexVector c, int from, int to) {
            this.length = to - from;
            this.a = c.a;
            this.offset = from << 1;
        }

        void add(int idxa, MutableComplex c) {
            double[] dArr = this.a;
            int realIdx = realIdx(idxa);
            dArr[realIdx] = dArr[realIdx] + c.real;
            double[] dArr2 = this.a;
            int imagIdx = imagIdx(idxa);
            dArr2[imagIdx] = dArr2[imagIdx] + c.imag;
        }

        void addInto(int idxa, ComplexVector c, int idxc, MutableComplex destination) {
            destination.real = this.a[realIdx(idxa)] + c.real(idxc);
            destination.imag = this.a[imagIdx(idxa)] + c.imag(idxc);
        }

        void addTimesIInto(int idxa, ComplexVector c, int idxc, MutableComplex destination) {
            destination.real = this.a[realIdx(idxa)] - c.imag(idxc);
            destination.imag = this.a[imagIdx(idxa)] + c.real(idxc);
        }

        void applyInverseWeights(ComplexVector weights) {
            int offa = this.offset;
            int offw = weights.offset;
            double[] w = weights.a;
            for (int i = 0; i < this.length; i++) {
                double real = this.a[offa + 0];
                double imag = this.a[offa + 1];
                this.a[offa] = FastDoubleSwar.fma(real, w[offw + 0], imag * w[offw + 1]);
                this.a[offa + 1] = FastDoubleSwar.fma(-real, w[offw + 1], imag * w[offw + 0]);
                offa += 2;
                offw += 2;
            }
        }

        void applyWeights(ComplexVector weights) {
            int offw = weights.offset;
            double[] w = weights.a;
            int end = (this.offset + this.length) << 1;
            for (int offa = this.offset; offa < end; offa += 2) {
                double real = this.a[offa + 0];
                this.a[offa + 0] = real * w[offw + 0];
                this.a[offa + 1] = real * w[offw + 1];
                offw += 2;
            }
        }

        void copyInto(int idxa, MutableComplex destination) {
            destination.real = this.a[realIdx(idxa)];
            destination.imag = this.a[imagIdx(idxa)];
        }

        double imag(int idxa) {
            return this.a[(idxa << 1) + this.offset + 1];
        }

        void imag(int idxa, double value) {
            this.a[(idxa << 1) + this.offset + 1] = value;
        }

        private int imagIdx(int idxa) {
            return (idxa << 1) + this.offset + 1;
        }

        void multiply(int idxa, MutableComplex c) {
            int ri = realIdx(idxa);
            int ii = imagIdx(idxa);
            double real = this.a[ri];
            double imag = this.a[ii];
            this.a[ri] = FastDoubleSwar.fma(real, c.real, (-imag) * c.imag);
            this.a[ii] = FastDoubleSwar.fma(real, c.imag, imag * c.real);
        }

        void multiplyByIAnd(int idxa, MutableComplex c) {
            int ri = realIdx(idxa);
            int ii = imagIdx(idxa);
            double real = this.a[ri];
            double imag = this.a[ii];
            this.a[ri] = FastDoubleSwar.fma(-real, c.imag, (-imag) * c.real);
            this.a[ii] = FastDoubleSwar.fma(real, c.real, (-imag) * c.imag);
        }

        void multiplyConjugate(int idxa, MutableComplex c) {
            int ri = realIdx(idxa);
            int ii = imagIdx(idxa);
            double real = this.a[ri];
            double imag = this.a[ii];
            this.a[ri] = FastDoubleSwar.fma(real, c.real, imag * c.imag);
            this.a[ii] = FastDoubleSwar.fma(-real, c.imag, imag * c.real);
        }

        void multiplyConjugateInto(int idxa, MutableComplex c, MutableComplex destination) {
            double real = this.a[realIdx(idxa)];
            double imag = this.a[imagIdx(idxa)];
            destination.real = FastDoubleSwar.fma(real, c.real, imag * c.imag);
            destination.imag = FastDoubleSwar.fma(-real, c.imag, imag * c.real);
        }

        void multiplyConjugateTimesI(int idxa, MutableComplex c) {
            int ri = realIdx(idxa);
            int ii = imagIdx(idxa);
            double real = this.a[ri];
            double imag = this.a[ii];
            this.a[ri] = FastDoubleSwar.fma(-real, c.imag, imag * c.real);
            this.a[ii] = FastDoubleSwar.fma(-real, c.real, (-imag) * c.imag);
        }

        void multiplyInto(int idxa, MutableComplex c, MutableComplex destination) {
            double real = this.a[realIdx(idxa)];
            double imag = this.a[imagIdx(idxa)];
            destination.real = FastDoubleSwar.fma(real, c.real, (-imag) * c.imag);
            destination.imag = FastDoubleSwar.fma(real, c.imag, imag * c.real);
        }

        void multiplyPointwise(ComplexVector cvec) {
            int offc = cvec.offset;
            double[] c = cvec.a;
            int end = (this.offset + this.length) << 1;
            for (int offa = this.offset; offa < end; offa += 2) {
                double real = this.a[offa + 0];
                double imag = this.a[offa + 1];
                double creal = c[offc + 0];
                double cimag = c[offc + 1];
                this.a[offa + 0] = FastDoubleSwar.fma(real, creal, (-imag) * cimag);
                this.a[offa + 1] = FastDoubleSwar.fma(real, cimag, imag * creal);
                offc += 2;
            }
        }

        double part(int idxa, int part) {
            return this.a[(idxa << 1) + part];
        }

        double real(int idxa) {
            return this.a[(idxa << 1) + this.offset];
        }

        void real(int idxa, double value) {
            this.a[(idxa << 1) + this.offset] = value;
        }

        private int realIdx(int idxa) {
            return (idxa << 1) + this.offset;
        }

        void set(int idxa, double real, double imag) {
            int idx = realIdx(idxa);
            this.a[idx] = real;
            this.a[idx + 1] = imag;
        }

        void squarePointwise() {
            int end = (this.offset + this.length) << 1;
            for (int offa = this.offset; offa < end; offa += 2) {
                double real = this.a[offa + 0];
                double imag = this.a[offa + 1];
                this.a[offa + 0] = FastDoubleSwar.fma(real, real, (-imag) * imag);
                this.a[offa + 1] = 2.0d * real * imag;
            }
        }

        void subtractInto(int idxa, ComplexVector c, int idxc, MutableComplex destination) {
            destination.real = this.a[realIdx(idxa)] - c.real(idxc);
            destination.imag = this.a[imagIdx(idxa)] - c.imag(idxc);
        }

        void subtractTimesIInto(int idxa, ComplexVector c, int idxc, MutableComplex destination) {
            destination.real = this.a[realIdx(idxa)] + c.imag(idxc);
            destination.imag = this.a[imagIdx(idxa)] - c.real(idxc);
        }

        void timesTwoToThe(int idxa, int n) {
            int ri = realIdx(idxa);
            int ii = imagIdx(idxa);
            double real = this.a[ri];
            double imag = this.a[ii];
            this.a[ri] = Math.scalb(real, n);
            this.a[ii] = Math.scalb(imag, n);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/io/doubleparser/FftMultiplier$MutableComplex.class */
    public static final class MutableComplex {
        double real;
        double imag;

        MutableComplex() {
        }

        void add(MutableComplex c) {
            this.real += c.real;
            this.imag += c.imag;
        }

        void add(ComplexVector c, int idxc) {
            this.real += c.real(idxc);
            this.imag += c.imag(idxc);
        }

        void addInto(MutableComplex c, MutableComplex destination) {
            destination.real = this.real + c.real;
            destination.imag = this.imag + c.imag;
        }

        void addTimesI(MutableComplex c) {
            this.real -= c.imag;
            this.imag += c.real;
        }

        void addTimesI(ComplexVector c, int idxc) {
            this.real -= c.imag(idxc);
            this.imag += c.real(idxc);
        }

        void addTimesIInto(MutableComplex c, MutableComplex destination) {
            destination.real = this.real - c.imag;
            destination.imag = this.imag + c.real;
        }

        void copyInto(ComplexVector c, int idxc) {
            c.real(idxc, this.real);
            c.imag(idxc, this.imag);
        }

        void multiply(MutableComplex c) {
            double temp = this.real;
            this.real = FastDoubleSwar.fma(temp, c.real, (-this.imag) * c.imag);
            this.imag = FastDoubleSwar.fma(temp, c.imag, this.imag * c.real);
        }

        void multiplyConjugate(MutableComplex c) {
            double temp = this.real;
            this.real = FastDoubleSwar.fma(temp, c.real, this.imag * c.imag);
            this.imag = FastDoubleSwar.fma(-temp, c.imag, this.imag * c.real);
        }

        void set(ComplexVector c, int idxc) {
            this.real = c.real(idxc);
            this.imag = c.imag(idxc);
        }

        void squareInto(MutableComplex destination) {
            destination.real = FastDoubleSwar.fma(this.real, this.real, (-this.imag) * this.imag);
            destination.imag = 2.0d * this.real * this.imag;
        }

        void subtract(MutableComplex c) {
            this.real -= c.real;
            this.imag -= c.imag;
        }

        void subtract(ComplexVector c, int idxc) {
            this.real -= c.real(idxc);
            this.imag -= c.imag(idxc);
        }

        void subtractInto(MutableComplex c, MutableComplex destination) {
            destination.real = this.real - c.real;
            destination.imag = this.imag - c.imag;
        }

        void subtractInto(MutableComplex c, ComplexVector destination, int idxd) {
            destination.real(idxd, this.real - c.real);
            destination.imag(idxd, this.imag - c.imag);
        }

        void subtractTimesI(MutableComplex c) {
            this.real += c.imag;
            this.imag -= c.real;
        }

        void subtractTimesI(ComplexVector c, int idxc) {
            this.real += c.imag(idxc);
            this.imag -= c.real(idxc);
        }

        void subtractTimesIInto(MutableComplex c, MutableComplex destination) {
            destination.real = this.real + c.imag;
            destination.imag = this.imag - c.real;
        }
    }
}
