package io.joffrey.ccpp.projectplanning.infrastructure.configuration;

import com.ccpp.shared.command.CommandBus;
import com.ccpp.shared.command.SimpleCommandBus;
import com.ccpp.shared.eventstore.EventStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommandConfiguration {

    @Bean
    CommandBus commandBus(EventStore eventStore) {
        SimpleCommandBus simpleCommandBus = new SimpleCommandBus();
        return simpleCommandBus;
    }

}
