package org.springframework.aot.nativex;

import ch.qos.logback.core.joran.conditional.IfAction;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.aot.hint.JdkProxyHint;
import org.springframework.aot.hint.ProxyHints;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/nativex/ProxyHintsWriter.class */
public class ProxyHintsWriter {
    public static final ProxyHintsWriter INSTANCE = new ProxyHintsWriter();
    private static final Comparator<JdkProxyHint> JDK_PROXY_HINT_COMPARATOR = (left, right) -> {
        String leftSignature = (String) left.getProxiedInterfaces().stream().map((v0) -> {
            return v0.getCanonicalName();
        }).collect(Collectors.joining(","));
        String rightSignature = (String) right.getProxiedInterfaces().stream().map((v0) -> {
            return v0.getCanonicalName();
        }).collect(Collectors.joining(","));
        return leftSignature.compareTo(rightSignature);
    };

    ProxyHintsWriter() {
    }

    public void write(BasicJsonWriter writer, ProxyHints hints) {
        writer.writeArray(hints.jdkProxyHints().sorted(JDK_PROXY_HINT_COMPARATOR).map(this::toAttributes).toList());
    }

    private Map<String, Object> toAttributes(JdkProxyHint hint) {
        Map<String, Object> attributes = new LinkedHashMap<>();
        handleCondition(attributes, hint);
        attributes.put("interfaces", hint.getProxiedInterfaces());
        return attributes;
    }

    private void handleCondition(Map<String, Object> attributes, JdkProxyHint hint) {
        if (hint.getReachableType() != null) {
            Map<String, Object> conditionAttributes = new LinkedHashMap<>();
            conditionAttributes.put("typeReachable", hint.getReachableType());
            attributes.put(IfAction.CONDITION_ATTRIBUTE, conditionAttributes);
        }
    }
}
