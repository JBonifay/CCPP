package fr.joffreybonifay.ccpp.projectplanning;

import fr.joffreybonifay.ccpp.shared.command.CommandBus;
import fr.joffreybonifay.ccpp.shared.eventstore.EventStore;
import fr.joffreybonifay.ccpp.shared.identities.ProjectId;
import fr.joffreybonifay.ccpp.shared.identities.UserId;
import fr.joffreybonifay.ccpp.shared.identities.WorkspaceId;
import fr.joffreybonifay.ccpp.shared.valueobjects.DateRange;
import fr.joffreybonifay.ccpp.shared.valueobjects.Money;
import fr.joffreybonifay.ccpp.projectplanning.application.command.command.AddBudgetItemCommand;
import fr.joffreybonifay.ccpp.projectplanning.application.command.command.InviteParticipantCommand;
import fr.joffreybonifay.ccpp.projectplanning.application.query.model.ProjectDetailDTO;
import fr.joffreybonifay.ccpp.projectplanning.application.query.repository.ProjectDetailReadRepository;
import fr.joffreybonifay.ccpp.projectplanning.application.query.repository.ProjectListReadRepository;
import fr.joffreybonifay.ccpp.projectplanning.domain.event.ProjectCreated;
import fr.joffreybonifay.ccpp.projectplanning.domain.model.ProjectStatus;
import fr.joffreybonifay.ccpp.projectplanning.domain.valueobject.BudgetItemId;
import fr.joffreybonifay.ccpp.projectplanning.domain.valueobject.ParticipantId;
import fr.joffreybonifay.ccpp.projectplanning.infrastructure.spi.MockBudgetItemIdGenerator;
import fr.joffreybonifay.ccpp.projectplanning.infrastructure.spi.MockParticipantIdGenerator;
import fr.joffreybonifay.ccpp.projectplanning.infrastructure.spi.MockProjectIdGenerator;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

@ActiveProfiles("in-memory")
@Import({AbstractE2eTestConfiguration.class, TestcontainersConfiguration.class})
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
    protected ProjectListReadRepository projectListRepository;

    @Autowired
    protected ProjectDetailReadRepository projectDetailRepository;

    @Autowired
    protected CommandBus commandBus;

    @Autowired
    protected EventStore eventStore;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "";
        projectIdGenerator.setMock(new ProjectId(UUID.randomUUID()));
        budgetItemIdGenerator.setMock(new BudgetItemId(UUID.randomUUID()));
    }

    public void aProjectExist(WorkspaceId workspaceId, UserId userId, ProjectId projectId) {
        eventStore.saveEvents(projectId.value(), List.of(
                new ProjectCreated(projectId,
                        workspaceId,
                        userId,
                        "Title",
                        "Description",
                        new DateRange(LocalDate.of(2015, 2, 3), LocalDate.of(2023, 1, 2)),
                        BigDecimal.valueOf(1000))
        ), -1, null, null);
        projectDetailRepository.save(new ProjectDetailDTO(projectId,
                workspaceId,
                "Title",
                "Description",
                ProjectStatus.READY,
                List.of(),
                List.of(),
                List.of(),
                new DateRange(LocalDate.of(2015, 2, 3), LocalDate.of(2023, 1, 2))
        ));
    }

    public void aBudgetItemIsPresent(ProjectId projectId, BudgetItemId budgetItemId) {
        commandBus.execute(new AddBudgetItemCommand(projectId, budgetItemId, "description", new Money(BigDecimal.ZERO, Currency.getInstance("EUR")), null));
    }

    protected void aParticipantIsInvited(ProjectId projectId, ParticipantId participantId) {
        commandBus.execute(new InviteParticipantCommand(projectId, participantId, "mcfly@mcfly.com", "McFly", null));
    }
}
