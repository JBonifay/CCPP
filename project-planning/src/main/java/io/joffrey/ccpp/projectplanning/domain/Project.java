package io.joffrey.ccpp.projectplanning.domain;

import com.ccpp.shared.domain.AggregateRoot;
import com.ccpp.shared.domain.DomainEvent;
import com.ccpp.shared.domain.event.ProjectCreated;
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
import java.util.List;
import java.util.Map;

public class Project extends AggregateRoot {

    private WorkspaceId workspaceId;
    private ProjectStatus projectStatus;
    private Map<BudgetItemId, BudgetItem> budgetItems;
    private BigDecimal budgetLimit;

    private Project(
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

        ProjectCreated projectCreated = new ProjectCreated(
                projectId,
                workspaceId,
                userId,
                title,
                description,
                timeline,
                projectBudgetLimit
        );
        raiseEvent(projectCreated);
    }

    public Project(List<DomainEvent> events) {
        loadFromHistory(events);
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
        return new Project(workspaceId, userId, projectId, title, description, timeline, projectBudgetLimit);
    }

    public static Project fromHistory(List<DomainEvent> events) {
        return new Project(events);
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
        raiseEvent(new ProjectDetailsUpdated(new ProjectId(aggregateId), title, description));
    }

    public void addBudgetItem(BudgetItemId budgetItemId, String description, Money amount) {
        verifyProjectIsModifiable();
        validateBudgetItem(description, amount);
        raiseEvent(new BudgetItemAdded(new ProjectId(aggregateId), budgetItemId, description, amount));

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
            raiseEvent(new ProjectBudgetCapExceeded(new ProjectId(aggregateId), new Money(totalBudget, currency)));
        }
    }

    public void removeBudgetItem(BudgetItemId budgetItemId) {
        verifyProjectIsModifiable();
        raiseEvent(new BudgetItemRemoved(new ProjectId(aggregateId), budgetItemId));
    }

    public void changeTimeline(DateRange newTimeline) {
        verifyProjectIsModifiable();
        raiseEvent(new ProjectTimelineChanged(new ProjectId(aggregateId), newTimeline));
    }

    public void updateBudgetItem(BudgetItemId budgetItemId, String description, Money newAmount) {
        verifyProjectIsModifiable();
        verifyBudgetItemIsPresent(budgetItemId);
        raiseEvent(new BudgetItemUpdated(new ProjectId(aggregateId), budgetItemId, description, newAmount));
    }

    public void addNote(String content, UserId userId) {
        if (content == null || content.isBlank()) throw new InvalidProjectNoteException("Note content cannot be empty");
        raiseEvent(new NoteAdded(new ProjectId(aggregateId), content, userId));
    }

    public void inviteParticipant(ParticipantId participantId, String mail, String name) {
        if (mail == null || mail.isBlank()) throw new InvalidParticipantDataException("Participant email cannot be empty");
        if (name == null || name.isBlank()) throw new InvalidParticipantDataException("Participant name cannot be empty");
        raiseEvent(new ParticipantInvited(new ProjectId(aggregateId), participantId, mail, name));
    }

    public void participantAcceptedInvitation(ParticipantId participantId) {
        raiseEvent(new ParticipantAcceptedInvitation(new ProjectId(aggregateId), participantId));
    }

    public void participantDeclinedInvitation(ParticipantId participantId) {
        raiseEvent(new ParticipantDeclinedInvitation(new ProjectId(aggregateId), participantId));
    }

    public void markAsReady(UserId userId) {
        if (projectStatus == ProjectStatus.READY) return;
        raiseEvent(new ProjectMarkedAsReady(new ProjectId(aggregateId), workspaceId, userId));
    }

    public void cancel(String reason) {
        raiseEvent(new ProjectCreationCancelled(new ProjectId(aggregateId), reason));
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
    protected void apply(DomainEvent event) {
        switch (event) {
            case ProjectCreated projectCreated -> apply(projectCreated);
            case ProjectCreationCancelled projectCreationCancelled -> apply(projectCreationCancelled);
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
        aggregateId = projectCreated.projectId().value();
        workspaceId = projectCreated.workspaceId();
        projectStatus = ProjectStatus.PLANNING;
        budgetItems = new HashMap<>();
        budgetLimit = projectCreated.projectBudgetLimit();
    }

    private void apply(ProjectCreationCancelled projectCreationCancelled) {}

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
