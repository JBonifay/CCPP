package fr.joffreybonifay.ccpp.projectplanning.domain.event;

import fr.joffreybonifay.ccpp.shared.identities.ProjectId;
import fr.joffreybonifay.ccpp.shared.identities.UserId;
import fr.joffreybonifay.ccpp.shared.event.DomainEvent;

public record NoteAdded(
        ProjectId projectId,
        String content,
        UserId userId
) implements DomainEvent {
}

