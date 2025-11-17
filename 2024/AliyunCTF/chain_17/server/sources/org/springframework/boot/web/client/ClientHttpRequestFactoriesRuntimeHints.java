package org.springframework.boot.web.client;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.util.function.Supplier;
import org.springframework.aot.hint.ExecutableMode;
import org.springframework.aot.hint.ReflectionHints;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;
import org.springframework.http.client.AbstractClientHttpRequestFactoryWrapper;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.JettyClientHttpRequestFactory;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/client/ClientHttpRequestFactoriesRuntimeHints.class */
class ClientHttpRequestFactoriesRuntimeHints implements RuntimeHintsRegistrar {
    ClientHttpRequestFactoriesRuntimeHints() {
    }

    @Override // org.springframework.aot.hint.RuntimeHintsRegistrar
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        if (ClassUtils.isPresent("org.springframework.http.client.ClientHttpRequestFactory", classLoader)) {
            registerHints(hints.reflection(), classLoader);
        }
    }

    private void registerHints(ReflectionHints hints, ClassLoader classLoader) {
        hints.registerField(findField(AbstractClientHttpRequestFactoryWrapper.class, "requestFactory"));
        hints.registerTypeIfPresent(classLoader, "org.apache.hc.client5.http.impl.classic.HttpClients", typeHint -> {
            typeHint.onReachableType(TypeReference.of("org.apache.hc.client5.http.impl.classic.HttpClients"));
            registerReflectionHints(hints, HttpComponentsClientHttpRequestFactory.class);
        });
        hints.registerTypeIfPresent(classLoader, "org.eclipse.jetty.client.HttpClient", typeHint2 -> {
            typeHint2.onReachableType(TypeReference.of("org.eclipse.jetty.client.HttpClient"));
            registerReflectionHints(hints, JettyClientHttpRequestFactory.class, Long.TYPE);
        });
        hints.registerType(SimpleClientHttpRequestFactory.class, typeHint3 -> {
            typeHint3.onReachableType(HttpURLConnection.class);
            registerReflectionHints(hints, SimpleClientHttpRequestFactory.class);
        });
        registerOkHttpHints(hints, classLoader);
    }

    @Deprecated(since = "3.2.0", forRemoval = true)
    private void registerOkHttpHints(ReflectionHints hints, ClassLoader classLoader) {
        hints.registerTypeIfPresent(classLoader, "okhttp3.OkHttpClient", typeHint -> {
            typeHint.onReachableType(TypeReference.of("okhttp3.OkHttpClient"));
            registerReflectionHints(hints, OkHttp3ClientHttpRequestFactory.class);
        });
    }

    private void registerReflectionHints(ReflectionHints hints, Class<? extends ClientHttpRequestFactory> requestFactoryType) {
        registerReflectionHints(hints, requestFactoryType, Integer.TYPE);
    }

    private void registerReflectionHints(ReflectionHints hints, Class<? extends ClientHttpRequestFactory> requestFactoryType, Class<?> readTimeoutType) {
        registerMethod(hints, requestFactoryType, "setConnectTimeout", Integer.TYPE);
        registerMethod(hints, requestFactoryType, "setReadTimeout", readTimeoutType);
    }

    private void registerMethod(ReflectionHints hints, Class<? extends ClientHttpRequestFactory> requestFactoryType, String methodName, Class<?>... parameterTypes) {
        Method method = ReflectionUtils.findMethod(requestFactoryType, methodName, parameterTypes);
        if (method != null) {
            hints.registerMethod(method, ExecutableMode.INVOKE);
        }
    }

    private Field findField(Class<?> type, String name) {
        Field field = ReflectionUtils.findField(type, name);
        Assert.state(field != null, (Supplier<String>) () -> {
            return "Unable to find field '%s' on %s".formatted(type.getName(), name);
        });
        return field;
    }
}
