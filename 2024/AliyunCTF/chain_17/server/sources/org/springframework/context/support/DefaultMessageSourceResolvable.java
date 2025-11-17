package org.springframework.context.support;

import java.io.Serializable;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/support/DefaultMessageSourceResolvable.class */
public class DefaultMessageSourceResolvable implements MessageSourceResolvable, Serializable {

    @Nullable
    private final String[] codes;

    @Nullable
    private final Object[] arguments;

    @Nullable
    private final String defaultMessage;

    public DefaultMessageSourceResolvable(String code) {
        this(new String[]{code}, null, null);
    }

    public DefaultMessageSourceResolvable(String[] codes) {
        this(codes, null, null);
    }

    public DefaultMessageSourceResolvable(String[] codes, String defaultMessage) {
        this(codes, null, defaultMessage);
    }

    public DefaultMessageSourceResolvable(String[] codes, Object[] arguments) {
        this(codes, arguments, null);
    }

    public DefaultMessageSourceResolvable(@Nullable String[] codes, @Nullable Object[] arguments, @Nullable String defaultMessage) {
        this.codes = codes;
        this.arguments = arguments;
        this.defaultMessage = defaultMessage;
    }

    public DefaultMessageSourceResolvable(MessageSourceResolvable resolvable) {
        this(resolvable.getCodes(), resolvable.getArguments(), resolvable.getDefaultMessage());
    }

    @Nullable
    public String getCode() {
        if (this.codes == null || this.codes.length <= 0) {
            return null;
        }
        return this.codes[this.codes.length - 1];
    }

    @Override // org.springframework.context.MessageSourceResolvable
    @Nullable
    public String[] getCodes() {
        return this.codes;
    }

    @Override // org.springframework.context.MessageSourceResolvable
    @Nullable
    public Object[] getArguments() {
        return this.arguments;
    }

    @Override // org.springframework.context.MessageSourceResolvable
    @Nullable
    public String getDefaultMessage() {
        return this.defaultMessage;
    }

    public boolean shouldRenderDefaultMessage() {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final String resolvableToString() {
        StringBuilder result = new StringBuilder(64);
        result.append("codes [").append(StringUtils.arrayToDelimitedString(this.codes, ","));
        result.append("]; arguments [").append(StringUtils.arrayToDelimitedString(this.arguments, ","));
        result.append("]; default message [").append(this.defaultMessage).append(']');
        return result.toString();
    }

    public String toString() {
        return getClass().getName() + ": " + resolvableToString();
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof MessageSourceResolvable) {
                MessageSourceResolvable that = (MessageSourceResolvable) other;
                if (!ObjectUtils.nullSafeEquals(getCodes(), that.getCodes()) || !ObjectUtils.nullSafeEquals(getArguments(), that.getArguments()) || !ObjectUtils.nullSafeEquals(getDefaultMessage(), that.getDefaultMessage())) {
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        return ObjectUtils.nullSafeHash(getCode(), getArguments(), getDefaultMessage());
    }
}
