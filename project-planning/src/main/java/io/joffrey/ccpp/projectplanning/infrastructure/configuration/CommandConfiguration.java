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
        simpleCommandBus.register(AcceptInvitationCommand.class, new AcceptInvitationHandler(eventStore));
        simpleCommandBus.register(AddBudgetItemCommand.class, new AddBudgetItemHandler(eventStore));
        simpleCommandBus.register(AddNoteCommand.class, new AddNoteHandler(eventStore));
        simpleCommandBus.register(CancelProjectCreationCommand.class, new CancelProjectCreationHandler(eventStore));
        simpleCommandBus.register(ChangeTimelineCommand.class, new ChangeTimelineHandler(eventStore));
        simpleCommandBus.register(RequestProjectCreationCommand.class, new RequestProjectCreationHandler(eventStore));
        simpleCommandBus.register(RejectInvitationCommand.class, new RejectInvitiationHandler(eventStore));
        simpleCommandBus.register(InviteParticipantCommand.class, new InviteParticipantHandler(eventStore));
        simpleCommandBus.register(MarkProjectReadyCommand.class, new MarkProjectReadyHandler(eventStore));
        simpleCommandBus.register(RemoveBudgetItemCommand.class, new RemoveBudgetItemHandler(eventStore));
        simpleCommandBus.register(UpdateBudgetItemCommand.class, new UpdateBudgetItemHandler(eventStore));
        simpleCommandBus.register(UpdateDetailsCommand.class, new UpdateDetailsHandler(eventStore));
        return simpleCommandBus;
    }

}
