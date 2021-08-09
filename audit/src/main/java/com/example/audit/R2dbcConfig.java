package com.example.audit;

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.postgresql.codec.EnumCodec;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;

import java.util.List;

class R2dbcConfig extends AbstractR2dbcConfiguration {
    @Bean
    @Primary
    public ConnectionFactory connectionFactory() {
        return new PostgresqlConnectionFactory(
                PostgresqlConnectionConfiguration.builder()
                        .host("localhost")
                        .database("blogdb")
                        .username("user")
                        .password("password")
                        .codecRegistrar(EnumCodec.builder().withEnum("post_status", Post.Status.class).build())
                        .build());
    }

    @Override
    protected List<Object> getCustomConverters() {
        return List.of(new PostReadingConverter(), new PostStatusWritingConverter());
    }
}
