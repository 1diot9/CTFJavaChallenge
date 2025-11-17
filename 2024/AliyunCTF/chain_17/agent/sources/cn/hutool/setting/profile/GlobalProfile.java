package cn.hutool.setting.profile;

import cn.hutool.core.lang.Singleton;
import cn.hutool.setting.Setting;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/setting/profile/GlobalProfile.class */
public class GlobalProfile {
    private GlobalProfile() {
    }

    public static Profile setProfile(String profile) {
        return (Profile) Singleton.get(Profile.class, profile);
    }

    public static Setting getSetting(String settingName) {
        return ((Profile) Singleton.get(Profile.class, new Object[0])).getSetting(settingName);
    }
}
