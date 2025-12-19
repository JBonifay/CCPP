package fr.joffreybonifay.ccpp.projectplanning.infrastructure.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fr.joffreybonifay.ccpp.projectplanning.application.command.command.*;
import fr.joffreybonifay.ccpp.projectplanning.application.command.handler.*;
import fr.joffreybonifay.ccpp.projectplanning.application.query.GetProjectDetailQuery;
import fr.joffreybonifay.ccpp.projectplanning.application.query.GetProjectListQuery;
import fr.joffreybonifay.ccpp.projectplanning.application.query.handler.GetProjectDetailQueryHandler;
import fr.joffreybonifay.ccpp.projectplanning.application.query.handler.GetProjectListQueryHandler;
import fr.joffreybonifay.ccpp.projectplanning.application.query.repository.ProjectDetailReadRepository;
import fr.joffreybonifay.ccpp.projectplanning.application.query.repository.ProjectListReadRepository;
import fr.joffreybonifay.ccpp.projectplanning.domain.spi.BudgetItemIdGenerator;
import fr.joffreybonifay.ccpp.projectplanning.domain.spi.ParticipantIdGenerator;
import fr.joffreybonifay.ccpp.projectplanning.domain.spi.ProjectIdGenerator;
import fr.joffreybonifay.ccpp.projectplanning.infrastructure.projection.ProjectDetailProjectionUpdater;
import fr.joffreybonifay.ccpp.projectplanning.infrastructure.projection.ProjectListProjectionUpdater;
import fr.joffreybonifay.ccpp.projectplanning.infrastructure.query.InMemoryProjectDetailReadRepository;
import fr.joffreybonifay.ccpp.projectplanning.infrastructure.query.InMemoryProjectListReadRepository;
import fr.joffreybonifay.ccpp.projectplanning.infrastructure.spi.UuidBudgetItemIdGenerator;
import fr.joffreybonifay.ccpp.projectplanning.infrastructure.spi.UuidParticipantIdGenerator;
import fr.joffreybonifay.ccpp.projectplanning.infrastructure.spi.UuidProjectIdGenerator;
import fr.joffreybonifay.ccpp.shared.command.CommandBus;
import fr.joffreybonifay.ccpp.shared.command.SimpleCommandBus;
import fr.joffreybonifay.ccpp.shared.eventstore.EventStore;
import fr.joffreybonifay.ccpp.shared.query.QueryBus;
import fr.joffreybonifay.ccpp.shared.query.SimpleQueryBus;
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
