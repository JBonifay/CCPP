package io.joffrey.ccpp.projectplanning.infrastructure.rest.dto;

import io.joffrey.ccpp.projectplanning.application.query.model.ProjectDetailDTO;

import java.time.LocalDate;
import java.util.List;

public record ProjectDetailResponse(
        String projectId,
        String workspaceId,
        String title,
        String description,
        String status,
        List<BudgetItemResponse> budgetItems,
        List<ParticipantResponse> participants,
        List<NoteResponse> notes,
        LocalDate startDate,
        LocalDate endDate
) {

    public static ProjectDetailResponse from(ProjectDetailDTO projectDetailDTO) {
        return new ProjectDetailResponse(
                projectDetailDTO.projectId().value().toString(),
                projectDetailDTO.workspaceId().value().toString(),
                projectDetailDTO.title(),
                projectDetailDTO.description(),
                projectDetailDTO.status().name(),
                projectDetailDTO.budgetItems().stream().map(BudgetItemResponse::from).toList(),
                projectDetailDTO.participants().stream().map(ParticipantResponse::from).toList(),
                projectDetailDTO.notes().stream().map(NoteResponse::from).toList(),
                projectDetailDTO.timeline().startDate(),
                projectDetailDTO.timeline().endDate()
        );
    }

}
