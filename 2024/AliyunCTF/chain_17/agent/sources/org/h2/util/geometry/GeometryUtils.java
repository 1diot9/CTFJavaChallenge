package org.h2.util.geometry;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/geometry/GeometryUtils.class */
public final class GeometryUtils {
    public static final int POINT = 1;
    public static final int LINE_STRING = 2;
    public static final int POLYGON = 3;
    public static final int MULTI_POINT = 4;
    public static final int MULTI_LINE_STRING = 5;
    public static final int MULTI_POLYGON = 6;
    public static final int GEOMETRY_COLLECTION = 7;
    public static final int X = 0;
    public static final int Y = 1;
    public static final int Z = 2;
    public static final int M = 3;
    public static final int DIMENSION_SYSTEM_XY = 0;
    public static final int DIMENSION_SYSTEM_XYZ = 1;
    public static final int DIMENSION_SYSTEM_XYM = 2;
    public static final int DIMENSION_SYSTEM_XYZM = 3;
    public static final int MIN_X = 0;
    public static final int MAX_X = 1;
    public static final int MIN_Y = 2;
    public static final int MAX_Y = 3;

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/geometry/GeometryUtils$Target.class */
    public static abstract class Target {
        /* JADX INFO: Access modifiers changed from: protected */
        public abstract void addCoordinate(double d, double d2, double d3, double d4, int i, int i2);

        /* JADX INFO: Access modifiers changed from: protected */
        public void init(int i) {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void dimensionSystem(int i) {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void startPoint() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void startLineString(int i) {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void startPolygon(int i, int i2) {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void startPolygonInner(int i) {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void endNonEmptyPolygon() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void startCollection(int i, int i2) {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public Target startCollectionItem(int i, int i2) {
            return this;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void endCollectionItem(Target target, int i, int i2, int i3) {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void endObject(int i) {
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/geometry/GeometryUtils$EnvelopeTarget.class */
    public static final class EnvelopeTarget extends Target {
        private boolean enabled;
        private boolean set;
        private double minX;
        private double maxX;
        private double minY;
        private double maxY;

        @Override // org.h2.util.geometry.GeometryUtils.Target
        protected void startPoint() {
            this.enabled = true;
        }

        @Override // org.h2.util.geometry.GeometryUtils.Target
        protected void startLineString(int i) {
            this.enabled = true;
        }

        @Override // org.h2.util.geometry.GeometryUtils.Target
        protected void startPolygon(int i, int i2) {
            this.enabled = true;
        }

        @Override // org.h2.util.geometry.GeometryUtils.Target
        protected void startPolygonInner(int i) {
            this.enabled = false;
        }

        @Override // org.h2.util.geometry.GeometryUtils.Target
        protected void addCoordinate(double d, double d2, double d3, double d4, int i, int i2) {
            if (this.enabled && !Double.isNaN(d) && !Double.isNaN(d2)) {
                if (!this.set) {
                    this.maxX = d;
                    this.minX = d;
                    this.maxY = d2;
                    this.minY = d2;
                    this.set = true;
                    return;
                }
                if (this.minX > d) {
                    this.minX = d;
                }
                if (this.maxX < d) {
                    this.maxX = d;
                }
                if (this.minY > d2) {
                    this.minY = d2;
                }
                if (this.maxY < d2) {
                    this.maxY = d2;
                }
            }
        }

        public double[] getEnvelope() {
            if (this.set) {
                return new double[]{this.minX, this.maxX, this.minY, this.maxY};
            }
            return null;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/geometry/GeometryUtils$DimensionSystemTarget.class */
    public static final class DimensionSystemTarget extends Target {
        private boolean hasZ;
        private boolean hasM;

        @Override // org.h2.util.geometry.GeometryUtils.Target
        protected void dimensionSystem(int i) {
            if ((i & 1) != 0) {
                this.hasZ = true;
            }
            if ((i & 2) != 0) {
                this.hasM = true;
            }
        }

        @Override // org.h2.util.geometry.GeometryUtils.Target
        protected void addCoordinate(double d, double d2, double d3, double d4, int i, int i2) {
            if (!this.hasZ && !Double.isNaN(d3)) {
                this.hasZ = true;
            }
            if (!this.hasM && !Double.isNaN(d4)) {
                this.hasM = true;
            }
        }

        public int getDimensionSystem() {
            return (this.hasZ ? 1 : 0) | (this.hasM ? 2 : 0);
        }
    }

    public static double[] getEnvelope(byte[] bArr) {
        EnvelopeTarget envelopeTarget = new EnvelopeTarget();
        EWKBUtils.parseEWKB(bArr, envelopeTarget);
        return envelopeTarget.getEnvelope();
    }

    public static boolean intersects(double[] dArr, double[] dArr2) {
        return dArr != null && dArr2 != null && dArr[1] >= dArr2[0] && dArr[0] <= dArr2[1] && dArr[3] >= dArr2[2] && dArr[2] <= dArr2[3];
    }

    public static double[] union(double[] dArr, double[] dArr2) {
        if (dArr == null) {
            return dArr2;
        }
        if (dArr2 == null) {
            return dArr;
        }
        double d = dArr[0];
        double d2 = dArr[1];
        double d3 = dArr[2];
        double d4 = dArr[3];
        double d5 = dArr2[0];
        double d6 = dArr2[1];
        double d7 = dArr2[2];
        double d8 = dArr2[3];
        boolean z = false;
        if (d > d5) {
            d = d5;
            z = true;
        }
        if (d2 < d6) {
            d2 = d6;
            z = true;
        }
        if (d3 > d7) {
            d3 = d7;
            z = true;
        }
        if (d4 < d8) {
            d4 = d8;
            z = true;
        }
        return z ? new double[]{d, d2, d3, d4} : dArr;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static double toCanonicalDouble(double d) {
        if (Double.isNaN(d)) {
            return Double.NaN;
        }
        if (d == 0.0d) {
            return 0.0d;
        }
        return d;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static double checkFinite(double d) {
        if (!Double.isFinite(d)) {
            throw new IllegalArgumentException();
        }
        return d;
    }

    private GeometryUtils() {
    }
}
