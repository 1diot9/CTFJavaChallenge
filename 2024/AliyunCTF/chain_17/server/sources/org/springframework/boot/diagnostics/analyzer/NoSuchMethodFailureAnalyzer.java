package org.springframework.boot.diagnostics.analyzer;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.util.ClassUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/diagnostics/analyzer/NoSuchMethodFailureAnalyzer.class */
class NoSuchMethodFailureAnalyzer extends AbstractFailureAnalyzer<NoSuchMethodError> {
    NoSuchMethodFailureAnalyzer() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.boot.diagnostics.AbstractFailureAnalyzer
    public FailureAnalysis analyze(Throwable rootFailure, NoSuchMethodError cause) {
        NoSuchMethodDescriptor calledDescriptor;
        NoSuchMethodDescriptor callerDescriptor = getCallerMethodDescriptor(cause);
        if (callerDescriptor == null || (calledDescriptor = getNoSuchMethodDescriptor(cause.getMessage())) == null) {
            return null;
        }
        String description = getDescription(callerDescriptor, calledDescriptor);
        String action = getAction(callerDescriptor, calledDescriptor);
        return new FailureAnalysis(description, action, cause);
    }

    private NoSuchMethodDescriptor getCallerMethodDescriptor(NoSuchMethodError cause) {
        StackTraceElement firstStackTraceElement = cause.getStackTrace()[0];
        String message = firstStackTraceElement.toString();
        String className = firstStackTraceElement.getClassName();
        return getDescriptorForClass(message, className);
    }

    protected NoSuchMethodDescriptor getNoSuchMethodDescriptor(String cause) {
        String message = cleanMessage(cause);
        String className = extractClassName(message);
        return getDescriptorForClass(message, className);
    }

    private NoSuchMethodDescriptor getDescriptorForClass(String message, String className) {
        List<URL> candidates;
        Class<?> type;
        List<ClassDescriptor> typeHierarchy;
        if (className == null || (candidates = findCandidates(className)) == null || (type = load(className)) == null || (typeHierarchy = getTypeHierarchy(type)) == null) {
            return null;
        }
        return new NoSuchMethodDescriptor(message, className, candidates, typeHierarchy);
    }

    private String cleanMessage(String message) {
        int loadedFromIndex = message.indexOf(" (loaded from");
        if (loadedFromIndex == -1) {
            return message;
        }
        return message.substring(0, loadedFromIndex);
    }

    private String extractClassName(String message) {
        String classAndMethodName;
        int methodNameIndex;
        if (message.startsWith("'") && message.endsWith("'")) {
            int splitIndex = message.indexOf(32);
            if (splitIndex == -1) {
                return null;
            }
            message = message.substring(splitIndex + 1);
        }
        int descriptorIndex = message.indexOf(40);
        if (descriptorIndex == -1 || (methodNameIndex = (classAndMethodName = message.substring(0, descriptorIndex)).lastIndexOf(46)) == -1) {
            return null;
        }
        String className = classAndMethodName.substring(0, methodNameIndex);
        return className.replace('/', '.');
    }

    private List<URL> findCandidates(String className) {
        try {
            return Collections.list(NoSuchMethodFailureAnalyzer.class.getClassLoader().getResources(ClassUtils.convertClassNameToResourcePath(className) + ".class"));
        } catch (Throwable th) {
            return null;
        }
    }

    private Class<?> load(String className) {
        try {
            return Class.forName(className, false, getClass().getClassLoader());
        } catch (Throwable th) {
            return null;
        }
    }

    private List<ClassDescriptor> getTypeHierarchy(Class<?> type) {
        try {
            List<ClassDescriptor> typeHierarchy = new ArrayList<>();
            while (type != null) {
                if (type.equals(Object.class)) {
                    break;
                }
                typeHierarchy.add(new ClassDescriptor(type.getCanonicalName(), type.getProtectionDomain().getCodeSource().getLocation()));
                type = type.getSuperclass();
            }
            return typeHierarchy;
        } catch (Throwable th) {
            return null;
        }
    }

    private String getDescription(NoSuchMethodDescriptor callerDescriptor, NoSuchMethodDescriptor calledDescriptor) {
        StringWriter description = new StringWriter();
        PrintWriter writer = new PrintWriter(description);
        writer.println("An attempt was made to call a method that does not exist. The attempt was made from the following location:");
        writer.println();
        writer.printf("    %s%n", callerDescriptor.getErrorMessage());
        writer.println();
        writer.println("The following method did not exist:");
        writer.println();
        writer.printf("    %s%n", calledDescriptor.getErrorMessage());
        writer.println();
        if (callerDescriptor.getCandidateLocations().size() > 1) {
            writer.printf("The calling method's class, %s, is available from the following locations:%n", callerDescriptor.getClassName());
            writer.println();
            for (URL candidate : callerDescriptor.getCandidateLocations()) {
                writer.printf("    %s%n", candidate);
            }
            writer.println();
            writer.println("The calling method's class was loaded from the following location:");
            writer.println();
            writer.printf("    %s%n", callerDescriptor.getTypeHierarchy().get(0).getLocation());
        } else {
            writer.printf("The calling method's class, %s, was loaded from the following location:%n", callerDescriptor.getClassName());
            writer.println();
            writer.printf("    %s%n", callerDescriptor.getCandidateLocations().get(0));
        }
        writer.println();
        writer.printf("The called method's class, %s, is available from the following locations:%n", calledDescriptor.getClassName());
        writer.println();
        for (URL candidate2 : calledDescriptor.getCandidateLocations()) {
            writer.printf("    %s%n", candidate2);
        }
        writer.println();
        writer.println("The called method's class hierarchy was loaded from the following locations:");
        writer.println();
        for (ClassDescriptor type : calledDescriptor.getTypeHierarchy()) {
            writer.printf("    %s: %s%n", type.getName(), type.getLocation());
        }
        return description.toString();
    }

    private String getAction(NoSuchMethodDescriptor callerDescriptor, NoSuchMethodDescriptor calledDescriptor) {
        if (callerDescriptor.getClassName().equals(calledDescriptor.getClassName())) {
            return "Correct the classpath of your application so that it contains a single, compatible version of " + calledDescriptor.getClassName();
        }
        return "Correct the classpath of your application so that it contains compatible versions of the classes " + callerDescriptor.getClassName() + " and " + calledDescriptor.getClassName();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/diagnostics/analyzer/NoSuchMethodFailureAnalyzer$NoSuchMethodDescriptor.class */
    public static class NoSuchMethodDescriptor {
        private final String errorMessage;
        private final String className;
        private final List<URL> candidateLocations;
        private final List<ClassDescriptor> typeHierarchy;

        public NoSuchMethodDescriptor(String errorMessage, String className, List<URL> candidateLocations, List<ClassDescriptor> typeHierarchy) {
            this.errorMessage = errorMessage;
            this.className = className;
            this.candidateLocations = candidateLocations;
            this.typeHierarchy = typeHierarchy;
        }

        public String getErrorMessage() {
            return this.errorMessage;
        }

        public String getClassName() {
            return this.className;
        }

        public List<URL> getCandidateLocations() {
            return this.candidateLocations;
        }

        public List<ClassDescriptor> getTypeHierarchy() {
            return this.typeHierarchy;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/diagnostics/analyzer/NoSuchMethodFailureAnalyzer$ClassDescriptor.class */
    public static class ClassDescriptor {
        private final String name;
        private final URL location;

        public ClassDescriptor(String name, URL location) {
            this.name = name;
            this.location = location;
        }

        public String getName() {
            return this.name;
        }

        public URL getLocation() {
            return this.location;
        }
    }
}
