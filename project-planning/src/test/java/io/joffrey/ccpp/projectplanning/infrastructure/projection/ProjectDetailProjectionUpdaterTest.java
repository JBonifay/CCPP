package io.joffrey.ccpp.projectplanning.infrastructure.projection;

import com.ccpp.shared.domain.event.ProjectCreated;
import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.UserId;
import com.ccpp.shared.identities.WorkspaceId;
import com.ccpp.shared.valueobjects.DateRange;
import com.ccpp.shared.valueobjects.Money;
import io.joffrey.ccpp.projectplanning.application.query.model.BudgetItemDTO;
import io.joffrey.ccpp.projectplanning.application.query.model.NoteDTO;
import io.joffrey.ccpp.projectplanning.application.query.model.ParticipantDTO;
import io.joffrey.ccpp.projectplanning.application.query.model.ProjectDetailDTO;
import io.joffrey.ccpp.projectplanning.application.query.repository.ProjectDetailReadRepository;
import io.joffrey.ccpp.projectplanning.domain.event.*;
import io.joffrey.ccpp.projectplanning.domain.model.InvitationStatus;
import io.joffrey.ccpp.projectplanning.domain.valueobject.BudgetItemId;
import io.joffrey.ccpp.projectplanning.domain.valueobject.ParticipantId;
import io.joffrey.ccpp.projectplanning.infrastructure.query.InMemoryProjectDetailReadRepository;
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
        var event = new ProjectCreated(
                projectId,
                workspaceId,
                userId,
                "New Project",
                "Project Description",
                timeline,
                BigDecimal.valueOf(1000)
        );

        updater.onEvent(event);

        var projection = repository.findById(projectId);
        assertThat(projection.get()).isEqualTo(
                new ProjectDetailDTO(
                        projectId,
                        workspaceId,
                        "New Project",
                        "Project Description",
                        "PLANNING",
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

        updater.onEvent(new ProjectDetailsUpdated(
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
                        "PLANNING",
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

        updater.onEvent(event);

        assertThat(repository.findById(projectId).get()).isEqualTo(
                new ProjectDetailDTO(
                        projectId,
                        workspaceId,
                        "Test Project",
                        "Description",
                        "PLANNING",
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
        updater.onEvent(new BudgetItemAdded(projectId, budgetItemId, "Hotel", Money.of(300, "USD")));

        var event = new BudgetItemUpdated(
                projectId,
                budgetItemId,
                "Hotel (updated)",
                Money.of(450, "USD")
        );

        updater.onEvent(event);

        assertThat(repository.findById(projectId).get()).isEqualTo(
                new ProjectDetailDTO(
                        projectId,
                        workspaceId,
                        "Test Project",
                        "Description",
                        "PLANNING",
                        List.of(new BudgetItemDTO(budgetItemId, "Hotel (updated)", Money.of(450, "USD"))),
                        List.of(),
                        List.of(),
                        timeline
                )
        );
    }

    @Test
    void should_remove_budget_item_from_collection() {
        givenProjectCreated();
        var budgetItemId = new BudgetItemId(UUID.randomUUID());
        updater.onEvent(new BudgetItemAdded(projectId, budgetItemId, "Hotel", Money.of(300, "USD")));

        var event = new BudgetItemRemoved(projectId, budgetItemId);

        updater.onEvent(event);

        assertThat(repository.findById(projectId).get()).isEqualTo(
                new ProjectDetailDTO(
                        projectId,
                        workspaceId,
                        "Test Project",
                        "Description",
                        "PLANNING",
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

        updater.onEvent(event);

        assertThat(repository.findById(projectId).get()).isEqualTo(
                new ProjectDetailDTO(
                        projectId,
                        workspaceId,
                        "Test Project",
                        "Description",
                        "PLANNING",
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
        updater.onEvent(new ParticipantInvited(projectId, participantId, "john@example.com", "John Doe"));

        var event = new ParticipantAcceptedInvitation(projectId, participantId);

        updater.onEvent(event);

        assertThat(repository.findById(projectId).get()).isEqualTo(
                new ProjectDetailDTO(
                        projectId,
                        workspaceId,
                        "Test Project",
                        "Description",
                        "PLANNING",
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

        updater.onEvent(event);

        assertThat(repository.findById(projectId).get()).isEqualTo(
                new ProjectDetailDTO(
                        projectId,
                        workspaceId,
                        "Test Project",
                        "Description",
                        "PLANNING",
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

        updater.onEvent(event);

        assertThat(repository.findById(projectId).get()).isEqualTo(
                new ProjectDetailDTO(
                        projectId,
                        workspaceId,
                        "Test Project",
                        "Description",
                        "PLANNING",
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

        updater.onEvent(event);

        assertThat(repository.findById(projectId).get()).isEqualTo(
                new ProjectDetailDTO(
                        projectId,
                        workspaceId,
                        "Test Project",
                        "Description",
                        "READY",
                        List.of(),
                        List.of(),
                        List.of(),
                        timeline
                )
        );
    }

    private void givenProjectCreated() {
        updater.onEvent(new ProjectCreated(
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
