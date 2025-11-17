package com.fasterxml.jackson.datatype.jsr310;

import com.fasterxml.jackson.core.io.NumberInput;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.function.BiFunction;

/* loaded from: server.jar:BOOT-INF/lib/jackson-datatype-jsr310-2.15.3.jar:com/fasterxml/jackson/datatype/jsr310/DecimalUtils.class */
public final class DecimalUtils {
    private static final BigDecimal ONE_BILLION = new BigDecimal(1000000000L);

    private DecimalUtils() {
    }

    public static String toDecimal(long seconds, int nanoseconds) {
        StringBuilder sb = new StringBuilder(20).append(seconds).append('.');
        if (nanoseconds != 0) {
            StringBuilder nanoSB = new StringBuilder(9);
            nanoSB.append(nanoseconds);
            int nanosLen = nanoSB.length();
            int prepZeroes = 9 - nanosLen;
            while (prepZeroes > 0) {
                prepZeroes--;
                sb.append('0');
            }
            sb.append((CharSequence) nanoSB);
        } else {
            if (seconds == 0) {
                return "0.0";
            }
            sb.append("000000000");
        }
        return sb.toString();
    }

    public static BigDecimal toBigDecimal(long seconds, int nanoseconds) {
        if (nanoseconds != 0) {
            return NumberInput.parseBigDecimal(toDecimal(seconds, nanoseconds));
        }
        if (seconds == 0) {
            return BigDecimal.ZERO.setScale(1);
        }
        return BigDecimal.valueOf(seconds).setScale(9);
    }

    @Deprecated
    public static int extractNanosecondDecimal(BigDecimal value, long integer) {
        return value.subtract(BigDecimal.valueOf(integer)).multiply(ONE_BILLION).intValue();
    }

    public static <T> T extractSecondsAndNanos(BigDecimal seconds, BiFunction<Long, Integer, T> convert) {
        long secondsOnly;
        int nanosOnly;
        BigDecimal nanoseconds = seconds.scaleByPowerOfTen(9);
        if (nanoseconds.precision() - nanoseconds.scale() <= 0) {
            nanosOnly = 0;
            secondsOnly = 0;
        } else if (seconds.scale() < -63) {
            nanosOnly = 0;
            secondsOnly = 0;
        } else {
            secondsOnly = seconds.longValue();
            nanosOnly = nanoseconds.subtract(BigDecimal.valueOf(secondsOnly).scaleByPowerOfTen(9)).intValue();
            if (secondsOnly < 0 && secondsOnly > Instant.MIN.getEpochSecond()) {
                nanosOnly = Math.abs(nanosOnly);
            }
        }
        return convert.apply(Long.valueOf(secondsOnly), Integer.valueOf(nanosOnly));
    }
}
