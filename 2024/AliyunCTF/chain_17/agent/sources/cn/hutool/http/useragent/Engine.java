package cn.hutool.http.useragent;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReUtil;
import java.util.List;
import java.util.regex.Pattern;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/http/useragent/Engine.class */
public class Engine extends UserAgentInfo {
    private static final long serialVersionUID = 1;
    public static final Engine Unknown = new Engine(UserAgentInfo.NameUnknown, null);
    public static final List<Engine> engines = CollUtil.newArrayList(new Engine("Trident", "trident"), new Engine("Webkit", "webkit"), new Engine("Chrome", "chrome"), new Engine("Opera", "opera"), new Engine("Presto", "presto"), new Engine("Gecko", "gecko"), new Engine("KHTML", "khtml"), new Engine("Konqueror", "konqueror"), new Engine("MIDP", "MIDP"));
    private final Pattern versionPattern;

    public Engine(String name, String regex) {
        super(name, regex);
        this.versionPattern = Pattern.compile(name + "[/\\- ]([\\d\\w.\\-]+)", 2);
    }

    public String getVersion(String userAgentString) {
        if (isUnknown()) {
            return null;
        }
        return ReUtil.getGroup1(this.versionPattern, userAgentString);
    }
}
