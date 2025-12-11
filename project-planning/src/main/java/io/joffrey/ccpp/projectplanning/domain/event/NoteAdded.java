package io.joffrey.ccpp.projectplanning.domain.event;

import com.ccpp.shared.domain.DomainEvent;
import com.ccpp.shared.identities.ProjectId;
import com.ccpp.shared.identities.UserId;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.Instant;

@Getter
@EqualsAndHashCode(callSuper = true)
public final class NoteAdded extends DomainEvent {

    private final ProjectId projectId;
    private final String content;
    private final UserId userId;

    public NoteAdded(ProjectId projectId, String content, UserId userId) {
        super();
        this.projectId = projectId;
        this.content = content;
        this.userId = userId;
    }

}

