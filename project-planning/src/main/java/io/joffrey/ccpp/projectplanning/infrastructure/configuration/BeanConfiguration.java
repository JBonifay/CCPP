package io.joffrey.ccpp.projectplanning.infrastructure.configuration;

import com.ccpp.shared.command.CommandBus;
import com.ccpp.shared.command.SimpleCommandBus;
import com.ccpp.shared.eventstore.EventStore;
import com.ccpp.shared.query.QueryBus;
import com.ccpp.shared.query.SimpleQueryBus;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.joffrey.ccpp.projectplanning.application.command.command.*;
import io.joffrey.ccpp.projectplanning.application.command.handler.*;
import io.joffrey.ccpp.projectplanning.application.query.GetProjectDetailQuery;
import io.joffrey.ccpp.projectplanning.application.query.GetProjectListQuery;
import io.joffrey.ccpp.projectplanning.application.query.handler.GetProjectDetailQueryHandler;
import io.joffrey.ccpp.projectplanning.application.query.handler.GetProjectListQueryHandler;
import io.joffrey.ccpp.projectplanning.application.query.repository.ProjectDetailReadRepository;
import io.joffrey.ccpp.projectplanning.application.query.repository.ProjectListReadRepository;
import io.joffrey.ccpp.projectplanning.domain.spi.BudgetItemIdGenerator;
import io.joffrey.ccpp.projectplanning.domain.spi.ParticipantIdGenerator;
import io.joffrey.ccpp.projectplanning.domain.spi.ProjectIdGenerator;
import io.joffrey.ccpp.projectplanning.infrastructure.projection.ProjectDetailProjectionUpdater;
import io.joffrey.ccpp.projectplanning.infrastructure.projection.ProjectListProjectionUpdater;
import io.joffrey.ccpp.projectplanning.infrastructure.query.InMemoryProjectDetailReadRepository;
import io.joffrey.ccpp.projectplanning.infrastructure.query.InMemoryProjectListReadRepository;
import io.joffrey.ccpp.projectplanning.infrastructure.spi.UuidBudgetItemIdGenerator;
import io.joffrey.ccpp.projectplanning.infrastructure.spi.UuidParticipantIdGenerator;
import io.joffrey.ccpp.projectplanning.infrastructure.spi.UuidProjectIdGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    ProjectListReadRepository projectListReadRepository() {
        return new InMemoryProjectListReadRepository();
    }

    @Bean
    ProjectDetailReadRepository projectDetailReadRepository() {
        return new InMemoryProjectDetailReadRepository();
    }

    @Bean
    ProjectDetailProjectionUpdater projectDetailProjectionUpdater(ProjectDetailReadRepository projectDetailReadRepository) {
        return new ProjectDetailProjectionUpdater(projectDetailReadRepository);
    }

    @Bean
    ProjectListProjectionUpdater projectListProjectionUpdater(ProjectListReadRepository projectListReadRepository) {
        return new ProjectListProjectionUpdater(projectListReadRepository);
    }

    @Bean
    ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    @Bean
    public ProjectIdGenerator projectIdGenerator() {
        return new UuidProjectIdGenerator();
    }

    @Bean
    public BudgetItemIdGenerator budgetItemIdGenerator() {
        return new UuidBudgetItemIdGenerator();
    }

    @Bean
    public ParticipantIdGenerator participantIdGenerator() {
        return new UuidParticipantIdGenerator();
    }

    @Bean
    CommandBus commandBus(EventStore eventStore) {
        SimpleCommandBus simpleCommandBus = new SimpleCommandBus();
        simpleCommandBus.register(AcceptInvitationCommand.class, new AcceptInvitationHandler(eventStore));
        simpleCommandBus.register(AddBudgetItemCommand.class, new AddBudgetItemHandler(eventStore));
        simpleCommandBus.register(AddNoteCommand.class, new AddNoteHandler(eventStore));
        simpleCommandBus.register(CancelProjectCreationCommand.class, new CancelProjectCreationHandler(eventStore));
        simpleCommandBus.register(ChangeTimelineCommand.class, new ChangeTimelineHandler(eventStore));
        simpleCommandBus.register(RequestProjectCreationCommand.class, new RequestProjectCreationHandler(eventStore));
        simpleCommandBus.register(RejectInvitationCommand.class, new RejectInvitiationHandler(eventStore));
        simpleCommandBus.register(InviteParticipantCommand.class, new InviteParticipantHandler(eventStore));
        simpleCommandBus.register(MarkProjectReadyCommand.class, new MarkProjectReadyHandler(eventStore));
        simpleCommandBus.register(RemoveBudgetItemCommand.class, new RemoveBudgetItemHandler(eventStore));
        simpleCommandBus.register(UpdateBudgetItemCommand.class, new UpdateBudgetItemHandler(eventStore));
        simpleCommandBus.register(UpdateDetailsCommand.class, new UpdateDetailsHandler(eventStore));
        return simpleCommandBus;
    }

    @Bean
    QueryBus queryBus(ProjectDetailReadRepository projectDetailReadRepository, ProjectListReadRepository projectListReadRepository) {
        SimpleQueryBus simpleQueryBus = new SimpleQueryBus();
        simpleQueryBus.subscribe(GetProjectListQuery.class, new GetProjectListQueryHandler(projectListReadRepository));
        simpleQueryBus.subscribe(GetProjectDetailQuery.class, new GetProjectDetailQueryHandler(projectDetailReadRepository));
        return simpleQueryBus;
    }


}
