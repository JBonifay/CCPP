package io.joffrey.ccpp.projectplanning.infrastructure.configuration;

import com.ccpp.shared.domain.EventStore;
import com.ccpp.shared.command.CommandBus;
import io.joffrey.ccpp.projectplanning.application.command.command.*;
import io.joffrey.ccpp.projectplanning.application.command.handler.*;
import com.ccpp.shared.command.SimpleCommandBus;
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
                AddNoteCommand.class, new AddNoteHandler(eventStore),
                MarkProjectAsReadyCommand.class, new MarkProjectAsReadyHandler(eventStore),
                RemoveBudgetItemCommand.class, new RemoveBudgetItemHandler(eventStore),
                UpdateBudgetItemCommand.class, new UpdateBudgetItemHandler(eventStore),
                AcceptParticipantInvitationCommand.class, new AcceptParticipantInvitationHandler(eventStore),
                DeclineParticipantInvitationCommand.class, new DeclineParticipantInvitationHandler(eventStore),
                ChangeProjectTimelineCommand.class, new ChangeProjectTimelineHandler(eventStore)
        ));
    }

}
