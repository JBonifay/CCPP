package fr.joffreybonifay.ccpp.workspace.infrastructure.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fr.joffreybonifay.ccpp.shared.command.CommandBus;
import fr.joffreybonifay.ccpp.shared.command.SimpleCommandBus;
import fr.joffreybonifay.ccpp.shared.eventstore.EventStore;
import fr.joffreybonifay.ccpp.shared.query.QueryBus;
import fr.joffreybonifay.ccpp.shared.query.SimpleQueryBus;
import fr.joffreybonifay.ccpp.shared.rest.TenantContextFilter;
import fr.joffreybonifay.ccpp.workspace.application.command.command.ApproveProjectCreationCommand;
import fr.joffreybonifay.ccpp.workspace.application.command.command.CreateWorkspaceCommand;
import fr.joffreybonifay.ccpp.workspace.application.command.handler.ApproveProjectCreationCommandHandler;
import fr.joffreybonifay.ccpp.workspace.application.command.handler.CreateWorkspaceCommandHandler;
import fr.joffreybonifay.ccpp.workspace.domain.WorkspaceIdGenerator;
import fr.joffreybonifay.ccpp.workspace.infrastructure.spi.UuidWorkspaceIdGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    TenantContextFilter tenantContextFilter() {
        return new TenantContextFilter();
    }

    @Bean
    WorkspaceIdGenerator workspaceIdGenerator() {
        return new UuidWorkspaceIdGenerator();
    }

    @Bean
    ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    @Bean
    CommandBus commandBus(EventStore eventStore) {
        SimpleCommandBus simpleCommandBus = new SimpleCommandBus();
        simpleCommandBus.register(CreateWorkspaceCommand.class, new CreateWorkspaceCommandHandler(eventStore));
        simpleCommandBus.register(ApproveProjectCreationCommand.class, new ApproveProjectCreationCommandHandler(eventStore));
        return simpleCommandBus;
    }

    @Bean
    QueryBus queryBus() {
        return new SimpleQueryBus();
    }

}
