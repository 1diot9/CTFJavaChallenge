package org.apache.catalina.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.BitSet;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/util/URLEncoder.class */
public final class URLEncoder implements Cloneable {
    private static final char[] hexadecimal = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    public static final URLEncoder DEFAULT = new URLEncoder();
    public static final URLEncoder QUERY = new URLEncoder();
    private final BitSet safeCharacters;
    private boolean encodeSpaceAsPlus;

    static {
        DEFAULT.addSafeCharacter('-');
        DEFAULT.addSafeCharacter('.');
        DEFAULT.addSafeCharacter('_');
        DEFAULT.addSafeCharacter('~');
        DEFAULT.addSafeCharacter('!');
        DEFAULT.addSafeCharacter('$');
        DEFAULT.addSafeCharacter('&');
        DEFAULT.addSafeCharacter('\'');
        DEFAULT.addSafeCharacter('(');
        DEFAULT.addSafeCharacter(')');
        DEFAULT.addSafeCharacter('*');
        DEFAULT.addSafeCharacter('+');
        DEFAULT.addSafeCharacter(',');
        DEFAULT.addSafeCharacter(';');
        DEFAULT.addSafeCharacter('=');
        DEFAULT.addSafeCharacter(':');
        DEFAULT.addSafeCharacter('@');
        DEFAULT.addSafeCharacter('/');
        QUERY.setEncodeSpaceAsPlus(true);
        QUERY.addSafeCharacter('*');
        QUERY.addSafeCharacter('-');
        QUERY.addSafeCharacter('.');
        QUERY.addSafeCharacter('_');
        QUERY.addSafeCharacter('=');
        QUERY.addSafeCharacter('&');
    }

    public URLEncoder() {
        this(new BitSet(256));
        char c = 'a';
        while (true) {
            char i = c;
            if (i > 'z') {
                break;
            }
            addSafeCharacter(i);
            c = (char) (i + 1);
        }
        char c2 = 'A';
        while (true) {
            char i2 = c2;
            if (i2 > 'Z') {
                break;
            }
            addSafeCharacter(i2);
            c2 = (char) (i2 + 1);
        }
        char c3 = '0';
        while (true) {
            char i3 = c3;
            if (i3 <= '9') {
                addSafeCharacter(i3);
                c3 = (char) (i3 + 1);
            } else {
                return;
            }
        }
    }

    private URLEncoder(BitSet safeCharacters) {
        this.encodeSpaceAsPlus = false;
        this.safeCharacters = safeCharacters;
    }

    public void addSafeCharacter(char c) {
        this.safeCharacters.set(c);
    }

    public void removeSafeCharacter(char c) {
        this.safeCharacters.clear(c);
    }

    public void setEncodeSpaceAsPlus(boolean encodeSpaceAsPlus) {
        this.encodeSpaceAsPlus = encodeSpaceAsPlus;
    }

    public String encode(String path, Charset charset) {
        StringBuilder rewrittenPath = new StringBuilder(path.length());
        ByteArrayOutputStream buf = new ByteArrayOutputStream(10);
        OutputStreamWriter writer = new OutputStreamWriter(buf, charset);
        for (int i = 0; i < path.length(); i++) {
            int c = path.charAt(i);
            if (this.safeCharacters.get(c)) {
                rewrittenPath.append((char) c);
            } else if (this.encodeSpaceAsPlus && c == 32) {
                rewrittenPath.append('+');
            } else {
                try {
                    writer.write((char) c);
                    writer.flush();
                    byte[] ba = buf.toByteArray();
                    for (byte toEncode : ba) {
                        rewrittenPath.append('%');
                        int low = toEncode & 15;
                        int high = (toEncode & 240) >> 4;
                        rewrittenPath.append(hexadecimal[high]);
                        rewrittenPath.append(hexadecimal[low]);
                    }
                    buf.reset();
                } catch (IOException e) {
                    buf.reset();
                }
            }
        }
        return rewrittenPath.toString();
    }

    public Object clone() {
        URLEncoder result = new URLEncoder((BitSet) this.safeCharacters.clone());
        result.setEncodeSpaceAsPlus(this.encodeSpaceAsPlus);
        return result;
    }
}
