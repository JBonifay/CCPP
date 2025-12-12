package io.joffrey.ccpp.projectplanning.infrastructure.event;

import com.ccpp.shared.domain.EventStore;
import com.ccpp.shared.repository.InMemoryEventStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventConfiguration {

    @Bean
    public EventStore eventStore() {
        return new InMemoryEventStore();
    }

}
