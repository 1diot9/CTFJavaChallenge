package com.fasterxml.jackson.databind.util;

import java.io.Serializable;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/util/NameTransformer.class */
public abstract class NameTransformer {
    public static final NameTransformer NOP = new NopTransformer();

    public abstract String transform(String str);

    public abstract String reverse(String str);

    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/util/NameTransformer$NopTransformer.class */
    protected static final class NopTransformer extends NameTransformer implements Serializable {
        private static final long serialVersionUID = 1;

        protected NopTransformer() {
        }

        @Override // com.fasterxml.jackson.databind.util.NameTransformer
        public String transform(String name) {
            return name;
        }

        @Override // com.fasterxml.jackson.databind.util.NameTransformer
        public String reverse(String transformed) {
            return transformed;
        }
    }

    protected NameTransformer() {
    }

    public static NameTransformer simpleTransformer(final String prefix, final String suffix) {
        boolean hasPrefix = (prefix == null || prefix.isEmpty()) ? false : true;
        boolean hasSuffix = (suffix == null || suffix.isEmpty()) ? false : true;
        if (hasPrefix) {
            if (hasSuffix) {
                return new NameTransformer() { // from class: com.fasterxml.jackson.databind.util.NameTransformer.1
                    @Override // com.fasterxml.jackson.databind.util.NameTransformer
                    public String transform(String name) {
                        return prefix + name + suffix;
                    }

                    @Override // com.fasterxml.jackson.databind.util.NameTransformer
                    public String reverse(String transformed) {
                        if (transformed.startsWith(prefix)) {
                            String str = transformed.substring(prefix.length());
                            if (str.endsWith(suffix)) {
                                return str.substring(0, str.length() - suffix.length());
                            }
                            return null;
                        }
                        return null;
                    }

                    public String toString() {
                        return "[PreAndSuffixTransformer('" + prefix + "','" + suffix + "')]";
                    }
                };
            }
            return new NameTransformer() { // from class: com.fasterxml.jackson.databind.util.NameTransformer.2
                @Override // com.fasterxml.jackson.databind.util.NameTransformer
                public String transform(String name) {
                    return prefix + name;
                }

                @Override // com.fasterxml.jackson.databind.util.NameTransformer
                public String reverse(String transformed) {
                    if (transformed.startsWith(prefix)) {
                        return transformed.substring(prefix.length());
                    }
                    return null;
                }

                public String toString() {
                    return "[PrefixTransformer('" + prefix + "')]";
                }
            };
        }
        if (hasSuffix) {
            return new NameTransformer() { // from class: com.fasterxml.jackson.databind.util.NameTransformer.3
                @Override // com.fasterxml.jackson.databind.util.NameTransformer
                public String transform(String name) {
                    return name + suffix;
                }

                @Override // com.fasterxml.jackson.databind.util.NameTransformer
                public String reverse(String transformed) {
                    if (transformed.endsWith(suffix)) {
                        return transformed.substring(0, transformed.length() - suffix.length());
                    }
                    return null;
                }

                public String toString() {
                    return "[SuffixTransformer('" + suffix + "')]";
                }
            };
        }
        return NOP;
    }

    public static NameTransformer chainedTransformer(NameTransformer t1, NameTransformer t2) {
        return new Chained(t1, t2);
    }

    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/util/NameTransformer$Chained.class */
    public static class Chained extends NameTransformer implements Serializable {
        private static final long serialVersionUID = 1;
        protected final NameTransformer _t1;
        protected final NameTransformer _t2;

        public Chained(NameTransformer t1, NameTransformer t2) {
            this._t1 = t1;
            this._t2 = t2;
        }

        @Override // com.fasterxml.jackson.databind.util.NameTransformer
        public String transform(String name) {
            return this._t1.transform(this._t2.transform(name));
        }

        @Override // com.fasterxml.jackson.databind.util.NameTransformer
        public String reverse(String transformed) {
            String transformed2 = this._t1.reverse(transformed);
            if (transformed2 != null) {
                transformed2 = this._t2.reverse(transformed2);
            }
            return transformed2;
        }

        public String toString() {
            return "[ChainedTransformer(" + this._t1 + ", " + this._t2 + ")]";
        }
    }
}
