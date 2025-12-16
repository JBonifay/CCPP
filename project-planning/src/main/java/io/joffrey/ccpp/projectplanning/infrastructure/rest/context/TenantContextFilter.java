package io.joffrey.ccpp.projectplanning.infrastructure.rest.context;

import com.ccpp.shared.domain.identities.UserId;
import com.ccpp.shared.domain.identities.WorkspaceId;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class TenantContextFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain chain
    ) throws ServletException, IOException {
        try {
            String workspaceId = request.getHeader("X-Workspace-Id");
            String userId = request.getHeader("X-User-Id");

            if (workspaceId != null && userId != null) {
                RequestContext.setWorkspaceId(new WorkspaceId(UUID.fromString(workspaceId)));
                RequestContext.setUserId(new UserId(UUID.fromString(userId)));
            }

            chain.doFilter(request, response);
        } finally {
            RequestContext.clear();
        }
    }
}
