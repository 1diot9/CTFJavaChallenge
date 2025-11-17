package org.apache.coyote.http11.filters;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.apache.coyote.BadRequestException;
import org.apache.coyote.InputBuffer;
import org.apache.coyote.Request;
import org.apache.coyote.http11.InputFilter;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.buf.HexUtils;
import org.apache.tomcat.util.http.parser.HttpParser;
import org.apache.tomcat.util.net.ApplicationBufferHandler;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http11/filters/ChunkedInputFilter.class */
public class ChunkedInputFilter implements InputFilter, ApplicationBufferHandler {
    protected static final String ENCODING_NAME = "chunked";
    protected InputBuffer buffer;
    protected ByteBuffer readChunk;
    private Request request;
    private final long maxExtensionSize;
    private final int maxTrailerSize;
    private long extensionSize;
    private final int maxSwallowSize;
    private boolean error;
    private final Set<String> allowedTrailerHeaders;
    private static final StringManager sm = StringManager.getManager((Class<?>) ChunkedInputFilter.class);
    protected static final ByteChunk ENCODING = new ByteChunk();
    protected int remaining = 0;
    protected boolean endChunk = false;
    protected final ByteChunk trailingHeaders = new ByteChunk();
    protected boolean needCRLFParse = false;

    static {
        ENCODING.setBytes("chunked".getBytes(StandardCharsets.ISO_8859_1), 0, "chunked".length());
    }

    public ChunkedInputFilter(int maxTrailerSize, Set<String> allowedTrailerHeaders, int maxExtensionSize, int maxSwallowSize) {
        this.trailingHeaders.setLimit(maxTrailerSize);
        this.allowedTrailerHeaders = allowedTrailerHeaders;
        this.maxExtensionSize = maxExtensionSize;
        this.maxTrailerSize = maxTrailerSize;
        this.maxSwallowSize = maxSwallowSize;
    }

    @Override // org.apache.coyote.InputBuffer
    public int doRead(ApplicationBufferHandler handler) throws IOException {
        int result;
        if (this.endChunk) {
            return -1;
        }
        checkError();
        if (this.needCRLFParse) {
            this.needCRLFParse = false;
            parseCRLF(false);
        }
        if (this.remaining <= 0) {
            if (!parseChunkHeader()) {
                throwBadRequestException(sm.getString("chunkedInputFilter.invalidHeader"));
            }
            if (this.endChunk) {
                parseEndChunk();
                return -1;
            }
        }
        if ((this.readChunk == null || this.readChunk.position() >= this.readChunk.limit()) && readBytes() < 0) {
            throwEOFException(sm.getString("chunkedInputFilter.eos"));
        }
        if (this.remaining > this.readChunk.remaining()) {
            result = this.readChunk.remaining();
            this.remaining -= result;
            if (this.readChunk != handler.getByteBuffer()) {
                handler.setByteBuffer(this.readChunk.duplicate());
            }
            this.readChunk.position(this.readChunk.limit());
        } else {
            result = this.remaining;
            if (this.readChunk != handler.getByteBuffer()) {
                handler.setByteBuffer(this.readChunk.duplicate());
                handler.getByteBuffer().limit(this.readChunk.position() + this.remaining);
            }
            this.readChunk.position(this.readChunk.position() + this.remaining);
            this.remaining = 0;
            if (this.readChunk.position() + 1 >= this.readChunk.limit()) {
                this.needCRLFParse = true;
            } else {
                parseCRLF(false);
            }
        }
        return result;
    }

    @Override // org.apache.coyote.http11.InputFilter
    public void setRequest(Request request) {
        this.request = request;
    }

    @Override // org.apache.coyote.http11.InputFilter
    public long end() throws IOException {
        long swallowed = 0;
        while (true) {
            int read = doRead(this);
            if (read >= 0) {
                swallowed += read;
                if (this.maxSwallowSize > -1 && swallowed > this.maxSwallowSize) {
                    throwBadRequestException(sm.getString("inputFilter.maxSwallow"));
                }
            } else {
                return this.readChunk.remaining();
            }
        }
    }

    @Override // org.apache.coyote.InputBuffer
    public int available() {
        int available = 0;
        if (this.readChunk != null) {
            available = this.readChunk.remaining();
        }
        if (available == 0) {
            return this.buffer.available();
        }
        return available;
    }

    @Override // org.apache.coyote.http11.InputFilter
    public void setBuffer(InputBuffer buffer) {
        this.buffer = buffer;
    }

    @Override // org.apache.coyote.http11.InputFilter
    public void recycle() {
        this.remaining = 0;
        if (this.readChunk != null) {
            this.readChunk.position(0).limit(0);
        }
        this.endChunk = false;
        this.needCRLFParse = false;
        this.trailingHeaders.recycle();
        this.trailingHeaders.setLimit(this.maxTrailerSize);
        this.extensionSize = 0L;
        this.error = false;
    }

    @Override // org.apache.coyote.http11.InputFilter
    public ByteChunk getEncodingName() {
        return ENCODING;
    }

    @Override // org.apache.coyote.http11.InputFilter
    public boolean isFinished() {
        return this.endChunk;
    }

    protected int readBytes() throws IOException {
        return this.buffer.doRead(this);
    }

    protected boolean parseChunkHeader() throws IOException {
        int result = 0;
        boolean eol = false;
        int readDigit = 0;
        boolean extension = false;
        while (!eol) {
            if ((this.readChunk == null || this.readChunk.position() >= this.readChunk.limit()) && readBytes() <= 0) {
                return false;
            }
            byte chr = this.readChunk.get(this.readChunk.position());
            if (chr == 13 || chr == 10) {
                parseCRLF(false);
                eol = true;
            } else if (chr == 59 && !extension) {
                extension = true;
                this.extensionSize++;
            } else if (!extension) {
                int charValue = HexUtils.getDec(chr);
                if (charValue != -1 && readDigit < 8) {
                    readDigit++;
                    result = (result << 4) | charValue;
                } else {
                    return false;
                }
            } else {
                this.extensionSize++;
                if (this.maxExtensionSize > -1 && this.extensionSize > this.maxExtensionSize) {
                    throwBadRequestException(sm.getString("chunkedInputFilter.maxExtension"));
                }
            }
            if (!eol) {
                this.readChunk.position(this.readChunk.position() + 1);
            }
        }
        if (readDigit == 0 || result < 0) {
            return false;
        }
        if (result == 0) {
            this.endChunk = true;
        }
        this.remaining = result;
        return true;
    }

    protected void parseCRLF(boolean tolerant) throws IOException {
        boolean eol = false;
        boolean crfound = false;
        while (!eol) {
            if ((this.readChunk == null || this.readChunk.position() >= this.readChunk.limit()) && readBytes() <= 0) {
                throwBadRequestException(sm.getString("chunkedInputFilter.invalidCrlfNoData"));
            }
            byte chr = this.readChunk.get(this.readChunk.position());
            if (chr == 13) {
                if (crfound) {
                    throwBadRequestException(sm.getString("chunkedInputFilter.invalidCrlfCRCR"));
                }
                crfound = true;
            } else if (chr == 10) {
                if (!tolerant && !crfound) {
                    throwBadRequestException(sm.getString("chunkedInputFilter.invalidCrlfNoCR"));
                }
                eol = true;
            } else {
                throwBadRequestException(sm.getString("chunkedInputFilter.invalidCrlf"));
            }
            this.readChunk.position(this.readChunk.position() + 1);
        }
    }

    protected void parseEndChunk() throws IOException {
        do {
        } while (parseHeader());
    }

    private boolean parseHeader() throws IOException {
        Map<String, String> headers = this.request.getTrailerFields();
        if ((this.readChunk == null || this.readChunk.position() >= this.readChunk.limit()) && readBytes() < 0) {
            throwEOFException(sm.getString("chunkedInputFilter.eosTrailer"));
        }
        byte chr = this.readChunk.get(this.readChunk.position());
        if (chr == 13 || chr == 10) {
            parseCRLF(false);
            return false;
        }
        int startPos = this.trailingHeaders.getEnd();
        boolean colon = false;
        while (!colon) {
            if ((this.readChunk == null || this.readChunk.position() >= this.readChunk.limit()) && readBytes() < 0) {
                throwEOFException(sm.getString("chunkedInputFilter.eosTrailer"));
            }
            byte chr2 = this.readChunk.get(this.readChunk.position());
            if (chr2 >= 65 && chr2 <= 90) {
                chr2 = (byte) (chr2 - (-32));
            }
            if (chr2 == 58) {
                colon = true;
            } else if (!HttpParser.isToken(chr2)) {
                throwBadRequestException(sm.getString("chunkedInputFilter.invalidTrailerHeaderName"));
            } else if (this.trailingHeaders.getEnd() >= this.trailingHeaders.getLimit()) {
                throwBadRequestException(sm.getString("chunkedInputFilter.maxTrailer"));
            } else {
                this.trailingHeaders.append(chr2);
            }
            this.readChunk.position(this.readChunk.position() + 1);
        }
        int colonPos = this.trailingHeaders.getEnd();
        boolean eol = false;
        boolean validLine = true;
        int lastSignificantChar = 0;
        while (validLine) {
            boolean space = true;
            while (space) {
                if ((this.readChunk == null || this.readChunk.position() >= this.readChunk.limit()) && readBytes() < 0) {
                    throwEOFException(sm.getString("chunkedInputFilter.eosTrailer"));
                }
                byte chr3 = this.readChunk.get(this.readChunk.position());
                if (chr3 == 32 || chr3 == 9) {
                    this.readChunk.position(this.readChunk.position() + 1);
                    int newlimit = this.trailingHeaders.getLimit() - 1;
                    if (this.trailingHeaders.getEnd() > newlimit) {
                        throwBadRequestException(sm.getString("chunkedInputFilter.maxTrailer"));
                    }
                    this.trailingHeaders.setLimit(newlimit);
                } else {
                    space = false;
                }
            }
            while (!eol) {
                if ((this.readChunk == null || this.readChunk.position() >= this.readChunk.limit()) && readBytes() < 0) {
                    throwEOFException(sm.getString("chunkedInputFilter.eosTrailer"));
                }
                byte chr4 = this.readChunk.get(this.readChunk.position());
                if (chr4 == 13 || chr4 == 10) {
                    parseCRLF(true);
                    eol = true;
                } else {
                    if (HttpParser.isControl(chr4) && chr4 != 9) {
                        throw new IOException(sm.getString("chunkedInputFilter.invalidTrailerHeaderValue"));
                    }
                    if (this.trailingHeaders.getEnd() >= this.trailingHeaders.getLimit()) {
                        throwBadRequestException(sm.getString("chunkedInputFilter.maxTrailer"));
                    } else if (chr4 == 32 || chr4 == 9) {
                        this.trailingHeaders.append(chr4);
                    } else {
                        this.trailingHeaders.append(chr4);
                        lastSignificantChar = this.trailingHeaders.getEnd();
                    }
                }
                if (!eol) {
                    this.readChunk.position(this.readChunk.position() + 1);
                }
            }
            if ((this.readChunk == null || this.readChunk.position() >= this.readChunk.limit()) && readBytes() < 0) {
                throwEOFException(sm.getString("chunkedInputFilter.eosTrailer"));
            }
            byte chr5 = this.readChunk.get(this.readChunk.position());
            if (chr5 != 32 && chr5 != 9) {
                validLine = false;
            } else if (this.trailingHeaders.getEnd() >= this.trailingHeaders.getLimit()) {
                throwBadRequestException(sm.getString("chunkedInputFilter.maxTrailer"));
            } else {
                eol = false;
                this.trailingHeaders.append(chr5);
            }
        }
        String headerName = new String(this.trailingHeaders.getBytes(), startPos, colonPos - startPos, StandardCharsets.ISO_8859_1).toLowerCase(Locale.ENGLISH);
        if (this.allowedTrailerHeaders.contains(headerName)) {
            String value = new String(this.trailingHeaders.getBytes(), colonPos, lastSignificantChar - colonPos, StandardCharsets.ISO_8859_1);
            headers.put(headerName, value);
            return true;
        }
        return true;
    }

    private void throwBadRequestException(String msg) throws IOException {
        this.error = true;
        throw new BadRequestException(msg);
    }

    private void throwEOFException(String msg) throws IOException {
        this.error = true;
        throw new EOFException(msg);
    }

    private void checkError() throws IOException {
        if (this.error) {
            throw new IOException(sm.getString("chunkedInputFilter.error"));
        }
    }

    @Override // org.apache.tomcat.util.net.ApplicationBufferHandler
    public void setByteBuffer(ByteBuffer buffer) {
        this.readChunk = buffer;
    }

    @Override // org.apache.tomcat.util.net.ApplicationBufferHandler
    public ByteBuffer getByteBuffer() {
        return this.readChunk;
    }

    @Override // org.apache.tomcat.util.net.ApplicationBufferHandler
    public void expand(int size) {
    }
}
