package org.springframework.web.servlet.tags;

import jakarta.servlet.jsp.JspException;
import java.beans.PropertyEditor;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/tags/EditorAwareTag.class */
public interface EditorAwareTag {
    @Nullable
    PropertyEditor getEditor() throws JspException;
}
