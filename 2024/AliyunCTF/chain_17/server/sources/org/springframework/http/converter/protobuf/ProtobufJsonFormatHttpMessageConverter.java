package org.springframework.http.converter.protobuf;

import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.util.JsonFormat;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/converter/protobuf/ProtobufJsonFormatHttpMessageConverter.class */
public class ProtobufJsonFormatHttpMessageConverter extends ProtobufHttpMessageConverter {
    public ProtobufJsonFormatHttpMessageConverter() {
        this(null, null);
    }

    public ProtobufJsonFormatHttpMessageConverter(@Nullable JsonFormat.Parser parser, @Nullable JsonFormat.Printer printer) {
        this(parser, printer, (ExtensionRegistry) null);
    }

    public ProtobufJsonFormatHttpMessageConverter(@Nullable JsonFormat.Parser parser, @Nullable JsonFormat.Printer printer, @Nullable ExtensionRegistry extensionRegistry) {
        super(new ProtobufHttpMessageConverter.ProtobufJavaUtilSupport(parser, printer), extensionRegistry);
    }
}
