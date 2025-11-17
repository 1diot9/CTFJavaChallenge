package org.jooq;

import org.apache.logging.log4j.message.StructuredDataId;
import org.jooq.conf.RenderKeywordCase;
import org.jooq.conf.RenderNameCase;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/DiffCLI.class */
public final class DiffCLI {

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/DiffCLI$Args.class */
    public static final class Args {
        String sql1;
        String sql2;
        RenderKeywordCase keywords = RenderKeywordCase.LOWER;
        RenderNameCase name = RenderNameCase.LOWER;
        SQLDialect toDialect = SQLDialect.DEFAULT;
        SQLDialect fromDialect = SQLDialect.DEFAULT;
        boolean formatted;
        boolean done;
    }

    public static final void main(String... args) throws Exception {
        CLIUtil.main("https://www.jooq.org/doc/latest/manual/sql-building/schema-diff-cli/", () -> {
            Settings settings = new Settings();
            Args a = parse(args);
            settings(a, settings);
            DSLContext ctx = ctx(a, settings);
            if (!a.done) {
                if (a.toDialect == null || a.sql1 == null || a.sql2 == null) {
                    System.out.println("Mandatory arguments: -T and -1, -2. Use -h for help");
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
        if (a.formatted) {
            settings.setRenderFormatted(true);
        }
        if (a.keywords != null) {
            settings.setRenderKeywordCase(a.keywords);
        }
        if (a.name != null) {
            settings.setRenderNameCase(a.name);
        }
        if (a.fromDialect != null) {
            settings.setParseDialect(a.fromDialect);
        }
    }

    private static final void render(DSLContext ctx, Args a) {
        String sql1 = a.sql1.trim();
        String sql2 = a.sql2.trim();
        System.out.println(ctx.render(ctx.meta(sql1).migrateTo(ctx.meta(sql2))));
    }

    private static final Args parse(String[] args) {
        Args result = new Args();
        int i = 0;
        while (i < args.length) {
            if ("-f".equals(args[i]) || "--formatted".equals(args[i])) {
                result.formatted = true;
            } else if ("-k".equals(args[i]) || "--keyword".equals(args[i])) {
                try {
                    i++;
                    result.keywords = RenderKeywordCase.valueOf(args[i].toUpperCase());
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("Flag -k / --keyword requires <RenderKeywordCase> argument");
                    throw e;
                } catch (IllegalArgumentException e2) {
                    invalid(args[i], RenderKeywordCase.class);
                    throw e2;
                }
            } else if ("-i".equals(args[i]) || "--identifier".equals(args[i])) {
                try {
                    i++;
                    result.keywords = RenderKeywordCase.valueOf(args[i].toUpperCase());
                } catch (ArrayIndexOutOfBoundsException e3) {
                    System.out.println("Flag -i / --identifier requires <RenderNameCase> argument");
                    throw e3;
                } catch (IllegalArgumentException e4) {
                    invalid(args[i], RenderKeywordCase.class);
                    throw e4;
                }
            } else if ("-F".equals(args[i]) || "--from-dialect".equals(args[i])) {
                try {
                    i++;
                    result.fromDialect = SQLDialect.valueOf(args[i].toUpperCase());
                } catch (ArrayIndexOutOfBoundsException e5) {
                    System.out.println("Flag -F / --from-dialect requires <SQLDialect> argument");
                    throw e5;
                } catch (IllegalArgumentException e6) {
                    invalid(args[i], SQLDialect.class);
                    throw e6;
                }
            } else if ("-t".equals(args[i]) || "-T".equals(args[i]) || "--to-dialect".equals(args[i])) {
                try {
                    i++;
                    result.toDialect = SQLDialect.valueOf(args[i].toUpperCase());
                } catch (ArrayIndexOutOfBoundsException e7) {
                    System.out.println("Flag -T / --to-dialect requires <SQLDialect> argument");
                    throw e7;
                } catch (IllegalArgumentException e8) {
                    invalid(args[i], SQLDialect.class);
                    throw e8;
                }
            } else if (StructuredDataId.RESERVED.equals(args[i]) || "--sql1".equals(args[i])) {
                try {
                    i++;
                    result.sql1 = args[i];
                } catch (ArrayIndexOutOfBoundsException e9) {
                    System.out.println("Flag -1 / --sql1 requires <String> argument");
                    throw e9;
                }
            } else if ("-2".equals(args[i]) || "--sql2".equals(args[i])) {
                try {
                    i++;
                    result.sql2 = args[i];
                } catch (ArrayIndexOutOfBoundsException e10) {
                    System.out.println("Flag -2 / --sql2 requires <String> argument");
                    throw e10;
                }
            } else if ("-h".equals(args[i]) || "--help".equals(args[i])) {
                help();
                result.done = true;
            } else {
                System.out.println("Unknown flag: " + args[i] + ". Use -h or --help");
                throw new RuntimeException();
            }
            i++;
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
        System.out.println("  -f / --formatted                        Format output SQL");
        System.out.println("  -h / --help                             Display this help");
        System.out.println("  -k / --keyword      <RenderKeywordCase> Specify the output keyword case (org.jooq.conf.RenderKeywordCase)");
        System.out.println("  -i / --identifier   <RenderNameCase>    Specify the output identifier case (org.jooq.conf.RenderNameCase)");
        System.out.println("  -F / --from-dialect <SQLDialect>        Specify the input dialect (org.jooq.SQLDialect)");
        System.out.println("  -T / --to-dialect   <SQLDialect>        Specify the output dialect (org.jooq.SQLDialect)");
        System.out.println("  -1 / --sql1         <String>            Specify the input SQL string 1 (from SQL)");
        System.out.println("  -2 / --sql2         <String>            Specify the input SQL string 2 (to SQL)");
    }
}
