package org.springframework.web.servlet.mvc.condition;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Collections;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/condition/RequestConditionHolder.class */
public final class RequestConditionHolder extends AbstractRequestCondition<RequestConditionHolder> {

    @Nullable
    private final RequestCondition<Object> condition;

    public RequestConditionHolder(@Nullable RequestCondition<?> requestCondition) {
        this.condition = requestCondition;
    }

    @Nullable
    public RequestCondition<?> getCondition() {
        return this.condition;
    }

    @Override // org.springframework.web.servlet.mvc.condition.AbstractRequestCondition
    protected Collection<?> getContent() {
        return this.condition != null ? Collections.singleton(this.condition) : Collections.emptyList();
    }

    @Override // org.springframework.web.servlet.mvc.condition.AbstractRequestCondition
    protected String getToStringInfix() {
        return " ";
    }

    @Override // org.springframework.web.servlet.mvc.condition.RequestCondition
    public RequestConditionHolder combine(RequestConditionHolder other) {
        if (this.condition == null && other.condition == null) {
            return this;
        }
        if (this.condition == null) {
            return other;
        }
        if (other.condition == null) {
            return this;
        }
        assertEqualConditionTypes(this.condition, other.condition);
        RequestCondition<?> combined = (RequestCondition) this.condition.combine(other.condition);
        return new RequestConditionHolder(combined);
    }

    private void assertEqualConditionTypes(RequestCondition<?> thisCondition, RequestCondition<?> otherCondition) {
        Class<?> clazz = thisCondition.getClass();
        Class<?> otherClazz = otherCondition.getClass();
        if (!clazz.equals(otherClazz)) {
            throw new ClassCastException("Incompatible request conditions: " + clazz + " and " + otherClazz);
        }
    }

    @Override // org.springframework.web.servlet.mvc.condition.RequestCondition
    @Nullable
    public RequestConditionHolder getMatchingCondition(HttpServletRequest request) {
        if (this.condition == null) {
            return this;
        }
        RequestCondition<?> match = (RequestCondition) this.condition.getMatchingCondition(request);
        if (match != null) {
            return new RequestConditionHolder(match);
        }
        return null;
    }

    @Override // org.springframework.web.servlet.mvc.condition.RequestCondition
    public int compareTo(RequestConditionHolder other, HttpServletRequest request) {
        if (this.condition == null && other.condition == null) {
            return 0;
        }
        if (this.condition == null) {
            return 1;
        }
        if (other.condition == null) {
            return -1;
        }
        assertEqualConditionTypes(this.condition, other.condition);
        return this.condition.compareTo(other.condition, request);
    }
}
