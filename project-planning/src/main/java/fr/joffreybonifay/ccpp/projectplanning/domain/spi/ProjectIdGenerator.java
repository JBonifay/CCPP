package fr.joffreybonifay.ccpp.projectplanning.domain.spi;

import fr.joffreybonifay.ccpp.shared.domain.identities.ProjectId;

public interface ProjectIdGenerator {
      ProjectId generate();
  }
