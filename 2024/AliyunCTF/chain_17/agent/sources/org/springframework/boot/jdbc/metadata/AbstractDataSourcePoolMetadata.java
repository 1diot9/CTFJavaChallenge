package org.springframework.boot.jdbc.metadata;

import javax.sql.DataSource;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/jdbc/metadata/AbstractDataSourcePoolMetadata.class */
public abstract class AbstractDataSourcePoolMetadata<T extends DataSource> implements DataSourcePoolMetadata {
    private final T dataSource;

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractDataSourcePoolMetadata(T dataSource) {
        this.dataSource = dataSource;
    }

    @Override // org.springframework.boot.jdbc.metadata.DataSourcePoolMetadata
    public Float getUsage() {
        Integer maxSize = getMax();
        Integer currentSize = getActive();
        if (maxSize == null || currentSize == null) {
            return null;
        }
        if (maxSize.intValue() < 0) {
            return Float.valueOf(-1.0f);
        }
        if (currentSize.intValue() == 0) {
            return Float.valueOf(0.0f);
        }
        return Float.valueOf(currentSize.intValue() / maxSize.intValue());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final T getDataSource() {
        return this.dataSource;
    }
}
