package fr.joffreybonifay.ccpp.usermanagement.infrastructure.configuration;

import fr.joffreybonifay.ccpp.shared.eventbus.EventBus;
import fr.joffreybonifay.ccpp.shared.eventbus.SimpleEventBus;
import fr.joffreybonifay.ccpp.shared.eventstore.EventStore;
import fr.joffreybonifay.ccpp.shared.eventstore.impl.InMemoryEventStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("in-memory")
@Configuration
public class DevConfiguration {

    @Bean
    EventBus eventBus() {
        SimpleEventBus simpleEventBus = new SimpleEventBus();
        return simpleEventBus;
    }

    @Bean
    EventStore inMemoryEventStore(EventBus eventBus) {
        return new InMemoryEventStore(eventBus);
    }
}
