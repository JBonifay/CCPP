package io.joffrey.ccpp.projectplanning.domain.exception;

import com.ccpp.shared.exception.DomainException;

public class InvalidProjectNoteException extends DomainException {

  public InvalidProjectNoteException(String message) {
        super(message);
    }

}
