package org.springframework.web.servlet.view.feed;

import com.rometools.rome.feed.rss.Channel;
import com.rometools.rome.feed.rss.Item;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/view/feed/AbstractRssFeedView.class */
public abstract class AbstractRssFeedView extends AbstractFeedView<Channel> {
    protected abstract List<Item> buildFeedItems(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception;

    @Override // org.springframework.web.servlet.view.feed.AbstractFeedView
    protected /* bridge */ /* synthetic */ void buildFeedEntries(Map model, Channel channel, HttpServletRequest request, HttpServletResponse response) throws Exception {
        buildFeedEntries2((Map<String, Object>) model, channel, request, response);
    }

    public AbstractRssFeedView() {
        setContentType(MediaType.APPLICATION_RSS_XML_VALUE);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.servlet.view.feed.AbstractFeedView
    public Channel newFeed() {
        return new Channel("rss_2.0");
    }

    /* renamed from: buildFeedEntries, reason: avoid collision after fix types in other method */
    protected final void buildFeedEntries2(Map<String, Object> model, Channel channel, HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<Item> items = buildFeedItems(model, request, response);
        channel.setItems(items);
    }
}
