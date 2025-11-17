package cn.hutool.core.math;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/math/Combination.class */
public class Combination implements Serializable {
    private static final long serialVersionUID = 1;
    private final String[] datas;

    public Combination(String[] datas) {
        this.datas = datas;
    }

    public static long count(int n, int m) {
        if (0 == m || n == m) {
            return serialVersionUID;
        }
        if (n > m) {
            return NumberUtil.factorial(n, n - m) / NumberUtil.factorial(m);
        }
        return 0L;
    }

    public static long countAll(int n) {
        if (n < 0 || n > 63) {
            throw new IllegalArgumentException(StrUtil.format("countAll must have n >= 0 and n <= 63, but got n={}", Integer.valueOf(n)));
        }
        if (n == 63) {
            return Long.MAX_VALUE;
        }
        return (serialVersionUID << n) - serialVersionUID;
    }

    public List<String[]> select(int m) {
        List<String[]> result = new ArrayList<>((int) count(this.datas.length, m));
        select(0, new String[m], 0, result);
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
    private void select(int dataIndex, String[] resultList, int resultIndex, List<String[]> list) {
        int resultLen = resultList.length;
        int resultCount = resultIndex + 1;
        if (resultCount > resultLen) {
            list.add(Arrays.copyOf(resultList, resultList.length));
            return;
        }
        for (int i = dataIndex; i < (this.datas.length + resultCount) - resultLen; i++) {
            resultList[resultIndex] = this.datas[i];
            select(i + 1, resultList, resultIndex + 1, list);
        }
    }
}
