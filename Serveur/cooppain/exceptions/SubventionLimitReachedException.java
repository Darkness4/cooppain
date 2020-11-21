package cooppain.exceptions;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class SubventionLimitReachedException extends Exception {
    public SubventionLimitReachedException() {
        super("Subvention limit reached");
    }
}
