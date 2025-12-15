
package io.joffrey.ccpp.projectplanning.infrastructure.projection;

import com.ccpp.shared.domain.DomainEvent;
import com.ccpp.shared.domain.EventListener;
import com.ccpp.shared.domain.event.ProjectCreated;
import io.joffrey.ccpp.projectplanning.application.query.model.BudgetItemDTO;
import io.joffrey.ccpp.projectplanning.application.query.model.NoteDTO;
import io.joffrey.ccpp.projectplanning.application.query.model.ParticipantDTO;
import io.joffrey.ccpp.projectplanning.application.query.model.ProjectDetailDTO;
import io.joffrey.ccpp.projectplanning.application.query.repository.ProjectDetailReadRepository;
import io.joffrey.ccpp.projectplanning.domain.event.*;
import io.joffrey.ccpp.projectplanning.domain.model.InvitationStatus;

import java.util.ArrayList;

public class ProjectDetailProjectionUpdater implements EventListener {

    private final ProjectDetailReadRepository repository;

    public ProjectDetailProjectionUpdater(ProjectDetailReadRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean canHandle(DomainEvent event) {
        return event instanceof ProjectDomainEvent;
    }

    @Override
    public void onEvent(DomainEvent event) {
        switch (event) {
            case ProjectCreated e -> handleProjectCreated(e);
            case ProjectDetailsUpdated e -> handleDetailsUpdated(e);
            case ProjectTimelineChanged e -> handleTimelineChanged(e);
            case ProjectMarkedAsReady e -> handleMarkedAsReady(e);
            case BudgetItemAdded e -> handleBudgetItemAdded(e);
            case BudgetItemUpdated e -> handleBudgetItemUpdated(e);
            case BudgetItemRemoved e -> handleBudgetItemRemoved(e);
            case ParticipantInvited e -> handleParticipantInvited(e);
            case ParticipantAcceptedInvitation e -> handleParticipantAccepted(e);
            case ParticipantDeclinedInvitation e -> handleParticipantDeclined(e);
            case NoteAdded e -> handleNoteAdded(e);
            default -> {}
        }
    }

    private void handleProjectCreated(ProjectCreated event) {
        var dto = new ProjectDetailDTO(
                event.projectId(),
                event.workspaceId(),
                event.title(),
                event.description(),
                "PLANNING",
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                event.timeline()
        );
        repository.save(dto);
    }

    private void handleDetailsUpdated(ProjectDetailsUpdated event) {
        repository.findById(event.projectId()).ifPresent(current -> {
            var updated = new ProjectDetailDTO(
                    current.projectId(),
                    current.workspaceId(),
                    event.title(),
                    event.description(),
                    current.status(),
                    current.budgetItems(),
                    current.participants(),
                    current.notes(),
                    current.timeline()
            );
            repository.update(updated);
        });
    }

    private void handleTimelineChanged(ProjectTimelineChanged event) {
        repository.findById(event.projectId()).ifPresent(current -> {
            var updated = new ProjectDetailDTO(
                    current.projectId(),
                    current.workspaceId(),
                    current.title(),
                    current.description(),
                    current.status(),
                    current.budgetItems(),
                    current.participants(),
                    current.notes(),
                    event.newTimeline()
            );
            repository.update(updated);
        });
    }

    private void handleBudgetItemAdded(BudgetItemAdded event) {
        repository.findById(event.projectId()).ifPresent(current -> {
            var budgetItems = new ArrayList<>(current.budgetItems());
            budgetItems.add(new BudgetItemDTO(
                    event.budgetItemId(),
                    event.description(),
                    event.amount()
            ));

            var updated = new ProjectDetailDTO(
                    current.projectId(),
                    current.workspaceId(),
                    current.title(),
                    current.description(),
                    current.status(),
                    budgetItems,
                    current.participants(),
                    current.notes(),
                    current.timeline()
            );
            repository.update(updated);
        });
    }

    private void handleBudgetItemUpdated(BudgetItemUpdated event) {
        repository.findById(event.projectId()).ifPresent(current -> {
            var budgetItems = current.budgetItems().stream()
                    .map(item -> item.budgetItemId().equals(event.budgetItemId())
                            ? new BudgetItemDTO(event.budgetItemId(), event.description(), event.newAmount())
                            : item)
                    .toList();

            var updated = new ProjectDetailDTO(
                    current.projectId(),
                    current.workspaceId(),
                    current.title(),
                    current.description(),
                    current.status(),
                    budgetItems,
                    current.participants(),
                    current.notes(),
                    current.timeline()
            );
            repository.update(updated);
        });
    }

    private void handleBudgetItemRemoved(BudgetItemRemoved event) {
        repository.findById(event.projectId()).ifPresent(current -> {
            var budgetItems = current.budgetItems().stream()
                    .filter(item -> !item.budgetItemId().equals(event.budgetItemId()))
                    .toList();

            var updated = new ProjectDetailDTO(
                    current.projectId(),
                    current.workspaceId(),
                    current.title(),
                    current.description(),
                    current.status(),
                    budgetItems,
                    current.participants(),
                    current.notes(),
                    current.timeline()
            );
            repository.update(updated);
        });
    }

    private void handleParticipantInvited(ParticipantInvited event) {
        repository.findById(event.projectId()).ifPresent(current -> {
            var participants = new ArrayList<>(current.participants());
            participants.add(new ParticipantDTO(
                    event.participantId(),
                    event.name(),
                    event.mail(),
                    InvitationStatus.INVITED
            ));

            var updated = new ProjectDetailDTO(
                    current.projectId(),
                    current.workspaceId(),
                    current.title(),
                    current.description(),
                    current.status(),
                    current.budgetItems(),
                    participants,
                    current.notes(),
                    current.timeline()
            );
            repository.update(updated);
        });
    }

    private void handleParticipantAccepted(ParticipantAcceptedInvitation event) {
        repository.findById(event.projectId()).ifPresent(current -> {
            var participants = current.participants().stream()
                    .map(p -> p.participantId().equals(event.participantId())
                            ? new ParticipantDTO(p.participantId(), p.name(), p.email(), InvitationStatus.ACCEPTED)
                            : p)
                    .toList();

            var updated = new ProjectDetailDTO(
                    current.projectId(),
                    current.workspaceId(),
                    current.title(),
                    current.description(),
                    current.status(),
                    current.budgetItems(),
                    participants,
                    current.notes(),
                    current.timeline()
            );
            repository.update(updated);
        });
    }

    private void handleParticipantDeclined(ParticipantDeclinedInvitation event) {
        repository.findById(event.projectId()).ifPresent(current -> {
            var participants = current.participants().stream()
                    .map(p -> p.participantId().equals(event.participantId())
                            ? new ParticipantDTO(p.participantId(), p.name(), p.email(), InvitationStatus.DECLINED)
                            : p)
                    .toList();

            var updated = new ProjectDetailDTO(
                    current.projectId(),
                    current.workspaceId(),
                    current.title(),
                    current.description(),
                    current.status(),
                    current.budgetItems(),
                    participants,
                    current.notes(),
                    current.timeline()
            );
            repository.update(updated);
        });
    }

    private void handleNoteAdded(NoteAdded event) {
        repository.findById(event.projectId()).ifPresent(current -> {
            var notes = new ArrayList<>(current.notes());
            notes.add(new NoteDTO(
                    event.content(),
                    event.userId()
            ));

            var updated = new ProjectDetailDTO(
                    current.projectId(),
                    current.workspaceId(),
                    current.title(),
                    current.description(),
                    current.status(),
                    current.budgetItems(),
                    current.participants(),
                    notes,
                    current.timeline()
            );
            repository.update(updated);
        });
    }

    private void handleMarkedAsReady(ProjectMarkedAsReady event) {
        repository.findById(event.projectId()).ifPresent(current -> {
            var updated = new ProjectDetailDTO(
                    current.projectId(),
                    current.workspaceId(),
                    current.title(),
                    current.description(),
                    "READY",
                    current.budgetItems(),
                    current.participants(),
                    current.notes(),
                    current.timeline()
            );
            repository.update(updated);
        });
    }
}

