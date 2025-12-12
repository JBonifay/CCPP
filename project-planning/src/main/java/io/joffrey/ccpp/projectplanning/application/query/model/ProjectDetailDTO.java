package io.joffrey.ccpp.projectplanning.application.query.model;

import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.WorkspaceId;
import com.ccpp.shared.valueobjects.DateRange;

import java.util.List;

public record ProjectDetailDTO(
        ProjectId projectId,
        WorkspaceId workspaceId,
        String title,
        String description,
        String status,
        List<BudgetItemDTO> budgetItems,
        List<ParticipantDTO> participants,
        List<NoteDTO> notes,
        DateRange timeline
) {
}
