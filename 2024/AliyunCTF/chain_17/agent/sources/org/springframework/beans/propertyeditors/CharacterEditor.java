package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/propertyeditors/CharacterEditor.class */
public class CharacterEditor extends PropertyEditorSupport {
    private static final String UNICODE_PREFIX = "\\u";
    private static final int UNICODE_LENGTH = 6;
    private final boolean allowEmpty;

    public CharacterEditor(boolean allowEmpty) {
        this.allowEmpty = allowEmpty;
    }

    public void setAsText(@Nullable String text) throws IllegalArgumentException {
        if (this.allowEmpty && !StringUtils.hasLength(text)) {
            setValue(null);
            return;
        }
        if (text == null) {
            throw new IllegalArgumentException("null String cannot be converted to char type");
        }
        if (isUnicodeCharacterSequence(text)) {
            setAsUnicode(text);
        } else {
            if (text.length() == 1) {
                setValue(Character.valueOf(text.charAt(0)));
                return;
            }
            throw new IllegalArgumentException("String [" + text + "] with length " + text.length() + " cannot be converted to char type: neither Unicode nor single character");
        }
    }

    public String getAsText() {
        Object value = getValue();
        return value != null ? value.toString() : "";
    }

    private boolean isUnicodeCharacterSequence(String sequence) {
        return sequence.startsWith(UNICODE_PREFIX) && sequence.length() == 6;
    }

    private void setAsUnicode(String text) {
        int code = Integer.parseInt(text.substring(UNICODE_PREFIX.length()), 16);
        setValue(Character.valueOf((char) code));
    }
}
