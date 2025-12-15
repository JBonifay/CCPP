package io.joffrey.ccpp.projectplanning.infrastructure.rest.controller;

import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.valueobjects.DateRange;
import com.ccpp.shared.valueobjects.Money;
import io.joffrey.ccpp.projectplanning.application.command.CommandBus;
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

    @PatchMapping("/{projectId}/timeline")
    public ResponseEntity<Void> changeProjectTimeline(
            @PathVariable String projectId,
            @RequestBody ChangeProjectTimelineRequest changeProjectTimelineRequest
    ) {
        commandBus.execute(new ChangeProjectTimelineCommand(new ProjectId(projectId), new DateRange(changeProjectTimelineRequest.startDate(), changeProjectTimelineRequest.endDate())));
        return ResponseEntity.noContent().build();
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

    @DeleteMapping("/{projectId}/budget-items/{budgetItemId}")
    public ResponseEntity<Void> removeBudgetItem(
            @PathVariable String projectId,
            @PathVariable String budgetItemId
    ) {
        commandBus.execute(new RemoveBudgetItemCommand(new ProjectId(projectId), new BudgetItemId(budgetItemId)));
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{projectId}/budget-items/{budgetItemId}")
    public ResponseEntity<Void> updateBudgetItem(
            @PathVariable String projectId,
            @PathVariable String budgetItemId,
            @RequestBody UpdateBudgetItemRequest updateBudgetItemRequest
    ) {
        commandBus.execute(new UpdateBudgetItemCommand(
                new ProjectId(projectId),
                new BudgetItemId(budgetItemId),
                updateBudgetItemRequest.description(),
                updateBudgetItemRequest.amount()));
        return ResponseEntity.noContent().build();
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

    @PostMapping("/{projectId}/participants/{participantId}/accept")
    public ResponseEntity<UUID> participantAcceptedInvitation(
            @PathVariable String projectId,
            @PathVariable String participantId
    ) {
        commandBus.execute(new AcceptParticipantInvitationCommand(new ProjectId(projectId), new ParticipantId(UUID.fromString(participantId))));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{projectId}/participants/{participantId}/decline")
    public ResponseEntity<UUID> participantDeclinedInvitation(
            @PathVariable String projectId,
            @PathVariable String participantId
    ) {
        commandBus.execute(new DeclineParticipantInvitationCommand(new ProjectId(projectId), new ParticipantId(UUID.fromString(participantId))));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{projectId}/notes")
    public ResponseEntity<UUID> addNote(
            @PathVariable String projectId,
            @RequestBody AddNoteRequest addNoteRequest
    ) {
        commandBus.execute(new AddNoteCommand(new ProjectId(projectId), addNoteRequest.content(), RequestContext.getUserId()));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/{projectId}/ready")
    public ResponseEntity<Void> markProjectAsReady(
            @PathVariable String projectId
    ) {
        commandBus.execute(new MarkProjectAsReadyCommand(new ProjectId(projectId), RequestContext.getUserId()));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
