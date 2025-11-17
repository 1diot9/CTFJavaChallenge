package com.fasterxml.jackson.databind;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/EnumNamingStrategies.class */
public class EnumNamingStrategies {
    private EnumNamingStrategies() {
    }

    /* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/EnumNamingStrategies$CamelCaseStrategy.class */
    public static class CamelCaseStrategy implements EnumNamingStrategy {
        public static final CamelCaseStrategy INSTANCE = new CamelCaseStrategy();

        @Override // com.fasterxml.jackson.databind.EnumNamingStrategy
        public String convertEnumToExternalName(String enumName) {
            if (enumName == null) {
                return null;
            }
            StringBuilder out = null;
            int iterationCnt = 0;
            int lastSeparatorIdx = -1;
            do {
                lastSeparatorIdx = indexIn(enumName, lastSeparatorIdx + 1);
                if (lastSeparatorIdx != -1) {
                    if (iterationCnt == 0) {
                        out = new StringBuilder(enumName.length() + (4 * "_".length()));
                        out.append(toLowerCase(enumName.substring(iterationCnt, lastSeparatorIdx)));
                    } else {
                        out.append(normalizeWord(enumName.substring(iterationCnt, lastSeparatorIdx)));
                    }
                    iterationCnt = lastSeparatorIdx + "_".length();
                }
            } while (lastSeparatorIdx != -1);
            if (iterationCnt == 0) {
                return toLowerCase(enumName);
            }
            out.append(normalizeWord(enumName.substring(iterationCnt)));
            return out.toString();
        }

        private static int indexIn(CharSequence sequence, int start) {
            int length = sequence.length();
            for (int i = start; i < length; i++) {
                if ('_' == sequence.charAt(i)) {
                    return i;
                }
            }
            return -1;
        }

        private static String normalizeWord(String word) {
            int length = word.length();
            if (length == 0) {
                return word;
            }
            return new StringBuilder(length).append(charToUpperCaseIfLower(word.charAt(0))).append(toLowerCase(word.substring(1))).toString();
        }

        private static String toLowerCase(String string) {
            int length = string.length();
            StringBuilder builder = new StringBuilder(length);
            for (int i = 0; i < length; i++) {
                builder.append(charToLowerCaseIfUpper(string.charAt(i)));
            }
            return builder.toString();
        }

        private static char charToUpperCaseIfLower(char c) {
            return Character.isLowerCase(c) ? Character.toUpperCase(c) : c;
        }

        private static char charToLowerCaseIfUpper(char c) {
            return Character.isUpperCase(c) ? Character.toLowerCase(c) : c;
        }
    }
}
