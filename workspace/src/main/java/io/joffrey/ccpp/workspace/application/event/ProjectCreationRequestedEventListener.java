package io.joffrey.ccpp.workspace.application.event;

import com.ccpp.shared.domain.event.ProjectCreationRequested;
import com.ccpp.shared.domain.event.WorkspaceProjectCreationApproved;
import com.ccpp.shared.domain.event.WorkspaceProjectCreationRejected;
import com.ccpp.shared.infrastructure.event.EventBus;
import com.ccpp.shared.domain.identities.WorkspaceId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProjectCreationRequestedEventListener {

  private final EventBus eventBus;

  public ProjectCreationRequestedEventListener(EventBus eventBus) {
    this.eventBus = eventBus;
  }

  @EventListener
  public void onProjectCreationRequested(ProjectCreationRequested event) {
    log.info("Workspace received ProjectCreationRequested for project: {}, workspace: {}", event.projectId(),
        event.workspaceId());

    WorkspaceId workspaceId = event.workspaceId();

    // Check if workspace can create this project (quota validation)
    if (canCreateProject(workspaceId)) {
      // Approve project creation
      var approved = new WorkspaceProjectCreationApproved(
          workspaceId,
          event.projectId()
      );
      eventBus.publish(approved);
      log.info("Workspace approved project creation: {}", event.projectId());
    } else {
      // Reject - workspace quota exceeded
      var rejected = new WorkspaceProjectCreationRejected(
          workspaceId,
          event.projectId(),
          "Workspace has reached maximum project limit"
      );
      eventBus.publish(rejected);
      log.warn("Workspace rejected project creation for {}: quota exceeded", workspaceId);
    }
  }

  private boolean canCreateProject(WorkspaceId workspaceId) {
    throw new RuntimeException("Missing");
  }
}
