package io.joffrey.ccpp.workspace.infrastructure.configuration;

import com.ccpp.shared.command.CommandBus;
import com.ccpp.shared.command.SimpleCommandBus;
import com.ccpp.shared.eventstore.EventStore;
import io.joffrey.ccpp.workspace.application.command.command.CreateWorkspaceCommand;
import io.joffrey.ccpp.workspace.application.command.handler.CreateWorkspaceCommandHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommandConfiguration {


    @Bean
    CommandBus commandBus(EventStore eventStore) {
        SimpleCommandBus simpleCommandBus = new SimpleCommandBus();
        simpleCommandBus.register(CreateWorkspaceCommand.class, new CreateWorkspaceCommandHandler(eventStore));
        return simpleCommandBus;
    }

}
