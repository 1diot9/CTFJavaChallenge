package org.h2.util.geometry;

import cn.hutool.core.net.NetUtil;
import java.io.ByteArrayOutputStream;
import org.h2.util.Bits;
import org.h2.util.StringUtils;
import org.h2.util.geometry.GeometryUtils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/geometry/EWKBUtils.class */
public final class EWKBUtils {
    public static final int EWKB_Z = Integer.MIN_VALUE;
    public static final int EWKB_M = 1073741824;
    public static final int EWKB_SRID = 536870912;

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/geometry/EWKBUtils$EWKBTarget.class */
    public static final class EWKBTarget extends GeometryUtils.Target {
        private final ByteArrayOutputStream output;
        private final int dimensionSystem;
        private final byte[] buf = new byte[8];
        private int type;
        private int srid;

        public EWKBTarget(ByteArrayOutputStream byteArrayOutputStream, int i) {
            this.output = byteArrayOutputStream;
            this.dimensionSystem = i;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.h2.util.geometry.GeometryUtils.Target
        public void init(int i) {
            this.srid = i;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.h2.util.geometry.GeometryUtils.Target
        public void startPoint() {
            writeHeader(1);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.h2.util.geometry.GeometryUtils.Target
        public void startLineString(int i) {
            writeHeader(2);
            writeInt(i);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.h2.util.geometry.GeometryUtils.Target
        public void startPolygon(int i, int i2) {
            writeHeader(3);
            if (i == 0 && i2 == 0) {
                writeInt(0);
            } else {
                writeInt(i + 1);
                writeInt(i2);
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.h2.util.geometry.GeometryUtils.Target
        public void startPolygonInner(int i) {
            writeInt(i);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.h2.util.geometry.GeometryUtils.Target
        public void startCollection(int i, int i2) {
            writeHeader(i);
            writeInt(i2);
        }

        private void writeHeader(int i) {
            this.type = i;
            switch (this.dimensionSystem) {
                case 1:
                    i |= Integer.MIN_VALUE;
                    break;
                case 3:
                    i |= Integer.MIN_VALUE;
                case 2:
                    i |= 1073741824;
                    break;
            }
            if (this.srid != 0) {
                i |= 536870912;
            }
            this.output.write(0);
            writeInt(i);
            if (this.srid != 0) {
                writeInt(this.srid);
                this.srid = 0;
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.h2.util.geometry.GeometryUtils.Target
        public GeometryUtils.Target startCollectionItem(int i, int i2) {
            return this;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.h2.util.geometry.GeometryUtils.Target
        public void addCoordinate(double d, double d2, double d3, double d4, int i, int i2) {
            boolean z = (this.type == 1 && Double.isNaN(d) && Double.isNaN(d2) && Double.isNaN(d3) && Double.isNaN(d4)) ? false : true;
            if (z) {
                GeometryUtils.checkFinite(d);
                GeometryUtils.checkFinite(d2);
            }
            writeDouble(d);
            writeDouble(d2);
            if ((this.dimensionSystem & 1) != 0) {
                writeDouble(z ? GeometryUtils.checkFinite(d3) : d3);
            } else if (z && !Double.isNaN(d3)) {
                throw new IllegalArgumentException();
            }
            if ((this.dimensionSystem & 2) != 0) {
                writeDouble(z ? GeometryUtils.checkFinite(d4) : d4);
            } else if (z && !Double.isNaN(d4)) {
                throw new IllegalArgumentException();
            }
        }

        private void writeInt(int i) {
            Bits.writeInt(this.buf, 0, i);
            this.output.write(this.buf, 0, 4);
        }

        private void writeDouble(double d) {
            Bits.writeDouble(this.buf, 0, GeometryUtils.toCanonicalDouble(d));
            this.output.write(this.buf, 0, 8);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/geometry/EWKBUtils$EWKBSource.class */
    public static final class EWKBSource {
        private final byte[] ewkb;
        private int offset;
        boolean bigEndian;

        EWKBSource(byte[] bArr) {
            this.ewkb = bArr;
        }

        byte readByte() {
            byte[] bArr = this.ewkb;
            int i = this.offset;
            this.offset = i + 1;
            return bArr[i];
        }

        int readInt() {
            int readInt = this.bigEndian ? Bits.readInt(this.ewkb, this.offset) : Bits.readIntLE(this.ewkb, this.offset);
            this.offset += 4;
            return readInt;
        }

        double readCoordinate() {
            double readDouble = this.bigEndian ? Bits.readDouble(this.ewkb, this.offset) : Bits.readDoubleLE(this.ewkb, this.offset);
            this.offset += 8;
            return GeometryUtils.toCanonicalDouble(readDouble);
        }

        public String toString() {
            String convertBytesToHex = StringUtils.convertBytesToHex(this.ewkb);
            int i = this.offset * 2;
            return new StringBuilder(convertBytesToHex.length() + 3).append((CharSequence) convertBytesToHex, 0, i).append("<*>").append((CharSequence) convertBytesToHex, i, convertBytesToHex.length()).toString();
        }
    }

    public static byte[] ewkb2ewkb(byte[] bArr) {
        return ewkb2ewkb(bArr, getDimensionSystem(bArr));
    }

    public static byte[] ewkb2ewkb(byte[] bArr, int i) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        parseEWKB(bArr, new EWKBTarget(byteArrayOutputStream, i));
        return byteArrayOutputStream.toByteArray();
    }

    public static void parseEWKB(byte[] bArr, GeometryUtils.Target target) {
        try {
            parseEWKB(new EWKBSource(bArr), target, 0);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException();
        }
    }

    public static int type2dimensionSystem(int i) {
        boolean z = (i & Integer.MIN_VALUE) != 0;
        boolean z2 = (i & 1073741824) != 0;
        switch ((i & NetUtil.PORT_RANGE_MAX) / 1000) {
            case 1:
                z = true;
                break;
            case 3:
                z = true;
            case 2:
                z2 = true;
                break;
        }
        return (z ? 1 : 0) | (z2 ? 2 : 0);
    }

    private static void parseEWKB(EWKBSource eWKBSource, GeometryUtils.Target target, int i) {
        switch (eWKBSource.readByte()) {
            case 0:
                eWKBSource.bigEndian = true;
                break;
            case 1:
                eWKBSource.bigEndian = false;
                break;
            default:
                throw new IllegalArgumentException();
        }
        int readInt = eWKBSource.readInt();
        boolean z = (readInt & Integer.MIN_VALUE) != 0;
        boolean z2 = (readInt & 1073741824) != 0;
        int readInt2 = (readInt & 536870912) != 0 ? eWKBSource.readInt() : 0;
        if (i == 0) {
            target.init(readInt2);
        }
        int i2 = readInt & NetUtil.PORT_RANGE_MAX;
        switch (i2 / 1000) {
            case 1:
                z = true;
                break;
            case 3:
                z = true;
            case 2:
                z2 = true;
                break;
        }
        target.dimensionSystem((z ? 1 : 0) | (z2 ? 2 : 0));
        int i3 = i2 % 1000;
        switch (i3) {
            case 1:
                if (i != 0 && i != 4 && i != 7) {
                    throw new IllegalArgumentException();
                }
                target.startPoint();
                addCoordinate(eWKBSource, target, z, z2, 0, 1);
                break;
                break;
            case 2:
                if (i != 0 && i != 5 && i != 7) {
                    throw new IllegalArgumentException();
                }
                int readInt3 = eWKBSource.readInt();
                if (readInt3 < 0 || readInt3 == 1) {
                    throw new IllegalArgumentException();
                }
                target.startLineString(readInt3);
                for (int i4 = 0; i4 < readInt3; i4++) {
                    addCoordinate(eWKBSource, target, z, z2, i4, readInt3);
                }
                break;
                break;
            case 3:
                if (i != 0 && i != 6 && i != 7) {
                    throw new IllegalArgumentException();
                }
                int readInt4 = eWKBSource.readInt();
                if (readInt4 == 0) {
                    target.startPolygon(0, 0);
                    break;
                } else {
                    if (readInt4 < 0) {
                        throw new IllegalArgumentException();
                    }
                    int i5 = readInt4 - 1;
                    int readInt5 = eWKBSource.readInt();
                    if (readInt5 < 0 || (readInt5 >= 1 && readInt5 <= 3)) {
                        throw new IllegalArgumentException();
                    }
                    if (readInt5 == 0 && i5 > 0) {
                        throw new IllegalArgumentException();
                    }
                    target.startPolygon(i5, readInt5);
                    if (readInt5 > 0) {
                        addRing(eWKBSource, target, z, z2, readInt5);
                        for (int i6 = 0; i6 < i5; i6++) {
                            int readInt6 = eWKBSource.readInt();
                            if (readInt6 < 0 || (readInt6 >= 1 && readInt6 <= 3)) {
                                throw new IllegalArgumentException();
                            }
                            target.startPolygonInner(readInt6);
                            addRing(eWKBSource, target, z, z2, readInt6);
                        }
                        target.endNonEmptyPolygon();
                        break;
                    }
                }
                break;
            case 4:
            case 5:
            case 6:
            case 7:
                if (i != 0 && i != 7) {
                    throw new IllegalArgumentException();
                }
                int readInt7 = eWKBSource.readInt();
                if (readInt7 < 0) {
                    throw new IllegalArgumentException();
                }
                target.startCollection(i3, readInt7);
                for (int i7 = 0; i7 < readInt7; i7++) {
                    GeometryUtils.Target startCollectionItem = target.startCollectionItem(i7, readInt7);
                    parseEWKB(eWKBSource, startCollectionItem, i3);
                    target.endCollectionItem(startCollectionItem, i3, i7, readInt7);
                }
                break;
                break;
            default:
                throw new IllegalArgumentException();
        }
        target.endObject(i3);
    }

    private static void addRing(EWKBSource eWKBSource, GeometryUtils.Target target, boolean z, boolean z2, int i) {
        if (i >= 4) {
            double readCoordinate = eWKBSource.readCoordinate();
            double readCoordinate2 = eWKBSource.readCoordinate();
            target.addCoordinate(readCoordinate, readCoordinate2, z ? eWKBSource.readCoordinate() : Double.NaN, z2 ? eWKBSource.readCoordinate() : Double.NaN, 0, i);
            for (int i2 = 1; i2 < i - 1; i2++) {
                addCoordinate(eWKBSource, target, z, z2, i2, i);
            }
            double readCoordinate3 = eWKBSource.readCoordinate();
            double readCoordinate4 = eWKBSource.readCoordinate();
            if (readCoordinate != readCoordinate3 || readCoordinate2 != readCoordinate4) {
                throw new IllegalArgumentException();
            }
            target.addCoordinate(readCoordinate3, readCoordinate4, z ? eWKBSource.readCoordinate() : Double.NaN, z2 ? eWKBSource.readCoordinate() : Double.NaN, i - 1, i);
        }
    }

    private static void addCoordinate(EWKBSource eWKBSource, GeometryUtils.Target target, boolean z, boolean z2, int i, int i2) {
        target.addCoordinate(eWKBSource.readCoordinate(), eWKBSource.readCoordinate(), z ? eWKBSource.readCoordinate() : Double.NaN, z2 ? eWKBSource.readCoordinate() : Double.NaN, i, i2);
    }

    public static int getDimensionSystem(byte[] bArr) {
        EWKBSource eWKBSource = new EWKBSource(bArr);
        switch (eWKBSource.readByte()) {
            case 0:
                eWKBSource.bigEndian = true;
                break;
            case 1:
                eWKBSource.bigEndian = false;
                break;
            default:
                throw new IllegalArgumentException();
        }
        return type2dimensionSystem(eWKBSource.readInt());
    }

    public static byte[] envelope2wkb(double[] dArr) {
        byte[] bArr;
        if (dArr == null) {
            return null;
        }
        double d = dArr[0];
        double d2 = dArr[1];
        double d3 = dArr[2];
        double d4 = dArr[3];
        if (d == d2 && d3 == d4) {
            bArr = new byte[21];
            bArr[4] = 1;
            Bits.writeDouble(bArr, 5, d);
            Bits.writeDouble(bArr, 13, d3);
        } else if (d == d2 || d3 == d4) {
            bArr = new byte[41];
            bArr[4] = 2;
            bArr[8] = 2;
            Bits.writeDouble(bArr, 9, d);
            Bits.writeDouble(bArr, 17, d3);
            Bits.writeDouble(bArr, 25, d2);
            Bits.writeDouble(bArr, 33, d4);
        } else {
            bArr = new byte[93];
            bArr[4] = 3;
            bArr[8] = 1;
            bArr[12] = 5;
            Bits.writeDouble(bArr, 13, d);
            Bits.writeDouble(bArr, 21, d3);
            Bits.writeDouble(bArr, 29, d);
            Bits.writeDouble(bArr, 37, d4);
            Bits.writeDouble(bArr, 45, d2);
            Bits.writeDouble(bArr, 53, d4);
            Bits.writeDouble(bArr, 61, d2);
            Bits.writeDouble(bArr, 69, d3);
            Bits.writeDouble(bArr, 77, d);
            Bits.writeDouble(bArr, 85, d3);
        }
        return bArr;
    }

    private EWKBUtils() {
    }
}
