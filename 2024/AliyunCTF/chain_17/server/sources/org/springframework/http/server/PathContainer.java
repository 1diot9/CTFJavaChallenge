package org.springframework.http.server;

import java.util.List;
import org.springframework.util.MultiValueMap;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/PathContainer.class */
public interface PathContainer {

    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/PathContainer$Element.class */
    public interface Element {
        String value();
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/PathContainer$PathSegment.class */
    public interface PathSegment extends Element {
        String valueToMatch();

        char[] valueToMatchAsChars();

        MultiValueMap<String, String> parameters();
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/PathContainer$Separator.class */
    public interface Separator extends Element {
    }

    String value();

    List<Element> elements();

    default PathContainer subPath(int index) {
        return subPath(index, elements().size());
    }

    default PathContainer subPath(int startIndex, int endIndex) {
        return DefaultPathContainer.subPath(this, startIndex, endIndex);
    }

    static PathContainer parsePath(String path) {
        return DefaultPathContainer.createFromUrlPath(path, Options.HTTP_PATH);
    }

    static PathContainer parsePath(String path, Options options) {
        return DefaultPathContainer.createFromUrlPath(path, options);
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/server/PathContainer$Options.class */
    public static class Options {
        public static final Options HTTP_PATH = create('/', true);
        public static final Options MESSAGE_ROUTE = create('.', false);
        private final char separator;
        private final boolean decodeAndParseSegments;

        private Options(char separator, boolean decodeAndParseSegments) {
            this.separator = separator;
            this.decodeAndParseSegments = decodeAndParseSegments;
        }

        public char separator() {
            return this.separator;
        }

        public boolean shouldDecodeAndParseSegments() {
            return this.decodeAndParseSegments;
        }

        public static Options create(char separator, boolean decodeAndParseSegments) {
            return new Options(separator, decodeAndParseSegments);
        }
    }
}
