package org.springframework.web.servlet.mvc.support;

import java.util.Collection;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.ui.Model;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/support/RedirectAttributes.class */
public interface RedirectAttributes extends Model {
    @Override // org.springframework.ui.Model
    RedirectAttributes addAttribute(String attributeName, @Nullable Object attributeValue);

    @Override // org.springframework.ui.Model
    RedirectAttributes addAttribute(Object attributeValue);

    @Override // org.springframework.ui.Model
    RedirectAttributes addAllAttributes(Collection<?> attributeValues);

    @Override // org.springframework.ui.Model
    RedirectAttributes mergeAttributes(Map<String, ?> attributes);

    RedirectAttributes addFlashAttribute(String attributeName, @Nullable Object attributeValue);

    RedirectAttributes addFlashAttribute(Object attributeValue);

    Map<String, ?> getFlashAttributes();

    @Override // org.springframework.ui.Model
    /* bridge */ /* synthetic */ default Model mergeAttributes(Map attributes) {
        return mergeAttributes((Map<String, ?>) attributes);
    }

    @Override // org.springframework.ui.Model
    /* bridge */ /* synthetic */ default Model addAllAttributes(Collection attributeValues) {
        return addAllAttributes((Collection<?>) attributeValues);
    }
}
