package cn.hutool.core.lang;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.RandomUtil;
import java.io.Serializable;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/WeightRandom.class */
public class WeightRandom<T> implements Serializable {
    private static final long serialVersionUID = -8244697995702786499L;
    private final TreeMap<Double, T> weightMap;

    public static <T> WeightRandom<T> create() {
        return new WeightRandom<>();
    }

    public WeightRandom() {
        this.weightMap = new TreeMap<>();
    }

    public WeightRandom(WeightObj<T> weightObj) {
        this();
        if (null != weightObj) {
            add(weightObj);
        }
    }

    public WeightRandom(Iterable<WeightObj<T>> weightObjs) {
        this();
        if (CollUtil.isNotEmpty(weightObjs)) {
            for (WeightObj<T> weightObj : weightObjs) {
                add(weightObj);
            }
        }
    }

    public WeightRandom(WeightObj<T>[] weightObjs) {
        this();
        for (WeightObj<T> weightObj : weightObjs) {
            add(weightObj);
        }
    }

    public WeightRandom<T> add(T obj, double weight) {
        return add(new WeightObj<>(obj, weight));
    }

    public WeightRandom<T> add(WeightObj<T> weightObj) {
        if (null != weightObj) {
            double weight = weightObj.getWeight();
            if (weightObj.getWeight() > 0.0d) {
                double lastWeight = this.weightMap.size() == 0 ? 0.0d : this.weightMap.lastKey().doubleValue();
                this.weightMap.put(Double.valueOf(weight + lastWeight), weightObj.getObj());
            }
        }
        return this;
    }

    public WeightRandom<T> clear() {
        if (null != this.weightMap) {
            this.weightMap.clear();
        }
        return this;
    }

    public T next() {
        if (MapUtil.isEmpty(this.weightMap)) {
            return null;
        }
        Random random = RandomUtil.getRandom();
        double randomWeight = this.weightMap.lastKey().doubleValue() * random.nextDouble();
        SortedMap<Double, T> tailMap = this.weightMap.tailMap(Double.valueOf(randomWeight), false);
        return this.weightMap.get(tailMap.firstKey());
    }

    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/WeightRandom$WeightObj.class */
    public static class WeightObj<T> {
        private T obj;
        private final double weight;

        public WeightObj(T obj, double weight) {
            this.obj = obj;
            this.weight = weight;
        }

        public T getObj() {
            return this.obj;
        }

        public void setObj(T obj) {
            this.obj = obj;
        }

        public double getWeight() {
            return this.weight;
        }

        public int hashCode() {
            int result = (31 * 1) + (this.obj == null ? 0 : this.obj.hashCode());
            long temp = Double.doubleToLongBits(this.weight);
            return (31 * result) + ((int) (temp ^ (temp >>> 32)));
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            WeightObj<?> other = (WeightObj) obj;
            if (this.obj == null) {
                if (other.obj != null) {
                    return false;
                }
            } else if (!this.obj.equals(other.obj)) {
                return false;
            }
            return Double.doubleToLongBits(this.weight) == Double.doubleToLongBits(other.weight);
        }
    }
}
