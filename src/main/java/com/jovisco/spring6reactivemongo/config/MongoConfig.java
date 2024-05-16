package com.jovisco.spring6reactivemongo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;

import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.MongoClientSettings.Builder;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;

import static java.util.Collections.singletonList;

@Configuration
public class MongoConfig extends AbstractReactiveMongoConfiguration {

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create();
    }

    @Override
    protected String getDatabaseName() {
        return "sf6test";
    }

    @Override
    protected void configureClientSettings(Builder builder) {
        builder
            .credential(MongoCredential.createCredential("root", "admin", "example".toCharArray()))
            .applyToClusterSettings(
                settings -> settings.hosts(
                    singletonList(new ServerAddress("127.0.0.1", 27017))
                )
            );
    }

    
}
