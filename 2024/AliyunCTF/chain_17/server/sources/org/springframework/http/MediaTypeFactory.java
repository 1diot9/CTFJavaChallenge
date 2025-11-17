package org.springframework.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/MediaTypeFactory.class */
public final class MediaTypeFactory {
    private static final String MIME_TYPES_FILE_NAME = "/org/springframework/http/mime.types";
    private static final MultiValueMap<String, MediaType> fileExtensionToMediaTypes = parseMimeTypes();

    private MediaTypeFactory() {
    }

    private static MultiValueMap<String, MediaType> parseMimeTypes() {
        InputStream is = MediaTypeFactory.class.getResourceAsStream(MIME_TYPES_FILE_NAME);
        Assert.state(is != null, "/org/springframework/http/mime.types not found in classpath");
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.US_ASCII));
            try {
                MultiValueMap<String, MediaType> result = new LinkedMultiValueMap<>();
                while (true) {
                    String line = reader.readLine();
                    if (line != null) {
                        if (!line.isEmpty() && line.charAt(0) != '#') {
                            String[] tokens = StringUtils.tokenizeToStringArray(line, " \t\n\r\f");
                            MediaType mediaType = MediaType.parseMediaType(tokens[0]);
                            for (int i = 1; i < tokens.length; i++) {
                                String fileExtension = tokens[i].toLowerCase(Locale.ENGLISH);
                                result.add(fileExtension, mediaType);
                            }
                        }
                    } else {
                        reader.close();
                        return result;
                    }
                }
            } finally {
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Could not read /org/springframework/http/mime.types", ex);
        }
    }

    public static Optional<MediaType> getMediaType(@Nullable Resource resource) {
        return Optional.ofNullable(resource).map((v0) -> {
            return v0.getFilename();
        }).flatMap(MediaTypeFactory::getMediaType);
    }

    public static Optional<MediaType> getMediaType(@Nullable String filename) {
        return getMediaTypes(filename).stream().findFirst();
    }

    public static List<MediaType> getMediaTypes(@Nullable String filename) {
        List<MediaType> mediaTypes = null;
        String ext = StringUtils.getFilenameExtension(filename);
        if (ext != null) {
            mediaTypes = (List) fileExtensionToMediaTypes.get(ext.toLowerCase(Locale.ENGLISH));
        }
        return mediaTypes != null ? mediaTypes : Collections.emptyList();
    }
}
