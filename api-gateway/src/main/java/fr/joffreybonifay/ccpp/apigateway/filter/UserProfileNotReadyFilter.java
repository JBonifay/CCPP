package fr.joffreybonifay.ccpp.apigateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class UserProfileNotReadyFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange)
                .onErrorResume(err -> Mono.empty())
                .then(Mono.defer(() -> {
                    var response = exchange.getResponse();
                    if (response.getStatusCode() != null && response.getStatusCode().value() == 404 &&
                        exchange.getRequest().getPath().toString().startsWith("/users/me")) {
                        response.setStatusCode(org.springframework.http.HttpStatus.CONFLICT);
                    }
                    return response.setComplete();
                }));
    }

    @Override
    public int getOrder() {
        return 0; // after JWT filter
    }
}
