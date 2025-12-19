package io.joffrey.ccpp.workspace.infrastructure.configuration;

import com.ccpp.shared.eventbus.EventBus;
import com.ccpp.shared.eventbus.SimpleEventBus;
import com.ccpp.shared.eventstore.EventStore;
import com.ccpp.shared.eventstore.InMemoryEventStore;
import io.joffrey.ccpp.workspace.application.query.repository.WorkspaceProjectCountReadRepository;
import io.joffrey.ccpp.workspace.infrastructure.projection.WorkspaceProjectCountProjectionUpdater;
import io.joffrey.ccpp.workspace.infrastructure.query.InMemoryWorkspaceProjectCountReadRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventConfiguration {

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
