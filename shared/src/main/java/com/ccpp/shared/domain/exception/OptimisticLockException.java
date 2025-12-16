package com.ccpp.shared.domain.exception;

import java.util.UUID;

public class OptimisticLockException extends DomainException {

    public OptimisticLockException(
            UUID aggregateId,
            int expectedVersion,
            int actualVersion
    ) {
        super(String.format(
                "Optimistic lock failed for aggregate %s. Expected version %d but was %d.",
                aggregateId,
                expectedVersion,
                actualVersion
        ));
    }

}
