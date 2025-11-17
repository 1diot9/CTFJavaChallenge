package org.springframework.http.converter.feed;

import com.rometools.rome.feed.rss.Channel;
import org.springframework.http.MediaType;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/converter/feed/RssChannelHttpMessageConverter.class */
public class RssChannelHttpMessageConverter extends AbstractWireFeedHttpMessageConverter<Channel> {
    public RssChannelHttpMessageConverter() {
        super(MediaType.APPLICATION_RSS_XML);
    }

    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    protected boolean supports(Class<?> clazz) {
        return Channel.class.isAssignableFrom(clazz);
    }
}
