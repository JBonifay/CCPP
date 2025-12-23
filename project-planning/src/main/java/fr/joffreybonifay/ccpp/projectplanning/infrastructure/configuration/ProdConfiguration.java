package fr.joffreybonifay.ccpp.projectplanning.infrastructure.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.joffreybonifay.ccpp.projectplanning.infrastructure.messaging.WorkspaceEventListener;
import fr.joffreybonifay.ccpp.shared.command.CommandBus;
import fr.joffreybonifay.ccpp.shared.eventstore.EventStore;
import fr.joffreybonifay.ccpp.shared.eventstore.impl.EventStreamRepository;
import fr.joffreybonifay.ccpp.shared.eventstore.impl.JpaEventStore;
import fr.joffreybonifay.ccpp.shared.indempotency.ProcessedEventRepository;
import fr.joffreybonifay.ccpp.shared.outbox.OutboxProcessor;
import fr.joffreybonifay.ccpp.shared.outbox.OutboxRepository;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.ApplicationEventPublisher;
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
    EventStore jpaEventStore(
            EventStreamRepository eventStreamRepository,
            OutboxRepository outboxRepository,
            ObjectMapper objectMapper,
            ApplicationEventPublisher applicationEventPublisher
    ) {
        return new JpaEventStore(eventStreamRepository, outboxRepository, objectMapper, applicationEventPublisher);
    }

    @Bean
    WorkspaceEventListener workspaceEventListener(
            ProcessedEventRepository processedEventRepository,
            ObjectMapper objectMapper,
            CommandBus commandBus
    ) {
        return new WorkspaceEventListener(objectMapper, commandBus, processedEventRepository);
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
