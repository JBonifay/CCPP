package fr.joffreybonifay.ccpp.usermanagement.infrastructure.repository;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.UUID;

@Getter
@Entity
@Table(name = "user_workspaces")
public class UserWorkspacesJpaEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;

    @Column(name = "workspace_name", nullable = false)
    private String workspaceName;

    @Column(name = "workspace_logo_url", nullable = false)
    private String workspaceLogoUrl;

    public UserWorkspacesJpaEntity() {
    }

    public UserWorkspacesJpaEntity(UUID userId, UUID workspaceId, String workspaceName, String workspaceLogoUrl) {
        this.userId = userId;
        this.workspaceId = workspaceId;
        this.workspaceName = workspaceName;
        this.workspaceLogoUrl = workspaceLogoUrl;
    }

}
