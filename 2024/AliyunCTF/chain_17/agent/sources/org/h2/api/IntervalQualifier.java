package org.h2.api;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/api/IntervalQualifier.class */
public enum IntervalQualifier {
    YEAR,
    MONTH,
    DAY,
    HOUR,
    MINUTE,
    SECOND,
    YEAR_TO_MONTH,
    DAY_TO_HOUR,
    DAY_TO_MINUTE,
    DAY_TO_SECOND,
    HOUR_TO_MINUTE,
    HOUR_TO_SECOND,
    MINUTE_TO_SECOND;

    private final String string = name().replace('_', ' ').intern();

    public static IntervalQualifier valueOf(int i) {
        switch (i) {
            case 0:
                return YEAR;
            case 1:
                return MONTH;
            case 2:
                return DAY;
            case 3:
                return HOUR;
            case 4:
                return MINUTE;
            case 5:
                return SECOND;
            case 6:
                return YEAR_TO_MONTH;
            case 7:
                return DAY_TO_HOUR;
            case 8:
                return DAY_TO_MINUTE;
            case 9:
                return DAY_TO_SECOND;
            case 10:
                return HOUR_TO_MINUTE;
            case 11:
                return HOUR_TO_SECOND;
            case 12:
                return MINUTE_TO_SECOND;
            default:
                throw new IllegalArgumentException();
        }
    }

    IntervalQualifier() {
    }

    public boolean isYearMonth() {
        return this == YEAR || this == MONTH || this == YEAR_TO_MONTH;
    }

    public boolean isDayTime() {
        return !isYearMonth();
    }

    public boolean hasYears() {
        return this == YEAR || this == YEAR_TO_MONTH;
    }

    public boolean hasMonths() {
        return this == MONTH || this == YEAR_TO_MONTH;
    }

    public boolean hasDays() {
        switch (this) {
            case DAY:
            case DAY_TO_HOUR:
            case DAY_TO_MINUTE:
            case DAY_TO_SECOND:
                return true;
            default:
                return false;
        }
    }

    public boolean hasHours() {
        switch (this) {
            case DAY_TO_HOUR:
            case DAY_TO_MINUTE:
            case DAY_TO_SECOND:
            case HOUR:
            case HOUR_TO_MINUTE:
            case HOUR_TO_SECOND:
                return true;
            default:
                return false;
        }
    }

    public boolean hasMinutes() {
        switch (this) {
            case DAY_TO_MINUTE:
            case DAY_TO_SECOND:
            case HOUR_TO_MINUTE:
            case HOUR_TO_SECOND:
            case MINUTE:
            case MINUTE_TO_SECOND:
                return true;
            case HOUR:
            default:
                return false;
        }
    }

    public boolean hasSeconds() {
        switch (this) {
            case DAY_TO_SECOND:
            case HOUR_TO_SECOND:
            case MINUTE_TO_SECOND:
            case SECOND:
                return true;
            case HOUR:
            case HOUR_TO_MINUTE:
            case MINUTE:
            default:
                return false;
        }
    }

    public boolean hasMultipleFields() {
        return ordinal() > 5;
    }

    @Override // java.lang.Enum
    public String toString() {
        return this.string;
    }

    public String getTypeName(int i, int i2) {
        return getTypeName(new StringBuilder(), i, i2, false).toString();
    }

    public StringBuilder getTypeName(StringBuilder sb, int i, int i2, boolean z) {
        if (!z) {
            sb.append("INTERVAL ");
        }
        switch (this) {
            case DAY:
            case HOUR:
            case MINUTE:
            case YEAR:
            case MONTH:
                sb.append(this.string);
                if (i > 0) {
                    sb.append('(').append(i).append(')');
                    break;
                }
                break;
            case DAY_TO_HOUR:
                sb.append("DAY");
                if (i > 0) {
                    sb.append('(').append(i).append(')');
                }
                sb.append(" TO HOUR");
                break;
            case DAY_TO_MINUTE:
                sb.append("DAY");
                if (i > 0) {
                    sb.append('(').append(i).append(')');
                }
                sb.append(" TO MINUTE");
                break;
            case DAY_TO_SECOND:
                sb.append("DAY");
                if (i > 0) {
                    sb.append('(').append(i).append(')');
                }
                sb.append(" TO SECOND");
                if (i2 >= 0) {
                    sb.append('(').append(i2).append(')');
                    break;
                }
                break;
            case HOUR_TO_MINUTE:
                sb.append("HOUR");
                if (i > 0) {
                    sb.append('(').append(i).append(')');
                }
                sb.append(" TO MINUTE");
                break;
            case HOUR_TO_SECOND:
                sb.append("HOUR");
                if (i > 0) {
                    sb.append('(').append(i).append(')');
                }
                sb.append(" TO SECOND");
                if (i2 >= 0) {
                    sb.append('(').append(i2).append(')');
                    break;
                }
                break;
            case MINUTE_TO_SECOND:
                sb.append("MINUTE");
                if (i > 0) {
                    sb.append('(').append(i).append(')');
                }
                sb.append(" TO SECOND");
                if (i2 >= 0) {
                    sb.append('(').append(i2).append(')');
                    break;
                }
                break;
            case SECOND:
                sb.append(this.string);
                if (i > 0 || i2 >= 0) {
                    sb.append('(').append(i > 0 ? i : 2);
                    if (i2 >= 0) {
                        sb.append(", ").append(i2);
                    }
                    sb.append(')');
                    break;
                }
                break;
            case YEAR_TO_MONTH:
                sb.append("YEAR");
                if (i > 0) {
                    sb.append('(').append(i).append(')');
                }
                sb.append(" TO MONTH");
                break;
        }
        return sb;
    }
}
