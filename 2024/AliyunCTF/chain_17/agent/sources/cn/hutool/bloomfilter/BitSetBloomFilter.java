package cn.hutool.bloomfilter;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.HashUtil;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.BitSet;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/bloomfilter/BitSetBloomFilter.class */
public class BitSetBloomFilter implements BloomFilter {
    private static final long serialVersionUID = 1;
    private final BitSet bitSet;
    private final int bitSetSize;
    private final int addedElements;
    private final int hashFunctionNumber;

    public BitSetBloomFilter(int c, int n, int k) {
        this.hashFunctionNumber = k;
        this.bitSetSize = (int) Math.ceil(c * k);
        this.addedElements = n;
        this.bitSet = new BitSet(this.bitSetSize);
    }

    @Deprecated
    public void init(String path, String charsetName) throws IOException {
        init(path, CharsetUtil.charset(charsetName));
    }

    public void init(String path, Charset charset) throws IOException {
        BufferedReader reader = FileUtil.getReader(path, charset);
        while (true) {
            try {
                String line = reader.readLine();
                if (line != null) {
                    add(line);
                } else {
                    return;
                }
            } finally {
                IoUtil.close((Closeable) reader);
            }
        }
    }

    @Override // cn.hutool.bloomfilter.BloomFilter
    public boolean add(String str) {
        if (contains(str)) {
            return false;
        }
        int[] positions = createHashes(str, this.hashFunctionNumber);
        for (int value : positions) {
            int position = Math.abs(value % this.bitSetSize);
            this.bitSet.set(position, true);
        }
        return true;
    }

    @Override // cn.hutool.bloomfilter.BloomFilter
    public boolean contains(String str) {
        int[] positions = createHashes(str, this.hashFunctionNumber);
        for (int i : positions) {
            int position = Math.abs(i % this.bitSetSize);
            if (!this.bitSet.get(position)) {
                return false;
            }
        }
        return true;
    }

    public double getFalsePositiveProbability() {
        return Math.pow(1.0d - Math.exp(((-this.hashFunctionNumber) * this.addedElements) / this.bitSetSize), this.hashFunctionNumber);
    }

    public static int[] createHashes(String str, int hashNumber) {
        int[] result = new int[hashNumber];
        for (int i = 0; i < hashNumber; i++) {
            result[i] = hash(str, i);
        }
        return result;
    }

    public static int hash(String str, int k) {
        switch (k) {
            case 0:
                return HashUtil.rsHash(str);
            case 1:
                return HashUtil.jsHash(str);
            case 2:
                return HashUtil.elfHash(str);
            case 3:
                return HashUtil.bkdrHash(str);
            case 4:
                return HashUtil.apHash(str);
            case 5:
                return HashUtil.djbHash(str);
            case 6:
                return HashUtil.sdbmHash(str);
            case 7:
                return HashUtil.pjwHash(str);
            default:
                return 0;
        }
    }
}
