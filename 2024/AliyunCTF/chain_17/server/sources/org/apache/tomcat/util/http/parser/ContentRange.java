package org.apache.tomcat.util.http.parser;

import java.io.IOException;
import java.io.StringReader;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/parser/ContentRange.class */
public class ContentRange {
    private final String units;
    private final long start;
    private final long end;
    private final long length;

    public ContentRange(String units, long start, long end, long length) {
        this.units = units;
        this.start = start;
        this.end = end;
        this.length = length;
    }

    public String getUnits() {
        return this.units;
    }

    public long getStart() {
        return this.start;
    }

    public long getEnd() {
        return this.end;
    }

    public long getLength() {
        return this.length;
    }

    public static ContentRange parse(StringReader input) throws IOException {
        String units = HttpParser.readToken(input);
        if (units == null || units.length() == 0) {
            return null;
        }
        long start = HttpParser.readLong(input);
        if (HttpParser.skipConstant(input, "-") == SkipResult.NOT_FOUND) {
            return null;
        }
        long end = HttpParser.readLong(input);
        if (HttpParser.skipConstant(input, "/") == SkipResult.NOT_FOUND) {
            return null;
        }
        long length = HttpParser.readLong(input);
        SkipResult skipResult = HttpParser.skipConstant(input, "X");
        if (skipResult != SkipResult.EOF) {
            return null;
        }
        return new ContentRange(units, start, end, length);
    }
}
