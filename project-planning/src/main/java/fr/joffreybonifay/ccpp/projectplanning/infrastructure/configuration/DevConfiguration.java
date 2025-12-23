package fr.joffreybonifay.ccpp.projectplanning.infrastructure.configuration;

import fr.joffreybonifay.ccpp.shared.eventbus.EventBus;
import fr.joffreybonifay.ccpp.shared.eventbus.SimpleEventBus;
import fr.joffreybonifay.ccpp.shared.eventstore.EventStore;
import fr.joffreybonifay.ccpp.shared.eventstore.impl.InMemoryEventStore;
import fr.joffreybonifay.ccpp.projectplanning.infrastructure.projection.ProjectDetailProjectionUpdater;
import fr.joffreybonifay.ccpp.projectplanning.infrastructure.projection.ProjectListProjectionUpdater;
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
