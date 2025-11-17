package org.apache.tomcat.util.http.parser;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/parser/AcceptEncoding.class */
public class AcceptEncoding {
    private final String encoding;
    private final double quality;

    protected AcceptEncoding(String encoding, double quality) {
        this.encoding = encoding;
        this.quality = quality;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public double getQuality() {
        return this.quality;
    }

    public static List<AcceptEncoding> parse(StringReader input) throws IOException {
        List<AcceptEncoding> result = new ArrayList<>();
        while (true) {
            String encoding = HttpParser.readToken(input);
            if (encoding == null) {
                HttpParser.skipUntil(input, 0, ',');
            } else if (encoding.length() != 0) {
                double quality = 1.0d;
                SkipResult lookForSemiColon = HttpParser.skipConstant(input, ";");
                if (lookForSemiColon == SkipResult.FOUND) {
                    quality = HttpParser.readWeight(input, ',');
                }
                if (quality > 0.0d) {
                    result.add(new AcceptEncoding(encoding, quality));
                }
            } else {
                return result;
            }
        }
    }
}
