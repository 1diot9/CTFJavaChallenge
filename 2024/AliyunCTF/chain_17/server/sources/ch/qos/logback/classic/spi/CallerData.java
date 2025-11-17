package ch.qos.logback.classic.spi;

import ch.qos.logback.core.CoreConstants;
import java.util.List;

/* loaded from: server.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/spi/CallerData.class */
public class CallerData {
    private static final String LOG4J_CATEGORY = "org.apache.log4j.Category";
    private static final String SLF4J_BOUNDARY = "org.slf4j.Logger";
    public static final int LINE_NA = -1;
    public static final String CALLER_DATA_NA = "?#?:?" + CoreConstants.LINE_SEPARATOR;
    public static final StackTraceElement[] EMPTY_CALLER_DATA_ARRAY = new StackTraceElement[0];

    public static StackTraceElement[] extract(Throwable t, String fqnOfInvokingClass, int maxDepth, List<String> frameworkPackageList) {
        if (t == null) {
            return null;
        }
        StackTraceElement[] steArray = t.getStackTrace();
        int found = -1;
        for (int i = 0; i < steArray.length; i++) {
            if (isInFrameworkSpace(steArray[i].getClassName(), fqnOfInvokingClass, frameworkPackageList)) {
                found = i + 1;
            } else if (found != -1) {
                break;
            }
        }
        if (found == -1) {
            return EMPTY_CALLER_DATA_ARRAY;
        }
        int availableDepth = steArray.length - found;
        int desiredDepth = maxDepth < availableDepth ? maxDepth : availableDepth;
        StackTraceElement[] callerDataArray = new StackTraceElement[desiredDepth];
        for (int i2 = 0; i2 < desiredDepth; i2++) {
            callerDataArray[i2] = steArray[found + i2];
        }
        return callerDataArray;
    }

    static boolean isInFrameworkSpace(String currentClass, String fqnOfInvokingClass, List<String> frameworkPackageList) {
        if (currentClass.equals(fqnOfInvokingClass) || currentClass.equals(LOG4J_CATEGORY) || currentClass.startsWith(SLF4J_BOUNDARY) || isInFrameworkSpaceList(currentClass, frameworkPackageList)) {
            return true;
        }
        return false;
    }

    private static boolean isInFrameworkSpaceList(String currentClass, List<String> frameworkPackageList) {
        if (frameworkPackageList == null) {
            return false;
        }
        for (String s : frameworkPackageList) {
            if (currentClass.startsWith(s)) {
                return true;
            }
        }
        return false;
    }

    public static StackTraceElement naInstance() {
        return new StackTraceElement(CoreConstants.NA, CoreConstants.NA, CoreConstants.NA, -1);
    }
}
