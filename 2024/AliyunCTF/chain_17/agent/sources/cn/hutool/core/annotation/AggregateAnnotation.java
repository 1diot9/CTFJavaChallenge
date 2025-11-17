package cn.hutool.core.annotation;

import java.lang.annotation.Annotation;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/annotation/AggregateAnnotation.class */
public interface AggregateAnnotation extends Annotation {
    boolean isAnnotationPresent(Class<? extends Annotation> cls);

    Annotation[] getAnnotations();
}
