package org.springframework.beans.factory.parsing;

import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/parsing/NullSourceExtractor.class */
public class NullSourceExtractor implements SourceExtractor {
    @Override // org.springframework.beans.factory.parsing.SourceExtractor
    @Nullable
    public Object extractSource(Object sourceCandidate, @Nullable Resource definitionResource) {
        return null;
    }
}
