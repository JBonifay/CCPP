package io.joffrey.ccpp.projectplanning.infrastructure.rest;

import com.ccpp.shared.identities.ProjectId;
import io.joffrey.ccpp.projectplanning.application.query.repository.ProjectDetailReadRepository;
import io.joffrey.ccpp.projectplanning.application.query.repository.ProjectListReadRepository;
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

import java.util.UUID;

@Import(AbstractE2eTest.AbstractE2eTestConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AbstractE2eTest {

    @LocalServerPort
    protected int port;

    @Autowired
    protected MockProjectIdGenerator projectIdGenerator;

    @Autowired
    protected ProjectListReadRepository projectListRepository;

    @Autowired
    protected ProjectDetailReadRepository projectDetailRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "";
        projectIdGenerator.setMock(new ProjectId(UUID.randomUUID()));
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
