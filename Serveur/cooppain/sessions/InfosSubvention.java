package cooppain.sessions;

import cooppain.entities.Subvention;

import javax.ejb.Remote;
import java.util.List;

@Remote
public interface InfosSubvention {
    Subvention findById(int id);

    List<Subvention> find();

    int clear();

    List<String> findLibelleExcluding(int subventionId);
}
