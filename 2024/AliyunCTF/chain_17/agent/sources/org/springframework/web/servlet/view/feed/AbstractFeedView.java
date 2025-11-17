package org.springframework.web.servlet.view.feed;

import cn.hutool.core.util.CharsetUtil;
import com.rometools.rome.feed.WireFeed;
import com.rometools.rome.io.WireFeedOutput;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.OutputStreamWriter;
import java.util.Map;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.view.AbstractView;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/view/feed/AbstractFeedView.class */
public abstract class AbstractFeedView<T extends WireFeed> extends AbstractView {
    protected abstract T newFeed();

    protected abstract void buildFeedEntries(Map<String, Object> model, T feed, HttpServletRequest request, HttpServletResponse response) throws Exception;

    @Override // org.springframework.web.servlet.view.AbstractView
    protected final void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        T wireFeed = newFeed();
        buildFeedMetadata(model, wireFeed, request);
        buildFeedEntries(model, wireFeed, request, response);
        setResponseContentType(request, response);
        if (!StringUtils.hasText(wireFeed.getEncoding())) {
            wireFeed.setEncoding(CharsetUtil.UTF_8);
        }
        WireFeedOutput feedOutput = new WireFeedOutput();
        ServletOutputStream out = response.getOutputStream();
        feedOutput.output(wireFeed, new OutputStreamWriter(out, wireFeed.getEncoding()));
        out.flush();
    }

    protected void buildFeedMetadata(Map<String, Object> model, T feed, HttpServletRequest request) {
    }
}
