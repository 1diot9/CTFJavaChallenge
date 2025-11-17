package org.springframework.boot.autoconfigure.logging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.boot.autoconfigure.condition.ConditionEvaluationReport;
import org.springframework.util.ClassUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/logging/ConditionEvaluationReportMessage.class */
public class ConditionEvaluationReportMessage {
    private final StringBuilder message;

    public ConditionEvaluationReportMessage(ConditionEvaluationReport report) {
        this(report, "CONDITIONS EVALUATION REPORT");
    }

    public ConditionEvaluationReportMessage(ConditionEvaluationReport report, String title) {
        this.message = getLogMessage(report, title);
    }

    private StringBuilder getLogMessage(ConditionEvaluationReport report, String title) {
        String separator = "=".repeat(title.length());
        StringBuilder message = new StringBuilder();
        message.append(String.format("%n%n%n", new Object[0]));
        message.append(String.format("%s%n", separator));
        message.append(String.format("%s%n", title));
        message.append(String.format("%s%n%n%n", separator));
        Map<String, ConditionEvaluationReport.ConditionAndOutcomes> shortOutcomes = orderByName(report.getConditionAndOutcomesBySource());
        logPositiveMatches(message, shortOutcomes);
        logNegativeMatches(message, shortOutcomes);
        logExclusions(report, message);
        logUnconditionalClasses(report, message);
        message.append(String.format("%n%n", new Object[0]));
        return message;
    }

    private void logPositiveMatches(StringBuilder message, Map<String, ConditionEvaluationReport.ConditionAndOutcomes> shortOutcomes) {
        message.append(String.format("Positive matches:%n", new Object[0]));
        message.append(String.format("-----------------%n", new Object[0]));
        List<Map.Entry<String, ConditionEvaluationReport.ConditionAndOutcomes>> matched = shortOutcomes.entrySet().stream().filter(entry -> {
            return ((ConditionEvaluationReport.ConditionAndOutcomes) entry.getValue()).isFullMatch();
        }).toList();
        if (matched.isEmpty()) {
            message.append(String.format("%n    None%n", new Object[0]));
        } else {
            matched.forEach(entry2 -> {
                addMatchLogMessage(message, (String) entry2.getKey(), (ConditionEvaluationReport.ConditionAndOutcomes) entry2.getValue());
            });
        }
        message.append(String.format("%n%n", new Object[0]));
    }

    private void logNegativeMatches(StringBuilder message, Map<String, ConditionEvaluationReport.ConditionAndOutcomes> shortOutcomes) {
        message.append(String.format("Negative matches:%n", new Object[0]));
        message.append(String.format("-----------------%n", new Object[0]));
        List<Map.Entry<String, ConditionEvaluationReport.ConditionAndOutcomes>> nonMatched = shortOutcomes.entrySet().stream().filter(entry -> {
            return !((ConditionEvaluationReport.ConditionAndOutcomes) entry.getValue()).isFullMatch();
        }).toList();
        if (nonMatched.isEmpty()) {
            message.append(String.format("%n    None%n", new Object[0]));
        } else {
            nonMatched.forEach(entry2 -> {
                addNonMatchLogMessage(message, (String) entry2.getKey(), (ConditionEvaluationReport.ConditionAndOutcomes) entry2.getValue());
            });
        }
        message.append(String.format("%n%n", new Object[0]));
    }

    private void logExclusions(ConditionEvaluationReport report, StringBuilder message) {
        message.append(String.format("Exclusions:%n", new Object[0]));
        message.append(String.format("-----------%n", new Object[0]));
        if (report.getExclusions().isEmpty()) {
            message.append(String.format("%n    None%n", new Object[0]));
        } else {
            for (String exclusion : report.getExclusions()) {
                message.append(String.format("%n    %s%n", exclusion));
            }
        }
        message.append(String.format("%n%n", new Object[0]));
    }

    private void logUnconditionalClasses(ConditionEvaluationReport report, StringBuilder message) {
        message.append(String.format("Unconditional classes:%n", new Object[0]));
        message.append(String.format("----------------------%n", new Object[0]));
        if (report.getUnconditionalClasses().isEmpty()) {
            message.append(String.format("%n    None%n", new Object[0]));
            return;
        }
        for (String unconditionalClass : report.getUnconditionalClasses()) {
            message.append(String.format("%n    %s%n", unconditionalClass));
        }
    }

    private Map<String, ConditionEvaluationReport.ConditionAndOutcomes> orderByName(Map<String, ConditionEvaluationReport.ConditionAndOutcomes> outcomes) {
        MultiValueMap<String, String> map = mapToFullyQualifiedNames(outcomes.keySet());
        List<String> shortNames = new ArrayList<>(map.keySet());
        Collections.sort(shortNames);
        Map<String, ConditionEvaluationReport.ConditionAndOutcomes> result = new LinkedHashMap<>();
        for (String shortName : shortNames) {
            List<String> fullyQualifiedNames = (List) map.get(shortName);
            if (fullyQualifiedNames.size() > 1) {
                fullyQualifiedNames.forEach(fullyQualifiedName -> {
                    result.put(fullyQualifiedName, (ConditionEvaluationReport.ConditionAndOutcomes) outcomes.get(fullyQualifiedName));
                });
            } else {
                result.put(shortName, outcomes.get(fullyQualifiedNames.get(0)));
            }
        }
        return result;
    }

    private MultiValueMap<String, String> mapToFullyQualifiedNames(Set<String> keySet) {
        LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        keySet.forEach(fullyQualifiedName -> {
            map.add(ClassUtils.getShortName(fullyQualifiedName), fullyQualifiedName);
        });
        return map;
    }

    private void addMatchLogMessage(StringBuilder message, String source, ConditionEvaluationReport.ConditionAndOutcomes matches) {
        message.append(String.format("%n   %s matched:%n", source));
        Iterator<ConditionEvaluationReport.ConditionAndOutcome> it = matches.iterator();
        while (it.hasNext()) {
            ConditionEvaluationReport.ConditionAndOutcome match = it.next();
            logConditionAndOutcome(message, "      ", match);
        }
    }

    private void addNonMatchLogMessage(StringBuilder message, String source, ConditionEvaluationReport.ConditionAndOutcomes conditionAndOutcomes) {
        message.append(String.format("%n   %s:%n", source));
        List<ConditionEvaluationReport.ConditionAndOutcome> matches = new ArrayList<>();
        List<ConditionEvaluationReport.ConditionAndOutcome> nonMatches = new ArrayList<>();
        Iterator<ConditionEvaluationReport.ConditionAndOutcome> it = conditionAndOutcomes.iterator();
        while (it.hasNext()) {
            ConditionEvaluationReport.ConditionAndOutcome conditionAndOutcome = it.next();
            if (conditionAndOutcome.getOutcome().isMatch()) {
                matches.add(conditionAndOutcome);
            } else {
                nonMatches.add(conditionAndOutcome);
            }
        }
        message.append(String.format("      Did not match:%n", new Object[0]));
        for (ConditionEvaluationReport.ConditionAndOutcome nonMatch : nonMatches) {
            logConditionAndOutcome(message, "         ", nonMatch);
        }
        if (!matches.isEmpty()) {
            message.append(String.format("      Matched:%n", new Object[0]));
            for (ConditionEvaluationReport.ConditionAndOutcome match : matches) {
                logConditionAndOutcome(message, "         ", match);
            }
        }
    }

    private void logConditionAndOutcome(StringBuilder message, String indent, ConditionEvaluationReport.ConditionAndOutcome conditionAndOutcome) {
        message.append(String.format("%s- ", indent));
        String outcomeMessage = conditionAndOutcome.getOutcome().getMessage();
        if (StringUtils.hasLength(outcomeMessage)) {
            message.append(outcomeMessage);
        } else {
            message.append(conditionAndOutcome.getOutcome().isMatch() ? "matched" : "did not match");
        }
        message.append(" (");
        message.append(ClassUtils.getShortName(conditionAndOutcome.getCondition().getClass()));
        message.append(String.format(")%n", new Object[0]));
    }

    public String toString() {
        return this.message.toString();
    }
}
