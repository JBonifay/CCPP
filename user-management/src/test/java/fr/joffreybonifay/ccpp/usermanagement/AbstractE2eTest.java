package fr.joffreybonifay.ccpp.usermanagement;

import fr.joffreybonifay.ccpp.shared.command.CommandBus;
import fr.joffreybonifay.ccpp.shared.eventstore.EventStore;
import fr.joffreybonifay.ccpp.usermanagement.application.query.repository.UserReadRepository;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

@Import({AbstractE2eTestConfiguration.class, TestcontainersConfiguration.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AbstractE2eTest {

    @LocalServerPort
    protected int port;

    @Autowired
    protected UserReadRepository userReadRepository;

    @Autowired
    protected CommandBus commandBus;

    @Autowired
    protected EventStore eventStore;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "";
    }

}
