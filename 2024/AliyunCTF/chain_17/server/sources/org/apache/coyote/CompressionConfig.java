package org.apache.coyote;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.http.MimeHeaders;
import org.apache.tomcat.util.http.ResponseUtil;
import org.apache.tomcat.util.http.parser.AcceptEncoding;
import org.apache.tomcat.util.http.parser.TokenList;
import org.apache.tomcat.util.res.StringManager;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.http.HttpHeaders;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/coyote/CompressionConfig.class */
public class CompressionConfig {
    private static final Log log = LogFactory.getLog((Class<?>) CompressionConfig.class);
    private static final StringManager sm = StringManager.getManager((Class<?>) CompressionConfig.class);
    private int compressionLevel = 0;
    private Pattern noCompressionUserAgents = null;
    private String compressibleMimeType = "text/html,text/xml,text/plain,text/css,text/javascript,application/javascript,application/json,application/xml";
    private String[] compressibleMimeTypes = null;
    private int compressionMinSize = 2048;

    public void setCompression(String compression) {
        if (compression.equals(CustomBooleanEditor.VALUE_ON)) {
            this.compressionLevel = 1;
            return;
        }
        if (compression.equals("force")) {
            this.compressionLevel = 2;
            return;
        }
        if (compression.equals(CustomBooleanEditor.VALUE_OFF)) {
            this.compressionLevel = 0;
            return;
        }
        try {
            setCompressionMinSize(Integer.parseInt(compression));
            this.compressionLevel = 1;
        } catch (Exception e) {
            this.compressionLevel = 0;
        }
    }

    public String getCompression() {
        switch (this.compressionLevel) {
            case 0:
                return CustomBooleanEditor.VALUE_OFF;
            case 1:
                return CustomBooleanEditor.VALUE_ON;
            case 2:
                return "force";
            default:
                return CustomBooleanEditor.VALUE_OFF;
        }
    }

    public int getCompressionLevel() {
        return this.compressionLevel;
    }

    public String getNoCompressionUserAgents() {
        if (this.noCompressionUserAgents == null) {
            return null;
        }
        return this.noCompressionUserAgents.toString();
    }

    public Pattern getNoCompressionUserAgentsPattern() {
        return this.noCompressionUserAgents;
    }

    public void setNoCompressionUserAgents(String noCompressionUserAgents) {
        if (noCompressionUserAgents == null || noCompressionUserAgents.length() == 0) {
            this.noCompressionUserAgents = null;
        } else {
            this.noCompressionUserAgents = Pattern.compile(noCompressionUserAgents);
        }
    }

    public String getCompressibleMimeType() {
        return this.compressibleMimeType;
    }

    public void setCompressibleMimeType(String valueS) {
        this.compressibleMimeType = valueS;
        this.compressibleMimeTypes = null;
    }

    public String[] getCompressibleMimeTypes() {
        String[] result = this.compressibleMimeTypes;
        if (result != null) {
            return result;
        }
        List<String> values = new ArrayList<>();
        StringTokenizer tokens = new StringTokenizer(this.compressibleMimeType, ",");
        while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken().trim();
            if (token.length() > 0) {
                values.add(token);
            }
        }
        String[] result2 = (String[]) values.toArray(new String[0]);
        this.compressibleMimeTypes = result2;
        return result2;
    }

    public int getCompressionMinSize() {
        return this.compressionMinSize;
    }

    public void setCompressionMinSize(int compressionMinSize) {
        this.compressionMinSize = compressionMinSize;
    }

    public boolean useCompression(Request request, Response response) {
        Pattern noCompressionUserAgents;
        MessageBytes userAgentValueMB;
        if (this.compressionLevel == 0) {
            return false;
        }
        MimeHeaders responseHeaders = response.getMimeHeaders();
        MessageBytes contentEncodingMB = responseHeaders.getValue(HttpHeaders.CONTENT_ENCODING);
        if (contentEncodingMB != null) {
            Set<String> tokens = new HashSet<>();
            try {
                TokenList.parseTokenList(responseHeaders.values(HttpHeaders.CONTENT_ENCODING), tokens);
                if (tokens.contains("gzip") || tokens.contains("br")) {
                    return false;
                }
            } catch (IOException e) {
                log.warn(sm.getString("compressionConfig.ContentEncodingParseFail"), e);
                return false;
            }
        }
        if (this.compressionLevel != 2) {
            long contentLength = response.getContentLengthLong();
            if (contentLength != -1 && contentLength < this.compressionMinSize) {
                return false;
            }
            String[] compressibleMimeTypes = getCompressibleMimeTypes();
            if (compressibleMimeTypes != null && !startsWithStringArray(compressibleMimeTypes, response.getContentType())) {
                return false;
            }
        }
        String eTag = responseHeaders.getHeader(HttpHeaders.ETAG);
        if (eTag != null && !eTag.trim().startsWith("W/")) {
            return false;
        }
        ResponseUtil.addVaryFieldName(responseHeaders, "accept-encoding");
        Enumeration<String> headerValues = request.getMimeHeaders().values("accept-encoding");
        boolean foundGzip = false;
        while (!foundGzip && headerValues.hasMoreElements()) {
            try {
                List<AcceptEncoding> acceptEncodings = AcceptEncoding.parse(new StringReader(headerValues.nextElement()));
                Iterator<AcceptEncoding> it = acceptEncodings.iterator();
                while (true) {
                    if (it.hasNext()) {
                        AcceptEncoding acceptEncoding = it.next();
                        if ("gzip".equalsIgnoreCase(acceptEncoding.getEncoding())) {
                            foundGzip = true;
                            break;
                        }
                    }
                }
            } catch (IOException e2) {
                return false;
            }
        }
        if (!foundGzip) {
            return false;
        }
        if (this.compressionLevel != 2 && (noCompressionUserAgents = this.noCompressionUserAgents) != null && (userAgentValueMB = request.getMimeHeaders().getValue("user-agent")) != null) {
            String userAgentValue = userAgentValueMB.toString();
            if (noCompressionUserAgents.matcher(userAgentValue).matches()) {
                return false;
            }
        }
        response.setContentLength(-1L);
        responseHeaders.setValue(HttpHeaders.CONTENT_ENCODING).setString("gzip");
        return true;
    }

    private static boolean startsWithStringArray(String[] sArray, String value) {
        if (value == null) {
            return false;
        }
        for (String s : sArray) {
            if (value.startsWith(s)) {
                return true;
            }
        }
        return false;
    }
}
