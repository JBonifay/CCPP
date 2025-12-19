package io.joffrey.ccpp.projectplanning.infrastructure.rest.dto;

public record ChangeProjectDetailsRequest(
        String title,
        String description
) {
}
