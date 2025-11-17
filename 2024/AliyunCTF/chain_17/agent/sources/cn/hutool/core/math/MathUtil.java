package cn.hutool.core.math;

import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/math/MathUtil.class */
public class MathUtil {
    public static long arrangementCount(int n, int m) {
        return Arrangement.count(n, m);
    }

    public static long arrangementCount(int n) {
        return Arrangement.count(n);
    }

    public static List<String[]> arrangementSelect(String[] datas, int m) {
        return new Arrangement(datas).select(m);
    }

    public static List<String[]> arrangementSelect(String[] datas) {
        return new Arrangement(datas).select();
    }

    public static long combinationCount(int n, int m) {
        return Combination.count(n, m);
    }

    public static List<String[]> combinationSelect(String[] datas, int m) {
        return new Combination(datas).select(m);
    }

    public static long yuanToCent(double yuan) {
        return new Money(yuan).getCent();
    }

    public static double centToYuan(long cent) {
        long yuan = cent / 100;
        int centPart = (int) (cent % 100);
        return new Money(yuan, centPart).getAmount().doubleValue();
    }
}
