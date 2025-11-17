package com.fasterxml.jackson.core.io.doubleparser;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/io/doubleparser/AbstractFloatValueParser.class */
abstract class AbstractFloatValueParser extends AbstractNumberParser {
    public static final int MAX_INPUT_LENGTH = 2147483643;
    static final long MINIMAL_NINETEEN_DIGIT_INTEGER = 1000000000000000000L;
    static final int MAX_EXPONENT_NUMBER = 1024;
}
