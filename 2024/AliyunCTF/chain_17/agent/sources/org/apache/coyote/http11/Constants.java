package org.apache.coyote.http11;

import org.apache.tomcat.util.buf.ByteChunk;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http11/Constants.class */
public final class Constants {
    public static final int DEFAULT_CONNECTION_TIMEOUT = 60000;
    public static final String CRLF = "\r\n";
    public static final byte CR = 13;
    public static final byte LF = 10;
    public static final byte SP = 32;
    public static final byte HT = 9;
    public static final byte COLON = 58;
    public static final byte SEMI_COLON = 59;
    public static final byte A = 65;
    public static final byte a = 97;
    public static final byte Z = 90;
    public static final byte QUESTION = 63;
    public static final byte LC_OFFSET = -32;
    public static final String CONNECTION = "Connection";
    public static final String CLOSE = "close";
    public static final String KEEP_ALIVE_HEADER_VALUE_TOKEN = "keep-alive";
    public static final String CHUNKED = "chunked";
    public static final String TRANSFERENCODING = "Transfer-Encoding";
    public static final String KEEP_ALIVE_HEADER_NAME = "Keep-Alive";
    public static final int IDENTITY_FILTER = 0;
    public static final int CHUNKED_FILTER = 1;
    public static final int VOID_FILTER = 2;
    public static final int GZIP_FILTER = 3;
    public static final int BUFFERED_FILTER = 3;
    public static final String HTTP_10 = "HTTP/1.0";
    public static final String HTTP_11 = "HTTP/1.1";
    public static final byte[] ACK_BYTES = ByteChunk.convertToBytes("HTTP/1.1 100 \r\n\r\n");
    public static final byte[] _200_BYTES = ByteChunk.convertToBytes("200");
    public static final byte[] _400_BYTES = ByteChunk.convertToBytes("400");
    public static final byte[] _404_BYTES = ByteChunk.convertToBytes("404");
    public static final byte[] HTTP_11_BYTES = ByteChunk.convertToBytes("HTTP/1.1");
}
