package org.springframework.boot.context.properties.source;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/source/ConfigurationPropertyName.class */
public final class ConfigurationPropertyName implements Comparable<ConfigurationPropertyName> {
    private static final String EMPTY_STRING = "";
    public static final ConfigurationPropertyName EMPTY = new ConfigurationPropertyName(Elements.EMPTY);
    private final Elements elements;
    private final CharSequence[] uniformElements;
    private String string;
    private int hashCode;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/source/ConfigurationPropertyName$ElementCharPredicate.class */
    public interface ElementCharPredicate {
        boolean test(char ch2, int index);
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/source/ConfigurationPropertyName$Form.class */
    public enum Form {
        ORIGINAL,
        DASHED,
        UNIFORM
    }

    private ConfigurationPropertyName(Elements elements) {
        this.elements = elements;
        this.uniformElements = new CharSequence[elements.getSize()];
    }

    public boolean isEmpty() {
        return this.elements.getSize() == 0;
    }

    public boolean isLastElementIndexed() {
        int size = getNumberOfElements();
        return size > 0 && isIndexed(size - 1);
    }

    public boolean hasIndexedElement() {
        for (int i = 0; i < getNumberOfElements(); i++) {
            if (isIndexed(i)) {
                return true;
            }
        }
        return false;
    }

    boolean isIndexed(int elementIndex) {
        return this.elements.getType(elementIndex).isIndexed();
    }

    public boolean isNumericIndex(int elementIndex) {
        return this.elements.getType(elementIndex) == ElementType.NUMERICALLY_INDEXED;
    }

    public String getLastElement(Form form) {
        int size = getNumberOfElements();
        return size != 0 ? getElement(size - 1, form) : "";
    }

    public String getElement(int elementIndex, Form form) {
        CharSequence element = this.elements.get(elementIndex);
        ElementType type = this.elements.getType(elementIndex);
        if (type.isIndexed()) {
            return element.toString();
        }
        if (form == Form.ORIGINAL) {
            if (type != ElementType.NON_UNIFORM) {
                return element.toString();
            }
            return convertToOriginalForm(element).toString();
        }
        if (form == Form.DASHED) {
            if (type == ElementType.UNIFORM || type == ElementType.DASHED) {
                return element.toString();
            }
            return convertToDashedElement(element).toString();
        }
        CharSequence uniformElement = this.uniformElements[elementIndex];
        if (uniformElement == null) {
            uniformElement = type != ElementType.UNIFORM ? convertToUniformElement(element) : element;
            this.uniformElements[elementIndex] = uniformElement.toString();
        }
        return uniformElement.toString();
    }

    private CharSequence convertToOriginalForm(CharSequence element) {
        return convertElement(element, false, (ch2, i) -> {
            return ch2 == '_' || ElementsParser.isValidChar(Character.toLowerCase(ch2), i);
        });
    }

    private CharSequence convertToDashedElement(CharSequence element) {
        return convertElement(element, true, ElementsParser::isValidChar);
    }

    private CharSequence convertToUniformElement(CharSequence element) {
        return convertElement(element, true, (ch2, i) -> {
            return ElementsParser.isAlphaNumeric(ch2);
        });
    }

    private CharSequence convertElement(CharSequence element, boolean lowercase, ElementCharPredicate filter) {
        StringBuilder result = new StringBuilder(element.length());
        for (int i = 0; i < element.length(); i++) {
            char ch2 = lowercase ? Character.toLowerCase(element.charAt(i)) : element.charAt(i);
            if (filter.test(ch2, i)) {
                result.append(ch2);
            }
        }
        return result;
    }

    public int getNumberOfElements() {
        return this.elements.getSize();
    }

    public ConfigurationPropertyName append(String suffix) {
        if (!StringUtils.hasLength(suffix)) {
            return this;
        }
        Elements additionalElements = probablySingleElementOf(suffix);
        return new ConfigurationPropertyName(this.elements.append(additionalElements));
    }

    public ConfigurationPropertyName append(ConfigurationPropertyName suffix) {
        if (suffix == null) {
            return this;
        }
        return new ConfigurationPropertyName(this.elements.append(suffix.elements));
    }

    public ConfigurationPropertyName getParent() {
        int numberOfElements = getNumberOfElements();
        return numberOfElements <= 1 ? EMPTY : chop(numberOfElements - 1);
    }

    public ConfigurationPropertyName chop(int size) {
        if (size >= getNumberOfElements()) {
            return this;
        }
        return new ConfigurationPropertyName(this.elements.chop(size));
    }

    public ConfigurationPropertyName subName(int offset) {
        if (offset == 0) {
            return this;
        }
        if (offset == getNumberOfElements()) {
            return EMPTY;
        }
        if (offset < 0 || offset > getNumberOfElements()) {
            throw new IndexOutOfBoundsException("Offset: " + offset + ", NumberOfElements: " + getNumberOfElements());
        }
        return new ConfigurationPropertyName(this.elements.subElements(offset));
    }

    public boolean isParentOf(ConfigurationPropertyName name) {
        Assert.notNull(name, "Name must not be null");
        if (getNumberOfElements() != name.getNumberOfElements() - 1) {
            return false;
        }
        return isAncestorOf(name);
    }

    public boolean isAncestorOf(ConfigurationPropertyName name) {
        Assert.notNull(name, "Name must not be null");
        if (getNumberOfElements() >= name.getNumberOfElements()) {
            return false;
        }
        return elementsEqual(name);
    }

    @Override // java.lang.Comparable
    public int compareTo(ConfigurationPropertyName other) {
        return compare(this, other);
    }

    private int compare(ConfigurationPropertyName n1, ConfigurationPropertyName n2) {
        ElementType type;
        String str;
        String str2;
        int result;
        int l1 = n1.getNumberOfElements();
        int l2 = n2.getNumberOfElements();
        int i1 = 0;
        int i2 = 0;
        do {
            if (i1 < l1 || i2 < l2) {
                if (i1 < l1) {
                    try {
                        type = n1.elements.getType(i1);
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        throw new RuntimeException(ex);
                    }
                } else {
                    type = null;
                }
                ElementType type1 = type;
                ElementType type2 = i2 < l2 ? n2.elements.getType(i2) : null;
                if (i1 < l1) {
                    int i = i1;
                    i1++;
                    str = n1.getElement(i, Form.UNIFORM);
                } else {
                    str = null;
                }
                String e1 = str;
                if (i2 < l2) {
                    int i3 = i2;
                    i2++;
                    str2 = n2.getElement(i3, Form.UNIFORM);
                } else {
                    str2 = null;
                }
                String e2 = str2;
                result = compare(e1, type1, e2, type2);
            } else {
                return 0;
            }
        } while (result == 0);
        return result;
    }

    private int compare(String e1, ElementType type1, String e2, ElementType type2) {
        if (e1 == null) {
            return -1;
        }
        if (e2 == null) {
            return 1;
        }
        int result = Boolean.compare(type2.isIndexed(), type1.isIndexed());
        if (result != 0) {
            return result;
        }
        if (type1 == ElementType.NUMERICALLY_INDEXED && type2 == ElementType.NUMERICALLY_INDEXED) {
            long v1 = Long.parseLong(e1);
            long v2 = Long.parseLong(e2);
            return Long.compare(v1, v2);
        }
        return e1.compareTo(e2);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        ConfigurationPropertyName other = (ConfigurationPropertyName) obj;
        if (getNumberOfElements() != other.getNumberOfElements()) {
            return false;
        }
        if (this.elements.canShortcutWithSource(ElementType.UNIFORM) && other.elements.canShortcutWithSource(ElementType.UNIFORM)) {
            return toString().equals(other.toString());
        }
        return elementsEqual(other);
    }

    private boolean elementsEqual(ConfigurationPropertyName name) {
        for (int i = this.elements.getSize() - 1; i >= 0; i--) {
            if (elementDiffers(this.elements, name.elements, i)) {
                return false;
            }
        }
        return true;
    }

    private boolean elementDiffers(Elements e1, Elements e2, int i) {
        ElementType type1 = e1.getType(i);
        ElementType type2 = e2.getType(i);
        return (type1.allowsFastEqualityCheck() && type2.allowsFastEqualityCheck()) ? !fastElementEquals(e1, e2, i) : (type1.allowsDashIgnoringEqualityCheck() && type2.allowsDashIgnoringEqualityCheck()) ? !dashIgnoringElementEquals(e1, e2, i) : !defaultElementEquals(e1, e2, i);
    }

    private boolean fastElementEquals(Elements e1, Elements e2, int i) {
        int length1 = e1.getLength(i);
        int length2 = e2.getLength(i);
        if (length1 == length2) {
            int i1 = 0;
            while (true) {
                int i2 = length1;
                length1--;
                if (i2 != 0) {
                    char ch1 = e1.charAt(i, i1);
                    char ch2 = e2.charAt(i, i1);
                    if (ch1 != ch2) {
                        return false;
                    }
                    i1++;
                } else {
                    return true;
                }
            }
        } else {
            return false;
        }
    }

    private boolean dashIgnoringElementEquals(Elements e1, Elements e2, int i) {
        int l1 = e1.getLength(i);
        int l2 = e2.getLength(i);
        int i1 = 0;
        int i2 = 0;
        while (i1 < l1) {
            if (i2 >= l2) {
                return remainderIsDashes(e1, i, i1);
            }
            char ch1 = e1.charAt(i, i1);
            char ch2 = e2.charAt(i, i2);
            if (ch1 == '-') {
                i1++;
            } else if (ch2 == '-') {
                i2++;
            } else {
                if (ch1 != ch2) {
                    return false;
                }
                i1++;
                i2++;
            }
        }
        if (i2 < l2) {
            if (e2.getType(i).isIndexed()) {
                return false;
            }
            do {
                int i3 = i2;
                i2++;
                if (e2.charAt(i, i3) != '-') {
                    return false;
                }
            } while (i2 < l2);
            return true;
        }
        return true;
    }

    private boolean defaultElementEquals(Elements e1, Elements e2, int i) {
        int l1 = e1.getLength(i);
        int l2 = e2.getLength(i);
        boolean indexed1 = e1.getType(i).isIndexed();
        boolean indexed2 = e2.getType(i).isIndexed();
        int i1 = 0;
        int i2 = 0;
        while (i1 < l1) {
            if (i2 >= l2) {
                return remainderIsNotAlphanumeric(e1, i, i1);
            }
            char ch1 = indexed1 ? e1.charAt(i, i1) : Character.toLowerCase(e1.charAt(i, i1));
            char ch2 = indexed2 ? e2.charAt(i, i2) : Character.toLowerCase(e2.charAt(i, i2));
            if (!indexed1 && !ElementsParser.isAlphaNumeric(ch1)) {
                i1++;
            } else if (!indexed2 && !ElementsParser.isAlphaNumeric(ch2)) {
                i2++;
            } else {
                if (ch1 != ch2) {
                    return false;
                }
                i1++;
                i2++;
            }
        }
        if (i2 < l2) {
            return remainderIsNotAlphanumeric(e2, i, i2);
        }
        return true;
    }

    private boolean remainderIsNotAlphanumeric(Elements elements, int element, int index) {
        if (elements.getType(element).isIndexed()) {
            return false;
        }
        int length = elements.getLength(element);
        do {
            int i = index;
            index++;
            char c = Character.toLowerCase(elements.charAt(element, i));
            if (ElementsParser.isAlphaNumeric(c)) {
                return false;
            }
        } while (index < length);
        return true;
    }

    private boolean remainderIsDashes(Elements elements, int element, int index) {
        if (elements.getType(element).isIndexed()) {
            return false;
        }
        int length = elements.getLength(element);
        do {
            int i = index;
            index++;
            char c = elements.charAt(element, i);
            if (c != '-') {
                return false;
            }
        } while (index < length);
        return true;
    }

    public int hashCode() {
        int hashCode = this.hashCode;
        Elements elements = this.elements;
        if (hashCode == 0 && elements.getSize() != 0) {
            for (int elementIndex = 0; elementIndex < elements.getSize(); elementIndex++) {
                int elementHashCode = 0;
                boolean indexed = elements.getType(elementIndex).isIndexed();
                int length = elements.getLength(elementIndex);
                for (int i = 0; i < length; i++) {
                    char ch2 = elements.charAt(elementIndex, i);
                    if (!indexed) {
                        ch2 = Character.toLowerCase(ch2);
                    }
                    if (ElementsParser.isAlphaNumeric(ch2)) {
                        elementHashCode = (31 * elementHashCode) + ch2;
                    }
                }
                hashCode = (31 * hashCode) + elementHashCode;
            }
            this.hashCode = hashCode;
        }
        return hashCode;
    }

    public String toString() {
        if (this.string == null) {
            this.string = buildToString();
        }
        return this.string;
    }

    private String buildToString() {
        if (this.elements.canShortcutWithSource(ElementType.UNIFORM, ElementType.DASHED)) {
            return this.elements.getSource().toString();
        }
        int elements = getNumberOfElements();
        StringBuilder result = new StringBuilder(elements * 8);
        for (int i = 0; i < elements; i++) {
            boolean indexed = isIndexed(i);
            if (!result.isEmpty() && !indexed) {
                result.append('.');
            }
            if (indexed) {
                result.append('[');
                result.append(getElement(i, Form.ORIGINAL));
                result.append(']');
            } else {
                result.append(getElement(i, Form.DASHED));
            }
        }
        return result.toString();
    }

    public static boolean isValid(CharSequence name) {
        return of(name, true) != null;
    }

    public static ConfigurationPropertyName of(CharSequence name) {
        return of(name, false);
    }

    public static ConfigurationPropertyName ofIfValid(CharSequence name) {
        return of(name, true);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ConfigurationPropertyName of(CharSequence name, boolean returnNullIfInvalid) {
        Elements elements = elementsOf(name, returnNullIfInvalid);
        if (elements != null) {
            return new ConfigurationPropertyName(elements);
        }
        return null;
    }

    private static Elements probablySingleElementOf(CharSequence name) {
        return elementsOf(name, false, 1);
    }

    private static Elements elementsOf(CharSequence name, boolean returnNullIfInvalid) {
        return elementsOf(name, returnNullIfInvalid, 6);
    }

    private static Elements elementsOf(CharSequence name, boolean returnNullIfInvalid, int parserCapacity) {
        if (name == null) {
            Assert.isTrue(returnNullIfInvalid, "Name must not be null");
            return null;
        }
        if (name.isEmpty()) {
            return Elements.EMPTY;
        }
        if (name.charAt(0) == '.' || name.charAt(name.length() - 1) == '.') {
            if (returnNullIfInvalid) {
                return null;
            }
            throw new InvalidConfigurationPropertyNameException(name, Collections.singletonList('.'));
        }
        Elements elements = new ElementsParser(name, '.', parserCapacity).parse();
        for (int i = 0; i < elements.getSize(); i++) {
            if (elements.getType(i) == ElementType.NON_UNIFORM) {
                if (returnNullIfInvalid) {
                    return null;
                }
                throw new InvalidConfigurationPropertyNameException(name, getInvalidChars(elements, i));
            }
        }
        return elements;
    }

    private static List<Character> getInvalidChars(Elements elements, int index) {
        List<Character> invalidChars = new ArrayList<>();
        for (int charIndex = 0; charIndex < elements.getLength(index); charIndex++) {
            char ch2 = elements.charAt(index, charIndex);
            if (!ElementsParser.isValidChar(ch2, charIndex)) {
                invalidChars.add(Character.valueOf(ch2));
            }
        }
        return invalidChars;
    }

    public static ConfigurationPropertyName adapt(CharSequence name, char separator) {
        return adapt(name, separator, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ConfigurationPropertyName adapt(CharSequence name, char separator, Function<CharSequence, CharSequence> elementValueProcessor) {
        Assert.notNull(name, "Name must not be null");
        if (name.isEmpty()) {
            return EMPTY;
        }
        Elements elements = new ElementsParser(name, separator).parse(elementValueProcessor);
        if (elements.getSize() == 0) {
            return EMPTY;
        }
        return new ConfigurationPropertyName(elements);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/source/ConfigurationPropertyName$Elements.class */
    public static class Elements {
        private static final int[] NO_POSITION = new int[0];
        private static final ElementType[] NO_TYPE = new ElementType[0];
        public static final Elements EMPTY = new Elements("", 0, NO_POSITION, NO_POSITION, NO_TYPE, null);
        private final CharSequence source;
        private final int size;
        private final int[] start;
        private final int[] end;
        private final ElementType[] type;
        private final CharSequence[] resolved;

        Elements(CharSequence source, int size, int[] start, int[] end, ElementType[] type, CharSequence[] resolved) {
            this.source = source;
            this.size = size;
            this.start = start;
            this.end = end;
            this.type = type;
            this.resolved = resolved;
        }

        Elements append(Elements additional) {
            int size = this.size + additional.size;
            ElementType[] type = new ElementType[size];
            System.arraycopy(this.type, 0, type, 0, this.size);
            System.arraycopy(additional.type, 0, type, this.size, additional.size);
            CharSequence[] resolved = newResolved(size);
            for (int i = 0; i < additional.size; i++) {
                resolved[this.size + i] = additional.get(i);
            }
            return new Elements(this.source, size, this.start, this.end, type, resolved);
        }

        Elements chop(int size) {
            CharSequence[] resolved = newResolved(size);
            return new Elements(this.source, size, this.start, this.end, this.type, resolved);
        }

        Elements subElements(int offset) {
            int size = this.size - offset;
            CharSequence[] resolved = newResolved(size);
            int[] start = new int[size];
            System.arraycopy(this.start, offset, start, 0, size);
            int[] end = new int[size];
            System.arraycopy(this.end, offset, end, 0, size);
            ElementType[] type = new ElementType[size];
            System.arraycopy(this.type, offset, type, 0, size);
            return new Elements(this.source, size, start, end, type, resolved);
        }

        private CharSequence[] newResolved(int size) {
            CharSequence[] resolved = new CharSequence[size];
            if (this.resolved != null) {
                System.arraycopy(this.resolved, 0, resolved, 0, Math.min(size, this.size));
            }
            return resolved;
        }

        int getSize() {
            return this.size;
        }

        CharSequence get(int index) {
            if (this.resolved != null && this.resolved[index] != null) {
                return this.resolved[index];
            }
            int start = this.start[index];
            int end = this.end[index];
            return this.source.subSequence(start, end);
        }

        int getLength(int index) {
            if (this.resolved != null && this.resolved[index] != null) {
                return this.resolved[index].length();
            }
            int start = this.start[index];
            int end = this.end[index];
            return end - start;
        }

        char charAt(int index, int charIndex) {
            if (this.resolved != null && this.resolved[index] != null) {
                return this.resolved[index].charAt(charIndex);
            }
            int start = this.start[index];
            return this.source.charAt(start + charIndex);
        }

        ElementType getType(int index) {
            return this.type[index];
        }

        CharSequence getSource() {
            return this.source;
        }

        boolean canShortcutWithSource(ElementType requiredType) {
            return canShortcutWithSource(requiredType, requiredType);
        }

        boolean canShortcutWithSource(ElementType requiredType, ElementType alternativeType) {
            if (this.resolved != null) {
                return false;
            }
            for (int i = 0; i < this.size; i++) {
                ElementType type = this.type[i];
                if (type != requiredType && type != alternativeType) {
                    return false;
                }
                if (i > 0 && this.end[i - 1] + 1 != this.start[i]) {
                    return false;
                }
            }
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/source/ConfigurationPropertyName$ElementsParser.class */
    public static class ElementsParser {
        private static final int DEFAULT_CAPACITY = 6;
        private final CharSequence source;
        private final char separator;
        private int size;
        private int[] start;
        private int[] end;
        private ElementType[] type;
        private CharSequence[] resolved;

        ElementsParser(CharSequence source, char separator) {
            this(source, separator, 6);
        }

        ElementsParser(CharSequence source, char separator, int capacity) {
            this.source = source;
            this.separator = separator;
            this.start = new int[capacity];
            this.end = new int[capacity];
            this.type = new ElementType[capacity];
        }

        Elements parse() {
            return parse(null);
        }

        Elements parse(Function<CharSequence, CharSequence> valueProcessor) {
            int length = this.source.length();
            int openBracketCount = 0;
            int start = 0;
            ElementType type = ElementType.EMPTY;
            for (int i = 0; i < length; i++) {
                char ch2 = this.source.charAt(i);
                if (ch2 == '[') {
                    if (openBracketCount == 0) {
                        add(start, i, type, valueProcessor);
                        start = i + 1;
                        type = ElementType.NUMERICALLY_INDEXED;
                    }
                    openBracketCount++;
                } else if (ch2 == ']') {
                    openBracketCount--;
                    if (openBracketCount == 0) {
                        add(start, i, type, valueProcessor);
                        start = i + 1;
                        type = ElementType.EMPTY;
                    }
                } else if (!type.isIndexed() && ch2 == this.separator) {
                    add(start, i, type, valueProcessor);
                    start = i + 1;
                    type = ElementType.EMPTY;
                } else {
                    type = updateType(type, ch2, i - start);
                }
            }
            if (openBracketCount != 0) {
                type = ElementType.NON_UNIFORM;
            }
            add(start, length, type, valueProcessor);
            return new Elements(this.source, this.size, this.start, this.end, this.type, this.resolved);
        }

        private ElementType updateType(ElementType existingType, char ch2, int index) {
            if (existingType.isIndexed()) {
                if (existingType == ElementType.NUMERICALLY_INDEXED && !isNumeric(ch2)) {
                    return ElementType.INDEXED;
                }
                return existingType;
            }
            if (existingType == ElementType.EMPTY && isValidChar(ch2, index)) {
                return index == 0 ? ElementType.UNIFORM : ElementType.NON_UNIFORM;
            }
            if (existingType == ElementType.UNIFORM && ch2 == '-') {
                return ElementType.DASHED;
            }
            if (!isValidChar(ch2, index)) {
                if (existingType == ElementType.EMPTY && !isValidChar(Character.toLowerCase(ch2), index)) {
                    return ElementType.EMPTY;
                }
                return ElementType.NON_UNIFORM;
            }
            return existingType;
        }

        private void add(int start, int end, ElementType type, Function<CharSequence, CharSequence> valueProcessor) {
            if (end - start < 1 || type == ElementType.EMPTY) {
                return;
            }
            if (this.start.length == this.size) {
                this.start = expand(this.start);
                this.end = expand(this.end);
                this.type = expand(this.type);
                this.resolved = expand(this.resolved);
            }
            if (valueProcessor != null) {
                if (this.resolved == null) {
                    this.resolved = new CharSequence[this.start.length];
                }
                CharSequence resolved = valueProcessor.apply(this.source.subSequence(start, end));
                Elements resolvedElements = new ElementsParser(resolved, '.').parse();
                Assert.state(resolvedElements.getSize() == 1, "Resolved element must not contain multiple elements");
                this.resolved[this.size] = resolvedElements.get(0);
                type = resolvedElements.getType(0);
            }
            this.start[this.size] = start;
            this.end[this.size] = end;
            this.type[this.size] = type;
            this.size++;
        }

        private int[] expand(int[] src) {
            int[] dest = new int[src.length + 6];
            System.arraycopy(src, 0, dest, 0, src.length);
            return dest;
        }

        private ElementType[] expand(ElementType[] src) {
            ElementType[] dest = new ElementType[src.length + 6];
            System.arraycopy(src, 0, dest, 0, src.length);
            return dest;
        }

        private CharSequence[] expand(CharSequence[] src) {
            if (src == null) {
                return null;
            }
            CharSequence[] dest = new CharSequence[src.length + 6];
            System.arraycopy(src, 0, dest, 0, src.length);
            return dest;
        }

        static boolean isValidChar(char ch2, int index) {
            return isAlpha(ch2) || isNumeric(ch2) || (index != 0 && ch2 == '-');
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static boolean isAlphaNumeric(char ch2) {
            return isAlpha(ch2) || isNumeric(ch2);
        }

        private static boolean isAlpha(char ch2) {
            return ch2 >= 'a' && ch2 <= 'z';
        }

        private static boolean isNumeric(char ch2) {
            return ch2 >= '0' && ch2 <= '9';
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/properties/source/ConfigurationPropertyName$ElementType.class */
    public enum ElementType {
        EMPTY(false),
        UNIFORM(false),
        DASHED(false),
        NON_UNIFORM(false),
        INDEXED(true),
        NUMERICALLY_INDEXED(true);

        private final boolean indexed;

        ElementType(boolean indexed) {
            this.indexed = indexed;
        }

        public boolean isIndexed() {
            return this.indexed;
        }

        public boolean allowsFastEqualityCheck() {
            return this == UNIFORM || this == NUMERICALLY_INDEXED;
        }

        public boolean allowsDashIgnoringEqualityCheck() {
            return allowsFastEqualityCheck() || this == DASHED;
        }
    }
}
