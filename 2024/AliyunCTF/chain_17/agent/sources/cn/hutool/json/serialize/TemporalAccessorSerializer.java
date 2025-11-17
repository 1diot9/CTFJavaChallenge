package cn.hutool.json.serialize;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONException;
import cn.hutool.json.JSONObject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAccessor;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/json/serialize/TemporalAccessorSerializer.class */
public class TemporalAccessorSerializer implements JSONObjectSerializer<TemporalAccessor>, JSONDeserializer<TemporalAccessor> {
    private static final String YEAR_KEY = "year";
    private static final String MONTH_KEY = "month";
    private static final String DAY_KEY = "day";
    private static final String HOUR_KEY = "hour";
    private static final String MINUTE_KEY = "minute";
    private static final String SECOND_KEY = "second";
    private static final String NANO_KEY = "nano";
    private final Class<? extends TemporalAccessor> temporalAccessorClass;

    public TemporalAccessorSerializer(Class<? extends TemporalAccessor> temporalAccessorClass) {
        this.temporalAccessorClass = temporalAccessorClass;
    }

    @Override // cn.hutool.json.serialize.JSONSerializer
    public void serialize(JSONObject json, TemporalAccessor bean) {
        if (bean instanceof LocalDate) {
            LocalDate localDate = (LocalDate) bean;
            json.set(YEAR_KEY, Integer.valueOf(localDate.getYear()));
            json.set(MONTH_KEY, Integer.valueOf(localDate.getMonthValue()));
            json.set(DAY_KEY, Integer.valueOf(localDate.getDayOfMonth()));
            return;
        }
        if (!(bean instanceof LocalDateTime)) {
            if (bean instanceof LocalTime) {
                LocalTime localTime = (LocalTime) bean;
                json.set(HOUR_KEY, Integer.valueOf(localTime.getHour()));
                json.set(MINUTE_KEY, Integer.valueOf(localTime.getMinute()));
                json.set(SECOND_KEY, Integer.valueOf(localTime.getSecond()));
                json.set(NANO_KEY, Integer.valueOf(localTime.getNano()));
                return;
            }
            throw new JSONException("Unsupported type to JSON: {}", bean.getClass().getName());
        }
        LocalDateTime localDateTime = (LocalDateTime) bean;
        json.set(YEAR_KEY, Integer.valueOf(localDateTime.getYear()));
        json.set(MONTH_KEY, Integer.valueOf(localDateTime.getMonthValue()));
        json.set(DAY_KEY, Integer.valueOf(localDateTime.getDayOfMonth()));
        json.set(HOUR_KEY, Integer.valueOf(localDateTime.getHour()));
        json.set(MINUTE_KEY, Integer.valueOf(localDateTime.getMinute()));
        json.set(SECOND_KEY, Integer.valueOf(localDateTime.getSecond()));
        json.set(NANO_KEY, Integer.valueOf(localDateTime.getNano()));
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // cn.hutool.json.serialize.JSONDeserializer
    public TemporalAccessor deserialize(JSON json) {
        JSONObject jsonObject = (JSONObject) json;
        if (LocalDate.class.equals(this.temporalAccessorClass)) {
            return LocalDate.of(jsonObject.getInt(YEAR_KEY).intValue(), jsonObject.getInt(MONTH_KEY).intValue(), jsonObject.getInt(DAY_KEY).intValue());
        }
        if (LocalDateTime.class.equals(this.temporalAccessorClass)) {
            return LocalDateTime.of(jsonObject.getInt(YEAR_KEY).intValue(), jsonObject.getInt(MONTH_KEY).intValue(), jsonObject.getInt(DAY_KEY).intValue(), jsonObject.getInt(HOUR_KEY).intValue(), jsonObject.getInt(MINUTE_KEY).intValue(), jsonObject.getInt(SECOND_KEY).intValue(), jsonObject.getInt(NANO_KEY).intValue());
        }
        if (LocalTime.class.equals(this.temporalAccessorClass)) {
            return LocalTime.of(jsonObject.getInt(HOUR_KEY).intValue(), jsonObject.getInt(MINUTE_KEY).intValue(), jsonObject.getInt(SECOND_KEY).intValue(), jsonObject.getInt(NANO_KEY).intValue());
        }
        throw new JSONException("Unsupported type from JSON: {}", this.temporalAccessorClass);
    }
}
