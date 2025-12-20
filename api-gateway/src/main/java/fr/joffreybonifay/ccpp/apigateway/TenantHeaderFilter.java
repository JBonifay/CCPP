package fr.joffreybonifay.ccpp.apigateway;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class TenantHeaderFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Here you would implement your logic to retrieve the IDs (e.g., from security context or token)
        // For demonstration, let's assume we are extracting or verifying them.

        // If you need to add/mutate headers for downstream services:
        ServerHttpRequest request = exchange.getRequest().mutate()
                // .header("X-Workspace-Id", extractedWorkspaceId)
                // .header("X-User-Id", extractedUserId)
                .build();

        return chain.filter(exchange.mutate().request(request).build());
    }

    @Override
    public int getOrder() {
        // High priority to ensure headers are set before other filters
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
