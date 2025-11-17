package org.springframework.http.codec.protobuf;

import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/protobuf/ProtobufCodecSupport.class */
public abstract class ProtobufCodecSupport {
    static final MimeType[] MIME_TYPES = {new MimeType("application", "x-protobuf"), new MimeType("application", "octet-stream"), new MimeType("application", "vnd.google.protobuf")};
    static final String DELIMITED_KEY = "delimited";
    static final String DELIMITED_VALUE = "true";

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean supportsMimeType(@Nullable MimeType mimeType) {
        if (mimeType == null) {
            return true;
        }
        for (MimeType m : MIME_TYPES) {
            if (m.isCompatibleWith(mimeType)) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public List<MimeType> getMimeTypes() {
        return Arrays.asList(MIME_TYPES);
    }
}
