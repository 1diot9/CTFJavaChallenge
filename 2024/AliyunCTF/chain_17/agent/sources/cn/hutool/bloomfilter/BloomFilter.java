package cn.hutool.bloomfilter;

import java.io.Serializable;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/bloomfilter/BloomFilter.class */
public interface BloomFilter extends Serializable {
    boolean contains(String str);

    boolean add(String str);
}
