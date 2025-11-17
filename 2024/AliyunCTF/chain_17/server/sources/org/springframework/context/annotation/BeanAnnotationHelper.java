package org.springframework.context.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Map;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.util.ConcurrentReferenceHashMap;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/context/annotation/BeanAnnotationHelper.class */
abstract class BeanAnnotationHelper {
    private static final Map<Method, String> beanNameCache = new ConcurrentReferenceHashMap();
    private static final Map<Method, Boolean> scopedProxyCache = new ConcurrentReferenceHashMap();

    BeanAnnotationHelper() {
    }

    public static boolean isBeanAnnotated(Method method) {
        return AnnotatedElementUtils.hasAnnotation(method, Bean.class);
    }

    public static String determineBeanNameFor(Method beanMethod) {
        String beanName = beanNameCache.get(beanMethod);
        if (beanName == null) {
            beanName = beanMethod.getName();
            AnnotationAttributes bean = AnnotatedElementUtils.findMergedAnnotationAttributes((AnnotatedElement) beanMethod, (Class<? extends Annotation>) Bean.class, false, false);
            if (bean != null) {
                String[] names = bean.getStringArray("name");
                if (names.length > 0) {
                    beanName = names[0];
                }
            }
            beanNameCache.put(beanMethod, beanName);
        }
        return beanName;
    }

    public static boolean isScopedProxy(Method beanMethod) {
        Boolean scopedProxy = scopedProxyCache.get(beanMethod);
        if (scopedProxy == null) {
            AnnotationAttributes scope = AnnotatedElementUtils.findMergedAnnotationAttributes((AnnotatedElement) beanMethod, (Class<? extends Annotation>) Scope.class, false, false);
            scopedProxy = Boolean.valueOf((scope == null || scope.getEnum("proxyMode") == ScopedProxyMode.NO) ? false : true);
            scopedProxyCache.put(beanMethod, scopedProxy);
        }
        return scopedProxy.booleanValue();
    }
}
