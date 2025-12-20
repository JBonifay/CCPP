package fr.joffreybonifay.ccpp.apigateway.filter;

import fr.joffreybonifay.ccpp.apigateway.model.AuthContext;
import fr.joffreybonifay.ccpp.apigateway.exception.InvalidTokenException;
import fr.joffreybonifay.ccpp.apigateway.service.JwtTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTH_HEADER = "Authorization";

    private final JwtTokenService jwtTokenService;

    public AuthenticationFilter(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String authHeader = request.getHeaders().getFirst(AUTH_HEADER);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            return chain.filter(exchange);
        }

        String token = authHeader.substring(BEARER_PREFIX.length());

        try {
            AuthContext authContext = jwtTokenService.validateAndExtract(token);

            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-Id", authContext.userId().toString())
                    .header("X-Workspace-Id", authContext.workspaceId().toString())
                    .build();

            return chain.filter(exchange.mutate().request(modifiedRequest).build());

        } catch (InvalidTokenException e) {
            logger.error("Token validation failed: {}", e.getMessage());
            return onError(exchange, "Invalid or expired token", HttpStatus.UNAUTHORIZED);
        }
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        response.getHeaders().add("Content-Type", "application/json");

        String errorResponse = String.format(
                "{\"error\":\"%s\",\"message\":\"%s\",\"status\":%d}",
                httpStatus.getReasonPhrase(),
                message,
                httpStatus.value()
        );

        return response.writeWith(
                Mono.just(response.bufferFactory().wrap(errorResponse.getBytes()))
        );
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
