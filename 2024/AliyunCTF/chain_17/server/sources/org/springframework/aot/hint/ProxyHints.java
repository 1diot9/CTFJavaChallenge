package org.springframework.aot.hint;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.springframework.aot.hint.JdkProxyHint;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/hint/ProxyHints.class */
public class ProxyHints {
    private final Set<JdkProxyHint> jdkProxies = new LinkedHashSet();

    public Stream<JdkProxyHint> jdkProxyHints() {
        return this.jdkProxies.stream();
    }

    public ProxyHints registerJdkProxy(Consumer<JdkProxyHint.Builder> jdkProxyHint) {
        JdkProxyHint.Builder builder = new JdkProxyHint.Builder();
        jdkProxyHint.accept(builder);
        this.jdkProxies.add(builder.build());
        return this;
    }

    public ProxyHints registerJdkProxy(TypeReference... proxiedInterfaces) {
        return registerJdkProxy(jdkProxyHint -> {
            jdkProxyHint.proxiedInterfaces(proxiedInterfaces);
        });
    }

    public ProxyHints registerJdkProxy(Class<?>... proxiedInterfaces) {
        return registerJdkProxy(jdkProxyHint -> {
            jdkProxyHint.proxiedInterfaces((Class<?>[]) proxiedInterfaces);
        });
    }
}
