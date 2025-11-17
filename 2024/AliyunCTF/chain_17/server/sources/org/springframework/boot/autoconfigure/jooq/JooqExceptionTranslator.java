package org.springframework.boot.autoconfigure.jooq;

import java.sql.SQLException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jooq.ExecuteContext;
import org.jooq.ExecuteListener;
import org.jooq.SQLDialect;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.jdbc.support.SQLStateSQLExceptionTranslator;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jooq/JooqExceptionTranslator.class */
public class JooqExceptionTranslator implements ExecuteListener {
    private static final Log logger = LogFactory.getLog((Class<?>) JooqExceptionTranslator.class);

    @Override // org.jooq.ExecuteListener
    public void exception(ExecuteContext context) {
        SQLExceptionTranslator translator = getTranslator(context);
        SQLException sqlException = context.sqlException();
        while (true) {
            SQLException exception = sqlException;
            if (exception != null) {
                handle(context, translator, exception);
                sqlException = exception.getNextException();
            } else {
                return;
            }
        }
    }

    private SQLExceptionTranslator getTranslator(ExecuteContext context) {
        String dbName;
        SQLDialect dialect = context.configuration().dialect();
        if (dialect != null && dialect.thirdParty() != null && (dbName = dialect.thirdParty().springDbName()) != null) {
            return new SQLErrorCodeSQLExceptionTranslator(dbName);
        }
        return new SQLStateSQLExceptionTranslator();
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void handle(ExecuteContext context, SQLExceptionTranslator translator, SQLException exception) {
        DataAccessException translated = translate(context, translator, exception);
        if (exception.getNextException() == null) {
            if (translated != null) {
                context.exception(translated);
                return;
            }
            return;
        }
        logger.error("Execution of SQL statement failed.", translated != null ? translated : exception);
    }

    private DataAccessException translate(ExecuteContext context, SQLExceptionTranslator translator, SQLException exception) {
        return translator.translate("jOOQ", context.sql(), exception);
    }
}
