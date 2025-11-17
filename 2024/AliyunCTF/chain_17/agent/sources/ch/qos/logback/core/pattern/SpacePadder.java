package ch.qos.logback.core.pattern;

import cn.hutool.core.text.CharSequenceUtil;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/pattern/SpacePadder.class */
public class SpacePadder {
    static final String[] SPACES = {CharSequenceUtil.SPACE, "  ", "    ", "        ", "                ", "                                "};

    public static final void leftPad(StringBuilder buf, String s, int desiredLength) {
        int actualLen = 0;
        if (s != null) {
            actualLen = s.length();
        }
        if (actualLen < desiredLength) {
            spacePad(buf, desiredLength - actualLen);
        }
        if (s != null) {
            buf.append(s);
        }
    }

    public static final void rightPad(StringBuilder buf, String s, int desiredLength) {
        int actualLen = 0;
        if (s != null) {
            actualLen = s.length();
        }
        if (s != null) {
            buf.append(s);
        }
        if (actualLen < desiredLength) {
            spacePad(buf, desiredLength - actualLen);
        }
    }

    public static final void spacePad(StringBuilder sbuf, int length) {
        while (length >= 32) {
            sbuf.append(SPACES[5]);
            length -= 32;
        }
        for (int i = 4; i >= 0; i--) {
            if ((length & (1 << i)) != 0) {
                sbuf.append(SPACES[i]);
            }
        }
    }
}
