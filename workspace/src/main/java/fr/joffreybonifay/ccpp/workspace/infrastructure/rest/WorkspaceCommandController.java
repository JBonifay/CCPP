package fr.joffreybonifay.ccpp.workspace.infrastructure.rest;

import fr.joffreybonifay.ccpp.shared.command.CommandBus;
import fr.joffreybonifay.ccpp.shared.domain.identities.WorkspaceId;
import fr.joffreybonifay.ccpp.shared.rest.RequestContext;
import fr.joffreybonifay.ccpp.workspace.application.command.command.CreateWorkspaceCommand;
import fr.joffreybonifay.ccpp.workspace.domain.WorkspaceIdGenerator;
import fr.joffreybonifay.ccpp.workspace.infrastructure.rest.dto.CreateWorkspaceRequest;
import fr.joffreybonifay.ccpp.workspace.infrastructure.rest.dto.CreateWorkspaceResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/workspaces")
public class WorkspaceCommandController {

    private final WorkspaceIdGenerator workspaceIdGenerator;
    private final CommandBus commandBus;

    public WorkspaceCommandController(WorkspaceIdGenerator workspaceIdGenerator, CommandBus commandBus) {
        this.workspaceIdGenerator = workspaceIdGenerator;
        this.commandBus = commandBus;
    }

    @PostMapping
    public ResponseEntity<CreateWorkspaceResponse> createWorkspace(
            @RequestBody CreateWorkspaceRequest request
    ) {
        WorkspaceId workspaceId = workspaceIdGenerator.generate();
        commandBus.execute(new CreateWorkspaceCommand(
                workspaceId,
                RequestContext.getUserId(),
                request.name()
        ));
        return new ResponseEntity<>(new CreateWorkspaceResponse(workspaceId.value()), HttpStatus.CREATED);
    }
}
