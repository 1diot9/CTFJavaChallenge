package org.apache.tomcat.util.http.parser;

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import org.apache.tomcat.websocket.BasicAuthenticator;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/parser/MediaType.class */
public class MediaType {
    private final String type;
    private final String subtype;
    private final LinkedHashMap<String, String> parameters;
    private final String charset;
    private volatile String noCharset;
    private volatile String withCharset;

    protected MediaType(String type, String subtype, LinkedHashMap<String, String> parameters) {
        this.type = type;
        this.subtype = subtype;
        this.parameters = parameters;
        String cs = parameters.get(BasicAuthenticator.charsetparam);
        if (cs != null && cs.length() > 0 && cs.charAt(0) == '\"') {
            cs = HttpParser.unquote(cs);
        }
        this.charset = cs;
    }

    public String getType() {
        return this.type;
    }

    public String getSubtype() {
        return this.subtype;
    }

    public String getCharset() {
        return this.charset;
    }

    public int getParameterCount() {
        return this.parameters.size();
    }

    public String getParameterValue(String parameter) {
        return this.parameters.get(parameter.toLowerCase(Locale.ENGLISH));
    }

    public String toString() {
        if (this.withCharset == null) {
            synchronized (this) {
                if (this.withCharset == null) {
                    StringBuilder result = new StringBuilder();
                    result.append(this.type);
                    result.append('/');
                    result.append(this.subtype);
                    for (Map.Entry<String, String> entry : this.parameters.entrySet()) {
                        String value = entry.getValue();
                        if (value != null && value.length() != 0) {
                            result.append(';');
                            result.append(entry.getKey());
                            result.append('=');
                            result.append(value);
                        }
                    }
                    this.withCharset = result.toString();
                }
            }
        }
        return this.withCharset;
    }

    public String toStringNoCharset() {
        if (this.noCharset == null) {
            synchronized (this) {
                if (this.noCharset == null) {
                    StringBuilder result = new StringBuilder();
                    result.append(this.type);
                    result.append('/');
                    result.append(this.subtype);
                    for (Map.Entry<String, String> entry : this.parameters.entrySet()) {
                        if (!entry.getKey().equalsIgnoreCase(BasicAuthenticator.charsetparam)) {
                            result.append(';');
                            result.append(entry.getKey());
                            result.append('=');
                            result.append(entry.getValue());
                        }
                    }
                    this.noCharset = result.toString();
                }
            }
        }
        return this.noCharset;
    }

    public static MediaType parseMediaType(StringReader input) throws IOException {
        String subtype;
        String type = HttpParser.readToken(input);
        if (type == null || type.length() == 0 || HttpParser.skipConstant(input, "/") == SkipResult.NOT_FOUND || (subtype = HttpParser.readToken(input)) == null || subtype.length() == 0) {
            return null;
        }
        LinkedHashMap<String, String> parameters = new LinkedHashMap<>();
        SkipResult lookForSemiColon = HttpParser.skipConstant(input, ";");
        if (lookForSemiColon == SkipResult.NOT_FOUND) {
            return null;
        }
        while (lookForSemiColon == SkipResult.FOUND) {
            String attribute = HttpParser.readToken(input);
            String value = "";
            if (HttpParser.skipConstant(input, "=") == SkipResult.FOUND) {
                value = HttpParser.readTokenOrQuotedString(input, true);
            }
            if (attribute != null) {
                parameters.put(attribute.toLowerCase(Locale.ENGLISH), value);
            }
            lookForSemiColon = HttpParser.skipConstant(input, ";");
            if (lookForSemiColon == SkipResult.NOT_FOUND) {
                return null;
            }
        }
        return new MediaType(type, subtype, parameters);
    }
}
