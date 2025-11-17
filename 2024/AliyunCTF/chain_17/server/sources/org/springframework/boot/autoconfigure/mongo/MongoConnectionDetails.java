package org.springframework.boot.autoconfigure.mongo;

import com.mongodb.ConnectionString;
import org.springframework.boot.autoconfigure.service.connection.ConnectionDetails;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/mongo/MongoConnectionDetails.class */
public interface MongoConnectionDetails extends ConnectionDetails {
    ConnectionString getConnectionString();

    default GridFs getGridFs() {
        return null;
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/mongo/MongoConnectionDetails$GridFs.class */
    public interface GridFs {
        String getDatabase();

        String getBucket();

        static GridFs of(final String database, final String bucket) {
            return new GridFs() { // from class: org.springframework.boot.autoconfigure.mongo.MongoConnectionDetails.GridFs.1
                @Override // org.springframework.boot.autoconfigure.mongo.MongoConnectionDetails.GridFs
                public String getDatabase() {
                    return database;
                }

                @Override // org.springframework.boot.autoconfigure.mongo.MongoConnectionDetails.GridFs
                public String getBucket() {
                    return bucket;
                }
            };
        }
    }
}
