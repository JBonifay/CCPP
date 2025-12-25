package fr.joffreybonifay.ccpp.projectplanning.infrastructure.projection;

import fr.joffreybonifay.ccpp.projectplanning.application.projection.ProjectListProjection;
import fr.joffreybonifay.ccpp.projectplanning.application.query.model.ProjectListDTO;
import fr.joffreybonifay.ccpp.projectplanning.application.query.repository.ProjectListReadRepository;
import fr.joffreybonifay.ccpp.projectplanning.domain.event.*;
import fr.joffreybonifay.ccpp.projectplanning.domain.model.ProjectStatus;
import fr.joffreybonifay.ccpp.shared.domain.event.ProjectActivated;
import fr.joffreybonifay.ccpp.shared.domain.event.ProjectCreationFailed;
import fr.joffreybonifay.ccpp.shared.domain.event.ProjectCreationRequested;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
public class ProjectListProjectionUpdater implements ProjectListProjection {

    private final ProjectListReadRepository repository;

    public ProjectListProjectionUpdater(ProjectListReadRepository repository) {
        this.repository = repository;
    }

    @Override
    @EventListener
    @Transactional
    public void on(ProjectCreationRequested event) {
        var dto = new ProjectListDTO(
                event.projectId(),
                event.workspaceId(),
                event.title(),
                ProjectStatus.PLANNING,
                event.projectBudgetLimit(),
                BigDecimal.ZERO,
                0
        );
        repository.save(dto);
    }

    @Override
    @EventListener
    @Transactional
    public void on(ProjectDetailsUpdated event) {
        var current = repository.findById(event.projectId())
                .orElseThrow(() -> new IllegalStateException(
                        "Missing projection for project " + event.projectId()
                ));

        repository.update(new ProjectListDTO(
                current.projectId(),
                current.workspaceId(),
                event.title(),
                current.status(),
                current.totalBudget(),
                current.actualBudget(),
                current.participantCount()
        ));
    }

    @Override
    public void on(BudgetItemAdded event) {
        var current = repository.findById(event.projectId())
                .orElseThrow(() -> new IllegalStateException(
                        "Missing projection for project " + event.projectId()
                ));

        repository.update(new ProjectListDTO(
                current.projectId(),
                current.workspaceId(),
                current.title(),
                current.status(),
                current.totalBudget(),
                current.actualBudget().add(event.amount().value()),
                current.participantCount()
        ));
    }

    @Override
    public void on(BudgetItemUpdated event) {
        var current = repository.findById(event.projectId())
                .orElseThrow(() -> new IllegalStateException(
                        "Missing projection for project " + event.projectId()
                ));

        repository.update(new ProjectListDTO(
                current.projectId(),
                current.workspaceId(),
                current.title(),
                current.status(),
                current.totalBudget(),
                current.actualBudget().subtract(event.oldAmount().value()).add(event.newAmount().value()),
                current.participantCount()
        ));
    }

    @Override
    @EventListener
    @Transactional
    public void on(BudgetItemRemoved event) {
        var current = repository.findById(event.projectId())
                .orElseThrow(() -> new IllegalStateException(
                        "Missing projection for project " + event.projectId()
                ));

        repository.update(new ProjectListDTO(
                current.projectId(),
                current.workspaceId(),
                current.title(),
                current.status(),
                current.totalBudget(),
                current.actualBudget().subtract(event.budgetItem().getCost()),
                current.participantCount()
        ));    }

    @Override
    @EventListener
    @Transactional
    public void on(ParticipantInvited event) {
        var current = repository.findById(event.projectId())
                .orElseThrow(() -> new IllegalStateException(
                        "Missing projection for project " + event.projectId()
                ));

        repository.update(new ProjectListDTO(
                current.projectId(),
                current.workspaceId(),
                current.title(),
                current.status(),
                current.totalBudget(),
                current.actualBudget(),
                current.participantCount() + 1
        ));
    }

    @Override
    @EventListener
    @Transactional
    public void on(ProjectMarkedAsReady event) {
        var current = repository.findById(event.projectId())
                .orElseThrow(() -> new IllegalStateException(
                        "Missing projection for project " + event.projectId()
                ));

        repository.update(new ProjectListDTO(
                current.projectId(),
                current.workspaceId(),
                current.title(),
                ProjectStatus.READY,
                current.totalBudget(),
                current.actualBudget(),
                current.participantCount()
        ));
    }

    @Override
    @EventListener
    @Transactional
    public void on(ProjectActivated event) {
        var current = repository.findById(event.projectId())
                .orElseThrow(() -> new IllegalStateException(
                        "Missing projection for project " + event.projectId()
                ));

        repository.update(new ProjectListDTO(
                current.projectId(),
                current.workspaceId(),
                current.title(),
                ProjectStatus.ACTIVE,
                current.totalBudget(),
                current.actualBudget(),
                current.participantCount()
        ));
    }

    @Override
    @EventListener
    @Transactional
    public void on(ProjectCreationFailed event) {
        var current = repository.findById(event.projectId())
                .orElseThrow(() -> new IllegalStateException(
                        "Missing projection for project " + event.projectId()
                ));

        repository.update(new ProjectListDTO(
                current.projectId(),
                current.workspaceId(),
                current.title(),
                ProjectStatus.FAILED,
                current.totalBudget(),
                current.actualBudget(),
                current.participantCount()
        ));
    }

}
