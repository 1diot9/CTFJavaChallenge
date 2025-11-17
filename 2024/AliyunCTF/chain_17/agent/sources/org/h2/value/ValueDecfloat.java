package org.h2.value;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.h2.api.ErrorCode;
import org.h2.engine.CastDataProvider;
import org.h2.message.DbException;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/value/ValueDecfloat.class */
public final class ValueDecfloat extends ValueBigDecimalBase {
    public static final ValueDecfloat ZERO = new ValueDecfloat(BigDecimal.ZERO);
    public static final ValueDecfloat ONE = new ValueDecfloat(BigDecimal.ONE);
    public static final ValueDecfloat POSITIVE_INFINITY = new ValueDecfloat(null);
    public static final ValueDecfloat NEGATIVE_INFINITY = new ValueDecfloat(null);
    public static final ValueDecfloat NAN = new ValueDecfloat(null);

    private ValueDecfloat(BigDecimal bigDecimal) {
        super(bigDecimal);
    }

    @Override // org.h2.value.Value
    public String getString() {
        if (this.value == null) {
            if (this == POSITIVE_INFINITY) {
                return "Infinity";
            }
            if (this == NEGATIVE_INFINITY) {
                return "-Infinity";
            }
            return "NaN";
        }
        return this.value.toString();
    }

    @Override // org.h2.util.HasSQL
    public StringBuilder getSQL(StringBuilder sb, int i) {
        if ((i & 4) == 0) {
            return getSQL(sb.append("CAST(")).append(" AS DECFLOAT)");
        }
        return getSQL(sb);
    }

    private StringBuilder getSQL(StringBuilder sb) {
        if (this.value != null) {
            return sb.append(this.value);
        }
        if (this == POSITIVE_INFINITY) {
            return sb.append("'Infinity'");
        }
        if (this == NEGATIVE_INFINITY) {
            return sb.append("'-Infinity'");
        }
        return sb.append("'NaN'");
    }

    @Override // org.h2.value.Value, org.h2.value.Typed
    public TypeInfo getType() {
        TypeInfo typeInfo = this.type;
        if (typeInfo == null) {
            TypeInfo typeInfo2 = new TypeInfo(16, this.value != null ? this.value.precision() : 1L, 0, null);
            typeInfo = typeInfo2;
            this.type = typeInfo2;
        }
        return typeInfo;
    }

    @Override // org.h2.value.Value
    public int getValueType() {
        return 16;
    }

    @Override // org.h2.value.Value
    public Value add(Value value) {
        BigDecimal bigDecimal = ((ValueDecfloat) value).value;
        if (this.value != null) {
            if (bigDecimal != null) {
                return get(this.value.add(bigDecimal));
            }
            return value;
        }
        if (bigDecimal != null || this == value) {
            return this;
        }
        return NAN;
    }

    @Override // org.h2.value.Value
    public Value subtract(Value value) {
        BigDecimal bigDecimal = ((ValueDecfloat) value).value;
        if (this.value != null) {
            if (bigDecimal != null) {
                return get(this.value.subtract(bigDecimal));
            }
            return value == POSITIVE_INFINITY ? NEGATIVE_INFINITY : value == NEGATIVE_INFINITY ? POSITIVE_INFINITY : NAN;
        }
        if (bigDecimal != null) {
            return this;
        }
        if (this == POSITIVE_INFINITY) {
            if (value == NEGATIVE_INFINITY) {
                return POSITIVE_INFINITY;
            }
        } else if (this == NEGATIVE_INFINITY && value == POSITIVE_INFINITY) {
            return NEGATIVE_INFINITY;
        }
        return NAN;
    }

    @Override // org.h2.value.Value
    public Value negate() {
        if (this.value != null) {
            return get(this.value.negate());
        }
        return this == POSITIVE_INFINITY ? NEGATIVE_INFINITY : this == NEGATIVE_INFINITY ? POSITIVE_INFINITY : NAN;
    }

    @Override // org.h2.value.Value
    public Value multiply(Value value) {
        BigDecimal bigDecimal = ((ValueDecfloat) value).value;
        if (this.value != null) {
            if (bigDecimal != null) {
                return get(this.value.multiply(bigDecimal));
            }
            if (value == POSITIVE_INFINITY) {
                int signum = this.value.signum();
                if (signum > 0) {
                    return POSITIVE_INFINITY;
                }
                if (signum < 0) {
                    return NEGATIVE_INFINITY;
                }
            } else if (value == NEGATIVE_INFINITY) {
                int signum2 = this.value.signum();
                if (signum2 > 0) {
                    return NEGATIVE_INFINITY;
                }
                if (signum2 < 0) {
                    return POSITIVE_INFINITY;
                }
            }
        } else if (bigDecimal != null) {
            if (this == POSITIVE_INFINITY) {
                int signum3 = bigDecimal.signum();
                if (signum3 > 0) {
                    return POSITIVE_INFINITY;
                }
                if (signum3 < 0) {
                    return NEGATIVE_INFINITY;
                }
            } else if (this == NEGATIVE_INFINITY) {
                int signum4 = bigDecimal.signum();
                if (signum4 > 0) {
                    return NEGATIVE_INFINITY;
                }
                if (signum4 < 0) {
                    return POSITIVE_INFINITY;
                }
            }
        } else if (this == POSITIVE_INFINITY) {
            if (value == POSITIVE_INFINITY) {
                return POSITIVE_INFINITY;
            }
            if (value == NEGATIVE_INFINITY) {
                return NEGATIVE_INFINITY;
            }
        } else if (this == NEGATIVE_INFINITY) {
            if (value == POSITIVE_INFINITY) {
                return NEGATIVE_INFINITY;
            }
            if (value == NEGATIVE_INFINITY) {
                return POSITIVE_INFINITY;
            }
        }
        return NAN;
    }

    @Override // org.h2.value.Value
    public Value divide(Value value, TypeInfo typeInfo) {
        BigDecimal bigDecimal = ((ValueDecfloat) value).value;
        if (bigDecimal != null && bigDecimal.signum() == 0) {
            throw DbException.get(ErrorCode.DIVISION_BY_ZERO_1, getTraceSQL());
        }
        if (this.value != null) {
            if (bigDecimal != null) {
                return divide(this.value, bigDecimal, typeInfo);
            }
            if (value != NAN) {
                return ZERO;
            }
        } else if (bigDecimal != null && this != NAN) {
            return (this == POSITIVE_INFINITY) == (bigDecimal.signum() > 0) ? POSITIVE_INFINITY : NEGATIVE_INFINITY;
        }
        return NAN;
    }

    public static ValueDecfloat divide(BigDecimal bigDecimal, BigDecimal bigDecimal2, TypeInfo typeInfo) {
        int precision = (int) typeInfo.getPrecision();
        BigDecimal divide = bigDecimal.divide(bigDecimal2, (((bigDecimal.scale() - bigDecimal.precision()) + bigDecimal2.precision()) - bigDecimal2.scale()) + precision, RoundingMode.HALF_DOWN);
        int precision2 = divide.precision();
        if (precision2 > precision) {
            divide = divide.setScale((divide.scale() - precision2) + precision, RoundingMode.HALF_UP);
        }
        return get(divide);
    }

    @Override // org.h2.value.Value
    public Value modulus(Value value) {
        BigDecimal bigDecimal = ((ValueDecfloat) value).value;
        if (bigDecimal != null && bigDecimal.signum() == 0) {
            throw DbException.get(ErrorCode.DIVISION_BY_ZERO_1, getTraceSQL());
        }
        if (this.value != null) {
            if (bigDecimal != null) {
                return get(this.value.remainder(bigDecimal));
            }
            if (value != NAN) {
                return this;
            }
        }
        return NAN;
    }

    @Override // org.h2.value.Value
    public int compareTypeSafe(Value value, CompareMode compareMode, CastDataProvider castDataProvider) {
        BigDecimal bigDecimal = ((ValueDecfloat) value).value;
        if (this.value != null) {
            if (bigDecimal != null) {
                return this.value.compareTo(bigDecimal);
            }
            return value == NEGATIVE_INFINITY ? 1 : -1;
        }
        if (bigDecimal != null) {
            return this == NEGATIVE_INFINITY ? -1 : 1;
        }
        if (this == value) {
            return 0;
        }
        if (this == NEGATIVE_INFINITY) {
            return -1;
        }
        return (value != NEGATIVE_INFINITY && this == POSITIVE_INFINITY) ? -1 : 1;
    }

    @Override // org.h2.value.Value
    public int getSignum() {
        if (this.value != null) {
            return this.value.signum();
        }
        if (this == POSITIVE_INFINITY) {
            return 1;
        }
        return this == NEGATIVE_INFINITY ? -1 : 0;
    }

    @Override // org.h2.value.Value
    public BigDecimal getBigDecimal() {
        if (this.value != null) {
            return this.value;
        }
        throw getDataConversionError(13);
    }

    @Override // org.h2.value.Value
    public float getFloat() {
        if (this.value != null) {
            return this.value.floatValue();
        }
        if (this == POSITIVE_INFINITY) {
            return Float.POSITIVE_INFINITY;
        }
        if (this == NEGATIVE_INFINITY) {
            return Float.NEGATIVE_INFINITY;
        }
        return Float.NaN;
    }

    @Override // org.h2.value.Value
    public double getDouble() {
        if (this.value != null) {
            return this.value.doubleValue();
        }
        if (this == POSITIVE_INFINITY) {
            return Double.POSITIVE_INFINITY;
        }
        if (this == NEGATIVE_INFINITY) {
            return Double.NEGATIVE_INFINITY;
        }
        return Double.NaN;
    }

    @Override // org.h2.value.Value
    public int hashCode() {
        return this.value != null ? (getClass().hashCode() * 31) + this.value.hashCode() : System.identityHashCode(this);
    }

    @Override // org.h2.value.Value
    public boolean equals(Object obj) {
        if (obj instanceof ValueDecfloat) {
            BigDecimal bigDecimal = ((ValueDecfloat) obj).value;
            if (this.value != null) {
                return this.value.equals(bigDecimal);
            }
            if (bigDecimal == null && this == obj) {
                return true;
            }
            return false;
        }
        return false;
    }

    @Override // org.h2.value.Value
    public int getMemory() {
        if (this.value != null) {
            return this.value.precision() + 120;
        }
        return 32;
    }

    public boolean isFinite() {
        return this.value != null;
    }

    public static ValueDecfloat get(BigDecimal bigDecimal) {
        BigDecimal stripTrailingZeros = bigDecimal.stripTrailingZeros();
        if (BigDecimal.ZERO.equals(stripTrailingZeros)) {
            return ZERO;
        }
        if (BigDecimal.ONE.equals(stripTrailingZeros)) {
            return ONE;
        }
        return (ValueDecfloat) Value.cache(new ValueDecfloat(stripTrailingZeros));
    }
}
