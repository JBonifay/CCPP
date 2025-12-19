package fr.joffreybonifay.ccpp.workspace.infrastructure.configuration;

import fr.joffreybonifay.ccpp.shared.query.QueryBus;
import fr.joffreybonifay.ccpp.shared.query.SimpleQueryBus;
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
