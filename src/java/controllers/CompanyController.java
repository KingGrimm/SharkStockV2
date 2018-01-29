/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import controllersBasic.CompanyJpaController;
import entitites.Company;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Kingu
 */
public class CompanyController extends CompanyJpaController {

    public CompanyController(EntityManagerFactory emf) {
        super(emf);
    }

    public CompanyController() {
        super(Persistence.createEntityManagerFactory("SharkStockV6PU"));
    }

}
