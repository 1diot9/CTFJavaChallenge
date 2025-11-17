package org.springframework.web.servlet.mvc.condition;

import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/condition/AbstractMediaTypeExpression.class */
abstract class AbstractMediaTypeExpression implements MediaTypeExpression, Comparable<AbstractMediaTypeExpression> {
    private final MediaType mediaType;
    private final boolean isNegated;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractMediaTypeExpression(String expression) {
        if (expression.startsWith("!")) {
            this.isNegated = true;
            expression = expression.substring(1);
        } else {
            this.isNegated = false;
        }
        this.mediaType = MediaType.parseMediaType(expression);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractMediaTypeExpression(MediaType mediaType, boolean negated) {
        this.mediaType = mediaType;
        this.isNegated = negated;
    }

    @Override // org.springframework.web.servlet.mvc.condition.MediaTypeExpression
    public MediaType getMediaType() {
        return this.mediaType;
    }

    @Override // org.springframework.web.servlet.mvc.condition.MediaTypeExpression
    public boolean isNegated() {
        return this.isNegated;
    }

    @Override // java.lang.Comparable
    public int compareTo(AbstractMediaTypeExpression other) {
        MediaType mediaType1 = getMediaType();
        MediaType mediaType2 = other.getMediaType();
        if (mediaType1.isMoreSpecific(mediaType2)) {
            return -1;
        }
        if (mediaType1.isLessSpecific(mediaType2)) {
            return 1;
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean matchParameters(MediaType contentType) {
        for (Map.Entry<String, String> entry : getMediaType().getParameters().entrySet()) {
            if (StringUtils.hasText(entry.getValue())) {
                String value = contentType.getParameter(entry.getKey());
                if (StringUtils.hasText(value) && !entry.getValue().equalsIgnoreCase(value)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        AbstractMediaTypeExpression otherExpr = (AbstractMediaTypeExpression) other;
        return this.mediaType.equals(otherExpr.mediaType) && this.isNegated == otherExpr.isNegated;
    }

    public int hashCode() {
        return this.mediaType.hashCode();
    }

    public String toString() {
        if (this.isNegated) {
            return "!" + this.mediaType.toString();
        }
        return this.mediaType.toString();
    }
}
