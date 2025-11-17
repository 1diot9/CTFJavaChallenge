package cn.hutool.core.img;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/img/ColorUtil.class */
public class ColorUtil {
    private static final int RGB_COLOR_BOUND = 256;

    public static String toHex(Color color) {
        return toHex(color.getRed(), color.getGreen(), color.getBlue());
    }

    public static String toHex(int r, int g, int b) {
        if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255) {
            throw new IllegalArgumentException("RGB must be 0~255!");
        }
        return String.format("#%02X%02X%02X", Integer.valueOf(r), Integer.valueOf(g), Integer.valueOf(b));
    }

    public static Color getColor(String colorName) {
        if (StrUtil.isBlank(colorName)) {
            return null;
        }
        String colorName2 = colorName.toUpperCase();
        if ("BLACK".equals(colorName2)) {
            return Color.BLACK;
        }
        if ("WHITE".equals(colorName2)) {
            return Color.WHITE;
        }
        if ("LIGHTGRAY".equals(colorName2) || "LIGHT_GRAY".equals(colorName2)) {
            return Color.LIGHT_GRAY;
        }
        if ("GRAY".equals(colorName2)) {
            return Color.GRAY;
        }
        if ("DARKGRAY".equals(colorName2) || "DARK_GRAY".equals(colorName2)) {
            return Color.DARK_GRAY;
        }
        if ("RED".equals(colorName2)) {
            return Color.RED;
        }
        if ("PINK".equals(colorName2)) {
            return Color.PINK;
        }
        if ("ORANGE".equals(colorName2)) {
            return Color.ORANGE;
        }
        if ("YELLOW".equals(colorName2)) {
            return Color.YELLOW;
        }
        if ("GREEN".equals(colorName2)) {
            return Color.GREEN;
        }
        if ("MAGENTA".equals(colorName2)) {
            return Color.MAGENTA;
        }
        if ("CYAN".equals(colorName2)) {
            return Color.CYAN;
        }
        if ("BLUE".equals(colorName2)) {
            return Color.BLUE;
        }
        if ("DARKGOLD".equals(colorName2)) {
            return hexToColor("#9e7e67");
        }
        if ("LIGHTGOLD".equals(colorName2)) {
            return hexToColor("#ac9c85");
        }
        if (StrUtil.startWith((CharSequence) colorName2, '#')) {
            return hexToColor(colorName2);
        }
        if (StrUtil.startWith((CharSequence) colorName2, '$')) {
            return hexToColor("#" + colorName2.substring(1));
        }
        List<String> rgb = StrUtil.split((CharSequence) colorName2, ',');
        if (3 == rgb.size()) {
            Integer r = Convert.toInt(rgb.get(0));
            Integer g = Convert.toInt(rgb.get(1));
            Integer b = Convert.toInt(rgb.get(2));
            if (false == ArrayUtil.hasNull(r, g, b)) {
                return new Color(r.intValue(), g.intValue(), b.intValue());
            }
            return null;
        }
        return null;
    }

    public static Color getColor(int rgb) {
        return new Color(rgb);
    }

    public static Color hexToColor(String hex) {
        return getColor(Integer.parseInt(StrUtil.removePrefix(hex, "#"), 16));
    }

    public static Color add(Color color1, Color color2) {
        double r1 = color1.getRed();
        double g1 = color1.getGreen();
        double b1 = color1.getBlue();
        double a1 = color1.getAlpha();
        double r2 = color2.getRed();
        double g2 = color2.getGreen();
        double b2 = color2.getBlue();
        double a2 = color2.getAlpha();
        int r = (int) ((((r1 * a1) / 255.0d) + ((r2 * a2) / 255.0d)) / ((a1 / 255.0d) + (a2 / 255.0d)));
        int g = (int) ((((g1 * a1) / 255.0d) + ((g2 * a2) / 255.0d)) / ((a1 / 255.0d) + (a2 / 255.0d)));
        int b = (int) ((((b1 * a1) / 255.0d) + ((b2 * a2) / 255.0d)) / ((a1 / 255.0d) + (a2 / 255.0d)));
        return new Color(r, g, b);
    }

    public static Color randomColor() {
        return randomColor(null);
    }

    public static Color randomColor(Random random) {
        if (null == random) {
            random = RandomUtil.getRandom();
        }
        return new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    public static String getMainColor(BufferedImage image, int[]... rgbFilters) {
        Map<String, Long> countMap = new HashMap<>();
        int width = image.getWidth();
        int height = image.getHeight();
        int minx = image.getMinX();
        int miny = image.getMinY();
        for (int i = minx; i < width; i++) {
            for (int j = miny; j < height; j++) {
                int pixel = image.getRGB(i, j);
                int r = (pixel & 16711680) >> 16;
                int g = (pixel & 65280) >> 8;
                int b = pixel & Const.MAX_ARRAY_DIMENSIONS;
                if (!matchFilters(r, g, b, rgbFilters)) {
                    countMap.merge(r + "-" + g + "-" + b, 1L, (v0, v1) -> {
                        return Long.sum(v0, v1);
                    });
                }
            }
        }
        String maxColor = null;
        long maxCount = 0;
        for (Map.Entry<String, Long> entry : countMap.entrySet()) {
            String key = entry.getKey();
            Long count = entry.getValue();
            if (count.longValue() > maxCount) {
                maxColor = key;
                maxCount = count.longValue();
            }
        }
        String[] splitRgbStr = StrUtil.splitToArray((CharSequence) maxColor, '-');
        String rHex = Integer.toHexString(Integer.parseInt(splitRgbStr[0]));
        String gHex = Integer.toHexString(Integer.parseInt(splitRgbStr[1]));
        String bHex = Integer.toHexString(Integer.parseInt(splitRgbStr[2]));
        return "#" + (rHex.length() == 1 ? CustomBooleanEditor.VALUE_0 + rHex : rHex) + (gHex.length() == 1 ? CustomBooleanEditor.VALUE_0 + gHex : gHex) + (bHex.length() == 1 ? CustomBooleanEditor.VALUE_0 + bHex : bHex);
    }

    private static boolean matchFilters(int r, int g, int b, int[]... rgbFilters) {
        if (rgbFilters != null && rgbFilters.length > 0) {
            for (int[] rgbFilter : rgbFilters) {
                if (r == rgbFilter[0] && g == rgbFilter[1] && b == rgbFilter[2]) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }
}
