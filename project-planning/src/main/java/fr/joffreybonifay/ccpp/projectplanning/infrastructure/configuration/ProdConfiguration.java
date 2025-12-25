package fr.joffreybonifay.ccpp.projectplanning.infrastructure.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.joffreybonifay.ccpp.shared.eventpublisher.EventPublisher;
import fr.joffreybonifay.ccpp.shared.eventpublisher.SpringEventPublisher;
import fr.joffreybonifay.ccpp.shared.eventstore.EventStore;
import fr.joffreybonifay.ccpp.shared.eventstore.impl.EventStreamRepository;
import fr.joffreybonifay.ccpp.shared.eventstore.impl.JpaEventStore;
import fr.joffreybonifay.ccpp.shared.outbox.OutboxProcessor;
import fr.joffreybonifay.ccpp.shared.outbox.OutboxRepository;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableJpaRepositories(basePackages = {"fr.joffreybonifay.ccpp.shared"})
@EntityScan(basePackages = {"fr.joffreybonifay.ccpp.shared"})
@EnableScheduling
@Configuration
public class ProdConfiguration {

    @Bean
    EventPublisher eventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        return new SpringEventPublisher(applicationEventPublisher);
    }

    @Bean
    EventStore jpaEventStore(
            EventStreamRepository eventStreamRepository,
            OutboxRepository outboxRepository,
            ObjectMapper objectMapper,
            EventPublisher eventPublisher
    ) {
        return new JpaEventStore(eventStreamRepository, outboxRepository, objectMapper, eventPublisher);
    }

    @Bean
    OutboxProcessor outboxProcessor(
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper,
            OutboxRepository outboxRepository
    ) {
        return new OutboxProcessor(outboxRepository, kafkaTemplate, objectMapper, "project-planning-events");
    }

}
