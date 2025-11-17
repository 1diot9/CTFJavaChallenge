package org.springframework.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Supplier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/HttpRange.class */
public abstract class HttpRange {
    private static final int MAX_RANGES = 100;
    private static final String BYTE_RANGE_PREFIX = "bytes=";

    public abstract long getRangeStart(long length);

    public abstract long getRangeEnd(long length);

    public ResourceRegion toResourceRegion(Resource resource) {
        Assert.isTrue(resource.getClass() != InputStreamResource.class, "Cannot convert an InputStreamResource to a ResourceRegion");
        long contentLength = getLengthFor(resource);
        long start = getRangeStart(contentLength);
        long end = getRangeEnd(contentLength);
        Assert.isTrue(start < contentLength, (Supplier<String>) () -> {
            return "'position' exceeds the resource length " + contentLength;
        });
        return new ResourceRegion(resource, start, (end - start) + 1);
    }

    public static HttpRange createByteRange(long firstBytePos) {
        return new ByteRange(firstBytePos, null);
    }

    public static HttpRange createByteRange(long firstBytePos, long lastBytePos) {
        return new ByteRange(firstBytePos, Long.valueOf(lastBytePos));
    }

    public static HttpRange createSuffixRange(long suffixLength) {
        return new SuffixByteRange(suffixLength);
    }

    public static List<HttpRange> parseRanges(@Nullable String ranges) {
        if (!StringUtils.hasLength(ranges)) {
            return Collections.emptyList();
        }
        if (!ranges.startsWith(BYTE_RANGE_PREFIX)) {
            throw new IllegalArgumentException("Range '" + ranges + "' does not start with 'bytes='");
        }
        String[] tokens = StringUtils.tokenizeToStringArray(ranges.substring(BYTE_RANGE_PREFIX.length()), ",");
        if (tokens.length > 100) {
            throw new IllegalArgumentException("Too many ranges: " + tokens.length);
        }
        List<HttpRange> result = new ArrayList<>(tokens.length);
        for (String token : tokens) {
            result.add(parseRange(token));
        }
        return result;
    }

    private static HttpRange parseRange(String range) {
        Assert.hasLength(range, "Range String must not be empty");
        int dashIdx = range.indexOf(45);
        if (dashIdx > 0) {
            long firstPos = Long.parseLong(range, 0, dashIdx, 10);
            if (dashIdx < range.length() - 1) {
                Long lastPos = Long.valueOf(Long.parseLong(range, dashIdx + 1, range.length(), 10));
                return new ByteRange(firstPos, lastPos);
            }
            return new ByteRange(firstPos, null);
        }
        if (dashIdx == 0) {
            long suffixLength = Long.parseLong(range, 1, range.length(), 10);
            return new SuffixByteRange(suffixLength);
        }
        throw new IllegalArgumentException("Range '" + range + "' does not contain \"-\"");
    }

    public static List<ResourceRegion> toResourceRegions(List<HttpRange> ranges, Resource resource) {
        if (CollectionUtils.isEmpty(ranges)) {
            return Collections.emptyList();
        }
        List<ResourceRegion> regions = new ArrayList<>(ranges.size());
        for (HttpRange range : ranges) {
            regions.add(range.toResourceRegion(resource));
        }
        if (ranges.size() > 1) {
            long length = getLengthFor(resource);
            long total = 0;
            for (ResourceRegion region : regions) {
                total += region.getCount();
            }
            if (total >= length) {
                IllegalArgumentException illegalArgumentException = new IllegalArgumentException("The sum of all ranges (" + total + ") should be less than the resource length (" + illegalArgumentException + ")");
                throw illegalArgumentException;
            }
        }
        return regions;
    }

    private static long getLengthFor(Resource resource) {
        try {
            long contentLength = resource.contentLength();
            Assert.isTrue(contentLength > 0, "Resource content length should be > 0");
            return contentLength;
        } catch (IOException ex) {
            throw new IllegalArgumentException("Failed to obtain Resource content length", ex);
        }
    }

    public static String toString(Collection<HttpRange> ranges) {
        Assert.notEmpty(ranges, "Ranges Collection must not be empty");
        StringJoiner builder = new StringJoiner(", ", BYTE_RANGE_PREFIX, "");
        for (HttpRange range : ranges) {
            builder.add(range.toString());
        }
        return builder.toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/HttpRange$ByteRange.class */
    public static class ByteRange extends HttpRange {
        private final long firstPos;

        @Nullable
        private final Long lastPos;

        public ByteRange(long firstPos, @Nullable Long lastPos) {
            assertPositions(firstPos, lastPos);
            this.firstPos = firstPos;
            this.lastPos = lastPos;
        }

        private void assertPositions(long firstBytePos, @Nullable Long lastBytePos) {
            if (firstBytePos < 0) {
                throw new IllegalArgumentException("Invalid first byte position: " + firstBytePos);
            }
            if (lastBytePos != null && lastBytePos.longValue() < firstBytePos) {
                IllegalArgumentException illegalArgumentException = new IllegalArgumentException("firstBytePosition=" + firstBytePos + " should be less then or equal to lastBytePosition=" + illegalArgumentException);
                throw illegalArgumentException;
            }
        }

        @Override // org.springframework.http.HttpRange
        public long getRangeStart(long length) {
            return this.firstPos;
        }

        @Override // org.springframework.http.HttpRange
        public long getRangeEnd(long length) {
            if (this.lastPos != null && this.lastPos.longValue() < length) {
                return this.lastPos.longValue();
            }
            return length - 1;
        }

        public boolean equals(@Nullable Object other) {
            if (this != other) {
                if (other instanceof ByteRange) {
                    ByteRange that = (ByteRange) other;
                    if (this.firstPos != that.firstPos || !ObjectUtils.nullSafeEquals(this.lastPos, that.lastPos)) {
                    }
                }
                return false;
            }
            return true;
        }

        public int hashCode() {
            return Objects.hash(Long.valueOf(this.firstPos), this.lastPos);
        }

        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append(this.firstPos);
            builder.append('-');
            if (this.lastPos != null) {
                builder.append(this.lastPos);
            }
            return builder.toString();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/HttpRange$SuffixByteRange.class */
    public static class SuffixByteRange extends HttpRange {
        private final long suffixLength;

        public SuffixByteRange(long suffixLength) {
            if (suffixLength < 0) {
                throw new IllegalArgumentException("Invalid suffix length: " + suffixLength);
            }
            this.suffixLength = suffixLength;
        }

        @Override // org.springframework.http.HttpRange
        public long getRangeStart(long length) {
            if (this.suffixLength < length) {
                return length - this.suffixLength;
            }
            return 0L;
        }

        @Override // org.springframework.http.HttpRange
        public long getRangeEnd(long length) {
            return length - 1;
        }

        public boolean equals(@Nullable Object other) {
            if (this != other) {
                if (other instanceof SuffixByteRange) {
                    SuffixByteRange that = (SuffixByteRange) other;
                    if (this.suffixLength == that.suffixLength) {
                    }
                }
                return false;
            }
            return true;
        }

        public int hashCode() {
            return Long.hashCode(this.suffixLength);
        }

        public String toString() {
            return "-" + this.suffixLength;
        }
    }
}
