package org.springframework.aot.hint.predicate;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.TypeReference;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/hint/predicate/ProxyHintsPredicates.class */
public class ProxyHintsPredicates {
    public Predicate<RuntimeHints> forInterfaces(Class<?>... interfaces) {
        Assert.notEmpty(interfaces, "'interfaces' should not be empty");
        return forInterfaces((TypeReference[]) Arrays.stream(interfaces).map(TypeReference::of).toArray(x$0 -> {
            return new TypeReference[x$0];
        }));
    }

    public Predicate<RuntimeHints> forInterfaces(TypeReference... interfaces) {
        Assert.notEmpty(interfaces, "'interfaces' should not be empty");
        List<TypeReference> interfaceList = Arrays.asList(interfaces);
        return hints -> {
            return hints.proxies().jdkProxyHints().anyMatch(proxyHint -> {
                return proxyHint.getProxiedInterfaces().equals(interfaceList);
            });
        };
    }
}
