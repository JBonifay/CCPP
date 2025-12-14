package io.joffrey.ccpp.projectplanning.infrastructure.rest.controller;

import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.valueobjects.DateRange;
import com.ccpp.shared.valueobjects.Money;
import io.joffrey.ccpp.projectplanning.application.command.CommandBus;
import io.joffrey.ccpp.projectplanning.application.command.command.AddBudgetItemCommand;
import io.joffrey.ccpp.projectplanning.application.command.command.AddNoteCommand;
import io.joffrey.ccpp.projectplanning.application.command.command.CreateProjectCommand;
import io.joffrey.ccpp.projectplanning.application.command.command.InviteParticipantCommand;
import io.joffrey.ccpp.projectplanning.domain.spi.BudgetItemIdGenerator;
import io.joffrey.ccpp.projectplanning.domain.spi.ParticipantIdGenerator;
import io.joffrey.ccpp.projectplanning.domain.spi.ProjectIdGenerator;
import io.joffrey.ccpp.projectplanning.domain.valueobject.BudgetItemId;
import io.joffrey.ccpp.projectplanning.domain.valueobject.ParticipantId;
import io.joffrey.ccpp.projectplanning.infrastructure.rest.context.RequestContext;
import io.joffrey.ccpp.projectplanning.infrastructure.rest.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Currency;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
public class ProjectCommandController {

    private final CommandBus commandBus;
    private final ProjectIdGenerator projectIdGenerator;
    private final BudgetItemIdGenerator budgetItemIdGenerator;
    private final ParticipantIdGenerator participantIdGenerator;

    public ProjectCommandController(CommandBus commandBus, ProjectIdGenerator projectIdGenerator, BudgetItemIdGenerator budgetItemIdGenerator, ParticipantIdGenerator participantIdGenerator) {
        this.commandBus = commandBus;
        this.projectIdGenerator = projectIdGenerator;
        this.budgetItemIdGenerator = budgetItemIdGenerator;
        this.participantIdGenerator = participantIdGenerator;
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

        return new ResponseEntity<>(new CreateProjectResponse(projectId.value()), HttpStatus.CREATED);
    }

    @PostMapping("/{projectId}/budget-items")
    public ResponseEntity<UUID> addBudgetItem(
            @PathVariable String projectId,
            @RequestBody AddBudgetItemRequest addBudgetItemRequest
    ) {
        BudgetItemId budgetItemId = budgetItemIdGenerator.generate();

        commandBus.execute(new AddBudgetItemCommand(
                new ProjectId(UUID.fromString(projectId)),
                budgetItemId,
                addBudgetItemRequest.description(),
                new Money(addBudgetItemRequest.amount(), Currency.getInstance(addBudgetItemRequest.currency()))
        ));

        return new ResponseEntity<>(budgetItemId.value(), HttpStatus.CREATED);
    }

    @PostMapping("/{projectId}/participants")
    public ResponseEntity<UUID> inviteParticipant(
            @PathVariable String projectId,
            @RequestBody InviteParticipantRequest inviteParticipantRequest
    ) {
        ParticipantId participantId = participantIdGenerator.generate();
        commandBus.execute(new InviteParticipantCommand(
                new ProjectId(UUID.fromString(projectId)),
                participantId,
                inviteParticipantRequest.email(),
                inviteParticipantRequest.name()
        ));
        return new ResponseEntity<>(participantId.value(), HttpStatus.CREATED);
    }

    @PostMapping("/{projectId}/notes")
    public ResponseEntity<UUID> addNote(
            @PathVariable String projectId,
            @RequestBody AddNoteRequest addNoteRequest
    ) {
        commandBus.execute(new AddNoteCommand(new ProjectId(projectId), addNoteRequest.content(), RequestContext.getUserId()));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
