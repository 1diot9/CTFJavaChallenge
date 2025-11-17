package cn.hutool.cron.pattern.parser;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.cron.CronException;
import cn.hutool.cron.pattern.Part;
import cn.hutool.cron.pattern.matcher.AlwaysTrueMatcher;
import cn.hutool.cron.pattern.matcher.PartMatcher;
import cn.hutool.cron.pattern.matcher.PatternMatcher;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/cron/pattern/parser/PatternParser.class */
public class PatternParser {
    private static final PartParser SECOND_VALUE_PARSER = PartParser.of(Part.SECOND);
    private static final PartParser MINUTE_VALUE_PARSER = PartParser.of(Part.MINUTE);
    private static final PartParser HOUR_VALUE_PARSER = PartParser.of(Part.HOUR);
    private static final PartParser DAY_OF_MONTH_VALUE_PARSER = PartParser.of(Part.DAY_OF_MONTH);
    private static final PartParser MONTH_VALUE_PARSER = PartParser.of(Part.MONTH);
    private static final PartParser DAY_OF_WEEK_VALUE_PARSER = PartParser.of(Part.DAY_OF_WEEK);
    private static final PartParser YEAR_VALUE_PARSER = PartParser.of(Part.YEAR);

    public static List<PatternMatcher> parse(String cronPattern) {
        return parseGroupPattern(cronPattern);
    }

    private static List<PatternMatcher> parseGroupPattern(String groupPattern) {
        List<String> patternList = StrUtil.splitTrim((CharSequence) groupPattern, '|');
        List<PatternMatcher> patternMatchers = new ArrayList<>(patternList.size());
        for (String pattern : patternList) {
            patternMatchers.add(parseSingle(pattern));
        }
        return patternMatchers;
    }

    private static PatternMatcher parseSingle(String pattern) {
        PartMatcher yearMatcher;
        String[] parts = pattern.split("\\s+");
        Assert.checkBetween(parts.length, 5, 7, () -> {
            return new CronException("Pattern [{}] is invalid, it must be 5-7 parts!", pattern);
        });
        int offset = 0;
        if (parts.length == 6 || parts.length == 7) {
            offset = 1;
        }
        String secondPart = 1 == offset ? parts[0] : CustomBooleanEditor.VALUE_0;
        if (parts.length == 7) {
            yearMatcher = YEAR_VALUE_PARSER.parse(parts[6]);
        } else {
            yearMatcher = AlwaysTrueMatcher.INSTANCE;
        }
        return new PatternMatcher(SECOND_VALUE_PARSER.parse(secondPart), MINUTE_VALUE_PARSER.parse(parts[offset]), HOUR_VALUE_PARSER.parse(parts[1 + offset]), DAY_OF_MONTH_VALUE_PARSER.parse(parts[2 + offset]), MONTH_VALUE_PARSER.parse(parts[3 + offset]), DAY_OF_WEEK_VALUE_PARSER.parse(parts[4 + offset]), yearMatcher);
    }
}
