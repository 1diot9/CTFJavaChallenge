package org.springframework.web.servlet.mvc.condition;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.web.cors.CorsUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/condition/HeadersRequestCondition.class */
public final class HeadersRequestCondition extends AbstractRequestCondition<HeadersRequestCondition> {
    private static final HeadersRequestCondition PRE_FLIGHT_MATCH = new HeadersRequestCondition(new String[0]);
    private final Set<HeaderExpression> expressions;

    public HeadersRequestCondition(String... headers) {
        this.expressions = parseExpressions(headers);
    }

    private static Set<HeaderExpression> parseExpressions(String... headers) {
        Set<HeaderExpression> result = null;
        if (!ObjectUtils.isEmpty((Object[]) headers)) {
            for (String header : headers) {
                HeaderExpression expr = new HeaderExpression(header);
                if (!HttpHeaders.ACCEPT.equalsIgnoreCase(expr.name) && !HttpHeaders.CONTENT_TYPE.equalsIgnoreCase(expr.name)) {
                    result = result != null ? result : new LinkedHashSet<>(headers.length);
                    result.add(expr);
                }
            }
        }
        return result != null ? result : Collections.emptySet();
    }

    private HeadersRequestCondition(Set<HeaderExpression> conditions) {
        this.expressions = conditions;
    }

    public Set<NameValueExpression<String>> getExpressions() {
        return new LinkedHashSet(this.expressions);
    }

    @Override // org.springframework.web.servlet.mvc.condition.AbstractRequestCondition
    protected Collection<HeaderExpression> getContent() {
        return this.expressions;
    }

    @Override // org.springframework.web.servlet.mvc.condition.AbstractRequestCondition
    protected String getToStringInfix() {
        return " && ";
    }

    @Override // org.springframework.web.servlet.mvc.condition.RequestCondition
    public HeadersRequestCondition combine(HeadersRequestCondition other) {
        if (isEmpty() && other.isEmpty()) {
            return this;
        }
        if (other.isEmpty()) {
            return this;
        }
        if (isEmpty()) {
            return other;
        }
        Set<HeaderExpression> set = new LinkedHashSet<>(this.expressions);
        set.addAll(other.expressions);
        return new HeadersRequestCondition(set);
    }

    @Override // org.springframework.web.servlet.mvc.condition.RequestCondition
    @Nullable
    public HeadersRequestCondition getMatchingCondition(HttpServletRequest request) {
        if (CorsUtils.isPreFlightRequest(request)) {
            return PRE_FLIGHT_MATCH;
        }
        for (HeaderExpression expression : this.expressions) {
            if (!expression.match(request)) {
                return null;
            }
        }
        return this;
    }

    @Override // org.springframework.web.servlet.mvc.condition.RequestCondition
    public int compareTo(HeadersRequestCondition other, HttpServletRequest request) {
        int result = other.expressions.size() - this.expressions.size();
        if (result != 0) {
            return result;
        }
        return (int) (getValueMatchCount(other.expressions) - getValueMatchCount(this.expressions));
    }

    private long getValueMatchCount(Set<HeaderExpression> expressions) {
        long count = 0;
        for (HeaderExpression e : expressions) {
            if (e.getValue() != null && !e.isNegated()) {
                count++;
            }
        }
        return count;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/condition/HeadersRequestCondition$HeaderExpression.class */
    public static class HeaderExpression extends AbstractNameValueExpression<String> {
        /* JADX INFO: Access modifiers changed from: package-private */
        public HeaderExpression(String expression) {
            super(expression);
        }

        @Override // org.springframework.web.servlet.mvc.condition.AbstractNameValueExpression
        protected boolean isCaseSensitiveName() {
            return false;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.springframework.web.servlet.mvc.condition.AbstractNameValueExpression
        public String parseValue(String valueExpression) {
            return valueExpression;
        }

        @Override // org.springframework.web.servlet.mvc.condition.AbstractNameValueExpression
        protected boolean matchName(HttpServletRequest request) {
            return request.getHeader(this.name) != null;
        }

        @Override // org.springframework.web.servlet.mvc.condition.AbstractNameValueExpression
        protected boolean matchValue(HttpServletRequest request) {
            return ObjectUtils.nullSafeEquals(this.value, request.getHeader(this.name));
        }
    }
}
