package org.springframework.web.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Supplier;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/util/HtmlCharacterEntityReferences.class */
class HtmlCharacterEntityReferences {
    private static final String PROPERTIES_FILE = "HtmlCharacterEntityReferences.properties";
    static final char REFERENCE_START = '&';
    static final String DECIMAL_REFERENCE_START = "&#";
    static final String HEX_REFERENCE_START = "&#x";
    static final char REFERENCE_END = ';';
    static final char CHAR_NULL = 65535;
    private final String[] characterToEntityReferenceMap = new String[3000];
    private final Map<String, Character> entityReferenceToCharacterMap = new HashMap(512);

    public HtmlCharacterEntityReferences() {
        Properties entityReferences = new Properties();
        InputStream is = HtmlCharacterEntityReferences.class.getResourceAsStream(PROPERTIES_FILE);
        if (is == null) {
            throw new IllegalStateException("Cannot find reference definition file [HtmlCharacterEntityReferences.properties] as class path resource");
        }
        try {
            try {
                entityReferences.load(is);
                is.close();
                Enumeration<?> keys = entityReferences.propertyNames();
                while (keys.hasMoreElements()) {
                    String key = (String) keys.nextElement();
                    int referredChar = Integer.parseInt(key);
                    Assert.isTrue(referredChar < 1000 || (referredChar >= 8000 && referredChar < 10000), (Supplier<String>) () -> {
                        return "Invalid reference to special HTML entity: " + referredChar;
                    });
                    int index = referredChar < 1000 ? referredChar : referredChar - 7000;
                    String reference = entityReferences.getProperty(key);
                    this.characterToEntityReferenceMap[index] = "&" + reference + ";";
                    this.entityReferenceToCharacterMap.put(reference, Character.valueOf((char) referredChar));
                }
            } catch (Throwable th) {
                is.close();
                throw th;
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to parse reference definition file [HtmlCharacterEntityReferences.properties]: " + ex.getMessage());
        }
    }

    public int getSupportedReferenceCount() {
        return this.entityReferenceToCharacterMap.size();
    }

    public boolean isMappedToReference(char character) {
        return isMappedToReference(character, "ISO-8859-1");
    }

    public boolean isMappedToReference(char character, String encoding) {
        return convertToReference(character, encoding) != null;
    }

    @Nullable
    public String convertToReference(char character) {
        return convertToReference(character, "ISO-8859-1");
    }

    @Nullable
    public String convertToReference(char character, String encoding) {
        if (encoding.startsWith("UTF-")) {
            switch (character) {
                case '\"':
                    return "&quot;";
                case '&':
                    return "&amp;";
                case '\'':
                    return "&#39;";
                case '<':
                    return "&lt;";
                case '>':
                    return "&gt;";
                default:
                    return null;
            }
        }
        if (character < 1000 || (character >= 8000 && character < 10000)) {
            int index = character < 1000 ? character : character - 7000;
            String entityReference = this.characterToEntityReferenceMap[index];
            if (entityReference != null) {
                return entityReference;
            }
            return null;
        }
        return null;
    }

    public char convertToCharacter(String entityReference) {
        Character referredCharacter = this.entityReferenceToCharacterMap.get(entityReference);
        if (referredCharacter != null) {
            return referredCharacter.charValue();
        }
        return (char) 65535;
    }
}
