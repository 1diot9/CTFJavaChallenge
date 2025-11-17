package org.jooq.impl;

import java.sql.Connection;
import java.util.Locale;
import java.util.regex.Pattern;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Meta;
import org.jooq.MetaProvider;
import org.jooq.Name;
import org.jooq.QueryPart;
import org.jooq.Source;
import org.jooq.VisitListener;
import org.jooq.conf.RenderNameCase;
import org.jooq.conf.Settings;
import org.jooq.conf.SettingsTools;
import org.jooq.exception.DataAccessException;
import org.jooq.tools.JooqLogger;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/TranslatingMetaProvider.class */
public final class TranslatingMetaProvider implements MetaProvider {
    private static final JooqLogger log = JooqLogger.getLogger((Class<?>) TranslatingMetaProvider.class);
    private static final Pattern P_NAME = Pattern.compile("(?s:.*?\"([^\"]*)\".*)");
    private final Configuration configuration;
    private final Source[] scripts;

    public TranslatingMetaProvider(Configuration configuration, Source... scripts) {
        this.configuration = Tools.configuration(configuration);
        this.scripts = scripts;
    }

    @Override // org.jooq.MetaProvider
    public Meta provide() {
        DDLDatabaseInitializer initializer = new DDLDatabaseInitializer();
        try {
            for (Source script : this.scripts) {
                initializer.loadScript(script);
            }
            Snapshot snapshot = new Snapshot(new DefaultMetaProvider(this.configuration.derive().set(initializer.connection).set(this.configuration.settings().getInterpreterDialect())).provide());
            initializer.close();
            return snapshot;
        } catch (Throwable th) {
            try {
                initializer.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/TranslatingMetaProvider$DDLDatabaseInitializer.class */
    final class DDLDatabaseInitializer implements AutoCloseable {
        private Connection connection;
        private DSLContext ctx;

        private DDLDatabaseInitializer() {
            try {
                Settings settings = TranslatingMetaProvider.this.configuration.settings();
                this.connection = TranslatingMetaProvider.this.configuration.interpreterConnectionProvider().acquire();
                this.ctx = DSL.using(this.connection, settings.getInterpreterDialect(), settings);
                this.ctx.data("org.jooq.ddl.ignore-storage-clauses", true);
                this.ctx.data("org.jooq.ddl.parse-for-ddldatabase", true);
                RenderNameCase nameCase = settings.getRenderNameCase();
                Locale locale = SettingsTools.interpreterLocale(this.ctx.settings());
                if (nameCase != null && nameCase != RenderNameCase.AS_IS) {
                    this.ctx.configuration().set(VisitListener.onVisitStart(c -> {
                        QueryPart patt0$temp = c.queryPart();
                        if (patt0$temp instanceof Name) {
                            Name n = (Name) patt0$temp;
                            Name[] parts = n.parts();
                            boolean changed = false;
                            int i = 0;
                            while (i < parts.length) {
                                Name replacement = parts[i];
                                switch (nameCase) {
                                    case LOWER:
                                        replacement = DSL.quotedName(parts[i].first().toLowerCase(locale));
                                        break;
                                    case UPPER:
                                        replacement = DSL.quotedName(parts[i].first().toUpperCase(locale));
                                        break;
                                }
                                if (!replacement.equals(parts[i])) {
                                    parts[i] = replacement;
                                    changed = true;
                                }
                                i++;
                            }
                            if (changed) {
                                c.queryPart(DSL.name(parts));
                            }
                        }
                    }));
                }
            } catch (Exception e) {
                throw new DataAccessException("Error while exporting schema", e);
            }
        }

        /* JADX WARN: Code restructure failed: missing block: B:15:0x0061, code lost:            if ((r0 instanceof org.jooq.ResultQuery) == false) goto L14;     */
        /* JADX WARN: Code restructure failed: missing block: B:16:0x0064, code lost:            org.jooq.impl.TranslatingMetaProvider.log.info("\n" + java.lang.String.valueOf(((org.jooq.ResultQuery) r0).fetch()));     */
        /* JADX WARN: Code restructure failed: missing block: B:19:0x007f, code lost:            org.jooq.impl.TranslatingMetaProvider.log.info("Update count: " + r0.execute());     */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        private final void loadScript(org.jooq.Source r6) {
            /*
                Method dump skipped, instructions count: 276
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: org.jooq.impl.TranslatingMetaProvider.DDLDatabaseInitializer.loadScript(org.jooq.Source):void");
        }

        @Override // java.lang.AutoCloseable
        public void close() {
            TranslatingMetaProvider.this.configuration.interpreterConnectionProvider().release(this.connection);
        }
    }
}
