package cn.hutool.core.text.csv;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/text/csv/CsvRow.class */
public final class CsvRow implements List<String> {
    private final long originalLineNumber;
    final Map<String, Integer> headerMap;
    final List<String> fields;

    public CsvRow(long originalLineNumber, Map<String, Integer> headerMap, List<String> fields) {
        Assert.notNull(fields, "fields must be not null!", new Object[0]);
        this.originalLineNumber = originalLineNumber;
        this.headerMap = headerMap;
        this.fields = fields;
    }

    public long getOriginalLineNumber() {
        return this.originalLineNumber;
    }

    public String getByName(String name) {
        Assert.notNull(this.headerMap, "No header available!", new Object[0]);
        Integer col = this.headerMap.get(name);
        if (col != null) {
            return get(col.intValue());
        }
        return null;
    }

    public List<String> getRawList() {
        return this.fields;
    }

    public Map<String, String> getFieldMap() {
        if (this.headerMap == null) {
            throw new IllegalStateException("No header available");
        }
        Map<String, String> fieldMap = new LinkedHashMap<>(this.headerMap.size(), 1.0f);
        for (Map.Entry<String, Integer> header : this.headerMap.entrySet()) {
            String key = header.getKey();
            Integer col = this.headerMap.get(key);
            String val = null == col ? null : get(col.intValue());
            fieldMap.put(key, val);
        }
        return fieldMap;
    }

    public <T> T toBean(Class<T> cls) {
        return (T) BeanUtil.toBeanIgnoreError(getFieldMap(), cls);
    }

    public int getFieldCount() {
        return this.fields.size();
    }

    @Override // java.util.List, java.util.Collection
    public int size() {
        return this.fields.size();
    }

    @Override // java.util.List, java.util.Collection
    public boolean isEmpty() {
        return this.fields.isEmpty();
    }

    @Override // java.util.List, java.util.Collection
    public boolean contains(Object o) {
        return this.fields.contains(o);
    }

    @Override // java.util.List, java.util.Collection, java.lang.Iterable
    public Iterator<String> iterator() {
        return this.fields.iterator();
    }

    @Override // java.util.List, java.util.Collection
    public Object[] toArray() {
        return this.fields.toArray();
    }

    @Override // java.util.List, java.util.Collection
    public <T> T[] toArray(T[] tArr) {
        return (T[]) this.fields.toArray(tArr);
    }

    @Override // java.util.List, java.util.Collection
    public boolean add(String e) {
        return this.fields.add(e);
    }

    @Override // java.util.List, java.util.Collection
    public boolean remove(Object o) {
        return this.fields.remove(o);
    }

    @Override // java.util.List, java.util.Collection
    public boolean containsAll(Collection<?> c) {
        return this.fields.containsAll(c);
    }

    @Override // java.util.List, java.util.Collection
    public boolean addAll(Collection<? extends String> c) {
        return this.fields.addAll(c);
    }

    @Override // java.util.List
    public boolean addAll(int index, Collection<? extends String> c) {
        return this.fields.addAll(index, c);
    }

    @Override // java.util.List, java.util.Collection
    public boolean removeAll(Collection<?> c) {
        return this.fields.removeAll(c);
    }

    @Override // java.util.List, java.util.Collection
    public boolean retainAll(Collection<?> c) {
        return this.fields.retainAll(c);
    }

    @Override // java.util.List, java.util.Collection
    public void clear() {
        this.fields.clear();
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // java.util.List
    public String get(int index) {
        if (index >= this.fields.size()) {
            return null;
        }
        return this.fields.get(index);
    }

    @Override // java.util.List
    public String set(int index, String element) {
        return this.fields.set(index, element);
    }

    @Override // java.util.List
    public void add(int index, String element) {
        this.fields.add(index, element);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // java.util.List
    public String remove(int index) {
        return this.fields.remove(index);
    }

    @Override // java.util.List
    public int indexOf(Object o) {
        return this.fields.indexOf(o);
    }

    @Override // java.util.List
    public int lastIndexOf(Object o) {
        return this.fields.lastIndexOf(o);
    }

    @Override // java.util.List
    public ListIterator<String> listIterator() {
        return this.fields.listIterator();
    }

    @Override // java.util.List
    public ListIterator<String> listIterator(int index) {
        return this.fields.listIterator(index);
    }

    @Override // java.util.List
    public List<String> subList(int fromIndex, int toIndex) {
        return this.fields.subList(fromIndex, toIndex);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("CsvRow{");
        sb.append("originalLineNumber=");
        sb.append(this.originalLineNumber);
        sb.append(", ");
        sb.append("fields=");
        if (this.headerMap != null) {
            sb.append('{');
            Iterator<Map.Entry<String, String>> it = getFieldMap().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> entry = it.next();
                sb.append(entry.getKey());
                sb.append('=');
                if (entry.getValue() != null) {
                    sb.append(entry.getValue());
                }
                if (it.hasNext()) {
                    sb.append(", ");
                }
            }
            sb.append('}');
        } else {
            sb.append(this.fields.toString());
        }
        sb.append('}');
        return sb.toString();
    }
}
