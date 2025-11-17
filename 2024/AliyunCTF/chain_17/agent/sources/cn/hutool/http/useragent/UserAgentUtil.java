package cn.hutool.http.useragent;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/http/useragent/UserAgentUtil.class */
public class UserAgentUtil {
    public static UserAgent parse(String userAgentString) {
        return UserAgentParser.parse(userAgentString);
    }
}
