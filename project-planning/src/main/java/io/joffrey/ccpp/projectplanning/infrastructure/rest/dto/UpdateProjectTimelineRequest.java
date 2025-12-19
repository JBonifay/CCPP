package io.joffrey.ccpp.projectplanning.infrastructure.rest.dto;

import java.time.LocalDate;

public record UpdateProjectTimelineRequest(
        LocalDate startDate,
        LocalDate endDate
) {
}
