package fr.joffreybonifay.ccpp.projectplanning.domain.event;

import fr.joffreybonifay.ccpp.shared.domain.identities.ProjectId;
import fr.joffreybonifay.ccpp.shared.domain.identities.UserId;
import fr.joffreybonifay.ccpp.shared.domain.event.DomainEvent;

public record NoteAdded(
        ProjectId projectId,
        String content,
        UserId userId
) implements DomainEvent {
}

