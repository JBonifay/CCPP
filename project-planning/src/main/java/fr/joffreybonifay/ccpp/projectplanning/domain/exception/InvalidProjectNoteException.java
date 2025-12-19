package fr.joffreybonifay.ccpp.projectplanning.domain.exception;

import fr.joffreybonifay.ccpp.shared.exception.DomainException;

public class InvalidProjectNoteException extends DomainException {

  public InvalidProjectNoteException(String message) {
        super(message);
    }

}
