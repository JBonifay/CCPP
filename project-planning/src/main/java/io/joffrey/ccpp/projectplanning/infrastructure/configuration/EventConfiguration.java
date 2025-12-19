package io.joffrey.ccpp.projectplanning.infrastructure.configuration;

import com.ccpp.shared.eventbus.EventBus;
import com.ccpp.shared.eventbus.SimpleEventBus;
import com.ccpp.shared.eventstore.EventStore;
import com.ccpp.shared.eventstore.InMemoryEventStore;
import io.joffrey.ccpp.projectplanning.application.query.repository.ProjectDetailReadRepository;
import io.joffrey.ccpp.projectplanning.application.query.repository.ProjectListReadRepository;
import io.joffrey.ccpp.projectplanning.infrastructure.projection.ProjectDetailProjectionUpdater;
import io.joffrey.ccpp.projectplanning.infrastructure.projection.ProjectListProjectionUpdater;
import io.joffrey.ccpp.projectplanning.infrastructure.query.InMemoryProjectDetailReadRepository;
import io.joffrey.ccpp.projectplanning.infrastructure.query.InMemoryProjectListReadRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventConfiguration {

    @Bean
    ProjectListReadRepository projectListReadRepository() {
        return new InMemoryProjectListReadRepository();
    }

    @Bean
    ProjectDetailReadRepository projectDetailReadRepository() {
        return new InMemoryProjectDetailReadRepository();
    }

    @Bean
    ProjectDetailProjectionUpdater projectDetailProjectionUpdater(ProjectDetailReadRepository projectDetailReadRepository) {
        return new ProjectDetailProjectionUpdater(projectDetailReadRepository);
    }

    @Bean
    ProjectListProjectionUpdater projectListProjectionUpdater(ProjectListReadRepository projectListReadRepository) {
        return new ProjectListProjectionUpdater(projectListReadRepository);
    }

    @Bean
    EventBus eventBus(
            ProjectDetailProjectionUpdater projectDetailProjectionUpdater,
            ProjectListProjectionUpdater projectListProjectionUpdater
    ) {
        SimpleEventBus simpleEventBus = new SimpleEventBus();
        simpleEventBus.subscribe(projectDetailProjectionUpdater::handle);
        simpleEventBus.subscribe(projectListProjectionUpdater::handle);
        return simpleEventBus;
    }

    @Bean
    EventStore eventStore(EventBus eventBus) {
        return new InMemoryEventStore(eventBus);
    }

}
