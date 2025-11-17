package org.jooq.conf;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;
import org.jooq.Configuration;
import org.jooq.tools.JooqLogger;
import org.jooq.tools.StringUtils;
import org.jooq.util.jaxb.tools.MiniJAXB;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/conf/SettingsTools.class */
public final class SettingsTools {
    private static final Settings DEFAULT_SETTINGS;
    private static final JooqLogger log = JooqLogger.getLogger((Class<?>) SettingsTools.class);

    static {
        InputStream in;
        Settings settings = null;
        String property = System.getProperty("org.jooq.settings");
        if (property != null) {
            log.warn("DEPRECATION", "Loading system wide default settings via org.jooq.settings system properties has been deprecated. Please use explicit Settings in your Configuration references, instead.");
            in = SettingsTools.class.getResourceAsStream(property);
            if (in != null) {
                try {
                    try {
                        settings = (Settings) MiniJAXB.unmarshal(in, Settings.class);
                        if (in != null) {
                            in.close();
                        }
                    } finally {
                    }
                } catch (IOException e) {
                    log.error("Error while reading settings: " + String.valueOf(e));
                }
            } else {
                settings = (Settings) MiniJAXB.unmarshal(new File(property), Settings.class);
            }
        }
        if (settings == null && (in = SettingsTools.class.getResourceAsStream("/jooq-settings.xml")) != null) {
            try {
                try {
                    log.warn("DEPRECATION", "Loading system wide default settings via the classpath /jooq-settings.xml resource has been deprecated. Please use explicit Settings in your Configuration references, instead.");
                    settings = (Settings) MiniJAXB.unmarshal(in, Settings.class);
                    if (in != null) {
                        in.close();
                    }
                } finally {
                }
            } catch (IOException e2) {
                log.error("Error while reading settings: " + String.valueOf(e2));
            }
        }
        if (settings == null) {
            settings = new Settings();
        }
        DEFAULT_SETTINGS = settings;
    }

    public static final ParamType getParamType(Settings settings) {
        ParamType result;
        if (executeStaticStatements(settings)) {
            return ParamType.INLINED;
        }
        if (settings != null && (result = settings.getParamType()) != null) {
            return result;
        }
        return ParamType.INDEXED;
    }

    public static final StatementType getStatementType(Settings settings) {
        StatementType result;
        if (settings != null && (result = settings.getStatementType()) != null) {
            return result;
        }
        return StatementType.PREPARED_STATEMENT;
    }

    public static final BackslashEscaping getBackslashEscaping(Settings settings) {
        BackslashEscaping result;
        if (settings != null && (result = settings.getBackslashEscaping()) != null) {
            return result;
        }
        return BackslashEscaping.DEFAULT;
    }

    public static final boolean executePreparedStatements(Settings settings) {
        return getStatementType(settings) == StatementType.PREPARED_STATEMENT;
    }

    public static final boolean executeStaticStatements(Settings settings) {
        return getStatementType(settings) == StatementType.STATIC_STATEMENT;
    }

    public static final boolean updatablePrimaryKeys(Settings settings) {
        return ((Boolean) StringUtils.defaultIfNull(settings.isUpdatablePrimaryKeys(), false)).booleanValue();
    }

    public static final boolean reflectionCaching(Settings settings) {
        return ((Boolean) StringUtils.defaultIfNull(settings.isReflectionCaching(), true)).booleanValue();
    }

    public static final boolean recordMapperCaching(Settings settings) {
        return ((Boolean) StringUtils.defaultIfNull(settings.isCacheRecordMappers(), true)).booleanValue();
    }

    public static final boolean parsingConnectionCaching(Settings settings) {
        return ((Boolean) StringUtils.defaultIfNull(settings.isCacheParsingConnection(), true)).booleanValue();
    }

    public static final Locale locale(Settings settings) {
        return (Locale) StringUtils.defaultIfNull(settings.getLocale(), Locale.getDefault());
    }

    public static final Locale renderLocale(Settings settings) {
        return (Locale) StringUtils.defaultIfNull(settings.getRenderLocale(), locale(settings));
    }

    public static final Locale parseLocale(Settings settings) {
        return (Locale) StringUtils.defaultIfNull(settings.getParseLocale(), locale(settings));
    }

    public static final Locale interpreterLocale(Settings settings) {
        return (Locale) StringUtils.defaultIfNull(settings.getInterpreterLocale(), locale(settings));
    }

    public static final RenderTable getRenderTable(Settings settings) {
        if (settings.getRenderTable() == null) {
            settings.setRenderTable(RenderTable.ALWAYS);
        }
        return settings.getRenderTable();
    }

    public static final RenderMapping getRenderMapping(Settings settings) {
        if (settings.getRenderMapping() == null) {
            settings.setRenderMapping(new RenderMapping());
        }
        return settings.getRenderMapping();
    }

    public static final RenderKeywordCase getRenderKeywordCase(Settings settings) {
        RenderKeywordCase result = settings.getRenderKeywordCase();
        if (result == null || result == RenderKeywordCase.AS_IS) {
            RenderKeywordStyle style = settings.getRenderKeywordStyle();
            if (style != null) {
                switch (style) {
                    case AS_IS:
                        result = RenderKeywordCase.AS_IS;
                        break;
                    case LOWER:
                        result = RenderKeywordCase.LOWER;
                        break;
                    case UPPER:
                        result = RenderKeywordCase.UPPER;
                        break;
                    case PASCAL:
                        result = RenderKeywordCase.PASCAL;
                        break;
                    default:
                        throw new UnsupportedOperationException("Unsupported style: " + String.valueOf(style));
                }
            } else {
                result = RenderKeywordCase.AS_IS;
            }
        }
        return result;
    }

    public static final RenderNameCase getRenderNameCase(Settings settings) {
        RenderNameCase result = settings.getRenderNameCase();
        if (result == null || result == RenderNameCase.AS_IS) {
            RenderNameStyle style = settings.getRenderNameStyle();
            if (style == RenderNameStyle.LOWER) {
                result = RenderNameCase.LOWER;
            } else if (style == RenderNameStyle.UPPER) {
                result = RenderNameCase.UPPER;
            } else {
                result = RenderNameCase.AS_IS;
            }
        }
        return result;
    }

    public static final RenderQuotedNames getRenderQuotedNames(Settings settings) {
        RenderQuotedNames result = settings.getRenderQuotedNames();
        if (result == null || result == RenderQuotedNames.EXPLICIT_DEFAULT_QUOTED) {
            RenderNameStyle style = settings.getRenderNameStyle();
            if (style == null || style == RenderNameStyle.QUOTED) {
                result = RenderQuotedNames.EXPLICIT_DEFAULT_QUOTED;
            } else {
                result = RenderQuotedNames.NEVER;
            }
        }
        return result;
    }

    public static final ExecuteWithoutWhere getExecuteUpdateWithoutWhere(Settings settings) {
        ExecuteWithoutWhere result = settings.getExecuteUpdateWithoutWhere();
        return result == null ? ExecuteWithoutWhere.LOG_DEBUG : result;
    }

    public static final ExecuteWithoutWhere getExecuteDeleteWithoutWhere(Settings settings) {
        ExecuteWithoutWhere result = settings.getExecuteDeleteWithoutWhere();
        return result == null ? ExecuteWithoutWhere.LOG_DEBUG : result;
    }

    public static final TransformUnneededArithmeticExpressions getTransformUnneededArithmeticExpressions(Settings settings) {
        if (settings.getTransformUnneededArithmeticExpressions() != null) {
            return settings.getTransformUnneededArithmeticExpressions();
        }
        return TransformUnneededArithmeticExpressions.NEVER;
    }

    public static final Settings defaultSettings() {
        return clone(DEFAULT_SETTINGS);
    }

    public static final Settings clone(Settings settings) {
        Settings result = (Settings) settings.clone();
        if (result.renderFormatting != null) {
            result.renderFormatting = (RenderFormatting) result.renderFormatting.clone();
        }
        if (result.parseSearchPath != null) {
            result.parseSearchPath = new ArrayList(result.parseSearchPath);
        }
        if (result.migrationSchemata != null) {
            result.migrationSchemata = new ArrayList(result.migrationSchemata);
        }
        return result;
    }

    public static final int getQueryTimeout(int timeout, Settings settings) {
        if (timeout != 0) {
            return timeout;
        }
        if (settings.getQueryTimeout() != null) {
            return settings.getQueryTimeout().intValue();
        }
        return 0;
    }

    public static final QueryPoolable getQueryPoolable(QueryPoolable poolable, Settings settings) {
        if (poolable != null && poolable != QueryPoolable.DEFAULT) {
            return poolable;
        }
        if (settings.getQueryPoolable() != null) {
            return settings.getQueryPoolable();
        }
        return QueryPoolable.DEFAULT;
    }

    public static final int getMaxRows(int maxRows, Settings settings) {
        if (maxRows != 0) {
            return maxRows;
        }
        if (settings.getMaxRows() != null) {
            return settings.getMaxRows().intValue();
        }
        return 0;
    }

    public static final boolean fetchIntermediateResult(Configuration configuration) {
        switch ((FetchIntermediateResult) StringUtils.defaultIfNull(configuration.settings().getFetchIntermediateResult(), FetchIntermediateResult.WHEN_RESULT_REQUESTED)) {
            case ALWAYS:
                return true;
            case WHEN_EXECUTE_LISTENERS_PRESENT:
                return configuration.executeListenerProviders().length > 0;
            case WHEN_RESULT_REQUESTED:
                return false;
            default:
                throw new IllegalStateException("Unhandled FetchIntermediateResult: " + String.valueOf(configuration.settings().getFetchIntermediateResult()));
        }
    }

    public static final int getFetchSize(int fetchSize, Settings settings) {
        if (fetchSize != 0) {
            return fetchSize;
        }
        if (settings.getFetchSize() != null) {
            return settings.getFetchSize().intValue();
        }
        return 0;
    }

    public static final int getBatchSize(Settings settings) {
        if (settings.getBatchSize() != null) {
            return settings.getBatchSize().intValue();
        }
        return 0;
    }

    public static final int getFetchServerOutputSize(int fetchServerOutputSize, Settings settings) {
        if (fetchServerOutputSize != 0) {
            return fetchServerOutputSize;
        }
        if (settings.getFetchServerOutputSize() != null) {
            return settings.getFetchServerOutputSize().intValue();
        }
        return 0;
    }

    public static final boolean returnAnyOnUpdatableRecord(Settings settings) {
        return !Boolean.FALSE.equals(settings.isReturnIdentityOnUpdatableRecord()) || returnAnyNonIdentityOnUpdatableRecord(settings);
    }

    public static final boolean returnAnyNonIdentityOnUpdatableRecord(Settings settings) {
        return Boolean.TRUE.equals(settings.isReturnAllOnUpdatableRecord()) || Boolean.TRUE.equals(settings.isReturnDefaultOnUpdatableRecord()) || Boolean.TRUE.equals(settings.isReturnComputedOnUpdatableRecord());
    }
}
