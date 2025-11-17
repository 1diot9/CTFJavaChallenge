package cn.hutool.core.io.resource;

import cn.hutool.core.util.CharsetUtil;
import java.nio.charset.Charset;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/io/resource/StringResource.class */
public class StringResource extends CharSequenceResource {
    private static final long serialVersionUID = 1;

    public StringResource(String data) {
        super(data, null);
    }

    public StringResource(String data, String name) {
        super(data, name, CharsetUtil.CHARSET_UTF_8);
    }

    public StringResource(String data, String name, Charset charset) {
        super(data, name, charset);
    }
}
