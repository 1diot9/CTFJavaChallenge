package org.springframework.http;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Base64;
import java.util.BitSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.tomcat.util.http.fileupload.FileUploadBase;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.servlet.tags.form.InputTag;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/ContentDisposition.class */
public final class ContentDisposition {
    private static final String INVALID_HEADER_FIELD_PARAMETER_FORMAT = "Invalid header field parameter format (as defined in RFC 5987)";

    @Nullable
    private final String type;

    @Nullable
    private final String name;

    @Nullable
    private final String filename;

    @Nullable
    private final Charset charset;

    @Nullable
    private final Long size;

    @Nullable
    private final ZonedDateTime creationDate;

    @Nullable
    private final ZonedDateTime modificationDate;

    @Nullable
    private final ZonedDateTime readDate;
    private static final Pattern BASE64_ENCODED_PATTERN = Pattern.compile("=\\?([0-9a-zA-Z-_]+)\\?B\\?([+/0-9a-zA-Z]+=*)\\?=");
    private static final Pattern QUOTED_PRINTABLE_ENCODED_PATTERN = Pattern.compile("=\\?([0-9a-zA-Z-_]+)\\?Q\\?([!->@-~]+)\\?=");
    private static final BitSet PRINTABLE = new BitSet(256);

    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/ContentDisposition$Builder.class */
    public interface Builder {
        Builder name(@Nullable String name);

        Builder filename(@Nullable String filename);

        Builder filename(@Nullable String filename, @Nullable Charset charset);

        @Deprecated
        Builder size(@Nullable Long size);

        @Deprecated
        Builder creationDate(@Nullable ZonedDateTime creationDate);

        @Deprecated
        Builder modificationDate(@Nullable ZonedDateTime modificationDate);

        @Deprecated
        Builder readDate(@Nullable ZonedDateTime readDate);

        ContentDisposition build();
    }

    static {
        for (int i = 33; i <= 126; i++) {
            PRINTABLE.set(i);
        }
        PRINTABLE.set(61, false);
        PRINTABLE.set(63, false);
        PRINTABLE.set(95, false);
    }

    private ContentDisposition(@Nullable String type, @Nullable String name, @Nullable String filename, @Nullable Charset charset, @Nullable Long size, @Nullable ZonedDateTime creationDate, @Nullable ZonedDateTime modificationDate, @Nullable ZonedDateTime readDate) {
        this.type = type;
        this.name = name;
        this.filename = filename;
        this.charset = charset;
        this.size = size;
        this.creationDate = creationDate;
        this.modificationDate = modificationDate;
        this.readDate = readDate;
    }

    public boolean isAttachment() {
        return this.type != null && this.type.equalsIgnoreCase(FileUploadBase.ATTACHMENT);
    }

    public boolean isFormData() {
        return this.type != null && this.type.equalsIgnoreCase(FileUploadBase.FORM_DATA);
    }

    public boolean isInline() {
        return this.type != null && this.type.equalsIgnoreCase("inline");
    }

    @Nullable
    public String getType() {
        return this.type;
    }

    @Nullable
    public String getName() {
        return this.name;
    }

    @Nullable
    public String getFilename() {
        return this.filename;
    }

    @Nullable
    public Charset getCharset() {
        return this.charset;
    }

    @Nullable
    @Deprecated
    public Long getSize() {
        return this.size;
    }

    @Nullable
    @Deprecated
    public ZonedDateTime getCreationDate() {
        return this.creationDate;
    }

    @Nullable
    @Deprecated
    public ZonedDateTime getModificationDate() {
        return this.modificationDate;
    }

    @Nullable
    @Deprecated
    public ZonedDateTime getReadDate() {
        return this.readDate;
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof ContentDisposition) {
                ContentDisposition that = (ContentDisposition) other;
                if (!ObjectUtils.nullSafeEquals(this.type, that.type) || !ObjectUtils.nullSafeEquals(this.name, that.name) || !ObjectUtils.nullSafeEquals(this.filename, that.filename) || !ObjectUtils.nullSafeEquals(this.charset, that.charset) || !ObjectUtils.nullSafeEquals(this.size, that.size) || !ObjectUtils.nullSafeEquals(this.creationDate, that.creationDate) || !ObjectUtils.nullSafeEquals(this.modificationDate, that.modificationDate) || !ObjectUtils.nullSafeEquals(this.readDate, that.readDate)) {
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        return ObjectUtils.nullSafeHash(this.type, this.name, this.filename, this.charset, this.size, this.creationDate, this.modificationDate, this.readDate);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.type != null) {
            sb.append(this.type);
        }
        if (this.name != null) {
            sb.append("; name=\"");
            sb.append(this.name).append('\"');
        }
        if (this.filename != null) {
            if (this.charset == null || StandardCharsets.US_ASCII.equals(this.charset)) {
                sb.append("; filename=\"");
                sb.append(encodeQuotedPairs(this.filename)).append('\"');
            } else {
                sb.append("; filename=\"");
                sb.append(encodeQuotedPrintableFilename(this.filename, this.charset)).append('\"');
                sb.append("; filename*=");
                sb.append(encodeRfc5987Filename(this.filename, this.charset));
            }
        }
        if (this.size != null) {
            sb.append("; size=");
            sb.append(this.size);
        }
        if (this.creationDate != null) {
            sb.append("; creation-date=\"");
            sb.append(DateTimeFormatter.RFC_1123_DATE_TIME.format(this.creationDate));
            sb.append('\"');
        }
        if (this.modificationDate != null) {
            sb.append("; modification-date=\"");
            sb.append(DateTimeFormatter.RFC_1123_DATE_TIME.format(this.modificationDate));
            sb.append('\"');
        }
        if (this.readDate != null) {
            sb.append("; read-date=\"");
            sb.append(DateTimeFormatter.RFC_1123_DATE_TIME.format(this.readDate));
            sb.append('\"');
        }
        return sb.toString();
    }

    public static Builder attachment() {
        return builder(FileUploadBase.ATTACHMENT);
    }

    public static Builder formData() {
        return builder(FileUploadBase.FORM_DATA);
    }

    public static Builder inline() {
        return builder("inline");
    }

    public static Builder builder(String type) {
        return new BuilderImpl(type);
    }

    public static ContentDisposition empty() {
        return new ContentDisposition("", null, null, null, null, null, null, null);
    }

    public static ContentDisposition parse(String contentDisposition) {
        String substring;
        List<String> parts = tokenize(contentDisposition);
        String type = parts.get(0);
        String name = null;
        String filename = null;
        Charset charset = null;
        Long size = null;
        ZonedDateTime creationDate = null;
        ZonedDateTime modificationDate = null;
        ZonedDateTime readDate = null;
        for (int i = 1; i < parts.size(); i++) {
            String part = parts.get(i);
            int eqIndex = part.indexOf(61);
            if (eqIndex != -1) {
                String attribute = part.substring(0, eqIndex);
                if (part.startsWith("\"", eqIndex + 1) && part.endsWith("\"")) {
                    substring = part.substring(eqIndex + 2, part.length() - 1);
                } else {
                    substring = part.substring(eqIndex + 1);
                }
                String value = substring;
                if (attribute.equals("name")) {
                    name = value;
                } else if (attribute.equals("filename*")) {
                    int idx1 = value.indexOf(39);
                    int idx2 = value.indexOf(39, idx1 + 1);
                    if (idx1 != -1 && idx2 != -1) {
                        charset = Charset.forName(value.substring(0, idx1).trim());
                        Assert.isTrue(StandardCharsets.UTF_8.equals(charset) || StandardCharsets.ISO_8859_1.equals(charset), "Charset must be UTF-8 or ISO-8859-1");
                        filename = decodeRfc5987Filename(value.substring(idx2 + 1), charset);
                    } else {
                        filename = decodeRfc5987Filename(value, StandardCharsets.US_ASCII);
                    }
                } else if (attribute.equals("filename") && filename == null) {
                    if (value.startsWith("=?")) {
                        Matcher matcher = BASE64_ENCODED_PATTERN.matcher(value);
                        if (matcher.find()) {
                            Base64.Decoder decoder = Base64.getDecoder();
                            StringBuilder builder = new StringBuilder();
                            do {
                                charset = Charset.forName(matcher.group(1));
                                byte[] decoded = decoder.decode(matcher.group(2));
                                builder.append(new String(decoded, charset));
                            } while (matcher.find());
                            filename = builder.toString();
                        } else {
                            Matcher matcher2 = QUOTED_PRINTABLE_ENCODED_PATTERN.matcher(value);
                            if (matcher2.find()) {
                                StringBuilder builder2 = new StringBuilder();
                                do {
                                    charset = Charset.forName(matcher2.group(1));
                                    String decoded2 = decodeQuotedPrintableFilename(matcher2.group(2), charset);
                                    builder2.append(decoded2);
                                } while (matcher2.find());
                                filename = builder2.toString();
                            } else {
                                filename = value;
                            }
                        }
                    } else if (value.indexOf(92) != -1) {
                        filename = decodeQuotedPairs(value);
                    } else {
                        filename = value;
                    }
                } else if (attribute.equals(InputTag.SIZE_ATTRIBUTE)) {
                    size = Long.valueOf(Long.parseLong(value));
                } else if (attribute.equals("creation-date")) {
                    try {
                        creationDate = ZonedDateTime.parse(value, DateTimeFormatter.RFC_1123_DATE_TIME);
                    } catch (DateTimeParseException e) {
                    }
                } else if (attribute.equals("modification-date")) {
                    try {
                        modificationDate = ZonedDateTime.parse(value, DateTimeFormatter.RFC_1123_DATE_TIME);
                    } catch (DateTimeParseException e2) {
                    }
                } else if (attribute.equals("read-date")) {
                    try {
                        readDate = ZonedDateTime.parse(value, DateTimeFormatter.RFC_1123_DATE_TIME);
                    } catch (DateTimeParseException e3) {
                    }
                }
            } else {
                throw new IllegalArgumentException("Invalid content disposition format");
            }
        }
        return new ContentDisposition(type, name, filename, charset, size, creationDate, modificationDate, readDate);
    }

    /* JADX WARN: Code restructure failed: missing block: B:10:0x003c, code lost:            if (r5 >= 0) goto L12;     */
    /* JADX WARN: Code restructure failed: missing block: B:11:0x003f, code lost:            r8 = r5 + 1;        r9 = false;        r10 = false;     */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x0050, code lost:            if (r8 >= r4.length()) goto L46;     */
    /* JADX WARN: Code restructure failed: missing block: B:14:0x0053, code lost:            r0 = r4.charAt(r8);     */
    /* JADX WARN: Code restructure failed: missing block: B:15:0x005f, code lost:            if (r0 != ';') goto L20;     */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x0064, code lost:            if (r9 != false) goto L29;     */
    /* JADX WARN: Code restructure failed: missing block: B:19:0x0084, code lost:            if (r10 != false) goto L34;     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x008b, code lost:            if (r0 != '\\') goto L34;     */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x008e, code lost:            r0 = true;     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x0093, code lost:            r10 = r0;        r8 = r8 + 1;     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x0092, code lost:            r0 = false;     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x009b, code lost:            r0 = r4.substring(r5 + 1, r8).trim();     */
    /* JADX WARN: Code restructure failed: missing block: B:29:0x00ae, code lost:            if (r0.isEmpty() != false) goto L39;     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x00b1, code lost:            r0.add(r0);     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x00ba, code lost:            r5 = r8;     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x00c2, code lost:            if (r5 < r4.length()) goto L44;     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x006c, code lost:            if (r10 != false) goto L29;     */
    /* JADX WARN: Code restructure failed: missing block: B:38:0x0073, code lost:            if (r0 != '\"') goto L29;     */
    /* JADX WARN: Code restructure failed: missing block: B:40:0x0078, code lost:            if (r9 != false) goto L27;     */
    /* JADX WARN: Code restructure failed: missing block: B:41:0x007b, code lost:            r0 = true;     */
    /* JADX WARN: Code restructure failed: missing block: B:42:0x0080, code lost:            r9 = r0;     */
    /* JADX WARN: Code restructure failed: missing block: B:43:0x007f, code lost:            r0 = false;     */
    /* JADX WARN: Code restructure failed: missing block: B:46:0x00c6, code lost:            return r0;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static java.util.List<java.lang.String> tokenize(java.lang.String r4) {
        /*
            r0 = r4
            r1 = 59
            int r0 = r0.indexOf(r1)
            r5 = r0
            r0 = r5
            if (r0 < 0) goto L14
            r0 = r4
            r1 = 0
            r2 = r5
            java.lang.String r0 = r0.substring(r1, r2)
            goto L15
        L14:
            r0 = r4
        L15:
            java.lang.String r0 = r0.trim()
            r6 = r0
            r0 = r6
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L2b
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            r1 = r0
            java.lang.String r2 = "Content-Disposition header must not be empty"
            r1.<init>(r2)
            throw r0
        L2b:
            java.util.ArrayList r0 = new java.util.ArrayList
            r1 = r0
            r1.<init>()
            r7 = r0
            r0 = r7
            r1 = r6
            boolean r0 = r0.add(r1)
            r0 = r5
            if (r0 < 0) goto Lc5
        L3f:
            r0 = r5
            r1 = 1
            int r0 = r0 + r1
            r8 = r0
            r0 = 0
            r9 = r0
            r0 = 0
            r10 = r0
        L4a:
            r0 = r8
            r1 = r4
            int r1 = r1.length()
            if (r0 >= r1) goto L9b
            r0 = r4
            r1 = r8
            char r0 = r0.charAt(r1)
            r11 = r0
            r0 = r11
            r1 = 59
            if (r0 != r1) goto L6a
            r0 = r9
            if (r0 != 0) goto L82
            goto L9b
        L6a:
            r0 = r10
            if (r0 != 0) goto L82
            r0 = r11
            r1 = 34
            if (r0 != r1) goto L82
            r0 = r9
            if (r0 != 0) goto L7f
            r0 = 1
            goto L80
        L7f:
            r0 = 0
        L80:
            r9 = r0
        L82:
            r0 = r10
            if (r0 != 0) goto L92
            r0 = r11
            r1 = 92
            if (r0 != r1) goto L92
            r0 = 1
            goto L93
        L92:
            r0 = 0
        L93:
            r10 = r0
            int r8 = r8 + 1
            goto L4a
        L9b:
            r0 = r4
            r1 = r5
            r2 = 1
            int r1 = r1 + r2
            r2 = r8
            java.lang.String r0 = r0.substring(r1, r2)
            java.lang.String r0 = r0.trim()
            r11 = r0
            r0 = r11
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto Lba
            r0 = r7
            r1 = r11
            boolean r0 = r0.add(r1)
        Lba:
            r0 = r8
            r5 = r0
            r0 = r5
            r1 = r4
            int r1 = r1.length()
            if (r0 < r1) goto L3f
        Lc5:
            r0 = r7
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.springframework.http.ContentDisposition.tokenize(java.lang.String):java.util.List");
    }

    private static String decodeRfc5987Filename(String filename, Charset charset) {
        Assert.notNull(filename, "'filename' must not be null");
        Assert.notNull(charset, "'charset' must not be null");
        byte[] value = filename.getBytes(charset);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int index = 0;
        while (index < value.length) {
            byte b = value[index];
            if (isRFC5987AttrChar(b)) {
                baos.write((char) b);
                index++;
            } else if (b == 37 && index < value.length - 2) {
                char[] array = {(char) value[index + 1], (char) value[index + 2]};
                try {
                    baos.write(Integer.parseInt(String.valueOf(array), 16));
                    index += 3;
                } catch (NumberFormatException ex) {
                    throw new IllegalArgumentException(INVALID_HEADER_FIELD_PARAMETER_FORMAT, ex);
                }
            } else {
                throw new IllegalArgumentException(INVALID_HEADER_FIELD_PARAMETER_FORMAT);
            }
        }
        return StreamUtils.copyToString(baos, charset);
    }

    private static boolean isRFC5987AttrChar(byte c) {
        return (c >= 48 && c <= 57) || (c >= 97 && c <= 122) || ((c >= 65 && c <= 90) || c == 33 || c == 35 || c == 36 || c == 38 || c == 43 || c == 45 || c == 46 || c == 94 || c == 95 || c == 96 || c == 124 || c == 126);
    }

    private static String decodeQuotedPrintableFilename(String filename, Charset charset) {
        Assert.notNull(filename, "'filename' must not be null");
        Assert.notNull(charset, "'charset' must not be null");
        byte[] value = filename.getBytes(StandardCharsets.US_ASCII);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int index = 0;
        while (index < value.length) {
            byte b = value[index];
            if (b == 95) {
                baos.write(32);
                index++;
            } else if (b == 61 && index < value.length - 2) {
                int i1 = Character.digit((char) value[index + 1], 16);
                int i2 = Character.digit((char) value[index + 2], 16);
                if (i1 == -1 || i2 == -1) {
                    throw new IllegalArgumentException("Not a valid hex sequence: " + filename.substring(index));
                }
                baos.write((i1 << 4) | i2);
                index += 3;
            } else {
                baos.write(b);
                index++;
            }
        }
        return StreamUtils.copyToString(baos, charset);
    }

    private static String encodeQuotedPrintableFilename(String filename, Charset charset) {
        Assert.notNull(filename, "'filename' must not be null");
        Assert.notNull(charset, "'charset' must not be null");
        byte[] source = filename.getBytes(charset);
        StringBuilder sb = new StringBuilder(source.length << 1);
        sb.append("=?");
        sb.append(charset.name());
        sb.append("?Q?");
        for (byte b : source) {
            if (b == 32) {
                sb.append('_');
            } else if (isPrintable(b)) {
                sb.append((char) b);
            } else {
                sb.append('=');
                char ch1 = hexDigit(b >> 4);
                char ch2 = hexDigit(b);
                sb.append(ch1);
                sb.append(ch2);
            }
        }
        sb.append("?=");
        return sb.toString();
    }

    private static boolean isPrintable(byte c) {
        int b = c;
        if (b < 0) {
            b = 256 + b;
        }
        return PRINTABLE.get(b);
    }

    private static String encodeQuotedPairs(String filename) {
        if (filename.indexOf(34) == -1 && filename.indexOf(92) == -1) {
            return filename;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < filename.length(); i++) {
            char c = filename.charAt(i);
            if (c == '\"' || c == '\\') {
                sb.append('\\');
            }
            sb.append(c);
        }
        return sb.toString();
    }

    private static String decodeQuotedPairs(String filename) {
        StringBuilder sb = new StringBuilder();
        int length = filename.length();
        int i = 0;
        while (i < length) {
            char c = filename.charAt(i);
            if (filename.charAt(i) == '\\' && i + 1 < length) {
                i++;
                char next = filename.charAt(i);
                if (next != '\"' && next != '\\') {
                    sb.append(c);
                }
                sb.append(next);
            } else {
                sb.append(c);
            }
            i++;
        }
        return sb.toString();
    }

    private static String encodeRfc5987Filename(String input, Charset charset) {
        Assert.notNull(input, "'input' must not be null");
        Assert.notNull(charset, "'charset' must not be null");
        Assert.isTrue(!StandardCharsets.US_ASCII.equals(charset), "ASCII does not require encoding");
        Assert.isTrue(StandardCharsets.UTF_8.equals(charset) || StandardCharsets.ISO_8859_1.equals(charset), "Only UTF-8 and ISO-8859-1 are supported");
        byte[] source = input.getBytes(charset);
        StringBuilder sb = new StringBuilder(source.length << 1);
        sb.append(charset.name());
        sb.append("''");
        for (byte b : source) {
            if (isRFC5987AttrChar(b)) {
                sb.append((char) b);
            } else {
                sb.append('%');
                char hex1 = hexDigit(b >> 4);
                char hex2 = hexDigit(b);
                sb.append(hex1);
                sb.append(hex2);
            }
        }
        return sb.toString();
    }

    private static char hexDigit(int b) {
        return Character.toUpperCase(Character.forDigit(b & 15, 16));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/ContentDisposition$BuilderImpl.class */
    public static class BuilderImpl implements Builder {
        private final String type;

        @Nullable
        private String name;

        @Nullable
        private String filename;

        @Nullable
        private Charset charset;

        @Nullable
        private Long size;

        @Nullable
        private ZonedDateTime creationDate;

        @Nullable
        private ZonedDateTime modificationDate;

        @Nullable
        private ZonedDateTime readDate;

        public BuilderImpl(String type) {
            Assert.hasText(type, "'type' must not be not empty");
            this.type = type;
        }

        @Override // org.springframework.http.ContentDisposition.Builder
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        @Override // org.springframework.http.ContentDisposition.Builder
        public Builder filename(String filename) {
            this.filename = filename;
            return this;
        }

        @Override // org.springframework.http.ContentDisposition.Builder
        public Builder filename(String filename, Charset charset) {
            this.filename = filename;
            this.charset = charset;
            return this;
        }

        @Override // org.springframework.http.ContentDisposition.Builder
        public Builder size(Long size) {
            this.size = size;
            return this;
        }

        @Override // org.springframework.http.ContentDisposition.Builder
        public Builder creationDate(ZonedDateTime creationDate) {
            this.creationDate = creationDate;
            return this;
        }

        @Override // org.springframework.http.ContentDisposition.Builder
        public Builder modificationDate(ZonedDateTime modificationDate) {
            this.modificationDate = modificationDate;
            return this;
        }

        @Override // org.springframework.http.ContentDisposition.Builder
        public Builder readDate(ZonedDateTime readDate) {
            this.readDate = readDate;
            return this;
        }

        @Override // org.springframework.http.ContentDisposition.Builder
        public ContentDisposition build() {
            return new ContentDisposition(this.type, this.name, this.filename, this.charset, this.size, this.creationDate, this.modificationDate, this.readDate);
        }
    }
}
