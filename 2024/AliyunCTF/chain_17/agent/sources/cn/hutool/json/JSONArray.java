package cn.hutool.json;

import cn.hutool.core.bean.BeanPath;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Filter;
import cn.hutool.core.lang.mutable.Mutable;
import cn.hutool.core.lang.mutable.MutableObj;
import cn.hutool.core.lang.mutable.MutablePair;
import cn.hutool.core.text.StrJoiner;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.serialize.JSONWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.invoke.SerializedLambda;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;
import java.util.function.Supplier;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/json/JSONArray.class */
public class JSONArray implements JSON, JSONGetter<Integer>, List<Object>, RandomAccess {
    private static final long serialVersionUID = 2664900568717612292L;
    public static final int DEFAULT_CAPACITY = 10;
    private List<Object> rawList;
    private final JSONConfig config;

    private static /* synthetic */ Object $deserializeLambda$(SerializedLambda lambda) {
        String implMethodName = lambda.getImplMethodName();
        boolean z = -1;
        switch (implMethodName.hashCode()) {
            case -1220786921:
                if (implMethodName.equals("lambda$write$2cc9e97d$1")) {
                    z = false;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
                if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/collection/CollUtil$Consumer") && lambda.getFunctionalInterfaceMethodName().equals("accept") && lambda.getFunctionalInterfaceMethodSignature().equals("(Ljava/lang/Object;I)V") && lambda.getImplClass().equals("cn/hutool/json/JSONArray") && lambda.getImplMethodSignature().equals("(Lcn/hutool/json/serialize/JSONWriter;Lcn/hutool/core/lang/Filter;Ljava/lang/Object;I)V")) {
                    JSONWriter jSONWriter = (JSONWriter) lambda.getCapturedArg(0);
                    Filter filter = (Filter) lambda.getCapturedArg(1);
                    return (value, index) -> {
                        jSONWriter.writeField(new MutablePair<>(Integer.valueOf(index), value), (Filter<MutablePair<Object, Object>>) filter);
                    };
                }
                break;
        }
        throw new IllegalArgumentException("Invalid lambda deserialization");
    }

    public JSONArray() {
        this(10);
    }

    public JSONArray(int initialCapacity) {
        this(initialCapacity, JSONConfig.create());
    }

    public JSONArray(JSONConfig config) {
        this(10, config);
    }

    public JSONArray(int initialCapacity, JSONConfig config) {
        this.rawList = new ArrayList(initialCapacity);
        this.config = (JSONConfig) ObjectUtil.defaultIfNull(config, (Supplier<? extends JSONConfig>) JSONConfig::create);
    }

    public JSONArray(Object object) throws JSONException {
        this(object, true);
    }

    public JSONArray(Object object, boolean ignoreNullValue) throws JSONException {
        this(object, JSONConfig.create().setIgnoreNullValue(ignoreNullValue));
    }

    public JSONArray(Object object, JSONConfig jsonConfig) throws JSONException {
        this(object, jsonConfig, null);
    }

    public JSONArray(Object object, JSONConfig jsonConfig, Filter<Mutable<Object>> filter) throws JSONException {
        this(10, jsonConfig);
        ObjectMapper.of(object).map(this, filter);
    }

    @Override // cn.hutool.json.JSON, cn.hutool.json.JSONGetter
    public JSONConfig getConfig() {
        return this.config;
    }

    public JSONArray setDateFormat(String format) {
        this.config.setDateFormat(format);
        return this;
    }

    public String join(String separator) throws JSONException {
        return StrJoiner.of(separator).append(this, InternalJSONUtil::valueToString).toString();
    }

    @Override // java.util.List
    public Object get(int index) {
        return this.rawList.get(index);
    }

    @Override // cn.hutool.core.getter.OptBasicTypeGetter
    public Object getObj(Integer index, Object defaultValue) {
        return (index.intValue() < 0 || index.intValue() >= size()) ? defaultValue : this.rawList.get(index.intValue());
    }

    @Override // cn.hutool.json.JSON
    public Object getByPath(String expression) {
        return BeanPath.create(expression).get(this);
    }

    @Override // cn.hutool.json.JSON
    public <T> T getByPath(String str, Class<T> cls) {
        return (T) JSONConverter.jsonConvert(cls, getByPath(str), getConfig());
    }

    @Override // cn.hutool.json.JSON
    public void putByPath(String expression, Object value) {
        BeanPath.create(expression).set(this, value);
    }

    public JSONArray put(Object value) {
        return set(value);
    }

    public JSONArray set(Object value) {
        add(value);
        return this;
    }

    public JSONArray put(int index, Object value) throws JSONException {
        set(index, value);
        return this;
    }

    public JSONObject toJSONObject(JSONArray names) throws JSONException {
        if (names == null || names.size() == 0 || size() == 0) {
            return null;
        }
        JSONObject jo = new JSONObject(this.config);
        for (int i = 0; i < names.size(); i++) {
            jo.set(names.getStr(Integer.valueOf(i)), getObj(Integer.valueOf(i)));
        }
        return jo;
    }

    @Override // java.util.List, java.util.Collection
    public int hashCode() {
        int result = (31 * 1) + (this.rawList == null ? 0 : this.rawList.hashCode());
        return result;
    }

    @Override // java.util.List, java.util.Collection
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        JSONArray other = (JSONArray) obj;
        if (this.rawList == null) {
            return other.rawList == null;
        }
        return this.rawList.equals(other.rawList);
    }

    @Override // java.util.List, java.util.Collection, java.lang.Iterable
    public Iterator<Object> iterator() {
        return this.rawList.iterator();
    }

    public Iterable<JSONObject> jsonIter() {
        return new JSONObjectIter(iterator());
    }

    @Override // java.util.List, java.util.Collection
    public int size() {
        return this.rawList.size();
    }

    @Override // java.util.List, java.util.Collection
    public boolean isEmpty() {
        return this.rawList.isEmpty();
    }

    @Override // java.util.List, java.util.Collection
    public boolean contains(Object o) {
        return this.rawList.contains(o);
    }

    @Override // java.util.List, java.util.Collection
    public Object[] toArray() {
        return this.rawList.toArray();
    }

    @Override // java.util.List, java.util.Collection
    public <T> T[] toArray(T[] tArr) {
        return (T[]) ((Object[]) JSONConverter.toArray(this, tArr.getClass().getComponentType()));
    }

    @Override // java.util.List, java.util.Collection
    public boolean add(Object e) {
        return addRaw(JSONUtil.wrap(e, this.config), null);
    }

    @Override // java.util.List
    public Object remove(int index) {
        if (index < 0 || index >= size()) {
            return null;
        }
        return this.rawList.remove(index);
    }

    @Override // java.util.List, java.util.Collection
    public boolean remove(Object o) {
        return this.rawList.remove(o);
    }

    @Override // java.util.List, java.util.Collection
    public boolean containsAll(Collection<?> c) {
        return this.rawList.containsAll(c);
    }

    @Override // java.util.List, java.util.Collection
    public boolean addAll(Collection<?> c) {
        if (CollUtil.isEmpty(c)) {
            return false;
        }
        for (Object obj : c) {
            add(obj);
        }
        return true;
    }

    @Override // java.util.List
    public boolean addAll(int index, Collection<? extends Object> collection) {
        if (CollUtil.isEmpty((Collection<?>) collection)) {
            return false;
        }
        ArrayList<Object> list = new ArrayList<>(collection.size());
        for (Object object : collection) {
            list.add(JSONUtil.wrap(object, this.config));
        }
        return this.rawList.addAll(index, list);
    }

    @Override // java.util.List, java.util.Collection
    public boolean removeAll(Collection<?> c) {
        return this.rawList.removeAll(c);
    }

    @Override // java.util.List, java.util.Collection
    public boolean retainAll(Collection<?> c) {
        return this.rawList.retainAll(c);
    }

    @Override // java.util.List, java.util.Collection
    public void clear() {
        this.rawList.clear();
    }

    @Override // java.util.List
    public Object set(int index, Object element) {
        return set(index, element, null);
    }

    public Object set(int index, Object element, Filter<MutablePair<Integer, Object>> filter) {
        if (null != filter) {
            MutablePair<Integer, Object> pair = new MutablePair<>(Integer.valueOf(index), element);
            if (filter.accept(pair)) {
                element = pair.getValue();
            }
        }
        if (index >= size()) {
            add(index, element);
        }
        return this.rawList.set(index, JSONUtil.wrap(element, this.config));
    }

    @Override // java.util.List
    public void add(int index, Object element) {
        if (index < 0) {
            throw new JSONException("JSONArray[{}] not found.", Integer.valueOf(index));
        }
        if (index < size()) {
            InternalJSONUtil.testValidity(element);
            this.rawList.add(index, JSONUtil.wrap(element, this.config));
        } else {
            while (index != size()) {
                add(JSONNull.NULL);
            }
            set(element);
        }
    }

    @Override // java.util.List
    public int indexOf(Object o) {
        return this.rawList.indexOf(o);
    }

    @Override // java.util.List
    public int lastIndexOf(Object o) {
        return this.rawList.lastIndexOf(o);
    }

    @Override // java.util.List
    public ListIterator<Object> listIterator() {
        return this.rawList.listIterator();
    }

    @Override // java.util.List
    public ListIterator<Object> listIterator(int index) {
        return this.rawList.listIterator(index);
    }

    @Override // java.util.List
    public List<Object> subList(int fromIndex, int toIndex) {
        return this.rawList.subList(fromIndex, toIndex);
    }

    public Object toArray(Class<?> arrayClass) {
        return JSONConverter.toArray(this, arrayClass);
    }

    public <T> List<T> toList(Class<T> elementType) {
        return JSONConverter.toList(this, elementType);
    }

    public String toString() {
        return toJSONString(0);
    }

    public String toJSONString(int indentFactor, Filter<MutablePair<Object, Object>> filter) {
        String obj;
        StringWriter sw = new StringWriter();
        synchronized (sw.getBuffer()) {
            obj = write(sw, indentFactor, 0, filter).toString();
        }
        return obj;
    }

    @Override // cn.hutool.json.JSON
    public Writer write(Writer writer, int indentFactor, int indent) throws JSONException {
        return write(writer, indentFactor, indent, null);
    }

    public Writer write(Writer writer, int indentFactor, int indent, Filter<MutablePair<Object, Object>> filter) throws JSONException {
        JSONWriter jsonWriter = JSONWriter.of(writer, indentFactor, indent, this.config).beginArray();
        CollUtil.forEach(this, (value, index) -> {
            jsonWriter.writeField(new MutablePair<>(Integer.valueOf(index), value), (Filter<MutablePair<Object, Object>>) filter);
        });
        jsonWriter.end();
        return writer;
    }

    public Object clone() throws CloneNotSupportedException {
        JSONArray clone = (JSONArray) super.clone();
        clone.rawList = (List) ObjectUtil.clone(this.rawList);
        return clone;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean addRaw(Object obj, Filter<Mutable<Object>> filter) {
        if (null != filter) {
            Mutable<Object> mutable = new MutableObj<>(obj);
            if (filter.accept(mutable)) {
                obj = mutable.get2();
            } else {
                return false;
            }
        }
        return this.rawList.add(obj);
    }
}
