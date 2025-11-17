package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/propertyeditors/CustomBooleanEditor.class */
public class CustomBooleanEditor extends PropertyEditorSupport {
    public static final String VALUE_TRUE = "true";
    public static final String VALUE_FALSE = "false";
    public static final String VALUE_ON = "on";
    public static final String VALUE_OFF = "off";
    public static final String VALUE_YES = "yes";
    public static final String VALUE_NO = "no";
    public static final String VALUE_1 = "1";
    public static final String VALUE_0 = "0";

    @Nullable
    private final String trueString;

    @Nullable
    private final String falseString;
    private final boolean allowEmpty;

    public CustomBooleanEditor(boolean allowEmpty) {
        this(null, null, allowEmpty);
    }

    public CustomBooleanEditor(@Nullable String trueString, @Nullable String falseString, boolean allowEmpty) {
        this.trueString = trueString;
        this.falseString = falseString;
        this.allowEmpty = allowEmpty;
    }

    public void setAsText(@Nullable String text) throws IllegalArgumentException {
        String input = text != null ? text.trim() : null;
        if (this.allowEmpty && !StringUtils.hasLength(input)) {
            setValue(null);
            return;
        }
        if (this.trueString != null && this.trueString.equalsIgnoreCase(input)) {
            setValue(Boolean.TRUE);
            return;
        }
        if (this.falseString != null && this.falseString.equalsIgnoreCase(input)) {
            setValue(Boolean.FALSE);
            return;
        }
        if (this.trueString == null && ("true".equalsIgnoreCase(input) || VALUE_ON.equalsIgnoreCase(input) || VALUE_YES.equalsIgnoreCase(input) || VALUE_1.equals(input))) {
            setValue(Boolean.TRUE);
            return;
        }
        if (this.falseString == null && ("false".equalsIgnoreCase(input) || VALUE_OFF.equalsIgnoreCase(input) || "no".equalsIgnoreCase(input) || VALUE_0.equals(input))) {
            setValue(Boolean.FALSE);
            return;
        }
        throw new IllegalArgumentException("Invalid boolean value [" + text + "]");
    }

    public String getAsText() {
        if (Boolean.TRUE.equals(getValue())) {
            return this.trueString != null ? this.trueString : "true";
        }
        if (Boolean.FALSE.equals(getValue())) {
            return this.falseString != null ? this.falseString : "false";
        }
        return "";
    }
}
