package org.springframework.boot.autoconfigure.flyway;

import java.util.Arrays;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.flywaydb.core.api.migration.JavaMigration;
import org.flywaydb.core.internal.scanner.LocationScannerCache;
import org.flywaydb.core.internal.scanner.ResourceNameCache;
import org.flywaydb.core.internal.scanner.Scanner;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/flyway/NativeImageResourceProviderCustomizer.class */
class NativeImageResourceProviderCustomizer extends ResourceProviderCustomizer {
    NativeImageResourceProviderCustomizer() {
    }

    @Override // org.springframework.boot.autoconfigure.flyway.ResourceProviderCustomizer
    public void customize(FluentConfiguration configuration) {
        if (configuration.getResourceProvider() == null) {
            Scanner<JavaMigration> scanner = new Scanner<>(JavaMigration.class, Arrays.asList(configuration.getLocations()), configuration.getClassLoader(), configuration.getEncoding(), configuration.isDetectEncoding(), false, new ResourceNameCache(), new LocationScannerCache(), configuration.isFailOnMissingLocations());
            NativeImageResourceProvider resourceProvider = new NativeImageResourceProvider(scanner, configuration.getClassLoader(), Arrays.asList(configuration.getLocations()), configuration.getEncoding(), configuration.isFailOnMissingLocations());
            configuration.resourceProvider(resourceProvider);
        }
    }
}
