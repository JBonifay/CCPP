package fr.joffreybonifay.ccpp.workspace.infrastructure.configuration;

import fr.joffreybonifay.ccpp.shared.eventstore.EventStore;
import fr.joffreybonifay.ccpp.shared.eventstore.impl.InMemoryEventStore;
import fr.joffreybonifay.ccpp.workspace.application.query.repository.WorkspaceProjectCountReadRepository;
import fr.joffreybonifay.ccpp.workspace.infrastructure.projection.WorkspaceProjectCountUpdater;
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
    WorkspaceProjectCountUpdater workspaceProjectCountProjectionUpdater(
            WorkspaceProjectCountReadRepository workspaceProjectCountReadRepository
    ) {
        return new WorkspaceProjectCountUpdater(workspaceProjectCountReadRepository);
    }

    @Bean
    EventStore eventStore() {
        return new InMemoryEventStore();
    }

}
