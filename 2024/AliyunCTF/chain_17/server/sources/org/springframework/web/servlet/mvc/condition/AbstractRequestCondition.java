package org.springframework.web.servlet.mvc.condition;

import java.util.Collection;
import java.util.StringJoiner;
import org.springframework.beans.PropertyAccessor;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.mvc.condition.AbstractRequestCondition;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/condition/AbstractRequestCondition.class */
public abstract class AbstractRequestCondition<T extends AbstractRequestCondition<T>> implements RequestCondition<T> {
    protected abstract Collection<?> getContent();

    protected abstract String getToStringInfix();

    public boolean isEmpty() {
        return getContent().isEmpty();
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        return getContent().equals(((AbstractRequestCondition) other).getContent());
    }

    public int hashCode() {
        return getContent().hashCode();
    }

    public String toString() {
        String infix = getToStringInfix();
        StringJoiner joiner = new StringJoiner(infix, PropertyAccessor.PROPERTY_KEY_PREFIX, "]");
        for (Object expression : getContent()) {
            joiner.add(expression.toString());
        }
        return joiner.toString();
    }
}
