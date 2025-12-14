package io.joffrey.ccpp.projectplanning.infrastructure.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateProjectRequest(
        String title,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal budgetLimit
) {
}
