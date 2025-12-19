package io.joffrey.ccpp.workspace.infrastructure.configuration;

import com.ccpp.shared.query.QueryBus;
import com.ccpp.shared.query.SimpleQueryBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueryConfiguration {

    @Bean
    QueryBus queryBus() {
        SimpleQueryBus simpleQueryBus = new SimpleQueryBus();
        return simpleQueryBus;
    }

}
