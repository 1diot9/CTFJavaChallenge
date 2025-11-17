package ch.qos.logback.core.helpers;

import ch.qos.logback.core.CoreConstants;
import java.util.LinkedList;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/helpers/ThrowableToStringArray.class */
public class ThrowableToStringArray {
    public static String[] convert(Throwable t) {
        List<String> strList = new LinkedList<>();
        extract(strList, t, null);
        return (String[]) strList.toArray(new String[0]);
    }

    private static void extract(List<String> strList, Throwable t, StackTraceElement[] parentSTE) {
        StackTraceElement[] ste = t.getStackTrace();
        int numberOfcommonFrames = findNumberOfCommonFrames(ste, parentSTE);
        strList.add(formatFirstLine(t, parentSTE));
        for (int i = 0; i < ste.length - numberOfcommonFrames; i++) {
            strList.add("\tat " + ste[i].toString());
        }
        if (numberOfcommonFrames != 0) {
            strList.add("\t... " + numberOfcommonFrames + " common frames omitted");
        }
        Throwable cause = t.getCause();
        if (cause != null) {
            extract(strList, cause, ste);
        }
    }

    private static String formatFirstLine(Throwable t, StackTraceElement[] parentSTE) {
        String prefix = "";
        if (parentSTE != null) {
            prefix = CoreConstants.CAUSED_BY;
        }
        String result = prefix + t.getClass().getName();
        if (t.getMessage() != null) {
            result = result + ": " + t.getMessage();
        }
        return result;
    }

    private static int findNumberOfCommonFrames(StackTraceElement[] ste, StackTraceElement[] parentSTE) {
        if (parentSTE == null) {
            return 0;
        }
        int steIndex = ste.length - 1;
        int count = 0;
        for (int parentIndex = parentSTE.length - 1; steIndex >= 0 && parentIndex >= 0 && ste[steIndex].equals(parentSTE[parentIndex]); parentIndex--) {
            count++;
            steIndex--;
        }
        return count;
    }
}
