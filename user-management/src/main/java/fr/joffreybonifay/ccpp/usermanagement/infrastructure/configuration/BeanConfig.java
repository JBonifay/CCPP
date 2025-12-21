package fr.joffreybonifay.ccpp.usermanagement.infrastructure.configuration;

import fr.joffreybonifay.ccpp.shared.command.CommandBus;
import fr.joffreybonifay.ccpp.shared.command.SimpleCommandBus;
import fr.joffreybonifay.ccpp.shared.eventstore.EventStore;
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
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
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


}
