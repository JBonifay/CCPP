package io.joffrey.ccpp.projectplanning.application.query.handler;

import io.joffrey.ccpp.projectplanning.domain.event.*;
import io.joffrey.ccpp.projectplanning.application.query.model.ProjectReadModel;
import io.joffrey.ccpp.projectplanning.application.query.repository.ProjectQueryRepository;

import java.math.BigDecimal;

public class ProjectEventHandler {

    private final ProjectQueryRepository repository;

    public ProjectEventHandler(ProjectQueryRepository repository) {
        this.repository = repository;
    }

    public void handle(ProjectCreated event) {
        var readModel = new ProjectReadModel(
                event.projectId().value(),
                event.workspaceId().value(),
                event.title(),
                event.description(),
                event.timeline().startDate(),
                event.timeline().endDate(),
                "PLANNING",
                BigDecimal.ZERO,
                0,
                0
        );
        repository.save(readModel);
    }

    public void handle(ProjectDetailsUpdated event) {
        var existing = repository.findById(event.projectId().value()).orElseThrow();
        var updated = new ProjectReadModel(
                existing.projectId(),
                existing.workspaceId(),
                event.title(),
                event.description(),
                existing.startDate(),
                existing.endDate(),
                existing.status(),
                existing.totalBudget(),
                existing.noteCount(),
                existing.participantCount()
        );
        repository.save(updated);
    }

    public void handle(ProjectMarkedAsReady event) {
        var existing = repository.findById(event.projectId().value()).orElseThrow();
        var updated = new ProjectReadModel(
                existing.projectId(),
                existing.workspaceId(),
                existing.title(),
                existing.description(),
                existing.startDate(),
                existing.endDate(),
                "READY",
                existing.totalBudget(),
                existing.noteCount(),
                existing.participantCount()
        );
        repository.save(updated);
    }

    public void handle(ProjectTimelineChanged event) {
        var existing = repository.findById(event.projectId().value()).orElseThrow();
        var updated = new ProjectReadModel(
                existing.projectId(),
                existing.workspaceId(),
                existing.title(),
                existing.description(),
                event.newTimeline().startDate(),
                event.newTimeline().endDate(),
                existing.status(),
                existing.totalBudget(),
                existing.noteCount(),
                existing.participantCount()
        );
        repository.save(updated);
    }

    public void handle(BudgetItemAdded event) {
        var existing = repository.findById(event.projectId().value()).orElseThrow();
        var newTotalBudget = existing.totalBudget().add(event.amount().amount());
        var updated = new ProjectReadModel(
                existing.projectId(),
                existing.workspaceId(),
                existing.title(),
                existing.description(),
                existing.startDate(),
                existing.endDate(),
                existing.status(),
                newTotalBudget,
                existing.noteCount(),
                existing.participantCount()
        );
        repository.save(updated);
    }

    public void handle(BudgetItemUpdated event) {
        // TODO: Cannot update totalBudget without storing individual budget items in read model
        // Options: 1) Enrich ProjectReadModel with List<BudgetItem>, or 2) Rebuild projection from events
        // For now, totalBudget will not reflect updates, only additions
    }

    public void handle(BudgetItemRemoved event) {
        // TODO: Cannot update totalBudget without storing individual budget items in read model
        // BudgetItemRemoved event doesn't contain the amount, so we can't subtract from totalBudget
        // Options: 1) Enrich ProjectReadModel with List<BudgetItem>, or 2) Rebuild projection from events
    }

    public void handle(ProjectBudgetCapExceeded event) {
        // Informational event - no read model update needed
        // Could add a warning flag to ProjectReadModel if needed
    }

    public void handle(NoteAdded event) {
        var existing = repository.findById(event.projectId().value()).orElseThrow();
        var updated = new ProjectReadModel(
                existing.projectId(),
                existing.workspaceId(),
                existing.title(),
                existing.description(),
                existing.startDate(),
                existing.endDate(),
                existing.status(),
                existing.totalBudget(),
                existing.noteCount() + 1,
                existing.participantCount()
        );
        repository.save(updated);
    }

    public void handle(ParticipantInvited event) {
        var existing = repository.findById(event.projectId().value()).orElseThrow();
        var updated = new ProjectReadModel(
                existing.projectId(),
                existing.workspaceId(),
                existing.title(),
                existing.description(),
                existing.startDate(),
                existing.endDate(),
                existing.status(),
                existing.totalBudget(),
                existing.noteCount(),
                existing.participantCount() + 1
        );
        repository.save(updated);
    }

    public void handle(ParticipantAcceptedInvitation event) {
        // Read model only tracks count, not individual participant status
        // No update needed unless we want to track accepted/declined separately
    }

    public void handle(ParticipantDeclinedInvitation event) {
        // Read model only tracks count, not individual participant status
        // Could decrement participantCount if desired
        var existing = repository.findById(event.projectId().value()).orElseThrow();
        var updated = new ProjectReadModel(
                existing.projectId(),
                existing.workspaceId(),
                existing.title(),
                existing.description(),
                existing.startDate(),
                existing.endDate(),
                existing.status(),
                existing.totalBudget(),
                existing.noteCount(),
                existing.participantCount() - 1
        );
        repository.save(updated);
    }

}
