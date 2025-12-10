package io.joffrey.ccpp.projectplanning.domain.model;

import com.ccpp.shared.domain.AggregateRoot;
import com.ccpp.shared.domain.DomainEvent;
import com.ccpp.shared.exception.CurrencyException;
import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.UserId;
import com.ccpp.shared.identities.WorkspaceId;
import com.ccpp.shared.valueobjects.DateRange;
import com.ccpp.shared.valueobjects.Money;
import io.joffrey.ccpp.projectplanning.domain.event.*;
import io.joffrey.ccpp.projectplanning.domain.exception.CannotModifyReadyProjectException;
import io.joffrey.ccpp.projectplanning.domain.exception.InvalidProjectDataException;
import io.joffrey.ccpp.projectplanning.domain.valueobject.BudgetItemId;

import java.util.ArrayList;
import java.util.List;

public class Project extends AggregateRoot {

    private ProjectId projectId;
    private WorkspaceId workspaceId;
    private ProjectStatus projectStatus;
    private List<BudgetItem> budgetItems;

    public static Project create(WorkspaceId workspaceId, UserId userId, ProjectId projectId, String title, String description, DateRange timeline) {
        validateTitle(title);
        validateDescription(description);

        Project project = new Project();
        project.raiseEvent(new ProjectCreated(workspaceId, userId, projectId, title, description, timeline));
        return project;
    }

    public static Project loadFromHistory(List<DomainEvent> events) {
        Project project = new Project();
        events.forEach(project::apply);
        return project;
    }

    private static void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new InvalidProjectDataException("Title cannot be empty");
        }
    }

    private static void validateDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new InvalidProjectDataException("Description cannot be empty");
        }
    }

    public void addBudgetItem(BudgetItemId budgetItemId, String description, Money amount) {
        verifyProjectIsModifiable();
        if (!budgetItems.isEmpty() && !budgetItems.getFirst().getCurrency().equals(amount.currency())) {
            throw new CurrencyException("Cannot add budget items with different currencies.");
        }
        if (description.isBlank()) throw new InvalidProjectDataException("Budget item description cannot be empty");
        raiseEvent(new BudgetItemAdded(projectId, budgetItemId, description, amount));
    }

    public void removeBudgetItem(BudgetItemId budgetItemId) {
        verifyProjectIsModifiable();
        raiseEvent(new BudgetItemRemoved(projectId, budgetItemId));
    }

    public void changeTimeline(DateRange newTimeline) {
        verifyProjectIsModifiable();
        raiseEvent(new ProjectTimelineChanged(projectId, newTimeline));
    }

    public void updateBudgetItem(BudgetItemId budgetItemId, String description, Money newAmount) {
        verifyProjectIsModifiable();
        raiseEvent(new BudgetItemUpdated(projectId, budgetItemId, description, newAmount));
    }

    public void markAsReady(UserId userId) {
        if (projectStatus == ProjectStatus.READY) return;
        raiseEvent(new ProjectMarkedAsReady(projectId, workspaceId, userId));
    }

    private void verifyProjectIsModifiable() {
        if (projectStatus == ProjectStatus.READY)
            throw new CannotModifyReadyProjectException("Cannot modify project in READY status");
    }

    @Override
    protected void apply(DomainEvent event) {
        switch (event) {
            case ProjectCreated projectCreated -> apply(projectCreated);
            case ProjectMarkedAsReady projectMarkedAsReady -> apply(projectMarkedAsReady);
            case ProjectTimelineChanged projectTimelineChanged -> apply(projectTimelineChanged);
            case BudgetItemAdded budgetItemAdded -> apply(budgetItemAdded);
            case BudgetItemRemoved budgetItemRemoved -> apply(budgetItemRemoved);
            case BudgetItemUpdated budgetItemUpdated -> apply(budgetItemUpdated);
            default -> throw new IllegalStateException("Unexpected value: " + event);
        }
    }

    private void apply(ProjectCreated projectCreated) {
        projectId = projectCreated.projectId();
        workspaceId = projectCreated.workspaceId();
        projectStatus = ProjectStatus.PLANNING;
        budgetItems = new ArrayList<>();
    }

    private void apply(ProjectMarkedAsReady projectMarkedAsReady) {
        projectStatus = ProjectStatus.READY;
    }

    private void apply(ProjectTimelineChanged projectTimelineChanged) {

    }

    private void apply(BudgetItemAdded budgetItemAdded) {
        budgetItems.add(new BudgetItem(budgetItemAdded.budgetItemId(), budgetItemAdded.description(), budgetItemAdded.amount()));
    }

    private void apply(BudgetItemRemoved budgetItemRemoved) {

    }

    private void apply(BudgetItemUpdated budgetItemUpdated) {

    }
}
