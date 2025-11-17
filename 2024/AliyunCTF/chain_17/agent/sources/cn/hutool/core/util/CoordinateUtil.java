package cn.hutool.core.util;

import java.io.Serializable;
import java.util.Objects;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/util/CoordinateUtil.class */
public class CoordinateUtil {
    public static final double X_PI = 52.35987755982988d;
    public static final double PI = 3.141592653589793d;
    public static final double RADIUS = 6378245.0d;
    public static final double CORRECTION_PARAM = 0.006693421622965943d;

    public static boolean outOfChina(double lng, double lat) {
        return lng < 72.004d || lng > 137.8347d || lat < 0.8293d || lat > 55.8271d;
    }

    public static Coordinate wgs84ToGcj02(double lng, double lat) {
        return new Coordinate(lng, lat).offset(offset(lng, lat, true));
    }

    public static Coordinate wgs84ToBd09(double lng, double lat) {
        Coordinate gcj02 = wgs84ToGcj02(lng, lat);
        return gcj02ToBd09(gcj02.lng, gcj02.lat);
    }

    public static Coordinate gcj02ToWgs84(double lng, double lat) {
        return new Coordinate(lng, lat).offset(offset(lng, lat, false));
    }

    public static Coordinate gcj02ToBd09(double lng, double lat) {
        double z = Math.sqrt((lng * lng) + (lat * lat)) + (2.0E-5d * Math.sin(lat * 52.35987755982988d));
        double theta = Math.atan2(lat, lng) + (3.0E-6d * Math.cos(lng * 52.35987755982988d));
        double bd_lng = (z * Math.cos(theta)) + 0.0065d;
        double bd_lat = (z * Math.sin(theta)) + 0.006d;
        return new Coordinate(bd_lng, bd_lat);
    }

    public static Coordinate bd09ToGcj02(double lng, double lat) {
        double x = lng - 0.0065d;
        double y = lat - 0.006d;
        double z = Math.sqrt((x * x) + (y * y)) - (2.0E-5d * Math.sin(y * 52.35987755982988d));
        double theta = Math.atan2(y, x) - (3.0E-6d * Math.cos(x * 52.35987755982988d));
        double gg_lng = z * Math.cos(theta);
        double gg_lat = z * Math.sin(theta);
        return new Coordinate(gg_lng, gg_lat);
    }

    public static Coordinate bd09toWgs84(double lng, double lat) {
        Coordinate gcj02 = bd09ToGcj02(lng, lat);
        return gcj02ToWgs84(gcj02.lng, gcj02.lat);
    }

    public static Coordinate wgs84ToMercator(double lng, double lat) {
        double x = (lng * 2.0037508342789244E7d) / 180.0d;
        double y = Math.log(Math.tan(((90.0d + lat) * 3.141592653589793d) / 360.0d)) / 0.017453292519943295d;
        return new Coordinate(x, (y * 2.0037508342789244E7d) / 180.0d);
    }

    public static Coordinate mercatorToWgs84(double mercatorX, double mercatorY) {
        double x = (mercatorX / 2.0037508342789244E7d) * 180.0d;
        double y = (mercatorY / 2.0037508342789244E7d) * 180.0d;
        return new Coordinate(x, 57.29577951308232d * ((2.0d * Math.atan(Math.exp((y * 3.141592653589793d) / 180.0d))) - 1.5707963267948966d));
    }

    private static Coordinate offset(double lng, double lat, boolean isPlus) {
        double dlng = transLng(lng - 105.0d, lat - 35.0d);
        double dlat = transLat(lng - 105.0d, lat - 35.0d);
        double magic = Math.sin((lat / 180.0d) * 3.141592653589793d);
        double magic2 = 1.0d - ((0.006693421622965943d * magic) * magic);
        double sqrtMagic = Math.sqrt(magic2);
        double dlng2 = (dlng * 180.0d) / (((6378245.0d / sqrtMagic) * Math.cos((lat / 180.0d) * 3.141592653589793d)) * 3.141592653589793d);
        double dlat2 = (dlat * 180.0d) / ((6335552.717000426d / (magic2 * sqrtMagic)) * 3.141592653589793d);
        if (false == isPlus) {
            dlng2 = -dlng2;
            dlat2 = -dlat2;
        }
        return new Coordinate(dlng2, dlat2);
    }

    private static double transLng(double lng, double lat) {
        double ret = 300.0d + lng + (2.0d * lat) + (0.1d * lng * lng) + (0.1d * lng * lat) + (0.1d * Math.sqrt(Math.abs(lng)));
        return ret + ((((20.0d * Math.sin((6.0d * lng) * 3.141592653589793d)) + (20.0d * Math.sin((2.0d * lng) * 3.141592653589793d))) * 2.0d) / 3.0d) + ((((20.0d * Math.sin(lng * 3.141592653589793d)) + (40.0d * Math.sin((lng / 3.0d) * 3.141592653589793d))) * 2.0d) / 3.0d) + ((((150.0d * Math.sin((lng / 12.0d) * 3.141592653589793d)) + (300.0d * Math.sin((lng / 30.0d) * 3.141592653589793d))) * 2.0d) / 3.0d);
    }

    private static double transLat(double lng, double lat) {
        double ret = (-100.0d) + (2.0d * lng) + (3.0d * lat) + (0.2d * lat * lat) + (0.1d * lng * lat) + (0.2d * Math.sqrt(Math.abs(lng)));
        return ret + ((((20.0d * Math.sin((6.0d * lng) * 3.141592653589793d)) + (20.0d * Math.sin((2.0d * lng) * 3.141592653589793d))) * 2.0d) / 3.0d) + ((((20.0d * Math.sin(lat * 3.141592653589793d)) + (40.0d * Math.sin((lat / 3.0d) * 3.141592653589793d))) * 2.0d) / 3.0d) + ((((160.0d * Math.sin((lat / 12.0d) * 3.141592653589793d)) + (320.0d * Math.sin((lat * 3.141592653589793d) / 30.0d))) * 2.0d) / 3.0d);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/util/CoordinateUtil$Coordinate.class */
    public static class Coordinate implements Serializable {
        private static final long serialVersionUID = 1;
        private double lng;
        private double lat;

        public Coordinate(double lng, double lat) {
            this.lng = lng;
            this.lat = lat;
        }

        public double getLng() {
            return this.lng;
        }

        public Coordinate setLng(double lng) {
            this.lng = lng;
            return this;
        }

        public double getLat() {
            return this.lat;
        }

        public Coordinate setLat(double lat) {
            this.lat = lat;
            return this;
        }

        public Coordinate offset(Coordinate offset) {
            this.lng += offset.lng;
            this.lat += offset.lat;
            return this;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Coordinate that = (Coordinate) o;
            return Double.compare(that.lng, this.lng) == 0 && Double.compare(that.lat, this.lat) == 0;
        }

        public int hashCode() {
            return Objects.hash(Double.valueOf(this.lng), Double.valueOf(this.lat));
        }

        public String toString() {
            return "Coordinate{lng=" + this.lng + ", lat=" + this.lat + '}';
        }
    }
}
