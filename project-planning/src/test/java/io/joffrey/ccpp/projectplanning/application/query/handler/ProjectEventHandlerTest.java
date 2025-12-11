package io.joffrey.ccpp.projectplanning.application.query.handler;

import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.UserId;
import com.ccpp.shared.identities.WorkspaceId;
import com.ccpp.shared.valueobjects.DateRange;
import io.joffrey.ccpp.projectplanning.domain.event.ProjectCreated;
import io.joffrey.ccpp.projectplanning.infrastructure.repository.InMemoryProjectQueryRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectEventHandlerTest {

    @Test
    void should_create_read_model_when_project_created() {
        var repository = new InMemoryProjectQueryRepository();
        var handler = new ProjectEventHandler(repository);
        UUID projectId = UUID.randomUUID();

        handler.handle(new ProjectCreated(
                new WorkspaceId(UUID.randomUUID()),
                new UserId(UUID.randomUUID()),
                new ProjectId(projectId),
                "New project",
                "Test",
                new DateRange(
                        LocalDate.of(2015, 2, 4),
                        LocalDate.of(2016, 3, 1)
                ),
                BigDecimal.valueOf(100)
        ));

        assertThat(repository.findById(projectId)).isPresent();
    }

}
