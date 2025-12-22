package fr.joffreybonifay.ccpp.workspace;

import fr.joffreybonifay.ccpp.shared.command.CommandBus;
import fr.joffreybonifay.ccpp.shared.eventstore.EventStore;
import fr.joffreybonifay.ccpp.shared.identities.WorkspaceId;
import fr.joffreybonifay.ccpp.workspace.infrastructure.spi.MockWorkspaceIdGenerator;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

@ActiveProfiles("in-memory")
@Import({AbstractE2eTestConfiguration.class, TestcontainersConfiguration.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AbstractE2eTest {

    @LocalServerPort
    protected int port;

    @Autowired
    protected MockWorkspaceIdGenerator workspaceIdGenerator;

    @Autowired
    protected CommandBus commandBus;

    @Autowired
    protected EventStore eventStore;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "";
        workspaceIdGenerator.setMock(new WorkspaceId(UUID.randomUUID()));
    }

}
