package io.joffrey.ccpp.projectplanning.infrastructure.rest.controller;

import com.ccpp.shared.command.CommandBus;
import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.valueobjects.DateRange;
import com.ccpp.shared.valueobjects.Money;
import io.joffrey.ccpp.projectplanning.application.command.command.*;
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
        CreateProjectCommand command = new CreateProjectCommand(
                newCommandId(),
                RequestContext.getWorkspaceId(),
                RequestContext.getUserId(),
                projectId,
                createProjectRequest.title(),
                createProjectRequest.description(),
                new DateRange(createProjectRequest.startDate(), createProjectRequest.endDate()),
                createProjectRequest.budgetLimit(),
                newCorrelationId()
        );

        commandBus.execute(command);

        return new ResponseEntity<>(new CreateProjectResponse(projectId.value()), HttpStatus.CREATED);
    }

    @PatchMapping("/{projectId}/timeline")
    public ResponseEntity<Void> changeProjectTimeline(
            @PathVariable String projectId,
            @RequestBody ChangeProjectTimelineRequest changeProjectTimelineRequest
    ) {

        commandBus.execute(new ChangeProjectTimelineCommand(
                newCommandId(),
                new ProjectId(projectId),
                new DateRange(
                        changeProjectTimelineRequest.startDate(),
                        changeProjectTimelineRequest.endDate()),
                newCorrelationId()));

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{projectId}/budget-items")
    public ResponseEntity<UUID> addBudgetItem(
            @PathVariable String projectId,
            @RequestBody AddBudgetItemRequest addBudgetItemRequest
    ) {
        BudgetItemId budgetItemId = budgetItemIdGenerator.generate();
        commandBus.execute(new AddBudgetItemCommand(
                newCommandId(),
                new ProjectId(UUID.fromString(projectId)),
                budgetItemId,
                addBudgetItemRequest.description(),
                new Money(addBudgetItemRequest.amount(), Currency.getInstance(addBudgetItemRequest.currency())),
                newCorrelationId()
        ));

        return new ResponseEntity<>(budgetItemId.value(), HttpStatus.CREATED);
    }

    @DeleteMapping("/{projectId}/budget-items/{budgetItemId}")
    public ResponseEntity<Void> removeBudgetItem(
            @PathVariable String projectId,
            @PathVariable String budgetItemId
    ) {
        RemoveBudgetItemCommand command = new RemoveBudgetItemCommand(
                newCommandId(),
                new ProjectId(projectId),
                new BudgetItemId(budgetItemId),
                newCorrelationId()
        );

        commandBus.execute(command);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{projectId}/budget-items/{budgetItemId}")
    public ResponseEntity<Void> updateBudgetItem(
            @PathVariable String projectId,
            @PathVariable String budgetItemId,
            @RequestBody UpdateBudgetItemRequest updateBudgetItemRequest
    ) {
        UpdateBudgetItemCommand command = new UpdateBudgetItemCommand(
                newCommandId(),
                new ProjectId(projectId),
                new BudgetItemId(budgetItemId),
                updateBudgetItemRequest.description(),
                updateBudgetItemRequest.amount(),
                newCorrelationId());

        commandBus.execute(command);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{projectId}/participants")
    public ResponseEntity<UUID> inviteParticipant(
            @PathVariable String projectId,
            @RequestBody InviteParticipantRequest inviteParticipantRequest
    ) {
        ParticipantId participantId = participantIdGenerator.generate();
        InviteParticipantCommand command = new InviteParticipantCommand(
                newCommandId(),
                new ProjectId(UUID.fromString(projectId)),
                participantId,
                inviteParticipantRequest.email(),
                inviteParticipantRequest.name(),
                newCorrelationId()
        );

        commandBus.execute(command);

        return new ResponseEntity<>(participantId.value(), HttpStatus.CREATED);
    }

    @PostMapping("/{projectId}/participants/{participantId}/accept")
    public ResponseEntity<UUID> participantAcceptedInvitation(
            @PathVariable String projectId,
            @PathVariable String participantId
    ) {
        AcceptParticipantInvitationCommand command = new AcceptParticipantInvitationCommand(
                newCommandId(),
                new ProjectId(projectId),
                new ParticipantId(UUID.fromString(participantId)),
                newCorrelationId()
        );

        commandBus.execute(command);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{projectId}/participants/{participantId}/decline")
    public ResponseEntity<UUID> participantDeclinedInvitation(
            @PathVariable String projectId,
            @PathVariable String participantId
    ) {
        DeclineParticipantInvitationCommand command = new DeclineParticipantInvitationCommand(
                newCommandId(),
                new ProjectId(projectId),
                new ParticipantId(UUID.fromString(participantId)),
                newCorrelationId()
        );

        commandBus.execute(command);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{projectId}/notes")
    public ResponseEntity<UUID> addNote(
            @PathVariable String projectId,
            @RequestBody AddNoteRequest addNoteRequest
    ) {
        AddNoteCommand command = new AddNoteCommand(
                newCommandId(),
                new ProjectId(projectId),
                addNoteRequest.content(),
                RequestContext.getUserId(),
                newCorrelationId()
        );

        commandBus.execute(command);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/{projectId}/ready")
    public ResponseEntity<Void> markProjectAsReady(
            @PathVariable String projectId
    ) {
        MarkProjectAsReadyCommand command = new MarkProjectAsReadyCommand(
                newCommandId(),
                new ProjectId(projectId),
                RequestContext.getUserId(),
                newCorrelationId());

        commandBus.execute(command);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private UUID newCommandId() { return UUID.randomUUID(); }
    private UUID newCorrelationId() { return UUID.randomUUID(); }
}
