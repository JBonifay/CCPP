package fr.joffreybonifay.ccpp.usermanagement.application.projection;

import fr.joffreybonifay.ccpp.usermanagement.domain.event.UserAssignedToWorkspace;
import fr.joffreybonifay.ccpp.usermanagement.domain.event.UserCreated;

public interface UserProjectionHandler {
    void on(UserCreated event);
    void on(UserAssignedToWorkspace event);
}
