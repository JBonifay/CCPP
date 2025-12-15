package io.joffrey.ccpp.projectplanning.infrastructure.configuration;

import io.joffrey.ccpp.projectplanning.application.query.GetProjectDetailQuery;
import io.joffrey.ccpp.projectplanning.application.query.GetProjectListQuery;
import com.ccpp.shared.query.QueryBus;
import io.joffrey.ccpp.projectplanning.application.query.handler.GetProjectDetailQueryHandler;
import io.joffrey.ccpp.projectplanning.application.query.handler.GetProjectListQueryHandler;
import io.joffrey.ccpp.projectplanning.application.query.repository.ProjectDetailReadRepository;
import io.joffrey.ccpp.projectplanning.application.query.repository.ProjectListReadRepository;
import io.joffrey.ccpp.projectplanning.infrastructure.query.InMemoryProjectDetailReadRepository;
import io.joffrey.ccpp.projectplanning.infrastructure.query.InMemoryProjectListReadRepository;
import io.joffrey.ccpp.projectplanning.infrastructure.query.SimpleQueryBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class QueryBusConfiguration {

    @Bean
    public ProjectDetailReadRepository projectDetailReadRepository() {
        return new InMemoryProjectDetailReadRepository();
    }

    @Bean
    public ProjectListReadRepository projectListReadRepository() {
        return new InMemoryProjectListReadRepository();
    }

    @Bean
    public QueryBus queryBus(
            ProjectDetailReadRepository projectDetailReadRepository,
            ProjectListReadRepository projectListReadRepository
    ) {
        return new SimpleQueryBus(Map.of(
                GetProjectDetailQuery.class, new GetProjectDetailQueryHandler(projectDetailReadRepository),
                GetProjectListQuery.class, new GetProjectListQueryHandler(projectListReadRepository)
        ));
    }

}
