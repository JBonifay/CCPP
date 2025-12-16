package io.joffrey.ccpp.projectplanning.infrastructure.configuration;

import com.ccpp.shared.infrastructure.event.EventStore;
import com.ccpp.shared.infrastructure.event.EventBus;
import com.ccpp.shared.infrastructure.event.InMemoryEventBus;
import com.ccpp.shared.infrastructure.event.InMemoryEventStore;
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
    public EventBus eventPublisher() {
        return new InMemoryEventBus();
    }

    @Bean
    public EventStore eventStore(
            ProjectListReadRepository projectListReadRepository,
            ProjectDetailReadRepository projectDetailReadRepository
    ) {
        InMemoryEventStore inMemoryEventStore = new InMemoryEventStore();
//        inMemoryEventStore.subscribe(new ProjectListProjectionUpdater(projectListReadRepository));
//        inMemoryEventStore.subscribe(new ProjectDetailProjectionUpdater(projectDetailReadRepository));
        return inMemoryEventStore;
    }

}
