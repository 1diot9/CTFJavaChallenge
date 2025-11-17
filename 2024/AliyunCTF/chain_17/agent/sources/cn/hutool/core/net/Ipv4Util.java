package cn.hutool.core.net;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.PatternPool;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import org.apache.tomcat.util.bcel.Const;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/net/Ipv4Util.class */
public class Ipv4Util {
    public static final String LOCAL_IP = "127.0.0.1";
    public static final String IP_SPLIT_MARK = "-";
    public static final String IP_MASK_SPLIT_MARK = "/";
    public static final int IP_MASK_MAX = 32;

    public static String formatIpBlock(String ip, String mask) {
        return ip + "/" + getMaskBitByMask(mask);
    }

    public static List<String> list(String ipRange, boolean isAll) {
        if (ipRange.contains("-")) {
            String[] range = StrUtil.splitToArray(ipRange, "-");
            return list(range[0], range[1]);
        }
        if (!ipRange.contains("/")) {
            return ListUtil.toList(ipRange);
        }
        String[] param = StrUtil.splitToArray(ipRange, "/");
        return list(param[0], Integer.parseInt(param[1]), isAll);
    }

    public static List<String> list(String ip, int maskBit, boolean isAll) {
        if (maskBit == 32) {
            List<String> list = new ArrayList<>();
            if (isAll) {
                list.add(ip);
            }
            return list;
        }
        String startIp = getBeginIpStr(ip, maskBit);
        String endIp = getEndIpStr(ip, maskBit);
        if (isAll) {
            return list(startIp, endIp);
        }
        int lastDotIndex = startIp.lastIndexOf(46) + 1;
        String startIp2 = StrUtil.subPre(startIp, lastDotIndex) + (Integer.parseInt((String) Objects.requireNonNull(StrUtil.subSuf(startIp, lastDotIndex))) + 1);
        int lastDotIndex2 = endIp.lastIndexOf(46) + 1;
        return list(startIp2, StrUtil.subPre(endIp, lastDotIndex2) + (Integer.parseInt((String) Objects.requireNonNull(StrUtil.subSuf(endIp, lastDotIndex2))) - 1));
    }

    public static List<String> list(String ipFrom, String ipTo) {
        int count = countByIpRange(ipFrom, ipTo);
        int[] from = (int[]) Convert.convert(int[].class, (Object) StrUtil.splitToArray((CharSequence) ipFrom, '.'));
        int[] to = (int[]) Convert.convert(int[].class, (Object) StrUtil.splitToArray((CharSequence) ipTo, '.'));
        List<String> ips = new ArrayList<>(count);
        boolean aIsStart = true;
        boolean bIsStart = true;
        boolean cIsStart = true;
        int aEnd = to[0];
        int a = from[0];
        while (a <= aEnd) {
            boolean aIsEnd = a == aEnd;
            int bEnd = aIsEnd ? to[1] : Const.MAX_ARRAY_DIMENSIONS;
            int b = aIsStart ? from[1] : 0;
            while (b <= bEnd) {
                boolean bIsEnd = aIsEnd && b == bEnd;
                int cEnd = bIsEnd ? to[2] : Const.MAX_ARRAY_DIMENSIONS;
                int c = bIsStart ? from[2] : 0;
                while (c <= cEnd) {
                    boolean cIsEnd = bIsEnd && c == cEnd;
                    int dEnd = cIsEnd ? to[3] : Const.MAX_ARRAY_DIMENSIONS;
                    for (int d = cIsStart ? from[3] : 0; d <= dEnd; d++) {
                        ips.add(a + "." + b + "." + c + "." + d);
                    }
                    cIsStart = false;
                    c++;
                }
                bIsStart = false;
                b++;
            }
            aIsStart = false;
            a++;
        }
        return ips;
    }

    public static String longToIpv4(long longIP) {
        StringBuilder sb = StrUtil.builder();
        sb.append((longIP >> 24) & 255);
        sb.append('.');
        sb.append((longIP >> 16) & 255);
        sb.append('.');
        sb.append((longIP >> 8) & 255);
        sb.append('.');
        sb.append(longIP & 255);
        return sb.toString();
    }

    public static long ipv4ToLong(String strIP) {
        Matcher matcher = PatternPool.IPV4.matcher(strIP);
        if (matcher.matches()) {
            return matchAddress(matcher);
        }
        throw new IllegalArgumentException("Invalid IPv4 address!");
    }

    public static long ipv4ToLong(String strIP, long defaultValue) {
        return Validator.isIpv4(strIP) ? ipv4ToLong(strIP) : defaultValue;
    }

    public static String getBeginIpStr(String ip, int maskBit) {
        return longToIpv4(getBeginIpLong(ip, maskBit).longValue());
    }

    public static Long getBeginIpLong(String ip, int maskBit) {
        return Long.valueOf(ipv4ToLong(ip) & ipv4ToLong(getMaskByMaskBit(maskBit)));
    }

    public static String getEndIpStr(String ip, int maskBit) {
        return longToIpv4(getEndIpLong(ip, maskBit).longValue());
    }

    public static int getMaskBitByMask(String mask) {
        Integer maskBit = MaskBit.getMaskBit(mask);
        if (maskBit == null) {
            throw new IllegalArgumentException("Invalid netmask " + mask);
        }
        return maskBit.intValue();
    }

    public static int countByMaskBit(int maskBit, boolean isAll) {
        if (false == isAll && (maskBit <= 0 || maskBit >= 32)) {
            return 0;
        }
        int count = (int) Math.pow(2.0d, 32 - maskBit);
        return isAll ? count : count - 2;
    }

    public static String getMaskByMaskBit(int maskBit) {
        return MaskBit.get(maskBit);
    }

    public static String getMaskByIpRange(String fromIp, String toIp) {
        long toIpLong = ipv4ToLong(toIp);
        long fromIpLong = ipv4ToLong(fromIp);
        Assert.isTrue(fromIpLong < toIpLong, "to IP must be greater than from IP!", new Object[0]);
        String[] fromIpSplit = StrUtil.splitToArray((CharSequence) fromIp, '.');
        String[] toIpSplit = StrUtil.splitToArray((CharSequence) toIp, '.');
        StringBuilder mask = new StringBuilder();
        for (int i = 0; i < toIpSplit.length; i++) {
            mask.append((Const.MAX_ARRAY_DIMENSIONS - Integer.parseInt(toIpSplit[i])) + Integer.parseInt(fromIpSplit[i])).append('.');
        }
        return mask.substring(0, mask.length() - 1);
    }

    public static int countByIpRange(String fromIp, String toIp) {
        long toIpLong = ipv4ToLong(toIp);
        long fromIpLong = ipv4ToLong(fromIp);
        if (fromIpLong > toIpLong) {
            throw new IllegalArgumentException("to IP must be greater than from IP!");
        }
        int count = 1;
        int[] fromIpSplit = StrUtil.split((CharSequence) fromIp, '.').stream().mapToInt(Integer::parseInt).toArray();
        int[] toIpSplit = StrUtil.split((CharSequence) toIp, '.').stream().mapToInt(Integer::parseInt).toArray();
        for (int i = fromIpSplit.length - 1; i >= 0; i--) {
            count = (int) (count + ((toIpSplit[i] - fromIpSplit[i]) * Math.pow(256.0d, (fromIpSplit.length - i) - 1)));
        }
        return count;
    }

    public static boolean isMaskValid(String mask) {
        return MaskBit.getMaskBit(mask) != null;
    }

    public static boolean isMaskBitValid(int maskBit) {
        return MaskBit.get(maskBit) != null;
    }

    public static boolean isInnerIP(String ipAddress) {
        long ipNum = ipv4ToLong(ipAddress);
        long aBegin = ipv4ToLong("10.0.0.0");
        long aEnd = ipv4ToLong("10.255.255.255");
        long bBegin = ipv4ToLong("172.16.0.0");
        long bEnd = ipv4ToLong("172.31.255.255");
        long cBegin = ipv4ToLong("192.168.0.0");
        long cEnd = ipv4ToLong("192.168.255.255");
        boolean isInnerIp = isInner(ipNum, aBegin, aEnd) || isInner(ipNum, bBegin, bEnd) || isInner(ipNum, cBegin, cEnd) || "127.0.0.1".equals(ipAddress);
        return isInnerIp;
    }

    public static Long getEndIpLong(String ip, int maskBit) {
        return Long.valueOf(getBeginIpLong(ip, maskBit).longValue() + (ipv4ToLong(getMaskByMaskBit(maskBit)) ^ (-1)));
    }

    private static long matchAddress(Matcher matcher) {
        long addr = 0;
        for (int i = 1; i <= 4; i++) {
            addr |= Long.parseLong(matcher.group(i)) << (8 * (4 - i));
        }
        return addr;
    }

    private static boolean isInner(long userIp, long begin, long end) {
        return userIp >= begin && userIp <= end;
    }
}
