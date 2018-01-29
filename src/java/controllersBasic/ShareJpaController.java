/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllersBasic;

import controllersBasic.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entitites.Company;
import entitites.Share;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Kingu
 */
public class ShareJpaController implements Serializable {

    public ShareJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Share share) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Company company = share.getCompany();
            if (company != null) {
                company = em.getReference(company.getClass(), company.getCompanyId());
                share.setCompany(company);
            }
            em.persist(share);
            if (company != null) {
                company.getShareList().add(share);
                company = em.merge(company);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Share share) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Share persistentShare = em.find(Share.class, share.getShareId());
            Company companyOld = persistentShare.getCompany();
            Company companyNew = share.getCompany();
            if (companyNew != null) {
                companyNew = em.getReference(companyNew.getClass(), companyNew.getCompanyId());
                share.setCompany(companyNew);
            }
            share = em.merge(share);
            if (companyOld != null && !companyOld.equals(companyNew)) {
                companyOld.getShareList().remove(share);
                companyOld = em.merge(companyOld);
            }
            if (companyNew != null && !companyNew.equals(companyOld)) {
                companyNew.getShareList().add(share);
                companyNew = em.merge(companyNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = share.getShareId();
                if (findShare(id) == null) {
                    throw new NonexistentEntityException("The share with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Share share;
            try {
                share = em.getReference(Share.class, id);
                share.getShareId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The share with id " + id + " no longer exists.", enfe);
            }
            Company company = share.getCompany();
            if (company != null) {
                company.getShareList().remove(share);
                company = em.merge(company);
            }
            em.remove(share);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Share> findShareEntities() {
        return findShareEntities(true, -1, -1);
    }

    public List<Share> findShareEntities(int maxResults, int firstResult) {
        return findShareEntities(false, maxResults, firstResult);
    }

    private List<Share> findShareEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Share.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Share findShare(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Share.class, id);
        } finally {
            em.close();
        }
    }

    public int getShareCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Share> rt = cq.from(Share.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
