package org.springframework.beans.factory.parsing;

import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;

@FunctionalInterface
/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/parsing/SourceExtractor.class */
public interface SourceExtractor {
    @Nullable
    Object extractSource(Object sourceCandidate, @Nullable Resource definingResource);
}
