package io.joffrey.ccpp.projectplanning.infrastructure.configuration;

import com.ccpp.shared.eventstore.EventStore;
import com.ccpp.shared.eventstore.InMemoryEventStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    EventStore eventStore() {
        return new InMemoryEventStore();
    }

}
