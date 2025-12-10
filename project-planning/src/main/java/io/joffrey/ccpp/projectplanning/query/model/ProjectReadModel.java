package io.joffrey.ccpp.projectplanning.query.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ProjectReadModel(
        UUID projectId,
        UUID workspaceId,
        String title,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        String status,
        BigDecimal totalBudget,
        int noteCount,
        int participantCount
) {

}
