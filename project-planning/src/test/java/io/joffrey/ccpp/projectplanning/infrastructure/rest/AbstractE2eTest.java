package io.joffrey.ccpp.projectplanning.infrastructure.rest;

import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.UserId;
import com.ccpp.shared.identities.WorkspaceId;
import com.ccpp.shared.valueobjects.DateRange;
import com.ccpp.shared.valueobjects.Money;
import com.ccpp.shared.command.CommandBus;
import io.joffrey.ccpp.projectplanning.application.command.command.AddBudgetItemCommand;
import io.joffrey.ccpp.projectplanning.application.command.command.CreateProjectCommand;
import io.joffrey.ccpp.projectplanning.application.command.command.InviteParticipantCommand;
import io.joffrey.ccpp.projectplanning.application.query.repository.ProjectDetailReadRepository;
import io.joffrey.ccpp.projectplanning.application.query.repository.ProjectListReadRepository;
import io.joffrey.ccpp.projectplanning.domain.valueobject.BudgetItemId;
import io.joffrey.ccpp.projectplanning.domain.valueobject.ParticipantId;
import io.joffrey.ccpp.projectplanning.infrastructure.spi.MockBudgetItemIdGenerator;
import io.joffrey.ccpp.projectplanning.infrastructure.spi.MockParticipantIdGenerator;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.UUID;

@Import(AbstractE2eTest.AbstractE2eTestConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AbstractE2eTest {

    @LocalServerPort
    protected int port;

    @Autowired
    protected MockProjectIdGenerator projectIdGenerator;

    @Autowired
    protected MockBudgetItemIdGenerator budgetItemIdGenerator;

    @Autowired
    protected MockParticipantIdGenerator participantIdGenerator;

    @Autowired
    CommandBus commandBus;

    @Autowired
    protected ProjectListReadRepository projectListRepository;

    @Autowired
    protected ProjectDetailReadRepository projectDetailRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "";
        projectIdGenerator.setMock(new ProjectId(UUID.randomUUID()));
        budgetItemIdGenerator.setMock(new BudgetItemId(UUID.randomUUID()));
    }

    @TestConfiguration
    public static class AbstractE2eTestConfiguration {

        @Bean
        @Primary
        MockProjectIdGenerator testProjectIdGenerator() {
            return new MockProjectIdGenerator();
        }

        @Bean
        @Primary
        MockBudgetItemIdGenerator testBudgetItemIdGenerator() {
            return new MockBudgetItemIdGenerator();
        }

        @Bean
        @Primary
        MockParticipantIdGenerator testParticipantIdGenerator() {
            return new MockParticipantIdGenerator();
        }

    }

    public void aProjectExist(WorkspaceId workspaceId, UserId userId, ProjectId projectId) {
        commandBus.execute(new CreateProjectCommand(
                workspaceId,
                userId,
                projectId,
                "Title",
                "Description",
                new DateRange(LocalDate.of(2015, 2, 3), LocalDate.of(2023, 1, 2)),
                BigDecimal.valueOf(1000)
        ));
    }

    public void aBudgetItemIsPresent(ProjectId projectId, BudgetItemId budgetItemId) {
        commandBus.execute(new AddBudgetItemCommand(projectId, budgetItemId, "description", new Money(BigDecimal.ZERO, Currency.getInstance("EUR"))));
    }

    protected void aParticipantIsInvited(ProjectId projectId, ParticipantId participantId) {
        commandBus.execute(new InviteParticipantCommand(projectId, participantId, "mcfly@mcfly.com", "McFly"));
    }
}
