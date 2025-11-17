package cn.hutool.core.lang.ansi;

import ch.qos.logback.core.pattern.color.ANSIConstants;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/ansi/AnsiEncoder.class */
public abstract class AnsiEncoder {
    private static final String ENCODE_JOIN = ";";
    private static final String ENCODE_START = "\u001b[";
    private static final String ENCODE_END = "m";
    private static final String RESET = ANSIConstants.RESET + AnsiColor.DEFAULT;

    public static String encode(Object... elements) {
        StringBuilder sb = new StringBuilder();
        buildEnabled(sb, elements);
        return sb.toString();
    }

    private static void buildEnabled(StringBuilder sb, Object[] elements) {
        boolean writingAnsi = false;
        boolean containsEncoding = false;
        for (Object element : elements) {
            if (null != element) {
                if (element instanceof AnsiElement) {
                    containsEncoding = true;
                    if (writingAnsi) {
                        sb.append(ENCODE_JOIN);
                    } else {
                        sb.append("\u001b[");
                        writingAnsi = true;
                    }
                } else if (writingAnsi) {
                    sb.append("m");
                    writingAnsi = false;
                }
                sb.append(element);
            }
        }
        if (containsEncoding) {
            sb.append(writingAnsi ? ENCODE_JOIN : "\u001b[");
            sb.append(RESET);
            sb.append("m");
        }
    }
}
