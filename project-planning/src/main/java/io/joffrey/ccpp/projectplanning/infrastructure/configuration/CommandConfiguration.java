package io.joffrey.ccpp.projectplanning.infrastructure.configuration;

import com.ccpp.shared.command.CommandBus;
import com.ccpp.shared.command.SimpleCommandBus;
import com.ccpp.shared.eventstore.EventStore;
import io.joffrey.ccpp.projectplanning.application.command.command.*;
import io.joffrey.ccpp.projectplanning.application.command.handler.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommandConfiguration {

    @Bean
    CommandBus commandBus(EventStore eventStore) {
        SimpleCommandBus simpleCommandBus = new SimpleCommandBus();
        simpleCommandBus.register(AcceptParticipantInvitationCommand.class, new AcceptParticipantInvitationHandler(eventStore));
        simpleCommandBus.register(AddBudgetItemCommand.class, new AddBudgetItemHandler(eventStore));
        simpleCommandBus.register(AddNoteCommand.class, new AddNoteHandler(eventStore));
        simpleCommandBus.register(CancelProjectCreationCommand.class, new CancelProjectCreationCommandHandler(eventStore));
        simpleCommandBus.register(ChangeProjectTimelineCommand.class, new ChangeProjectTimelineHandler(eventStore));
        simpleCommandBus.register(CreateProjectCommand.class, new CreateProjectHandler(eventStore));
        simpleCommandBus.register(DeclineParticipantInvitationCommand.class, new DeclineParticipantInvitationHandler(eventStore));
        simpleCommandBus.register(InviteParticipantCommand.class, new InviteParticipantHandler(eventStore));
        simpleCommandBus.register(MarkProjectAsReadyCommand.class, new MarkProjectAsReadyHandler(eventStore));
        simpleCommandBus.register(RemoveBudgetItemCommand.class, new RemoveBudgetItemHandler(eventStore));
        simpleCommandBus.register(UpdateBudgetItemCommand.class, new UpdateBudgetItemHandler(eventStore));
        simpleCommandBus.register(UpdateProjectDetailsCommand.class, new UpdateProjectDetailsHandler(eventStore));
        return simpleCommandBus;
    }

}
