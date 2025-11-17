package cn.hutool.core.img;

import cn.hutool.core.lang.Assert;
import java.awt.Color;
import java.awt.color.ColorSpace;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/img/LabColor.class */
public class LabColor {
    private static final ColorSpace XYZ_COLOR_SPACE = ColorSpace.getInstance(1001);
    private final double l;
    private final double a;
    private final double b;

    public LabColor(Integer rgb) {
        this(rgb != null ? new Color(rgb.intValue()) : null);
    }

    public LabColor(Color color) {
        Assert.notNull(color, "Color must not be null", new Object[0]);
        float[] lab = fromXyz(color.getColorComponents(XYZ_COLOR_SPACE, (float[]) null));
        this.l = lab[0];
        this.a = lab[1];
        this.b = lab[2];
    }

    public double getDistance(LabColor other) {
        double c1 = Math.sqrt((this.a * this.a) + (this.b * this.b));
        double deltaC = c1 - Math.sqrt((other.a * other.a) + (other.b * other.b));
        double deltaA = this.a - other.a;
        double deltaB = this.b - other.b;
        double deltaH = Math.sqrt(Math.max(0.0d, ((deltaA * deltaA) + (deltaB * deltaB)) - (deltaC * deltaC)));
        return Math.sqrt(Math.max(0.0d, Math.pow(this.l - other.l, 2.0d) + Math.pow(deltaC / (1.0d + (0.045d * c1)), 2.0d) + Math.pow(deltaH / (1.0d + (0.015d * c1)), 2.0d)));
    }

    private float[] fromXyz(float[] xyz) {
        return fromXyz(xyz[0], xyz[1], xyz[2]);
    }

    private static float[] fromXyz(float x, float y, float z) {
        double l = (f(y) - 16.0d) * 116.0d;
        double a = (f(x) - f(y)) * 500.0d;
        double b = (f(y) - f(z)) * 200.0d;
        return new float[]{(float) l, (float) a, (float) b};
    }

    private static double f(double t) {
        return t > 0.008856451679035631d ? Math.cbrt(t) : (0.3333333333333333d * Math.pow(4.833333333333333d, 2.0d) * t) + 0.13793103448275862d;
    }
}
