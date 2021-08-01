package com.example.audit;

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.postgresql.PostgresqlConnectionFactoryProvider;
import io.r2dbc.postgresql.codec.EnumCodec;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;

import java.util.Map;

public class PgConnectionFactories {
    static ConnectionFactory fromUrl() {
        return ConnectionFactories.get("r2dbc:postgres://temporal:temporal@localhost/blogdb");
    }

    static ConnectionFactory fromOptions() {
        var options = ConnectionFactoryOptions.builder()
                .option(ConnectionFactoryOptions.HOST, "localhost")
                .option(ConnectionFactoryOptions.DATABASE, "blogdb")
                .option(ConnectionFactoryOptions.USER, "temporal")
                .option(ConnectionFactoryOptions.PASSWORD, "temporal")
                .option(ConnectionFactoryOptions.DRIVER, "postgresql")
                .option(PostgresqlConnectionFactoryProvider.OPTIONS, Map.of("lock_timeout", "30s"))
                .build();
        return ConnectionFactories.get(options);
    }

    static ConnectionFactory pgConnectionFactory() {
        return new PostgresqlConnectionFactory(
                PostgresqlConnectionConfiguration.builder()
                        .host("localhost")
                        .database("blogdb")
                        .username("temporal")
                        .password("temporal")
                        //.codecRegistrar(EnumCodec.builder().withEnum("post_status", Post.Status.class).build())
                        .build()
        );
    }
}
