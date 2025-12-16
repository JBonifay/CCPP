package io.joffrey.ccpp.projectplanning.infrastructure.configuration;

import com.ccpp.shared.domain.EventStore;
import com.ccpp.shared.event.EventPublisher;
import com.ccpp.shared.event.InMemoryEventPublisher;
import com.ccpp.shared.repository.InMemoryEventStore;
import io.joffrey.ccpp.projectplanning.application.query.repository.ProjectDetailReadRepository;
import io.joffrey.ccpp.projectplanning.application.query.repository.ProjectListReadRepository;
import io.joffrey.ccpp.projectplanning.infrastructure.projection.ProjectDetailProjectionUpdater;
import io.joffrey.ccpp.projectplanning.infrastructure.projection.ProjectListProjectionUpdater;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventConfiguration {

    @Bean
    public EventPublisher eventPublisher(ApplicationContext applicationContext) {
        return new InMemoryEventPublisher(applicationContext);
    }

    @Bean
    public EventStore eventStore(
            ProjectListReadRepository projectListReadRepository,
            ProjectDetailReadRepository projectDetailReadRepository
    ) {
        InMemoryEventStore inMemoryEventStore = new InMemoryEventStore();
        inMemoryEventStore.subscribe(new ProjectListProjectionUpdater(projectListReadRepository));
        inMemoryEventStore.subscribe(new ProjectDetailProjectionUpdater(projectDetailReadRepository));
        return inMemoryEventStore;
    }

}
