package cn.hutool.core.img;

import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.io.IORuntimeException;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/img/FontUtil.class */
public class FontUtil {
    public static Font createFont() {
        return new Font((Map) null);
    }

    public static Font createSansSerifFont(int size) {
        return createFont("SansSerif", size);
    }

    public static Font createFont(String name, int size) {
        return new Font(name, 0, size);
    }

    public static Font createFont(File fontFile) {
        try {
            return Font.createFont(0, fontFile);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        } catch (FontFormatException e2) {
            try {
                return Font.createFont(1, fontFile);
            } catch (Exception e3) {
                throw new UtilException((Throwable) e2);
            }
        }
    }

    public static Font createFont(InputStream fontStream) {
        try {
            return Font.createFont(0, fontStream);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        } catch (FontFormatException e2) {
            try {
                return Font.createFont(1, fontStream);
            } catch (Exception e1) {
                throw new UtilException(e1);
            }
        }
    }

    public static Dimension getDimension(FontMetrics metrics, String str) {
        int width = metrics.stringWidth(str);
        int height = (metrics.getAscent() - metrics.getLeading()) - metrics.getDescent();
        return new Dimension(width, height);
    }
}
