package cn.hutool.core.math;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.NumberUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/math/Arrangement.class */
public class Arrangement implements Serializable {
    private static final long serialVersionUID = 1;
    private final String[] datas;

    public Arrangement(String[] datas) {
        this.datas = datas;
    }

    public static long count(int n) {
        return count(n, n);
    }

    public static long count(int n, int m) {
        if (n == m) {
            return NumberUtil.factorial(n);
        }
        if (n > m) {
            return NumberUtil.factorial(n, n - m);
        }
        return 0L;
    }

    public static long countAll(int n) {
        long total = 0;
        for (int i = 1; i <= n; i++) {
            total += count(n, i);
        }
        return total;
    }

    public List<String[]> select() {
        return select(this.datas.length);
    }

    public List<String[]> select(int m) {
        List<String[]> result = new ArrayList<>((int) count(this.datas.length, m));
        select(this.datas, new String[m], 0, result);
        return result;
    }

    public List<String[]> selectAll() {
        List<String[]> result = new ArrayList<>((int) countAll(this.datas.length));
        for (int i = 1; i <= this.datas.length; i++) {
            result.addAll(select(i));
        }
        return result;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void select(String[] datas, String[] resultList, int resultIndex, List<String[]> list) {
        if (resultIndex >= resultList.length) {
            if (false == list.contains(resultList)) {
                list.add(Arrays.copyOf(resultList, resultList.length));
            }
        } else {
            for (int i = 0; i < datas.length; i++) {
                resultList[resultIndex] = datas[i];
                select((String[]) ArrayUtil.remove((Object[]) datas, i), resultList, resultIndex + 1, list);
            }
        }
    }
}
