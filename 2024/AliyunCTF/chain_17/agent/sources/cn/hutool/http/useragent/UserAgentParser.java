package cn.hutool.http.useragent;

import cn.hutool.core.util.StrUtil;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/http/useragent/UserAgentParser.class */
public class UserAgentParser {
    public static UserAgent parse(String userAgentString) {
        if (StrUtil.isBlank(userAgentString)) {
            return null;
        }
        UserAgent userAgent = new UserAgent();
        Browser browser = parseBrowser(userAgentString);
        userAgent.setBrowser(browser);
        userAgent.setVersion(browser.getVersion(userAgentString));
        Engine engine = parseEngine(userAgentString);
        userAgent.setEngine(engine);
        userAgent.setEngineVersion(engine.getVersion(userAgentString));
        OS os = parseOS(userAgentString);
        userAgent.setOs(os);
        userAgent.setOsVersion(os.getVersion(userAgentString));
        Platform platform = parsePlatform(userAgentString);
        userAgent.setPlatform(platform);
        userAgent.setMobile(platform.isMobile() || browser.isMobile());
        return userAgent;
    }

    private static Browser parseBrowser(String userAgentString) {
        for (Browser browser : Browser.browers) {
            if (browser.isMatch(userAgentString)) {
                return browser;
            }
        }
        return Browser.Unknown;
    }

    private static Engine parseEngine(String userAgentString) {
        for (Engine engine : Engine.engines) {
            if (engine.isMatch(userAgentString)) {
                return engine;
            }
        }
        return Engine.Unknown;
    }

    private static OS parseOS(String userAgentString) {
        for (OS os : OS.oses) {
            if (os.isMatch(userAgentString)) {
                return os;
            }
        }
        return OS.Unknown;
    }

    private static Platform parsePlatform(String userAgentString) {
        for (Platform platform : Platform.platforms) {
            if (platform.isMatch(userAgentString)) {
                return platform;
            }
        }
        return Platform.Unknown;
    }
}
