package org.springframework.javapoet;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.lang.model.SourceVersion;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/javapoet/NameAllocator.class */
public final class NameAllocator implements Cloneable {
    private final Set<String> allocatedNames;
    private final Map<Object, String> tagToName;

    public NameAllocator() {
        this(new LinkedHashSet(), new LinkedHashMap());
    }

    private NameAllocator(LinkedHashSet<String> allocatedNames, LinkedHashMap<Object, String> tagToName) {
        this.allocatedNames = allocatedNames;
        this.tagToName = tagToName;
    }

    public String newName(String suggestion) {
        return newName(suggestion, UUID.randomUUID().toString());
    }

    public String newName(String suggestion, Object tag) {
        String suggestion2;
        Util.checkNotNull(suggestion, "suggestion", new Object[0]);
        Util.checkNotNull(tag, "tag", new Object[0]);
        String javaIdentifier = toJavaIdentifier(suggestion);
        while (true) {
            suggestion2 = javaIdentifier;
            if (!SourceVersion.isKeyword(suggestion2) && this.allocatedNames.add(suggestion2)) {
                break;
            }
            javaIdentifier = suggestion2 + "_";
        }
        String replaced = this.tagToName.put(tag, suggestion2);
        if (replaced != null) {
            this.tagToName.put(tag, replaced);
            throw new IllegalArgumentException("tag " + tag + " cannot be used for both '" + replaced + "' and '" + suggestion2 + "'");
        }
        return suggestion2;
    }

    public static String toJavaIdentifier(String suggestion) {
        StringBuilder result = new StringBuilder();
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 < suggestion.length()) {
                int codePoint = suggestion.codePointAt(i2);
                if (i2 == 0 && !Character.isJavaIdentifierStart(codePoint) && Character.isJavaIdentifierPart(codePoint)) {
                    result.append("_");
                }
                int validCodePoint = Character.isJavaIdentifierPart(codePoint) ? codePoint : 95;
                result.appendCodePoint(validCodePoint);
                i = i2 + Character.charCount(codePoint);
            } else {
                return result.toString();
            }
        }
    }

    public String get(Object tag) {
        String result = this.tagToName.get(tag);
        if (result == null) {
            throw new IllegalArgumentException("unknown tag: " + tag);
        }
        return result;
    }

    /* renamed from: clone, reason: merged with bridge method [inline-methods] */
    public NameAllocator m2676clone() {
        return new NameAllocator(new LinkedHashSet(this.allocatedNames), new LinkedHashMap(this.tagToName));
    }
}
