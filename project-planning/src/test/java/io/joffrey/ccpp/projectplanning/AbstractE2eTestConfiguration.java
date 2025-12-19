package io.joffrey.ccpp.projectplanning;

import io.joffrey.ccpp.projectplanning.infrastructure.spi.MockBudgetItemIdGenerator;
import io.joffrey.ccpp.projectplanning.infrastructure.spi.MockParticipantIdGenerator;
import io.joffrey.ccpp.projectplanning.infrastructure.spi.MockProjectIdGenerator;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class AbstractE2eTestConfiguration {

    @Bean
    @Primary
    MockProjectIdGenerator testProjectIdGenerator() {
        return new MockProjectIdGenerator();
    }

    @Bean
    @Primary
    MockBudgetItemIdGenerator testBudgetItemIdGenerator() {
        return new MockBudgetItemIdGenerator();
    }

    @Bean
    @Primary
    MockParticipantIdGenerator testParticipantIdGenerator() {
        return new MockParticipantIdGenerator();
    }

}
