package io.joffrey.ccpp.projectplanning.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.ccpp.shared.eventstore.EventStore;
import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.UserId;
import com.ccpp.shared.identities.WorkspaceId;
import com.ccpp.shared.eventstore.InMemoryEventStore;
import com.ccpp.shared.valueobjects.DateRange;
import com.ccpp.shared.valueobjects.Money;
import io.joffrey.ccpp.projectplanning.application.command.command.AddBudgetItemCommand;
import io.joffrey.ccpp.projectplanning.application.command.command.AddNoteCommand;
import io.joffrey.ccpp.projectplanning.application.command.command.CreateProjectCommand;
import io.joffrey.ccpp.projectplanning.application.command.command.InviteParticipantCommand;
import io.joffrey.ccpp.projectplanning.application.command.command.MarkProjectAsReadyCommand;
import io.joffrey.ccpp.projectplanning.application.command.command.RemoveBudgetItemCommand;
import io.joffrey.ccpp.projectplanning.application.command.command.UpdateBudgetItemCommand;
import io.joffrey.ccpp.projectplanning.application.command.handler.AddBudgetItemHandler;
import io.joffrey.ccpp.projectplanning.application.command.handler.AddNoteHandler;
import io.joffrey.ccpp.projectplanning.application.command.handler.CreateProjectHandler;
import io.joffrey.ccpp.projectplanning.application.command.handler.InviteParticipantHandler;
import io.joffrey.ccpp.projectplanning.application.command.handler.MarkProjectAsReadyHandler;
import io.joffrey.ccpp.projectplanning.application.command.handler.RemoveBudgetItemHandler;
import io.joffrey.ccpp.projectplanning.application.command.handler.UpdateBudgetItemHandler;
import io.joffrey.ccpp.projectplanning.application.query.GetProjectDetailQuery;
import io.joffrey.ccpp.projectplanning.application.query.GetProjectListQuery;
import io.joffrey.ccpp.projectplanning.application.query.handler.GetProjectDetailQueryHandler;
import io.joffrey.ccpp.projectplanning.application.query.handler.GetProjectListQueryHandler;
import io.joffrey.ccpp.projectplanning.application.query.model.ProjectListDTO;
import io.joffrey.ccpp.projectplanning.application.query.repository.ProjectDetailReadRepository;
import io.joffrey.ccpp.projectplanning.application.query.repository.ProjectListReadRepository;
import io.joffrey.ccpp.projectplanning.domain.model.InvitationStatus;
import io.joffrey.ccpp.projectplanning.domain.model.ProjectStatus;
import io.joffrey.ccpp.projectplanning.domain.valueobject.BudgetItemId;
import io.joffrey.ccpp.projectplanning.domain.valueobject.ParticipantId;
import io.joffrey.ccpp.projectplanning.infrastructure.query.InMemoryProjectDetailReadRepository;
import io.joffrey.ccpp.projectplanning.infrastructure.query.InMemoryProjectListReadRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CQRSIntegrationTest {

    EventStore eventStore = new InMemoryEventStore();
    ProjectListReadRepository listRepository = new InMemoryProjectListReadRepository();
    ProjectDetailReadRepository detailRepository = new InMemoryProjectDetailReadRepository();

    CreateProjectHandler createProjectHandler = new CreateProjectHandler(eventStore);
    AddBudgetItemHandler addBudgetItemHandler = new AddBudgetItemHandler(eventStore);
    UpdateBudgetItemHandler updateBudgetItemHandler = new UpdateBudgetItemHandler(eventStore);
    RemoveBudgetItemHandler removeBudgetItemHandler = new RemoveBudgetItemHandler(eventStore);
    InviteParticipantHandler inviteParticipantHandler = new InviteParticipantHandler(eventStore);
    AddNoteHandler addNoteHandler = new AddNoteHandler(eventStore);
    MarkProjectAsReadyHandler markReadyHandler = new MarkProjectAsReadyHandler(eventStore);

    GetProjectListQueryHandler listQueryHandler = new GetProjectListQueryHandler(listRepository);
    GetProjectDetailQueryHandler detailQueryHandler = new GetProjectDetailQueryHandler(detailRepository);

    ProjectId projectId = new ProjectId(UUID.randomUUID());
    WorkspaceId workspaceId = new WorkspaceId(UUID.randomUUID());
    UserId userId = new UserId(UUID.randomUUID());
    DateRange timeline = new DateRange(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 3, 31));

    @BeforeEach
    void setUp() {
//        eventBus.subscribe(new ProjectListProjectionUpdater(listRepository));
//        eventBus.subscribe(new ProjectDetailProjectionUpdater(detailRepository));
    }

    @Test
    void should_project_created_project_to_both_read_models() {
        createProjectHandler.handle(new CreateProjectCommand(
            workspaceId,
            userId,
            projectId,
            "Q1 2025 Video Series",
            "Educational content about Event Sourcing",
            timeline,
            BigDecimal.valueOf(5000)
        ));

        assertThat(listQueryHandler.handle(new GetProjectListQuery(workspaceId))).containsExactly(
            new ProjectListDTO(
                projectId,
                workspaceId,
                "Q1 2025 Video Series",
                ProjectStatus.PLANNING,
                BigDecimal.valueOf(5000),
                0
            )
        );

        var detailQuery = new GetProjectDetailQuery(projectId, workspaceId);
        var detail = detailQueryHandler.handle(detailQuery);

        assertThat(detail.title()).isEqualTo("Q1 2025 Video Series");
        assertThat(detail.description()).isEqualTo("Educational content about Event Sourcing");
        assertThat(detail.budgetItems()).isEmpty();
    }

    @Test
    void should_update_projections_when_budget_items_added() {
        // GIVEN - Project created
        givenProjectCreated();

        // WHEN - Add budget item
        var budgetItemId = new BudgetItemId(UUID.randomUUID());
        var addCommand = new AddBudgetItemCommand(
                projectId,
                budgetItemId,
                "Hotel 2x nights",
                Money.of(300, "USD")
        );
        addBudgetItemHandler.handle(addCommand);

        // THEN - List projection shows updated total budget
        var list = listQueryHandler.handle(new GetProjectListQuery(workspaceId));
        assertThat(list.get(0).totalBudget()).isEqualByComparingTo(BigDecimal.valueOf(300));

        // AND - Detail projection shows budget item in collection
        var detail = detailQueryHandler.handle(new GetProjectDetailQuery(projectId, workspaceId));
        assertThat(detail.budgetItems())
                .hasSize(1)
                .first()
                .satisfies(item -> {
                    assertThat(item.description()).isEqualTo("Hotel 2x nights");
                    assertThat(item.amount()).isEqualTo(Money.of(300, "USD"));
                });
    }

    @Test
    void should_update_projections_when_budget_items_updated() {
        // GIVEN - Project with budget item
        givenProjectCreated();
        var budgetItemId = new BudgetItemId(UUID.randomUUID());
        addBudgetItemHandler.handle(new AddBudgetItemCommand(
                projectId, budgetItemId, "Hotel", Money.of(300, "USD")
        ));

        // WHEN - Update budget item
        var updateCommand = new UpdateBudgetItemCommand(
                projectId,
                budgetItemId,
                "Hotel (3 nights)",
                Money.of(450, "USD")
        );
        updateBudgetItemHandler.handle(updateCommand);

        // THEN - List projection shows updated total
        var list = listQueryHandler.handle(new GetProjectListQuery(workspaceId));
        assertThat(list.get(0).totalBudget()).isEqualByComparingTo(BigDecimal.valueOf(450));

        // AND - Detail projection shows updated item
        var detail = detailQueryHandler.handle(new GetProjectDetailQuery(projectId, workspaceId));
        assertThat(detail.budgetItems().get(0).description()).isEqualTo("Hotel (3 nights)");
        assertThat(detail.budgetItems().get(0).amount()).isEqualTo(Money.of(450, "USD"));
    }

    @Test
    void should_update_projections_when_budget_items_removed() {
        // GIVEN - Project with budget item
        givenProjectCreated();
        var budgetItemId = new BudgetItemId(UUID.randomUUID());
        addBudgetItemHandler.handle(new AddBudgetItemCommand(
                projectId, budgetItemId, "Hotel", Money.of(300, "USD")
        ));

        // WHEN - Remove budget item
        var removeCommand = new RemoveBudgetItemCommand(projectId, budgetItemId);
        removeBudgetItemHandler.handle(removeCommand);

        // THEN - List projection shows zero budget
        var list = listQueryHandler.handle(new GetProjectListQuery(workspaceId));
        assertThat(list.get(0).totalBudget()).isEqualByComparingTo(BigDecimal.ZERO);

        // AND - Detail projection has no budget items
        var detail = detailQueryHandler.handle(new GetProjectDetailQuery(projectId, workspaceId));
        assertThat(detail.budgetItems()).isEmpty();
    }

    @Test
    void should_update_projections_when_participants_invited() {
        // GIVEN - Project created
        givenProjectCreated();

        // WHEN - Invite participant
        var participantId = new ParticipantId(UUID.randomUUID());
        var inviteCommand = new InviteParticipantCommand(
                projectId,
                participantId,
                "john@example.com",
                "John Doe"
        );
        inviteParticipantHandler.handle(inviteCommand);

        // THEN - List projection shows participant count
        var list = listQueryHandler.handle(new GetProjectListQuery(workspaceId));
        assertThat(list.get(0).participantCount()).isEqualTo(1);

        // AND - Detail projection shows participant details
        var detail = detailQueryHandler.handle(new GetProjectDetailQuery(projectId, workspaceId));
        assertThat(detail.participants())
                .hasSize(1)
                .first()
                .satisfies(p -> {
                    assertThat(p.name()).isEqualTo("John Doe");
                    assertThat(p.email()).isEqualTo("john@example.com");
                    assertThat(p.invitationStatus()).isEqualTo(InvitationStatus.INVITED);
                });
    }

    @Test
    void should_update_projections_when_notes_added() {
        // GIVEN - Project created
        givenProjectCreated();

        // WHEN - Add note
        var addNoteCommand = new AddNoteCommand(
                projectId,
                "Remember to book equipment",
                userId
        );
        addNoteHandler.handle(addNoteCommand);

        // THEN - Detail projection shows note
        var detail = detailQueryHandler.handle(new GetProjectDetailQuery(projectId, workspaceId));
        assertThat(detail.notes())
                .hasSize(1)
                .first()
                .satisfies(note -> {
                    assertThat(note.content()).isEqualTo("Remember to book equipment");
                    assertThat(note.createdBy()).isEqualTo(userId);
                });
    }

    @Test
    void should_update_projections_when_marked_as_ready() {
        // GIVEN - Project created
        givenProjectCreated();

        // WHEN - Mark as ready
        var markReadyCommand = new MarkProjectAsReadyCommand(projectId, userId);
        markReadyHandler.handle(markReadyCommand);

        // THEN - List projection shows READY status
        var list = listQueryHandler.handle(new GetProjectListQuery(workspaceId));
        assertThat(list.get(0).status()).isEqualTo(ProjectStatus.READY);

        // AND - Detail projection shows READY status
        var detail = detailQueryHandler.handle(new GetProjectDetailQuery(projectId, workspaceId));
        assertThat(detail.status()).isEqualTo(ProjectStatus.READY);
    }

    @Test
    void should_enforce_multi_tenant_isolation_in_queries() {
        // GIVEN - Projects in different workspaces
        givenProjectCreated();

        var otherWorkspace = new WorkspaceId(UUID.randomUUID());
        var otherProjectId = new ProjectId(UUID.randomUUID());
        createProjectHandler.handle(new CreateProjectCommand(
                otherWorkspace,
                userId,
                otherProjectId,
                "Other Project",
                "Description",
                timeline,
                BigDecimal.valueOf(1000)
        ));

        // WHEN - Query for first workspace
        var list = listQueryHandler.handle(new GetProjectListQuery(workspaceId));

        // THEN - Only returns projects from that workspace
        assertThat(list)
                .hasSize(1)
                .extracting("title")
                .containsExactly("Q1 2025 Video Series");
    }

    @Test
    void should_handle_complex_scenario_with_multiple_operations() {
        // GIVEN - Project created
        givenProjectCreated();

        // WHEN - Multiple operations
        var budgetItem1 = new BudgetItemId(UUID.randomUUID());
        var budgetItem2 = new BudgetItemId(UUID.randomUUID());

        addBudgetItemHandler.handle(new AddBudgetItemCommand(
                projectId, budgetItem1, "Hotel", Money.of(300, "USD")
        ));
        addBudgetItemHandler.handle(new AddBudgetItemCommand(
                projectId, budgetItem2, "Equipment", Money.of(500, "USD")
        ));

        inviteParticipantHandler.handle(new InviteParticipantCommand(
                projectId, new ParticipantId(UUID.randomUUID()), "John Doe", "john@example.com"
        ));
        inviteParticipantHandler.handle(new InviteParticipantCommand(
                projectId, new ParticipantId(UUID.randomUUID()), "Jane Smith", "jane@example.com"
        ));

        addNoteHandler.handle(new AddNoteCommand(
                projectId, "Important reminder", userId
        ));

        // THEN - List projection shows aggregated data
        var list = listQueryHandler.handle(new GetProjectListQuery(workspaceId));
        assertThat(list.get(0).totalBudget()).isEqualByComparingTo(BigDecimal.valueOf(800));
        assertThat(list.get(0).participantCount()).isEqualTo(2);

        // AND - Detail projection shows all data
        var detail = detailQueryHandler.handle(new GetProjectDetailQuery(projectId, workspaceId));
        assertThat(detail.budgetItems()).hasSize(2);
        assertThat(detail.participants()).hasSize(2);
        assertThat(detail.notes()).hasSize(1);
    }

    private void givenProjectCreated() {
        createProjectHandler.handle(new CreateProjectCommand(
                workspaceId,
                userId,
                projectId,
                "Q1 2025 Video Series",
                "Educational content about Event Sourcing",
                timeline,
                BigDecimal.valueOf(5000)
        ));
    }
}
