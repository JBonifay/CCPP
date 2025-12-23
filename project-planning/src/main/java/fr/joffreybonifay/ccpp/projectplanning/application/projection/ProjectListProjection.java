package fr.joffreybonifay.ccpp.projectplanning.application.projection;

import fr.joffreybonifay.ccpp.projectplanning.domain.event.*;
import fr.joffreybonifay.ccpp.shared.domain.event.ProjectActivated;
import fr.joffreybonifay.ccpp.shared.domain.event.ProjectCreationFailed;
import fr.joffreybonifay.ccpp.shared.domain.event.ProjectCreationRequested;

public interface ProjectListProjection {
    void on(ProjectCreationRequested event);
    void on(ProjectDetailsUpdated event);
    void on(ProjectMarkedAsReady event);
    void on(BudgetItemAdded event);
    void on(BudgetItemUpdated event);
    void on(BudgetItemRemoved event);
    void on(ParticipantInvited event);
    void on(ProjectActivated event);
    void on(ProjectCreationFailed event);
}
