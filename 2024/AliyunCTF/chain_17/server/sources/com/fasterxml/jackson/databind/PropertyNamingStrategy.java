package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import java.io.Serializable;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/PropertyNamingStrategy.class */
public class PropertyNamingStrategy implements Serializable {
    private static final long serialVersionUID = 2;

    @Deprecated
    public static final PropertyNamingStrategy LOWER_CAMEL_CASE = new PropertyNamingStrategy();

    @Deprecated
    public static final PropertyNamingStrategy UPPER_CAMEL_CASE = new UpperCamelCaseStrategy();

    @Deprecated
    public static final PropertyNamingStrategy SNAKE_CASE = new SnakeCaseStrategy();

    @Deprecated
    public static final PropertyNamingStrategy LOWER_CASE = new LowerCaseStrategy();

    @Deprecated
    public static final PropertyNamingStrategy KEBAB_CASE = new KebabCaseStrategy();

    @Deprecated
    public static final PropertyNamingStrategy LOWER_DOT_CASE = new LowerDotCaseStrategy();

    @Deprecated
    public static final PropertyNamingStrategy CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES = SNAKE_CASE;

    @Deprecated
    public static final PropertyNamingStrategy PASCAL_CASE_TO_CAMEL_CASE = UPPER_CAMEL_CASE;

    @Deprecated
    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/PropertyNamingStrategy$LowerCaseWithUnderscoresStrategy.class */
    public static class LowerCaseWithUnderscoresStrategy extends SnakeCaseStrategy {
    }

    @Deprecated
    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/PropertyNamingStrategy$PascalCaseStrategy.class */
    public static class PascalCaseStrategy extends UpperCamelCaseStrategy {
    }

    public String nameForField(MapperConfig<?> config, AnnotatedField field, String defaultName) {
        return defaultName;
    }

    public String nameForGetterMethod(MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
        return defaultName;
    }

    public String nameForSetterMethod(MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
        return defaultName;
    }

    public String nameForConstructorParameter(MapperConfig<?> config, AnnotatedParameter ctorParam, String defaultName) {
        return defaultName;
    }

    @Deprecated
    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/PropertyNamingStrategy$PropertyNamingStrategyBase.class */
    public static abstract class PropertyNamingStrategyBase extends PropertyNamingStrategy {
        public abstract String translate(String str);

        @Override // com.fasterxml.jackson.databind.PropertyNamingStrategy
        public String nameForField(MapperConfig<?> config, AnnotatedField field, String defaultName) {
            return translate(defaultName);
        }

        @Override // com.fasterxml.jackson.databind.PropertyNamingStrategy
        public String nameForGetterMethod(MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
            return translate(defaultName);
        }

        @Override // com.fasterxml.jackson.databind.PropertyNamingStrategy
        public String nameForSetterMethod(MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
            return translate(defaultName);
        }

        @Override // com.fasterxml.jackson.databind.PropertyNamingStrategy
        public String nameForConstructorParameter(MapperConfig<?> config, AnnotatedParameter ctorParam, String defaultName) {
            return translate(defaultName);
        }

        protected static String translateLowerCaseWithSeparator(String input, char separator) {
            if (input == null) {
                return input;
            }
            int length = input.length();
            if (length == 0) {
                return input;
            }
            StringBuilder result = new StringBuilder(length + (length >> 1));
            int upperCount = 0;
            for (int i = 0; i < length; i++) {
                char ch2 = input.charAt(i);
                char lc = Character.toLowerCase(ch2);
                if (lc == ch2) {
                    if (upperCount > 1) {
                        result.insert(result.length() - 1, separator);
                    }
                    upperCount = 0;
                } else {
                    if (upperCount == 0 && i > 0) {
                        result.append(separator);
                    }
                    upperCount++;
                }
                result.append(lc);
            }
            return result.toString();
        }
    }

    @Deprecated
    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/PropertyNamingStrategy$SnakeCaseStrategy.class */
    public static class SnakeCaseStrategy extends PropertyNamingStrategyBase {
        @Override // com.fasterxml.jackson.databind.PropertyNamingStrategy.PropertyNamingStrategyBase
        public String translate(String input) {
            if (input == null) {
                return input;
            }
            int length = input.length();
            StringBuilder result = new StringBuilder(length * 2);
            int resultLength = 0;
            boolean wasPrevTranslated = false;
            for (int i = 0; i < length; i++) {
                char c = input.charAt(i);
                if (i > 0 || c != '_') {
                    if (Character.isUpperCase(c)) {
                        if (!wasPrevTranslated && resultLength > 0 && result.charAt(resultLength - 1) != '_') {
                            result.append('_');
                            resultLength++;
                        }
                        c = Character.toLowerCase(c);
                        wasPrevTranslated = true;
                    } else {
                        wasPrevTranslated = false;
                    }
                    result.append(c);
                    resultLength++;
                }
            }
            return resultLength > 0 ? result.toString() : input;
        }
    }

    @Deprecated
    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/PropertyNamingStrategy$UpperCamelCaseStrategy.class */
    public static class UpperCamelCaseStrategy extends PropertyNamingStrategyBase {
        @Override // com.fasterxml.jackson.databind.PropertyNamingStrategy.PropertyNamingStrategyBase
        public String translate(String input) {
            if (input == null || input.isEmpty()) {
                return input;
            }
            char c = input.charAt(0);
            char uc = Character.toUpperCase(c);
            if (c == uc) {
                return input;
            }
            StringBuilder sb = new StringBuilder(input);
            sb.setCharAt(0, uc);
            return sb.toString();
        }
    }

    @Deprecated
    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/PropertyNamingStrategy$LowerCaseStrategy.class */
    public static class LowerCaseStrategy extends PropertyNamingStrategyBase {
        @Override // com.fasterxml.jackson.databind.PropertyNamingStrategy.PropertyNamingStrategyBase
        public String translate(String input) {
            return input.toLowerCase();
        }
    }

    @Deprecated
    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/PropertyNamingStrategy$KebabCaseStrategy.class */
    public static class KebabCaseStrategy extends PropertyNamingStrategyBase {
        @Override // com.fasterxml.jackson.databind.PropertyNamingStrategy.PropertyNamingStrategyBase
        public String translate(String input) {
            return translateLowerCaseWithSeparator(input, '-');
        }
    }

    @Deprecated
    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/PropertyNamingStrategy$LowerDotCaseStrategy.class */
    public static class LowerDotCaseStrategy extends PropertyNamingStrategyBase {
        @Override // com.fasterxml.jackson.databind.PropertyNamingStrategy.PropertyNamingStrategyBase
        public String translate(String input) {
            return translateLowerCaseWithSeparator(input, '.');
        }
    }
}
