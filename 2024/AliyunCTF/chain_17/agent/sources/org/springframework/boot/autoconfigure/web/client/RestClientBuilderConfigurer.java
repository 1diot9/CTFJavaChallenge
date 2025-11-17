package org.springframework.boot.autoconfigure.web.client;

import java.util.List;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.web.client.RestClient;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/client/RestClientBuilderConfigurer.class */
public class RestClientBuilderConfigurer {
    private List<RestClientCustomizer> customizers;

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setRestClientCustomizers(List<RestClientCustomizer> customizers) {
        this.customizers = customizers;
    }

    public RestClient.Builder configure(RestClient.Builder builder) {
        applyCustomizers(builder);
        return builder;
    }

    private void applyCustomizers(RestClient.Builder builder) {
        if (this.customizers != null) {
            for (RestClientCustomizer customizer : this.customizers) {
                customizer.customize(builder);
            }
        }
    }
}
