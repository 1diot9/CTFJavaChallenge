package org.springframework.web.util;

import jakarta.servlet.jsp.tagext.Tag;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/util/TagUtils.class */
public abstract class TagUtils {
    public static final String SCOPE_PAGE = "page";
    public static final String SCOPE_REQUEST = "request";
    public static final String SCOPE_SESSION = "session";
    public static final String SCOPE_APPLICATION = "application";

    public static int getScope(String scope) {
        Assert.notNull(scope, "Scope to search for cannot be null");
        boolean z = -1;
        switch (scope.hashCode()) {
            case 1095692943:
                if (scope.equals("request")) {
                    z = false;
                    break;
                }
                break;
            case 1554253136:
                if (scope.equals("application")) {
                    z = 2;
                    break;
                }
                break;
            case 1984987798:
                if (scope.equals("session")) {
                    z = true;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
                return 2;
            case true:
                return 3;
            case true:
                return 4;
            default:
                return 1;
        }
    }

    public static boolean hasAncestorOfType(Tag tag, Class<?> ancestorTagClass) {
        Assert.notNull(tag, "Tag cannot be null");
        Assert.notNull(ancestorTagClass, "Ancestor tag class cannot be null");
        if (!Tag.class.isAssignableFrom(ancestorTagClass)) {
            throw new IllegalArgumentException("Class '" + ancestorTagClass.getName() + "' is not a valid Tag type");
        }
        Tag parent = tag.getParent();
        while (true) {
            Tag ancestor = parent;
            if (ancestor != null) {
                if (ancestorTagClass.isAssignableFrom(ancestor.getClass())) {
                    return true;
                }
                parent = ancestor.getParent();
            } else {
                return false;
            }
        }
    }

    public static void assertHasAncestorOfType(Tag tag, Class<?> ancestorTagClass, String tagName, String ancestorTagName) {
        Assert.hasText(tagName, "'tagName' must not be empty");
        Assert.hasText(ancestorTagName, "'ancestorTagName' must not be empty");
        if (!hasAncestorOfType(tag, ancestorTagClass)) {
            throw new IllegalStateException("The '" + tagName + "' tag can only be used inside a valid '" + ancestorTagName + "' tag.");
        }
    }
}
