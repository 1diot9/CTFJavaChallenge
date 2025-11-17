package org.springframework.http;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.tomcat.util.http.fileupload.FileUploadBase;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.InvalidMimeTypeException;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/MediaType.class */
public class MediaType extends MimeType implements Serializable {
    private static final long serialVersionUID = 2069937152339670231L;
    public static final String ALL_VALUE = "*/*";
    public static final String APPLICATION_ATOM_XML_VALUE = "application/atom+xml";
    public static final String APPLICATION_CBOR_VALUE = "application/cbor";
    public static final String APPLICATION_FORM_URLENCODED_VALUE = "application/x-www-form-urlencoded";

    @Deprecated(since = "6.0.3", forRemoval = true)
    public static final String APPLICATION_GRAPHQL_VALUE = "application/graphql+json";
    public static final String APPLICATION_GRAPHQL_RESPONSE_VALUE = "application/graphql-response+json";
    public static final String APPLICATION_JSON_VALUE = "application/json";

    @Deprecated
    public static final String APPLICATION_JSON_UTF8_VALUE = "application/json;charset=UTF-8";
    public static final String APPLICATION_OCTET_STREAM_VALUE = "application/octet-stream";
    public static final String APPLICATION_PDF_VALUE = "application/pdf";
    public static final String APPLICATION_PROBLEM_JSON_VALUE = "application/problem+json";

    @Deprecated
    public static final String APPLICATION_PROBLEM_JSON_UTF8_VALUE = "application/problem+json;charset=UTF-8";
    public static final String APPLICATION_PROBLEM_XML_VALUE = "application/problem+xml";
    public static final String APPLICATION_PROTOBUF_VALUE = "application/x-protobuf";
    public static final String APPLICATION_RSS_XML_VALUE = "application/rss+xml";
    public static final String APPLICATION_NDJSON_VALUE = "application/x-ndjson";

    @Deprecated
    public static final String APPLICATION_STREAM_JSON_VALUE = "application/stream+json";
    public static final String APPLICATION_XHTML_XML_VALUE = "application/xhtml+xml";
    public static final String APPLICATION_XML_VALUE = "application/xml";
    public static final String IMAGE_GIF_VALUE = "image/gif";
    public static final String IMAGE_JPEG_VALUE = "image/jpeg";
    public static final String IMAGE_PNG_VALUE = "image/png";
    public static final String MULTIPART_FORM_DATA_VALUE = "multipart/form-data";
    public static final String MULTIPART_MIXED_VALUE = "multipart/mixed";
    public static final String MULTIPART_RELATED_VALUE = "multipart/related";
    public static final String TEXT_EVENT_STREAM_VALUE = "text/event-stream";
    public static final String TEXT_HTML_VALUE = "text/html";
    public static final String TEXT_MARKDOWN_VALUE = "text/markdown";
    public static final String TEXT_PLAIN_VALUE = "text/plain";
    public static final String TEXT_XML_VALUE = "text/xml";
    private static final String PARAM_QUALITY_FACTOR = "q";
    public static final MediaType ALL = new MediaType("*", "*");
    public static final MediaType APPLICATION_ATOM_XML = new MediaType("application", "atom+xml");
    public static final MediaType APPLICATION_CBOR = new MediaType("application", "cbor");
    public static final MediaType APPLICATION_FORM_URLENCODED = new MediaType("application", "x-www-form-urlencoded");

    @Deprecated(since = "6.0.3", forRemoval = true)
    public static final MediaType APPLICATION_GRAPHQL = new MediaType("application", "graphql+json");
    public static final MediaType APPLICATION_GRAPHQL_RESPONSE = new MediaType("application", "graphql-response+json");
    public static final MediaType APPLICATION_JSON = new MediaType("application", "json");

    @Deprecated
    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType("application", "json", StandardCharsets.UTF_8);
    public static final MediaType APPLICATION_NDJSON = new MediaType("application", "x-ndjson");
    public static final MediaType APPLICATION_OCTET_STREAM = new MediaType("application", "octet-stream");
    public static final MediaType APPLICATION_PDF = new MediaType("application", "pdf");
    public static final MediaType APPLICATION_PROBLEM_JSON = new MediaType("application", "problem+json");

    @Deprecated
    public static final MediaType APPLICATION_PROBLEM_JSON_UTF8 = new MediaType("application", "problem+json", StandardCharsets.UTF_8);
    public static final MediaType APPLICATION_PROBLEM_XML = new MediaType("application", "problem+xml");
    public static final MediaType APPLICATION_PROTOBUF = new MediaType("application", "x-protobuf");
    public static final MediaType APPLICATION_RSS_XML = new MediaType("application", "rss+xml");

    @Deprecated
    public static final MediaType APPLICATION_STREAM_JSON = new MediaType("application", "stream+json");
    public static final MediaType APPLICATION_XHTML_XML = new MediaType("application", "xhtml+xml");
    public static final MediaType APPLICATION_XML = new MediaType("application", "xml");
    public static final MediaType IMAGE_GIF = new MediaType("image", "gif");
    public static final MediaType IMAGE_JPEG = new MediaType("image", "jpeg");
    public static final MediaType IMAGE_PNG = new MediaType("image", "png");
    public static final MediaType MULTIPART_FORM_DATA = new MediaType("multipart", FileUploadBase.FORM_DATA);
    public static final MediaType MULTIPART_MIXED = new MediaType("multipart", "mixed");
    public static final MediaType MULTIPART_RELATED = new MediaType("multipart", "related");
    public static final MediaType TEXT_EVENT_STREAM = new MediaType("text", "event-stream");
    public static final MediaType TEXT_HTML = new MediaType("text", "html");
    public static final MediaType TEXT_MARKDOWN = new MediaType("text", "markdown");
    public static final MediaType TEXT_PLAIN = new MediaType("text", "plain");
    public static final MediaType TEXT_XML = new MediaType("text", "xml");

    @Deprecated(since = "6.0", forRemoval = true)
    public static final Comparator<MediaType> QUALITY_VALUE_COMPARATOR = (mediaType1, mediaType2) -> {
        double quality1 = mediaType1.getQualityValue();
        double quality2 = mediaType2.getQualityValue();
        int qualityComparison = Double.compare(quality2, quality1);
        if (qualityComparison != 0) {
            return qualityComparison;
        }
        if (mediaType1.isWildcardType() && !mediaType2.isWildcardType()) {
            return 1;
        }
        if (mediaType2.isWildcardType() && !mediaType1.isWildcardType()) {
            return -1;
        }
        if (!mediaType1.getType().equals(mediaType2.getType())) {
            return 0;
        }
        if (mediaType1.isWildcardSubtype() && !mediaType2.isWildcardSubtype()) {
            return 1;
        }
        if (mediaType2.isWildcardSubtype() && !mediaType1.isWildcardSubtype()) {
            return -1;
        }
        if (!mediaType1.getSubtype().equals(mediaType2.getSubtype())) {
            return 0;
        }
        int paramsSize1 = mediaType1.getParameters().size();
        int paramsSize2 = mediaType2.getParameters().size();
        return Integer.compare(paramsSize2, paramsSize1);
    };

    @Deprecated(since = "6.0", forRemoval = true)
    public static final Comparator<MediaType> SPECIFICITY_COMPARATOR = new MimeType.SpecificityComparator<MediaType>() { // from class: org.springframework.http.MediaType.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // org.springframework.util.MimeType.SpecificityComparator
        public int compareParameters(MediaType mediaType1, MediaType mediaType2) {
            double quality1 = mediaType1.getQualityValue();
            double quality2 = mediaType2.getQualityValue();
            int qualityComparison = Double.compare(quality2, quality1);
            if (qualityComparison != 0) {
                return qualityComparison;
            }
            return super.compareParameters(mediaType1, mediaType2);
        }
    };

    public MediaType(String type) {
        super(type);
    }

    public MediaType(String type, String subtype) {
        super(type, subtype, (Map<String, String>) Collections.emptyMap());
    }

    public MediaType(String type, String subtype, Charset charset) {
        super(type, subtype, charset);
    }

    public MediaType(String type, String subtype, double qualityValue) {
        this(type, subtype, (Map<String, String>) Collections.singletonMap(PARAM_QUALITY_FACTOR, Double.toString(qualityValue)));
    }

    public MediaType(MediaType other, Charset charset) {
        super(other, charset);
    }

    public MediaType(MediaType other, @Nullable Map<String, String> parameters) {
        super(other.getType(), other.getSubtype(), parameters);
    }

    public MediaType(String type, String subtype, @Nullable Map<String, String> parameters) {
        super(type, subtype, parameters);
    }

    public MediaType(MimeType mimeType) {
        super(mimeType);
        getParameters().forEach(this::checkParameters);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.util.MimeType
    public void checkParameters(String parameter, String value) {
        super.checkParameters(parameter, value);
        if (PARAM_QUALITY_FACTOR.equals(parameter)) {
            String unquotedValue = unquote(value);
            double d = Double.parseDouble(unquotedValue);
            Assert.isTrue(d >= 0.0d && d <= 1.0d, (Supplier<String>) () -> {
                return "Invalid quality value \"" + unquotedValue + "\": should be between 0.0 and 1.0";
            });
        }
    }

    public double getQualityValue() {
        String qualityFactor = getParameter(PARAM_QUALITY_FACTOR);
        if (qualityFactor != null) {
            return Double.parseDouble(unquote(qualityFactor));
        }
        return 1.0d;
    }

    @Override // org.springframework.util.MimeType
    public boolean isMoreSpecific(MimeType other) {
        Assert.notNull(other, "Other must not be null");
        if (other instanceof MediaType) {
            MediaType otherMediaType = (MediaType) other;
            double quality1 = getQualityValue();
            double quality2 = otherMediaType.getQualityValue();
            if (quality1 > quality2) {
                return true;
            }
            if (quality1 < quality2) {
                return false;
            }
        }
        return super.isMoreSpecific(other);
    }

    @Override // org.springframework.util.MimeType
    public boolean isLessSpecific(MimeType other) {
        Assert.notNull(other, "Other must not be null");
        return other.isMoreSpecific(this);
    }

    public boolean includes(@Nullable MediaType other) {
        return super.includes((MimeType) other);
    }

    public boolean isCompatibleWith(@Nullable MediaType other) {
        return super.isCompatibleWith((MimeType) other);
    }

    public MediaType copyQualityValue(MediaType mediaType) {
        if (!mediaType.getParameters().containsKey(PARAM_QUALITY_FACTOR)) {
            return this;
        }
        Map<String, String> params = new LinkedHashMap<>(getParameters());
        params.put(PARAM_QUALITY_FACTOR, mediaType.getParameters().get(PARAM_QUALITY_FACTOR));
        return new MediaType(this, params);
    }

    public MediaType removeQualityValue() {
        if (!getParameters().containsKey(PARAM_QUALITY_FACTOR)) {
            return this;
        }
        Map<String, String> params = new LinkedHashMap<>(getParameters());
        params.remove(PARAM_QUALITY_FACTOR);
        return new MediaType(this, params);
    }

    public static MediaType valueOf(String value) {
        return parseMediaType(value);
    }

    public static MediaType parseMediaType(String mediaType) {
        try {
            MimeType type = MimeTypeUtils.parseMimeType(mediaType);
            try {
                return new MediaType(type);
            } catch (IllegalArgumentException ex) {
                throw new InvalidMediaTypeException(mediaType, ex.getMessage());
            }
        } catch (InvalidMimeTypeException ex2) {
            throw new InvalidMediaTypeException(ex2);
        }
    }

    public static List<MediaType> parseMediaTypes(@Nullable String mediaTypes) {
        if (!StringUtils.hasLength(mediaTypes)) {
            return Collections.emptyList();
        }
        List<String> tokenizedTypes = MimeTypeUtils.tokenize(mediaTypes);
        List<MediaType> result = new ArrayList<>(tokenizedTypes.size());
        for (String type : tokenizedTypes) {
            if (StringUtils.hasText(type)) {
                result.add(parseMediaType(type));
            }
        }
        return result;
    }

    public static List<MediaType> parseMediaTypes(@Nullable List<String> mediaTypes) {
        if (CollectionUtils.isEmpty(mediaTypes)) {
            return Collections.emptyList();
        }
        if (mediaTypes.size() == 1) {
            return parseMediaTypes(mediaTypes.get(0));
        }
        List<MediaType> result = new ArrayList<>(8);
        for (String mediaType : mediaTypes) {
            result.addAll(parseMediaTypes(mediaType));
        }
        return result;
    }

    public static List<MediaType> asMediaTypes(List<MimeType> mimeTypes) {
        List<MediaType> mediaTypes = new ArrayList<>(mimeTypes.size());
        for (MimeType mimeType : mimeTypes) {
            mediaTypes.add(asMediaType(mimeType));
        }
        return mediaTypes;
    }

    public static MediaType asMediaType(MimeType mimeType) {
        if (mimeType instanceof MediaType) {
            MediaType mediaType = (MediaType) mimeType;
            return mediaType;
        }
        return new MediaType(mimeType.getType(), mimeType.getSubtype(), mimeType.getParameters());
    }

    public static String toString(Collection<MediaType> mediaTypes) {
        return MimeTypeUtils.toString(mediaTypes);
    }

    @Deprecated(since = "6.0", forRemoval = true)
    public static void sortBySpecificity(List<MediaType> mediaTypes) {
        Assert.notNull(mediaTypes, "'mediaTypes' must not be null");
        if (mediaTypes.size() > 1) {
            mediaTypes.sort(SPECIFICITY_COMPARATOR);
        }
    }

    @Deprecated(since = "6.0", forRemoval = true)
    public static void sortByQualityValue(List<MediaType> mediaTypes) {
        Assert.notNull(mediaTypes, "'mediaTypes' must not be null");
        if (mediaTypes.size() > 1) {
            mediaTypes.sort(QUALITY_VALUE_COMPARATOR);
        }
    }

    @Deprecated(since = "6.0")
    public static void sortBySpecificityAndQuality(List<MediaType> mediaTypes) {
        Assert.notNull(mediaTypes, "'mediaTypes' must not be null");
        if (mediaTypes.size() > 1) {
            mediaTypes.sort(SPECIFICITY_COMPARATOR.thenComparing(QUALITY_VALUE_COMPARATOR));
        }
    }
}
