package org.springframework.web.multipart.support;

import java.io.IOException;
import org.springframework.beans.propertyeditors.ByteArrayPropertyEditor;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/multipart/support/ByteArrayMultipartFileEditor.class */
public class ByteArrayMultipartFileEditor extends ByteArrayPropertyEditor {
    public void setValue(@Nullable Object value) {
        if (value instanceof MultipartFile) {
            MultipartFile multipartFile = (MultipartFile) value;
            try {
                super.setValue(multipartFile.getBytes());
                return;
            } catch (IOException ex) {
                throw new IllegalArgumentException("Cannot read contents of multipart file", ex);
            }
        }
        if (value instanceof byte[]) {
            super.setValue(value);
        } else {
            super.setValue(value != null ? value.toString().getBytes() : null);
        }
    }

    @Override // org.springframework.beans.propertyeditors.ByteArrayPropertyEditor
    public String getAsText() {
        byte[] value = (byte[]) getValue();
        return value != null ? new String(value) : "";
    }
}
