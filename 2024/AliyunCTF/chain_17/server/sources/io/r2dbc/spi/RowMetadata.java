package io.r2dbc.spi;

import java.util.List;

/* loaded from: server.jar:BOOT-INF/lib/r2dbc-spi-1.0.0.RELEASE.jar:io/r2dbc/spi/RowMetadata.class */
public interface RowMetadata {
    ColumnMetadata getColumnMetadata(int i);

    ColumnMetadata getColumnMetadata(String str);

    List<? extends ColumnMetadata> getColumnMetadatas();

    default boolean contains(String columnName) {
        for (ColumnMetadata columnMetadata : getColumnMetadatas()) {
            if (columnMetadata.getName().equalsIgnoreCase(columnName)) {
                return true;
            }
        }
        return false;
    }
}
