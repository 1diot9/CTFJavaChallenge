package org.apache.tomcat.util.http.parser;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/parser/AcceptLanguage.class */
public class AcceptLanguage {
    private final Locale locale;
    private final double quality;

    protected AcceptLanguage(Locale locale, double quality) {
        this.locale = locale;
        this.quality = quality;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public double getQuality() {
        return this.quality;
    }

    public static List<AcceptLanguage> parse(StringReader input) throws IOException {
        List<AcceptLanguage> result = new ArrayList<>();
        while (true) {
            String languageTag = HttpParser.readToken(input);
            if (languageTag == null) {
                HttpParser.skipUntil(input, 0, ',');
            } else if (languageTag.length() != 0) {
                double quality = 1.0d;
                SkipResult lookForSemiColon = HttpParser.skipConstant(input, ";");
                if (lookForSemiColon == SkipResult.FOUND) {
                    quality = HttpParser.readWeight(input, ',');
                }
                if (quality > 0.0d) {
                    result.add(new AcceptLanguage(Locale.forLanguageTag(languageTag), quality));
                }
            } else {
                return result;
            }
        }
    }
}
