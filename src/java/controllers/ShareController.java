/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import controllersBasic.ShareJpaController;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Kingu
 */
public class ShareController extends ShareJpaController{
       public ShareController(EntityManagerFactory emf) {
        super(emf);
    }

    public ShareController() {
        super(Persistence.createEntityManagerFactory("SharkStockV6PU"));
    }
}
