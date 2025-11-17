package org.springframework.web.servlet.resource;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.resource.EncodedResourceResolver;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/resource/CssLinkResourceTransformer.class */
public class CssLinkResourceTransformer extends ResourceTransformerSupport {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final Log logger = LogFactory.getLog((Class<?>) CssLinkResourceTransformer.class);
    private final List<LinkParser> linkParsers = new ArrayList(2);

    @FunctionalInterface
    /* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/resource/CssLinkResourceTransformer$LinkParser.class */
    protected interface LinkParser {
        void parse(String content, SortedSet<ContentChunkInfo> result);
    }

    public CssLinkResourceTransformer() {
        this.linkParsers.add(new ImportStatementLinkParser());
        this.linkParsers.add(new UrlFunctionLinkParser());
    }

    @Override // org.springframework.web.servlet.resource.ResourceTransformer
    public Resource transform(HttpServletRequest request, Resource resource, ResourceTransformerChain transformerChain) throws IOException {
        Resource resource2 = transformerChain.transform(request, resource);
        String filename = resource2.getFilename();
        if (!"css".equals(StringUtils.getFilenameExtension(filename)) || (resource2 instanceof EncodedResourceResolver.EncodedResource)) {
            return resource2;
        }
        byte[] bytes = FileCopyUtils.copyToByteArray(resource2.getInputStream());
        String content = new String(bytes, DEFAULT_CHARSET);
        SortedSet<ContentChunkInfo> links = new TreeSet<>();
        for (LinkParser parser : this.linkParsers) {
            parser.parse(content, links);
        }
        if (links.isEmpty()) {
            return resource2;
        }
        int index = 0;
        StringWriter writer = new StringWriter();
        for (ContentChunkInfo linkContentChunkInfo : links) {
            writer.write(content.substring(index, linkContentChunkInfo.getStart()));
            String link = content.substring(linkContentChunkInfo.getStart(), linkContentChunkInfo.getEnd());
            String newLink = null;
            if (!hasScheme(link)) {
                String absolutePath = toAbsolutePath(link, request);
                newLink = resolveUrlPath(absolutePath, request, resource2, transformerChain);
            }
            writer.write(newLink != null ? newLink : link);
            index = linkContentChunkInfo.getEnd();
        }
        writer.write(content.substring(index));
        return new TransformedResource(resource2, writer.toString().getBytes(DEFAULT_CHARSET));
    }

    private boolean hasScheme(String link) {
        int schemeIndex = link.indexOf(58);
        return (schemeIndex > 0 && !link.substring(0, schemeIndex).contains("/")) || link.indexOf("//") == 0;
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/resource/CssLinkResourceTransformer$AbstractLinkParser.class */
    protected static abstract class AbstractLinkParser implements LinkParser {
        protected abstract String getKeyword();

        protected abstract int extractLink(int index, String content, SortedSet<ContentChunkInfo> linksToAdd);

        protected AbstractLinkParser() {
        }

        @Override // org.springframework.web.servlet.resource.CssLinkResourceTransformer.LinkParser
        public void parse(String content, SortedSet<ContentChunkInfo> result) {
            int position = 0;
            while (true) {
                int position2 = content.indexOf(getKeyword(), position);
                if (position2 == -1) {
                    return;
                }
                int position3 = position2 + getKeyword().length();
                while (Character.isWhitespace(content.charAt(position3))) {
                    position3++;
                }
                if (content.charAt(position3) == '\'') {
                    position = extractLink(position3, "'", content, result);
                } else if (content.charAt(position3) == '\"') {
                    position = extractLink(position3, "\"", content, result);
                } else {
                    position = extractLink(position3, content, result);
                }
            }
        }

        protected int extractLink(int index, String endKey, String content, SortedSet<ContentChunkInfo> linksToAdd) {
            int start = index + 1;
            int end = content.indexOf(endKey, start);
            linksToAdd.add(new ContentChunkInfo(start, end));
            return end + endKey.length();
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/resource/CssLinkResourceTransformer$ImportStatementLinkParser.class */
    private static class ImportStatementLinkParser extends AbstractLinkParser {
        private ImportStatementLinkParser() {
        }

        @Override // org.springframework.web.servlet.resource.CssLinkResourceTransformer.AbstractLinkParser
        protected String getKeyword() {
            return "@import";
        }

        @Override // org.springframework.web.servlet.resource.CssLinkResourceTransformer.AbstractLinkParser
        protected int extractLink(int index, String content, SortedSet<ContentChunkInfo> linksToAdd) {
            if (!content.startsWith("url(", index) && CssLinkResourceTransformer.logger.isTraceEnabled()) {
                CssLinkResourceTransformer.logger.trace("Unexpected syntax for @import link at index " + index);
            }
            return index;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/resource/CssLinkResourceTransformer$UrlFunctionLinkParser.class */
    private static class UrlFunctionLinkParser extends AbstractLinkParser {
        private UrlFunctionLinkParser() {
        }

        @Override // org.springframework.web.servlet.resource.CssLinkResourceTransformer.AbstractLinkParser
        protected String getKeyword() {
            return "url(";
        }

        @Override // org.springframework.web.servlet.resource.CssLinkResourceTransformer.AbstractLinkParser
        protected int extractLink(int index, String content, SortedSet<ContentChunkInfo> linksToAdd) {
            return extractLink(index - 1, ")", content, linksToAdd);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/resource/CssLinkResourceTransformer$ContentChunkInfo.class */
    public static class ContentChunkInfo implements Comparable<ContentChunkInfo> {
        private final int start;
        private final int end;

        ContentChunkInfo(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public int getStart() {
            return this.start;
        }

        public int getEnd() {
            return this.end;
        }

        @Override // java.lang.Comparable
        public int compareTo(ContentChunkInfo other) {
            return Integer.compare(this.start, other.start);
        }

        public boolean equals(@Nullable Object other) {
            if (this != other) {
                if (other instanceof ContentChunkInfo) {
                    ContentChunkInfo that = (ContentChunkInfo) other;
                    if (this.start != that.start || this.end != that.end) {
                    }
                }
                return false;
            }
            return true;
        }

        public int hashCode() {
            return (this.start * 31) + this.end;
        }
    }
}
