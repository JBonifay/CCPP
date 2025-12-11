package io.joffrey.ccpp.projectplanning.infrastructure.event;

import com.ccpp.shared.domain.EventStore;
import io.joffrey.ccpp.projectplanning.infrastructure.repository.InMemoryProjectQueryRepository;
import io.joffrey.ccpp.projectplanning.application.query.handler.ProjectEventHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventConfiguration {

    @Bean
    public EventStore eventStore() {
        return new InMemoryEventStore();
    }

    @Bean
    public ProjectEventHandler projectEventHandler() {
        return new ProjectEventHandler(new InMemoryProjectQueryRepository());
    }
}
