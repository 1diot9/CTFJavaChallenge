package cn.hutool.system;

import cn.hutool.core.util.StrUtil;
import java.io.File;
import java.io.Serializable;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/system/UserInfo.class */
public class UserInfo implements Serializable {
    private static final long serialVersionUID = 1;
    private final String USER_NAME = fixPath(SystemUtil.get("user.name", false));
    private final String USER_HOME = fixPath(SystemUtil.get("user.home", false));
    private final String USER_DIR = fixPath(SystemUtil.get("user.dir", false));
    private final String JAVA_IO_TMPDIR = fixPath(SystemUtil.get("java.io.tmpdir", false));
    private final String USER_LANGUAGE = SystemUtil.get("user.language", false);
    private final String USER_COUNTRY;

    public UserInfo() {
        String userCountry = SystemUtil.get("user.country", false);
        this.USER_COUNTRY = null == userCountry ? SystemUtil.get("user.country", false) : userCountry;
    }

    public final String getName() {
        return this.USER_NAME;
    }

    public final String getHomeDir() {
        return this.USER_HOME;
    }

    public final String getCurrentDir() {
        return this.USER_DIR;
    }

    public final String getTempDir() {
        return this.JAVA_IO_TMPDIR;
    }

    public final String getLanguage() {
        return this.USER_LANGUAGE;
    }

    public final String getCountry() {
        return this.USER_COUNTRY;
    }

    public final String toString() {
        StringBuilder builder = new StringBuilder();
        SystemUtil.append(builder, "User Name:        ", getName());
        SystemUtil.append(builder, "User Home Dir:    ", getHomeDir());
        SystemUtil.append(builder, "User Current Dir: ", getCurrentDir());
        SystemUtil.append(builder, "User Temp Dir:    ", getTempDir());
        SystemUtil.append(builder, "User Language:    ", getLanguage());
        SystemUtil.append(builder, "User Country:     ", getCountry());
        return builder.toString();
    }

    private static String fixPath(String path) {
        return StrUtil.addSuffixIfNot(path, File.separator);
    }
}
