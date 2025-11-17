package org.springframework.boot.ansi;

import java.util.Locale;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/ansi/AnsiOutput.class */
public abstract class AnsiOutput {
    private static final String ENCODE_JOIN = ";";
    private static Boolean consoleAvailable;
    private static Boolean ansiCapable;
    private static final String ENCODE_START = "\u001b[";
    private static final String ENCODE_END = "m";
    private static Enabled enabled = Enabled.DETECT;
    private static final String OPERATING_SYSTEM_NAME = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
    private static final String RESET = "0;" + AnsiColor.DEFAULT;

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/ansi/AnsiOutput$Enabled.class */
    public enum Enabled {
        DETECT,
        ALWAYS,
        NEVER
    }

    public static void setEnabled(Enabled enabled2) {
        Assert.notNull(enabled2, "Enabled must not be null");
        enabled = enabled2;
    }

    public static Enabled getEnabled() {
        return enabled;
    }

    public static void setConsoleAvailable(Boolean consoleAvailable2) {
        consoleAvailable = consoleAvailable2;
    }

    public static String encode(AnsiElement element) {
        if (isEnabled()) {
            return "\u001b[" + element + "m";
        }
        return "";
    }

    public static String toString(Object... elements) {
        StringBuilder sb = new StringBuilder();
        if (isEnabled()) {
            buildEnabled(sb, elements);
        } else {
            buildDisabled(sb, elements);
        }
        return sb.toString();
    }

    private static void buildEnabled(StringBuilder sb, Object[] elements) {
        boolean writingAnsi = false;
        boolean containsEncoding = false;
        for (Object element : elements) {
            if (element instanceof AnsiElement) {
                containsEncoding = true;
                if (!writingAnsi) {
                    sb.append("\u001b[");
                    writingAnsi = true;
                } else {
                    sb.append(ENCODE_JOIN);
                }
            } else if (writingAnsi) {
                sb.append("m");
                writingAnsi = false;
            }
            sb.append(element);
        }
        if (containsEncoding) {
            sb.append(writingAnsi ? ENCODE_JOIN : "\u001b[");
            sb.append(RESET);
            sb.append("m");
        }
    }

    private static void buildDisabled(StringBuilder sb, Object[] elements) {
        for (Object element : elements) {
            if (!(element instanceof AnsiElement) && element != null) {
                sb.append(element);
            }
        }
    }

    private static boolean isEnabled() {
        if (enabled != Enabled.DETECT) {
            return enabled == Enabled.ALWAYS;
        }
        if (ansiCapable == null) {
            ansiCapable = Boolean.valueOf(detectIfAnsiCapable());
        }
        return ansiCapable.booleanValue();
    }

    private static boolean detectIfAnsiCapable() {
        try {
            if (Boolean.FALSE.equals(consoleAvailable)) {
                return false;
            }
            if (consoleAvailable == null && System.console() == null) {
                return false;
            }
            return !OPERATING_SYSTEM_NAME.contains("win");
        } catch (Throwable th) {
            return false;
        }
    }
}
