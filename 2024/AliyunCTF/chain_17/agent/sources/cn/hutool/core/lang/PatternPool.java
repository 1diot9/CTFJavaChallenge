package cn.hutool.core.lang;

import cn.hutool.core.map.WeakConcurrentMap;
import java.util.function.Function;
import java.util.regex.Pattern;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/PatternPool.class */
public class PatternPool {
    public static final Pattern GENERAL = Pattern.compile(RegexPool.GENERAL);
    public static final Pattern NUMBERS = Pattern.compile(RegexPool.NUMBERS);
    public static final Pattern WORD = Pattern.compile(RegexPool.WORD);
    public static final Pattern CHINESE = Pattern.compile("[⺀-\u2eff⼀-\u2fdf㇀-\u31ef㐀-䶿一-鿿豈-\ufaff��-����-����-����-����-��]");
    public static final Pattern CHINESES = Pattern.compile("[⺀-\u2eff⼀-\u2fdf㇀-\u31ef㐀-䶿一-鿿豈-\ufaff��-����-����-����-����-��]+");
    public static final Pattern GROUP_VAR = Pattern.compile(RegexPool.GROUP_VAR);
    public static final Pattern IPV4 = Pattern.compile(RegexPool.IPV4);
    public static final Pattern IPV6 = Pattern.compile(RegexPool.IPV6);
    public static final Pattern MONEY = Pattern.compile(RegexPool.MONEY);
    public static final Pattern EMAIL = Pattern.compile(RegexPool.EMAIL, 2);
    public static final Pattern MOBILE = Pattern.compile(RegexPool.MOBILE);
    public static final Pattern MOBILE_HK = Pattern.compile(RegexPool.MOBILE_HK);
    public static final Pattern MOBILE_TW = Pattern.compile(RegexPool.MOBILE_TW);
    public static final Pattern MOBILE_MO = Pattern.compile(RegexPool.MOBILE_MO);
    public static final Pattern TEL = Pattern.compile(RegexPool.TEL);
    public static final Pattern TEL_400_800 = Pattern.compile(RegexPool.TEL_400_800);
    public static final Pattern CITIZEN_ID = Pattern.compile(RegexPool.CITIZEN_ID);
    public static final Pattern ZIP_CODE = Pattern.compile(RegexPool.ZIP_CODE);
    public static final Pattern BIRTHDAY = Pattern.compile(RegexPool.BIRTHDAY);
    public static final Pattern URL = Pattern.compile(RegexPool.URL);
    public static final Pattern URL_HTTP = Pattern.compile(RegexPool.URL_HTTP, 2);
    public static final Pattern GENERAL_WITH_CHINESE = Pattern.compile(RegexPool.GENERAL_WITH_CHINESE);
    public static final Pattern UUID = Pattern.compile(RegexPool.UUID, 2);
    public static final Pattern UUID_SIMPLE = Pattern.compile(RegexPool.UUID_SIMPLE);
    public static final Pattern MAC_ADDRESS = Pattern.compile(RegexPool.MAC_ADDRESS, 2);
    public static final Pattern HEX = Pattern.compile(RegexPool.HEX);
    public static final Pattern TIME = Pattern.compile(RegexPool.TIME);
    public static final Pattern PLATE_NUMBER = Pattern.compile(RegexPool.PLATE_NUMBER);
    public static final Pattern CREDIT_CODE = Pattern.compile(RegexPool.CREDIT_CODE);
    public static final Pattern CAR_VIN = Pattern.compile(RegexPool.CAR_VIN);
    public static final Pattern CAR_DRIVING_LICENCE = Pattern.compile(RegexPool.CAR_DRIVING_LICENCE);
    public static final Pattern CHINESE_NAME = Pattern.compile(RegexPool.CHINESE_NAME);
    private static final WeakConcurrentMap<RegexWithFlag, Pattern> POOL = new WeakConcurrentMap<>();

    public static Pattern get(String regex) {
        return get(regex, 0);
    }

    public static Pattern get(String regex, int flags) {
        RegexWithFlag regexWithFlag = new RegexWithFlag(regex, flags);
        return POOL.computeIfAbsent((WeakConcurrentMap<RegexWithFlag, Pattern>) regexWithFlag, (Function<? super WeakConcurrentMap<RegexWithFlag, Pattern>, ? extends Pattern>) key -> {
            return Pattern.compile(regex, flags);
        });
    }

    public static Pattern remove(String regex, int flags) {
        return POOL.remove(new RegexWithFlag(regex, flags));
    }

    public static void clear() {
        POOL.clear();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/PatternPool$RegexWithFlag.class */
    public static class RegexWithFlag {
        private final String regex;
        private final int flag;

        public RegexWithFlag(String regex, int flag) {
            this.regex = regex;
            this.flag = flag;
        }

        public int hashCode() {
            int result = (31 * 1) + this.flag;
            return (31 * result) + (this.regex == null ? 0 : this.regex.hashCode());
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            RegexWithFlag other = (RegexWithFlag) obj;
            if (this.flag != other.flag) {
                return false;
            }
            if (this.regex == null) {
                return other.regex == null;
            }
            return this.regex.equals(other.regex);
        }
    }
}
