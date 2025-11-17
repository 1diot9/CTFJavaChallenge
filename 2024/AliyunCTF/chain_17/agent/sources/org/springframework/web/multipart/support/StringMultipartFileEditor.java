package org.springframework.web.multipart.support;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/multipart/support/StringMultipartFileEditor.class */
public class StringMultipartFileEditor extends PropertyEditorSupport {

    @Nullable
    private final String charsetName;

    public StringMultipartFileEditor() {
        this.charsetName = null;
    }

    public StringMultipartFileEditor(String charsetName) {
        this.charsetName = charsetName;
    }

    public void setAsText(String text) {
        setValue(text);
    }

    public void setValue(Object value) {
        String str;
        if (value instanceof MultipartFile) {
            MultipartFile multipartFile = (MultipartFile) value;
            try {
                if (this.charsetName != null) {
                    str = new String(multipartFile.getBytes(), this.charsetName);
                } else {
                    str = new String(multipartFile.getBytes());
                }
                super.setValue(str);
                return;
            } catch (IOException ex) {
                throw new IllegalArgumentException("Cannot read contents of multipart file", ex);
            }
        }
        super.setValue(value);
    }
}
