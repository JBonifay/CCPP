package io.joffrey.ccpp.projectplanning.infrastructure.configuration;

import com.ccpp.shared.domain.EventStore;
import com.ccpp.shared.command.CommandBus;
import com.ccpp.shared.event.EventPublisher;
import io.joffrey.ccpp.projectplanning.application.command.command.*;
import io.joffrey.ccpp.projectplanning.application.command.handler.*;
import com.ccpp.shared.command.SimpleCommandBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class CommandBusConfiguration {

    @Bean
    public CommandBus commandBus(EventStore eventStore, EventPublisher eventPublisher) {
        return new SimpleCommandBus(Map.ofEntries(
                Map.entry(CreateProjectCommand.class, new CreateProjectHandler(eventStore, eventPublisher)),
                Map.entry(AddBudgetItemCommand.class, new AddBudgetItemHandler(eventStore)),
                Map.entry(InviteParticipantCommand.class, new InviteParticipantHandler(eventStore)),
                Map.entry(AddNoteCommand.class, new AddNoteHandler(eventStore)),
                Map.entry(MarkProjectAsReadyCommand.class, new MarkProjectAsReadyHandler(eventStore)),
                Map.entry(RemoveBudgetItemCommand.class, new RemoveBudgetItemHandler(eventStore)),
                Map.entry(UpdateBudgetItemCommand.class, new UpdateBudgetItemHandler(eventStore)),
                Map.entry(AcceptParticipantInvitationCommand.class, new AcceptParticipantInvitationHandler(eventStore)),
                Map.entry(DeclineParticipantInvitationCommand.class, new DeclineParticipantInvitationHandler(eventStore)),
                Map.entry(ChangeProjectTimelineCommand.class, new ChangeProjectTimelineHandler(eventStore)),
                Map.entry(CancelProjectCreationCommand.class, new CancelProjectCreationCommandHandler(eventStore))
        ));
    }

}
