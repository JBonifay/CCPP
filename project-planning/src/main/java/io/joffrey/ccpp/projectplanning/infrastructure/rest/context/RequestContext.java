package io.joffrey.ccpp.projectplanning.infrastructure.rest.context;

import com.ccpp.shared.identities.UserId;
import com.ccpp.shared.identities.WorkspaceId;

public class RequestContext {

    private static final ThreadLocal<WorkspaceId> workspaceId = new ThreadLocal<>();
    private static final ThreadLocal<UserId> userId = new ThreadLocal<>();

    public static void setWorkspaceId(WorkspaceId id) {
        workspaceId.set(id);
    }

    public static WorkspaceId getWorkspaceId() {
        WorkspaceId id = workspaceId.get();
        if (id == null) throw new IllegalStateException("WorkspaceId not set");
        return id;
    }

    public static void setUserId(UserId id) {
        userId.set(id);
    }

    public static UserId getUserId() {
        UserId id = userId.get();
        if (id == null) throw new IllegalStateException("UserId not set");
        return id;
    }

    public static void clear() {
        workspaceId.remove();
        userId.remove();
    }

}
