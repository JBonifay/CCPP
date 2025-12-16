package io.joffrey.ccpp.workspace.infrastructure.configuration;

import com.ccpp.shared.command.CommandBus;
import com.ccpp.shared.command.SimpleCommandBus;
import com.ccpp.shared.domain.EventStore;
import com.ccpp.shared.event.EventPublisher;
import com.ccpp.shared.event.InMemoryEventPublisher;
import com.ccpp.shared.repository.InMemoryEventStore;
import io.joffrey.ccpp.workspace.application.command.command.ApproveProjectCreationCommand;
import io.joffrey.ccpp.workspace.application.command.command.CreateWorkspaceCommand;
import io.joffrey.ccpp.workspace.application.command.command.RejectProjectCreationCommand;
import io.joffrey.ccpp.workspace.application.command.command.UpgradeWorkspaceSubscriptionCommand;
import io.joffrey.ccpp.workspace.application.command.handler.ApproveProjectCreationCommandHandler;
import io.joffrey.ccpp.workspace.application.command.handler.CreateWorkspaceCommandHandler;
import io.joffrey.ccpp.workspace.application.command.handler.RejectProjectCreationCommandHandler;
import io.joffrey.ccpp.workspace.application.command.handler.UpgradeWorkspaceSubscriptionCommandHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class CommandBusConfiguration {

    @Bean
    public EventStore eventStore() {
        return new InMemoryEventStore();
    }

    @Bean
    public EventPublisher eventPublisher(ApplicationContext applicationContext) {
        return new InMemoryEventPublisher(applicationContext);
    }

    @Bean
    public CommandBus commandBus(EventStore eventStore, EventPublisher eventPublisher) {
        return new SimpleCommandBus(Map.of(
                CreateWorkspaceCommand.class, new CreateWorkspaceCommandHandler(eventStore),
                UpgradeWorkspaceSubscriptionCommand.class, new UpgradeWorkspaceSubscriptionCommandHandler(eventStore),
                ApproveProjectCreationCommand.class, new ApproveProjectCreationCommandHandler(eventStore, eventPublisher),
                RejectProjectCreationCommand.class, new RejectProjectCreationCommandHandler(eventPublisher)
        ));
    }
}
