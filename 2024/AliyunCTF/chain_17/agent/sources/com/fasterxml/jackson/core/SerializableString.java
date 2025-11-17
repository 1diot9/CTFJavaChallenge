package com.fasterxml.jackson.core;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/SerializableString.class */
public interface SerializableString {
    String getValue();

    int charLength();

    char[] asQuotedChars();

    byte[] asUnquotedUTF8();

    byte[] asQuotedUTF8();

    int appendQuotedUTF8(byte[] bArr, int i);

    int appendQuoted(char[] cArr, int i);

    int appendUnquotedUTF8(byte[] bArr, int i);

    int appendUnquoted(char[] cArr, int i);

    int writeQuotedUTF8(OutputStream outputStream) throws IOException;

    int writeUnquotedUTF8(OutputStream outputStream) throws IOException;

    int putQuotedUTF8(ByteBuffer byteBuffer) throws IOException;

    int putUnquotedUTF8(ByteBuffer byteBuffer) throws IOException;
}
