package cooppain.exceptions;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class LibelleConflictException extends Exception {
    public LibelleConflictException() {
        super("Libelle Conflict");
    }
}
