package cn.hutool.http.body;

import cn.hutool.core.io.IoUtil;
import java.io.OutputStream;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/http/body/BytesBody.class */
public class BytesBody implements RequestBody {
    private final byte[] content;

    public static BytesBody create(byte[] content) {
        return new BytesBody(content);
    }

    public BytesBody(byte[] content) {
        this.content = content;
    }

    @Override // cn.hutool.http.body.RequestBody
    public void write(OutputStream out) {
        IoUtil.write(out, false, this.content);
    }
}
