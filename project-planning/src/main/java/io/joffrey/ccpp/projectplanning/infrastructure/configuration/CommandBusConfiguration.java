package io.joffrey.ccpp.projectplanning.infrastructure.configuration;

import com.ccpp.shared.domain.EventStore;
import io.joffrey.ccpp.projectplanning.application.command.CommandBus;
import io.joffrey.ccpp.projectplanning.application.command.command.AddBudgetItemCommand;
import io.joffrey.ccpp.projectplanning.application.command.command.AddNoteCommand;
import io.joffrey.ccpp.projectplanning.application.command.command.CreateProjectCommand;
import io.joffrey.ccpp.projectplanning.application.command.command.InviteParticipantCommand;
import io.joffrey.ccpp.projectplanning.application.command.handler.AddBudgetItemHandler;
import io.joffrey.ccpp.projectplanning.application.command.handler.AddNoteHandler;
import io.joffrey.ccpp.projectplanning.application.command.handler.CreateProjectHandler;
import io.joffrey.ccpp.projectplanning.application.command.handler.InviteParticipantHandler;
import io.joffrey.ccpp.projectplanning.infrastructure.command.SimpleCommandBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class CommandBusConfiguration {

    @Bean
    public CommandBus commandBus(EventStore eventStore) {
        return new SimpleCommandBus(Map.of(
                CreateProjectCommand.class, new CreateProjectHandler(eventStore),
                AddBudgetItemCommand.class, new AddBudgetItemHandler(eventStore),
                InviteParticipantCommand.class, new InviteParticipantHandler(eventStore),
                AddNoteCommand.class, new AddNoteHandler(eventStore)
        ));
    }

}
