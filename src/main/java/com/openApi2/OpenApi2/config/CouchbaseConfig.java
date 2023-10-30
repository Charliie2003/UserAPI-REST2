package com.openApi2.OpenApi2.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.couchbase.config.AbstractCouchbaseConfiguration;

@Configuration
public class CouchbaseConfig extends AbstractCouchbaseConfiguration {
    @Override
    public String getConnectionString() {
        return "couchbase://127.0.0.1";
    }

    @Override
    public String getUserName() {
        return "Charlie";
    }

    @Override
    public String getPassword() {
        return "Cha2022*";
    }

    @Override
    public String getBucketName() {
        return "BaseDatosSpring";
    }
}
