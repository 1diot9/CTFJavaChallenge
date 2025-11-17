package org.h2.util.geometry;

import java.io.ByteArrayOutputStream;
import org.h2.message.DbException;
import org.h2.util.geometry.EWKBUtils;
import org.h2.util.geometry.GeometryUtils;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.geom.impl.CoordinateArraySequenceFactory;
import org.locationtech.jts.geom.impl.PackedCoordinateSequenceFactory;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/geometry/JTSUtils.class */
public final class JTSUtils {

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/geometry/JTSUtils$GeometryTarget.class */
    public static final class GeometryTarget extends GeometryUtils.Target {
        private final int dimensionSystem;
        private GeometryFactory factory;
        private int type;
        private CoordinateSequence coordinates;
        private CoordinateSequence[] innerCoordinates;
        private int innerOffset;
        private Geometry[] subgeometries;

        public GeometryTarget(int i) {
            this.dimensionSystem = i;
        }

        private GeometryTarget(int i, GeometryFactory geometryFactory) {
            this.dimensionSystem = i;
            this.factory = geometryFactory;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.h2.util.geometry.GeometryUtils.Target
        public void init(int i) {
            this.factory = new GeometryFactory(new PrecisionModel(), i, (this.dimensionSystem & 2) != 0 ? PackedCoordinateSequenceFactory.DOUBLE_FACTORY : CoordinateArraySequenceFactory.instance());
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.h2.util.geometry.GeometryUtils.Target
        public void startPoint() {
            this.type = 1;
            initCoordinates(1);
            this.innerOffset = -1;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.h2.util.geometry.GeometryUtils.Target
        public void startLineString(int i) {
            this.type = 2;
            initCoordinates(i);
            this.innerOffset = -1;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.h2.util.geometry.GeometryUtils.Target
        public void startPolygon(int i, int i2) {
            this.type = 3;
            initCoordinates(i2);
            this.innerCoordinates = new CoordinateSequence[i];
            this.innerOffset = -1;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.h2.util.geometry.GeometryUtils.Target
        public void startPolygonInner(int i) {
            CoordinateSequence[] coordinateSequenceArr = this.innerCoordinates;
            int i2 = this.innerOffset + 1;
            this.innerOffset = i2;
            coordinateSequenceArr[i2] = createCoordinates(i);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.h2.util.geometry.GeometryUtils.Target
        public void startCollection(int i, int i2) {
            this.type = i;
            switch (i) {
                case 4:
                    this.subgeometries = new Point[i2];
                    return;
                case 5:
                    this.subgeometries = new LineString[i2];
                    return;
                case 6:
                    this.subgeometries = new Polygon[i2];
                    return;
                case 7:
                    this.subgeometries = new Geometry[i2];
                    return;
                default:
                    throw new IllegalArgumentException();
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.h2.util.geometry.GeometryUtils.Target
        public GeometryUtils.Target startCollectionItem(int i, int i2) {
            return new GeometryTarget(this.dimensionSystem, this.factory);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.h2.util.geometry.GeometryUtils.Target
        public void endCollectionItem(GeometryUtils.Target target, int i, int i2, int i3) {
            this.subgeometries[i2] = ((GeometryTarget) target).getGeometry();
        }

        private void initCoordinates(int i) {
            this.coordinates = createCoordinates(i);
        }

        private CoordinateSequence createCoordinates(int i) {
            int i2;
            int i3;
            switch (this.dimensionSystem) {
                case 0:
                    i2 = 2;
                    i3 = 0;
                    break;
                case 1:
                    i2 = 3;
                    i3 = 0;
                    break;
                case 2:
                    i2 = 3;
                    i3 = 1;
                    break;
                case 3:
                    i2 = 4;
                    i3 = 1;
                    break;
                default:
                    throw DbException.getInternalError();
            }
            return this.factory.getCoordinateSequenceFactory().create(i, i2, i3);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.h2.util.geometry.GeometryUtils.Target
        public void addCoordinate(double d, double d2, double d3, double d4, int i, int i2) {
            if (this.type == 1 && Double.isNaN(d) && Double.isNaN(d2) && Double.isNaN(d3) && Double.isNaN(d4)) {
                this.coordinates = createCoordinates(0);
                return;
            }
            CoordinateSequence coordinateSequence = this.innerOffset < 0 ? this.coordinates : this.innerCoordinates[this.innerOffset];
            coordinateSequence.setOrdinate(i, 0, GeometryUtils.checkFinite(d));
            coordinateSequence.setOrdinate(i, 1, GeometryUtils.checkFinite(d2));
            switch (this.dimensionSystem) {
                case 1:
                    break;
                case 2:
                    coordinateSequence.setOrdinate(i, 2, GeometryUtils.checkFinite(d4));
                    return;
                case 3:
                    coordinateSequence.setOrdinate(i, 3, GeometryUtils.checkFinite(d4));
                    break;
                default:
                    return;
            }
            coordinateSequence.setOrdinate(i, 2, GeometryUtils.checkFinite(d3));
        }

        Geometry getGeometry() {
            switch (this.type) {
                case 1:
                    return new Point(this.coordinates, this.factory);
                case 2:
                    return new LineString(this.coordinates, this.factory);
                case 3:
                    LinearRing linearRing = new LinearRing(this.coordinates, this.factory);
                    int length = this.innerCoordinates.length;
                    LinearRing[] linearRingArr = new LinearRing[length];
                    for (int i = 0; i < length; i++) {
                        linearRingArr[i] = new LinearRing(this.innerCoordinates[i], this.factory);
                    }
                    return new Polygon(linearRing, linearRingArr, this.factory);
                case 4:
                    return new MultiPoint(this.subgeometries, this.factory);
                case 5:
                    return new MultiLineString(this.subgeometries, this.factory);
                case 6:
                    return new MultiPolygon(this.subgeometries, this.factory);
                case 7:
                    return new GeometryCollection(this.subgeometries, this.factory);
                default:
                    throw new IllegalStateException();
            }
        }
    }

    public static Geometry ewkb2geometry(byte[] bArr) {
        return ewkb2geometry(bArr, EWKBUtils.getDimensionSystem(bArr));
    }

    public static Geometry ewkb2geometry(byte[] bArr, int i) {
        GeometryTarget geometryTarget = new GeometryTarget(i);
        EWKBUtils.parseEWKB(bArr, geometryTarget);
        return geometryTarget.getGeometry();
    }

    public static byte[] geometry2ewkb(Geometry geometry) {
        return geometry2ewkb(geometry, getDimensionSystem(geometry));
    }

    public static byte[] geometry2ewkb(Geometry geometry, int i) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        parseGeometry(geometry, new EWKBUtils.EWKBTarget(byteArrayOutputStream, i));
        return byteArrayOutputStream.toByteArray();
    }

    public static void parseGeometry(Geometry geometry, GeometryUtils.Target target) {
        parseGeometry(geometry, target, 0);
    }

    private static void parseGeometry(Geometry geometry, GeometryUtils.Target target, int i) {
        int i2;
        if (i == 0) {
            target.init(geometry.getSRID());
        }
        if (geometry instanceof Point) {
            if (i != 0 && i != 4 && i != 7) {
                throw new IllegalArgumentException();
            }
            target.startPoint();
            Point point = (Point) geometry;
            if (point.isEmpty()) {
                target.addCoordinate(Double.NaN, Double.NaN, Double.NaN, Double.NaN, 0, 1);
            } else {
                addCoordinate(point.getCoordinateSequence(), target, 0, 1);
            }
            target.endObject(1);
            return;
        }
        if (geometry instanceof LineString) {
            if (i != 0 && i != 5 && i != 7) {
                throw new IllegalArgumentException();
            }
            CoordinateSequence coordinateSequence = ((LineString) geometry).getCoordinateSequence();
            int size = coordinateSequence.size();
            if (size == 1) {
                throw new IllegalArgumentException();
            }
            target.startLineString(size);
            for (int i3 = 0; i3 < size; i3++) {
                addCoordinate(coordinateSequence, target, i3, size);
            }
            target.endObject(2);
            return;
        }
        if (!(geometry instanceof Polygon)) {
            if (geometry instanceof GeometryCollection) {
                if (i != 0 && i != 7) {
                    throw new IllegalArgumentException();
                }
                GeometryCollection geometryCollection = (GeometryCollection) geometry;
                if (geometryCollection instanceof MultiPoint) {
                    i2 = 4;
                } else if (geometryCollection instanceof MultiLineString) {
                    i2 = 5;
                } else if (geometryCollection instanceof MultiPolygon) {
                    i2 = 6;
                } else {
                    i2 = 7;
                }
                int numGeometries = geometryCollection.getNumGeometries();
                target.startCollection(i2, numGeometries);
                for (int i4 = 0; i4 < numGeometries; i4++) {
                    GeometryUtils.Target startCollectionItem = target.startCollectionItem(i4, numGeometries);
                    parseGeometry(geometryCollection.getGeometryN(i4), startCollectionItem, i2);
                    target.endCollectionItem(startCollectionItem, i2, i4, numGeometries);
                }
                target.endObject(i2);
                return;
            }
            throw new IllegalArgumentException();
        }
        if (i != 0 && i != 6 && i != 7) {
            throw new IllegalArgumentException();
        }
        Polygon polygon = (Polygon) geometry;
        int numInteriorRing = polygon.getNumInteriorRing();
        CoordinateSequence coordinateSequence2 = polygon.getExteriorRing().getCoordinateSequence();
        int size2 = coordinateSequence2.size();
        if (size2 >= 1 && size2 <= 3) {
            throw new IllegalArgumentException();
        }
        if (size2 == 0 && numInteriorRing > 0) {
            throw new IllegalArgumentException();
        }
        target.startPolygon(numInteriorRing, size2);
        if (size2 > 0) {
            addRing(coordinateSequence2, target, size2);
            for (int i5 = 0; i5 < numInteriorRing; i5++) {
                CoordinateSequence coordinateSequence3 = polygon.getInteriorRingN(i5).getCoordinateSequence();
                int size3 = coordinateSequence3.size();
                if (size3 >= 1 && size3 <= 3) {
                    throw new IllegalArgumentException();
                }
                target.startPolygonInner(size3);
                addRing(coordinateSequence3, target, size3);
            }
            target.endNonEmptyPolygon();
        }
        target.endObject(3);
    }

    private static void addRing(CoordinateSequence coordinateSequence, GeometryUtils.Target target, int i) {
        if (i >= 4) {
            double canonicalDouble = GeometryUtils.toCanonicalDouble(coordinateSequence.getX(0));
            double canonicalDouble2 = GeometryUtils.toCanonicalDouble(coordinateSequence.getY(0));
            addCoordinate(coordinateSequence, target, 0, i, canonicalDouble, canonicalDouble2);
            for (int i2 = 1; i2 < i - 1; i2++) {
                addCoordinate(coordinateSequence, target, i2, i);
            }
            double canonicalDouble3 = GeometryUtils.toCanonicalDouble(coordinateSequence.getX(i - 1));
            double canonicalDouble4 = GeometryUtils.toCanonicalDouble(coordinateSequence.getY(i - 1));
            if (canonicalDouble != canonicalDouble3 || canonicalDouble2 != canonicalDouble4) {
                throw new IllegalArgumentException();
            }
            addCoordinate(coordinateSequence, target, i - 1, i, canonicalDouble3, canonicalDouble4);
        }
    }

    private static void addCoordinate(CoordinateSequence coordinateSequence, GeometryUtils.Target target, int i, int i2) {
        addCoordinate(coordinateSequence, target, i, i2, GeometryUtils.toCanonicalDouble(coordinateSequence.getX(i)), GeometryUtils.toCanonicalDouble(coordinateSequence.getY(i)));
    }

    private static void addCoordinate(CoordinateSequence coordinateSequence, GeometryUtils.Target target, int i, int i2, double d, double d2) {
        target.addCoordinate(d, d2, GeometryUtils.toCanonicalDouble(coordinateSequence.getZ(i)), GeometryUtils.toCanonicalDouble(coordinateSequence.getM(i)), i, i2);
    }

    public static int getDimensionSystem(Geometry geometry) {
        int dimensionSystem1 = getDimensionSystem1(geometry);
        if (dimensionSystem1 >= 0) {
            return dimensionSystem1;
        }
        return 0;
    }

    private static int getDimensionSystem1(Geometry geometry) {
        int i;
        if (geometry instanceof Point) {
            i = getDimensionSystemFromSequence(((Point) geometry).getCoordinateSequence());
        } else if (geometry instanceof LineString) {
            i = getDimensionSystemFromSequence(((LineString) geometry).getCoordinateSequence());
        } else if (geometry instanceof Polygon) {
            i = getDimensionSystemFromSequence(((Polygon) geometry).getExteriorRing().getCoordinateSequence());
        } else if (geometry instanceof GeometryCollection) {
            i = -1;
            GeometryCollection geometryCollection = (GeometryCollection) geometry;
            int numGeometries = geometryCollection.getNumGeometries();
            for (int i2 = 0; i2 < numGeometries; i2++) {
                i = getDimensionSystem1(geometryCollection.getGeometryN(i2));
                if (i >= 0) {
                    break;
                }
            }
        } else {
            throw new IllegalArgumentException();
        }
        return i;
    }

    private static int getDimensionSystemFromSequence(CoordinateSequence coordinateSequence) {
        int size = coordinateSequence.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                int dimensionSystemFromCoordinate = getDimensionSystemFromCoordinate(coordinateSequence, i);
                if (dimensionSystemFromCoordinate >= 0) {
                    return dimensionSystemFromCoordinate;
                }
            }
        }
        return (coordinateSequence.hasZ() ? 1 : 0) | (coordinateSequence.hasM() ? 2 : 0);
    }

    private static int getDimensionSystemFromCoordinate(CoordinateSequence coordinateSequence, int i) {
        if (Double.isNaN(coordinateSequence.getX(i))) {
            return -1;
        }
        return (!Double.isNaN(coordinateSequence.getZ(i)) ? 1 : 0) | (!Double.isNaN(coordinateSequence.getM(i)) ? 2 : 0);
    }

    private JTSUtils() {
    }
}
