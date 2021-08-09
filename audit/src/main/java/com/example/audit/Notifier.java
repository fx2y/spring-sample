package com.example.audit;

import io.r2dbc.postgresql.api.PostgresqlConnection;
import io.r2dbc.postgresql.api.PostgresqlResult;
import io.r2dbc.spi.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.LocalDateTime;

@Component
@Slf4j
class Notifier {
    @Autowired
    @Qualifier("pgConnectionFactory")
    ConnectionFactory pgConnectionFactory;

    PostgresqlConnection sender;

    @PostConstruct
    public void iniitalize() throws InterruptedException {
        sender = Mono.from(pgConnectionFactory.create())
                .cast(PostgresqlConnection.class)
                .block();
    }

    public Mono<Void> send() {
        return sender.createStatement("NOTIFY mymessage, 'Hello world at " + LocalDateTime.now() + "'")
                .execute()
                .flatMap(PostgresqlResult::getRowsUpdated)
                .log("sending notification::")
                .then();
    }

    @PreDestroy
    public void destroy() {
        sender.close().subscribe();
    }
}
