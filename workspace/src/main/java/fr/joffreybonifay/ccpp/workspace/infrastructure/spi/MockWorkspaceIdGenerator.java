package fr.joffreybonifay.ccpp.workspace.infrastructure.spi;

import fr.joffreybonifay.ccpp.shared.domain.identities.WorkspaceId;
import fr.joffreybonifay.ccpp.workspace.domain.WorkspaceIdGenerator;
import lombok.Getter;
import lombok.Setter;

public class MockWorkspaceIdGenerator implements WorkspaceIdGenerator {

    @Setter
    @Getter
    private WorkspaceId mock;

    @Override
    public WorkspaceId generate() {
        return mock;
    }

    public String getValue() {
        return mock.value().toString();
    }

}
