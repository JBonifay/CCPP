package fr.joffreybonifay.ccpp.workspace.infrastructure.configuration;

import fr.joffreybonifay.ccpp.shared.eventbus.EventBus;
import fr.joffreybonifay.ccpp.shared.eventbus.SimpleEventBus;
import fr.joffreybonifay.ccpp.shared.eventstore.EventStore;
import fr.joffreybonifay.ccpp.shared.eventstore.impl.InMemoryEventStore;
import fr.joffreybonifay.ccpp.workspace.application.query.repository.WorkspaceProjectCountReadRepository;
import fr.joffreybonifay.ccpp.workspace.infrastructure.projection.WorkspaceProjectCountProjectionUpdater;
import fr.joffreybonifay.ccpp.workspace.infrastructure.query.InMemoryWorkspaceProjectCountReadRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("in-memory")
@Configuration
public class DevConfiguration {

    @Bean
    WorkspaceProjectCountReadRepository workspaceProjectCountReadRepository() {
        return new InMemoryWorkspaceProjectCountReadRepository();
    }

    @Bean
    WorkspaceProjectCountProjectionUpdater workspaceProjectCountProjectionUpdater(
            WorkspaceProjectCountReadRepository workspaceProjectCountReadRepository
    ) {
        return new WorkspaceProjectCountProjectionUpdater(workspaceProjectCountReadRepository);
    }

    @Bean
    EventBus eventBus(WorkspaceProjectCountProjectionUpdater workspaceProjectCountProjectionUpdater) {
        SimpleEventBus simpleEventBus = new SimpleEventBus();
        simpleEventBus.subscribe(workspaceProjectCountProjectionUpdater::handle);
        return simpleEventBus;
    }

    @Bean
    EventStore eventStore(EventBus eventBus) {
        return new InMemoryEventStore(eventBus);
    }

}
