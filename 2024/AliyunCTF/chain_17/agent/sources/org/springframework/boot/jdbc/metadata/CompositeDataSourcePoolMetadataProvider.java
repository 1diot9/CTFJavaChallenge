package org.springframework.boot.jdbc.metadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.sql.DataSource;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/jdbc/metadata/CompositeDataSourcePoolMetadataProvider.class */
public class CompositeDataSourcePoolMetadataProvider implements DataSourcePoolMetadataProvider {
    private final List<DataSourcePoolMetadataProvider> providers;

    public CompositeDataSourcePoolMetadataProvider(Collection<? extends DataSourcePoolMetadataProvider> providers) {
        this.providers = providers != null ? Collections.unmodifiableList(new ArrayList(providers)) : Collections.emptyList();
    }

    @Override // org.springframework.boot.jdbc.metadata.DataSourcePoolMetadataProvider
    public DataSourcePoolMetadata getDataSourcePoolMetadata(DataSource dataSource) {
        for (DataSourcePoolMetadataProvider provider : this.providers) {
            DataSourcePoolMetadata metadata = provider.getDataSourcePoolMetadata(dataSource);
            if (metadata != null) {
                return metadata;
            }
        }
        return null;
    }
}
