package org.springframework.beans;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/PropertyMatches.class */
public abstract class PropertyMatches {
    public static final int DEFAULT_MAX_DISTANCE = 2;
    private final String propertyName;
    private final String[] possibleMatches;

    public abstract String buildErrorMessage();

    public static PropertyMatches forProperty(String propertyName, Class<?> beanClass) {
        return forProperty(propertyName, beanClass, 2);
    }

    public static PropertyMatches forProperty(String propertyName, Class<?> beanClass, int maxDistance) {
        return new BeanPropertyMatches(propertyName, beanClass, maxDistance);
    }

    public static PropertyMatches forField(String propertyName, Class<?> beanClass) {
        return forField(propertyName, beanClass, 2);
    }

    public static PropertyMatches forField(String propertyName, Class<?> beanClass, int maxDistance) {
        return new FieldPropertyMatches(propertyName, beanClass, maxDistance);
    }

    private PropertyMatches(String propertyName, String[] possibleMatches) {
        this.propertyName = propertyName;
        this.possibleMatches = possibleMatches;
    }

    public String getPropertyName() {
        return this.propertyName;
    }

    public String[] getPossibleMatches() {
        return this.possibleMatches;
    }

    protected void appendHintMessage(StringBuilder msg) {
        msg.append("Did you mean ");
        for (int i = 0; i < this.possibleMatches.length; i++) {
            msg.append('\'');
            msg.append(this.possibleMatches[i]);
            if (i < this.possibleMatches.length - 2) {
                msg.append("', ");
            } else if (i == this.possibleMatches.length - 2) {
                msg.append("', or ");
            }
        }
        msg.append("'?");
    }

    private static int calculateStringDistance(String s1, String s2) {
        int i;
        if (s1.isEmpty()) {
            return s2.length();
        }
        if (s2.isEmpty()) {
            return s1.length();
        }
        int[][] d = new int[s1.length() + 1][s2.length() + 1];
        for (int i2 = 0; i2 <= s1.length(); i2++) {
            d[i2][0] = i2;
        }
        for (int j = 0; j <= s2.length(); j++) {
            d[0][j] = j;
        }
        for (int i3 = 1; i3 <= s1.length(); i3++) {
            char c1 = s1.charAt(i3 - 1);
            for (int j2 = 1; j2 <= s2.length(); j2++) {
                char c2 = s2.charAt(j2 - 1);
                if (c1 == c2) {
                    i = 0;
                } else {
                    i = 1;
                }
                int cost = i;
                d[i3][j2] = Math.min(Math.min(d[i3 - 1][j2] + 1, d[i3][j2 - 1] + 1), d[i3 - 1][j2 - 1] + cost);
            }
        }
        return d[s1.length()][s2.length()];
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/PropertyMatches$BeanPropertyMatches.class */
    public static class BeanPropertyMatches extends PropertyMatches {
        public BeanPropertyMatches(String propertyName, Class<?> beanClass, int maxDistance) {
            super(propertyName, calculateMatches(propertyName, BeanUtils.getPropertyDescriptors(beanClass), maxDistance));
        }

        private static String[] calculateMatches(String name, PropertyDescriptor[] descriptors, int maxDistance) {
            List<String> candidates = new ArrayList<>();
            for (PropertyDescriptor pd : descriptors) {
                if (pd.getWriteMethod() != null) {
                    String possibleAlternative = pd.getName();
                    if (PropertyMatches.calculateStringDistance(name, possibleAlternative) <= maxDistance) {
                        candidates.add(possibleAlternative);
                    }
                }
            }
            Collections.sort(candidates);
            return StringUtils.toStringArray(candidates);
        }

        @Override // org.springframework.beans.PropertyMatches
        public String buildErrorMessage() {
            StringBuilder msg = new StringBuilder(160);
            msg.append("Bean property '").append(getPropertyName()).append("' is not writable or has an invalid setter method. ");
            if (!ObjectUtils.isEmpty((Object[]) getPossibleMatches())) {
                appendHintMessage(msg);
            } else {
                msg.append("Does the parameter type of the setter match the return type of the getter?");
            }
            return msg.toString();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/PropertyMatches$FieldPropertyMatches.class */
    public static class FieldPropertyMatches extends PropertyMatches {
        public FieldPropertyMatches(String propertyName, Class<?> beanClass, int maxDistance) {
            super(propertyName, calculateMatches(propertyName, beanClass, maxDistance));
        }

        private static String[] calculateMatches(final String name, Class<?> clazz, final int maxDistance) {
            List<String> candidates = new ArrayList<>();
            ReflectionUtils.doWithFields(clazz, field -> {
                String possibleAlternative = field.getName();
                if (PropertyMatches.calculateStringDistance(name, possibleAlternative) <= maxDistance) {
                    candidates.add(possibleAlternative);
                }
            });
            Collections.sort(candidates);
            return StringUtils.toStringArray(candidates);
        }

        @Override // org.springframework.beans.PropertyMatches
        public String buildErrorMessage() {
            StringBuilder msg = new StringBuilder(80);
            msg.append("Bean property '").append(getPropertyName()).append("' has no matching field.");
            if (!ObjectUtils.isEmpty((Object[]) getPossibleMatches())) {
                msg.append(' ');
                appendHintMessage(msg);
            }
            return msg.toString();
        }
    }
}
