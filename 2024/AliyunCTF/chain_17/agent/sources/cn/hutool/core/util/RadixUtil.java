package cn.hutool.core.util;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/util/RadixUtil.class */
public class RadixUtil {
    public static final String RADIXS_34 = "0123456789ABCDEFGHJKLMNPQRSTUVWXYZ";
    public static final String RADIXS_SHUFFLE_34 = "H3UM16TDFPSBZJ90CW28QYRE45AXKNGV7L";
    public static final String RADIXS_59 = "0123456789abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ";
    public static final String RADIXS_SHUFFLE_59 = "vh9wGkfK8YmqbsoENP3764SeCX0dVzrgy1HRtpnTaLjJW2xQiZAcBMUFDu5";

    public static String encode(String radixs, int num) {
        long tmpNum = num >= 0 ? num : 4294967296L - ((num ^ (-1)) + 1);
        return encode(radixs, tmpNum, 32);
    }

    public static String encode(String radixs, long num) {
        if (num < 0) {
            throw new RuntimeException("暂不支持负数！");
        }
        return encode(radixs, num, 64);
    }

    public static int decodeToInt(String radixs, String encodeStr) {
        return (int) decode(radixs, encodeStr);
    }

    public static long decode(String radixs, String encodeStr) {
        int rl = radixs.length();
        long res = 0;
        for (char c : encodeStr.toCharArray()) {
            res = (res * rl) + radixs.indexOf(c);
        }
        return res;
    }

    private static String encode(String radixs, long num, int maxLength) {
        if (radixs.length() < 2) {
            throw new RuntimeException("自定义进制最少两个字符哦！");
        }
        int rl = radixs.length();
        long tmpNum = num;
        char[] aa = new char[maxLength];
        int i = aa.length;
        do {
            i--;
            aa[i] = radixs.charAt((int) (tmpNum % rl));
            tmpNum /= rl;
        } while (tmpNum > 0);
        return new String(aa, i, aa.length - i);
    }
}
