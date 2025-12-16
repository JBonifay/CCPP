package io.joffrey.ccpp.projectplanning.domain.spi;

import com.ccpp.shared.domain.identities.ProjectId;

public interface ProjectIdGenerator {
      ProjectId generate();
  }
