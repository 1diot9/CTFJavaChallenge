package org.springframework.boot.autoconfigure.web.reactive.function.client;

import java.util.List;
import org.springframework.boot.web.codec.CodecCustomizer;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.web.reactive.function.client.WebClient;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/reactive/function/client/WebClientCodecCustomizer.class */
public class WebClientCodecCustomizer implements WebClientCustomizer {
    private final List<CodecCustomizer> codecCustomizers;

    public WebClientCodecCustomizer(List<CodecCustomizer> codecCustomizers) {
        this.codecCustomizers = codecCustomizers;
    }

    @Override // org.springframework.boot.web.reactive.function.client.WebClientCustomizer
    public void customize(WebClient.Builder webClientBuilder) {
        webClientBuilder.codecs(codecs -> {
            this.codecCustomizers.forEach(customizer -> {
                customizer.customize(codecs);
            });
        });
    }
}
