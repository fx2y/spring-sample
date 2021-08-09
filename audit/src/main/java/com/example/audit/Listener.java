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
import java.time.Duration;

@Component
@Slf4j
class Listener {
    @Autowired
    @Qualifier("pgConnectionFactory")
    ConnectionFactory pgConnectionFactory;

    PostgresqlConnection receiver;

    @PostConstruct
    public void initialize() throws InterruptedException {
        receiver = Mono.from(pgConnectionFactory.create())
                .cast(PostgresqlConnection.class)
                .block();
        receiver.createStatement("LISTEN mymessage")
                .execute()
                .flatMap(PostgresqlResult::getRowsUpdated)
                .log("listen::")
                .subscribe();
        receiver.getNotifications()
                .delayElements(Duration.ofSeconds(1))
                .log()
                .subscribe(data -> log.info("notifications: {}", data));
    }

    @PreDestroy
    public void destroy() {
        receiver.close().subscribe();
    }
}
