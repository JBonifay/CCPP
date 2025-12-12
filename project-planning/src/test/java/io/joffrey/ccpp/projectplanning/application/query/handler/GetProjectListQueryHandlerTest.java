package io.joffrey.ccpp.projectplanning.application.query.handler;

import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.WorkspaceId;
import io.joffrey.ccpp.projectplanning.application.query.GetProjectListQuery;
import io.joffrey.ccpp.projectplanning.application.query.model.ProjectListDTO;
import io.joffrey.ccpp.projectplanning.application.query.repository.ProjectListReadRepository;
import io.joffrey.ccpp.projectplanning.infrastructure.query.InMemoryProjectListReadRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GetProjectListQueryHandlerTest {

    ProjectListReadRepository repository = new InMemoryProjectListReadRepository();
    GetProjectListQueryHandler handler = new GetProjectListQueryHandler(repository);
    WorkspaceId workspaceId = new WorkspaceId(UUID.randomUUID());

    @Test
    void should_return_projects_for_workspace() {
        ProjectListDTO projectListDTO = projectListDTOExists(workspaceId);

        GetProjectListQuery query = new GetProjectListQuery(workspaceId);
        List<ProjectListDTO> result = handler.handle(query);

        assertThat(result).containsExactly(projectListDTO);
    }

    @Test
    void should_return_empty_list_when_no_projects() {
        List<ProjectListDTO> result = handler.handle(new GetProjectListQuery(workspaceId));

        assertThat(result).isEmpty();
    }

    @Test
    void should_filter_by_workspace_id() {
        ProjectListDTO expected = projectListDTOExists(workspaceId);
        projectListDTOExists(new WorkspaceId(UUID.randomUUID()));

        List<ProjectListDTO> result = handler.handle(new GetProjectListQuery(workspaceId));

        assertThat(result).containsExactly(
                expected
        );
    }

    public ProjectListDTO projectListDTOExists(WorkspaceId workspaceId) {
        ProjectListDTO projectListDTO = new ProjectListDTO(new ProjectId(UUID.randomUUID()), workspaceId, "Project", "PLANNING", BigDecimal.ZERO, 0);
        repository.save(projectListDTO);
        return projectListDTO;
    }

}
