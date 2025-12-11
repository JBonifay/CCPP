package io.joffrey.ccpp.projectplanning.domain.event;

import com.ccpp.shared.domain.DomainEvent;
import com.ccpp.shared.identities.ProjectId;
import io.joffrey.ccpp.projectplanning.domain.valueobject.ParticipantId;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.Instant;

@Getter
@EqualsAndHashCode(callSuper = true)
public class ParticipantInvited extends DomainEvent {

    private final ProjectId projectId;
    private final ParticipantId participantId;
    private final String mail;
    private final String name;

    public ParticipantInvited(ProjectId projectId, ParticipantId participantId, String mail, String name) {
        super();
        this.projectId = projectId;
        this.participantId = participantId;
        this.mail = mail;
        this.name = name;
    }
}
