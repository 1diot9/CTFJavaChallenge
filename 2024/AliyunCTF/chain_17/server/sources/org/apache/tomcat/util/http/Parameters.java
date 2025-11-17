package org.apache.tomcat.util.http;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.buf.StringUtils;
import org.apache.tomcat.util.buf.UDecoder;
import org.apache.tomcat.util.log.UserDataHelper;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/Parameters.class */
public final class Parameters {
    private MessageBytes queryMB;
    private UDecoder urlDec;
    private static final Log log = LogFactory.getLog((Class<?>) Parameters.class);
    private static final UserDataHelper userDataLog = new UserDataHelper(log);
    private static final UserDataHelper maxParamCountLog = new UserDataHelper(log);
    private static final StringManager sm = StringManager.getManager("org.apache.tomcat.util.http");
    private static final Charset DEFAULT_BODY_CHARSET = StandardCharsets.ISO_8859_1;
    private static final Charset DEFAULT_URI_CHARSET = StandardCharsets.UTF_8;
    private final Map<String, ArrayList<String>> paramHashValues = new LinkedHashMap();
    private boolean didQueryParameters = false;
    private final MessageBytes decodedQuery = MessageBytes.newInstance();
    private Charset charset = StandardCharsets.ISO_8859_1;
    private Charset queryStringCharset = StandardCharsets.UTF_8;
    private int limit = -1;
    private int parameterCount = 0;
    private FailReason parseFailedReason = null;
    private final ByteChunk tmpName = new ByteChunk();
    private final ByteChunk tmpValue = new ByteChunk();
    private final ByteChunk origName = new ByteChunk();
    private final ByteChunk origValue = new ByteChunk();

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/Parameters$FailReason.class */
    public enum FailReason {
        CLIENT_DISCONNECT,
        MULTIPART_CONFIG_INVALID,
        INVALID_CONTENT_TYPE,
        IO_ERROR,
        NO_NAME,
        POST_TOO_LARGE,
        REQUEST_BODY_INCOMPLETE,
        TOO_MANY_PARAMETERS,
        UNKNOWN,
        URL_DECODING
    }

    public void setQuery(MessageBytes queryMB) {
        this.queryMB = queryMB;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public Charset getCharset() {
        return this.charset;
    }

    public void setCharset(Charset charset) {
        if (charset == null) {
            charset = DEFAULT_BODY_CHARSET;
        }
        this.charset = charset;
        if (log.isDebugEnabled()) {
            log.debug("Set encoding to " + charset.name());
        }
    }

    public void setQueryStringCharset(Charset queryStringCharset) {
        if (queryStringCharset == null) {
            queryStringCharset = DEFAULT_URI_CHARSET;
        }
        this.queryStringCharset = queryStringCharset;
        if (log.isDebugEnabled()) {
            log.debug("Set query string encoding to " + queryStringCharset.name());
        }
    }

    public boolean isParseFailed() {
        return this.parseFailedReason != null;
    }

    public FailReason getParseFailedReason() {
        return this.parseFailedReason;
    }

    public void setParseFailedReason(FailReason failReason) {
        if (this.parseFailedReason == null) {
            this.parseFailedReason = failReason;
        }
    }

    public int size() {
        return this.parameterCount;
    }

    public void recycle() {
        this.parameterCount = 0;
        this.paramHashValues.clear();
        this.didQueryParameters = false;
        this.charset = DEFAULT_BODY_CHARSET;
        this.decodedQuery.recycle();
        this.parseFailedReason = null;
    }

    public String[] getParameterValues(String name) {
        handleQueryParameters();
        ArrayList<String> values = this.paramHashValues.get(name);
        if (values == null) {
            return null;
        }
        return (String[]) values.toArray(new String[0]);
    }

    public Enumeration<String> getParameterNames() {
        handleQueryParameters();
        return Collections.enumeration(this.paramHashValues.keySet());
    }

    public String getParameter(String name) {
        handleQueryParameters();
        ArrayList<String> values = this.paramHashValues.get(name);
        if (values != null) {
            if (values.size() == 0) {
                return "";
            }
            return values.get(0);
        }
        return null;
    }

    public void handleQueryParameters() {
        if (this.didQueryParameters) {
            return;
        }
        this.didQueryParameters = true;
        if (this.queryMB == null || this.queryMB.isNull()) {
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug("Decoding query " + this.decodedQuery + " " + this.queryStringCharset.name());
        }
        try {
            this.decodedQuery.duplicate(this.queryMB);
        } catch (IOException e) {
            log.error(sm.getString("parameters.copyFail"), e);
        }
        processParameters(this.decodedQuery, this.queryStringCharset);
    }

    public void addParameter(String key, String value) throws IllegalStateException {
        if (key == null) {
            return;
        }
        if (this.limit > -1 && this.parameterCount >= this.limit) {
            setParseFailedReason(FailReason.TOO_MANY_PARAMETERS);
            throw new IllegalStateException(sm.getString("parameters.maxCountFail", Integer.valueOf(this.limit)));
        }
        this.parameterCount++;
        this.paramHashValues.computeIfAbsent(key, k -> {
            return new ArrayList(1);
        }).add(value);
    }

    public void setURLDecoder(UDecoder u) {
        this.urlDec = u;
    }

    public void processParameters(byte[] bytes, int start, int len) {
        processParameters(bytes, start, len, this.charset);
    }

    /* JADX WARN: Failed to find 'out' block for switch in B:126:0x01d4. Please report as an issue. */
    /* JADX WARN: Failed to find 'out' block for switch in B:99:0x03fd. Please report as an issue. */
    /* JADX WARN: Removed duplicated region for block: B:116:0x0155 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:20:0x00e7  */
    /* JADX WARN: Removed duplicated region for block: B:35:0x0223 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void processParameters(byte[] r14, int r15, int r16, java.nio.charset.Charset r17) {
        /*
            Method dump skipped, instructions count: 1262
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tomcat.util.http.Parameters.processParameters(byte[], int, int, java.nio.charset.Charset):void");
    }

    private void urlDecode(ByteChunk bc) throws IOException {
        if (this.urlDec == null) {
            this.urlDec = new UDecoder();
        }
        this.urlDec.convert(bc, true);
    }

    public void processParameters(MessageBytes data, Charset charset) {
        if (data == null || data.isNull() || data.getLength() <= 0) {
            return;
        }
        if (data.getType() != 2) {
            data.toBytes();
        }
        ByteChunk bc = data.getByteChunk();
        processParameters(bc.getBytes(), bc.getOffset(), bc.getLength(), charset);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, ArrayList<String>> e : this.paramHashValues.entrySet()) {
            sb.append(e.getKey()).append('=');
            StringUtils.join((Iterable<String>) e.getValue(), ',', sb);
            sb.append('\n');
        }
        return sb.toString();
    }
}
