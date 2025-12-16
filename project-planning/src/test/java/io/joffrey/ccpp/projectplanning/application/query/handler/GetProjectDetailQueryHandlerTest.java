package io.joffrey.ccpp.projectplanning.application.query.handler;

import com.ccpp.shared.domain.identities.ProjectId;
import com.ccpp.shared.domain.identities.WorkspaceId;
import com.ccpp.shared.domain.valueobjects.DateRange;
import io.joffrey.ccpp.projectplanning.application.query.GetProjectDetailQuery;
import io.joffrey.ccpp.projectplanning.application.query.exception.ProjectNotFoundException;
import io.joffrey.ccpp.projectplanning.application.query.model.ProjectDetailDTO;
import io.joffrey.ccpp.projectplanning.application.query.repository.ProjectDetailReadRepository;
import io.joffrey.ccpp.projectplanning.domain.model.ProjectStatus;
import io.joffrey.ccpp.projectplanning.infrastructure.query.InMemoryProjectDetailReadRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GetProjectDetailQueryHandlerTest {

    ProjectDetailReadRepository repository = new InMemoryProjectDetailReadRepository();
    GetProjectDetailQueryHandler handler = new GetProjectDetailQueryHandler(repository);
    ProjectId projectId = new ProjectId(UUID.randomUUID());
    WorkspaceId workspaceId = new WorkspaceId(UUID.randomUUID());


    @Test
    void should_return_project_details() {
        ProjectDetailDTO expected = createProjectDetailDTO(projectId, workspaceId, "Project Title");
        repository.save(expected);

        ProjectDetailDTO result = handler.handle(new GetProjectDetailQuery(projectId, workspaceId));

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void should_throw_when_project_not_found() {
        assertThatThrownBy(() -> handler.handle(new GetProjectDetailQuery(projectId, workspaceId)))
                .isInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    void should_enforce_workspace_isolation() {

        repository.save(createProjectDetailDTO(projectId, workspaceId, "Secret Project"));

        assertThatThrownBy(() -> handler.handle(new GetProjectDetailQuery(projectId, new WorkspaceId(UUID.randomUUID()))))
                .isInstanceOf(ProjectNotFoundException.class)
                .hasMessage("Project not found: " + projectId.value());
    }

    private ProjectDetailDTO createProjectDetailDTO(ProjectId projectId, WorkspaceId workspaceId, String title) {
        return new ProjectDetailDTO(
                projectId,
                workspaceId,
                title,
                "Description",
                ProjectStatus.PLANNING,
                List.of(),
                List.of(),
                List.of(),
                new DateRange(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 3, 31))
        );
    }
}
