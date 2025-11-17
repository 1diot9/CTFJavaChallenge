package org.springframework.boot.jdbc.init;

import java.util.Iterator;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.sql.init.AbstractScriptDatabaseInitializer;
import org.springframework.boot.sql.init.DatabaseInitializationSettings;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/jdbc/init/DataSourceScriptDatabaseInitializer.class */
public class DataSourceScriptDatabaseInitializer extends AbstractScriptDatabaseInitializer {
    private static final Log logger = LogFactory.getLog((Class<?>) DataSourceScriptDatabaseInitializer.class);
    private final DataSource dataSource;

    public DataSourceScriptDatabaseInitializer(DataSource dataSource, DatabaseInitializationSettings settings) {
        super(settings);
        this.dataSource = dataSource;
    }

    protected final DataSource getDataSource() {
        return this.dataSource;
    }

    @Override // org.springframework.boot.sql.init.AbstractScriptDatabaseInitializer
    protected boolean isEmbeddedDatabase() {
        try {
            return EmbeddedDatabaseConnection.isEmbedded(this.dataSource);
        } catch (Exception ex) {
            logger.debug("Could not determine if datasource is embedded", ex);
            return false;
        }
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
            Resource resource = it.next();
            populator.addScript(resource);
        }
        customize(populator);
        DatabasePopulatorUtils.execute(populator, this.dataSource);
    }

    protected void customize(ResourceDatabasePopulator populator) {
    }
}
