package org.h2.util.geometry;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import org.h2.api.ErrorCode;
import org.h2.message.DbException;
import org.h2.util.geometry.EWKBUtils;
import org.h2.util.geometry.GeometryUtils;
import org.h2.util.json.JSONArray;
import org.h2.util.json.JSONByteArrayTarget;
import org.h2.util.json.JSONBytesSource;
import org.h2.util.json.JSONNull;
import org.h2.util.json.JSONNumber;
import org.h2.util.json.JSONObject;
import org.h2.util.json.JSONString;
import org.h2.util.json.JSONValue;
import org.h2.util.json.JSONValueTarget;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/geometry/GeoJsonUtils.class */
public final class GeoJsonUtils {
    static final String[] TYPES = {"Point", "LineString", "Polygon", "MultiPoint", "MultiLineString", "MultiPolygon", "GeometryCollection"};

    /* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/geometry/GeoJsonUtils$GeoJsonTarget.class */
    public static final class GeoJsonTarget extends GeometryUtils.Target {
        private final JSONByteArrayTarget output;
        private final int dimensionSystem;
        private int type;
        private boolean inMulti;
        private boolean inMultiLine;
        private boolean wasEmpty;

        public GeoJsonTarget(JSONByteArrayTarget jSONByteArrayTarget, int i) {
            if (i == 2) {
                throw DbException.get(ErrorCode.DATA_CONVERSION_ERROR_1, "M (XYM) dimension system is not supported in GeoJson");
            }
            this.output = jSONByteArrayTarget;
            this.dimensionSystem = i;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.h2.util.geometry.GeometryUtils.Target
        public void startPoint() {
            this.type = 1;
            this.wasEmpty = false;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.h2.util.geometry.GeometryUtils.Target
        public void startLineString(int i) {
            writeHeader(2);
            if (i == 0) {
                this.output.endArray();
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.h2.util.geometry.GeometryUtils.Target
        public void startPolygon(int i, int i2) {
            writeHeader(3);
            if (i2 == 0) {
                this.output.endArray();
            } else {
                this.output.startArray();
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.h2.util.geometry.GeometryUtils.Target
        public void startPolygonInner(int i) {
            this.output.startArray();
            if (i == 0) {
                this.output.endArray();
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.h2.util.geometry.GeometryUtils.Target
        public void endNonEmptyPolygon() {
            this.output.endArray();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.h2.util.geometry.GeometryUtils.Target
        public void startCollection(int i, int i2) {
            writeHeader(i);
            if (i != 7) {
                this.inMulti = true;
                if (i == 5 || i == 6) {
                    this.inMultiLine = true;
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.h2.util.geometry.GeometryUtils.Target
        public GeometryUtils.Target startCollectionItem(int i, int i2) {
            if (this.inMultiLine) {
                this.output.startArray();
            }
            return this;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.h2.util.geometry.GeometryUtils.Target
        public void endObject(int i) {
            switch (i) {
                case 4:
                case 5:
                case 6:
                    this.inMulti = false;
                    this.inMultiLine = false;
                case 7:
                    this.output.endArray();
                    break;
            }
            if (!this.inMulti && !this.wasEmpty) {
                this.output.endObject();
            }
        }

        private void writeHeader(int i) {
            this.type = i;
            this.wasEmpty = false;
            if (!this.inMulti) {
                writeStartObject(i);
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.h2.util.geometry.GeometryUtils.Target
        public void addCoordinate(double d, double d2, double d3, double d4, int i, int i2) {
            if (this.type == 1) {
                if (Double.isNaN(d) && Double.isNaN(d2) && Double.isNaN(d3) && Double.isNaN(d4)) {
                    this.wasEmpty = true;
                    this.output.valueNull();
                    return;
                } else if (!this.inMulti) {
                    writeStartObject(1);
                }
            }
            this.output.startArray();
            writeDouble(d);
            writeDouble(d2);
            if ((this.dimensionSystem & 1) != 0) {
                writeDouble(d3);
            }
            if ((this.dimensionSystem & 2) != 0) {
                writeDouble(d4);
            }
            this.output.endArray();
            if (this.type != 1 && i + 1 == i2) {
                this.output.endArray();
            }
        }

        private void writeStartObject(int i) {
            this.output.startObject();
            this.output.member("type");
            this.output.valueString(GeoJsonUtils.TYPES[i - 1]);
            this.output.member(i != 7 ? "coordinates" : "geometries");
            if (i != 1) {
                this.output.startArray();
            }
        }

        private void writeDouble(double d) {
            this.output.valueNumber(BigDecimal.valueOf(GeometryUtils.checkFinite(d)).stripTrailingZeros());
        }
    }

    public static byte[] ewkbToGeoJson(byte[] bArr, int i) {
        JSONByteArrayTarget jSONByteArrayTarget = new JSONByteArrayTarget();
        EWKBUtils.parseEWKB(bArr, new GeoJsonTarget(jSONByteArrayTarget, i));
        return jSONByteArrayTarget.getResult();
    }

    public static byte[] geoJsonToEwkb(byte[] bArr, int i) {
        JSONValue jSONValue = (JSONValue) JSONBytesSource.parse(bArr, new JSONValueTarget());
        GeometryUtils.DimensionSystemTarget dimensionSystemTarget = new GeometryUtils.DimensionSystemTarget();
        parse(jSONValue, dimensionSystemTarget);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        EWKBUtils.EWKBTarget eWKBTarget = new EWKBUtils.EWKBTarget(byteArrayOutputStream, dimensionSystemTarget.getDimensionSystem());
        eWKBTarget.init(i);
        parse(jSONValue, eWKBTarget);
        return byteArrayOutputStream.toByteArray();
    }

    private static void parse(JSONValue jSONValue, GeometryUtils.Target target) {
        if (jSONValue instanceof JSONNull) {
            target.startPoint();
            target.addCoordinate(Double.NaN, Double.NaN, Double.NaN, Double.NaN, 0, 1);
            target.endObject(1);
            return;
        }
        if (jSONValue instanceof JSONObject) {
            JSONObject jSONObject = (JSONObject) jSONValue;
            JSONValue first = jSONObject.getFirst("type");
            if (!(first instanceof JSONString)) {
                throw new IllegalArgumentException();
            }
            String string = ((JSONString) first).getString();
            boolean z = -1;
            switch (string.hashCode()) {
                case -2116761119:
                    if (string.equals("MultiPolygon")) {
                        z = 5;
                        break;
                    }
                    break;
                case -1065891849:
                    if (string.equals("MultiPoint")) {
                        z = 3;
                        break;
                    }
                    break;
                case -627102946:
                    if (string.equals("MultiLineString")) {
                        z = 4;
                        break;
                    }
                    break;
                case 77292912:
                    if (string.equals("Point")) {
                        z = false;
                        break;
                    }
                    break;
                case 1267133722:
                    if (string.equals("Polygon")) {
                        z = 2;
                        break;
                    }
                    break;
                case 1806700869:
                    if (string.equals("LineString")) {
                        z = true;
                        break;
                    }
                    break;
                case 1950410960:
                    if (string.equals("GeometryCollection")) {
                        z = 6;
                        break;
                    }
                    break;
            }
            switch (z) {
                case false:
                    parse(jSONObject, target, 1);
                    return;
                case true:
                    parse(jSONObject, target, 2);
                    return;
                case true:
                    parse(jSONObject, target, 3);
                    return;
                case true:
                    parse(jSONObject, target, 4);
                    return;
                case true:
                    parse(jSONObject, target, 5);
                    return;
                case true:
                    parse(jSONObject, target, 6);
                    return;
                case true:
                    parseGeometryCollection(jSONObject, target);
                    return;
                default:
                    throw new IllegalArgumentException();
            }
        }
        throw new IllegalArgumentException();
    }

    private static void parse(JSONObject jSONObject, GeometryUtils.Target target, int i) {
        JSONValue first = jSONObject.getFirst("coordinates");
        if (!(first instanceof JSONArray)) {
            throw new IllegalArgumentException();
        }
        JSONArray jSONArray = (JSONArray) first;
        switch (i) {
            case 1:
                target.startPoint();
                parseCoordinate(jSONArray, target, 0, 1);
                target.endObject(1);
                return;
            case 2:
                parseLineString(jSONArray, target);
                return;
            case 3:
                parsePolygon(jSONArray, target);
                return;
            case 4:
                JSONValue[] array = jSONArray.getArray();
                int length = array.length;
                target.startCollection(4, length);
                for (int i2 = 0; i2 < length; i2++) {
                    target.startPoint();
                    parseCoordinate(array[i2], target, 0, 1);
                    target.endObject(1);
                    target.endCollectionItem(target, 4, i2, length);
                }
                target.endObject(4);
                return;
            case 5:
                JSONValue[] array2 = jSONArray.getArray();
                int length2 = array2.length;
                target.startCollection(5, length2);
                for (int i3 = 0; i3 < length2; i3++) {
                    JSONValue jSONValue = array2[i3];
                    if (!(jSONValue instanceof JSONArray)) {
                        throw new IllegalArgumentException();
                    }
                    parseLineString((JSONArray) jSONValue, target);
                    target.endCollectionItem(target, 5, i3, length2);
                }
                target.endObject(5);
                return;
            case 6:
                JSONValue[] array3 = jSONArray.getArray();
                int length3 = array3.length;
                target.startCollection(6, length3);
                for (int i4 = 0; i4 < length3; i4++) {
                    JSONValue jSONValue2 = array3[i4];
                    if (!(jSONValue2 instanceof JSONArray)) {
                        throw new IllegalArgumentException();
                    }
                    parsePolygon((JSONArray) jSONValue2, target);
                    target.endCollectionItem(target, 6, i4, length3);
                }
                target.endObject(6);
                return;
            default:
                throw new IllegalArgumentException();
        }
    }

    private static void parseGeometryCollection(JSONObject jSONObject, GeometryUtils.Target target) {
        JSONValue first = jSONObject.getFirst("geometries");
        if (!(first instanceof JSONArray)) {
            throw new IllegalArgumentException();
        }
        JSONValue[] array = ((JSONArray) first).getArray();
        int length = array.length;
        target.startCollection(7, length);
        for (int i = 0; i < length; i++) {
            parse(array[i], target);
            target.endCollectionItem(target, 7, i, length);
        }
        target.endObject(7);
    }

    private static void parseLineString(JSONArray jSONArray, GeometryUtils.Target target) {
        JSONValue[] array = jSONArray.getArray();
        int length = array.length;
        target.startLineString(length);
        for (int i = 0; i < length; i++) {
            parseCoordinate(array[i], target, i, length);
        }
        target.endObject(2);
    }

    private static void parsePolygon(JSONArray jSONArray, GeometryUtils.Target target) {
        JSONValue[] array = jSONArray.getArray();
        int length = array.length;
        if (length == 0) {
            target.startPolygon(0, 0);
        } else {
            JSONValue jSONValue = array[0];
            if (!(jSONValue instanceof JSONArray)) {
                throw new IllegalArgumentException();
            }
            JSONValue[] array2 = ((JSONArray) jSONValue).getArray();
            target.startPolygon(length - 1, array2.length);
            parseRing(array2, target);
            for (int i = 1; i < length; i++) {
                JSONValue jSONValue2 = array[i];
                if (!(jSONValue2 instanceof JSONArray)) {
                    throw new IllegalArgumentException();
                }
                JSONValue[] array3 = ((JSONArray) jSONValue2).getArray();
                target.startPolygonInner(array3.length);
                parseRing(array3, target);
            }
            target.endNonEmptyPolygon();
        }
        target.endObject(3);
    }

    private static void parseRing(JSONValue[] jSONValueArr, GeometryUtils.Target target) {
        int length = jSONValueArr.length;
        for (int i = 0; i < length; i++) {
            parseCoordinate(jSONValueArr[i], target, i, length);
        }
    }

    private static void parseCoordinate(JSONValue jSONValue, GeometryUtils.Target target, int i, int i2) {
        if (jSONValue instanceof JSONNull) {
            target.addCoordinate(Double.NaN, Double.NaN, Double.NaN, Double.NaN, 0, 1);
        } else {
            if (!(jSONValue instanceof JSONArray)) {
                throw new IllegalArgumentException();
            }
            JSONValue[] array = ((JSONArray) jSONValue).getArray();
            if (array.length < 2) {
                throw new IllegalArgumentException();
            }
            target.addCoordinate(readCoordinate(array, 0), readCoordinate(array, 1), readCoordinate(array, 2), readCoordinate(array, 3), i, i2);
        }
    }

    private static double readCoordinate(JSONValue[] jSONValueArr, int i) {
        if (i >= jSONValueArr.length) {
            return Double.NaN;
        }
        JSONValue jSONValue = jSONValueArr[i];
        if (!(jSONValue instanceof JSONNumber)) {
            throw new IllegalArgumentException();
        }
        return ((JSONNumber) jSONValue).getBigDecimal().doubleValue();
    }

    private GeoJsonUtils() {
    }
}
