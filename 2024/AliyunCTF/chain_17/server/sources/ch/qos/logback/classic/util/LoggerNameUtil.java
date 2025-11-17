package ch.qos.logback.classic.util;

import java.util.ArrayList;
import java.util.List;

/* loaded from: server.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/util/LoggerNameUtil.class */
public class LoggerNameUtil {
    public static int getFirstSeparatorIndexOf(String name) {
        return getSeparatorIndexOf(name, 0);
    }

    public static int getSeparatorIndexOf(String name, int fromIndex) {
        int dotIndex = name.indexOf(46, fromIndex);
        int dollarIndex = name.indexOf(36, fromIndex);
        if (dotIndex == -1 && dollarIndex == -1) {
            return -1;
        }
        if (dotIndex == -1) {
            return dollarIndex;
        }
        if (dollarIndex == -1) {
            return dotIndex;
        }
        return dotIndex < dollarIndex ? dotIndex : dollarIndex;
    }

    public static List<String> computeNameParts(String loggerName) {
        List<String> partList = new ArrayList<>();
        int i = 0;
        while (true) {
            int fromIndex = i;
            int index = getSeparatorIndexOf(loggerName, fromIndex);
            if (index == -1) {
                partList.add(loggerName.substring(fromIndex));
                return partList;
            }
            partList.add(loggerName.substring(fromIndex, index));
            i = index + 1;
        }
    }
}
