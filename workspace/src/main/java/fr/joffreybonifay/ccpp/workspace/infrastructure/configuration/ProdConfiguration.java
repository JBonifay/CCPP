package fr.joffreybonifay.ccpp.workspace.infrastructure.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.joffreybonifay.ccpp.shared.event.EventRepository;
import fr.joffreybonifay.ccpp.shared.eventstore.EventStore;
import fr.joffreybonifay.ccpp.shared.eventstore.JpaEventStore;
import fr.joffreybonifay.ccpp.shared.outbox.OutboxProcessor;
import fr.joffreybonifay.ccpp.shared.outbox.OutboxRepository;
import fr.joffreybonifay.ccpp.shared.outbox.OutboxWorker;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableJpaRepositories(basePackages = {"fr.joffreybonifay.ccpp.shared"})
@EntityScan(basePackages = {"fr.joffreybonifay.ccpp.shared"})
@EnableScheduling
@Configuration
@Profile("default")
public class ProdConfiguration {

    @Bean
    @Profile("default")
    EventStore jpaEventStore(
            EventRepository eventRepository,
            OutboxRepository outboxRepository,
            ObjectMapper objectMapper
    ) {
        return new JpaEventStore(eventRepository, outboxRepository, objectMapper);
    }

    @Bean
    OutboxProcessor outboxProcessor(
            OutboxWorker outboxWorker,
            OutboxRepository outboxRepository
    ) {
        return new OutboxProcessor(outboxWorker, outboxRepository);
    }

    @Bean
    OutboxWorker outboxWorker(
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper,
            OutboxRepository outboxRepository
    ) {
        return new OutboxWorker(outboxRepository, kafkaTemplate, objectMapper, "workspace-events");
    }
}
