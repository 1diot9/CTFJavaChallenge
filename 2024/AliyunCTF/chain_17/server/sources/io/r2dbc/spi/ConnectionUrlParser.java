package io.r2dbc.spi;

import io.r2dbc.spi.ConnectionFactoryOptions;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.el.parser.ELParserConstants;

/* loaded from: server.jar:BOOT-INF/lib/r2dbc-spi-1.0.0.RELEASE.jar:io/r2dbc/spi/ConnectionUrlParser.class */
abstract class ConnectionUrlParser {
    private static final Set<String> PROHIBITED_QUERY_OPTIONS = (Set) Stream.of((Object[]) new Option[]{ConnectionFactoryOptions.DATABASE, ConnectionFactoryOptions.DRIVER, ConnectionFactoryOptions.HOST, ConnectionFactoryOptions.PASSWORD, ConnectionFactoryOptions.PORT, ConnectionFactoryOptions.PROTOCOL, ConnectionFactoryOptions.USER}).map((v0) -> {
        return v0.name();
    }).collect(Collectors.toSet());
    private static final String R2DBC_SCHEME = "r2dbc";
    private static final String R2DBC_SSL_SCHEME = "r2dbcs";

    static void validate(String url) {
        int driverPartIndex;
        Assert.requireNonNull(url, "URL must not be null");
        if (!url.startsWith("r2dbc:") && !url.startsWith("r2dbcs:")) {
            throw new IllegalArgumentException(String.format("URL %s does not start with the %s scheme", url, R2DBC_SCHEME));
        }
        int schemeSpecificPartIndex = url.indexOf("://");
        if (url.startsWith(R2DBC_SSL_SCHEME)) {
            driverPartIndex = R2DBC_SSL_SCHEME.length() + 1;
        } else {
            driverPartIndex = R2DBC_SCHEME.length() + 1;
        }
        if (schemeSpecificPartIndex == -1 || driverPartIndex >= schemeSpecificPartIndex) {
            throw new IllegalArgumentException(String.format("Invalid URL: %s", url));
        }
        String[] schemeParts = url.split(":", 3);
        String driver = schemeParts[1];
        if (driver.trim().isEmpty()) {
            throw new IllegalArgumentException(String.format("Empty driver in URL: %s", url));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ConnectionFactoryOptions parseQuery(CharSequence url) {
        String urlToUse = url.toString();
        validate(urlToUse);
        String[] schemeParts = urlToUse.split(":", 3);
        String scheme = schemeParts[0];
        String driver = schemeParts[1];
        String protocol = schemeParts[2];
        int schemeSpecificPartIndex = urlToUse.indexOf("://");
        String rewrittenUrl = scheme + urlToUse.substring(schemeSpecificPartIndex);
        URI uri = URI.create(rewrittenUrl);
        ConnectionFactoryOptions.Builder builder = ConnectionFactoryOptions.builder();
        if (scheme.equals(R2DBC_SSL_SCHEME)) {
            builder.option(ConnectionFactoryOptions.SSL, true);
        }
        builder.option(ConnectionFactoryOptions.DRIVER, driver);
        int protocolEnd = protocol.indexOf("://");
        if (protocolEnd != -1) {
            String protocol2 = protocol.substring(0, protocolEnd);
            if (!protocol2.trim().isEmpty()) {
                builder.option(ConnectionFactoryOptions.PROTOCOL, protocol2);
            }
        }
        if (hasText(uri.getHost())) {
            builder.option(ConnectionFactoryOptions.HOST, decode(uri.getHost().trim()).toString());
            if (hasText(uri.getRawUserInfo())) {
                parseUserinfo(uri.getRawUserInfo(), builder);
            }
        } else if (hasText(uri.getRawAuthority())) {
            String authorityToUse = uri.getRawAuthority();
            if (authorityToUse.contains("@")) {
                int atIndex = authorityToUse.lastIndexOf(64);
                String userinfo = authorityToUse.substring(0, atIndex);
                authorityToUse = authorityToUse.substring(atIndex + 1);
                if (!userinfo.isEmpty()) {
                    parseUserinfo(userinfo, builder);
                }
            }
            builder.option(ConnectionFactoryOptions.HOST, decode(authorityToUse.trim()).toString());
        }
        if (uri.getPort() != -1) {
            builder.option(ConnectionFactoryOptions.PORT, Integer.valueOf(uri.getPort()));
        }
        if (hasText(uri.getPath())) {
            String path = uri.getPath().substring(1).trim();
            if (hasText(path)) {
                builder.option(ConnectionFactoryOptions.DATABASE, path);
            }
        }
        if (hasText(uri.getRawQuery())) {
            parseQuery(uri.getRawQuery().trim(), (k, v) -> {
                if (PROHIBITED_QUERY_OPTIONS.contains(k)) {
                    throw new IllegalArgumentException(String.format("URL %s must not declare option %s in the query string", url, k));
                }
                builder.option(Option.valueOf(k), v);
            });
        }
        return builder.build();
    }

    static void parseQuery(CharSequence s, BiConsumer<String, String> tupleConsumer) {
        QueryStringParser parser = QueryStringParser.create(s);
        while (!parser.isFinished()) {
            CharSequence name = parser.parseName();
            CharSequence value = parser.isFinished() ? null : parser.parseValue();
            if (name.length() != 0 && value != null) {
                tupleConsumer.accept(decode(name).toString(), decode(value).toString());
            }
        }
    }

    private static void parseUserinfo(String s, ConnectionFactoryOptions.Builder builder) {
        if (!s.contains(":")) {
            builder.option(ConnectionFactoryOptions.USER, decode(s).toString());
            return;
        }
        String[] userinfo = s.split(":", 2);
        String user = decode(userinfo[0]).toString();
        if (!user.isEmpty()) {
            builder.option(ConnectionFactoryOptions.USER, user);
        }
        CharSequence password = decode(userinfo[1]);
        if (password.length() != 0) {
            builder.option(ConnectionFactoryOptions.PASSWORD, password);
        }
    }

    private static CharSequence decode(CharSequence s) {
        boolean encoded = false;
        int numChars = s.length();
        StringBuffer sb = new StringBuffer(numChars > 500 ? numChars / 2 : numChars);
        int i = 0;
        byte[] bytes = null;
        while (i < numChars) {
            char c = s.charAt(i);
            switch (c) {
                case '%':
                    if (bytes == null) {
                        try {
                            bytes = new byte[(numChars - i) / 3];
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException("URLDecoder: Illegal hex characters in escape (%) pattern - " + e.getMessage());
                        }
                    }
                    int pos = 0;
                    while (i + 2 < numChars && c == '%') {
                        int v = Integer.parseInt(s.subSequence(i + 1, i + 3).toString(), 16);
                        if (v < 0) {
                            throw new IllegalArgumentException("URLDecoder: Illegal hex characters in escape (%) pattern - negative value");
                        }
                        int i2 = pos;
                        pos++;
                        bytes[i2] = (byte) v;
                        i += 3;
                        if (i < numChars) {
                            c = s.charAt(i);
                        }
                    }
                    if (i < numChars && c == '%') {
                        throw new IllegalArgumentException("URLDecoder: Incomplete trailing escape (%) pattern");
                    }
                    sb.append((CharSequence) StandardCharsets.UTF_8.decode(ByteBuffer.wrap(bytes, 0, pos)));
                    encoded = true;
                    break;
                    break;
                case ELParserConstants.EMPTY /* 43 */:
                    sb.append(' ');
                    i++;
                    encoded = true;
                    break;
                default:
                    sb.append(c);
                    i++;
                    break;
            }
        }
        return encoded ? sb : s;
    }

    private static boolean hasText(@Nullable String s) {
        return (s == null || s.isEmpty()) ? false : true;
    }

    private ConnectionUrlParser() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/r2dbc-spi-1.0.0.RELEASE.jar:io/r2dbc/spi/ConnectionUrlParser$QueryStringParser.class */
    public static class QueryStringParser {
        static final char CR = '\r';
        static final char LF = '\n';
        static final char SPACE = ' ';
        static final char TAB = '\t';
        private final CharSequence input;
        private final Cursor state;
        private final BitSet delimiters = new BitSet(256);

        private QueryStringParser(CharSequence input) {
            this.input = input;
            this.state = new Cursor(input.length());
            this.delimiters.set(38);
        }

        static QueryStringParser create(CharSequence input) {
            return new QueryStringParser(input);
        }

        boolean isFinished() {
            return this.state.isFinished();
        }

        CharSequence parseName() {
            if (this.state.isFinished()) {
                throw new IllegalStateException("Parsing is finished");
            }
            this.delimiters.set(61);
            return parseToken();
        }

        @Nullable
        CharSequence parseValue() {
            if (this.state.isFinished()) {
                throw new IllegalStateException("Parsing is finished");
            }
            int delim = this.input.charAt(this.state.getParsePosition());
            this.state.incrementParsePosition();
            if (delim == 61) {
                this.delimiters.clear(61);
                try {
                    return parseToken();
                } finally {
                    if (!isFinished()) {
                        this.state.incrementParsePosition();
                    }
                }
            }
            return null;
        }

        private CharSequence parseToken() {
            StringBuilder dst = new StringBuilder();
            boolean z = false;
            while (true) {
                boolean whitespace = z;
                if (this.state.isFinished()) {
                    break;
                }
                char current = this.input.charAt(this.state.getParsePosition());
                if (this.delimiters.get(current)) {
                    break;
                }
                if (isWhitespace(current)) {
                    skipWhiteSpace();
                    z = true;
                } else {
                    if (whitespace && dst.length() > 0) {
                        dst.append(' ');
                    }
                    copyContent(dst);
                    z = false;
                }
            }
            return dst;
        }

        private void skipWhiteSpace() {
            int pos = this.state.getParsePosition();
            for (int i = this.state.getParsePosition(); i < this.state.getUpperBound(); i++) {
                char current = this.input.charAt(i);
                if (!isWhitespace(current)) {
                    break;
                }
                pos++;
            }
            this.state.updatePos(pos);
        }

        private void copyContent(StringBuilder target) {
            int pos = this.state.getParsePosition();
            for (int i = this.state.getParsePosition(); i < this.state.getUpperBound(); i++) {
                char current = this.input.charAt(i);
                if (this.delimiters.get(current) || isWhitespace(current)) {
                    break;
                }
                pos++;
                target.append(current);
            }
            this.state.updatePos(pos);
        }

        private static boolean isWhitespace(char ch2) {
            return ch2 == ' ' || ch2 == '\t' || ch2 == '\r' || ch2 == '\n';
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/r2dbc-spi-1.0.0.RELEASE.jar:io/r2dbc/spi/ConnectionUrlParser$Cursor.class */
    public static class Cursor {
        private final int upperBound;
        private int pos = 0;

        Cursor(int upperBound) {
            this.upperBound = upperBound;
        }

        void incrementParsePosition() {
            updatePos(getParsePosition() + 1);
        }

        int getUpperBound() {
            return this.upperBound;
        }

        int getParsePosition() {
            return this.pos;
        }

        void updatePos(int pos) {
            this.pos = pos;
        }

        boolean isFinished() {
            return this.pos >= this.upperBound;
        }
    }
}
