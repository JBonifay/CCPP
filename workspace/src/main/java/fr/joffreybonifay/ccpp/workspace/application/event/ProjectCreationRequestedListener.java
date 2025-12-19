package fr.joffreybonifay.ccpp.workspace.application.event;

import fr.joffreybonifay.ccpp.shared.command.CommandBus;
import fr.joffreybonifay.ccpp.shared.event.ProjectCreationRequested;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProjectCreationRequestedListener {

    private final CommandBus commandBus;

    public ProjectCreationRequestedListener(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    @EventListener
    public void on(ProjectCreationRequested event) {

//        commandBus.execute(
//                new DecideOnProjectCreationCommand(
//                        event.workspaceId(),
//                        event.projectId(),
//                        event.correlationId()
//                )
//        );
    }
}
