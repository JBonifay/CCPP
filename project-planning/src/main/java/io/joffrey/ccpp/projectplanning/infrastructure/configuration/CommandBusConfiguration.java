package io.joffrey.ccpp.projectplanning.infrastructure.configuration;

import com.ccpp.shared.infrastructure.event.EventStore;
import com.ccpp.shared.infrastructure.command.CommandBus;
import com.ccpp.shared.infrastructure.event.EventBus;
import io.joffrey.ccpp.projectplanning.application.command.command.*;
import io.joffrey.ccpp.projectplanning.application.command.handler.*;
import com.ccpp.shared.infrastructure.command.SimpleCommandBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class CommandBusConfiguration {

    @Bean
    public CommandBus commandBus(EventStore eventStore, EventBus eventBus) {
        SimpleCommandBus simpleCommandBus = new SimpleCommandBus();
        simpleCommandBus.register(CreateProjectCommand.class, new CreateProjectHandler(eventStore, eventBus));
        simpleCommandBus.register(AddBudgetItemCommand.class, new AddBudgetItemHandler(eventStore));
        simpleCommandBus.register(InviteParticipantCommand.class, new InviteParticipantHandler(eventStore));
        simpleCommandBus.register(AddNoteCommand.class, new AddNoteHandler(eventStore));
        simpleCommandBus.register(MarkProjectAsReadyCommand.class, new MarkProjectAsReadyHandler(eventStore));
        simpleCommandBus.register(RemoveBudgetItemCommand.class, new RemoveBudgetItemHandler(eventStore));
        simpleCommandBus.register(UpdateBudgetItemCommand.class, new UpdateBudgetItemHandler(eventStore));
        simpleCommandBus.register(AcceptParticipantInvitationCommand.class, new AcceptParticipantInvitationHandler(eventStore));
        simpleCommandBus.register(DeclineParticipantInvitationCommand.class, new DeclineParticipantInvitationHandler(eventStore));
        simpleCommandBus.register(ChangeProjectTimelineCommand.class, new ChangeProjectTimelineHandler(eventStore));
        simpleCommandBus.register(CancelProjectCreationCommand.class, new CancelProjectCreationCommandHandler(eventStore));
        return simpleCommandBus;
    }

}
