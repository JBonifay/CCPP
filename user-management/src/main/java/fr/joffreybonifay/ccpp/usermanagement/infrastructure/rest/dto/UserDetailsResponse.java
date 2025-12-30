package fr.joffreybonifay.ccpp.usermanagement.infrastructure.rest.dto;

import fr.joffreybonifay.ccpp.shared.domain.identities.UserId;
import fr.joffreybonifay.ccpp.usermanagement.application.query.model.WorkspaceDTO;

import java.util.List;

public record UserDetailsResponse(
        String id,
        String email,
        String name,
        String role,
        List<WorkspaceResponse> workspaces
) {
    public UserDetailsResponse(UserId userId, String email, String name, String role, List<WorkspaceDTO> workspaces) {
        this(
                userId.value().toString(),
                email,
                name,
                role,
                workspaces.stream().map(workspaceDTO -> new WorkspaceResponse(
                        workspaceDTO.workspaceId().value().toString(),
                        workspaceDTO.workspaceName(),
                        workspaceDTO.workspaceLogoUrl()
                )).toList()
        );
    }
}
