package fr.joffreybonifay.ccpp.usermanagement.infrastructure.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.joffreybonifay.ccpp.usermanagement.infrastructure.projection.UserProjectionUpdater;
import fr.joffreybonifay.ccpp.usermanagement.infrastructure.repository.JpaUserRepository;
import fr.joffreybonifay.ccpp.usermanagement.infrastructure.repository.JpaUserWorkspacesRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProjectionConfiguration {

    @Bean
    UserProjectionUpdater userProjectionUpdater(
            JpaUserRepository jpaUserRepository,
            ObjectMapper objectMapper,
            JpaUserWorkspacesRepository jpaUserWorkspacesRepository
    ) {
        return new UserProjectionUpdater(jpaUserRepository, jpaUserWorkspacesRepository, objectMapper);
    }

}
