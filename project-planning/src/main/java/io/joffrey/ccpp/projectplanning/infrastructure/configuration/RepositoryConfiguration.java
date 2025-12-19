package io.joffrey.ccpp.projectplanning.infrastructure.configuration;

import io.joffrey.ccpp.projectplanning.application.query.repository.ProjectDetailReadRepository;
import io.joffrey.ccpp.projectplanning.application.query.repository.ProjectListReadRepository;
import io.joffrey.ccpp.projectplanning.infrastructure.query.InMemoryProjectDetailReadRepository;
import io.joffrey.ccpp.projectplanning.infrastructure.query.InMemoryProjectListReadRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryConfiguration {

    @Bean
    ProjectListReadRepository projectListReadRepository() {
        return new InMemoryProjectListReadRepository();
    }

    @Bean
    ProjectDetailReadRepository projectDetailReadRepository() {
        return new InMemoryProjectDetailReadRepository();
    }

}
