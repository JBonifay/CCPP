package io.joffrey.ccpp.projectplanning.infrastructure.rest;

import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.UserId;
import com.ccpp.shared.identities.WorkspaceId;
import io.joffrey.ccpp.projectplanning.domain.spi.ProjectIdGenerator;
import io.joffrey.ccpp.projectplanning.infrastructure.spi.MockProjectIdGenerator;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.UUID;

@Import(AbstractE2eTest.AbstractE2eTestConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AbstractE2eTest {

    @LocalServerPort
    protected int port;

    @Autowired
    protected MockProjectIdGenerator projectIdGenerator;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "";
        projectIdGenerator.setMock(new ProjectId(UUID.randomUUID()));
    }

    protected HttpHeaders buildHeaders(WorkspaceId workspaceId, UserId userId) {
        var headers = new HttpHeaders();
        headers.set("X-Workspace-Id", workspaceId.value().toString());
        headers.set("X-User-Id", userId.value().toString());
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @TestConfiguration
    public static class AbstractE2eTestConfiguration {

        @Bean
        @Primary
        MockProjectIdGenerator testProjectIdGenerator() {
            return new MockProjectIdGenerator();
        }

    }

}
