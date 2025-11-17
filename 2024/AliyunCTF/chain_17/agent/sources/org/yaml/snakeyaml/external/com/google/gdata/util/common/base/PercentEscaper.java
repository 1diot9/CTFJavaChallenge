package org.yaml.snakeyaml.external.com.google.gdata.util.common.base;

import cn.hutool.core.text.CharSequenceUtil;
import org.springframework.aop.framework.autoproxy.target.QuickTargetSourceCreator;

/* loaded from: agent.jar:BOOT-INF/lib/snakeyaml-2.2.jar:org/yaml/snakeyaml/external/com/google/gdata/util/common/base/PercentEscaper.class */
public class PercentEscaper extends UnicodeEscaper {
    public static final String SAFECHARS_URLENCODER = "-_.*";
    public static final String SAFEPATHCHARS_URLENCODER = "-_.!~*'()@:$&,;=";
    public static final String SAFEQUERYSTRINGCHARS_URLENCODER = "-_.!~*'()@:$,;/?:";
    private static final char[] URI_ESCAPED_SPACE = {'+'};
    private static final char[] UPPER_HEX_DIGITS = "0123456789ABCDEF".toCharArray();
    private final boolean plusForSpace;
    private final boolean[] safeOctets;

    public PercentEscaper(String safeChars, boolean plusForSpace) {
        if (safeChars.matches(".*[0-9A-Za-z].*")) {
            throw new IllegalArgumentException("Alphanumeric characters are always 'safe' and should not be explicitly specified");
        }
        if (plusForSpace && safeChars.contains(CharSequenceUtil.SPACE)) {
            throw new IllegalArgumentException("plusForSpace cannot be specified when space is a 'safe' character");
        }
        if (safeChars.contains(QuickTargetSourceCreator.PREFIX_THREAD_LOCAL)) {
            throw new IllegalArgumentException("The '%' character cannot be specified as 'safe'");
        }
        this.plusForSpace = plusForSpace;
        this.safeOctets = createSafeOctets(safeChars);
    }

    private static boolean[] createSafeOctets(String safeChars) {
        int maxChar = 122;
        char[] safeCharArray = safeChars.toCharArray();
        for (char c : safeCharArray) {
            maxChar = Math.max((int) c, maxChar);
        }
        boolean[] octets = new boolean[maxChar + 1];
        for (int c2 = 48; c2 <= 57; c2++) {
            octets[c2] = true;
        }
        for (int c3 = 65; c3 <= 90; c3++) {
            octets[c3] = true;
        }
        for (int c4 = 97; c4 <= 122; c4++) {
            octets[c4] = true;
        }
        for (char c5 : safeCharArray) {
            octets[c5] = true;
        }
        return octets;
    }

    @Override // org.yaml.snakeyaml.external.com.google.gdata.util.common.base.UnicodeEscaper
    protected int nextEscapeIndex(CharSequence csq, int index, int end) {
        char c;
        while (index < end && (c = csq.charAt(index)) < this.safeOctets.length && this.safeOctets[c]) {
            index++;
        }
        return index;
    }

    @Override // org.yaml.snakeyaml.external.com.google.gdata.util.common.base.UnicodeEscaper, org.yaml.snakeyaml.external.com.google.gdata.util.common.base.Escaper
    public String escape(String s) {
        int slen = s.length();
        for (int index = 0; index < slen; index++) {
            char c = s.charAt(index);
            if (c >= this.safeOctets.length || !this.safeOctets[c]) {
                return escapeSlow(s, index);
            }
        }
        return s;
    }

    @Override // org.yaml.snakeyaml.external.com.google.gdata.util.common.base.UnicodeEscaper
    protected char[] escape(int cp) {
        if (cp < this.safeOctets.length && this.safeOctets[cp]) {
            return null;
        }
        if (cp == 32 && this.plusForSpace) {
            return URI_ESCAPED_SPACE;
        }
        if (cp <= 127) {
            char[] dest = {'%', UPPER_HEX_DIGITS[cp >>> 4], UPPER_HEX_DIGITS[cp & 15]};
            return dest;
        }
        if (cp <= 2047) {
            char[] dest2 = {'%', UPPER_HEX_DIGITS[12 | (cp >>> 4)], UPPER_HEX_DIGITS[cp & 15], '%', UPPER_HEX_DIGITS[8 | (cp & 3)], UPPER_HEX_DIGITS[cp & 15]};
            int cp2 = cp >>> 4;
            int cp3 = cp2 >>> 2;
            return dest2;
        }
        if (cp <= 65535) {
            char[] dest3 = {'%', 'E', r2[cp >>> 2], '%', UPPER_HEX_DIGITS[8 | (cp & 3)], UPPER_HEX_DIGITS[cp & 15], '%', UPPER_HEX_DIGITS[8 | (cp & 3)], UPPER_HEX_DIGITS[cp & 15]};
            int cp4 = cp >>> 4;
            int cp5 = cp4 >>> 2;
            int cp6 = cp5 >>> 4;
            char[] cArr = UPPER_HEX_DIGITS;
            return dest3;
        }
        if (cp <= 1114111) {
            char[] dest4 = {'%', 'F', UPPER_HEX_DIGITS[(cp >>> 2) & 7], '%', UPPER_HEX_DIGITS[8 | (cp & 3)], UPPER_HEX_DIGITS[cp & 15], '%', UPPER_HEX_DIGITS[8 | (cp & 3)], UPPER_HEX_DIGITS[cp & 15], '%', UPPER_HEX_DIGITS[8 | (cp & 3)], UPPER_HEX_DIGITS[cp & 15]};
            int cp7 = cp >>> 4;
            int cp8 = cp7 >>> 2;
            int cp9 = cp8 >>> 4;
            int cp10 = cp9 >>> 2;
            int cp11 = cp10 >>> 4;
            return dest4;
        }
        throw new IllegalArgumentException("Invalid unicode character value " + cp);
    }
}
