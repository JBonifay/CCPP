package io.joffrey.ccpp.projectplanning.domain;

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
import io.joffrey.ccpp.projectplanning.domain.exception.InvalidParticipantDataException;
import io.joffrey.ccpp.projectplanning.domain.exception.InvalidProjectDataException;
import io.joffrey.ccpp.projectplanning.domain.exception.InvalidProjectNoteException;
import io.joffrey.ccpp.projectplanning.domain.model.BudgetItem;
import io.joffrey.ccpp.projectplanning.domain.model.ProjectStatus;
import io.joffrey.ccpp.projectplanning.domain.valueobject.BudgetItemId;
import io.joffrey.ccpp.projectplanning.domain.valueobject.ParticipantId;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

public class Project extends AggregateRoot {

    private ProjectId projectId;
    private WorkspaceId workspaceId;
    private ProjectStatus projectStatus;
    private Map<BudgetItemId, BudgetItem> budgetItems;
    private BigDecimal budgetLimit;

    public Project(ProjectId projectId) {
        super(projectId.value());
    }

    public static Project create(
            WorkspaceId workspaceId,
            UserId userId,
            ProjectId projectId,
            String title,
            String description,
            DateRange timeline,
            BigDecimal projectBudgetLimit
    ) {
        validateTitle(title);
        validateDescription(description);

        Project project = new Project(projectId);
        project.raiseEvent(new ProjectCreated(projectId, workspaceId, userId, title, description, timeline, projectBudgetLimit));
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

    public void updateDetails(String title, String description) {
        validateTitle(title);
        validateDescription(description);
        raiseEvent(new ProjectDetailsUpdated(projectId, title, description));
    }

    public void addBudgetItem(BudgetItemId budgetItemId, String description, Money amount) {
        verifyProjectIsModifiable();
        validateBudgetItem(description, amount);
        raiseEvent(new BudgetItemAdded(projectId, budgetItemId, description, amount));

        BigDecimal totalBudget = calculateTotalBudget(amount);
        checkIfBudgetExceedsLimit(totalBudget, amount.currency());
    }

    private void validateBudgetItem(String description, Money amount) {
        if (description.isBlank()) {
            throw new InvalidProjectDataException("Budget item description cannot be empty");
        }

        if (!budgetItems.isEmpty() && !isCurrencyConsistent(amount.currency())) {
            throw new CurrencyException("Cannot add budget items with different currencies.");
        }
    }

    private boolean isCurrencyConsistent(Currency currency) {
        return budgetItems.values().stream()
                .allMatch(item -> item.getCurrency().equals(currency));
    }

    private BigDecimal calculateTotalBudget(Money newAmount) {
        return budgetItems.values()
                .stream()
                .map(BudgetItem::getCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void checkIfBudgetExceedsLimit(BigDecimal totalBudget, Currency currency) {
        if (totalBudget.compareTo(budgetLimit) > 0) {
            raiseEvent(new ProjectBudgetCapExceeded(projectId, new Money(totalBudget, currency)));
        }
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
        verifyBudgetItemIsPresent(budgetItemId);
        raiseEvent(new BudgetItemUpdated(projectId, budgetItemId, description, newAmount));
    }

    public void addNote(String content, UserId userId) {
        if (content == null || content.isBlank()) throw new InvalidProjectNoteException("Note content cannot be empty");
        raiseEvent(new NoteAdded(projectId, content, userId));
    }

    public void inviteParticipant(ParticipantId participantId, String mail, String name) {
        if (mail == null || mail.isBlank()) throw new InvalidParticipantDataException("Participant email cannot be empty");
        if (name == null || name.isBlank()) throw new InvalidParticipantDataException("Participant name cannot be empty");
        raiseEvent(new ParticipantInvited(projectId, participantId, mail, name));
    }

    public void participantAcceptedInvitation(ParticipantId participantId) {
        raiseEvent(new ParticipantAcceptedInvitation(projectId, participantId));
    }

    public void participantDeclinedInvitation(ParticipantId participantId) {
        raiseEvent(new ParticipantDeclinedInvitation(projectId, participantId));
    }

    public void markAsReady(UserId userId) {
        if (projectStatus == ProjectStatus.READY) return;
        raiseEvent(new ProjectMarkedAsReady(projectId, workspaceId, userId));
    }

    private void verifyProjectIsModifiable() {
        if (projectStatus == ProjectStatus.READY)
            throw new CannotModifyReadyProjectException("Cannot modify project in READY status");
    }

    private void verifyBudgetItemIsPresent(BudgetItemId budgetItemId) {
        if (!budgetItems.containsKey(budgetItemId)) {
            throw new InvalidProjectDataException("Budget item not present!");
        }
    }

    @Override
    protected void apply(Object event) {
        switch (event) {
            case ProjectCreated projectCreated -> apply(projectCreated);
            case ProjectDetailsUpdated projectDetailsUpdated -> apply(projectDetailsUpdated);
            case ProjectMarkedAsReady projectMarkedAsReady -> apply(projectMarkedAsReady);
            case ProjectTimelineChanged projectTimelineChanged -> apply(projectTimelineChanged);
            case BudgetItemAdded budgetItemAdded -> apply(budgetItemAdded);
            case BudgetItemRemoved budgetItemRemoved -> apply(budgetItemRemoved);
            case BudgetItemUpdated budgetItemUpdated -> apply(budgetItemUpdated);
            case ProjectBudgetCapExceeded projectBudgetCapExceeded -> apply(projectBudgetCapExceeded);
            case NoteAdded noteAdded -> apply(noteAdded);
            case ParticipantInvited participantInvited -> apply(participantInvited);
            case ParticipantAcceptedInvitation participantAcceptedInvitation -> apply(participantAcceptedInvitation);
            case ParticipantDeclinedInvitation participantDeclinedInvitation -> apply(participantDeclinedInvitation);
            default -> throw new IllegalStateException("Unexpected value: " + event);
        }
    }

    private void apply(ProjectCreated projectCreated) {
        projectId = projectCreated.projectId();
        workspaceId = projectCreated.workspaceId();
        projectStatus = ProjectStatus.PLANNING;
        budgetItems = new HashMap<>();
        budgetLimit = projectCreated.projectBudgetLimit();
    }

    private void apply(ProjectDetailsUpdated projectDetailsUpdated) {}

    private void apply(ProjectMarkedAsReady projectMarkedAsReady) {
        projectStatus = ProjectStatus.READY;
    }

    private void apply(ProjectTimelineChanged projectTimelineChanged) {
    }

    private void apply(BudgetItemAdded budgetItemAdded) {
        budgetItems.put(
                budgetItemAdded.budgetItemId(),
                new BudgetItem(budgetItemAdded.budgetItemId(), budgetItemAdded.description(), budgetItemAdded.amount())
        );
    }

    private void apply(BudgetItemRemoved budgetItemRemoved) {
        budgetItems.remove(budgetItemRemoved.budgetItemId());
    }

    private void apply(BudgetItemUpdated budgetItemUpdated) {
        budgetItems.put(
                budgetItemUpdated.budgetItemId(),
                new BudgetItem(budgetItemUpdated.budgetItemId(),
                        budgetItemUpdated.description(),
                        budgetItemUpdated.newAmount()
                )
        );
    }

    private void apply(ProjectBudgetCapExceeded projectBudgetCapExceeded) {
    }

    private void apply(NoteAdded noteAdded) {
    }

    private void apply(ParticipantInvited participantInvited) {
    }

    private void apply(ParticipantAcceptedInvitation participantAcceptedInvitation) {
    }

    private void apply(ParticipantDeclinedInvitation participantDeclinedInvitation) {}
}
