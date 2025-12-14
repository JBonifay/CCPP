package io.joffrey.ccpp.projectplanning.infrastructure.query;

import io.joffrey.ccpp.projectplanning.application.query.QueryBus;
import io.joffrey.ccpp.projectplanning.application.query.handler.QueryHandler;

import java.util.Map;

public class SimpleQueryBus implements QueryBus {

    private final Map<Class<?>, QueryHandler<?, ?>> queryHandlers;

    public SimpleQueryBus(Map<Class<?>, QueryHandler<?, ?>> queryHandlers) {
        this.queryHandlers = queryHandlers;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <Q, R> R execute(Q query) {
        var queryHandler = queryHandlers.get(query.getClass());
        if (queryHandler == null) {
            throw new IllegalArgumentException(
                    "No handler found for: " + query.getClass().getSimpleName()
            );
        }

        QueryHandler<Q, R> handler = (QueryHandler<Q, R>) queryHandler;
        return handler.handle(query);

    }

}
