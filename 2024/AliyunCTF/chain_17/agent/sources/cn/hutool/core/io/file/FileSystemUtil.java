package cn.hutool.core.io.file;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.CharsetUtil;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/io/file/FileSystemUtil.class */
public class FileSystemUtil {
    public static FileSystem create(String path) {
        try {
            return FileSystems.newFileSystem(Paths.get(path, new String[0]).toUri(), MapUtil.of("create", "true"));
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public static FileSystem createZip(String path) {
        return createZip(path, null);
    }

    public static FileSystem createZip(String path, Charset charset) {
        if (null == charset) {
            charset = CharsetUtil.CHARSET_UTF_8;
        }
        HashMap<String, String> env = new HashMap<>();
        env.put("create", "true");
        env.put("encoding", charset.name());
        try {
            return FileSystems.newFileSystem(URI.create("jar:" + Paths.get(path, new String[0]).toUri()), env);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public static Path getRoot(FileSystem fileSystem) {
        return fileSystem.getPath("/", new String[0]);
    }
}
