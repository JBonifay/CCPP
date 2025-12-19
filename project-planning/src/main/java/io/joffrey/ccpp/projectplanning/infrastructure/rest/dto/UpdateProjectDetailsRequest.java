package io.joffrey.ccpp.projectplanning.infrastructure.rest.dto;

public record UpdateProjectDetailsRequest(
        String title,
        String description
) {
}
