package io.joffrey.ccpp.workspace.infrastructure.configuration;

import com.ccpp.shared.infrastructure.command.CommandBus;
import com.ccpp.shared.infrastructure.command.SimpleCommandBus;
import com.ccpp.shared.infrastructure.event.EventBus;
import com.ccpp.shared.infrastructure.event.EventStore;
import com.ccpp.shared.infrastructure.event.InMemoryEventBus;
import com.ccpp.shared.infrastructure.event.InMemoryEventStore;
import io.joffrey.ccpp.workspace.application.command.command.ApproveProjectCreationCommand;
import io.joffrey.ccpp.workspace.application.command.command.CreateWorkspaceCommand;
import io.joffrey.ccpp.workspace.application.command.command.RejectProjectCreationCommand;
import io.joffrey.ccpp.workspace.application.command.command.UpgradeWorkspaceSubscriptionCommand;
import io.joffrey.ccpp.workspace.application.command.handler.ApproveProjectCreationCommandHandler;
import io.joffrey.ccpp.workspace.application.command.handler.CreateWorkspaceCommandHandler;
import io.joffrey.ccpp.workspace.application.command.handler.RejectProjectCreationCommandHandler;
import io.joffrey.ccpp.workspace.application.command.handler.UpgradeWorkspaceSubscriptionCommandHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommandBusConfiguration {

    @Bean
    public EventStore eventStore() {
        return new InMemoryEventStore();
    }

    @Bean
    public EventBus eventPublisher() {
        return new InMemoryEventBus();
    }

    @Bean
    public CommandBus commandBus(EventStore eventStore, EventBus eventBus) {
        SimpleCommandBus simpleCommandBus = new SimpleCommandBus();
        simpleCommandBus.register(CreateWorkspaceCommand.class, new CreateWorkspaceCommandHandler(eventStore));
        simpleCommandBus.register(UpgradeWorkspaceSubscriptionCommand.class, new UpgradeWorkspaceSubscriptionCommandHandler(eventStore));
        simpleCommandBus.register(ApproveProjectCreationCommand.class, new ApproveProjectCreationCommandHandler(eventStore, eventBus));
        simpleCommandBus.register(RejectProjectCreationCommand.class, new RejectProjectCreationCommandHandler(eventBus));
        return simpleCommandBus;
    }
}
