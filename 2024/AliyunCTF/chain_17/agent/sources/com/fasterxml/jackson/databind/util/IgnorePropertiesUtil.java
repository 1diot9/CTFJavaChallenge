package com.fasterxml.jackson.databind.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/util/IgnorePropertiesUtil.class */
public class IgnorePropertiesUtil {
    public static boolean shouldIgnore(Object value, Collection<String> toIgnore, Collection<String> toInclude) {
        if (toIgnore == null && toInclude == null) {
            return false;
        }
        if (toInclude == null) {
            return toIgnore.contains(value);
        }
        return toIgnore == null ? !toInclude.contains(value) : !toInclude.contains(value) || toIgnore.contains(value);
    }

    public static Checker buildCheckerIfNeeded(Set<String> toIgnore, Set<String> toInclude) {
        if (toInclude == null && (toIgnore == null || toIgnore.isEmpty())) {
            return null;
        }
        return Checker.construct(toIgnore, toInclude);
    }

    public static Set<String> combineNamesToInclude(Set<String> prevToInclude, Set<String> newToInclude) {
        if (prevToInclude == null) {
            return newToInclude;
        }
        if (newToInclude == null) {
            return prevToInclude;
        }
        Set<String> result = new HashSet<>();
        for (String prop : newToInclude) {
            if (prevToInclude.contains(prop)) {
                result.add(prop);
            }
        }
        return result;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/util/IgnorePropertiesUtil$Checker.class */
    public static final class Checker implements Serializable {
        private static final long serialVersionUID = 1;
        private final Set<String> _toIgnore;
        private final Set<String> _toInclude;

        private Checker(Set<String> toIgnore, Set<String> toInclude) {
            this._toIgnore = toIgnore == null ? Collections.emptySet() : toIgnore;
            this._toInclude = toInclude;
        }

        public static Checker construct(Set<String> toIgnore, Set<String> toInclude) {
            return new Checker(toIgnore, toInclude);
        }

        public boolean shouldIgnore(Object propertyName) {
            return !(this._toInclude == null || this._toInclude.contains(propertyName)) || this._toIgnore.contains(propertyName);
        }
    }
}
