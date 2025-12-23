package fr.joffreybonifay.ccpp.projectplanning.infrastructure.projection;

import fr.joffreybonifay.ccpp.projectplanning.application.query.model.ProjectListDTO;
import fr.joffreybonifay.ccpp.projectplanning.application.query.repository.ProjectListReadRepository;
import fr.joffreybonifay.ccpp.projectplanning.domain.event.*;
import fr.joffreybonifay.ccpp.projectplanning.domain.model.ProjectStatus;
import fr.joffreybonifay.ccpp.projectplanning.domain.valueobject.BudgetItemId;
import fr.joffreybonifay.ccpp.shared.domain.event.DomainEvent;
import fr.joffreybonifay.ccpp.shared.domain.event.ProjectActivated;
import fr.joffreybonifay.ccpp.shared.domain.event.ProjectCreationFailed;
import fr.joffreybonifay.ccpp.shared.domain.event.ProjectCreationRequested;
import fr.joffreybonifay.ccpp.shared.eventhandler.EventHandler;
import fr.joffreybonifay.ccpp.shared.domain.identities.ProjectId;
import fr.joffreybonifay.ccpp.shared.domain.valueobjects.Money;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProjectListProjectionUpdater implements EventHandler {

    private final ProjectListReadRepository repository;

    private final Map<ProjectId, Map<BudgetItemId, Money>> projectBudgetItems = new ConcurrentHashMap<>();

    public ProjectListProjectionUpdater(ProjectListReadRepository repository) {
        this.repository = repository;
    }

    @Override
    public void handle(DomainEvent event) {
        switch (event) {
            case ProjectCreationRequested e -> handleProjectCreated(e);
            case ProjectDetailsUpdated e -> handleDetailsUpdated(e);
            case ProjectMarkedAsReady e -> handleMarkedAsReady(e);
            case BudgetItemAdded e -> handleBudgetItemAdded(e);
            case BudgetItemUpdated e -> handleBudgetItemUpdated(e);
            case BudgetItemRemoved e -> handleBudgetItemRemoved(e);
            case ParticipantInvited e -> handleParticipantInvited(e);
            case ProjectActivated e -> handleProjectActivated(e);
            case ProjectCreationFailed e -> handleProjectCreationFailed(e);
            default -> {} // Ignore other events
        }
    }

    private void handleProjectCreated(ProjectCreationRequested event) {
        projectBudgetItems.put(event.projectId(), new ConcurrentHashMap<>());

        var dto = new ProjectListDTO(
                event.projectId(),
                event.workspaceId(),
                event.title(),
                ProjectStatus.PLANNING,
                event.projectBudgetLimit(),
                0
        );
        repository.save(dto);
    }

    private void handleDetailsUpdated(ProjectDetailsUpdated event) {
        repository.findById(event.projectId()).ifPresent(current -> {
            var updated = new ProjectListDTO(
                    current.projectId(),
                    current.workspaceId(),
                    event.title(),
                    current.status(),
                    current.totalBudget(),
                    current.participantCount()
            );
            repository.update(updated);
        });
    }

    private void handleBudgetItemAdded(BudgetItemAdded event) {
        // Update internal state
        projectBudgetItems
                .computeIfAbsent(event.projectId(), k -> new ConcurrentHashMap<>())
                .put(event.budgetItemId(), event.amount());

        // Recalculate total from internal state
        var newTotal = calculateTotalBudget(event.projectId());

        // Update projection
        updateProjectListTotal(event.projectId(), newTotal);
    }

    private void handleBudgetItemUpdated(BudgetItemUpdated event) {
        // Update internal state
        var budgetItems = projectBudgetItems.get(event.projectId());
        if (budgetItems != null) {
            budgetItems.put(event.budgetItemId(), event.newAmount());
        }

        // Recalculate total from internal state
        var newTotal = calculateTotalBudget(event.projectId());

        // Update projection
        updateProjectListTotal(event.projectId(), newTotal);
    }

    private void handleBudgetItemRemoved(BudgetItemRemoved event) {
        // Update internal state
        var budgetItems = projectBudgetItems.get(event.projectId());
        if (budgetItems != null) {
            budgetItems.remove(event.budgetItemId());
        }

        // Recalculate total from internal state
        var newTotal = calculateTotalBudget(event.projectId());

        // Update projection
        updateProjectListTotal(event.projectId(), newTotal);
    }

    private void handleParticipantInvited(ParticipantInvited event) {
        repository.findById(event.projectId()).ifPresent(current -> {
            var updated = new ProjectListDTO(
                    current.projectId(),
                    current.workspaceId(),
                    current.title(),
                    current.status(),
                    current.totalBudget(),
                    current.participantCount() + 1
            );
            repository.update(updated);
        });
    }

    private void handleMarkedAsReady(ProjectMarkedAsReady event) {
        repository.findById(event.projectId()).ifPresent(current -> {
            var updated = new ProjectListDTO(
                    current.projectId(),
                    current.workspaceId(),
                    current.title(),
                    ProjectStatus.READY,
                    current.totalBudget(),
                    current.participantCount()
            );
            repository.update(updated);
        });
    }

    private BigDecimal calculateTotalBudget(ProjectId projectId) {
        return projectBudgetItems.getOrDefault(projectId, Map.of())
                .values()
                .stream()
                .map(Money::value)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void updateProjectListTotal(ProjectId projectId, BigDecimal newTotal) {
        repository.findById(projectId).ifPresent(current -> {
            var updated = new ProjectListDTO(
                    current.projectId(),
                    current.workspaceId(),
                    current.title(),
                    current.status(),
                    newTotal,
                    current.participantCount()
            );
            repository.update(updated);
        });
    }

    private void handleProjectActivated(ProjectActivated event) {
        repository.findById(event.projectId()).ifPresent(current -> {
            var updated = new ProjectListDTO(
                    current.projectId(),
                    current.workspaceId(),
                    current.title(),
                    ProjectStatus.ACTIVE,
                    current.totalBudget(),
                    current.participantCount()
            );
            repository.update(updated);
        });
    }

    private void handleProjectCreationFailed(ProjectCreationFailed event) {
        repository.findById(event.projectId()).ifPresent(current -> {
            var updated = new ProjectListDTO(
                    current.projectId(),
                    current.workspaceId(),
                    current.title(),
                    ProjectStatus.FAILED,
                    current.totalBudget(),
                    current.participantCount()
            );
            repository.update(updated);
        });
    }
}
