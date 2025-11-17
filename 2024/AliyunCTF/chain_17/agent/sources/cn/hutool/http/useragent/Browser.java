package cn.hutool.http.useragent;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReUtil;
import java.util.List;
import java.util.regex.Pattern;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/http/useragent/Browser.class */
public class Browser extends UserAgentInfo {
    private static final long serialVersionUID = 1;
    public static final Browser Unknown = new Browser(UserAgentInfo.NameUnknown, null, null);
    public static final String Other_Version = "[\\/ ]([\\d\\w\\.\\-]+)";
    public static final List<Browser> browers = CollUtil.newArrayList(new Browser("wxwork", "wxwork", "wxwork\\/([\\d\\w\\.\\-]+)"), new Browser("MicroMessenger", "MicroMessenger", Other_Version), new Browser("miniProgram", "miniProgram", Other_Version), new Browser("QQBrowser", "MQQBrowser", "MQQBrowser\\/([\\d\\w\\.\\-]+)"), new Browser("DingTalk-win", "dingtalk-win", "DingTalk\\(([\\d\\w\\.\\-]+)\\)"), new Browser("DingTalk", "DingTalk", "AliApp\\(DingTalk\\/([\\d\\w\\.\\-]+)\\)"), new Browser("Alipay", "AlipayClient", "AliApp\\(AP\\/([\\d\\w\\.\\-]+)\\)"), new Browser("Taobao", "taobao", "AliApp\\(TB\\/([\\d\\w\\.\\-]+)\\)"), new Browser("UCBrowser", "UC?Browser", "UC?Browser\\/([\\d\\w\\.\\-]+)"), new Browser("MiuiBrowser", "MiuiBrowser|mibrowser", "MiuiBrowser\\/([\\d\\w\\.\\-]+)"), new Browser("Quark", "Quark", Other_Version), new Browser("Lenovo", "SLBrowser", "SLBrowser/([\\d\\w\\.\\-]+)"), new Browser("MSEdge", "Edge|Edg", "(?:edge|Edg|EdgA)\\/([\\d\\w\\.\\-]+)"), new Browser("Chrome", "chrome", Other_Version), new Browser("Firefox", "firefox", Other_Version), new Browser("IEMobile", "iemobile", Other_Version), new Browser("Android Browser", "android", "version\\/([\\d\\w\\.\\-]+)"), new Browser("Safari", "safari", "version\\/([\\d\\w\\.\\-]+)"), new Browser("Opera", "opera", Other_Version), new Browser("Konqueror", "konqueror", Other_Version), new Browser("PS3", "playstation 3", "([\\d\\w\\.\\-]+)\\)\\s*$"), new Browser("PSP", "playstation portable", "([\\d\\w\\.\\-]+)\\)?\\s*$"), new Browser("Lotus", "lotus.notes", "Lotus-Notes\\/([\\w.]+)"), new Browser("Thunderbird", "thunderbird", Other_Version), new Browser("Netscape", "netscape", Other_Version), new Browser("Seamonkey", "seamonkey", Other_Version), new Browser("Outlook", "microsoft.outlook", Other_Version), new Browser("Evolution", "evolution", Other_Version), new Browser("MSIE", "msie", "msie ([\\d\\w\\.\\-]+)"), new Browser("MSIE11", "rv:11", "rv:([\\d\\w\\.\\-]+)"), new Browser("Gabble", "Gabble", Other_Version), new Browser("Yammer Desktop", "AdobeAir", "([\\d\\w\\.\\-]+)\\/Yammer"), new Browser("Yammer Mobile", "Yammer[\\s]+([\\d\\w\\.\\-]+)", "Yammer[\\s]+([\\d\\w\\.\\-]+)"), new Browser("Apache HTTP Client", "Apache\\\\-HttpClient", "Apache\\-HttpClient\\/([\\d\\w\\.\\-]+)"), new Browser("BlackBerry", "BlackBerry", "BlackBerry[\\d]+\\/([\\d\\w\\.\\-]+)"));
    private Pattern versionPattern;

    public static synchronized void addCustomBrowser(String name, String regex, String versionRegex) {
        browers.add(new Browser(name, regex, versionRegex));
    }

    public Browser(String name, String regex, String versionRegex) {
        super(name, regex);
        versionRegex = Other_Version.equals(versionRegex) ? name + versionRegex : versionRegex;
        if (null != versionRegex) {
            this.versionPattern = Pattern.compile(versionRegex, 2);
        }
    }

    public String getVersion(String userAgentString) {
        if (isUnknown()) {
            return null;
        }
        return ReUtil.getGroup1(this.versionPattern, userAgentString);
    }

    public boolean isMobile() {
        String name = getName();
        return "PSP".equals(name) || "Yammer Mobile".equals(name) || "Android Browser".equals(name) || "IEMobile".equals(name) || "MicroMessenger".equals(name) || "miniProgram".equals(name) || "DingTalk".equals(name);
    }
}
