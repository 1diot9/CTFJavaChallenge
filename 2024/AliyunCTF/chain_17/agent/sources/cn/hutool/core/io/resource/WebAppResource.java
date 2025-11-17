package cn.hutool.core.io.resource;

import cn.hutool.core.io.FileUtil;
import java.io.File;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/io/resource/WebAppResource.class */
public class WebAppResource extends FileResource {
    private static final long serialVersionUID = 1;

    public WebAppResource(String path) {
        super(new File(FileUtil.getWebRoot(), path));
    }
}
