package io.joffrey.ccpp.projectplanning.infrastructure.configuration;

import com.ccpp.shared.query.QueryBus;
import com.ccpp.shared.query.SimpleQueryBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class QueryConfiguration {

    @Bean
    QueryBus queryBus() {
        return new SimpleQueryBus(Map.of());
    }

}
