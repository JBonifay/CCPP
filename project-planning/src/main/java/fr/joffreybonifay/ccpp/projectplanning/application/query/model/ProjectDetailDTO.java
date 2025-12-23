package fr.joffreybonifay.ccpp.projectplanning.application.query.model;

import fr.joffreybonifay.ccpp.shared.domain.identities.ProjectId;
import fr.joffreybonifay.ccpp.shared.domain.identities.WorkspaceId;
import fr.joffreybonifay.ccpp.shared.domain.valueobjects.DateRange;
import fr.joffreybonifay.ccpp.projectplanning.domain.model.ProjectStatus;

import java.util.List;

public record ProjectDetailDTO(
        ProjectId projectId,
        WorkspaceId workspaceId,
        String title,
        String description,
        ProjectStatus status,
        List<BudgetItemDTO> budgetItems,
        List<ParticipantDTO> participants,
        List<NoteDTO> notes,
        DateRange timeline
) {
}
