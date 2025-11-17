package ch.qos.logback.core.pattern;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/pattern/FormatInfo.class */
public class FormatInfo {
    private int min;
    private int max;
    private boolean leftPad;
    private boolean leftTruncate;

    public FormatInfo() {
        this.min = Integer.MIN_VALUE;
        this.max = Integer.MAX_VALUE;
        this.leftPad = true;
        this.leftTruncate = true;
    }

    public FormatInfo(int min, int max) {
        this.min = Integer.MIN_VALUE;
        this.max = Integer.MAX_VALUE;
        this.leftPad = true;
        this.leftTruncate = true;
        this.min = min;
        this.max = max;
    }

    public FormatInfo(int min, int max, boolean leftPad, boolean leftTruncate) {
        this.min = Integer.MIN_VALUE;
        this.max = Integer.MAX_VALUE;
        this.leftPad = true;
        this.leftTruncate = true;
        this.min = min;
        this.max = max;
        this.leftPad = leftPad;
        this.leftTruncate = leftTruncate;
    }

    public static FormatInfo valueOf(String str) throws IllegalArgumentException {
        String minPart;
        if (str == null) {
            throw new NullPointerException("Argument cannot be null");
        }
        FormatInfo fi = new FormatInfo();
        int indexOfDot = str.indexOf(46);
        String maxPart = null;
        if (indexOfDot != -1) {
            minPart = str.substring(0, indexOfDot);
            if (indexOfDot + 1 == str.length()) {
                throw new IllegalArgumentException("Formatting string [" + str + "] should not end with '.'");
            }
            maxPart = str.substring(indexOfDot + 1);
        } else {
            minPart = str;
        }
        if (minPart != null && minPart.length() > 0) {
            int min = Integer.parseInt(minPart);
            if (min >= 0) {
                fi.min = min;
            } else {
                fi.min = -min;
                fi.leftPad = false;
            }
        }
        if (maxPart != null && maxPart.length() > 0) {
            int max = Integer.parseInt(maxPart);
            if (max >= 0) {
                fi.max = max;
            } else {
                fi.max = -max;
                fi.leftTruncate = false;
            }
        }
        return fi;
    }

    public boolean isLeftPad() {
        return this.leftPad;
    }

    public void setLeftPad(boolean leftAlign) {
        this.leftPad = leftAlign;
    }

    public int getMax() {
        return this.max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getMin() {
        return this.min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public boolean isLeftTruncate() {
        return this.leftTruncate;
    }

    public void setLeftTruncate(boolean leftTruncate) {
        this.leftTruncate = leftTruncate;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FormatInfo)) {
            return false;
        }
        FormatInfo r = (FormatInfo) o;
        return this.min == r.min && this.max == r.max && this.leftPad == r.leftPad && this.leftTruncate == r.leftTruncate;
    }

    public int hashCode() {
        int result = this.min;
        return (31 * ((31 * ((31 * result) + this.max)) + (this.leftPad ? 1 : 0))) + (this.leftTruncate ? 1 : 0);
    }

    public String toString() {
        return "FormatInfo(" + this.min + ", " + this.max + ", " + this.leftPad + ", " + this.leftTruncate + ")";
    }
}
