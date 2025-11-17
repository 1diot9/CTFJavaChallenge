package org.apache.coyote.http11;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.apache.coyote.CloseNowException;
import org.apache.coyote.InputBuffer;
import org.apache.coyote.Request;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.http.HeaderUtil;
import org.apache.tomcat.util.http.MimeHeaders;
import org.apache.tomcat.util.http.parser.HttpParser;
import org.apache.tomcat.util.net.ApplicationBufferHandler;
import org.apache.tomcat.util.net.SocketWrapperBase;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http11/Http11InputBuffer.class */
public class Http11InputBuffer implements InputBuffer, ApplicationBufferHandler {
    private static final Log log = LogFactory.getLog((Class<?>) Http11InputBuffer.class);
    private static final StringManager sm = StringManager.getManager((Class<?>) Http11InputBuffer.class);
    private static final byte[] CLIENT_PREFACE_START = "PRI * HTTP/2.0\r\n\r\nSM\r\n\r\n".getBytes(StandardCharsets.ISO_8859_1);
    private final Request request;
    private final MimeHeaders headers;
    private final boolean rejectIllegalHeader;
    private ByteBuffer byteBuffer;
    private int end;
    private SocketWrapperBase<?> wrapper;
    private int parsingRequestLinePhase;
    private boolean parsingRequestLineEol;
    private int parsingRequestLineStart;
    private int parsingRequestLineQPos;
    private final HttpParser httpParser;
    private final int headerBufferSize;
    private int socketReadBufferSize;
    private byte prevChr = 0;
    private byte chr = 0;
    private final HeaderParseData headerData = new HeaderParseData();
    private InputFilter[] filterLibrary = new InputFilter[0];
    private InputFilter[] activeFilters = new InputFilter[0];
    private int lastActiveFilter = -1;
    private volatile boolean parsingHeader = true;
    private volatile boolean parsingRequestLine = true;
    private HeaderParsePosition headerParsePos = HeaderParsePosition.HEADER_START;
    private boolean swallowInput = true;
    private InputBuffer inputStreamInputBuffer = new SocketInputBuffer();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http11/Http11InputBuffer$HeaderParsePosition.class */
    public enum HeaderParsePosition {
        HEADER_START,
        HEADER_NAME,
        HEADER_VALUE_START,
        HEADER_VALUE,
        HEADER_MULTI_LINE,
        HEADER_SKIPLINE
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http11/Http11InputBuffer$HeaderParseStatus.class */
    public enum HeaderParseStatus {
        DONE,
        HAVE_MORE_HEADERS,
        NEED_MORE_DATA
    }

    public Http11InputBuffer(Request request, int headerBufferSize, boolean rejectIllegalHeader, HttpParser httpParser) {
        this.parsingRequestLinePhase = 0;
        this.parsingRequestLineEol = false;
        this.parsingRequestLineStart = 0;
        this.parsingRequestLineQPos = -1;
        this.request = request;
        this.headers = request.getMimeHeaders();
        this.headerBufferSize = headerBufferSize;
        this.rejectIllegalHeader = rejectIllegalHeader;
        this.httpParser = httpParser;
        this.parsingRequestLinePhase = 0;
        this.parsingRequestLineEol = false;
        this.parsingRequestLineStart = 0;
        this.parsingRequestLineQPos = -1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addFilter(InputFilter filter) {
        if (filter == null) {
            throw new NullPointerException(sm.getString("iib.filter.npe"));
        }
        InputFilter[] newFilterLibrary = (InputFilter[]) Arrays.copyOf(this.filterLibrary, this.filterLibrary.length + 1);
        newFilterLibrary[this.filterLibrary.length] = filter;
        this.filterLibrary = newFilterLibrary;
        this.activeFilters = new InputFilter[this.filterLibrary.length];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public InputFilter[] getFilters() {
        return this.filterLibrary;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addActiveFilter(InputFilter filter) {
        if (this.lastActiveFilter == -1) {
            filter.setBuffer(this.inputStreamInputBuffer);
        } else {
            for (int i = 0; i <= this.lastActiveFilter; i++) {
                if (this.activeFilters[i] == filter) {
                    return;
                }
            }
            filter.setBuffer(this.activeFilters[this.lastActiveFilter]);
        }
        InputFilter[] inputFilterArr = this.activeFilters;
        int i2 = this.lastActiveFilter + 1;
        this.lastActiveFilter = i2;
        inputFilterArr[i2] = filter;
        filter.setRequest(this.request);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setSwallowInput(boolean swallowInput) {
        this.swallowInput = swallowInput;
    }

    @Override // org.apache.coyote.InputBuffer
    public int doRead(ApplicationBufferHandler handler) throws IOException {
        if (this.lastActiveFilter == -1) {
            return this.inputStreamInputBuffer.doRead(handler);
        }
        return this.activeFilters[this.lastActiveFilter].doRead(handler);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void recycle() {
        this.wrapper = null;
        this.request.recycle();
        for (int i = 0; i <= this.lastActiveFilter; i++) {
            this.activeFilters[i].recycle();
        }
        this.byteBuffer.limit(0).position(0);
        this.lastActiveFilter = -1;
        this.swallowInput = true;
        this.chr = (byte) 0;
        this.prevChr = (byte) 0;
        this.headerParsePos = HeaderParsePosition.HEADER_START;
        this.parsingRequestLinePhase = 0;
        this.parsingRequestLineEol = false;
        this.parsingRequestLineStart = 0;
        this.parsingRequestLineQPos = -1;
        this.headerData.recycle();
        this.parsingRequestLine = true;
        this.parsingHeader = true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void nextRequest() {
        this.request.recycle();
        if (this.byteBuffer.position() > 0) {
            if (this.byteBuffer.remaining() > 0) {
                this.byteBuffer.compact();
                this.byteBuffer.flip();
            } else {
                this.byteBuffer.position(0).limit(0);
            }
        }
        for (int i = 0; i <= this.lastActiveFilter; i++) {
            this.activeFilters[i].recycle();
        }
        this.lastActiveFilter = -1;
        this.parsingHeader = true;
        this.swallowInput = true;
        this.headerParsePos = HeaderParsePosition.HEADER_START;
        this.parsingRequestLine = true;
        this.parsingRequestLinePhase = 0;
        this.parsingRequestLineEol = false;
        this.parsingRequestLineStart = 0;
        this.parsingRequestLineQPos = -1;
        this.headerData.recycle();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean parseRequestLine(boolean keptAlive, int connectionTimeout, int keepAliveTimeout) throws IOException {
        if (!this.parsingRequestLine) {
            return true;
        }
        if (this.parsingRequestLinePhase < 2) {
            while (true) {
                if (this.byteBuffer.position() >= this.byteBuffer.limit()) {
                    if (keptAlive) {
                        this.wrapper.setReadTimeout(keepAliveTimeout);
                    }
                    if (!fill(false)) {
                        this.parsingRequestLinePhase = 1;
                        return false;
                    }
                    this.wrapper.setReadTimeout(connectionTimeout);
                }
                if (!keptAlive && this.byteBuffer.position() == 0 && this.byteBuffer.limit() >= CLIENT_PREFACE_START.length) {
                    boolean prefaceMatch = true;
                    for (int i = 0; i < CLIENT_PREFACE_START.length && prefaceMatch; i++) {
                        if (CLIENT_PREFACE_START[i] != this.byteBuffer.get(i)) {
                            prefaceMatch = false;
                        }
                    }
                    if (prefaceMatch) {
                        this.parsingRequestLinePhase = -1;
                        return false;
                    }
                }
                if (this.request.getStartTimeNanos() < 0) {
                    this.request.setStartTimeNanos(System.nanoTime());
                }
                this.chr = this.byteBuffer.get();
                if (this.chr != 13 && this.chr != 10) {
                    this.byteBuffer.position(this.byteBuffer.position() - 1);
                    this.parsingRequestLineStart = this.byteBuffer.position();
                    this.parsingRequestLinePhase = 2;
                    break;
                }
            }
        }
        if (this.parsingRequestLinePhase == 2) {
            boolean space = false;
            while (!space) {
                if (this.byteBuffer.position() >= this.byteBuffer.limit() && !fill(false)) {
                    return false;
                }
                int pos = this.byteBuffer.position();
                this.chr = this.byteBuffer.get();
                if (this.chr == 32 || this.chr == 9) {
                    space = true;
                    this.request.method().setBytes(this.byteBuffer.array(), this.parsingRequestLineStart, pos - this.parsingRequestLineStart);
                } else if (!HttpParser.isToken(this.chr)) {
                    this.request.protocol().setString("HTTP/1.1");
                    String invalidMethodValue = parseInvalid(this.parsingRequestLineStart, this.byteBuffer);
                    throw new IllegalArgumentException(sm.getString("iib.invalidmethod", invalidMethodValue));
                }
            }
            this.parsingRequestLinePhase = 3;
        }
        if (this.parsingRequestLinePhase == 3) {
            boolean space2 = true;
            while (space2) {
                if (this.byteBuffer.position() >= this.byteBuffer.limit() && !fill(false)) {
                    return false;
                }
                this.chr = this.byteBuffer.get();
                if (this.chr != 32 && this.chr != 9) {
                    space2 = false;
                    this.byteBuffer.position(this.byteBuffer.position() - 1);
                }
            }
            this.parsingRequestLineStart = this.byteBuffer.position();
            this.parsingRequestLinePhase = 4;
        }
        if (this.parsingRequestLinePhase == 4) {
            int end = 0;
            boolean space3 = false;
            while (!space3) {
                if (this.byteBuffer.position() >= this.byteBuffer.limit() && !fill(false)) {
                    return false;
                }
                int pos2 = this.byteBuffer.position();
                this.prevChr = this.chr;
                this.chr = this.byteBuffer.get();
                if (this.prevChr == 13 && this.chr != 10) {
                    this.request.protocol().setString("HTTP/1.1");
                    String invalidRequestTarget = parseInvalid(this.parsingRequestLineStart, this.byteBuffer);
                    throw new IllegalArgumentException(sm.getString("iib.invalidRequestTarget", invalidRequestTarget));
                }
                if (this.chr == 32 || this.chr == 9) {
                    space3 = true;
                    end = pos2;
                } else if (this.chr == 13) {
                    continue;
                } else if (this.chr == 10) {
                    space3 = true;
                    this.request.protocol().setString("");
                    this.parsingRequestLinePhase = 7;
                    if (this.prevChr == 13) {
                        end = pos2 - 1;
                    } else {
                        end = pos2;
                    }
                } else if (this.chr == 63 && this.parsingRequestLineQPos == -1) {
                    this.parsingRequestLineQPos = pos2;
                } else {
                    if (this.parsingRequestLineQPos != -1 && !this.httpParser.isQueryRelaxed(this.chr)) {
                        this.request.protocol().setString("HTTP/1.1");
                        String invalidRequestTarget2 = parseInvalid(this.parsingRequestLineStart, this.byteBuffer);
                        throw new IllegalArgumentException(sm.getString("iib.invalidRequestTarget", invalidRequestTarget2));
                    }
                    if (this.httpParser.isNotRequestTargetRelaxed(this.chr)) {
                        this.request.protocol().setString("HTTP/1.1");
                        String invalidRequestTarget3 = parseInvalid(this.parsingRequestLineStart, this.byteBuffer);
                        throw new IllegalArgumentException(sm.getString("iib.invalidRequestTarget", invalidRequestTarget3));
                    }
                }
            }
            if (this.parsingRequestLineQPos >= 0) {
                this.request.queryString().setBytes(this.byteBuffer.array(), this.parsingRequestLineQPos + 1, (end - this.parsingRequestLineQPos) - 1);
                this.request.requestURI().setBytes(this.byteBuffer.array(), this.parsingRequestLineStart, this.parsingRequestLineQPos - this.parsingRequestLineStart);
            } else {
                this.request.requestURI().setBytes(this.byteBuffer.array(), this.parsingRequestLineStart, end - this.parsingRequestLineStart);
            }
            if (this.parsingRequestLinePhase == 4) {
                this.parsingRequestLinePhase = 5;
            }
        }
        if (this.parsingRequestLinePhase == 5) {
            boolean space4 = true;
            while (space4) {
                if (this.byteBuffer.position() >= this.byteBuffer.limit() && !fill(false)) {
                    return false;
                }
                byte chr = this.byteBuffer.get();
                if (chr != 32 && chr != 9) {
                    space4 = false;
                    this.byteBuffer.position(this.byteBuffer.position() - 1);
                }
            }
            this.parsingRequestLineStart = this.byteBuffer.position();
            this.parsingRequestLinePhase = 6;
            this.end = 0;
        }
        if (this.parsingRequestLinePhase == 6) {
            while (!this.parsingRequestLineEol) {
                if (this.byteBuffer.position() >= this.byteBuffer.limit() && !fill(false)) {
                    return false;
                }
                int pos3 = this.byteBuffer.position();
                this.prevChr = this.chr;
                this.chr = this.byteBuffer.get();
                if (this.chr != 13) {
                    if (this.prevChr == 13 && this.chr == 10) {
                        this.end = pos3 - 1;
                        this.parsingRequestLineEol = true;
                    } else if (this.chr == 10) {
                        this.end = pos3;
                        this.parsingRequestLineEol = true;
                    } else if (this.prevChr == 13 || !HttpParser.isHttpProtocol(this.chr)) {
                        String invalidProtocol = parseInvalid(this.parsingRequestLineStart, this.byteBuffer);
                        throw new IllegalArgumentException(sm.getString("iib.invalidHttpProtocol", invalidProtocol));
                    }
                }
            }
            if (this.end - this.parsingRequestLineStart > 0) {
                this.request.protocol().setBytes(this.byteBuffer.array(), this.parsingRequestLineStart, this.end - this.parsingRequestLineStart);
                this.parsingRequestLinePhase = 7;
            }
        }
        if (this.parsingRequestLinePhase == 7) {
            this.parsingRequestLine = false;
            this.parsingRequestLinePhase = 0;
            this.parsingRequestLineEol = false;
            this.parsingRequestLineStart = 0;
            return true;
        }
        throw new IllegalStateException(sm.getString("iib.invalidPhase", Integer.valueOf(this.parsingRequestLinePhase)));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean parseHeaders() throws IOException {
        HeaderParseStatus status;
        if (!this.parsingHeader) {
            throw new IllegalStateException(sm.getString("iib.parseheaders.ise.error"));
        }
        HeaderParseStatus headerParseStatus = HeaderParseStatus.HAVE_MORE_HEADERS;
        do {
            status = parseHeader();
            if (this.byteBuffer.position() > this.headerBufferSize || this.byteBuffer.capacity() - this.byteBuffer.position() < this.socketReadBufferSize) {
                throw new IllegalArgumentException(sm.getString("iib.requestheadertoolarge.error"));
            }
        } while (status == HeaderParseStatus.HAVE_MORE_HEADERS);
        if (status == HeaderParseStatus.DONE) {
            this.parsingHeader = false;
            this.end = this.byteBuffer.position();
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getParsingRequestLinePhase() {
        return this.parsingRequestLinePhase;
    }

    private String parseInvalid(int startPos, ByteBuffer buffer) {
        byte b;
        byte b2 = 0;
        while (true) {
            b = b2;
            if (!buffer.hasRemaining() || b == 32) {
                break;
            }
            b2 = buffer.get();
        }
        String result = HeaderUtil.toPrintableString(buffer.array(), buffer.arrayOffset() + startPos, buffer.position() - startPos);
        if (b != 32) {
            result = result + "...";
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void endRequest() throws IOException {
        if (this.swallowInput && this.lastActiveFilter != -1) {
            int extraBytes = (int) this.activeFilters[this.lastActiveFilter].end();
            this.byteBuffer.position(this.byteBuffer.position() - extraBytes);
        }
    }

    @Override // org.apache.coyote.InputBuffer
    public int available() {
        return available(false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int available(boolean read) {
        int available;
        if (this.lastActiveFilter == -1) {
            available = this.inputStreamInputBuffer.available();
        } else {
            available = this.activeFilters[this.lastActiveFilter].available();
        }
        if (available == 0 && read) {
            try {
                if (!this.byteBuffer.hasRemaining() && this.wrapper.hasDataToRead()) {
                    fill(false);
                    available = this.byteBuffer.remaining();
                }
            } catch (IOException ioe) {
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("iib.available.readFail"), ioe);
                }
                available = 1;
            }
        }
        return available;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isFinished() {
        if (this.lastActiveFilter >= 0) {
            return this.activeFilters[this.lastActiveFilter].isFinished();
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ByteBuffer getLeftover() {
        int available = this.byteBuffer.remaining();
        if (available > 0) {
            return ByteBuffer.wrap(this.byteBuffer.array(), this.byteBuffer.position(), available);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isChunking() {
        for (int i = 0; i < this.lastActiveFilter; i++) {
            if (this.activeFilters[i] == this.filterLibrary[1]) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void init(SocketWrapperBase<?> socketWrapper) {
        this.wrapper = socketWrapper;
        this.wrapper.setAppReadBufHandler(this);
        int bufLength = this.headerBufferSize + this.wrapper.getSocketBufferHandler().getReadBuffer().capacity();
        if (this.byteBuffer == null || this.byteBuffer.capacity() < bufLength) {
            this.byteBuffer = ByteBuffer.allocate(bufLength);
            this.byteBuffer.position(0).limit(0);
        }
    }

    private boolean fill(boolean block) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug("Before fill(): parsingHeader: [" + this.parsingHeader + "], parsingRequestLine: [" + this.parsingRequestLine + "], parsingRequestLinePhase: [" + this.parsingRequestLinePhase + "], parsingRequestLineStart: [" + this.parsingRequestLineStart + "], byteBuffer.position(): [" + this.byteBuffer.position() + "], byteBuffer.limit(): [" + this.byteBuffer.limit() + "], end: [" + this.end + "]");
        }
        if (this.parsingHeader) {
            if (this.byteBuffer.limit() >= this.headerBufferSize) {
                if (this.parsingRequestLine) {
                    this.request.protocol().setString("HTTP/1.1");
                }
                throw new IllegalArgumentException(sm.getString("iib.requestheadertoolarge.error"));
            }
        } else {
            this.byteBuffer.limit(this.end).position(this.end);
        }
        int mark = this.byteBuffer.position();
        try {
            if (this.byteBuffer.position() < this.byteBuffer.limit()) {
                this.byteBuffer.position(this.byteBuffer.limit());
            }
            this.byteBuffer.limit(this.byteBuffer.capacity());
            SocketWrapperBase<?> socketWrapper = this.wrapper;
            if (socketWrapper != null) {
                int nRead = socketWrapper.read(block, this.byteBuffer);
                if (log.isDebugEnabled()) {
                    log.debug("Received [" + new String(this.byteBuffer.array(), this.byteBuffer.position(), this.byteBuffer.remaining(), StandardCharsets.ISO_8859_1) + "]");
                }
                if (nRead > 0) {
                    return true;
                }
                if (nRead == -1) {
                    throw new EOFException(sm.getString("iib.eof.error"));
                }
                return false;
            }
            throw new CloseNowException(sm.getString("iib.eof.error"));
        } finally {
            if (this.byteBuffer.position() >= mark) {
                this.byteBuffer.limit(this.byteBuffer.position());
                this.byteBuffer.position(mark);
            } else {
                this.byteBuffer.position(0);
                this.byteBuffer.limit(0);
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:100:0x0264, code lost:            if (fill(false) != false) goto L85;     */
    /* JADX WARN: Code restructure failed: missing block: B:103:0x026a, code lost:            return org.apache.coyote.http11.Http11InputBuffer.HeaderParseStatus.NEED_MORE_DATA;     */
    /* JADX WARN: Code restructure failed: missing block: B:104:0x026b, code lost:            r7.prevChr = r7.chr;        r7.chr = r7.byteBuffer.get();     */
    /* JADX WARN: Code restructure failed: missing block: B:105:0x0284, code lost:            if (r7.chr != 13) goto L159;     */
    /* JADX WARN: Code restructure failed: missing block: B:107:0x028d, code lost:            if (r7.prevChr == 13) goto L161;     */
    /* JADX WARN: Code restructure failed: missing block: B:110:0x0299, code lost:            if (r7.chr != 10) goto L162;     */
    /* JADX WARN: Code restructure failed: missing block: B:113:0x02a7, code lost:            if (r7.prevChr != 13) goto L97;     */
    /* JADX WARN: Code restructure failed: missing block: B:115:0x02b7, code lost:            if (org.apache.tomcat.util.http.parser.HttpParser.isControl(r7.chr) == false) goto L103;     */
    /* JADX WARN: Code restructure failed: missing block: B:117:0x02c0, code lost:            if (r7.chr == 9) goto L103;     */
    /* JADX WARN: Code restructure failed: missing block: B:120:0x02c8, code lost:            return skipLine(true);     */
    /* JADX WARN: Code restructure failed: missing block: B:122:0x02cf, code lost:            if (r7.chr == 32) goto L163;     */
    /* JADX WARN: Code restructure failed: missing block: B:124:0x02d8, code lost:            if (r7.chr != 9) goto L166;     */
    /* JADX WARN: Code restructure failed: missing block: B:126:0x02db, code lost:            r7.byteBuffer.put(r7.headerData.realPos, r7.chr);        r7.headerData.realPos++;     */
    /* JADX WARN: Code restructure failed: missing block: B:130:0x02fe, code lost:            r7.byteBuffer.put(r7.headerData.realPos, r7.chr);        r7.headerData.realPos++;        r7.headerData.lastSignificantChar = r7.headerData.realPos;     */
    /* JADX WARN: Code restructure failed: missing block: B:135:0x02af, code lost:            return skipLine(true);     */
    /* JADX WARN: Code restructure failed: missing block: B:137:0x029c, code lost:            r8 = true;     */
    /* JADX WARN: Code restructure failed: missing block: B:142:0x032f, code lost:            r7.headerData.realPos = r7.headerData.lastSignificantChar;        r7.headerParsePos = org.apache.coyote.http11.Http11InputBuffer.HeaderParsePosition.HEADER_MULTI_LINE;     */
    /* JADX WARN: Code restructure failed: missing block: B:144:0x0352, code lost:            if (r7.byteBuffer.position() < r7.byteBuffer.limit()) goto L116;     */
    /* JADX WARN: Code restructure failed: missing block: B:146:0x035a, code lost:            if (fill(false) != false) goto L116;     */
    /* JADX WARN: Code restructure failed: missing block: B:149:0x0360, code lost:            return org.apache.coyote.http11.Http11InputBuffer.HeaderParseStatus.NEED_MORE_DATA;     */
    /* JADX WARN: Code restructure failed: missing block: B:150:0x0361, code lost:            r0 = r7.byteBuffer.get(r7.byteBuffer.position());     */
    /* JADX WARN: Code restructure failed: missing block: B:151:0x0377, code lost:            if (r7.headerParsePos != org.apache.coyote.http11.Http11InputBuffer.HeaderParsePosition.HEADER_MULTI_LINE) goto L152;     */
    /* JADX WARN: Code restructure failed: missing block: B:154:0x037d, code lost:            if (r0 == 32) goto L123;     */
    /* JADX WARN: Code restructure failed: missing block: B:156:0x0383, code lost:            if (r0 == 9) goto L123;     */
    /* JADX WARN: Code restructure failed: missing block: B:158:0x0386, code lost:            r7.headerParsePos = org.apache.coyote.http11.Http11InputBuffer.HeaderParsePosition.HEADER_START;     */
    /* JADX WARN: Code restructure failed: missing block: B:159:0x0390, code lost:            r7.byteBuffer.put(r7.headerData.realPos, r0);        r7.headerData.realPos++;        r7.headerParsePos = org.apache.coyote.http11.Http11InputBuffer.HeaderParsePosition.HEADER_VALUE_START;     */
    /* JADX WARN: Code restructure failed: missing block: B:61:0x01b3, code lost:            if (r7.headerParsePos != org.apache.coyote.http11.Http11InputBuffer.HeaderParsePosition.HEADER_SKIPLINE) goto L55;     */
    /* JADX WARN: Code restructure failed: missing block: B:63:0x01bb, code lost:            return skipLine(false);     */
    /* JADX WARN: Code restructure failed: missing block: B:65:0x01c3, code lost:            if (r7.headerParsePos == org.apache.coyote.http11.Http11InputBuffer.HeaderParsePosition.HEADER_VALUE_START) goto L61;     */
    /* JADX WARN: Code restructure failed: missing block: B:67:0x01cd, code lost:            if (r7.headerParsePos == org.apache.coyote.http11.Http11InputBuffer.HeaderParsePosition.HEADER_VALUE) goto L61;     */
    /* JADX WARN: Code restructure failed: missing block: B:69:0x01d7, code lost:            if (r7.headerParsePos != org.apache.coyote.http11.Http11InputBuffer.HeaderParsePosition.HEADER_MULTI_LINE) goto L145;     */
    /* JADX WARN: Code restructure failed: missing block: B:71:0x03b7, code lost:            r7.headerData.headerValue.setBytes(r7.byteBuffer.array(), r7.headerData.start, r7.headerData.lastSignificantChar - r7.headerData.start);        r7.headerData.recycle();     */
    /* JADX WARN: Code restructure failed: missing block: B:72:0x03e8, code lost:            return org.apache.coyote.http11.Http11InputBuffer.HeaderParseStatus.HAVE_MORE_HEADERS;     */
    /* JADX WARN: Code restructure failed: missing block: B:74:0x01e1, code lost:            if (r7.headerParsePos != org.apache.coyote.http11.Http11InputBuffer.HeaderParsePosition.HEADER_VALUE_START) goto L74;     */
    /* JADX WARN: Code restructure failed: missing block: B:76:0x01f2, code lost:            if (r7.byteBuffer.position() < r7.byteBuffer.limit()) goto L69;     */
    /* JADX WARN: Code restructure failed: missing block: B:78:0x01fa, code lost:            if (fill(false) != false) goto L69;     */
    /* JADX WARN: Code restructure failed: missing block: B:81:0x0200, code lost:            return org.apache.coyote.http11.Http11InputBuffer.HeaderParseStatus.NEED_MORE_DATA;     */
    /* JADX WARN: Code restructure failed: missing block: B:82:0x0201, code lost:            r7.chr = r7.byteBuffer.get();     */
    /* JADX WARN: Code restructure failed: missing block: B:83:0x0212, code lost:            if (r7.chr == 32) goto L157;     */
    /* JADX WARN: Code restructure failed: missing block: B:86:0x021b, code lost:            if (r7.chr == 9) goto L158;     */
    /* JADX WARN: Code restructure failed: missing block: B:88:0x021e, code lost:            r7.headerParsePos = org.apache.coyote.http11.Http11InputBuffer.HeaderParsePosition.HEADER_VALUE;        r7.byteBuffer.position(r7.byteBuffer.position() - 1);        r7.chr = 0;     */
    /* JADX WARN: Code restructure failed: missing block: B:93:0x0245, code lost:            if (r7.headerParsePos != org.apache.coyote.http11.Http11InputBuffer.HeaderParsePosition.HEADER_VALUE) goto L110;     */
    /* JADX WARN: Code restructure failed: missing block: B:94:0x0248, code lost:            r8 = false;     */
    /* JADX WARN: Code restructure failed: missing block: B:96:0x024b, code lost:            if (r8 != false) goto L160;     */
    /* JADX WARN: Code restructure failed: missing block: B:98:0x025c, code lost:            if (r7.byteBuffer.position() < r7.byteBuffer.limit()) goto L85;     */
    /* JADX WARN: Removed duplicated region for block: B:163:0x01ac A[EDGE_INSN: B:163:0x01ac->B:60:0x01ac BREAK  A[LOOP:1: B:27:0x00ba->B:48:0x00ba], SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:25:0x0097  */
    /* JADX WARN: Removed duplicated region for block: B:29:0x00c4  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private org.apache.coyote.http11.Http11InputBuffer.HeaderParseStatus parseHeader() throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 1001
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.coyote.http11.Http11InputBuffer.parseHeader():org.apache.coyote.http11.Http11InputBuffer$HeaderParseStatus");
    }

    private HeaderParseStatus skipLine(boolean deleteHeader) throws IOException {
        boolean rejectThisHeader = this.rejectIllegalHeader;
        if (!rejectThisHeader && deleteHeader) {
            if (this.headers.getName(this.headers.size() - 1).equalsIgnoreCase("content-length")) {
                rejectThisHeader = true;
            } else {
                this.headers.removeHeader(this.headers.size() - 1);
            }
        }
        this.headerParsePos = HeaderParsePosition.HEADER_SKIPLINE;
        boolean eol = false;
        while (!eol) {
            if (this.byteBuffer.position() >= this.byteBuffer.limit() && !fill(false)) {
                return HeaderParseStatus.NEED_MORE_DATA;
            }
            int pos = this.byteBuffer.position();
            this.prevChr = this.chr;
            this.chr = this.byteBuffer.get();
            if (this.chr != 13) {
                if (this.chr == 10) {
                    eol = true;
                } else {
                    this.headerData.lastSignificantChar = pos;
                }
            }
        }
        if (rejectThisHeader || log.isDebugEnabled()) {
            if (rejectThisHeader) {
                throw new IllegalArgumentException(sm.getString("iib.invalidheader.reject", HeaderUtil.toPrintableString(this.byteBuffer.array(), this.headerData.lineStart, (this.headerData.lastSignificantChar - this.headerData.lineStart) + 1)));
            }
            log.debug(sm.getString("iib.invalidheader", HeaderUtil.toPrintableString(this.byteBuffer.array(), this.headerData.lineStart, (this.headerData.lastSignificantChar - this.headerData.lineStart) + 1)));
        }
        this.headerParsePos = HeaderParsePosition.HEADER_START;
        return HeaderParseStatus.HAVE_MORE_HEADERS;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http11/Http11InputBuffer$HeaderParseData.class */
    public static class HeaderParseData {
        int lineStart = 0;
        int start = 0;
        int realPos = 0;
        int lastSignificantChar = 0;
        MessageBytes headerValue = null;

        private HeaderParseData() {
        }

        public void recycle() {
            this.lineStart = 0;
            this.start = 0;
            this.realPos = 0;
            this.lastSignificantChar = 0;
            this.headerValue = null;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/http11/Http11InputBuffer$SocketInputBuffer.class */
    private class SocketInputBuffer implements InputBuffer {
        private SocketInputBuffer() {
        }

        @Override // org.apache.coyote.InputBuffer
        public int doRead(ApplicationBufferHandler handler) throws IOException {
            if (Http11InputBuffer.this.byteBuffer.position() >= Http11InputBuffer.this.byteBuffer.limit()) {
                boolean block = Http11InputBuffer.this.request.getReadListener() == null;
                if (!Http11InputBuffer.this.fill(block)) {
                    if (block) {
                        return -1;
                    }
                    return 0;
                }
            }
            int length = Http11InputBuffer.this.byteBuffer.remaining();
            handler.setByteBuffer(Http11InputBuffer.this.byteBuffer.duplicate());
            Http11InputBuffer.this.byteBuffer.position(Http11InputBuffer.this.byteBuffer.limit());
            return length;
        }

        @Override // org.apache.coyote.InputBuffer
        public int available() {
            return Http11InputBuffer.this.byteBuffer.remaining();
        }
    }

    @Override // org.apache.tomcat.util.net.ApplicationBufferHandler
    public void setByteBuffer(ByteBuffer buffer) {
        this.byteBuffer = buffer;
    }

    @Override // org.apache.tomcat.util.net.ApplicationBufferHandler
    public ByteBuffer getByteBuffer() {
        return this.byteBuffer;
    }

    @Override // org.apache.tomcat.util.net.ApplicationBufferHandler
    public void expand(int size) {
        if (this.byteBuffer.capacity() >= size) {
            this.byteBuffer.limit(size);
        }
        ByteBuffer temp = ByteBuffer.allocate(size);
        temp.put(this.byteBuffer);
        this.byteBuffer = temp;
        this.byteBuffer.mark();
    }
}
