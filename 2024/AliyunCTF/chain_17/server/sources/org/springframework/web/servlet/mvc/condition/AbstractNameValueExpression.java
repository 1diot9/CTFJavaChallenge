package org.springframework.web.servlet.mvc.condition;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/condition/AbstractNameValueExpression.class */
abstract class AbstractNameValueExpression<T> implements NameValueExpression<T> {
    protected final String name;

    @Nullable
    protected final T value;
    protected final boolean isNegated;

    protected abstract boolean isCaseSensitiveName();

    protected abstract T parseValue(String valueExpression);

    protected abstract boolean matchName(HttpServletRequest request);

    protected abstract boolean matchValue(HttpServletRequest request);

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractNameValueExpression(String expression) {
        int separator = expression.indexOf(61);
        if (separator == -1) {
            this.isNegated = expression.startsWith("!");
            this.name = this.isNegated ? expression.substring(1) : expression;
            this.value = null;
        } else {
            this.isNegated = separator > 0 && expression.charAt(separator - 1) == '!';
            this.name = this.isNegated ? expression.substring(0, separator - 1) : expression.substring(0, separator);
            this.value = parseValue(expression.substring(separator + 1));
        }
    }

    @Override // org.springframework.web.servlet.mvc.condition.NameValueExpression
    public String getName() {
        return this.name;
    }

    @Override // org.springframework.web.servlet.mvc.condition.NameValueExpression
    @Nullable
    public T getValue() {
        return this.value;
    }

    @Override // org.springframework.web.servlet.mvc.condition.NameValueExpression
    public boolean isNegated() {
        return this.isNegated;
    }

    public final boolean match(HttpServletRequest request) {
        boolean isMatch;
        if (this.value != null) {
            isMatch = matchValue(request);
        } else {
            isMatch = matchName(request);
        }
        return this.isNegated != isMatch;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        AbstractNameValueExpression<?> that = (AbstractNameValueExpression) other;
        if (!isCaseSensitiveName() ? this.name.equalsIgnoreCase(that.name) : this.name.equals(that.name)) {
            if (ObjectUtils.nullSafeEquals(this.value, that.value) && this.isNegated == that.isNegated) {
                return true;
            }
        }
        return false;
    }

    public int hashCode() {
        int result = isCaseSensitiveName() ? this.name.hashCode() : this.name.toLowerCase().hashCode();
        return (31 * ((31 * result) + (this.value != null ? this.value.hashCode() : 0))) + (this.isNegated ? 1 : 0);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (this.value != null) {
            builder.append(this.name);
            if (this.isNegated) {
                builder.append('!');
            }
            builder.append('=');
            builder.append(this.value);
        } else {
            if (this.isNegated) {
                builder.append('!');
            }
            builder.append(this.name);
        }
        return builder.toString();
    }
}
