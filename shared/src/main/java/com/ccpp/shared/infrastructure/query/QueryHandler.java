package com.ccpp.shared.infrastructure.query;

public interface QueryHandler<Query, Result> {
    Result handle(Query query);
}
