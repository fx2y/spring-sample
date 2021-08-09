package com.example.audit;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.postgresql.codec.EnumCodec;
import io.r2dbc.postgresql.codec.Json;
import io.r2dbc.spi.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.r2dbc.connection.init.CompositeDatabasePopulator;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.transaction.reactive.TransactionalOperator;

@SpringBootApplication
@Slf4j
public class AuditApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuditApplication.class, args);
    }

    @Bean
    ApplicationRunner initialize(DatabaseClient databaseClient, PostRepository posts, CommentRepository comments, TransactionalOperator operator) {
        log.info("start data initialization...");
        return args -> {
            databaseClient
                    .sql("INSERT INTO  posts (title, content, metadata) VALUES (:title, :content, :metadata)")
                    .filter((statement, executeFunction) -> statement.returnGeneratedValues("id").execute())
                    .bind("title", "my first post")
                    .bind("content", "content of my first post")
                    .bind("metadata", Json.of("{\"tags\":[\"spring\", \"r2dbc\"]}"))
                    .fetch()
                    .first()
                    .subscribe(
                            data -> log.info("inserted data : {}", data),
                            error -> log.error("error: {}", error));
            posts
                    .save(Post.builder().title("another post").content("content of another post").build())
                    .map(p -> {
                        p.setTitle("new Title");
                        return p;
                    })
                    .flatMap(posts::save)
                    .flatMap(saved -> comments
                            .save(Comment.builder()
                                    .content("dummy contents")
                                    .postId(saved.getId())
                                    .build()))
                    .log()
                    .then()
                    .thenMany(posts.findAll())
                    .as(operator::transactional)
                    .subscribe(
                            data -> log.info("saved data: {}", data),
                            err -> log.error("err: {}", err));
        };
    }

    @Bean
    @Qualifier("pgConnectionFactory")
    public ConnectionFactory pgConnectionFactory() {
        return new PostgresqlConnectionFactory(
                PostgresqlConnectionConfiguration.builder()
                        .host("localhost")
                        .database("blogdb")
                        .username("user")
                        .password("password")
                        .codecRegistrar(EnumCodec.builder().withEnum("post_status", Post.Status.class).build())
                        .build());
    }

    @Bean
    public ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) {
        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);

        CompositeDatabasePopulator populator = new CompositeDatabasePopulator();
        populator.addPopulators(new ResourceDatabasePopulator(new ClassPathResource("schema.sql")));
        populator.addPopulators(new ResourceDatabasePopulator(new ClassPathResource("data.sql")));
        initializer.setDatabasePopulator(populator);

        return initializer;
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> {
            builder.serializationInclusion(JsonInclude.Include.NON_EMPTY);
            builder.featuresToDisable(
                    SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
                    SerializationFeature.FAIL_ON_EMPTY_BEANS,
                    DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES,
                    DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            builder.featuresToEnable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        };
    }
}