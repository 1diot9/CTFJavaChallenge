package org.jooq;

import ch.qos.logback.core.rolling.helper.IntegerTokenConverter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jooq.conf.ParseNameCase;
import org.jooq.conf.ParseUnknownFunctions;
import org.jooq.conf.ParseUnsupportedSyntax;
import org.jooq.conf.ParseWithMetaLookups;
import org.jooq.conf.RenderKeywordCase;
import org.jooq.conf.RenderNameCase;
import org.jooq.conf.RenderOptionalKeyword;
import org.jooq.conf.RenderQuotedNames;
import org.jooq.conf.Settings;
import org.jooq.conf.TransformUnneededArithmeticExpressions;
import org.jooq.conf.Transformation;
import org.jooq.impl.DSL;
import org.jooq.impl.ParserException;
import org.jooq.tools.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/ParserCLI.class */
public final class ParserCLI {
    private static final Pattern FLAG = Pattern.compile("^/([\\w\\-]+)(?:\\s+([^\\r\\n]+))?\\s*$");

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/ParserCLI$Args.class */
    public static final class Args {
        boolean interactive;
        boolean done;
        String schema;
        String sql;
        Settings d = new Settings();
        List<String> history = new ArrayList();
        RenderKeywordCase keywords = RenderKeywordCase.LOWER;
        RenderNameCase name = RenderNameCase.LOWER;
        RenderQuotedNames quoted = RenderQuotedNames.EXPLICIT_DEFAULT_UNQUOTED;
        SQLDialect toDialect = SQLDialect.DEFAULT;
        SQLDialect fromDialect = SQLDialect.DEFAULT;
        Boolean formatted = this.d.isRenderFormatted();
        Boolean renderCoalesceToEmptyStringInConcat = this.d.isRenderCoalesceToEmptyStringInConcat();
        RenderOptionalKeyword renderOptionalInnerKeyword = RenderOptionalKeyword.DEFAULT;
        RenderOptionalKeyword renderOptionalOuterKeyword = RenderOptionalKeyword.DEFAULT;
        RenderOptionalKeyword renderOptionalAsKeywordForFieldAliases = RenderOptionalKeyword.DEFAULT;
        RenderOptionalKeyword renderOptionalAsKeywordForTableAliases = RenderOptionalKeyword.DEFAULT;
        String parseDateFormat = this.d.getParseDateFormat();
        Boolean parseIgnoreComments = this.d.isParseIgnoreComments();
        String parseIgnoreCommentStart = this.d.getParseIgnoreCommentStart();
        String parseIgnoreCommentStop = this.d.getParseIgnoreCommentStop();
        Locale parseLocale = this.d.getParseLocale();
        ParseNameCase parseNameCase = this.d.getParseNameCase();
        String parseNamedParamPrefix = this.d.getParseNamedParamPrefix();
        Boolean parseRetainCommentsBetweenQueries = this.d.isParseRetainCommentsBetweenQueries();
        Boolean parseSetCommands = this.d.isParseSetCommands();
        String parseTimestampFormat = this.d.getParseTimestampFormat();
        ParseUnknownFunctions parseUnknownFunctions = this.d.getParseUnknownFunctions();
        ParseUnsupportedSyntax parseUnsupportedSyntax = this.d.getParseUnsupportedSyntax();
        Boolean transformPatterns = this.d.isTransformPatterns();
        Boolean transformAnsiJoinToTableLists = this.d.isTransformAnsiJoinToTableLists();
        Transformation transformQualify = this.d.getTransformQualify();
        Transformation transformRownum = this.d.getTransformRownum();
        Transformation transformGroupByColumnIndex = this.d.getTransformGroupByColumnIndex();
        Transformation transformInlineCTE = this.d.getTransformInlineCTE();
        Boolean transformTableListsToAnsiJoin = this.d.isTransformTableListsToAnsiJoin();
        TransformUnneededArithmeticExpressions transformUnneededArithmetic = TransformUnneededArithmeticExpressions.NEVER;
    }

    public static final void main(String... args) throws Exception {
        CLIUtil.main("https://www.jooq.org/doc/latest/manual/sql-building/sql-parser/sql-parser-cli/", () -> {
            Settings settings = new Settings();
            Args a = parse(args);
            settings(a, settings);
            DSLContext ctx = ctx(a, settings);
            if (a.interactive || args == null || args.length == 0) {
                interactiveMode(ctx, a);
                return;
            }
            if (!a.done) {
                if (a.toDialect == null || a.sql == null) {
                    System.out.println("Mandatory arguments: -T and -s. Use -h for help");
                    throw new RuntimeException();
                }
                render(ctx, a);
            }
        });
    }

    private static final DSLContext ctx(Args a, Settings settings) {
        return DSL.using(a.toDialect, settings);
    }

    private static final void settings(Args a, Settings settings) {
        if (a.formatted != null) {
            settings.setRenderFormatted(a.formatted);
        }
        if (a.keywords != null) {
            settings.setRenderKeywordCase(a.keywords);
        }
        if (a.name != null) {
            settings.setRenderNameCase(a.name);
        }
        if (a.quoted != null) {
            settings.setRenderQuotedNames(a.quoted);
        }
        if (a.fromDialect != null) {
            settings.setParseDialect(a.fromDialect);
        }
        if (a.parseDateFormat != null) {
            settings.setParseDateFormat(a.parseDateFormat);
        }
        if (a.parseIgnoreComments != null) {
            settings.setParseIgnoreComments(a.parseIgnoreComments);
        }
        if (a.parseIgnoreCommentStart != null) {
            settings.setParseIgnoreCommentStart(a.parseIgnoreCommentStart);
        }
        if (a.parseIgnoreCommentStop != null) {
            settings.setParseIgnoreCommentStop(a.parseIgnoreCommentStop);
        }
        if (a.parseLocale != null) {
            settings.setParseLocale(a.parseLocale);
        }
        if (a.parseNameCase != null) {
            settings.setParseNameCase(a.parseNameCase);
        }
        if (a.parseNamedParamPrefix != null) {
            settings.setParseNamedParamPrefix(a.parseNamedParamPrefix);
        }
        if (a.parseRetainCommentsBetweenQueries != null) {
            settings.setParseRetainCommentsBetweenQueries(a.parseRetainCommentsBetweenQueries);
        }
        if (a.parseSetCommands != null) {
            settings.setParseSetCommands(a.parseSetCommands);
        }
        if (a.parseTimestampFormat != null) {
            settings.setParseTimestampFormat(a.parseTimestampFormat);
        }
        if (a.parseUnknownFunctions != null) {
            settings.setParseUnknownFunctions(a.parseUnknownFunctions);
        }
        if (a.renderCoalesceToEmptyStringInConcat != null) {
            settings.setRenderCoalesceToEmptyStringInConcat(a.renderCoalesceToEmptyStringInConcat);
        }
        if (a.renderOptionalInnerKeyword != null) {
            settings.setRenderOptionalInnerKeyword(a.renderOptionalInnerKeyword);
        }
        if (a.renderOptionalOuterKeyword != null) {
            settings.setRenderOptionalOuterKeyword(a.renderOptionalOuterKeyword);
        }
        if (a.renderOptionalAsKeywordForFieldAliases != null) {
            settings.setRenderOptionalAsKeywordForFieldAliases(a.renderOptionalAsKeywordForFieldAliases);
        }
        if (a.renderOptionalAsKeywordForTableAliases != null) {
            settings.setRenderOptionalAsKeywordForTableAliases(a.renderOptionalAsKeywordForTableAliases);
        }
        if (a.transformPatterns != null) {
            settings.setTransformPatterns(a.transformPatterns);
        }
        if (a.transformAnsiJoinToTableLists != null) {
            settings.setTransformAnsiJoinToTableLists(a.transformAnsiJoinToTableLists);
        }
        if (a.transformTableListsToAnsiJoin != null) {
            settings.setTransformTableListsToAnsiJoin(a.transformTableListsToAnsiJoin);
        }
        if (a.transformUnneededArithmetic != null) {
            settings.setTransformUnneededArithmeticExpressions(a.transformUnneededArithmetic);
        }
        if (a.transformQualify != null) {
            settings.setTransformRownum(a.transformQualify);
        }
        if (a.transformRownum != null) {
            settings.setTransformRownum(a.transformRownum);
        }
        if (a.transformGroupByColumnIndex != null) {
            settings.setTransformGroupByColumnIndex(a.transformGroupByColumnIndex);
        }
        if (a.transformInlineCTE != null) {
            settings.setTransformInlineCTE(a.transformInlineCTE);
        }
    }

    private static final <E extends Enum<E>> void parseInteractive(Class<E> type, String arg, Consumer<? super E> onSuccess, Runnable display) {
        if (arg != null) {
            try {
                onSuccess.accept(Enum.valueOf(type, arg.toUpperCase()));
            } catch (IllegalArgumentException e) {
                invalid(arg, type);
                return;
            }
        }
        display.run();
    }

    private static final void interactiveMode(DSLContext ctx, Args a) {
        Scanner scan = new Scanner(System.in);
        System.out.print("> ");
        do {
            String line = scan.nextLine();
            a.history.add(line);
            boolean leadingSlash = line.matches("^/[^/*].*$");
            if (a.sql == null && leadingSlash) {
                if ("/q".equals(line) || "/quit".equals(line) || "/e".equals(line) || "/exit".equals(line)) {
                    System.out.println("Bye");
                    return;
                }
                if ("/?".equals(line) || "/h".equals(line) || "/help".equals(line)) {
                    helpInteractive();
                } else if ("/d".equals(line) || "/display".equals(line)) {
                    displayArguments(a);
                } else {
                    Matcher matcher = FLAG.matcher(line);
                    if (matcher.find()) {
                        String flag = matcher.group(1);
                        String arg = matcher.group(2);
                        if (flag != null) {
                            if ("f".equals(flag) || "formatted".equals(flag)) {
                                if (arg != null) {
                                    a.formatted = Boolean.valueOf(Boolean.parseBoolean(arg.toLowerCase()));
                                }
                                displayFormatted(a);
                            } else if ("k".equals(flag) || "keyword".equals(flag)) {
                                parseInteractive(RenderKeywordCase.class, arg, e -> {
                                    a.keywords = e;
                                }, () -> {
                                    displayKeywords(a);
                                });
                            } else if (IntegerTokenConverter.CONVERTER_KEY.equals(flag) || "identifier".equals(flag)) {
                                parseInteractive(RenderNameCase.class, arg, e2 -> {
                                    a.name = e2;
                                }, () -> {
                                    displayIdentifiers(a);
                                });
                            } else if ("Q".equals(flag) || "quoted".equals(flag)) {
                                parseInteractive(RenderQuotedNames.class, arg, e3 -> {
                                    a.quoted = e3;
                                }, () -> {
                                    displayQuoted(a);
                                });
                            } else if ("F".equals(flag) || "from-dialect".equals(flag)) {
                                parseInteractive(SQLDialect.class, arg, e4 -> {
                                    a.fromDialect = e4;
                                }, () -> {
                                    displayFromDialect(a);
                                });
                            } else if ("S".equals(flag) || "schema".equals(flag)) {
                                a.schema = arg;
                                displaySchema(a);
                            } else if ("render-coalesce-to-empty-string-in-concat".equals(flag)) {
                                if (arg != null) {
                                    a.renderCoalesceToEmptyStringInConcat = Boolean.valueOf(Boolean.parseBoolean(arg.toLowerCase()));
                                }
                                displayRenderCoalesceToEmptyStringInConcat(a);
                            } else if ("parse-date-format".equals(flag)) {
                                if (arg != null) {
                                    a.parseDateFormat = arg;
                                }
                                displayParseDateFormat(a);
                            } else if ("parse-ignore-comments".equals(flag)) {
                                if (arg != null) {
                                    a.parseIgnoreComments = Boolean.valueOf(Boolean.parseBoolean(arg.toLowerCase()));
                                }
                                displayParseIgnoreComments(a);
                            } else if ("parse-ignore-comment-start".equals(flag)) {
                                if (arg != null) {
                                    a.parseIgnoreCommentStart = arg;
                                }
                                displayParseIgnoreCommentStart(a);
                            } else if ("parse-ignore-comment-stop".equals(flag)) {
                                if (arg != null) {
                                    a.parseIgnoreCommentStop = arg;
                                }
                                displayParseIgnoreCommentStop(a);
                            } else if ("parse-locale".equals(flag)) {
                                if (arg != null) {
                                    a.parseLocale = Locale.forLanguageTag(arg);
                                }
                                displayParseLocale(a);
                            } else if ("parse-name-case".equals(flag)) {
                                parseInteractive(ParseNameCase.class, arg, e5 -> {
                                    a.parseNameCase = e5;
                                }, () -> {
                                    displayParseNameCase(a);
                                });
                            } else if ("parse-named-param-prefix".equals(flag)) {
                                if (arg != null) {
                                    a.parseNamedParamPrefix = arg;
                                }
                                displayParseNamedParamPrefix(a);
                            } else if ("parse-retain-comments-between-queries".equals(flag)) {
                                if (arg != null) {
                                    a.parseRetainCommentsBetweenQueries = Boolean.valueOf(Boolean.parseBoolean(arg.toLowerCase()));
                                }
                                displayParseRetainCommentsBetweenQueries(a);
                            } else if ("parse-set-commands".equals(flag)) {
                                if (arg != null) {
                                    a.parseSetCommands = Boolean.valueOf(Boolean.parseBoolean(arg.toLowerCase()));
                                }
                                displayParseSetCommands(a);
                            } else if ("parse-timestamp-format".equals(flag)) {
                                if (arg != null) {
                                    a.parseTimestampFormat = arg;
                                }
                                displayParseTimestampFormat(a);
                            } else if ("parse-unknown-functions".equals(flag)) {
                                parseInteractive(ParseUnknownFunctions.class, arg, e6 -> {
                                    a.parseUnknownFunctions = e6;
                                }, () -> {
                                    displayParseUnknownFunctions(a);
                                });
                            } else if ("parse-unsupported-syntax".equals(flag)) {
                                parseInteractive(ParseUnsupportedSyntax.class, arg, e7 -> {
                                    a.parseUnsupportedSyntax = e7;
                                }, () -> {
                                    displayParseUnsupportedSyntax(a);
                                });
                            } else if ("render-optional-inner-keyword".equals(flag)) {
                                parseInteractive(RenderOptionalKeyword.class, arg, e8 -> {
                                    a.renderOptionalInnerKeyword = e8;
                                }, () -> {
                                    displayRenderOptionalInnerKeyword(a);
                                });
                            } else if ("render-optional-outer-keyword".equals(flag)) {
                                parseInteractive(RenderOptionalKeyword.class, arg, e9 -> {
                                    a.renderOptionalOuterKeyword = e9;
                                }, () -> {
                                    displayRenderOptionalOuterKeyword(a);
                                });
                            } else if ("render-optional-as-keyword-for-field-aliases".equals(flag)) {
                                parseInteractive(RenderOptionalKeyword.class, arg, e10 -> {
                                    a.renderOptionalAsKeywordForFieldAliases = e10;
                                }, () -> {
                                    displayRenderOptionalAsKeywordForFieldAliases(a);
                                });
                            } else if ("render-optional-as-keyword-for-table-aliases".equals(flag)) {
                                parseInteractive(RenderOptionalKeyword.class, arg, e11 -> {
                                    a.renderOptionalAsKeywordForTableAliases = e11;
                                }, () -> {
                                    displayRenderOptionalAsKeywordForTableAliases(a);
                                });
                            } else if ("transform-patterns".equals(flag)) {
                                if (arg != null) {
                                    a.transformPatterns = Boolean.valueOf(Boolean.parseBoolean(arg.toLowerCase()));
                                }
                                displayTransformPatterns(a);
                            } else if ("transform-ansi-join-to-table-lists".equals(flag)) {
                                if (arg != null) {
                                    a.transformAnsiJoinToTableLists = Boolean.valueOf(Boolean.parseBoolean(arg.toLowerCase()));
                                }
                                displayTransformAnsiJoinToTablesLists(a);
                            } else if ("transform-qualify".equals(flag)) {
                                parseInteractive(Transformation.class, arg, e12 -> {
                                    a.transformQualify = e12;
                                }, () -> {
                                    displayTransformQualify(a);
                                });
                            } else if ("transform-rownum".equals(flag)) {
                                parseInteractive(Transformation.class, arg, e13 -> {
                                    a.transformRownum = e13;
                                }, () -> {
                                    displayTransformRownum(a);
                                });
                            } else if ("transform-group-by-column-index".equals(flag)) {
                                parseInteractive(Transformation.class, arg, e14 -> {
                                    a.transformGroupByColumnIndex = e14;
                                }, () -> {
                                    displayTransformGroupByColumnIndex(a);
                                });
                            } else if ("transform-inline-cte".equals(flag)) {
                                parseInteractive(Transformation.class, arg, e15 -> {
                                    a.transformInlineCTE = e15;
                                }, () -> {
                                    displayTransformInlineCTE(a);
                                });
                            } else if ("transform-table-lists-to-ansi-join".equals(flag)) {
                                if (arg != null) {
                                    a.transformTableListsToAnsiJoin = Boolean.valueOf(Boolean.parseBoolean(arg.toLowerCase()));
                                }
                                displayTransformTableListsToAnsiJoin(a);
                            } else if ("transform-unneeded-arithmetic".equals(flag)) {
                                parseInteractive(TransformUnneededArithmeticExpressions.class, arg, e16 -> {
                                    a.transformUnneededArithmetic = e16;
                                }, () -> {
                                    displayTransformUnneededArithmetic(a);
                                });
                            } else if ("t".equals(flag) || "T".equals(flag) || "to-dialect".equals(flag)) {
                                parseInteractive(SQLDialect.class, arg, e17 -> {
                                    a.toDialect = e17;
                                }, () -> {
                                    displayToDialect(a);
                                });
                            }
                        }
                    } else {
                        System.out.println("Unrecognised command: " + line);
                        System.out.println("Type /h for help");
                    }
                }
                settings(a, ctx.settings());
                ctx = ctx(a, ctx.settings());
            }
            if (a.sql != null || !leadingSlash) {
                if (a.sql == null) {
                    a.sql = line;
                } else {
                    a.sql += "\n" + line;
                }
                if (a.sql.trim().endsWith(";")) {
                    render(ctx, a);
                    a.sql = null;
                    System.out.println();
                }
            }
            System.out.print("> ");
        } while (scan.hasNextLine());
    }

    private static final void displayArguments(Args a) {
        displayFormatted(a);
        displayFromDialect(a);
        displayToDialect(a);
        displayKeywords(a);
        displayIdentifiers(a);
        displayQuoted(a);
        displayTransformAnsiJoinToTablesLists(a);
        displayTransformQualify(a);
        displayTransformRownum(a);
        displayTransformTableListsToAnsiJoin(a);
        displayTransformUnneededArithmetic(a);
        displayTransformGroupByColumnIndex(a);
        displayTransformInlineCTE(a);
        displaySchema(a);
    }

    private static void displaySchema(Args a) {
        System.out.println("Schema                             : " + a.schema);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void displayIdentifiers(Args a) {
        System.out.println("Identifiers                        : " + String.valueOf(a.name));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void displayQuoted(Args a) {
        System.out.println("Quoted                             : " + String.valueOf(a.quoted));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void displayKeywords(Args a) {
        System.out.println("Keywords                           : " + String.valueOf(a.keywords));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void displayToDialect(Args a) {
        System.out.println("To dialect                         : " + String.valueOf(a.toDialect));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void displayFromDialect(Args a) {
        System.out.println("From dialect                       : " + String.valueOf(a.fromDialect));
    }

    private static void displayFormatted(Args a) {
        System.out.println("Formatted                          : " + a.formatted);
    }

    private static void displayParseDateFormat(Args a) {
        System.out.println("Parse date format                  : " + a.parseDateFormat);
    }

    private static void displayParseIgnoreComments(Args a) {
        System.out.println("Parse ignore comments              : " + a.parseIgnoreComments);
    }

    private static void displayParseIgnoreCommentStart(Args a) {
        System.out.println("Parse ignore comment start         : " + a.parseIgnoreCommentStart);
    }

    private static void displayParseIgnoreCommentStop(Args a) {
        System.out.println("Parse ignore comment stop          : " + a.parseIgnoreCommentStop);
    }

    private static void displayParseLocale(Args a) {
        System.out.println("Parse locale                       : " + String.valueOf(a.parseLocale));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void displayParseNameCase(Args a) {
        System.out.println("Parse name case                    : " + String.valueOf(a.parseNameCase));
    }

    private static void displayParseNamedParamPrefix(Args a) {
        System.out.println("Parse named param prefix           : " + a.parseNamedParamPrefix);
    }

    private static void displayParseRetainCommentsBetweenQueries(Args a) {
        System.out.println("Retain comments between queries    : " + a.parseRetainCommentsBetweenQueries);
    }

    private static void displayParseSetCommands(Args a) {
        System.out.println("Parse set commands                 : " + a.parseSetCommands);
    }

    private static void displayParseTimestampFormat(Args a) {
        System.out.println("Parse timestamp format             : " + a.parseTimestampFormat);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void displayParseUnknownFunctions(Args a) {
        System.out.println("Parse unknown functions            : " + String.valueOf(a.parseUnknownFunctions));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void displayParseUnsupportedSyntax(Args a) {
        System.out.println("Parse unsupported syntax           : " + String.valueOf(a.parseUnsupportedSyntax));
    }

    private static void displayRenderCoalesceToEmptyStringInConcat(Args a) {
        System.out.println("Render COALESCE(X, '') in CONCAT   : " + a.renderCoalesceToEmptyStringInConcat);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void displayRenderOptionalInnerKeyword(Args a) {
        System.out.println("Render INNER keyword in INNER JOIN : " + String.valueOf(a.renderOptionalInnerKeyword));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void displayRenderOptionalOuterKeyword(Args a) {
        System.out.println("Render OUTER keyword in OUTER JOIN : " + String.valueOf(a.renderOptionalOuterKeyword));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void displayRenderOptionalAsKeywordForFieldAliases(Args a) {
        System.out.println("Render AS keyword to alias fields  :" + String.valueOf(a.renderOptionalAsKeywordForFieldAliases));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void displayRenderOptionalAsKeywordForTableAliases(Args a) {
        System.out.println("Render AS keyword to alias tables  :" + String.valueOf(a.renderOptionalAsKeywordForTableAliases));
    }

    private static void displayTransformPatterns(Args a) {
        System.out.println("Transform patterns                 : " + a.transformPatterns);
    }

    private static void displayTransformAnsiJoinToTablesLists(Args a) {
        System.out.println("Transform ANSI join to table lists : " + a.transformAnsiJoinToTableLists);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void displayTransformQualify(Args a) {
        System.out.println("Transform QUALIFY                  : " + String.valueOf(a.transformQualify));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void displayTransformRownum(Args a) {
        System.out.println("Transform ROWNUM                   : " + String.valueOf(a.transformRownum));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void displayTransformGroupByColumnIndex(Args a) {
        System.out.println("Transform GROUP BY <column index>  : " + String.valueOf(a.transformGroupByColumnIndex));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void displayTransformInlineCTE(Args a) {
        System.out.println("Transform inline CTE               : " + String.valueOf(a.transformInlineCTE));
    }

    private static void displayTransformTableListsToAnsiJoin(Args a) {
        System.out.println("Transform table lists to ANSI join : " + a.transformTableListsToAnsiJoin);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void displayTransformUnneededArithmetic(Args a) {
        System.out.println("Transform unneeded arithmetic      : " + String.valueOf(a.transformUnneededArithmetic));
    }

    private static final void render(DSLContext ctx, Args a) {
        String sql = a.sql.trim();
        if (!StringUtils.isBlank(a.schema)) {
            ctx = ctx.configuration().derive(() -> {
                return ctx.meta(a.schema);
            }).deriveSettings(s -> {
                return s.withParseWithMetaLookups(ParseWithMetaLookups.THROW_ON_FAILURE);
            }).dsl();
        }
        try {
            System.out.println(ctx.render(ctx.parser().parse(a.sql)));
        } catch (ParserException e1) {
            ParserException e = e1;
            if (!sql.matches("^(?is:(?:ALTER|BEGIN|COMMENT|CREATE|DECLARE|DELETE|DESCRIBE|DROP|GRANT|INSERT|MERGE|RENAME|REVOKE|SELECT|SET|SHOW|TRUNCATE|UPDATE|USE).*)$")) {
                try {
                    System.out.println(ctx.render(ctx.parser().parseField(a.sql)));
                } catch (ParserException e2) {
                    e = e1.position() >= e2.position() ? e1 : e2;
                }
            }
            System.out.println(e.getMessage());
        }
    }

    private static final <E extends Enum<E>> E parse(Class<E> cls, String str) {
        try {
            return (E) Enum.valueOf(cls, str.toUpperCase());
        } catch (IllegalArgumentException e) {
            invalid(str, cls);
            throw e;
        }
    }

    private static final <E extends Enum<E>> Args parse(String[] args) {
        Args result = new Args();
        int i = 0;
        while (i < args.length) {
            Class<? extends Enum<?>> enumArgument = null;
            try {
                if ("-f".equals(args[i]) || "--formatted".equals(args[i])) {
                    result.formatted = true;
                } else if ("-k".equals(args[i]) || "--keyword".equals(args[i])) {
                    i++;
                    result.keywords = (RenderKeywordCase) parse(RenderKeywordCase.class, args[i]);
                } else if ("-i".equals(args[i]) || "--identifier".equals(args[i])) {
                    i++;
                    result.name = (RenderNameCase) parse(RenderNameCase.class, args[i]);
                } else if ("-Q".equals(args[i]) || "--quoted".equals(args[i])) {
                    i++;
                    result.quoted = (RenderQuotedNames) parse(RenderQuotedNames.class, args[i]);
                } else if ("-F".equals(args[i]) || "--from-dialect".equals(args[i])) {
                    i++;
                    result.fromDialect = (SQLDialect) parse(SQLDialect.class, args[i]);
                } else if ("-t".equals(args[i]) || "-T".equals(args[i]) || "--to-dialect".equals(args[i])) {
                    i++;
                    result.toDialect = (SQLDialect) parse(SQLDialect.class, args[i]);
                } else if ("--parse-date-format".equals(args[i])) {
                    i++;
                    result.parseDateFormat = args[i];
                } else if ("--parse-ignore-comments".equals(args[i])) {
                    result.parseIgnoreComments = true;
                } else if ("--parse-ignore-comment-start".equals(args[i])) {
                    i++;
                    result.parseIgnoreCommentStart = args[i];
                } else if ("--parse-ignore-comment-stop".equals(args[i])) {
                    i++;
                    result.parseIgnoreCommentStop = args[i];
                } else if ("--parse-locale".equals(args[i])) {
                    i++;
                    result.parseLocale = Locale.forLanguageTag(args[i]);
                } else if ("--parse-name-case".equals(args[i])) {
                    i++;
                    result.parseNameCase = (ParseNameCase) parse(ParseNameCase.class, args[i]);
                } else if ("--parse-named-param-prefix".equals(args[i])) {
                    i++;
                    result.parseNamedParamPrefix = args[i];
                } else if ("--parse-set-commands".equals(args[i])) {
                    result.parseSetCommands = true;
                } else if ("--parse-retain-comments-between-queries".equals(args[i])) {
                    result.parseRetainCommentsBetweenQueries = true;
                } else if ("--parse-timestamp-format".equals(args[i])) {
                    i++;
                    result.parseTimestampFormat = args[i];
                } else if ("--parse-unknown-functions".equals(args[i])) {
                    i++;
                    result.parseUnknownFunctions = (ParseUnknownFunctions) parse(ParseUnknownFunctions.class, args[i]);
                } else if ("--parse-unsupported-syntax".equals(args[i])) {
                    i++;
                    result.parseUnsupportedSyntax = (ParseUnsupportedSyntax) parse(ParseUnsupportedSyntax.class, args[i]);
                } else if ("--render-coalesce-to-empty-string-in-concat".equals(args[i])) {
                    result.renderCoalesceToEmptyStringInConcat = true;
                } else if ("--render-optional-inner-keyword".equals(args[i])) {
                    i++;
                    result.renderOptionalInnerKeyword = (RenderOptionalKeyword) parse(RenderOptionalKeyword.class, args[i]);
                } else if ("--render-optional-outer-keyword".equals(args[i])) {
                    i++;
                    result.renderOptionalOuterKeyword = (RenderOptionalKeyword) parse(RenderOptionalKeyword.class, args[i]);
                } else if ("--render-optional-as-keyword-for-field-aliases".equals(args[i])) {
                    i++;
                    result.renderOptionalAsKeywordForFieldAliases = (RenderOptionalKeyword) parse(RenderOptionalKeyword.class, args[i]);
                } else if ("--render-optional-as-keyword-for-table-aliases".equals(args[i])) {
                    i++;
                    result.renderOptionalAsKeywordForTableAliases = (RenderOptionalKeyword) parse(RenderOptionalKeyword.class, args[i]);
                } else if ("--transform-patterns".equals(args[i])) {
                    result.transformPatterns = true;
                } else if ("--transform-ansi-join-to-table-lists".equals(args[i])) {
                    result.transformAnsiJoinToTableLists = true;
                } else if ("--transform-qualify".equals(args[i])) {
                    i++;
                    result.transformQualify = (Transformation) parse(Transformation.class, args[i]);
                } else if ("--transform-rownum".equals(args[i])) {
                    i++;
                    result.transformRownum = (Transformation) parse(Transformation.class, args[i]);
                } else if ("--transform-group-by-column-index".equals(args[i])) {
                    i++;
                    result.transformGroupByColumnIndex = (Transformation) parse(Transformation.class, args[i]);
                } else if ("--transform-inline-cte".equals(args[i])) {
                    i++;
                    result.transformInlineCTE = (Transformation) parse(Transformation.class, args[i]);
                } else if ("--transform-table-lists-to-ansi-join".equals(args[i])) {
                    result.transformTableListsToAnsiJoin = true;
                } else if ("--transform-unneeded-arithmetic".equals(args[i])) {
                    i++;
                    result.transformUnneededArithmetic = (TransformUnneededArithmeticExpressions) parse(TransformUnneededArithmeticExpressions.class, args[i]);
                } else if ("-S".equals(args[i]) || "--schema".equals(args[i])) {
                    i++;
                    result.schema = args[i];
                } else if ("-s".equals(args[i]) || "--sql".equals(args[i])) {
                    i++;
                    result.sql = args[i];
                } else if ("-I".equals(args[i]) || "--interactive".equals(args[i])) {
                    result.interactive = true;
                } else if ("-h".equals(args[i]) || "--help".equals(args[i])) {
                    help();
                    result.done = true;
                } else {
                    System.out.println("Unknown flag: " + args[i] + ". Use -h or --help");
                    throw new RuntimeException();
                }
                i++;
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Flag " + args[i - 1] + " requires <" + (0 != 0 ? enumArgument.getName() : "Unknown") + "> argument");
                throw e;
            }
        }
        return result;
    }

    private static final void invalid(String string, Class<? extends Enum<?>> type) {
        System.out.println("Invalid " + type.getSimpleName() + ": " + string);
        System.out.println("Possible values:");
        for (Enum<?> e : (Enum[]) type.getEnumConstants()) {
            System.out.println("  " + e.name());
        }
    }

    private static final void help() {
        System.out.println("Usage:");
        System.out.println("  -f / --formatted                                                    Format output SQL");
        System.out.println("  -h / --help                                                         Display this help");
        System.out.println("  -k / --keyword                                  <RenderKeywordCase> Specify the output keyword case (org.jooq.conf.RenderKeywordCase)");
        System.out.println("  -i / --identifier                               <RenderNameCase>    Specify the output identifier case (org.jooq.conf.RenderNameCase)");
        System.out.println("  -Q / --quoted                                   <RenderQuotedNames> Specify the output identifier quoting (org.jooq.conf.RenderQuotedNames)");
        System.out.println("  -F / --from-dialect                             <SQLDialect>        Specify the input dialect (org.jooq.SQLDialect)");
        System.out.println("  -T / --to-dialect                               <SQLDialect>        Specify the output dialect (org.jooq.SQLDialect)");
        System.out.println("  -S / --schema                                   <String>            Specify the input SQL schema");
        System.out.println("  -s / --sql                                      <String>            Specify the input SQL string");
        System.out.println("");
        System.out.println("Additional flags:");
        System.out.println("  --parse-date-format                             <String>");
        System.out.println("  --parse-locale                                  <Locale>");
        System.out.println("  --parse-name-case                               <ParseNameCase>");
        System.out.println("  --parse-named-param-prefix                      <String>");
        System.out.println("  --parse-retain-comments-between-queries");
        System.out.println("  --parse-set-commands");
        System.out.println("  --parse-timestamp-format                        <String>");
        System.out.println("  --parse-unknown-functions                       <ParseUnknownFunctions>");
        System.out.println("  --parse-unsupported-syntax                      <ParseUnsupportedSyntax>");
        System.out.println("  --render-optional-inner-keyword                 <RenderOptionalKeyword>");
        System.out.println("  --render-optional-outer-keyword                 <RenderOptionalKeyword>");
        System.out.println("  --render-optional-as-keyword-for-field-aliases  <RenderOptionalKeyword>");
        System.out.println("  --render-optional-as-keyword-for-table-aliases  <RenderOptionalKeyword>");
        System.out.println("");
        System.out.println("Commercial distribution only features:");
        System.out.println("  --render-coalesce-to-empty-string-in-concat");
        System.out.println("  --transform-patterns");
        System.out.println("  --transform-ansi-join-to-table-lists");
        System.out.println("  --transform-qualify                             <Transformation>");
        System.out.println("  --transform-rownum                              <Transformation>");
        System.out.println("  --transform-group-by-column-index               <Transformation>");
        System.out.println("  --transform-inline-cte                          <Transformation>");
        System.out.println("  --transform-table-lists-to-ansi-join");
        System.out.println("  --transform-unneeded-arithmetic                 <TransformUnneededArithmeticExpressions>");
        System.out.println("");
        System.out.println("  -I / --interactive                                               Start interactive mode");
    }

    private static final void helpInteractive() {
        System.out.println("Usage:");
        System.out.println("  /d  or  /display                                                   Display arguments");
        System.out.println("  /f  or  /formatted                             <boolean>           Format output SQL");
        System.out.println("  /h  or  /help                                                      Display this help");
        System.out.println("  /k  or  /keyword                               <RenderKeywordCase> Specify the output keyword case (org.jooq.conf.RenderKeywordCase)");
        System.out.println("  /i  or  /identifier                            <RenderNameCase>    Specify the output identifier case (org.jooq.conf.RenderNameCase)");
        System.out.println("  /Q  or  /quoted                                <RenderQuotedNames> Specify the output identifier quoting (org.jooq.conf.RenderQuotedNames)");
        System.out.println("  /F  or  /from-dialect                          <SQLDialect>        Specify the input dialect (org.jooq.SQLDialect)");
        System.out.println("  /T  or  /to-dialect                            <SQLDialect>        Specify the output dialect (org.jooq.SQLDialect)");
        System.out.println("  /S  or  /schema                                <String>            Specify the input SQL schema");
        System.out.println("                                                 <String>            Specify the input SQL string");
        System.out.println("");
        System.out.println("Additional flags:");
        System.out.println("  /parse-date-format                             <String>");
        System.out.println("  /parse-locale                                  <Locale>");
        System.out.println("  /parse-name-case                               <ParseNameCase>");
        System.out.println("  /parse-named-param-prefix                      <String>");
        System.out.println("  /parse-retain-comments-between-queries         <boolean>");
        System.out.println("  /parse-set-commands                            <boolean>");
        System.out.println("  /parse-timestamp-format                        <String>");
        System.out.println("  /parse-unknown-functions                       <ParseUnknownFunctions>");
        System.out.println("  /parse-unsupported-syntax                      <ParseUnsupportedSyntax>");
        System.out.println("  /render-optional-inner-keyword                 <RenderOptionalKeyword>");
        System.out.println("  /render-optional-outer-keyword                 <RenderOptionalKeyword>");
        System.out.println("  /render-optional-as-keyword-for-field-aliases  <RenderOptionalKeyword>");
        System.out.println("  /render-optional-as-keyword-for-table-aliases  <RenderOptionalKeyword>");
        System.out.println("");
        System.out.println("Commercial distribution only features:");
        System.out.println("  /render-coalesce-to-empty-string-in-concat     <boolean>");
        System.out.println("  /transform-patterns                            <boolean>");
        System.out.println("  /transform-ansi-join-to-table-lists            <boolean>");
        System.out.println("  /transform-qualify                             <Transformation>");
        System.out.println("  /transform-rownum                              <Transformation>");
        System.out.println("  /transform-group-by-column-index               <Transformation>");
        System.out.println("  /transform-inline-cte                          <Transformation>");
        System.out.println("  /transform-table-lists-to-ansi-join            <boolean>");
        System.out.println("  /transform-unneeded-arithmetic                 <TransformUnneededArithmeticExpressions>");
        System.out.println("");
        System.out.println("  /q  or  /quit   Quit");
        System.out.println("  /e  or  /exit   Also quit");
    }
}
