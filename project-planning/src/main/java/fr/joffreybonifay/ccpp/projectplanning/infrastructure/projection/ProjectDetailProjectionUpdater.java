
package fr.joffreybonifay.ccpp.projectplanning.infrastructure.projection;

import fr.joffreybonifay.ccpp.projectplanning.application.projection.ProjectDetailProjection;
import fr.joffreybonifay.ccpp.projectplanning.application.query.model.BudgetItemDTO;
import fr.joffreybonifay.ccpp.projectplanning.application.query.model.NoteDTO;
import fr.joffreybonifay.ccpp.projectplanning.application.query.model.ParticipantDTO;
import fr.joffreybonifay.ccpp.projectplanning.application.query.model.ProjectDetailDTO;
import fr.joffreybonifay.ccpp.projectplanning.application.query.repository.ProjectDetailReadRepository;
import fr.joffreybonifay.ccpp.projectplanning.domain.event.*;
import fr.joffreybonifay.ccpp.projectplanning.domain.model.InvitationStatus;
import fr.joffreybonifay.ccpp.projectplanning.domain.model.ProjectStatus;
import fr.joffreybonifay.ccpp.shared.domain.event.ProjectActivated;
import fr.joffreybonifay.ccpp.shared.domain.event.ProjectCreationFailed;
import fr.joffreybonifay.ccpp.shared.domain.event.ProjectCreationRequested;

import java.util.ArrayList;

public class ProjectDetailProjectionUpdater implements ProjectDetailProjection {

    private final ProjectDetailReadRepository repository;

    public ProjectDetailProjectionUpdater(ProjectDetailReadRepository repository) {
        this.repository = repository;
    }

    @Override
    public void on(ProjectCreationRequested event) {
        var dto = new ProjectDetailDTO(
                event.projectId(),
                event.workspaceId(),
                event.title(),
                event.description(),
                ProjectStatus.PLANNING,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                event.timeline()
        );
        repository.save(dto);
    }

    @Override
    public void on(ProjectDetailsUpdated event) {
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

    @Override
    public void on(ProjectTimelineChanged event) {
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

    @Override
    public void on(BudgetItemAdded event) {
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

    @Override
    public void on(BudgetItemUpdated event) {
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

    @Override
    public void on(BudgetItemRemoved event) {
        repository.findById(event.projectId()).ifPresent(current -> {
            var budgetItems = current.budgetItems().stream()
                    .filter(item -> !item.budgetItemId().equals(event.budgetItem().getBudgetItemId()))
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

    @Override
    public void on(ParticipantInvited event) {
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

    @Override
    public void on(ParticipantAcceptedInvitation event) {
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

    @Override
    public void on(ParticipantDeclinedInvitation event) {
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

    @Override
    public void on(NoteAdded event) {
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

    @Override
    public void on(ProjectMarkedAsReady event) {
        repository.findById(event.projectId()).ifPresent(current -> {
            var updated = new ProjectDetailDTO(
                    current.projectId(),
                    current.workspaceId(),
                    current.title(),
                    current.description(),
                    ProjectStatus.READY,
                    current.budgetItems(),
                    current.participants(),
                    current.notes(),
                    current.timeline()
            );
            repository.update(updated);
        });
    }

    @Override
    public void on(ProjectActivated event) {
        repository.findById(event.projectId()).ifPresent(current -> {
            var updated = new ProjectDetailDTO(
                    current.projectId(),
                    current.workspaceId(),
                    current.title(),
                    current.description(),
                    ProjectStatus.ACTIVE,
                    current.budgetItems(),
                    current.participants(),
                    current.notes(),
                    current.timeline()
            );
            repository.update(updated);
        });
    }

    @Override
    public void on(ProjectCreationFailed event) {
        repository.findById(event.projectId()).ifPresent(current -> {
            var updated = new ProjectDetailDTO(
                    current.projectId(),
                    current.workspaceId(),
                    current.title(),
                    current.description(),
                    ProjectStatus.FAILED,
                    current.budgetItems(),
                    current.participants(),
                    current.notes(),
                    current.timeline()
            );
            repository.update(updated);
        });
    }

}

