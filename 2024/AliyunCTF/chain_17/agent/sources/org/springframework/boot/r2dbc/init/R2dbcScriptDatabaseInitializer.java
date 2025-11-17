package org.springframework.boot.r2dbc.init;

import io.r2dbc.spi.ConnectionFactory;
import java.util.Iterator;
import org.springframework.boot.r2dbc.EmbeddedDatabaseConnection;
import org.springframework.boot.sql.init.AbstractScriptDatabaseInitializer;
import org.springframework.boot.sql.init.DatabaseInitializationSettings;
import org.springframework.core.io.Resource;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/r2dbc/init/R2dbcScriptDatabaseInitializer.class */
public class R2dbcScriptDatabaseInitializer extends AbstractScriptDatabaseInitializer {
    private final ConnectionFactory connectionFactory;

    public R2dbcScriptDatabaseInitializer(ConnectionFactory connectionFactory, DatabaseInitializationSettings settings) {
        super(settings);
        this.connectionFactory = connectionFactory;
    }

    @Override // org.springframework.boot.sql.init.AbstractScriptDatabaseInitializer
    protected boolean isEmbeddedDatabase() {
        return EmbeddedDatabaseConnection.isEmbedded(this.connectionFactory);
    }

    @Override // org.springframework.boot.sql.init.AbstractScriptDatabaseInitializer
    protected void runScripts(AbstractScriptDatabaseInitializer.Scripts scripts) {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.setContinueOnError(scripts.isContinueOnError());
        populator.setSeparator(scripts.getSeparator());
        if (scripts.getEncoding() != null) {
            populator.setSqlScriptEncoding(scripts.getEncoding().name());
        }
        Iterator<Resource> it = scripts.iterator();
        while (it.hasNext()) {
            Resource script = it.next();
            populator.addScript(script);
        }
        populator.populate(this.connectionFactory).block();
    }
}
