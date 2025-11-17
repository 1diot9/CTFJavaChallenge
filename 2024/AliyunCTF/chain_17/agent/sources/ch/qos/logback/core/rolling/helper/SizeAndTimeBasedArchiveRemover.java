package ch.qos.logback.core.rolling.helper;

import java.io.File;
import java.time.Instant;
import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/rolling/helper/SizeAndTimeBasedArchiveRemover.class */
public class SizeAndTimeBasedArchiveRemover extends TimeBasedArchiveRemover {
    protected static final int NO_INDEX = -1;

    public SizeAndTimeBasedArchiveRemover(FileNamePattern fileNamePattern, RollingCalendar rc) {
        super(fileNamePattern, rc);
    }

    @Override // ch.qos.logback.core.rolling.helper.TimeBasedArchiveRemover
    protected File[] getFilesInPeriod(Instant instantOfPeriodToClean) {
        File archive0 = new File(this.fileNamePattern.convertMultipleArguments(instantOfPeriodToClean, 0));
        File parentDir = getParentDir(archive0);
        String stemRegex = createStemRegex(instantOfPeriodToClean);
        File[] matchingFileArray = FileFilterUtil.filesInFolderMatchingStemRegex(parentDir, stemRegex);
        return matchingFileArray;
    }

    private String createStemRegex(Instant instantOfPeriodToClean) {
        String regex = this.fileNamePattern.toRegexForFixedDate(instantOfPeriodToClean);
        return FileFilterUtil.afterLastSlash(regex);
    }

    @Override // ch.qos.logback.core.rolling.helper.TimeBasedArchiveRemover
    protected void descendingSort(File[] matchingFileArray, Instant instant) {
        String regexForIndexExtreaction = createStemRegex(instant);
        final Pattern pattern = Pattern.compile(regexForIndexExtreaction);
        Arrays.sort(matchingFileArray, new Comparator<File>() { // from class: ch.qos.logback.core.rolling.helper.SizeAndTimeBasedArchiveRemover.1
            @Override // java.util.Comparator
            public int compare(File f1, File f2) {
                int index1 = extractIndex(pattern, f1);
                int index2 = extractIndex(pattern, f2);
                if (index1 == index2) {
                    return 0;
                }
                if (index2 < index1) {
                    return -1;
                }
                return 1;
            }

            private int extractIndex(Pattern pattern2, File f1) {
                String indexAsStr;
                Matcher matcher = pattern2.matcher(f1.getName());
                if (!matcher.find() || (indexAsStr = matcher.group(1)) == null || indexAsStr.isEmpty()) {
                    return -1;
                }
                return Integer.parseInt(indexAsStr);
            }
        });
    }
}
