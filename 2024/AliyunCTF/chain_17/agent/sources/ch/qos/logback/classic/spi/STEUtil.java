package ch.qos.logback.classic.spi;

/* loaded from: agent.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/spi/STEUtil.class */
public class STEUtil {
    static int UNUSED_findNumberOfCommonFrames(StackTraceElement[] steArray, StackTraceElement[] otherSTEArray) {
        if (otherSTEArray == null) {
            return 0;
        }
        int steIndex = steArray.length - 1;
        int count = 0;
        for (int parentIndex = otherSTEArray.length - 1; steIndex >= 0 && parentIndex >= 0 && steArray[steIndex].equals(otherSTEArray[parentIndex]); parentIndex--) {
            count++;
            steIndex--;
        }
        return count;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int findNumberOfCommonFrames(StackTraceElement[] steArray, StackTraceElementProxy[] otherSTEPArray) {
        if (otherSTEPArray == null) {
            return 0;
        }
        int steIndex = steArray.length - 1;
        int count = 0;
        for (int parentIndex = otherSTEPArray.length - 1; steIndex >= 0 && parentIndex >= 0 && steArray[steIndex].equals(otherSTEPArray[parentIndex].ste); parentIndex--) {
            count++;
            steIndex--;
        }
        return count;
    }
}
