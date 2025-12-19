package fr.joffreybonifay.ccpp.projectplanning.infrastructure.spi;

import fr.joffreybonifay.ccpp.shared.identities.ProjectId;
import fr.joffreybonifay.ccpp.projectplanning.domain.spi.ProjectIdGenerator;
import lombok.Setter;

@Setter
public class MockProjectIdGenerator implements ProjectIdGenerator {

    private ProjectId mock;

    @Override
    public ProjectId generate() {
        return mock;
    }

    public String getValue() {
        return mock.value().toString();
    }

}
