package io.joffrey.ccpp.projectplanning.infrastructure.rest.dto;

import java.time.LocalDate;

public record ChangeProjectTimelineRequest(
        LocalDate startDate,
        LocalDate endDate
) {
}
