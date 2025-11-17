package org.springframework.web.servlet.mvc.condition;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.mvc.condition.HeadersRequestCondition;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/condition/ConsumesRequestCondition.class */
public final class ConsumesRequestCondition extends AbstractRequestCondition<ConsumesRequestCondition> {
    private static final ConsumesRequestCondition EMPTY_CONDITION = new ConsumesRequestCondition(new String[0]);
    private final List<ConsumeMediaTypeExpression> expressions;
    private boolean bodyRequired;

    public ConsumesRequestCondition(String... consumes) {
        this(consumes, null);
    }

    public ConsumesRequestCondition(String[] consumes, @Nullable String[] headers) {
        this.bodyRequired = true;
        this.expressions = parseExpressions(consumes, headers);
        if (this.expressions.size() > 1) {
            Collections.sort(this.expressions);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    private static List<ConsumeMediaTypeExpression> parseExpressions(String[] consumes, @Nullable String[] headers) {
        Set<ConsumeMediaTypeExpression> result = null;
        if (!ObjectUtils.isEmpty((Object[]) headers)) {
            for (String header : headers) {
                HeadersRequestCondition.HeaderExpression expr = new HeadersRequestCondition.HeaderExpression(header);
                if (HttpHeaders.CONTENT_TYPE.equalsIgnoreCase(expr.name) && expr.value != 0) {
                    result = result != null ? result : new LinkedHashSet<>();
                    for (MediaType mediaType : MediaType.parseMediaTypes((String) expr.value)) {
                        result.add(new ConsumeMediaTypeExpression(mediaType, expr.isNegated));
                    }
                }
            }
        }
        if (!ObjectUtils.isEmpty((Object[]) consumes)) {
            result = result != null ? result : new LinkedHashSet<>();
            for (String consume : consumes) {
                result.add(new ConsumeMediaTypeExpression(consume));
            }
        }
        return result != null ? new ArrayList(result) : Collections.emptyList();
    }

    private ConsumesRequestCondition(List<ConsumeMediaTypeExpression> expressions) {
        this.bodyRequired = true;
        this.expressions = expressions;
    }

    public Set<MediaTypeExpression> getExpressions() {
        return new LinkedHashSet(this.expressions);
    }

    public Set<MediaType> getConsumableMediaTypes() {
        Set<MediaType> result = new LinkedHashSet<>();
        for (ConsumeMediaTypeExpression expression : this.expressions) {
            if (!expression.isNegated()) {
                result.add(expression.getMediaType());
            }
        }
        return result;
    }

    @Override // org.springframework.web.servlet.mvc.condition.AbstractRequestCondition
    public boolean isEmpty() {
        return this.expressions.isEmpty();
    }

    @Override // org.springframework.web.servlet.mvc.condition.AbstractRequestCondition
    protected Collection<ConsumeMediaTypeExpression> getContent() {
        return this.expressions;
    }

    @Override // org.springframework.web.servlet.mvc.condition.AbstractRequestCondition
    protected String getToStringInfix() {
        return " || ";
    }

    public void setBodyRequired(boolean bodyRequired) {
        this.bodyRequired = bodyRequired;
    }

    public boolean isBodyRequired() {
        return this.bodyRequired;
    }

    @Override // org.springframework.web.servlet.mvc.condition.RequestCondition
    public ConsumesRequestCondition combine(ConsumesRequestCondition other) {
        return !other.expressions.isEmpty() ? other : this;
    }

    @Override // org.springframework.web.servlet.mvc.condition.RequestCondition
    @Nullable
    public ConsumesRequestCondition getMatchingCondition(HttpServletRequest request) {
        MediaType mediaType;
        if (CorsUtils.isPreFlightRequest(request)) {
            return EMPTY_CONDITION;
        }
        if (isEmpty()) {
            return this;
        }
        if (!hasBody(request) && !this.bodyRequired) {
            return EMPTY_CONDITION;
        }
        try {
            if (StringUtils.hasLength(request.getContentType())) {
                mediaType = MediaType.parseMediaType(request.getContentType());
            } else {
                mediaType = MediaType.APPLICATION_OCTET_STREAM;
            }
            MediaType contentType = mediaType;
            List<ConsumeMediaTypeExpression> result = getMatchingExpressions(contentType);
            if (CollectionUtils.isEmpty(result)) {
                return null;
            }
            return new ConsumesRequestCondition(result);
        } catch (InvalidMediaTypeException e) {
            return null;
        }
    }

    private boolean hasBody(HttpServletRequest request) {
        String contentLength = request.getHeader(HttpHeaders.CONTENT_LENGTH);
        String transferEncoding = request.getHeader("Transfer-Encoding");
        return StringUtils.hasText(transferEncoding) || (StringUtils.hasText(contentLength) && !contentLength.trim().equals(CustomBooleanEditor.VALUE_0));
    }

    @Nullable
    private List<ConsumeMediaTypeExpression> getMatchingExpressions(MediaType contentType) {
        List<ConsumeMediaTypeExpression> result = null;
        for (ConsumeMediaTypeExpression expression : this.expressions) {
            if (expression.match(contentType)) {
                result = result != null ? result : new ArrayList<>();
                result.add(expression);
            }
        }
        return result;
    }

    @Override // org.springframework.web.servlet.mvc.condition.RequestCondition
    public int compareTo(ConsumesRequestCondition other, HttpServletRequest request) {
        if (this.expressions.isEmpty() && other.expressions.isEmpty()) {
            return 0;
        }
        if (this.expressions.isEmpty()) {
            return 1;
        }
        if (other.expressions.isEmpty()) {
            return -1;
        }
        return this.expressions.get(0).compareTo((AbstractMediaTypeExpression) other.expressions.get(0));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/condition/ConsumesRequestCondition$ConsumeMediaTypeExpression.class */
    public static class ConsumeMediaTypeExpression extends AbstractMediaTypeExpression {
        ConsumeMediaTypeExpression(String expression) {
            super(expression);
        }

        ConsumeMediaTypeExpression(MediaType mediaType, boolean negated) {
            super(mediaType, negated);
        }

        public final boolean match(MediaType contentType) {
            boolean match = getMediaType().includes(contentType) && matchParameters(contentType);
            return (!isNegated()) == match;
        }
    }
}
