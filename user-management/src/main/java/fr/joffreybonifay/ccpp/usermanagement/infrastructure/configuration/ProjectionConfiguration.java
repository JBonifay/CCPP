package fr.joffreybonifay.ccpp.usermanagement.infrastructure.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.joffreybonifay.ccpp.usermanagement.infrastructure.projection.UserProjectionUpdater;
import fr.joffreybonifay.ccpp.usermanagement.infrastructure.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProjectionConfiguration {

    @Bean
    UserProjectionUpdater userProjectionUpdater(UserRepository userRepository, ObjectMapper objectMapper) {
        return new UserProjectionUpdater(userRepository, objectMapper);
    }

}
