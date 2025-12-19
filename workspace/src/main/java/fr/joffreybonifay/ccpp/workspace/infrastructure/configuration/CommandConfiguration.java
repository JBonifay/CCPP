package fr.joffreybonifay.ccpp.workspace.infrastructure.configuration;

import fr.joffreybonifay.ccpp.shared.command.CommandBus;
import fr.joffreybonifay.ccpp.shared.command.SimpleCommandBus;
import fr.joffreybonifay.ccpp.shared.eventstore.EventStore;
import fr.joffreybonifay.ccpp.workspace.application.command.command.CreateWorkspaceCommand;
import fr.joffreybonifay.ccpp.workspace.application.command.handler.CreateWorkspaceCommandHandler;
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
