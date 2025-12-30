package fr.joffreybonifay.ccpp.usermanagement.infrastructure.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fr.joffreybonifay.ccpp.shared.command.CommandBus;
import fr.joffreybonifay.ccpp.shared.command.SimpleCommandBus;
import fr.joffreybonifay.ccpp.shared.eventstore.EventStore;
import fr.joffreybonifay.ccpp.shared.query.QueryBus;
import fr.joffreybonifay.ccpp.shared.query.SimpleQueryBus;
import fr.joffreybonifay.ccpp.shared.rest.TenantContextFilter;
import fr.joffreybonifay.ccpp.usermanagement.application.command.AssignUserToWorkspaceCommand;
import fr.joffreybonifay.ccpp.usermanagement.application.command.AssignUserToWorkspaceCommandHandler;
import fr.joffreybonifay.ccpp.usermanagement.application.command.RegisterNewUserCommand;
import fr.joffreybonifay.ccpp.usermanagement.application.command.RegisterNewUserCommandHandler;
import fr.joffreybonifay.ccpp.usermanagement.domain.service.UserUniquenessChecker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class BeanConfig {

    @Bean
    TenantContextFilter tenantContextFilter() {
        return new TenantContextFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    @Bean
    CommandBus commandBus(
            EventStore eventStore,
            PasswordEncoder passwordEncoder,
            UserUniquenessChecker userUniquenessChecker
    ) {
        SimpleCommandBus simpleCommandBus = new SimpleCommandBus();
        simpleCommandBus.register(RegisterNewUserCommand.class, new RegisterNewUserCommandHandler(eventStore, passwordEncoder, userUniquenessChecker));
        simpleCommandBus.register(AssignUserToWorkspaceCommand.class,new AssignUserToWorkspaceCommandHandler(eventStore));
        return simpleCommandBus;
    }


    @Bean
    QueryBus queryBus() {
        return new SimpleQueryBus();
    }

}
