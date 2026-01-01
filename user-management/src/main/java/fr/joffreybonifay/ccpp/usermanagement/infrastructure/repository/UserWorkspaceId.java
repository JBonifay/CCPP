package fr.joffreybonifay.ccpp.usermanagement.infrastructure.repository;

import java.io.Serializable;
import java.util.UUID;

public class UserWorkspaceId implements Serializable {
    private UUID userId;
    private UUID workspaceId;
}
