package cooppain.sessions;

import cooppain.entities.Campus;
import cooppain.entities.Subvention;
import cooppain.exceptions.LibelleConflictException;
import cooppain.exceptions.SubventionLimitReachedException;

import javax.ejb.Remote;
import java.util.List;

@Remote
public interface GestionSubvention {
    /**
     * This Session Bean is parameterized to manage this campus.
     *
     * This method should be called onInit.
     *
     * @param campusId Campus to be managed.
     */
    Campus init(int campusId);

    Subvention createSubvention(Subvention subvention) throws SubventionLimitReachedException, LibelleConflictException;

    List<Subvention> findSubventions();

    Subvention updateSubvention(Subvention subvention) throws LibelleConflictException, SubventionLimitReachedException;

    List<Subvention> deleteSubvention(Subvention... subventions);

    long countSubventions();

    long getSubventionLimit();
}
