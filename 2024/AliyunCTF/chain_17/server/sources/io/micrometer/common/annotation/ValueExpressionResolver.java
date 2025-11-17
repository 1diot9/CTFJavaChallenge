package io.micrometer.common.annotation;

import io.micrometer.common.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/micrometer-commons-1.12.2.jar:io/micrometer/common/annotation/ValueExpressionResolver.class */
public interface ValueExpressionResolver {
    @Nullable
    String resolve(String str, @Nullable Object obj);
}
