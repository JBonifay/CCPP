package io.joffrey.ccpp.projectplanning.application.query.model;

import com.ccpp.shared.domain.identities.ProjectId;
import com.ccpp.shared.domain.identities.WorkspaceId;
import com.ccpp.shared.domain.valueobjects.DateRange;
import io.joffrey.ccpp.projectplanning.domain.model.ProjectStatus;

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
