package org.h2.util.geometry;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import org.h2.util.StringUtils;
import org.h2.util.geometry.EWKBUtils;
import org.h2.util.geometry.GeometryUtils;
import org.springframework.asm.Opcodes;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/geometry/EWKTUtils.class */
public final class EWKTUtils {
    static final String[] TYPES = {"POINT", "LINESTRING", "POLYGON", "MULTIPOINT", "MULTILINESTRING", "MULTIPOLYGON", "GEOMETRYCOLLECTION"};
    private static final String[] DIMENSION_SYSTEMS = {"XY", "Z", "M", "ZM"};

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/geometry/EWKTUtils$EWKTTarget.class */
    public static final class EWKTTarget extends GeometryUtils.Target {
        private final StringBuilder output;
        private final int dimensionSystem;
        private int type;
        private boolean inMulti;

        public EWKTTarget(StringBuilder sb, int i) {
            this.output = sb;
            this.dimensionSystem = i;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.h2.util.geometry.GeometryUtils.Target
        public void init(int i) {
            if (i != 0) {
                this.output.append("SRID=").append(i).append(';');
            }
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
            if (i == 0) {
                this.output.append("EMPTY");
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.h2.util.geometry.GeometryUtils.Target
        public void startPolygon(int i, int i2) {
            writeHeader(3);
            if (i2 == 0) {
                this.output.append("EMPTY");
            } else {
                this.output.append('(');
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.h2.util.geometry.GeometryUtils.Target
        public void startPolygonInner(int i) {
            this.output.append(i > 0 ? ", " : ", EMPTY");
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.h2.util.geometry.GeometryUtils.Target
        public void endNonEmptyPolygon() {
            this.output.append(')');
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.h2.util.geometry.GeometryUtils.Target
        public void startCollection(int i, int i2) {
            writeHeader(i);
            if (i2 == 0) {
                this.output.append("EMPTY");
            }
            if (i != 7) {
                this.inMulti = true;
            }
        }

        private void writeHeader(int i) {
            this.type = i;
            if (this.inMulti) {
                return;
            }
            this.output.append(EWKTUtils.TYPES[i - 1]);
            switch (this.dimensionSystem) {
                case 1:
                    this.output.append(" Z");
                    break;
                case 2:
                    this.output.append(" M");
                    break;
                case 3:
                    this.output.append(" ZM");
                    break;
            }
            this.output.append(' ');
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.h2.util.geometry.GeometryUtils.Target
        public GeometryUtils.Target startCollectionItem(int i, int i2) {
            if (i == 0) {
                this.output.append('(');
            } else {
                this.output.append(", ");
            }
            return this;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.h2.util.geometry.GeometryUtils.Target
        public void endCollectionItem(GeometryUtils.Target target, int i, int i2, int i3) {
            if (i2 + 1 == i3) {
                this.output.append(')');
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.h2.util.geometry.GeometryUtils.Target
        public void endObject(int i) {
            switch (i) {
                case 4:
                case 5:
                case 6:
                    this.inMulti = false;
                    return;
                default:
                    return;
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.h2.util.geometry.GeometryUtils.Target
        public void addCoordinate(double d, double d2, double d3, double d4, int i, int i2) {
            if (this.type == 1 && Double.isNaN(d) && Double.isNaN(d2) && Double.isNaN(d3) && Double.isNaN(d4)) {
                this.output.append("EMPTY");
                return;
            }
            if (i == 0) {
                this.output.append('(');
            } else {
                this.output.append(", ");
            }
            writeDouble(d);
            this.output.append(' ');
            writeDouble(d2);
            if ((this.dimensionSystem & 1) != 0) {
                this.output.append(' ');
                writeDouble(d3);
            }
            if ((this.dimensionSystem & 2) != 0) {
                this.output.append(' ');
                writeDouble(d4);
            }
            if (i + 1 == i2) {
                this.output.append(')');
            }
        }

        private void writeDouble(double d) {
            String d2 = Double.toString(GeometryUtils.checkFinite(d));
            if (d2.endsWith(".0")) {
                this.output.append((CharSequence) d2, 0, d2.length() - 2);
                return;
            }
            int indexOf = d2.indexOf(".0E");
            if (indexOf < 0) {
                this.output.append(d2);
            } else {
                this.output.append((CharSequence) d2, 0, indexOf).append((CharSequence) d2, indexOf + 2, d2.length());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/geometry/EWKTUtils$EWKTSource.class */
    public static final class EWKTSource {
        private final String ewkt;
        private int offset;

        EWKTSource(String str) {
            this.ewkt = str;
        }

        int readSRID() {
            int i;
            skipWS();
            if (this.ewkt.regionMatches(true, this.offset, "SRID=", 0, 5)) {
                this.offset += 5;
                int indexOf = this.ewkt.indexOf(59, 5);
                if (indexOf < 0) {
                    throw new IllegalArgumentException();
                }
                int i2 = indexOf;
                while (this.ewkt.charAt(i2 - 1) <= ' ') {
                    i2--;
                }
                i = Integer.parseInt(StringUtils.trimSubstring(this.ewkt, this.offset, i2));
                this.offset = indexOf + 1;
            } else {
                i = 0;
            }
            return i;
        }

        void read(char c) {
            skipWS();
            if (this.offset >= this.ewkt.length()) {
                throw new IllegalArgumentException();
            }
            if (this.ewkt.charAt(this.offset) != c) {
                throw new IllegalArgumentException();
            }
            this.offset++;
        }

        int readType() {
            skipWS();
            if (this.offset >= this.ewkt.length()) {
                throw new IllegalArgumentException();
            }
            int i = 0;
            switch (this.ewkt.charAt(this.offset)) {
                case 'G':
                case 'g':
                    i = match("GEOMETRYCOLLECTION", 7);
                    break;
                case 'L':
                case 'l':
                    i = match("LINESTRING", 2);
                    break;
                case 'M':
                case Opcodes.LDIV /* 109 */:
                    if (match("MULTI", 1) != 0) {
                        i = match("POINT", 4);
                        if (i == 0) {
                            i = match("POLYGON", 6);
                            if (i == 0) {
                                i = match("LINESTRING", 5);
                                break;
                            }
                        }
                    }
                    break;
                case 'P':
                case 'p':
                    i = match("POINT", 1);
                    if (i == 0) {
                        i = match("POLYGON", 3);
                        break;
                    }
                    break;
            }
            if (i == 0) {
                throw new IllegalArgumentException();
            }
            return i;
        }

        int readDimensionSystem() {
            int i;
            int i2 = this.offset;
            skipWS();
            int length = this.ewkt.length();
            if (this.offset >= length) {
                throw new IllegalArgumentException();
            }
            switch (this.ewkt.charAt(this.offset)) {
                case 'M':
                case Opcodes.LDIV /* 109 */:
                    i = 2;
                    this.offset++;
                    break;
                case 'Z':
                case 'z':
                    this.offset++;
                    if (this.offset >= length) {
                        i = 1;
                        break;
                    } else {
                        char charAt = this.ewkt.charAt(this.offset);
                        if (charAt == 'M' || charAt == 'm') {
                            this.offset++;
                            i = 3;
                            break;
                        } else {
                            i = 1;
                            break;
                        }
                    }
                    break;
                default:
                    i = 0;
                    if (i2 != this.offset) {
                        return 0;
                    }
                    break;
            }
            checkStringEnd(length);
            return i;
        }

        boolean readEmpty() {
            skipWS();
            int length = this.ewkt.length();
            if (this.offset >= length) {
                throw new IllegalArgumentException();
            }
            if (this.ewkt.charAt(this.offset) == '(') {
                this.offset++;
                return false;
            }
            if (match("EMPTY", 1) != 0) {
                checkStringEnd(length);
                return true;
            }
            throw new IllegalArgumentException();
        }

        private int match(String str, int i) {
            int length = str.length();
            if (this.offset <= this.ewkt.length() - length && this.ewkt.regionMatches(true, this.offset, str, 0, length)) {
                this.offset += length;
            } else {
                i = 0;
            }
            return i;
        }

        private void checkStringEnd(int i) {
            char charAt;
            if (this.offset < i && (charAt = this.ewkt.charAt(this.offset)) > ' ' && charAt != '(' && charAt != ')' && charAt != ',') {
                throw new IllegalArgumentException();
            }
        }

        public boolean hasCoordinate() {
            skipWS();
            if (this.offset >= this.ewkt.length()) {
                return false;
            }
            return isNumberStart(this.ewkt.charAt(this.offset));
        }

        public double readCoordinate() {
            skipWS();
            int length = this.ewkt.length();
            if (this.offset >= length) {
                throw new IllegalArgumentException();
            }
            char charAt = this.ewkt.charAt(this.offset);
            if (!isNumberStart(charAt)) {
                throw new IllegalArgumentException();
            }
            int i = this.offset;
            this.offset = i + 1;
            while (this.offset < length) {
                char charAt2 = this.ewkt.charAt(this.offset);
                charAt = charAt2;
                if (!isNumberPart(charAt2)) {
                    break;
                }
                this.offset++;
            }
            if (this.offset < length && charAt > ' ' && charAt != ')' && charAt != ',') {
                throw new IllegalArgumentException();
            }
            Double valueOf = Double.valueOf(Double.parseDouble(this.ewkt.substring(i, this.offset)));
            if (valueOf.doubleValue() == 0.0d) {
                return 0.0d;
            }
            return valueOf.doubleValue();
        }

        private static boolean isNumberStart(char c) {
            if (c >= '0' && c <= '9') {
                return true;
            }
            switch (c) {
                case '+':
                case '-':
                case '.':
                    return true;
                case ',':
                default:
                    return false;
            }
        }

        private static boolean isNumberPart(char c) {
            if (c >= '0' && c <= '9') {
                return true;
            }
            switch (c) {
                case '+':
                case '-':
                case '.':
                case 'E':
                case 'e':
                    return true;
                default:
                    return false;
            }
        }

        public boolean hasMoreCoordinates() {
            skipWS();
            if (this.offset >= this.ewkt.length()) {
                throw new IllegalArgumentException();
            }
            switch (this.ewkt.charAt(this.offset)) {
                case ')':
                    this.offset++;
                    return false;
                case ',':
                    this.offset++;
                    return true;
                default:
                    throw new IllegalArgumentException();
            }
        }

        boolean hasData() {
            skipWS();
            return this.offset < this.ewkt.length();
        }

        int getItemCount() {
            int i = 1;
            int i2 = this.offset;
            int i3 = 0;
            int length = this.ewkt.length();
            while (i2 < length) {
                int i4 = i2;
                i2++;
                switch (this.ewkt.charAt(i4)) {
                    case '(':
                        i3++;
                        break;
                    case ')':
                        i3--;
                        if (i3 >= 0) {
                            break;
                        } else {
                            return i;
                        }
                    case ',':
                        if (i3 != 0) {
                            break;
                        } else {
                            i++;
                            break;
                        }
                }
            }
            throw new IllegalArgumentException();
        }

        private void skipWS() {
            int length = this.ewkt.length();
            while (this.offset < length && this.ewkt.charAt(this.offset) <= ' ') {
                this.offset++;
            }
        }

        public String toString() {
            return new StringBuilder(this.ewkt.length() + 3).append((CharSequence) this.ewkt, 0, this.offset).append("<*>").append((CharSequence) this.ewkt, this.offset, this.ewkt.length()).toString();
        }
    }

    public static String ewkb2ewkt(byte[] bArr) {
        return ewkb2ewkt(bArr, EWKBUtils.getDimensionSystem(bArr));
    }

    public static String ewkb2ewkt(byte[] bArr, int i) {
        StringBuilder sb = new StringBuilder();
        EWKBUtils.parseEWKB(bArr, new EWKTTarget(sb, i));
        return sb.toString();
    }

    public static byte[] ewkt2ewkb(String str) {
        return ewkt2ewkb(str, getDimensionSystem(str));
    }

    public static byte[] ewkt2ewkb(String str, int i) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        parseEWKT(str, new EWKBUtils.EWKBTarget(byteArrayOutputStream, i));
        return byteArrayOutputStream.toByteArray();
    }

    public static void parseEWKT(String str, GeometryUtils.Target target) {
        parseEWKT(new EWKTSource(str), target, 0, 0);
    }

    public static int parseGeometryType(String str) {
        EWKTSource eWKTSource = new EWKTSource(str);
        int readType = eWKTSource.readType();
        int i = 0;
        if (eWKTSource.hasData()) {
            i = eWKTSource.readDimensionSystem();
            if (eWKTSource.hasData()) {
                throw new IllegalArgumentException();
            }
        }
        return (i * 1000) + readType;
    }

    public static int parseDimensionSystem(String str) {
        EWKTSource eWKTSource = new EWKTSource(str);
        int readDimensionSystem = eWKTSource.readDimensionSystem();
        if (eWKTSource.hasData() || readDimensionSystem == 0) {
            throw new IllegalArgumentException();
        }
        return readDimensionSystem;
    }

    public static StringBuilder formatGeometryTypeAndDimensionSystem(StringBuilder sb, int i) {
        int i2 = i % 1000;
        int i3 = i / 1000;
        if (i2 < 1 || i2 > 7 || i3 < 0 || i3 > 3) {
            throw new IllegalArgumentException();
        }
        sb.append(TYPES[i2 - 1]);
        if (i3 != 0) {
            sb.append(' ').append(DIMENSION_SYSTEMS[i3]);
        }
        return sb;
    }

    private static void parseEWKT(EWKTSource eWKTSource, GeometryUtils.Target target, int i, int i2) {
        int i3;
        if (i == 0) {
            target.init(eWKTSource.readSRID());
        }
        switch (i) {
            case 4:
                i3 = 1;
                break;
            case 5:
                i3 = 2;
                break;
            case 6:
                i3 = 3;
                break;
            default:
                i3 = eWKTSource.readType();
                i2 = eWKTSource.readDimensionSystem();
                break;
        }
        target.dimensionSystem(i2);
        switch (i3) {
            case 1:
                if (i != 0 && i != 4 && i != 7) {
                    throw new IllegalArgumentException();
                }
                boolean readEmpty = eWKTSource.readEmpty();
                target.startPoint();
                if (readEmpty) {
                    target.addCoordinate(Double.NaN, Double.NaN, Double.NaN, Double.NaN, 0, 1);
                    break;
                } else {
                    addCoordinate(eWKTSource, target, i2, 0, 1);
                    eWKTSource.read(')');
                    break;
                }
                break;
            case 2:
                if (i != 0 && i != 5 && i != 7) {
                    throw new IllegalArgumentException();
                }
                if (eWKTSource.readEmpty()) {
                    target.startLineString(0);
                    break;
                } else {
                    ArrayList arrayList = new ArrayList();
                    do {
                        arrayList.add(readCoordinate(eWKTSource, i2));
                    } while (eWKTSource.hasMoreCoordinates());
                    int size = arrayList.size();
                    if (size < 0 || size == 1) {
                        throw new IllegalArgumentException();
                    }
                    target.startLineString(size);
                    for (int i4 = 0; i4 < size; i4++) {
                        double[] dArr = (double[]) arrayList.get(i4);
                        target.addCoordinate(dArr[0], dArr[1], dArr[2], dArr[3], i4, size);
                    }
                    break;
                }
                break;
            case 3:
                if (i != 0 && i != 6 && i != 7) {
                    throw new IllegalArgumentException();
                }
                if (eWKTSource.readEmpty()) {
                    target.startPolygon(0, 0);
                    break;
                } else {
                    ArrayList<double[]> readRing = readRing(eWKTSource, i2);
                    ArrayList arrayList2 = new ArrayList();
                    while (eWKTSource.hasMoreCoordinates()) {
                        arrayList2.add(readRing(eWKTSource, i2));
                    }
                    int size2 = arrayList2.size();
                    int size3 = readRing.size();
                    if (size3 >= 1 && size3 <= 3) {
                        throw new IllegalArgumentException();
                    }
                    if (size3 == 0 && size2 > 0) {
                        throw new IllegalArgumentException();
                    }
                    target.startPolygon(size2, size3);
                    if (size3 > 0) {
                        addRing(readRing, target);
                        for (int i5 = 0; i5 < size2; i5++) {
                            ArrayList arrayList3 = (ArrayList) arrayList2.get(i5);
                            int size4 = arrayList3.size();
                            if (size4 >= 1 && size4 <= 3) {
                                throw new IllegalArgumentException();
                            }
                            target.startPolygonInner(size4);
                            addRing(arrayList3, target);
                        }
                        target.endNonEmptyPolygon();
                        break;
                    }
                }
                break;
            case 4:
            case 5:
            case 6:
                parseCollection(eWKTSource, target, i3, i, i2);
                break;
            case 7:
                parseCollection(eWKTSource, target, 7, i, 0);
                break;
            default:
                throw new IllegalArgumentException();
        }
        target.endObject(i3);
        if (i == 0 && eWKTSource.hasData()) {
            throw new IllegalArgumentException();
        }
    }

    private static void parseCollection(EWKTSource eWKTSource, GeometryUtils.Target target, int i, int i2, int i3) {
        if (i2 != 0 && i2 != 7) {
            throw new IllegalArgumentException();
        }
        if (eWKTSource.readEmpty()) {
            target.startCollection(i, 0);
            return;
        }
        if (i == 4 && eWKTSource.hasCoordinate()) {
            parseMultiPointAlternative(eWKTSource, target, i3);
            return;
        }
        int itemCount = eWKTSource.getItemCount();
        target.startCollection(i, itemCount);
        for (int i4 = 0; i4 < itemCount; i4++) {
            if (i4 > 0) {
                eWKTSource.read(',');
            }
            GeometryUtils.Target startCollectionItem = target.startCollectionItem(i4, itemCount);
            parseEWKT(eWKTSource, startCollectionItem, i, i3);
            target.endCollectionItem(startCollectionItem, i, i4, itemCount);
        }
        eWKTSource.read(')');
    }

    private static void parseMultiPointAlternative(EWKTSource eWKTSource, GeometryUtils.Target target, int i) {
        ArrayList arrayList = new ArrayList();
        do {
            arrayList.add(readCoordinate(eWKTSource, i));
        } while (eWKTSource.hasMoreCoordinates());
        int size = arrayList.size();
        target.startCollection(4, size);
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            GeometryUtils.Target startCollectionItem = target.startCollectionItem(i2, size);
            target.startPoint();
            double[] dArr = (double[]) arrayList.get(i2);
            target.addCoordinate(dArr[0], dArr[1], dArr[2], dArr[3], 0, 1);
            target.endCollectionItem(startCollectionItem, 4, i2, size);
        }
    }

    private static ArrayList<double[]> readRing(EWKTSource eWKTSource, int i) {
        if (eWKTSource.readEmpty()) {
            return new ArrayList<>(0);
        }
        ArrayList<double[]> arrayList = new ArrayList<>();
        double[] readCoordinate = readCoordinate(eWKTSource, i);
        double d = readCoordinate[0];
        double d2 = readCoordinate[1];
        arrayList.add(readCoordinate);
        while (eWKTSource.hasMoreCoordinates()) {
            arrayList.add(readCoordinate(eWKTSource, i));
        }
        int size = arrayList.size();
        if (size < 4) {
            throw new IllegalArgumentException();
        }
        double[] dArr = arrayList.get(size - 1);
        double d3 = dArr[0];
        double d4 = dArr[1];
        if (d != d3 || d2 != d4) {
            throw new IllegalArgumentException();
        }
        return arrayList;
    }

    private static void addRing(ArrayList<double[]> arrayList, GeometryUtils.Target target) {
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            double[] dArr = arrayList.get(i);
            target.addCoordinate(dArr[0], dArr[1], dArr[2], dArr[3], i, size);
        }
    }

    private static void addCoordinate(EWKTSource eWKTSource, GeometryUtils.Target target, int i, int i2, int i3) {
        target.addCoordinate(eWKTSource.readCoordinate(), eWKTSource.readCoordinate(), (i & 1) != 0 ? eWKTSource.readCoordinate() : Double.NaN, (i & 2) != 0 ? eWKTSource.readCoordinate() : Double.NaN, i2, i3);
    }

    private static double[] readCoordinate(EWKTSource eWKTSource, int i) {
        return new double[]{eWKTSource.readCoordinate(), eWKTSource.readCoordinate(), (i & 1) != 0 ? eWKTSource.readCoordinate() : Double.NaN, (i & 2) != 0 ? eWKTSource.readCoordinate() : Double.NaN};
    }

    public static int getDimensionSystem(String str) {
        EWKTSource eWKTSource = new EWKTSource(str);
        eWKTSource.readSRID();
        eWKTSource.readType();
        return eWKTSource.readDimensionSystem();
    }

    private EWKTUtils() {
    }
}
