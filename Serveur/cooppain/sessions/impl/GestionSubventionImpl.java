package cooppain.sessions.impl;

import cooppain.entities.Campus;
import cooppain.entities.Subvention;
import cooppain.exceptions.LibelleConflictException;
import cooppain.exceptions.SubventionLimitReachedException;
import cooppain.sessions.GestionSubvention;
import cooppain.sessions.InfosSubvention;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Stateful
public class GestionSubventionImpl implements GestionSubvention {
    @Resource(name = "subventionLimit")
    long subventionLimit;

    @EJB
    InfosSubvention infosSubvention;

    @PersistenceContext(unitName = "CoopPain_PU")
    private EntityManager em;

    private Campus campusSession = null;

    @Override
    public Campus init(int campusId) {
        campusSession = em.find(Campus.class, campusId);
        return campusSession;
    }

    @Override
    public Subvention createSubvention(Subvention subvention) throws SubventionLimitReachedException, LibelleConflictException {
        if (subvention == null) {
            throw new IllegalArgumentException("subvention shouldn't be null.");
        }
        if (campusSession == null) {
            throw new NullPointerException("campusSession is null. Did you called `.init(int campusID)` at least once?");
        }
        if (countSubventions() >= subventionLimit) {
            throw new SubventionLimitReachedException();
        }

        if (infosSubvention.findLibelleExcluding(subvention.getId()).contains(subvention.getLibelle())) {
            throw new LibelleConflictException();
        }

        subvention.setCampusID(campusSession.getId());
        em.persist(subvention);
        em.flush();
        return subvention;
    }

    @Override
    public List<Subvention> findSubventions() {
        if (campusSession == null) {
            throw new NullPointerException("campusSession is null. Did you called `.init(int campusID)` at least once?");
        }

        // SELECT * FROM Subvention s WHERE s.CampusID = :campusIdParam.
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<Subvention> query = cb.createQuery(Subvention.class);
        final Root<Subvention> s = query.from(Subvention.class);
        final ParameterExpression<Integer> campusIdParam = cb.parameter(Integer.class);
        final CriteriaQuery<Subvention> all = query.select(s).where(cb.equal(s.get("campusID"), campusIdParam));

        // :campusIdParam = campusSession.getId()
        final TypedQuery<Subvention> typedQuery = em.createQuery(all);
        typedQuery.setParameter(campusIdParam, campusSession.getId());
        return typedQuery.getResultList();
    }

    @Override
    public Subvention updateSubvention(Subvention subvention) throws LibelleConflictException, SubventionLimitReachedException {
        if (subvention == null) {
            throw new IllegalArgumentException("subvention shouldn't be null.");
        }
        if (campusSession == null) {
            throw new NullPointerException("campusSession is null. Did you called `.init(int campusID)` at least once?");
        }

        subvention.setCampusID(campusSession.getId());

        if (countSubventions() < subventionLimit) {
            subvention = em.merge(subvention);
        } else {
            throw new SubventionLimitReachedException();
        }

        if (!infosSubvention.findLibelleExcluding(subvention.getId()).contains(subvention.getLibelle())) {
            subvention = em.merge(subvention);
        } else {
            throw new LibelleConflictException();
        }
        em.flush();
        return subvention;
    }

    @Override
    public List<Subvention> deleteSubvention(Subvention... subventions) {
        if (campusSession == null) {
            throw new NullPointerException("campusSession is null. Did you called `.init(int campusID)` at least once?");
        }

        final List<Subvention> modified = new ArrayList<>();

        for (Subvention subvention : subventions) {
            if (subvention.getCampusID() == campusSession.getId()) {
                subvention = em.merge(subvention);
                em.remove(subvention);
                modified.add(subvention);
            }
        }
        em.flush();
        return modified;
    }

    @Override
    public long countSubventions() {
        if (campusSession == null) {
            throw new NullPointerException("campusSession is null. Did you called `.init(int campusID)` at least once?");
        }

        // SELECT COUNT(*) FROM Subvention s WHERE s.CampusID = :campusIdParam.
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<Long> query = cb.createQuery(Long.class);
        final Root<Subvention> s = query.from(Subvention.class);
        final ParameterExpression<Integer> campusIdParam = cb.parameter(Integer.class);
        final CriteriaQuery<Long> all = query.select(cb.count(s)).where(cb.equal(s.get("campusID"), campusIdParam));

        // :campusIdParam = campusSession.getId()
        final TypedQuery<Long> typedQuery = em.createQuery(all);
        typedQuery.setParameter(campusIdParam, campusSession.getId());
        return typedQuery.getSingleResult();
    }

    @Override
    public long getSubventionLimit() {
        return subventionLimit;
    }
}
