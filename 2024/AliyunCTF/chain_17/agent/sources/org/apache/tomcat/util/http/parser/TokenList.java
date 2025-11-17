package org.apache.tomcat.util.http.parser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/parser/TokenList.class */
public class TokenList {
    private TokenList() {
    }

    public static boolean parseTokenList(Enumeration<String> inputs, Collection<String> collection) throws IOException {
        boolean result = true;
        while (inputs.hasMoreElements()) {
            String nextHeaderValue = inputs.nextElement();
            if (nextHeaderValue != null && !parseTokenList(new StringReader(nextHeaderValue), collection)) {
                result = false;
            }
        }
        return result;
    }

    public static boolean parseTokenList(Reader input, Collection<String> collection) throws IOException {
        boolean invalid = false;
        boolean valid = false;
        while (true) {
            String element = HttpParser.readToken(input);
            if (element == null) {
                if (HttpParser.skipConstant(input, ",") != SkipResult.FOUND) {
                    invalid = true;
                    HttpParser.skipUntil(input, 0, ',');
                }
            } else {
                if (element.length() == 0) {
                    break;
                }
                SkipResult skipResult = HttpParser.skipConstant(input, ",");
                if (skipResult == SkipResult.EOF) {
                    valid = true;
                    collection.add(element.toLowerCase(Locale.ENGLISH));
                    break;
                }
                if (skipResult == SkipResult.FOUND) {
                    valid = true;
                    collection.add(element.toLowerCase(Locale.ENGLISH));
                } else {
                    invalid = true;
                    HttpParser.skipUntil(input, 0, ',');
                }
            }
        }
        return valid && !invalid;
    }
}
