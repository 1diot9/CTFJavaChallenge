package org.springframework.boot.autoconfigure.influx;

import java.util.function.Supplier;
import okhttp3.OkHttpClient;

@FunctionalInterface
@Deprecated(since = "3.2.0", forRemoval = true)
/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/influx/InfluxDbOkHttpClientBuilderProvider.class */
public interface InfluxDbOkHttpClientBuilderProvider extends Supplier<OkHttpClient.Builder> {
}
