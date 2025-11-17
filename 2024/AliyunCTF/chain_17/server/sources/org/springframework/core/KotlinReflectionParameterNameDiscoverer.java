package org.springframework.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import kotlin.reflect.KFunction;
import kotlin.reflect.KParameter;
import kotlin.reflect.jvm.ReflectJvmMapping;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/KotlinReflectionParameterNameDiscoverer.class */
public class KotlinReflectionParameterNameDiscoverer implements ParameterNameDiscoverer {
    @Override // org.springframework.core.ParameterNameDiscoverer
    @Nullable
    public String[] getParameterNames(Method method) {
        if (!KotlinDetector.isKotlinType(method.getDeclaringClass())) {
            return null;
        }
        try {
            KFunction<?> function = ReflectJvmMapping.getKotlinFunction(method);
            if (function != null) {
                return getParameterNames(function.getParameters());
            }
            return null;
        } catch (UnsupportedOperationException e) {
            return null;
        }
    }

    @Override // org.springframework.core.ParameterNameDiscoverer
    @Nullable
    public String[] getParameterNames(Constructor<?> ctor) {
        if (ctor.getDeclaringClass().isEnum() || !KotlinDetector.isKotlinType(ctor.getDeclaringClass())) {
            return null;
        }
        try {
            KFunction<?> function = ReflectJvmMapping.getKotlinFunction(ctor);
            if (function != null) {
                return getParameterNames(function.getParameters());
            }
            return null;
        } catch (UnsupportedOperationException e) {
            return null;
        }
    }

    @Nullable
    private String[] getParameterNames(List<KParameter> parameters) {
        String[] parameterNames = (String[]) parameters.stream().filter(p -> {
            return KParameter.Kind.VALUE.equals(p.getKind()) || KParameter.Kind.EXTENSION_RECEIVER.equals(p.getKind());
        }).map(p2 -> {
            return KParameter.Kind.EXTENSION_RECEIVER.equals(p2.getKind()) ? "$receiver" : p2.getName();
        }).toArray(x$0 -> {
            return new String[x$0];
        });
        for (String parameterName : parameterNames) {
            if (parameterName == null) {
                return null;
            }
        }
        return parameterNames;
    }
}
