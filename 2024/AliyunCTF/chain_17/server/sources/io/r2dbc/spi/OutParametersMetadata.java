package io.r2dbc.spi;

import java.util.List;

/* loaded from: server.jar:BOOT-INF/lib/r2dbc-spi-1.0.0.RELEASE.jar:io/r2dbc/spi/OutParametersMetadata.class */
public interface OutParametersMetadata {
    OutParameterMetadata getParameterMetadata(int i);

    OutParameterMetadata getParameterMetadata(String str);

    List<? extends OutParameterMetadata> getParameterMetadatas();
}
