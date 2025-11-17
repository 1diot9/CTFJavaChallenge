package ch.qos.logback.classic.pattern;

/* loaded from: agent.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/pattern/TargetLengthBasedClassNameAbbreviator.class */
public class TargetLengthBasedClassNameAbbreviator implements Abbreviator {
    final int targetLength;

    public TargetLengthBasedClassNameAbbreviator(int targetLength) {
        this.targetLength = targetLength;
    }

    @Override // ch.qos.logback.classic.pattern.Abbreviator
    public String abbreviate(String fqClassName) {
        if (fqClassName == null) {
            throw new IllegalArgumentException("Class name may not be null");
        }
        int inLen = fqClassName.length();
        if (inLen < this.targetLength) {
            return fqClassName;
        }
        StringBuilder buf = new StringBuilder(inLen);
        int rightMostDotIndex = fqClassName.lastIndexOf(46);
        if (rightMostDotIndex == -1) {
            return fqClassName;
        }
        int lastSegmentLength = inLen - rightMostDotIndex;
        int leftSegments_TargetLen = this.targetLength - lastSegmentLength;
        if (leftSegments_TargetLen < 0) {
            leftSegments_TargetLen = 0;
        }
        int leftSegmentsLen = inLen - lastSegmentLength;
        int maxPossibleTrim = leftSegmentsLen - leftSegments_TargetLen;
        int trimmed = 0;
        boolean inDotState = true;
        int i = 0;
        while (i < rightMostDotIndex) {
            char c = fqClassName.charAt(i);
            if (c == '.') {
                if (trimmed >= maxPossibleTrim) {
                    break;
                }
                buf.append(c);
                inDotState = true;
            } else if (inDotState) {
                buf.append(c);
                inDotState = false;
            } else {
                trimmed++;
            }
            i++;
        }
        buf.append(fqClassName.substring(i));
        return buf.toString();
    }
}
