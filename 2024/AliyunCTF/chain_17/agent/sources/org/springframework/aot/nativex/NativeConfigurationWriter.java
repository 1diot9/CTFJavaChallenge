package org.springframework.aot.nativex;

import java.util.function.Consumer;
import org.springframework.aot.hint.ProxyHints;
import org.springframework.aot.hint.ReflectionHints;
import org.springframework.aot.hint.ResourceHints;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.SerializationHints;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/nativex/NativeConfigurationWriter.class */
public abstract class NativeConfigurationWriter {
    protected abstract void writeTo(String fileName, Consumer<BasicJsonWriter> writer);

    public void write(RuntimeHints hints) {
        if (hints.serialization().javaSerializationHints().findAny().isPresent()) {
            writeSerializationHints(hints.serialization());
        }
        if (hints.proxies().jdkProxyHints().findAny().isPresent()) {
            writeProxyHints(hints.proxies());
        }
        if (hints.reflection().typeHints().findAny().isPresent()) {
            writeReflectionHints(hints.reflection());
        }
        if (hints.resources().resourcePatternHints().findAny().isPresent() || hints.resources().resourceBundleHints().findAny().isPresent()) {
            writeResourceHints(hints.resources());
        }
        if (hints.jni().typeHints().findAny().isPresent()) {
            writeJniHints(hints.jni());
        }
    }

    private void writeSerializationHints(SerializationHints hints) {
        writeTo("serialization-config.json", writer -> {
            SerializationHintsWriter.INSTANCE.write(writer, hints);
        });
    }

    private void writeProxyHints(ProxyHints hints) {
        writeTo("proxy-config.json", writer -> {
            ProxyHintsWriter.INSTANCE.write(writer, hints);
        });
    }

    private void writeReflectionHints(ReflectionHints hints) {
        writeTo("reflect-config.json", writer -> {
            ReflectionHintsWriter.INSTANCE.write(writer, hints);
        });
    }

    private void writeResourceHints(ResourceHints hints) {
        writeTo("resource-config.json", writer -> {
            ResourceHintsWriter.INSTANCE.write(writer, hints);
        });
    }

    private void writeJniHints(ReflectionHints hints) {
        writeTo("jni-config.json", writer -> {
            ReflectionHintsWriter.INSTANCE.write(writer, hints);
        });
    }
}
