package fr.joffreybonifay.ccpp.shared.exception;

import java.util.UUID;

public class OptimisticLockException extends RuntimeException {

    public OptimisticLockException(String message) {
        super(message);
    }

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
