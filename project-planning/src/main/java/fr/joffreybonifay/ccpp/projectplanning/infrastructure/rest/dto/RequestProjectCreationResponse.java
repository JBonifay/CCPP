package fr.joffreybonifay.ccpp.projectplanning.infrastructure.rest.dto;

import java.util.UUID;

public record RequestProjectCreationResponse(
        UUID projectId
) {
}
