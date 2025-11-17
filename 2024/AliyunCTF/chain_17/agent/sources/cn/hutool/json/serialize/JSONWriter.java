package cn.hutool.json.serialize;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TemporalAccessorUtil;
import cn.hutool.core.date.format.GlobalCustomFormat;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.lang.Filter;
import cn.hutool.core.lang.mutable.MutablePair;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONException;
import cn.hutool.json.JSONNull;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONString;
import cn.hutool.json.JSONUtil;
import java.io.IOException;
import java.io.Writer;
import java.time.MonthDay;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/json/serialize/JSONWriter.class */
public class JSONWriter extends Writer {
    private final int indentFactor;
    private final int indent;
    private final Writer writer;
    private final JSONConfig config;
    private boolean needSeparator;
    private boolean arrayMode;

    public static JSONWriter of(Writer writer, int indentFactor, int indent, JSONConfig config) {
        return new JSONWriter(writer, indentFactor, indent, config);
    }

    public JSONWriter(Writer writer, int indentFactor, int indent, JSONConfig config) {
        this.writer = writer;
        this.indentFactor = indentFactor;
        this.indent = indent;
        this.config = config;
    }

    public JSONWriter beginObj() {
        writeRaw('{');
        return this;
    }

    public JSONWriter beginArray() {
        writeRaw('[');
        this.arrayMode = true;
        return this;
    }

    public JSONWriter end() {
        writeLF().writeSpace(this.indent);
        writeRaw(this.arrayMode ? ']' : '}');
        flush();
        this.arrayMode = false;
        this.needSeparator = true;
        return this;
    }

    public JSONWriter writeKey(String key) {
        if (this.needSeparator) {
            writeRaw(',');
        }
        writeLF().writeSpace(this.indentFactor + this.indent);
        return writeRaw(JSONUtil.quote(key));
    }

    public JSONWriter writeValue(Object value) {
        if (JSONUtil.isNull(value) && this.config.isIgnoreNullValue()) {
            return this;
        }
        return writeValueDirect(value, null);
    }

    @Deprecated
    public JSONWriter writeField(String key, Object value) {
        if (JSONUtil.isNull(value) && this.config.isIgnoreNullValue()) {
            return this;
        }
        return writeKey(key).writeValueDirect(value, null);
    }

    public JSONWriter writeField(MutablePair<Object, Object> pair, Filter<MutablePair<Object, Object>> filter) {
        if (JSONUtil.isNull(pair.getValue()) && this.config.isIgnoreNullValue()) {
            return this;
        }
        if (null == filter || filter.accept(pair)) {
            if (false == this.arrayMode) {
                writeKey(StrUtil.toString(pair.getKey()));
            }
            return writeValueDirect(pair.getValue(), filter);
        }
        return this;
    }

    @Override // java.io.Writer
    public void write(char[] cbuf, int off, int len) throws IOException {
        this.writer.write(cbuf, off, len);
    }

    @Override // java.io.Writer, java.io.Flushable
    public void flush() {
        try {
            this.writer.flush();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    @Override // java.io.Writer, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.writer.close();
    }

    private JSONWriter writeValueDirect(Object value, Filter<MutablePair<Object, Object>> filter) {
        if (this.arrayMode) {
            if (this.needSeparator) {
                writeRaw(',');
            }
            writeLF().writeSpace(this.indentFactor + this.indent);
        } else {
            writeRaw(':').writeSpace(1);
        }
        this.needSeparator = true;
        return writeObjValue(value, filter);
    }

    private JSONWriter writeObjValue(Object value, Filter<MutablePair<Object, Object>> filter) {
        int indent = this.indentFactor + this.indent;
        if (value == null || (value instanceof JSONNull)) {
            writeRaw(JSONNull.NULL.toString());
        } else if (value instanceof JSON) {
            if (value instanceof JSONObject) {
                ((JSONObject) value).write(this.writer, this.indentFactor, indent, filter);
            } else if (value instanceof JSONArray) {
                ((JSONArray) value).write(this.writer, this.indentFactor, indent, filter);
            }
        } else if ((value instanceof Map) || (value instanceof Map.Entry)) {
            new JSONObject(value).write(this.writer, this.indentFactor, indent);
        } else if ((value instanceof Iterable) || (value instanceof Iterator) || ArrayUtil.isArray(value)) {
            new JSONArray(value).write(this.writer, this.indentFactor, indent);
        } else if (value instanceof Number) {
            writeNumberValue((Number) value);
        } else if ((value instanceof Date) || (value instanceof Calendar) || (value instanceof TemporalAccessor)) {
            if (value instanceof MonthDay) {
                writeStrValue(value.toString());
                return this;
            }
            String format = null == this.config ? null : this.config.getDateFormat();
            writeRaw(formatDate(value, format));
        } else if (value instanceof Boolean) {
            writeBooleanValue((Boolean) value);
        } else if (value instanceof JSONString) {
            writeJSONStringValue((JSONString) value);
        } else {
            writeStrValue(value.toString());
        }
        return this;
    }

    private void writeNumberValue(Number number) {
        boolean isStripTrailingZeros = null == this.config || this.config.isStripTrailingZeros();
        writeRaw(NumberUtil.toStr(number, isStripTrailingZeros));
    }

    private void writeBooleanValue(Boolean value) {
        writeRaw(value.toString());
    }

    private void writeJSONStringValue(JSONString jsonString) {
        try {
            String valueStr = jsonString.toJSONString();
            if (null != valueStr) {
                writeRaw(valueStr);
            } else {
                writeStrValue(jsonString.toString());
            }
        } catch (Exception e) {
            throw new JSONException(e);
        }
    }

    private void writeStrValue(String csq) {
        try {
            JSONUtil.quote(csq, this.writer);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    private void writeSpace(int count) {
        if (this.indentFactor > 0) {
            for (int i = 0; i < count; i++) {
                writeRaw(' ');
            }
        }
    }

    private JSONWriter writeLF() {
        if (this.indentFactor > 0) {
            writeRaw('\n');
        }
        return this;
    }

    private JSONWriter writeRaw(String csq) {
        try {
            this.writer.append((CharSequence) csq);
            return this;
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    private JSONWriter writeRaw(char c) {
        try {
            this.writer.write(c);
            return this;
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    private static String formatDate(Object dateObj, String format) {
        long timeMillis;
        String dateStr;
        if (StrUtil.isNotBlank(format)) {
            if (dateObj instanceof TemporalAccessor) {
                dateStr = TemporalAccessorUtil.format((TemporalAccessor) dateObj, format);
            } else {
                dateStr = DateUtil.format(Convert.toDate(dateObj), format);
            }
            if (GlobalCustomFormat.FORMAT_SECONDS.equals(format) || GlobalCustomFormat.FORMAT_MILLISECONDS.equals(format)) {
                return dateStr;
            }
            return JSONUtil.quote(dateStr);
        }
        if (dateObj instanceof TemporalAccessor) {
            timeMillis = TemporalAccessorUtil.toEpochMilli((TemporalAccessor) dateObj);
        } else if (dateObj instanceof Date) {
            timeMillis = ((Date) dateObj).getTime();
        } else if (dateObj instanceof Calendar) {
            timeMillis = ((Calendar) dateObj).getTimeInMillis();
        } else {
            throw new UnsupportedOperationException("Unsupported Date type: " + dateObj.getClass());
        }
        return String.valueOf(timeMillis);
    }
}
