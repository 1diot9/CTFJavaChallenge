package org.springframework.boot.autoconfigure.couchbase;

import org.springframework.boot.autoconfigure.service.connection.ConnectionDetails;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/couchbase/CouchbaseConnectionDetails.class */
public interface CouchbaseConnectionDetails extends ConnectionDetails {
    String getConnectionString();

    String getUsername();

    String getPassword();
}
