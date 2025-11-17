package org.springframework.util;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/MimeTypeUtils.class */
public abstract class MimeTypeUtils {
    public static final String ALL_VALUE = "*/*";
    public static final String APPLICATION_GRAPHQL_VALUE = "application/graphql+json";
    public static final String APPLICATION_JSON_VALUE = "application/json";
    public static final String APPLICATION_OCTET_STREAM_VALUE = "application/octet-stream";
    public static final String APPLICATION_XML_VALUE = "application/xml";
    public static final String IMAGE_GIF_VALUE = "image/gif";
    public static final String IMAGE_JPEG_VALUE = "image/jpeg";
    public static final String IMAGE_PNG_VALUE = "image/png";
    public static final String TEXT_HTML_VALUE = "text/html";
    public static final String TEXT_PLAIN_VALUE = "text/plain";
    public static final String TEXT_XML_VALUE = "text/xml";

    @Nullable
    private static volatile Random random;
    private static final byte[] BOUNDARY_CHARS = {45, 95, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90};

    @Deprecated(since = "6.0", forRemoval = true)
    public static final Comparator<MimeType> SPECIFICITY_COMPARATOR = new MimeType.SpecificityComparator();
    private static final ConcurrentLruCache<String, MimeType> cachedMimeTypes = new ConcurrentLruCache<>(64, MimeTypeUtils::parseMimeTypeInternal);
    public static final MimeType ALL = new MimeType("*", "*");
    public static final MimeType APPLICATION_GRAPHQL = new MimeType("application", "graphql+json");
    public static final MimeType APPLICATION_JSON = new MimeType("application", "json");
    public static final MimeType APPLICATION_OCTET_STREAM = new MimeType("application", "octet-stream");
    public static final MimeType APPLICATION_XML = new MimeType("application", "xml");
    public static final MimeType IMAGE_GIF = new MimeType("image", "gif");
    public static final MimeType IMAGE_JPEG = new MimeType("image", "jpeg");
    public static final MimeType IMAGE_PNG = new MimeType("image", "png");
    public static final MimeType TEXT_HTML = new MimeType("text", "html");
    public static final MimeType TEXT_PLAIN = new MimeType("text", "plain");
    public static final MimeType TEXT_XML = new MimeType("text", "xml");

    public static MimeType parseMimeType(String mimeType) {
        if (!StringUtils.hasLength(mimeType)) {
            throw new InvalidMimeTypeException(mimeType, "'mimeType' must not be empty");
        }
        if (mimeType.startsWith("multipart")) {
            return parseMimeTypeInternal(mimeType);
        }
        return cachedMimeTypes.get(mimeType);
    }

    /* JADX WARN: Code restructure failed: missing block: B:49:0x014a, code lost:            r13 = move-exception;     */
    /* JADX WARN: Code restructure failed: missing block: B:51:0x015e, code lost:            throw new org.springframework.util.InvalidMimeTypeException(r6, "unsupported charset '" + r13.getCharsetName() + "'");     */
    /* JADX WARN: Code restructure failed: missing block: B:52:0x015f, code lost:            r13 = move-exception;     */
    /* JADX WARN: Code restructure failed: missing block: B:54:0x016e, code lost:            throw new org.springframework.util.InvalidMimeTypeException(r6, r13.getMessage());     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static org.springframework.util.MimeType parseMimeTypeInternal(java.lang.String r6) {
        /*
            Method dump skipped, instructions count: 367
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.springframework.util.MimeTypeUtils.parseMimeTypeInternal(java.lang.String):org.springframework.util.MimeType");
    }

    public static List<MimeType> parseMimeTypes(String mimeTypes) {
        if (!StringUtils.hasLength(mimeTypes)) {
            return Collections.emptyList();
        }
        return (List) tokenize(mimeTypes).stream().filter(StringUtils::hasText).map(MimeTypeUtils::parseMimeType).collect(Collectors.toList());
    }

    public static List<String> tokenize(String mimeTypes) {
        if (!StringUtils.hasLength(mimeTypes)) {
            return Collections.emptyList();
        }
        List<String> tokens = new ArrayList<>();
        boolean inQuotes = false;
        int startIndex = 0;
        int i = 0;
        while (i < mimeTypes.length()) {
            switch (mimeTypes.charAt(i)) {
                case '\"':
                    inQuotes = !inQuotes;
                    break;
                case ',':
                    if (!inQuotes) {
                        tokens.add(mimeTypes.substring(startIndex, i));
                        startIndex = i + 1;
                        break;
                    } else {
                        break;
                    }
                case '\\':
                    i++;
                    break;
            }
            i++;
        }
        tokens.add(mimeTypes.substring(startIndex));
        return tokens;
    }

    public static String toString(Collection<? extends MimeType> mimeTypes) {
        StringBuilder builder = new StringBuilder();
        Iterator<? extends MimeType> iterator = mimeTypes.iterator();
        while (iterator.hasNext()) {
            MimeType mimeType = iterator.next();
            mimeType.appendTo(builder);
            if (iterator.hasNext()) {
                builder.append(", ");
            }
        }
        return builder.toString();
    }

    public static <T extends MimeType> void sortBySpecificity(List<T> mimeTypes) {
        Assert.notNull(mimeTypes, "'mimeTypes' must not be null");
        if (mimeTypes.size() > 50) {
            throw new InvalidMimeTypeException(mimeTypes.toString(), "Too many elements");
        }
        bubbleSort(mimeTypes, (v0, v1) -> {
            return v0.isLessSpecific(v1);
        });
    }

    static <T> void bubbleSort(List<T> list, BiPredicate<? super T, ? super T> swap) {
        int len = list.size();
        for (int i = 0; i < len; i++) {
            for (int j = 1; j < len - i; j++) {
                T prev = list.get(j - 1);
                T cur = list.get(j);
                if (swap.test(prev, cur)) {
                    list.set(j, prev);
                    list.set(j - 1, cur);
                }
            }
        }
    }

    public static byte[] generateMultipartBoundary() {
        Random randomToUse = initRandom();
        byte[] boundary = new byte[randomToUse.nextInt(11) + 30];
        for (int i = 0; i < boundary.length; i++) {
            boundary[i] = BOUNDARY_CHARS[randomToUse.nextInt(BOUNDARY_CHARS.length)];
        }
        return boundary;
    }

    private static Random initRandom() {
        Random randomToUse = random;
        if (randomToUse == null) {
            synchronized (MimeTypeUtils.class) {
                randomToUse = random;
                if (randomToUse == null) {
                    randomToUse = new SecureRandom();
                    random = randomToUse;
                }
            }
        }
        return randomToUse;
    }

    public static String generateMultipartBoundaryString() {
        return new String(generateMultipartBoundary(), StandardCharsets.US_ASCII);
    }
}
