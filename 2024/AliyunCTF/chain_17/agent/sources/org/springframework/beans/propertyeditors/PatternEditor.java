package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.util.regex.Pattern;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/propertyeditors/PatternEditor.class */
public class PatternEditor extends PropertyEditorSupport {
    private final int flags;

    public PatternEditor() {
        this.flags = 0;
    }

    public PatternEditor(int flags) {
        this.flags = flags;
    }

    public void setAsText(@Nullable String text) {
        setValue(text != null ? Pattern.compile(text, this.flags) : null);
    }

    public String getAsText() {
        Pattern value = (Pattern) getValue();
        return value != null ? value.pattern() : "";
    }
}
