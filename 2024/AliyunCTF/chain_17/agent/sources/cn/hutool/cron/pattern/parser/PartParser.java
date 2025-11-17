package cn.hutool.cron.pattern.parser;

import ch.qos.logback.core.CoreConstants;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.Month;
import cn.hutool.core.date.Week;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.cron.CronException;
import cn.hutool.cron.pattern.Part;
import cn.hutool.cron.pattern.matcher.AlwaysTrueMatcher;
import cn.hutool.cron.pattern.matcher.BoolArrayMatcher;
import cn.hutool.cron.pattern.matcher.DayOfMonthMatcher;
import cn.hutool.cron.pattern.matcher.PartMatcher;
import cn.hutool.cron.pattern.matcher.YearValueMatcher;
import java.util.ArrayList;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/cron/pattern/parser/PartParser.class */
public class PartParser {
    private final Part part;

    public static PartParser of(Part part) {
        return new PartParser(part);
    }

    public PartParser(Part part) {
        this.part = part;
    }

    public PartMatcher parse(String value) {
        if (isMatchAllStr(value)) {
            return new AlwaysTrueMatcher();
        }
        List<Integer> values = parseArray(value);
        if (values.size() == 0) {
            throw new CronException("Invalid part value: [{}]", value);
        }
        switch (this.part) {
            case DAY_OF_MONTH:
                return new DayOfMonthMatcher(values);
            case YEAR:
                return new YearValueMatcher(values);
            default:
                return new BoolArrayMatcher(values);
        }
    }

    private List<Integer> parseArray(String value) {
        List<Integer> values = new ArrayList<>();
        List<String> parts = StrUtil.split((CharSequence) value, ',');
        for (String part : parts) {
            CollUtil.addAllIfNotContains(values, parseStep(part));
        }
        return values;
    }

    private List<Integer> parseStep(String value) {
        List<Integer> results;
        List<String> parts = StrUtil.split((CharSequence) value, '/');
        int size = parts.size();
        if (size == 1) {
            results = parseRange(value, -1);
        } else if (size == 2) {
            int step = parseNumber(parts.get(1));
            if (step < 1) {
                throw new CronException("Non positive divisor for field: [{}]", value);
            }
            results = parseRange(parts.get(0), step);
        } else {
            throw new CronException("Invalid syntax of field: [{}]", value);
        }
        return results;
    }

    private List<Integer> parseRange(String value, int step) {
        List<Integer> results = new ArrayList<>();
        if (value.length() <= 2) {
            int minValue = this.part.getMin();
            if (false == isMatchAllStr(value)) {
                minValue = Math.max(minValue, parseNumber(value));
            } else if (step < 1) {
                step = 1;
            }
            if (step > 0) {
                int maxValue = this.part.getMax();
                if (minValue > maxValue) {
                    throw new CronException("Invalid value {} > {}", Integer.valueOf(minValue), Integer.valueOf(maxValue));
                }
                int i = minValue;
                while (true) {
                    int i2 = i;
                    if (i2 > maxValue) {
                        break;
                    }
                    results.add(Integer.valueOf(i2));
                    i = i2 + step;
                }
            } else {
                results.add(Integer.valueOf(minValue));
            }
            return results;
        }
        List<String> parts = StrUtil.split((CharSequence) value, '-');
        int size = parts.size();
        if (size == 1) {
            int v1 = parseNumber(value);
            if (step > 0) {
                NumberUtil.appendRange(v1, this.part.getMax(), step, results);
            } else {
                results.add(Integer.valueOf(v1));
            }
        } else if (size == 2) {
            int v12 = parseNumber(parts.get(0));
            int v2 = parseNumber(parts.get(1));
            if (step < 1) {
                step = 1;
            }
            if (v12 < v2) {
                NumberUtil.appendRange(v12, v2, step, results);
            } else if (v12 > v2) {
                NumberUtil.appendRange(v12, this.part.getMax(), step, results);
                NumberUtil.appendRange(this.part.getMin(), v2, step, results);
            } else {
                NumberUtil.appendRange(v12, this.part.getMax(), step, results);
            }
        } else {
            throw new CronException("Invalid syntax of field: [{}]", value);
        }
        return results;
    }

    private static boolean isMatchAllStr(String value) {
        return 1 == value.length() && ("*".equals(value) || CoreConstants.NA.equals(value));
    }

    private int parseNumber(String value) throws CronException {
        int i;
        try {
            i = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            i = parseAlias(value);
        }
        if (i < 0) {
            i += this.part.getMax();
        }
        if (Part.DAY_OF_WEEK.equals(this.part) && Week.SUNDAY.getIso8601Value() == i) {
            i = Week.SUNDAY.ordinal();
        }
        return this.part.checkValue(i);
    }

    private int parseAlias(String name) throws CronException {
        if ("L".equalsIgnoreCase(name)) {
            return this.part.getMax();
        }
        switch (this.part) {
            case MONTH:
                return Month.of(name).getValueBaseOne();
            case DAY_OF_WEEK:
                return Week.of(name).ordinal();
            default:
                throw new CronException("Invalid alias value: [{}]", name);
        }
    }
}
