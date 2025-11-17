package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.util.Currency;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/propertyeditors/CurrencyEditor.class */
public class CurrencyEditor extends PropertyEditorSupport {
    public void setAsText(String text) throws IllegalArgumentException {
        if (StringUtils.hasText(text)) {
            text = text.trim();
        }
        setValue(Currency.getInstance(text));
    }

    public String getAsText() {
        Currency value = (Currency) getValue();
        return value != null ? value.getCurrencyCode() : "";
    }
}
