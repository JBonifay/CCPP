package io.joffrey.ccpp.projectplanning.domain.event;

import com.ccpp.shared.domain.DomainEvent;
import com.ccpp.shared.identities.ProjectId;


public sealed interface ProjectDomainEvent extends DomainEvent permits ProjectCreated, BudgetItemAdded,
    BudgetItemRemoved, BudgetItemUpdated, NoteAdded, ParticipantAcceptedInvitation, ParticipantDeclinedInvitation,
    ParticipantInvited, ProjectBudgetCapExceeded, ProjectDetailsUpdated, ProjectMarkedAsReady,
    ProjectTimelineChanged {

    ProjectId projectId();
}
