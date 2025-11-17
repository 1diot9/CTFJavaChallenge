package org.springframework.boot.origin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/origin/Origin.class */
public interface Origin {
    default Origin getParent() {
        return null;
    }

    static Origin from(Object source) {
        if (source instanceof Origin) {
            Origin origin = (Origin) source;
            return origin;
        }
        Origin origin2 = null;
        if (source instanceof OriginProvider) {
            OriginProvider originProvider = (OriginProvider) source;
            origin2 = originProvider.getOrigin();
        }
        if (origin2 == null && (source instanceof Throwable)) {
            Throwable throwable = (Throwable) source;
            return from(throwable.getCause());
        }
        return origin2;
    }

    static List<Origin> parentsFrom(Object source) {
        Origin origin = from(source);
        if (origin == null) {
            return Collections.emptyList();
        }
        Set<Origin> parents = new LinkedHashSet<>();
        Origin parent = origin.getParent();
        while (true) {
            Origin origin2 = parent;
            if (origin2 == null || parents.contains(origin2)) {
                break;
            }
            parents.add(origin2);
            parent = origin2.getParent();
        }
        return Collections.unmodifiableList(new ArrayList(parents));
    }
}
