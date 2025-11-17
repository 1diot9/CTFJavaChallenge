package org.springframework.beans.factory.parsing;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/factory/parsing/Problem.class */
public class Problem {
    private final String message;
    private final Location location;

    @Nullable
    private final ParseState parseState;

    @Nullable
    private final Throwable rootCause;

    public Problem(String message, Location location) {
        this(message, location, null, null);
    }

    public Problem(String message, Location location, ParseState parseState) {
        this(message, location, parseState, null);
    }

    public Problem(String message, Location location, @Nullable ParseState parseState, @Nullable Throwable rootCause) {
        Assert.notNull(message, "Message must not be null");
        Assert.notNull(location, "Location must not be null");
        this.message = message;
        this.location = location;
        this.parseState = parseState;
        this.rootCause = rootCause;
    }

    public String getMessage() {
        return this.message;
    }

    public Location getLocation() {
        return this.location;
    }

    public String getResourceDescription() {
        return getLocation().getResource().getDescription();
    }

    @Nullable
    public ParseState getParseState() {
        return this.parseState;
    }

    @Nullable
    public Throwable getRootCause() {
        return this.rootCause;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Configuration problem: ");
        sb.append(getMessage());
        sb.append("\nOffending resource: ").append(getResourceDescription());
        if (getParseState() != null) {
            sb.append('\n').append(getParseState());
        }
        return sb.toString();
    }
}
