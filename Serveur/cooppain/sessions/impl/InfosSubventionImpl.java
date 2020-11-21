package cooppain.sessions.impl;

import cooppain.entities.Subvention;
import cooppain.sessions.InfosSubvention;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.List;

@Stateless
public class InfosSubventionImpl implements InfosSubvention {
    @PersistenceContext(unitName = "CoopPain_PU")
    private EntityManager em;

    @Override
    public Subvention findById(int id) {
        return em.find(Subvention.class, id);
    }

    @Override
    public List<Subvention> find() {
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<Subvention> query = cb.createQuery(Subvention.class);
        final CriteriaQuery<Subvention> all = query.select(query.from(Subvention.class));
        return em.createQuery(all).getResultList();
    }

    @Override
    public int clear() {
        // DELETE FROM Subvention
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaDelete<Subvention> delete = cb.createCriteriaDelete(Subvention.class);
        delete.from(Subvention.class);

        final int rowsDeleted = em.createQuery(delete).executeUpdate();
        em.flush();
        return rowsDeleted;
    }

    @Override
    public List<String> findLibelleExcluding(int subventionId) {
        // SELECT s.Libelle FROM Subvention s WHERE s.ID != :idParam.
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<String> query = cb.createQuery(String.class);
        final Root<Subvention> s = query.from(Subvention.class);
        final ParameterExpression<Integer> idParam = cb.parameter(Integer.class);
        final CriteriaQuery<String> all = query.select(s.get("libelle")).where(cb.notEqual(s.get("id"), idParam));

        final TypedQuery<String> typedQuery = em.createQuery(all);
        typedQuery.setParameter(idParam, subventionId);
        return typedQuery.getResultList();
    }
}
