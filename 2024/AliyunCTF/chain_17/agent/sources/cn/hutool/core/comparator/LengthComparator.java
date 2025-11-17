package cn.hutool.core.comparator;

import java.util.Comparator;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/comparator/LengthComparator.class */
public class LengthComparator implements Comparator<CharSequence> {
    public static final LengthComparator INSTANCE = new LengthComparator();

    @Override // java.util.Comparator
    public int compare(CharSequence o1, CharSequence o2) {
        int result = Integer.compare(o1.length(), o2.length());
        if (0 == result) {
            result = CompareUtil.compare(o1.toString(), o2.toString());
        }
        return result;
    }
}
