package cn.hutool.core.io.resource;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.Charset;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/io/resource/CharSequenceResource.class */
public class CharSequenceResource implements Resource, Serializable {
    private static final long serialVersionUID = 1;
    private final CharSequence data;
    private final CharSequence name;
    private final Charset charset;

    public CharSequenceResource(CharSequence data) {
        this(data, null);
    }

    public CharSequenceResource(CharSequence data, String name) {
        this(data, name, CharsetUtil.CHARSET_UTF_8);
    }

    public CharSequenceResource(CharSequence data, CharSequence name, Charset charset) {
        this.data = data;
        this.name = name;
        this.charset = charset;
    }

    @Override // cn.hutool.core.io.resource.Resource
    public String getName() {
        return StrUtil.str(this.name);
    }

    @Override // cn.hutool.core.io.resource.Resource
    public URL getUrl() {
        return null;
    }

    @Override // cn.hutool.core.io.resource.Resource
    public InputStream getStream() {
        return new ByteArrayInputStream(readBytes());
    }

    @Override // cn.hutool.core.io.resource.Resource
    public BufferedReader getReader(Charset charset) {
        return IoUtil.getReader(new StringReader(this.data.toString()));
    }

    @Override // cn.hutool.core.io.resource.Resource
    public String readStr(Charset charset) throws IORuntimeException {
        return this.data.toString();
    }

    @Override // cn.hutool.core.io.resource.Resource
    public byte[] readBytes() throws IORuntimeException {
        return this.data.toString().getBytes(this.charset);
    }
}
