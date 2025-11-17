package org.yaml.snakeyaml.scanner;

import java.util.Arrays;

/* loaded from: server.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/scanner/Constant.class */
public final class Constant {
    private static final String FULL_LINEBR_S = "\r\n\u0085\u2028\u2029";
    private String content;
    boolean[] contains = new boolean[128];
    boolean noASCII;
    private static final String LINEBR_S = "\n\u0085\u2028\u2029";
    public static final Constant LINEBR = new Constant(LINEBR_S);
    private static final String NULL_OR_LINEBR_S = "��\r\n\u0085\u2028\u2029";
    public static final Constant NULL_OR_LINEBR = new Constant(NULL_OR_LINEBR_S);
    private static final String NULL_BL_LINEBR_S = " ��\r\n\u0085\u2028\u2029";
    public static final Constant NULL_BL_LINEBR = new Constant(NULL_BL_LINEBR_S);
    private static final String NULL_BL_T_LINEBR_S = "\t ��\r\n\u0085\u2028\u2029";
    public static final Constant NULL_BL_T_LINEBR = new Constant(NULL_BL_T_LINEBR_S);
    private static final String NULL_BL_T_S = "�� \t";
    public static final Constant NULL_BL_T = new Constant(NULL_BL_T_S);
    private static final String URI_CHARS_S = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-_-;/?:@&=+$,_.!~*'()[]%";
    public static final Constant URI_CHARS = new Constant(URI_CHARS_S);
    private static final String ALPHA_S = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-_";
    public static final Constant ALPHA = new Constant(ALPHA_S);

    private Constant(String content) {
        this.noASCII = false;
        Arrays.fill(this.contains, false);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < content.length(); i++) {
            int c = content.codePointAt(i);
            if (c < 128) {
                this.contains[c] = true;
            } else {
                sb.appendCodePoint(c);
            }
        }
        if (sb.length() > 0) {
            this.noASCII = true;
            this.content = sb.toString();
        }
    }

    public boolean has(int c) {
        return c < 128 ? this.contains[c] : this.noASCII && this.content.indexOf(c) != -1;
    }

    public boolean hasNo(int c) {
        return !has(c);
    }

    public boolean has(int c, String additional) {
        return has(c) || additional.indexOf(c) != -1;
    }

    public boolean hasNo(int c, String additional) {
        return !has(c, additional);
    }
}
