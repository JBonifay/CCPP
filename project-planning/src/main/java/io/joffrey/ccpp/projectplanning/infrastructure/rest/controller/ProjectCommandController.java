package io.joffrey.ccpp.projectplanning.infrastructure.rest.controller;

import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.valueobjects.DateRange;
import io.joffrey.ccpp.projectplanning.application.command.CommandBus;
import io.joffrey.ccpp.projectplanning.application.command.command.CreateProjectCommand;
import io.joffrey.ccpp.projectplanning.domain.spi.ProjectIdGenerator;
import io.joffrey.ccpp.projectplanning.infrastructure.rest.context.RequestContext;
import io.joffrey.ccpp.projectplanning.infrastructure.rest.dto.CreateProjectRequest;
import io.joffrey.ccpp.projectplanning.infrastructure.rest.dto.CreateProjectResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects")
public class ProjectCommandController {

    private final CommandBus commandBus;
    private final ProjectIdGenerator projectIdGenerator;

    public ProjectCommandController(CommandBus commandBus, ProjectIdGenerator projectIdGenerator) {
        this.commandBus = commandBus;
        this.projectIdGenerator = projectIdGenerator;
    }

    @PostMapping
    public ResponseEntity<CreateProjectResponse> createProject(
            @RequestBody CreateProjectRequest createProjectRequest
    ) {
        ProjectId projectId = projectIdGenerator.generate();
        commandBus.execute(new CreateProjectCommand(
                RequestContext.getWorkspaceId(),
                RequestContext.getUserId(),
                new ProjectId(projectId.value()),
                createProjectRequest.title(),
                createProjectRequest.description(),
                new DateRange(createProjectRequest.startDate(), createProjectRequest.endDate()),
                createProjectRequest.budgetLimit()
        ));
        return new ResponseEntity<>(
                new CreateProjectResponse(projectId.value()),
                HttpStatus.CREATED
        );
    }

}
