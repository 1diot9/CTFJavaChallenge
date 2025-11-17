package org.apache.coyote.http2;

import java.nio.MappedByteBuffer;
import java.nio.file.Path;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http2/SendfileData.class */
class SendfileData {
    Path path;
    Stream stream;
    MappedByteBuffer mappedBuffer;
    long left;
    int streamReservation;
    int connectionReservation;
    long pos;
    long end;
}
