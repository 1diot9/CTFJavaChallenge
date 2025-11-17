package org.springframework.web.util;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/util/ForwardedHeaderUtils.class */
public abstract class ForwardedHeaderUtils {
    private static final String FORWARDED_VALUE = "\"?([^;,\"]+)\"?";
    private static final Pattern FORWARDED_HOST_PATTERN = Pattern.compile("(?i:host)=\"?([^;,\"]+)\"?");
    private static final Pattern FORWARDED_PROTO_PATTERN = Pattern.compile("(?i:proto)=\"?([^;,\"]+)\"?");
    private static final Pattern FORWARDED_FOR_PATTERN = Pattern.compile("(?i:for)=\"?([^;,\"]+)\"?");

    public static UriComponentsBuilder adaptFromForwardedHeaders(URI uri, HttpHeaders headers) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUri(uri);
        try {
            String forwardedHeader = headers.getFirst("Forwarded");
            if (StringUtils.hasText(forwardedHeader)) {
                Matcher matcher = FORWARDED_PROTO_PATTERN.matcher(forwardedHeader);
                if (matcher.find()) {
                    uriComponentsBuilder.scheme(matcher.group(1).trim());
                    uriComponentsBuilder.port((String) null);
                } else if (isForwardedSslOn(headers)) {
                    uriComponentsBuilder.scheme("https");
                    uriComponentsBuilder.port((String) null);
                }
                Matcher matcher2 = FORWARDED_HOST_PATTERN.matcher(forwardedHeader);
                if (matcher2.find()) {
                    adaptForwardedHost(uriComponentsBuilder, matcher2.group(1).trim());
                }
            } else {
                String protocolHeader = headers.getFirst("X-Forwarded-Proto");
                if (StringUtils.hasText(protocolHeader)) {
                    uriComponentsBuilder.scheme(StringUtils.tokenizeToStringArray(protocolHeader, ",")[0]);
                    uriComponentsBuilder.port((String) null);
                } else if (isForwardedSslOn(headers)) {
                    uriComponentsBuilder.scheme("https");
                    uriComponentsBuilder.port((String) null);
                }
                String hostHeader = headers.getFirst("X-Forwarded-Host");
                if (StringUtils.hasText(hostHeader)) {
                    adaptForwardedHost(uriComponentsBuilder, StringUtils.tokenizeToStringArray(hostHeader, ",")[0]);
                }
                String portHeader = headers.getFirst("X-Forwarded-Port");
                if (StringUtils.hasText(portHeader)) {
                    uriComponentsBuilder.port(Integer.parseInt(StringUtils.tokenizeToStringArray(portHeader, ",")[0]));
                }
            }
            uriComponentsBuilder.resetPortIfDefaultForScheme();
            return uriComponentsBuilder;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Failed to parse a port from \"forwarded\"-type headers. If not behind a trusted proxy, consider using ForwardedHeaderFilter with removeOnly=true. Request headers: " + headers);
        }
    }

    private static boolean isForwardedSslOn(HttpHeaders headers) {
        String forwardedSsl = headers.getFirst("X-Forwarded-Ssl");
        return StringUtils.hasText(forwardedSsl) && forwardedSsl.equalsIgnoreCase(CustomBooleanEditor.VALUE_ON);
    }

    private static void adaptForwardedHost(UriComponentsBuilder uriComponentsBuilder, String rawValue) {
        int portSeparatorIdx = rawValue.lastIndexOf(58);
        int squareBracketIdx = rawValue.lastIndexOf(93);
        if (portSeparatorIdx > squareBracketIdx) {
            if (squareBracketIdx == -1 && rawValue.indexOf(58) != portSeparatorIdx) {
                throw new IllegalArgumentException("Invalid IPv4 address: " + rawValue);
            }
            uriComponentsBuilder.host(rawValue.substring(0, portSeparatorIdx));
            uriComponentsBuilder.port(Integer.parseInt(rawValue, portSeparatorIdx + 1, rawValue.length(), 10));
            return;
        }
        uriComponentsBuilder.host(rawValue);
        uriComponentsBuilder.port((String) null);
    }

    @Nullable
    public static InetSocketAddress parseForwardedFor(URI uri, HttpHeaders headers, @Nullable InetSocketAddress remoteAddress) {
        int i;
        if (remoteAddress != null) {
            i = remoteAddress.getPort();
        } else {
            i = "https".equals(uri.getScheme()) ? 443 : 80;
        }
        int port = i;
        String forwardedHeader = headers.getFirst("Forwarded");
        if (StringUtils.hasText(forwardedHeader)) {
            String forwardedToUse = StringUtils.tokenizeToStringArray(forwardedHeader, ",")[0];
            Matcher matcher = FORWARDED_FOR_PATTERN.matcher(forwardedToUse);
            if (matcher.find()) {
                String value = matcher.group(1).trim();
                String host = value;
                int portSeparatorIdx = value.lastIndexOf(58);
                int squareBracketIdx = value.lastIndexOf(93);
                if (portSeparatorIdx > squareBracketIdx) {
                    if (squareBracketIdx == -1 && value.indexOf(58) != portSeparatorIdx) {
                        throw new IllegalArgumentException("Invalid IPv4 address: " + value);
                    }
                    host = value.substring(0, portSeparatorIdx);
                    try {
                        port = Integer.parseInt(value, portSeparatorIdx + 1, value.length(), 10);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Failed to parse a port from \"forwarded\"-type header value: " + value);
                    }
                }
                return InetSocketAddress.createUnresolved(host, port);
            }
        }
        String forHeader = headers.getFirst("X-Forwarded-For");
        if (StringUtils.hasText(forHeader)) {
            String host2 = StringUtils.tokenizeToStringArray(forHeader, ",")[0];
            return InetSocketAddress.createUnresolved(host2, port);
        }
        return null;
    }
}
