/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import controllersBasic.CompanyJpaController;
import entitites.Company;
import entitites.Share;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Kingu
 */
//   return em.createQuery("SELECT s FROM Spolka s WHERE s." + column + " = :ss", Spolka.class).setParameter("ss", search).getResultList();
public class CompanyController extends CompanyJpaController {

    public CompanyController(EntityManagerFactory emf) {
        super(emf);
    }

    public CompanyController() {
        super(Persistence.createEntityManagerFactory("SharkStockV6PU"));
    }

    public Share getLastShare(Company fromCompany) {
        /*
       SELECT DISTINCT ON (share.share_id) * 
        FROM share INNER JOIN company ON share.company=company.company_id
        WHERE company.company_id = 5
        ORDER BY share.share_id DESC LIMIT 1;
         */
        EntityManager em = getEntityManager();
        //   return em.createQuery("SELECT s FROM Spolka s WHERE s." + column + " = :ss", Spolka.class).setParameter("ss", search).getResultList();
        return em.createQuery("SELECT s "
                + "FROM Share s WHERE s.company = :company "
                + "ORDER BY s.shareId DESC", Share.class).setMaxResults(1).setParameter("company", fromCompany).getSingleResult();
    }

    public Company findCompany(String column, String search) {
        EntityManager em = getEntityManager();
        return em.createQuery("SELECT s FROM Company s WHERE s." + column + " = :ss", Company.class).setParameter("ss", search).getResultList().get(0);
    }

    public List<Share> getTwoToCompare(Company fromCompany, Integer howOld) {
        List<Share> twoShares = new ArrayList<>();
        twoShares.add(getLastShare(fromCompany));
        EntityManager em = getEntityManager();
        em.createQuery("SELECT s "
                + "FROM Share s WHERE s.company = :company AND s.probeNumber = :howOld "
                + "ORDER BY s.shareId DESC", Share.class).setMaxResults(1).setParameter("company", fromCompany).setParameter("howOld", twoShares.get(0).getProbeNumber()).getSingleResult();

        int timesLooped = 0;
        if (twoShares.get(0).getProbeNumber() - howOld >= 0) {
            howOld = twoShares.get(0).getProbeNumber() - howOld;
        } else {
            while (twoShares.get(0).getProbeNumber() - howOld < 0) {
                howOld = constant.Constants.PROBES_IN_8H_PERIOD + (twoShares.get(0).getProbeNumber() - howOld);
                ++timesLooped;
            }
        }

        List<Share> potentialShares = em.createQuery("SELECT s "
                + "FROM Share s WHERE s.company = :company AND s.probeNumber = :howOld "
                + "ORDER BY s.shareId DESC", Share.class)
                .setParameter("howOld", howOld)
                .setParameter("company", fromCompany)
                .setMaxResults(1 + timesLooped)
                .getResultList();

        twoShares.add(potentialShares.get(potentialShares.size()-1));
        return twoShares;
        }
    
  
}
