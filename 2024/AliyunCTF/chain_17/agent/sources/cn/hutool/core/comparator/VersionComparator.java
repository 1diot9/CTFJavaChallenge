package cn.hutool.core.comparator;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/comparator/VersionComparator.class */
public class VersionComparator implements Comparator<String>, Serializable {
    private static final long serialVersionUID = 8083701245147495562L;
    public static final VersionComparator INSTANCE = new VersionComparator();

    @Override // java.util.Comparator
    public int compare(String version1, String version2) {
        if (ObjectUtil.equal(version1, version2)) {
            return 0;
        }
        if (version1 == null && version2 == null) {
            return 0;
        }
        if (version1 == null) {
            return -1;
        }
        if (version2 == null) {
            return 1;
        }
        List<String> v1s = StrUtil.split((CharSequence) version1, '.');
        List<String> v2s = StrUtil.split((CharSequence) version2, '.');
        int diff = 0;
        int minLength = Math.min(v1s.size(), v2s.size());
        for (int i = 0; i < minLength; i++) {
            String v1 = v1s.get(i);
            String v2 = v2s.get(i);
            diff = v1.length() - v2.length();
            if (0 == diff) {
                diff = v1.compareTo(v2);
            }
            if (diff != 0) {
                break;
            }
        }
        return diff != 0 ? diff : v1s.size() - v2s.size();
    }
}
