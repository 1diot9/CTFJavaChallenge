package org.springframework.http.codec.xml;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSchema;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import javax.xml.namespace.QName;
import javax.xml.stream.events.XMLEvent;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SynchronousSink;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/xml/Jaxb2Helper.class */
abstract class Jaxb2Helper {
    private static final String JAXB_DEFAULT_ANNOTATION_VALUE = "##default";

    Jaxb2Helper() {
    }

    public static Set<QName> toQNames(Class<?> clazz) {
        Set<QName> result = new HashSet<>(1);
        findQNames(clazz, result, new HashSet());
        return result;
    }

    private static void findQNames(Class<?> clazz, Set<QName> qNames, Set<Class<?>> completedClasses) {
        if (completedClasses.contains(clazz)) {
            return;
        }
        if (clazz.isAnnotationPresent(XmlRootElement.class)) {
            XmlRootElement annotation = clazz.getAnnotation(XmlRootElement.class);
            qNames.add(new QName(namespace(annotation.namespace(), clazz), localPart(annotation.name(), clazz)));
        } else if (clazz.isAnnotationPresent(XmlType.class)) {
            XmlType annotation2 = clazz.getAnnotation(XmlType.class);
            qNames.add(new QName(namespace(annotation2.namespace(), clazz), localPart(annotation2.name(), clazz)));
        } else {
            throw new IllegalArgumentException("Output class [" + clazz.getName() + "] is neither annotated with @XmlRootElement nor @XmlType");
        }
        completedClasses.add(clazz);
        if (clazz.isAnnotationPresent(XmlSeeAlso.class)) {
            for (Class<?> seeAlso : clazz.getAnnotation(XmlSeeAlso.class).value()) {
                findQNames(seeAlso, qNames, completedClasses);
            }
        }
    }

    private static String localPart(String value, Class<?> outputClass) {
        if ("##default".equals(value)) {
            return ClassUtils.getShortNameAsProperty(outputClass);
        }
        return value;
    }

    private static String namespace(String value, Class<?> outputClass) {
        if ("##default".equals(value)) {
            Package outputClassPackage = outputClass.getPackage();
            if (outputClassPackage != null && outputClassPackage.isAnnotationPresent(XmlSchema.class)) {
                XmlSchema annotation = outputClassPackage.getAnnotation(XmlSchema.class);
                return annotation.namespace();
            }
            return "";
        }
        return value;
    }

    public static Flux<List<XMLEvent>> split(Flux<XMLEvent> xmlEventFlux, Set<QName> names) {
        return xmlEventFlux.handle(new SplitHandler(names));
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/xml/Jaxb2Helper$SplitHandler.class */
    private static class SplitHandler implements BiConsumer<XMLEvent, SynchronousSink<List<XMLEvent>>> {
        private final Set<QName> names;

        @Nullable
        private List<XMLEvent> events;
        private int elementDepth = 0;
        private int barrier = Integer.MAX_VALUE;

        public SplitHandler(Set<QName> names) {
            this.names = names;
        }

        @Override // java.util.function.BiConsumer
        public void accept(XMLEvent event, SynchronousSink<List<XMLEvent>> sink) {
            if (event.isStartElement()) {
                if (this.barrier == Integer.MAX_VALUE) {
                    QName startElementName = event.asStartElement().getName();
                    if (this.names.contains(startElementName)) {
                        this.events = new ArrayList();
                        this.barrier = this.elementDepth;
                    }
                }
                this.elementDepth++;
            }
            if (this.elementDepth > this.barrier) {
                Assert.state(this.events != null, "No XMLEvent List");
                this.events.add(event);
            }
            if (event.isEndElement()) {
                this.elementDepth--;
                if (this.elementDepth == this.barrier) {
                    this.barrier = Integer.MAX_VALUE;
                    Assert.state(this.events != null, "No XMLEvent List");
                    sink.next(this.events);
                }
            }
        }
    }
}
