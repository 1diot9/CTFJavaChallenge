package cn.hutool.core.text.csv;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/text/csv/CsvData.class */
public class CsvData implements Iterable<CsvRow>, Serializable {
    private static final long serialVersionUID = 1;
    private final List<String> header;
    private final List<CsvRow> rows;

    public CsvData(List<String> header, List<CsvRow> rows) {
        this.header = header;
        this.rows = rows;
    }

    public int getRowCount() {
        return this.rows.size();
    }

    public List<String> getHeader() {
        if (null == this.header) {
            return null;
        }
        return Collections.unmodifiableList(this.header);
    }

    public CsvRow getRow(int index) {
        return this.rows.get(index);
    }

    public List<CsvRow> getRows() {
        return this.rows;
    }

    @Override // java.lang.Iterable
    public Iterator<CsvRow> iterator() {
        return this.rows.iterator();
    }

    public String toString() {
        return "CsvData{header=" + this.header + ", rows=" + this.rows + '}';
    }
}
