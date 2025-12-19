package io.joffrey.ccpp.projectplanning.infrastructure.spi;

import com.ccpp.shared.identities.ProjectId;
import io.joffrey.ccpp.projectplanning.domain.spi.ProjectIdGenerator;
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
