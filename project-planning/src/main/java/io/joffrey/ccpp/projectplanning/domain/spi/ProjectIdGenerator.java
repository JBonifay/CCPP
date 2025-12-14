package io.joffrey.ccpp.projectplanning.domain.spi;

import com.ccpp.shared.identities.ProjectId;

public interface ProjectIdGenerator {
      ProjectId generate();
  }
