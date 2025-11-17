package org.apache.tomcat.websocket.server;

import cn.hutool.core.text.StrPool;
import jakarta.websocket.DeploymentException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/server/UriTemplate.class */
public class UriTemplate {
    private static final StringManager sm = StringManager.getManager((Class<?>) UriTemplate.class);
    private final String normalized;
    private final List<Segment> segments = new ArrayList();
    private final boolean hasParameters;

    public UriTemplate(String path) throws DeploymentException {
        if (path == null || path.length() == 0 || !path.startsWith("/") || path.contains("/../") || path.contains("/./") || path.contains("//")) {
            throw new DeploymentException(sm.getString("uriTemplate.invalidPath", path));
        }
        StringBuilder normalized = new StringBuilder(path.length());
        Set<String> paramNames = new HashSet<>();
        String[] segments = path.split("/", -1);
        int paramCount = 0;
        int segmentCount = 0;
        for (int i = 0; i < segments.length; i++) {
            String segment = segments[i];
            if (segment.length() == 0) {
                if (i != 0 && (i != segments.length - 1 || paramCount != 0)) {
                    throw new DeploymentException(sm.getString("uriTemplate.emptySegment", path));
                }
            } else {
                normalized.append('/');
                int index = -1;
                if (segment.startsWith(StrPool.DELIM_START) && segment.endsWith("}")) {
                    index = segmentCount;
                    segment = segment.substring(1, segment.length() - 1);
                    normalized.append('{');
                    int i2 = paramCount;
                    paramCount++;
                    normalized.append(i2);
                    normalized.append('}');
                    if (!paramNames.add(segment)) {
                        throw new DeploymentException(sm.getString("uriTemplate.duplicateParameter", segment));
                    }
                } else {
                    if (segment.contains(StrPool.DELIM_START) || segment.contains("}")) {
                        throw new DeploymentException(sm.getString("uriTemplate.invalidSegment", segment, path));
                    }
                    normalized.append(segment);
                }
                this.segments.add(new Segment(index, segment));
                segmentCount++;
            }
        }
        this.normalized = normalized.toString();
        this.hasParameters = paramCount > 0;
    }

    public Map<String, String> match(UriTemplate candidate) {
        Map<String, String> result = new HashMap<>();
        if (candidate.getSegmentCount() != getSegmentCount()) {
            return null;
        }
        Iterator<Segment> targetSegments = this.segments.iterator();
        for (Segment candidateSegment : candidate.getSegments()) {
            Segment targetSegment = targetSegments.next();
            if (targetSegment.getParameterIndex() == -1) {
                if (!targetSegment.getValue().equals(candidateSegment.getValue())) {
                    return null;
                }
            } else {
                result.put(targetSegment.getValue(), candidateSegment.getValue());
            }
        }
        return result;
    }

    public boolean hasParameters() {
        return this.hasParameters;
    }

    public int getSegmentCount() {
        return this.segments.size();
    }

    public String getNormalizedPath() {
        return this.normalized;
    }

    private List<Segment> getSegments() {
        return this.segments;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-websocket-10.1.18.jar:org/apache/tomcat/websocket/server/UriTemplate$Segment.class */
    public static class Segment {
        private final int parameterIndex;
        private final String value;

        Segment(int parameterIndex, String value) {
            this.parameterIndex = parameterIndex;
            this.value = value;
        }

        public int getParameterIndex() {
            return this.parameterIndex;
        }

        public String getValue() {
            return this.value;
        }
    }
}
