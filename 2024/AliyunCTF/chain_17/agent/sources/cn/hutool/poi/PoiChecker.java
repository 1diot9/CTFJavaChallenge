package cn.hutool.poi;

import cn.hutool.core.exceptions.DependencyException;
import cn.hutool.core.util.ClassLoaderUtil;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/poi/PoiChecker.class */
public class PoiChecker {
    public static final String NO_POI_ERROR_MSG = "You need to add dependency of 'poi-ooxml' to your project, and version >= 4.1.2";

    public static void checkPoiImport() {
        try {
            Class.forName("org.apache.poi.ss.usermodel.Workbook", false, ClassLoaderUtil.getClassLoader());
        } catch (ClassNotFoundException | NoClassDefFoundError | NoSuchMethodError e) {
            throw new DependencyException(e, NO_POI_ERROR_MSG, new Object[0]);
        }
    }
}
