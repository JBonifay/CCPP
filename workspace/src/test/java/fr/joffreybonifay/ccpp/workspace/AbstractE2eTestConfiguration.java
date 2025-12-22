package fr.joffreybonifay.ccpp.workspace;

import fr.joffreybonifay.ccpp.workspace.infrastructure.spi.MockWorkspaceIdGenerator;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class AbstractE2eTestConfiguration {

    @Bean
    @Primary
    MockWorkspaceIdGenerator testWorkspaceIdGenerator() {
        return new MockWorkspaceIdGenerator();
    }


}
