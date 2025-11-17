package org.springframework.core.codec;

import java.util.Arrays;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/codec/AbstractEncoder.class */
public abstract class AbstractEncoder<T> implements Encoder<T> {
    private final List<MimeType> encodableMimeTypes;
    protected Log logger = LogFactory.getLog(getClass());

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractEncoder(MimeType... supportedMimeTypes) {
        this.encodableMimeTypes = Arrays.asList(supportedMimeTypes);
    }

    public void setLogger(Log logger) {
        this.logger = logger;
    }

    public Log getLogger() {
        return this.logger;
    }

    @Override // org.springframework.core.codec.Encoder
    public List<MimeType> getEncodableMimeTypes() {
        return this.encodableMimeTypes;
    }

    @Override // org.springframework.core.codec.Encoder
    public boolean canEncode(ResolvableType elementType, @Nullable MimeType mimeType) {
        if (mimeType == null) {
            return true;
        }
        for (MimeType candidate : this.encodableMimeTypes) {
            if (candidate.isCompatibleWith(mimeType)) {
                return true;
            }
        }
        return false;
    }
}
