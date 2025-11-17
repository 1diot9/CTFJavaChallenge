package org.springframework.web.util;

import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/util/HtmlUtils.class */
public abstract class HtmlUtils {
    private static final HtmlCharacterEntityReferences characterEntityReferences = new HtmlCharacterEntityReferences();

    public static String htmlEscape(String input) {
        return htmlEscape(input, "ISO-8859-1");
    }

    public static String htmlEscape(String input, String encoding) {
        Assert.notNull(input, "Input is required");
        Assert.notNull(encoding, "Encoding is required");
        StringBuilder escaped = new StringBuilder(input.length() * 2);
        for (int i = 0; i < input.length(); i++) {
            char character = input.charAt(i);
            String reference = characterEntityReferences.convertToReference(character, encoding);
            if (reference != null) {
                escaped.append(reference);
            } else {
                escaped.append(character);
            }
        }
        return escaped.toString();
    }

    public static String htmlEscapeDecimal(String input) {
        return htmlEscapeDecimal(input, "ISO-8859-1");
    }

    public static String htmlEscapeDecimal(String input, String encoding) {
        Assert.notNull(input, "Input is required");
        Assert.notNull(encoding, "Encoding is required");
        StringBuilder escaped = new StringBuilder(input.length() * 2);
        for (int i = 0; i < input.length(); i++) {
            char character = input.charAt(i);
            if (characterEntityReferences.isMappedToReference(character, encoding)) {
                escaped.append("&#");
                escaped.append((int) character);
                escaped.append(';');
            } else {
                escaped.append(character);
            }
        }
        return escaped.toString();
    }

    public static String htmlEscapeHex(String input) {
        return htmlEscapeHex(input, "ISO-8859-1");
    }

    public static String htmlEscapeHex(String input, String encoding) {
        Assert.notNull(input, "Input is required");
        Assert.notNull(encoding, "Encoding is required");
        StringBuilder escaped = new StringBuilder(input.length() * 2);
        for (int i = 0; i < input.length(); i++) {
            char character = input.charAt(i);
            if (characterEntityReferences.isMappedToReference(character, encoding)) {
                escaped.append("&#x");
                escaped.append(Integer.toString(character, 16));
                escaped.append(';');
            } else {
                escaped.append(character);
            }
        }
        return escaped.toString();
    }

    public static String htmlUnescape(String input) {
        return new HtmlCharacterEntityDecoder(characterEntityReferences, input).decode();
    }
}
