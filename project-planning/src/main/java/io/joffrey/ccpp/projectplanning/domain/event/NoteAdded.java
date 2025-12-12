package io.joffrey.ccpp.projectplanning.domain.event;

import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.UserId;
import io.joffrey.ccpp.projectplanning.domain.ProjectDomainEvent;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public final class NoteAdded extends ProjectDomainEvent {

    private final String content;
    private final UserId userId;


    public NoteAdded(ProjectId projectId, String content, UserId userId, Integer eventSequence) {
        super(projectId, eventSequence);
        this.content = content;
        this.userId = userId;
    }
}

