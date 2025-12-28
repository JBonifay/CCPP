package fr.joffreybonifay.ccpp.projectplanning.infrastructure.rest.dto;

import fr.joffreybonifay.ccpp.projectplanning.application.query.model.ProjectListDTO;

import java.math.BigDecimal;
import java.util.List;

public record ProjectListResponse(
        String projectId,
        String title,
        String status,
        BigDecimal totalBudget,
        int participantCount
) {

    public static List<ProjectListResponse> from(List<ProjectListDTO> projectListDTOS) {
        return projectListDTOS.stream().map(ProjectListResponse::from).toList();
    }

    public static ProjectListResponse from(ProjectListDTO projectListDTO) {
        return new ProjectListResponse(
                projectListDTO.projectId().value().toString(),
                projectListDTO.title(),
                projectListDTO.status().name(),
                projectListDTO.totalBudget(),
                projectListDTO.participantCount()
        );
    }

}
