package org.springframework.validation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/validation/DefaultMessageCodesResolver.class */
public class DefaultMessageCodesResolver implements MessageCodesResolver, Serializable {
    public static final String CODE_SEPARATOR = ".";
    private static final MessageCodeFormatter DEFAULT_FORMATTER = Format.PREFIX_ERROR_CODE;
    private String prefix = "";
    private MessageCodeFormatter formatter = DEFAULT_FORMATTER;

    public void setPrefix(@Nullable String prefix) {
        this.prefix = prefix != null ? prefix : "";
    }

    protected String getPrefix() {
        return this.prefix;
    }

    public void setMessageCodeFormatter(@Nullable MessageCodeFormatter formatter) {
        this.formatter = formatter != null ? formatter : DEFAULT_FORMATTER;
    }

    @Override // org.springframework.validation.MessageCodesResolver
    public String[] resolveMessageCodes(String errorCode, String objectName) {
        return resolveMessageCodes(errorCode, objectName, "", null);
    }

    @Override // org.springframework.validation.MessageCodesResolver
    public String[] resolveMessageCodes(String errorCode, String objectName, String field, @Nullable Class<?> fieldType) {
        Set<String> codeList = new LinkedHashSet<>();
        List<String> fieldList = new ArrayList<>();
        buildFieldList(field, fieldList);
        addCodes(codeList, errorCode, objectName, fieldList);
        int dotIndex = field.lastIndexOf(46);
        if (dotIndex != -1) {
            buildFieldList(field.substring(dotIndex + 1), fieldList);
        }
        addCodes(codeList, errorCode, null, fieldList);
        if (fieldType != null) {
            addCode(codeList, errorCode, null, fieldType.getName());
        }
        addCode(codeList, errorCode, null, null);
        return StringUtils.toStringArray(codeList);
    }

    private void addCodes(Collection<String> codeList, String errorCode, @Nullable String objectName, Iterable<String> fields) {
        for (String field : fields) {
            addCode(codeList, errorCode, objectName, field);
        }
    }

    private void addCode(Collection<String> codeList, String errorCode, @Nullable String objectName, @Nullable String field) {
        codeList.add(postProcessMessageCode(this.formatter.format(errorCode, objectName, field)));
    }

    protected void buildFieldList(String field, List<String> fieldList) {
        fieldList.add(field);
        String plainField = field;
        int lastIndexOf = plainField.lastIndexOf(91);
        while (true) {
            int keyIndex = lastIndexOf;
            if (keyIndex != -1) {
                int endKeyIndex = plainField.indexOf(93, keyIndex);
                if (endKeyIndex != -1) {
                    plainField = plainField.substring(0, keyIndex) + plainField.substring(endKeyIndex + 1);
                    fieldList.add(plainField);
                    lastIndexOf = plainField.lastIndexOf(91);
                } else {
                    lastIndexOf = -1;
                }
            } else {
                return;
            }
        }
    }

    protected String postProcessMessageCode(String code) {
        return getPrefix() + code;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/validation/DefaultMessageCodesResolver$Format.class */
    public enum Format implements MessageCodeFormatter {
        PREFIX_ERROR_CODE { // from class: org.springframework.validation.DefaultMessageCodesResolver.Format.1
            @Override // org.springframework.validation.MessageCodeFormatter
            public String format(String errorCode, @Nullable String objectName, @Nullable String field) {
                return toDelimitedString(errorCode, objectName, field);
            }
        },
        POSTFIX_ERROR_CODE { // from class: org.springframework.validation.DefaultMessageCodesResolver.Format.2
            @Override // org.springframework.validation.MessageCodeFormatter
            public String format(String errorCode, @Nullable String objectName, @Nullable String field) {
                return toDelimitedString(objectName, field, errorCode);
            }
        };

        public static String toDelimitedString(String... elements) {
            StringJoiner rtn = new StringJoiner(".");
            for (String element : elements) {
                if (StringUtils.hasLength(element)) {
                    rtn.add(element);
                }
            }
            return rtn.toString();
        }
    }
}
