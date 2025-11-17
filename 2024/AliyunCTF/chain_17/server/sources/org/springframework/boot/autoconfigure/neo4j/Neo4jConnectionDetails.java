package org.springframework.boot.autoconfigure.neo4j;

import java.net.URI;
import org.neo4j.driver.AuthToken;
import org.neo4j.driver.AuthTokenManager;
import org.neo4j.driver.AuthTokens;
import org.springframework.boot.autoconfigure.service.connection.ConnectionDetails;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/neo4j/Neo4jConnectionDetails.class */
public interface Neo4jConnectionDetails extends ConnectionDetails {
    default URI getUri() {
        return URI.create("bolt://localhost:7687");
    }

    default AuthToken getAuthToken() {
        return AuthTokens.none();
    }

    default AuthTokenManager getAuthTokenManager() {
        return null;
    }
}
