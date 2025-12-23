package fr.joffreybonifay.ccpp.projectplanning.infrastructure.projection;

import fr.joffreybonifay.ccpp.projectplanning.application.query.model.BudgetItemDTO;
import fr.joffreybonifay.ccpp.projectplanning.application.query.model.NoteDTO;
import fr.joffreybonifay.ccpp.projectplanning.application.query.model.ParticipantDTO;
import fr.joffreybonifay.ccpp.projectplanning.application.query.model.ProjectDetailDTO;
import fr.joffreybonifay.ccpp.projectplanning.application.query.repository.ProjectDetailReadRepository;
import fr.joffreybonifay.ccpp.projectplanning.domain.event.*;
import fr.joffreybonifay.ccpp.projectplanning.domain.model.BudgetItem;
import fr.joffreybonifay.ccpp.projectplanning.domain.model.InvitationStatus;
import fr.joffreybonifay.ccpp.projectplanning.domain.model.ProjectStatus;
import fr.joffreybonifay.ccpp.projectplanning.domain.valueobject.BudgetItemId;
import fr.joffreybonifay.ccpp.projectplanning.domain.valueobject.ParticipantId;
import fr.joffreybonifay.ccpp.projectplanning.infrastructure.query.InMemoryProjectDetailReadRepository;
import fr.joffreybonifay.ccpp.shared.domain.event.ProjectCreationRequested;
import fr.joffreybonifay.ccpp.shared.domain.identities.ProjectId;
import fr.joffreybonifay.ccpp.shared.domain.identities.UserId;
import fr.joffreybonifay.ccpp.shared.domain.identities.WorkspaceId;
import fr.joffreybonifay.ccpp.shared.domain.valueobjects.DateRange;
import fr.joffreybonifay.ccpp.shared.domain.valueobjects.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectDetailProjectionUpdaterTest {

    ProjectDetailReadRepository repository = new InMemoryProjectDetailReadRepository();
    ProjectDetailProjectionUpdater updater = new ProjectDetailProjectionUpdater(repository);
    ProjectId projectId = new ProjectId(UUID.randomUUID());
    WorkspaceId workspaceId = new WorkspaceId(UUID.randomUUID());
    UserId userId = new UserId(UUID.randomUUID());
    DateRange timeline = new DateRange(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 3, 31));

    @Test
    void should_create_detail_projection_on_project_created() {
        var event = new ProjectCreationRequested(
                projectId,
                workspaceId,
                userId,
                "New Project",
                "Project Description",
                timeline,
                BigDecimal.valueOf(1000)
        );

        updater.on(event);

        var projection = repository.findById(projectId);
        assertThat(projection.get()).isEqualTo(
                new ProjectDetailDTO(
                        projectId,
                        workspaceId,
                        "New Project",
                        "Project Description",
                        ProjectStatus.PLANNING,
                        List.of(),
                        List.of(),
                        List.of(),
                        timeline
                )
        );
    }

    @Test
    void should_update_details_on_project_details_updated() {
        givenProjectCreated();

        updater.on(new ProjectDetailsUpdated(
                projectId,
                "Updated Title",
                "Updated Description"
        ));

        assertThat(repository.findById(projectId).get()).isEqualTo(
                new ProjectDetailDTO(
                        projectId,
                        workspaceId,
                        "Updated Title",
                        "Updated Description",
                        ProjectStatus.PLANNING,
                        List.of(),
                        List.of(),
                        List.of(),
                        timeline
                )
        );
    }

    @Test
    void should_add_budget_item_to_collection() {
        givenProjectCreated();

        var budgetItemId = new BudgetItemId(UUID.randomUUID());
        var event = new BudgetItemAdded(
                projectId,
                budgetItemId,
                "Hotel",
                Money.of(300, "USD")
        );

        updater.on(event);

        assertThat(repository.findById(projectId).get()).isEqualTo(
                new ProjectDetailDTO(
                        projectId,
                        workspaceId,
                        "Test Project",
                        "Description",
                        ProjectStatus.PLANNING,
                        List.of(new BudgetItemDTO(budgetItemId, "Hotel", Money.of(300, "USD"))),
                        List.of(),
                        List.of(),
                        timeline
                )
        );
    }

    @Test
    void should_update_budget_item_in_collection() {
        givenProjectCreated();
        var budgetItemId = new BudgetItemId(UUID.randomUUID());
        updater.on(new BudgetItemAdded(projectId, budgetItemId, "Hotel", Money.of(300, "USD")));

        updater.on(new BudgetItemUpdated(
                projectId,
                budgetItemId,
                "Hotel (updated)",
                Money.of(300, "USD"),
                Money.of(500, "USD")
        ));

        assertThat(repository.findById(projectId).get()).isEqualTo(
                new ProjectDetailDTO(
                        projectId,
                        workspaceId,
                        "Test Project",
                        "Description",
                        ProjectStatus.PLANNING,
                        List.of(new BudgetItemDTO(budgetItemId, "Hotel (updated)", Money.of(500, "USD"))),
                        List.of(),
                        List.of(),
                        timeline
                )
        );
    }

    @Test
    void should_remove_budget_item_from_collection() {
        givenProjectCreated();

        var event = new BudgetItemRemoved(projectId, null);

        updater.on(event);

        assertThat(repository.findById(projectId).get()).isEqualTo(
                new ProjectDetailDTO(
                        projectId,
                        workspaceId,
                        "Test Project",
                        "Description",
                        ProjectStatus.PLANNING,
                        List.of(),
                        List.of(),
                        List.of(),
                        timeline
                )
        );
    }

    @Test
    void should_add_participant_to_collection() {
        givenProjectCreated();

        var participantId = new ParticipantId(UUID.randomUUID());
        var event = new ParticipantInvited(
                projectId,
                participantId,
                "john@example.com",
                "John Doe"
        );

        updater.on(event);

        assertThat(repository.findById(projectId).get()).isEqualTo(
                new ProjectDetailDTO(
                        projectId,
                        workspaceId,
                        "Test Project",
                        "Description",
                        ProjectStatus.PLANNING,
                        List.of(),
                        List.of(new ParticipantDTO(participantId, "John Doe", "john@example.com", InvitationStatus.INVITED)),
                        List.of(),
                        timeline
                )
        );
    }

    @Test
    void should_update_participant_status_on_acceptance() {
        givenProjectCreated();
        var participantId = new ParticipantId(UUID.randomUUID());
        updater.on(new ParticipantInvited(projectId, participantId, "john@example.com", "John Doe"));

        var event = new ParticipantAcceptedInvitation(projectId, participantId);

        updater.on(event);

        assertThat(repository.findById(projectId).get()).isEqualTo(
                new ProjectDetailDTO(
                        projectId,
                        workspaceId,
                        "Test Project",
                        "Description",
                        ProjectStatus.PLANNING,
                        List.of(),
                        List.of(new ParticipantDTO(participantId, "John Doe", "john@example.com", InvitationStatus.ACCEPTED)),
                        List.of(),
                        timeline
                )
        );
    }

    @Test
    void should_add_note_to_collection() {
        givenProjectCreated();

        var event = new NoteAdded(projectId, "Important note", userId);

        updater.on(event);

        assertThat(repository.findById(projectId).get()).isEqualTo(
                new ProjectDetailDTO(
                        projectId,
                        workspaceId,
                        "Test Project",
                        "Description",
                        ProjectStatus.PLANNING,
                        List.of(),
                        List.of(),
                        List.of(new NoteDTO("Important note", userId)),
                        timeline
                )
        );
    }

    @Test
    void should_update_timeline() {
        givenProjectCreated();

        var newTimeline = new DateRange(LocalDate.of(2025, 2, 1), LocalDate.of(2025, 4, 30));
        var event = new ProjectTimelineChanged(projectId, newTimeline);

        updater.on(event);

        assertThat(repository.findById(projectId).get()).isEqualTo(
                new ProjectDetailDTO(
                        projectId,
                        workspaceId,
                        "Test Project",
                        "Description",
                        ProjectStatus.PLANNING,
                        List.of(),
                        List.of(),
                        List.of(),
                        newTimeline
                )
        );
    }

    @Test
    void should_update_status_on_marked_as_ready() {
        givenProjectCreated();

        var event = new ProjectMarkedAsReady(projectId, workspaceId, userId);

        updater.on(event);

        assertThat(repository.findById(projectId).get()).isEqualTo(
                new ProjectDetailDTO(
                        projectId,
                        workspaceId,
                        "Test Project",
                        "Description",
                        ProjectStatus.READY,
                        List.of(),
                        List.of(),
                        List.of(),
                        timeline
                )
        );
    }

    private void givenProjectCreated() {
        updater.on(new ProjectCreationRequested(
                projectId,
                workspaceId,
                userId,
                "Test Project",
                "Description",
                timeline,
                BigDecimal.valueOf(1000)
        ));
    }
}
