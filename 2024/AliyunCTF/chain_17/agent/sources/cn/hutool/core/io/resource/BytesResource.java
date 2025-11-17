package cn.hutool.core.io.resource;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.StrUtil;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.nio.charset.Charset;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/io/resource/BytesResource.class */
public class BytesResource implements Resource, Serializable {
    private static final long serialVersionUID = 1;
    private final byte[] bytes;
    private final String name;

    public BytesResource(byte[] bytes) {
        this(bytes, null);
    }

    public BytesResource(byte[] bytes, String name) {
        this.bytes = bytes;
        this.name = name;
    }

    @Override // cn.hutool.core.io.resource.Resource
    public String getName() {
        return this.name;
    }

    @Override // cn.hutool.core.io.resource.Resource
    public URL getUrl() {
        return null;
    }

    @Override // cn.hutool.core.io.resource.Resource
    public InputStream getStream() {
        return new ByteArrayInputStream(this.bytes);
    }

    @Override // cn.hutool.core.io.resource.Resource
    public String readStr(Charset charset) throws IORuntimeException {
        return StrUtil.str(this.bytes, charset);
    }

    @Override // cn.hutool.core.io.resource.Resource
    public byte[] readBytes() throws IORuntimeException {
        return this.bytes;
    }
}
