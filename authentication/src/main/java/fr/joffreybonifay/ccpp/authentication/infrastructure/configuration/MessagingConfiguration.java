package fr.joffreybonifay.ccpp.authentication.infrastructure.configuration;

import fr.joffreybonifay.ccpp.authentication.infrastructure.messaging.UserEventPublisher;
import fr.joffreybonifay.ccpp.shared.event.UserRegisteredEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class MessagingConfiguration {

    @Bean
    public UserEventPublisher userEventPublisher(KafkaTemplate<String, UserRegisteredEvent> kafkaTemplate) {
        return event -> kafkaTemplate.send("user-registered-events", event.userId().toString(), event);
    }

}
