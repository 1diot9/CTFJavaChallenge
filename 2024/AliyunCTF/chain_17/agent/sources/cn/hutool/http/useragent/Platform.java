package cn.hutool.http.useragent;

import cn.hutool.core.collection.CollUtil;
import java.util.ArrayList;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/http/useragent/Platform.class */
public class Platform extends UserAgentInfo {
    private static final long serialVersionUID = 1;
    public static final Platform Unknown = new Platform(UserAgentInfo.NameUnknown, null);
    public static final Platform IPHONE = new Platform("iPhone", "iphone");
    public static final Platform IPOD = new Platform("iPod", "ipod");
    public static final Platform IPAD = new Platform("iPad", "ipad");
    public static final Platform ANDROID = new Platform("Android", "android");
    public static final Platform GOOGLE_TV = new Platform("GoogleTV", "googletv");
    public static final Platform WINDOWS_PHONE = new Platform("Windows Phone", "windows (ce|phone|mobile)( os)?");
    public static final List<Platform> mobilePlatforms = CollUtil.newArrayList(WINDOWS_PHONE, IPAD, IPOD, IPHONE, new Platform("Android", "XiaoMi|MI\\s+"), ANDROID, GOOGLE_TV, new Platform("htcFlyer", "htc_flyer"), new Platform("Symbian", "symbian(os)?"), new Platform("Blackberry", "blackberry"));
    public static final List<Platform> desktopPlatforms = CollUtil.newArrayList(new Platform("Windows", "windows"), new Platform("Mac", "(macintosh|darwin)"), new Platform("Linux", "linux"), new Platform("Wii", "wii"), new Platform("Playstation", "playstation"), new Platform("Java", "java"));
    public static final List<Platform> platforms = new ArrayList(13);

    static {
        platforms.addAll(mobilePlatforms);
        platforms.addAll(desktopPlatforms);
    }

    public Platform(String name, String regex) {
        super(name, regex);
    }

    public boolean isMobile() {
        return mobilePlatforms.contains(this);
    }

    public boolean isIPhoneOrIPod() {
        return equals(IPHONE) || equals(IPOD);
    }

    public boolean isIPad() {
        return equals(IPAD);
    }

    public boolean isIos() {
        return isIPhoneOrIPod() || isIPad();
    }

    public boolean isAndroid() {
        return equals(ANDROID) || equals(GOOGLE_TV);
    }
}
