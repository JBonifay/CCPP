package io.joffrey.ccpp.projectplanning.infrastructure.configuration;

import com.ccpp.shared.eventbus.EventBus;
import com.ccpp.shared.eventbus.SimpleEventBus;
import com.ccpp.shared.eventstore.EventStore;
import com.ccpp.shared.eventstore.InMemoryEventStore;
import io.joffrey.ccpp.projectplanning.infrastructure.projection.ProjectDetailProjectionUpdater;
import io.joffrey.ccpp.projectplanning.infrastructure.projection.ProjectListProjectionUpdater;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("in-memory")
@Configuration
public class DevConfiguration {

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
    EventStore inMemoryEventStore(EventBus eventBus) {
        return new InMemoryEventStore(eventBus);
    }
}
