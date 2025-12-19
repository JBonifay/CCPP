package io.joffrey.ccpp.workspace.infrastructure.configuration;

import com.ccpp.shared.command.CommandBus;
import com.ccpp.shared.command.SimpleCommandBus;
import com.ccpp.shared.eventstore.EventStore;
import com.ccpp.shared.eventstore.InMemoryEventStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommandBusConfiguration {

//    @Bean
//    public EventStore eventStore() {
//        return new InMemoryEventStore();
//    }

    @Bean
    public CommandBus commandBus(EventStore eventStore) {
        SimpleCommandBus simpleCommandBus = new SimpleCommandBus();
//        simpleCommandBus.register(CreateWorkspaceCommand.class, new CreateWorkspaceCommandHandler(eventStore));
//        simpleCommandBus.register(UpgradeWorkspaceSubscriptionCommand.class, new UpgradeWorkspaceSubscriptionCommandHandler(eventStore));
//        simpleCommandBus.register(ApproveProjectCreationCommand.class, new ApproveProjectCreationCommandHandler(eventStore, eventBus));
//        simpleCommandBus.register(RejectProjectCreationCommand.class, new RejectProjectCreationCommandHandler(eventBus));
        return simpleCommandBus;
    }
}
