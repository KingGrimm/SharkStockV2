/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllersBasic;

import controllersBasic.exceptions.IllegalOrphanException;
import controllersBasic.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entitites.Company;
import entitites.Stock;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Kingu
 */
public class StockJpaController implements Serializable {

    public StockJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Stock stock) {
        if (stock.getCompanyList() == null) {
            stock.setCompanyList(new ArrayList<Company>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Company> attachedCompanyList = new ArrayList<Company>();
            for (Company companyListCompanyToAttach : stock.getCompanyList()) {
                companyListCompanyToAttach = em.getReference(companyListCompanyToAttach.getClass(), companyListCompanyToAttach.getCompanyId());
                attachedCompanyList.add(companyListCompanyToAttach);
            }
            stock.setCompanyList(attachedCompanyList);
            em.persist(stock);
            for (Company companyListCompany : stock.getCompanyList()) {
                Stock oldStockOfCompanyListCompany = companyListCompany.getStock();
                companyListCompany.setStock(stock);
                companyListCompany = em.merge(companyListCompany);
                if (oldStockOfCompanyListCompany != null) {
                    oldStockOfCompanyListCompany.getCompanyList().remove(companyListCompany);
                    oldStockOfCompanyListCompany = em.merge(oldStockOfCompanyListCompany);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Stock stock) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Stock persistentStock = em.find(Stock.class, stock.getStockId());
            List<Company> companyListOld = persistentStock.getCompanyList();
            List<Company> companyListNew = stock.getCompanyList();
            List<String> illegalOrphanMessages = null;
            for (Company companyListOldCompany : companyListOld) {
                if (!companyListNew.contains(companyListOldCompany)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Company " + companyListOldCompany + " since its stock field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Company> attachedCompanyListNew = new ArrayList<Company>();
            for (Company companyListNewCompanyToAttach : companyListNew) {
                companyListNewCompanyToAttach = em.getReference(companyListNewCompanyToAttach.getClass(), companyListNewCompanyToAttach.getCompanyId());
                attachedCompanyListNew.add(companyListNewCompanyToAttach);
            }
            companyListNew = attachedCompanyListNew;
            stock.setCompanyList(companyListNew);
            stock = em.merge(stock);
            for (Company companyListNewCompany : companyListNew) {
                if (!companyListOld.contains(companyListNewCompany)) {
                    Stock oldStockOfCompanyListNewCompany = companyListNewCompany.getStock();
                    companyListNewCompany.setStock(stock);
                    companyListNewCompany = em.merge(companyListNewCompany);
                    if (oldStockOfCompanyListNewCompany != null && !oldStockOfCompanyListNewCompany.equals(stock)) {
                        oldStockOfCompanyListNewCompany.getCompanyList().remove(companyListNewCompany);
                        oldStockOfCompanyListNewCompany = em.merge(oldStockOfCompanyListNewCompany);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Short id = stock.getStockId();
                if (findStock(id) == null) {
                    throw new NonexistentEntityException("The stock with id " + id + " no longer exists.");
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
            Stock stock;
            try {
                stock = em.getReference(Stock.class, id);
                stock.getStockId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The stock with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Company> companyListOrphanCheck = stock.getCompanyList();
            for (Company companyListOrphanCheckCompany : companyListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Stock (" + stock + ") cannot be destroyed since the Company " + companyListOrphanCheckCompany + " in its companyList field has a non-nullable stock field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(stock);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Stock> findStockEntities() {
        return findStockEntities(true, -1, -1);
    }

    public List<Stock> findStockEntities(int maxResults, int firstResult) {
        return findStockEntities(false, maxResults, firstResult);
    }

    private List<Stock> findStockEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Stock.class));
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

    public Stock findStock(Short id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Stock.class, id);
        } finally {
            em.close();
        }
    }

    public int getStockCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Stock> rt = cq.from(Stock.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
