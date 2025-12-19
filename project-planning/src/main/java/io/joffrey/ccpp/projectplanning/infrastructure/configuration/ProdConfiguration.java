package io.joffrey.ccpp.projectplanning.infrastructure.configuration;

import com.ccpp.shared.event.EventRepository;
import com.ccpp.shared.eventstore.EventStore;
import com.ccpp.shared.eventstore.JpaEventStore;
import com.ccpp.shared.outbox.OutboxProcessor;
import com.ccpp.shared.outbox.OutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableJpaRepositories(basePackages = {"com.ccpp.shared"})
@EntityScan(basePackages = {"com.ccpp.shared"})
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
            OutboxRepository outboxRepository,
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper
    ) {
        return new OutboxProcessor(outboxRepository, kafkaTemplate, objectMapper);
    }
}
