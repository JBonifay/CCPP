package fr.joffreybonifay.ccpp.projectplanning.infrastructure.rest.controller;

import fr.joffreybonifay.ccpp.projectplanning.application.command.command.*;
import fr.joffreybonifay.ccpp.projectplanning.infrastructure.rest.dto.*;
import fr.joffreybonifay.ccpp.shared.command.CommandBus;
import fr.joffreybonifay.ccpp.shared.identities.ProjectId;
import fr.joffreybonifay.ccpp.shared.valueobjects.DateRange;
import fr.joffreybonifay.ccpp.shared.valueobjects.Money;
import fr.joffreybonifay.ccpp.projectplanning.domain.spi.BudgetItemIdGenerator;
import fr.joffreybonifay.ccpp.projectplanning.domain.spi.ParticipantIdGenerator;
import fr.joffreybonifay.ccpp.projectplanning.domain.spi.ProjectIdGenerator;
import fr.joffreybonifay.ccpp.projectplanning.domain.valueobject.BudgetItemId;
import fr.joffreybonifay.ccpp.projectplanning.domain.valueobject.ParticipantId;
import fr.joffreybonifay.ccpp.projectplanning.infrastructure.rest.context.RequestContext;
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
    public ResponseEntity<RequestProjectCreationResponse> requestProjectCreation(
            @RequestBody RequestProjectCreationRequest requestProjectCreationRequest
    ) {
        ProjectId projectId = projectIdGenerator.generate();
        RequestProjectCreationCommand command = new RequestProjectCreationCommand(
                newCommandId(),
                RequestContext.getWorkspaceId(),
                RequestContext.getUserId(),
                projectId,
                requestProjectCreationRequest.title(),
                requestProjectCreationRequest.description(),
                new DateRange(requestProjectCreationRequest.startDate(), requestProjectCreationRequest.endDate()),
                requestProjectCreationRequest.budgetLimit(),
                newCorrelationId()
        );

        commandBus.execute(command);

        return new ResponseEntity<>(new RequestProjectCreationResponse(projectId.value()), HttpStatus.CREATED);
    }

    @PatchMapping("/{projectId}/details")
    private ResponseEntity<Void> updateProjectDetails(
            @PathVariable String projectId,
            @RequestBody UpdateProjectDetailsRequest updateProjectDetailsRequest
    ) {
        commandBus.execute(new UpdateDetailsCommand(
                newCommandId(),
                new ProjectId(projectId),
                updateProjectDetailsRequest.title(),
                updateProjectDetailsRequest.description(),
                newCorrelationId()
        ));
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{projectId}/timeline")
    public ResponseEntity<Void> updateProjectTimeline(
            @PathVariable String projectId,
            @RequestBody UpdateProjectTimelineRequest updateProjectTimelineRequest
    ) {

        commandBus.execute(new ChangeTimelineCommand(
                newCommandId(),
                new ProjectId(projectId),
                new DateRange(
                        updateProjectTimelineRequest.startDate(),
                        updateProjectTimelineRequest.endDate()),
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
        AcceptInvitationCommand command = new AcceptInvitationCommand(
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
        RejectInvitationCommand command = new RejectInvitationCommand(
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
        MarkProjectReadyCommand command = new MarkProjectReadyCommand(
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
