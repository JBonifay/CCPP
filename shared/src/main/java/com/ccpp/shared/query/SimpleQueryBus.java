package com.ccpp.shared.query;

import java.util.HashMap;
import java.util.Map;

public class SimpleQueryBus implements QueryBus {

    private final Map<Class<?>, QueryHandler<?, ?>> queryHandlers = new HashMap<>();

    public void subscribe(Class<?> handlerClass, QueryHandler<?, ?> queryHandlers){
        this.queryHandlers.put(handlerClass, queryHandlers);
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
