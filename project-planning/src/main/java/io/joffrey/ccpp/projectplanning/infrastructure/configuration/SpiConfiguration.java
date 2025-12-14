package io.joffrey.ccpp.projectplanning.infrastructure.configuration;

import io.joffrey.ccpp.projectplanning.domain.spi.ProjectIdGenerator;
import io.joffrey.ccpp.projectplanning.infrastructure.spi.UuidProjectIdGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpiConfiguration {

    @Bean
    public ProjectIdGenerator projectIdGenerator() {
        return new UuidProjectIdGenerator();
    }

}
