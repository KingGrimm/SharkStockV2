/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllersBasic;

import controllersBasic.exceptions.IllegalOrphanException;
import controllersBasic.exceptions.NonexistentEntityException;
import entitites.Company;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entitites.Stock;
import entitites.Share;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Kingu
 */
public class CompanyJpaController implements Serializable {

    public CompanyJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Company company) {
        if (company.getShareList() == null) {
            company.setShareList(new ArrayList<Share>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Stock stock = company.getStock();
            if (stock != null) {
                stock = em.getReference(stock.getClass(), stock.getStockId());
                company.setStock(stock);
            }
            List<Share> attachedShareList = new ArrayList<Share>();
            for (Share shareListShareToAttach : company.getShareList()) {
                shareListShareToAttach = em.getReference(shareListShareToAttach.getClass(), shareListShareToAttach.getShareId());
                attachedShareList.add(shareListShareToAttach);
            }
            company.setShareList(attachedShareList);
            em.persist(company);
            if (stock != null) {
                stock.getCompanyList().add(company);
                stock = em.merge(stock);
            }
            for (Share shareListShare : company.getShareList()) {
                Company oldCompanyOfShareListShare = shareListShare.getCompany();
                shareListShare.setCompany(company);
                shareListShare = em.merge(shareListShare);
                if (oldCompanyOfShareListShare != null) {
                    oldCompanyOfShareListShare.getShareList().remove(shareListShare);
                    oldCompanyOfShareListShare = em.merge(oldCompanyOfShareListShare);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Company company) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Company persistentCompany = em.find(Company.class, company.getCompanyId());
            Stock stockOld = persistentCompany.getStock();
            Stock stockNew = company.getStock();
            List<Share> shareListOld = persistentCompany.getShareList();
            List<Share> shareListNew = company.getShareList();
            List<String> illegalOrphanMessages = null;
            for (Share shareListOldShare : shareListOld) {
                if (!shareListNew.contains(shareListOldShare)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Share " + shareListOldShare + " since its company field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (stockNew != null) {
                stockNew = em.getReference(stockNew.getClass(), stockNew.getStockId());
                company.setStock(stockNew);
            }
            List<Share> attachedShareListNew = new ArrayList<Share>();
            for (Share shareListNewShareToAttach : shareListNew) {
                shareListNewShareToAttach = em.getReference(shareListNewShareToAttach.getClass(), shareListNewShareToAttach.getShareId());
                attachedShareListNew.add(shareListNewShareToAttach);
            }
            shareListNew = attachedShareListNew;
            company.setShareList(shareListNew);
            company = em.merge(company);
            if (stockOld != null && !stockOld.equals(stockNew)) {
                stockOld.getCompanyList().remove(company);
                stockOld = em.merge(stockOld);
            }
            if (stockNew != null && !stockNew.equals(stockOld)) {
                stockNew.getCompanyList().add(company);
                stockNew = em.merge(stockNew);
            }
            for (Share shareListNewShare : shareListNew) {
                if (!shareListOld.contains(shareListNewShare)) {
                    Company oldCompanyOfShareListNewShare = shareListNewShare.getCompany();
                    shareListNewShare.setCompany(company);
                    shareListNewShare = em.merge(shareListNewShare);
                    if (oldCompanyOfShareListNewShare != null && !oldCompanyOfShareListNewShare.equals(company)) {
                        oldCompanyOfShareListNewShare.getShareList().remove(shareListNewShare);
                        oldCompanyOfShareListNewShare = em.merge(oldCompanyOfShareListNewShare);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Short id = company.getCompanyId();
                if (findCompany(id) == null) {
                    throw new NonexistentEntityException("The company with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Short id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Company company;
            try {
                company = em.getReference(Company.class, id);
                company.getCompanyId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The company with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Share> shareListOrphanCheck = company.getShareList();
            for (Share shareListOrphanCheckShare : shareListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Company (" + company + ") cannot be destroyed since the Share " + shareListOrphanCheckShare + " in its shareList field has a non-nullable company field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Stock stock = company.getStock();
            if (stock != null) {
                stock.getCompanyList().remove(company);
                stock = em.merge(stock);
            }
            em.remove(company);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Company> findCompanyEntities() {
        return findCompanyEntities(true, -1, -1);
    }

    public List<Company> findCompanyEntities(int maxResults, int firstResult) {
        return findCompanyEntities(false, maxResults, firstResult);
    }

    private List<Company> findCompanyEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Company.class));
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

    public Company findCompany(Short id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Company.class, id);
        } finally {
            em.close();
        }
    }

    public int getCompanyCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Company> rt = cq.from(Company.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
