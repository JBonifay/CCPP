package io.joffrey.ccpp.projectplanning.infrastructure.configuration;

import com.ccpp.shared.query.QueryBus;
import com.ccpp.shared.query.SimpleQueryBus;
import io.joffrey.ccpp.projectplanning.application.query.GetProjectDetailQuery;
import io.joffrey.ccpp.projectplanning.application.query.GetProjectListQuery;
import io.joffrey.ccpp.projectplanning.application.query.handler.GetProjectDetailQueryHandler;
import io.joffrey.ccpp.projectplanning.application.query.handler.GetProjectListQueryHandler;
import io.joffrey.ccpp.projectplanning.application.query.repository.ProjectDetailReadRepository;
import io.joffrey.ccpp.projectplanning.application.query.repository.ProjectListReadRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueryConfiguration {

    @Bean
    QueryBus queryBus(ProjectDetailReadRepository projectDetailReadRepository, ProjectListReadRepository projectListReadRepository) {
        SimpleQueryBus simpleQueryBus = new SimpleQueryBus();
        simpleQueryBus.subscribe(GetProjectListQuery.class, new GetProjectListQueryHandler(projectListReadRepository));
        simpleQueryBus.subscribe(GetProjectDetailQuery.class, new GetProjectDetailQueryHandler(projectDetailReadRepository));
        return simpleQueryBus;
    }

}
