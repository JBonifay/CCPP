package io.joffrey.ccpp.projectplanning.infrastructure.event;

import io.joffrey.ccpp.projectplanning.infrastructure.repository.InMemoryProjectQueryRepository;
import io.joffrey.ccpp.projectplanning.query.handler.ProjectEventHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventConfiguration {

    @Bean
    public ProjectEventHandler projectEventHandler() {
        return new ProjectEventHandler(new InMemoryProjectQueryRepository());
    }
}
