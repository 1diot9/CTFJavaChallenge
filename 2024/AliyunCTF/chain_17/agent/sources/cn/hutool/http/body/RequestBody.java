package cn.hutool.http.body;

import cn.hutool.core.io.IoUtil;
import java.io.Closeable;
import java.io.OutputStream;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/http/body/RequestBody.class */
public interface RequestBody {
    void write(OutputStream outputStream);

    default void writeClose(OutputStream out) {
        try {
            write(out);
        } finally {
            IoUtil.close((Closeable) out);
        }
    }
}
